/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.DataContract;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an error returned to the client
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceFault {

	/**
	 * error message
	 */
	private String message;

	/**
	 * correlation ID of the error message
	 */
	private UUID requestId;

	/**
	 * exception stack
	 */
	private LoggedException[] exceptionStack;

	/**
	 * Gets the error message
	 * 
	 * @return String error message
	 */
	@JsonProperty("Message")
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the correlation ID of error message
	 * 
	 * @return UUID the correlation ID of error message
	 */
	@JsonProperty("RequestId")
	public UUID getRequestId() {
		return requestId;
	}

	/**
	 * Sets the correlation ID of error message
	 * 
	 * @param requestId
	 *            the correlation ID of error message void
	 */
	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the exception stack
	 */
	@JsonProperty("ExceptionStack")
	public LoggedException[] getExceptionStack() {
		return exceptionStack;
	}

	/**
	 * Sets the exception stack collection
	 * 
	 * @param exceptionStack
	 *            the exception stack collection
	 */
	public void setExceptionStack(LoggedException[] exceptionStack) {
		this.exceptionStack = exceptionStack;
	}

	/**
	 * Initializes an instance of the {@link ServiceFault} class
	 */
	public ServiceFault() {
	}
}
