/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Identities;

import java.net.URI;

import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnRetrieveAccessTokenCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;

/**
 * Client Identity class to represent the hawaii ADM authentication
 */
public class AdmAuthClientIdentity extends ClientIdentity {

	/**
	 * token service object to retrieve ADM access token
	 */
	private AdmTokenService tokenService;

	/**
	 * ADM client Id
	 */
	private String clientId;

	/**
	 * Gets the service scope for target service
	 * 
	 * @param baseUriString
	 *            base uri of target service
	 * @return String
	 */
	public static String getServiceScope(URI baseUri) {
		Utility.assertNotNull("baseUri", baseUri);

		return String.format("%s://%s", baseUri.getScheme(), baseUri.getHost());
	}

	/**
	 * Initializes an instance of the {@link AdmAuthClientIdentity} class
	 * 
	 * @param clientId
	 *            ADM client ID
	 * @param clientSecret
	 *            ADM client secret
	 * @param serviceScope
	 *            service scope for ADM
	 * @throws HawaiiException
	 */
	public AdmAuthClientIdentity(String clientId, String clientSecret,
			String serviceScope) throws HawaiiException {
		this(clientId, clientSecret, serviceScope, null, null);
	}

	/**
	 * Initializes an instance of the {@link AdmAuthClientIdentity} class
	 * 
	 * @param clientId
	 *            ADM client ID
	 * @param clientSecret
	 *            ADM client secret
	 * @param serviceScope
	 *            service scope for ADM
	 * @param registrationId
	 *            registration ID
	 * @param secretKey
	 *            secret key
	 * @throws HawaiiException
	 */
	public AdmAuthClientIdentity(String clientId, String clientSecret,
			String serviceScope, String registrationId, String secretKey)
			throws HawaiiException {
		super(registrationId, secretKey);

		this.clientId = clientId;
		this.tokenService = AdmTokenService.getTokenServiceInstance(clientId,
				clientSecret, serviceScope);
		this.tokenService
				.setCompleteCallback(new AdmRetrieveTokenCompleteCallback());
	}

	private class AdmRetrieveTokenCompleteCallback implements
			OnRetrieveAccessTokenCompleteListener {
		public void done(String token, Exception ex) {
			AdmAuthClientIdentity identity = AdmAuthClientIdentity.this;
			if (ex == null && !Utility.isStringNullOrEmpty(token)) {
				String registrationId = identity.getRegistrationId();
				String secretKey = identity.getSecretKey();
				if (!Utility.isStringNullOrEmpty(registrationId)
						&& !Utility.isStringNullOrEmpty(secretKey)) {
					token = String.format("BEARER %s %s %s", token, secretKey,
							registrationId);
				} else {
					token = String.format("BEARER %s", token);
				}
			}

			identity.onRetriveAccessTokenComplete(token, ex);
		}
	}

	/**
	 * Gets the client Id
	 * 
	 * @return String
	 */
	public String getClientId() {
		return this.clientId;
	}

	/**
	 * Retrieve the access token for hawaii ADM authentication
	 */
	@Override
	public void retrieveAccessToken() {
		this.tokenService.getAccessToken();
	}
}
