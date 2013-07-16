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
package org.openflexo.fge.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.notifications.GeometryModified;

public abstract class DrawShapeToolController<S extends FGEShape<S>> extends Observable implements Observer {

	private static final Logger logger = Logger.getLogger(DrawShapeToolController.class.getPackage().getName());

	private DrawingController controller;
	private DrawShapeAction control;

	protected GraphicalRepresentation parentGR = null;

	private S shape;
	private GeometricGraphicalRepresentation<S> currentEditedShapeGR;

	private boolean editionHasBeenStarted = false;

	private ForegroundStyle currentlyEditedForeground;

	public DrawShapeToolController(DrawingController controller, DrawShapeAction control) {
		super();
		this.controller = controller;
		this.control = control;
		editionHasBeenStarted = false;

		currentlyEditedForeground = controller.getFactory().makeForegroundStyle(Color.GREEN);
	}

	protected void startMouseEdition(MouseEvent e) {
		editionHasBeenStarted = true;
		parentGR = getController().getDrawingView().getFocusRetriever()
				.getFocusedObject(getController().getDrawingGraphicalRepresentation(), e);
		shape = makeDefaultShape(e);
		currentEditedShapeGR = getController().getFactory().makeGeometricGraphicalRepresentation(shape, shape, controller.getDrawing());
		currentEditedShapeGR.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object dataModification) {
				if (dataModification instanceof GeometryModified) {
					geometryChanged();
				}
			}
		});
		// TODO Check this / fge_under_pamela
		/*currentEditedShapeGR = new GeometricGraphicalRepresentationImpl(shape, shape, controller.getDrawing()) {
			@Override
			public void notifyGeometryChanged() {
				super.notifyGeometryChanged();
				geometryChanged();
			}
		};*/
		currentEditedShapeGR.setBackground(getController().getCurrentBackgroundStyle());
		currentEditedShapeGR.setForeground(getController().getCurrentForegroundStyle());
		geometryChanged();
	}

	protected void stopMouseEdition() {
		editionHasBeenStarted = false;
	}

	public boolean editionHasBeenStarted() {
		return editionHasBeenStarted;
	}

	public abstract S makeDefaultShape(MouseEvent e);

	/**
	 * Returns shape currently being edited (using DrawShape tool)
	 * 
	 * @return
	 */
	public S getShape() {
		return shape;
	}

	public void setShape(FGEShape shape) {
		this.shape = (S) shape.clone();
		currentEditedShapeGR.setGeometricObject(this.shape);
		geometryChanged();
	}

	public DrawingController getController() {
		return controller;
	}

	/**
	 * Returns graphical representation for shape currently being edited (using DrawShape tool)
	 * 
	 * @return
	 */
	public GeometricGraphicalRepresentation<S> getCurrentEditedShapeGR() {
		return currentEditedShapeGR;
	}

	protected void geometryChanged() {
		if (controller.getPaintManager() != null) {
			controller.getPaintManager().repaint(controller.getDrawingView());
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		logger.info("update in DrawShapeToolController");
	}

	public void mouseClicked(MouseEvent e) {
		// System.out.println("mouseClicked() on " + getPoint(e));
	}

	public void mousePressed(MouseEvent e) {
		// System.out.println("mousePressed() on " + getPoint(e));
	}

	public void mouseReleased(MouseEvent e) {
		// System.out.println("mouseReleased() on " + getPoint(e));
	}

	public void mouseDragged(MouseEvent e) {
		// System.out.println("mouseDragged() on " + getPoint(e));
	}

	public void mouseMoved(MouseEvent e) {
		// System.out.println("mouseMoved() on " + getPoint(e));
	}

	protected FGEPoint getPoint(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), controller.getDrawingView());
		return currentEditedShapeGR.convertRemoteViewCoordinatesToLocalNormalizedPoint(pt, controller.getDrawingGraphicalRepresentation(),
				controller.getScale());
	}

	public void paintCurrentEditedShape(FGEDrawingGraphics graphics) {

		if (!editionHasBeenStarted) {
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(currentlyEditedForeground);

		currentEditedShapeGR.paint(graphics.getGraphics(), getController());

		graphics.releaseClonedGraphics(oldGraphics);
	}

	public List<? extends ControlArea<?>> getControlAreas() {
		return currentEditedShapeGR.getControlPoints();
	}

	public abstract ShapeGraphicalRepresentation buildShapeGraphicalRepresentation();

	public void makeNewShape() {
		if (control != null) {
			ShapeGraphicalRepresentation newShapeGraphicalRepresentation = buildShapeGraphicalRepresentation();
			control.performedDrawNewShape(newShapeGraphicalRepresentation, parentGR);
		} else {
			logger.warning("No DrawShapeAction defined !");
		}
	}
}
