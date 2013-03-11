/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class
 */
public final class Utility {
	/**
	 * An empty <code>String</code> to use for comparison.
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * Stores a reference to the RFC1123 date/time pattern.
	 */
	public static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

	/**
	 * Stores a reference to the ISO8061 date/time pattern.
	 */
	public static final String ISO8061_PATTERN_NO_SECONDS = "yyyy-MM-dd'T'HH:mm'Z'";

	/**
	 * Stores a reference to the ISO8061 date/time pattern.
	 */
	public static final String ISO8061_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * Stores a reference to the ISO8061_LONG date/time pattern.
	 */
	public static final String ISO8061_LONG_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

	/**
	 * Assert specified object is not null
	 * 
	 * @param description
	 *            error message if the specified object is null
	 * @param object
	 *            specified object void
	 */
	public static void assertNotNull(final String description,
			final Object object) {
		if (object == null) {
			throw new IllegalArgumentException(description);
		}
	}

	/**
	 * Assert specified string is not null or empty
	 * 
	 * @param description
	 *            error message if the specified string is null or empty
	 * @param string
	 *            the specified string void
	 */
	public static void assertStringNotNullOrEmpty(final String description,
			final String string) {
		assertNotNull(description, string);
		if (string.length() == 0) {
			throw new IllegalArgumentException(
					"The argument must not be an empty string or null:"
							.concat(description));
		}
	}

	/**
	 * check whether the specified string is null or empty
	 * 
	 * @param string
	 * @return boolean
	 */
	public static boolean isStringNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static String trimEnd(String string, char characterToTrim) {
		int i;
		for (i = string.length() - 1; i > 0
				&& string.charAt(i) == characterToTrim; i--)
			;
		return i != string.length() - 1 ? string.substring(i) : string;
	}

	public static String trimStart(String string) {
		int i;
		for (i = 0; i < string.length() && string.charAt(i) == ' '; i++)
			;
		return string.substring(i);
	}

	public static String readStringFromStream(InputStream inputStream)
			throws UnsupportedEncodingException, IOException {
		Utility.assertNotNull("inputStream", inputStream);
		Writer writer = new StringWriter();
		Reader reader = new BufferedReader(new InputStreamReader(inputStream,
				"UTF-8"));
		char[] buffer = new char[1024];
		int bytesRead;

		while ((bytesRead = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, bytesRead);
		}

		return writer.toString();
	}

	/**
	 * internal Calendar object to performance Date manipulations such as Add
	 */
	private static Calendar s_calendar = Calendar.getInstance();

	/**
	 * Adds the specified amount to a Date field for the specified Date
	 * 
	 * @param date
	 *            the specified original Date object
	 * @param field
	 *            the Date field to modify
	 * @param value
	 *            the amount to add to the field
	 * @return Date
	 */
	public static Date AddDate(Date date, int field, int value) {
		s_calendar.setTime(date);
		s_calendar.add(field, value);
		return s_calendar.getTime();
	}

	/**
	 * Convert specified Date to ISO8601 String
	 * 
	 * @param date
	 *            specified Date
	 * @return String
	 */
	public static String ConvertDateToISO8601String(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(ISO8061_PATTERN);
		return format.format(date);
	}
}
