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
package org.netbeans.lib.cvsclient.command.log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.CommandRequest;

/**
 * The rlog command is similar to log, but doens't operate on currently checked out sources.
 * 
 * @author MIlos Kleint
 */
public class RlogCommand extends BasicCommand {

	/**
	 * The modules to rlog.
	 */
	private final List modules = new LinkedList();

	/**
	 * Holds value of property defaultBranch.
	 */
	private boolean defaultBranch;

	/**
	 * Holds value of property dateFilter.
	 */
	private String dateFilter;

	/**
	 * Holds value of property headerOnly.
	 */
	private boolean headerOnly;

	/**
	 * Holds value of property suppressHeader.
	 */
	private boolean suppressHeader;

	/**
	 * Holds value of property noTags.
	 */
	private boolean noTags;

	/**
	 * Holds value of property revisionFilter.
	 */
	private String revisionFilter;

	/**
	 * Holds value of property stateFilter.
	 */
	private String stateFilter;

	/**
	 * Holds value of property userFilter.
	 */
	private String userFilter;

	/**
	 * Holds value of property headerAndDescOnly.
	 */
	private boolean headerAndDescOnly;

	public RlogCommand() {
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

	/**
	 * Getter for property defaultBranch, equals the command-line CVS switch "-b".
	 * 
	 * @return Value of property defaultBranch.
	 */
	public boolean isDefaultBranch() {
		return defaultBranch;
	}

	/**
	 * Setter for property defaultBranch, equals the command-line CVS switch "-b".
	 * 
	 * @param defaultBranch
	 *            New value of property defaultBranch.
	 */
	public void setDefaultBranch(boolean defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	/**
	 * Getter for property dateFilter, equals the command-line CVS switch "-d".
	 * 
	 * @return Value of property dateFilter.
	 */
	public String getDateFilter() {
		return dateFilter;
	}

	/**
	 * Setter for property dateFilter, equals the command-line CVS switch "-d".
	 * 
	 * @param dateFilter
	 *            New value of property dateFilter.
	 */
	public void setDateFilter(String dateFilter) {
		this.dateFilter = dateFilter;
	}

	/**
	 * Getter for property headerOnly, equals the command-line CVS switch "-h".
	 * 
	 * @return Value of property headerOnly.
	 */
	public boolean isHeaderOnly() {
		return headerOnly;
	}

	/**
	 * Setter for property headerOnly, equals the command-line CVS switch "-h".
	 * 
	 * @param headerOnly
	 *            New value of property headerOnly.
	 */
	public void setHeaderOnly(boolean headerOnly) {
		this.headerOnly = headerOnly;
	}

	/**
	 * Getter for property suppressHeader, equals the command-line CVS switch "-S".
	 * 
	 * @return Value of property suppressHeader.
	 */
	public boolean isSuppressHeader() {
		return suppressHeader;
	}

	/**
	 * Setter for property headerOnly, equals the command-line CVS switch "-S".
	 * 
	 * @param suppressHeader
	 *            New value of property suppressHeader.
	 */
	public void setSuppressHeader(boolean suppressHeader) {
		this.suppressHeader = suppressHeader;
	}

	/**
	 * Getter for property noTags, equals the command-line CVS switch "-N".
	 * 
	 * @return Value of property noTags.
	 */
	public boolean isNoTags() {
		return noTags;
	}

	/**
	 * Setter for property noTags, equals the command-line CVS switch "-N".
	 * 
	 * @param noTags
	 *            New value of property noTags.
	 */
	public void setNoTags(boolean noTags) {
		this.noTags = noTags;
	}

	/**
	 * Getter for property revisionFilter, equals the command-line CVS switch "-r".
	 * 
	 * @return Value of property revisionFilter.
	 */
	public String getRevisionFilter() {
		return revisionFilter;
	}

	/**
	 * Setter for property revisionFilter, equals the command-line CVS switch "-r".
	 * 
	 * @param revisionFilter
	 *            New value of property revisionFilter. empty string means latest revision of default branch.
	 */
	public void setRevisionFilter(String revisionFilter) {
		this.revisionFilter = revisionFilter;
	}

	/**
	 * Getter for property stateFilter, equals the command-line CVS switch "-s".
	 * 
	 * @return Value of property stateFilter.
	 */
	public String getStateFilter() {
		return stateFilter;
	}

	/**
	 * Setter for property stateFilter, equals the command-line CVS switch "-s".
	 * 
	 * @param stateFilter
	 *            New value of property stateFilter.
	 */
	public void setStateFilter(String stateFilter) {
		this.stateFilter = stateFilter;
	}

	/**
	 * Getter for property userFilter, equals the command-line CVS switch "-w".
	 * 
	 * @return Value of property userFilter, empty string means the current user.
	 */
	public String getUserFilter() {
		return userFilter;
	}

	/**
	 * Setter for property userFilter, equals the command-line CVS switch "-w".
	 * 
	 * @param userFilter
	 *            New value of property userFilter.
	 */
	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}

	/**
	 * Getter for property headerAndDescOnly, equals the command-line CVS switch "-t".
	 * 
	 * @return Value of property headerAndDescOnly.
	 */
	public boolean isHeaderAndDescOnly() {
		return headerAndDescOnly;
	}

	/**
	 * Setter for property headerAndDescOnly, equals the command-line CVS switch "-t".
	 * 
	 * @param headerAndDescOnly
	 *            New value of property headerAndDescOnly.
	 */
	public void setHeaderAndDescOnly(boolean headerAndDescOnly) {
		this.headerAndDescOnly = headerAndDescOnly;
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
		super.execute(client, em);

		//
		// moved modules code to the end of the other arguments --GAR
		//
		if (!isRecursive()) {
			requests.add(new ArgumentRequest("-l")); // NOI18N
		}
		// first send out all possible parameters..
		if (defaultBranch) {
			requests.add(new ArgumentRequest("-b")); // NOI18N
		}
		if (headerAndDescOnly) {
			requests.add(new ArgumentRequest("-t")); // NOI18N
		}
		if (headerOnly) {
			requests.add(new ArgumentRequest("-h")); // NOI18N
		}
		if (suppressHeader) {
			requests.add(new ArgumentRequest("-S")); // NOI18IN
		}
		if (noTags) {
			requests.add(new ArgumentRequest("-N")); // NOI18N
		}
		if (userFilter != null) {
			requests.add(new ArgumentRequest("-w" + userFilter)); // NOI18N
		}
		if (revisionFilter != null) {
			requests.add(new ArgumentRequest("-r" + revisionFilter)); // NOI18N
		}
		if (stateFilter != null) {
			requests.add(new ArgumentRequest("-s" + stateFilter)); // NOI18N
		}
		if (dateFilter != null) {
			requests.add(new ArgumentRequest("-d" + dateFilter)); // NOI18N
		}

		for (Iterator it = modules.iterator(); it.hasNext();) {
			String module = (String) it.next();
			requests.add(new ArgumentRequest(module));
		}

		requests.add(CommandRequest.RLOG);
		try {
			client.processRequests(requests);
			requests.clear();

		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		}
	}

	/**
	 * Don't send status of local files prior to executing command, as it's not needed.
	 */
	@Override
	protected boolean assumeLocalPathWhenUnspecified() {
		return false;
	}

	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("rlog "); // NOI18N
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
		if (isDefaultBranch()) {
			toReturn.append("-b "); // NOI18N
		}
		if (isHeaderAndDescOnly()) {
			toReturn.append("-t "); // NOI18N
		}
		if (isHeaderOnly()) {
			toReturn.append("-h "); // NOI18N
		}
		if (isSuppressHeader()) {
			toReturn.append("-S "); // NOI18N
		}
		if (isNoTags()) {
			toReturn.append("-N "); // NOI18N
		}
		if (!isRecursive()) {
			toReturn.append("-l "); // NOI18N
		}
		if (userFilter != null) {
			toReturn.append("-w"); // NOI18N
			toReturn.append(userFilter);
			toReturn.append(' ');
		}
		if (revisionFilter != null) {
			toReturn.append("-r"); // NOI18N
			toReturn.append(revisionFilter);
			toReturn.append(' ');
		}
		if (stateFilter != null) {
			toReturn.append("-s"); // NOI18N
			toReturn.append(stateFilter);
			toReturn.append(' ');
		}
		if (dateFilter != null) {
			toReturn.append("-d"); // NOI18N
			toReturn.append(dateFilter);
			toReturn.append(' ');
		}
		return toReturn.toString();
	}

	@Override
	public boolean setCVSCommand(char opt, String optArg) {
		if (opt == 'R') {
			setRecursive(true);
		} else if (opt == 'l') {
			setRecursive(false);
		} else if (opt == 'b') {
			setDefaultBranch(true);
		} else if (opt == 'h') {
			setHeaderOnly(true);
		} else if (opt == 't') {
			setHeaderAndDescOnly(true);
		} else if (opt == 'S') {
			setSuppressHeader(true);
		} else if (opt == 'N') {
			setNoTags(true);
		} else if (opt == 'd') {
			setDateFilter(optArg);
		} else if (opt == 'r') {
			setRevisionFilter(optArg == null ? "" : optArg); // NOI18N
			// for switches with optional args do that.. ^^^^
		} else if (opt == 's') {
			setStateFilter(optArg);
		} else if (opt == 'w') {
			setUserFilter(optArg == null ? "" : optArg); // NOI18N
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setDefaultBranch(false);
		setHeaderOnly(false);
		setHeaderAndDescOnly(false);
		setSuppressHeader(false);
		setNoTags(false);
		setDateFilter(null);
		setRevisionFilter(null);
		setStateFilter(null);
		setUserFilter(null);
	}

	/**
	 * String returned by this method defines which options are available for this particular command
	 */
	@Override
	public String getOptString() {
		return "RlbhStNd:r:s:w:"; // NOI18N4
	}

	/**
	 * Create a builder for this command.
	 * 
	 * @param eventMan
	 *            the event manager used to receive events.
	 */
	@Override
	public Builder createBuilder(EventManager eventMan) {
		return new LogBuilder(eventMan, this);
	}
}
