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
package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementPatternRole;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

/**
 * This abstract class is an action that allows to create an edition pattern from a graphical representation(for instance a shape or
 * connector)
 * 
 * @author Sylvain, Vincent
 * 
 * @param <A>
 * @param <T1>
 */
public abstract class DeclareInEditionPattern<A extends DeclareInEditionPattern<A, T>, T extends DiagramElement<?>> extends
		FlexoAction<A, T, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(DeclareInEditionPattern.class.getPackage().getName());

	/**
	 * Stores the model slot which encodes the access to a {@link Diagram} conform to a {@link DiagramSpecification}, in the context of a
	 * {@link VirtualModel} (which is a context where a diagram is federated with other sources of informations)
	 */
	private TypedDiagramModelSlot diagramModelSlot;

	/**
	 * Stores the model slot used as source of information (data) in pattern proposal
	 */
	private ModelSlot<?> modelSlot;

	private EditionPattern editionPattern;

	private List<VirtualModelModelSlot> virtualModelModelSlots = null;
	private List<TypeAwareModelSlot<?, ?>> typeAwareModelSlots = null;

	/**
	 * Constructor for this class
	 * 
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	DeclareInEditionPattern(FlexoActionType<A, T, DiagramElement<?>> actionType, T focusedObject,
			Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		// Get the set of model slots that are available from the current virtual model
		List<ModelSlot<?>> availableModelSlots = getModelSlots();
		if (availableModelSlots.size() > 0) {
			modelSlot = availableModelSlots.get(0);
		}
		// Get the set of internal elements inside the current focused object
		drawingObjectEntries = new Vector<DrawingObjectEntry>();
		int shapeIndex = 1;
		int connectorIndex = 1;

		List<? extends DiagramElement<?>> elements = (getFocusedObject() instanceof DiagramContainerElement ? ((DiagramContainerElement<?>) getFocusedObject())
				.getDescendants() : Collections.singletonList(getFocusedObject()));

		for (DiagramElement<?> o : elements) {
			if (o instanceof DiagramShape) {
				DiagramShape shape = (DiagramShape) o;
				String shapeRoleName = "shape" + (shapeIndex > 1 ? shapeIndex : "");
				drawingObjectEntries.add(new DrawingObjectEntry(shape, shapeRoleName));
				shapeIndex++;
			}
			if (o instanceof DiagramConnector) {
				DiagramConnector connector = (DiagramConnector) o;
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

	/**
	 * Return the model slot which encodes the access to a {@link Diagram} conform to a {@link DiagramSpecification}, in the context of a
	 * {@link VirtualModel} (which is a context where a diagram is federated with other sources of informations)
	 * 
	 * @return
	 */
	public TypedDiagramModelSlot getDiagramModelSlot() {
		return diagramModelSlot;
	}

	/**
	 * Sets the model slot which encodes the access to a {@link Diagram} conform to a {@link DiagramSpecification}, in the context of a
	 * {@link VirtualModel} (which is a context where a diagram is federated with other sources of informations)
	 * 
	 * @return
	 */
	public void setDiagramModelSlot(TypedDiagramModelSlot diagramModelSlot) {
		this.diagramModelSlot = diagramModelSlot;
	}

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
		public DiagramElement<?> graphicalObject;
		public String patternRoleName;

		public DrawingObjectEntry(DiagramElement<?> graphicalObject, String patternRoleName) {
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

	public DrawingObjectEntry getEntry(DiagramElement<?> o) {
		for (DrawingObjectEntry e : drawingObjectEntries) {
			if (e.graphicalObject == o) {
				return e;
			}
		}
		return null;
	}

	public DiagramSpecification getDiagramSpecification() {
		return diagramModelSlot.getMetaModelResource().getLoadedResourceData();
	}

	/**
	 * Return the model slot used as source of information (data) in pattern proposal
	 * 
	 * @return
	 */
	public ModelSlot<?> getModelSlot() {
		return modelSlot;
	}

	/**
	 * Sets the model slot used as source of information (data) in pattern proposal
	 * 
	 * @return
	 */
	public void setModelSlot(ModelSlot<?> modelSlot) {
		this.modelSlot = modelSlot;
	}

	/**
	 * Return the list of all model slots declared in virtual model where this action is defined
	 * 
	 * @return
	 */
	public List<ModelSlot<?>> getModelSlots() {
		return getModelSlot().getVirtualModel().getModelSlots();
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

	public List<VirtualModelModelSlot> getVirtualModelModelSlots() {
		if (getModelSlot() != null) {
			if (virtualModelModelSlots == null) {
				virtualModelModelSlots = new ArrayList<VirtualModelModelSlot>();
			}
			if (!virtualModelModelSlots.isEmpty()) {
				virtualModelModelSlots.clear();
			}
			for (ModelSlot<?> modelSlot : getModelSlot().getVirtualModel().getModelSlots()) {
				if (modelSlot instanceof VirtualModelModelSlot) {
					virtualModelModelSlots.add((VirtualModelModelSlot) modelSlot);
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

}
