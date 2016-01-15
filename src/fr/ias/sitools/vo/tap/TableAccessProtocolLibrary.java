/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ias.sitools.vo.tap;

import fr.ias.sitools.vo.representation.VOTableRepresentation;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import java.util.Map;
import java.util.logging.Level;
import org.restlet.Context;
import org.restlet.Request;

/**
 *
 * @author marc
 */
public class TableAccessProtocolLibrary {

    public enum langSupported {

        ADQL
    };

    public enum formatResultsSupported {

        VOTABLE
    };

    // Pour la requete
    public static final String FORMAT = "FORMAT";
    public static final String QUERY = "QUERY";
    public static final String LANG = "LANG";
    public static final String PHASE = "PHASE";
    public static final String REQUEST = "REQUEST";
    // String pour décomposer la requete ADQL
    public static final String SELECT = "SELECT";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String SELECT_ALL = "*";
    public static final String BLANCK = " ";

    private transient DataSetApplication datasetApp;
    private transient ResourceModel resourceModel;
    private transient Request request;
    private transient Context context;

    /**
     *
     */
    public static final String DICTIONARY = "PARAM_Dictionary";
    //Pour les Metadata
    /**
     *
     */
    public static final String DESCRIPTION = "Description";
    /**
     *
     */
    public static final String INSTRUMENT = "Instrument";
    /**
     *
     */
    public static final String SERVICE_NAME = "Service Name";
    /**
     *
     */
    public static final String MAX_RECORDS = "Max records";

    public TableAccessProtocolLibrary(final DataSetApplication datasetApp, final ResourceModel resourceModel, final Request request, final Context context) {
        this.datasetApp = datasetApp;
        this.resourceModel = resourceModel;
        this.request = request;
        this.context = context;
    }

    /**
     * Fill data Model that will be used in the template.
     *
     * @return data model for the template
     */
    private Map fillDataModel() {
        // init
        Map dataModel = null;
        Map<String, Object> map = this.request.getAttributes();
        /*
         String entityAsText = this.request.getEntityAsText(); 
         String query1 = this.request.getResourceRef().getQuery();
         String queryDecoded = this.request.getResourceRef().getQuery(true);
         String queryNotDecoded = this.request.getResourceRef().getQuery(false);
         */
        String tapRequestType = this.request.getAttributes().get("tapRequestType").toString();
        // Handling input parameters
        final DataModelInterface inputParameters = new TableAccessProtocolInputParameters(datasetApp, request, this.context, this.resourceModel);
        if (tapRequestType.equalsIgnoreCase("sync")) {
            this.context.getLogger().log(Level.INFO, "JE SUIS DANS LE SYNC !!!");
            // data model response
            if (inputParameters.getDataModel().containsKey("infos")) {
                dataModel = inputParameters.getDataModel();
            } else {
                final TableAccessProtocolDataModelInterface response = new TableAccessProtocolResponse((TableAccessProtocolInputParameters) inputParameters, resourceModel);
                dataModel = response.getDataModel();
            }
        } else if (tapRequestType.equalsIgnoreCase("async")) {
            this.context.getLogger().log(Level.INFO, "JE SUIS DANS LE ASYNC !!!");
            TableAccessProtocolAsynchronousResponse asyncTask = new TableAccessProtocolAsynchronousResponse((TableAccessProtocolInputParameters) inputParameters);
            asyncTask.run();
            this.context.getLogger().log(Level.INFO, "Apres le run de la tache async !!!");
            
        } else {
            this.context.getLogger().log(Level.INFO, "JE SUIS DANS NI SYNC NI ASYNC !!!");
        }

        return dataModel;
    }

    /**
     * VOTable response.
     *
     * @return VOTable response
     */
    public final VOTableRepresentation getResponse() {
        final Map dataModel = fillDataModel();
        return new VOTableRepresentation(dataModel, "votable.ftl");
    }

}
