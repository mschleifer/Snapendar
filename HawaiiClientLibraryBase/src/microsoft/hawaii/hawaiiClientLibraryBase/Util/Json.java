/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package microsoft.hawaii.hawaiiClientLibraryBase.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON utility class for JSON serialization and deserialization based on
 * Jackson JSON libary
 */
public class Json {
	/**
	 * ObjectMapper object for Json serialization/deserialization
	 */
	private static final ObjectMapper s_mapper;

	static {
		s_mapper = new ObjectMapper();
	}

	/**
	 * deserialize from specified string
	 * 
	 * @param objClass
	 *            Class object of entity
	 * @param jsonString
	 *            specified json string
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             T
	 */
	public static <T> T deserializeFromString(final Class<T> objClass,
			String jsonString) throws JsonParseException, JsonMappingException,
			IOException {
		return s_mapper.readValue(jsonString, objClass);
	}

	/**
	 * deserialize from specified input stream
	 * 
	 * @param objClass
	 *            Class object of entity
	 * @param stream
	 *            specified input stream
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             T
	 */
	public static <T> T deserializeFromStream(final Class<T> objClass,
			InputStream stream) throws JsonParseException,
			JsonMappingException, IOException {
		return s_mapper.readValue(stream, objClass);
	}

	/**
	 * serialize to specified output stream
	 * 
	 * @param object
	 *            specified entity object
	 * @param stream
	 *            specified output stream
	 * @throws IOException
	 *             void
	 */
	public static <T> void serializeToStream(T object, OutputStream stream)
			throws IOException {
		s_mapper.writeValue(stream, object);
	}

	/**
	 * serialize as string representation
	 * 
	 * @param object
	 *            specified entity object
	 * @return Json string representation
	 * @throws IOException
	 *             String
	 */
	public static <T> String serializeAsString(T object) throws IOException {
		return s_mapper.writeValueAsString(object);
	}
}
