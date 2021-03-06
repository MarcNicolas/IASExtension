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
/*global Ext, sitools*/
/*
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.common.forms.components');

/**
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.common.forms.components.TextArea
 * @extends Ext.Container
 */
sitools.common.forms.components.TextArea = Ext.extend(Ext.Container, {

    initComponent : function () {
	this.context = new sitools.common.forms.ComponentFactory(this.context);
        var items = [];
		this.defaultValues = [];
		for (i = 0; i < this.values.length; i++) {
			value = this.values[i];
			items.push([ value.value, value.value ]);
			if (value.defaultValue) {
				this.defaultValues.push(value.value);
			}
		}

    	var store;
        this.panel = new Ext.form.TextArea({
            width : this.width,
            height : this.height - 10,
            emptyText : 'Entrer le texte que vous souhaitez !' 
            
        });
        /*if (this.valueSelection === 'S') {
            store = new Ext.data.ArrayStore({
                fields : ['value','text'],
                data : items, 
                valueField : 'value', 
                displayField : 'text'
            });
        } else {
            var params = {
                colModel : [ this.code ],
                distinct : true
            };
            store = new Ext.data.JsonStore({
                fields : [ {
                    name : 'value',
                    mapping : this.code
                }, {
                    name : 'text',
                    mapping : this.code
                } ],
                autoLoad : !Ext.isEmpty(this.dataUrl) ? true : false,
                root : 'data',
                restful : true,
                url : this.dataUrl + "/records",
                baseParams : params, 
                valueField : 'value', 
                displayField : 'text'
            });
        } 
        this.multiSelect = new Ext.ux.form.MultiSelect ({
	        store : store,
	        width : this.width,
	        height : this.height - 10,
	        flex : 1, 
	        delimiter : '|', 
			stype : "sitoolsFormItem", 
            listeners : {
                scope : this, 
                'click' : function () {
                    this.form.fireEvent('componentChanged', this.form, this);
                },
                'afterRender' : function () {
				    this.setSelectedValue(this.defaultValues);
			    }
            }
	    });*/
        Ext.apply(this, {
	    	height : this.height, 
	        width : this.width,
	        overCls : 'fieldset-child',
	        layout : "hbox", 
	        stype : "sitoolsFormContainer",
	        items : [this.panel]
	    });
	    sitools.common.forms.components.TextArea.superclass.initComponent.apply(this,
	            arguments);
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, new Ext.Container({
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

    },

    /**
	 * The code of the parameter to notify changed event.
	 */
    code : null,

    isValueDefined : function () {
	    if (this.panel.getValue() && this.panel.getValue() !== "") {
		    return true;
	    } else {
		    return false;
	    }
    },

    getSelectedValue : function () {
	    if (this.panel.getValue()) {
		    return this.panel.getValue();
	    } else {
		    return null;
	    }
    },
    

    setSelectedValue : function (values) {
	    this.panel.setValue(values);
    },
    
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    if (Ext.isEmpty(value)) {
		    return null;
	    }
	   	return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value
	    };
	    
    }
});
