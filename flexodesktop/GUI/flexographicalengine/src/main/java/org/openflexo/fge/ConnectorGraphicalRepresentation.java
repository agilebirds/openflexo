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
package org.openflexo.fge;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.notifications.ConnectorModified;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.toolbox.ToolBox;

public class ConnectorGraphicalRepresentation<O> extends GraphicalRepresentation<O> implements Observer {

	private static final Logger logger = Logger.getLogger(ConnectorGraphicalRepresentation.class.getPackage().getName());

	// *******************************************************************************
	// * Parameters *
	// *******************************************************************************

	public static enum Parameters implements GRParameter {
		connector,
		foreground,
		selectedForeground,
		focusedForeground,
		hasSelectedForeground,
		hasFocusedForeground,
		startSymbol,
		endSymbol,
		middleSymbol,
		startSymbolSize,
		endSymbolSize,
		middleSymbolSize,
		relativeMiddleSymbolLocation,
		applyForegroundToSymbols,
		debugCoveringArea
	}

	private Connector connector = null;

	private ForegroundStyle foreground;

	private ForegroundStyle selectedForeground = null;
	private ForegroundStyle focusedForeground = null;

	private boolean hasSelectedForeground = false;
	private boolean hasFocusedForeground = false;

	private StartSymbolType startSymbol = StartSymbolType.NONE;
	private EndSymbolType endSymbol = EndSymbolType.NONE;
	private MiddleSymbolType middleSymbol = MiddleSymbolType.NONE;

	private double startSymbolSize = 10.0;
	private double endSymbolSize = 10.0;
	private double middleSymbolSize = 10.0;

	private double relativeMiddleSymbolLocation = 0.5; // default is in the middle !

	private boolean applyForegroundToSymbols = true;

	private boolean debugCoveringArea = false;

	// *******************************************************************************
	// * Inner classes *
	// *******************************************************************************

	// *******************************************************************************
	// * Fields *
	// *******************************************************************************

	private ShapeGraphicalRepresentation<?> startObject;
	private ShapeGraphicalRepresentation<?> endObject;
	protected FGEConnectorGraphics graphics;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Never use this constructor (used during deserialization only)
	public ConnectorGraphicalRepresentation() {
		this(null, null, null, null, null);
	}

	public ConnectorGraphicalRepresentation(ConnectorType aConnectorType, ShapeGraphicalRepresentation<?> aStartObject,
			ShapeGraphicalRepresentation<?> anEndObject, O aDrawable, Drawing<?> aDrawing) {
		super(aDrawable, aDrawing);

		layer = FGEConstants.DEFAULT_CONNECTOR_LAYER;

		setStartObject(aStartObject);
		setEndObject(anEndObject);
		setConnectorType(aConnectorType);
		graphics = new FGEConnectorGraphics(this);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		// foreground.setGraphicalRepresentation(this);
		foreground.addObserver(this);

		addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	// ***************************************************************************
	// * Deletion *
	// ***************************************************************************

	@Override
	public void delete() {
		if (foreground != null) {
			foreground.deleteObserver(this);
		}
		super.delete();
		disableStartObjectObserving();
		disableEndObjectObserving();
	}

	@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = super.getAllParameters();
		Parameters[] allParams = Parameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}

	@Override
	public final void setsWith(GraphicalRepresentation<?> gr) {
		super.setsWith(gr);
		if (gr instanceof ConnectorGraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				if (p != Parameters.connector) {
					_setParameterValueWith(p, gr);
				}
			}
			Connector connectorToCopy = ((ConnectorGraphicalRepresentation<?>) gr).getConnector();
			Connector clone = connectorToCopy.clone();
			setConnector(clone);
		}
	}

	@Override
	public final void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters) {
		super.setsWith(gr, exceptedParameters);
		if (gr instanceof ConnectorGraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				boolean excepted = false;
				for (GRParameter ep : exceptedParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (p != Parameters.connector && !excepted) {
					_setParameterValueWith(p, gr);
				}
			}
			Connector connectorToCopy = ((ConnectorGraphicalRepresentation<?>) gr).getConnector();
			Connector clone = connectorToCopy.clone();
			setConnector(clone);
		}
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector aConnector) {
		if (aConnector != null) {
			aConnector.setGraphicalRepresentation(this);
		}
		FGENotification notification = requireChange(Parameters.connector, aConnector);
		if (notification != null) {
			this.connector = aConnector;
			hasChanged(notification);
		}
	}

	public ForegroundStyle getForeground() {
		return foreground;
	}

	public void setForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(Parameters.foreground, aForeground);
		if (notification != null) {
			if (foreground != null) {
				foreground.deleteObserver(this);
			}
			foreground = aForeground;
			if (aForeground != null) {
				aForeground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	public ForegroundStyle getSelectedForeground() {
		if (selectedForeground == null) {
			selectedForeground = foreground.clone();
		}
		return selectedForeground;
	}

	public void setSelectedForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(Parameters.selectedForeground, aForeground, false);
		if (notification != null) {
			if (selectedForeground != null) {
				selectedForeground.deleteObserver(this);
			}
			selectedForeground = aForeground;
			if (aForeground != null) {
				aForeground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	public boolean getHasSelectedForeground() {
		return hasSelectedForeground;
	}

	public void setHasSelectedForeground(boolean aFlag) {
		hasSelectedForeground = aFlag;
	}

	public ForegroundStyle getFocusedForeground() {
		if (focusedForeground == null) {
			focusedForeground = foreground.clone();
		}
		return focusedForeground;
	}

	public void setFocusedForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(Parameters.focusedForeground, aForeground, false);
		if (notification != null) {
			if (focusedForeground != null) {
				focusedForeground.deleteObserver(this);
			}
			focusedForeground = aForeground;
			if (aForeground != null) {
				aForeground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	public boolean getHasFocusedForeground() {
		return hasFocusedForeground;
	}

	public void setHasFocusedForeground(boolean aFlag) {
		hasFocusedForeground = aFlag;
	}

	@Override
	public final boolean shouldBeDisplayed() {
		return super.shouldBeDisplayed();
	}

	public void notifyConnectorChanged() {
		if (!isRegistered()) {
			return;
		}
		checkViewBounds();
		setChanged();
		notifyObservers(new ConnectorModified());
	}

	public ConnectorType getConnectorType() {
		if (connector != null) {
			return connector.getConnectorType();
		}
		return null;
	}

	public void setConnectorType(ConnectorType connectorType) {
		if (getConnectorType() != connectorType) {
			setConnector(Connector.makeConnector(connectorType, this));
		}
	}

	@Override
	public final void setText(String text) {
		super.setText(text);
	}

	public ShapeGraphicalRepresentation<?> getStartObject() {
		return startObject;
	}

	public void setStartObject(ShapeGraphicalRepresentation<?> aStartObject) {
		startObject = aStartObject;
		if (!enabledStartObjectObserving) {
			enableStartObjectObserving(startObject);
		}
	}

	private boolean enabledStartObjectObserving = false;
	private Vector<Observable> observedStartObjects = new Vector<Observable>();

	private boolean enabledEndObjectObserving = false;
	private Vector<Observable> observedEndObjects = new Vector<Observable>();

	protected void enableStartObjectObserving(ShapeGraphicalRepresentation<?> aStartObject) {

		if (aStartObject == null || !aStartObject.isValidated()) {
			return;
		}

		if (enabledStartObjectObserving) {
			disableStartObjectObserving();
		}

		if (aStartObject != null /*&& !enabledStartObjectObserving*/) {
			aStartObject.addObserver(this);
			observedStartObjects.add(aStartObject);
			if (!isDeserializing()) {
				for (Object o : aStartObject.getAncestors(true)) {
					if (getGraphicalRepresentation(o) != null) {
						getGraphicalRepresentation(o).addObserver(this);
						observedStartObjects.add(getGraphicalRepresentation(o));
					}
				}
			}
			enabledStartObjectObserving = true;
		}
	}

	protected void disableStartObjectObserving(/*ShapeGraphicalRepresentation<?> aStartObject*/) {
		if (/*aStartObject != null &&*/enabledStartObjectObserving) {
			/*aStartObject.deleteObserver(this);
			if (!isDeserializing()) {
				for (Object o : aStartObject.getAncestors())
					if (getGraphicalRepresentation(o) != null) getGraphicalRepresentation(o).deleteObserver(this);
			}*/
			for (Observable o : observedStartObjects) {
				o.deleteObserver(this);
			}

			enabledStartObjectObserving = false;
		}
	}

	public ShapeGraphicalRepresentation<?> getEndObject() {
		return endObject;
	}

	public void setEndObject(ShapeGraphicalRepresentation<?> anEndObject) {
		endObject = anEndObject;
		if (!enabledEndObjectObserving) {
			enableEndObjectObserving(endObject);
		}
	}

	protected void enableEndObjectObserving(ShapeGraphicalRepresentation<?> anEndObject) {

		if (anEndObject == null || !anEndObject.isValidated()) {
			return;
		}

		if (enabledEndObjectObserving) {
			disableEndObjectObserving();
		}

		if (anEndObject != null /*&& !enabledEndObjectObserving*/) {
			anEndObject.addObserver(this);
			observedEndObjects.add(anEndObject);
			if (!isDeserializing()) {
				for (Object o : anEndObject.getAncestors(true)) {
					if (getGraphicalRepresentation(o) != null) {
						getGraphicalRepresentation(o).addObserver(this);
						observedEndObjects.add(getGraphicalRepresentation(o));
					}
				}
			}
			enabledEndObjectObserving = true;
		}
	}

	protected void disableEndObjectObserving(/*ShapeGraphicalRepresentation<?> anEndObject*/) {
		if (/*anEndObject != null &&*/enabledEndObjectObserving) {
			/*anEndObject.deleteObserver(this);
			if (!isDeserializing()) {
				for (Object o : anEndObject.getAncestors())
					if (getGraphicalRepresentation(o) != null) getGraphicalRepresentation(o).deleteObserver(this);
			}*/
			for (Observable o : observedEndObjects) {
				o.deleteObserver(this);
			}
			enabledEndObjectObserving = false;
		}
	}

	public void observeRelevantObjects() {
		enableStartObjectObserving(getStartObject());
		enableEndObjectObserving(getEndObject());
	}

	@Override
	public int getViewX(double scale) {
		return getViewBounds(scale).x;
	}

	@Override
	public int getViewY(double scale) {
		return getViewBounds(scale).y;
	}

	@Override
	public int getViewWidth(double scale) {
		return getViewBounds(scale).width;
	}

	@Override
	public int getViewHeight(double scale) {
		return getViewBounds(scale).height;
	}

	private double minX = 0.0;
	private double minY = 0.0;
	private double maxX = 1.0;
	private double maxY = 1.0;

	private void checkViewBounds() {
		FGERectangle r = getConnector().getConnectorUsedBounds();
		if (FGEUtils.checkDoubleIsAValue(r.getMinX()) && FGEUtils.checkDoubleIsAValue(r.getMinY())
				&& FGEUtils.checkDoubleIsAValue(r.getMaxX()) && FGEUtils.checkDoubleIsAValue(r.getMaxY())) {
			minX = Math.min(r.getMinX(), 0.0);
			minY = Math.min(r.getMinY(), 0.0);
			maxX = Math.max(r.getMaxX(), 1.0);
			maxY = Math.max(r.getMaxY(), 1.0);
		}
	}

	@Override
	public Rectangle getViewBounds(double scale) {
		// return getNormalizedBounds(scale);

		Rectangle bounds = getNormalizedBounds(scale);
		/*System.out.println("Bounds="+bounds);
		System.out.println("minX="+minX);
		System.out.println("minY="+minY);
		System.out.println("maxX="+maxX);
		System.out.println("maxY="+maxY);*/
		Rectangle returned = new Rectangle();
		returned.x = (int) (bounds.x + (minX < 0 ? minX * bounds.width : 0));
		returned.y = (int) (bounds.y + (minY < 0 ? minY * bounds.height : 0));
		returned.width = (int) ((maxX - minX) * bounds.width);
		returned.height = (int) ((maxY - minY) * bounds.height);
		return returned;
	}

	public int getExtendedX(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);
		return (int) (bounds.x + (minX < 0 ? minX * bounds.width : 0));
	}

	public int getExtendedY(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);
		return (int) (bounds.y + (minY < 0 ? minY * bounds.height : 0));
	}

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	public Rectangle getNormalizedBounds(double scale) {
		if (getStartObject() == null || getStartObject().isDeleted() || getEndObject() == null || getEndObject().isDeleted()) {
			logger.warning("Could not obtain connector bounds: start or end object is null or deleted");
			logger.warning("Object: " + this + " startObject=" + getStartObject() + " endObject=" + getEndObject());
			// Here, we return a (1,1)-size to avoid obtaining Infinity AffinTransform !!!
			return new Rectangle(0, 0, 1, 1);
		}

		if (getContainerGraphicalRepresentation() == null) {
			logger.warning("getNormalizedBounds() called for GR " + this + " with containerGR=null, validated=" + isValidated());
		}

		Rectangle startBounds = getStartObject().getViewBounds(getContainerGraphicalRepresentation(), scale);
		Rectangle endsBounds = getEndObject().getViewBounds(getContainerGraphicalRepresentation(), scale);

		Rectangle bounds = new Rectangle();
		Rectangle2D.union(startBounds, endsBounds, bounds);

		return bounds;
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		FGERectangle drawingViewBounds = new FGERectangle(drawingViewSelection.getX(), drawingViewSelection.getY(),
				drawingViewSelection.getWidth(), drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		for (ControlArea<?> ca : getConnector().getControlAreas()) {
			if (ca instanceof ControlPoint) {
				ControlPoint cp = (ControlPoint) ca;
				Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(),
						getDrawingGraphicalRepresentation(), scale);
				FGEPoint preciseCPInContainerView = new FGEPoint(cpInContainerView.x, cpInContainerView.y);
				if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
					// System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
					isFullyContained = false;
				}
			}
		}
		return isFullyContained;
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);

		// return AffineTransform.getScaleInstance(bounds.width, bounds.height);

		AffineTransform returned = AffineTransform.getTranslateInstance(minX < 0 ? -minX * bounds.width : 0, minY < 0 ? -minY
				* bounds.height : 0);

		returned.concatenate(AffineTransform.getScaleInstance(bounds.width, bounds.height));

		return returned;

	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);

		// return AffineTransform.getScaleInstance(1.0/bounds.width, 1.0/bounds.height);

		AffineTransform returned = AffineTransform.getTranslateInstance(minX < 0 ? minX * bounds.width : 0, minY < 0 ? minY * bounds.height
				: 0);

		returned.preConcatenate(AffineTransform.getScaleInstance(1.0 / bounds.width, 1.0 / bounds.height));

		return returned;

	}

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		return connector.distanceToConnector(aPoint, scale);
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		if (!isRegistered()) {
			setRegistered(true);
		}

		super.paint(g, controller);

		if (getStartObject() == null || getStartObject().isDeleted()) {
			logger.warning("Could not paint connector: start object is null or deleted");
			return;
		}

		if (getEndObject() == null || getEndObject().isDeleted()) {
			logger.warning("Could not paint connector: end object is null or deleted");
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		graphics.createGraphics(g2, controller);

		if (FGEConstants.DEBUG) {
			g2.setColor(Color.PINK);
			g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
		}

		if (connector != null) {

			if (!isValidated()) {
				logger.warning("paint connector requested for not validated connector graphical representation: " + this);
				return;
			}
			if (getStartObject() == null || getStartObject().isDeleted() || !getStartObject().isValidated()) {
				logger.warning("paint connector requested for invalid start object (either null, deleted or not validated) : " + this
						+ " start=" + getStartObject());
				return;
			}
			if (getEndObject() == null || getEndObject().isDeleted() || !getEndObject().isValidated()) {
				logger.warning("paint connector requested for invalid start object (either null, deleted or not validated) : " + this
						+ " end=" + getEndObject());
				return;
			}
			connector.paintConnector(graphics);
		}

		graphics.releaseGraphics();
	}

	@Override
	public Point getLabelLocation(double scale) {
		Point connectorCenter = convertNormalizedPointToViewCoordinates(getConnector().getMiddleSymbolLocation(), scale);
		return new Point((int) (connectorCenter.x + getAbsoluteTextX() * scale + getViewX(scale)), (int) (connectorCenter.y
				+ getAbsoluteTextY() * scale + getViewY(scale)));
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		Point connectorCenter = convertNormalizedPointToViewCoordinates(getConnector().getMiddleSymbolLocation(), scale);
		setAbsoluteTextX(((double) point.x - connectorCenter.x - getViewX(scale)) / scale);
		setAbsoluteTextY(((double) point.y - connectorCenter.y - getViewY(scale)) / scale);
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText();
	}

	@Override
	public String getInspectorName() {
		return "ConnectorGraphicalRepresentation.inspector";
	}

	@Override
	public void update(Observable observable, Object notification) {
		// System.out.println("Connector received "+notification+" from "+observable);

		super.update(observable, notification);

		if (observable instanceof ForegroundStyle) {
			notifyAttributeChange(Parameters.foreground);
		}

		if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize) {
			connector.connectorWillBeModified();
			// Propagate notification to views
			setChanged();
			notifyObservers(notification);
		}
		if (notification instanceof ObjectHasMoved || notification instanceof ObjectHasResized) {
			connector.connectorHasBeenModified();
			// Propagate notification to views
			setChanged();
			notifyObservers(notification);
		}
		if (notification instanceof ObjectMove || notification instanceof ObjectResized || notification instanceof ShapeChanged) {
			// if (observable == startObject || observable == endObject) {
			// !!! or any of ancestors
			refreshConnector();
			// }
		}
	}

	protected boolean isConnectorConsistent() {
		// if (true) return true;
		return getStartObject() != null && getEndObject() != null && !getStartObject().isDeleted() && !getEndObject().isDeleted()
				&& GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(getStartObject(), getEndObject());
	}

	public void refreshConnector() {
		refreshConnector(false);
	}

	protected void refreshConnector(boolean forceRefresh) {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		try {
			if (forceRefresh || connector.needsRefresh()) {
				connector.refreshConnector(forceRefresh);
				checkViewBounds();
				setChanged();
				notifyObservers(new ConnectorModified());
			}
		} catch (Exception e) {
			logger.warning("Unexpected exception: " + e);
			e.printStackTrace();
		}
	}

	@Override
	public void setRegistered(boolean aFlag) {
		if (aFlag != isRegistered()) {
			super.setRegistered(aFlag);
			if (aFlag && !isDeleted()) {
				refreshConnector();
			}
		}
	}

	// Override for a custom view management
	public ConnectorView<O> makeConnectorView(DrawingController<?> controller) {
		return new ConnectorView<O>(this, controller);
	}

	public EndSymbolType getEndSymbol() {
		return endSymbol;
	}

	public void setEndSymbol(EndSymbolType endSymbol) {
		FGENotification notification = requireChange(Parameters.endSymbol, endSymbol);
		if (notification != null) {
			this.endSymbol = endSymbol;
			hasChanged(notification);
		}
	}

	public double getEndSymbolSize() {
		return endSymbolSize;
	}

	public void setEndSymbolSize(double endSymbolSize) {
		FGENotification notification = requireChange(Parameters.endSymbolSize, endSymbolSize);
		if (notification != null) {
			this.endSymbolSize = endSymbolSize;
			hasChanged(notification);
		}
	}

	public MiddleSymbolType getMiddleSymbol() {
		return middleSymbol;
	}

	public void setMiddleSymbol(MiddleSymbolType middleSymbol) {
		FGENotification notification = requireChange(Parameters.middleSymbol, middleSymbol);
		if (notification != null) {
			this.middleSymbol = middleSymbol;
			hasChanged(notification);
		}
	}

	public double getMiddleSymbolSize() {
		return middleSymbolSize;
	}

	public void setMiddleSymbolSize(double middleSymbolSize) {
		FGENotification notification = requireChange(Parameters.middleSymbolSize, middleSymbolSize);
		if (notification != null) {
			this.middleSymbolSize = middleSymbolSize;
			hasChanged(notification);
		}
	}

	public StartSymbolType getStartSymbol() {
		return startSymbol;
	}

	public void setStartSymbol(StartSymbolType startSymbol) {
		FGENotification notification = requireChange(Parameters.startSymbol, startSymbol);
		if (notification != null) {
			this.startSymbol = startSymbol;
			hasChanged(notification);
		}
	}

	public double getStartSymbolSize() {
		return startSymbolSize;
	}

	public void setStartSymbolSize(double startSymbolSize) {
		FGENotification notification = requireChange(Parameters.startSymbolSize, startSymbolSize);
		if (notification != null) {
			this.startSymbolSize = startSymbolSize;
			hasChanged(notification);
		}
	}

	public double getRelativeMiddleSymbolLocation() {
		return relativeMiddleSymbolLocation;
	}

	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		FGENotification notification = requireChange(Parameters.relativeMiddleSymbolLocation, relativeMiddleSymbolLocation);
		if (notification != null) {
			this.relativeMiddleSymbolLocation = relativeMiddleSymbolLocation;
			hasChanged(notification);
		}
	}

	public boolean getApplyForegroundToSymbols() {
		return applyForegroundToSymbols;
	}

	public void setApplyForegroundToSymbols(boolean applyForegroundToSymbols) {
		FGENotification notification = requireChange(Parameters.applyForegroundToSymbols, applyForegroundToSymbols);
		if (notification != null) {
			this.applyForegroundToSymbols = applyForegroundToSymbols;
			hasChanged(notification);
		}
	}

	public boolean getDebugCoveringArea() {
		return debugCoveringArea;
	}

	public void setDebugCoveringArea(boolean debugCoveringArea) {
		FGENotification notification = requireChange(Parameters.debugCoveringArea, debugCoveringArea);
		if (notification != null) {
			this.debugCoveringArea = debugCoveringArea;
			hasChanged(notification);
		}
	}

	// Override when required
	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		refreshConnector();
	}

	public FGEConnectorGraphics getGraphics() {
		return graphics;
	}

	public List<? extends ControlArea> getControlAreas() {
		return getConnector().getControlAreas();
	}

	/**
	 * Override parent's behaviour by enabling start and end object observing
	 */
	@Override
	public void setValidated(boolean validated) {
		super.setValidated(validated);
		if (!enabledStartObjectObserving) {
			enableStartObjectObserving(startObject);
		}
		if (!enabledEndObjectObserving) {
			enableEndObjectObserving(endObject);
		}
	}

	@Override
	public ConnectorGraphicalRepresentation<O> clone() {
		// logger.info("La GR "+this+" se fait cloner la");
		try {
			return (ConnectorGraphicalRepresentation<O>) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

}
