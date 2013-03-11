/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Customized Json deserializer for {@link Date} object
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
	 * .jackson.core.JsonParser,
	 * com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		String text = parser.getText();
		if (!Utility.isStringNullOrEmpty(text)) {
			return null;
		}

		String dateText = text.substring(6);
		dateText = dateText.substring(0, dateText.indexOf(")"));
		long milliseconds = Long.parseLong(dateText);

		return new Date(milliseconds);
	}
}
