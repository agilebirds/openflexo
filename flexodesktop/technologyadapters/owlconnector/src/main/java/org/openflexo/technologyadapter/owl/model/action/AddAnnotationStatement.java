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
package org.openflexo.technologyadapter.owl.model.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.technologyadapter.owl.model.OWLConcept;

public class AddAnnotationStatement extends FlexoAction<AddAnnotationStatement, OWLConcept, OWLConcept> {

	private static final Logger logger = Logger.getLogger(AddAnnotationStatement.class.getPackage().getName());

	public static FlexoActionType<AddAnnotationStatement, OWLConcept, OWLConcept> actionType = new FlexoActionType<AddAnnotationStatement, OWLConcept, OWLConcept>(
			"add_annotation", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddAnnotationStatement makeNewAction(OWLConcept focusedObject, Vector<OWLConcept> globalSelection, FlexoEditor editor) {
			return new AddAnnotationStatement(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OWLConcept object, Vector<OWLConcept> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

		@Override
		public boolean isEnabledForSelection(OWLConcept object, Vector<OWLConcept> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(AddAnnotationStatement.actionType, OWLConcept.class);
	}

	AddAnnotationStatement(OWLConcept focusedObject, Vector<OWLConcept> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("AddAnnotationStatement on " + getFocusedObject());
	}

}
