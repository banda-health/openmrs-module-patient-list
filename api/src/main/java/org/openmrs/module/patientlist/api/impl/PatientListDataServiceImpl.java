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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListData;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.api.model.PatientInformationField;
import org.openmrs.module.patientlist.api.model.IBasePatientList;
import org.openmrs.module.patientlist.api.model.PatientListRelativeDate;
import org.openmrs.module.patientlist.api.util.PatientListDateUtil;
import org.openmrs.module.patientlist.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.util.ConvertPatientListOperators;
import org.openmrs.module.patientlist.api.util.PatientInformation;
import org.openmrs.module.patientlist.api.util.PatientListTemplateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Data service implementation class for {@link PatientListData}'s.
 */
public class PatientListDataServiceImpl extends
        BaseObjectDataServiceImpl<PatientListData, BasicObjectAuthorizationPrivileges>
        implements IPatientListDataService {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(PatientListData object) {
		return;
	}

	@Override
	public List<PatientListData> getPatientListData(PatientList patientList, PagingInfo pagingInfo) {
		List<PatientListData> patientListDataSet = new ArrayList<PatientListData>();
		try {
			// get record count
			Query countQuery = createQuery(patientList, true);

			// set paging params
			Long count = (Long)countQuery.uniqueResult();
			pagingInfo.setTotalRecordCount(count);
			pagingInfo.setLoadRecordCount(false);

			// retrieve actual objects
			Query query = createQuery(patientList, false);
			query = this.createPagingQuery(pagingInfo, query);
			List results = query.list();
			if (results.size() > 0) {
				for (Object result : results) {
					Patient patient;
					Visit visit = null;
					if (result instanceof Patient) {
						patient = (Patient)result;
					} else {
						visit = (Visit)result;
						patient = visit.getPatient();
					}

					PatientListData patientListData = new PatientListData(patient, visit, patientList);
					// apply header template.
					if (patientListData.getPatientList().getHeaderTemplate() != null) {
						patientListData.setHeaderContent(
						        PatientListTemplateUtil.applyTemplate(
						            patientListData.getPatientList().getHeaderTemplate(), patientListData));
					}

					// apply body template
					if (patientListData.getPatientList().getBodyTemplate() != null) {
						patientListData.setBodyContent(
						        PatientListTemplateUtil.applyTemplate(
						            patientListData.getPatientList().getBodyTemplate(), patientListData));
					}

					// Set the data uuid to a consistent, generated uuid based on the list and patient uuid's
					String source = patientListData.getPatientList().getUuid() + patientListData.getPatient().getUuid();
					String uuid = UUID.nameUUIDFromBytes(source.getBytes()).toString();
					patientListData.setUuid(uuid);

					patientListDataSet.add(patientListData);
				}
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}

		return patientListDataSet;
	}

	private Query createQuery(PatientList patientList, boolean countQuery) {
		List<Object> paramValues = new ArrayList<Object>();
		Query query = getRepository().createQuery(constructHqlQuery(patientList, paramValues, countQuery));
		// set parameters with actual values
		if (paramValues.size() > 0) {
			int index = 0;
			for (Object value : paramValues) {
				query.setParameter(index++, value);
			}
		}

		return query;
	}

	/**
	 * Constructs a patient list with given conditions (and ordering)
	 * @param patientList
	 * @param paramValues
	 * @return
	 */
	private String constructHqlQuery(
	        PatientList patientList, List<Object> paramValues, boolean countQuery) {
		StringBuilder hql = new StringBuilder();
		if (patientList != null && patientList.getPatientListConditions() != null) {
			if (searchField(patientList.getPatientListConditions(), "v.", false)
			        || searchField(patientList.getPatientListConditions(), "hasActiveVisit", false)
			        || searchField(patientList.getPatientListConditions(), "hasDiagnosis", false)) {
				// join visit and patient tables
				if (countQuery) {
					hql.append("select count(v) from Visit v inner join v.patient as p ");
				} else {
					hql.append("select v from Visit v inner join v.patient as p ");
				}
			} else {
				// use only the patient table
				if (countQuery) {
					hql.append("select count(p) from Patient p ");
				} else {
					hql.append("select p from Patient p ");
				}
			}

			// only join person attributes and attribute types if required to
			if (searchField(patientList.getPatientListConditions(), "p.attr", false)
			        || searchField(patientList.getOrdering(), "p.attr", false)) {
				hql.append("inner join p.attributes as attr ");
				hql.append("inner join attr.attributeType as attrType ");
			}

			// only join visit attributes and attribute types if required to
			if (searchField(patientList.getPatientListConditions(), "v.attr", false)
			        || searchField(patientList.getOrdering(), "v.attr", false)) {
				hql.append("inner join v.attributes as vattr ");
				hql.append("inner join vattr.attributeType as vattrType ");
			}

			if (searchField(patientList.getPatientListConditions(), "v.diagnosis", false)
			        || searchField(patientList.getPatientListConditions(), "hasDiagnosis", false)) {
				hql.append("inner join v.encounters as encounter ");
				hql.append("inner join encounter.obs as ob ");
			}

			// only join names if required
			if (searchField(patientList.getPatientListConditions(), "p.names", true)
			        || searchField(patientList.getOrdering(), "p.names", true)) {
				hql.append("inner join p.names as pnames ");
			}

			// only join addresses if required
			if (searchField(patientList.getPatientListConditions(), "p.addresses", true)
			        || searchField(patientList.getOrdering(), "p.addresses", true)) {
				hql.append("inner join p.addresses as paddresses ");
			}

			// only join identifiers if required
			if (searchField(patientList.getPatientListConditions(), "p.identifiers", true)
			        || searchField(patientList.getOrdering(), "p.identifiers", true)) {
				hql.append("inner join p.identifiers as pidentifiers ");
			}
		}

		// add where clause
		hql.append(" where ");

		// apply patient list conditions
		hql.append("(");
		hql.append(applyPatientListConditions(patientList.getPatientListConditions(), paramValues));
		hql.append(")");

		//apply ordering if any
		if (!countQuery) {
			hql.append(applyPatientListOrdering(patientList.getOrdering()));
		}

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
		int len = patientListConditions.size();
		StringBuilder hql = new StringBuilder();
		// apply conditions
		for (PatientListCondition condition : patientListConditions) {
			++count;
			if (condition != null && PatientInformation.getInstance().getField(condition.getField()) != null) {
				String join = " AND ";
				String operator = "";
				if (condition.getOperator() != null) {
					operator = ConvertPatientListOperators.convertOperator(condition.getOperator());
				}

				PatientInformationField patientInformationField =
				        PatientInformation.getInstance().getField(condition.getField());
				String mappingFieldName = patientInformationField.getMappingFieldName();
				if ((StringUtils.contains(condition.getField(), "p.attr.")
				|| StringUtils.contains(condition.getField(), "v.attr."))) {
					hql.append(createAttributeSubQueries(condition, paramValues));
					if (!searchField(patientListConditions, "p.hasActiveVisit", false)) {
						join = " OR ";
					}
				} else if (StringUtils.contains(mappingFieldName, "p.names.")
				        || StringUtils.contains(mappingFieldName, "p.addresses.")
				        || StringUtils.contains(mappingFieldName, "p.identifiers.")) {
					hql.append(createAliasesSubQueries(condition, mappingFieldName, paramValues));
				} else if (StringUtils.contains(condition.getField(), "p.hasActiveVisit")) {
					hql.append(" v.startDatetime IS NOT NULL AND v.stopDatetime is NULL ");
				} else if (StringUtils.contains(condition.getField(), "v.hasDiagnosis")) {
					hql.append("(ob.voided != true AND (ob.valueCoded.conceptClass.uuid = ? or ob.valueText != ''))");
					paramValues.add("8d4918b0-c2cc-11de-8d13-0010c6dffd0f");
				} else if (StringUtils.contains(condition.getField(), "v.diagnosis")) {
					// coded diagnosis
					if (NumberUtils.isDigits(condition.getValue())) {
						hql.append(" ob.voided != true AND ob.valueCoded.conceptId ");
						hql.append(operator);
						hql.append(" ? ");
						paramValues.add(Integer.valueOf(condition.getValue()));
					} else {
						// un-coded diagnosis
						hql.append(" ob.voided != true AND ob.valueText ");
						hql.append(operator);
						hql.append(" ? ");
						paramValues.add(condition.getValue());
					}
				} else if (StringUtils.contains(condition.getField(), "p.age")) {
					try {
						hql.append(" p.birthdate ");
						if (StringUtils.contains(operator, "<")) {
							operator = StringUtils.replace(operator, "<", ">");
						} else if (StringUtils.contains(operator, ">")) {
							operator = StringUtils.replace(operator, ">", "<");
						}

						if (!StringUtils.containsIgnoreCase(operator, "null") && !StringUtils
						        .containsIgnoreCase(operator, "BETWEEN")) {
							if (StringUtils.equals(operator, "=")) {
								operator = StringUtils.replace(operator, "=", "BETWEEN");
								hql.append(" ");
								hql.append(operator);
								hql.append(" ");
								hql.append(" ? ");
								if (StringUtils.isNumeric(condition.getValue())) {
									int age = Integer.valueOf(condition.getValue());
									Calendar calendar = Calendar.getInstance();
									calendar.add(Calendar.YEAR, -age);
									Calendar calendar1 = Calendar.getInstance();
									calendar1.add(Calendar.YEAR, -(age + 1));
									paramValues.add(PatientListDateUtil.simpleDateFormat.parse(
									        PatientListDateUtil.simpleDateFormat.format(calendar1.getTime())));
									hql.append(" AND ? ");
									paramValues.add(PatientListDateUtil.simpleDateFormat.parse(
									        PatientListDateUtil.simpleDateFormat.format(calendar.getTime())));
								}
							} else {
								hql.append(" ");
								hql.append(operator);
								hql.append(" ? ");
								if (StringUtils.isNumeric(condition.getValue())) {
									int age = Integer.valueOf(condition.getValue());
									Calendar calendar = Calendar.getInstance();
									if (StringUtils.equals(operator, ">=") || StringUtils.equals(operator, "<")) {
										calendar.add(Calendar.YEAR, -(age + 1));
									} else {
										calendar.add(Calendar.YEAR, -age);
									}
									paramValues.add(PatientListDateUtil.simpleDateFormat.parse(
									        PatientListDateUtil.simpleDateFormat.format(calendar.getTime())));
								}
							}
						} else {
							try {
								// BETWEEN age should be separated by |
								hql.append(operator);
								hql.append(" ");
								if (StringUtils.contains(condition.getValue(), "|")) {
									hql.append(" ? ");
									String[] numbers = StringUtils.split(condition.getValue(), "|");
									int ageOne = Integer.valueOf(numbers[0]);
									int ageTwo = Integer.valueOf(numbers[1]) + 1;
									Calendar calendar = Calendar.getInstance();
									calendar.add(Calendar.YEAR, -ageTwo);
									paramValues.add(PatientListDateUtil.simpleDateFormat.parse(
									        PatientListDateUtil.simpleDateFormat.format(calendar.getTime())));
									hql.append(" AND ? ");
									Calendar calendar1 = Calendar.getInstance();
									calendar1.add(Calendar.YEAR, -ageOne);
									paramValues.add(PatientListDateUtil.simpleDateFormat.parse(
									        PatientListDateUtil.simpleDateFormat.format(calendar1.getTime())));
								} else {
									if (!StringUtils.containsIgnoreCase(operator, "IS NOT NULL")) {
										hql.append(" ? ");
										paramValues.add(condition.getValue());
									}

								}
							} catch (ParseException pex) {
								paramValues.add(condition.getValue());
							}
						}

					} catch (ParseException pex) {
						LOG.error("error parsing date: ", pex);
					}
				} else {
					if (mappingFieldName == null) {
						LOG.error("Unknown mapping for field name: " + condition.getField());
						continue;
					}

					hql.append(mappingFieldName);
					hql.append(" ");

					String value = condition.getValue();
					if (StringUtils.equalsIgnoreCase(operator, "RELATIVE")) {
						operator = "BETWEEN";
						value = PatientListDateUtil.createRelativeDate(
						        PatientListRelativeDate.valueOf(value));
					}

					hql.append(operator);
					hql.append(" ");
					if (StringUtils.isNotEmpty(value)) {
						if (!StringUtils.containsIgnoreCase(operator, "null")) {
							hql.append("?");
							if (patientInformationField.getDataType().isAssignableFrom(Date.class)) {
								try {
									// BETWEEN dates should be separated by |
									if (StringUtils.contains(value, "|")) {
										String[] dates = StringUtils.split(value, "|");
										paramValues.add(
										        PatientListDateUtil.simpleDateFormat.parse(dates[0]));
										hql.append(" AND ? ");
										paramValues.add(
										        PatientListDateUtil.simpleDateFormat.parse(dates[1]));
									} else {
										paramValues.add(
										        PatientListDateUtil.simpleDateFormat.parse(value));
									}
								} catch (ParseException pex) {
									paramValues.add(value);
								}
							} else {
								if (StringUtils.equals(operator, "LIKE")) {
									paramValues.add("%" + value + "%");
								} else {
									paramValues.add(value);
								}
							}
						}
					}
				}

				if (count < len) {
					hql.append(join);
				}
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
			hql.append("(attrType.name = ? AND attr.voided != true AND ");
			if (StringUtils.equalsIgnoreCase(operator, "exists")) {
				hql.append("attr is not null");
			} else if (StringUtils.equalsIgnoreCase(operator, "not exists")) {
				hql.append("attr is null");
			} else {
				hql.append("attr.value ");
				hql.append(operator);
			}
		} else if (StringUtils.contains(condition.getField(), "v.attr.")) {
			hql.append("(vattrType.name = ? AND vattr.voided != true AND ");
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
				PatientInformationField field = PatientInformation.getInstance().
				        getField(order.getField());

				if (field == null) {
					continue;
				}

				String mappingFieldName = PatientInformation.getInstance().
				        getField(order.getField()).getMappingFieldName();

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
	 * Searches for a given field in the patient list conditions and ordering.
	 * @param list
	 * @param search
	 * @return
	 */
	private <T extends IBasePatientList> boolean searchField(
	        List<T> list, String search, boolean mappingField) {
		for (T t : list) {
			if (t == null) {
				continue;
			}

			String field = t.getField();

			if (field == null) {
				continue;
			}

			if (mappingField) {
				PatientInformationField patientInformationField =
				        PatientInformation.getInstance().getField(field);
				if (patientInformationField == null) {
					continue;
				}

				String matchField = patientInformationField.getMappingFieldName();
				if (StringUtils.contains(matchField, search)) {
					return true;
				}
			} else if (StringUtils.contains(field, search)) {
				return true;
			}
		}

		return false;
	}
}
