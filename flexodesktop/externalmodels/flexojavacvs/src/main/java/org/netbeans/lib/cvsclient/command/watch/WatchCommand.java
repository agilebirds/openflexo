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
package org.netbeans.lib.cvsclient.command.watch;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.Watch;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;

/**
 * @author Thomas Singer
 */
public class WatchCommand extends BasicCommand {
	private WatchMode watchMode;

	private Watch watch;

	/**
	 * Construct a new WatchCommand.
	 */
	public WatchCommand() {
		resetCVSCommand();
	}

	/**
	 * Executes this command.
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 * @param eventManager
	 *            the EventManager used for sending events
	 * 
	 * @throws IllegalStateException
	 *             if the commands options aren't set correctly
	 * @throws AuthenticationException
	 *             if the connection could not be established
	 * @throws CommandException
	 *             if some other thing gone wrong
	 */
	@Override
	public void execute(ClientServices client, EventManager eventManager) throws CommandException, AuthenticationException {
		checkState();

		client.ensureConnection();

		try {
			super.execute(client, eventManager);

			if (getWatchMode().isWatchOptionAllowed()) {
				String[] arguments = getWatchNotNull().getArguments();
				for (int i = 0; i < arguments.length; i++) {
					addRequest(new ArgumentRequest("-a")); // NOI18N
					addRequest(new ArgumentRequest(arguments[i]));
				}
			}

			addRequestForWorkingDirectory(client);
			addArgumentRequests();
			addRequest(getWatchMode().getCommand());

			client.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		} finally {
			requests.clear();
		}
	}

	/**
	 * If a builder was set-up, it's outputDone() method is called. This method is called, when the server responses with "ok" or "error"
	 * (== when the command finishes).
	 */
	@Override
	public void commandTerminated(TerminationEvent e) {
		if (builder != null) {
			builder.outputDone();
		}
	}

	/**
	 * Uses the specified argument to set the appropriate properties. To be mainly used for automatic settings (like parsing the .cvsrc
	 * file)
	 * 
	 * @return whether the option (switch) was recognized and set
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
	 * Resets all switches in this command. After calling this method, the command behaves like newly created.
	 */
	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setWatch(null);
	}

	/**
	 * Returns how this command would look like when typed on the command line.
	 */
	@Override
	public String getCVSCommand() {
		StringBuffer cvsCommand = new StringBuffer("watch "); // NOI18N
		cvsCommand.append(getCVSArguments());
		appendFileArguments(cvsCommand);
		return cvsCommand.toString();
	}

	/**
	 * Returns the arguments of the command in the command-line style. Similar to getCVSCommand() however without the files and command's
	 * name
	 */
	@Override
	public String getCVSArguments() {
		checkState();

		StringBuffer cvsArguments = new StringBuffer();
		cvsArguments.append(getWatchMode().toString());
		cvsArguments.append(' ');

		if (!isRecursive()) {
			cvsArguments.append("-l "); // NOI18N
		}

		if (getWatchMode().isWatchOptionAllowed()) {
			cvsArguments.append("-a ");
			cvsArguments.append(getWatchNotNull().toString());
		}
		return cvsArguments.toString();
	}

	/**
	 * Returns the WatchMode.
	 */
	public WatchMode getWatchMode() {
		return watchMode;
	}

	/**
	 * Sets the WatchMode.
	 */
	public void setWatchMode(WatchMode watchMode) {
		this.watchMode = watchMode;
	}

	/**
	 * Returns the watch.
	 */
	public Watch getWatch() {
		return watch;
	}

	private Watch getWatchNotNull() {
		if (watch == null) {
			return Watch.ALL;
		}
		return watch;
	}

	/**
	 * Sets the watch. If the WatchMode ADD or REMOVE is used, null is the same as Watch.ALL. If the WatchMode ON or OFF is used, this
	 * option isn't used at all.
	 */
	public void setWatch(Watch watch) {
		this.watch = watch;
	}

	private void checkState() {
		if (getWatchMode() == null) {
			throw new IllegalStateException("Watch mode expected!");
		}
	}
}
