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
(function () {
	'use strict';
	
	var base = angular.module('app.genericEntityController');
	base.controller("EntityController", EntityController);
	EntityController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory', 'PatientListModel',
		'PatientListConditionModel', 'PatientListFunctions', 'EntityFunctions', 'PatientListRestfulService', 'PatientListOrderingModel',
		'$sce'];
	
	var ENTITY_NAME = "list";
	
	function EntityController($stateParams, $injector, $scope, $filter, EntityRestFactory, PatientListModel,
	                          PatientListConditionModel, PatientListFunctions, EntityFunctions, PatientListRestfulService,
	                          PatientListOrderingModel, $sce) {
		var self = this;
		
		var entity_name_message_key = "patientlist.page";
		
		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function () {
				self.bindBaseParameters(PATIENT_LIST_MODULE_NAME, ENTITY_NAME, entity_name_message_key, PATIENT_LIST_RELATIVE_CANCEL_PAGE_URL);
				self.checkPrivileges(TASK_MANAGE_PATIENT_LIST_METADATA);
			};
		
		/**
		 * Initializes and binds any required variable and/or function specific to entity.page
		 * @type {Function}
		 */
		// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function (uuid) {
				/* bind variables.. */
				$scope.uuid = uuid;
				PatientListRestfulService.loadFields(self.onLoadFieldsSuccessful);
				$scope.listConditions = [];
				$scope.listOrderings = [];
				$scope.dropDownEntries = [];
				$scope.removeListCondition = self.removeListCondition;
				$scope.removeListOrdering = self.removeListOrdering;
				$scope.onListConditionDateSuccessfulCallback = self.onListConditionDateSuccessfulCallback;
				$scope.relativeDates =
					[
						{display: 'Yesterday', value: "YESTERDAY"},
						{display: 'This Week', value: "THIS_WEEK"},
						{display: 'Last Week', value: "LAST_WEEK"},
						{display: 'Last Two Weeks', value: "LAST_TWO_WEEKS"},
						{display: 'This Month', value: "THIS_MONTH"},
						{display: 'Last Month', value: "LAST_MONTH"},
						{display: 'Last 3 Months', value: "LAST_THREE_MONTHS"},
						{display: 'Last Half Year', value: "LAST_SIX_MONTHS"},
						{display: 'Last 9 Months', value: "LAST_NINE_MONTHS"},
						{display: 'This Year', value: "THIS_YEAR"},
						{display: 'Last Year', value: "LAST_YEAR"}
					];
				// auto-complete search concept function
				$scope.searchConcepts = function (search) {
					return PatientListRestfulService.searchConcepts(PATIENT_LIST_MODULE_NAME, search);
				};
				
				if ($scope.entity != undefined) {
					self.addExistingListConditions();
					if ($scope.entity.ordering.length > 0) {
						self.addExistingListOrdering();
					} else {
						self.addListOrdering();
					}
					self.livePreview($scope.entity.headerTemplate, $scope.entity.bodyTemplate);
					self.renderTemplate($scope.entity.headerTemplate);
					self.renderTemplate($scope.entity.bodyTemplate);
				} else {
					self.addListCondition();
					self.addListOrdering();
				}
				
				if (uuid == undefined) {
					PatientListRestfulService.preLoadDefaultDisplayTemplate(self.onPreLoadDefaultDisplayTemplateSuccessful);
				}
				
				$scope.patientListSortOrder = function (listOrdering) {
					if (listOrdering.field != null) {
						listOrdering.id = listOrdering.field + "_" + listOrdering.sortOrder;
						listOrdering.selected = true;
						self.getNewPatientListSortOrder(listOrdering);
						self.addListOrdering();
					}
				};
				
				$scope.patientListCondition = function (listCondition) {
					if (listCondition.field == "p.hasActiveVisit" || listCondition.field == "v.hasDiagnosis") {
						listCondition.id = listCondition.field + "_" + listCondition.value;
						listCondition.selected = true;
						self.getNewPatientListCondition(listCondition);
						self.addListCondition();
					} else if ((listCondition.inputType == "numberInput" || listCondition.inputType == "textInput") && listCondition.operator == "BETWEEN") {
						if ((listCondition.betweenValues[0] != undefined && listCondition.betweenValues[1] != undefined) && (listCondition.betweenValues[0] != "" && listCondition.betweenValues[1] != "")) {
							listCondition.id = listCondition.field + "_" + listCondition.betweenValues[0] + "_" + listCondition.betweenValues[1];
							listCondition.selected = true;
							listCondition.value = listCondition.betweenValues[0] + "|" + listCondition.betweenValues[1];
							self.getNewPatientListCondition(listCondition);
							self.addListCondition();
						}
					} else {
						if (listCondition.field != "" && listCondition.operator != "" && listCondition.value != "") {
							listCondition.id = listCondition.field + "_" + listCondition.value;
							listCondition.selected = true;
							self.getNewPatientListCondition(listCondition);
							self.addListCondition();
						}
					}
					if (listCondition.dataType == "org.openmrs.Location") {
						self.selectLocation(listCondition);
					}
				};
				
				$scope.patientListConditionOperator = function (listCondition) {
					if (listCondition.field != "" && listCondition.operator != "") {
						if (listCondition.operator != "BETWEEN" && listCondition.value != null && listCondition.value.indexOf("|") != -1) {
							listCondition.value = null;
						} else {
							//Adding the functionality for the between dates saving.
							if (listCondition.inputType == "dateInput") {
								if (listCondition.operator == "BETWEEN") {
									if ((listCondition.betweenValues[0] != undefined || listCondition.betweenValues[0] != "") && (listCondition.betweenValues[1] != undefined || listCondition.betweenValues[1] != "")) {
										listCondition.id = listCondition.field + "_" + listCondition.betweenValues[0] + "_" + listCondition.betweenValues[1];
										listCondition.selected = true;
										listCondition.value = PatientListFunctions.formatDate(listCondition.betweenValues[0]) + "|" + PatientListFunctions.formatDate(listCondition.betweenValues[1]);
										self.getNewPatientListCondition(listCondition);
										self.addListCondition();
									}
								} else {
									listCondition.id = listCondition.field + "_" + listCondition.value;
									listCondition.selected = true;
									self.getNewPatientListCondition(listCondition);
									self.addListCondition();
								}
							}
						}
					}
					if (listCondition.operator == "RELATIVE"){
						listCondition.inputType = "dropDownInput";
						$scope.dropDownEntries = $scope.relativeDates;
					}
				};
				
				$scope.inputsValueChange = function (listCondition) {
					if (listCondition.value != null) {
						$scope.resetValue(listCondition);
					}
					for (var i = 0; i < $scope.fields.length; i++) {
						if ($scope.fields[i].field == listCondition.field) {
							$scope.valueInputConditions($scope.fields[i].desc, listCondition);
						}
					}
					if (listCondition.field == "p.hasActiveVisit" || listCondition.field == "v.hasDiagnosis") {
						$scope.patientListCondition(listCondition);
					}
				};
				
				$scope.valueInputConditions = function (fieldDescription, listCondition) {
					if (fieldDescription.dataType == "java.lang.String") {
						if (listCondition.field == "v.diagnosis") {
							listCondition.inputType = "conceptInput";
						} else if (listCondition.field == "v.visitType") {
							listCondition.inputType = "dropDownInput";
							PatientListRestfulService.loadVisitTypes(PATIENT_LIST_MODULE_NAME, self.onLoadVisitTypesSuccessful)
						} else {
							listCondition.inputType = "textInput";
						}
						
					} else if (fieldDescription.dataType == "java.util.Date"
						|| fieldDescription.dataType == "org.openmrs.customdatatype.datatype.DateDatatype") {
						if (listCondition.operator == "RELATIVE") {
							listCondition.inputType = "dropDownInput";
							$scope.dropDownEntries = $scope.relativeDates;
						} else {
							listCondition.inputType = "dateInput";
							PatientListFunctions.onChangeDatePicker(self.onListConditionDateSuccessfulCallback, undefined, listCondition);
						}
						
					} else if (fieldDescription.dataType == "java.lang.Boolean"
						|| fieldDescription.dataType == "org.openmrs.customdatatype.datatype.BooleanDatatype") {
						listCondition.inputType = "checkBoxInput"
						
					} else if (listCondition.field == "p.gender") {
						listCondition.inputType = "dropDownInput";
						$scope.dropDownEntries = [{display: 'Female', value: "F"}, {display: 'Male', value: "M"}];
						
					} else if (fieldDescription.dataType == "org.openmrs.Location") {
						listCondition.inputType = "dropDownInput";
						PatientListRestfulService.loadLocations(PATIENT_LIST_MODULE_NAME, self.onLoadLocationsSuccessful);
						listCondition.dataType = "org.openmrs.Location";
						
					} else if (fieldDescription.dataType == "org.openmrs.Concept") {
						listCondition.inputType = "conceptInput";
						
					} else if (fieldDescription.dataType == "java.lang.Integer") {
						listCondition.inputType = "numberInput";
						
					} else if (fieldDescription.dataType == "org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype"
						|| fieldDescription.attributeTypeConfig != null){
						PatientListRestfulService.loadConceptAnswers(fieldDescription.attributeTypeConfig,
							PATIENT_LIST_MODULE_NAME, self.onLoadConceptAnswersSuccessful);
						listCondition.inputType = "dropDownInput";
					} else {
						listCondition.inputType = "textInput";
					}
				};
				
				$scope.resetValue = function (listCondition) {
					listCondition.value = ""
				};
				
				
				$scope.livePreview = self.livePreview;
				$scope.renderTemplate = self.renderTemplate;
				
				$scope.selectConcept = self.selectConcept;
				$scope.getConceptName = self.getConceptName;
				$scope.getLocationUuid = self.getLocationUuid;
				$scope.addListCondition = self.addListCondition;
				$scope.loadPreview = false;
			};
		
		self.getNewPatientListSortOrder = self.getNewPatientListSortOrder || function (newPatientListSortOrder) {
				var index = EntityFunctions.findIndexByKeyValue($scope.listOrderings, newPatientListSortOrder.id);
				if (index < 0) {
					$scope.listOrderings.push(newPatientListSortOrder);
				} else {
					$scope.listOrderings[index] = newPatientListSortOrder;
				}
			};
		
		self.getNewPatientListCondition = self.getNewPatientListCondition || function (newPatientListCondition) {
				var index = EntityFunctions.findIndexByKeyValue($scope.listConditions, newPatientListCondition.id);
				if (index < 0) {
					$scope.listConditions.push(newPatientListCondition);
				} else {
					$scope.listConditions[index] = newPatientListCondition;
				}
			};
		
		self.addExistingListConditions = self.addExistingListConditions || function () {
				PatientListFunctions.populateExistingPatientListCondition($scope.entity.patientListConditions, $scope.listConditions, $scope);
				self.addListCondition();
			};
		
		self.addExistingListOrdering = self.addExistingListOrdering || function () {
				PatientListFunctions.populateExistingPatientListOrdering($scope.entity.ordering, $scope.listOrderings, $scope);
				self.addListOrdering();
			};
		
		self.addListCondition = self.addListCondition || function () {
				var addListCondition = true;
				for (var i = 0; i < $scope.listConditions.length; i++) {
					var listCondition = $scope.listConditions[i];
					if (!listCondition.selected) {
						addListCondition = false;
						break;
					}
				}
				if (addListCondition) {
					var listCondition = new PatientListConditionModel('', '', '', 'textInput');
					$scope.listConditions.push(listCondition);
				}
			};
		
		self.addListOrdering = self.addListOrdering || function () {
				var addListOrdering = true;
				for (var i = 0; i < $scope.listOrderings.length; i++) {
					var listOrdering = $scope.listOrderings[i];
					if (!listOrdering.selected) {
						addListOrdering = false;
						break;
					}
				}
				if (addListOrdering) {
					var listOrdering = new PatientListOrderingModel('', '');
					$scope.listOrderings.push(listOrdering);
				}
			};
		
		self.removeListCondition = self.removeListCondition || function (listCondition) {
				//only remove selected line items..
				if (listCondition.selected) {
					var index = $scope.listConditions.indexOf(listCondition);
					if (index !== -1) {
						$scope.listConditions.splice(index, 1);
					}
					
					if ($scope.listConditions.length == 0) {
						self.addListCondition();
					}
				}
			};
		
		self.removeListOrdering = self.removeListOrdering || function (listOrdering) {
				//only remove selected line items..
				if (listOrdering.selected) {
					var index = $scope.listOrderings.indexOf(listOrdering);
					if (index !== -1) {
						$scope.listOrderings.splice(index, 1);
					}
					
					if ($scope.listOrderings.length == 0) {
						self.addListOrdering();
					}
				}
			};
		
		self.livePreview = self.livePreview || function (headerTemplate, bodyTemplate) {
				$scope.loadPreview = true;
				PatientListRestfulService.livePreview(headerTemplate,
					bodyTemplate, self.onLivePreviewSuccessful);
			}
		
		self.renderTemplate = self.renderTemplate || function (template) {
				return $sce.trustAsHtml(template);
			}
		
		self.onLoadFieldsSuccessful = self.onLoadFieldsSuccessful || function (data) {
				$scope.fields = data.results;
				$scope.fields = $filter('orderBy')($scope.fields, 'field');
			};
		
		self.onPreLoadDefaultDisplayTemplateSuccessful = self.onPreLoadDefaultDisplayTemplateSuccessful || function (data) {
				$scope.entity.headerTemplate = data.headerTemplate;
				$scope.entity.bodyTemplate = data.bodyTemplate;
				
				self.livePreview(data.headerTemplate, data.bodyTemplate);
				self.renderTemplate(data.headerTemplate);
				self.renderTemplate(data.bodyTemplate);
			};
		
		self.onLoadLocationsSuccessful = self.onLoadLocationsSuccessful || function (data) {
				$scope.locations = data.results;
				for (var i = 0; i < $scope.locations.length; i++) {
					$scope.locations[i].value = $scope.locations[i].uuid;
				}
				$scope.dropDownEntries = $scope.locations;
			};

		self.onLoadConceptAnswersSuccessful = self.onLoadConceptAnswersSuccessful || function (data) {
				var conceptAnswers = data.results;
				for (var i = 0; i < conceptAnswers.length; i++) {
					conceptAnswers[i].value = conceptAnswers[i].uuid;
				}
				$scope.dropDownEntries = conceptAnswers;
			}
		
		self.onLoadVisitTypesSuccessful = self.onLoadVisitTypesSuccessful || function (data) {
				$scope.visitTypes = data.results;
				for (var i = 0; i < $scope.visitTypes.length; i++) {
					$scope.visitTypes[i].value = $scope.visitTypes[i].display;
				}
				$scope.dropDownEntries = $scope.visitTypes;
			};
		
		/**
		 * Binds the selected concept item to entity
		 * @type {Function}
		 * @parameter concept
		 */
		self.selectConcept = self.selectConcept || function (concept, listCondition) {
				PatientListRestfulService.getConceptId(concept.uuid, function (data) {
					listCondition.value = concept.display;
					listCondition.valueRef = data["id"];
				});
			};
		
		self.getConceptName = self.getConceptName || function (id, onGetConceptNameSuccessfulCallback) {
				if (angular.isNumber(parseInt(id))) {
				PatientListRestfulService.getConceptName(id, onGetConceptNameSuccessfulCallback);
				}
			};
		
		self.selectLocation = self.selectLocation || function (listCondition) {
				PatientListRestfulService.getLocationId(listCondition.value, function (data) {
					listCondition.valueRef = data["id"];
				});
			};
		
		self.getLocationUuid = self.getLocationUuid || function (id, onGetLocationUuidSuccessfulCallback) {
				PatientListRestfulService.getLocationUuid(id, onGetLocationUuidSuccessfulCallback);
			};
		
		self.onLivePreviewSuccessful = self.onLivePreviewSuccessful || function (data) {
				$scope.headerContent = data['headerContent'];
				$scope.bodyContent = data['bodyContent'];
				$scope.loadPreview = false;
			};
		
		self.onListConditionDateSuccessfulCallback = self.onListConditionDateSuccessfulCallback || function (date) {
				if (date !== undefined) {
					var listConditionValueDate = PatientListFunctions.formatDate(date);
				}
			};
		
		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
		// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function () {
				if (!angular.isDefined($scope.entity.name) || $scope.entity.name === '') {
					$scope.submitted = true;
					emr.errorAlert("patientlist.name.error");
					return false;
				}
				
				if ($scope.entity.headerTemplate === "" || !angular.isDefined($scope.entity.headerTemplate) || $scope.entity.headerTemplate == null) {
					$scope.entity.headerTemplate = null;
				}
				if ($scope.entity.bodyTemplate === "" || !angular.isDefined($scope.entity.bodyTemplate) || $scope.entity.bodyTemplate == null) {
					$scope.entity.bodyTemplate = null;
				}
				
				if ($scope.listConditions.length == 1) {
					emr.errorAlert("patientlist.list.condition.error");
					return false;
				} else {
					var patientListConditions = [];
					for (var r = 0; r < $scope.listConditions.length; r++) {
						var patientListCondition = $scope.listConditions[r];
						if (patientListCondition.selected == false) {
							continue;
						} else {
							var requestCondition = {};
							
							if (patientListCondition.field != "p.hasActiveVisit" && patientListCondition.field != "v.hasDiagnosis") {
								if (patientListCondition.field === "") {
									emr.errorAlert("patientlist.list.condition.field.error");
									return false;
								}
								
								if (patientListCondition.operator != "NULL" || patientListCondition.operator != "NOT_NULL"
									|| patientListCondition.operator != "DEFINED") {
									if (patientListCondition.operator === "") {
										emr.errorAlert("patientlist.list.condition.operator.error");
										return false;
									}
								} else {
									patientListCondition.value = null;
								}
								
							} else {
								patientListCondition.value = null;
								patientListCondition.operator = null;
							}
							
							requestCondition['field'] = patientListCondition.field;
							requestCondition['conditionOrder'] = r;
							requestCondition['operator'] = patientListCondition.operator;
							requestCondition['value'] = patientListCondition.value;
							if (patientListCondition.valueRef !== undefined) {
								requestCondition['value'] = patientListCondition.valueRef;
							}
							
							patientListConditions.push(requestCondition);
						}
					}
					
					var ordering = [];
					for (var i = 0; i < $scope.listOrderings.length; i++) {
						var sortOrder = {};
						if ($scope.listOrderings[i].selected == false) {
							continue;
						}
						
						if ($scope.listOrderings[i].field === "") {
							emr.errorAlert("patientlist.list.sort.order.field.error");
							return false;
						}
						
						if ($scope.listOrderings[i].sortOrder === "") {
							$scope.listOrderings[i].sortOrder = "asc";
						}
						
						sortOrder.conditionOrder = i;
						sortOrder.field = $scope.listOrderings[i].field;
						sortOrder.sortOrder = $scope.listOrderings[i].sortOrder;
						ordering.push(sortOrder);
					}
					
					$scope.entity.ordering = ordering;
					$scope.entity.patientListConditions = patientListConditions;
					$scope.loading = true;
				}
				
				return true;
			};
		
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			GenericMetadataModel: PatientListModel
		});
	}
})();
