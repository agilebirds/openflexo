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

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fme.model.ConceptGRAssociation;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.Shape;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.undo.CompoundEdit;

public class DynamicPalette extends DrawingPalette implements PropertyChangeListener {

	private static final Logger logger = FlexoLogger.getLogger(DynamicPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 50;
	private static final int GRID_HEIGHT = 40;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 7);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private DiagramEditor editor;

	private Hashtable<ConceptGRAssociation, PaletteElement> elementsForAssociations;

	public DynamicPalette() {
		super(200, 200, "default");
		elementsForAssociations = new Hashtable<ConceptGRAssociation, PaletteElement>();
	}

	public void update() {

		List<PaletteElement> elementsToAdd = new ArrayList<PaletteElement>();
		List<PaletteElement> elementsToRemove = new ArrayList<PaletteElement>(getElements());

		// For each existing association
		for (ConceptGRAssociation association : getEditor().getDiagram().getAssociations()) {
			// Retrieve the corresponding palette element
			PaletteElement e = elementsForAssociations.get(association);

			if (getEditor().getDiagram().getElementsWithAssociation(association).isEmpty()) {
				if (getEditor().getDiagram().getElementsWithGraphicalRepresentation(association.getGraphicalRepresentation()).isEmpty()) {
					// If there is no graphical element then we can delete the palette element
					System.out.println("No diagram elements with this palette element, delete the palette element");
					elementsForAssociations.remove(association);
				}

			}
			// If a palestte element exist
			else if (e != null) {
				elementsToRemove.remove(e);
			} else if (association.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				e = new DynamicPaletteElement(association);
				elementsForAssociations.put(association, e);
				elementsToAdd.add(e);
			}

			/*PaletteElement e = elementsForAssociations.get(association);
			if (e != null) {
				elementsToRemove.remove(e);
			} else {
				e = new DynamicPaletteElement(association);
				elementsForAssociations.put(association, e);
				elementsToAdd.add(e);
			}*/

		}
		for (PaletteElement e : elementsToRemove) {
			removeElement(e);
		}
		for (PaletteElement e : elementsToAdd) {
			addElement(e);
		}

		for (PaletteElement e : getElements()) {
			int px, py;
			int index = getElements().indexOf(e);

			px = index % 3;
			py = (int) index / 3;

			// FACTORY.applyDefaultProperties(gr);
			if (e.getGraphicalRepresentation().getShapeSpecification().getShapeType() == ShapeType.SQUARE
					|| e.getGraphicalRepresentation().getShapeSpecification().getShapeType() == ShapeType.CIRCLE) {
				e.getGraphicalRepresentation().setX(px * GRID_WIDTH + 15);
				e.getGraphicalRepresentation().setY(py * GRID_HEIGHT + 10);
				e.getGraphicalRepresentation().setWidth(30);
				e.getGraphicalRepresentation().setHeight(30);
			} else {
				e.getGraphicalRepresentation().setX(px * GRID_WIDTH + 10);
				e.getGraphicalRepresentation().setY(py * GRID_HEIGHT + 10);
				e.getGraphicalRepresentation().setWidth(40);
				e.getGraphicalRepresentation().setHeight(30);
			}

		}
	}

	public PaletteElement getPaletteElement(ConceptGRAssociation association) {
		return elementsForAssociations.get(association);
	}

	/*public void addAssociation(ConceptGRAssociation association) {
		if (association.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			addElement(makePaletteElement((ShapeGraphicalRepresentation) association.getGraphicalRepresentation().cloneObject()));
		}
	}*/

	public DiagramEditor getEditor() {
		return editor;
	}

	public void setEditor(DiagramEditor editor) {
		if (editor != getEditor()) {
			if (this.editor != null) {
				this.editor.getDiagram().getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			this.editor = editor;
			List<PaletteElement> elementsToRemove = new ArrayList<PaletteElement>(getElements());
			for (PaletteElement e : elementsToRemove) {
				removeElement(e);
				elementsForAssociations.clear();

			}
			update();
			this.editor.getDiagram().getPropertyChangeSupport().addPropertyChangeListener(this);

		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("On update la palette dynamique");
		update();
	}

	public class DynamicPaletteElement implements PaletteElement {

		private ConceptGRAssociation association;
		private ShapeGraphicalRepresentation elementGR;

		public DynamicPaletteElement(ConceptGRAssociation association) {
			this.association = association;
			elementGR = (ShapeGraphicalRepresentation) ((ShapeGraphicalRepresentation) association.getGraphicalRepresentation());

			elementGR.setIsFloatingLabel(false);
			elementGR.setIsVisible(true);
			elementGR.setAllowToLeaveBounds(false);

		}

		@Override
		public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
			return getEditor() != null && target instanceof ContainerNode;
		}

		@Override
		public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation) {

			if (getEditor() == null) {
				return false;
			}

			DiagramElement<?, ?> container = (DiagramElement<?, ?>) target.getDrawable();

			logger.info("dragging " + this + " in " + container);

			// getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation,
			// getController().getDrawing()),container);

			CompoundEdit edit = getEditor().getFactory().getUndoManager().startRecording("Dragging new Element");

			Shape newShape = getEditor().createNewShape(container, association, dropLocation);
			newShape.getGraphicalRepresentation().setWidth(50);
			newShape.getGraphicalRepresentation().setHeight(40);

			getEditor().getFactory().getUndoManager().stopRecording(edit);

			getEditor().getController().setCurrentTool(EditorTool.SelectionTool);
			getEditor().getController().setSelectedObject(getEditor().getDrawing().getDrawingTreeNode(newShape));

			return true;
		}

		@Override
		public ShapeGraphicalRepresentation getGraphicalRepresentation() {
			// return (ShapeGraphicalRepresentation) association.getGraphicalRepresentation();
			return elementGR;
		}

		@Override
		public void delete() {
			association = null;
		}

	}
}
