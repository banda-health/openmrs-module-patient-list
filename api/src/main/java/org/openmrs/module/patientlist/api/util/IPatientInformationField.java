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

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.IPatientListFieldOperatorType;

import java.util.List;

/**
 * Created by andrew on 3/6/17.
 */
public interface IPatientInformationField<T extends OpenmrsData>
        extends IPatientListFieldOperatorType {

	String getPrefix();

	void setPrefix(String prefix);

	String getName();

	void setName(String name);

	Class<?> getDataType();

	void setDataType(Class<?> dataType);

	void setValueFunc(Func1<T, Object> func);

	Object getValue(T source);

	String getMappingFieldName();

	void setMappingFieldName(String mappingFieldName);

	List<Object> getParameterValues();
}
