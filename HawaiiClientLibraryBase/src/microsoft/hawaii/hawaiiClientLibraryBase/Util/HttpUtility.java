/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import microsoft.hawaii.hawaiiClientLibraryBase.HttpDeleteWithBody;
import microsoft.hawaii.hawaiiClientLibraryBase.HttpMethod;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.HttpURLConnectionSetter;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;

/**
 * Utility Class to contain a set of HTTP related methods
 */
public final class HttpUtility {

	/**
	 * construct the {@link HttpURLConnection} object
	 * 
	 * @param url
	 *            specified {@link URL} object
	 * @param method
	 *            HTTP method
	 * @param buffer
	 *            byte array which is needed to write to the request body
	 * @param contentType
	 *            Content-Type HTTP header
	 * @return
	 * @throws IOException
	 *             HttpURLConnection
	 */
	public static HttpURLConnection constructHttpConnection(URL url,
			HttpMethod method, byte[] buffer, String contentType)
			throws IOException {
		return constructHttpConnection(url, method, buffer, contentType, 5,
				null);
	}

	/**
	 * construct the {@link HttpURLConnection} object
	 * 
	 * @param url
	 *            specified {@link URL} object
	 * @param method
	 *            HTTP method
	 * @param buffer
	 *            byte array which is needed to write to the request body
	 * @param contentType
	 *            Content-Type HTTP header
	 * @param setter
	 *            callback to set user defined properties to the
	 *            {@link HttpURLConnection} object
	 * @return
	 * @throws IOException
	 *             HttpURLConnection
	 */
	public static HttpURLConnection constructHttpConnection(URL url,
			HttpMethod method, byte[] buffer, String contentType,
			HttpURLConnectionSetter setter) throws IOException {
		return constructHttpConnection(url, method, buffer, contentType, 5,
				setter);
	}

	/**
	 * construct the {@link HttpURLConnection} object
	 * 
	 * @param url
	 *            specified {@link URL} object
	 * @param method
	 *            HTTP method
	 * @param buffer
	 *            byte array which is needed to write to the request body
	 * @param contentType
	 *            Content-Type HTTP header
	 * @param timeOutInSecond
	 *            HTTP(S) connection timeout
	 * @param setter
	 *            callback to set user defined properties to the
	 *            {@link HttpURLConnection} object
	 * @return
	 * @throws IOException
	 *             HttpURLConnection
	 */
	public static HttpURLConnection constructHttpConnection(URL url,
			HttpMethod method, byte[] buffer, String contentType,
			int timeOutInSecond, HttpURLConnectionSetter setter)
			throws IOException {
		Utility.assertNotNull("url cannot be null", url);
		Utility.assertStringNotNullOrEmpty(
				"content type cannot be null or empty", contentType);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestMethod(method.toString());
		request.setRequestProperty("Content-Type", contentType);
		request.setRequestProperty("Accept", contentType);
		request.setConnectTimeout(timeOutInSecond * 1000);
		request.setAllowUserInteraction(false);
		if (setter != null) {
			setter.set(request);
		}

		request.setDoInput(true);
		// write necessary data to request body
		if (method != HttpMethod.GET) {
			request.setDoOutput(true);
			DataOutputStream outStream = null;
			try {
				// open up the output stream of the connection
				outStream = new DataOutputStream(request.getOutputStream());
				outStream.write(buffer);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new IOException("failed to write data to request body",
						ex);
			} finally {
				if (outStream != null) {
					outStream.flush();
					outStream.close();
				}
			}
		}

		return request;
	}

	/**
	 * construct HttpRequestBase object based on apache HttpClient
	 * 
	 * @param url
	 *            specified {@link URL} object
	 * @param method
	 *            HTTP method
	 * @param buffer
	 *            byte array which is needed to write to the request body
	 * @param contentType
	 *            Content-Type HTTP header
	 * @return
	 * @throws HawaiiException
	 *             HttpRequestBase
	 */
	public static HttpRequestBase constructHttpRequest(URI uri,
			HttpMethod method, byte[] buffer, String contentType)
			throws HawaiiException {
		HttpRequestBase request = null;
		HttpEntity entity = null;

		if (buffer != null) {
			entity = new ByteArrayEntity(buffer);
		}

		switch (method) {
		case GET: {
			request = new HttpGet(uri);
			break;
		}
		case POST: {
			HttpPost postRequest = new HttpPost(uri);
			postRequest.setEntity(entity);
			request = postRequest;
			break;
		}
		case PUT: {
			HttpPut putRequest = new HttpPut(uri);
			putRequest.setEntity(entity);
			request = putRequest;
			break;
		}
		case DELETE: {
			HttpDeleteWithBody deleteRequest = new HttpDeleteWithBody(uri);
			deleteRequest.setEntity(entity);
			request = deleteRequest;
			break;
		}
		}

		request.addHeader("Content-Type", contentType);
		return request;
	}
}
