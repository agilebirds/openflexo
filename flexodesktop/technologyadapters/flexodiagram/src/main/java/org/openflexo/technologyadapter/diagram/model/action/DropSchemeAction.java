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
package org.openflexo.technologyadapter.diagram.model.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.technologyadapter.diagram.fml.DiagramEditionScheme;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementPatternRole;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

public class DropSchemeAction extends DiagramEditionSchemeAction<DropSchemeAction, DropScheme, VirtualModelInstanceObject> {

	private static final Logger logger = Logger.getLogger(DropSchemeAction.class.getPackage().getName());

	public static FlexoActionType<DropSchemeAction, VirtualModelInstanceObject, VirtualModelInstanceObject> actionType = new FlexoActionType<DropSchemeAction, VirtualModelInstanceObject, VirtualModelInstanceObject>(
			"drop_palette_element", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DropSchemeAction makeNewAction(VirtualModelInstanceObject focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
				FlexoEditor editor) {
			return new DropSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModelInstanceObject object, Vector<VirtualModelInstanceObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModelInstanceObject object, Vector<VirtualModelInstanceObject> globalSelection) {
			return object instanceof DiagramElement<?>;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(actionType, VirtualModelInstanceObject.class);
	}

	private DiagramContainerElement<?> _parent;
	private DiagramPaletteElement _paletteElement;
	private DropScheme _dropScheme;
	private DiagramShape _primaryShape;

	public FGEPoint dropLocation;

	DropSchemeAction(VirtualModelInstanceObject focusedObject, Vector<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoObject> createdObjects;

	private EditionPatternInstance editionPatternInstance;

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParametersException {
		logger.info("Drop palette element");

		logger.info("project=" + getProject());
		// getEditionPattern().getViewPoint().getViewpointOntology().loadWhenUnloaded();

		editionPatternInstance = getVirtualModelInstance().makeNewEditionPatternInstance(getEditionPattern());

		logger.info("editionPatternInstance=" + editionPatternInstance);
		logger.info("epi project=" + editionPatternInstance.getProject());
		logger.info("epi resource data =" + editionPatternInstance.getResourceData());

		applyEditionActions();

	}

	public DiagramContainerElement<?> getParent() {
		if (_parent == null) {
			/*if (getFocusedObject() instanceof DiagramShape) {
				_parent = (DiagramShape) getFocusedObject();
			} else if (getFocusedObject() instanceof DiagramRootPane) {
				_parent = (DiagramRootPane) getFocusedObject();
			}*/
			_parent = getDiagram();
		}
		return _parent;
	}

	public void setParent(DiagramContainerElement<?> parent) {
		_parent = parent;
	}

	@Override
	public Diagram getDiagram() {
		return getParent().getDiagram();
	}

	public DropScheme getDropScheme() {
		return _dropScheme;
	}

	public void setDropScheme(DropScheme dropScheme) {
		_dropScheme = dropScheme;
	}

	@Override
	public DropScheme getEditionScheme() {
		return getDropScheme();
	}

	public DiagramPaletteElement getPaletteElement() {
		return _paletteElement;
	}

	public void setPaletteElement(DiagramPaletteElement paletteElement) {
		_paletteElement = paletteElement;
	}

	public DiagramShape getPrimaryShape() {
		return _primaryShape;
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		return getFocusedObject().getVirtualModelInstance();
	}

	@Override
	protected Object performAction(EditionAction anAction, Hashtable<EditionAction, Object> performedActions) {
		Object assignedObject = super.performAction(anAction, performedActions);
		if (anAction instanceof AddShape) {
			AddShape action = (AddShape) anAction;
			DiagramShape newShape = (DiagramShape) assignedObject;
			if (newShape != null) {
				ShapeGraphicalRepresentation gr = newShape.getGraphicalRepresentation();
				// if (action.getPatternRole().getIsPrimaryRepresentationRole()) {
				// Declare shape as new shape only if it is the primary representation role of the EP

				_primaryShape = newShape;
				gr.setX(dropLocation.getX());
				gr.setY(dropLocation.getY());

				// Temporary comment this portion of code if child shapes are declared inside this shape
				if (!action.getPatternRole().containsShapes() && action.getContainer().toString().equals(DiagramEditionScheme.TOP_LEVEL)) {
					ShapeBorder border = gr.getBorder();
					ShapeBorder newBorder = gr.getFactory().makeShapeBorder(border);
					boolean requireNewBorder = false;
					double deltaX = 0;
					double deltaY = 0;
					if (border.getTop() < 25) {
						requireNewBorder = true;
						deltaY = border.getTop() - 25;
						newBorder.setTop(25);
					}
					if (border.getLeft() < 25) {
						requireNewBorder = true;
						deltaX = border.getLeft() - 25;
						newBorder.setLeft(25);
					}
					if (border.getRight() < 25) {
						requireNewBorder = true;
						newBorder.setRight(25);
					}
					if (border.getBottom() < 25) {
						requireNewBorder = true;
						newBorder.setBottom(25);
					}
					if (requireNewBorder) {
						gr.setBorder(newBorder);
						gr.setX(gr.getX() + deltaX);
						gr.setY(gr.getY() + deltaY);
						if (gr.getIsFloatingLabel()) {
							gr.setAbsoluteTextX(gr.getAbsoluteTextX() - deltaX);
							gr.setAbsoluteTextY(gr.getAbsoluteTextY() - deltaY);
						}
					}
				}
				/*} else if (action.getPatternRole().getParentShapeAsDefinedInAction()) {
					Object graphicalRepresentation = action.getEditionPattern().getPrimaryRepresentationRole().getGraphicalRepresentation();
					if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
						ShapeGraphicalRepresentation primaryGR = (ShapeGraphicalRepresentation) graphicalRepresentation;
						gr.setX(dropLocation.x + gr.getX() - primaryGR.getX());
						gr.setY(dropLocation.y + gr.getY() - primaryGR.getY());
					}
				}*/
			} else {
				logger.warning("Inconsistant data: shape has not been created");
			}

			/*if (action.getExtendParentBoundsToHostThisShape()) {
				((ShapeGraphicalRepresentation) newShape.getGraphicalRepresentation()).extendParentBoundsToHostThisShape();
			}*/

		}
		return assignedObject;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramEditionScheme.TARGET) && _dropScheme.getTargetEditionPattern() != null) {
			/*if (getParent() instanceof DiagramShape) {
				return ((DiagramShape) getParent()).getEditionPatternInstance();
			}*/
			// TODO
			logger.warning("Please implement getValue() for target");
			return null;
		}
		return super.getValue(variable);
	}

	public GraphicalRepresentation getOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
		/*if (getPaletteElement() != null) {
			if (getPaletteElement().getOverridingGraphicalRepresentation(patternRole) != null) {
				return getPaletteElement().getOverridingGraphicalRepresentation(patternRole);
			}
		}*/

		// return overridenGraphicalRepresentations.get(patternRole);
		// TODO temporary desactivate overriden GR
		return null;
	}

}
