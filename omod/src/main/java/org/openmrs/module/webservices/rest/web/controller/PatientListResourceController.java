package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * REST controller for patient list resources.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.MODULE_REST_ROOT)
public class PatientListResourceController extends MainResourceController {

	@Override
	public String getNamespace() {
		return ModuleRestConstants.MODULE_REST_ROOT;
	}
}
