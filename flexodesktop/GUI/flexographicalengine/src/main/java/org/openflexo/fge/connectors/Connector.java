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
package org.openflexo.fge.connectors;

import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class Connector extends KVCObject implements XMLSerializable, Cloneable {

	private static final Logger logger = Logger.getLogger(Connector.class.getPackage().getName());

	private transient ConnectorGraphicalRepresentation graphicalRepresentation;

	private boolean debug = false;

	protected FGERectangle NORMALIZED_BOUNDS = new FGERectangle(0, 0, 1, 1, Filling.FILLED);

	public Connector(ConnectorGraphicalRepresentation aGraphicalRepresentation) {
		super();
		graphicalRepresentation = aGraphicalRepresentation;
		// labelCP1 = new LabelControlPoint(aGraphicalRepresentation,new FGEPoint());
		// labelCP2 = new LabelControlPoint(aGraphicalRepresentation,new FGEPoint());
	}

	public static Connector makeConnector(ConnectorType type, ConnectorGraphicalRepresentation aGraphicalRepresentation) {

		if (type == ConnectorType.LINE) {
			return new LineConnector(aGraphicalRepresentation);
		}
		/*
		 * else if (type == ConnectorType.RECT_LINE) { return new RectLineConnector(aGraphicalRepresentation); }
		 */
		else if (type == ConnectorType.RECT_POLYLIN) {
			return new RectPolylinConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.CURVE) {
			return new CurveConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.CURVED_POLYLIN) {
			return new CurvedPolylinConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.CUSTOM) {
			return null;
		}

		logger.warning("Unexpected type: " + type);
		return null;

	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ConnectorGraphicalRepresentation aGraphicalRepresentation) {
		graphicalRepresentation = aGraphicalRepresentation;
	}

	public abstract double getStartAngle();

	public abstract double getEndAngle();

	public Object getDrawable() {
		return graphicalRepresentation.getDrawable();
	}

	public ShapeGraphicalRepresentation getStartObject() {
		if (graphicalRepresentation == null) {
			return null;
		}
		return graphicalRepresentation.getStartObject();
	}

	public ShapeGraphicalRepresentation getEndObject() {
		if (graphicalRepresentation == null) {
			return null;
		}
		return graphicalRepresentation.getEndObject();
	}

	/**
	 * Return value indicating distance from aPoint to connector, asserting aPoint is related to local normalized coordinates system
	 * 
	 * @param aPoint
	 * @return
	 */
	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	public void setPaintAttributes(FGEConnectorGraphics g) {

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

	public final void paintConnector(FGEConnectorGraphics g) {
		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		setPaintAttributes(g);
		drawConnector(g);
	}

	public abstract void drawConnector(FGEConnectorGraphics g);

	public abstract List<? extends ControlArea> getControlAreas();

	public static enum ConnectorType {
		LINE,
		// RECT_LINE,
		RECT_POLYLIN,
		CURVE,
		CURVED_POLYLIN,
		CUSTOM;

		public ImageIcon getIcon() {
			if (this == RECT_POLYLIN) {
				return org.openflexo.fge.FGEIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			} else if (this == CURVE) {
				return org.openflexo.fge.FGEIconLibrary.CURVE_CONNECTOR_ICON;
			} else if (this == LINE) {
				return org.openflexo.fge.FGEIconLibrary.LINE_CONNECTOR_ICON;
			}
			return null;
		}

	}

	public abstract ConnectorType getConnectorType();

	public void refreshConnector() {
		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { computeCoveringAreas(); }
		 */
		storeLayoutOfStartOrEndObject();
	}

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
		if ((startShape == null) || ((startShape != null) && (startShape.getShapeType() != getStartObject().getShape().getShapeType()))) {
			// logger.info("Layout has changed because start shape change");
			return true;
		}
		if ((startShapeDimension == null) || ((startShapeDimension != null) && !startShapeDimension.equals(getStartObject().getSize()))) {
			// logger.info("Layout has changed because start shape dimension change");
			return true;
		}
		if ((startShapeLocation == null)
				|| ((startShapeLocation != null) && !startShapeLocation.equals(getStartObject().getLocationInDrawing()))) {
			// logger.info("Layout has changed because start shape location change");
			return true;
		}
		if ((endShape == null) || ((endShape != null) && (endShape.getShapeType() != getEndObject().getShape().getShapeType()))) {
			// logger.info("Layout has changed because end shape change");
			return true;
		}
		if ((endShapeDimension == null) || ((endShapeDimension != null) && !endShapeDimension.equals(getEndObject().getSize()))) {
			// logger.info("Layout has changed because end shape dimension change");
			return true;
		}
		if ((endShapeLocation == null) || ((endShapeLocation != null) && !endShapeLocation.equals(getEndObject().getLocationInDrawing()))) {
			// logger.info("Layout has changed because end shape location change");
			return true;
		}
		if ((knownConnectorUsedBounds == null)
				|| ((knownConnectorUsedBounds != null) && !knownConnectorUsedBounds.equals(getConnectorUsedBounds()))) {
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

	public void connectorWillBeModified() {

	}

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

	public abstract FGEPoint getMiddleSymbolLocation();

	/**
	 * Perform an area computation related to the both extremity objects
	 * 
	 * If order equals 0, return intersection between shapes representing the two object If order equals 1, return intersection of
	 * 
	 * @param order
	 * @return
	 */
	public FGEArea computeCoveringArea(int order) {
		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getStartObject(), getGraphicalRepresentation());

		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getEndObject(), getGraphicalRepresentation());

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

	public boolean getDebug() {
		return debug;
	}

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
	public abstract FGERectangle getConnectorUsedBounds();

	@Override
	public Connector clone() {
		try {
			Connector returned = (Connector) super.clone();
			returned.graphicalRepresentation = null;
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

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

}
