package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.model.PatientListData;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link PatientListData}
 */
@Resource(name = ModuleRestConstants.PATIENT_LIST_DATA_RESOURCE, supportedClass = PatientListData.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
@Handler(supports = { PatientListData.class }, order = 0)
public class PatientListDataResource extends BaseRestObjectResource<PatientListData> {

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
	public PatientListData newDelegate() {
		return new PatientListData();
	}

	@Override
	public Class<? extends IObjectDataService<PatientListData>> getServiceClass() {
		return IPatientListDataService.class;
	}

}
