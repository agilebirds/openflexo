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

import org.netbeans.lib.cvsclient.command.FileInfoContainer;

/**
 * This event is created when file information is received from the server.
 * 
 * @author Milos Kleint
 */
public class FileInfoEvent extends CVSEvent {
	/**
	 * The information about the file.
	 */
	private final FileInfoContainer infoContainer;

	/**
	 * Construct a FileInfoEvent
	 * 
	 * @param source
	 *            the source of the event
	 * @param message
	 *            the message text
	 * @param isError
	 *            true if the message is an error message (i.e. intended for stderr rather than stdout), false otherwise
	 */
	public FileInfoEvent(Object source, FileInfoContainer infoContainer) {
		super(source);
		this.infoContainer = infoContainer;
	}

	/**
	 * Get the information in this event
	 * 
	 * @return the information object describing a file's info received from the server
	 */
	public FileInfoContainer getInfoContainer() {
		return infoContainer;
	}

	/**
	 * Fire the event to the event listener. Subclasses should call the appropriate method on the listener to dispatch this event.
	 * 
	 * @param listener
	 *            the event listener
	 */
	@Override
	protected void fireEvent(CVSListener listener) {
		listener.fileInfoGenerated(this);
	}
}