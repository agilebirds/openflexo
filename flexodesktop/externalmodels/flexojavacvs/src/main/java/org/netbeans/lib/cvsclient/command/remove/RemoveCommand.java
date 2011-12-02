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
package org.netbeans.lib.cvsclient.command.remove;

import java.io.File;
import java.io.IOException;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.request.CommandRequest;
import org.netbeans.lib.cvsclient.util.BugLog;

/**
 * The remove command is used to remove files and directories from the repository.
 * 
 * @author Robert Greig
 */
public class RemoveCommand extends BasicCommand {
	/**
	 * If true, will delete the file in working dir before it gets removed.
	 */
	private boolean deleteBeforeRemove;

	private boolean ignoreLocallyExistingFiles;

	/**
	 * Returns true if the local files will be deleted automatically.
	 */
	public boolean isDeleteBeforeRemove() {
		return deleteBeforeRemove;
	}

	/**
	 * Sets whether the local files will be deleted before.
	 */
	public void setDeleteBeforeRemove(boolean deleteBeforeRemove) {
		this.deleteBeforeRemove = deleteBeforeRemove;
	}

	/**
	 * Returns true to indicate that locally existing files are treated as they would not exist. This is a extension to the standard
	 * cvs-behaviour!
	 * 
	 * @deprecated
	 */
	@Deprecated
	public boolean doesIgnoreLocallyExistingFiles() {
		return ignoreLocallyExistingFiles;
	}

	/**
	 * Returns true to indicate that locally existing files are treated as they would not exist. This is a extension to the standard
	 * cvs-behaviour!
	 */
	public boolean isIgnoreLocallyExistingFiles() {
		return ignoreLocallyExistingFiles;
	}

	/**
	 * Sets whether locally existing files will be treated as they were deleted before. This is a extension to the standard cvs-behaviour!
	 */
	public void setIgnoreLocallyExistingFiles(boolean ignoreLocallyExistingFiles) {
		this.ignoreLocallyExistingFiles = ignoreLocallyExistingFiles;
	}

	/**
	 * Method that is called while the command is being executed. Descendants can override this method to return a Builder instance that
	 * will parse the server's output and create data structures.
	 */
	@Override
	public Builder createBuilder(EventManager eventMan) {
		return new RemoveBuilder(eventMan, this);
	}

	/**
	 * Executes this command.
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 */
	@Override
	public void execute(ClientServices client, EventManager em) throws CommandException, AuthenticationException {
		if (files == null || files.length == 0) {
			throw new CommandException("No files have been specified for " + // NOI18N
					"removal.", CommandException.getLocalMessage("RemoveCommand.noFilesSpecified", null)); // NOI18N
		}

		client.ensureConnection();

		if (isDeleteBeforeRemove()) {
			removeAll(files);
		}
		super.execute(client, em);

		try {
			addRequestForWorkingDirectory(client);
			addArgumentRequests();
			addRequest(CommandRequest.REMOVE);

			client.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		} finally {
			requests.clear();
		}
	}

	@Override
	protected void sendEntryAndModifiedRequests(Entry entry, File file) {
		super.sendEntryAndModifiedRequests(entry, isIgnoreLocallyExistingFiles() ? null : file);
		if (entry.getRevision().equals("0")) {
			// zero means a locally added file, not yet commited.
			try {
				clientServices.removeEntry(file);
			} catch (IOException exc) {
				BugLog.getInstance().showException(exc);
			}

		}
	}

	/**
	 * This method returns how the command would looks like when typed on the command line. Each command is responsible for constructing
	 * this information.
	 * 
	 * @returns <command's name> [<parameters>] files/dirs. Example: checkout -p CvsCommand.java
	 */
	@Override
	public String getCVSCommand() {
		StringBuffer toReturn = new StringBuffer("remove "); // NOI18N
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
	 * Takes the arguments and sets the command. To be mainly used for automatic settings (like parsing the .cvsrc file).
	 * 
	 * @return true if the option (switch) was recognized and set
	 */
	@Override
	public boolean setCVSCommand(char opt, String optArg) {
		if (opt == 'l') {
			setRecursive(false);
		} else if (opt == 'R') {
			setRecursive(true);
		} else if (opt == 'f') {
			setDeleteBeforeRemove(true);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Deletes all files being removed from the working directory. Doesn't delete directories. Attempts a recursive delete
	 * 
	 * @throws CommandException
	 *             - in case the file cannot be deleted.
	 */
	private void removeAll(File[] filesToDel) throws CommandException {
		if (filesToDel == null) {
			return;
		}
		for (int index = 0; index < filesToDel.length; index++) {
			File file = filesToDel[index];
			if (file.exists() && file.isFile()) {
				if (!file.delete()) {
					throw new CommandException("Cannot delete file " + file.getAbsolutePath(), // NOI18N
							CommandException.getLocalMessage("RemoveCommand.cannotDelete", new Object[] { file.getAbsolutePath() })); // NOI18N
				}
			} else {
				// For directories remove only it's files.
				// Preserve the cvs structure though.
				if (isRecursive() && !file.getName().equalsIgnoreCase("CVS")) { // NOI18N
					removeAll(file.listFiles());
				}
			}
		}
	}

	/**
	 * String returned by this method defines which options are available for this particular command
	 */
	@Override
	public String getOptString() {
		return "flR"; // NOI18N
	}

	/**
	 * resets all switches in the command. After calling this method, the command should have no switches defined and should behave
	 * defaultly.
	 */
	@Override
	public void resetCVSCommand() {
		setRecursive(true);
		setDeleteBeforeRemove(false);
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
		if (isDeleteBeforeRemove()) {
			toReturn.append("-f "); // NOI18N
		}
		return toReturn.toString();
	}
}
