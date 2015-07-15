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
package fr.ias.sitools.resources.vo;

import fr.ias.sitools.vo.tap.TableAccessProtocolLibrary;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.extensions.astro.resource.SimpleImageAccessResourcePlugin;
import fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary;
import fr.ias.sitools.vo.tap.TableAccessProtocolLibrary;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

/**
 * Queries the dataset and retrieves the result using the Simple Image Access Protocol.
 * @see SimpleImageAccessResourcePlugin the plugin
 * @see SimpleSpectralAccessProtocolLibrary the library that we use for SIAP.
 * @author Jean-Christophe Malapert <jean-christophe.malapert@cnes.fr>
 */
public class TableAccessProtocolResource extends SitoolsParameterizedResource {

  /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(TableAccessProtocolResource.class.getName());

  /**
   * Initialize.
   */
  @Override
  public final void doInit() {
    super.doInit();
  }

  /**
   * Returns the supported representation.
   *
   * @param variant variant
   * @return XML mediaType
   */
  @Override
  protected final Representation head(final Variant variant) {
    final Representation repr = super.head();
    repr.setMediaType(MediaType.TEXT_XML);
    return repr;
  }

  /**
   * Returns the VOTable response.
   *
   * @return VOTable response
   */
  @Get
  public final Representation getVOResponse() {
    //LOG.finest(String.format("SSA : %s", getRequest()));
    final TableAccessProtocolLibrary tap = new TableAccessProtocolLibrary((DataSetApplication) this.getApplication(),
            this.getModel(), this.getRequest(), this.getContext());
    final Representation rep = tap.getResponse();
    if (fileName != null && !"".equals(fileName)) {
      final Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
      disp.setFilename(fileName);
      rep.setDisposition(disp);
    }
    return rep;
  }

  /**
   * Describes SITools2 in the WADL.
   */
  @Override
  public final void sitoolsDescribe() {
    setName("Tablel Access Protocol");
    setDescription("This class implements the Table Access Protocol for Virtual Observatory. "
            + "See http://ivoa.net web site for information about this protocol.");
  }

  /**
   * Describe GET method in the WADL.
   *
   * @param info information
   */
  @Override
  protected final void describeGet(final MethodInfo info) {
    this.addInfo(info);
    info.setIdentifier("TableAccessProtocol");
    info.setDocumentation("Table Access Protocol for Virtual Observatory");

    /*final List<ParameterInfo> parametersInfo = new ArrayList<ParameterInfo>();
    parametersInfo.add(new ParameterInfo("POS", true, "string", ParameterStyle.QUERY,
            "Box Central position (decimal degree) in ICRS such as RA,DEC."));
    parametersInfo.add(new ParameterInfo("SIZE", true, "string", ParameterStyle.QUERY,
            "Size of the box in decimal degree such as width,height or width."));
    info.getRequest().setParameters(parametersInfo);
    */
    info.getResponse().getStatuses().add(Status.SUCCESS_OK);

    final DocumentationInfo documentation = new DocumentationInfo();
    documentation.setTitle("TAP");
    documentation.setTextContent("Table Access Protocol");

    final List<RepresentationInfo> representationsInfo = new ArrayList<RepresentationInfo>();
    final RepresentationInfo representationInfo = new RepresentationInfo(MediaType.TEXT_XML);
    representationInfo.setDocumentation(documentation);
    representationsInfo.add(representationInfo);
    info.getResponse().setRepresentations(representationsInfo);
  }
}
