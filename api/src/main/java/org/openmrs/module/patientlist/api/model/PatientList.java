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

import java.util.ArrayList;
import java.util.List;

/**
 * Model class that represents a patient list definition.
 */
public class PatientList extends BaseSerializableOpenmrsMetadata {

	private Integer patientListId;
	private List<PatientListCondition> patientListConditions;
	private List<PatientListOrder> ordering;
	private String headerTemplate;
	private String bodyTemplate;

	@Override
	public Integer getId() {
		return this.patientListId;
	}

	@Override
	public void setId(Integer id) {
		this.patientListId = id;
	}

	public List<PatientListCondition> getPatientListConditions() {
		return patientListConditions;
	}

	public void setPatientListConditions(
	        List<PatientListCondition> patientListConditions) {
		this.patientListConditions = patientListConditions;
	}

	public List<PatientListOrder> getOrdering() {
		return ordering;
	}

	public void setOrdering(List<PatientListOrder> ordering) {
		this.ordering = ordering;
	}

	public void addCondition(PatientListCondition condition) {
		if (condition == null) {
			throw new NullPointerException("The selection rule to add must be defined.");
		}

		if (this.patientListConditions == null) {
			this.patientListConditions = new ArrayList<PatientListCondition>();
		}

		condition.setPatientList(this);

		this.patientListConditions.add(condition);
	}

	public void addSortOrder(PatientListOrder sortOrder) {
		if (sortOrder == null) {
			throw new NullPointerException("The sort order to add must be defined.");
		}

		if (this.ordering == null) {
			this.ordering = new ArrayList<PatientListOrder>();
		}

		sortOrder.setPatientList(this);

		this.ordering.add(sortOrder);
	}

	public String getHeaderTemplate() {
		return headerTemplate;
	}

	public void setHeaderTemplate(String headerTemplate) {
		this.headerTemplate = headerTemplate;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}
}
