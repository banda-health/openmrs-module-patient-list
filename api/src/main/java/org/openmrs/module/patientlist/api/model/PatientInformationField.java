package org.openmrs.module.patientlist.api.model;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;

/**
 * Model class that represents an patient information field.
 * @param <T> The source object class.
 */
public class PatientInformationField<T extends OpenmrsData> {
	private String prefix;
	private String name;
	private String mappingFieldName;
	private Class<?> dataType;
	private Func1<T, Object> getValueFunc;

	public PatientInformationField(String prefix, String name, Class<?> dataType,
	    Func1<T, Object> getValueFunc, String mappingFieldName) {
		this.prefix = prefix;
		this.name = name;
		this.dataType = dataType;
		this.getValueFunc = getValueFunc;
		this.mappingFieldName = mappingFieldName;
	}

	public String getPrefix() {
		return prefix;
	}

	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Class<?> getDataType() {
		return dataType;
	}

	protected void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	protected void setValueFunc(Func1<T, Object> func) {
		this.getValueFunc = func;
	}

	public Object getValue(T source) {
		return getValueFunc.apply(source);
	}

	public String getMappingFieldName() {
		return mappingFieldName;
	}

	public void setMappingFieldName(String mappingFieldName) {
		this.mappingFieldName = mappingFieldName;
	}
}
