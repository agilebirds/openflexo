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
package org.netbeans.lib.cvsclient.command.status;

import java.io.File;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.CommandRequest;

/**
 * The status command looks up the status of files in the repository
 * 
 * @author Robert Greig
 */
public class StatusCommand extends BasicCommand {
	/**
	 * The event manager to use
	 */
	private EventManager eventManager;

	/**
	 * Holds value of property includeTags.
	 */
	private boolean includeTags;

	/**
	 * Construct a new status command
	 */
	public StatusCommand() {
	}

	/**
	 * Create a builder for this command.
	 * 
	 * @param eventMan
	 *            the event manager used to receive events.
	 */
	@Override
	public Builder createBuilder(EventManager eventManager) {
		return new StatusBuilder(eventManager, this);
	}

	/**
	 * Execute a command
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests.
	 */
	@Override
	public void execute(ClientServices client, EventManager em) throws CommandException, AuthenticationException {
		client.ensureConnection();

		eventManager = em;

		super.execute(client, em);

		try {
			// parameters come now..
			if (includeTags) {
				requests.add(1, new ArgumentRequest("-v")); // NOI18N
			}

			addRequestForWorkingDirectory(client);
			addArgumentRequests();
			addRequest(CommandRequest.STATUS);

			client.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (Exception e) {
			throw new CommandException(e, e.getLocalizedMessage());
		} finally {
			requests.clear();
		}
	}

	/**
	 * Getter for property includeTags.
	 * 
	 * @return Value of property includeTags.
	 */
	public boolean isIncludeTags() {
		return includeTags;
	}

	/**
	 * Setter for property includeTags.
	 * 
	 * @param includeTags
	 *            New value of property includeTags.
	 */
	public void setIncludeTags(boolean inclTags) {
		includeTags = inclTags;
	}

	/**
	 * called when server responses with "ok" or "error", (when the command finishes)
	 */
	@Override
	public void commandTerminated(TerminationEvent e) {
		if (builder != null) {
			builder.outputDone();
		}
	}

	/**
	 * This method returns how the command would looklike when typed on the command line. Each command is responsible for constructing this
	 * information.
	 * 
	 * @returns <command's name> [<parameters>] files/dirs. Example: checkout -p CvsCommand.java
	 */
	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("status "); // NOI18N
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
	 * takes the arguments and sets the command. To be mainly used for automatic settings (like parsing the .cvsrc file)
	 * 
	 * @return true if the option (switch) was recognized and set
	 */
	@Override
	public boolean setCVSCommand(char opt, String optArg) {
		if (opt == 'R') {
			setRecursive(true);
		} else if (opt == 'l') {
			setRecursive(false);
		} else if (opt == 'v') {
			setIncludeTags(true);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * String returned by this method defines which options are available for this particular command
	 */
	@Override
	public String getOptString() {
		return "Rlv"; // NOI18N
	}

	/**
	 * resets all switches in the command. After calling this method, the command should have no switches defined and should behave
	 * defaultly.
	 */
	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setIncludeTags(false);
	}

	/**
	 * Returns the arguments of the command in the command-line style. Similar to getCVSCommand() however without the files and command's
	 * name
	 */
	@Override
	public String getCVSArguments() {
		StringBuffer toReturn = new StringBuffer(""); // NOI18N
		if (isIncludeTags()) {
			toReturn.append("-v "); // NOI18N
		}
		if (!isRecursive()) {
			toReturn.append("-l "); // NOI18N
		}
		return toReturn.toString();
	}

}
