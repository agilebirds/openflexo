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
package org.netbeans.lib.cvsclient.command;

import java.io.UnsupportedEncodingException;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.BinaryMessageEvent;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.netbeans.lib.cvsclient.event.TerminationEvent;

/**
 * A class that provides common functionality for many of the CVS command that send similar sequences of requests.
 * 
 * @author Robert Greig
 */
public abstract class BuildableCommand extends Command {

	/**
	 * An implementation of Builder interface that constructs a FileContainerInfo object from the server's output..
	 */
	protected Builder builder;

	private final StringBuffer taggedLineBuffer = new StringBuffer();

	/**
	 * A boolean value indicating if the user has used the setBuilder() method.
	 */
	private boolean builderSet;

	/**
	 * Execute a command. This implementation sends a Root request, followed by as many Directory and Entry requests as is required by the
	 * recurse setting and the file arguments that have been set. Subclasses should call this first, and tag on the end of the requests list
	 * any further requests and, finally, the actually request that does the command (e.g.
	 * 
	 * <pre>
	 * update
	 * </pre>
	 * 
	 * ,
	 * 
	 * <pre>
	 * status
	 * </pre>
	 * 
	 * etc.)
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 * @throws CommandException
	 *             if an error occurs executing the command
	 */
	@Override
	public void execute(ClientServices client, EventManager eventManager) throws CommandException, AuthenticationException {
		super.execute(client, eventManager);

		if (builder == null && !isBuilderSet()) {
			builder = createBuilder(eventManager);
		}
	}

	/**
	 * Method that is called while the command is being executed. Descendants can override this method to return a Builder instance that
	 * will parse the server's output and create data structures.
	 */
	public Builder createBuilder(EventManager eventManager) {
		return null;
	}

	@Override
	public void messageSent(BinaryMessageEvent e) {
		super.messageSent(e);

		if (builder == null) {
			return;
		}

		if (builder instanceof BinaryBuilder) { // XXX assert it?
			BinaryBuilder binaryBuilder = (BinaryBuilder) builder;
			binaryBuilder.parseBytes(e.getMessage(), e.getMessageLength());
		}
	}

	@Override
	public void messageSent(MessageEvent e) {
		super.messageSent(e);
		if (builder == null) {
			return;
		}

		if (e instanceof EnhancedMessageEvent) {
			EnhancedMessageEvent eEvent = (EnhancedMessageEvent) e;
			builder.parseEnhancedMessage(eEvent.getKey(), eEvent.getValue());
			return;
		}

		if (e.isTagged()) {
			String message = MessageEvent.parseTaggedMessage(taggedLineBuffer, e.getMessage());
			if (message != null) {
				builder.parseLine(message, false);
				taggedLineBuffer.setLength(0);
			}
		} else {
			if (taggedLineBuffer.length() > 0) {
				builder.parseLine(taggedLineBuffer.toString(), false);
				taggedLineBuffer.setLength(0);
			}
			// #67337 do not interpret piped data using platform default encoding
			// UTF-8 causes problems as raw data (non UTf-8) can contain confusing sequences
			// use safe encoding that does not interpret byte sequences
			if (builder instanceof PipedFilesBuilder && e.isError() == false) {
				try {
					String iso88591 = new String(e.getRawData(), "ISO-8859-1");
					builder.parseLine(iso88591, e.isError());
				} catch (UnsupportedEncodingException e1) {
					assert false;
				}
			} else {
				builder.parseLine(e.getMessage(), e.isError());
			}
		}
	}

	/**
	 * Returns whether the builder is set.
	 */
	protected boolean isBuilderSet() {
		return builderSet;
	}

	/**
	 * Used for setting user-defined builder. Can be also set null, in that case the builder mechanism is not used at all.
	 */
	public void setBuilder(Builder builder) {
		this.builder = builder;
		builderSet = true;
	}

	/**
	 * Called when server responses with "ok" or "error", (when the command finishes).
	 */
	@Override
	public void commandTerminated(TerminationEvent e) {
		if (builder == null) {
			return;
		}

		if (taggedLineBuffer.length() > 0) {
			builder.parseLine(taggedLineBuffer.toString(), false);
			taggedLineBuffer.setLength(0);
		}
		builder.outputDone();
	}
}
