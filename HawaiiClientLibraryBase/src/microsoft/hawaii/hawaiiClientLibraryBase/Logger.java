/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase;

/**
 *
 */
public final class Logger {

	public static void printThreadInfo(String msg) {
		System.out.println(String.format("Thread ID: %d || %s", Thread
				.currentThread().getId(), msg));
	}
}