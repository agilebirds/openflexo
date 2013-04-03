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
import org.openflexo.foundation.viewpoint.VirtualModel;

public class AddEditionPattern extends FlexoAction<AddEditionPattern, VirtualModel, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(AddEditionPattern.class.getPackage().getName());

	public static FlexoActionType<AddEditionPattern, VirtualModel, ViewPointObject> actionType = new FlexoActionType<AddEditionPattern, VirtualModel, ViewPointObject>(
			"add_new_edition_pattern", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddEditionPattern makeNewAction(VirtualModel focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new AddEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddEditionPattern.actionType, VirtualModel.class);
	}

	private String _newEditionPatternName;
	private EditionPattern _newEditionPattern;

	public boolean switchNewlyCreatedEditionPattern = true;

	AddEditionPattern(VirtualModel focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add new edition pattern");

		_newEditionPattern = new EditionPattern(null);
		_newEditionPattern.setName(getNewEditionPatternName());
		getFocusedObject().addToEditionPatterns(_newEditionPattern);
	}

	public EditionPattern getNewEditionPattern() {
		return _newEditionPattern;
	}

	public String getNewEditionPatternName() {
		return _newEditionPatternName;
	}

	public void setNewEditionPatternName(String newEditionPatternName) {
		_newEditionPatternName = newEditionPatternName;
	}

}
