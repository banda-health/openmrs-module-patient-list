package org.openmrs.module.patientlist.api.util;

import org.openmrs.module.patientlist.api.model.PatientListOperator;

/**
 * Convert Patient List Operators to literal values.
 */
public class ConvertPatientListOperators {

	public static String convertOperator(PatientListOperator operator) {
		String literalOperator;
		switch (operator) {
			case EQUALS:
				literalOperator = "=";
				return literalOperator;
			case NOT_EQUALS:
				literalOperator = "!=";
				return literalOperator;
			case GT:
				literalOperator = ">";
				return literalOperator;
			case GTE:
				literalOperator = ">=";
				return literalOperator;
			case LT:
				literalOperator = "<";
				return literalOperator;
			case LTE:
				literalOperator = "<=";
				return literalOperator;
			case NULL:
				literalOperator = "IS NULL";
				return literalOperator;
			case NOT_NULL:
				literalOperator = "IS NOT NULL";
				return literalOperator;
			case BETWEEN:
				literalOperator = "BETWEEN";
				return literalOperator;
			case LIKE:
				literalOperator = "LIKE";
				return literalOperator;
			case DEFINED:
				literalOperator = "EXISTS";
				return literalOperator;
			case NOT_DEFINED:
				literalOperator = "NOT EXISTS";
				return literalOperator;
			case RELATIVE:
				literalOperator = "RELATIVE";
				return literalOperator;
			default:
				throw new IllegalArgumentException("Invalid operator " + operator);
		}
	}
}
