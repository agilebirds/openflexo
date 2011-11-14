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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.ShapePatternRole;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class DeclareShapeInEditionPattern extends DeclareInEditionPattern<DeclareShapeInEditionPattern, ExampleDrawingShape> {

	private static final Logger logger = Logger.getLogger(DeclareShapeInEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DeclareShapeInEditionPattern, ExampleDrawingShape, ExampleDrawingObject> actionType = new FlexoActionType<DeclareShapeInEditionPattern, ExampleDrawingShape, ExampleDrawingObject>(
			"declare_in_edition_pattern", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeclareShapeInEditionPattern makeNewAction(ExampleDrawingShape focusedObject, Vector<ExampleDrawingObject> globalSelection,
				FlexoEditor editor) {
			return new DeclareShapeInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingShape shape, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingShape shape, Vector<ExampleDrawingObject> globalSelection) {
			return (shape != null && shape.getCalc().getEditionPatterns().size() > 0);
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareShapeInEditionPattern.actionType, ExampleDrawingShape.class);
	}

	public static enum NewEditionPatternChoices {
		MAP_SINGLE_INDIVIDUAL
	}

	public NewEditionPatternChoices patternChoice = NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL;

	public String editionPatternName;
	private OntologyClass concept;
	public String individualPatternRoleName;
	public String shapePatternRoleName = "shape";

	DeclareShapeInEditionPattern(ExampleDrawingShape focusedObject, Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Push to palette");
		if (isValid()) {
			if (getPatternRole() != null) {
				getPatternRole().setGraphicalRepresentation(getFocusedObject().getGraphicalRepresentation());
			}
		} else {
			logger.warning("Focused role is null !");
		}
	}

	@Override
	public boolean isValid() {
		if (getFocusedObject() == null)
			return false;
		switch (primaryChoice) {
		case CHOOSE_EXISTING_EDITION_PATTERN:
			return getEditionPattern() != null && getPatternRole() != null;
		case CREATES_EDITION_PATTERN:
			switch (patternChoice) {
			case MAP_SINGLE_INDIVIDUAL:
				return StringUtils.isNotEmpty(editionPatternName) && concept != null && StringUtils.isNotEmpty(individualPatternRoleName)
						&& StringUtils.isNotEmpty(shapePatternRoleName);
			default:
				break;
			}
		default:
			return false;
		}
	}

	private ShapePatternRole patternRole;

	@Override
	public ShapePatternRole getPatternRole() {
		return patternRole;
	}

	public void setPatternRole(ShapePatternRole patternRole) {
		this.patternRole = patternRole;
	}

	@Override
	public void resetPatternRole() {
		this.patternRole = null;
	}

	public OntologyClass getConcept() {
		return concept;
	}

	public void setConcept(OntologyClass concept) {
		this.concept = concept;
		if (StringUtils.isEmpty(editionPatternName)) {
			editionPatternName = concept.getName();
		}
		if (StringUtils.isEmpty(individualPatternRoleName)) {
			individualPatternRoleName = JavaUtils.getVariableName(concept.getName());
		}
	}

}
