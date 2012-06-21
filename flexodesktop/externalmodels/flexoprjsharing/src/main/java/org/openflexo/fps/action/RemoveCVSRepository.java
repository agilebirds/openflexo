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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.FlexoAuthentificationException;

public class RemoveCVSRepository extends CVSAction<RemoveCVSRepository, CVSRepository> {

	private static final Logger logger = Logger.getLogger(RemoveCVSRepository.class.getPackage().getName());

	public static FlexoActionType<RemoveCVSRepository, CVSRepository, FPSObject> actionType = new FlexoActionType<RemoveCVSRepository, CVSRepository, FPSObject>(
			"remove_CVS_repository", FlexoActionType.defaultGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RemoveCVSRepository makeNewAction(CVSRepository focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new RemoveCVSRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CVSRepository object, Vector<FPSObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(CVSRepository object, Vector<FPSObject> globalSelection) {
			return (object != null);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, CVSRepository.class);
	}

	RemoveCVSRepository(CVSRepository focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException, FlexoAuthentificationException {
		logger.info("RemoveCVSRepository");
		getFocusedObject().delete();
	}

}
