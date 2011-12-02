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

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class CreateViewPointPalette extends FlexoAction<CreateViewPointPalette, ViewPoint, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateViewPointPalette.class.getPackage().getName());

	public static FlexoActionType<CreateViewPointPalette, ViewPoint, ViewPointObject> actionType = new FlexoActionType<CreateViewPointPalette, ViewPoint, ViewPointObject>(
			"create_new_palette", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateViewPointPalette makeNewAction(ViewPoint focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreateViewPointPalette(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewPoint object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(ViewPoint object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateViewPointPalette.actionType, ViewPoint.class);
	}

	public String newPaletteName;
	public String description;
	public Object graphicalRepresentation;

	private ViewPointPalette _newPalette;

	CreateViewPointPalette(ViewPoint focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add calc palette");

		_newPalette = ViewPointPalette.newCalcPalette(getFocusedObject(), new File(getFocusedObject().getViewPointDirectory(),
				newPaletteName + ".palette"), graphicalRepresentation);
		_newPalette.setDescription(description);
		getFocusedObject().addToCalcPalettes(_newPalette);
		_newPalette.save();

	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public ViewPointPalette getNewPalette() {
		return _newPalette;
	}

	private String nameValidityMessage = EMPTY_NAME;

	private static final String NAME_IS_VALID = FlexoLocalization.localizedForKey("name_is_valid");
	private static final String DUPLICATED_NAME = FlexoLocalization.localizedForKey("this_name_is_already_used_please_choose_an_other_one");
	private static final String EMPTY_NAME = FlexoLocalization.localizedForKey("empty_name");

	public String getNameValidityMessage() {
		return nameValidityMessage;
	}

	public boolean isNameValid() {
		if (StringUtils.isEmpty(newPaletteName)) {
			nameValidityMessage = EMPTY_NAME;
			return false;
		} else if (getFocusedObject().getPalette(newPaletteName) != null) {
			nameValidityMessage = DUPLICATED_NAME;
			return false;
		} else {
			nameValidityMessage = NAME_IS_VALID;
			return true;
		}
	}
}