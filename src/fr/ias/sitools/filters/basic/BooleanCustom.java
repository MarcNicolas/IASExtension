/*******************************************************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.ias.sitools.filters.basic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.util.SQLUtils;

/**
 * Filter defined for Single Value Component
 * 
 * 
 * @author d.arpin
 */
public final class BooleanCustom extends AbstractFilter {
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

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** Boolean Custom type */
    BOOLEAN_CUSTOM
  }
  
  private String[] values;

  private static final Logger LOGGER = Logger.getLogger(BooleanCustom.class.getName());
  
  /** The TEMPLATE_PARAM */
  private static final String TEMPLATE_PARAM = "p[#]";

  /**
   * Default constructor
   */
  public BooleanCustom() {

    super();
    this.setName("BooleanCustom");
    this.setDescription("Required when using Boolean Custom Component");
    this.setClassAuthor("Marc NICOLAS");
    this.setClassOwner("IAS");
    this.setClassVersion("0.9");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "BOOLEAN_CUSTOM|columnAlias|value");
    rpd.put("0", paramInfo);
    this.setRequestParamsDescription(rpd);

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
  
    DataSetApplication dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
    DataSet ds = dsApplication.getDataSet();

    Form params = request.getResourceRef().getQueryAsForm();
    boolean filterExists = true;
    int i = 0;
    // Build predicat for filters param
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
            String columnAlias = null;
            if (parameters.length >= VALUES) {
              
              columnAlias = parameters[COLUMN];
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null && col.getFilter() != null && col.getFilter()) {
                // get the value and escape it to avoid SQL injection
                String value = SQLUtils.escapeString(parameters[VALUES]);
                Predicat predicat = new Predicat();
                if (value != null) {
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.EQ);
                  predicat.setRightValue("'" + value + "'");
                  if(value.equalsIgnoreCase("false")){
                    predicats.add(predicat);
                  }
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
  
  /**
   * Check the number of values
   * 
   * @param parameters
   *          the values
   * @return true if the number of values is correct
   */
  private boolean checkValues(String[] parameters) {
    if(parameters.length >= 3){ 
      values = Arrays.copyOfRange(parameters, VALUES, VALUES + NUMBER_OF_VALUES);
      if (values.length == NUMBER_OF_VALUES) {
        return true;
      }
      return false;
    }else{
      return false;
    }
  }
  

  /**
   * Gets the validator for this Filter
   * 
   * @return the validator for the filter
   */
  @Override
  public Validator<AbstractFilter> getValidator() {
    return new Validator<AbstractFilter>() {
      @Override
      public Set<ConstraintViolation> validate(AbstractFilter item) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

}
