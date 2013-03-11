/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sampleapp.ocrservice;

import microsoft.hawaii.sampleappbase.HawaiiBaseApplication;

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
