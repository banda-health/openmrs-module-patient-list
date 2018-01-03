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
package org.openmrs.module.patientlist.api.util;

import org.openmrs.Visit;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.VisitAttribute;
import org.openmrs.PersonAttribute;
import org.openmrs.OpenmrsData;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Obs;
import org.openmrs.module.patientlist.api.model.IBasePatientList;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListCondition;

import java.util.List;

/**
 * Construct Base Patient List HQL
 */
public class PatientListHQLBuilder {

	public static String constructBaseHQL(PatientList patientList, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();

		// TODO: Need a better way of building the base query. Looping everytime in search of a field isn't optimal

		// set base entity
		if (searchFieldType(patientList.getPatientListConditions(), Visit.class, false)
		        || searchFieldType(patientList.getPatientListConditions(), Obs.class, false)
		        || searchFieldType(patientList.getPatientListConditions(), VisitAttribute.class, false)) {
			// set visit entity
			hql.append("select v from Visit v ");
			if (searchFieldType(patientList.getPatientListConditions(), Patient.class, false)
			        || searchFieldType(patientList.getOrdering(), Patient.class, false)) {
				// join patient entity
				hql.append("inner join v.patient as p ");
			}
		} else {
			// use only the patient entity (no need to join visit entity)
			hql.append("select p from Patient p ");
		}

		// add joins as required
		if (searchFieldType(patientList.getPatientListConditions(), VisitAttribute.class, true)
		        || searchFieldType(patientList.getOrdering(), VisitAttribute.class, true)) {
			hql.append("inner join v.attributes as vattr ");
			hql.append("inner join vattr.attributeType as vattrType ");
		} else if (searchFieldType(patientList.getPatientListConditions(), PersonAttribute.class, true)
		        || searchFieldType(patientList.getOrdering(), PersonAttribute.class, true)) {
			hql.append("inner join p.attributes as attr ");
			hql.append("inner join attr.attributeType as attrType ");
		} else if (searchFieldType(patientList.getPatientListConditions(), PersonAddress.class, true)
		        || searchFieldType(patientList.getOrdering(), PersonAddress.class, true)) {
			hql.append("inner join p.addresses as paddresses ");
		} else if (searchFieldType(patientList.getPatientListConditions(), PersonName.class, true)
		        || searchFieldType(patientList.getOrdering(), PersonName.class, true)) {
			hql.append("inner join p.names as pnames ");
		} else if (searchFieldType(patientList.getPatientListConditions(), PatientIdentifier.class, true)
		        || searchFieldType(patientList.getOrdering(), PatientIdentifier.class, true)) {
			hql.append("inner join p.identifiers as pidentifiers ");
		} else if (searchFieldType(patientList.getPatientListConditions(), Obs.class, false)) {
			hql.append("inner join v.encounters as encounter ");
			hql.append("inner join encounter.obs as ob ");
		}

		hql.append(" where ");

		// apply patient list conditions
		hql.append("(");
		hql.append(applyPatientListConditions(patientList.getPatientListConditions(), paramValues));
		hql.append(")");

		return hql.toString();
	}

	/**
	 * Parse patient list conditions and create sub queries to be added on the main HQL query.
	 * @param patientListConditions
	 * @param paramValues
	 * @return
	 */
	private static String applyPatientListConditions(List<PatientListCondition> patientListConditions,
	        List paramValues) {
		int count = 0;
		int noOfConditions = patientListConditions.size();
		StringBuilder hql = new StringBuilder();
		// apply conditions
		for (PatientListCondition condition : patientListConditions) {
			if (condition == null || PatientInformation.getInstance().getField(condition.getField()) == null) {
				throw new RuntimeException("condition cannot be null");
			}

			String join = " AND ";

			IPatientInformationField patientInformationField =
			        PatientInformation.getInstance().getField(condition.getField());
			patientInformationField.refresh();

			if (condition.getOperator() != null) {
				switch (condition.getOperator()) {
					case EQUALS:
						hql.append(patientInformationField.equalOperator(condition.getValue()));
						break;
					case NOT_EQUALS:
						hql.append(patientInformationField.notEquals(condition.getValue()));
						break;
					case GT:
						hql.append(patientInformationField.greaterThan(condition.getValue()));
						break;
					case GTE:
						hql.append(patientInformationField.greaterThanOrEquals(condition.getValue()));
						break;
					case LT:
						hql.append(patientInformationField.lesserThan(condition.getValue()));
						break;
					case LTE:
						hql.append(patientInformationField.lesserThanOrEquals(condition.getValue()));
						break;
					case LIKE:
						hql.append(patientInformationField.like(condition.getValue()));
						break;
					case BETWEEN:
						String[] betweenValues = condition.getValue().split("|");
						hql.append(patientInformationField.between(betweenValues[0], betweenValues[1]));
						break;
					case NULL:
						hql.append(patientInformationField.isNull());
						break;
					case NOT_NULL:
						hql.append(patientInformationField.notNull());
						break;
					default:
						break;
				}
			} else if (patientInformationField.getDataType() == Boolean.class) {
				//fields such as hasActiveVisits, hasDiagnosis don't have operators
				hql.append(patientInformationField.equalOperator(condition.getValue()));
			}

			paramValues.addAll(patientInformationField.getParameterValues());

			if (noOfConditions > ++count) {
				hql.append(join);
			}
		}

		return hql.toString();
	}

	/**
	 * Lookup a given field type in list
	 * @param list
	 * @param type
	 * @return boolean
	 */
	private static <T extends IBasePatientList, S extends OpenmrsData> boolean searchFieldType(
	        List<T> list, Class<S> type, boolean mappingField) {
		for (T t : list) {
			if (t == null) {
				continue;
			}

			String field = t.getField();
			if (field == null) {
				continue;
			}

			IPatientInformationField patientInformationField =
			        PatientInformation.getInstance().getField(field);
			if (patientInformationField == null) {
				continue;
			}

			if (mappingField && patientInformationField.getMappingField() != null) {
				if (patientInformationField.getMappingField().getType() == type) {
					return true;
				}
			} else if (patientInformationField.getEntityType() == type) {
				return true;
			}
		}
		return false;
	}
}
