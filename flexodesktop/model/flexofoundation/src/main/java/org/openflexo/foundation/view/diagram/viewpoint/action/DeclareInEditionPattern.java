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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;

/**
 * This abstract class is an action that allows to create an edition pattern from a graphical representation(for instance a shape or
 * connector)
 * 
 * @author Sylvain, Vincent
 * 
 * @param <A>
 * @param <T1>
 *            is the graphical repesentation
 */
public abstract class DeclareInEditionPattern<A extends DeclareInEditionPattern<A, T1, T2>, T1 extends FlexoObject & GRTemplate, T2 extends FlexoObject>
		extends FlexoAction<A, T1, T2> {

	private static final Logger logger = Logger.getLogger(DeclareInEditionPattern.class.getPackage().getName());

	private EditionPattern editionPattern;

	private ModelSlot<?> modelSlot;

	private List<VirtualModelModelSlot<?, ?>> virtualModelModelSlots = null;
	private List<TypeAwareModelSlot<?, ?>> typeAwareModelSlots = null;

	/**
	 * Constructor for this class
	 * 
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	DeclareInEditionPattern(FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		// Get the set of model slots that are available from the current virtual model
		List<ModelSlot> availableModelSlots = getModelSlots();
		if (availableModelSlots.size() > 0) {
			modelSlot = availableModelSlots.get(0);
		}
		// Get the set of internal elements inside the current focused object
		drawingObjectEntries = new Vector<DeclareInEditionPattern<A, T1, T2>.DrawingObjectEntry>();
		int shapeIndex = 1;
		int connectorIndex = 1;
		for (GRTemplate o : getFocusedObject().getDescendants()) {
			if (o instanceof GRShapeTemplate) {
				GRShapeTemplate shape = (GRShapeTemplate) o;
				String shapeRoleName = "shape" + (shapeIndex > 1 ? shapeIndex : "");
				drawingObjectEntries.add(new DrawingObjectEntry(shape, shapeRoleName));
				shapeIndex++;
			}
			if (o instanceof GRConnectorTemplate) {
				GRConnectorTemplate connector = (GRConnectorTemplate) o;
				String connectorRoleName = "connector" + (connectorIndex > 1 ? connectorIndex : "");
				drawingObjectEntries.add(new DrawingObjectEntry(connector, connectorRoleName));
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

	public Vector<DrawingObjectEntry> drawingObjectEntries;

	public class DrawingObjectEntry {
		private boolean selectThis;
		public GRTemplate graphicalObject;
		public String patternRoleName;

		public DrawingObjectEntry(GRTemplate graphicalObject, String patternRoleName) {
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

		public DrawingObjectEntry getParentEntry() {
			return getEntry(graphicalObject.getParent());
		}
	}

	public int getSelectedEntriesCount() {
		int returned = 0;
		for (DrawingObjectEntry e : drawingObjectEntries) {
			if (e.selectThis) {
				returned++;
			}
		}
		return returned;
	}

	public DrawingObjectEntry getEntry(GRTemplate o) {
		for (DrawingObjectEntry e : drawingObjectEntries) {
			if (e.graphicalObject == o) {
				return e;
			}
		}
		return null;
	}

	public DiagramSpecification getDiagramSpecification() {
		return getFocusedObject().getDiagramSpecification();
	}

	public ModelSlot<?> getModelSlot() {
		return modelSlot;
	}

	public void setModelSlot(ModelSlot<?> modelSlot) {
		this.modelSlot = modelSlot;
	}

	public List<ModelSlot> getModelSlots() {
		return getFocusedObject().getDiagramSpecification().getModelSlots();
	}

	/**
	 * Return a virtual model adressed by a model slot
	 * 
	 * @return
	 */
	public VirtualModel getAdressedVirtualModel() {
		if (isVirtualModelModelSlot()) {
			VirtualModelModelSlot virtualModelModelSlot = (VirtualModelModelSlot) getModelSlot();
			return virtualModelModelSlot.getAddressedVirtualModel();
		}
		return null;
	}

	/**
	 * Return a virtual model adressed by a model slot
	 * 
	 * @return
	 */
	public FlexoMetaModel getAdressedFlexoMetaModel() {
		if (isTypeAwareModelSlot()) {
			TypeAwareModelSlot typeAwareModelSlot = (TypeAwareModelSlot) getModelSlot();
			return typeAwareModelSlot.getMetaModelResource().getMetaModelData();
		}
		return null;
	}

	public List<VirtualModelModelSlot<?, ?>> getVirtualModelModelSlots() {
		if (getModelSlot() != null) {
			if (virtualModelModelSlots == null) {
				virtualModelModelSlots = new ArrayList<VirtualModelModelSlot<?, ?>>();
			}
			if (!virtualModelModelSlots.isEmpty()) {
				virtualModelModelSlots.clear();
			}
			for (ModelSlot<?> modelSlot : getModelSlot().getVirtualModel().getModelSlots()) {
				if (modelSlot instanceof VirtualModelModelSlot) {
					virtualModelModelSlots.add((VirtualModelModelSlot<?, ?>) modelSlot);
				}
			}
		}
		return virtualModelModelSlots;
	}

	public List<TypeAwareModelSlot<?, ?>> getTypeAwareModelSlots() {
		if (getModelSlot() != null) {
			if (typeAwareModelSlots == null) {
				typeAwareModelSlots = new ArrayList<TypeAwareModelSlot<?, ?>>();
			}
			if (!typeAwareModelSlots.isEmpty()) {
				typeAwareModelSlots.clear();
			}
			for (ModelSlot<?> modelSlot : getModelSlot().getVirtualModel().getModelSlots()) {
				if (modelSlot instanceof TypeAwareModelSlot) {
					typeAwareModelSlots.add((TypeAwareModelSlot<?, ?>) modelSlot);
				}
			}
		}
		return typeAwareModelSlots;
	}

	public boolean isTypeAwareModelSlot() {
		if (getModelSlot() instanceof TypeAwareModelSlot) {
			return true;
		}
		return false;
	}

	public boolean isVirtualModelModelSlot() {
		if (getModelSlot() instanceof VirtualModelModelSlot) {
			return true;
		}
		return false;
	}

	public static enum EditionSchemeChoice {
		DELETE_SHAPE, DELETE_SHAPE_MODEL, DROP
	}

	public class EditionSchemeConfiguration {

		private String name;

		private EditionSchemeChoice type;

		private boolean isValid;

		public EditionSchemeConfiguration(String name, EditionSchemeChoice type) {
			super();
			this.name = name;
			this.type = type;
		}

		public EditionSchemeChoice getType() {
			return type;
		}

		public void setType(EditionSchemeChoice type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isValid() {
			return isValid;
		}

		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}
	}

}
