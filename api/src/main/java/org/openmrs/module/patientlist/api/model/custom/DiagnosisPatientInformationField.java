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

import org.apache.commons.lang.math.NumberUtils;
import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientListMappingField;

import java.util.ArrayList;
import java.util.List;

/**
 * Define {@link DiagnosisPatientInformationField} operators
 * @param <T>
 */
public class DiagnosisPatientInformationField<T extends OpenmrsData>
        extends AbstractPatientListField<T, String> {

	public DiagnosisPatientInformationField(
	    String prefix, String name, Class<?> dataType, Class<T> entityType,
	    Func1<T, Object> valueFunc, String mappingField) {
		setPrefix(prefix);
		setName(name);
		setDataType(dataType);
		setValueFunc(valueFunc);
		setEntityType(entityType);
		setMappingField(new PatientListMappingField<T>(mappingField, entityType));
	}

	@Override
	public String equalOperator(String searchValue) {
		StringBuilder hql = new StringBuilder();

		// coded diagnosis
		if (NumberUtils.isDigits(searchValue)) {
			hql.append(" ob.valueCoded.conceptId = ? ");
			//getParameterValues().add(Integer.valueOf(searchValue));
		} else {
			// un-coded diagnosis
			hql.append(" ob.valueText = ? ");
			getParameterValues().add(searchValue);
		}

		return hql.toString();
	}
}
