 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.ias.sitools.astro.resolverName;

import fr.cnes.sitools.astro.representation.GeoJsonRepresentation;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.extensions.astro.application.opensearch.datamodel.FeatureDataModel;
import fr.cnes.sitools.extensions.astro.application.opensearch.datamodel.FeaturesDataModel;
import fr.cnes.sitools.extensions.common.AstroCoordinate;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import java.util.Map;
import java.util.logging.Level;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;



public class  CorotIdResolverResource extends SitoolsParameterizedResource{
    
    String  corotIDValueStr;
    Boolean exoBool;
    
    Double raValue;
    Double decValue;
    ResourceParameter corotIDParam;
    ResourceParameter raParam;
    ResourceParameter decParam;
    
    @Override
    public void sitoolsDescribe() {
        setName("CorotIdResolverResource");
        setDescription("Get Star coordinates from corotID");
    }

    @Override
    public void doInit() {
        super.doInit();
        this.corotIDValueStr = (String) this.getRequestAttributes().get("corotID");
        this.exoBool = Boolean.parseBoolean(getModel().getParametersMap().get("exoBool").getValue());
        
        try{
          int corotIdValuInt = Integer.parseInt(corotIDValueStr);
        }catch(NumberFormatException e){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "CorotID must be a number.");
        }
        if(this.corotIDValueStr.length() != 9 && exoBool){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "CorotID must be composed of 9 digits.");
        }
    }

    /**
    * Get HTML
    * 
    * @return Representation the HTML result
    */
    @Get
    public Representation getCorotIdResolver() {
        return execute();
    }

  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
    info.setIdentifier("get ra and dec coordinates from corotID");
    info.setDocumentation("Method to retrieve coordinates of a star from its corotID");
    addStandardGetRequestInfo(info);
    DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
    DataSetApplication application = (DataSetApplication) getApplication();
    DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  @Override
  protected Representation get(Variant variant) {
    Representation repr = super.get();
    if(repr == null){
        throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"No reccord matches with your query");
    }
    repr.setMediaType(MediaType.APPLICATION_JSON);
    return repr;
  }

  /**
   * Execute the request and return a Representation
   * 
   * @return the HTML representation
   */
  private Representation execute() {
    
    
    Representation repr = null;
    
    // Get context
    Context context = getContext();
 
    // generate the DatabaseRequest
    DataSetApplication datasetApp = (DataSetApplication) getApplication();
    DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());


    this.corotIDParam = this.getModel().getParameterByName("corotIdCol");
    this.raParam = this.getModel().getParameterByName("raCol");
    this.decParam = this.getModel().getParameterByName("decCol");
    

    // Get request parameters
    if (datasetApp.getConverterChained() != null) {
      datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
    }
    // Get DatabaseRequestParameters
    DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();
    DataSet ds = datasetApp.getDataSet();
    createQueryCorotID(params,ds);
    
    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);

  /*  AttributeValue attrRaValue = null;
    AttributeValue attrDecValue = null;
   */
      try {

          if (params.getDistinct()) {
            databaseRequest.createDistinctRequest();
        }else {
            databaseRequest.createRequest();
        }
        // Recuperation dans la base du fichiers fits
        context.getLogger().log(Level.INFO, "nbr de resultat : "+databaseRequest.getCount());
        while (databaseRequest.nextResult()) {
            Record rec = databaseRequest.getRecord();
            this.raValue = Double.parseDouble(OrderResourceUtils.getInParam(raParam, rec).getValue().toString());
            this.decValue = Double.parseDouble(OrderResourceUtils.getInParam(decParam, rec).getValue().toString());

            Map a = getDataModel();
            repr = new GeoJsonRepresentation(a);
            
        }
      } catch (Exception e) {
           throw new ResourceException(Status.SERVER_ERROR_INTERNAL,e.getMessage());
      }
    return repr;
  }
  
  public Map getDataModel(){
      final FeaturesDataModel features = new FeaturesDataModel();
      final FeatureDataModel feature = new FeatureDataModel();
      AstroCoordinate a = new AstroCoordinate(this.raValue,this.decValue);
      
      feature.createGeometry(String.format("[%s,%s]", this.raValue,this.decValue),"Point");
      feature.createCrs(a.getCoordinateSystem().getCrs());
      feature.addProperty("credits", "CDS");
      feature.addProperty("identifier", "CDS0");
      features.addFeature(feature);
      return features.getFeatures();
  }
  
  private void createQueryCorotID(DatabaseRequestParameters params, DataSet ds){
      Column corotIDCol = ds.findByColumnAlias(this.corotIDParam.getValue());
      Predicat pred = new Predicat();
      pred.setLogicOperator("AND");
      pred.setLeftAttribute(corotIDCol);
      pred.setCompareOperator(Operator.EQ);
      pred.setRightValue(this.corotIDValueStr);
      params.getPredicats().add(pred);
      if(exoBool){
        pred = new Predicat();
        Column en2WinDescCol = ds.findByColumnAlias("en2_windescriptor");
        pred.setLogicOperator("AND");
        pred.setLeftAttribute(en2WinDescCol);
        pred.setCompareOperator(Operator.EQ);
        pred.setRightValue("false");
        params.getPredicats().add(pred);
      }
  }
}