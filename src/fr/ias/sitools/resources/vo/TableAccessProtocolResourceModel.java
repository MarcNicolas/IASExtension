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
 * Plugin for publishing a dataset through the Simple Image Access Protocol.
 *
 * <p>
 * The plugin answers to the need of the following user story:<br/>
 * As administrator, I publish my data through SIAP so that the users can
 * request my images by the use of an interoperability standard.
 * <br/>
 * <img src="../../../../../../images/SIAP-usecase.png"/>
 * <br/>
 * In addition, this plugin has several dependencies with different
 * components:<br/>
 * <img src="../../../../../../images/SimpleImageAccessResourcePlugin.png"/>
 * <br/>
 * </p>
 *
 * @author Jean-Christophe Malapert <jean-christophe.malapert@cnes.fr>
 * @startuml SIAP-usecase.png title Publishing data through SIAP User --> (SIAP
 * service) : requests Admin --> (SIAP service) : adds and configures the SIAP
 * service from the dataset. (SIAP service) .. (dataset) : uses
 * @enduml
 * @startuml package "Services" { HTTP - [SimpleImageAccessResourcePlugin] }
 * database "Database" { frame "Data" { [myData] } } package "Dataset" { HTTP -
 * [Dataset] [VODictionary] } folder "DataStorage" { HTTP - [directory] }
 * [SimpleImageAccessResourcePlugin] --> [Dataset] [Dataset] --> [directory]
 * [Dataset] --> [myData] [Dataset] --> [VODictionary]
 * @enduml
 */
public class TableAccessProtocolResourceModel extends ResourceModel {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(TableAccessProtocolResourceModel.class.getName());

    /**
     * Constructs the configuration panel of the plugin.
     */
    public TableAccessProtocolResourceModel() {
        super();
        setClassAuthor("Mnicoals");
        setClassOwner("IAS");
        setClassVersion("0.1");
        setName("Table Access Protocol");
        setDescription("This plugin provides an access to your data through the Table Access Protocol");
        setResourceClassName(fr.ias.sitools.resources.vo.TableAccessProtocolResource.class.getName());
        
        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.SINGLE);
        this.getParameterByName("methods").setValue("GET");
        this.completeAttachUrlWith("/services/vo/tap/sync");
        //setConfiguration();
       
    }

    /**
     * Sets the configuration for the administrator.
     */
    /*
    private void setConfiguration() {
        final ResourceParameter dico = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.DICTIONARY, "Dictionary name that sets up the service.",
        ResourceParameterType.PARAMETER_INTERN);
        dico.setValueType("xs:dictionary");
        this.addParam(dico);
        
        final ResourceParameter intersect = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.INTERSECT,
                "how matched images should intersect the region of interest",
                ResourceParameterType.PARAMETER_INTERN);
        //String intersectEnum = "xs:enum[COVERS, ENCLOSED, CENTER, OVERLAPS]";
        intersect.setValueType("xs:enum[CENTER, OVERLAPS]");
        intersect.setValue("OVERLAPS");
        addParam(intersect);
        
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
        serviceName.setValueType("xs:enum[Spectral Archive Service, Spectral Cutout Service, Spectral Mosaicing Service, Spectral Extraction Service]");
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
        
        final ResourceParameter maxQuerySize = new ResourceParameter(
                fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_QUERY_SIZE,
                "The largest search area, given in decimal degrees, that will be accepted by the service without returning an error condition."
                + " A value of 64800 indicates that there is no restriction",
                ResourceParameterType.PARAMETER_INTERN);
        maxQuerySize.setValue("64800");
        addParam(maxQuerySize);

        final ResourceParameter maxImageSize = new ResourceParameter(
                fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_IMAGE_SIZE,
                "The largest image area, given in decimal degrees, that will be returned by the service",
                ResourceParameterType.PARAMETER_INTERN);
        addParam(maxImageSize);

        final ResourceParameter maxFileSize = new ResourceParameter(
                fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_FILE_SIZE,
                "The largest file size, given in Bytes, that will be returned by the service",
                ResourceParameterType.PARAMETER_INTERN);
        addParam(maxFileSize);

        final ResourceParameter maxRecords = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.MAX_RECORDS,
                "The largest number of records that the service will return", ResourceParameterType.PARAMETER_INTERN);
        maxRecords.setValue("-1");
        addParam(maxRecords);
        
        final ResourceParameter geoAttribut = new ResourceParameter(fr.ias.sitools.vo.ssa.SimpleSpectralAccessProtocolLibrary.GEO_ATTRIBUT,
                "Geographical attribut for OVERLAPS mode. The geographical attribut must be spoly datatype from pgsphere",
                ResourceParameterType.PARAMETER_INTERN);
        geoAttribut.setValueType("xs:dataset.columnAlias");
        addParam(geoAttribut);

    }
    */
    /**
     * Validates the configuration that has been set by the administrator.
     *
     * @return the error or warning
     */
    @Override
    public final Validator<ResourceModel> getValidator() {
        return new Validator<ResourceModel>() {
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
        };
    }
}
