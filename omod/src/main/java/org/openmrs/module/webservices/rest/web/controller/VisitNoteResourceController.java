package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.fragment.controller.htmlform.EnterHtmlFormFragmentController;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Save & Update Visit Note.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.VISIT_NOTE_RESOURCE)
public class VisitNoteResourceController {

	@Autowired
	private FeatureToggleProperties featureToggles;

	@Autowired
	private FormatterService formatterService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public SimpleObject save(
	        @RequestParam("personId") Patient patient,
	        @RequestParam("htmlFormId") Integer htmlFormId,
	        @RequestParam(value = "encounterId", required = false) Encounter encounter,
	        @RequestParam(value = "visitId", required = false) Visit visit,
	        @RequestParam(value = "createVisit", required = false) Boolean createVisit,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        HttpServletRequest request) {

		SimpleObject result;
		HtmlForm hf = null;
		if (htmlFormId != null) {
			HtmlFormEntryService service = Context.getService(HtmlFormEntryService.class);
			hf = service.getHtmlForm(htmlFormId);
		}

		if (encounter != null) {
			encounter.setVoided(true);
		}

		AdtService adtService = Context.getService(AdtService.class);
		LocationService locationService = Context.getLocationService();
		ProviderService providerService = Context.getProviderService();

		UiSessionContext sessionContext = new UiSessionContext(locationService, providerService, request);

		FragmentActionUiUtils uiUtils = new FragmentActionUiUtils(
		        new MutableResourceBundleMessageSource(), null, null, formatterService);

		EnterHtmlFormFragmentController controller = new EnterHtmlFormFragmentController();

		try {
			result = controller.submit(
			    sessionContext, patient, hf, encounter, visit, createVisit, returnUrl,
			    adtService, featureToggles, uiUtils, request);
		} catch (Exception ex) {
			result = SimpleObject.create("error", ex.getMessage());
		}

		return result;
	}
}
