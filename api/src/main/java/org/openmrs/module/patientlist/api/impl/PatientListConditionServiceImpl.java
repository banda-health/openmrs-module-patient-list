package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.IPatientListConditionService;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.security.BasicMetadataAuthorizationPrivileges;

/**
 * Data service implementation class for {@link PatientListCondition}'s.
 */
public class PatientListConditionServiceImpl extends BaseMetadataDataServiceImpl<PatientListCondition>
        implements IPatientListConditionService {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(PatientListCondition object) {
		return;
	}
}
