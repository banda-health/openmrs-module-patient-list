package org.openmrs.module.patientlist.api.model;

import org.openmrs.OpenmrsData;

/**
 * Created by andrew on 3/16/17.
 */
public class PatientListMappingField<T extends OpenmrsData> {

	private T type;
	private String mappingFieldName;

	public PatientListMappingField(T type, String mappingFieldName) {
		this.type = type;
		this.mappingFieldName = mappingFieldName;
	}

	public T getType() {
		return type;
	}

	public String getMappingFieldName() {
		return mappingFieldName;
	}
}
