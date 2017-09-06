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
	
	var app = angular.module('app.patientListFunctionsFactory', []);
	app.service('PatientListFunctions', PatientListFunctions);
	
	PatientListFunctions.$inject = ['EntityFunctions', 'PatientListConditionModel','PatientListOrderingModel', '$filter', '$timeout'];
	
	function PatientListFunctions(EntityFunctions, PatientListConditionModel,PatientListOrderingModel, $filter, $timeout) {
		var service;
		
		service = {
			populateExistingPatientListCondition: populateExistingPatientListCondition,
			populateExistingPatientListOrdering: populateExistingPatientListOrdering,
			validateListConditions: validateListConditions,
			onChangeDatePicker: onChangeDatePicker,
			formatDate: formatDate
		};
		
		return service;
		
		function formatDate(date, includeTime) {
			var format = 'yyyy-MM-dd';
			if (includeTime) {
				format += ' HH:mm';
			}
			return ($filter('date')(new Date(date), format));
		}
		
		function populateExistingPatientListCondition(listConditions, populatedListConditions, $scope) {
			for (var i = 0; i < listConditions.length; i++) {
				var listCondition = listConditions[i];
				var value = listCondition.value;
				
				if (listCondition !== null) {
					var listConditionModel = new PatientListConditionModel(listCondition.field, listCondition.operator,
						value, listCondition.inputType, listCondition.conditionOrder, listCondition.valueRef, listCondition.dataType);
					listConditionModel.setSelected(true);

					if (listCondition.dataType == "java.util.Date") {
						onChangeDatePicker($scope.onListConditionDateSuccessfulCallback, undefined, listCondition);
					} else if (listCondition.inputType == "conceptInput") {
						$scope.getConceptName(value, function (data) {
							listConditionModel.setValueRef(value);
							listConditionModel.setValue(data["display"]);
						});
						
					} else if (listCondition.dataType == "java.lang.Boolean") {
						if (value == "false") {
							listConditionModel.setValue(false);
						} else {
							listConditionModel.setValue(true);
						}
						
					} else if (listCondition.dataType == "org.openmrs.Location") {
						$scope.getLocationUuid(value, function (data) {
							listConditionModel.setValueRef(value);
							listConditionModel.setValue(data["uuid"]);
						});
					} else if (listCondition.inputType == "numberInput" && listCondition.operator == "BETWEEN") {
						var values = value.split("|");
						listConditionModel.setBetweenValues(values);
					} else if (listCondition.inputType == "dateInput" && listCondition.operator == "BETWEEN") {
						var dates = value.split("|");
						listConditionModel.setBetweenValues(dates);
					}
					
					listConditionModel.setInputType(listCondition.inputType);
					listConditionModel.setId(listCondition.field + "_" + listCondition.value);

					for (var r = 0; r < $scope.fields.length; r++) {
						if ($scope.fields[r].field == listCondition.field) {
							listConditionModel.setDataType($scope.fields[r].desc.datatype);
							$scope.valueInputConditions($scope.fields[r].desc, listConditionModel);
						}
					}

					populatedListConditions.push(listConditionModel);
					
					$scope.listConditions = populatedListConditions;
				}
			}
		}
		
		
		function populateExistingPatientListOrdering(listOrderings, populatedListOrdering, $scope) {
			for (var i = 0; i < listOrderings.length; i++) {
				var listOrdering = listOrderings[i];
				if (listOrdering !== null) {
					var listOrderingModel = new PatientListOrderingModel(listOrdering.field, listOrdering.sortOrder);
					listOrderingModel.setSelected(true);
					listOrderingModel.setId(listOrdering.field + "_" + listOrdering.value);
					populatedListOrdering.push(listOrderingModel);
					
					$scope.listOrderings = populatedListOrdering;
				}
			}
		}
		
		function onChangeDatePicker(successfulCallback, id, listCondition) {
			var picker;
			if (id !== undefined) {
				picker = angular.element(document.getElementById(id));
				picker.bind('keyup change select', function() {
					successfulCallback(this.value);
				});
			} else {
				var elements = angular.element(document.getElementsByTagName("input"));
				for (var i = 0; i < elements.length; i++) {
					var element = elements[i];
					if (element.id.indexOf("display") > -1) {
						if (listCondition !== undefined) {
							element.id = listCondition.id;
						}
						picker = angular.element(element);
						picker.bind('keyup change select', function() {
							listCondition.value = formatDate(new Date(this.value));
						});
					}
				}
			}
		}
		
		function validateListConditions(listConditions, validatedListConditions) {
			// validate list condition
			//var count = 0;
			var failed = false;
			for (var i = 0; i < listConditions.length; i++) {
				var listCondition = listConditions[i];
				if (listCondition.selected) {
					if (listCondition.field === undefined) {
						var errorMessage =
							emr.message("patientlist.list.condition.error.invalidCondition") + " - " + listCondition.field();
						emr.errorAlert(errorMessage);
						listCondition.invalidEntry = true;
						failed = true;
						continue;
					}
					var requestListCondition = {
						field: listCondition.field,
						operator: listCondition.operator,
						value: listCondition.value
					};
					validatedListConditions.push(requestListCondition);
				} else if (listCondition.field !== "") {
					var errorMessage =
						emr.message("patientlist.list.condition.error.invalidCondition") + " - " + listCondition.field();
					emr.errorAlert(errorMessage);
					listCondition.invalidEntry = true;
					failed = true;
				}
			}
			
			if (validatedListConditions.length == 0 && !failed) {
				emr.errorAlert("patientlist.condition.chooseConditionErrorMessage");
			} else if (validatedListConditions.length > 0 && !failed) {
				return true;
			}
			
			return false;
		}
	}
})();
