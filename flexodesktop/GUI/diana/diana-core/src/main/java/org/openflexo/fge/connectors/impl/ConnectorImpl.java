package org.openflexo.fge.connectors.impl;

import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.PersistenceMode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
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
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This is an instance of a {@link ConnectorSpecification}. As it, it is attached to a {@link ConnectorNode}. A {@link ConnectorImpl}
 * observes its {@link ConnectorSpecification}
 * 
 * @author sylvain
 * 
 */
public abstract class ConnectorImpl<CS extends ConnectorSpecification> implements Connector<CS> {

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

	/**
	 * Store temporary properties that may not be serialized
	 */
	private Map<GRParameter, Object> propertyValues = new HashMap<GRParameter, Object>();

	public ConnectorImpl(ConnectorNode<?> connectorNode) {
		super();
		this.connectorNode = connectorNode;
		propertyValues = new HashMap<GRParameter, Object>();

	}

	public void delete() {

		System.out.println("Le connecteur se fait deleter");

		if (getConnectorSpecification() != null) {
			getConnectorSpecification().getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		connectorNode = null;
		startShape = null;
		startShape = null;
		startShapeLocation = null;
		endShape = null;
		endShape = null;
		endShape = null;
		knownConnectorUsedBounds = null;
		isDeleted = true;
	}

	private boolean isDeleted = false;

	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CS getConnectorSpecification() {
		if (connectorNode != null) {
			return (CS) connectorNode.getConnectorSpecification();
		}
		return null;
	}

	@Override
	public ConnectorNode<?> getConnectorNode() {
		return connectorNode;
	}

	@Override
	public ShapeNode<?> getStartNode() {
		if (connectorNode == null) {
			return null;
		}
		return connectorNode.getStartNode();
	}

	@Override
	public ShapeNode<?> getEndNode() {
		if (connectorNode == null) {
			return null;
		}
		return connectorNode.getEndNode();
	}

	@Override
	public ConnectorType getConnectorType() {
		return getConnectorSpecification().getConnectorType();
	}

	// *******************************************************************************
	// * Abstract Methods *
	// *******************************************************************************

	@Override
	public abstract double getStartAngle();

	@Override
	public abstract double getEndAngle();

	@Override
	public abstract double distanceToConnector(FGEPoint aPoint, double scale);

	public abstract void drawConnector(FGEConnectorGraphics g);

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	@Override
	public abstract List<? extends ControlArea<?>> getControlAreas();

	@Override
	public abstract FGEPoint getMiddleSymbolLocation();

	/**
	 * Return bounds of actually required area to fully display current connector (which might require to be paint outside normalized
	 * bounds)
	 * 
	 * @return
	 */
	@Override
	public abstract FGERectangle getConnectorUsedBounds();

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
		if (connectorNode.getIsSelected()) {
			if (connectorNode.getGraphicalRepresentation().getHasSelectedForeground()) {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getSelectedForeground());
			} else if (connectorNode.getGraphicalRepresentation().getHasFocusedForeground()) {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getFocusedForeground());
			} else {
				g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
			}
		} else if (connectorNode.getIsFocused() && connectorNode.getGraphicalRepresentation().getHasFocusedForeground()) {
			g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getFocusedForeground());
		} else {
			g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
		}

		g.setDefaultTextStyle(connectorNode.getGraphicalRepresentation().getTextStyle());
	}

	@Override
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
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
	}

/**
	 * Returns the property value for supplied parameter<br>
	 * If many Connectors share same ConnectorSpecification (as indicated by {@link Drawing#getPersistenceMode()), do not store value in ConnectorSpecification, but store it in the Connector itself.<br>
	 * This implies that this value is not persistent (not serializable)
	 * 
	 * @param parameter
	 * @return
	 */
	@Override
	public <T> T getPropertyValue(GRParameter<T> parameter) {

		// Now we have to think of this:
		// New architecture of FGE now authorizes a ConnectorSpecification to be shared by many Connectors
		// If UniqueGraphicalRepresentations is active, use ConnectorSpecification to store graphical properties

		if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			if (getConnectorSpecification() == null) {
				return null;
			}
			return (T) getConnectorSpecification().objectForKey(parameter.getName());
		}

		// If SharedGraphicalRepresentations is active, GR should not be used to store graphical properties

		else if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {

			T returned = (T) propertyValues.get(parameter);
			if (returned == null) {
				// Init default value with GR
				returned = (T) getConnectorSpecification().objectForKey(parameter.getName());
				if (returned != null) {
					propertyValues.put(parameter, returned);
				}
			}

			return returned;
		}

		else {
			logger.warning("Not implemented: " + getConnectorNode().getDrawing().getPersistenceMode());
			return null;
		}
	}

/**
	 * Sets the property value for supplied parameter<br>
	 * If many Connectors share same ConnectorSpecification (as indicated by {@link Drawing#getPersistenceMode()), do not store value in ConnectorSpecification, but store it in the Connector itself.<br>
	 * This implies that this value is not persistent (not serializable)
	 * 
	 * @param parameter
	 * @return
	 */
	@Override
	public <T> void setPropertyValue(GRParameter<T> parameter, T value) {

		// Now we have to think of this:
		// New architecture of FGE now authorizes a ConnectorSpecification to be shared by many Connectors
		// If UniqueGraphicalRepresentations is active, use ConnectorSpecification to store graphical properties

		if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.UniqueGraphicalRepresentations) {
			boolean wasObserving = ignoreNotificationsFrom(getConnectorSpecification());
			// This line is really important, since it avoid GR to be notified of this set
			// Otherwise GR forward notification to DTN whiich will delete current connector
			getConnectorSpecification().getPropertyChangeSupport().removePropertyChangeListener(connectorNode.getGraphicalRepresentation());
			T oldValue = (T) getConnectorSpecification().objectForKey(parameter.getName());
			getConnectorSpecification().setObjectForKey(value, parameter.getName());
			if (wasObserving) {
				observeAgain(getConnectorSpecification());
			}
			// At the end, let the GR observes again the CS
			getConnectorSpecification().getPropertyChangeSupport().addPropertyChangeListener(connectorNode.getGraphicalRepresentation());
			// Since CS is prevented to fire notifications, do it myself
			// getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue, value);
		}

		// If SharedGraphicalRepresentations is active, GR should not be used to store graphical properties

		else if (getConnectorNode().getDrawing().getPersistenceMode() == PersistenceMode.SharedGraphicalRepresentations) {
			propertyValues.put(parameter, value);
		}

		else {
			logger.warning("Not implemented: " + getConnectorNode().getDrawing().getPersistenceMode());
		}

	}

	protected Set<HasPropertyChangeSupport> temporaryIgnoredObservables = new HashSet<HasPropertyChangeSupport>();

	/**
	 * 
	 * @param observable
	 * @return a flag indicating if observable was added to the list of ignored observables
	 */
	protected boolean ignoreNotificationsFrom(HasPropertyChangeSupport observable) {
		if (temporaryIgnoredObservables.contains(observable)) {
			return false;
		}
		temporaryIgnoredObservables.add(observable);
		return true;
	}

	protected void observeAgain(HasPropertyChangeSupport observable) {
		temporaryIgnoredObservables.remove(observable);
	}

	public StartSymbolType getStartSymbol() {
		return getPropertyValue(ConnectorSpecification.START_SYMBOL);
	}

	public void setStartSymbol(StartSymbolType startSymbol) {
		setPropertyValue(ConnectorSpecification.START_SYMBOL, startSymbol);
	}

	public EndSymbolType getEndSymbol() {
		return getPropertyValue(ConnectorSpecification.END_SYMBOL);
	}

	public void setEndSymbol(EndSymbolType endSymbol) {
		setPropertyValue(ConnectorSpecification.END_SYMBOL, endSymbol);
	}

	public MiddleSymbolType getMiddleSymbol() {
		return getPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL);
	}

	public void setMiddleSymbol(MiddleSymbolType middleSymbol) {
		setPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL, middleSymbol);
	}

	public double getStartSymbolSize() {
		return getPropertyValue(ConnectorSpecification.START_SYMBOL_SIZE);
	}

	public void setStartSymbolSize(double startSymbolSize) {
		setPropertyValue(ConnectorSpecification.START_SYMBOL_SIZE, startSymbolSize);
	}

	public double getEndSymbolSize() {
		return getPropertyValue(ConnectorSpecification.END_SYMBOL_SIZE);
	}

	public void setEndSymbolSize(double endSymbolSize) {
		setPropertyValue(ConnectorSpecification.END_SYMBOL_SIZE, endSymbolSize);
	}

	public double getMiddleSymbolSize() {
		return getPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL_SIZE);
	}

	public void setMiddleSymbolSize(double middleSymbolSize) {
		setPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL_SIZE, middleSymbolSize);
	}

	public double getRelativeMiddleSymbolLocation() {
		return getPropertyValue(ConnectorSpecification.RELATIVE_MIDDLE_SYMBOL_LOCATION);
	}

	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		setPropertyValue(ConnectorSpecification.RELATIVE_MIDDLE_SYMBOL_LOCATION, relativeMiddleSymbolLocation);
	}

}
