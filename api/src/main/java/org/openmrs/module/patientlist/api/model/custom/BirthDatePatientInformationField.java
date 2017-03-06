package org.openmrs.module.patientlist.api.model.custom;

import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.util.PatientListDateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 3/3/17.
 */
public class BirthDatePatientInformationField<T extends OpenmrsData> extends AbstractPatientListField {

	public BirthDatePatientInformationField(String prefix, String name, Class<?> dataType,
	    Func1<T, Object> valueFunc, String mappingFieldName) {
		setPrefix(prefix);
		setName(name);
		setDataType(dataType);
		setValueFunc(valueFunc);
		setMappingFieldName(mappingFieldName);
	}

	@Override
	public List<Object> getParameterValues() {
		return new ArrayList<Object>();
	}

	@Override
	public String equalOperator(String searchDate) {
		StringBuilder hql = new StringBuilder();
		hql.append(getMappingFieldName());
		hql.append(" = ");
		getParameterValues().add(PatientListDateUtil.formatDate(searchDate));

		return hql.toString();
	}

	@Override
	public String greaterThan(String searchValue) {
		return null;
	}

	@Override
	public String lesserThan(String searchValue) {
		return null;
	}
}
