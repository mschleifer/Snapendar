/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Listeners;

/**
 * callback delegate class for completion of retrieval for access token
 */
public interface OnRetrieveAccessTokenCompleteListener {

	/**
	 * operation completion callback method
	 * 
	 * @param accessToken
	 *            access token
	 * @param ex
	 *            {@link Exception} object
	 */
	void done(String accessToken, Exception ex);
}
