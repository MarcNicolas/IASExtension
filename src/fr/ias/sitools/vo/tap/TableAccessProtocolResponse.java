/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.tap;

import adql.parser.ADQLParser;
import adql.parser.ParseException;
import adql.query.ADQLQuery;
import adql.translator.ADQLTranslator;
import adql.translator.PostgreSQLTranslator;
import adql.translator.TranslationException;
//import fr.cnes.sitools.astro.representation.DatabaseRequestModel;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.dto.ColumnConceptMappingDTO;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.util.Util;
import fr.ias.sitools.vo.representation.DatabaseRequestIasModel;
import freemarker.template.TemplateSequenceModel;
import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ivoa.xml.votable.v1.AnyTEXT;
import net.ivoa.xml.votable.v1.DataType;
import net.ivoa.xml.votable.v1.Field;
import net.ivoa.xml.votable.v1.Info;
import net.ivoa.xml.votable.v1.Param;
import org.restlet.Context;

/**
 *
 * @author marc
 */
class TableAccessProtocolResponse implements TableAccessProtocolDataModelInterface { 
    
    /**
    * Data model.
    */
    private final transient Map dataModel = new HashMap();
    
    /**
    * Context 
    **/
    private final transient Context ctx;
    
    /**
    * The ADQL Query
    */
    private final String adqlQuery;
    
    /**
     * The Psql Query from the ADQL query
     */
    private transient String psqlQuery;
    /**
     * The col to query
     */
    private ArrayList<String> colsToQuery = new ArrayList<String>();
    
    private String clauseWhereToQuery;
    
    private String clauseLimit;
    private int clauseLimitInt;
    private String format;
    
    boolean isPrimaryKey;
    
    public TableAccessProtocolResponse(final TableAccessProtocolInputParameters inputParameters, final ResourceModel model) {        
        this.format = inputParameters.getFormat();
        this.adqlQuery = inputParameters.getQuery();
        this.ctx = inputParameters.getContext();

        if(this.adqlQuery == null || this.adqlQuery .equalsIgnoreCase("")){
            // TO DO
        }else{
            processQuery();
            createResponse(inputParameters, model); 
        }
    }

    private void createResponse(final TableAccessProtocolInputParameters inputParameters, final ResourceModel model){


        // On récupère le nom du dico
        final String dictionaryName = model.getParameterByName(TableAccessProtocolLibrary.DICTIONARY).getValue();

        //On set les params
        setVotableParametersFromConfiguration(this.dataModel, model);
        //On requete la base et on remplit le template
        setVotableResource(inputParameters.getDatasetApp(), inputParameters, model, dictionaryName);

        // On set le query_status à OK
        setQueryInfos(model);
   
    }
    // FONCTIONS PRIVATE
    /**
    * Creates the response based on Table.
    *
    * @param datasetApp Dataset application
    * @param inputParameters Input Parameters
    * @param model data model
    * @param dictionaryName TAP dictionary
    */
    private void setVotableResource(final DataSetApplication datasetApp, final TableAccessProtocolInputParameters inputParameters,
          final ResourceModel model, final String dictionaryName) {
        
        final List<Field> fieldList = new ArrayList<Field>();
        final List<String> columnStringList = new ArrayList<String>();
        final List<Column> columnList = datasetApp.getDataSet().getColumnModel();
        
        DatabaseRequest databaseRequest = null;
        try {
            // On récupère les columns

            // On récupere le mapping
            List<ColumnConceptMappingDTO> mappingList = getDicoFromConfiguration(datasetApp, dictionaryName);
            
            final DatabaseRequestParameters dbParams = setQueryParameters(datasetApp, model, inputParameters, mappingList);

            // On récupère les colonnes à requeter
            List<Column> listCol = getColumnToQuery(columnList);
            listCol = checkPrimaryKeyAndAddItColTQuery(columnList, listCol);
            // On envoie les colonnes à requeter
            dbParams.setSqlVisibleColumns(listCol);
            
            // S'il y a un parametre Limit on le rajoute au parametre de requete
            if(this.clauseLimitInt > 0)
            {
                dbParams.setMaxrows(this.clauseLimitInt);
            }   
            databaseRequest = DatabaseRequestFactory.getDatabaseRequest(dbParams);
            databaseRequest.checkRequest();
            // Execute query
            databaseRequest.createRequest();

            getCtx().getLogger().log(Level.FINEST, "-------- DB REQUEST : {0}", databaseRequest.getRequestAsString());

            setFields(fieldList, columnStringList, mappingList, listCol);
            
            final int count = (databaseRequest.getCount() > dbParams.getPaginationExtend()) ? dbParams.getPaginationExtend() : databaseRequest.getCount();
            dataModel.put("nrows", count);
            dataModel.put("fields", fieldList);
            dataModel.put("sqlColAlias", columnStringList);
            
            final ConverterChained converterChained = datasetApp.getConverterChained();
            
            //final TemplateSequenceModel rows = new DatabaseRequestModel(databaseRequest, converterChained);
            final TemplateSequenceModel rows = new DatabaseRequestIasModel(databaseRequest, converterChained);

            dataModel.put("rows", rows);
        } catch (SitoolsException ex) {
            Logger.getLogger(TableAccessProtocolResponse.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }
    
    /**
   * Set Query parameters to the database.
   *
   * @param datasetApp Dataset Application
   * @param model Data model
   * @param inputParameters Input Parameters
   * @return DatabaseRequestParamerters object
   */
  @SuppressWarnings("empty-statement")
  private DatabaseRequestParameters setQueryParameters(final DataSetApplication datasetApp, final ResourceModel model,
          final TableAccessProtocolInputParameters inputParameters, List<ColumnConceptMappingDTO> mappingList) {

    // Get the dataset
    final DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, inputParameters.getRequest(),
            inputParameters.getContext());

    // Get query parameters
    final DatabaseRequestParameters dbParams = dsExplorerUtil.getDatabaseParams();
    // Get dataset records
    final int nbRecordsInDataSet = datasetApp.getDataSet().getNbRecords();

    // Get max records that is defined by admin
    int nbMaxRecords = Integer.valueOf(model.getParameterByName(TableAccessProtocolLibrary.MAX_RECORDS).getValue());
    nbMaxRecords = (nbMaxRecords > nbRecordsInDataSet || nbMaxRecords == -1) ? nbRecordsInDataSet : nbMaxRecords;
    // if a limit clause is defined, used it.
    if(this.clauseLimit != null){        
        nbMaxRecords = Integer.parseInt(this.clauseLimit);
    }
    // Set max records
    dbParams.setPaginationExtend(nbMaxRecords);
    
    
    
    final List<Predicat> predicatList = dbParams.getPredicats();
    //String customQuery = "AND ra > 25 and dec < 180 and flux > 0.256";
    Predicat predicat = new Predicat();
    
    predicat.setStringDefinition(this.clauseWhereToQuery);
    predicatList.add(predicat);
    dbParams.setPredicats(predicatList);
    
    return dbParams;
  }

    
    private void setQueryInfos(final ResourceModel model){
        final List<Info> queryInfos = new ArrayList<Info>();
        
        Info info = new Info();
        info.setName("QUERY_STATUS");
        info.setValueAttribute("OK");
        queryInfos.add(info);
        info = new Info();
        info.setName("ADQL query");
        String query = this.adqlQuery.replaceAll("\"", "").replaceAll(">","&gt;").replaceAll("<","&lt;");
        info.setValueAttribute(query);
        
        queryInfos.add(info);

        this.dataModel.put("queryInfos", queryInfos);
    }
    /**
   * Sets VOTable parameters coming from administration configuration.
   *
   * @param dataModel data model to set
   * @param model parameters from administration
   */
  private void setVotableParametersFromConfiguration(final Map dataModel, final ResourceModel model) {
    final List<Param> params = new ArrayList<Param>();
    setVotableParam(params, model, TableAccessProtocolLibrary.INSTRUMENT, DataType.CHAR);
    setVotableParam(params, model, TableAccessProtocolLibrary.SERVICE_NAME, DataType.CHAR);
    if (Util.isSet(params)) {
      this.dataModel.put("params", params);
    }
  }

  /**
   * Sets Votable Param.
   *
   * @param params List of params
   * @param model data model
   * @param parameterName parameter name
   * @param datatype datatype
   */
  private void setVotableParam(final List<Param> params, final ResourceModel model, final String parameterName,
          final DataType datatype) {
    final String parameterValue = model.getParameterByName(parameterName).getValue();
    if (Util.isNotEmpty(parameterValue)) {
        final Param param = new Param();
        param.setName(parameterName);
        param.setValue(parameterValue);
        param.setDatatype(datatype);
        params.add(param);
    }
  }
    /**
    * Provide the mapping between SQL column/concept for a given dictionary.
    *
    * @param datasetApp Application where this service is attached
    * @param dicoToFind Dictionary name to find
    * @return Returns a mapping SQL column/Concept
    * @throws SitoolsException No mapping has been done or cannot find the dico
    */
    private List<ColumnConceptMappingDTO> getDicoFromConfiguration(final DataSetApplication datasetApp,
          final String dicoToFind) throws SitoolsException {
        List<ColumnConceptMappingDTO> colConceptMappingDTOList = null;
        
        // Get the list of dictionnaries related to the datasetApplication
        final List<DictionaryMappingDTO> dicoMappingList = datasetApp.getDictionaryMappings();
        if (!Util.isSet(dicoMappingList) || dicoMappingList.isEmpty()) {
          throw new SitoolsException("No mapping with VO concepts has been done. please contact the administrator");
        }

        // For each dictionary, find the interesting one and return the mapping SQLcolumn/concept
        for (DictionaryMappingDTO dicoMappingIter : dicoMappingList) {
          final String dicoName = dicoMappingIter.getDictionaryName();
          if (dicoToFind.equals(dicoName)) {
            colConceptMappingDTOList = dicoMappingIter.getMapping();
            break;
          }
        }
        return colConceptMappingDTOList;
    }
    
    
    private List<Column> getColumnToQuery(List<Column> columnList){
        List<Column> colsToQueryList = new ArrayList<Column>();
        String all = this.adqlQuery.split(TableAccessProtocolLibrary.SELECT)[1].split(TableAccessProtocolLibrary.FROM)[0];      
        if(this.colsToQuery.size() == 1 && all.contains("*")){
            for(Column colCol : columnList){ 
                colsToQueryList.add(colCol);
            }
            return colsToQueryList;
        }
        for(String col : this.colsToQuery){          
            for(Column colCol : columnList){            
                if(col.equalsIgnoreCase(colCol.getColumnAlias())){
                    colsToQueryList.add(colCol);
                }
            }
        }
        
        return colsToQueryList;
    }
    
    /**
   * Set Fields and columnSqlAliasList.
   *
   * @param fieldList List of fields to display on the VOTable
   * @param colToQuery List of column to query and so to display in VOTable
   * @param columnList List of SQL column
   * @param mappingList List of SQL column/concept
   */
  private void setFields(final List<Field> fieldList, final List<String> columnList, final List<ColumnConceptMappingDTO> mappingList, final List<Column> colToQuery) {
    
    List<Column> colToQuery2 = colToQuery;
    if(!isPrimaryKey){
        colToQuery2.remove(colToQuery2.size()-1);
    }
    for(Column col : colToQuery2){
        
        for (ColumnConceptMappingDTO mappingIter : mappingList) {
            if(col.getColumnAlias().equalsIgnoreCase(mappingIter.getColumnAlias())){
                
                String id = null;
                String name = null;
                String ucd = null;
                String utype = null;
                String ref = null;
                String datatype = null;
                String width = null;
                String precision = null;
                String unit = null;
                String type = null;
                String xtype = null;
                String arraysize = null;
                String descriptionValue = null;
                columnList.add(mappingIter.getColumnAlias());
                final Concept concept = mappingIter.getConcept();
                if (concept.getName() != null) {
                  name = concept.getName();
                }
                if (concept.getPropertyFromName("ID").getValue() != null) {
                  id = concept.getPropertyFromName("ID").getValue();
                }
                if (concept.getPropertyFromName("ucd").getValue() != null) {
                  ucd = concept.getPropertyFromName("ucd").getValue();
                }
                if (concept.getPropertyFromName("utype").getValue() != null) {
                  utype = concept.getPropertyFromName("utype").getValue();
                }
                if (concept.getPropertyFromName("ref").getValue() != null) {
                  ref = concept.getPropertyFromName("ref").getValue();
                }
                if (concept.getPropertyFromName("datatype").getValue() != null) {
                  datatype = concept.getPropertyFromName("datatype").getValue();
                }
                if (concept.getPropertyFromName("width").getValue() != null) {
                  width = concept.getPropertyFromName("width").getValue();
                }
                if (concept.getPropertyFromName("precision").getValue() != null) {
                  precision = concept.getPropertyFromName("precision").getValue();
                }
                if (concept.getPropertyFromName("unit").getValue() != null) {
                  unit = concept.getPropertyFromName("unit").getValue();
                }
                if (concept.getPropertyFromName("type").getValue() != null) {
                  type = concept.getPropertyFromName("type").getValue();
                }
                if (concept.getPropertyFromName("xtype").getValue() != null) {
                  xtype = concept.getPropertyFromName("xtype").getValue();
                }
                if (concept.getPropertyFromName("arraysize").getValue() != null) {
                  arraysize = concept.getPropertyFromName("arraysize").getValue();
                }
                if (concept.getDescription() != null) {
                  descriptionValue = concept.getDescription();
                }
                final Field field = new Field();
                field.setID(id);
                field.setName(name);
                field.setUcd(ucd);
                field.setUtype(utype);
                field.setRef(ref);
                field.setDatatype(DataType.fromValue(datatype));
                if (width != null) {
                  field.setWidth(BigInteger.valueOf(Long.valueOf(width)));
                }
                field.setPrecision(precision);
                field.setUnit(unit);
                field.setType(type);
                field.setXtype(xtype);
                field.setArraysize(arraysize);
                final AnyTEXT anyText = new AnyTEXT();
                anyText.getContent().add(descriptionValue);
                field.setDESCRIPTION(anyText);
                fieldList.add(field);
            }
        }
    }
  }
  //List<Column> columnList = datasetApp.getDataSet().getColumnModel();
  private List<Column> checkPrimaryKeyAndAddItColTQuery(List<Column> columnList, List<Column> colToQuery){
      Column colPrimKey = null;
      for(Column co : columnList){
          if(co.isPrimaryKey()){
              colPrimKey = co;
          }
      }
      for(Column col : colToQuery){
          if(col.isPrimaryKey()){
              isPrimaryKey = true; 
          }
      }
      if(!isPrimaryKey &&  "" != colPrimKey.getColumnAlias()){
          colToQuery.add(colPrimKey);
      }
      
      return colToQuery;
  }
    
    private void processQuery(){
        try {
            // On crée un parser pour transformer notre string query en adql query
            ADQLParser parser = new ADQLParser();
            ADQLQuery adqlQueryValue = parser.parseQuery(this.adqlQuery);
            // On traduit l'adql query en psql query
            ADQLTranslator translator = new PostgreSQLTranslator();

            this.psqlQuery = translator.translate(adqlQueryValue);
            ctx.getLogger().log(Level.SEVERE, "#### ADQL request : "+this.adqlQuery);
            ctx.getLogger().log(Level.SEVERE, "#### PSQL request : "+this.psqlQuery);

            if(this.psqlQuery.contains("Limit ")){
                this.clauseLimit = this.psqlQuery.split("Limit ")[1];
                this.clauseLimitInt = Integer.parseInt(this.clauseLimit.replaceAll(" ", ""));
                this.psqlQuery = this.psqlQuery.split("Limit ")[0];
            }else{
                this.clauseLimit = null;
                this.clauseLimitInt = -1;
            }

            this.clauseWhereToQuery = "AND"+ this.psqlQuery.split(TableAccessProtocolLibrary.FROM)[1].split(TableAccessProtocolLibrary.WHERE)[1];  

            String[] colsToQueryTmp = this.psqlQuery.split(TableAccessProtocolLibrary.FROM)[0].split(TableAccessProtocolLibrary.SELECT)[1].split(",");
            for(String col : colsToQueryTmp){
                if(col.equalsIgnoreCase(TableAccessProtocolLibrary.SELECT_ALL)){
                    colsToQuery.add(col.replaceAll(TableAccessProtocolLibrary.BLANCK, ""));
                    break;
                }else{
                    colsToQuery.add(col.split("AS")[0].replaceAll(TableAccessProtocolLibrary.BLANCK, ""));
                }
            }
            if(format == null || format.equalsIgnoreCase("")){
                // TO DO
            }else{
                ctx.getLogger().log(Level.INFO, "format = "+format);
            }
        } catch (TranslationException ex) {
            Logger.getLogger(TableAccessProtocolResponse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TableAccessProtocolResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
    @Override
    public final Map getDataModel() {
        return Collections.unmodifiableMap(this.dataModel);
    }
    
    // GETTER DE LA CLASSE
    public Context getCtx() {
        return ctx;
    }
}
