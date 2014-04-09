/***************************************
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
***************************************/
/*global Ext, sitools, projectGlobal, commonTreeUtils, showResponse, DEFAULT_PREFERENCES_FOLDER, 
 document, i18n, $, Flotr, userLogin, SitoolsDesk, sql2ext, loadUrl,
 SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, DEFAULT_LIVEGRID_BUFFER_SIZE*/
/*
 * @include "../viewDataDetail/viewDataDetail.js"
 * @include "../../sitoolsProject.js"
 */
/**
 * 
 * <a href="https://sourceforge.net/tracker/?func=detail&aid=3313793&group_id=531341&atid=2158259">[3313793]</a><br/>
 * 16/06/2011 m.gond {Display the right number of data plotted} <br/>
 * 
 * ExtJS layout for plotting data
 */

Ext.namespace('sitools.user.component');
/**
 * @cfg {String} dataUrl the dataset url attachment
 * @cfg {} columnModel the dataset's column model
 * @cfg {} filters the list of filters to apply (filters from the dataview)
 * @cfg {String} selections the selections as a String to add to the URL (selections from the dataview) 
 * @cfg {String} datasetName the name of the dataset
 * @cfg {} formParam list of parameters from a query form
 * @cfg {} formMultiDsParams list of parameters from a multidataset query form
 * @cfg {String} datasetId the id of the dataset
 * @cfg {string} componentType Should be "plot"
 * @requires sitools.user.component.viewDataDetail
 * @class sitools.user.component.viewCubeService
 * @extends Ext.Panel
 */
sitools.user.component.viewCubeService = function (config) {

    Ext.apply(this, config);
   
    this.datasetName = config.datasetName;
    this.datasetId = config.datasetId;
    /**
     * Buffer range for display in the bottom bar
     */
    this.bufferRange = 300;

    /**
     * Dataset url for data details
     */
    var dataUrl = config.dataUrl;

    this.columnModel = config.columnModel;
    
    this.titlePlot = new Ext.form.Field({
        anchor : "95%",
        fieldLabel : "Test !!", 
        name : "titlePlot"
    });

    /*
     * Constructor call
     */
    sitools.user.component.viewCubeService.superclass.constructor.call(this, Ext.apply({
        id : 'plot-panel',
        datasetName : config.datasetName, 
        layout : 'border',
        items : [ this.titlePlot ]
    }, config));
    
};

Ext.extend(sitools.user.component.viewCubeService, Ext.Panel, {
	/** 
     * Must be implemented to save window Settings
     * @return {}
     */
    _getSettings : function () {
        console.log('aaaaaaaaaaaaaaaaaaaaaaaaaaaaa');
        return {
            datasetName : this.datasetName, 
            //leftPanelValues : this.leftPanel.getForm().getValues(), 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };
    }, 
    /**
     * Load the userPreferences...
     */
    afterRender : function () {
        sitools.user.component.viewCubeService.superclass.afterRender.call(this);
	
    
        
    },
    
    /**
     * Function to show the details of a record
     * @param {} evt the event calling the function
     */
/*    showDataDetail : function (primaryKey) {
        
        
        var idx = encodeURIComponent(primaryKey);
        
        var jsObj = sitools.user.component.viewDataDetail;
        var componentCfg = {
            datasetUrl : this.dataUrl, 
            baseUrl : this.dataUrl + '/records',
            datasetId : this.datasetId, 
            fromWhere : "plot",
            url : this.dataUrl + '/records/' + idx, 
            preferencesPath : "/" + this.datasetName, 
            preferencesFileName : "dataDetails"
        };
        
        var windowConfig = {
            id : "simpleDataDetail" + this.datasetId, 
            title : i18n.get('label.viewDataDetail') + " : " + primaryKey,
            datasetName : this.datasetName, 
            saveToolbar : false, 
            type : "simpleDataDetail", 
            iconCls : "dataDetail"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
    } */
});

Ext.reg('sitools.user.component.viewCubeService', sitools.user.component.viewCubeService);
