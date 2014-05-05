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
 document, i18n, $, Flotr, userLogin, SitoolsDesk, sql2ext, loadUrl,SitoolsDesk,,
 SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, DEFAULT_LIVEGRID_BUFFER_SIZE*/
/*
 * @include "../viewDataDetail/viewDataDetail.js"
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../desktop/navProfile/fixed.js"
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
 * @class sitools.user.component.viewShowHeaderService
 * @extends Ext.Panel
 */
sitools.user.component.viewShowHeaderService = function (config) {

	Ext.apply(this, config);
   
	this.datasetName = config.datasetName;
	this.datasetId = config.datasetId;
	this.url = config.urlShowHeader;

	/**
	* Dataset url for data details
	*/
    
	this.columnModel = config.columnModel;
    
	var rec;
	if (Ext.isEmpty(this.record)) {
		rec = config.dataview.getSelections()[0];
	 }else{ 
		rec = this.record;
	}
	
	this.panel = new Ext.Panel({ id:'panel', title: 'Show Headers',region : 'center',layout: 'fit',autoScroll : true });
	

	// Searching for primary key name
	var primaryKeyName = "";
	Ext.each(rec.fields.items, function (field) {
		if (field.primaryKey) { primaryKeyName = field.name; }
	});
	var primaryKeyValue = rec.get(primaryKeyName);
	var request = "?1=1&p[0]=LISTBOXMULTIPLE|" + primaryKeyName + "|" + primaryKeyValue;
	
	Ext.Ajax.on('beforerequest', function(connection,options){ Ext.getBody().mask('Retrieving data from fits file...'); });
	Ext.Ajax.on('requestcomplete', function(connection,options){ Ext.getBody().unmask(); });
	Ext.Ajax.on('requestexception', function(connection,options){ Ext.getBody().unmask(); });

	Ext.Ajax.request({
        url : this.dataview.sitoolsAttachementForUsers+this.url+request,
        method : "GET",
        scope : this,
        waitMsg : 'Processing your request...',
        success : function (ret) { 
        	
		var json = Ext.decode(ret.responseText);
  		
    		var headerPanel = new Ext.Panel({
			//title: 'Header(s)',
			id: 'headerPanel',
			autoScroll: true,
			layout: 'accordion',
			//width: '100%',
			//height: '100%'
    		});
    		
    		itemsAccordion = [];
    		nbreHDUs = json.HDUs;
		console.log("NB HDU's : "+nbreHDUs);
    		for (var i=0; i<nbreHDUs; i++){
    			var extension = "";
    			var extensionName = "Header "+i;
    			if (i==0) {extensionName = "Primary";}
			var jsonHeader = [];
			for (var j = 0; j < json.HEADERSGRID[i].length; j++) {
				if (json.HEADERSGRID[i][j][0]=="EXTNAME") {extensionName=json.HEADERSGRID[i][j][1];}
				if (json.HEADERSGRID[i][j][0]=="XTENSION") {extension=json.HEADERSGRID[i][j][1];}
				extensionName = extensionName.charAt(0).toUpperCase() + extensionName.slice(1).toLowerCase();
				jsonHeader.push({
				        key: json.HEADERSGRID[i][j][0],
				        value: json.HEADERSGRID[i][j][1],
				        description: json.HEADERSGRID[i][j][2]
				});
			}
			var storeHeader = new Ext.data.JsonStore({
				fields : ['key','value','description'],
			}); 
			storeHeader.loadData(jsonHeader, false);
			var titleGrid;
			if (extensionName  == "Primary") {
				titleGrid = "<html><b>"+i+" - "+extensionName;
			}else{
				titleGrid = "<html><b>"+i+" - "+extensionName+" - <font color='red'>"+extension+"</font></b></html>";
			}
			var gridHeader = new Ext.grid.GridPanel({
				title: titleGrid,
				//autoScroll: true,
				layout: 'fit',
    				store : storeHeader,
				colModel: new Ext.grid.ColumnModel({
				    columns: [
				              {header: 'Key', width: 200, dataIndex: 'key', sortable: true},
				              {header: 'Value', width: 300, dataIndex: 'value'},
				              {header: 'Description', width: 600, dataIndex: 'description'},
				        ]
				}),
				width: '100%',
				height: '100%'
			}); 
    			itemsAccordion.push(gridHeader);
    		}
    		        		
    		headerPanel.add(itemsAccordion);
		this.panel.add(headerPanel);
		this.panel.doLayout();
		console.log("JSON : "+Object.keys(json));
        },
    	failure: function(){ alert("Ajax Request Failed !"); }
    });
	

    //***********************************************************************************************************************************************************
    /*
     * Constructor call
     */
    sitools.user.component.viewShowHeaderService.superclass.constructor.call(this, Ext.apply({
        id : 'viewHeader-panel',
        datasetName : config.datasetName, 
        layout : 'border',
	autoScroll : true,
        items : [this.panel]
    }, config));	
    
};

Ext.extend(sitools.user.component.viewShowHeaderService, Ext.Panel, {
	/** 
     * Must be implemented to save window Settings
     * @return {}
     */
    _getSettings : function () {
        return {
            datasetName : this.datasetName, 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };
    }, 
    /**
     * Load the userPreferences...
     */
    afterRender : function () {
        sitools.user.component.viewShowHeaderService.superclass.afterRender.call(this); 
    },
   
});

Ext.reg('sitools.user.component.viewShowHeaderService', sitools.user.component.viewShowHeaderService);
