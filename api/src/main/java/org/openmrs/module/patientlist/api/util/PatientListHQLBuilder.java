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
import org.openmrs.module.patientlist.api.model.IBasePatientList;
import org.openmrs.module.patientlist.api.model.PatientList;

import java.util.List;

/**
 * Construct Base Patient List HQL
 */
public class PatientListHQLBuilder {

	public static String constructBaseHQL(PatientList patientList) {
		StringBuilder hql = new StringBuilder();

		// set base entity
		if (searchFieldType(patientList.getPatientListConditions(), Visit.class, false)
		        || searchFieldType(patientList.getOrdering(), Visit.class, false)) {
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

			if (mappingField) {
				if (patientInformationField.getMappingField().getType().getClass().isInstance(type)) {
					return true;
				}
			} else {
				if (patientInformationField.getEntityType().getClass().isInstance(type)) {
					return true;
				}
			}
		}
		return false;
	}
}
