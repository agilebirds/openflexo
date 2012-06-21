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
package org.openflexo.foundation.ontology.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.OntologyObject;

public class AddAnnotationStatement extends FlexoAction<AddAnnotationStatement, OntologyObject, OntologyObject> {

	private static final Logger logger = Logger.getLogger(AddAnnotationStatement.class.getPackage().getName());

	public static FlexoActionType<AddAnnotationStatement, OntologyObject, OntologyObject> actionType = new FlexoActionType<AddAnnotationStatement, OntologyObject, OntologyObject>(
			"add_annotation", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddAnnotationStatement makeNewAction(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor) {
			return new AddAnnotationStatement(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OntologyObject object, Vector<OntologyObject> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

		@Override
		public boolean isEnabledForSelection(OntologyObject object, Vector<OntologyObject> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddAnnotationStatement.actionType, OntologyObject.class);
	}

	AddAnnotationStatement(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("AddAnnotationStatement on " + getFocusedObject());
	}

}
