package fr.ias.sitools.filters.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
//import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.common.RequestFactory;
import fr.cnes.sitools.dataset.database.jdbc.RequestSql;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.util.SQLUtils;

public class BooleanCorotAsteroAndExo extends AbstractFormFilter {
  
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
    BOOLEAN_CHECKBOX_COROT,
    CHECKBOX_COROT_ASTERO
  } 
 // private static final Logger LOGGER = Logger.getLogger(BooleanCorot.class.getName());
  
  public BooleanCorotAsteroAndExo() {
    super();
    this.setName("BooleanCorotAsteroAndExo");
    this.setDescription("Required when using BooleanCorot for exo or astero data checkbox");
    this.setClassAuthor("MNICOLAS");
    this.setClassOwner("IAS");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);
    
    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

     ParameterInfo param1 = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "BOOLEAN_CHECKBOX_COROT|columnAlias1,columnAlias2" +
     		",columnAlias3,columnAlias4|long|short|center|anticenter");
     rpd.put("0", param1);
     ParameterInfo param2 = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "CHECKBOX_COROT_ASTERO|columnAlias1,columnAlias2" +
     		",columnAlias3,columnAlias4|long|short|center|anticenter");
     rpd.put("1", param2);
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
            SitoolsSQLDataSource dsource = SitoolsSQLDataSourceFactory.getDataSource(ds.getDatasource().getId());
            RequestSql requestSql = RequestFactory.getRequest(dsource.getDsModel().getDriverClass());
            
            String longRunColStr = requestSql.convertColumnToString(columns.get(0));
            String shortRunColStr = requestSql.convertColumnToString(columns.get(1));
            String centerColStr = requestSql.convertColumnToString(columns.get(2));
            String anticenterColStr = requestSql.convertColumnToString(columns.get(3));
            
            boolean longRunValue = Boolean.valueOf(SQLUtils.escapeString(values[0]));
            boolean shortRunValue = Boolean.valueOf(SQLUtils.escapeString(values[1]));
            boolean centerValue = Boolean.valueOf(SQLUtils.escapeString(values[2]));
            boolean anticenterValue = Boolean.valueOf(SQLUtils.escapeString(values[3]));

            
            Predicat predicat = new Predicat();
            
            predicat.setStringDefinition(" AND ("+longRunColStr+" = "+longRunValue+" OR "+shortRunColStr+" = "+shortRunValue+")"+" AND ("+centerColStr+ " = "+ centerValue+" OR "+anticenterColStr+" = "+anticenterValue+")");
            predicats.add(predicat);
            
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
