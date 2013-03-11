/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Exceptions;

/**
 * General exception class for Hawaii service
 */
public class HawaiiException extends Exception {

	private static final long serialVersionUID = -7117421154454670133L;

	/**
	 * Initializes an instance of the {@link HawaiiException} class
	 * 
	 * @param detailMessage
	 */
	public HawaiiException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Initializes an instance of the {@link HawaiiException} class
	 * 
	 * @param throwable
	 */
	public HawaiiException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Initializes an instance of the {@link HawaiiException} class
	 * 
	 * @param detailMessage
	 * @param throwable
	 */
	public HawaiiException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
