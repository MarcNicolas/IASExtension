/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, WARNING_NB_RECORDS_PLOT*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * service used to show the plot
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addSelectionService
 * @extends Ext.Window
 */
sitools.user.component.dataviews.services.viewShowHeaderService = {};

Ext.reg('sitools.user.component.dataviews.services.viewShowHeaderService', sitools.user.component.dataviews.services.viewShowHeaderService);

sitools.user.component.dataviews.services.viewShowHeaderService.getParameters = function () {
	return [
	        {
		jsObj : "Ext.form.TextField",
		config : {
		    fieldLabel : "url of showHeader service",
		    allowBlank : false,
		    width : 200,
		    listeners : {
		        render : function (c) {
		            Ext.QuickTips.register({
		                target : c,
		                text : i18n.get('label.sizeLimitWidthTooltip')
		            });
		        }
		    },
		    name : "urlShowHeaderService",
		    value : "/plugin/getHeaderFits"
		}
        } 
	    ];
};

sitools.user.component.dataviews.services.viewShowHeaderService.executeAsService = function (config) {

    var grid = config.dataview;
    var datasetId = grid.datasetId;
    var datasetUrl = grid.sitoolsAttachementForUsers;
    var datasetName = grid.datasetName;
    var colModel = config.columnModel;
    var urlShowHeader;
    
    Ext.each(config.parameters, function (param) {
        if (param.name === "urlShowHeaderService") {
        	urlShowHeader = param.value;
        }
    }, this);
    
    var jsObj = sitools.user.component.viewShowHeaderService;
    var componentCfg = {
        dataUrl : datasetUrl,
        datasetName : datasetName,
        datasetId : datasetId,
        componentType : "viewCube",
        preferencesPath : "/" + datasetName,
        preferencesFileName : "viewCube",
        dataview : config.dataview,
        urlShowHeader : urlShowHeader,
        columnModel : colModel
    };
    var windowConfig = {
        id : "showHeader" + datasetId,
        title : "show Header : " + datasetName,
        iconCls : "shwoHeader",
        datasetName : datasetName,
        type : "showHeader",
        saveToolbar : true,
    };
    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
};