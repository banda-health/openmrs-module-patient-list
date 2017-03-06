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

import org.openmrs.module.patientlist.api.model.PatientListRelativeDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Patient list date operations
 */
public class PatientListDateUtil {

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final int DAYS_IN_WEEK = 7;
	private static final int DAYS_IN_TWO_WEEKS = 14;
	private static final int DAYS_IN_MONTH = 30;
	private static final int DAYS_IN_THREE_MONTHS = 90;
	private static final int DAYS_IN_SIX_MONTHS = 180;
	private static final int DAYS_IN_NINE_MONTHS = 270;
	private static final int DAYS_IN_YEAR = 365;
	private static Date startDate;
	private static Date endDate;
	private static Calendar calendar = Calendar.getInstance();

	/**
	 * Dynamically calculate start and end days for the given time interval
	 * @param relativeDate
	 * @return
	 */
	public static String createRelativeDate(PatientListRelativeDate relativeDate) {
		int days;
		int interval;

		switch (relativeDate) {
			case YESTERDAY:
				calendar.add(Calendar.DATE, -1);
				return dateFormatting(calendar, 1);
			case THIS_WEEK:
				interval = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				calendar.add(Calendar.DATE, -interval);
				return dateFormatting(calendar, interval);
			case LAST_WEEK:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				calendar.add(Calendar.DATE, -days - DAYS_IN_WEEK);
				return dateFormatting(calendar, DAYS_IN_WEEK);
			case LAST_TWO_WEEKS:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				calendar.add(Calendar.DATE, -days - DAYS_IN_TWO_WEEKS);
				return dateFormatting(calendar, DAYS_IN_TWO_WEEKS);
			case THIS_MONTH:
				interval = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DATE, -interval);
				return dateFormatting(calendar, interval);
			case LAST_MONTH:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_MONTH);
				return dateFormatting(calendar, DAYS_IN_MONTH);
			case LAST_THREE_MONTHS:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_THREE_MONTHS);
				return dateFormatting(calendar, DAYS_IN_THREE_MONTHS);
			case LAST_SIX_MONTHS:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_SIX_MONTHS);
				return dateFormatting(calendar, DAYS_IN_SIX_MONTHS);
			case LAST_NINE_MONTHS:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_NINE_MONTHS);
				return dateFormatting(calendar, DAYS_IN_NINE_MONTHS);
			case THIS_YEAR:
				interval = calendar.get(Calendar.DAY_OF_YEAR) - 1;
				calendar.add(Calendar.DATE, -interval);
				return dateFormatting(calendar, interval);
			case LAST_YEAR:
				days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
				calendar.add(Calendar.DAY_OF_YEAR, -days - DAYS_IN_YEAR);
				return dateFormatting(calendar, DAYS_IN_YEAR);
			default:
				throw new IllegalArgumentException("Invalid relative date " + relativeDate);
		}
	}

	private static String dateFormatting(Calendar calendar, int addDays) {
		startDate = calendar.getTime();
		calendar.add(Calendar.DATE, addDays);

		endDate = calendar.getTime();
		return formatDates(startDate, endDate);
	}

	private static String formatDates(Date startDate, Date endDate) {
		return simpleDateFormat.format(startDate) + "|" + simpleDateFormat.format(endDate);
	}

	public static Date formatDate(String date) {
		try {
			return simpleDateFormat.parse(date);
		} catch (ParseException ex) {
			throw new RuntimeException("error parsing date ", ex);
		}
	}
}
