/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Utility class to manipulate Strings
 */
public final class StringUtil {

	/**
	 * perform join operation on specified String collection sperated by
	 * specified delimiter
	 * 
	 * @param s
	 *            specified String collection
	 * @param delimiter
	 *            specified delimiter
	 * @return String
	 */
	public static String join(Collection<String> s, String delimiter) {
		if (s == null || s.isEmpty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = s.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;
			}

			builder.append(delimiter);
		}

		return builder.toString();
	}
}
