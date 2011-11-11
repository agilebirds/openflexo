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
package org.openflexo.generator.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.action.GCAction;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;

public class ImportInModel extends GCAction<ImportInModel, CGObject> {

	private static final Logger logger = Logger.getLogger(ImportInModel.class.getPackage().getName());

	public static FlexoActionType<ImportInModel, CGObject, CGObject> actionType = new FlexoActionType<ImportInModel, CGObject, CGObject>(
			"import_in_model", MODEL_MENU, MODEL_GROUP1, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public ImportInModel makeNewAction(CGObject object, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ImportInModel(object, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return (object instanceof AbstractCGFile);
		}

		@Override
		protected boolean isEnabledForSelection(CGObject object, Vector<CGObject> globalSelection) {
			GenerationRepository repository = getRepository(object, globalSelection);
			if (!(repository instanceof CGRepository))
				return false;
			ProjectGenerator pg = (ProjectGenerator) getProjectGenerator(repository);
			return pg != null && pg.hasBeenInitialized();
		}

	};

	static {
		FlexoModelObject.addActionForClass(ImportInModel.actionType, CGObject.class);
	}

	ImportInModel(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Import in model");

		ProjectGenerator pg = (ProjectGenerator) getProjectGenerator();
		pg.setAction(this);
		// Refresh repository
		getRepository().refresh();
		((CGRepository) getRepository()).clearAllJavaParsingData();
	}

}
