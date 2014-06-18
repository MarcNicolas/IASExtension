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
sitools.common.forms.components.CheckboxCorotAstero = Ext.extend(Ext.Container, {

    initComponent : function () {
	this.context = new sitools.common.forms.ComponentFactory(this.context);
	if (Ext.isArray(this.defaultValues) && this.defaultValues.length == 4) {
		defaultLongRunAstero = this.defaultValues[0];
		defaultShortRunAstero = this.defaultValues[1];
		defaultCenterAstero = this.defaultValues[2];
		defaultAntiCenterAstero = this.defaultValues[3];
	}
	
        this.cbGroupAstero = new Ext.form.CheckboxGroup ({
	        allowBlank : true,
	        flex : 1,
		id : "cbGroupIdAstero",
		columns: 2,	        
		items : [ {
	    	    boxLabel : "Long Run", 
		    xtype : "checkbox",
		    id : "LongRunAsteroId",
		    checked : defaultLongRunAstero,
		 },{
		    boxLabel : "Center", 
	            xtype : "checkbox",
		    id : "CenterAsteroId",
	            checked : defaultCenterAstero,
		},{
		    boxLabel : "Short Run", 
	            xtype : "checkbox",
		    id : "ShortRunAsteroId",
	            checked : defaultShortRunAstero,
	        },{
		    boxLabel : "Anticenter", 
	            xtype : "checkbox",
		    id : "AntiCenterAsteroId",
	            checked : defaultAntiCenterAstero,
	        } 
		 ]
	    });
	    Ext.apply(this, {
	    	layout : "hbox",
	    	stype : "sitoolsFormContainer",
	        items : [this.cbGroupAstero]
	    });
	    sitools.common.forms.components.CheckboxCorotAstero.superclass.initComponent.apply(
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
	value = LongRunAsteroId.checked+"|"+ShortRunAsteroId.checked+"|"+CenterAsteroId.checked+"|"+AntiCenterAsteroId.checked
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
