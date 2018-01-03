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
package org.openmrs.module.patientlist.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.api.model.PatientInformationField;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dummy visit used for live previewing.
 */
public class DummyVisit extends Visit {

	private DummyVisit() {}

	public static DummyVisit getInstance() {
		return DummyVisit.Holder.INSTANCE;
	}

	private DummyVisit loadData() {
		DummyVisit dummyVisit = new DummyVisit();

		// set id
		dummyVisit.setId(1);

		dummyVisit.setVisitId(1);

		// set visit type
		VisitType visitType = new VisitType();
		visitType.setId(1);
		visitType.setName("Outpatient");
		dummyVisit.setVisitType(visitType);

		// set location
		Location location = new Location();
		location.setId(1);
		location.setName("OpenHMIS offices");
		dummyVisit.setLocation(location);

		// set startdatetime
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		//cal.add(Calendar.MONTH, -2);
		dummyVisit.setStartDatetime(cal.getTime());

		// set enddatetime -- if required

		// set visit attributes
		Map<String, IPatientInformationField<?, ?>> fields =
		        PatientInformation.getInstance().getFields();

		int count = 0;
		List<VisitAttributeType> types = Context.getVisitService().getAllVisitAttributeTypes();
		for (Map.Entry<String, IPatientInformationField<?, ?>> field : fields.entrySet()) {
			if (StringUtils.contains(field.getKey(), "v.attr")) {
				VisitAttribute visitAttribute = new VisitAttribute();
				visitAttribute.setVisitAttributeId(count++);
				visitAttribute.setVisit(dummyVisit);
				visitAttribute.setValue("TEST " + field.getValue().getName().toUpperCase());

				for (VisitAttributeType type : types) {
					if (type.getName().equalsIgnoreCase(field.getValue().getName())) {
						visitAttribute.setAttributeType(type);
					}
				}

				dummyVisit.setAttribute(visitAttribute);
			}
		}

		return dummyVisit;
	}

	private static class Holder {
		private static final DummyVisit INSTANCE = new DummyVisit().loadData();
	}
}
