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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.util.IPatientInformationField;

/**
 * Abstract common patient list field functionality and implement operator types with default implementation.
 */
public abstract class AbstractPatientListField<T extends OpenmrsData>
        implements IPatientInformationField<T> {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	private String prefix;
	private String name;
	private String mappingFieldName;
	private Class<?> dataType;
	private Func1<T, Object> getValueFunc;

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

	public void setValueFunc(Func1<T, Object> func) {
		this.getValueFunc = func;
	}

	public Object getValue(T source) {
		return getValueFunc.apply(source);
	}

	public String getMappingFieldName() {
		return mappingFieldName;
	}

	public void setMappingFieldName(String mappingFieldName) {
		this.mappingFieldName = mappingFieldName;
	}

	private String getDefaultOperatorImplementation(String searchValue, String operator) {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingFieldName());
		hql.append(" ");
		hql.append(operator);
		hql.append(" ? ");
		getParameterValues().add(searchValue);

		return hql.toString();
	}

	@Override
	public String between(String searchValue) {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingFieldName());
		hql.append(" BETWEEN ? AND ? ");
		if (StringUtils.contains(searchValue, "|")) {
			String[] splitSearchValues = searchValue.split("|");
			getParameterValues().add(splitSearchValues[0]);
			getParameterValues().add(splitSearchValues[1]);
		}

		return hql.toString();
	}

	@Override
	public String equalOperator(String searchValue) {
		return getDefaultOperatorImplementation(searchValue, "=");
	}

	@Override
	public String greaterThan(String searchValue) {
		return getDefaultOperatorImplementation(searchValue, ">");
	}

	@Override
	public String lesserThan(String searchValue) {
		return getDefaultOperatorImplementation(searchValue, "<");
	}
}
