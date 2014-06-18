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

sitools.admin.forms.multiParam.BooleanCorotAsteroAdmin = Ext.extend(sitools.admin.forms.multiParam.abstractForm, {
	height : 330,
	id : "sitools.component.forms.definitionId",
	initComponent : function () {
		this.winPropComponent.specificHeight = 500;
		this.winPropComponent.specificWidth = 380;
		sitools.admin.forms.multiParam.BooleanCorotAsteroAdmin.superclass.initComponent.call(this);

		this.context.buildCombosCorotAstero(this);
		
		this.componentDefaultLongRun = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Long Run',
	            name : 'componentDefaultLongRun',
	            anchor : '100%'
	        });
	        this.componentDefaultShortRun = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Short Run',
	            name : 'componentDefaultShortRun',
	            anchor : '100%'
	        });
	        this.componentDefaultCenter = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Center',
	            name : 'componentDefaultCenter',
	            anchor : '100%'
	        });
		this.componentDefaultAntiCenter = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' AntiCenter',
	            name : 'componentDefaultAntiCenter',
	            anchor : '100%'
	        });
	        this.add(this.componentDefaultLongRun);
	        this.add(this.componentDefaultShortRun);
	        this.add(this.componentDefaultCenter);
		this.add(this.componentDefaultAntiCenter);

	},
	onRender : function () {
		sitools.admin.forms.multiParam.BooleanCorotAsteroAdmin.superclass.onRender.apply(this, arguments);
		if (this.action == 'modify') {
			if (!Ext.isEmpty(this.selectedRecord.data.defaultValues)) {
				this.componentDefaultLongRun.setValue(this.selectedRecord.data.defaultValues[0]);
				this.componentDefaultShortRun.setValue(this.selectedRecord.data.defaultValues[1]);
				this.componentDefaultCenter.setValue(this.selectedRecord.data.defaultValues[2]);
				this.componentDefaultAntiCenter.setValue(this.selectedRecord.data.defaultValues[3]);
			}
		}
	},
	_onValidate : function (action,formComponentsStore) {
		var f = this.getForm();
		if (!f.isValid()) {
			Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
			return false;
		}
		var defaultLongRun = Ext.isEmpty(f.findField('componentDefaultLongRun')) ? "" : f.findField('componentDefaultLongRun').getValue();
        	var defaultShortRun = Ext.isEmpty(f.findField('componentDefaultShortRun')) ? "" : f.findField('componentDefaultShortRun').getValue();
       		var defaultCenter = Ext.isEmpty(f.findField('componentDefaultCenter')) ? "" : f.findField('componentDefaultCenter').getValue();
		var defaultAntiCenter = Ext.isEmpty(f.findField('componentDefaultAntiCenter')) ? "" : f.findField('componentDefaultAntiCenter').getValue();

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
			rec.set('defaultValues', [ defaultLongRun,  defaultShortRun, defaultCenter, defaultAntiCenter ]);
		} else {
			var lastId = 0;
			formComponentsStore.each(function (component) {
	            		if (component.data.id > lastId) {
        	        	    lastId = parseInt(component.data.id, 10);
        	    		}
	            	});
   			
			var componentId = lastId + 1;
			componentId = componentId.toString();
			alert("Ds BooleanCorotAsteroAdmin code : "+code);
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
				defaultValues : [ defaultLongRun,  defaultShortRun, defaultCenter,  defaultAntiCenter],
				containerPanelId : this.containerPanelId
			}));
		}
		
		return true;
	}

});
