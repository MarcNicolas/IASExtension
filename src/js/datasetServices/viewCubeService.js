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
 * service used to show the cube explorer
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addSelectionService
 * @extends Ext.Window
 */
sitools.user.component.dataviews.services.viewCubeService = {};

Ext.reg('sitools.user.component.dataviews.services.viewCubeService', sitools.user.component.dataviews.services.viewCubeService);

sitools.user.component.dataviews.services.viewCubeService.getParameters = function () {
	return [];
};

sitools.user.component.dataviews.services.viewCubeService.getDefaultParameters = function () {
	return [{
			name : "featureType",
			value : "URL"
		}, {
			name : "columnAlias",
			value : ""
		}];
};

sitools.user.component.dataviews.services.viewCubeService.executeAsService = function (config) {

	var grid = config.dataview;
	var datasetId = grid.datasetId;
	var datasetUrl = grid.sitoolsAttachementForUsers;
	var datasetName = grid.datasetName;
	//var sortInfo = grid.getSortInfo();
	var colModel = config.columnModel;

	if (Ext.isEmpty(this.parameters)) {
		this.parameters =
		sitools.user.component.dataviews.services.viewCubeService.getDefaultParameters();
	}

    
	var jsObj = sitools.user.component.viewCubeService;
	var componentCfg = {
	        dataUrl : datasetUrl,
	        datasetName : datasetName,
	        datasetId : datasetId,
	        componentType : "viewCube",
	        preferencesPath : "/" + datasetName,
	        preferencesFileName : "viewCube",
	        dataview : grid,
	        columnModel : colModel
	};
	var windowConfig = {
	        id : "viewCube" + datasetId,
	        title : "View cube : " + datasetName,
	        iconCls : "viewCube",
	        datasetName : datasetName,
	        type : "viewCube",
	        saveToolbar : true,
	};
	SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
};

