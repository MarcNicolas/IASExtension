/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.resources;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary;
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
 *
 * @author marc
 */
public class SimpleSpectralAccessResource extends SitoolsParameterizedResource {

   /**
   * Logger.
   */
  private static final Logger LOG = Logger.getLogger(SimpleSpectralAccessResource.class.getName());

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
    LOG.finest(String.format("SSA : %s", getRequest()));
    final SimpleSpectralAccessProtocolLibrary ssa = new SimpleSpectralAccessProtocolLibrary((DataSetApplication) this.getApplication(),
            this.getModel(), this.getRequest(), this.getContext());
    final Representation rep = ssa.getResponse();
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
    setName("Simple Spectral Access Protocol");
    setDescription("This class implements the Simple Spectral Access Protocol for Virtual Observatory. "
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
    info.setIdentifier("SimpleSpectralAccessProtocolLibrary");
    info.setDocumentation("Interoperability service to distribute images through the Simple Spectral Access Protocol");

    final List<ParameterInfo> parametersInfo = new ArrayList<ParameterInfo>();
    parametersInfo.add(new ParameterInfo("POS", true, "string", ParameterStyle.QUERY,
            "Box Central position (decimal degree) in ICRS such as RA,DEC."));
    parametersInfo.add(new ParameterInfo("SIZE", true, "string", ParameterStyle.QUERY,
            "Size of the box in decimal degree such as width,height or width."));
    parametersInfo.add(new ParameterInfo("BAND", true, "string", ParameterStyle.QUERY,
            "Band of the searching target."));
    parametersInfo.add(new ParameterInfo("TIME", true, "string", ParameterStyle.QUERY,
            "Time of the searching target."));
    info.getRequest().setParameters(parametersInfo);

    info.getResponse().getStatuses().add(Status.SUCCESS_OK);

    final DocumentationInfo documentation = new DocumentationInfo();
    documentation.setTitle("SSAP");
    documentation.setTextContent("Simple Spectral Access Protocol");

    final List<RepresentationInfo> representationsInfo = new ArrayList<RepresentationInfo>();
    final RepresentationInfo representationInfo = new RepresentationInfo(MediaType.TEXT_XML);
    representationInfo.setDocumentation(documentation);
    representationsInfo.add(representationInfo);
    info.getResponse().setRepresentations(representationsInfo);
  }
 

}
