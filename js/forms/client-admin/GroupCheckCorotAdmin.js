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

sitools.admin.forms.multiParam.GroupCheckCorotAdmin = Ext.extend(sitools.admin.forms.multiParam.abstractForm, {
	height : 330,
	id : "sitools.component.forms.definitionId",
	initComponent : function () {
		this.winPropComponent.specificHeight = 500;
		this.winPropComponent.specificWidth = 380;
		sitools.admin.forms.multiParam.GroupCheckCorotAdmin.superclass.initComponent.call(this);

		this.context.buildComboGroupCheckBoxCorot(this);
		
		this.componentDefaultStarChr = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Star Chr',
	            name : 'componentDefaultStarChr',
	            anchor : '100%'
	        });
	        this.componentDefaultStarMon = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Star Mon',
	            name : 'componentDefaultStarMon',
	            anchor : '100%'
	        });
	        this.componentDefaultStar512 = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Star 512',
	            name : 'componentDefaultStar512',
	            anchor : '100%'
	        });
		this.componentDefaultStar32 = new Ext.form.TextField({
	            fieldLabel : i18n.get('label.defaultValue') + ' Star 32',
	            name : 'componentDefaultStar32',
	            anchor : '100%'
	        });
	        this.add(this.componentDefaultStarChr);
	        this.add(this.componentDefaultStarMon);
	        this.add(this.componentDefaultStar512);
		this.add(this.componentDefaultStar32);

	},
	onRender : function () {
		sitools.admin.forms.multiParam.GroupCheckCorotAdmin.superclass.onRender.apply(this, arguments);
		if (this.action == 'modify') {
			if (!Ext.isEmpty(this.selectedRecord.data.defaultValues)) {
				this.componentDefaultStarChr.setValue(this.selectedRecord.data.defaultValues[0]);
				this.componentDefaultStarMon.setValue(this.selectedRecord.data.defaultValues[1]);
				this.componentDefaultStar512.setValue(this.selectedRecord.data.defaultValues[2]);
				this.componentDefaultStar32.setValue(this.selectedRecord.data.defaultValues[3]);
			}
		}
	},
	_onValidate : function (action,formComponentsStore) {
		var f = this.getForm();
		if (!f.isValid()) {
			Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
			return false;
		}
		var defaultStarChr = Ext.isEmpty(f.findField('componentDefaultStarChr')) ? "" : f.findField('componentDefaultStarChr').getValue();
        	var defaultStarMon = Ext.isEmpty(f.findField('componentDefaultStarMon')) ? "" : f.findField('componentDefaultStarMon').getValue();
       		var defaultStar512 = Ext.isEmpty(f.findField('componentDefaultStar512')) ? "" : f.findField('componentDefaultStar512').getValue();
		var defaultStar32 = Ext.isEmpty(f.findField('componentDefaultStar32')) ? "" : f.findField('componentDefaultStar32').getValue();
		
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
			rec.set('defaultValues', [ defaultStarChr,  defaultStarMon, defaultStar512, defaultStar32 ]);
		} else {
			var lastId = 0;
			formComponentsStore.each(function (component) {
	            		if (component.data.id > lastId) {
        	        	    lastId = parseInt(component.data.id, 10);
        	    		}
	            	});
   			
			var componentId = lastId + 1;
			componentId = componentId.toString();

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
				defaultValues : [ defaultStarChr,  defaultStarMon, defaultStar512, defaultStar32],
				containerPanelId : this.containerPanelId
			}));
		}
		
		return true;
	}

});
