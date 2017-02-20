package org.openmrs.module.patientlist.uiframework;

import org.openmrs.module.openhmis.commons.uiframework.UiConfigurationFactory;

/**
 * The OpenMRS UI Framework configuration settings.
 */
public class UiConfigurationPatientList extends UiConfigurationFactory {

	@Override
	public String getModuleId() {
		return "patientlist";
	}
}
