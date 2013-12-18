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
package org.openflexo.fme;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseClickControlActionImpl;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fme.model.Concept;
import org.openflexo.fme.model.Connector;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.DiagramFactory;
import org.openflexo.fme.model.Shape;
import org.openflexo.model.undo.CompoundEdit;

public class PipetteControl extends MouseClickControlImpl<DianaDrawingEditor> {

	public PipetteControl(DiagramFactory factory) {
		super("Pipette", MouseButton.RIGHT, 1, new PipetteControlAction(factory), false, false, false, false, factory); // CTRL-DRAG
	}

	protected static class PipetteControlAction extends MouseClickControlActionImpl<DianaDrawingEditor> {

		private DiagramFactory factory;
		private DiagramElement<?, ?> diagramElementToApply;
		private Concept currentConcept;
		private DiagramEditor diagramEditor;
		private GraphicalRepresentation currentGraphicalRepresentation;

		public PipetteControlAction(DiagramFactory factory) {
			this.factory = factory;
		}

		@Override
		public boolean handleClick(DrawingTreeNode<?, ?> dtn, DianaDrawingEditor controller, MouseControlContext context) {

			if (((DianaDrawingEditor) controller).getDiagramEditor().hasPipette()) {

				CompoundEdit applyPipette = null;
				if (!factory.getUndoManager().isBeeingRecording()) {
					applyPipette = factory.getUndoManager().startRecording("Apply Pipette");
				}

				// Prepare variables
				diagramEditor = ((DianaDrawingEditor) controller).getDiagramEditor();
				currentConcept = diagramEditor.getCurrentConcept();
				diagramElementToApply = (DiagramElement<?, ?>) dtn.getDrawable();
				currentGraphicalRepresentation = diagramEditor.getCurrentGraphicalRepresentation();

				// Update Concept
				if (currentConcept != null) {
					applyConcept();
				}
				// Update Graphical Properties
				else if (currentGraphicalRepresentation != null) {
					applyGraphicalProperties();
				}

				if (applyPipette != null) {
					factory.getUndoManager().stopRecording(applyPipette);
				}

				return true;
			}
			return false;
		}

		private void applyConcept() {
			Concept oldConcept = diagramElementToApply.getAssociation().getConcept();
			oldConcept.removeFromInstances(diagramElementToApply.getInstance());
			diagramElementToApply.getInstance().setConcept(currentConcept);
			currentConcept.addToInstances(diagramElementToApply.getInstance());
			diagramElementToApply.getAssociation().setConcept(currentConcept);
		}

		private void applyGraphicalProperties() {
			if (currentGraphicalRepresentation instanceof ShapeGraphicalRepresentation) {

				Shape oldShape = (Shape) diagramElementToApply;
				ShapeGraphicalRepresentation currentSGR = (ShapeGraphicalRepresentation) currentGraphicalRepresentation;
				ShapeGraphicalRepresentation oldSGR = (ShapeGraphicalRepresentation) oldShape.getGraphicalRepresentation();
				ShapeGraphicalRepresentation newSGR = factory.makeShapeGraphicalRepresentation(currentSGR);
				oldShape.setGraphicalRepresentation(newSGR);

				// Update GR
				Shape newShape = diagramEditor.createNewShape(oldShape.getDiagram(), currentSGR, oldSGR.getLocation());
				List<DiagramElement<?, ?>> elements = new ArrayList<DiagramElement<?, ?>>();
				elements.add(oldShape);
				diagramEditor.delete(elements);

			}

			if (currentGraphicalRepresentation instanceof ConnectorGraphicalRepresentation) {
				ConnectorGraphicalRepresentation currentSGR = (ConnectorGraphicalRepresentation) currentGraphicalRepresentation;
				ConnectorGraphicalRepresentation oldCGR = (ConnectorGraphicalRepresentation) diagramElementToApply
						.getGraphicalRepresentation();
				Connector oldConnector = (Connector) diagramElementToApply;

				// Update GR
				Connector newConnector = factory.makeNewConnector(currentSGR, oldConnector.getStartShape(), oldConnector.getEndShape(),
						oldConnector.getDiagram());
				newConnector.setAssociation(diagramElementToApply.getAssociation());
				newConnector.setInstance(diagramElementToApply.getInstance());
				diagramEditor.getDiagram().addToConnectors(newConnector);
				diagramEditor.getDiagram().removeFromConnectors(oldConnector);
				diagramElementToApply.delete(null);
			}
		}
	}

}
