package org.openmrs.module.patientlist.api;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.patientlist.api.model.PatientList;

public class IPatientListDataServiceTest extends IMetadataDataServiceTest<IPatientListService, PatientList> {
	private static final String PATIENT_LIST_DATASET = TestConstants.BASE_DATASET_DIR + "PatientListTest.xml";

	@Override
	public void before() throws Exception {
		super.before();
		executeDataSet(PATIENT_LIST_DATASET);
		executeDataSet(TestConstants.CORE_DATASET);

	}

	@Override
	public PatientList createEntity(boolean valid) {
		PatientList patientList = new PatientList();
		if (valid) {
			patientList.setName("Test new patient list");
		}

		patientList.setDescription("new patient list description");
		patientList.setRetired(false);
		return patientList;
	}

	@Override
	protected int getTestEntityCount() {
		return 1;
	}

	@Override
	protected void updateEntityFields(PatientList entity) {}

	@Override
	protected void assertEntity(PatientList expected, PatientList actual) {
		super.assertEntity(expected, actual);
	}
}
