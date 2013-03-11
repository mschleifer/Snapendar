/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Identities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent ADM access token
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdmAccessToken {

	/**
	 * ADM access token
	 */
	private String accessToken;

	/**
	 * ADM token type
	 */
	private String tokenType;

	/**
	 * token expiration time
	 */
	private String expiresIn;

	/**
	 * service scope
	 */
	private String Scope;

	/**
	 * token expiration time. This field is not intended to be serialized
	 */
	private Date expiresAtUtc;

	/**
	 * Gets the access token
	 */
	@JsonProperty("access_token")
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the access token
	 * 
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Gets the token type
	 */
	@JsonProperty("token_type")
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Sets the token type
	 * 
	 * @param tokenType
	 *            the tokenType to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * Gets the expiration time
	 */
	@JsonProperty("expires_in")
	public String getExpiresIn() {
		return this.expiresIn;
	}

	/**
	 * Sets the expiration time
	 * 
	 * @param expiresIn
	 *            the expiresIn to set
	 */
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * Gets the scope
	 */
	@JsonProperty("scope")
	public String getScope() {
		return Scope;
	}

	/**
	 * Sets the scope
	 * 
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(String scope) {
		Scope = scope;
	}

	/**
	 * Gets the UTC time when access token expires
	 */
	public Date getExpiresAtUtc() {
		return expiresAtUtc;
	}

	/**
	 * Sets the UTC time when access token expires
	 * 
	 * @param expiresAtUtc
	 *            the expiresAtUtc to set
	 */
	public void setExpiresAtUtc(Date expiresAtUtc) {
		this.expiresAtUtc = expiresAtUtc;
	}

	/**
	 * Initializes an instance of the {@link AdmAccessToken} class
	 */
	public AdmAccessToken() {
	}

}
