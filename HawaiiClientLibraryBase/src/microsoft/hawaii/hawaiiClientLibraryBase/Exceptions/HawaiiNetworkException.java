/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Exceptions;

/**
 * General exception class to represent network errors for Hawaii service
 */
public class HawaiiNetworkException extends Exception {

	private static final long serialVersionUID = 5864961328696207695L;

	private final int httpStatusCode;

	/**
	 * Initializes an instance of the {@link HawaiiNetworkException} class
	 * 
	 * @param message
	 *            error message
	 * @param statusCode
	 *            Http status code
	 * @param exception
	 *            inner exception
	 */
	public HawaiiNetworkException(String message, int statusCode,
			Exception exception) {
		super(message, exception);
		this.httpStatusCode = statusCode;
	}

	/**
	 * Gets the Http Status Code
	 * 
	 * @return int
	 */
	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}

	public static HawaiiNetworkException translateFromException(
			Exception exception) {
		return new HawaiiNetworkException("Unexpected client error.", 306,
				exception);
	}

	public static HawaiiNetworkException translateFromHttpStatus(
			int statusCode, String reasonPhrase, Exception exception) {
		return new HawaiiNetworkException(reasonPhrase, statusCode, exception);
	}
}
