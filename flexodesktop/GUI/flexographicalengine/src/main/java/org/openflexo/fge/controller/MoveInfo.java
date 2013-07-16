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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.view.FGEView;

public class MoveInfo {
	private static final Logger logger = Logger.getLogger(MoveInfo.class.getPackage().getName());

	private FGEView view;
	private Point startMovingLocationInDrawingView;
	private Hashtable<ShapeGraphicalRepresentation, FGEPoint> movedObjects;
	private ShapeGraphicalRepresentation movedObject;

	private boolean moveHasStarted = false;

	private Point currentLocationInDrawingView;

	MoveInfo(ShapeGraphicalRepresentation graphicalRepresentation, DrawingController controller) {
		view = controller.getDrawingView();

		startMovingLocationInDrawingView = FGEUtils
				.convertPointFromDrawableToDrawing(graphicalRepresentation.getParentGraphicalRepresentation(), new Point(
						graphicalRepresentation.getViewX(controller.getScale()), graphicalRepresentation.getViewY(controller.getScale())),
						controller.getScale());
		currentLocationInDrawingView = new Point(startMovingLocationInDrawingView);

		movedObject = graphicalRepresentation;

		if (!controller.getSelectedObjects().contains(movedObject)) {
			controller.setSelectedObject(movedObject);
		}

		movedObjects = new Hashtable<ShapeGraphicalRepresentation, FGEPoint>();
		movedObjects.put(movedObject, movedObject.getLocation());

		// Now see objects coming with
		for (GraphicalRepresentation d : controller.getSelectedObjects()) {
			if (d != graphicalRepresentation && d instanceof ShapeGraphicalRepresentation
					&& ((ShapeGraphicalRepresentation) d).getContainer() == graphicalRepresentation.getContainer() && !d.getIsReadOnly()
					&& ((ShapeGraphicalRepresentation) d).getLocationConstraints() != LocationConstraints.UNMOVABLE) {
				// OK, d comes with me
				movedObjects.put((ShapeGraphicalRepresentation) d, ((ShapeGraphicalRepresentation) d).getLocation());
			}
		}

	}

	MoveInfo(ShapeGraphicalRepresentation graphicalRepresentation, MouseEvent e, FGEView view, DrawingController controller) {
		this(graphicalRepresentation, controller);

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
		for (ShapeGraphicalRepresentation d : movedObjects.keySet()) {
			d.notifyObjectWillMove();
		}

		if (movedObject.isParentLayoutedAsContainer()) {
			((ShapeGraphicalRepresentation) movedObject.getContainerGraphicalRepresentation()).notifyObjectWillMove();
			((ShapeGraphicalRepresentation) movedObject.getContainerGraphicalRepresentation()).notifyObjectWillResize();
			for (GraphicalRepresentation gr : ((ShapeGraphicalRepresentation) movedObject).getContainedGraphicalRepresentations()) {
				if (gr instanceof ShapeGraphicalRepresentation) {
					((ShapeGraphicalRepresentation) gr).notifyObjectWillMove();
				}
			}
		}

		moveHasStarted = true;
	}

	void moveTo(Point newLocationInDrawingView) {

		if (!moveHasStarted) {
			startDragging();
		}

		for (ShapeGraphicalRepresentation d : movedObjects.keySet()) {
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
		for (ShapeGraphicalRepresentation d : movedObjects.keySet()) {
			d.notifyObjectHasMoved();
		}
		if (movedObject.isParentLayoutedAsContainer()) {
			((ShapeGraphicalRepresentation) movedObject.getContainerGraphicalRepresentation()).notifyObjectHasMoved();
			((ShapeGraphicalRepresentation) movedObject.getContainerGraphicalRepresentation()).notifyObjectHasResized();
			for (GraphicalRepresentation gr : ((ShapeGraphicalRepresentation) movedObject).getContainedGraphicalRepresentations()) {
				if (gr instanceof ShapeGraphicalRepresentation) {
					((ShapeGraphicalRepresentation) gr).notifyObjectHasMoved();
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

		if (movedObject.getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation container = (ShapeGraphicalRepresentation) movedObject.getContainerGraphicalRepresentation();
			FGERectangle bounds = new FGERectangle(0, 0, container.getWidth() - movedObject.getWidth(), container.getHeight()
					- movedObject.getHeight(), Filling.FILLED);
			FGEPoint nearestPoint = bounds.getNearestPoint(desiredLocation);
			Point p1 = FGEUtils.convertPointFromDrawableToDrawing(movedObject.getContainerGraphicalRepresentation(),
					desiredLocation.toPoint(), view.getScale());
			Point p2 = FGEUtils.convertPointFromDrawableToDrawing(movedObject.getContainerGraphicalRepresentation(),
					nearestPoint.toPoint(), view.getScale());
			if (Point2D.distance(p1.x, p1.y, p2.x, p2.y) > FGEConstants.DND_DISTANCE) {
				return true;
			}
		}

		return false;
	}

	public ShapeGraphicalRepresentation getMovedObject() {
		return movedObject;
	}

	public Set<ShapeGraphicalRepresentation> getMovedObjects() {
		return movedObjects.keySet();
	}

	public Hashtable<ShapeGraphicalRepresentation, FGEPoint> getInitialLocations() {
		return movedObjects;
	}

	public Point getInitialLocationInDrawingView() {
		return startMovingLocationInDrawingView;
	}

	public Point getCurrentLocationInDrawingView() {
		return currentLocationInDrawingView;
	}

}