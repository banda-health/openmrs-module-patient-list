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

	var base = angular.module('app.genericEntityController');
	base.controller("PatientListController", PatientListController);
	PatientListController.$inject = ['$injector', '$scope', '$filter',
		'EntityRestFactory', 'PatientListRestfulService', 'PatientListModel',
		'$window', 'PaginationService', 'CookiesService', 'CommonsRestfulFunctions',
		'EntityFunctions', '$timeout', '$sce'];

	function PatientListController($injector, $scope, $filter, EntityRestFactory,
	                               PatientListRestfulService, PatientListModel, $window,
	                               PaginationService, CookiesService, CommonsRestfulFunctions,
	                               EntityFunctions, $timeout, $sce) {
		var self = this;
		var entity_name_message_key = emr.message("patientlist.page");
		var REST_ENTITY_NAME = "list";

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
				self.bindBaseParameters(PATIENT_LIST_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, '');
				self.checkPrivileges(TASK_VIEW_PATIENT_LIST);
			}
		/**
		 * Initializes and binds any required variable and/or function specific to entity.page
		 * @type {Function}
		 */
		// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function() {
				$scope.limit = 25;

				$scope.pagingFrom = PaginationService.pagingFrom;
				$scope.pagingTo = PaginationService.pagingTo;
				$scope.totalNumOfResults = 0;
				$scope.fetchedEntities = [];

				$scope.getPatientListData = self.getPatientListData;
				self.getPatientLists();
				$scope.loadFirstPatientList = true;
				$scope.patientList = $scope.patientList || {};
				$scope.patientList.currentPage = $scope.patientList.currentPage || 1;
				$scope.endVisitDialog = self.endVisitDialog;

				$scope.renderTemplate = self.renderTemplate;
			}

		self.renderTemplate = self.renderTemplate || function(template) {
			return $sce.trustAsHtml(template);
			}

		self.getPatientLists = self.getPatientLists || function() {
				PatientListRestfulService.getPatientList(self.onLoadPatientListsSuccessful);
			}

		self.getPatientListData = self.getPatientListData || function(patientList, currentPage, limit, selectElement) {
				if(selectElement === true) {
					self.clearSelectedPatientLists();
					$scope.patientList = patientList;
					patientList.selected = true;
				}

				patientList.showSpinner = true;
				$scope.fetchedEntities = [];
				$scope.totalNumOfResults = 0;
				$scope.loaded = false;
				$scope.patientList.currentPage = currentPage;
				PatientListRestfulService.getPatientListData(patientList.uuid, currentPage, limit,
					self.onLoadPatientListDataSuccessful);
			}

		self.clearSelectedPatientLists = self.clearSelectedPatientLists || function() {
				for(var i = 0; i < $scope.patientLists.length; i++) {
					$scope.patientLists[i].selected = false;
				}
			}

		self.endVisitDialog = self.endVisitDialog || function(uuid) {
				var dialog = emr.setupConfirmationDialog({
					selector: '#end-visit-dialog',
					actions: {
						confirm: function() {
							CommonsRestfulFunctions.endVisit(PATIENT_LIST_MODULE_NAME, uuid, $scope);

							//refresh patient list data to reflect changes
							$timeout(function() {
								self.getPatientListData($scope.patientList, $scope.patientList.currentPage, $scope.limit);
							}, 300);

							dialog.close();
						},
						cancel: function() {
							dialog.close();
						}
					}
				});

				dialog.show();
				EntityFunctions.disableBackground();
			}


		// callbacks
		self.onLoadPatientListsSuccessful = self.onLoadPatientListsSuccessful || function(data) {
				$scope.patientLists = data.results;
				if($scope.loadFirstPatientList === true && $scope.patientLists.length > 0) {
					//var patientList = data.results[0];
					//self.getPatientListData(patientList, patientList.currentPage, $scope.limit, true);
					$scope.loadPatientListData = false;
				}

				$scope.loadFirstPatientList = false;
			}

		self.onLoadPatientListDataSuccessful = self.onLoadPatientListDataSuccessful || function(data) {
				$scope.fetchedEntities = data.results;
				$scope.loaded=true;
				$scope.totalNumOfResults = data.length;
				$scope.patientList.showSpinner = false;
				$scope.loadPatientListData = true;
			}

		// @Override
		self.cancel = self.cancel || function() {
				$window.location = decodeURIComponent('/' + OPENMRS_CONTEXT_PATH);
			}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			EntityRestFactory: EntityRestFactory,
			PaginationService: PaginationService,
			GenericMetadataModel: PatientListModel,
			CookiesService: CookiesService
		});
	}
})();
