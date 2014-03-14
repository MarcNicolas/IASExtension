/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.converters;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc
 */
public class fileSizeConverter extends AbstractConverter {
    
    /** Class logger */
    private static final Logger LOGGER = Logger.getLogger(fileSizeConverter.class.getName());

    public fileSizeConverter() {
        
        this.setName("FileSizeConverter");
        this.setDescription("A converter applying an unit conversion from bytes into Kb or Mb or Gb");
        this.setClassAuthor("Marc NICOLAS");
        this.setClassOwner("IAS");
        this.setClassVersion("0.1");
        
        ConverterParameter colFileSize = new ConverterParameter("colFileSizeInBytes", "row of dataset with the filesize in bytes",ConverterParameterType.CONVERTER_PARAMETER_IN);
        ConverterParameter colFileSizeConvert = new ConverterParameter("colFileSizeConvert", "File Size converted", ConverterParameterType.CONVERTER_PARAMETER_OUT);
        this.addParam(colFileSize);
        this.addParam(colFileSizeConvert);
        
        LOGGER.log(Level.INFO, "Converter :{0} version {1}", new Object[] { this.getName(), this.getClassVersion() });
    }   

    @Override
    public Record getConversionOf(Record record) throws Exception {
        Record out = record;
        String result = "";
        
        AttributeValue attrOut = this.getOutParam("colFileSizeConvert", out); 
        
        AttributeValue attrIn = this.getInParam("colFileSizeInBytes", out);
        
        if (!isNull(attrIn) && !isNull(attrOut)) {
            try{
                double input = Double.parseDouble(attrIn.getValue().toString());
                if(input>0 && input<1024){
                    result = (double)Math.round(input*10)/10 +" o";
                }else if(input >= 1024 && input < Math.pow(1024, 2) ){
                   result = (double)Math.round((input/1024)*10)/10+" Ko"; 
                }else if(input >=Math.pow(1024, 2) && input < Math.pow(1024, 3)){
                    result = (double)Math.round((input/Math.pow(1024, 2))*10)/10+" Mo" ;
                }else if( input >= Math.pow(1024, 3) && input < Math.pow(1024, 4)){
                    result = (double)Math.round((input/Math.pow(1024, 3))*10)/10+" Go" ;
                }
                
                if(!result.isEmpty()){
                    attrOut.setValue(result);
                }
            }catch (Exception e) {
                attrOut.setValue(Double.NaN);
            }
        }
        
        return out;
    }

    @Override
    public Validator<?> getValidator() {
        return null;
    }
    
}
