/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Exceptions;

/**
 * General exception class to represent invalid operation Exception for Hawaii
 * service
 */
public class HawaiiInvalidOperationException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Initializes an instance of the {@link HawaiiInvalidOperationException}
	 * class
	 * 
	 * @param message
	 *            error message
	 */
	public HawaiiInvalidOperationException(String message) {
		super(message);
	}

	/**
	 * Initializes an instance of the {@link HawaiiInvalidOperationException}
	 * class
	 * 
	 * @param message
	 *            error message
	 * @param ex
	 *            inner exception
	 */
	public HawaiiInvalidOperationException(String message, Throwable ex) {
		super(message, ex);
	}
}
