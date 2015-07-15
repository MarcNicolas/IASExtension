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
    
    public static final String FORMAT = "FORMAT";
    public static final String QUERY = "QUERY";
    public static final String LANG = "LANG";
    public static final String PHASE = "PHASE";
    public static final String REQUEST = "REQUEST";
    private transient DataSetApplication datasetApp;
    private transient ResourceModel resourceModel;
    private transient Request request;
    private transient Context context;

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

    // Handling input parameters
    final DataModelInterface inputParameters = new TableAccessProtocolInputParameters(datasetApp, request, this.context, this.resourceModel);
    // data model response
    if (inputParameters.getDataModel().containsKey("infos")) {
      dataModel = inputParameters.getDataModel();
    } else {
      final TableAccessProtocolDataModelInterface response = new TableAccessProtocolResponse((TableAccessProtocolInputParameters) inputParameters, resourceModel);
      dataModel = response.getDataModel();
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
    return new VOTableRepresentation(dataModel,"votableTAP.ftl");
  }  
    
}
