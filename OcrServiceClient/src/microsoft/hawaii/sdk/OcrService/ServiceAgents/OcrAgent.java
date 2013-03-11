/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sdk.OcrService.ServiceAgents;

import java.net.URI;

import microsoft.hawaii.hawaiiClientLibraryBase.HttpMethod;
import microsoft.hawaii.hawaiiClientLibraryBase.ServiceAgent;
import microsoft.hawaii.hawaiiClientLibraryBase.Status;
import microsoft.hawaii.hawaiiClientLibraryBase.Exceptions.HawaiiException;
import microsoft.hawaii.hawaiiClientLibraryBase.Identities.ClientIdentity;
import microsoft.hawaii.hawaiiClientLibraryBase.Util.Utility;
import microsoft.hawaii.sdk.OcrService.ServiceContracts.OcrServiceResult;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class provides helper methods used to communicate with the Hawaii OCR
 * Service. for character recognition. This class accepts an image buffer as an
 * input, sends it to the OCR service and receives the result of the OCR
 * processing.
 */
public class OcrAgent extends ServiceAgent<OcrServiceResult> {

	/**
	 * The buffer contains the image that has to be processed.
	 */
	private byte[] imageBuffer;

	/**
	 * Gets the buffer containing the image
	 * 
	 * @return the image buffer
	 */
	@JsonProperty("ImageBuffer")
	public byte[] getImageBuffer() {
		return imageBuffer;
	}

	/**
	 * Sets a buffer with the image bytes
	 * 
	 * @param imageBuffer
	 *            the image bytes
	 */
	private void setImageBuffer(byte[] imageBuffer) {
		this.imageBuffer = imageBuffer;
	}

	/**
	 * 
	 * Initializes an instance of the {@link OcrAgent} class
	 * 
	 * @param baseUri
	 *            URI to access service
	 * @param clientIdentity
	 *            the client identity
	 * @param imageBuffer
	 *            Request image bytes
	 * @param state
	 *            specifies a user-defined object
	 * @throws Exception
	 */
	public OcrAgent(URI baseUri, ClientIdentity clientIdentity,
			byte[] imageBuffer, Object state) throws Exception {
		super(OcrServiceResult.class, HttpMethod.POST, state);

		// parameter validation
		Utility.assertNotNull("baseUri", baseUri);
		Utility.assertNotNull("identity", clientIdentity);

		if (imageBuffer == null || imageBuffer.length == 0) {
			throw new IllegalArgumentException("audioBuffer");
		}

		this.setImageBuffer(imageBuffer);
		this.clientIdentity = clientIdentity;
		this.serviceUri = baseUri;
	}

	/**
	 * Override {@link getContentType} method of {@link ServiceAgent} class
	 */
	@Override
	public String getContentType() {
		return "application/json";
	}

	/**
	 * Override {@link getRequestBodyData} method of {@link ServiceAgent} class
	 */
	@Override
	protected byte[] getRequestBodyData() throws HawaiiException {
		return this.imageBuffer;
	}

	/**
	 * Override {@link OnCompleteRequest} of {@link ServiceAgent} class
	 */
	@Override
	protected void OnCompleteRequest() {

		if (this.onCompleteListener != null) {
			if (this.serviceResult.getException() == null) {
				OcrServiceResult ocrResult = this.serviceResult;

				if (ocrResult.getInternalErrorMessage() != null) {
					this.serviceResult
							.setException(new Exception(
									"A server side error occured while processing the OCR request."));
					this.serviceResult.setStatus(Status.InternalServerError);
				}
			}

			this.onCompleteListener.done(this.serviceResult);
		}
	}
}
