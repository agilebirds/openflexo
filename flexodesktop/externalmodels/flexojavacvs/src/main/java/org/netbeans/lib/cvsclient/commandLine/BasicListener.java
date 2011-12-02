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
package org.netbeans.lib.cvsclient.commandLine;

import java.io.PrintStream;

import org.netbeans.lib.cvsclient.event.BinaryMessageEvent;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;

/**
 * A basic implementation of a CVS listener. Is really only interested in message events. This listener is suitable for command line clients
 * and clients that don't "persist".
 * 
 * @author Robert Greig
 */
public class BasicListener extends CVSAdapter {
	private final StringBuffer taggedLine = new StringBuffer();
	private PrintStream stdout;
	private PrintStream stderr;

	public BasicListener() {
		this(System.out, System.err);
	}

	public BasicListener(PrintStream stdout, PrintStream stderr) {
		this.stdout = stdout;
		this.stderr = stderr;
	}

	/**
	 * Called when the server wants to send a message to be displayed to the user. The message is only for information purposes and clients
	 * can choose to ignore these messages if they wish.
	 * 
	 * @param e
	 *            the event
	 */
	@Override
	public void messageSent(MessageEvent e) {
		String line = e.getMessage();
		if (e instanceof EnhancedMessageEvent) {
			return;
		}
		PrintStream stream = e.isError() ? stderr : stdout;

		if (e.isTagged()) {
			String message = MessageEvent.parseTaggedMessage(taggedLine, e.getMessage());
			if (message != null) {
				stream.println(message);
			}
		} else {
			stream.println(line);
		}
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
		byte[] bytes = e.getMessage();
		int len = e.getMessageLength();
		stdout.write(bytes, 0, len);
	}

	/**
	 * Called when file status information has been received
	 */
	@Override
	public void fileInfoGenerated(FileInfoEvent e) {
		// FileInfoContainer fileInfo = e.getInfoContainer();
		// if (fileInfo.getClass().equals(StatusInformation.class)) {
		// System.err.println("A file status event was received.");
		// System.err.println("The status information object is: " +
		// fileInfo);
		// }
	}
}