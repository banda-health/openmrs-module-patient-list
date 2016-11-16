package org.openmrs.module.patientlist.api.model;

import org.openmrs.Patient;
import org.openmrs.module.openhmis.commons.api.f.Func1;

public class PatientInformationField extends BaseInformationField<Patient> {
	public PatientInformationField(String prefix, String name, Object dataType, Func1<Patient, Object> getValueFunc) {
		super(prefix, name, dataType, getValueFunc);
	}
}
