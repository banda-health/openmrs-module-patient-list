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
	'use strict'
	
	angular.module('app.restfulServices').service(
		'PatientListRestfulService', PatientListRestfulService);
	
	PatientListRestfulService.$inject = ['EntityRestFactory'];
	
	function PatientListRestfulService(EntityRestFactory) {
		var service;
		service = {
			loadConceptAnswers : loadConceptAnswers,
			loadFields: loadFields,
			livePreview: livePreview,
			loadLocations: loadLocations,
			searchConcepts: searchConcepts,
			getConceptId: getConceptId,
			getConceptName: getConceptName,
			getLocationId: getLocationId,
			getLocationUuid: getLocationUuid,
			preLoadDefaultDisplayTemplate: preLoadDefaultDisplayTemplate,
			loadVisitTypes: loadVisitTypes
		};
		
		return service;
		
		/**
		 * @param onLoadConceptAnswersSuccessful
		 * @param module_name
		 * @param uuid
		 */
		function loadConceptAnswers(uuid, module_name, onLoadConceptAnswersSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'conceptanswer';
			requestParams['conceptUuid'] = uuid;
			EntityRestFactory.setBaseUrl('custom');
			EntityRestFactory.loadEntities(requestParams,
				onLoadConceptAnswersSuccessful, errorCallback);
			EntityRestFactory.setBaseUrl(module_name);
		}
		
		/**
		 * @param onLoadLocationsSuccessful
		 * @param module_name
		 */
		function loadLocations(module_name,onLoadLocationsSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			EntityRestFactory.setBaseUrl('location', 'v1');
			EntityRestFactory.loadEntities(requestParams,
				onLoadLocationsSuccessful, errorCallback);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}
		
		/**
		 * @param onLoadVisitTypesSuccessful
		 * @param module_name
		 */
		function loadVisitTypes(module_name,onLoadVisitTypesSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			EntityRestFactory.setBaseUrl('visittype', 'v1');
			EntityRestFactory.loadEntities(requestParams,
				onLoadVisitTypesSuccessful, errorCallback);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}
		
		/**
		 * An auto-complete function to search concepts given a query term.
		 * @param module_name
		 * @param q - search term
		 * @param limit
		 */
		function searchConcepts(module_name, q) {
			var requestParams = [];
			requestParams['q'] = q;
			requestParams['limit'] = 10;
			return EntityRestFactory.autocompleteSearch(requestParams, 'concept', module_name, 'v1');
		}
		
		/**
		 * @param onLoadFieldsSuccessful
		 */
		function loadFields(onLoadFieldsSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'fields';
			requestParams['template'] = false;
			EntityRestFactory.loadEntities(requestParams,
				onLoadFieldsSuccessful,
				errorCallback
			);
		}
		
		function preLoadDefaultDisplayTemplate(onPreLoadDefaultDisplayTemplateSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'fields';
			requestParams['template'] = true;
			EntityRestFactory.loadEntities(requestParams,
				onPreLoadDefaultDisplayTemplateSuccessful,
				errorCallback
			);
		}

		function livePreview(headerTemplate, bodyTemplate, onLivePreviewSuccessful){
			var requestParams = [];
			requestParams['rest_entity_name'] = 'live';
			requestParams['headerTemplate'] = headerTemplate;
			requestParams['bodyTemplate'] = bodyTemplate;
			EntityRestFactory.loadEntities(requestParams,
				onLivePreviewSuccessful,
				errorCallback
			);
		}

		function getConceptId(uuid, onLoadConceptId){
			var requestParams = [];
			requestParams['rest_entity_name'] = 'lookup';
			requestParams['uuid'] = uuid;
			requestParams['type'] = "concept";
			EntityRestFactory.loadEntities(requestParams,
				onLoadConceptId,
				errorCallback
			);
		}

		function getConceptName(id, onLoadConceptName){
			var requestParams = [];
			requestParams['rest_entity_name'] = 'lookup';
			requestParams['id'] = id;
			requestParams['type'] = "concept";
			EntityRestFactory.loadEntities(requestParams,
				onLoadConceptName,
				errorCallback
			);
		}

		function getLocationId(uuid, onLoadLocationId){
			var requestParams = [];
			requestParams['rest_entity_name'] = 'lookup';
			requestParams['uuid'] = uuid;
			requestParams['type'] = "location";
			EntityRestFactory.loadEntities(requestParams,
				onLoadLocationId,
				errorCallback
			);
		}

		function getLocationUuid(id, onLoadLocationUuid){
			var requestParams = [];
			requestParams['rest_entity_name'] = 'lookup';
			requestParams['id'] = id;
			requestParams['type'] = "location";
			EntityRestFactory.loadEntities(requestParams,
				onLoadLocationUuid,
				errorCallback
			);
		}
		
		function errorCallback(error) {
			emr.errorAlert(error);
		}
	}
	
})();
