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

import org.openflexo.dataimporter.DataImporter;
import org.openflexo.dataimporter.DataImporterLoader;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.RationalRoseRepository;
import org.openflexo.localization.FlexoLocalization;

public class ImportRationalRoseRepository extends CreateDMRepository<ImportRationalRoseRepository> {

	static final Logger logger = Logger.getLogger(ImportRationalRoseRepository.class.getPackage().getName());

	public static FlexoActionType<ImportRationalRoseRepository, DMObject, DMObject> actionType = new FlexoActionType<ImportRationalRoseRepository, DMObject, DMObject>(
			"import_rational_rose", FlexoActionType.importMenu, FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ImportRationalRoseRepository makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new ImportRationalRoseRepository(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return DataImporterLoader.KnownDataImporter.RATIONAL_ROSE_IMPORTER.isAvailable();
		}

		@Override
		protected boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

	};

	ImportRationalRoseRepository(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		logger.info("Importing from RationalRose...");
		DataImporter rationalRoseImporter = DataImporterLoader.KnownDataImporter.RATIONAL_ROSE_IMPORTER.getImporter();
		if (rationalRoseImporter != null) {
			Object[] params = new Object[3];
			params[0] = getNewRepositoryName();
			params[1] = getRationalRosePackageName();
			params[2] = this;
			makeFlexoProgress(FlexoLocalization.localizedForKey("importing") + " " + getRationalRoseFile().getName(), 4);
			_newRepository = (RationalRoseRepository) rationalRoseImporter.importInProject(getProject(), getRationalRoseFile(), params);
			hideFlexoProgress();
		} else {
			logger.warning("Sorry, data importer " + DataImporterLoader.KnownDataImporter.RATIONAL_ROSE_IMPORTER + " not found ");
		}
		logger.info("Importing from RationalRose... DONE.");
	}

	@Override
	public String getRepositoryType() {
		return RATIONAL_ROSE_REPOSITORY;
	}

}
