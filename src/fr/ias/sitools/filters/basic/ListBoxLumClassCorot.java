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
package fr.ias.sitools.filters.basic;

import java.util.ArrayList;
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
import fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter;
import static fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter.TEMPLATE_PARAM;
import static fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter.TEMPLATE_PARAM_CONCEPT;
import static fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter.TYPE;
import static fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter.VALUES;
import java.util.logging.Level;

/**
 * Filter defined for Multiple Value Component
 * 
 * 
 * @author d.arpin
 */
public final class ListBoxLumClassCorot extends AbstractFormFilter {

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** List box Lum class for Corot */
    LISTBOXLUMCLASSCOROT
  }
  
  /*
    Pour EXO : 
    ""
    "I"
    "II"
    "III"
    "III:"
    "IV"
    "null"
    "V"
    ""
  
    Pour Astero : 
    ""
    "I"
    "II"
    "III"
    "III-IV"
    "IV"
    "V"

  */

  /**
   * Default constructor
   */
  public ListBoxLumClassCorot() {

    super();
    this.setName("ListBoxLumClassCorot");
    this.setDescription("Required when using List Box Lum Class for Corot Components");

    this.setClassAuthor("Mnicolas");
    this.setClassOwner("IAS");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
        "LISTBOXLUMCLASSCOROT|columnAlias|value1|...|value n");
    rpd.put("0", paramInfo);
    this.setRequestParamsDescription(rpd);

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    // Get the dataset
    DataSetApplication dsApplication = null;
    DataSet ds = null;
    boolean isConcept = true;
    Form params = request.getResourceRef().getQueryAsForm();
    boolean filterExists = true;
    int i = 0;
    // Build predicat for filters param
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

            /*
             * columnsAlias = parameters[COLUMN].split(","); ArrayList<Column> columns = new ArrayList<Column>(); for
             * (String columnAlias : columnsAlias) { Column col = ds.findByColumnAlias(columnAlias); if (col != null) {
             * columns.add(col); }
             * 
             * }
             */
            columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
            if (columnAlias != null) {
              Column col = ds.findByColumnAlias(columnAlias);
              List<String> lumClassSeeked = new ArrayList<String>();

              if (col != null && col.getFilter() != null && col.getFilter()) {
                String[] values = Arrays.copyOfRange(parameters, VALUES, parameters.length);
                Predicat predicat = new Predicat();
                if (values != null) {
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.IN);

                  boolean all = false;
                  for (String value : values) {
                      if(value.equalsIgnoreCase("All")){
                        all = true;
                      }else{
                          if(value.equalsIgnoreCase(" ")){
                              lumClassSeeked.add(value);
                              lumClassSeeked.add("null");
                          }else if(value.equalsIgnoreCase("III")){
                              lumClassSeeked.add(value);
                              lumClassSeeked.add("III:");
                          }else{
                            lumClassSeeked.add(value);
                          }
                      }
                  }
                  getContext().getLogger().log(Level.INFO, "lumClassSeeked : "+lumClassSeeked.toArray().toString());
                  if(!lumClassSeeked.isEmpty() && !all) {
                    predicat.setRightValue(lumClassSeeked);
                    predicats.add(predicat);
                  }
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
