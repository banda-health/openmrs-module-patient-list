package org.openmrs.module.patientlist.api.model.custom;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientListMappingField;
import org.openmrs.module.patientlist.api.util.PatientInformation;

/**
 * Define {@link HasActiveVisitPatientInformationField} operators
 */
public class HasActiveVisitPatientInformationField<T extends OpenmrsData>
        extends AbstractPatientListField<T, Boolean> {

	public HasActiveVisitPatientInformationField(
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
		hql.append(PatientInformation.VISIT_PREFIX);
		hql.append(".startDatetime IS NOT NULL AND ");
		hql.append(PatientInformation.VISIT_PREFIX);
		hql.append(".stopDatetime is NULL ");

		return hql.toString();
	}
}
