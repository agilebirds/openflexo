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
package org.openflexo.vpm.drawingshema;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JScrollPane;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.AddShape;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.foundation.viewpoint.action.AddExampleDrawingShape;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;

public class ContextualPalette extends DrawingPalette implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(ContextualPalette.class.getPackage().getName());

	private ViewPointPalette _calcPalette;

	public ContextualPalette(ViewPointPalette viewPointPalette) {
		super((int) ((DrawingGraphicalRepresentation) viewPointPalette.getGraphicalRepresentation()).getWidth(),
				(int) ((DrawingGraphicalRepresentation) viewPointPalette.getGraphicalRepresentation()).getHeight(), viewPointPalette
						.getName());

		_calcPalette = viewPointPalette;

		for (ViewPointPaletteElement element : viewPointPalette.getElements()) {
			addElement(makePaletteElement(element));
		}

		makePalettePanel();
		getPaletteView().revalidate();

		viewPointPalette.addObserver(this);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == _calcPalette) {
			if (dataModification instanceof CalcPaletteElementInserted) {
				logger.info("Notified new Palette Element added");
				CalcPaletteElementInserted dm = (CalcPaletteElementInserted) dataModification;
				ContextualPaletteElement e = makePaletteElement(dm.newValue());
				addElement(e);
				e.getGraphicalRepresentation().notifyObjectHierarchyHasBeenUpdated();
				JScrollPane oldPaletteView = getPaletteViewInScrollPane();
				updatePalette();
				getController().updatePalette(_calcPalette, oldPaletteView);
			} else if (dataModification instanceof CalcPaletteElementRemoved) {
				logger.info("Notified new Palette Element removed");
				CalcPaletteElementRemoved dm = (CalcPaletteElementRemoved) dataModification;
				ContextualPaletteElement e = getContextualPaletteElement(dm.oldValue());
				removeElement(e);
				JScrollPane oldPaletteView = getPaletteViewInScrollPane();
				updatePalette();
				getController().updatePalette(_calcPalette, oldPaletteView);
			}
		}
	}

	protected ContextualPaletteElement getContextualPaletteElement(ViewPointPaletteElement element) {
		for (PaletteElement e : elements) {
			if (e instanceof ContextualPaletteElement && ((ContextualPaletteElement) e).viewPointPaletteElement == element) {
				return (ContextualPaletteElement) e;
			}
		}
		return null;

	}

	@Override
	public CalcDrawingShemaController getController() {
		return (CalcDrawingShemaController) super.getController();
	}

	private Vector<DropScheme> getAvailableDropSchemes(EditionPattern pattern, GraphicalRepresentation target) {
		Vector<DropScheme> returned = new Vector<DropScheme>();
		for (DropScheme dropScheme : pattern.getDropSchemes()) {
			if (dropScheme.isTopTarget() && target instanceof DrawingGraphicalRepresentation) {
				returned.add(dropScheme);
			}
			if (target.getDrawable() instanceof ViewShape) {
				ViewShape targetShape = (ViewShape) target.getDrawable();
				for (EditionPatternReference ref : targetShape.getEditionPatternReferences()) {
					if (dropScheme.isValidTarget(ref.getEditionPattern(), ref.getPatternRole())) {
						returned.add(dropScheme);
					}
				}
			}
		}
		return returned;
	}

	private ContextualPaletteElement makePaletteElement(final ViewPointPaletteElement element) {
		return new ContextualPaletteElement(element);
	}

	protected class ContextualPaletteElement implements PaletteElement {
		private ViewPointPaletteElement viewPointPaletteElement;
		private PaletteElementGraphicalRepresentation gr;

		public ContextualPaletteElement(final ViewPointPaletteElement aPaletteElement) {
			viewPointPaletteElement = aPaletteElement;
			gr = new PaletteElementGraphicalRepresentation(viewPointPaletteElement.getGraphicalRepresentation(), null, getPaletteDrawing()) {
				@Override
				public String getText() {
					if (aPaletteElement != null && aPaletteElement.getBoundLabelToElementName()) {
						return aPaletteElement.getName();
					}
					return "";
				}
			};
			gr.setDrawable(this);
		}

		@Override
		public boolean acceptDragging(GraphicalRepresentation target) {
			return target instanceof CalcDrawingShemaGR || target instanceof CalcDrawingShapeGR;
		}

		@Override
		public boolean elementDragged(GraphicalRepresentation containerGR, FGEPoint dropLocation) {
			if (containerGR.getDrawable() instanceof ExampleDrawingObject) {

				ExampleDrawingObject rootContainer = (ExampleDrawingObject) containerGR.getDrawable();

				DropScheme dropScheme = viewPointPaletteElement.getDropScheme();

				Hashtable<GraphicalElementPatternRole, ExampleDrawingObject> grHierarchy = new Hashtable<GraphicalElementPatternRole, ExampleDrawingObject>();

				for (EditionAction action : dropScheme.getActions()) {
					if (action instanceof AddShape) {
						ShapePatternRole role = ((AddShape) action).getPatternRole();
						ShapeGraphicalRepresentation<?> shapeGR = (ShapeGraphicalRepresentation<?>) viewPointPaletteElement
								.getOverridingGraphicalRepresentation(role);
						if (shapeGR == null) {
							shapeGR = role.getGraphicalRepresentation();
						}
						ExampleDrawingObject container = null;
						if (role.getParentShapePatternRole() != null) {
							logger.info("Adding shape " + role + " under " + role.getParentShapePatternRole());
							container = grHierarchy.get(role.getParentShapePatternRole());
						} else {
							logger.info("Adding shape " + role + " as root");
							container = rootContainer;
							if (viewPointPaletteElement.getBoundLabelToElementName()) {
								shapeGR.setText(viewPointPaletteElement.getName());
							}
							shapeGR.setLocation(dropLocation);
						}
						AddExampleDrawingShape addShapeAction = AddExampleDrawingShape.actionType.makeNewAction(container, null,
								getController().getCEDController().getEditor());
						addShapeAction.graphicalRepresentation = shapeGR;
						addShapeAction.newShapeName = role.getPatternRoleName();
						if (role.getParentShapePatternRole() == null) {
							addShapeAction.newShapeName = viewPointPaletteElement.getName();
						}
						addShapeAction.doAction();
						grHierarchy.put(role, addShapeAction.getNewShape());
					}
				}

				/*ShapeGraphicalRepresentation<?> shapeGR = getGraphicalRepresentation().clone();
				shapeGR.setIsSelectable(true);
				shapeGR.setIsFocusable(true);
				shapeGR.setIsReadOnly(false);
				shapeGR.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
				shapeGR.setLocation(dropLocation);
				shapeGR.setLayer(containerGR.getLayer() + 1);
				shapeGR.setAllowToLeaveBounds(true);

				AddExampleDrawingShape action = AddExampleDrawingShape.actionType.makeNewAction(container, null, getController()
						.getCEDController().getEditor());
				action.graphicalRepresentation = shapeGR;
				action.newShapeName = shapeGR.getText();
				if (action.newShapeName == null) {
					action.newShapeName = FlexoLocalization.localizedForKey("shape");
					// action.nameSetToNull = true;
					// action.setNewShapeName(FlexoLocalization.localizedForKey("unnamed"));
				}

				action.doAction();
				return action.hasActionExecutionSucceeded();*/

				return true;
			}

			return false;
		}

		@Override
		public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
			return gr;
		}

		@Override
		public DrawingPalette getPalette() {
			return ContextualPalette.this;
		}

	}

}
