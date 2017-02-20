package org.openmrs.module.patientlist.api;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface that represents classes which perform data operations for {@link PatientListData}s.
 */
@Transactional
public interface IPatientListDataService extends IObjectDataService<PatientListData> {

	@Transactional(readOnly = true)
	List<PatientListData> getPatientListData(PatientList patientList, PagingInfo pagingInfo);

	String applyTemplate(String template, PatientListData patientListData);
}
