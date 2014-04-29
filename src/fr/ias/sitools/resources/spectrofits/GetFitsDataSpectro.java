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
package fr.ias.sitools.resources.spectrofits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

import nom.tam.fits.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * HTML resource
 * 
 * 
 * @author b.hasnoun
 */
public class GetFitsDataSpectro extends SitoolsParameterizedResource {

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
  public Representation getCsv() {
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

  /**
   * Execute the request and return a Representation
   * 
   * @return the HTML representation
   */
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
    int hdu = Integer.parseInt(this.getModel().getParameterByName("hduCube").getValue());
    String waveLocation = this.getModel().getParameterByName("wave").getValue();
    
    if (databaseRequest != null) {

      // Next for reading first record
      try {
        databaseRequest.nextResult();
        Record rec = databaseRequest.getRecord();
        
        AttributeValue attributeValueURL = OrderResourceUtils.getInParam(urlName, rec);
        
        if (attributeValueURL.getValue() != null){
            // get the file path
            String filePath = (String) attributeValueURL.getValue();
            try {
              
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
              System.out.println("Headers - Done !");
              
              
              System.out.println("Getting Wave...");
              // Wave from fits
              if (fits.getHDU(hdu).getHeader().findKey("CDELT3")!=null) {
                System.out.println("... from WCS");

                double naxis3 = fits.getHDU(hdu).getHeader().getDoubleValue("NAXIS3");
                System.out.println("CRVAL3 "+fits.getHDU(hdu).getHeader().findKey("CRVAL3"));
                double crval3 = fits.getHDU(hdu).getHeader().getDoubleValue("CRVAL3");
                double cdelt3 = fits.getHDU(hdu).getHeader().getDoubleValue("CDELT3");
                System.out.println(naxis3+" "+crval3+" "+cdelt3);
                float[] wave = new float[(int)naxis3];
                for (int i = 0 ; i < wave.length; i++) { wave[i] = (float) (crval3 + i * cdelt3); }
                jSON.put("WAVE", wave);
                
              }
              else {
                if (waveHDU!=-1) {
                  System.out.println("No wave data in the cube WCS - Using wave array from ImageIndex HDU table");
                  TableHDU cols = (TableHDU) fits.getHDU(waveHDU);
                  double[] waveD = (double[]) cols.getColumn(0);
                  float[] wave = new float[waveD.length];
                  for (int i = 0 ; i < waveD.length; i++) { wave[i] = (float) waveD[i]; }
                  jSON.put("WAVE", wave);
                } else {
                  System.out.println("No wave data in the cube WCS - Using wave array from specific HDU table - "+waveLocation);
                  if (waveLocation.matches("(\\d+)-(\\d+)")){
                    int waveTable = Integer.parseInt(waveLocation.split("-")[0]);
                    int waveColumn = Integer.parseInt(waveLocation.split("-")[1]);
                    TableHDU cols = (TableHDU) fits.getHDU(waveTable);
                    double[] waveD = (double[]) cols.getColumn(waveColumn);
                    float[] wave = new float[waveD.length];
                    for (int i = 0 ; i < waveD.length; i++) { wave[i] = (float) waveD[i]; }
                    jSON.put("WAVE", wave);
                  } else {
                    System.out.println("No wave data in "+waveLocation);
                  }
                  
                } 
              }
              System.out.println(" Wave - Done !");
              
              System.out.println("Getting Cube Data...");
              // Spectrum from fits
              for (int m=1; m<=2; m++) {
                jSON.put("NAXIS"+m, fits.getHDU(hdu).getHeader().getDoubleValue("NAXIS"+m));
                jSON.put("CRPIX"+m, fits.getHDU(hdu).getHeader().getDoubleValue("CRPIX"+m));
                jSON.put("CRVAL"+m, fits.getHDU(hdu).getHeader().getDoubleValue("CRVAL"+m));
                jSON.put("CDELT"+m, fits.getHDU(hdu).getHeader().getDoubleValue("CDELT"+m));
                jSON.put("CTYPE"+m, fits.getHDU(hdu).getHeader().getStringValue("CTYPE"+m));
              }
              
              jSON.put("NAXIS3", fits.getHDU(hdu).getHeader().getDoubleValue("NAXIS3"));
              
              jSON.put("INFO_QTTY", fits.getHDU(hdu).getHeader().getStringValue("INFO____") );
              jSON.put("UNIT_QTTY", fits.getHDU(hdu).getHeader().getStringValue("QTTY____") );
              jSON.put("INFO_WAVE", fits.getHDU(hdu).getHeader().getStringValue("CTYPE3") );
              jSON.put("UNIT_WAVE", fits.getHDU(hdu).getHeader().getStringValue("CUNIT3") );
              
              double[][][] cubeFits = ((double[][][]) fits.getHDU(hdu).getData().getData());
              int naxis1 = cubeFits[0][0].length;
              int naxis2 = cubeFits[0].length;
              int naxis3 = cubeFits.length;
              
              //Reorganizing cube
              List<List<List<Float>>> cube3DL = new ArrayList<List<List<Float>>>(naxis1);  
              //float cube[][][] = new float[naxis1][naxis2][naxis3];
              for (int x=0;x<naxis1;x++){
                List<List<Float>> list2 = new ArrayList<List<Float>>(naxis2);  
                for (int y=0;y<naxis2;y++){
                  List<Float> list3 = new ArrayList<Float>(naxis3);  
                  for (int z=0;z<naxis3;z++){
                    //cube[x][y][z] = (float) cubeFits[z][y][x];
                    if (cubeFits[z][y][x] != Double.NaN ) { list3.add( (float) cubeFits[z][y][x] ); }
                    else { list3.add(null); }
                  }
                  list2.add(list3);    
                }
                cube3DL.add(list2);  
              }
              
              /*
              List<List<List<Float>>> cube3DL = new ArrayList<List<List<Float>>>(naxis1);  
              for (int i = 0; i < naxis1; i++) {  
                  List<List<Float>> list2 = new ArrayList<List<Float>>(naxis2);  
                  for (int j = 0; j < naxis2; j++) {  
                      List<Float> list3 = new ArrayList<Float>(naxis3);  
                      for (int k = 0; k < naxis3; k++) { 
                        list3.add(cube[i][j][k]);
                        }  
                      list2.add(list3);  
                      }  
                  cube3DL.add(list2);  
                  } 
              */
              
              JSONArray cubeJL= new JSONArray(cube3DL.toString());
              jSON.put("SPECTRUM", cubeJL);
              
              System.out.println(" Cube - Done !");
             
              repr = new JsonRepresentation(jSON);
              
              
            }
            catch (FitsException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
            catch (JSONException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
        }
        databaseRequest.close();
        
      }
      catch (SitoolsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }

    return repr;
    
  }

}
