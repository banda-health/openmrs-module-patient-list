package org.openmrs.module.patientlist.api.model.custom;

import org.apache.commons.lang.math.NumberUtils;
import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 3/3/17.
 */
public class DiagnosisPatientInformationField<T extends OpenmrsData> extends AbstractPatientListField {

	public DiagnosisPatientInformationField(String prefix, String name, Class<?> dataType,
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
	public String equalOperator(String searchValue) {
		StringBuilder hql = new StringBuilder();

		// coded diagnosis
		if (NumberUtils.isDigits(searchValue)) {
			hql.append(" ob.valueCoded.conceptId = ? ");
			getParameterValues().add(Integer.valueOf(searchValue));
		} else {
			// un-coded diagnosis
			hql.append(" ob.valueText = ? ");
			getParameterValues().add(searchValue);
		}

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
