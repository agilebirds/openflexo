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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.viewpoint.ViewPointObject;

public class ShowFMLRepresentation extends FlexoGUIAction<ShowFMLRepresentation, ViewPointObject, ViewPointObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowFMLRepresentation.class.getPackage().getName());

	public static FlexoActionType<ShowFMLRepresentation, ViewPointObject, ViewPointObject> actionType = new FlexoActionType<ShowFMLRepresentation, ViewPointObject, ViewPointObject>(
			"show_flexo_modelling_language_representation", FlexoActionType.inspectGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ShowFMLRepresentation makeNewAction(ViewPointObject focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new ShowFMLRepresentation(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewPointObject object, Vector<ViewPointObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewPointObject object, Vector<ViewPointObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoObject.addActionForClass(ShowFMLRepresentation.actionType, ViewPointObject.class);
	}

	protected ShowFMLRepresentation(ViewPointObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}