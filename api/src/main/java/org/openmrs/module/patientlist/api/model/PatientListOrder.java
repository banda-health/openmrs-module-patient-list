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
 * Model class that represents the ordering of patients in a list.
 */
public class PatientListOrder extends BaseSerializableOpenmrsMetadata implements IBasePatientList {

	private Integer patientListOrderId;
	private PatientList patientList;
	private String field;
	private String sortOrder;
	private Integer conditionOrder;

	@Override
	public Integer getId() {
		return this.patientListOrderId;
	}

	@Override
	public void setId(Integer id) {
		this.patientListOrderId = id;
	}

	@Override
	public PatientList getPatientList() {
		return patientList;
	}

	@Override
	public void setPatientList(PatientList patientList) {
		this.patientList = patientList;
	}

	@Override
	public String getField() {
		return field;
	}

	@Override
	public void setField(String field) {
		this.field = field;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getConditionOrder() {
		return conditionOrder;
	}

	public void setConditionOrder(Integer conditionOrder) {
		this.conditionOrder = conditionOrder;
	}
}
