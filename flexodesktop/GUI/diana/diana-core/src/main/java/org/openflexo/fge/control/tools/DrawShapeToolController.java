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
package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.GeometricGRBinding;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.FGEDrawingGraphicsImpl;
import org.openflexo.fge.impl.ContainerNodeImpl;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.impl.GeometricNodeImpl;
import org.openflexo.fge.notifications.GeometryModified;
import org.openflexo.fge.view.DrawingView;

public abstract class DrawShapeToolController<S extends FGEShape<S>, ME> extends Observable implements Observer {

	private static final Logger logger = Logger.getLogger(DrawShapeToolController.class.getPackage().getName());

	private DianaInteractiveEditor<?, ?, ?> controller;
	private DrawShapeAction control;

	protected DrawingTreeNode<?, ?> parentNode = null;

	private S shape;
	private GeometricNode<S> currentEditedShapeGeometricNode;

	private boolean editionHasBeenStarted = false;

	private ForegroundStyle currentlyEditedForeground;

	public DrawShapeToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		super();
		this.controller = controller;
		this.control = control;
		editionHasBeenStarted = false;

		currentlyEditedForeground = controller.getFactory().makeForegroundStyle(Color.GREEN);
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	protected void startMouseEdition(ME e) {
		editionHasBeenStarted = true;
		parentNode = getFocusedObject(e);
		shape = makeDefaultShape(e);
		Class<S> shapeClass = (Class<S>) TypeUtils.getTypeArgument(getClass(), DrawShapeToolController.class, 0);
		GeometricGRBinding<S> editedGeometricObjectBinding = getController().getDrawing().bindGeometric(shapeClass,
				"editedGeometricObject", null);
		GeometricGraphicalRepresentation geomGR = getController().getFactory().makeGeometricGraphicalRepresentation(shape);
		currentEditedShapeGeometricNode = new GeometricNodeImpl<S>((DrawingImpl<?>) getController().getDrawing(), shape,
				editedGeometricObjectBinding, (ContainerNodeImpl<?, ?>) getController().getDrawing().getRoot());
		currentEditedShapeGeometricNode.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object dataModification) {
				if (dataModification instanceof GeometryModified) {
					geometryChanged();
				}
			}
		});
		// TODO Check this / fge_under_pamela
		/*currentEditedShapeGeometricNode = new GeometricGraphicalRepresentationImpl(shape, shape, controller.getDrawing()) {
			@Override
			public void notifyGeometryChanged() {
				super.notifyGeometryChanged();
				geometryChanged();
			}
		};*/
		currentEditedShapeGeometricNode.getGraphicalRepresentation().setBackground(getController().getCurrentBackgroundStyle());
		currentEditedShapeGeometricNode.getGraphicalRepresentation().setForeground(getController().getCurrentForegroundStyle());
		geometryChanged();
	}

	protected void stopMouseEdition() {
		editionHasBeenStarted = false;
	}

	public boolean editionHasBeenStarted() {
		return editionHasBeenStarted;
	}

	public abstract S makeDefaultShape(ME e);

	/**
	 * Returns shape currently being edited (using DrawShape tool)
	 * 
	 * @return
	 */
	public S getShape() {
		return shape;
	}

	public void setShape(FGEShape<?> shape) {
		this.shape = (S) shape.clone();
		currentEditedShapeGeometricNode.getGraphicalRepresentation().setGeometricObject(this.shape);
		geometryChanged();
	}

	public DianaInteractiveEditor<?, ?, ?> getController() {
		return controller;
	}

	/**
	 * Returns graphical representation for shape currently being edited (using DrawShape tool)
	 * 
	 * @return
	 */
	public GeometricNode<S> getCurrentEditedShape() {
		return currentEditedShapeGeometricNode;
	}

	protected void geometryChanged() {
		controller.getDelegate().repaintAll();
	}

	@Override
	public void update(Observable o, Object arg) {
		logger.info("update in DrawShapeToolController");
	}

	public void mouseClicked(ME e) {
		// System.out.println("mouseClicked() on " + getPoint(e));
	}

	public void mousePressed(ME e) {
		// System.out.println("mousePressed() on " + getPoint(e));
	}

	public void mouseReleased(ME e) {
		// System.out.println("mouseReleased() on " + getPoint(e));
	}

	public void mouseDragged(ME e) {
		// System.out.println("mouseDragged() on " + getPoint(e));
	}

	public void mouseMoved(ME e) {
		// System.out.println("mouseMoved() on " + getPoint(e));
	}

	public abstract DrawingTreeNode<?, ?> getFocusedObject(ME e);

	public abstract FGEPoint getPoint(ME e);

	public void paintCurrentEditedShape(FGEDrawingGraphicsImpl graphics) {

		if (!editionHasBeenStarted) {
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(currentlyEditedForeground);

		currentEditedShapeGeometricNode.paint(graphics.getGraphics(), getController());

		graphics.releaseClonedGraphics(oldGraphics);
	}

	public List<? extends ControlArea<?>> getControlAreas() {
		return currentEditedShapeGeometricNode.getControlPoints();
	}

	public abstract ShapeGraphicalRepresentation buildShapeGraphicalRepresentation();

	public void makeNewShape() {
		if (control != null && parentNode instanceof ContainerNode) {
			ShapeGraphicalRepresentation newShapeGraphicalRepresentation = buildShapeGraphicalRepresentation();
			control.performedDrawNewShape(newShapeGraphicalRepresentation, (ContainerNode<?, ?>) parentNode);
		} else {
			logger.warning("No DrawShapeAction defined !");
		}
	}
}
