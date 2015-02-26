/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.resources.geojson;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;

/**
 *
 * @author marc
 */
public class DatasetToJsonModel extends ResourceModel {
    
    public DatasetToJsonModel(){
        super();
        setClassAuthor("IDOC/IAS");
        setClassOwner("IDOC/IAS");
        setClassVersion("0.1");
        setName("DatasetToJsonModel");
        setDescription("dataset to GeoJson file");
        setResourceClassName("fr.ias.sitools.resources.geojson.DatasetToJson");

        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
        this.getParameterByName("methods").setValue("GET");
        this.getParameterByName("url").setValue("/mizar/dstojson");
    }
    
}
