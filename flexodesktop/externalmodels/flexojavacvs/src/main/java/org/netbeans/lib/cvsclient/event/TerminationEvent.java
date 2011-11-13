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
 * An event sent from the server to indicate that a the output from the server has ended for the current command
 * 
 * @author Milos Kleint
 */
public class TerminationEvent extends CVSEvent {

	/**
	 * Whether the termination is an error or not
	 */
	private boolean error;

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
	public TerminationEvent(Object source, boolean isError) {
		super(source);
		setError(isError);
	}

	/**
	 * Construct a MessageEvent with no message text
	 * 
	 * @param source
	 *            the source of the event
	 */
	public TerminationEvent(Object source) {
		this(source, false);
	}

	/**
	 * Get whether the command ended successfully or not
	 * 
	 * @return true if the successfull
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Get whether the command ended successfully or not
	 * 
	 * @param error
	 *            true if successfull
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
		listener.commandTerminated(this);
	}
}