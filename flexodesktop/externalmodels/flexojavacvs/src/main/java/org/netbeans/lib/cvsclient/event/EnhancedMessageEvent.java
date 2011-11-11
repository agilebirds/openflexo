/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.netbeans.lib.cvsclient.event;

/**
 * An event sent from the server to indicate that a message should be displayed to the user
 * 
 * @author Milos Kleint
 */
public class EnhancedMessageEvent extends MessageEvent {

	/**
	 * Sent by MergedResponse when 2 files were merged. The value is a String instance that tells the full path to the file.
	 */
	public static final String MERGED_PATH = "Merged_Response_File_Path"; // NOI18N

	/**
	 * Sent when a file was successfully sent to server.
	 */
	public static final String FILE_SENDING = "File_Sent_To_Server"; // NOI18N

	/**
	 * Sent when a all requests were sent to server. Value is a String with value of "ok".
	 */
	public static final String REQUESTS_SENT = "All_Requests_Were_Sent"; // NOI18N

	/**
	 * Sent before all request are processed. Value is an Integer object.
	 */
	public static final String REQUESTS_COUNT = "Requests_Count"; // NOI18N

	private String key;
	private Object value;

	/**
	 * Construct a MessageEvent
	 * 
	 * @param source
	 *            the source of the event
	 * @param key
	 *            identifier. Specifies what the value object is.
	 * @param value
	 *            . Some value passed to the listeners. The key parameter helps the listeners to identify the value and handle it correctly.
	 *            for stderr rather than stdout), false otherwise
	 */

	public EnhancedMessageEvent(Object source, String key, Object value) {
		super(source, "", false); // NOI18N
		this.key = key;
		this.value = value;
	}

	/**
	 * Getter for property key.
	 * 
	 * @return Value of property key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for property key.
	 * 
	 * @param key
	 *            New value of property key.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Getter for property value.
	 * 
	 * @return Value of property value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Setter for property value.
	 * 
	 * @param value
	 *            New value of property value.
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
