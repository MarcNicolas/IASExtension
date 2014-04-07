/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.astro.resolver;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author marc
 */
public class CorotIdPlugInModel extends ResourceModel {

    public CorotIdPlugInModel() {
        super();
        setClassAuthor("mnicolas");
        setClassOwner("IAS");
        setClassVersion("0.1");
        setName("CorotIdResolverName");
        setDescription("Getting coordinates of start from the corotId");
        setClassName("fr.ias.sitools.astro.resolver.CorotIdPlugInModel");
        setResourceClassName(fr.ias.sitools.astro.resolver.CorotIdPlugIn.class.getName());
        
        //Resources Parameters
        ResourceParameter raCol = new ResourceParameter("raCol", "Colum containing the right ascension of the object",
        ResourceParameterType.PARAMETER_INTERN);
        raCol.setValueType("xs:dataset.columnAlias");
        this.addParam(raCol);
        
        ResourceParameter decCol = new ResourceParameter("decCol", "Colum containing the declination of the object",
        ResourceParameterType.PARAMETER_INTERN);
        decCol.setValueType("xs:dataset.columnAlias");
        this.addParam(decCol);
        
        ResourceParameter corotIdCol = new ResourceParameter("corotIdCol", "Colum containing the corotId of the object",
        ResourceParameterType.PARAMETER_INTERN);
        corotIdCol.setValueType("xs:dataset.columnAlias");
        this.addParam(corotIdCol);
    }
    
    
    @Override
  public Validator<ResourceModel> getValidator() {
    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ResourceParameter> params = item.getParametersMap();
        ResourceParameter param = params.get("raCol");
        
        String value = param.getValue();
        if (value == null || value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("The attribute for RA must be set.");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        
        param = params.get("decCol");
        value = param.getValue();
        if (value == null || value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("The attribute for DEC must be set.");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        
        param = params.get("corotIdCol");
        value = param.getValue();
        if (value == null || value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("The attribute for CorotID must be set.");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        
        
        return constraints;
      }
    };
  }
   
    
}
