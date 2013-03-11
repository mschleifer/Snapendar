/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.DataContract;

import microsoft.hawaii.hawaiiClientLibraryBase.Status;

/**
 * Base class for all Hawaii service result classes. Various Hawaii service
 * result classes will represent the result corresponding to different type of
 * Hawaii service calls. This class contains functionality common to all Hawaii
 * service result classes.
 */
public abstract class ServiceResult {

	/**
	 * status of service response
	 */
	private Status status;

	/**
	 * the associated exception
	 */
	private Exception exception;

	/**
	 * user-defined state object
	 */
	private Object stateObject;

	/**
	 * Initializes an instance of the {@link ServiceResult} class
	 */
	public ServiceResult() {
		this(null);
	}

	/**
	 * Initializes an instance of the {@link ServiceResult} class
	 * 
	 * @param stateObject
	 *            user-defined state object
	 */
	public ServiceResult(Object stateObject) {
		this(stateObject, Status.Unspecified);
	}

	/**
	 * Initializes an instance of the {@link ServiceResult} class
	 * 
	 * @param stateObject
	 *            user-defined state object
	 * @param status
	 *            status of the service response
	 */
	public ServiceResult(Object stateObject, Status status) {
		this(stateObject, status, null);
	}

	/**
	 * Initializes an instance of the {@link ServiceResult} class
	 * 
	 * @param stateObject
	 *            user-defined state object
	 * @param status
	 *            status of the service response
	 * @param exception
	 *            associated exception
	 */
	public ServiceResult(Object stateObject, Status status, Exception exception) {
		this.stateObject = stateObject;
		this.status = status;
		this.exception = exception;
	}

	/**
	 * Gets the status of service response
	 * 
	 * @return Status
	 */
	public Status getStatus() {
		return this.status;
	}

	/**
	 * Gets the associated exception
	 * 
	 * @return Exception
	 */
	public Exception getException() {
		return this.exception;
	}

	/**
	 * Gets the user-defined state object
	 * 
	 * @return Object
	 */
	public Object getStateObject() {
		return this.stateObject;
	}

	/**
	 * Sets the status of service response
	 * 
	 * @param value
	 *            void
	 */
	public void setStatus(Status value) {
		this.status = value;
	}

	/**
	 * Sets the associated exception
	 * 
	 * @param value
	 *            void
	 */
	public void setException(Exception value) {
		this.exception = value;
	}

	/**
	 * Sets the user defined state object
	 * 
	 * @param value
	 *            user defined state object void
	 */
	public void setStateObject(Object value) {
		this.stateObject = value;
	}

	public String toString() {
		return String.format("%s\r\n%s", this.status.toString(),
				this.exception != null ? this.exception.toString() : "");
	}

}
