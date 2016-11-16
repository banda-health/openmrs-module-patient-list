package org.openmrs.module.patientlist.api.model;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;

import java.util.List;

public interface InformationField<T extends OpenmrsData> {
	String getPrefix();
	String getName();
	Object getDataType();
	Object getValue(T source, Func1<T, Object> func);
	boolean hasChildFields();
	List<InformationField<T>> getChildFields();
}

