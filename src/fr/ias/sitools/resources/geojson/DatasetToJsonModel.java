/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.resources.geojson;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 *
 * @author marc
 */
public class DatasetToJsonModel extends ResourceModel {
    
    public static final String DICO_PARAM_NAME = "dictionary_name";
    
    public DatasetToJsonModel(){
        super();
        setClassAuthor("IDOC/IAS");
        setClassOwner("IDOC/IAS");
        setClassVersion("0.2");
        setName("DatasetToJsonModel");
        setDescription("dataset to GeoJson file");
        setResourceClassName("fr.ias.sitools.resources.geojson.DatasetToJson");

        ResourceParameter param1 = new ResourceParameter(DICO_PARAM_NAME, "The name of the dictionary to use",
        ResourceParameterType.PARAMETER_INTERN);
        param1.setValueType("xs:dictionary");
        this.addParam(param1);
        
        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
        this.getParameterByName("methods").setValue("GET");
        this.getParameterByName("url").setValue("/mizar/dstojson");
    }
    
}
