package fr.ias.sitools.filters.basic;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.util.SQLUtils;
import fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter;

public class TextFieldSQLLikeFilter extends AbstractFormFilter {

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** DefaultType */
    TEXTFIELDSQLLIKEINSENSITIVE
  } 

  public TextFieldSQLLikeFilter() {
    super();
    this.setName("TextFieldSQLLikeFilter");
    this.setDescription("Required when using TextField with a sql like request");
    this.setClassAuthor("MNICOLAS");
    this.setClassOwner("IAS");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);
    
    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

     ParameterInfo param1 = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "TEXTFIELDSQLLIKEINSENSITIVE|columnAlias|value");
     rpd.put("0", param1);
     
     param1 = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
     "TEXTFIELDSQLLIKEINSENSITIVE|dictionaryName,conceptName|value");
     rpd.put("2", param1);
  
     this.setRequestParamsDescription(rpd);
  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    DataSetApplication dsApplication = null;
    getContext().getLogger().log(Level.CONFIG,"*********************** Je rentre dans le createPredicats du TEXTFIELDSQLLIKEINSENSITIVE");
    DataSet ds = null;
    boolean isConcept = true;
    Form params = request.getResourceRef().getQueryAsForm();
    boolean filterExists = true;
    int i = 0;
    
    while (filterExists) {
      // first check if the filter is applied on a Concept or not
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
        if (trouve) {
          if (dsApplication == null) {
            dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
            ds = dsApplication.getDataSet();
          }
          String columnAlias = null;
          if (parameters.length >= VALUES) {

           columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
            if (columnAlias != null) {
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null && col.getFilter() != null && col.getFilter()) {
                // get the value and escape it to avoid SQL injection
                String value = SQLUtils.escapeString(parameters[VALUES]);
                
                Predicat predicat = new Predicat();
                if (value != null) {
                  predicat.setLeftString("UPPER("+col.getColumnAlias()+")");
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  value = value.replace("*", "%");
                  
                  predicat.setCompareOperator(Operator.LIKE);
                  predicat.setRightValue("'" + value.toUpperCase() + "'");

                  predicats.add(predicat);
                }
              }
            }
          }
        }
      }

      else {
        filterExists = false;
      }
    }
    
    return predicats;
  }

  @Override
  public Validator<AbstractFilter> getValidator() {
    // TODO Auto-generated method stub
    return null;
  }
}
