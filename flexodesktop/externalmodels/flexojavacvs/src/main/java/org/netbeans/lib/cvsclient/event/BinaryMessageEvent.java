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
 * An event sent from the server to indicate that a binary message should be displayed to the user.
 * <p>
 * One protocol reponse mey be splitted into several messages.
 * 
 * @author Martin Entlicher
 */
public class BinaryMessageEvent extends CVSEvent {
	/**
	 * Holds value of property message.
	 */
	private byte[] message;

	private int len;

	/**
	 * Construct a MessageEvent
	 * 
	 * @param source
	 *            the source of the event
	 * @param message
	 *            the message text
	 */
	public BinaryMessageEvent(Object source, byte[] message, int len) {
		super(source);
		this.message = message;
		this.len = len;
	}

	/**
	 * Raw data buffer that holds binary data.
	 * 
	 * @return raw data buffer, its {@link #getMessageLength()} subset represents actual data
	 */
	public byte[] getMessage() {
		return message;
	}

	/**
	 * Defines valid data length in raw data buffer.
	 * 
	 * @return number of valid bytes in message raw data buffer.
	 */
	public int getMessageLength() {
		return len;
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

}