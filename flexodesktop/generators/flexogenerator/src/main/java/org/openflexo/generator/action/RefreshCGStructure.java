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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;

public class RefreshCGStructure extends GCAction<RefreshCGStructure, CGObject> {

	private static final Logger logger = Logger.getLogger(RefreshCGStructure.class.getPackage().getName());

	public static FlexoActionType<RefreshCGStructure, CGObject, CGObject> actionType = new FlexoActionType<RefreshCGStructure, CGObject, CGObject>(
			"refresh", REFRESH_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RefreshCGStructure makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new RefreshCGStructure(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) {
			if (focusedObject instanceof AbstractCGFileVersion)
				return false;
			return (focusedObject instanceof AbstractCGFile);
		}

		@Override
		protected boolean isEnabledForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) {
			GenerationRepository repository = getRepository(focusedObject, globalSelection);
			AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator(repository);
			return ((pg != null) && (pg.hasBeenInitialized()));
		}

	};

	static {
		FlexoModelObject.addActionForClass(RefreshCGStructure.actionType, CGObject.class);
	}

	RefreshCGStructure(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException {
		logger.info("Refresh CG structure");
		getRepository().refresh();
	}

}
