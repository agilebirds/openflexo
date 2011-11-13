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
 * Indicates that an existing file has been updated.
 * 
 * @author Robert Greig
 */
public class FileUpdatedEvent extends CVSEvent {
	/**
	 * The path of the file that has been added
	 */
	protected String path;

	/**
	 * Construct a FileUpdatedEvent
	 * 
	 * @param source
	 *            the event source
	 */
	public FileUpdatedEvent(Object source, String path) {
		super(source);
		this.path = path;
	}

	/**
	 * Get the path of the file that has been added
	 */
	public String getFilePath() {
		return path;
	}

	/**
	 * Fire the event to the event listener. Subclasses should call the appropriate method on the listener to dispatch this event.
	 * 
	 * @param listener
	 *            the event listener
	 */
	@Override
	protected void fireEvent(CVSListener listener) {
		listener.fileUpdated(this);
	}
}