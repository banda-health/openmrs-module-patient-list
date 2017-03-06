package org.openmrs.module.patientlist.api.model;

/**
 * Created by andrew on 3/3/17.
 */
public interface IPatientListFieldOperatorType {
	String equalOperator(String searchValue);

	String greaterThan(String searchValue);

	String lesserThan(String searchValue);

	String between(String searchValue);
}
