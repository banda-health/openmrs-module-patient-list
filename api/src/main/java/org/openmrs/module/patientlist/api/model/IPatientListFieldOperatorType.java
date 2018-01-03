package org.openmrs.module.patientlist.api.model;

/**
 * Define all operator types
 */
public interface IPatientListFieldOperatorType<E> {
	String equalOperator(E searchValue);

	String greaterThan(E searchValue);

	String greaterThanOrEquals(E searchValue);

	String lesserThan(E searchValue);

	String lesserThanOrEquals(E searchValue);

	String between(E value1, E value2);

	String notEquals(E searchValue);

	String like(E searchValue);

	String isNull();

	String notNull();
}
