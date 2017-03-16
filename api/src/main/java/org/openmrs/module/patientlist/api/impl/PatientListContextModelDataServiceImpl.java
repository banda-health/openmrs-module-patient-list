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
import org.openmrs.module.patientlist.api.IPatientListContextModelDataService;
import org.openmrs.module.patientlist.api.model.PatientListContextModel;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.model.PatientListOperator;
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
public class PatientListContextModelDataServiceImpl extends
        BaseObjectDataServiceImpl<PatientListContextModel, BasicObjectAuthorizationPrivileges>
        implements IPatientListContextModelDataService {

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
			List<Object> paramValues = new ArrayList<Object>();
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
			hql.append(PatientListHQLBuilder.constructBaseHQL(patientList));
		}

		// add where clause
		hql.append(" where ");

		// apply patient list conditions
		hql.append("(");
		hql.append(applyPatientListConditions(patientList.getPatientListConditions(), paramValues));
		hql.append(")");

		//apply ordering if any
		hql.append(applyPatientListOrdering(patientList.getOrdering()));

		return hql.toString();
	}

	/**
	 * Parse patient list conditions and add create sub queries to be added on the main HQL query. Parameter search values
	 * will be stored separately and later set when running query.
	 * @param patientListConditions
	 * @param paramValues
	 * @return
	 */
	private String applyPatientListConditions(List<PatientListCondition> patientListConditions,
	        List<Object> paramValues) {
		int count = 0;
		int noOfConditions = patientListConditions.size();
		StringBuilder hql = new StringBuilder();
		// apply conditions
		for (PatientListCondition condition : patientListConditions) {
			if (condition == null || PatientInformation.getInstance().getField(condition.getField()) == null) {
				throw new RuntimeException("condition cannot be null");
			}

			String join = " AND ";
			if (condition.getOperator() == null) {
				throw new RuntimeException("operator cannot be null");
			}

			IPatientInformationField patientInformationField =
			        PatientInformation.getInstance().getField(condition.getField());

			if (condition.getOperator().equals(PatientListOperator.EQUALS)) {
				hql.append(patientInformationField.equalOperator(condition.getValue()));
				paramValues.addAll(patientInformationField.getParameterValues());
			}

			if (noOfConditions > ++count) {
				hql.append(join);
			}
		}

		return hql.toString();
	}

	/**
	 * Creates hql sub-queries for patient and visit attributes. Example: v.attr.bed = 2
	 * @param condition
	 * @param paramValues
	 * @return
	 */
	private String createAttributeSubQueries(PatientListCondition condition, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();
		String attributeName = condition.getField().split("\\.")[2];
		attributeName = attributeName.replaceAll("_", " ");
		String operator = ConvertPatientListOperators.convertOperator(condition.getOperator());
		String value = condition.getValue();

		if (StringUtils.contains(condition.getField(), "p.attr.")) {
			hql.append("(attrType.name = ?");
			hql.append(" AND ");
			if (StringUtils.equalsIgnoreCase(operator, "exists")) {
				hql.append("attr is not null");
			} else if (StringUtils.equalsIgnoreCase(operator, "not exists")) {
				hql.append("attr is null");
			} else {
				hql.append("attr.value ");
				hql.append(operator);
			}
		} else if (StringUtils.contains(condition.getField(), "v.attr.")) {
			hql.append("(vattrType.name = ?");
			hql.append(" AND ");
			if (StringUtils.equalsIgnoreCase(operator, "exists")) {
				hql.append("vattr is not null");
			} else if (StringUtils.equalsIgnoreCase(operator, "not exists")) {
				hql.append("vattr is null");
			} else {
				hql.append("vattr.valueReference ");
				hql.append(operator);
			}
		}

		paramValues.add(attributeName);

		if (!StringUtils.containsIgnoreCase(operator, "null")
		        && !StringUtils.containsIgnoreCase(operator, "exists")) {
			hql.append(" ? ");
			if (StringUtils.equals(operator, "LIKE")) {
				paramValues.add("%" + value + "%");
			} else {
				paramValues.add(value);
			}
		}

		hql.append(") ");

		return hql.toString();
	}

	/**
	 * Creates hql sub-queries for patient aliases (names and addresses). Example: p.names.givenName, p.addresses.address1
	 * @param condition
	 * @param paramValues
	 * @return
	 */
	private String createAliasesSubQueries(PatientListCondition condition,
	        String mappingFieldName, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();
		String searchField = null;
		String operator = ConvertPatientListOperators.convertOperator(condition.getOperator());
		String value = condition.getValue();
		if (mappingFieldName != null) {
			// p.names.givenName
			String subs[] = mappingFieldName.split("\\.");
			if (subs != null) {
				searchField = subs[2];
			}
		}

		if (searchField != null) {
			if (StringUtils.contains(mappingFieldName, "p.names.")) {
				if (StringUtils.contains(condition.getField(), "p.fullName")) {
					hql.append(" (pnames.givenName ");
					hql.append(operator);
					if (!StringUtils.containsIgnoreCase(operator, "null")) {
						hql.append(" ? ");
					}

					hql.append(" or pnames.familyName ");
					hql.append(operator);
					hql.append(" ");
					if (!StringUtils.containsIgnoreCase(operator, "null")) {
						hql.append(" ? ");
						paramValues.add(value);
					}

					hql.append(" ) ");
				} else {
					hql.append("pnames.");
					hql.append(searchField);
					hql.append(" ");
					hql.append(operator);
					if (!StringUtils.containsIgnoreCase(operator, "null")) {
						hql.append(" ? ");
					}
				}
			} else if (StringUtils.contains(mappingFieldName, "p.addresses.")) {
				hql.append("paddresses.");
				hql.append(searchField);
				hql.append(" ");
				hql.append(operator);
				if (!StringUtils.containsIgnoreCase(operator, "null")) {
					hql.append(" ? ");
				}
			} else if (StringUtils.contains(mappingFieldName, "p.identifiers.")) {
				hql.append("pidentifiers.");
				hql.append(searchField);
				hql.append(" ");
				hql.append(operator);
				if (!StringUtils.containsIgnoreCase(operator, "null")) {
					hql.append(" ? ");
				}
			}

			if (StringUtils.equals(operator, "LIKE")) {
				paramValues.add("%" + value + "%");
			} else if (!StringUtils.containsIgnoreCase(operator, "null")) {
				paramValues.add(value);
			}

			hql.append(" ");
		}

		return hql.toString();
	}

	/**
	 * Order hql query by given fields
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
