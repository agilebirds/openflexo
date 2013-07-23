package org.openflexo.fge.connectors.impl;

import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.shapes.impl.Shape;

/**
 * This is an instance of a {@link ConnectorSpecification}. As it, it is attached to a {@link ConnectorNode}. A {@link Connector} observes
 * its {@link ConnectorSpecification}
 * 
 * @author sylvain
 * 
 */
public abstract class Connector<CS extends ConnectorSpecification> implements Observer {

	private static final Logger logger = Logger.getLogger(ConnectorSpecification.class.getPackage().getName());

	// private transient ConnectorGraphicalRepresentation graphicalRepresentation;

	private boolean debug = false;

	protected ConnectorNode<?> connectorNode;

	protected FGERectangle NORMALIZED_BOUNDS = new FGERectangle(0, 0, 1, 1, Filling.FILLED);

	private Shape<?> startShape;
	private FGEDimension startShapeDimension;
	private FGEPoint startShapeLocation;
	private Shape<?> endShape;
	private FGEDimension endShapeDimension;
	private FGEPoint endShapeLocation;
	private FGERectangle knownConnectorUsedBounds;

	public Connector(ConnectorNode<?> connectorNode) {
		super();
		this.connectorNode = connectorNode;
	}

	public void delete() {
		if (getConnectorSpecification() != null) {
			getConnectorSpecification().deleteObserver(this);
		}
		connectorNode = null;
		startShape = null;
		startShape = null;
		startShapeLocation = null;
		endShape = null;
		endShape = null;
		endShape = null;
		knownConnectorUsedBounds = null;
	}

	@SuppressWarnings("unchecked")
	public CS getConnectorSpecification() {
		return (CS) connectorNode.getGraphicalRepresentation().getConnector();
	}

	public ConnectorNode<?> getConnectorNode() {
		return connectorNode;
	}

	public ShapeNode<?> getStartNode() {
		if (connectorNode == null) {
			return null;
		}
		return connectorNode.getStartNode();
	}

	public ShapeNode<?> getEndNode() {
		if (connectorNode == null) {
			return null;
		}
		return connectorNode.getEndNode();
	}

	public ConnectorType getConnectorType() {
		return getConnectorSpecification().getConnectorType();
	}

	// *******************************************************************************
	// * Abstract Methods *
	// *******************************************************************************

	public abstract double getStartAngle();

	public abstract double getEndAngle();

	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	public abstract void drawConnector(FGEConnectorGraphics g);

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<? extends ControlArea<?>> getControlAreas();

	public abstract FGEPoint getMiddleSymbolLocation();

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	public abstract FGERectangle getConnectorUsedBounds();

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	public abstract FGEPoint getStartLocation();

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	public abstract FGEPoint getEndLocation();

	// *******************************************************************************
	// * Implementation *
	// *******************************************************************************

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		refreshConnector();
	}

	public void connectorWillBeModified() {

	}

	public void connectorHasBeenModified() {

	}

	public void setPaintAttributes(FGEConnectorGraphics g) {

		// Foreground
		if (connectorNode.getGraphicalRepresentation().getIsSelected()) {
			if (connectorNode.getGraphicalRepresentation().getHasSelectedForeground()) {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getSelectedForeground());
			} else if (connectorNode.getGraphicalRepresentation().getHasFocusedForeground()) {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getFocusedForeground());
			} else {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
			}
		} else if (connectorNode.getGraphicalRepresentation().getIsFocused()
				&& connectorNode.getGraphicalRepresentation().getHasFocusedForeground()) {
			g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getFocusedForeground());
		} else {
			g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
		}

		g.setDefaultTextStyle(connectorNode.getGraphicalRepresentation().getTextStyle());
	}

	public final void paintConnector(FGEConnectorGraphics g) {
		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		setPaintAttributes(g);
		drawConnector(g);
	}

	/**
	 * Perform an area computation related to the both extremity objects
	 * 
	 * If order equals 0, return intersection between shapes representing the two object If order equals 1, return intersection of
	 * 
	 * @param order
	 * @return
	 */
	public FGEArea computeCoveringArea(int order) {
		AffineTransform at1 = FGEUtils.convertNormalizedCoordinatesAT(connectorNode.getStartNode(), connectorNode);

		AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(connectorNode.getEndNode(), connectorNode);

		if (order == 0) {
			FGEArea startObjectShape = connectorNode.getStartNode().getFGEShape().transform(at1);
			FGEArea endObjectShape = connectorNode.getEndNode().getFGEShape().transform(at2);
			FGEArea returned = startObjectShape.intersect(endObjectShape);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("computeCoveringArea(" + order + ") = " + returned);
			}
			return returned;
		}

		FGEArea start_east = connectorNode.getStartNode().getShape().getAllowedHorizontalConnectorLocationFromEast().transform(at1);
		FGEArea start_west = connectorNode.getStartNode().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at1);
		FGEArea start_north = connectorNode.getStartNode().getShape().getAllowedVerticalConnectorLocationFromNorth().transform(at1);
		FGEArea start_south = connectorNode.getStartNode().getShape().getAllowedVerticalConnectorLocationFromSouth().transform(at1);

		FGEArea end_east = connectorNode.getEndNode().getShape().getAllowedHorizontalConnectorLocationFromEast().transform(at2);
		FGEArea end_west = connectorNode.getEndNode().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at2);
		FGEArea end_north = connectorNode.getEndNode().getShape().getAllowedVerticalConnectorLocationFromNorth().transform(at2);
		FGEArea end_south = connectorNode.getEndNode().getShape().getAllowedVerticalConnectorLocationFromSouth().transform(at2);

		FGEArea returned = new FGEEmptyArea();

		if (order == 1) {
			returned = FGEUnionArea.makeUnion(start_east.intersect(end_west), start_west.intersect(end_east),
					start_north.intersect(end_south), start_south.intersect(end_north));
		}

		else if (order == 2) {
			returned = FGEUnionArea.makeUnion(start_east.intersect(end_north), start_east.intersect(end_south),
					start_west.intersect(end_north), start_west.intersect(end_south), start_north.intersect(end_east),
					start_north.intersect(end_west), start_south.intersect(end_east), start_south.intersect(end_west));
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("computeCoveringArea(" + order + ") = " + returned);
		}

		return returned;
	}

	public final void refreshConnector() {
		refreshConnector(false);
	}

	public void refreshConnector(boolean forceRefresh) {
		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { computeCoveringAreas(); }
		 */
		storeLayoutOfStartOrEndObject(connectorNode);
	}

	public boolean needsRefresh() {
		return /*getGraphicalRepresentation().isRegistered() &&*/layoutOfStartOrEndObjectHasChanged(connectorNode);
	}

	private void storeLayoutOfStartOrEndObject(ConnectorNode<?> connectorNode) {
		startShape = connectorNode.getStartNode().getShape();
		startShapeDimension = connectorNode.getStartNode().getSize();
		startShapeLocation = connectorNode.getStartNode().getLocationInDrawing();
		endShape = connectorNode.getEndNode().getShape();
		endShapeDimension = connectorNode.getEndNode().getSize();
		endShapeLocation = connectorNode.getEndNode().getLocationInDrawing();
		knownConnectorUsedBounds = getConnectorUsedBounds();
	}

	private boolean layoutOfStartOrEndObjectHasChanged(ConnectorNode<?> connectorNode) {
		// if (true) return true;
		if (startShape == null || startShape != null && startShape.getShapeType() != connectorNode.getStartNode().getShape().getShapeType()) {
			// logger.info("Layout has changed because start shape change");
			return true;
		}
		if (startShapeDimension == null || startShapeDimension != null
				&& !startShapeDimension.equals(connectorNode.getStartNode().getSize())) {
			// logger.info("Layout has changed because start shape dimension change");
			return true;
		}
		if (startShapeLocation == null || startShapeLocation != null
				&& !startShapeLocation.equals(connectorNode.getStartNode().getLocationInDrawing())) {
			// logger.info("Layout has changed because start shape location change");
			return true;
		}
		if (endShape == null || endShape != null && endShape.getShapeType() != connectorNode.getEndNode().getShape().getShapeType()) {
			// logger.info("Layout has changed because end shape change");
			return true;
		}
		if (endShapeDimension == null || endShapeDimension != null && !endShapeDimension.equals(connectorNode.getEndNode().getSize())) {
			// logger.info("Layout has changed because end shape dimension change");
			return true;
		}
		if (endShapeLocation == null || endShapeLocation != null
				&& !endShapeLocation.equals(connectorNode.getEndNode().getLocationInDrawing())) {
			// logger.info("Layout has changed because end shape location change");
			return true;
		}
		if (knownConnectorUsedBounds == null || knownConnectorUsedBounds != null
				&& !knownConnectorUsedBounds.equals(getConnectorUsedBounds())) {
			// logger.info("Layout has changed because knownConnectorUsedBounds change");
			return true;
		}
		return false;
	}

	protected void notifyConnectorModified() {
		connectorNode.notifyConnectorModified();
	}

	@Override
	public void update(Observable observable, Object notification) {
	}

}
