/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientlist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class PatientListModuleActivator extends BaseModuleActivator {
	private static final Log LOG = LogFactory.getLog(PatientListModuleActivator.class);

	/**
	 * @see BaseModuleActivator#contextRefreshed()
	 */
	@Override
	public void contextRefreshed() {
		LOG.info("Visit Task Module refreshed");
	}

	/**
	 * @see #started()
	 */
	public void started() {
		LOG.info("Started Patient List");
	}

	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		LOG.info("Shutdown Patient List");
	}

}
