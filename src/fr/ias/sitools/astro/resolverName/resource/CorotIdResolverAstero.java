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
package fr.ias.sitools.astro.resolverName.resource;

import fr.cnes.sitools.astro.resolver.AbstractNameResolver;
import fr.cnes.sitools.astro.resolver.NameResolverException;
import fr.cnes.sitools.astro.resolver.NameResolverResponse;
import fr.cnes.sitools.util.ClientResourceProxy;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * Queries the Corot name resolver and returns the list of coordinates for a given identifier.<br/> A CorotIdResolver lets you get a sky
 * position given a Corot identifier
 *
 * @author Marc NICOLAS <marc.nicolas@ias.u-psud.fr>
 */
public class CorotIdResolverAstero extends AbstractNameResolver {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(CorotIdResolverAstero.class.getName());
  /**
   * Credits to return for CDS.
   */
  private static final String CREDITS_NAME = "IAS Astero/CNES";
  /**
   * Template URL for the Corot identifier resolver service.
   */
  private static final String TEMPLATE_NAME_RESOLVER = "http://idoc-corotn2-public-v2.ias.u-psud.fr/ds/astero/plugin/corotIdResolver/EQUATORIAL/<corotid>";
  /**
   * Corot service response.
   */
  private String corotId;

  /**
   * Empty constructor.
   */
  protected CorotIdResolverAstero() {
  }

  /**
   * Constructs a new CorotId resolver.
   *
   * @param corotIdVal Corot ID
   */
  public CorotIdResolverAstero(final String corotIdVal) {
    setCorotId(corotIdVal);
    checkInputParameters();      
  }
  /**
   * Sets the Corot Id.
   * @param corotIdVal the Corot ID to set
   */
  protected final void setCorotId(final String corotIdVal) {
      this.corotId = corotIdVal;
  }
  /**
   * Returns the Corot ID.
   * @return the Corot ID
   */
  protected final String getCorotId() {
      return this.corotId;
  }
  /**
   * Tests if the coroID is set.
   *
   * <p>
   * Returns IllegalArgumentException if <code>corotId</code> is <code>null</code> or empty.
   * </p>
   */
  protected final void checkInputParameters() {
    if (getCorotId() == null || getCorotId().isEmpty()) {
      throw new IllegalArgumentException("corotId must be set.");
    }
  }

  @Override
  public final NameResolverResponse getResponse() {
    NameResolverResponse response = new NameResolverResponse(CREDITS_NAME);
    LOG.log(Level.SEVERE, "JE RENTRE DANS LE NAMERESOLVERRESPONSE de l'Astero !!");
    try {
      final String query = TEMPLATE_NAME_RESOLVER.replace("<corotid>", corotId);   
      final JSONObject json = parseResponse(query);

      final String[] coordinates = parseCoordinates(json);
      final double rightAscension = Double.valueOf(coordinates[0]);
      final double declination = Double.valueOf(coordinates[1]);
      
      response.addAstroCoordinate(rightAscension, declination);
    } catch (NameResolverException ex) {
      if (getSuccessor() == null) {
        response.setError(ex);        
      } else {
        response = getSuccessor().getResponse();
      }
    } finally {
      return response;
    }
  }

  /**
   * Queries the SITools2 service at IAS and stores the result in <code>json</code>.
   *
   * @param query query the Corot service
   * @return the response from the server
   * @throws NameResolverException if a problem occurs while the response is being parsed
   */
  private JSONObject parseResponse(final String query) throws NameResolverException {
    assert query != null;
    LOG.log(Level.INFO, "Call IAS name resolver: {0}", query);
    final ClientResourceProxy proxy = new ClientResourceProxy(query, Method.GET);
    final ClientResource client = proxy.getClientResource();
    //client.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "guest", "sitools2public"));
    final Client clientHTTP = new Client(Protocol.HTTP);
    clientHTTP.setConnectTimeout(AbstractNameResolver.SERVER_TIMEOUT);
    client.setNext(clientHTTP);
    final Status status = client.getStatus();
    if (status.isSuccess()) {
      JSONObject json;
      try {
        json = new JSONObject(client.get().getText());
      } catch (IOException ex) {
        throw new NameResolverException(Status.SERVER_ERROR_INTERNAL, ex);
      } catch (JSONException ex) {
        LOG.log(Level.WARNING, "the response of Corot server may changed");
        throw new NameResolverException(Status.CLIENT_ERROR_NOT_ACCEPTABLE, ex);
      } catch (ResourceException ex) {
        throw new NameResolverException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, ex);
      }
      return json;
    } else {
      throw new NameResolverException(status, status.getThrowable());
    }
  }

  /**
   * Parses the coordinates from CDS response and return them.
   * @param json the server's response
   * @return the following array [rightAscension,declination]
   * @throws NameResolverException - if empty response from Corot
   */
  private String[] parseCoordinates(final JSONObject json) throws NameResolverException {
    try {
      //final JSONArray jsonArray = json.getJSONArray("data");
      final JSONArray jsonArray = json.getJSONArray("features");
      final JSONArray coords  = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
      
      if (jsonArray.length() != 1) {
        throw new NameResolverException(Status.CLIENT_ERROR_NOT_FOUND, "Not found");
      }
      
      if (coords.get(0).equals("") || coords.get(1).toString().equals("")) {
        throw new NameResolverException(Status.CLIENT_ERROR_NOT_FOUND, "Not found");
      }
      return new String[]{coords.get(0).toString(),coords.get(1).toString()};
    } catch (JSONException ex) {
      throw new NameResolverException(Status.SERVER_ERROR_INTERNAL, "cannot parse the coordinates");
    }
  }
}
