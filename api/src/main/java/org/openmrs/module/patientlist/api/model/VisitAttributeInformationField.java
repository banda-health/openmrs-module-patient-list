package org.openmrs.module.patientlist.api.model;

import org.openmrs.Visit;

import java.util.List;

public class VisitAttributeInformationField extends BaseAttributeInformationField<Visit> {
	protected VisitAttributeInformationField(String prefix) {
		super(prefix);
	}

	@Override
	protected List<InformationField<Visit>> loadAttributes() {
		return null;
	}
}
