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
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientListMappingField;
import org.openmrs.module.patientlist.api.model.PatientListOperatorConstants;
import org.openmrs.module.patientlist.api.util.PatientListDateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Define {@link AgePatientInformationField} operators
 * @param <T>
 */
public class AgePatientInformationField<T extends OpenmrsData>
        extends AbstractPatientListField<T, Object> {

	public AgePatientInformationField(
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
	public String equalOperator(Object searchValue) {
		return getDefaultOperatorImplementation(createDate((Integer)searchValue),
		    PatientListOperatorConstants.EQUALS);
	}

	@Override
	public String between(Object value1, Object value2) {
		return super.between(value1, value2);
	}

	@Override
	public String greaterThan(Object searchValue) {
		return getDefaultOperatorImplementation(createDate(searchValue),
		    PatientListOperatorConstants.LESS_THAN);
	}

	@Override
	public String lesserThan(Object searchValue) {
		return getDefaultOperatorImplementation(createDate(searchValue),
		    PatientListOperatorConstants.GREATER_THAN);
	}

	@Override
	public String greaterThanOrEquals(Object searchValue) {
		return getDefaultOperatorImplementation(createDate(searchValue),
		    PatientListOperatorConstants.LESS_THAN_OR_EQUALS);
	}

	@Override
	public String lesserThanOrEquals(Object searchValue) {
		return getDefaultOperatorImplementation(createDate(searchValue),
		    PatientListOperatorConstants.GREATER_THAN_OR_EQUALS);
	}

	private Date createDate(Object search) {
		int age = 0;
		if (search instanceof String) {
			age = Integer.valueOf((String)search);
		} else if (search instanceof Integer) {
			age = (Integer)search;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -age);
		return calendar.getTime();
	}
}
