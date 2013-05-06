/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package cs407.snapendar.main;

import cs407.snapendar.main.HawaiiBaseApplication;

/**
 *
 */
public class OcrApplication extends HawaiiBaseApplication {
	/**
	 * Gets current authentication type
	 * 
	 * @return AuthenticationType
	 */
	@Override
	public AuthenticationType getAuthType() {
		return AuthenticationType.GUID;
	}

}
