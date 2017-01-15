// Created by pranay on 09/01/17.

package com.moneymanager.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.moneymanager.Common.DATE_FORMAT;

/**
 * Utility class to handle all Calendar related functions
 */
public class MyCalendar {

	public static Date dateToday() {
		return new Date();
	}

	public static String dateTodayString() {
		int lastInt = dateToday().getDate() % 10;
		switch (lastInt) {
			case 1:
				return dateToday().getDate() + "st";
			case 2:
				return dateToday().getDate() + "nd";
			case 3:
				return dateToday().getDate() + "rd";
			default:
				return dateToday().getDate() + "th";
		}

	}

	public static String dateToString(Date date) {
		final int lastInt = date.getDate() % 10;
		int secondLastInt = (date.getDate() - lastInt) / 10;
		if (secondLastInt == 1) {
			return date.getDate() + "th";
		} else {
			switch (lastInt) {
				case 1:
					return date.getDate() + "st";
				case 2:
					return date.getDate() + "nd";
				case 3:
					return date.getDate() + "rd";
				default:
					return date.getDate() + "th";
			}
		}
	}

	public static Date dateBeforeDays(int noofDays) {
		final Date dateToday = dateToday();

		int date = dateToday.getDate();
		final Date newDate = new Date(dateToday.getYear(), dateToday.getMonth(), (-noofDays));
		return new Date(newDate.getYear(), newDate.getMonth(), newDate.getDate() + date);
	}

	public static String thisMonthString() {
		return String.format("%tb", dateToday());
	}

	public static String monthToString(Date date) {
		return String.format("%tb", date);
	}

	public static String monthToFullString(Date date) {
		return String.format("%tB", date);
	}

	public static int prevMonth() {
		int prevMonth = dateToday().getMonth() - 1;
		if (prevMonth < 0) {
			prevMonth = 11;
		}
		return prevMonth;

	}

	public static int nextMonth() {
		int nxtMon = dateToday().getMonth() + 1;
		if (nxtMon > 11) {
			nxtMon = 0;
		}
		return nxtMon;
	}

	public static int thisYear() {
		return new Date().getYear();
	}

	public static String yearToString(Date date) {
		return String.format("%tY", date);
	}

	public static String getNiceFormatedCompleteDateString(Date date) {
		final String dateStr = MyCalendar.dateToString(date);
		final String monthStr = MyCalendar.monthToString(date);
		final String yearStr = MyCalendar.yearToString(date);
		return dateStr + " " + monthStr + " " + yearStr;
	}

	public static SimpleDateFormat getSimpleDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
	}

}
