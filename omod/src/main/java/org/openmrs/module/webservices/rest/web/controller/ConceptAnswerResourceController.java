package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

/**
 * REST controller for returning concept name
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LIST_CONCEPT_ANSWER_RESOURCE)
public class ConceptAnswerResourceController {

	private final Log LOG = LogFactory.getLog(this.getClass());

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(@RequestParam(value = "conceptUuid", required = false) String conceptUuid) {
		SimpleObject results = new SimpleObject();
		Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
		if (concept != null) {
			List<SimpleObject> values = new LinkedList<SimpleObject>();
			for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
				SimpleObject result = new SimpleObject();
				result.put("uuid", conceptAnswer.getUuid());
				result.put("display", conceptAnswer.getAnswerConcept().getName().getName());
				values.add(result);
			}

			results.put("results", values);
		}

		return results;
	}
}
