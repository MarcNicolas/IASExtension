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
 * @author marc
 */
public class RaDecToXYZConverter extends AbstractConverter {
    
    /*
        Formule de changement de coordonée : 
    
            X=cos(DEC)*cos(RA)
            Y=cos(DEC)*sin(RA)
            Z=sin(DEC) 

    */
    
    /** Class logger */
    private static final Logger LOGGER = Logger.getLogger(RaDecToXYZConverter.class.getName());

    public RaDecToXYZConverter() {
         
        this.setName("RaDecToXYZConverter");
        this.setDescription("A converter applying an unit conversion from Ra and Dec into X, Y and Z coordinates");
        this.setClassAuthor("Marc NICOLAS");
        this.setClassOwner("IAS");
        this.setClassVersion("0.1");
        
        ConverterParameter colRa = new ConverterParameter("colRa", "row of dataset with the ra coordinate",ConverterParameterType.CONVERTER_PARAMETER_IN);
        ConverterParameter colDec = new ConverterParameter("colDec", "row of dataset with the dec coordinate",ConverterParameterType.CONVERTER_PARAMETER_IN);
        
        ConverterParameter colX = new ConverterParameter("colX", "Column for coordiante X", ConverterParameterType.CONVERTER_PARAMETER_OUT);
        ConverterParameter colY = new ConverterParameter("colY", "Column for coordiante Y", ConverterParameterType.CONVERTER_PARAMETER_OUT);
        ConverterParameter colZ = new ConverterParameter("colZ", "Column for coordiante Z", ConverterParameterType.CONVERTER_PARAMETER_OUT);
        
        this.addParam(colRa);
        this.addParam(colDec);
        this.addParam(colX);
        this.addParam(colY);
        this.addParam(colZ);
        
        LOGGER.log(Level.INFO, "Converter :{0} version {1}", new Object[] { this.getName(), this.getClassVersion() });
    }
    
    

    @Override
    public Record getConversionOf(Record record) throws Exception {
        Record out = record;
        Double x, y, z;

        AttributeValue attrOutX = this.getOutParam("colX", out); 
        AttributeValue attrOutY = this.getOutParam("colY", out); 
        AttributeValue attrOutZ = this.getOutParam("colZ", out); 
        
        AttributeValue attrInRa = this.getInParam("colRa", out);
        AttributeValue attrInDec = this.getInParam("colDec", out);
        
        if(!isNull(attrInRa) && !isNull(attrInRa)/* && !isNull(attrOutX) && !isNull(attrOutY) && !isNull(attrOutZ)*/){
            try{
                Double ra = Double.parseDouble(attrInRa.getValue().toString());
                Double dec = Double.parseDouble(attrInDec.getValue().toString());
            
                x = Math.cos(dec)*Math.cos(ra);
                y = Math.cos(dec)*Math.sin(ra);
                z = Math.sin(dec);
                
               if(!x.isNaN()){
                   attrOutX.setValue(x);
               }
               if(!y.isNaN()){
                   attrOutY.setValue(y);
               }
               if(!z.isNaN()){
                   
                   attrOutZ.setValue(z);
               }
            }catch(NumberFormatException e){
                
            }
        }
        
        return out;
    }

    @Override
    public Validator<?> getValidator() {
        // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {
        
      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        /*Map<String, ConverterParameter> params = item.getParametersMap();
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
        }*/
        
        return constraints;
      }
    };
    }
    
}
