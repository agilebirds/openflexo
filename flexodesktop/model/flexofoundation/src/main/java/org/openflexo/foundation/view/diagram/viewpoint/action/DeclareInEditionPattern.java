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
package org.openflexo.foundation.view.diagram.viewpoint.action;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;

public abstract class DeclareInEditionPattern<A extends DeclareInEditionPattern<A, T1>, T1 extends ExampleDiagramObject> extends
		FlexoAction<A, T1, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(DeclareInEditionPattern.class.getPackage().getName());

	private EditionPattern editionPattern;

	private TypeAwareModelSlot<?, ?> modelSlot;

	DeclareInEditionPattern(FlexoActionType<A, T1, ExampleDiagramObject> actionType, T1 focusedObject,
			Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		List<TypeAwareModelSlot> availableModelSlots = focusedObject.getVirtualModel().getModelSlots(TypeAwareModelSlot.class);
		if (availableModelSlots.size() > 0) {
			modelSlot = availableModelSlots.get(0);
		}
		drawingObjectEntries = new Vector<DeclareInEditionPattern<A, T1>.ExampleDrawingObjectEntry>();
		int shapeIndex = 1;
		int connectorIndex = 1;
		for (ExampleDiagramObject o : getFocusedObject().getDescendants()) {
			if (o instanceof ExampleDiagramShape) {
				ExampleDiagramShape shape = (ExampleDiagramShape) o;
				String shapeRoleName = "shape" + (shapeIndex > 1 ? shapeIndex : "");
				drawingObjectEntries.add(new ExampleDrawingObjectEntry(shape, shapeRoleName));
				shapeIndex++;
			}
			if (o instanceof ExampleDiagramConnector) {
				ExampleDiagramConnector connector = (ExampleDiagramConnector) o;
				String connectorRoleName = "connector" + (connectorIndex > 1 ? connectorIndex : "");
				drawingObjectEntries.add(new ExampleDrawingObjectEntry(connector, connectorRoleName));
				connectorIndex++;
			}
		}
	}

	public static enum DeclareInEditionPatternChoices {
		CREATES_EDITION_PATTERN, CHOOSE_EXISTING_EDITION_PATTERN
	}

	public DeclareInEditionPatternChoices primaryChoice = DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN;

	@Override
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

	public Vector<ExampleDrawingObjectEntry> drawingObjectEntries;

	public class ExampleDrawingObjectEntry {
		private boolean selectThis;
		public ExampleDiagramObject graphicalObject;
		public String patternRoleName;

		public ExampleDrawingObjectEntry(ExampleDiagramObject graphicalObject, String patternRoleName) {
			super();
			this.graphicalObject = graphicalObject;
			this.patternRoleName = patternRoleName;
			this.selectThis = isMainEntry();
		}

		public boolean isMainEntry() {
			return graphicalObject == getFocusedObject();
		}

		public boolean getSelectThis() {
			if (isMainEntry()) {
				return true;
			}
			return selectThis;
		}

		public void setSelectThis(boolean aFlag) {
			if (!isMainEntry()) {
				selectThis = aFlag;
			}
		}

		public ExampleDrawingObjectEntry getParentEntry() {
			return getEntry(graphicalObject.getParent());
		}
	}

	public int getSelectedEntriesCount() {
		int returned = 0;
		for (ExampleDrawingObjectEntry e : drawingObjectEntries) {
			if (e.selectThis) {
				returned++;
			}
		}
		return returned;
	}

	public ExampleDrawingObjectEntry getEntry(ExampleDiagramObject o) {
		for (ExampleDrawingObjectEntry e : drawingObjectEntries) {
			if (e.graphicalObject == o) {
				return e;
			}
		}
		return null;
	}

	public TypeAwareModelSlot<?, ?> getModelSlot() {
		return modelSlot;
	}

	public void setModelSlot(TypeAwareModelSlot<?, ?> modelSlot) {
		this.modelSlot = modelSlot;
	}
}
