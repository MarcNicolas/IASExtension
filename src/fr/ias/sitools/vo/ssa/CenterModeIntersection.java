 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.ias.sitools.vo.ssa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

  /**
   * Constructs SQL predicat for Center mode intersection.
   */
  public class CenterModeIntersection extends AbstractSqlGeometryConstraint {
    /**
     * LOGGER
     */
     private static final Logger LOG = Logger.getLogger(CenterModeIntersection.class.getName());
    /**
     * Right ascension attribut.
     */
    private transient String raCol;
    /**
     * Declination attribut.
     */
    private transient String decCol;
    /**
     * Time attribut.
     */
    private transient String timeCol;
    /**
     * Band attribut.
     */
    private transient String bandCol;

    @Override
    public final void setGeometry(final Object geometry) {

      if (geometry instanceof String[]) {
        final String[] geometryArray = (String[]) geometry;
        
        if (geometryArray.length != 4) {
          throw new IllegalArgumentException("geometry must be an array of four elements that contains racolName, decColName, timeColName and bandColName");
        } else {
          this.raCol = geometryArray[0];
          this.decCol = geometryArray[1];
          this.timeCol = geometryArray[2];
          this.bandCol = geometryArray[3];
        }
      } else {
        throw new IllegalArgumentException("geometry must be an array of four elements that contains racolName, decColName, timeColName and bandColName");
      }
    }

    @Override
    public final String getSqlPredicat() {
      if (isPolesCollision()) {
        return null;
      }
      final List rangeSsa = (List) computeTimeAndBandRange();
      
      final String[] timeRange = (String[]) rangeSsa.get(0);
      final double[] bandRange = (double[]) rangeSsa.get(1);
      
      
      final List ranges = (List) computeRange();
      final List<Double[]> raRanges = (List<Double[]>) ranges.get(0);
      final double[] decRange = (double[]) ranges.get(1);
      String predicatDefinition;
      if (raRanges.size() == 1) {
        final Double[] raRange = raRanges.get(0);
        predicatDefinition = String.format(" AND ( %s BETWEEN %s AND %s ) AND ( %s BETWEEN %s AND %s )", decCol, decRange[0], decRange[1], raCol, raRange[0], raRange[1]);
      } else {
        final Double[] raRange1 = raRanges.get(0);
        final Double[] raRange2 = raRanges.get(1);
        predicatDefinition = String.format(" AND ( %s BETWEEN %s AND %s ) AND (( %s BETWEEN %s AND %s ) OR ( %s BETWEEN %s AND %s ))",
                                             decCol, decRange[0], decRange[1],
                                             raCol, raRange1[0], raRange1[1], raCol, raRange2[0], raRange2[1]);
      }
      
      if(timeRange[0].equals(timeRange[1])){
          timeRange[1] = addHourinTimeRange(timeRange[0]);
      }
      predicatDefinition += String.format(" AND ( %s BETWEEN %s AND %s ) AND ( %s BETWEEN '%s' AND '%s' )",
                                             bandCol, bandRange[0], bandRange[1],timeCol, timeRange[0], timeRange[1]);
      LOG.log(Level.SEVERE,"predicatDefinition : "+predicatDefinition);
      return predicatDefinition;
    }
    
    private String addHourinTimeRange(String timeTo){
        String a = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
         try {
             Date date = df.parse(timeTo);
             Date newDate =  new Date(date.getTime()+TimeUnit.HOURS.toMillis(1));
             a = newDate.toString();
             LOG.log(Level.INFO," ************************  : newDate.toString : "+a);
             LOG.log(Level.INFO," ************************  : newDate.toString : "+newDate.toGMTString());
             LOG.log(Level.INFO," ************************  : newDate.toString : "+newDate.toLocaleString());
         } catch (ParseException ex) {
             Logger.getLogger(CenterModeIntersection.class.getName()).log(Level.SEVERE, null, ex);
             a=timeTo;
         }
        
        return a;
    }
  }
