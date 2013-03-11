/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Identities;

import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnRetrieveAccessTokenCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;

/**
 * Represents base client identity for the purposes of communicating with the
 * server
 */
public abstract class ClientIdentity {

	/**
	 * The registration id
	 */
	private String registrationId;

	/**
	 * The secret key
	 */
	private String secretKey;

	/**
	 * callback for completion of retrieving access token
	 */
	private OnRetrieveAccessTokenCompleteListener completeCallback;

	/**
	 * Initializes an instance of the {@link ClientIdentity} class
	 */
	public ClientIdentity() {
		this(null, null);
	}

	/**
	 * Initializes an instance of the {@link ClientIdentity} class
	 * 
	 * @param registrationId
	 * @param secretKey
	 */
	public ClientIdentity(String registrationId, String secretKey) {
		this.setRegistrationId(registrationId);
		this.setSecretKey(secretKey);
	}

	/**
	 * Gets the registration Id
	 */
	public String getRegistrationId() {
		return this.registrationId;
	}

	/**
	 * Sets the registration Id
	 * 
	 * @param registrationId
	 *            registration Id void
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the secret key
	 */
	public String getSecretKey() {
		return this.secretKey;
	}

	/**
	 * Sets the secret key
	 * 
	 * @param secretKey
	 *            the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * Sets callback for completion of retrieving access token
	 * 
	 * @param listener
	 *            void
	 */
	public void setCompleteListener(
			OnRetrieveAccessTokenCompleteListener listener) {
		if (listener != null) {
			this.completeCallback = listener;
		}
	}

	/**
	 * retrieve access token void
	 */
	public void retrieveAccessToken() {
		onRetriveAccessTokenComplete(null, null);
	}

	/**
	 * fire completion callback of retrieval of access token
	 * 
	 * @param token
	 *            access token
	 * @param ex
	 *            void
	 */
	protected void onRetriveAccessTokenComplete(String token, Exception ex) {
		if (this.completeCallback != null) {
			this.completeCallback.done(token, ex);
		}
	}

	/**
	 * create {@link ClientIdentity} derived object
	 * 
	 * @param clientId
	 *            client ID
	 * @param clientSecret
	 *            client secret
	 * @param serviceScope
	 *            service scope
	 * @return
	 * @throws HawaiiException
	 *             ClientIdentity
	 */
	public static ClientIdentity createClientIdentity(String clientId,
			String clientSecret, String serviceScope) throws HawaiiException {
		Utility.assertStringNotNullOrEmpty("clientId can not be null or empty",
				clientId);
		Utility.assertStringNotNullOrEmpty(
				"clientSecret can not be null or empty", clientSecret);
		Utility.assertStringNotNullOrEmpty(
				"serviceScope can not be null or empty", serviceScope);

		return new AdmAuthClientIdentity(clientId, clientSecret, serviceScope);
	}

	/**
	 * create {@link ClientIdentity} derived object
	 * 
	 * @param appId
	 *            application ID
	 * @return
	 * @throws HawaiiException
	 *             ClientIdentity
	 */
	public static ClientIdentity createClientIdentity(String appId)
			throws HawaiiException {
		Utility.assertStringNotNullOrEmpty(
				"application ID can not be null or empty", appId);

		return new GuidAuthClientIdentity(appId);
	}
}