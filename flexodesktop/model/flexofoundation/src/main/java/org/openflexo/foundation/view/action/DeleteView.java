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
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.AbstractViewObject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewDefinition.DuplicateShemaNameException;
import org.openflexo.foundation.view.ViewLibraryObject;

public class DeleteView extends FlexoAction<DeleteView, AbstractViewObject, ViewLibraryObject> {

	private static final Logger logger = Logger.getLogger(DeleteView.class.getPackage().getName());

	public static FlexoActionType<DeleteView, AbstractViewObject, ViewLibraryObject> actionType = new FlexoActionType<DeleteView, AbstractViewObject, ViewLibraryObject>(
			"delete_view", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteView makeNewAction(AbstractViewObject focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
			return new DeleteView(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AbstractViewObject view, Vector<ViewLibraryObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(AbstractViewObject view, Vector<ViewLibraryObject> globalSelection) {
			return view instanceof View || view instanceof ViewDefinition;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteView.actionType, View.class);
		FlexoModelObject.addActionForClass(DeleteView.actionType, ViewDefinition.class);
	}

	DeleteView(AbstractViewObject focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException,
			DuplicateShemaNameException {
		logger.info("Delete view");

		if (getFocusedObject() instanceof View) {
			getFocusedObject().delete();
		} else if (getFocusedObject() instanceof ViewDefinition) {
			((ViewDefinition) getFocusedObject()).getView().delete();
		}

	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

}