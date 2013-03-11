/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import microsoft.hawaii.hawaiiClientLibraryBase.DataContract.ServiceResult;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Identities.ClientIdentity;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Json;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;

/**
 * Base class for all Hawaii service agent classes which need to post data to
 * Hawaii server
 * 
 * @param <TRequest>
 *            Generic request type
 * @param <TResult>
 *            Generic service result type which inherits {@link ServiceResult}
 *            type
 */
public abstract class PostableServiceAgent<TRequest, TResult extends ServiceResult>
		extends ServiceAgent<TResult> {
	/**
	 * request object
	 */
	protected TRequest request;

	/**
	 * Initializes an instance of the {@link PostableServiceAgent} class
	 * 
	 * @param request
	 *            request object
	 * @param resultClass
	 *            The class object for the generic service result type
	 * @param baseUri
	 *            base URI object
	 * @param identity
	 *            client identity
	 * @param httpMethod
	 *            http method enum
	 * @param state
	 *            user defined state
	 * @throws Exception
	 */
	public PostableServiceAgent(TRequest request, Class<TResult> resultClass,
			URI baseUri, ClientIdentity identity, HttpMethod httpMethod,
			Object state) throws Exception {
		super(resultClass, httpMethod, state);

		// parameter validation
		Utility.assertNotNull("request", request);
		Utility.assertNotNull("baseUri", baseUri);
		Utility.assertNotNull("identity", identity);

		this.serviceUri = baseUri;
		this.clientIdentity = identity;
		this.request = request;
	}

	@Override
	protected byte[] getRequestBodyData() throws HawaiiException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			Json.serializeToStream(this.request, outStream);
		} catch (IOException ex) {
			throw new HawaiiException("Failed to prepare request body", ex);
		}

		if (outStream.size() > 0) {
			return outStream.toByteArray();
		} else {
			return null;
		}
	}
}
