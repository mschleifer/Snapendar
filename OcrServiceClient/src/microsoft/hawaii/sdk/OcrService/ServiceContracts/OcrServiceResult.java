/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sdk.OcrService.ServiceContracts;

import java.util.ArrayList;
import java.util.List;

import microsoft.hawaii.hawaiiClientLibraryBase.DataContract.ServiceResult;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the result of the OCR processing.
 */
public class OcrServiceResult extends ServiceResult {

	/**
	 * Initializes an instance of the {@link OcrServiceResult} class
	 */
	public OcrServiceResult() {
		this.internalErrorMessage = null;
		this.ocrTexts = new ArrayList<OcrText>();
	}

	/**
	 * Internal error message
	 */
	private String internalErrorMessage;

	/**
	 * OcrText items
	 */
	private List<OcrText> ocrTexts;

	/**
	 * Gets internal error message
	 * 
	 * @return String the internalErrorMessage
	 */
	@JsonProperty("InternalErrorMessage")
	public String getInternalErrorMessage() {
		return internalErrorMessage;
	}

	/**
	 * Sets internal error message
	 * 
	 * @param internalErrorMessage
	 *            internal error message value
	 */
	public void setInternalErrorMessage(String internalErrorMessage) {
		this.internalErrorMessage = internalErrorMessage;
	}

	/**
	 * 
	 * Gets OCR texts
	 * 
	 * @return List<OcrText> a list of OcrText
	 */
	@JsonProperty("OcrTexts")
	public List<OcrText> getOcrTexts() {
		return ocrTexts;
	}

	/**
	 * Sets OCR text
	 * 
	 * @param ocrTexts
	 *            a list containing ocr texts
	 */
	public void setOcrTexts(List<OcrText> ocrTexts) {
		this.ocrTexts = ocrTexts;
	}

}
