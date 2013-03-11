/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sdk.OcrService.ServiceContracts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains one word item that is obtained after a Hawaii OCR call.
 */
public class OcrWord {

	/**
	 * 
	 * Initializes an instance of the {@link OcrWord} class
	 */
	public OcrWord() {
	}

	/**
	 * The text of the word.
	 */
	private String text;

	/**
	 * The confidence of the word.
	 */
	private String confidence;

	/**
	 * Bounding box of the word in a string format. The box is described as
	 * X0,Y0,Width,Height. X0,Y0 are the coordinates of the top left corner of
	 * the word relative to the top left corner of the image.
	 */
	private String box;

	/**
	 * Override toString method
	 * 
	 * @return String a System.String that represents this OcrWord instance.
	 */
	@Override
	public String toString() {
		return this.text + " (" + this.confidence + ")";
	}

	/**
	 * 
	 * Gets the text of the word.
	 * 
	 * @return String the text of the word
	 */
	@JsonProperty("Text")
	public String getText() {
		return text;
	}

	/**
	 * Sets text
	 * 
	 * @param text
	 *            the text of the word
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets confidence of the word.
	 * 
	 * @return String the confidence of the word
	 */
	@JsonProperty("Confidence")
	public String getConfidence() {
		return confidence;
	}

	/**
	 * Sets confidence of the word.
	 * 
	 * @param confidence
	 *            the confidence of the word
	 */
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	/**
	 * Gets the bounding box of the word in a string format.
	 * 
	 * @return String the box of the word
	 */
	@JsonProperty("Box")
	public String getBox() {
		return box;
	}

	/**
	 * Set Box
	 * 
	 * @param box
	 *            the box of the word
	 */
	public void setBox(String box) {
		this.box = box;
	}

}
