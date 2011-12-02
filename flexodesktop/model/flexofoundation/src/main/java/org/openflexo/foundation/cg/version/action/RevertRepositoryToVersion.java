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
package org.openflexo.foundation.cg.version.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;

public class RevertRepositoryToVersion extends AbstractGCAction<RevertRepositoryToVersion, GenerationRepository> {

	private static final Logger logger = Logger.getLogger(RevertRepositoryToVersion.class.getPackage().getName());

	public static FlexoActionType<RevertRepositoryToVersion, GenerationRepository, CGObject> actionType = new FlexoActionType<RevertRepositoryToVersion, GenerationRepository, CGObject>(
			"revert_all_repository_to_version", versionningMenu, versionningActionsGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RevertRepositoryToVersion makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection,
				FlexoEditor editor) {
			return new RevertRepositoryToVersion(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return (object != null);
		}

	};

	RevertRepositoryToVersion(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException {
		logger.info("Revert all repository to version");

		/*	makeFlexoProgress(FlexoLocalization.localizedForKey("release_as") +  " "
					+ getVersionIdentifier().versionAsString(), getFocusedObject().getFiles().size()+1);

		    _newCGRelease = new CGRelease(getFocusedObject());
		    _newCGRelease.setName(getName());
		    _newCGRelease.setDescription(getDescription());
		    _newCGRelease.setDate(getDate());
		    _newCGRelease.setUserId(getUserId());
		    _newCGRelease.setVersionIdentifier(getVersionIdentifier());
		    getFocusedObject().addToReleases(_newCGRelease);
		    
		    for (CGFile file : getFocusedObject().getFiles()) {
				setProgress(FlexoLocalization.localizedForKey("release") +  " " + file.getFileName());
		    	file.releaseAs(_newCGRelease);
		    }
		    
			// Refreshing repository
		    getFocusedObject().refresh();

			hideFlexoProgress();
		*/
	}

}
