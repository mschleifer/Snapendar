/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Listeners;

import java.net.HttpURLConnection;

/**
 * Setter callback for specified {@link HttpURLConnection} object
 */
public interface HttpURLConnectionSetter {
	/**
	 * Add customized operation to the specified {@link HttpURLConnection}
	 * object
	 * 
	 * @param request
	 *            specified {@link HttpURLConnection} object
	 */
	void set(HttpURLConnection request);
}
