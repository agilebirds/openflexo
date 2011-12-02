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
package org.netbeans.lib.cvsclient.command.editors;

import java.io.EOFException;
import java.io.File;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.request.CommandRequest;

/**
 * @author Thomas Singer
 */
public class EditorsCommand extends BasicCommand {

	/**
	 * Construct a new editors command.
	 */
	public EditorsCommand() {
		resetCVSCommand();
	}

	/**
	 * Creates the EditorsBuilder.
	 * 
	 * @param eventManager
	 *            the event manager used to received cvs events
	 */
	@Override
	public Builder createBuilder(EventManager eventManager) {
		return new EditorsBuilder(eventManager);
	}

	/**
	 * Execute the command.
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests.
	 */
	@Override
	public void execute(ClientServices clientServices, EventManager eventManager) throws CommandException, AuthenticationException {

		clientServices.ensureConnection();

		super.execute(clientServices, eventManager);

		try {
			addRequestForWorkingDirectory(clientServices);
			addArgumentRequests();
			addRequest(CommandRequest.EDITORS);

			clientServices.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (EOFException ex) {
			throw new CommandException(ex, CommandException.getLocalMessage("CommandException.EndOfFile", null)); // NOI18N
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		} finally {
			requests.clear();
		}
	}

	/**
	 * Called when server responses with "ok" or "error", (when the command finishes).
	 */
	@Override
	public void commandTerminated(TerminationEvent e) {
		if (builder != null) {
			builder.outputDone();
		}
	}

	/**
	 * This method returns how the tag command would looklike when typed on the command line.
	 */
	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("editors "); // NOI18N
		toReturn.append(getCVSArguments());
		File[] files = getFiles();
		if (files != null) {
			for (int index = 0; index < files.length; index++) {
				toReturn.append(files[index].getName());
				toReturn.append(' ');
			}
		}
		return toReturn.toString();
	}

	/**
	 * Takes the arguments and sets the command. To be mainly used for automatic settings (like parsing the .cvsrc file)
	 * 
	 * @return true if the option (switch) was recognized and set
	 */
	@Override
	public boolean setCVSCommand(char opt, String optArg) {
		if (opt == 'R') {
			setRecursive(true);
		} else if (opt == 'l') {
			setRecursive(false);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * String returned by this method defines which options are available for this command.
	 */
	@Override
	public String getOptString() {
		return "Rl"; // NOI18N
	}

	/**
	 * Resets all switches in the command. After calling this method, the command should have no switches defined and should behave
	 * defaultly.
	 */
	@Override
	public void resetCVSCommand() {
		setRecursive(true);
	}

	/**
	 * Returns the arguments of the command in the command-line style. Similar to getCVSCommand() however without the files and command's
	 * name
	 */
	@Override
	public String getCVSArguments() {
		StringBuffer toReturn = new StringBuffer();
		if (!isRecursive()) {
			toReturn.append("-l "); // NOI18N
		}
		return toReturn.toString();
	}

}
