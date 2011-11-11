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
 * @author Robert Greig
 */
public class MessageEvent extends CVSEvent {
	/**
	 * Holds value of property message.
	 */
	private String message;

	/**
	 * Whether the message is an error message
	 */
	private boolean error;

	/** Holds value of property tagged. */
	private boolean tagged;

	private final byte[] raw;

	public MessageEvent(Object source, String message, byte[] raw, boolean isError) {
		super(source);
		setMessage(message);
		setError(isError);
		setTagged(false);
		this.raw = raw;
	}

	/**
	 * Construct a MessageEvent
	 * 
	 * @param source
	 *            the source of the event
	 * @param message
	 *            the message text
	 * @param isError
	 *            true if the message is an error message (i.e. intended for stderr rather than stdout), false otherwise
	 */
	public MessageEvent(Object source, String message, boolean isError) {
		this(source, message, null, isError);
	}

	/**
	 * Construct a MessageEvent with no message text
	 * 
	 * @param source
	 *            the source of the event
	 */
	public MessageEvent(Object source) {
		this(source, null, false);
	}

	/**
	 * Getter for property message.
	 * 
	 * @return Value of property message.
	 */
	public String getMessage() {
		return message;
	}

	/** @return bytes from wire or null */
	public byte[] getRawData() {
		return raw;
	}

	/**
	 * Setter for property message.
	 * 
	 * @param message
	 *            New value of property message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get whether the message should be displayed in stderr
	 * 
	 * @return true if the message should be sent to stderr, false otherwise
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Set whether the message should go to stderr
	 * 
	 * @param error
	 *            true if the message is an error message, false otherwise
	 */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * Fire the event to the event listener. Subclasses should call the appropriate method on the listener to dispatch this event.
	 * 
	 * @param listener
	 *            the event listener
	 */
	@Override
	protected void fireEvent(CVSListener listener) {
		listener.messageSent(this);
	}

	/**
	 * Getter for property tagged.
	 * 
	 * @return Value of property tagged.
	 */
	public boolean isTagged() {
		return tagged;
	}

	/**
	 * Setter for property tagged.
	 * 
	 * @param tagged
	 *            New value of property tagged.
	 */
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	/**
	 * Parses the tagged message using the specified buffer.
	 * 
	 * @returns != null, if the line is finished and could be processed
	 */
	public static String parseTaggedMessage(StringBuffer taggedLineBufferNotNull, String taggedMessage) {
		String line = taggedMessage;

		if (line.charAt(0) == '+' || line.charAt(0) == '-') {
			return null;
		}

		String result = null;
		if (line.equals("newline")) {// NOI18N
			result = taggedLineBufferNotNull.toString();
			taggedLineBufferNotNull.setLength(0);
		}
		int index = line.indexOf(' ');
		if (index > 0) {
			taggedLineBufferNotNull.append(line.substring(index + 1));
		}
		return result;
	}
}