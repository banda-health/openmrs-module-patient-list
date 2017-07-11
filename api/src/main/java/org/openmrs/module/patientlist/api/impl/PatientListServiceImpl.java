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
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.security.BasicMetadataAuthorizationPrivileges;

import java.util.List;

/**
 * Data service implementation class for {@link PatientList}'s.
 */
public class PatientListServiceImpl extends BaseMetadataDataServiceImpl<PatientList> implements
        IPatientListService {
	protected final Log LOG = LogFactory.getLog(this.getClass());

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(PatientList object) {
		return;
	}

	@Override
	protected Order[] getDefaultSort() {
		return new Order[] { Order.asc("id") };
	}
}
