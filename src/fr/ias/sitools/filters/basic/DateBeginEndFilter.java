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
 * Filter defined for Date Between Component
 * 
 * 
 * @author d.arpin
 * <a href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3411383&group_id=531341">[3411383]</a><br/>
 * 2011/09/19 d.arpin {add quotes arround date Value}
 */
public final class DateBeginEndFilter extends AbstractFilter {
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

  /** The TEMPLATE_PARAM */
  private static final String TEMPLATE_PARAM = "p[#]";
  /**
   * the values from to
   */
  private String[] values;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** DefaultType */
    DATE_BEGIN_END
  }

  /**
   * Default constructor
   */
  public DateBeginEndFilter() {

    super();
    this.setName("DateBeginEndFilter");
    this.setDescription("Required when using Date Between Components between 2 columns");

    this.setClassAuthor("HUSSON@IAS");
    this.setClassAuthor("HUSSON@IAS");
    this.setClassOwner("IAS");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
    "DATE_BEGIN_END|columnFrom,columnTo|valueFrom|valueTo");
    rpd.put("0", paramInfo);
    this.setRequestParamsDescription(rpd);
    //

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    // Get the dataset
    DataSetApplication dsApplication = null;
    DataSet ds = null;

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
          if (dsApplication == null) {
            dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
            ds = dsApplication.getDataSet();
          }
          String columnfrom = null;
          String columnto = null;
          if (parameters.length >= VALUES) {
            columnfrom = parameters[COLUMN].split(",")[0];
            columnto = parameters[COLUMN].split(",")[1];

            Column colfrom = ds.findByColumnAlias(columnfrom);
            Column colto = ds.findByColumnAlias(columnto);

            if (colfrom != null && colfrom.getFilter() != null && colfrom.getFilter() && checkValues(parameters) && colto != null && colto.getFilter() != null && colto.getFilter()) {
              // escape the values to avoid SQL injection
              String valuefrom = "'" + SQLUtils.escapeString(values[0]) + "'";
              String valueto = "'" + SQLUtils.escapeString(values[1]) + "'";

              Predicat predicat = new Predicat();
              predicat.setLeftAttribute(colfrom);
              predicat.setNbOpenedParanthesis(1);
              predicat.setNbClosedParanthesis(0);
              predicat.setCompareOperator(Operator.GTE);
              predicat.setRightValue(valuefrom);
              predicats.add(predicat);
              predicat = new Predicat();
              predicat.setLeftAttribute(colto);
              predicat.setNbOpenedParanthesis(0);
              predicat.setNbClosedParanthesis(1);
              predicat.setCompareOperator(Operator.LTE);
              predicat.setRightValue(valueto);
              predicats.add(predicat);
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
   * Check values of the form
   * 
   * @param parameters
   *          the parameters of the filter
   * @return true if values agree
   */
  private boolean checkValues(String[] parameters) {
    values = Arrays.copyOfRange(parameters, VALUES, parameters.length);
    if (values.length == 2) {
      return true;
    }
    return false;
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
        return null;
      }
    };
  }

}
