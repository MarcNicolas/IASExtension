/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ias.sitools.vo.resources;

/**
 * IMPORT
 */
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.util.Util;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author marc
 */
public class SimpleSpectralAccessResourcePlugin extends ResourceModel{
    
    /**
    * Logger.
    */
    private static final Logger LOG = Logger.getLogger(SimpleSpectralAccessResourcePlugin.class.getName());

    /**
     * Constructs the configuration panel of the plugin.
     */
    public SimpleSpectralAccessResourcePlugin(){
        super();
        setClassAuthor("Mnicolas");
        setClassOwner("IAS");
        setClassVersion("0.1");
        setName("Simple Spectral Access Protocol");
        setDescription("This plugin provides an access to your data through the Simple Spectral Access Protocol");
        setResourceClassName(fr.ias.sitools.vo.resources.SimpleSpectralAccessResourcePlugin.class.getName());

        this.setApplicationClassName(DataSetApplication.class.getName());

        //we set to NONE because this is a web service for Virtual Observatory
        // and we do not want to see it in the web user interface
        this.setDataSetSelection(DataSetSelectionType.NONE);
        setConfiguration();
    }
    
    private void setConfiguration() {
        final ResourceParameter dictionary = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.DICTIONARY,
                "Dictionary name that sets up the service", ResourceParameterType.PARAMETER_INTERN);
        dictionary.setValueType("xs:dictionary");
        addParam(dictionary);
/*
        final ResourceParameter intersect = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.INTERSECT,
                "how matched images should intersect the region of interest",
                ResourceParameterType.PARAMETER_INTERN);
        //String intersectEnum = "xs:enum[COVERS, ENCLOSED, CENTER, OVERLAPS]";
        intersect.setValueType("xs:enum[CENTER, OVERLAPS]");
        intersect.setValue("OVERLAPS");
        addParam(intersect);

        final ResourceParameter geoAttribut = new ResourceParameter(SimpleImageAccessProtocolLibrary.GEO_ATTRIBUT,
                "Geographical attribut for OVERLAPS mode. The geographical attribut must be spoly datatype from pgsphere",
                ResourceParameterType.PARAMETER_INTERN);
        geoAttribut.setValueType("xs:dataset.columnAlias");
        addParam(geoAttribut);
*/
        final ResourceParameter verb = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.VERB,
                "Verbosity determines how many columns are to be returned in the resulting table",
                ResourceParameterType.PARAMETER_INTERN);
        verb.setValueType("xs:enum[0, 1, 2, 3]");
        verb.setValue("1");
        addParam(verb);

        final ResourceParameter responsibleParty = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.RESPONSIBLE_PARTY,
                "The data provider's name and email", ResourceParameterType.PARAMETER_INTERN);
        addParam(responsibleParty);

        final ResourceParameter serviceName = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.SERVICE_NAME,
                "The name of the service",
                ResourceParameterType.PARAMETER_INTERN);
        serviceName.setValueType("xs:enum[Spectral Cutout Service, Spectral Mosaicing Service, Spectral Extraction Service, Spectral Archive Service]");
        serviceName.setValue("Spectral Archive Service");
        addParam(serviceName);

        final ResourceParameter description = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.DESCRIPTION,
                "A couple of paragraphs of text that describe the nature of the service and its wider context",
                ResourceParameterType.PARAMETER_INTERN);
        addParam(description);

        final ResourceParameter instrument = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.INSTRUMENT,
                "The instrument that made the observations, for example STScI.HST.WFPC2",
                ResourceParameterType.PARAMETER_INTERN);
        addParam(instrument);
/*
        final ResourceParameter band = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.BAND,
                "The waveband of the observations", ResourceParameterType.PARAMETER_INTERN);
        band.setValueType("xs:enum[radio, millimeter, infrared, optical, ultraviolet, xray, gammaray]");
        addParam(band);
*/
        final ResourceParameter coverage = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.COVERAGE,
                "The coverage on the sky, as a free-form string", ResourceParameterType.PARAMETER_INTERN);
        addParam(coverage);
/*
        final ResourceParameter time = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.TIME,
                "The temporal coverage, as a free-form string", ResourceParameterType.PARAMETER_INTERN);
        addParam(time);
*/
        final ResourceParameter maxQuerySize = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_QUERY_SIZE,
                "The largest search area, given in decimal degrees, that will be accepted by the service without returning an error condition."
                + " A value of 64800 indicates that there is no restriction",
                ResourceParameterType.PARAMETER_INTERN);
        maxQuerySize.setValue("64800");
        addParam(maxQuerySize);

        final ResourceParameter maxImageSize = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_IMAGE_SIZE,
                "The largest image area, given in decimal degrees, that will be returned by the service",
                ResourceParameterType.PARAMETER_INTERN);
        addParam(maxImageSize);

        final ResourceParameter maxFileSize = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_FILE_SIZE,
                "The largest file size, given in Bytes, that will be returned by the service",
                ResourceParameterType.PARAMETER_INTERN);
        addParam(maxFileSize);

        final ResourceParameter maxRecords = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_RECORDS,
                "The largest number of records that the service will return", ResourceParameterType.PARAMETER_INTERN);
        maxRecords.setValue("-1");
        addParam(maxRecords);
    }

    /**
     * Validates the configuration that has been set by the administrator.
     *
     * @return the error or warning
     */
    @Override
    public final Validator<ResourceModel> getValidator() {
        /*return new Validator<ResourceModel>() {
            @Override
            public final Set<ConstraintViolation> validate(final ResourceModel item) {
                final Set<ConstraintViolation> constraintList = new HashSet<ConstraintViolation>();
                final Map<String, ResourceParameter> params = item.getParametersMap();
                final ResourceParameter dico = params.get(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.DICTIONARY);

                if (!Util.isNotEmpty(dico.getValue())) {
                    final ConstraintViolation constraint = new ConstraintViolation();
                    constraint.setLevel(ConstraintViolationLevel.WARNING);
                    constraint.setMessage("A dictionary must be set");
                    constraint.setValueName(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.DICTIONARY);
                    constraintList.add(constraint);
                }
                return constraintList;
            }
        };   */
        return null;
    }
    
}
