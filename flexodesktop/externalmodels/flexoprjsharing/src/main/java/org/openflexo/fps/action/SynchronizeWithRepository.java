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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.SharedProject;
import org.openflexo.localization.FlexoLocalization;

public class SynchronizeWithRepository extends CVSAction<SynchronizeWithRepository, SharedProject> {

	private static final Logger logger = Logger.getLogger(SynchronizeWithRepository.class.getPackage().getName());

	public static FlexoActionType<SynchronizeWithRepository, SharedProject, FPSObject> actionType = new FlexoActionType<SynchronizeWithRepository, SharedProject, FPSObject>(
			"synchronize_with_repository", SYNCHRONIZE_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public SynchronizeWithRepository makeNewAction(SharedProject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new SynchronizeWithRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(SharedProject object, Vector<FPSObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(SharedProject object, Vector<FPSObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObject.addActionForClass(actionType, SharedProject.class);
	}

	SynchronizeWithRepository(SharedProject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException, FlexoAuthentificationException {
		makeFlexoProgress(FlexoLocalization.localizedForKey("synchronize_with_repository"), 6);
		getFocusedObject().synchronizeWithRepository(getFlexoProgress());
		waitRevisionRetrivingResponses();
		hideFlexoProgress();
	}

	private static final long TIME_OUT = 10000; // 10 s
	private long lastReception;

	private synchronized void waitRevisionRetrivingResponses() {
		Vector<CVSFile> filesBeingRetrieved = new Vector<CVSFile>();
		for (CVSFile f : getFocusedObject().getAllCVSFiles()) {
			if (f.getStatus().isConflicting()) {
				filesBeingRetrieved.add(f);
			}
		}

		setProgress(FlexoLocalization.localizedForKey("waiting_for_revision_retrieving"));
		resetSecondaryProgress(filesBeingRetrieved.size());

		lastReception = System.currentTimeMillis();
		timeOutReceived = false;

		while (filesBeingRetrieved.size() > 0 && System.currentTimeMillis() - lastReception < TIME_OUT) {
			synchronized (this) {
				try {
					wait(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (CVSFile f : (Vector<CVSFile>) filesBeingRetrieved.clone()) {
				if (!f.isReceivingContentOnRepository() && !f.isReceivingOriginalContent()) {
					filesBeingRetrieved.remove(f);
					lastReception = System.currentTimeMillis();
					setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + f.getFile().getName());
				}
			}
		}
		if (filesBeingRetrieved.size() > 0) {
			timeOutReceived = true;
			logger.warning("Commit finished with time-out expired: still waiting for " + filesBeingRetrieved.size() + " files");
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

}
