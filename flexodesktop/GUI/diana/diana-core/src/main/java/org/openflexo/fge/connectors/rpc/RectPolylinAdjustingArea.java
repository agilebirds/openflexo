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
package org.openflexo.fge.connectors.rpc;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.impl.RectPolylinConnector;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.graphics.FGEGraphics;

public class RectPolylinAdjustingArea extends ControlArea<FGERectPolylin> {

	private static final Hashtable<Integer, Image> PIN_CACHE = new Hashtable<Integer, Image>();
	protected FGERectPolylin initialPolylin;
	private RectPolylinConnector connector;

	// private FGERectPolylin newPolylin;

	public RectPolylinAdjustingArea(RectPolylinConnector connector) {
		super(connector.getConnectorNode(), connector.getCurrentPolylin());
		this.connector = connector;
	}

	@Override
	public ConnectorNode<?> getNode() {
		return (ConnectorNode<?>) super.getNode();
	}

	@Override
	public FGEArea getDraggingAuthorizedArea() {
		return new FGEPlane();
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		/*AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getStartObject(), getGraphicalRepresentation());
		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getEndObject(), getGraphicalRepresentation());
		FGEArea startArea = getConnector().getStartObject().getShape().getShape().transform(at1);
		FGEArea endArea = getConnector().getEndObject().getShape().getShape().transform(at2);

		newPolylin = FGERectPolylin.makeRectPolylinCrossingPoint(
				startArea, endArea, newRelativePoint,
				getConnector().getStartOrientation(),
				getConnector().getEndOrientation(),
				true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
		getConnector().getBasicallyAdjustableControlPoint().setPoint(newRelativePoint);
		getConnector().updateWithNewPolylin(newPolylin);*/

		getConnectorSpecification().setCrossedControlPoint(newRelativePoint);

		// getConnector().updateLayout();

		// getConnector()._updateAsBasicallyAdjustable();

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return true;
	}

	protected void notifyConnectorChanged() {
		getNode().notifyConnectorModified();
	}

	@Override
	public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
		if (controller instanceof AbstractDianaEditor) {
			if (((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().isPaintingCacheEnabled()) {
				((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().addToTemporaryObjects(getNode());
				((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().invalidate(getNode());
			}
		}
		initialPolylin = getPolylin().clone();
		// getConnector().setWasManuallyAdjusted(true);

	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		super.stopDragging(controller, focused);
		if (controller instanceof AbstractDianaEditor) {
			if (((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().isPaintingCacheEnabled()) {
				((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().removeFromTemporaryObjects(getNode());
				((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().invalidate(getNode());
				((AbstractDianaEditor<?, ?, ?>) controller).getPaintManager().repaint(
						((AbstractDianaEditor<?, ?, ?>) controller).getDrawingView());
			}
		}
		// getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public Cursor getDraggingCursor() {
		return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public Rectangle paint(FGEGraphics graphics) {
		/*System.out.println("prout");*/
		FGEPoint crossedControlPoint = getConnector().getCrossedControlPointOnRoundedArc();
		if (crossedControlPoint != null) {
			int pinSize = graphics.getScale() <= 1 ? 16 : (int) (16.0 / 2 * (1.0 + graphics.getScale()));
			Image PIN = getPinForPinSize(pinSize);
			// int d = (int) (PIN_SIZE * graphics.getScale());
			// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
			Point p = getNode().convertLocalNormalizedPointToRemoteViewCoordinates(crossedControlPoint, graphics.getNode(), 1.0);
			p.x -= (int) (54.0d / 196.0d * pinSize);
			p.y -= (int) (150.0d / 196.0d * pinSize);
			graphics.drawImage(PIN, new FGEPoint(p.x, p.y));
			// g.drawImage(FGEConstants.PIN_ICON.getImage(), ), , d, d, null);
		}
		return null;
	}

	public Image getPinForPinSize(int pinSize) {
		Image returned = PIN_CACHE.get(pinSize);
		if (returned == null) {
			PIN_CACHE.put(pinSize, returned = FGEIconLibrary.PIN_ICON.getImage().getScaledInstance(pinSize, pinSize, Image.SCALE_SMOOTH));
		}
		return returned;
	}

	/*private Rectangle paintPolylin(Graphics2D g, JDrawingView<?> drawingView, Color mainColor, Color backColor, FGERectPolylin polylin)
	{
		Rectangle r = new Rectangle();
		Point lastLocation = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(polylin.getFirstPoint(), getGraphicalRepresentation(), drawingView.getScale());
		for (int i=1; i<polylin.getPointsNb(); i++) {
			FGEPoint p = polylin.getPointAt(i);
			Point currentLocation = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(p, getGraphicalRepresentation(), drawingView.getScale());
			g.drawLine(lastLocation.x,lastLocation.y,currentLocation.x,currentLocation.y);
			lastLocation = currentLocation;
		}
		return r;
	}*/

	public RectPolylinConnector getConnector() {
		return connector;
	}

	public RectPolylinConnectorSpecification getConnectorSpecification() {
		return connector.getConnectorSpecification();
	}

	/*@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return connector.getGraphicalRepresentation();
	}*/

	public FGERectPolylin getPolylin() {
		return getConnector().getCurrentPolylin();
	}
}
