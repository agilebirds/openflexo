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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;

public abstract class DeclareInEditionPattern<A extends DeclareInEditionPattern<A, T1>, T1 extends ExampleDrawingObject> extends
		FlexoAction<A, T1, ExampleDrawingObject> {

	private static final Logger logger = Logger.getLogger(DeclareInEditionPattern.class.getPackage().getName());

	private EditionPattern editionPattern;

	DeclareInEditionPattern(FlexoActionType<A, T1, ExampleDrawingObject> actionType, T1 focusedObject,
			Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public static enum DeclareInEditionPatternChoices {
		CREATES_EDITION_PATTERN, CHOOSE_EXISTING_EDITION_PATTERN
	}

	public DeclareInEditionPatternChoices primaryChoice = DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN;

	public abstract boolean isValid();

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public void setEditionPattern(EditionPattern editionPattern) {
		if (editionPattern != this.editionPattern) {
			this.editionPattern = editionPattern;
			resetPatternRole();
		}
	}

	public abstract GraphicalElementPatternRole getPatternRole();

	public abstract void resetPatternRole();
}
