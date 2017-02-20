package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.IPatientListOrderService;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.api.security.BasicMetadataAuthorizationPrivileges;

/**
 * Data service implementation class for {@link PatientListOrder}'s.
 */
public class PatientListOrderServiceImpl extends BaseMetadataDataServiceImpl<PatientListOrder>
        implements IPatientListOrderService {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(PatientListOrder object) {
		return;
	}
}
