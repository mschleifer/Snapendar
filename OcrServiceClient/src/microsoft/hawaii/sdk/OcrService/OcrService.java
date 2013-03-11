/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sdk.OcrService;

import microsoft.hawaii.hawaiiClientLibraryBase.ServiceAgent;
import microsoft.hawaii.hawaiiClientLibraryBase.DataContract.ServiceResult;
import microsoft.hawaii.hawaiiClientLibraryBase.Identities.ClientIdentity;
import microsoft.hawaii.hawaiiClientLibraryBase.Listeners.OnCompleteListener;
import microsoft.hawaii.sdk.OcrService.ServiceAgents.OcrAgent;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrServiceResult;

/**
 * Helper class that provides access to the OCR service.
 */
public class OcrService {

	/**
	 * Execute specified ServiceAgent object
	 * 
	 * @param agent
	 *            the specified ServiceAgent object
	 * @param completedCallback
	 *            completion callback
	 * @throws Exception
	 */
	private static <T extends ServiceResult> void ExecuteAgent(
			ServiceAgent<T> agent, OnCompleteListener<T> completedCallback)
			throws Exception {
		agent.processRequest(completedCallback);
	}

	/**
	 * 
	 * Helper method to initiate the call to recognize image.
	 * 
	 * @param identity
	 *            client identity object
	 * @param imageBuffer
	 *            Specifies a buffer containing an image that has to be
	 *            processed. The image must be in JPEG format.
	 * @param completedCallback
	 *            completed callback
	 * @param state
	 *            specifies a user-defined object
	 * @throws Exception
	 */
	public static void recognizeImage(ClientIdentity identity,
			byte[] imageBuffer,
			OnCompleteListener<OcrServiceResult> completedCallback, Object state)
			throws Exception {
		OcrAgent agent = new OcrAgent(HawaiiClient.getServiceBaseUri(),
				identity, imageBuffer, state);
		ExecuteAgent(agent, completedCallback);
	}
}
