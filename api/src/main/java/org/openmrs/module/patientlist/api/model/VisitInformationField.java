package org.openmrs.module.patientlist.api.model;

import org.openmrs.Visit;
import org.openmrs.module.openhmis.commons.api.f.Func1;

public class VisitInformationField extends BaseInformationField<Visit> {
	public VisitInformationField(String prefix, String name, Object dataType, Func1<Visit, Object> getValueFunc) {
		super(prefix, name, dataType, getValueFunc);
	}
}
