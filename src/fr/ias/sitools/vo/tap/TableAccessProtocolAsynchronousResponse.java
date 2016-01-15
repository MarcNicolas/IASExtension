/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.tap;

import fr.cnes.sitools.extensions.astro.application.uws.common.Util;
import fr.cnes.sitools.extensions.astro.application.uws.jobmanager.AbstractJobTask;
import fr.cnes.sitools.xml.uws.v1.Job;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import net.ivoa.xml.uws.v1.ErrorSummary;
import net.ivoa.xml.uws.v1.ErrorType;
import net.ivoa.xml.uws.v1.ExecutionPhase;
import org.restlet.engine.Engine;

/**
 *
 * @author marc
 */
public class TableAccessProtocolAsynchronousResponse extends AbstractJobTask {

    final TableAccessProtocolInputParameters inputParams;
    
    public TableAccessProtocolAsynchronousResponse(final TableAccessProtocolInputParameters inputParameters) {
        this.inputParams = inputParameters;
    }
    
    @Override
    public void run() {
        Engine.getLogger(TableAccessProtocolAsynchronousResponse.class.getName()).log(Level.SEVERE, "***************** JE SUIS DANS LE RUN !!!");
        try {
            setBlinker(Thread.currentThread());
            setStartTime(Util.convertIntoXMLGregorian(new Date()));
            setPhase(ExecutionPhase.EXECUTING);
            final List<String> filenameList = createJob();
            //createResults(filenameList);
            setEndTime(Util.convertIntoXMLGregorian(new Date()));
            setPhase(ExecutionPhase.COMPLETED);
        } catch (DatatypeConfigurationException ex) {
            Engine.getLogger(TableAccessProtocolAsynchronousResponse.class.getName()).log(Level.SEVERE, null, ex);
            final ErrorSummary errorSumm = new ErrorSummary();
            errorSumm.setMessage(ex.getMessage());
            errorSumm.setType(ErrorType.FATAL);
            errorSumm.setHasDetail(true);
            setError(errorSumm);
            setPhase(ExecutionPhase.ERROR);
        } catch (Error error) {
            Engine.getLogger(TableAccessProtocolAsynchronousResponse.class.getName()).log(Level.SEVERE, null, error);
            final ErrorSummary errorSumm = new ErrorSummary();
            errorSumm.setMessage(error.getMessage());
            errorSumm.setType(ErrorType.FATAL);
            errorSumm.setHasDetail(true);
            setError(errorSumm);
            setPhase(ExecutionPhase.ERROR);
        }
    }

    private List<String> createJob(){
        List<String> a = new ArrayList<String>();
        Engine.getLogger(TableAccessProtocolAsynchronousResponse.class.getName()).log(Level.SEVERE,"+++++++++++++++++++++++");
        
        final String query = this.inputParams.getQuery();
        a.add("Test");
        Engine.getLogger(TableAccessProtocolAsynchronousResponse.class.getName()).log(Level.SEVERE,"+++++++++++++++++++++++ QUERY : "+query);
        //final String format = String.valueOf(getParameterValue(TableAccessProtocolLibrary.FORMAT));
        //final String lang = String.valueOf(getParameterValue(TableAccessProtocolLibrary.LANG));
        Engine.getLogger(TableAccessProtocolAsynchronousResponse.class.getName()).log(Level.SEVERE,"+++++++++++++++++++++++ getJobInfo "+getJobInfo().toString());
        return a;
    }
    
    @Override
    public Job getCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
