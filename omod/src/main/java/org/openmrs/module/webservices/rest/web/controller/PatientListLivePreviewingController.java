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
 */
package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.patientlist.api.IPatientListContextDataService;
import org.openmrs.module.patientlist.api.model.PatientListContextModel;
import org.openmrs.module.patientlist.api.util.DummyPatient;
import org.openmrs.module.patientlist.api.util.DummyVisit;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST controller for live previewing patient list templates
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LIST_LIVE_PREVIEWING_RESOURCE)
public class PatientListLivePreviewingController {

	@Autowired
	private IPatientListContextDataService patientListDataService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(
	        @RequestParam String headerTemplate,
	        @RequestParam String bodyTemplate) {
		SimpleObject results = new SimpleObject();

		DummyPatient patient = DummyPatient.getInstance();
		PatientListContextModel patientList = new PatientListContextModel();
		patientList.setPatient(patient);

		DummyVisit visit = DummyVisit.getInstance();
		visit.setPatient(patient);
		patientList.setVisit(visit);

		// header template
		String headerContent = patientListDataService.applyTemplate(headerTemplate, patientList);
		results.put("headerContent", headerContent);

		// body template
		String bodyContent = patientListDataService.applyTemplate(bodyTemplate, patientList);
		results.put("bodyContent", bodyContent);

		return results;
	}
}
