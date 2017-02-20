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
package org.openmrs.module.patientlist.api.model;

import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

/**
 * Model class that represents a list condition.
 */
public class PatientListCondition extends BaseSerializableOpenmrsMetadata {

	private Integer patientListConditionId;
	private PatientList patientList;
	private String field;
	private String value;
	private Integer conditionOrder;
	private PatientListOperator operator;

	@Override
	public Integer getId() {
		return this.patientListConditionId;
	}

	@Override
	public void setId(Integer id) {
		this.patientListConditionId = id;
	}

	public PatientList getPatientList() {
		return patientList;
	}

	public void setPatientList(PatientList patientList) {
		this.patientList = patientList;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getConditionOrder() {
		return conditionOrder;
	}

	public void setConditionOrder(Integer conditionOrder) {
		this.conditionOrder = conditionOrder;
	}

	public PatientListOperator getOperator() {
		return operator;
	}

	public void setOperator(PatientListOperator operator) {
		this.operator = operator;
	}
}
