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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.AddShape;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;

public class DropSchemeAction extends EditionSchemeAction<DropSchemeAction> {

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
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof ViewObject;
		}

	};

	private ViewObject _parent;
	private ViewPointPaletteElement _paletteElement;
	private DropScheme _dropScheme;
	private ViewShape _primaryShape;

	public FGEPoint dropLocation;

	DropSchemeAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	private EditionPatternInstance editionPatternInstance;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParametersException {
		logger.info("Drop palette element");

		getEditionPattern().getCalc().getCalcOntology().loadWhenUnloaded();

		editionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());

		applyEditionActions();

	}

	public ViewObject getParent() {
		if (_parent == null) {
			if (getFocusedObject() instanceof ViewShape) {
				_parent = (ViewShape) getFocusedObject();
			} else if (getFocusedObject() instanceof View) {
				_parent = (View) getFocusedObject();
			}
		}
		return _parent;
	}

	public void setParent(ViewObject parent) {
		_parent = parent;
	}

	public DropScheme getDropScheme() {
		return _dropScheme;
	}

	public void setDropScheme(DropScheme dropScheme) {
		_dropScheme = dropScheme;
	}

	@Override
	public EditionScheme getEditionScheme() {
		return getDropScheme();
	}

	public ViewPointPaletteElement getPaletteElement() {
		return _paletteElement;
	}

	public void setPaletteElement(ViewPointPaletteElement paletteElement) {
		_paletteElement = paletteElement;
	}

	public ViewShape getPrimaryShape() {
		return _primaryShape;
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

	@Override
	protected View retrieveOEShema() {
		if (getParent() != null) {
			return getParent().getShema();
		}
		if (getFocusedObject() instanceof ViewObject) {
			return ((ViewObject) getFocusedObject()).getShema();
		}
		return null;
	}

	@Override
	protected ViewShape performAddShape(AddShape action) {
		ViewShape newShape = super.performAddShape(action);
		if (newShape != null) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) newShape.getGraphicalRepresentation();
			if (action.getPatternRole().getIsPrimaryRepresentationRole()) {
				// Declare shape as new shape only if it is the primary representation role of the EP

				_primaryShape = newShape;
				gr.setLocation(dropLocation);

				// Temporary comment this portion of code if child shapes are declared inside this shape
				if (!action.getPatternRole().containsShapes() && action.getContainer().toString().equals(EditionScheme.TOP_LEVEL)) {
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
		return newShape;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof EditionPatternPathElement) {
			if (variable.getVariableName().equals(EditionScheme.TARGET) && _dropScheme.getTargetEditionPattern() != null) {
				if (getParent() instanceof ViewShape) {
					return ((ViewShape) getParent()).getEditionPatternInstance();
				}
			}
			return parameterValues;
		}
		return super.getValue(variable);
	}

}