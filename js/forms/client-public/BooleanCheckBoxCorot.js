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
sitools.common.forms.components.BooleanCheckboxCorot = Ext.extend(Ext.Container, {

    initComponent : function () {
	this.context = new sitools.common.forms.ComponentFactory(this.context);
	if (Ext.isArray(this.defaultValues) && this.defaultValues.length == 4) {
		defaultLongRun = this.defaultValues[0];
		defaultShortRun = this.defaultValues[1];
		defaultCenter = this.defaultValues[2];
		defaultAntiCenter = this.defaultValues[3];
	}
		
        this.cbGroup = new Ext.form.CheckboxGroup ({
	        allowBlank : true,
	        flex : 1,
		id : "cbGroupId",
		columns: 2,	        
		items : [ {
	    	    boxLabel : "Long Run", 
		    xtype : "checkbox",
		    id : "LongRunId",
		    checked : defaultLongRun,
		 },{
		    boxLabel : "Center", 
	            xtype : "checkbox",
		    id : "CenterId",
	            checked : defaultCenter,
		},{
		    boxLabel : "Short Run", 
	            xtype : "checkbox",
		    id : "ShortRunId",
	            checked : defaultShortRun,
	        },{
		    boxLabel : "Anticenter", 
	            xtype : "checkbox",
		    id : "AntiCenterId",
	            checked : defaultAntiCenter,
	        } 
		 ]
	    });
	    Ext.apply(this, {
	    	layout : "hbox",
	    	stype : "sitoolsFormContainer",
	        items : [this.cbGroup]
	    });
	    sitools.common.forms.components.BooleanCheckboxCorot.superclass.initComponent.apply(
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
	value = LongRunId.checked+"|"+ShortRunId.checked+"|"+CenterId.checked+"|"+AntiCenterId.checked
	return value;
    },
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value
	    };
    }

});
