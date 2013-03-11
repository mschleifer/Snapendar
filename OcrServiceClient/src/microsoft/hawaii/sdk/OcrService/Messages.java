/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sdk.OcrService;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is the Messages Class to help get final string from message properties
 */
public class Messages {

	/**
	 * Bundle file name
	 */
	private static final String BUNDLE_NAME = "microsoft.hawaii.sdk.OcrService.messages"; //$NON-NLS-1$

	/**
	 * Bundle resource
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * 
	 * Initializes an instance of the {@link Messages} class
	 */
	private Messages() {
	}

	/**
	 * 
	 * Gets string value from bundle resource with parameter key
	 * 
	 * @param key
	 *            key name
	 * @return String Value of key
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
