/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Identities;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import microsoft.hawaii.hawaiiClientLibraryBase.HttpMethod;
import microsoft.hawaii.hawaiiClientLibraryBase.Messages;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiNetworkException;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnRetrieveAccessTokenCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.HttpUtility;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Json;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.UriUtility;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;

import org.apache.http.HttpStatus;

/**
 * Used for manage ADM access token
 */
public class AdmTokenService {

	/**
	 * endpoint URL for ADM token service
	 */
	private static URL ADM_TokenService_URL;

	/**
	 * static code snippet to initialize the base URI field
	 */
	static {
		try {
			ADM_TokenService_URL = new URL(
					Messages.getString("AdmTokenService.URL"));
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Invalid URL for ADM token service", ex);
		}
	}

	/**
	 * ADM token expiration time in minute
	 */
	private final static int TOKEN_EXPIRE_IN_MINUTE = 40;

	/**
	 * Network latency
	 */
	private final static int NETWORK_LATENCY_IN_SECOND = 5;

	/**
	 * lock object when to renew access token
	 */
	private static byte[] s_lockRenewToken = new byte[0];

	/**
	 * lock object for operations on internal hashmap of {@link AdmTokenService}
	 * instance
	 */
	private static byte[] s_lockMap = new byte[0];

	/**
	 * ADM access token object
	 */
	private AdmAccessToken accessToken;

	/**
	 * query strings of ADM service
	 */
	private String admServiceQueryStrings;

	/**
	 * completion callback for retrieving access token
	 */
	private OnRetrieveAccessTokenCompleteListener completeCallback;

	/**
	 * Internal hashmap of {@link AdmTokenService} instance whose key is related
	 * to client id, client secret and service scope
	 */
	private static HashMap<String, AdmTokenService> tokenServiceInstanceMap = new HashMap<String, AdmTokenService>(
			1);

	/**
	 * generate key for specified client ID, client secret and service scope
	 * 
	 * @param clientId
	 *            client id
	 * @param clientSecret
	 *            client secret
	 * @param serviceScope
	 *            service scope
	 * @return String
	 */
	private static String generateKey(String clientId, String clientSecret,
			String serviceScope) {
		return String
				.format("%s||%s||%s", clientId, clientSecret, serviceScope); //$NON-NLS-1$
	}

	/**
	 * Gets the {@link AdmTokenService} instance based on specified client ID,
	 * client secret and service scope
	 * 
	 * @param clientId
	 *            client id
	 * @param clientSecret
	 *            client secret
	 * @param serviceScope
	 *            service scope
	 * @return
	 * @throws HawaiiException
	 *             AdmTokenService
	 */
	public static AdmTokenService getTokenServiceInstance(String clientId,
			String clientSecret, String serviceScope) throws HawaiiException {
		String key = generateKey(clientId, clientSecret, serviceScope);
		AdmTokenService instance = null;
		synchronized (s_lockMap) {
			instance = tokenServiceInstanceMap.get(key);
			if (instance != null) {
				return instance;
			}

			instance = new AdmTokenService(clientId, clientSecret, serviceScope);
			tokenServiceInstanceMap.put(key, instance);
		}

		return instance;
	}

	/**
	 * Initializes an instance of the {@link AdmTokenService} class
	 * 
	 * @throws HawaiiException
	 */
	private AdmTokenService(String clientId, String clientSecret,
			String serviceScope) throws HawaiiException {
		this.admServiceQueryStrings = String
				.format("grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s", //$NON-NLS-1$
						UriUtility.safeEncode(clientId),
						UriUtility.safeEncode(clientSecret),
						UriUtility.safeEncode(serviceScope));

	}

	/**
	 * Checks whether need to renew token
	 * 
	 * @return boolean
	 */
	private boolean needToRenewToken() {
		if (this.accessToken == null) {
			return true;
		}

		return this.accessToken.getExpiresAtUtc().before(
				Utility.AddDate(new Date(), Calendar.SECOND,
						NETWORK_LATENCY_IN_SECOND));
	}

	/**
	 * Gets ADM access token
	 */
	public void getAccessToken() {
		if (this.needToRenewToken()) {
			synchronized (s_lockRenewToken) {
				if (this.needToRenewToken()) {
					this.retrieveAccessToken();
				} else {
					this.fireCompleteCallback(
							this.accessToken.getAccessToken(), null);
				}
			}
		} else {
			this.fireCompleteCallback(this.accessToken.getAccessToken(), null);
		}
	}

	/**
	 * retrieve ADM access token via HTTPS
	 */
	private void retrieveAccessToken() {
		HttpURLConnection request = null;
		try {
			request = HttpUtility.constructHttpConnection(ADM_TokenService_URL,
					HttpMethod.POST, this.admServiceQueryStrings.getBytes(),
					"application/x-www-form-urlencoded"); //$NON-NLS-1$

			// send HTTP request and get response
			int statusCode = request.getResponseCode();

			// parse response
			InputStream inStream = null;
			try {
				if (statusCode == HttpStatus.SC_OK) {
					inStream = request.getInputStream();
					this.accessToken = Json.deserializeFromStream(
							AdmAccessToken.class, inStream);
					this.accessToken.setExpiresAtUtc(Utility
							.AddDate(new Date(), Calendar.MINUTE,
									TOKEN_EXPIRE_IN_MINUTE));

					fireCompleteCallback(this.accessToken.getAccessToken(),
							null);
				} else {
					fireCompleteCallback(null,
							HawaiiNetworkException.translateFromHttpStatus(
									statusCode,
									"failed to get ADM access token", null)); //$NON-NLS-1$
				}
			} finally {
				if (inStream != null) {
					inStream.close();
				}
			}
		} catch (Exception ex) {
			fireCompleteCallback(null, ex);
		} finally {
			if (request != null) {
				request.disconnect();
			}
		}
	}

	/**
	 * fire CompleteCallback
	 * 
	 * @param accessToken
	 *            specified access token
	 * @param ex
	 *            {@link Exception} object void
	 */
	private void fireCompleteCallback(String accessToken, Exception ex) {
		if (completeCallback != null) {
			completeCallback.done(accessToken, ex);
		}
	}

	/**
	 * Sets the completion callback for retrieving access token
	 * 
	 * @param callback
	 *            completion callback to set void
	 */
	public void setCompleteCallback(
			OnRetrieveAccessTokenCompleteListener callback) {
		this.completeCallback = callback;
	}
}
