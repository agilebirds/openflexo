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
package org.netbeans.lib.cvsclient.command.tag;

import java.io.EOFException;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.RepositoryCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.CommandRequest;

/**
 * The rtag command adds or deletes a tag to the specified files/directories in the repository.
 * 
 * @author Martin Entlicher
 */
public class RtagCommand extends RepositoryCommand {
	/**
	 * The event manager to use.
	 */
	private EventManager eventManager;

	private boolean clearFromRemoved;

	private boolean deleteTag;

	private boolean makeBranchTag;

	private boolean overrideExistingTag;

	private boolean matchHeadIfRevisionNotFound;

	private boolean noExecTagProgram;

	private String tag;

	private String tagByDate;

	private String tagByRevision;

	/**
	 * Construct a new tag command.
	 */
	public RtagCommand() {
	}

	/**
	 * Creates the TagBuilder.
	 * 
	 * @param eventManager
	 *            the event manager used to received cvs events
	 */
	@Override
	public Builder createBuilder(EventManager eventManager) {
		return new TagBuilder(eventManager, getLocalDirectory());
	}

	/**
	 * Returns true if the tag from removed files is cleared.
	 */
	public boolean isClearFromRemoved() {
		return clearFromRemoved;
	}

	/**
	 * Clear tag from removed files
	 */
	public void setClearFromRemoved(boolean clearFromRemoved) {
		this.clearFromRemoved = clearFromRemoved;
	}

	/**
	 * Returns true if the tag should be deleted (otherwise added).
	 */
	public boolean isDeleteTag() {
		return deleteTag;
	}

	/**
	 * Sets whether the tag should be deleted (true) or added (false).
	 */
	public void setDeleteTag(boolean deleteTag) {
		this.deleteTag = deleteTag;
	}

	/**
	 * Returns true if the tag should be a branch tag.
	 */
	public boolean isMakeBranchTag() {
		return makeBranchTag;
	}

	/**
	 * Sets whether the tag should be a branch tag.
	 */
	public void setMakeBranchTag(boolean makeBranchTag) {
		this.makeBranchTag = makeBranchTag;
	}

	/**
	 * Returns true to indicate that existing tag will be overridden.
	 */
	public boolean isOverrideExistingTag() {
		return overrideExistingTag;
	}

	/**
	 * Sets whether existing tags should be overridden.
	 */
	public void setOverrideExistingTag(boolean overrideExistingTag) {
		this.overrideExistingTag = overrideExistingTag;
	}

	public boolean isMatchHeadIfRevisionNotFound() {
		return matchHeadIfRevisionNotFound;
	}

	public void setMatchHeadIfRevisionNotFound(boolean matchHeadIfRevisionNotFound) {
		this.matchHeadIfRevisionNotFound = matchHeadIfRevisionNotFound;
	}

	public boolean isNoExecTagProgram() {
		return noExecTagProgram;
	}

	public void setNoExecTagProgram(boolean noExecTagProgram) {
		this.noExecTagProgram = noExecTagProgram;
	}

	/**
	 * Returns the tag that should be added or deleted.
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Sets the tag that should be added or deleted.
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Returns the latest date of a revision to be tagged.
	 * 
	 * @return date value. the latest Revision not later ten date is tagged.
	 */
	public String getTagByDate() {
		return tagByDate;
	}

	/**
	 * Sets the latest date of a revision to be tagged.
	 * 
	 * @param tagDate
	 *            New value of property tagDate.
	 */
	public void setTagByDate(String tagDate) {
		tagByDate = tagDate;
	}

	/**
	 * Sets the latest date of a revision to be tagged. Can be both a number and a tag.
	 * 
	 * @return Value of property tagRevision.
	 */
	public String getTagByRevision() {
		return tagByRevision;
	}

	/**
	 * Sets the latest date of a revision to be tagged. Can be both a number and a tag.
	 * 
	 * @param tagRevision
	 *            New value of property tagRevision.
	 */
	public void setTagByRevision(String tagRevision) {
		tagByRevision = tagRevision;
	}

	/**
	 * Execute the command.
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests.
	 */
	@Override
	protected void postExpansionExecute(ClientServices client, EventManager eventManager) throws CommandException, AuthenticationException {
		client.ensureConnection();

		this.eventManager = eventManager;

		try {
			if (clearFromRemoved) {
				requests.add(new ArgumentRequest("-a")); // NOI18N
			}

			if (overrideExistingTag) {
				requests.add(new ArgumentRequest("-F")); // NOI18N
			}

			if (matchHeadIfRevisionNotFound) {
				requests.add(new ArgumentRequest("-f")); // NOI18N
			}

			if (makeBranchTag) {
				requests.add(new ArgumentRequest("-b")); // NOI18N
			}

			if (deleteTag) {
				requests.add(new ArgumentRequest("-d")); // NOI18N
			}

			if (noExecTagProgram) {
				requests.add(new ArgumentRequest("-n ")); // NOI18N
			}

			if (tagByDate != null && tagByDate.length() > 0) {
				requests.add(new ArgumentRequest("-D")); // NOI18N
				requests.add(new ArgumentRequest(getTagByDate()));
			}
			if (tagByRevision != null && tagByRevision.length() > 0) {
				requests.add(new ArgumentRequest("-r")); // NOI18N
				requests.add(new ArgumentRequest(getTagByRevision()));
			}

			requests.add(new ArgumentRequest(getTag()));

			// addRequestForWorkingDirectory(client);
			addArgumentRequests();
			addRequest(CommandRequest.RTAG);

			client.processRequests(requests);
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
		StringBuffer toReturn = new StringBuffer("rtag "); // NOI18N
		toReturn.append(getCVSArguments());
		if (getTag() != null) {
			toReturn.append(getTag());
			toReturn.append(" "); // NOI18N
		}
		appendModuleArguments(toReturn);
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
		} else if (opt == 'a') {
			setClearFromRemoved(true);
		} else if (opt == 'd') {
			setDeleteTag(true);
		} else if (opt == 'F') {
			setOverrideExistingTag(true);
		} else if (opt == 'f') {
			setMatchHeadIfRevisionNotFound(true);
		} else if (opt == 'b') {
			setMakeBranchTag(true);
		} else if (opt == 'n') {
			setNoExecTagProgram(true);
		} else if (opt == 'D') {
			setTagByDate(optArg.trim());
		} else if (opt == 'r') {
			setTagByRevision(optArg.trim());
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
		return "RlaFfbdnD:r:"; // NOI18N
	}

	/**
	 * Resets all switches in the command. After calling this method, the command should have no switches defined and should behave
	 * defaultly.
	 */
	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setClearFromRemoved(false);
		setDeleteTag(false);
		setMakeBranchTag(false);
		setOverrideExistingTag(false);
		setMatchHeadIfRevisionNotFound(false);
		setNoExecTagProgram(false);
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
		if (isClearFromRemoved()) {
			toReturn.append("-a "); // NOI18N
		}
		if (isOverrideExistingTag()) {
			toReturn.append("-F "); // NOI18N
		}
		if (isMatchHeadIfRevisionNotFound()) {
			toReturn.append("-f ");
		}
		if (isMakeBranchTag()) {
			toReturn.append("-b "); // NOI18N
		}
		if (isDeleteTag()) {
			toReturn.append("-d "); // NOI18N
		}
		if (isNoExecTagProgram()) {
			toReturn.append("-n "); // NOI18N
		}
		if (getTagByRevision() != null && getTagByRevision().length() > 0) {
			toReturn.append("-r "); // NOI18N
			toReturn.append(getTagByRevision());
			toReturn.append(" "); // NOI18N
		}
		if (getTagByDate() != null && getTagByDate().length() > 0) {
			toReturn.append("-D "); // NOI18N
			toReturn.append(getTagByDate());
			toReturn.append(" "); // NOI18N
		}
		return toReturn.toString();
	}
}
