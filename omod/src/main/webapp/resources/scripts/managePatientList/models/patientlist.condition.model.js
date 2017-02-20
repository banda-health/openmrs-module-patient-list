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

	var baseModel = angular.module('app.patientListConditionModel', []);

	function PatientListConditionModel() {

		function PatientListConditionModel(field, operator, value, inputType, conditionOrder,id, valueRef, dataType) {
			this.field = field;
			this.operator = operator;
			this.value = value;
			this.selected = false;
			this.conditionOrder = conditionOrder;
			this.inputType = inputType;
			this.id = id;
			this.valueRef = valueRef;
			this.dataType = dataType;
		}

		PatientListConditionModel.prototype = {
			
			getDataType: function () {
				return this.dataType;
			},
			
			setDataType: function (dataType) {
				this.dataType = dataType;
			},

			getField: function() {
				return this.field;
			},

			setField: function(feild) {
				this.field = feild;
			},
			
			getInputType: function() {
				return this.inputType;
			},
			
			setInputType: function(inputType) {
				this.inputType = inputType;
			},
			
			getId: function() {
				return this.id;
			},
			
			setId: function(id) {
				this.id = id;
			},
			
			getOperator: function() {
				return this.operator;
			},

			setOperator: function(operator) {
				this.operator = operator;
			},

			getValue: function() {
				return this.value;
			},

			setValue: function(value) {
				this.value = value;
			},
			
			getValueRef: function() {
				return this.valueRef;
			},
			
			setValueRef: function(valueRef) {
				this.valueRef = valueRef;
			},
			
			getConditionOrder: function() {
				return this.conditionOrder;
			},
			
			setConditionOrder: function(conditionOrder) {
				this.conditionOrder = conditionOrder;
			},
			
			setSelected: function(selected) {
				this.selected = selected;
			},
			
			isSelected: function() {
				return this.selected;
			}
			
		};

		return PatientListConditionModel;
	}

	baseModel.factory("PatientListConditionModel", PatientListConditionModel);
	PatientListConditionModel.$inject = [];
})();
