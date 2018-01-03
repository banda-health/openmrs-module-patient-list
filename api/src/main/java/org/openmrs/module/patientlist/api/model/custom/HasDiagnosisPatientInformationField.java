package org.openmrs.module.patientlist.api.model.custom;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientListMappingField;
import org.openmrs.module.patientlist.api.util.PatientInformation;

/**
 * Define {@link HasDiagnosisPatientInformationField} operators
 */
public class HasDiagnosisPatientInformationField<T extends OpenmrsData>
        extends AbstractPatientListField<T, Boolean> {

	public HasDiagnosisPatientInformationField(
	    String prefix, String name, Class<?> dataType, Class<T> entityType,
	    Func1<T, Object> valueFunc, String mappingField) {
		setPrefix(prefix);
		setName(name);
		setDataType(dataType);
		setValueFunc(valueFunc);
		setEntityType(entityType);
		setMappingField(new PatientListMappingField<T>(mappingField, entityType));
	}

	@Override
	public String equalOperator(Boolean searchValue) {
		StringBuilder hql = new StringBuilder();
		hql.append("(ob.voided != true AND (ob.valueCoded.conceptClass.uuid = '8d4918b0-c2cc-11de-8d13-0010c6dffd0f' ");
		hql.append("or ob.valueText != ''))");

		return hql.toString();
	}
}
