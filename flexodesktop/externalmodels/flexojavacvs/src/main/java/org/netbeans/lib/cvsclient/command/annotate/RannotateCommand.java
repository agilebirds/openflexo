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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.CommandRequest;
import org.netbeans.lib.cvsclient.request.DirectoryRequest;
import org.netbeans.lib.cvsclient.request.ExpandModulesRequest;
import org.netbeans.lib.cvsclient.request.RootRequest;

/**
 * The rannotate command is similar to anootate, but doens't operate on currently checked out sources.
 * 
 * 
 * @author MIlos Kleint
 */
public class RannotateCommand extends BasicCommand {

	/**
	 * The modules to checkout. These names are unexpanded and will be passed to a module-expansion request.
	 */
	private final List modules = new LinkedList();

	/**
	 * The expanded modules.
	 */
	private final List expandedModules = new LinkedList();

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
	 * Holds value of property headerAndDescOnly.
	 */
	private boolean headerAndDescOnly;

	public RannotateCommand() {
		resetCVSCommand();
	}

	/**
	 * Set the modules to export.
	 * 
	 * @param theModules
	 *            the names of the modules to export
	 */
	public void setModule(String module) {
		modules.add(module);
	}

	/**
	 * clears the list of modules for export.
	 */

	public void clearModules() {
		this.modules.clear();
	}

	/**
	 * Set the modules to export.
	 * 
	 * @param theModules
	 *            the names of the modules to export
	 */
	public void setModules(String[] modules) {
		clearModules();
		if (modules == null) {
			return;
		}
		for (int i = 0; i < modules.length; i++) {
			String module = modules[i];
			this.modules.add(module);
		}
	}

	public String[] getModules() {
		String[] mods = new String[modules.size()];
		mods = (String[]) modules.toArray(mods);
		return mods;
	}

	private void processExistingModules(String localPath) {
		if (expandedModules.size() == 0) {
			return;
		}

		String[] directories = new String[expandedModules.size()];
		directories = (String[]) expandedModules.toArray(directories);
		setModules(directories);
	}

	/**
	 * Execute this command.
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 */
	@Override
	public void execute(ClientServices client, EventManager em) throws CommandException, AuthenticationException {

		client.ensureConnection();

		requests = new LinkedList();
		if (client.isFirstCommand()) {
			requests.add(new RootRequest(client.getRepository()));
		}
		for (Iterator it = modules.iterator(); it.hasNext();) {
			String module = (String) it.next();
			requests.add(new ArgumentRequest(module));
		}
		expandedModules.clear();
		requests.add(new DirectoryRequest(".", client.getRepository())); // NOI18N
		requests.add(new ExpandModulesRequest());
		try {
			client.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		}
		requests.clear();
		postExpansionExecute(client, em);
	}

	/**
	 * This is called when the server has responded to an expand-modules request.
	 */
	@Override
	public void moduleExpanded(ModuleExpansionEvent e) {
		expandedModules.add(e.getModule());
	}

	/**
	 * Execute this command
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 */
	private void postExpansionExecute(ClientServices client, EventManager em) throws CommandException, AuthenticationException {

		// processExistingModules(client.getLocalPath());
		super.execute(client, em);

		//
		// moved modules code to the end of the other arguments --GAR
		//
		if (!isRecursive()) {
			requests.add(1, new ArgumentRequest("-l")); // NOI18N
		}
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

		for (Iterator it = modules.iterator(); it.hasNext();) {
			String module = (String) it.next();
			requests.add(new ArgumentRequest(module));
		}

		requests.add(new DirectoryRequest(".", client.getRepository())); // NOI18N
		requests.add(CommandRequest.RANNOTATE);
		try {
			client.processRequests(requests);
			requests.clear();

		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		}
	}

	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("rannotate "); // NOI18N
		toReturn.append(getCVSArguments());
		if (modules != null && modules.size() > 0) {
			for (Iterator it = modules.iterator(); it.hasNext();) {
				String module = (String) it.next();
				toReturn.append(module);
				toReturn.append(' ');
			}
		} else {
			String localizedMsg = CommandException.getLocalMessage("ExportCommand.moduleEmpty.text"); // NOI18N
			toReturn.append(" "); // NOI18N
			toReturn.append(localizedMsg);
		}
		return toReturn.toString();
	}

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

	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setAnnotateByDate(null);
		setAnnotateByRevision(null);
		setUseHeadIfNotFound(false);
	}

	/**
	 * String returned by this method defines which options are available for this particular command
	 */
	@Override
	public String getOptString() {
		return "Rlr:D:f"; // NOI18N
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

}
