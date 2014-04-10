/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.astro.resolverName;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author marc
 */
public class CorotIdResolverModel extends ResourceModel {
    
    
    public CorotIdResolverModel(){
        super();
        setClassAuthor("Marc NICOLAS");
        setClassOwner("IAS");
        setClassVersion("0.6");
        setName("Corot ID Resolver service");
        setDescription("This service provides a resource something");
        setResourceClassName(fr.ias.sitools.astro.resolverName.CorotIdResolverResource.class.getName());
        
        ResourceParameter paramExo = new ResourceParameter("exoBool", "true if it's a exo dataset, false in other cases",
        ResourceParameterType.PARAMETER_USER_INPUT);
        paramExo.setValueType("xs:boolean");
        this.addParam(paramExo);
        
        ResourceParameter paramRaCol = new ResourceParameter("raCol", "Colum containing the right ascension.",
        ResourceParameterType.PARAMETER_INTERN);
        paramRaCol.setValueType("xs:dataset.columnAlias");
        this.addParam(paramRaCol);

        ResourceParameter paramDecCol = new ResourceParameter("decCol", "Colum containing the declination.",
        ResourceParameterType.PARAMETER_INTERN);
        paramDecCol.setValueType("xs:dataset.columnAlias");
        this.addParam(paramDecCol);

        ResourceParameter paramcorotIdCol = new ResourceParameter("corotIdCol", "Colum containing the Corot ID.",
        ResourceParameterType.PARAMETER_INTERN);
        paramcorotIdCol.setValueType("xs:dataset.columnAlias");
        this.addParam(paramcorotIdCol);
        
        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.SINGLE);
        this.getParameterByName("methods").setValue("GET");
        this.completeAttachUrlWith("/corotIdResolver/EQUATORIAL/{corotID}");
    }
    
    @Override
  public Validator<ResourceModel> getValidator() {
      return new Validator<ResourceModel>() {
      
        @Override
        public Set<ConstraintViolation> validate(ResourceModel item) {
            Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
            Map<String, ResourceParameter> params = item.getParametersMap();
            ResourceParameter param = params.get("exoBool");
        
            String value = param.getValue();
            if (value == null || value.equals("")) {
                ConstraintViolation constraint = new ConstraintViolation();
                constraint.setMessage("The boolean exoBool must be set.");
                constraint.setLevel(ConstraintViolationLevel.CRITICAL);
                constraint.setValueName(param.getName());
                constraints.add(constraint);
            }
            
            param = params.get("raCol");
            value = param.getValue();
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
