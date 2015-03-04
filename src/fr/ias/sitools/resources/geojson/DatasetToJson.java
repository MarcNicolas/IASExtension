/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.resources.geojson;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.dto.ColumnConceptMappingDTO;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.util.Property;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
// IMPORT POUR LE JSON
import org.restlet.representation.WriterRepresentation;
//-------------------------

/**
 *
 * @author marc
 */
public class DatasetToJson extends SitoolsParameterizedResource {
    
    private static final Logger LOG = Logger.getLogger(DatasetToJson.class.getName());
    
    private String dicoNameString = new String();
    
    @Override
    public void sitoolsDescribe() {
        setName("DatasetToJson");
        setDescription("retrieves Dataset to Json file");
    }

    @Override
    public void doInit() {
        super.doInit();
    }

    /**
    * Get HTML
    * 
    * @return Representation the HTML result
    */
    @Get
    public Representation get() {
        return execute();
    }

    @Override
    protected void describeGet(MethodInfo info) {
        this.addInfo(info);
        info.setIdentifier("retrieve records and ");
        info.setDocumentation("Method to get Json file from a dataset");
        addStandardGetRequestInfo(info);
        DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
        DataSetApplication application = (DataSetApplication) getApplication();
        DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
        addStandardResponseInfo(info);
        addStandardInternalServerErrorInfo(info);
    }

    @Override
    protected Representation head(Variant variant) {
        Representation repr = super.head();
        repr.setMediaType(MediaType.APPLICATION_JSON);
        return repr;
    }

    private Representation execute() {

        dicoNameString = DatasetToJsonModel.DICO_PARAM_NAME;
        
        Representation repr = new WriterRepresentation(MediaType.APPLICATION_JSON) {
            
            @Override
            public void write(Writer writer) throws IOException {
                
                // generate the DatabaseRequest
                DataSetApplication datasetApp = (DataSetApplication) getApplication();
                DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());
                DictionaryMappingDTO dico =null;
                
                String dicoName = getParameterValue(dicoNameString);
                // Get the HashMap with as key the concept and value the columnAlias
                final HashMap<Concept,String> conceptsColumns = getcolumnAliasFromDico(dicoName, dico, datasetApp);
                
                // Get request parameters
                if (datasetApp.getConverterChained() != null) {
                    datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
                }
                // Get DatabaseRequestParameters
                DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();
                DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
                try {
                    databaseRequest.createRequest();
                }catch (SitoolsException e) {
                    e.printStackTrace();
                }
                writer.write("{");
                writer.write("\"type\":\"FeatureCollection\",");
                // start features
                writer.write("\"totalResults\":" + databaseRequest.getTotalCount() + ",");
                writer.write("\"features\":[");
                // Next for reading first record
                try {
                    boolean first = true;
                    while (databaseRequest.nextResult()) {
                        Record rec = databaseRequest.getRecord();

                        if (!first) {
                          writer.write(",");
                        }
                        else {
                          first = false;
                        }
                        // creates a geometry and a properties string
                        String geometry = new String();
                        //String services = new String();
                        String properties = new String();
                        
                        boolean firstProp = true;
                        String coords[] = new String[2];
                        //String urlDownloadFits = null;
                        
                        /*for (Iterator<AttributeValue> it = rec.getAttributeValues().iterator(); it.hasNext();) {
                            AttributeValue attr = it.next();
                            if(attr.getName().toString().equals(conceptsColumns.get("ra"))){
                                coords[0]= attr.getValue().toString();
                            }
                            if(attr.getName().toString().equals(conceptsColumns.get("dec"))){
                                coords[1]= attr.getValue().toString();
                            }
                            for(String col : conceptsColumns.values()){
                                
                                if(col.equals(attr.getName())){
                                    if (attr.getValue() != null && !attr.getValue().equals("")) {
                                        if (!firstProp) {
                                            properties += ",";
                                        }
                                        else {
                                            firstProp = false;
                                        }
                                        properties += "\"" + getKeyFromValue(conceptsColumns,col) + "\":\"" + attr.getValue() + "\"";
                                    }
                                }
                            }
                        }*/
                        
                        for(Concept concept : conceptsColumns.keySet()){
                            //LOG.log(Level.SEVERE, "**************  name : "+concept.getName()+" category = "+concept.getPropertyFromName("category").getValue());
                            int i=0;
                            concept.getPropertyFromName("category");
                            /*for(Property p :concept.getProperties()){
                                LOG.log(Level.SEVERE, "**************  propertie["+i+"] : "+p.getName()+" = "+p.getValue());
                                i++;
                            }*/
                            String colAlias = conceptsColumns.get(concept);
                            for(AttributeValue attr : rec.getAttributeValues()){
                                 /*if(attr.getName().toString().equals(conceptsColumns.get("ra"))){
                                    coords[0]= attr.getValue().toString();
                                }
                                if(attr.getName().toString().equals(conceptsColumns.get("dec"))){
                                    coords[1]= attr.getValue().toString();
                                }*/
                                if(attr.getName().equals(colAlias)){
                                    if (attr.getValue() != null && !attr.getValue().equals("")) {
                                        if(concept.getPropertyFromName("category").getValue().contains("properties")){
                                            if (!firstProp) {
                                                properties += ",";
                                            }
                                            else {
                                                firstProp = false;
                                            }
                                            properties += "\"" + attr.getName() + "\":\"" + attr.getValue() + "\"";
                                        }
                                        if(concept.getPropertyFromName("category").getValue().contains("geometry")){
                                            if(concept.getName().equals("ra")){
                                                coords[0]= attr.getValue().toString();
                                            }else if(concept.getName().equals("dec")){
                                                coords[1]= attr.getValue().toString();
                                            }
                                        }
                                    }
                                }
                            }  
                        }
                                                
                        geometry += "\"coordinates\": ["+coords[0]+","+coords[1]+"],";
                        geometry += "\"referencesystem\": \"ICRS\",\"type\": \"Point\"}";
                        // start feature
                        writer.write("{");
                        writer.write("\"type\":\"feature\",");
                        // start geometry
                        writer.write("\"geometry\":{");
                        writer.write(geometry);
                        // end geometry
                        writer.write(",");
                        // start properties
                        writer.write("\"properties\":{");
                        writer.write(properties);
                        // end properties
                        writer.write("}");
                        // end feature
                        writer.write("}");

                    }
                    // end features
                    writer.write("]");


                    }catch (SitoolsException ex) {
                        Logger.getLogger(DatasetToJson.class.getName()).log(Level.SEVERE, null, ex);
                }finally {
                    writer.write("}");
                    if (databaseRequest != null) {
                      try {
                        databaseRequest.close();
                      }catch (SitoolsException e) {
                        e.printStackTrace();
                      }
                    }
                    if (writer != null) {
                    writer.flush();
                    }
                }

            }
        };
        return repr;
        
    }
    
    private HashMap<Concept,String> getcolumnAliasFromDico(String dicoName, DictionaryMappingDTO dico, DataSetApplication datasetApp){
        
        dico = datasetApp.getColumnConceptMappingDTO(dicoName);
        final List<ColumnConceptMappingDTO> colConceptMappingDTOList = dico.getMapping();
        final HashMap<Concept,String> conceptColumn = new HashMap<Concept,String>();
        
        for(ColumnConceptMappingDTO concepts : colConceptMappingDTOList){
            conceptColumn.put(concepts.getConcept(), concepts.getColumnAlias());
        }
        return conceptColumn;
    }
    
    /*private static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
        if (hm.get(o).equals(value)) {
            return o;
          }
        }
        return null;
    }*/
}