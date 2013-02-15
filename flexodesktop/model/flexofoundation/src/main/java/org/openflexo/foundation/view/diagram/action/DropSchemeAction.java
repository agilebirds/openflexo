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
package org.openflexo.foundation.view.diagram.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.viewpoint.EditionAction;

public class DropSchemeAction extends DiagramEditionSchemeAction<DropSchemeAction, DropScheme> {

	private static final Logger logger = Logger.getLogger(DropSchemeAction.class.getPackage().getName());

	public static FlexoActionType<DropSchemeAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<DropSchemeAction, FlexoModelObject, FlexoModelObject>(
			"drop_palette_element", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DropSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new DropSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof DiagramElement<?>;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DiagramElement.class);
	}

	private DiagramElement<?> _parent;
	private DiagramPaletteElement _paletteElement;
	private DropScheme _dropScheme;
	private DiagramShape _primaryShape;

	public FGEPoint dropLocation;

	DropSchemeAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	private EditionPatternInstance editionPatternInstance;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParametersException {
		logger.info("Drop palette element");

		// getEditionPattern().getViewPoint().getViewpointOntology().loadWhenUnloaded();

		editionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());

		applyEditionActions();

	}

	public DiagramElement<?> getParent() {
		if (_parent == null) {
			if (getFocusedObject() instanceof DiagramShape) {
				_parent = (DiagramShape) getFocusedObject();
			} else if (getFocusedObject() instanceof DiagramRootPane) {
				_parent = (DiagramRootPane) getFocusedObject();
			}
		}
		return _parent;
	}

	public void setParent(DiagramElement<?> parent) {
		_parent = parent;
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
		if (getParent() != null) {
			return getParent().getDiagram();
		}
		if (getFocusedObject() instanceof DiagramElement<?>) {
			return ((DiagramElement<?>) getFocusedObject()).getDiagram();
		}
		return null;
	}

	@Override
	protected Object performAction(EditionAction anAction, Hashtable<EditionAction, Object> performedActions) {
		Object assignedObject = super.performAction(anAction, performedActions);
		if (anAction instanceof AddShape) {
			AddShape action = (AddShape) anAction;
			DiagramShape newShape = (DiagramShape) assignedObject;
			if (newShape != null) {
				ShapeGraphicalRepresentation<?> gr = newShape.getGraphicalRepresentation();
				if (action.getPatternRole().getIsPrimaryRepresentationRole()) {
					// Declare shape as new shape only if it is the primary representation role of the EP

					_primaryShape = newShape;
					gr.setLocation(dropLocation);

					// Temporary comment this portion of code if child shapes are declared inside this shape
					if (!action.getPatternRole().containsShapes()
							&& action.getContainer().toString().equals(DiagramEditionScheme.TOP_LEVEL)) {
						ShapeBorder border = gr.getBorder();
						ShapeBorder newBorder = new ShapeBorder(border);
						boolean requireNewBorder = false;
						double deltaX = 0;
						double deltaY = 0;
						if (border.top < 25) {
							requireNewBorder = true;
							deltaY = border.top - 25;
							newBorder.top = 25;
						}
						if (border.left < 25) {
							requireNewBorder = true;
							deltaX = border.left - 25;
							newBorder.left = 25;
						}
						if (border.right < 25) {
							requireNewBorder = true;
							newBorder.right = 25;
						}
						if (border.bottom < 25) {
							requireNewBorder = true;
							newBorder.bottom = 25;
						}
						if (requireNewBorder) {
							gr.setBorder(newBorder);
							gr.setLocation(new FGEPoint(gr.getX() + deltaX, gr.getY() + deltaY));
							if (gr.getIsFloatingLabel()) {
								gr.setAbsoluteTextX(gr.getAbsoluteTextX() - deltaX);
								gr.setAbsoluteTextY(gr.getAbsoluteTextY() - deltaY);
							}
						}
					}
				} else if (action.getPatternRole().getParentShapeAsDefinedInAction()) {
					Object graphicalRepresentation = action.getEditionPattern().getPrimaryRepresentationRole().getGraphicalRepresentation();
					if (graphicalRepresentation instanceof ShapeGraphicalRepresentation<?>) {
						ShapeGraphicalRepresentation<?> primaryGR = (ShapeGraphicalRepresentation<?>) graphicalRepresentation;
						gr.setLocation(new FGEPoint(dropLocation.x + gr.getX() - primaryGR.getX(), dropLocation.y + gr.getY()
								- primaryGR.getY()));
					}
				}
				gr.updateConstraints();
			} else {
				logger.warning("Inconsistant data: shape has not been created");
			}

			if (action.getExtendParentBoundsToHostThisShape()) {
				((ShapeGraphicalRepresentation<?>) newShape.getGraphicalRepresentation()).extendParentBoundsToHostThisShape();
			}

		}
		return assignedObject;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramEditionScheme.TARGET) && _dropScheme.getTargetEditionPattern() != null) {
			if (getParent() instanceof DiagramShape) {
				return ((DiagramShape) getParent()).getEditionPatternInstance();
			}
		}
		return super.getValue(variable);
	}

	@Override
	public GraphicalRepresentation getOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
		if (getPaletteElement() != null) {
			if (getPaletteElement().getOverridingGraphicalRepresentation(patternRole) != null) {
				return getPaletteElement().getOverridingGraphicalRepresentation(patternRole);
			}
		}

		// return overridenGraphicalRepresentations.get(patternRole);
		// TODO temporary desactivate overriden GR
		return null;
	}

}
