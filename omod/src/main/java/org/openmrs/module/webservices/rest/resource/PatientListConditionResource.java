package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link PatientListCondition}
 */
@Resource(name = ModuleRestConstants.PATIENT_LIST_CONDITION_RESOURCE, supportedClass = PatientListCondition.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PatientListConditionResource extends BaseRestMetadataResource<PatientListCondition> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description.removeProperty("name");
			description.removeProperty("description");
			description.addProperty("field");
			description.addProperty("value");
			description.addProperty("conditionOrder");
			description.addProperty("operator");
		}

		return description;
	}

	@Override
	public PatientListCondition newDelegate() {
		return new PatientListCondition();
	}

	@Override
	public Class<? extends IMetadataDataService<PatientListCondition>> getServiceClass() {
		return null;
	}

}
