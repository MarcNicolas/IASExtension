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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Interface to define a SQL spatial constraint.
 * @author Jean-Christophe Malapert <jean-christophe.malapert@cnes.fr>
 */
public abstract class AbstractSqlGeometryConstraint {
    
    /**
     * LOGGER
     */
     private static final Logger LOG = Logger.getLogger(AbstractSqlGeometryConstraint.class.getName());

  /**
   * Index where the min value of a range is located.
   */
  protected static final int MIN = 0;
  /**
   * Index where the max value of a range is located.
   */
  protected static final int MAX = 1;

  /**
   * Size parameter given by the user.
   */
  private transient double[] sizeArray = new double[2];
  /**
   * Central position along right ascension axis.
   */
  private transient double raUser;
  /**
   * Central position along declination axis.
   */
  private transient double decUser;
  /**
   * time.
   */
  private transient String[] timeUser = new String[2];
  /**
   * band.
   */
  private transient double[] bandUser = new double[2];

  /**
   * Sets the geometry attribute.
   * @param geometry the geometry attribute
   */
  public abstract void setGeometry(final Object geometry);

  /**
   * Sets user input parameters.
   * @param inputParameters user input parameters
   */
  public final void setInputParameters(final SimpleSpectralAccessInputParameters inputParameters) {
    this.raUser = inputParameters.getRa();
    this.decUser = inputParameters.getDec();
    if (inputParameters.getBand().length == 2) {
        this.bandUser = inputParameters.getBand();
    }else{
        this.bandUser[0] = inputParameters.getBand()[0];
        this.bandUser[1] = inputParameters.getBand()[0];
    }
    if (inputParameters.getTime().length == 2) {
        this.timeUser = inputParameters.getTime();
    }else{
        
        this.timeUser[0] = inputParameters.getTime()[0];
        this.timeUser[1] = inputParameters.getTime()[0];
    }
    if (inputParameters.getSize().length == 2) {
      this.sizeArray = inputParameters.getSize();
    } else { 
      this.sizeArray[0] = inputParameters.getSize()[0];
      this.sizeArray[1] = inputParameters.getSize()[0];
    }
  }

  /**
   * Detects when the SSA request has a collition with South pole.
   * @return True when the SSA request has a collition with South pole otherwise False
   */
  protected final boolean isSouthPoleCollision() {
    return (decUser - sizeArray[1] / 2.0 <= SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_DECLINATION) ? true : false;
  }

  /**
   * Detects when the SSA request has a collition with North pole.
   * @return True when the SSA request has a collition with North pole otherwise False
   */
  protected final boolean isNorthPoleCollision() {
    return (decUser + sizeArray[1] / 2.0 >= SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_DECLINATION) ? true : false;
  }

  /**
   * Detects when the SSA request has a collition with both North and South pole.
   * @return True when the SSA request has a collition with both North and South pole otherwise False
   */
  protected final boolean isPolesCollision() {
    return (isNorthPoleCollision() && isSouthPoleCollision()) ? true : false;
  }

  /**
   * Detects when the SSA request is a large polygon.
   *
   * <p>
   * A large polygon is a polygon where two points is separated by MAX_VALUE_FOR_RIGHT_ASCENSION / 2.0
   * along right ascension axis.
   * </p>
   * @return True when the SSA request is a large polygon otherwise False
   */
  protected final boolean isLargePolygon() {
    return (sizeArray[0] >= SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION / 2.0) ? true : false;
  }

  /**
   * Detects there is a collision with RA=MAX_VALUE_FOR_RIGHT_ASCENSION.
   * @return True when there is a collision with RA=MAX_VALUE_FOR_RIGHT_ASCENSION otherwise False
   */
  protected final boolean isMaxRaCollision() {
    return (raUser + sizeArray[0] / 2.0 >= SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION) ? true : false;
  }

  /**
   * Detects there is a collision with RA=MIN_VALUE_FOR_RIGHT_ASCENSION.
   * @return True when there is a collision with RA=MIN_VALUE_FOR_RIGHT_ASCENSION otherwise False
   */
  protected final boolean isMinRaCollision() {
    return (raUser - sizeArray[0] / 2.0 <= SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION) ? true : false;
  }

  /**
   * Detects if the SSA request has a collision with the border of CAR projection centered on 0,0.
   * @return True when the SSA request has a collision with the borders otherwise False
   */
  protected final boolean isBorderRaCollision() {
    return (isMinRaCollision() && isMaxRaCollision()) ? true : false;
  }

  /**
   * Detects if the SSA request is a ring on the sphere.
   * @return True when the SSA request is a ring otherwise False
   */
  protected final boolean isRing() {
    return (sizeArray[0] == SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION) ? true : false;
  }

  /**
   * SQL geometry predicat to add to the SQL constraint.
   * @return SQL geometry predicat
   */
  public abstract String getSqlPredicat();

  /**
   * Computes ranges of the SSA request along right ascension and declination axes.
   *
   * <p>
   * For the declination axis, returns [decmin, decmax].<br/>
   * For the right ascension axis, returns List<Double[]>. The ranges are computed
   * based on the CAR projection.
   * </p>
   *
   * @return an array [List<Ra[min,max]>, Dec[min,max])
   */
  protected final Object computeRange() {
    final List<Double[]> raRange = new ArrayList<Double[]>();
    final double[] decRange = new double[2];
    if (this.isPolesCollision()) {
      decRange[MIN] = SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_DECLINATION;
      decRange[MAX] = SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_DECLINATION;
      raRange.add(new Double[]{SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION, SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
    } else if (this.isNorthPoleCollision()) {
      decRange[MIN] = decUser - sizeArray[1] / 2.0;
      decRange[MAX] = SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_DECLINATION;
      raRange.add(new Double[]{SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION, SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
    } else if (this.isSouthPoleCollision()) {
      decRange[MIN] = SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_DECLINATION;
      decRange[MAX] = decUser + sizeArray[1] / 2.0;
      raRange.add(new Double[]{SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION, SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
    } else {
      decRange[MIN] = decUser - sizeArray[1] / 2.0;
      decRange[MAX] = decUser + sizeArray[1] / 2.0;
      if (this.isBorderRaCollision()) {
        raRange.add(new Double[]{SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION,
                                 SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
      } else if (this.isMaxRaCollision()) {
        raRange.add(new Double[]{raUser - sizeArray[0] / 2.0,
                                 SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
        raRange.add(new Double[]{SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION,
                                (raUser + sizeArray[0] / 2.0 + SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION) % SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
      } else if (this.isMinRaCollision()) {
        raRange.add(new Double[]{SimpleSpectralAccessProtocolLibrary.MIN_VALUE_FOR_RIGHT_ASCENSION,
                                raUser + sizeArray[0] / 2.0});
        raRange.add(new Double[]{(raUser - sizeArray[0] / 2.0 + SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION) % SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION,
                                  SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
      } else {
        raRange.add(new Double[]{(raUser - sizeArray[0] / 2.0 + SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION) % SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION,
                                 (raUser + sizeArray[0] / 2.0 + SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION) % SimpleSpectralAccessProtocolLibrary.MAX_VALUE_FOR_RIGHT_ASCENSION});
      }
    }
    return Arrays.asList(raRange, decRange);
  }
  protected final Object computeTimeAndBandRange() {
   
   return Arrays.asList(timeUser, bandUser);
  }
}