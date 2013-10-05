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
package org.openflexo.fge.control.actions;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.view.FGEView;

/**
 * Utility class used to store a move
 * 
 * @author sylvain
 * 
 */
public class MoveInfo {

	private static final Logger logger = Logger.getLogger(MoveInfo.class.getPackage().getName());

	private FGEView<?, ?> view;
	private Point startMovingLocationInDrawingView;
	private Hashtable<ShapeNode<?>, FGEPoint> movedObjects;
	private ShapeNode<?> movedObject;

	private boolean moveHasStarted = false;

	private Point currentLocationInDrawingView;

	public MoveInfo(ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller) {
		view = controller.getDrawingView();

		startMovingLocationInDrawingView = FGEUtils.convertPointFromDrawableToDrawing(shapeNode.getParentNode(),
				new Point(shapeNode.getViewX(controller.getScale()), shapeNode.getViewY(controller.getScale())), controller.getScale());
		currentLocationInDrawingView = new Point(startMovingLocationInDrawingView);

		movedObject = shapeNode;

		if (!controller.getSelectedObjects().contains(movedObject)) {
			controller.setSelectedObject(movedObject);
		}

		movedObjects = new Hashtable<ShapeNode<?>, FGEPoint>();
		movedObjects.put(movedObject, movedObject.getLocation());

		// Now see objects coming with
		for (DrawingTreeNode<?, ?> o : controller.getSelectedObjects()) {
			if (o != shapeNode && o instanceof ShapeNode && ((ShapeNode<?>) o).getParentNode() == shapeNode.getParentNode()
					&& !o.getGraphicalRepresentation().getIsReadOnly()
					&& ((ShapeNode<?>) o).getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {
				// OK, o comes with me
				movedObjects.put((ShapeNode<?>) o, ((ShapeNode<?>) o).getLocation());
			}
		}

	}

	public MoveInfo(ShapeNode<?> shapeNode, MouseEvent e, FGEView<?, ?> view, DianaInteractiveViewer<?, ?, ?> controller) {
		this(shapeNode, controller);

		this.view = view;
		try {
			startMovingLocationInDrawingView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), view.getDrawingView());
		} catch (Error ex) {
			ex.printStackTrace();
			logger.warning("OK, ca chie la");
			logger.warning("e.getSource()=" + e.getSource());
			logger.warning("view " + Integer.toHexString(view.hashCode()));
			logger.warning("view.isDeleted()=" + view.isDeleted());
			logger.warning("view.getDrawingView()=" + view.getDrawingView());
			// logger.warning("view.getDrawingView().isDeleted()="+view.getDrawingView().isDeleted());
		}
		currentLocationInDrawingView = new Point(startMovingLocationInDrawingView);

	}

	private void startDragging() {
		for (ShapeNode<?> shapeNode : movedObjects.keySet()) {
			shapeNode.notifyObjectWillMove();
		}

		if (movedObject.isParentLayoutedAsContainer()) {
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectWillMove();
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectWillResize();
			for (DrawingTreeNode<?, ?> child : movedObject.getChildNodes()) {
				if (child instanceof ShapeNode) {
					((ShapeNode<?>) child).notifyObjectWillMove();
				}
			}
		}

		moveHasStarted = true;
	}

	public void moveTo(Point newLocationInDrawingView) {

		if (!moveHasStarted) {
			startDragging();
		}

		for (ShapeNode<?> d : movedObjects.keySet()) {
			FGEPoint startMovingPoint = movedObjects.get(d);

			FGEPoint desiredLocation = new FGEPoint(startMovingPoint.x + (newLocationInDrawingView.x - startMovingLocationInDrawingView.x)
					/ view.getScale(), startMovingPoint.y + (newLocationInDrawingView.y - startMovingLocationInDrawingView.y)
					/ view.getScale());

			/*double authorizedRatio = d.getMoveAuthorizedRatio(desiredLocation,startMovingPoint);
			FGEPoint newLocation = new FGEPoint(
					startMovingPoint.x+(desiredLocation.x-startMovingPoint.x)*authorizedRatio,
					startMovingPoint.y+(desiredLocation.y-startMovingPoint.y)*authorizedRatio);
			logger.info("\n>>>>>>>>>>> setLocation() from "+d.getLocation()+" to "+newLocation+" on "+d);
			d.setLocation(newLocation.clone());*/

			d.setLocation(desiredLocation);

			if (d.isParentLayoutedAsContainer()) {
				FGEPoint resultingLocation = d.getLocation();
				if (!resultingLocation.equals(desiredLocation)) {
					double dx = resultingLocation.x - desiredLocation.x;
					double dy = resultingLocation.y - desiredLocation.y;
					startMovingPoint.x = startMovingPoint.x + dx;
					startMovingPoint.y = startMovingPoint.y + dy;
				}
			}

		}

		currentLocationInDrawingView = newLocationInDrawingView;
	}

	void stopDragging() {
		for (ShapeNode<?> d : movedObjects.keySet()) {
			d.notifyObjectHasMoved();
		}
		if (movedObject.isParentLayoutedAsContainer()) {
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectHasMoved();
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectHasResized();
			for (DrawingTreeNode<?, ?> child : movedObject.getChildNodes()) {
				if (child instanceof ShapeNode) {
					((ShapeNode<?>) child).notifyObjectHasMoved();
				}
			}
		}
	}

	boolean isDnDPattern(Point newLocationInDrawingView, MouseEvent event) {
		if (movedObjects.size() != 1) {
			return false;
		}

		FGEPoint startMovingPoint = movedObjects.get(movedObject);

		FGEPoint desiredLocation = new FGEPoint(startMovingPoint.x + (newLocationInDrawingView.x - startMovingLocationInDrawingView.x)
				/ view.getScale(), startMovingPoint.y + (newLocationInDrawingView.y - startMovingLocationInDrawingView.y) / view.getScale());

		if (movedObject.getParentNode() instanceof ShapeNode) {
			ShapeNode<?> container = (ShapeNode<?>) movedObject.getParentNode();
			FGERectangle bounds = new FGERectangle(0, 0, container.getWidth() - movedObject.getWidth(), container.getHeight()
					- movedObject.getHeight(), Filling.FILLED);
			FGEPoint nearestPoint = bounds.getNearestPoint(desiredLocation);
			Point p1 = FGEUtils.convertPointFromDrawableToDrawing(movedObject.getParentNode(), desiredLocation.toPoint(), view.getScale());
			Point p2 = FGEUtils.convertPointFromDrawableToDrawing(movedObject.getParentNode(), nearestPoint.toPoint(), view.getScale());
			if (Point2D.distance(p1.x, p1.y, p2.x, p2.y) > FGEConstants.DND_DISTANCE) {
				return true;
			}
		}

		return false;
	}

	public ShapeNode<?> getMovedObject() {
		return movedObject;
	}

	public Set<ShapeNode<?>> getMovedObjects() {
		return movedObjects.keySet();
	}

	public Hashtable<ShapeNode<?>, FGEPoint> getInitialLocations() {
		return movedObjects;
	}

	public Point getInitialLocationInDrawingView() {
		return startMovingLocationInDrawingView;
	}

	public Point getCurrentLocationInDrawingView() {
		return currentLocationInDrawingView;
	}

}