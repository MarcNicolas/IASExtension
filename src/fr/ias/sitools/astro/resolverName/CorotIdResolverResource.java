/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.astro.resolverName;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

/**
 *
 * @author marc
 */
public class CorotIdResolverResource extends SitoolsParameterizedResource {

    public CorotIdResolverResource() {
        super();
    }

    @Override
    protected Representation head(Variant variant) {
        return super.head(variant); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addInfo(MethodInfo info) {
        super.addInfo(info); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sitoolsDescribe() {
        super.sitoolsDescribe(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void doInit() {
        super.doInit(); 
    }
    
    
}
