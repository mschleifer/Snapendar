/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import microsoft.hawaii.hawaiiClientLibraryBase.DataContract.ServiceFault;
import microsoft.hawaii.hawaiiClientLibraryBase.DataContract.ServiceResult;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiInvalidOperationException;
import microsoft.hawaii.hawaiiClientLibraryBase.Identities.ClientIdentity;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnRetrieveAccessTokenCompleteListener;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.HttpUtility;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Json;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Base class for all Hawaii service agent classes. These agents are wrapping
 * the communication tasks specific to each service. ServiceAgent provides
 * functionality common to all these clases.
 * 
 * @param <T>
 *            Generic service result type which inherits {@link ServiceResult}
 *            type
 */
public abstract class ServiceAgent<T extends ServiceResult> {
	/*
	 * Notice: Here we use protected instance field to improve performance for
	 * Android
	 */

	/**
	 * HttpMethod enum
	 */
	protected HttpMethod httpMethod;

	/**
	 * service result
	 */
	protected T serviceResult;

	/**
	 * Http status code
	 */
	private int statusCode = -1;

	/**
	 * class object of service result
	 */
	protected Class<T> serviceResultClass;

	/**
	 * service uri
	 */
	protected URI serviceUri;

	/**
	 * client identity object for authentication
	 */
	protected ClientIdentity clientIdentity;

	/**
	 * completion callback for service operation
	 */
	protected OnCompleteListener<T> onCompleteListener;

	/**
	 * Initializes an instance of the {@link ServiceAgent} class
	 * 
	 * @param objClass
	 *            The class object for the generic type
	 * @throws Exception
	 */
	public ServiceAgent(Class<T> objClass) throws Exception {
		this(objClass, HttpMethod.GET, null);
	}

	/**
	 * Initializes an instance of the {@link ServiceAgent} class
	 * 
	 * @param objClass
	 *            The class object for the generic service result type
	 * @param httpMethod
	 *            HttpMethod enum
	 * @param state
	 *            user state object
	 * @throws Exception
	 */
	public ServiceAgent(Class<T> objClass, HttpMethod httpMethod, Object state)
			throws Exception {
		this.httpMethod = httpMethod;
		try {
			this.serviceResultClass = objClass;
			this.serviceResult = objClass.newInstance();
		} catch (Exception ex) {
			throw new HawaiiInvalidOperationException(
					"Failed to create ServiceResult object", ex);
		}

		this.serviceResult.setStateObject(state);
	}

	/**
	 * Gets service result
	 * 
	 * @return T
	 */
	public T getServiceResult() {
		return this.serviceResult;
	}

	/**
	 * Sets the service result object
	 * 
	 * @param result
	 *            void
	 */
	protected void setServiceResult(T result) {
		if (result != null) {
			this.serviceResult = result;
			this.serviceResult.setStatus(Status.Success);
		}
	}

	/**
	 * Gets the required content type used by Content-Type HTTP header. This
	 * method can be overridden by sub classes to use different content type
	 * header
	 * 
	 * @return String content type
	 */
	public String getContentType() {
		return "application/json";
	}

	/**
	 * gets the flag to indicate whether service result are required to be
	 * parsed. This method can be overridden by sub classes to change to not
	 * required
	 * 
	 * @return boolean
	 */
	protected boolean parseServiceResultRequired() {
		return true;
	}

	/**
	 * Gets the byte array for HTTP request body. This method can be overridden
	 * by sub classes to generate their specific request body if necessary
	 * 
	 * @return
	 * @throws HawaiiException
	 *             byte[]
	 */
	protected byte[] getRequestBodyData() throws HawaiiException {
		return null;
	}

	/**
	 * Parses the response from server. This method must be overridden by sub
	 * classes to do their own processing logic
	 * 
	 * @param inputStream
	 * @throws HawaiiException
	 *             void
	 */
	protected void parseOutput(InputStream inputStream) {
		Utility.assertNotNull("input stream can't be null", inputStream);
		try {
			if (this.serviceResult.getStatus() == Status.Success) {
				if (this.parseServiceResultRequired()) {
					this.setServiceResult(Json.deserializeFromStream(
							this.serviceResultClass, inputStream));
				}
			} else {
				ServiceFault fault = Json.deserializeFromStream(
						ServiceFault.class, inputStream);
				if (fault != null) {
					this.handleException(fault.getMessage(), null);
				}
			}
		} catch (Exception ex) {
			this.handleException(
					"Failed to parse service result object from response", ex);
		}
	}

	/**
	 * Send HTTP request and process response
	 * 
	 * @param callback
	 *            callback method after response has been processed
	 */
	public void processRequest(OnCompleteListener<T> callback) {
		this.onCompleteListener = callback;

		try {
			if (this.clientIdentity == null) {
				this.handleException(
						"failed to specify client identity before sending request",
						null);
			}

			this.clientIdentity
					.setCompleteListener(new OnRetrieveAccessTokenCompleteListener() {
						public void done(String token, Exception ex) {
							if (ex == null) {
								try {
									HttpRequestBase request = constructHttpRequest(token);
									executeRequest(request);
								} catch (Exception e) {
									handleException(e);
								}
							} else {
								handleException(ex);
							}
						}
					});
			this.clientIdentity.retrieveAccessToken();
		} catch (Exception ex) {
			this.handleException(ex);
		} finally {
			OnCompleteRequest();
		}
	}

	/**
	 * construct http request object
	 * 
	 * @param token
	 *            access token
	 * @return
	 * @throws HawaiiException
	 *             HttpRequestBase
	 */
	protected HttpRequestBase constructHttpRequest(String token)
			throws HawaiiException {
		HttpRequestBase request = HttpUtility.constructHttpRequest(
				this.serviceUri, this.httpMethod, this.getRequestBodyData(),
				this.getContentType());

		if (!Utility.isStringNullOrEmpty(token)) {
			request.setHeader("Authorization", token);
		}

		return request;
	}

	private static void setupDebuggingProxy(HttpParams params) {
		params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
				"127.0.0.1", 8888));
	}

	/**
	 * Sends request and process response from server
	 * 
	 * @param request
	 *            {@link HttpURLConnection} object void
	 */
	private void executeRequest(HttpUriRequest request) throws Exception {
		HttpClient client = new DefaultHttpClient();

		HttpParams params = client.getParams();
		// Setting 30 second timeouts
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);

		if (ConditionalCompilation.DEBUG) {
			// set up proxy for debugging
			setupDebuggingProxy(params);
		}

		InputStream instream = null;
		try {
			HttpResponse httpResponse = client.execute(request);
			this.statusCode = httpResponse.getStatusLine().getStatusCode();
			this.serviceResult.setStatus(convertToStatus(this.statusCode));
			// get response entity
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				instream = entity.getContent();
				// Calls client method to parse result buffer.
				this.parseOutput(instream);
			}
		} catch (IOException e) {
			this.handleException("Failed to execute request", e);
		} finally {
			// Closing the input stream will trigger connection release
			if (instream != null) {
				instream.close();
			}

			client.getConnectionManager().shutdown();
		}
	}

	/**
	 * handle specified exception message and {@link Throwable} object
	 * 
	 * @param errorMessage
	 *            specified error message
	 * @param cause
	 *            inner {@link Throwable} object
	 */
	protected void handleException(String errorMessage, Throwable cause) {
		if (Utility.isStringNullOrEmpty(errorMessage)) {
			return;
		}

		if (this.serviceResult.getStatus() != Status.Success
				&& this.statusCode > 0) {
			errorMessage = String.format("HttpStatusCode: %d\r\n%s",
					this.statusCode, errorMessage);
		}

		this.serviceResult.setStatus(Status.Error);
		this.serviceResult
				.setException(new HawaiiException(errorMessage, cause));
	}

	/**
	 * handle unexpected exception
	 * 
	 * @param ex
	 *            unexpected exception
	 */
	protected void handleException(Exception ex) {
		if (ex == null) {
			return;
		}

		if (this.serviceResult.getStatus() != Status.Success
				&& this.statusCode > 0) {
			ex = new HawaiiException(String.format("%d: %s", this.statusCode,
					ex.getMessage()), ex);
		}

		this.serviceResult.setStatus(Status.Error);
		this.serviceResult.setException(ex);
	}

	/**
	 * Converts HTTP Status code to {@link Status} enum
	 * 
	 * @param statusCode
	 *            HTTP Status code
	 * @return Status
	 */
	private static Status convertToStatus(int statusCode) {
		switch (statusCode) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_CREATED:
			return Status.Success;

		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			return Status.InternalServerError;

		default:
			return Status.Error;
		}
	}

	/**
	 * A method can be overridden by sub classes. It is invoked after completing
	 * the service request. The implementation of this base class will invoke
	 * the client's "on complete" callback method. Classes that inherit from
	 * ServiceAgent can overwrite this method to further process the service
	 * call result before calling the client's "on complete" callback method.
	 * OnCompleteRequest void
	 */
	protected void OnCompleteRequest() {
		if (this.onCompleteListener != null) {
			this.onCompleteListener.done(this.serviceResult);
		}
	}
}