/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.validation.vo;

import fr.cnes.sitools.extensions.common.NumberArrayValidation;
import fr.cnes.sitools.extensions.common.Validation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marc
 */
public class TimeValidation  extends NumberArrayValidation{

    /**
    * Logger.
    */
    private static final Logger LOG = Logger.getLogger(TimeValidation.class.getName());
    
    /**
     * private time to check
     */
    private String timeRange;
    /**
     * private band to check
     */
    private double[] bandRange;
    /**
     * Char time split
     */
    String timeYearCharSplit = "-";
    /**
     * Char Hour split
     */
    String timeHourCharSplit = ":";
    /**
     * Char Date split
     */
    String timeDateCharSplit = "T";
    /**
     * 
     * First element of the range.
     */
    private static final int MIN = 0;
    /**
     * Last element of the range.
     */
    private static final int MAX = 1;

    public TimeValidation(final Validation validation,final String keyword,final String timeToCheck) {
         super(validation, keyword, ",", 2); 
         this.timeRange = timeToCheck;
    }
    
  

    @Override
    protected Map<String, String> localValidation() {
        final Map<String, String> error = new HashMap<String, String>();
        if(getTimeRange().split(",").length == 2){
            String timeMin = getTimeRange().split(",")[MIN];
            String timeMax = getTimeRange().split(",")[MAX];
            if(timeMin.contains(timeDateCharSplit)){
                if(timeMin.split(timeDateCharSplit).length > 2 ){
                    error.put("Time", "Time value must be in ISO8601 format, eg yyyy-mm-ddThh:mm:ss");
                }
                if(!isValidDate(timeMin)){
                    error.put("Time", timeMin+" is not a valide date ! Time value must be in ISO8601 format, eg yyyy-mm-ddThh:mm:ss");
                }
            }
            if(timeMax.contains(timeDateCharSplit)){
                if(timeMax.split(timeDateCharSplit).length > 2 ){
                    error.put("Time", "Time value must be in ISO8601 format, eg yyyy-mm-ddThh:mm:ss");
                }
                if(!isValidDate(timeMax)){
                    error.put("Time", timeMax+" is not a valide date ! Time value must be in ISO8601 format, eg yyyy-mm-ddThh:mm:ss");
                }
            }
        }else{
            String time = getTimeRange();
            if(!isValidDate(time)){
                error.put("Time", time+" is not a valide date ! Time value must be in ISO8601 format, eg yyyy-mm-ddThh:mm:ss");
            }
        }
        return error;
    }

    /**
     * @return the timeRange
     */
    public String getTimeRange() {
        return timeRange;
    }

    public static boolean isValidDate(String text) {
        if (text == null || !text.matches("\\d{4}-[01]\\d-[0-3]\\dT[0-9]{2}:[0-9]{2}:[0-9]{2}")){
            return false;
        }
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setLenient(false);
        try {
            df.parse(text);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
}
