/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;

/**
 * Uri Utility class
 */
public final class UriUtility {

	/**
	 * encode specified uri string
	 * 
	 * @param string
	 *            specified uri string
	 * @return
	 * @throws HawaiiException
	 *             String
	 */
	public static String safeEncode(String string) throws HawaiiException {
		if (string == null || string.length() == 0) {
			return string;
		}

		String stringAsUtf8;
		try {
			stringAsUtf8 = URLEncoder.encode(string, "UTF-8");
			if (string.contains(" ")) {
				StringBuilder stringbuilder = new StringBuilder();
				int i = 0;
				for (int j = 0; j < string.length(); j++) {
					if (string.charAt(j) != ' ') {
						continue;
					}

					if (j > i) {
						stringbuilder.append(URLEncoder.encode(
								string.substring(i, j), "UTF-8"));
					}

					stringbuilder.append("%20");
					i = j + 1;
				}

				if (i != string.length()) {
					stringbuilder.append(URLEncoder.encode(
							string.substring(i, string.length()), "UTF-8"));
				}

				return stringbuilder.toString();
			}
		} catch (UnsupportedEncodingException ex) {
			throw new HawaiiException("failed to url encode specified string",
					ex);
		}

		return stringAsUtf8;
	}

	/**
	 * decode encoded uri string representation
	 * 
	 * @param encodedString
	 *            encoded uri string representation
	 * @return
	 * @throws HawaiiException
	 *             String
	 */
	public static String safeDecode(String encodedString)
			throws HawaiiException {
		if (encodedString == null || encodedString.length() == 0) {
			return encodedString;
		}

		try {
			if (encodedString.contains("+")) {
				StringBuilder stringBuilder = new StringBuilder();
				int index = 0;
				for (int j = 0; j < encodedString.length(); j++) {
					if (encodedString.charAt(j) != '+') {
						continue;
					}

					if (j > index) {
						stringBuilder.append(URLDecoder.decode(
								encodedString.substring(index, j), "UTF-8"));
					}

					stringBuilder.append("+");
					index = j + 1;
				}

				if (index != encodedString.length()) {
					stringBuilder.append(URLDecoder.decode(
							encodedString.substring(index,
									encodedString.length()), "UTF-8"));
				}

				return stringBuilder.toString();
			}
		} catch (UnsupportedEncodingException ex) {
			throw new HawaiiException("failed to url decode specified string",
					ex);
		}

		try {
			return URLDecoder.decode(encodedString, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new HawaiiException("failed to url decode specified string",
					ex);
		}
	}

	public static String safeRelativize(URI baseUri, URI uriToRelativize)
			throws URISyntaxException {
		if (!baseUri.getHost().equals(uriToRelativize.getHost())
				|| !baseUri.getScheme().equals(uriToRelativize.getScheme())) {
			return uriToRelativize.toString();
		}

		String baseUriPath = baseUri.getPath();
		String uriToRelativizePath = uriToRelativize.getPath();

		int i = 1;
		int baseUriPathIndex = 0;
		int directoriesCount = 0;

		for (; baseUriPathIndex < baseUriPath.length(); baseUriPathIndex++) {
			if (baseUriPathIndex >= uriToRelativizePath.length()) {
				if (baseUriPath.charAt(baseUriPathIndex) == '/')
					directoriesCount++;
				continue;
			}
			if (baseUriPath.charAt(baseUriPathIndex) != uriToRelativizePath
					.charAt(baseUriPathIndex))
				break;
			if (baseUriPath.charAt(baseUriPathIndex) == '/')
				i = baseUriPathIndex + 1;
		}

		if (baseUriPathIndex == uriToRelativizePath.length())
			return (new URI(null, null, null, uriToRelativize.getQuery(),
					uriToRelativize.getFragment())).toString();

		uriToRelativizePath = uriToRelativizePath.substring(i);
		StringBuilder stringbuilder = new StringBuilder();

		for (; directoriesCount > 0; directoriesCount--) {
			stringbuilder.append("../");
		}

		if (!Utility.isStringNullOrEmpty(uriToRelativizePath)) {
			stringbuilder.append(uriToRelativizePath);
		}

		if (!Utility.isStringNullOrEmpty(uriToRelativize.getQuery())) {
			stringbuilder.append("?");
			stringbuilder.append(uriToRelativize.getQuery());
		}

		if (!Utility.isStringNullOrEmpty(uriToRelativize.getFragment())) {
			stringbuilder.append("#");
			stringbuilder.append(uriToRelativize.getRawFragment());
		}

		return stringbuilder.toString();
	}
}
