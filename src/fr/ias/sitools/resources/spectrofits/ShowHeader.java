/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.resources.spectrofits;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

/**
 *
 * @author mnicolas
 */
public class ShowHeader extends SitoolsParameterizedResource {
    
    @Override
  public void sitoolsDescribe() {
    setName("GetFitsDataSpectro");
    setDescription("Get cube fits file data");
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
    info.setIdentifier("retrieve records and extract data and metadata from fits file");
    info.setDocumentation("Method to get the data and metadata from a fits file");
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
        
    Representation repr = null;
    
    // generate the DatabaseRequest
    DataSetApplication datasetApp = (DataSetApplication) getApplication();
    DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());

    // Get request parameters
    if (datasetApp.getConverterChained() != null) {
      datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
    }
    // Get DatabaseRequestParameters
    DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();

    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
    
    if (params.getDistinct()) {
      try {
        databaseRequest.createDistinctRequest();
      }
      catch (SitoolsException e) {
        
        e.printStackTrace();
      }
    } else {
      try {
        databaseRequest.createRequest();
      }
      catch (SitoolsException e) {
       
        e.printStackTrace();
      }
    }
    
    ResourceParameter urlName = this.getModel().getParameterByName("colUrl");
    
    if (databaseRequest != null) {

      // Next for reading first record
      try {
        databaseRequest.nextResult();
        Record rec = databaseRequest.getRecord();
        
        AttributeValue attributeValueURL = OrderResourceUtils.getInParam(urlName, rec);
        
        if (attributeValueURL.getValue() != null){
            // get the file path
            String filePath = (String) attributeValueURL.getValue();
            
              
              Fits fits = new Fits(filePath);
              FitsFactory.setUseHierarch(true);
              Header.setLongStringsEnabled(true);
              
              JSONObject jSON = new JSONObject();
              
              int waveHDU = -1;
              
              int nberHDUs = fits.size();
              System.out.println("Number of HDUs: "+nberHDUs);
              jSON.put("HDUs", nberHDUs);
              
              // Headers from fits
              System.out.println("Getting Headers...");
              List<String> headers = new ArrayList<String>(nberHDUs);
              List<List<List<String>>> headersList = new ArrayList<List<List<String>>>(nberHDUs);
              
              for (int i=0;i<nberHDUs;i++){
                
                Header header = fits.getHDU(i).getHeader();

                int nbreCards = header.getNumberOfCards();
                String headerStr = "";
                
                //making HIERARCH mapping
                HashMap<String,String> mapHierarch = new HashMap<String,String>();                
                for (int j=0; j<nbreCards; j++){
                  String cardStr = header.getCard(j);
                  if (cardStr.matches("HIERARCH\\s.*\\s(META.*)\\s*=\\s*(.*)")) {
                    //System.out.println(cardStr);
                    Pattern p = Pattern.compile("HIERARCH\\s.*\\s(META.*)\\s*=\\s*(.*)");
                    Matcher m = p.matcher(cardStr);
                    if (m.find()) {
                      //System.out.println(m.group(1)+" "+m.group(2));
                      mapHierarch.put(m.group(1), m.group(2));
                    }
                  }
                  headerStr = headerStr+cardStr+"<br>";
                }
                
                //List of extensions -- List of List of Cards
                List<List<String>> cardList = new ArrayList<List<String>>(nbreCards);
                for (int j=0; j<nbreCards; j++){
                  
                  List<String> keyValueComment = new ArrayList<String>(nbreCards); 
                  HeaderCard card = header.findCard(header.getKey(j));
                  
                  if ( card!=null ) {
                    
                    //key
                    if (mapHierarch.containsKey(card.getKey())) { 
                      keyValueComment.add( "HIERARCH "+mapHierarch.get(card.getKey()) );
                      }
                    else {
                      keyValueComment.add(card.getKey());
                    }
                    
                    //value
                    keyValueComment.add(card.getValue());
                    
                    if ( card.getKey().contains("EXTNAME") && card.getValue().contains("ImageIndex") ) { 
                      System.out.println("ImageIndex in HDU "+i);
                      waveHDU=i;
                      }
                    
                    //comment
                    String commentCard = card.getComment();
                    if (commentCard!=null) {

                      if (commentCard.contains("&")) {
                        //System.out.println(j+" "+commentCard);
                        commentCard = commentCard.substring(0, commentCard.length()-1);
                        int jTmp = j;
                        while(header.getCard(jTmp+1).startsWith("COMMENT")){
                          String commentCardNext = header.getCard(jTmp+1);
                          if (commentCardNext.contains("&")) { commentCardNext = commentCardNext.substring(0, commentCardNext.length()-1); }
                          commentCard += commentCardNext.split("COMMENT ")[1];
                          jTmp++;
                          }
                        keyValueComment.add(commentCard);
                        }
                      else { keyValueComment.add(commentCard); }
                      
                    } else {keyValueComment.add("");}
                    
                    cardList.add(keyValueComment);
                    
                  }
                  
                }
                
                headersList.add(cardList);
                
                headers.add(headerStr);
                
              }
              jSON.put("HEADERSGRID", headersList);
             getContext().getLogger().log(Level.INFO,"In Show Header Resources : Headers - Done !");
            }
        } catch (FitsException ex) {
            Logger.getLogger(ShowHeader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(ShowHeader.class.getName()).log(Level.SEVERE, null, ex);
        }catch (SitoolsException ex) {
            Logger.getLogger(ShowHeader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ShowHeader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    return repr;
    }
    
}       
