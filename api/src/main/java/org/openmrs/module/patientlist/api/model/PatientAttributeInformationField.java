package org.openmrs.module.patientlist.api.model;

import org.openmrs.Patient;

import java.util.List;

public class PatientAttributeInformationField extends BaseAttributeInformationField<Patient> {
	protected PatientAttributeInformationField(String prefix) {
		super(prefix);
	}

	@Override
	protected List<InformationField<Patient>> loadAttributes() {
		return null;
	}
}
