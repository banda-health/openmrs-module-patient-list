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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Visit;

/**
 * Model class that represents patient list data.
 */
public class PatientListContextModel extends BaseOpenmrsData {

	private Patient patient;
	private Visit visit;
	private PatientList patientList;
	private String headerContent;
	private String bodyContent;

	public PatientListContextModel() {}

	public PatientListContextModel(Patient patient, Visit visit, PatientList patientList) {
		this.patient = patient;
		this.visit = visit;
		this.patientList = patientList;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public PatientList getPatientList() {
		return patientList;
	}

	public void setPatientList(PatientList patientList) {
		this.patientList = patientList;
	}

	public String getHeaderContent() {
		return headerContent;
	}

	public void setHeaderContent(String headerContent) {
		this.headerContent = headerContent;
	}

	public String getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}
}
