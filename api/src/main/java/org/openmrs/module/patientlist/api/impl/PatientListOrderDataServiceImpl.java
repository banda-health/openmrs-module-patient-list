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
package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.IPatientListOrderDataService;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.api.security.BasicMetadataAuthorizationPrivileges;

/**
 * Data service implementation class for {@link PatientListOrder}'s.
 */
public class PatientListOrderDataServiceImpl extends BaseMetadataDataServiceImpl<PatientListOrder>
        implements IPatientListOrderDataService {

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
