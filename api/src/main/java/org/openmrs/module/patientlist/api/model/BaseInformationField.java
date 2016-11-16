package org.openmrs.module.patientlist.api.model;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;

import java.util.List;

public abstract class BaseInformationField<T extends OpenmrsData> implements InformationField<T> {
	private String prefix;
	private String name;
	private Object dataType;
	private Func1<T, Object> getValueFunc;
	private boolean hasChildFields;
	private List<InformationField<T>> childFields;

	protected BaseInformationField(String prefix, String name, Object dataType, Func1<T, Object> getValueFunc) {
		this.prefix = prefix;
		this.name = name;
		this.dataType = dataType;
		this.getValueFunc = getValueFunc;
		this.hasChildFields = false;
		this.childFields = null;
	}

	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	protected void setDataType(Object dataType) {
		this.dataType = dataType;
	}

	@Override
	public Object getDataType() {
		return dataType;
	}

	protected void setValueFunc(Func1<T, Object> func) {
		this.getValueFunc = func;
	}

	@Override
	public Object getValue(T source, Func1<T, Object> func) {
		return getValueFunc.apply(source);
	}

	protected void setHasChildFields(boolean hasChildFields) {
		this.hasChildFields = hasChildFields;
	}

	@Override
	public boolean hasChildFields() {
		return false;
	}

	protected void setChildFields(List<InformationField<T>> childFields) {
		this.childFields = childFields;
	}

	@Override
	public List<InformationField<T>> getChildFields() {
		return childFields;
	}
}


