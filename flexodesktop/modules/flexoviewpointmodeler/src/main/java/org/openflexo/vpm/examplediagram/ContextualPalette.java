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
package org.openflexo.vpm.examplediagram;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.action.AddExampleDiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.technologyadapter.diagram.model.dm.DiagramPaletteElementInserted;
import org.openflexo.technologyadapter.diagram.model.dm.DiagramPaletteElementRemoved;

public class ContextualPalette extends DrawingPalette implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(ContextualPalette.class.getPackage().getName());

	private final DiagramPalette diagramPalette;

	private final ExampleDiagramEditor editor;

	public ContextualPalette(DiagramPalette diagramPalette, ExampleDiagramEditor editor) {
		super((int) diagramPalette.getGraphicalRepresentation().getWidth(), (int) diagramPalette.getGraphicalRepresentation().getHeight(),
				diagramPalette.getName());

		this.diagramPalette = diagramPalette;
		this.editor = editor;

		for (DiagramPaletteElement element : diagramPalette.getElements()) {
			addElement(makePaletteElement(element));
		}

		diagramPalette.addObserver(this);
	}

	public ExampleDiagramEditor getEditor() {
		return editor;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == diagramPalette) {
			if (dataModification instanceof DiagramPaletteElementInserted) {
				logger.info("Notified new Palette Element added");
				DiagramPaletteElementInserted dm = (DiagramPaletteElementInserted) dataModification;
				ContextualPaletteElement e = makePaletteElement(dm.newValue());
				addElement(e);
				// e.getGraphicalRepresentation().notifyObjectHierarchyHasBeenUpdated();
				// DrawingView<PaletteDrawing> oldPaletteView = getPaletteView();
				// updatePalette();
				// getController().updatePalette(diagramPalette, oldPaletteView);
				logger.warning("Sans doute des choses a faire ici ???");
			} else if (dataModification instanceof DiagramPaletteElementRemoved) {
				logger.info("Notified new Palette Element removed");
				DiagramPaletteElementRemoved dm = (DiagramPaletteElementRemoved) dataModification;
				ContextualPaletteElement e = getContextualPaletteElement(dm.oldValue());
				removeElement(e);
				// DrawingView<PaletteDrawing> oldPaletteView = getPaletteView();
				// updatePalette();
				// getController().updatePalette(diagramPalette, oldPaletteView);
				logger.warning("Sans doute des choses a faire ici ???");
			}
		}
	}

	protected ContextualPaletteElement getContextualPaletteElement(DiagramPaletteElement element) {
		for (PaletteElement e : elements) {
			if (e instanceof ContextualPaletteElement && ((ContextualPaletteElement) e).diagramPaletteElement == element) {
				return (ContextualPaletteElement) e;
			}
		}
		return null;

	}

	private Vector<DropScheme> getAvailableDropSchemes(EditionPattern pattern, DrawingTreeNode<?, ?> target) {
		Vector<DropScheme> returned = new Vector<DropScheme>();
		for (DropScheme dropScheme : pattern.getDropSchemes()) {
			if (dropScheme.isTopTarget() && target instanceof DrawingGraphicalRepresentation) {
				returned.add(dropScheme);
			}
			if (target.getDrawable() instanceof DiagramShape) {
				DiagramShape targetShape = (DiagramShape) target.getDrawable();
				for (FlexoModelObjectReference<EditionPatternInstance> ref : targetShape.getEditionPatternReferences()) {
					if (dropScheme.isValidTarget(ref.getObject().getEditionPattern(), ref.getObject().getRoleForActor(targetShape))) {
						returned.add(dropScheme);
					}
				}
			}
		}
		return returned;
	}

	private ContextualPaletteElement makePaletteElement(final DiagramPaletteElement element) {
		return new ContextualPaletteElement(element);
	}

	@SuppressWarnings("serial")
	protected class ContextualPaletteElement implements PaletteElement {
		private DiagramPaletteElement diagramPaletteElement;

		public ContextualPaletteElement(final DiagramPaletteElement aPaletteElement) {
			diagramPaletteElement = aPaletteElement;
		}

		@Override
		public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
			return getEditor() != null && target instanceof ContainerNode
					&& (target.getDrawable() instanceof ExampleDiagram || target.getDrawable() instanceof ExampleDiagramShape);
		}

		@Override
		public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation) {
			if (target.getDrawable() instanceof ExampleDiagramObject) {

				ExampleDiagramObject rootContainer = (ExampleDiagramObject) target.getDrawable();

				DropScheme dropScheme = diagramPaletteElement.getDropScheme();

				logger.info("Drop scheme being applied: " + dropScheme);
				System.out.println("Drop scheme being applied: " + dropScheme);

				Hashtable<GraphicalElementPatternRole, ExampleDiagramObject> grHierarchy = new Hashtable<GraphicalElementPatternRole, ExampleDiagramObject>();

				for (EditionAction action : dropScheme.getActions()) {
					if (action instanceof AddShape) {
						ShapePatternRole role = ((AddShape) action).getPatternRole();
						ShapeGraphicalRepresentation shapeGR = (ShapeGraphicalRepresentation) diagramPaletteElement
								.getOverridingGraphicalRepresentation(role);
						if (shapeGR == null) {
							shapeGR = role.getGraphicalRepresentation();
						}
						ExampleDiagramObject container = null;
						if (role.getParentShapePatternRole() != null) {
							logger.info("Adding shape " + role + " under " + role.getParentShapePatternRole());
							container = grHierarchy.get(role.getParentShapePatternRole());
						} else {
							logger.info("Adding shape " + role + " as root");
							container = rootContainer;
							if (diagramPaletteElement.getBoundLabelToElementName()) {
								shapeGR.setText(diagramPaletteElement.getName());
							}
							shapeGR.setX(dropLocation.x);
							shapeGR.setY(dropLocation.y);
						}
						AddExampleDiagramShape addShapeAction = AddExampleDiagramShape.actionType.makeNewAction(container, null,
								getEditor().getVPMController().getEditor());
						addShapeAction.graphicalRepresentation = shapeGR;
						addShapeAction.newShapeName = role.getPatternRoleName();
						if (role.getParentShapePatternRole() == null) {
							addShapeAction.newShapeName = diagramPaletteElement.getName();
						}
						addShapeAction.doAction();
						grHierarchy.put(role, addShapeAction.getNewShape());
					}
				}

				/*ShapeGraphicalRepresentation shapeGR = getGraphicalRepresentation().clone();
				shapeGR.setIsSelectable(true);
				shapeGR.setIsFocusable(true);
				shapeGR.setIsReadOnly(false);
				shapeGR.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
				shapeGR.setLocation(dropLocation);
				shapeGR.setLayer(containerGR.getLayer() + 1);
				shapeGR.setAllowToLeaveBounds(true);

				AddExampleDiagramShape action = AddExampleDiagramShape.actionType.makeNewAction(container, null, getController()
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
		public ShapeGraphicalRepresentation getGraphicalRepresentation() {
			return diagramPaletteElement.getGraphicalRepresentation();
		}

		@Override
		public void delete() {
			diagramPaletteElement = null;
		}

	}

}
