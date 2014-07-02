/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.filters.basic;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter;
import static fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter.TEMPLATE_PARAM;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.util.SQLUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

/**
 *
 * @author marc
 */
public class TextAreaCorotFilter  extends AbstractFormFilter {
    
    private enum TYPE_COMPONENT {
    /** DefaultType */
    TEXTAREA,
    TEXTAREAHDNUMBERCOROT
    }
    
    private String[] values;
    /**
   * The index of TYPE
   */
    private static final int TYPE = 0;
    /**
    * The index of COLUMN
    */
    private static final int COLUMN = 1;
    /**
    * The index of Values
    */
    private static final int VALUES = 2;
    
    /**
    * The number of values
    */
    private static final int NUMBER_OF_VALUES = 1;
    
    public TextAreaCorotFilter() {
        super();
        this.setName("TextAreaCorotFilter");
        this.setDescription("Required when using TextAreaCorot component");
        this.setClassAuthor("MNICOLAS");
        this.setClassOwner("IAS");
        this.setClassVersion("0.2");
        this.setDefaultFilter(true);
        
        HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

        ParameterInfo param1 = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "TEXTAREA|columnAlias|value");
        rpd.put("0", param1);
        ParameterInfo param2 = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "TEXTAREAHDNUMBERCOROT|columnAlias|value");
        rpd.put("1", param2);
     
        ParameterInfo param3 = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,"TEXTAREA|dictionaryName,conceptName|value");
        rpd.put("2", param3);
        ParameterInfo param4 = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,"TEXTAREAHDNUMBERCOROT|dictionaryName,conceptName|value");
        rpd.put("3", param4);
  
        this.setRequestParamsDescription(rpd);
    
    }
        

    @Override
    public List<Predicat> createPredicats(Request rqst, List<Predicat> predicats) throws Exception {
        DataSetApplication dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
        DataSet ds =  dsApplication.getDataSet();
        boolean isConcept = true;
        Form params = rqst.getResourceRef().getQueryAsForm();
        boolean filterExists = true;
        int i = 0;
        while (filterExists) {
            String index = TEMPLATE_PARAM_CONCEPT.replace("#", Integer.toString(i));
            String formParam = params.getFirstValue(index);
            if (formParam == null) {
                isConcept = false;
                index = TEMPLATE_PARAM.replace("#", Integer.toString(i));
                formParam = params.getFirstValue(index);
            }
            i++;
            if (formParam != null) {
                String[] parameters = formParam.split("\\|");
                TYPE_COMPONENT[] types = TYPE_COMPONENT.values();
                Boolean trouve = false;
                for (TYPE_COMPONENT typeCmp : types) {
                    if (typeCmp.name().equals(parameters[TYPE])) {
                        trouve = true;
                    }
                }
            
                if (trouve && parameters[TYPE].equalsIgnoreCase("TEXTAREA")) {
                    if (checkValues(parameters)) {
                        String[] columnsAlias = parameters[COLUMN].split(",");
                        ArrayList<Column> columns = new ArrayList<Column>();
                        for (String columnAlias : columnsAlias) {
                            Column col = ds.findByColumnAlias(columnAlias);
                            if (col != null) {
                                columns.add(col);
                            }
                        }
                        String value = String.valueOf(SQLUtils.escapeString(values[0]));

                        List<String> valuesFilter = getValuesCorotId(value);

                        if(valuesFilter != null){
                            
                            String columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
                        
                            if (columnAlias != null) {
                                Column col = ds.findByColumnAlias(columnAlias);
                                Predicat predicat = new Predicat();
                                if (!value.isEmpty()) {
                                    predicat.setLeftString(col.getColumnAlias());
                                    predicat.setNbOpenedParanthesis(0);
                                    predicat.setNbClosedParanthesis(0);                                
                                    predicat.setCompareOperator(Operator.IN);
                                    predicat.setRightValue(valuesFilter);
                                    predicats.add(predicat);
                                }
                            }
                        }
                    }    
                }else if(trouve && parameters[TYPE].equalsIgnoreCase("TEXTAREAHDNUMBERCOROT")){
                    if (checkValues(parameters)) {
                        
                        String[] columnsAlias = parameters[COLUMN].split(",");
                        ArrayList<Column> columns = new ArrayList<Column>();
                        for (String columnAlias : columnsAlias) {
                            Column col = ds.findByColumnAlias(columnAlias);
                            if (col != null) {
                                columns.add(col);
                            }
                        }

                        String value = String.valueOf(SQLUtils.escapeString(values[0]));
                        List<String> valuesFilter = getValuesHdNumber(value);
                        if(valuesFilter != null){
                            String columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
                            if (columnAlias != null) {
                                Column col = ds.findByColumnAlias(columnAlias);
                                Predicat predicat = new Predicat();
                                if (!value.isEmpty()) {
                                    predicat.setLeftString(col.getColumnAlias());
                                    predicat.setNbOpenedParanthesis(0);
                                    predicat.setNbClosedParanthesis(0);                                
                                    predicat.setCompareOperator(Operator.IN);
                                    predicat.setRightValue(valuesFilter);
                                    predicats.add(predicat);
                                }
                            }
                        }
                    } 
                }
            }else {
                filterExists = false;
            }
        }
        return predicats;
    }

    /**
   * Check the number of values
   * 
   * @param parameters
   *          the values
   * @return true if the number of values is correct
   */
  private boolean checkValues(String[] parameters) {
    values = Arrays.copyOfRange(parameters, VALUES, VALUES + NUMBER_OF_VALUES);
    if (values.length == NUMBER_OF_VALUES) {
      return true;
    }
    return false;
  }
    
    @Override
    public Validator<?> getValidator() {
        // TODO Auto-generated method stub
        return null;
    }
    
    private List<String> getValuesCorotId(String value){
        List<String> valuesFilter = new ArrayList<String>();
        List<String> valuesFilterTmp = new ArrayList<String>();
        List<String> valuesToSearchTmp = new ArrayList<String>();
        String separator = null;
        value = value.replaceAll("\\\\n", "!"); 
        for(int j =0;j<value.length();j++){
            if(!Character.isDigit(value.charAt(j))){
                separator = String.valueOf(value.charAt(j));
                break;
            }                          
        }
        if(separator != null){
                valuesFilterTmp = Arrays.asList(value.split(separator));
        }else{
                valuesFilterTmp = Arrays.asList(value);
        }
//-----------------------------------------------------
        int whiteSpacePlace = -1;
        for(String corotIdTmp : valuesFilterTmp){
            if(corotIdTmp.contains(" ")){
                whiteSpacePlace = corotIdTmp.indexOf(" ");
                if(whiteSpacePlace == 0){
                    int i=0;
                    while(Character.isWhitespace(corotIdTmp.charAt(i))){
                            i++;
                    }
                    corotIdTmp = corotIdTmp.substring(i);
                    whiteSpacePlace = corotIdTmp.indexOf(" ");
                }
                valuesToSearchTmp.add(corotIdTmp.substring(whiteSpacePlace+1));
            }else{
                valuesToSearchTmp.add(corotIdTmp);
            }    
        }
        for(String tmp : valuesToSearchTmp){
            boolean isDigitValue = true;
                for(int l=0;l<tmp.length();l++){
                    if(!Character.isDigit(tmp.charAt(l))){
                        isDigitValue = false;
                    }
                }
                if(isDigitValue){
                    valuesFilter.add(tmp);
                }
        }
//--------------------------------------------------------            
    /*    if(separator != null){
            for(String corotId : valuesToSearchTmp){
                boolean isDigitValue = true;
                for(int l=0;l<corotId.length();l++){
                    if(!Character.isDigit(corotId.charAt(l))){
                        isDigitValue = false;
                    }
                }
                if(isDigitValue){
                    valuesFilter.add(corotId);
                }
            }
        } else {
            valuesFilter.add(value);
        }
        */
        
        return valuesFilter;
    }
    
    private List<String> getValuesHdNumber(String value){
        List<String> valuesToSearchTmp = new ArrayList<String>();
        List<String> valuesToSearch = new ArrayList<String>();
        List<String> tmp = new ArrayList<String>();
        String separator = null;

        value = value.replaceAll("\\\\n", "!"); 
        for(int j =0;j<value.length();j++){
            if(!Character.isLetterOrDigit(value.charAt(j)) && !Character.isWhitespace(value.charAt(j))){
                separator = String.valueOf(value.charAt(j));
                break;
            }           
        }
        if(separator != null){
                tmp = Arrays.asList(value.split(separator));
        }else{
                tmp = Arrays.asList(value);
        }
        int whiteSpacePlace = -1;
        for(String hdNumberTmp : tmp){
            if(hdNumberTmp.contains(" ")){
                whiteSpacePlace = hdNumberTmp.indexOf(" ");
                if(whiteSpacePlace == 0){
                    int i=0;
                    while(Character.isWhitespace(hdNumberTmp.charAt(i))){
                            i++;
                    }
                    hdNumberTmp = hdNumberTmp.substring(i);
                    whiteSpacePlace = hdNumberTmp.indexOf(" ");
                }
                valuesToSearchTmp.add(hdNumberTmp.substring(whiteSpacePlace+1)); 
            }if(hdNumberTmp.toLowerCase().contains("hd") && !hdNumberTmp.contains(" ")){
                valuesToSearchTmp.add(hdNumberTmp.substring(2));
            }if(!hdNumberTmp.toLowerCase().contains("hd") && !hdNumberTmp.contains(" ")){
                valuesToSearchTmp.add(hdNumberTmp);
            }
        }  
        for(String hdNumber : valuesToSearchTmp){
            valuesToSearch.add("HD "+hdNumber);
        }
        return valuesToSearch;
    }
    
}
