/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
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
	public static final String PATIENT_LIST_PHOTOS_RESOURCE = MODULE_REST_ROOT + "photos";
	public static final String PATIENT_LIST_VISIT_EDIT_RESOURCE = MODULE_REST_ROOT + "visitedit";
	public static final String PATIENT_LIST_CONCEPT_ANSWER_RESOURCE = MODULE_REST_ROOT + "conceptanswer";
	public static final String PATIENT_LIST_LIVE_PREVIEWING_RESOURCE = MODULE_REST_ROOT + "live";
	public static final String PATIENT_LOOKUP_DATATYPE_RESOURCE = MODULE_REST_ROOT + "lookup";
	public static final String VISIT_NOTE_RESOURCE = MODULE_REST_ROOT + "visitnote";
}
