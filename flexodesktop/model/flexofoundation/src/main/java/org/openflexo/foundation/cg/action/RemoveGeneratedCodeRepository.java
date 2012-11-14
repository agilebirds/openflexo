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
package org.openflexo.foundation.cg.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.localization.FlexoLocalization;

public class RemoveGeneratedCodeRepository extends AbstractGCAction<RemoveGeneratedCodeRepository, GenerationRepository> {

	private static final Logger logger = Logger.getLogger(RemoveGeneratedCodeRepository.class.getPackage().getName());

	public static FlexoActionType<RemoveGeneratedCodeRepository, GenerationRepository, CGObject> actionType = new FlexoActionType<RemoveGeneratedCodeRepository, GenerationRepository, CGObject>(
			"delete_repository", FlexoActionType.defaultGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RemoveGeneratedCodeRepository makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection,
				FlexoEditor editor) {
			return new RemoveGeneratedCodeRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return object != null;
		}

	};

	private boolean deleteFiles;

	RemoveGeneratedCodeRepository(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException, RepositoryCannotBeDeletedException {
		logger.info("Remove GeneratedCodeRepository");
		if (getFocusedObject() instanceof DGRepository) {
			if (((DGRepository) getFocusedObject()).getRepositoriedUsingAsReader().size() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(FlexoLocalization.localizedForKey("repository_cannot_be_deleted_because_it_used_by_the_following_repositories"))
						.append(":\n");
				for (CGRepository r : ((DGRepository) getFocusedObject()).getRepositoriedUsingAsReader()) {
					sb.append("* ").append(r.getName()).append("\n");
				}
				throw new RepositoryCannotBeDeletedException(sb.toString());
			}
		}
		getFocusedObject().delete(
				makeFlexoProgress(FlexoLocalization.localizedForKey("removing_repository") + " " + getFocusedObject().getName(),
						getFocusedObject().getFiles().size()), deleteFiles);
	}

	public boolean getDeleteFiles() {
		return deleteFiles;
	}

	public void setDeleteFiles(boolean deleteFiles) {
		this.deleteFiles = deleteFiles;
	}

}
