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
package org.openflexo.fps.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSAbstractFile;
import org.openflexo.fps.CVSAbstractFile.CommitListener;
import org.openflexo.fps.CVSAbstractFile.CommitStatus;
import org.openflexo.fps.CVSConstants;
import org.openflexo.fps.CVSContainer;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.FPSObject;
import org.openflexo.localization.FlexoLocalization;

public class CommitFiles extends MultipleFileCVSAction<CommitFiles> implements CommitListener {

	private static final Logger logger = Logger.getLogger(CommitFiles.class.getPackage().getName());

	public static MultipleFileCVSActionType<CommitFiles> actionType = new MultipleFileCVSActionType<CommitFiles>("cvs_commit",
			CVS_OPERATIONS_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CommitFiles makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new CommitFiles(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean accept(CVSFile aFile) {
			return aFile.getStatus().isLocallyModified() && !aFile.getStatus().isConflicting();
		}

	};

	static {
		FlexoObject.addActionForClass(CommitFiles.actionType, CVSAbstractFile.class);
	}

	CommitFiles(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Commit files");
		makeFlexoProgress(FlexoLocalization.localizedForKey("committing_files"), 4);
		setProgress("preparing_commit");
		Hashtable<CVSContainer, Vector<CVSFile>> filesToCommit = new Hashtable<CVSContainer, Vector<CVSFile>>();
		for (CVSFile f : getSelectedCVSFilesOnWhyCurrentActionShouldApply()) {
			CVSContainer dir = f.getContainer();
			Vector<CVSFile> entriesForDir = filesToCommit.get(dir);
			if (entriesForDir == null) {
				entriesForDir = new Vector<CVSFile>();
				filesToCommit.put(dir, entriesForDir);
			}
			entriesForDir.add(f);
		}

		sendCommitRequests(filesToCommit);

		waitResponses();

		hideFlexoProgress();
	}

	private synchronized void sendCommitRequests(Hashtable<CVSContainer, Vector<CVSFile>> filesToCommit) {
		setProgress(FlexoLocalization.localizedForKey("committing_files"));
		resetSecondaryProgress(filesToCommit.keySet().size());
		for (CVSContainer dir : filesToCommit.keySet()) {
			setSecondaryProgress(FlexoLocalization.localizedForKey("committing") + " "
					+ ((CVSAbstractFile) dir).getFile().getAbsolutePath());
			CVSFile[] filesToCommitInThisDir = filesToCommit.get(dir).toArray(new CVSFile[filesToCommit.get(dir).size()]);
			logger.info("Committing in " + dir);
			((CVSAbstractFile) dir).commit(getCommitMessage(), this, filesToCommitInThisDir);
			filesToWait += filesToCommitInThisDir.length;
		}
	}

	private void waitResponses() {
		setProgress(FlexoLocalization.localizedForKey("waiting_for_responses"));
		resetSecondaryProgress(filesToWait);

		lastReception = System.currentTimeMillis();

		while (filesToWait > 0 && System.currentTimeMillis() - lastReception < TIME_OUT) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (this) {
				while (filesToNotify.size() > 0) {
					CVSFile file = filesToNotify.firstElement();
					filesToNotify.removeElementAt(0);
					setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + file.getFile().getName());
				}
			}
		}

		if (filesToWait > 0) {
			timeOutReceived = true;
			logger.warning("Commit finished with time-out expired: still waiting for " + filesToWait + " files");
		}

	}

	@Override
	public boolean hasActionExecutionSucceeded() {
		if (timeOutReceived) {
			return false;
		} else {
			return super.hasActionExecutionSucceeded();
		}
	}

	private boolean timeOutReceived;
	private long lastReception;
	private static final long TIME_OUT = CVSConstants.TIME_OUT; // 60 s
	private int filesToWait = 0;

	private Vector<CVSFile> filesToNotify = new Vector<CVSFile>();

	@Override
	public synchronized void notifyCommitFinished(CVSFile file, CommitStatus status) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Commit for " + file.getFile() + " finished with status " + status);
		}
		filesToWait--;
		filesToNotify.add(file);
		lastReception = System.currentTimeMillis();
	}

	private String commitMessage = null;

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
}
