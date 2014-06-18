/***************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.forms.multiParam');

sitools.admin.forms.multiParam.CorotAsteroCheckBox = Ext.extend(sitools.admin.forms.multiParam.abstractForm, {
	height : 330,
	id : "sitools.component.forms.definitionId",
	initComponent : function () {
		this.winPropComponent.specificHeight = 500;
		this.winPropComponent.specificWidth = 380;
		sitools.admin.forms.multiParam.CorotAsteroCheckBox.superclass.initComponent.call(this);

		this.context.buildCombosCorot(this);
		
		this.componentDefaultLongRunAstero = new Ext.form.TextField({
	            fieldLabel : 'Long Run Default Value ',
	            name : 'componentDefaultLongRun',
	            anchor : '100%'
	        });
	        this.componentDefaultShortRunAstero = new Ext.form.TextField({
	            fieldLabel : 'Short Run Default Value ',
	            name : 'componentDefaultShortRun',
	            anchor : '100%'
	        });
	        this.componentDefaultCenterAstero = new Ext.form.TextField({
	            fieldLabel : 'Center Default Value ',
	            name : 'componentDefaultCenter',
	            anchor : '100%'
	        });
		this.componentDefaultAntiCenterAstero = new Ext.form.TextField({
	            fieldLabel : 'AntiCenter Default Value ',
	            name : 'componentDefaultAntiCenter',
	            anchor : '100%'
	        });
	        this.add(this.componentDefaultLongRunAstero);
	        this.add(this.componentDefaultShortRunAstero);
	        this.add(this.componentDefaultCenterAstero);
		this.add(this.componentDefaultAntiCenterAstero);

	},
	onRender : function () {
		sitools.admin.forms.multiParam.CorotAsteroCheckBox.superclass.onRender.apply(this, arguments);
		if (this.action == 'modify') {
			if (!Ext.isEmpty(this.selectedRecord.data.defaultValues)) {
				this.componentDefaultLongRunAstero.setValue(this.selectedRecord.data.defaultValues[0]);
				this.componentDefaultShortRunAstero.setValue(this.selectedRecord.data.defaultValues[1]);
				this.componentDefaultCenterAstero.setValue(this.selectedRecord.data.defaultValues[2]);
				this.componentDefaultAntiCenterAstero.setValue(this.selectedRecord.data.defaultValues[3]);
			}
		}
	},
	_onValidate : function (action,formComponentsStore) {
		var f = this.getForm();
		if (!f.isValid()) {
			Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
			return false;
		}
		var defaultLongRunAstero = Ext.isEmpty(f.findField('componentDefaultLongRunAstero')) ? "" : f.findField('componentDefaultLongRunAstero').getValue();
        	var defaultShortRunAstero = Ext.isEmpty(f.findField('componentDefaultShortRunAstero')) ? "" : f.findField('componentDefaultShortRunAstero').getValue();
       		var defaultCenterAstero = Ext.isEmpty(f.findField('componentDefaultCenterAstero')) ? "" : f.findField('componentDefaultCenterAstero').getValue();
		var defaultAntiCenterAstero = Ext.isEmpty(f.findField('componentDefaultAntiCenterAstero')) ? "" : f.findField('componentDefaultAntiCenterAstero').getValue();

		var columnObjects = this.find('specificType', 'mapParam');
		var code = [];
		Ext.each(columnObjects, function (columnObject) {
			code.push(columnObject.getValue());
		});

		if (action == 'modify') {
			var rec = this.selectedRecord;
			var columnObjects = this.find('specificType', 'mapParam');
			var css = Ext.isEmpty(f.findField('CSS')) ? "" : f.findField('CSS').getValue();

			rec.set('label', labelParam1);
			rec.set('code', code);
			rec.set('css', css);
			rec.set('defaultValues', [ defaultLongRunAstero,  defaultShortRunAstero, defaultCenterAstero, defaultAntiCenterAstero ]);
		} else {
			var lastId = 0;
			formComponentsStore.each(function (component) {
	            		if (component.data.id > lastId) {
        	        	    lastId = parseInt(component.data.id, 10);
        	    		}
	            	});
   			
			var componentId = lastId + 1;
			componentId = componentId.toString();
//			alert("Ds BooleanCorotAdmin code : "+code);
			formComponentsStore.add(new Ext.data.Record({
				label : f.findField('LABEL_PARAM1').getValue(),
				type : this.ctype,
				code : code,
				width : f.findField('componentDefaultWidth').getValue(),
				height : f.findField('componentDefaultHeight').getValue(),
				id : componentId,
				ypos : this.xyOnCreate.y,
		                xpos : this.xyOnCreate.x, 
				css : f.findField('CSS').getValue(),
				jsAdminObject : this.jsAdminObject,
				jsUserObject : this.jsUserObject,
				defaultValues : [ defaultLongRunAstero,  defaultShortRunAstero, defaultCenterAstero,  defaultAntiCenterAstero],
				containerPanelId : this.containerPanelId
			}));
		}
		
		return true;
	}

});
