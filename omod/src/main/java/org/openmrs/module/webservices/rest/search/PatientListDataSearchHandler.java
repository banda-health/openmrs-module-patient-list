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

package org.openmrs.module.webservices.rest.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListData;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Search handler for {@link PatientList}s.
 */
@Component
public class PatientListDataSearchHandler implements SearchHandler {
	private static final Log LOG = LogFactory.getLog(PatientListDataSearchHandler.class);
	private final SearchConfig searchConfig =
	        new SearchConfig("default", ModuleRestConstants.PATIENT_LIST_DATA_RESOURCE,
	                Arrays.asList("*"),
	                Arrays.asList(
	                        new SearchQuery.Builder("Find patient list data by uuid")
	                                .withOptionalParameters("uuid")
	                                .build()));

	private IPatientListDataService patientListDataService;
	private IPatientListService patientListService;

	@Autowired
	public PatientListDataSearchHandler(IPatientListDataService patientListDataService,
	    IPatientListService patientListService) {
		this.patientListDataService = patientListDataService;
		this.patientListService = patientListService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		String uuid = context.getParameter("uuid");

		if (StringUtils.isEmpty(uuid)) {
			LOG.warn("Provide a uuid");
			return new EmptySearchResult();
		}

		PatientList patientList = patientListService.getByUuid(uuid);
		if (patientList == null) {
			LOG.error("PatientList with uuid '" + uuid + "' does not exist");
			return new EmptySearchResult();
		}

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<PatientListData> patientListData = patientListDataService.getPatientListData(patientList, pagingInfo);
		if (patientListData.size() == 0) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPagedWithLength<PatientListData>(context, patientListData, pagingInfo.hasMoreResults(),
			        pagingInfo.getTotalRecordCount());
		}
	}
}
