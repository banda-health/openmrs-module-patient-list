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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.util.IPatientInformationField;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract common patient list field functionality and implement operator types with default implementation.
 */
public abstract class AbstractPatientListField<T extends OpenmrsData, E>
        implements IPatientInformationField<T, E> {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	private String prefix;
	private String name;
	private PatientListMappingField mappingField;
	private Class<?> dataType;
	private Func1<T, Object> getValueFunc;
	private Class<T> entityType;
	private List<E> params = new ArrayList<E>();

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getDataType() {
		return dataType;
	}

	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	public void setEntityType(Class<T> entityType) {
		this.entityType = entityType;
	}

	public Class<T> getEntityType() {
		return entityType;
	}

	public void setValueFunc(Func1<T, Object> func) {
		this.getValueFunc = func;
	}

	public Object getValue(T source) {
		return getValueFunc.apply(source);
	}

	public PatientListMappingField getMappingField() {
		return mappingField;
	}

	public void setMappingField(PatientListMappingField mappingField) {
		this.mappingField = mappingField;
	}

	@Override
	public String between(E value1, E value2) {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingField().getMappingFieldName());
		hql.append(" ");
		hql.append(PatientListOperatorConstants.BETWEEN);
		hql.append(" ? AND ? ");

		getParameterValues().add(value1);
		getParameterValues().add(value2);

		return hql.toString();
	}

	@Override
	public String equalOperator(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.EQUALS);
	}

	@Override
	public String greaterThan(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.GREATER_THAN);
	}

	@Override
	public String lesserThan(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.LESS_THAN);
	}

	@Override
	public String greaterThanOrEquals(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.GREATER_THAN_OR_EQUALS);
	}

	@Override
	public String lesserThanOrEquals(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.LESS_THAN_OR_EQUALS);
	}

	@Override
	public String notEquals(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.NOT_EQUALS);
	}

	@Override
	public String like(E searchValue) {
		return getDefaultOperatorImplementation(searchValue, PatientListOperatorConstants.LIKE);
	}

	@Override
	public String isNull() {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingField().getMappingFieldName());
		hql.append(" IS NULL ");

		return hql.toString();
	}

	@Override
	public String notNull() {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingField().getMappingFieldName());
		hql.append(" IS NOT NULL ");

		return hql.toString();
	}

	protected String getDefaultOperatorImplementation(E searchValue, String operator) {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingField().getMappingFieldName());
		hql.append(" ");
		hql.append(operator);
		hql.append(" ? ");
		getParameterValues().add(searchValue);

		return hql.toString();
	}

	@Override
	public List<E> getParameterValues() {
		return params;
	}

	@Override
	public void refresh() {
		getParameterValues().clear();
	}
}
