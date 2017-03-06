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
package org.openmrs.module.patientlist.api;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListContextModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface that represents classes which perform data operations for {@link PatientListContextModel}s.
 */
@Transactional
public interface IPatientListContextModelDataService extends IObjectDataService<PatientListContextModel> {

	@Transactional(readOnly = true)
	List<PatientListContextModel> getPatientListData(PatientList patientList, PagingInfo pagingInfo);

	String applyTemplate(String template, PatientListContextModel patientListData);
}
