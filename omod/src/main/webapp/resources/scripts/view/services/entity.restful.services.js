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

	angular.module('app.restfulServices').service('PatientListRestfulService', PatientListRestfulService);

	PatientListRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];

	function PatientListRestfulService(EntityRestFactory, PaginationService) {
		var service;

		service = {
			getPatientList: getPatientList,
			getPatientListData: getPatientListData,
		};

		return service;

		function getPatientList(onLoadPatientListSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'list';
			EntityRestFactory.loadEntities(requestParams, onLoadPatientListSuccessful, errorCallback);
		}

		function getPatientListData(uuid, startIndex, limit, onLoadPatientListDataSuccessful){
			var requestParams = PaginationService.paginateParams(startIndex, limit, false, '')
			requestParams['rest_entity_name'] = 'data';
			requestParams['uuid'] = uuid;
			EntityRestFactory.loadEntities(requestParams, onLoadPatientListDataSuccessful, errorCallback);
		}

		function errorCallback(error) {
			emr.errorAlert(error);
		}
	}
})();
