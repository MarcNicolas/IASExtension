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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/*
 * The Model for show fits header resource
 * 
 * 
 * @author b.hasnoun
 */
public class GetFitsDataSpectroModel extends ResourceModel {

  /**
   * Constructor
   */
  public GetFitsDataSpectroModel() {

    super();
    setClassAuthor("Boualem HASNOUN");
    setClassOwner("IAS");
    setClassVersion("1.0");
    setName("GetFitsDataSpectroModel");
    setDescription("Getting data and metadata from a fits file");
    setClassName("fr.ias.sitools.resources.spectrofits.GetFitsDataSpectroModel");
    setResourceClassName(fr.ias.sitools.resources.spectrofits.GetFitsDataSpectro.class.getName());
    
    ResourceParameter paramColUrl = new ResourceParameter("colUrl", "Colum containing the url of the fits file.",
        ResourceParameterType.PARAMETER_INTERN);
    paramColUrl.setValueType("xs:dataset.columnAlias");
    this.addParam(paramColUrl);
    
    ResourceParameter paramHDU = new ResourceParameter("hduCube", "HDU number where to find the spectral cube.",
        ResourceParameterType.PARAMETER_INTERN);
    paramHDU.setValueType("xs:integer");
    this.addParam(paramHDU);
    
    ResourceParameter paramWaveFromWCS = new ResourceParameter("wave", "HDU Table and column number where to find the wave data if you can't build the wave array from the WCS metadata of the spectral cube. Ex: 4-0",
        ResourceParameterType.PARAMETER_INTERN);
    paramWaveFromWCS.setValueType("xs:string");
    this.addParam(paramWaveFromWCS);    
    
    this.setApplicationClassName(DataSetApplication.class.getName());
    this.setDataSetSelection(DataSetSelectionType.SINGLE);
    this.getParameterByName("methods").setValue("GET");
    this.completeAttachUrlWith("/getFitsDataSpectro");
  }

  @Override
  public Validator<ResourceModel> getValidator() {
    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ResourceParameter> params = item.getParametersMap();
        
        return constraints;
      }
    };
  }
}
