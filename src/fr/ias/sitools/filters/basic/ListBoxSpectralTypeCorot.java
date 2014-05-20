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
import static fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter.VALUES;

/**
 * Filter defined for Multiple Value Component
 * 
 * 
 * @author d.arpin
 */
public final class ListBoxSpectralTypeCorot extends AbstractFormFilter {

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** List box Spectral Type for Corot */
    LISTBOXSPECTRALTYPECOROT
  }
  
  private final List<String> spectralTypeO = Arrays.asList("O5","O6","O8","O8.5","O9");
  private final List<String> spectralTypeB = Arrays.asList("B0","B1","B2","B3","B5","B6","B8","B9","B2.5","B.5");
  private final List<String> spectralTypeA = Arrays.asList("A0","A1","A2","A3","A4","A5","A6","A7","A8","A9");
  private final List<String> spectralTypeF = Arrays.asList("F0","F1","F2","F3","F4","F5","F6","F7","F8","F9");
  private final List<String> spectralTypeG = Arrays.asList("G0","G1","G2","G3","G4","G5","G6","G7","G8","G9");
  private final List<String> spectralTypeK = Arrays.asList("K0","K1","K2","K3","K4","K5","K6","K7","K8","K9");
  private final List<String> spectralTypeM = Arrays.asList("M","M0","M1","M2","M3","M4","M5","M6","M7","M8","M9");
  
  /**
   * Default constructor
   */
  public ListBoxSpectralTypeCorot() {

    super();
    this.setName("ListBoxSpectralTypeCorot");
    this.setDescription("Required when using List Box Spectral Type for Corot Components");

    this.setClassAuthor("Mnicolas");
    this.setClassOwner("IAS");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
        "LISTBOXSPECTRALTYPECOROT|columnAlias|value1|...|value n");
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

              if (col != null && col.getFilter() != null && col.getFilter()) {
                String[] values = Arrays.copyOfRange(parameters, VALUES, parameters.length);
                Predicat predicat = new Predicat();
                if (values != null) {
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.IN);

                  boolean all = false;
                  List<String> spectralTypeSeeked = new ArrayList<String>();
                  for (String value : values) {
                      if(value.equalsIgnoreCase("O")){
                          spectralTypeSeeked.addAll(this.spectralTypeO);
                      }else if(value.equalsIgnoreCase("B")){
                          spectralTypeSeeked.addAll(this.spectralTypeB);
                      }else if(value.equalsIgnoreCase("A")){
                          spectralTypeSeeked.addAll(this.spectralTypeA);
                      }else if(value.equalsIgnoreCase("F")){
                          spectralTypeSeeked.addAll(this.spectralTypeF);
                      }else if(value.equalsIgnoreCase("G")){
                          spectralTypeSeeked.addAll(this.spectralTypeG);
                      }else if(value.equalsIgnoreCase("K")){
                          spectralTypeSeeked.addAll(this.spectralTypeK);
                      }else if(value.equalsIgnoreCase("M")){
                          spectralTypeSeeked.addAll(this.spectralTypeM);
                      }else if(value.equalsIgnoreCase("All")){
                          all = true;
                      }
                      
                    //in.add(SQLUtils.escapeString(value));
                  }
                  if(!spectralTypeSeeked.isEmpty()&& !all) {
                    predicat.setRightValue(spectralTypeSeeked);
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
