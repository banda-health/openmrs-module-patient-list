package org.openmrs.module.patientlist.api.model.custom;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientInformationField;
import org.openmrs.module.patientlist.api.util.PatientListDateUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by andrew on 3/3/17.
 */
public class AgePatientInformationField<T extends OpenmrsObject> extends AbstractPatientListField {

	public AgePatientInformationField(String prefix, String name, Class<?> dataType,
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
		hql.append(getMappingFieldName());
		hql.append(" = ");
		try {
			int age = Integer.valueOf(searchValue);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -age);
			getParameterValues().add(PatientListDateUtil.simpleDateFormat.parse(
			        PatientListDateUtil.simpleDateFormat.format(calendar.getTime())));
		} catch (ParseException pex) {
			LOG.error("error parsing date: ", pex);
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
