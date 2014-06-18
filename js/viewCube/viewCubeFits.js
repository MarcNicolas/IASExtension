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
 * @class sitools.user.component.viewCubeService
 * @extends Ext.Panel
 */
sitools.user.component.viewCubeService = function (config) {

    Ext.apply(this, config);
   
    this.datasetName = config.datasetName;
    this.datasetId = config.datasetId;

    /**
     * Dataset url for data details
     */
    //var dataUrl = config.dataUrl;
    this.columnModel = config.columnModel;
    //this.columnAlias = config.columnAlias;

	/*for (var key in this.dataview) {
		if (typeof this.dataview[key] === "function") {
		        console.log("Dans le service : key = "+key);
		}
	}*/
	
	var rec;
        
	if (Ext.isEmpty(this.record)) {
		rec = this.dataview.getSelections();
		console.log("empty : "+Ext.isEmpty(rec));
		console.log("length : "+ rec.length);
		
		console.log("Je suis dans le if dans le Ext.isEmpty");
		//console.log("Object.keys(this.dataview) : "+Object.keys(this.dataview.getSelections()));
        } else {
		console.log("Je suis dans le else dans le Ext.isEmpty")
		rec = this.record;
        }
   
	console.log("rec : "+rec);
	
	var object = rec.json.object;
	var obsid = rec.json.obsid;
	
	this.titlePanel ="Cube Explorer - "+object+" - "+obsid; 
	
	var naxis1, naxis2, naxis3, sizePix;
	
	naxis1 = rec.json.naxis1;//json.NAXIS1;
	naxis2 = rec.json.naxis2;//json.NAXIS2;
	naxis3 = rec.json.naxis3;//json.NAXIS3;
	
	var imageWidth, imageHeight;
	var imageWidthWanted = 400;
	var imageHeightWanted = 500;
	
	sizePix = Math.floor(imageHeightWanted/naxis2);
	imageWidth = naxis1 * sizePix;
	imageHeight = naxis2 * sizePix;
	var controlHeight = 200;
	
	
	var selectionType = "pixel";
	var slice = parseInt(naxis3/2);
	var imageSlice;
	var limitVal = [];
	var pixelSelected, colors;
	
	this.tabPanel = new Ext.TabPanel({ id:'tabPanel',region : 'center'});
	var itemsTab = [];
	
	this.panels = new Ext.Panel({ id:'panels', title: 'Cube Explorer', layout: 'table', layoutConfig: {columns:2} });
	var itemsPanels = [];

    // Searching for primary key name
	var primaryKeyName = "";
	Ext.each(rec.fields.items, function (field) {
        if (field.primaryKey) { primaryKeyName = field.name; }
    });
	var primaryKeyValue = rec.get(primaryKeyName);
	var request = "?1=1&p[0]=LISTBOXMULTIPLE|" + primaryKeyName + "|" + primaryKeyValue + "|";
	
	//----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	Ext.Ajax.on('beforerequest', function(connection,options){ Ext.getBody().mask('Retrieving data from fits file...'); });
	Ext.Ajax.on('requestcomplete', function(connection,options){ Ext.getBody().unmask(); });
	Ext.Ajax.on('requestexception', function(connection,options){ Ext.getBody().unmask(); });

	Ext.Ajax.request({
        url : this.dataview.sitoolsAttachementForUsers+"/plugin/getFitsDataSpectro"+request,
        method : "GET",
        scope : this,
        waitMsg : 'Processing your request...',
        success : function (ret) { 
        	
        	var json = Ext.decode(ret.responseText);

    		var layersZone = "";
    		// layer for map
    		layersZone = layersZone + '<canvas id="mapCanvas" style="z-index: 1; position:absolute; left:0px; top:0px;" width="'+imageWidth+'" height="'+imageHeight+'"></canvas>';
    		// layer for selection tool 
    		layersZone = layersZone + '<canvas id="selCanvas" style="z-index: 2; position:absolute; left:0px; top:0px;" width="'+imageWidth+'" height="'+imageHeight+'"></canvas>';
    		
            var map = new Ext.Panel({
    		    id: 'panelImage',
    		    region: 'west',
    		    html: layersZone, //bodyStyle: "background-image:url("+this.src+")",
    		    padding: '5px',
    		    width: imageWidth,
    		    height: imageHeight,
            });
            itemsPanels.push(map);

    		var spectrum = new Ext.Panel({
    		    id: 'panelSpectrum',
    		    //region: 'east',
    		    rowspan: 2,
    		    padding: '10px 10px 10px 10px',
    		    width: imageHeight+controlHeight - 50,
    		    height: imageHeight+controlHeight - 50,
            });
    		itemsPanels.push(spectrum);
    		
    		var sliderDepth = new Ext.Slider({
    			id: 'sliderDepthID',
    	        width: 114,
    	        minValue: 0,
    	        maxValue: json.WAVE.length-1,
    	        value: parseInt(naxis3/2),
    	        listeners : {
    	        	change: function(slider){
    	        		slice = slider.getValue();
    	        		plotColorHisto(slice,json,0,100);
    	        		Ext.getCmp('sliderColorID').setValue(0,0,false);
    	        		Ext.getCmp('sliderColorID').setValue(1,100,false);
    	        		drawMap(sizePix,json,slice,false,[]);
    	        		Ext.getCmp('wave').setValue('<b>'+slice+' - '+json.WAVE[slice].toFixed(2)+' ('+json.UNIT_WAVE+')</b>');
    	        		plotSpectra(pixelSelected,selectionType,json,spectrum,slice);
    	             },
    	            dragend: function(slider){
    	            	slice = slider.getValue();
    	            	limitVal = [];//plotColorHisto(slice,json,0,100);
    	            	plotColorHisto(slice,json,0,100);
    	            	drawMap(sizePix,json,slice,false,[]);
    	            }
    	        }
    	        
    	    });
    		
    		var colorHisto = new Ext.Panel({
    		    html: '<canvas id="canHisto" width="'+120+'" height="'+50+'"></canvas>',
    		    width: 120,
    		    height: 50
            });
    		
    		var sliderColor = new Ext.slider.MultiSlider({
    	        id: 'sliderColorID',
    			width: 114,//parseInt(imageWidth/4),
    	        minValue: 0,
    	        maxValue: 100,
    	        values: [0,100],
    	        listeners : {
    	        	change: function(slider){
    	        		limitVal = plotColorHisto(slice,json,slider.thumbs[0].value,slider.thumbs[1].value);
    	        		drawMap(sizePix,json,slice,false,limitVal);
    	             },
    	            dragend: function(slider){
    	            	limitVal = plotColorHisto(slice,json,slider.thumbs[0].value,slider.thumbs[1].value);
    	            	drawMap(sizePix,json,slice,false,limitVal);
    	            }
    	        }
    	    });
    		
    		var controlHisto = new Ext.Panel({
    			id: 'controlHisto',
    			fieldLabel: "Slice color scale",
    	        heigth: controlHeight,
    	        xtype: 'compositefield',
    	        //padding: '5px',
    	        items   : [ sliderColor, colorHisto ]
    		});
    		
    		var controlForm = new Ext.form.FormPanel({
    			id: 'controlPanel',
    	        heigth: controlHeight,
    	        width: imageWidth,
    	        padding: '5px',
    	        items   : [
    	            {
    	            	fieldLabel: 'Coordinates',
    	            	xtype: 'compositefield',
    	                items: [
    	                    { xtype: 'displayfield', value: 'RA'},
    	                    { xtype: 'displayfield', id: 'ra_field', name : 'ra', width: 75 },
    	                    { xtype: 'displayfield', value: 'DEC'},
    	                    { xtype: 'displayfield', id: 'dec_field', name : 'dec', width: 75  }
    	                ]
    	            },
    	            {
        		    	fieldLabel: 'Selection Type',
        		    	xtype: 'compositefield',
        		    	items: [
        		    	        {
        		    	        	xtype: 'radiogroup',
        		    	        	width: parseInt(imageWidth/2),
        	        		        listeners: {change:function( radiogroup, newValue ) {
        	        	                selectionType = newValue.inputValue;
        	        	               // console.log(selectionType);
        	        	                can = document.getElementById('selCanvas');
        	        	                clearArea(can);
        		    	        		pixelSelected = [];
        		    	        		plotSpectra(pixelSelected,selectionType,json,spectrum,slice);
        	        	                drawSelectionPlot(sizePix,spectrum,json,selectionType);
        	        	                }
        	        	            },
        	        		        items: [
        	        		            { inputValue: 'pixel', boxLabel: 'Pixel(s)', name: 'radioGroup', checked: true },
        	        		            { inputValue: 'line',  boxLabel: 'Line',     name: 'radioGroup' },
        	        		            ]
        		    	        },
        		    	        {
        		    	        	xtype: 'button',
        		    	        	width: 50,
        		    	        	height: 30,
        		    	        	text: 'Clear',
        		    	        	handler: function(){
        		    	        		can = document.getElementById('selCanvas');
        		    	        		clearArea(can);
        		    	        		pixelSelected = [];
        		    	        		plotSpectra(pixelSelected,selectionType,json,spectrum,slice);
        		    	        	} 
        		    	        }	        		    	        
        		    	        ]
    	            },
        		    {
        		    	fieldLabel: 'Cube Depth Index',
        		    	xtype: 'compositefield',
        		    	items: [
        		    	        sliderDepth,
        		    	        { 	
        		    	        	id: 'wave',
        		    	        	xtype: 'displayfield',
        		    	        	value: '<b>'+sliderDepth.getValue()+' - '+json.WAVE[sliderDepth.getValue()].toFixed(2)+' ('+json.UNIT_WAVE+')</b>',
    		    	        	}
		    	        ]		        		   
        		    },
        		    controlHisto
        		    ]
    		});
    		itemsPanels.push(controlForm);

    		this.panels.add(itemsPanels);
    		this.panels.doLayout();
    		        		        		
    		itemsTab.push(this.panels);
    		
    		var headerPanel = new Ext.Panel({
    			title: 'Header(s)',
    		    id: 'headerPanel',
    		    autoScroll: true,
    		    layout: 'accordion',
    		    width: imageWidth+(imageHeight+controlHeight),
    		    height: imageHeight+controlHeight,
    		});
    		
    		itemsAccordion = [];
    		nbreHDUs = json.HDUs;
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
    			var gridHeader = new Ext.grid.GridPanel({
                    title: "<html><b>"+i+" - "+extensionName+" - <font color='red'>"+extension+"</font></b></html>",
                    autoScroll: true,
                    layout: 'fit',
    				store : storeHeader,
    		        colModel: new Ext.grid.ColumnModel({
    		            columns: [
    		                      {header: 'Key', width: 200, dataIndex: 'key', sortable: true},
    		                      {header: 'Value', width: 300, dataIndex: 'value'},
    		                      {header: 'Description', width: 600, dataIndex: 'description'},
    		                      ]
    		        })
    		    }); 
    			itemsAccordion.push(gridHeader);
    		}
    		        		
    		headerPanel.add(itemsAccordion);

    		itemsTab.push(headerPanel);
    		this.tabPanel.add(itemsTab);
    		this.tabPanel.setActiveTab(0);
    		this.tabPanel.doLayout();
    		        		
    		plotColorHisto(slice,json,0,100);
    		drawMap(sizePix,json,slice,true,limitVal);
    		drawSelectionPlot(sizePix,spectrum,json,selectionType);
    		
        },
    	failure: function(){ alert("Ajax Request Failed !"); }
    });
	//this.items = [this.tabPanel];
	
	function getImageSlice(jSON,slice,limitValues) {
		imageSlice = [];
		for (var x = 0; x < naxis1; x++) {
			for (var y = 0; y < naxis2; y++) {
				b = jSON.SPECTRUM[x][(naxis2-1)-y][slice];
				if (limitValues.length==0){
					if (b!="NaN"){ imageSlice.push(b); }
				} else {
					if (b!="NaN" && b>limitValues[0] && b<limitValues[1]){ imageSlice.push(b); }
				}
			}
		}
		return imageSlice;
	}
	
	function drawMap(sizeRec, jSON, slice, init, limitValues) {
		
		canMap = document.getElementById('mapCanvas');
		ctxCube = canMap.getContext('2d');
		if (init) { ctxCube.scale(sizeRec,sizeRec); }
		drawSlice(jSON,slice,limitValues);
		console.log("sizeRec "+sizeRec);
					
		function drawSlice(jSON,slice,limitValues) {

			imageSlice = getImageSlice(jSON,slice,limitValues);
			if (imageSlice.length!=0) {
				var min = Math.min.apply(Math, imageSlice);
				var max = Math.max.apply(Math, imageSlice);
				//console.log(min+" "+max)
				for (var x = 0; x < naxis1; x++) {
					for (var y = 0; y < naxis2; y++) {
					    b = jSON.SPECTRUM[x][(naxis2-1)-y][slice];//Math.random() * 256 | 0;//
					    if (b!="NaN"){
					    	b = ((b - min)/(min+max))*256 | 0;
					    	ctxCube.fillStyle = 'rgb(0,0,'+b+')';
							ctxCube.fillRect(x, y, 1, 1);
					    }
					}	
				}
			}				
		}			
	}
	//-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
	function clearArea(can) { 
		ctx = can.getContext('2d');
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
	}
	
	function drawSelectionPlot(sizeRec,containerSpec,jSON,selectionType) {
					
		var can, ctx, canX, canY, lastX, lastY, mouseIsDown = false;
		
		// Selection drawing
		canOrig = document.getElementById('selCanvas');
		can = canOrig.cloneNode(true);
		canOrig.parentNode.replaceChild(can, canOrig);

		ctx = can.getContext('2d');
		ctx.strokeStyle = 'white';
		ctx.lineWidth = 2;
		var rect = can.getBoundingClientRect();
				
        clearArea(can);
        pixelSelected = [];
        colors = [];
        
        can.addEventListener("mousedown", mouseDown);
        can.addEventListener("mousemove", mouseMove);
        can.addEventListener("mouseup", mouseUp);
        
        function deg_to_dms (deg) {
    	   var d = Math.floor (deg);
    	   var minfloat = (deg-d)*60;
    	   var m = Math.floor(minfloat);
    	   var secfloat = (minfloat-m)*60;
    	   var s = Math.round(secfloat);
    	   if (s==60) { m++; s=0; }
    	   if (m==60) { d++; m=0; }
    	   return ("" + d + "°" + m + "'" + secfloat.toFixed(2) + '"');
        }
        
        function mouseDown(e) {            	
        	clearArea(can);
		mouseIsDown = true;
		lastX = e.clientX - rect.left;
		lastY = e.clientY - rect.top;
        }

        function mouseMove(e) {
		canX = e.clientX - rect.left;
		canY = e.clientY - rect.top;
		if (mouseIsDown) { drawSelection(lastX,lastY,canX,canY,selectionType); }
		displayRaDec(canX,canY);
        }
        
        function mouseUp(e) {
            mouseIsDown = false;
            canX = e.clientX - rect.left;//-window.screenX;
	    console.log("e.clientX : "+e.clientX+"  rect.left : "+rect.left+ "  window.screenX : "+ window.screenX);
            canY = e.clientY - rect.top;//-window.screenY;
	    console.log("e.clientY : "+e.clientY+"  rect.top : "+rect.top+ "  window.screenX : "+ window.screenY);
            drawSelectedPixels(lastX,lastY,canX,canY,selectionType);	            
        }

		function drawSelection(lastX, lastY, x, y, selectionType) {
			
        	clearArea(can);
        	ctx.strokeStyle = 'white';
		    	
	    	if (selectionType=="rect"){ ctx.strokeRect(lastX,lastY,x-lastX,y-lastY); }
	    	
	    	if (selectionType=="circ"){
		        var radius = Math.sqrt( Math.pow((x-lastX),2) + Math.pow((y-lastY),2) );
	    		ctx.beginPath();
		        ctx.arc(lastX,lastY, radius, 0, 2 * Math.PI);
		        ctx.stroke();
	    	}
	    	
	    	if (selectionType=="line"){ 
	    		ctx.beginPath();
	    		ctx.moveTo(lastX, lastY);
	    		ctx.lineTo(x, y);
		        ctx.stroke();
	        }
		    	
		}
		
		function displayRaDec(canX,canY) {
			xFits = Math.floor(canX/sizeRec);
			yFits = Math.floor(canY/sizeRec);
			var ra  = jSON.CRVAL1 + (xFits-(jSON.CRPIX1-1))*jSON.CDELT1;
			var dec = jSON.CRVAL2 + (((naxis2-1)-(jSON.CRPIX2-1))-yFits)*jSON.CDELT2;
			Ext.getCmp('ra_field').setValue('<b>'+deg_to_dms(ra)+'</b>');
			Ext.getCmp('dec_field').setValue('<b>'+deg_to_dms(dec)+'</b>');
		}
		
		function allreadySelected(array,x,y){
			for ( var pix = 0; pix<array.length; pix++) {
				if ( array[pix][0]==x && array[pix][1]==y ) { return pix;}
			}
			return -1;
		}
		
		function hexaDecimalColor() {
			function componentToHex(c) {
			    var hex = c.toString(16);
			    return hex.length == 1 ? "0" + hex : hex;
			}
			function rgbToHex(r, g, b) {
			    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(0);
			}
			return rgbToHex(Math.random()*256|0,Math.random()*256|0,0);
		}
		
		function drawSelectedPixels(startx,starty,endx,endy,selectionType) {
							
			clearArea(can);
			
			if (selectionType=="pixel"){
	            xFits = Math.floor(startx/sizeRec);
				yFits = Math.floor(starty/sizeRec);
				
				if (jSON.SPECTRUM[xFits][(naxis2-1)-yFits][parseInt(naxis3/2)]!="NaN"){
					console.log("pixelSelected : "+pixelSelected);
					if (allreadySelected(pixelSelected,xFits,yFits)!=-1){
						//console.log(xFits+" "+yFits+" already in selection");
						var idx = allreadySelected(pixelSelected,xFits,yFits);
						pixelSelected = pixelSelected.slice(0,idx).concat( pixelSelected.slice(idx+1) );
						colors = colors.slice(0,idx).concat( colors.slice(idx+1) );
					} else {
						pixelSelected.push([xFits,yFits]);
						colors.push(hexaDecimalColor());
					}
					for ( var pixel = 0; pixel<pixelSelected.length; pixel++) {
						xFits = pixelSelected[pixel][0];
						yFits = pixelSelected[pixel][1];
						ctx.strokeStyle = colors[pixel];
						ctx.strokeRect(xFits*sizeRec+1,yFits*sizeRec+1,sizeRec-1,sizeRec-1);
					}
				}					
			}
			
			/*if (selectionType=="rect"){
				
				pixelSelected = [];
				
				startX = Math.floor(startx/sizeRec);
				startY = Math.floor(starty/sizeRec);
				endX = Math.ceil(endx/sizeRec);
				endY = Math.ceil(endy/sizeRec);
				
				for (var i = startX; i < (endX); i++){
					for (var j = startY; j < (endY); j++){
						if (jSON.SPECTRUM[i][(naxis2-1)-j][parseInt(naxis3/2)]!="NaN"){
							ctx.strokeRect(i*sizeRec+1,j*sizeRec+1,sizeRec-1,sizeRec-1);
							//ctx.fillRect(i*sizeRec+(sizeRec/2),j*sizeRec+(sizeRec/2),1,1);
							pixelSelected.push([i,j]);
						}
					}
				}
			}
			
			if (selectionType=="circ"){
				
				pixelSelected = [];
				
				startX = Math.round(startx/sizeRec);
				startY = Math.round(starty/sizeRec);
				endX = Math.round(endx/sizeRec);
				endY = Math.round(endy/sizeRec);
				
				var radiusSelection =  Math.sqrt( Math.pow((endX*sizeRec-startX*sizeRec),2) + Math.pow((endY*sizeRec-startY*sizeRec),2) );
				radiusSelection = Math.floor(radiusSelection/sizeRec);
				
				for (var i = startX-radiusSelection; i < startX+radiusSelection; i++){
					for (var j = startY-radiusSelection; j < startY+radiusSelection; j++){
						distanceToCenter = Math.sqrt( Math.pow((i-startX),2) + Math.pow((j-startY),2));
						if (distanceToCenter<radiusSelection && jSON.SPECTRUM[i][(naxis2-1)-j][parseInt(naxis3/2)]!="NaN"){
							ctx.strokeRect(i*sizeRec,j*sizeRec,sizeRec-1,sizeRec-1);
							//ctx.fillRect(i*sizeRec+(sizeRec/2),j*sizeRec+(sizeRec/2),1,1);
							pixelSelected.push([i,j]);
							
						}
					}
				}
			}*/
			
			if (selectionType=="line"){
				
				pixelSelected = [];
				colors = [];
				
				startX = Math.floor(startx/sizeRec);
				startY = Math.floor(starty/sizeRec);
				endX = Math.ceil(endx/sizeRec);
				endY = Math.ceil(endy/sizeRec);
				
				//console.log("interpol x");
				slope = (endY-startY)/(endX-startX);
				//console.log("Line - "+startX+" "+startY+" - "+endX+" "+endY+" - "+slope);
				for (var x = Math.min(startX,endX); x <= Math.max(startX,endX); x++) {
					if (slope>0) { y = Math.floor(slope*(x-Math.min(startX,endX))) + Math.min(startY,endY); }
					else { y = Math.floor(slope*(x-Math.min(startX,endX))) + Math.max(startY,endY); }
					if (jSON.SPECTRUM[x][(naxis2-1)-y][parseInt(naxis3/2)]!="NaN" ){
						//console.log(x+" "+y);
						color = hexaDecimalColor();
						colors.push(color);
						ctx.strokeStyle = color;
						ctx.strokeRect(x*sizeRec+1,y*sizeRec+1,sizeRec-1,sizeRec-1);
						pixelSelected.push([x,y]);
					}

				}
				
				
				slope = (endX-startX)/(endY-startY);
				//console.log("Line - "+startX+" "+startY+" - "+endX+" "+endY+" - "+slope);
				for (var y = Math.min(startY,endY); y <= Math.max(startY,endY); y++) {
					if (slope>0) { x = Math.floor(slope*(y-Math.min(startY,endY))) + Math.min(startX,endX); }
					else { x = Math.floor(slope*(y-Math.min(startY,endY))) + Math.max(startX,endX); }
					if (jSON.SPECTRUM[x][(naxis2-1)-y][parseInt(naxis3/2)]!="NaN" && allreadySelected(pixelSelected,x,y)==-1){
						//console.log(x+" "+y);
						color = '#'+Math.floor(Math.random()*16777215).toString(16);
						colors.push(color);
						ctx.strokeStyle = color;
						ctx.strokeRect(x*sizeRec+1,y*sizeRec+1,sizeRec-1,sizeRec-1);
						pixelSelected.push([x,y]);
					}

				}
			}

			plotSpectra(pixelSelected,selectionType,jSON,containerSpec,slice);

		}
		
	}
		
	function plotSpectra(pixelSelected,selectionType,jSON,containerSpec,slice){
	
		//Spectrum
		var spec = [];
		var zero = [];
		
		var minF, maxF, xFits, yFits, titlePlot = "";
		
		var trunc = 3;
		
		zero.push([parseFloat(jSON.WAVE[trunc]),0]);
		zero.push([parseFloat(jSON.WAVE[jSON.WAVE.length-trunc]),0]);
		
		var unitWave = "("+jSON.UNIT_WAVE+")";
		var unitFlux = "("+jSON.UNIT_QTTY+")";
		var infoFlux = jSON.INFO_QTTY;
		var infoWave = jSON.INFO_WAVE;	
			
		if (selectionType=="rect" || selectionType=="circ") {
			var specLine = [];
			for ( var i = trunc; i <= jSON.WAVE.length - trunc; i++) {
				var fluxXY = 0;
				var nbPix = 0;
				for ( var pixel = 0; pixel<pixelSelected.length; pixel++){
					xFits = pixelSelected[pixel][0];
					yFits = pixelSelected[pixel][1];
					if (jSON.SPECTRUM[xFits][(naxis2-1)-yFits][i]!="Nan"){
						fluxXY = fluxXY+ ( jSON.SPECTRUM[xFits][(naxis2-1)-yFits][i] / (pixelSelected.length) );							
						nbPix++;
						}
					}
				fluxXY = fluxXY / nbPix;
				if (i==trunc){minF = fluxXY; maxF = fluxXY;}
				if (fluxXY<minF && !isNaN(fluxXY)){minF = fluxXY;}
				if (fluxXY>maxF && !isNaN(fluxXY)){maxF = fluxXY;}
				waveXY = parseFloat(jSON.WAVE[i]);
				specLine.push([waveXY,fluxXY]);
				}
			
			titlePlot = "Average "+infoFlux+" "+unitFlux+" for all selected pixel(s)";
			spec.push(specLine);
		}
		
		
		if (selectionType=="line" || selectionType=="pixel" ){
			for ( var pixel = 0; pixel<pixelSelected.length; pixel++) {
				var specLine = [];
				for ( var i = trunc; i <= jSON.WAVE.length - trunc; i++) {
					xFits = pixelSelected[pixel][0];
					yFits = pixelSelected[pixel][1];
					fluxXY = jSON.SPECTRUM[xFits][(naxis2-1)-yFits][i];
					if (i==trunc && pixel == 0){minF = fluxXY; maxF = fluxXY;}
					if (fluxXY<minF && !isNaN(fluxXY)){minF = fluxXY;}
					if (fluxXY>maxF && !isNaN(fluxXY)){maxF = fluxXY;}
					waveXY = parseFloat(jSON.WAVE[i]);
					specLine.push([waveXY,fluxXY]);
					}
				spec.push(specLine);
				
				}
			
			titlePlot = infoFlux+" "+unitFlux+" for each selected pixel(s)";
		}
		
		margeF = 0.1*(maxF-minF);

		var sliceLine = [];
		sliceLine.push([parseFloat(jSON.WAVE[slice]),minF-margeF]);
		sliceLine.push([parseFloat(jSON.WAVE[slice]),maxF+margeF]);
		
		options = {
				HtmlText: true,
		        xaxis: { title: infoWave+" "+unitWave, noTicks: 4 },
		        yaxis: { min: minF-margeF, max: maxF+margeF, base: Math.E,
		        	tickFormatter: function(x) {
		        		return parseFloat(x).toExponential();
		        	}
		        },
		        lines: { show: true },
		        points: { show: false },//{ show: true, radius: 1 },
		        selection: { mode: 'xy', fps: 50 },
		        title: titlePlot,
		        subtitle: 'Click to unzoom',
		        mouse: { track: true, relative: true }
		    };
		
		container = $(containerSpec.body.id);
		
		function drawGraph(spectralData,opts) {
	        var o = Flotr._.extend(Flotr._.clone(options), opts || {});
	        varToPlot = [];
	        varToPlot.push(
        			{
		            data: zero,
		            color: '#FC0000',
	            	});
/*		        varToPlot.push(
        			{
		            data: sliceLine,
		            color: '#FC0000',
	            	});*/
	        if (spectralData.length!=0) {
	        	for (var i = 0; i<=spectralData.length;i++){
	        		varToPlot.push(
		        			{
	        				data: spectralData[i],
	        				color: colors[i]
		        			});
	        	}
	        }
	        Flotr.draw(container, varToPlot, o);
	        
	    }
		drawGraph(spec);
		
		Flotr.EventAdapter.observe(container, 'flotr:select', function(area) {
	        drawGraph(spec,{
	            xaxis: { min: area.x1, max: area.x2 },
	            yaxis: { min: area.y1, max: area.y2 }
	        });
	    });

	    Flotr.EventAdapter.observe(container, 'flotr:click', function() {
	        drawGraph(spec);
	    });
		
	}
	
	function plotColorHisto(slice,jSON,sliderL,sliderR){
		
		imageSlice = getImageSlice(jSON,slice,[]);
		
		var nbrBin = 100;
		
		var minS = Math.min.apply(Math, imageSlice);
		var maxS = Math.max.apply(Math, imageSlice);
				
		var binSize = (maxS-minS)/nbrBin;
		
		histo = [];
		for (var x = 0; x < nbrBin; x++) { histo.push(0);}
		for (var x = 0; x < imageSlice.length; x++) { histo[Math.floor((imageSlice[x]-minS)/binSize)-1] += 1; }
		
		var maxHisto = Math.max.apply(Math, histo);
//		console.log(histo+" "+maxHisto);
		
		canHisto = document.getElementById('canHisto');
		ctxHisto = canHisto.getContext('2d');
		
		ctxHisto.clearRect(0, 0, ctxHisto.canvas.width, ctxHisto.canvas.height);
		
		ctxHisto.strokeStyle = 'black';
		ctxHisto.lineWidth = 1;	
		
		for (var x = 0; x < histo.length; x++){
			ctxHisto.strokeRect( 6+x, ctxHisto.canvas.height-5, 1, (-1)*histo[x]*Math.floor((ctxHisto.canvas.height-10)/maxHisto) );
		}
		ctxHisto.strokeStyle = 'red';
		ctxHisto.strokeRect( 6+sliderL, ctxHisto.canvas.height-5, 1, (-1)*(ctxHisto.canvas.height-10) );
		ctxHisto.strokeRect( 6+sliderR, ctxHisto.canvas.height-5, 1, (-1)*(ctxHisto.canvas.height-10) );
		
		return [minS+sliderL*binSize, minS+sliderR*binSize];
		
	}
	
	this.tabPanel.on('resize', function(vp, width, height) {
	    var me = this;
	    winWidth = me.getWidth();
	    winHeight = me.getHeight();
	});
	
    //***********************************************************************************************************************************************************
    /*
     * Constructor call
     */
    sitools.user.component.viewCubeService.superclass.constructor.call(this, Ext.apply({
        id : 'viewCube-panel',
        datasetName : config.datasetName, 
        layout : 'border',
        items : [ this.tabPanel ]
    }, config));	
    
};

Ext.extend(sitools.user.component.viewCubeService, Ext.Panel, {
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
        sitools.user.component.viewCubeService.superclass.afterRender.call(this);
    
        
        
    },
   
});

Ext.reg('sitools.user.component.viewCubeService', sitools.user.component.viewCubeService);

