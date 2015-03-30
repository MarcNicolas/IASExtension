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
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    final private String mimeTypeFits = "application/fits";
    
    final private int maxResultsSend = 999999999;
    
    final private double ratioSpolyToRaDec = 57.295779513;
    
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
        //repr.setMediaType(MediaType.APPLICATION_JSON);
        return repr;
    }

    private Representation execute() {

        dicoNameString = DatasetToJsonModel.DICO_PARAM_NAME;
        
        final Representation repr;
        repr = new WriterRepresentation(MediaType.APPLICATION_JSON) { ;            
                //repr =
                @Override
                public void write(Writer writer) throws IOException {
                    boolean jsonP = false;
                    String cb =  getRequest().getResourceRef().getQueryAsForm().getFirstValue("callback");
                    String limit = null;
                    limit = getRequest().getResourceRef().getQueryAsForm().getFirstValue("limit");
                    
                    if (cb != null) {
                        jsonP = true;
                        //getResponse().set //setContentType("text/javascript");
                        this.setMediaType(MediaType.TEXT_JAVASCRIPT);
                    } else {
                        this.setMediaType(MediaType.APPLICATION_JSON);
                    }
                    
                    
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
                    final DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();
                    LOG.log(Level.INFO,"***************************************** limit : "+ limit);
                    if(limit == null){
                        LOG.log(Level.INFO," ------------------- JE SUIS DANS LE IF");
                        params.setPaginationExtend(maxResultsSend);
                    }else if(limit.equals("25")){
                        LOG.log(Level.INFO," ------------------- JE SUIS DANS LE ELSE IF");
                        params.setPaginationExtend(maxResultsSend);
                    }else{
                        LOG.log(Level.INFO," ------------------- JE SUIS DANS LE ELSE");
                        params.setPaginationExtend(Integer.parseInt(limit));
                    }
                    //params.setPaginationExtend(maxResultsSend);
                    //params.setPaginationExtend(datasetApp.getDataSet().getNbRecords());
                    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
                    
                    try {
                        databaseRequest.createRequest();
                        
                    }catch (SitoolsException e) {
                        e.printStackTrace();
                    }
                    if(jsonP){
                        writer.write(cb + "(");
                    }
                    String response = "";
                    //response = "{";
                    writer.write("{");
                    //response += "\"type\":\"FeatureCollection\",";
                    writer.write("\"type\":\"FeatureCollection\",");
                    // start features
                    //response +="\"totalResults\":" + databaseRequest.getTotalCount() + ",";
                    writer.write("\"totalResults\":" + databaseRequest.getTotalCount() + ",");
                    //response +="\"features\":[";
                    writer.write("\"features\":[");
                    // Next for reading first record
                    
                    try {
                        boolean first = true;
                        while (databaseRequest.nextResult()) {                           
                            Record rec = databaseRequest.getRecord();                            
                            if (!first) {
                                //response += ",";
                                writer.write(",");
                            }
                            else {
                                first = false;
                            }
                            // creates a geometry and a properties string
                            String geometry = "";
                            String services = "";
                            String properties = "";
                            boolean firstProp = true;
                            String coords[] = new String[2];
                            String coordinateReference = "";
                            String urlDownloadFits = "";
                            String spoly = "";
                            
                            for(Concept concept : conceptsColumns.keySet()){
                                /*if(cpt%100 == 0){
                                    LOG.log(Level.INFO, "Je suis dans le DANS la boucle WHILE sur les records, et dans la boucle FOR des concepts et cpt = "+cpt);
                                }*/
                                concept.getPropertyFromName("category");
                                String colAlias = conceptsColumns.get(concept);
                                for(AttributeValue attr : rec.getAttributeValues()){
                                    if(attr.getName().equals(colAlias) && attr.getValue() != null && !attr.getValue().equals("")){
                                        if(concept.getPropertyFromName("category").getValue().contains("properties")){
                                            if (!firstProp) {
                                                properties += ",";
                                            }
                                            else {
                                                firstProp = false;
                                            }
                                            properties += "\"" + concept.getName().toString() + "\":\"" + attr.getValue() + "\"";
                                        }
                                        if(concept.getPropertyFromName("category").getValue().contains("geometry")){
                                            if(concept.getName().equals("ra")){
                                                coords[0]= attr.getValue().toString();
                                            }else if(concept.getName().equals("dec")){
                                                coords[1]= attr.getValue().toString();
                                            }
                                            if(concept.getName().equals("coordref")){
                                                coordinateReference = attr.getValue().toString();
                                            }
                                            if(concept.getName().equals("spoly")){
                                                spoly = attr.getValue().toString();
                                            }
                                        }
                                        if(concept.getPropertyFromName("category").getValue().contains("services")){
                                            if(concept.getName().equals("download")){
                                                urlDownloadFits = attr.getValue().toString();
                                            }
                                        }

                                    }
                                }
                            }
                            
                            // Set the geometry
                            geometry = setGeometry(coords, coordinateReference, spoly);
                            // Set The services
                            if(!urlDownloadFits.isEmpty()){
                                services = setServices(urlDownloadFits);
                            }
                            // start feature
                            //response += "{";
                            writer.write("{");
                            //response += "\"type\":\"feature\",";
                            writer.write("\"type\":\"feature\",");
                            // start geometry
                            //response += "\"geometry\":{";
                            writer.write("\"geometry\":{");
                            //response += geometry;
                            writer.write(geometry);
                            //response += "}";
                            writer.write("}");
                            // end geometry
                            //response += ",";
                            writer.write(",");
                            // start properties
                            //response += "\"properties\":{";
                            writer.write("\"properties\":{");
                            //response += properties;
                            writer.write(properties);
                            // end properties
                            //response += "}";
                            writer.write("}");
                            
                            // start services
                            if(!services.equals("")){
                                //response += ",";
                                writer.write(",");
                                //response += "\"services\":{";
                                writer.write("\"services\":{");
                                //response += services;
                                writer.write(services);
                                // end services
                                //response += "}";
                                writer.write("}");
                            }
                            
                            // end feature
                            //response += "}";
                            writer.write("}");
                        }
                        // end features
                        //response += "]";
                        writer.write("]");                                                
                        
                    }catch (SitoolsException ex) {
                        Logger.getLogger(DatasetToJson.class.getName()).log(Level.SEVERE, null, ex);
                    }finally {
                        //response += "}";
                        writer.write("}");
                        if (databaseRequest != null) {
                            try {
                                databaseRequest.close();
                            }catch (SitoolsException e) {
                                e.printStackTrace();
                            }
                        }
                        if (writer != null) {
                            //LOG.log(Level.INFO, "Je suis dans le finally avant le if jsonP");
                            if(jsonP){
                                //LOG.log(Level.INFO, "Je suis dans le finally dans le IF du if jsonP");
                                //String response2 = cb + "("+response +");";
                                writer.write(");");
                                writer.flush();
                            }else{
                                //LOG.log(Level.INFO, "Je suis dans le finally dans le ELSE du if jsonP");
                                //writer.write(response);
                                writer.flush();
                            }
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
    
    private String setGeometry(String[] coords, String coordRef, String spoly){
        String geometry = new String();
        String[] spolyStringTmp = new String[4];
        String[] spolyConverted = new String[4];
        if(spoly.equals("")){
            geometry = "\"coordinates\": ["+coords[0]+","+coords[1]+"],";
            geometry += "\"referencesystem\": \""+coordRef+"\",\"type\": \"Point\"";
        }else{
            
            String[] tmpString = spoly.split("\\)");
            //LOG.log(Level.INFO, " SPOLY : "+spoly);
            tmpString[0] = tmpString[0].substring(2, tmpString[0].length());
            spolyStringTmp[0] = tmpString[0];
            //LOG.log(Level.INFO, " i : 0 = "+spolyStringTmp[0]);
            for(int i=1;i<4;i++){
                spolyStringTmp[i] = tmpString[i].substring(2, tmpString[i].length());
                //LOG.log(Level.INFO, "spolyStringTmp  i :"+i+ " = "+spolyStringTmp[i]);
            }
            
            for(int k=0;k<spolyStringTmp.length;k++){
                spolyConverted[k] = "["+String.valueOf((Double.parseDouble(spolyStringTmp[k].split(",")[0])*ratioSpolyToRaDec))
                        +" , "+String.valueOf((Double.parseDouble(spolyStringTmp[k].split(",")[1])*ratioSpolyToRaDec))+"]";
                //LOG.log(Level.INFO, " spolyConverted k :"+k+ " = "+spolyConverted[k]);
            }
            
            geometry = "\"coordinates\":[["+spolyConverted[3]+","+spolyConverted[2]+","+spolyConverted[1]+","+spolyConverted[0]+","+spolyConverted[3]+"]],";
            geometry += "\"referencesystem\": \""+coordRef+"\",\"type\": \"Polygon\"";
        }
        
        return  geometry;
    }
    
    private String setServices(String urlDownFits){
        String services = new String();
        services += "\"download\":{ \"mimetype\":\""+mimeTypeFits+"\",";
        services += "\"url\":\""+urlDownFits+"\"}";
        return services;
    }
}