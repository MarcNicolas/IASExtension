package fr.ias.sitools.filters.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter;
import fr.cnes.sitools.util.SQLUtils;

public class GroupCheckBoxMasterLigthCurves extends AbstractFormFilter {
  
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
  private static final int NUMBER_OF_VALUES = 4;

  private String[] values;
  
  private enum TYPE_COMPONENT {
    /** DefaultType */
    GROUP_CHECKBOX_LIGHTCURVES_COROT
  } 
  //private static final Logger LOGGER = Logger.getLogger(GroupCheckBoxMasterLigthCurves.class.getName());
  
  public GroupCheckBoxMasterLigthCurves() {
    super();
    this.setName("GroupCheckBoxLightCurves");
    this.setDescription("Required when using the LightCurves checkbox component");
    this.setClassAuthor("MNICOLAS");
    this.setClassOwner("IAS");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);
    
    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

     ParameterInfo param1 = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "GROUP_CHECKBOX_LIGHTCURVES_COROT|columnAlias1,columnAlias2" +
     		",columnAlias3,columnAlias4|starChr|starMon|star512|star32");
     rpd.put("0", param1);
     this.setRequestParamsDescription(rpd);
  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    DataSetApplication dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
    DataSet ds =  dsApplication.getDataSet();

    Form params = request.getResourceRef().getQueryAsForm();
    boolean filterExists = true;
    int i = 0;
    while (filterExists) {
      String index = TEMPLATE_PARAM.replace("#", Integer.toString(i++));
      String formParam = params.getFirstValue(index);
      if (formParam != null) {
        String[] parameters = formParam.split("\\|");
        TYPE_COMPONENT[] types = TYPE_COMPONENT.values();
        Boolean trouve = false;
        for (TYPE_COMPONENT typeCmp : types) {
          if (typeCmp.name().equals(parameters[TYPE])) {
            trouve = true;
          }
        }
        if (trouve) {
          if (checkValues(parameters)) {
            String[] columnsAlias = parameters[COLUMN].split(",");
            ArrayList<Column> columns = new ArrayList<Column>();
            for (String columnAlias : columnsAlias) {
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null) {
                columns.add(col);
              }
            }

            int k =0;
            for(Column col : columns){
              Predicat predicat = new Predicat();
              predicat.setLeftAttribute(col);
              predicat.setNbOpenedParanthesis(0);
              predicat.setNbClosedParanthesis(0);
              predicat.setCompareOperator(Operator.EQ);
              predicat.setRightValue("'" + Boolean.valueOf(SQLUtils.escapeString(values[k])) + "'");
              if(!(Boolean.valueOf(SQLUtils.escapeString(values[k])))){
                predicats.add(predicat);
              }
              k++;
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

}
