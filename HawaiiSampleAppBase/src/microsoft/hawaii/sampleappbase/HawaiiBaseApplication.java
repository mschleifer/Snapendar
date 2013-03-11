/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sampleappbase;

import microsoft.hawaii.hawaiiClientLibraryBase.Identities.ClientIdentity;
import android.app.Application;

/**
 * Base application class for Hawaii sample applications
 */
public class HawaiiBaseApplication extends Application {

	/**
	 * Hawaii authentication type
	 */
	private AuthenticationType authType;

	/**
	 * ClientIdentity object
	 */
	private ClientIdentity identity;

	/**
	 * Enum class to represent Hawaii authentication type
	 */
	public enum AuthenticationType {
		GUID, ADM, NOVALUE;

		/**
		 * Convert to authentication type enum for specified String
		 * 
		 * @param str
		 *            specified String
		 * @return AuthenticationType
		 */
		public static AuthenticationType convertToAuthType(String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}

	/**
	 * Gets current authentication type
	 * 
	 * @return AuthenticationType
	 */
	public AuthenticationType getAuthType() {
		if (authType == null) {
			authType = AuthenticationType
					.convertToAuthType(getString(R.string.hawaii_authentication_type));
		}

		return authType;
	}

	/**
	 * Gets current ClientIdentity object
	 * 
	 * @return ClientIdentity
	 */
	public ClientIdentity getClientIdentity() {
		return this.identity;
	}

	/**
	 * Sets current ClientIdentity object
	 * 
	 * @param identity
	 *            current ClientIdentity object
	 */
	public void setClientIdentity(ClientIdentity identity) {
		this.identity = identity;
	}

}
