/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.tap;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.ivoa.xml.votable.v1.Info;
import org.restlet.Context;
import org.restlet.Request;

/**
 *
 * @author marc
 */
public class TableAccessProtocolInputParameters implements DataModelInterface {
     
    /**
    * Data model that stores the metadata response of the service.
    */
    private final transient Map dataModel = new HashMap();
    /**
    * Request.
    */
    private final transient Request request;
    /**
    * Context.
    */
    private final transient Context context;
    /**
    * Application where this resources is linked.
    */
    private final transient DataSetApplication datasetApp;
    /**
    * Configuration parameters of this resource.
    */
    private final transient ResourceModel resourceModel;

    private final String query;
    private final String format;
   
    
    /**
    * Constructs the objet that returns the metadata of the service.
    * @param datasetAppVal application
    * @param requestVal request
    * @param contextVal context
    * @param resourceModelVal configuration parameters
    */
    public TableAccessProtocolInputParameters(final DataSetApplication datasetAppVal, final Request requestVal, final Context contextVal, final ResourceModel resourceModelVal) {
        this.request = requestVal;
        this.context = contextVal;
        this.datasetApp = datasetAppVal;
        this.resourceModel = resourceModelVal;
        final String requestType = this.request.getResourceRef().getQueryAsForm().getFirstValue(TableAccessProtocolLibrary.REQUEST);
        final String langRequest = this.request.getResourceRef().getQueryAsForm().getFirstValue(TableAccessProtocolLibrary.LANG);
        final String phase = this.request.getResourceRef().getQueryAsForm().getFirstValue(TableAccessProtocolLibrary.PHASE);
        this.query = this.request.getResourceRef().getQueryAsForm().getFirstValue(TableAccessProtocolLibrary.QUERY);
        this.format = this.request.getResourceRef().getQueryAsForm().getFirstValue(TableAccessProtocolLibrary.FORMAT);
        //if(langRequestTableAccessProtocolLibrary.langSupported.)
        //fillMetadataFormat();
    }
    
   /**
   * Fills metadata response.
   */
  private void fillMetadataFormat() {
      
    this.dataModel.put("description", this.resourceModel.getParameterByName(TableAccessProtocolLibrary.DESCRIPTION).getValue());

    final Info info = new Info();
    info.setName("QUERY_STATUS");
    info.setValueAttribute("OK");
    final List<Info> listInfos = new ArrayList<Info>();
    listInfos.add(info);
    this.dataModel.put("infos", listInfos);
   
    /* ON EN N'A PAS BESOIN
    final List<Param> listParam = new ArrayList<Param>();
    Param param = new Param();
    param.setName("INPUT:POS");
    param.setValue("0,0");
    param.setDatatype(DataType.DOUBLE);
    AnyTEXT anyText = new AnyTEXT();
    anyText.getContent().add("Search Position in the form ra,dec where ra and dec are given in decimal degrees in the ICRS coordinate system.");
    param.setDESCRIPTION(anyText);
    listParam.add(param);

    param = new Param();
    param.setName("INPUT:SIZE");
    param.setValue("0.05");
    param.setDatatype(DataType.DOUBLE);
    anyText = new AnyTEXT();
    anyText.getContent().add("Size of search region in the RA and Dec directions.");
    param.setDESCRIPTION(anyText);
    listParam.add(param);

    param = new Param();
    param.setName("INPUT:FORMAT");
    param.setValue(TableAccessProtocolLibrary.ParamStandardFormat.ALL.name());
    param.setDatatype(DataType.CHAR);
    param.setArraysize("*");
    AnyTEXT anyText = new AnyTEXT();
    anyText.getContent().add("Requested format of result.");
    param.setDESCRIPTION(anyText);
    

    //TODO : le faire pour chaque format
    listParam.add(param);
 ON EN N'A PAS BESOIN    
    param = new Param();
    param.setName("INPUT:INTERSECT");
    param.setValue(this.resourceModel.getParameterByName(TableAccessProtocolLibrary.INTERSECT).getValue());
    param.setDatatype(DataType.CHAR);
    anyText = new AnyTEXT();
    anyText.getContent().add("Choice of overlap with requested region.");
    param.setDESCRIPTION(anyText);
    listParam.add(param);

    param = new Param();
    param.setName("INPUT:VERB");
    param.setValue(this.resourceModel.getParameterByName(TableAccessProtocolLibrary.VERB).getValue());
    param.setDatatype(DataType.INT);
    anyText = new AnyTEXT();
    anyText.getContent().add("Verbosity level, controlling the number of columns returned.");
    param.setDESCRIPTION(anyText);
    listParam.add(param);

    dataModel.put("params", listParam);

    String dictionaryName = resourceModel.getParameterByName(TableAccessProtocolLibrary.DICTIONARY).getValue();
    final List<String> columnList = new ArrayList<String>();
    List<Field> fieldList = new ArrayList<Field>();
    try {
        List<ColumnConceptMappingDTO> mappingList = getDicoFromConfiguration(datasetApp, dictionaryName);
        setFields(fieldList, columnList, mappingList);
    }catch (SitoolsException ex) {
             
    }
   */ 
  }
    
    @Override
    public final Map getDataModel() {
      return Collections.unmodifiableMap(this.dataModel);
    }

    /* GETTER DE LA CLASSE */
    public String getQuery() {
        return query;
    }
    public String getFormat() {
        return format;
    }
    public Context getContext() {
        return context;
    }

    public Request getRequest() {
        return request;
    }
    public DataSetApplication getDatasetApp() {
        return datasetApp;
    }

    public ResourceModel getResourceModel() {
        return resourceModel;
    }
 
}
