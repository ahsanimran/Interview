package com.interset.DataIntegrationTask.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

	private static final String DATE_TIME_FORMAT = "mm/dd/yy hh:mm:ssa";
	
	/**
	 * This method reads timestamp and timeOffset and provides ISO8610 format string as output.
	 * 
	 * @param timestamp : timestamp field of Json file.
	 * @param timeOffset : timeOffset field of Json file.
	 * @return String : ISO8610 format output string.
	 * @throws ParseException : If any parsing exception occurs.
	 */
	public static String getZonedTime(String timestamp, String timeOffset) throws ParseException  {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

		Date date = simpleDateFormat.parse(timestamp);

		DateFormat df ;
		if(timeOffset==null) {
			df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
		} else {
			df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS"+"'"+timeOffset+"'");
		}

		String nowAsISO = df.format(date);
		return nowAsISO;
	}

	/**
	 * Get date object from timestamp. Its not adjusted according to timezone.
	 * @param timestamp : timestamp field of json file.
	 * @return Date : for given timestamp.
	 * @throws ParseException : If any parsing exception occurs.
	 */
	public static Date getDate(String timestamp) throws ParseException {
		return new SimpleDateFormat(DATE_TIME_FORMAT).parse(timestamp);
	}

}
