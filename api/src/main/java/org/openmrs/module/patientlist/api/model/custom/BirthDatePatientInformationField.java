/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.patientlist.api.model.custom;

import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientListMappingField;
import org.openmrs.module.patientlist.api.util.PatientListDateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Define {@link BirthDatePatientInformationField} operators
 */
public class BirthDatePatientInformationField<T extends OpenmrsData> extends AbstractPatientListField {

	public BirthDatePatientInformationField(
	    String prefix, String name, Class<?> dataType,
	    Func1<T, Object> valueFunc, PatientListMappingField mappingFieldName, T entityType) {
		setPrefix(prefix);
		setName(name);
		setDataType(dataType);
		setValueFunc(valueFunc);
		setMappingField(mappingFieldName);
		setEntityType(entityType);
	}

	@Override
	public List<Object> getParameterValues() {
		return new ArrayList<Object>();
	}

	@Override
	public String equalOperator(String searchDate) {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingField().getMappingFieldName());
		hql.append(" = ");
		getParameterValues().add(PatientListDateUtil.formatDate(searchDate));

		return hql.toString();
	}

	@Override
	public String greaterThan(String searchValue) {
		return null;
	}

	@Override
	public String lesserThan(String searchValue) {
		return null;
	}
}
