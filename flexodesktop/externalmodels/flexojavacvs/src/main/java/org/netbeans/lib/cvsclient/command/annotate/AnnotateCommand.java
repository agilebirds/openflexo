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

package org.netbeans.lib.cvsclient.command.annotate;

import java.io.File;
import java.util.Iterator;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.CommandRequest;
import org.netbeans.lib.cvsclient.request.EntryRequest;

/**
 * The annotate command shows all lines of the file and annotates each line with cvs-related info.
 * 
 * @author Milos Kleint
 */
public class AnnotateCommand extends BasicCommand {
	/**
	 * The event manager to use
	 */
	protected EventManager eventManager;

	/**
	 * Use head revision if a revision meeting criteria set by switches -r/-D (tag/date) is not found.
	 */
	private boolean useHeadIfNotFound;

	/**
	 * equals the -D switch of command line cvs.
	 */
	private String annotateByDate;

	/**
	 * Equals the -r switch of command-line cvs.
	 */
	private String annotateByRevision;

	/**
	 * Construct a new diff command
	 */
	public AnnotateCommand() {
	}

	/**
	 * Create a builder for this command.
	 * 
	 * @param eventMan
	 *            the event manager used to receive events.
	 */
	@Override
	public Builder createBuilder(EventManager eventMan) {
		return new AnnotateBuilder(eventMan, this);
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
		eventManager = em;

		client.ensureConnection();

		super.execute(client, em);

		excludeBinaryFiles(requests);

		try {
			if (useHeadIfNotFound) {
				requests.add(1, new ArgumentRequest("-f")); // NOI18N
			}
			if (annotateByDate != null && annotateByDate.length() > 0) {
				requests.add(1, new ArgumentRequest("-D")); // NOI18N
				requests.add(2, new ArgumentRequest(getAnnotateByDate()));
			}
			if (annotateByRevision != null && annotateByRevision.length() > 0) {
				requests.add(1, new ArgumentRequest("-r")); // NOI18N
				requests.add(2, new ArgumentRequest(getAnnotateByRevision()));
			}
			addRequestForWorkingDirectory(client);
			addArgumentRequests();
			addRequest(CommandRequest.ANNOTATE);
			client.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		} finally {
			requests.clear();
		}
	}

	private void excludeBinaryFiles(java.util.List requests) {
		Iterator it = requests.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof EntryRequest) {
				EntryRequest req = (EntryRequest) obj;
				if (req.getEntry().isBinary()) {
					it.remove();
					if (it.hasNext()) {
						// removes also the follwoing modified/unchanged request
						it.next();
						it.remove();
					}
				}
			}
		}
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
	 * Getter for property useHeadIfNotFound.
	 * 
	 * @return Value of property useHeadIfNotFound.
	 */
	public boolean isUseHeadIfNotFound() {
		return useHeadIfNotFound;
	}

	/**
	 * Setter for property useHeadIfNotFound.
	 * 
	 * @param useHeadIfNotFound
	 *            New value of property useHeadIfNotFound.
	 */
	public void setUseHeadIfNotFound(boolean useHeadIfNotFound) {
		this.useHeadIfNotFound = useHeadIfNotFound;
	}

	/**
	 * Getter for property annotateByDate.
	 * 
	 * @return Value of property annotateByDate.
	 */
	public String getAnnotateByDate() {
		return annotateByDate;
	}

	/**
	 * Setter for property annotateByDate.
	 * 
	 * @param annotateByDate
	 *            New value of property annotateByDate.
	 */
	public void setAnnotateByDate(String annotateByDate) {
		this.annotateByDate = annotateByDate;
	}

	/**
	 * Getter for property annotateByRevision.
	 * 
	 * @return Value of property annotateByRevision.
	 */
	public String getAnnotateByRevision() {
		return annotateByRevision;
	}

	/**
	 * Setter for property annotateByRevision.
	 * 
	 * @param annotateByRevision
	 *            New value of property annotateByRevision.
	 */
	public void setAnnotateByRevision(String annotateByRevision) {
		this.annotateByRevision = annotateByRevision;
	}

	/**
	 * This method returns how the command would looklike when typed on the command line. Each command is responsible for constructing this
	 * information.
	 * 
	 * @returns <command's name> [<parameters>] files/dirs. Example: checkout -p CvsCommand.java
	 */
	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("annotate "); // NOI18N
		toReturn.append(getCVSArguments());
		File[] files = getFiles();
		if (files != null) {
			for (int index = 0; index < files.length; index++) {
				toReturn.append(files[index].getName() + " "); // NOI18N
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
		} else if (opt == 'r') {
			setAnnotateByRevision(optArg);
		} else if (opt == 'D') {
			setAnnotateByDate(optArg);
		} else if (opt == 'f') {
			setUseHeadIfNotFound(true);
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
		return "Rlr:D:f"; // NOI18N
	}

	/**
	 * resets all switches in the command. After calling this method, the command should have no switches defined and should behave
	 * defaultly.
	 */
	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setAnnotateByDate(null);
		setAnnotateByRevision(null);
		setUseHeadIfNotFound(false);
	}

	/**
	 * Returns the arguments of the command in the command-line style. Similar to getCVSCommand() however without the files and command's
	 * name
	 */
	@Override
	public String getCVSArguments() {
		StringBuffer toReturn = new StringBuffer(""); // NOI18N
		if (!isRecursive()) {
			toReturn.append("-l "); // NOI18N
		}
		if (getAnnotateByRevision() != null) {
			toReturn.append("-r "); // NOI18N
			toReturn.append(getAnnotateByRevision());
			toReturn.append(" "); // NOI18N
		}
		if (getAnnotateByDate() != null) {
			toReturn.append("-D "); // NOI18N
			toReturn.append(getAnnotateByDate());
			toReturn.append(" "); // NOI18N
		}
		if (isUseHeadIfNotFound()) {
			toReturn.append("-f "); // NOI18N
		}
		return toReturn.toString();
	}

}
