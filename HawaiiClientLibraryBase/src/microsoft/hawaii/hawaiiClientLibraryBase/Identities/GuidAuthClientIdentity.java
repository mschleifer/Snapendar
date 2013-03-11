/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Identities;

import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiInvalidOperationException;
import android.util.Base64;

/**
 * Client Identity class to represent the hawaii Guid authentication
 * 
 */
public class GuidAuthClientIdentity extends ClientIdentity {
	/**
	 * Application Id
	 */
	private String applicationId;

	/**
	 * Gets the Application Id
	 * 
	 * @return String
	 */
	public String getApplicationId() {
		return this.applicationId;
	}

	/**
	 * Initializes an instance of the {@link GuidAuthClientIdentity} class
	 * 
	 * @param applicationId
	 *            application Id
	 */
	public GuidAuthClientIdentity(String applicationId) {
		this(applicationId, null, null);
	}

	/**
	 * Initializes an instance of the {@link GuidAuthClientIdentity} class
	 * 
	 * @param applicationId
	 *            application Id
	 * @param registrationId
	 *            registration Id
	 * @param secretKey
	 *            secret key
	 */
	public GuidAuthClientIdentity(String applicationId, String registrationId,
			String secretKey) {
		super(registrationId, secretKey);
		this.applicationId = applicationId;
	}

	/**
	 * Retrieve the access token for hawaii Guid authentication
	 * 
	 */
	@Override
	public void retrieveAccessToken() {
		String token = null;
		if (this.getRegistrationId() != null && this.getSecretKey() != null) {
			if (this.applicationId != null) {
				token = String.format("%s:%s:%s", this.applicationId,
						this.getRegistrationId(), this.getSecretKey());
			} else {
				token = String.format("%s:%s", this.getRegistrationId(),
						this.getSecretKey());
			}
		} else if (this.applicationId != null) {
			token = this.applicationId;
		}

		if (token != null && token.length() > 1) {
			String encodedToken = String.format("Basic %s",
					Base64.encodeToString(token.getBytes(), Base64.NO_WRAP));
			this.onRetriveAccessTokenComplete(encodedToken.trim(), null);
		} else {
			this.onRetriveAccessTokenComplete(
					null,
					new HawaiiInvalidOperationException(
							"Hawaii Guid authentication token cannot be empty string"));
		}
	}
}
