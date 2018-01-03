package org.openmrs.module.patientlist.api.model;

import org.openmrs.OpenmrsData;

/**
 * PatientListMappingField implementation
 */
public class PatientListMappingField<T extends OpenmrsData> {
	private String mappingFieldName;
	private Class<T> type;

	public PatientListMappingField(String mappingFieldName, Class<T> type) {
		this.mappingFieldName = mappingFieldName;
		this.type = type;
	}

	public String getMappingFieldName() {
		return mappingFieldName;
	}

	public Class<T> getType() {
		return type;
	}
}
