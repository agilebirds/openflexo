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
import org.openflexo.foundation.action.ActionGroup;
import org.openflexo.foundation.action.ActionMenu;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSAbstractFile;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.SharedProject;

/**
 * Abstract class for actions applying on a set of CVSFile
 * 
 * @author sylvain
 * 
 */
public abstract class MultipleFileCVSAction<A extends MultipleFileCVSAction<A>> extends CVSAction<A, FPSObject> {

	private static final Logger logger = Logger.getLogger(MultipleFileCVSAction.class.getPackage().getName());

	public static abstract class MultipleFileCVSActionType<A extends MultipleFileCVSAction<A>> extends
			FlexoActionType<A, FPSObject, FPSObject> {
		protected MultipleFileCVSActionType(String actionName, ActionMenu actionMenu, ActionGroup actionGroup, int actionCategory) {
			super(actionName, actionMenu, actionGroup, actionCategory);
		}

		protected MultipleFileCVSActionType(String actionName, ActionGroup actionGroup, int actionCategory) {
			super(actionName, actionGroup, actionCategory);
		}

		@Override
		public boolean isVisibleForSelection(FPSObject focusedObject, Vector<FPSObject> globalSelection) {
			Vector<FPSObject> topLevelObjects = getSelectedTopLevelObjects(focusedObject, globalSelection);
			for (FPSObject obj : topLevelObjects) {
				if (!(obj instanceof CVSAbstractFile)) {
					return false;
				} else if (((CVSAbstractFile) obj).getSharedProject() == null) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FPSObject focusedObject, Vector<FPSObject> globalSelection) {
			SharedProject project = getSharedProject(focusedObject, globalSelection);
			if (project == null) {
				return false;
			}
			Vector<CVSFile> selectedFiles = getSelectedCVSFilesOnWhyCurrentActionShouldApply(focusedObject, globalSelection);
			return selectedFiles.size() > 0;
		}

		protected Vector<CVSFile> getSelectedCVSFilesOnWhyCurrentActionShouldApply(FPSObject focusedObject,
				Vector<FPSObject> globalSelection) {
			Vector<CVSFile> selectedFiles = getSelectedCVSFiles(focusedObject, globalSelection);
			Vector<CVSFile> returned = new Vector<CVSFile>();
			for (CVSFile file : selectedFiles) {
				if (accept(file)) {
					returned.add(file);
				}
			}
			return returned;
		}

		protected abstract boolean accept(CVSFile aFile);
	}

	MultipleFileCVSAction(MultipleFileCVSActionType<A> actionType, FPSObject focusedObject, Vector<FPSObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public MultipleFileCVSActionType<A> getActionType() {
		return (MultipleFileCVSActionType<A>) super.getActionType();
	}

	protected Vector<CVSFile> getSelectedCVSFilesOnWhyCurrentActionShouldApply() {
		return getActionType().getSelectedCVSFilesOnWhyCurrentActionShouldApply(getFocusedObject(), getGlobalSelection());
	}

}
