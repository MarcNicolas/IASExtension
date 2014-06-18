/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools*/
/*
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.common.forms.components');

/**
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.common.forms.components.BooleanCheckbox
 * @extends Ext.Container
 */
sitools.common.forms.components.GroupCheckBoxCorot = Ext.extend(Ext.Container, {

    initComponent : function () {
	this.context = new sitools.common.forms.ComponentFactory(this.context);
	if (Ext.isArray(this.defaultValues) && this.defaultValues.length == 4) {
		defaultStarChr = this.defaultValues[0];
		defaultStarMon = this.defaultValues[1];
		defaultStar512 = this.defaultValues[2];
		defaultStar32 = this.defaultValues[3];
	}
	var defaultCheckBoxMasterValue;
	if (defaultStarChr && defaultStarMon && defaultStar512 && defaultStar32){
		defaultCheckBoxMasterValue = true;
	}else{
		defaultCheckBoxMasterValue = false;
	}
	
        this.cbGroup = new Ext.form.CheckboxGroup ({
	        allowBlank : true,
	        flex : 1,
		id : "cbGroupIdCurves",
		columns: 2,	        
		items : [ {
	    	    boxLabel : "Chromatic Light Curves",
		    xtype : "checkbox",
		    id : "StarChrId",
		    name : "StarChrName",
		    checked : defaultStarChr,
		 },{
		    boxLabel : "Monochromatic Light Curves", 
	            xtype : "checkbox",
		    id : "StarMonId",
		    name : "StarMonName",
	            checked : defaultStarMon,
		},{
		    boxLabel : "Star 512", 
	            xtype : "checkbox",
		    id : "Star512Id",
		    name : "Star512Name",
	            checked : defaultStar512,
	        },{
		    boxLabel : "Star 32",  
	            xtype : "checkbox",
		    id : "Star32Id",
		    name : "Star32Name",
	            checked : defaultStar32,
	        } 
		 ]
	    });

	this.checkBoxMaster = new Ext.form.Checkbox({
		id : "checkBoxMasterId",
		boxLabel : " Light Curves &nbsp&nbsp",
		name : "checkBoxMasterName",
		checked : defaultCheckBoxMasterValue,
		value: defaultCheckBoxMasterValue,
		listeners: {
                	check: function (checked) {
//				console.log("je suis dans checked et checked.checked : "+checked.checked);
				if(checked.checked){
//					console.log("je suis dans le if");
					StarChrId.setValue(true);
					StarMonId.setValue(true);
					Star512Id.setValue(true);
					Star32Id.setValue(true);
				}else{
//					console.log("Je suis dans le else");
					StarChrId.setValue(false);
					StarMonId.setValue(false);
					Star512Id.setValue(false);
					Star32Id.setValue(false);
				}
                	}
		}
	});

	    Ext.apply(this, {
	    	layout : "hbox",
	    	stype : "sitoolsFormContainer",
	        items : [this.checkBoxMaster,this.cbGroup]
	    });
	    sitools.common.forms.components.GroupCheckBoxCorot.superclass.initComponent.apply(
	            this, arguments);
   	    
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, new Ext.Container({
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

	},

    getSelectedValue : function () {
        var value;
	value = StarChrId.checked+"|"+StarMonId.checked+"|"+Star512Id.checked+"|"+Star32Id.checked
	return value;
    },
    getParameterValue : function () {
	    var value = this.getSelectedValue();
//	console.log("value : "+value);
	    return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value
	    };
    }

});
