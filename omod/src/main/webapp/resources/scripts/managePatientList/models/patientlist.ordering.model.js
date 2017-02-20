/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 *
 */

(function() {
	'use strict';
	
	var baseModel = angular.module('app.patientListOrderingModel', []);
	
	function PatientListOrderingModel() {
		
		function PatientListOrderingModel(field, sortOrder,id) {
			this.field = field;
			this.sortOrder = sortOrder;
			this.selected = false;
			this.id = id;
		}
		
		PatientListOrderingModel.prototype = {
			
			getField: function() {
				return this.field;
			},
			
			setField: function(feild) {
				this.field = feild;
			},
			
			getId: function() {
				return this.id;
			},
			
			setId: function(id) {
				this.id = id;
			},
			
			getSortOrder: function() {
				return this.sortOrder;
			},
			
			setSortOrder: function(sortOrder) {
				this.sortOrder = sortOrder;
			},
			
			setSelected: function(selected) {
				this.selected = selected;
			},
			
			isSelected: function() {
				return this.selected;
			}
			
		};
		
		return PatientListOrderingModel;
	}
	
	baseModel.factory("PatientListOrderingModel", PatientListOrderingModel);
	PatientListOrderingModel.$inject = [];
})();
