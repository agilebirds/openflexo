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
 * A convenience class for implementing the CVSListener. This class provides empty implementations of the CVSListener interface. Subclasses
 * should override the methods for the event in which they are interested.
 * 
 * @author Robert Greig
 */
public class CVSAdapter implements CVSListener {

	/**
	 * Called when the server wants to send a message to be displayed to the user. The message is only for information purposes and clients
	 * can choose to ignore these messages if they wish.
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void messageSent(MessageEvent e) {
	}

	/**
	 * Called when the server wants to send a binary message to be displayed to the user. The message is only for information purposes and
	 * clients can choose to ignore these messages if they wish.
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void messageSent(BinaryMessageEvent e) {
	}

	/**
	 * Called when a file has been added.
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void fileAdded(FileAddedEvent e) {
	}

	/**
	 * Called when a file is going to be removed.
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void fileToRemove(FileToRemoveEvent e) {
	}

	/**
	 * Called when a file is removed.
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void fileRemoved(FileRemovedEvent e) {
	}

	/**
	 * Called when a file has been updated
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void fileUpdated(FileUpdatedEvent e) {
	}

	/**
	 * Called when file status information has been received
	 */
	@Override
	public void fileInfoGenerated(FileInfoEvent e) {
	}

	/**
	 * called when server responses with "ok" or "error", (when the command finishes)
	 */
	@Override
	public void commandTerminated(TerminationEvent e) {
	}

	/**
	 * Fire a module expansion event. This is called when the servers has responded to an expand-modules request.
	 */
	@Override
	public void moduleExpanded(ModuleExpansionEvent e) {
	}

}