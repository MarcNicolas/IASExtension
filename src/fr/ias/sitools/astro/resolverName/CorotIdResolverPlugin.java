/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.astro.resolverName;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 *
 * @author marc
 */
public class CorotIdResolverPlugin extends ResourceModel {
    
    
    public CorotIdResolverPlugin(){
        super();
        setClassAuthor("Marc NICOLAS");
        setClassOwner("IAS");
        setClassVersion("0.1");
        setName("Corot ID Resolver service");
        setDescription("This service provides a resource something");
        setResourceClassName(fr.ias.sitools.astro.resolverName.CorotIdResolverResource.class.getName());
        
        ResourceParameter paramRaCol = new ResourceParameter("Ra", "Colum containing the right ascension.",
        ResourceParameterType.PARAMETER_INTERN);
        paramRaCol.setValueType("xs:dataset.columnAlias");
        this.addParam(paramRaCol);

        ResourceParameter paramDecCol = new ResourceParameter("Dec", "Colum containing the declination.",
        ResourceParameterType.PARAMETER_INTERN);
        paramDecCol.setValueType("xs:dataset.columnAlias");
        this.addParam(paramDecCol);

        ResourceParameter paramcorotIdCol = new ResourceParameter("CorotId", "Colum containing the Corot ID.",
        ResourceParameterType.PARAMETER_INTERN);
        paramcorotIdCol.setValueType("xs:dataset.columnAlias");
        this.addParam(paramcorotIdCol);
        
        this.getParameterByName("methods").setValue("GET");
        this.completeAttachUrlWith("/corotIdResolver");
    }
    
    @Override
  public Validator<ResourceModel> getValidator() {
    return null;
    //return new Validator<ResourceModel>() {
    /*
      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ResourceParameter> params = item.getParametersMap();
        
        return constraints;
      }*/
    };
  //}
    
}
