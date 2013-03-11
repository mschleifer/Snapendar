/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.DataContract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to serialize exception information
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoggedException {
	/**
	 * exception name
	 */
	private String name;

	/**
	 * exception message
	 */
	private String message;

	/**
	 * exception source
	 */
	private String source;

	/**
	 * exception stack trace
	 */
	private String stackTrace;

	/**
	 * Initializes an instance of the {@link LoggedException} class
	 */
	public LoggedException() {
	}

	/**
	 * Gets the name
	 */
	@JsonProperty("Name")
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the message
	 */
	@JsonProperty("Mame")
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets the message
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the source
	 */
	@JsonProperty("Source")
	public String getSource() {
		return this.source;
	}

	/**
	 * Sets the source
	 * 
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Gets the stackTrace
	 */
	@JsonProperty("StackTrace")
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * Sets the stack trace
	 * 
	 * @param stackTrace
	 *            the stackTrace to set
	 */
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}
