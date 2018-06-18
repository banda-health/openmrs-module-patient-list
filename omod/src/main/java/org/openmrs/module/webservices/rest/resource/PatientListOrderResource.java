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

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link PatientListOrder}
 */
@Resource(name = ModuleRestConstants.PATIENT_LIST_ORDER_RESOURCE, supportedClass = PatientListOrder.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class PatientListOrderResource extends BaseRestMetadataResource<PatientListOrder> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description.removeProperty("name");
			description.removeProperty("description");
			description.addProperty("field");
			description.addProperty("sortOrder");
			description.addProperty("conditionOrder");
		}

		return description;
	}

	@Override
	public PatientListOrder newDelegate() {
		return new PatientListOrder();
	}

	@Override
	public Class<? extends IMetadataDataService<PatientListOrder>> getServiceClass() {
		return null;
	}

}
