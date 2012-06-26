package com.va.strand.biodiversity.model;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class DateModel extends AbstractModel {
	
	private static final String SEPARATOR = "/";

	public DateModel(Context context) {
		TYPE = "Date";
	}

	@Override
	public boolean validate(Object date) {
		if (!(date instanceof String))
			return false;
		if ("".equals(date))
			return true;	// empty date is acceptable
		String[] parts = ((String) date).split(SEPARATOR);
		try {
			int day = Integer.valueOf(parts[0]);
			int month = Integer.valueOf(parts[1]);
			int year = Integer.valueOf(parts[2]);

			int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);

			if (year > currentYear)
				return false;
			if (month > currentMonth)
				return false;
			if (day > currentDay)
				return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Date getDateObject(String date) {
		String[] parts = date.split(SEPARATOR);
		int day = Integer.valueOf(parts[0]);
		int month = Integer.valueOf(parts[1]) - 1;
		int year = Integer.valueOf(parts[2]);
		return new Date(year, month, day);
	}

}
