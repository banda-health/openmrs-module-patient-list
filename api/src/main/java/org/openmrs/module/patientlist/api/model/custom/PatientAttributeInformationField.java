package org.openmrs.module.patientlist.api.model.custom;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientListOperatorConstants;

/**
 * Define {@link PatientAttributeInformationField} operators
 */
public class PatientAttributeInformationField<T extends OpenmrsData>
        extends AbstractPatientListField<T, String> {
	private String attributeName;

	public PatientAttributeInformationField(
	    String prefix, String name, Class<?> dataType, Class<T> entityType,
	    Func1<T, Object> valueFunc, String attributeName) {
		setPrefix(prefix);
		setName(name);
		setDataType(dataType);
		setValueFunc(valueFunc);
		setEntityType(entityType);

		this.attributeName = attributeName;
	}

	@Override
	public String equalOperator(String searchValue) {
		StringBuilder hql = new StringBuilder();
		hql.append(" (attrType.name = ? AND attr.voided != true AND attr.value ");
		hql.append(PatientListOperatorConstants.EQUALS);
		hql.append(" ? ) ");

		getParameterValues().add(attributeName);
		getParameterValues().add(searchValue);

		return hql.toString();
	}
}
