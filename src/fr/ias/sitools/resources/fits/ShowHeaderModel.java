/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.resources.fits;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mnicolas
 */
public class ShowHeaderModel extends ResourceModel {
    
    public ShowHeaderModel(){
        super();
        setClassAuthor("Marc NICOLAS");
        setClassOwner("IAS");
        setClassVersion("0.1");
        setName("ShowHeaderModel");
        setDescription("Getting metadata from a fits file");
        setClassName("fr.ias.sitools.resources.spectrofits.ShowHeaderModel");
        setResourceClassName(fr.ias.sitools.resources.fits.ShowHeader.class.getName());
        
        ResourceParameter paramColUrl = new ResourceParameter("colUrl", "Colum containing the url of the fits file.",
        ResourceParameterType.PARAMETER_INTERN);
        paramColUrl.setValueType("xs:dataset.columnAlias");
        this.addParam(paramColUrl);
        
        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.SINGLE);
        this.getParameterByName("methods").setValue("GET");
        this.completeAttachUrlWith("/getHeaderFits");
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
