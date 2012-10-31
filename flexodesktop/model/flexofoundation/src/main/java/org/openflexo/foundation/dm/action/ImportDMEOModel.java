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

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.ExternalDatabaseRepository;
import org.openflexo.foundation.dm.ProjectDatabaseRepository;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.EOModelAlreadyRegisteredException;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.dm.eo.UnresovedEntitiesException;
import org.openflexo.foundation.rm.InvalidFileNameException;

public class ImportDMEOModel extends FlexoAction {

	private static final Logger logger = Logger.getLogger(ImportDMEOModel.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("import_eomodel", FlexoActionType.importMenu,
			FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new ImportDMEOModel(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return object instanceof DMEORepository;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, ExternalDatabaseRepository.class);
		FlexoModelObject.addActionForClass(actionType, ProjectDatabaseRepository.class);
	}

	ImportDMEOModel(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private File _eoModelFile;
	private DMEOModel _newDMEOModel;
	private DMEORepository _repository;

	@Override
	protected void doAction(Object context) throws InvalidEOModelFileException, EOAccessException, EOModelAlreadyRegisteredException,
			InvalidFileNameException, UnresovedEntitiesException {
		logger.info("CreateDMEOModel");
		if (getRepository() != null) {
			_newDMEOModel = getRepository().copyAndImportEOModel(getEOModelFile());
		}
	}

	public DMEORepository getRepository() {
		if (_repository == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DMEORepository) {
				_repository = (DMEORepository) getFocusedObject();
			}
		}
		return _repository;
	}

	public DMEOModel getNewDMEOModel() {
		return _newDMEOModel;
	}

	public File getEOModelFile() {
		return _eoModelFile;
	}

	public void setEOModelFile(File eoModelFile) {
		_eoModelFile = eoModelFile;
	}

}
