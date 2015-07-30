/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.representation;

import fr.cnes.sitools.astro.representation.DatabaseRequestModel;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marc
 */
public class DatabaseRequestIasModel extends  DatabaseRequestModel {
    
    /**
   * Logger.
   */
    private static final Logger LOG = Logger.getLogger(DatabaseRequestIasModel.class.getName());
   /**
     * DB Result set.
     */
    private DatabaseRequest request;
    /**
     * SITools2 converters.
     */
    private ConverterChained converterChained;
    /**
     * Number of rows.
     */
    private transient int sizeValue;

    /**
     * Empty constructor.
     */
    protected DatabaseRequestIasModel() {
        setSize(0);
    }

    public DatabaseRequestIasModel(DatabaseRequest rsVal, ConverterChained converterChainedVal) {
        super(rsVal, converterChainedVal);
        int countRowRequest = rsVal.getCount();
        setSize(countRowRequest);

        // we need to close the connection here
        // otherwise the connection will not be free.
        if (this.size() == 0) {
            try {
                this.request.close();
            } catch (SitoolsException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
