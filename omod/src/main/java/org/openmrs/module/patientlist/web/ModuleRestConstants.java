package org.openmrs.module.patientlist.web;

import org.openmrs.module.openhmis.commons.web.WebConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;

/**
 * Constants class for REST urls.
 */
public class ModuleRestConstants extends WebConstants {
	public static final String MODULE_REST_ROOT = RestConstants.VERSION_2 + "/patientlist/";
	public static final String PATIENT_LIST_RESOURCE = MODULE_REST_ROOT + "list";
	public static final String PATIENT_LIST_DATA_RESOURCE = MODULE_REST_ROOT + "data";
	public static final String PATIENT_LIST_CONDITION_RESOURCE = MODULE_REST_ROOT + "condition";
	public static final String PATIENT_LIST_ORDER_RESOURCE = MODULE_REST_ROOT + "order";
	public static final String PATIENT_LIST_FIELDS_RESOURCE = MODULE_REST_ROOT + "fields";
	public static final String PATIENT_LIST_LIVE_PREVIEWING_RESOURCE = MODULE_REST_ROOT + "live";
	public static final String PATIENT_LOOKUP_DATATYPE_RESOURCE = MODULE_REST_ROOT + "lookup";
}
