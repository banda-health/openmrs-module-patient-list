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

import org.openmrs.module.patientlist.api.util.IPatientInformationField;
import org.openmrs.module.patientlist.api.util.PatientInformation;
import org.openmrs.module.patientlist.api.util.PatientListTemplate;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Patient List Fields
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LIST_FIELDS_RESOURCE)
public class PatientListFieldsResourceController {

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(@RequestParam(value = "template", required = false) Boolean template) {
		SimpleObject results = new SimpleObject();
		if (template) {
			results.put("headerTemplate", PatientListTemplate.getInstance().getDefaultHeaderTemplate());
			results.put("bodyTemplate", PatientListTemplate.getInstance().getDefaultBodyTemplate());
		} else {
			List<SimpleObject> fields = new ArrayList<SimpleObject>();
			Map<String, IPatientInformationField<?, ?>> patientInformationFields =
			        PatientInformation.getInstance().getFields();
			for (Map.Entry<String, IPatientInformationField<?, ?>> set : patientInformationFields.entrySet()) {
				SimpleObject field = new SimpleObject();
				field.put("field", set.getKey());
				field.put("desc", set.getValue());
				fields.add(field);
			}

			results.put("results", fields);
		}

		return results;
	}
}
