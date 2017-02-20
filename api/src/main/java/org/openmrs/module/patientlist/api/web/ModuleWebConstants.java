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
package org.openmrs.module.patientlist.api.web;

import org.openmrs.module.openhmis.commons.web.WebConstants;
import org.openmrs.module.patientlist.api.util.ModuleConstants;

/**
 * Constants class for the module web resources.
 */
public class ModuleWebConstants extends WebConstants {
	public static final String MODULE_ROOT = WebConstants.MODULE_BASE + ModuleConstants.MODULE_NAME + "/";
	public static final String MODULE_RESOURCE_ROOT = WebConstants.MODULE_RESOURCE_BASE + ModuleConstants.MODULE_NAME + "/";

	public static final String PATIENT_LIST_LANDING_PAGE_EXTENSION_POINT_ID = "org.openmrs.module.patientlist.landing";
	public static final String PATIENT_LIST_MANAGE_MODULE_PAGE_EXTENSION_POINT_ID =
	        "org.openmrs.module.patientlist.manage.module";

	protected ModuleWebConstants() {}
}
