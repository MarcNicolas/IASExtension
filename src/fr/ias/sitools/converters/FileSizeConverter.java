/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.converters;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc
 */
public class FileSizeConverter extends AbstractConverter {
    
    /** Class logger */
    private static final Logger LOGGER = Logger.getLogger(FileSizeConverter.class.getName());

    public FileSizeConverter() {
        
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
  public Validator<AbstractConverter> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ConverterParameter> params = item.getParametersMap();
        ConverterParameter param = params.get("colFileSizeInBytes");
        
        if(param.getAttachedColumn().isEmpty()){
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("You must choose the column with filesize in bytes data");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        
        param = params.get("colFileSizeConvert");
        
        if(param.getAttachedColumn().isEmpty()){
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("You must choose the output column");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        
        return constraints;
      }
    };
  }

    
}
