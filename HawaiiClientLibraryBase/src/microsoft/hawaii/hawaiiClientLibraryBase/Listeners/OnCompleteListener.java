/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Listeners;

/**
 * callback delegate class for completion of hawaii service operation
 * 
 * @param <T>
 */
public interface OnCompleteListener<T> {
	/**
	 * operation completion callback method
	 * 
	 * @param result
	 *            result object from hawaii service
	 */
	void done(T result);
}
