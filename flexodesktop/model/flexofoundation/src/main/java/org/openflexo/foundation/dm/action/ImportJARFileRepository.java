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
package org.openflexo.foundation.dm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.LibraryRepositoryFolder;
import org.openflexo.localization.FlexoLocalization;

public class ImportJARFileRepository extends CreateDMRepository<ImportJARFileRepository> {

	static final Logger logger = Logger.getLogger(ImportJARFileRepository.class.getPackage().getName());

	public static FlexoActionType<ImportJARFileRepository, DMObject, DMObject> actionType = new FlexoActionType<ImportJARFileRepository, DMObject, DMObject>(
			"import_jar_file", FlexoActionType.importMenu, FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ImportJARFileRepository makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new ImportJARFileRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMModel.class);
		FlexoModelObject.addActionForClass(actionType, LibraryRepositoryFolder.class);
	}

	ImportJARFileRepository(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getJarFile() != null) {
			makeFlexoProgress(FlexoLocalization.localizedForKey("importing") + " " + getJarFile().getName(), 5);
			setProgress(FlexoLocalization.localizedForKey("importing") + " " + getJarFile().getName());
			_newRepository = ExternalRepository.createNewExternalRepository(getProject().getDataModel(), getJarFile(),
					getImportedClassSet(), getFlexoProgress());
			((ExternalRepository) _newRepository).setIsImportedByUser(true);
			hideFlexoProgress();
		}
	}

	@Override
	public String getRepositoryType() {
		return EXTERNAL_REPOSITORY;
	}

}
