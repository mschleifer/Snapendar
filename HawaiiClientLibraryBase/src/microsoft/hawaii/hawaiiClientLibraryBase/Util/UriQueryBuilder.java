/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;

/**
 * Builder class to add query string to URI object
 * 
 */
public final class UriQueryBuilder {
	/**
	 * Stores the one to one key/ value collection of query parameters.
	 */
	private final HashMap<String, ArrayList<String>> parameters = new HashMap<String, ArrayList<String>>();

	/**
	 * Adds a value to the URI with escaping.
	 * 
	 * @param name
	 *            the query key name.
	 * @param value
	 *            the query value.
	 * @throws HawaiiException
	 */
	public void add(final String name, final String value)
			throws HawaiiException {
		if (Utility.isStringNullOrEmpty(name)) {
			throw new IllegalArgumentException(
					"Cannot encode a query parameter with a null or empty key");
		}

		this.insertKeyValue(name, value);
	}

	/**
	 * Adds query parameter to an existing Uri. This takes care of any existing
	 * query parameters in the Uri.
	 * 
	 * @param uri
	 *            the original uri.
	 * @return the appended uri
	 * @throws URISyntaxException
	 *             if the resulting uri is invalid.
	 * @throws HawaiiException
	 */
	public URI addToURI(final URI uri) throws URISyntaxException,
			HawaiiException {
		final String origRawQuery = uri.getRawQuery();
		final String rawFragment = uri.getRawFragment();
		final String uriString = uri.resolve(uri).toASCIIString();

		final HashMap<String, String[]> origQueryMap = UriQueryBuilder
				.parseQueryString(origRawQuery);

		// Try/Insert original queries to map

		for (final Entry<String, String[]> entry : origQueryMap.entrySet()) {
			for (final String val : entry.getValue()) {
				this.insertKeyValue(entry.getKey(), val);
			}
		}

		final StringBuilder retBuilder = new StringBuilder();

		// has a fragment
		if (Utility.isStringNullOrEmpty(origRawQuery)
				&& !Utility.isStringNullOrEmpty(rawFragment)) {
			final int bangDex = uriString.indexOf('#');
			retBuilder.append(uriString.substring(0, bangDex));
		} else if (!Utility.isStringNullOrEmpty(origRawQuery)) {
			// has a query
			final int queryDex = uriString.indexOf('?');
			retBuilder.append(uriString.substring(0, queryDex));
		} else {
			// no fragment or query
			retBuilder.append(uriString);
			if (uri.getRawPath().length() <= 0) {
				retBuilder.append("/");
			}
		}

		final String finalQuery = this.toString();

		if (finalQuery.length() > 0) {
			retBuilder.append("?");
			retBuilder.append(finalQuery);
		}

		if (!Utility.isStringNullOrEmpty(rawFragment)) {
			retBuilder.append("#");
			retBuilder.append(rawFragment);
		}

		return new URI(retBuilder.toString());
	}

	/**
	 * Inserts a key / value in the Hashmap, assumes the value is already utf-8
	 * encoded.
	 * 
	 * @param key
	 *            the key to insert
	 * @param value
	 *            the value to insert
	 * @throws HawaiiException
	 */
	private void insertKeyValue(String key, String value)
			throws HawaiiException {
		if (value != null) {
			value = UriUtility.safeEncode(value);
		}
		if (!key.startsWith("$")) {
			key = UriUtility.safeEncode(key);
		}

		ArrayList<String> list = this.parameters.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			list.add(value);
			this.parameters.put(key, list);
		} else {
			if (!list.contains(value)) {
				list.add(value);
			}
		}
	}

	/**
	 * Parses a query string into a one to many hashmap.
	 * 
	 * @param parseString
	 *            the string to parse
	 * @return a HashMap<String, String[]> of the key values.
	 * @throws HawaiiException
	 */
	private static HashMap<String, String[]> parseQueryString(String parseString)
			throws HawaiiException {
		final HashMap<String, String[]> retVals = new HashMap<String, String[]>();
		if (Utility.isStringNullOrEmpty(parseString)) {
			return retVals;
		}

		// 1. Remove ? if present
		final int queryDex = parseString.indexOf("?");
		if (queryDex >= 0 && parseString.length() > 0) {
			parseString = parseString.substring(queryDex + 1);
		}

		// 2. split name value pairs by splitting on the 'c&' character
		final String[] valuePairs = parseString.contains("&") ? parseString
				.split("&") : parseString.split(";");

		// 3. for each field value pair parse into appropriate map entries
		for (int m = 0; m < valuePairs.length; m++) {
			final int equalDex = valuePairs[m].indexOf("=");

			if (equalDex < 0 || equalDex == valuePairs[m].length() - 1) {
				continue;
			}

			String key = valuePairs[m].substring(0, equalDex);
			String value = valuePairs[m].substring(equalDex + 1);

			key = UriUtility.safeDecode(key);
			value = UriUtility.safeDecode(value);

			// 3.1 add to map
			String[] values = retVals.get(key);

			if (values == null) {
				values = new String[] { value };
				if (!value.equals(Utility.EMPTY_STRING)) {
					retVals.put(key, values);
				}
			} else if (!value.equals(Utility.EMPTY_STRING)) {
				final String[] newValues = new String[values.length + 1];
				for (int j = 0; j < values.length; j++) {
					newValues[j] = values[j];
				}

				newValues[newValues.length] = value;
			}
		}

		return retVals;
	}

	/**
	 * Returns a string that represents this instance. This will construct the
	 * full URI.
	 * 
	 * @return a string that represents this instance.
	 */
	@Override
	public String toString() {
		final StringBuilder outString = new StringBuilder();
		Boolean isFirstPair = true;

		for (final String key : this.parameters.keySet()) {
			if (this.parameters.get(key) != null) {
				for (final String val : this.parameters.get(key)) {
					if (isFirstPair) {
						isFirstPair = false;
					} else {
						outString.append("&");
					}

					outString.append(String.format("%s=%s", key, val));
				}
			}
		}

		return outString.toString();
	}
}
