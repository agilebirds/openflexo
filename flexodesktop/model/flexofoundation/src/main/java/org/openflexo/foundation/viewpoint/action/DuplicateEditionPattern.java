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
package org.openflexo.foundation.viewpoint.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPointObject;

@SuppressWarnings("serial")
public class DuplicateEditionPattern extends FlexoAction<DuplicateEditionPattern, EditionPattern, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(DuplicateEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DuplicateEditionPattern, EditionPattern, ViewPointObject> actionType = new FlexoActionType<DuplicateEditionPattern, EditionPattern, ViewPointObject>(
			"duplicate_edition_pattern", FlexoActionType.editGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DuplicateEditionPattern makeNewAction(EditionPattern focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new DuplicateEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(EditionPattern object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(EditionPattern object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DuplicateEditionPattern.actionType, EditionPattern.class);
	}

	DuplicateEditionPattern(EditionPattern focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public String newName;
	public EditionPattern newEditionPattern;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Duplicate edition pattern");

		newEditionPattern = getFocusedObject().duplicate(newName);
	}

}