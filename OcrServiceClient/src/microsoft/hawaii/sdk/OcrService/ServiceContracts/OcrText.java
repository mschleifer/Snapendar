/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.sdk.OcrService.ServiceContracts;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains one text item that is obtained after a Hawaii OCR call.
 */
public class OcrText {

	/**
	 * Initializes an instance of the {@link OcrText} class
	 */
	public OcrText() {
		this.words = new ArrayList<OcrWord>();
	}

	/**
	 * Gets orientation of the text.
	 * 
	 * @return String the orientation of text
	 */
	@JsonProperty("Orientation")
	public String getOrientation() {
		return orientation;
	}

	/**
	 * Sets orientation of the text.
	 * 
	 * @param orientation
	 *            the orientation of text
	 */
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	/**
	 * Gets the skewness of the text.
	 * 
	 * @return String the skewness of the text.
	 */
	@JsonProperty("Skew")
	public String getSkew() {
		return skew;
	}

	/**
	 * 
	 * Sets skewness of the text.
	 * 
	 * @param skew
	 *            the skewness of the text
	 */
	public void setSkew(String skew) {
		this.skew = skew;
	}

	/**
	 * 
	 * Gets the list of words that are contained in the text.
	 * 
	 * @return List<OcrWord> the list of words that are contained in the text
	 */
	@JsonProperty("Words")
	public List<OcrWord> getWords() {
		return words;
	}

	/**
	 * 
	 * Sets the list of words that are contained in the text.
	 * 
	 * @param words
	 *            the list of words that are contained in the text
	 */
	public void setWords(List<OcrWord> words) {
		this.words = words;
	}

	/**
	 * 
	 * Help method to get text from words
	 * 
	 * @return String Gets the text of all the words (this.Words) separated by
	 *         space and combined in a single string.
	 */
	public String getText() {

		if (this.words.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (OcrWord word : this.words) {
			sb.append(word.getText());
			sb.append(' ');
		}

		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * Orientation of the text
	 */
	private String orientation;

	/**
	 * Skewness of the text
	 */
	private String skew;

	/**
	 * The list of words that are contained in the text
	 */
	private List<OcrWord> words;

}
