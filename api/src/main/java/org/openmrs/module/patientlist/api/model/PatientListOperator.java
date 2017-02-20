package org.openmrs.module.patientlist.api.model;

import org.openmrs.logic.op.Operator;

/**
 * The allowable {@link PatientListOperator} operators.
 */
public enum PatientListOperator {

	LIKE(Operator.CONTAINS),
	EQUALS(Operator.EQUALS),
	GT(Operator.GT),
	GTE(Operator.GTE),
	LT(Operator.LT),
	LTE(Operator.LTE),
	NOT_EQUALS,
	BETWEEN,
	NULL,
	NOT_NULL,
	DEFINED,
	NOT_DEFINED,
	RELATIVE;

	private Operator operator;

	private PatientListOperator() {}

	private PatientListOperator(Operator operator) {
		this.operator = operator;
	}

}
