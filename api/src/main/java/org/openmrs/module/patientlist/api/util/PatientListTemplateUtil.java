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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.api.model.PatientInformationField;
import org.openmrs.module.patientlist.api.model.PatientListData;
import org.openmrs.module.patientlist.api.model.PatientList;

/**
 * Implement {@link PatientList} template service methods
 */
public class PatientListTemplateUtil {
	private static Log LOG = LogFactory.getLog(PatientListTemplateUtil.class);

	public static String applyTemplate(String template, PatientListData patientListData) {
		String[] fields = StringUtils.substringsBetween(template, "{", "}");
		if (fields != null) {
			for (String field : fields) {
				Object value = null;
				PatientInformationField patientInformationField =
				        PatientInformation.getInstance().getField(field);
				if (patientInformationField != null) {
					if (patientListData.getPatient() != null && StringUtils.contains(field, "p.")) {
						value = patientInformationField.getValue(patientListData.getPatient());
					} else if (patientListData.getVisit() != null && StringUtils.contains(field, "v.")) {
						value = patientInformationField.getValue(patientListData.getVisit());
						if (value != null && StringUtils.contains(field, "v.attr.")) {
							try {
								ConceptAnswer conceptAnswer = Context.getConceptService()
								        .getConceptAnswerByUuid((String)value);
								if (conceptAnswer != null) {
									value = conceptAnswer.getAnswerConcept().getName().getName();
								}
							} catch (Exception ex) {
								LOG.error(ex);
							}
						}
					}
				}

				if (value != null) {
					template = StringUtils.replace(template, "{" + field + "}", value.toString());
				} else {
					template = StringUtils.replace(template, "{" + field + "}", "");
				}
			}
		}

		return template;
	}
}
