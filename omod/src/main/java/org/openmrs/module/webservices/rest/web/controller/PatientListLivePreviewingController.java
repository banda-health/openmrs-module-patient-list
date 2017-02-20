package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.model.PatientListData;
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
	private IPatientListDataService patientListDataService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(
	        @RequestParam String headerTemplate,
	        @RequestParam String bodyTemplate) {
		SimpleObject results = new SimpleObject();

		DummyPatient patient = DummyPatient.getInstance();
		PatientListData patientList = new PatientListData();
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
