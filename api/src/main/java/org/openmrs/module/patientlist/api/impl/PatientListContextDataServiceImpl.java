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
package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.hql.ast.QuerySyntaxException;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.patientlist.api.IPatientListContextDataService;
import org.openmrs.module.patientlist.api.model.PatientListContextModel;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.api.util.IPatientInformationField;
import org.openmrs.module.patientlist.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.util.ConvertPatientListOperators;
import org.openmrs.module.patientlist.api.util.PatientInformation;
import org.openmrs.module.patientlist.api.util.PatientListHQLBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Data service implementation class for {@link PatientListContextModel}'s.
 */
public class PatientListContextDataServiceImpl extends
        BaseObjectDataServiceImpl<PatientListContextModel, BasicObjectAuthorizationPrivileges>
        implements IPatientListContextDataService {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(PatientListContextModel object) {
		return;
	}

	@Override
	public List<PatientListContextModel> getPatientListData(PatientList patientList, PagingInfo pagingInfo) {
		List<PatientListContextModel> patientListDataSet = new ArrayList<PatientListContextModel>();
		try {
			List paramValues = new ArrayList();
			// Create query
			Query query = getRepository().createQuery(constructHqlQuery(patientList, paramValues));
			// set parameters with actual values
			if (paramValues.size() > 0) {
				int index = 0;
				for (Object value : paramValues) {
					query.setParameter(index++, value);
				}
			}

			// set paging params
			Integer count = query.list().size();
			pagingInfo.setTotalRecordCount(count.longValue());
			pagingInfo.setLoadRecordCount(false);

			query = this.createPagingQuery(pagingInfo, query);
			List results = query.list();
			count = results.size();
			if (count > 0) {
				for (Object result : results) {
					Patient patient;
					Visit visit = null;
					if (result instanceof Patient) {
						patient = (Patient)result;
					} else {
						visit = (Visit)result;
						patient = visit.getPatient();
					}

					PatientListContextModel patientListData = new PatientListContextModel(patient, visit, patientList);
					applyTemplates(patientListData);
					patientListDataSet.add(patientListData);
				}
			}
		} catch (QuerySyntaxException ex) {
			LOG.error(ex.getMessage());
		}

		return patientListDataSet;
	}

	/**
	 * Constructs a patient list with given conditions (and ordering)
	 * @param patientList
	 * @param paramValues
	 * @return
	 */
	private String constructHqlQuery(PatientList patientList, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();
		if (patientList != null && patientList.getPatientListConditions() != null) {
			hql.append(PatientListHQLBuilder.constructBaseHQL(patientList, paramValues));
		}

		//apply ordering if any
		//hql.append(applyPatientListOrdering(patientList.getOrdering()));

		return hql.toString();
	}

	/**
	 * TODO: Refactor this logic and use datatypes instead of string comparisons Order hql query by given fields
	 * @param ordering
	 * @return
	 */
	private String applyPatientListOrdering(List<PatientListOrder> ordering) {
		int count = 0;
		StringBuilder hql = new StringBuilder();
		boolean handleSpecialFields = false;
		for (PatientListOrder order : ordering) {
			if (order != null) {
				IPatientInformationField field = PatientInformation.getInstance().
				        getField(order.getField());

				if (field == null) {
					continue;
				}

				String mappingFieldName = PatientInformation.getInstance().
				        getField(order.getField()).getMappingField().getMappingFieldName();

				// attributes
				if (StringUtils.contains(order.getField(), "p.attr.")) {
					mappingFieldName = "attrType.name";
				} else if (StringUtils.contains(order.getField(), "v.attr.")) {
					mappingFieldName = "vattrType.name";
				} else if (StringUtils.contains(order.getField(), "p.age")) {
					mappingFieldName = "p.birthdate";
				}

				// aliases
				if (StringUtils.contains(mappingFieldName, "p.names.")) {
					if (StringUtils.contains(mappingFieldName, "p.names.fullName")) {
						mappingFieldName = "pnames.givenName " + order.getSortOrder()
						        + ", pnames.familyName " + order.getSortOrder();
						handleSpecialFields = true;

					} else {
						mappingFieldName = "pnames." + mappingFieldName.split("\\.")[2];
					}
				} else if (StringUtils.contains(mappingFieldName, "p.addresses.")) {
					mappingFieldName = "paddresses." + mappingFieldName.split("\\.")[2];
				} else if (StringUtils.contains(mappingFieldName, "p.identifiers.")) {
					mappingFieldName = "pidentifiers." + mappingFieldName.split("\\.")[2];
				}

				if (mappingFieldName == null) {
					LOG.error("Unknown mapping for field name: " + order.getField());
					continue;
				}

				hql.append(" ");
				if (count++ == 0) {
					hql.append("order by ");
				}

				hql.append(mappingFieldName);
				hql.append(" ");
				if (!handleSpecialFields) {
					// flip sort order for birthdate/age
					if (StringUtils.equalsIgnoreCase(mappingFieldName, "p.birthdate")) {
						String sortOrder = order.getSortOrder();
						if (StringUtils.equalsIgnoreCase(sortOrder, "asc")) {
							order.setSortOrder("desc");
						} else {
							order.setSortOrder("asc");
						}
					}

					hql.append(order.getSortOrder());
				}

				hql.append(",");
			}
		}

		//remove trailing coma.
		return StringUtils.removeEnd(hql.toString(), ",");
	}

	/**
	 * Apply header and body templates on patient list data
	 * @param patientListData
	 */
	private void applyTemplates(PatientListContextModel patientListData) {
		// apply header template.
		if (patientListData.getPatientList().getHeaderTemplate() != null) {
			patientListData.setHeaderContent(
			        applyTemplate(patientListData.getPatientList().getHeaderTemplate(), patientListData));
		}

		// apply body template
		if (patientListData.getPatientList().getBodyTemplate() != null) {
			patientListData.setBodyContent(
			        applyTemplate(patientListData.getPatientList().getBodyTemplate(), patientListData));
		}
	}

	@Override
	public String applyTemplate(String template, PatientListContextModel patientListData) {
		String[] fields = StringUtils.substringsBetween(template, "{", "}");
		if (fields != null) {
			for (String field : fields) {
				Object value = null;
				IPatientInformationField patientInformationField =
				        PatientInformation.getInstance().getField(field);
				if (patientInformationField != null) {
					if (patientListData.getPatient() != null && StringUtils.contains(field, "p.")) {
						value = patientInformationField.getValue(patientListData.getPatient());
					} else if (patientListData.getVisit() != null && StringUtils.contains(field, "v.")) {
						value = patientInformationField.getValue(patientListData.getVisit());
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
