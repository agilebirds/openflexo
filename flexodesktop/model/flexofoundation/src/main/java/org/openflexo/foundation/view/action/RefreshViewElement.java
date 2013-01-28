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
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.model.DiagramShape;

/**
 * This action is called to force refresh elements, by resetting graphical representation to those defined in EditionPattern
 * 
 * @author sylvain
 * 
 */
public class RefreshViewElement extends FlexoAction<RefreshViewElement, DiagramElement<?>, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(RefreshViewElement.class.getPackage().getName());

	public static FlexoActionType<RefreshViewElement, DiagramElement<?>, DiagramElement<?>> actionType = new FlexoActionType<RefreshViewElement, DiagramElement<?>, DiagramElement<?>>(
			"refresh", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RefreshViewElement makeNewAction(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection,
				FlexoEditor editor) {
			return new RefreshViewElement(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(RefreshViewElement.actionType, DiagramRootPane.class);
		FlexoModelObject.addActionForClass(RefreshViewElement.actionType, DiagramShape.class);
		FlexoModelObject.addActionForClass(RefreshViewElement.actionType, DiagramConnector.class);
	}

	RefreshViewElement(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Refresh view elements");
		refresh(getFocusedObject());

	}

	private void refresh(DiagramElement<?> objectToBeRefreshed) {
		if (objectToBeRefreshed instanceof DiagramElement) {
			((DiagramElement) objectToBeRefreshed).resetGraphicalRepresentation();
		}
		for (DiagramElement<?> o : objectToBeRefreshed.getChilds()) {
			refresh(o);
		}
	}

}