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
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.patientlist.api.IPatientListContextModelDataService;
import org.openmrs.module.patientlist.api.model.PatientListContextModel;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link PatientListContextModel}
 */
@Resource(name = ModuleRestConstants.PATIENT_LIST_DATA_RESOURCE, supportedClass = PatientListContextModel.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
@Handler(supports = { PatientListContextModel.class }, order = 0)
public class PatientListContextModelResource extends BaseRestObjectResource<PatientListContextModel> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("patient", Representation.DEFAULT);
		description.addProperty("visit", Representation.DEFAULT);
		description.addProperty("headerContent");
		description.addProperty("bodyContent");

		return description;
	}

	@Override
	public PatientListContextModel newDelegate() {
		return new PatientListContextModel();
	}

	@Override
	public Class<? extends IObjectDataService<PatientListContextModel>> getServiceClass() {
		return IPatientListContextModelDataService.class;
	}

}
