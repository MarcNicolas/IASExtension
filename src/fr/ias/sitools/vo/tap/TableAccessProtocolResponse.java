/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.tap;

import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marc
 */
class TableAccessProtocolResponse implements TableAccessProtocolDataModelInterface {

    
  /**
   * Data model.
   */
  private final transient Map dataModel = new HashMap();
    
    public TableAccessProtocolResponse(TableAccessProtocolInputParameters inputParameters, ResourceModel model) {
        final String query = inputParameters.getQuery();
        final String format = inputParameters.getFormat();
        
        createResponse(inputParameters, model);
    }

    private void createResponse(TableAccessProtocolInputParameters inputParams, ResourceModel model){
        
    }
    
    @Override
    public Map getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
