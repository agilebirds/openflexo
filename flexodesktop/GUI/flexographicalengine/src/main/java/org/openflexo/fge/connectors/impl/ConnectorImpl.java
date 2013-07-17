package org.openflexo.fge.connectors.impl;

import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.impl.FGEObjectImpl;
import org.openflexo.fge.shapes.Shape;

public abstract class ConnectorImpl extends FGEObjectImpl implements Connector {

	private static final Logger logger = Logger.getLogger(Connector.class.getPackage().getName());

	// private transient ConnectorGraphicalRepresentation graphicalRepresentation;

	private boolean debug = false;

	protected FGERectangle NORMALIZED_BOUNDS = new FGERectangle(0, 0, 1, 1, Filling.FILLED);

	public ConnectorImpl(/*ConnectorGraphicalRepresentation aGraphicalRepresentation*/) {
		super();
		// graphicalRepresentation = aGraphicalRepresentation;
		// labelCP1 = new LabelControlPoint(aGraphicalRepresentation,new FGEPoint());
		// labelCP2 = new LabelControlPoint(aGraphicalRepresentation,new FGEPoint());
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	/*@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(ConnectorGraphicalRepresentation aGraphicalRepresentation) {
		graphicalRepresentation = aGraphicalRepresentation;
	}*/

	@Override
	public abstract double getStartAngle();

	@Override
	public abstract double getEndAngle();

	/*@Override
	public Object getDrawable() {
		return graphicalRepresentation.getDrawable();
	}*/

	@Override
	public ShapeNode<?> getStartNode(ConnectorNode<?> node) {
		if (node == null) {
			return null;
		}
		return node.getStartNode();
	}

	@Override
	public ShapeNode<?> getEndNode(ConnectorNode<?> node) {
		if (node == null) {
			return null;
		}
		return node.getEndNode();
	}

	/**
	 * Return value indicating distance from aPoint to connector, asserting aPoint is related to local normalized coordinates system
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	@Override
	public void setPaintAttributes(ConnectorNode<?> node, FGEConnectorGraphics g) {

		// Foreground
		if (getGraphicalRepresentation().getIsSelected()) {
			if (getGraphicalRepresentation().getHasSelectedForeground()) {
				g.setDefaultForeground(getGraphicalRepresentation().getSelectedForeground());
			} else if (getGraphicalRepresentation().getHasFocusedForeground()) {
				g.setDefaultForeground(getGraphicalRepresentation().getFocusedForeground());
			} else {
				g.setDefaultForeground(getGraphicalRepresentation().getForeground());
			}
		} else if (getGraphicalRepresentation().getIsFocused() && getGraphicalRepresentation().getHasFocusedForeground()) {
			g.setDefaultForeground(getGraphicalRepresentation().getFocusedForeground());
		} else {
			g.setDefaultForeground(getGraphicalRepresentation().getForeground());
		}

		g.setDefaultTextStyle(getGraphicalRepresentation().getTextStyle());
	}

	@Override
	public final void paintConnector(ConnectorNode<?> node, FGEConnectorGraphics g) {
		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		setPaintAttributes(node, g);
		drawConnector(node, g);
	}

	@Override
	public abstract void drawConnector(ConnectorNode<?> node, FGEConnectorGraphics g);

	@Override
	public abstract List<? extends ControlArea<?>> getControlAreas();

	@Override
	public abstract ConnectorType getConnectorType();

	public final void refreshConnector() {
		refreshConnector(false);
	}

	@Override
	public void refreshConnector(boolean forceRefresh) {
		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { computeCoveringAreas(); }
		 */
		storeLayoutOfStartOrEndObject();
	}

	@Override
	public boolean needsRefresh() {
		return getGraphicalRepresentation().isRegistered() && layoutOfStartOrEndObjectHasChanged();
	}

	private void storeLayoutOfStartOrEndObject() {
		startShape = getStartObject().getShape();
		startShapeDimension = getStartObject().getSize();
		startShapeLocation = getStartObject().getLocationInDrawing();
		endShape = getEndObject().getShape();
		endShapeDimension = getEndObject().getSize();
		endShapeLocation = getEndObject().getLocationInDrawing();
		knownConnectorUsedBounds = getConnectorUsedBounds();
	}

	private boolean layoutOfStartOrEndObjectHasChanged() {
		// if (true) return true;
		if (startShape == null || startShape != null && startShape.getShapeType() != getStartObject().getShape().getShapeType()) {
			// logger.info("Layout has changed because start shape change");
			return true;
		}
		if (startShapeDimension == null || startShapeDimension != null && !startShapeDimension.equals(getStartObject().getSize())) {
			// logger.info("Layout has changed because start shape dimension change");
			return true;
		}
		if (startShapeLocation == null || startShapeLocation != null && !startShapeLocation.equals(getStartObject().getLocationInDrawing())) {
			// logger.info("Layout has changed because start shape location change");
			return true;
		}
		if (endShape == null || endShape != null && endShape.getShapeType() != getEndObject().getShape().getShapeType()) {
			// logger.info("Layout has changed because end shape change");
			return true;
		}
		if (endShapeDimension == null || endShapeDimension != null && !endShapeDimension.equals(getEndObject().getSize())) {
			// logger.info("Layout has changed because end shape dimension change");
			return true;
		}
		if (endShapeLocation == null || endShapeLocation != null && !endShapeLocation.equals(getEndObject().getLocationInDrawing())) {
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

	private Shape startShape;
	private FGEDimension startShapeDimension;
	private FGEPoint startShapeLocation;
	private Shape endShape;
	private FGEDimension endShapeDimension;
	private FGEPoint endShapeLocation;
	private FGERectangle knownConnectorUsedBounds;

	@Override
	public void connectorWillBeModified() {

	}

	@Override
	public void connectorHasBeenModified() {

	}

	/*
	 * private FGEArea trivialCoveringArea; private FGEArea firstDegreeCoveringArea; private FGEArea secondDegreeCoveringArea; private
	 * ForegroundStyle fs0 = ForegroundStyle.makeStyle(Color.BLUE); private BackgroundStyle bs0 =
	 * BackgroundStyle.makeTexturedBackground(TextureType.TEXTURE8,Color.RED,Color.YELLOW); private ForegroundStyle fs1 =
	 * ForegroundStyle.makeStyle(Color.RED); private BackgroundStyle bs1 =
	 * BackgroundStyle.makeTexturedBackground(TextureType.TEXTURE1,Color.RED,Color.WHITE); private ForegroundStyle fs2 =
	 * ForegroundStyle.makeStyle(Color.RED); private BackgroundStyle bs2 =
	 * BackgroundStyle.makeTexturedBackground(TextureType.TEXTURE4,Color.GREEN,Color.LIGHT_GRAY); private ForegroundStyle fs3 =
	 * ForegroundStyle.makeStyle(Color.MAGENTA); private BackgroundStyle bs3 =
	 * BackgroundStyle.makeTexturedBackground(TextureType.TEXTURE8,Color.BLACK,Color.WHITE);
	 */

	/*
	 * private void computeCoveringAreas() { trivialCoveringArea = computeCoveringArea(0); firstDegreeCoveringArea = computeCoveringArea(1);
	 * secondDegreeCoveringArea = computeCoveringArea(2); }
	 * 
	 * private void drawCoveringAreas(FGEConnectorGraphics g) { if (trivialCoveringArea == null || firstDegreeCoveringArea == null ||
	 * secondDegreeCoveringArea == null) { computeCoveringAreas(); } g.setDefaultForeground(fs0); g.setDefaultBackground(bs0);
	 * trivialCoveringArea.paint(g); g.setDefaultForeground(fs1); g.setDefaultBackground(bs1); firstDegreeCoveringArea.paint(g);
	 * g.setDefaultForeground(fs2); g.setDefaultBackground(bs2); secondDegreeCoveringArea.paint(g);
	 * 
	 * 
	 * AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getStartObject(), getGraphicalRepresentation());
	 * FGEArea testFromWest = getStartObject().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at1);
	 * System.out.println("Was:"+getStartObject().getShape().getAllowedHorizontalConnectorLocationFromWest());
	 * System.out.println("Is:"+testFromWest); g.setDefaultForeground(fs3); g.setDefaultBackground(bs3); testFromWest.paint(g);
	 * 
	 * setPaintAttributes(g); }
	 */

	@Override
	public abstract FGEPoint getMiddleSymbolLocation();

	/**
	 * Perform an area computation related to the both extremity objects
	 * 
	 * If order equals 0, return intersection between shapes representing the two object If order equals 1, return intersection of
	 * 
	 * @param order
	 * @return
	 */
	@Override
	public FGEArea computeCoveringArea(int order) {
		AffineTransform at1 = FGEUtils.convertNormalizedCoordinatesAT(getStartObject(), getGraphicalRepresentation());

		AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(getEndObject(), getGraphicalRepresentation());

		if (order == 0) {
			FGEArea startObjectShape = getStartObject().getShape().getShape().transform(at1);
			FGEArea endObjectShape = getEndObject().getShape().getShape().transform(at2);
			FGEArea returned = startObjectShape.intersect(endObjectShape);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("computeCoveringArea(" + order + ") = " + returned);
			}
			return returned;
		}

		FGEArea start_east = getStartObject().getShape().getAllowedHorizontalConnectorLocationFromEast().transform(at1);
		FGEArea start_west = getStartObject().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at1);
		FGEArea start_north = getStartObject().getShape().getAllowedVerticalConnectorLocationFromNorth().transform(at1);
		FGEArea start_south = getStartObject().getShape().getAllowedVerticalConnectorLocationFromSouth().transform(at1);

		FGEArea end_east = getEndObject().getShape().getAllowedHorizontalConnectorLocationFromEast().transform(at2);
		FGEArea end_west = getEndObject().getShape().getAllowedHorizontalConnectorLocationFromWest().transform(at2);
		FGEArea end_north = getEndObject().getShape().getAllowedVerticalConnectorLocationFromNorth().transform(at2);
		FGEArea end_south = getEndObject().getShape().getAllowedVerticalConnectorLocationFromSouth().transform(at2);

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

	@Override
	public boolean getDebug() {
		return debug;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
		refreshConnector();
	}

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	@Override
	public abstract FGERectangle getConnectorUsedBounds();

	@Override
	public abstract Connector clone();

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public abstract FGEPoint getStartLocation();

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public abstract FGEPoint getEndLocation();

}
