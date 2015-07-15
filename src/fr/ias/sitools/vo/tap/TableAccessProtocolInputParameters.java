/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.tap;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
        //testQuery(query);
    }
    
    private void testQuery(String query){
        /*ADQLParser parser = new ADQLParser();
        ADQLQuery queryParse = parser.parseQuery(query);
        ADQLTranslator translator = new PostgreSQLTranslator();
        translator.translate(queryParse);*/
    }
    
    @Override
    public final Map getDataModel() {
      return Collections.unmodifiableMap(this.dataModel);
    }
    
     public String getQuery() {
        return query;
    }

    public String getFormat() {
        return format;
    }
    
    
}
