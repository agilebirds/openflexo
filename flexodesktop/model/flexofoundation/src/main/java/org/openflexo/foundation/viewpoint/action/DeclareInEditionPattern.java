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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;

public class DeclareInEditionPattern extends FlexoAction<DeclareInEditionPattern, ExampleDrawingObject, ExampleDrawingObject> {

	private static final Logger logger = Logger.getLogger(DeclareInEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DeclareInEditionPattern, ExampleDrawingObject, ExampleDrawingObject> actionType = new FlexoActionType<DeclareInEditionPattern, ExampleDrawingObject, ExampleDrawingObject>(
			"declare_in_edition_pattern", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeclareInEditionPattern makeNewAction(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection,
				FlexoEditor editor) {
			return new DeclareInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingObject shape, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingObject shape, Vector<ExampleDrawingObject> globalSelection) {
			return (shape != null && shape.getCalc().getEditionPatterns().size() > 0);
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareInEditionPattern.actionType, ExampleDrawingShape.class);
		FlexoModelObject.addActionForClass(DeclareInEditionPattern.actionType, ExampleDrawingConnector.class);
	}

	protected static String CREATES_EDITION_PATTERN = "CREATES_EDITION_PATTERN";
	protected static String CHOOSE_ONE_EXISTING = "CHOOSE_ONE_EXISTING";
	public static String[] choices = { CREATES_EDITION_PATTERN, CHOOSE_ONE_EXISTING };

	public static enum DeclareInEditionPatternChoices {
		CREATES_EDITION_PATTERN, CHOOSE_EXISTING_EDITION_PATTERN
	}

	private EditionPattern editionPattern;
	private PatternRole patternRole;

	DeclareInEditionPattern(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Push to palette");
		if (isValid()) {
			if (patternRole instanceof ShapePatternRole) {
				((ShapePatternRole) patternRole).setGraphicalRepresentation(getFocusedObject().getGraphicalRepresentation());
			} else if (patternRole instanceof ConnectorPatternRole) {
				((ConnectorPatternRole) patternRole).setGraphicalRepresentation(getFocusedObject().getGraphicalRepresentation());
			}
		} else {
			logger.warning("Focused role is null !");
		}
	}

	public boolean isValid() {
		return (getFocusedObject() != null && editionPattern != null && patternRole != null && ((patternRole instanceof ShapePatternRole && getFocusedObject() instanceof ExampleDrawingShape) || (patternRole instanceof ConnectorPatternRole && getFocusedObject() instanceof ExampleDrawingConnector)));
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public void setEditionPattern(EditionPattern editionPattern) {
		if (editionPattern != this.editionPattern) {
			this.editionPattern = editionPattern;
			patternRole = null;
		}
	}

	public PatternRole getPatternRole() {
		return patternRole;
	}

	public void setPatternRole(PatternRole patternRole) {
		this.patternRole = patternRole;
	}
}
