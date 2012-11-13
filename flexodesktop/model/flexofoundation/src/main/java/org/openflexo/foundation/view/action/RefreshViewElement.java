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
package org.openflexo.foundation.view.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;

/**
 * This action is called to force refresh elements, by resetting graphical representation to those defined in EditionPattern
 * 
 * @author sylvain
 * 
 */
public class RefreshViewElement extends FlexoAction<RefreshViewElement, ViewObject, ViewObject> {

	private static final Logger logger = Logger.getLogger(RefreshViewElement.class.getPackage().getName());

	public static FlexoActionType<RefreshViewElement, ViewObject, ViewObject> actionType = new FlexoActionType<RefreshViewElement, ViewObject, ViewObject>(
			"refresh", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RefreshViewElement makeNewAction(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
			return new RefreshViewElement(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(RefreshViewElement.actionType, View.class);
		FlexoModelObject.addActionForClass(RefreshViewElement.actionType, ViewShape.class);
		FlexoModelObject.addActionForClass(RefreshViewElement.actionType, ViewConnector.class);
	}

	RefreshViewElement(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Refresh view elements");
		refresh(getFocusedObject());

	}

	private void refresh(ViewObject objectToBeRefreshed) {
		if (objectToBeRefreshed instanceof ViewElement) {
			((ViewElement) objectToBeRefreshed).resetGraphicalRepresentation();
		}
		for (ViewObject o : objectToBeRefreshed.getChilds()) {
			refresh(o);
		}
	}

}