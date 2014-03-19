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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.util.SQLUtils;

/**
 * Filter defined for Single Value Component
 * 
 * 
 * @author d.arpin
 */
public final class NThreeVariability extends AbstractFilter {
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
  
  private static final int NUMBER_OF_VALUES = 2;

  private String[] values;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** Boolean Custom type */
    NTHREE_VARIABILITY
  }

  /** The TEMPLATE_PARAM */
  private static final String TEMPLATE_PARAM = "p[#]";
  
  private static final Logger LOGGER = Logger.getLogger(NThreeVariability.class.getName());

  /**
   * Colonnes de la base ou sont les probabilitées
   */
  private String probClass1Col = "prbclas1_from";
  private String probClass2Col = "prbclas2_from";
  private String probClass3Col = "prbclas3_from";
  
  /**
   * Colonnes de la base ou sont les class de variabilité associée aux probabilitées 
   */
  private String varClass1 = "varclas1_from";
  private String varClass2 = "varclas2_from";
  private String varClass3 = "varclas3_from";
  
  /**
   * Nome de la table
   */
  private String tableName = "exo_n2_view";
  
  private char doubleQuote = '"'; 
  
  /**
   * Default constructor
   */
  public NThreeVariability() {

    super();
    this.setName("NThreeVariability");
    this.setDescription("Required when using NThreeVariability Component");

    this.setClassAuthor("Marc NICOLAS");
    this.setClassAuthor("IAS");
    this.setClassOwner("MN@IAS");
    this.setClassVersion("1.0");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "NTHREE_VARIABILITY|columnAlias|value");
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
        if(trouve){
          if (checkValues(parameters)) {
            
            String varClassSeek = null;
            Double probValue = null;
            try{
              varClassSeek = SQLUtils.escapeString(values[0]);
              probValue = Double.valueOf(values[1]);
            }catch(SitoolsException e){
              e.printStackTrace();
              LOGGER.log(Level.SEVERE,"Probleme lors de la récupération des valeurs dans le NThreeVariability, message d'erreur : "+e.getMessage());
            }
            
            Predicat predicat = new Predicat();
            predicat.setStringDefinition(" AND ("+doubleQuote+tableName+doubleQuote+"."+varClass1+" = "+"'"+varClassSeek+"'"+" AND "+doubleQuote+tableName+doubleQuote+"."+probClass1Col+" >= "+ probValue+") OR " +
            		"("+doubleQuote+tableName+doubleQuote+"."+varClass2+" = "+"'"+varClassSeek+"'"+" AND "+doubleQuote+tableName+doubleQuote+"."+probClass2Col+" >= "+ probValue+") OR ("+
            		doubleQuote+tableName+doubleQuote+"."+varClass3+" = "+"'"+varClassSeek+"'"+" AND "+doubleQuote+tableName+doubleQuote+"."+probClass3Col+" >= "+ probValue+")");
            predicats.add(predicat);
            
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
    values = Arrays.copyOfRange(parameters, VALUES, VALUES + NUMBER_OF_VALUES);
    for(String value : values){
    }
    if (values.length == NUMBER_OF_VALUES) {
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
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

}
