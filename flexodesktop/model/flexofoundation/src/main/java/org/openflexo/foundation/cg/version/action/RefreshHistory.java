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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;

public class RefreshHistory extends AbstractGCAction<RefreshHistory, CGObject> {

	private static final Logger logger = Logger.getLogger(RefreshHistory.class.getPackage().getName());

	public static FlexoActionType<RefreshHistory, CGObject, CGObject> actionType = new FlexoActionType<RefreshHistory, CGObject, CGObject>(
			"refresh_history", versionningMenu, versionningCleanGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RefreshHistory makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new RefreshHistory(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) {
			Vector<CGObject> topLevelObjects = getSelectedTopLevelObjects(focusedObject, globalSelection);
			for (CGObject obj : topLevelObjects) {
				if (obj instanceof GeneratedOutput)
					return false;
			}
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) {
			GenerationRepository repository = getRepository(focusedObject, globalSelection);
			if (repository == null)
				return false;
			return repository.getManageHistory();
		}

	};

	static {
		FlexoModelObject.addActionForClass(RefreshHistory.actionType, CGObject.class);
	}

	RefreshHistory(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws SaveResourceException, FlexoException {
		logger.info("Refresh history");
		for (CGFile file : getSelectedFiles()) {
			logger.info("Clean for file " + file.getFileName());
			if (file.getGeneratedResourceData() instanceof AbstractGeneratedFile)
				((AbstractGeneratedFile) file.getGeneratedResourceData()).getHistory().refresh();
		}
	}

}
