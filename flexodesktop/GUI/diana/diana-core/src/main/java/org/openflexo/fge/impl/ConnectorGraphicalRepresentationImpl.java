package org.openflexo.fge.impl;

import java.awt.Color;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.notifications.ConnectorModified;
import org.openflexo.fge.notifications.ConnectorNeedsToBeRedrawn;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.toolbox.ToolBox;

public class ConnectorGraphicalRepresentationImpl extends GraphicalRepresentationImpl implements ConnectorGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ConnectorGraphicalRepresentation.class.getPackage().getName());

	private ConnectorSpecification connector = null;

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

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ConnectorGraphicalRepresentationImpl() {
		super();
		// graphics = new FGEConnectorGraphicsImpl(this);
	}

	@Deprecated
	private ConnectorGraphicalRepresentationImpl(Object aDrawable, Drawing<?> aDrawing) {
		this();
		// setDrawable(aDrawable);
		setDrawing(aDrawing);
	}

	@Deprecated
	protected ConnectorGraphicalRepresentationImpl(ConnectorType aConnectorType, ShapeGraphicalRepresentation aStartObject,
			ShapeGraphicalRepresentation anEndObject, Object aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);

		layer = FGEConstants.DEFAULT_CONNECTOR_LAYER;

		// setStartObject(aStartObject);
		// setEndObject(anEndObject);
		setConnectorType(aConnectorType);
		// graphics = new FGEConnectorGraphicsImpl(this);

		foreground = getFactory().makeForegroundStyle(Color.BLACK);
		// foreground.setGraphicalRepresentation(this);
		foreground.addObserver(this);

		addToMouseClickControls(getFactory().makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(getFactory().makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			addToMouseClickControls(getFactory().makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
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
		// disableStartObjectObserving();
		// disableEndObjectObserving();
	}

	/*@Override
	public final void setsWith(GraphicalRepresentation gr) {
		super.setsWith(gr);
		if (gr instanceof ConnectorGraphicalRepresentation) {
			for (ConnectorParameters p : ConnectorParameters.values()) {
				if (p != ConnectorParameters.connector) {
					_setParameterValueWith(p, gr);
				}
			}
			ConnectorSpecification connectorToCopy = ((ConnectorGraphicalRepresentation) gr).getConnector();
			ConnectorSpecification clone = connectorToCopy.clone();
			setConnector(clone);
		}
	}

	@Override
	public final void setsWith(GraphicalRepresentation gr, GRParameter... exceptedParameters) {
		super.setsWith(gr, exceptedParameters);
		if (gr instanceof ConnectorGraphicalRepresentationImpl) {
			for (ConnectorParameters p : ConnectorParameters.values()) {
				boolean excepted = false;
				for (GRParameter ep : exceptedParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (p != ConnectorParameters.connector && !excepted) {
					_setParameterValueWith(p, gr);
				}
			}
			ConnectorSpecification connectorToCopy = ((ConnectorGraphicalRepresentationImpl) gr).getConnector();
			ConnectorSpecification clone = connectorToCopy.clone();
			setConnector(clone);
		}
	}*/

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public ConnectorSpecification getConnectorSpecification() {
		return connector;
	}

	@Override
	public void setConnectorSpecification(ConnectorSpecification aConnector) {
		if (connector != aConnector) {
			if (connector != null) {
				connector.deleteObserver(this);
			}
			aConnector.addObserver(this);
			FGENotification notification = requireChange(CONNECTOR, aConnector);
			if (notification != null) {
				this.connector = aConnector;
				hasChanged(notification);
			}
		}
	}

	@Override
	public ForegroundStyle getForeground() {
		return foreground;
	}

	@Override
	public void setForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(FOREGROUND, aForeground);
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

	@Override
	public ForegroundStyle getSelectedForeground() {
		if (selectedForeground == null && foreground != null) {
			selectedForeground = foreground.clone();
		}
		return selectedForeground;
	}

	@Override
	public void setSelectedForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(SELECTED_FOREGROUND, aForeground, false);
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

	@Override
	public boolean getHasSelectedForeground() {
		return hasSelectedForeground;
	}

	@Override
	public void setHasSelectedForeground(boolean aFlag) {
		hasSelectedForeground = aFlag;
	}

	@Override
	public ForegroundStyle getFocusedForeground() {
		if (focusedForeground == null && foreground != null) {
			focusedForeground = foreground.clone();
		}
		return focusedForeground;
	}

	@Override
	public void setFocusedForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(FOCUSED_FOREGROUND, aForeground, false);
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

	@Override
	public boolean getHasFocusedForeground() {
		return hasFocusedForeground;
	}

	@Override
	public void setHasFocusedForeground(boolean aFlag) {
		hasFocusedForeground = aFlag;
	}

	/*@Override
	public final boolean shouldBeDisplayed() {
		return super.shouldBeDisplayed();
	}*/

	@Override
	public void notifyConnectorModified() {
		/*if (!isRegistered()) {
			return;
		}
		checkViewBounds();*/
		setChanged();
		notifyObservers(new ConnectorModified());
	}

	@Override
	public void notifyConnectorNeedsToBeRedrawn() {
		setChanged();
		notifyObservers(new ConnectorNeedsToBeRedrawn());
	}

	@Override
	public ConnectorType getConnectorType() {
		if (connector != null) {
			return connector.getConnectorType();
		}
		return null;
	}

	@Override
	public void setConnectorType(ConnectorType connectorType) {
		if (getConnectorType() != connectorType) {
			setConnectorSpecification(getFactory().makeConnector(connectorType, this));
		}
	}

	@Override
	public final void setText(String text) {
		super.setText(text);
	}

	/*@Override
	public ShapeGraphicalRepresentation getStartObject() {
		return startObject;
	}

	@Override
	public void setStartObject(ShapeGraphicalRepresentation aStartObject) {
		startObject = aStartObject;
		if (!enabledStartObjectObserving) {
			enableStartObjectObserving(startObject);
		}
	}*/

	/*private boolean enabledStartObjectObserving = false;
	private Vector<Observable> observedStartObjects = new Vector<Observable>();

	private boolean enabledEndObjectObserving = false;
	private Vector<Observable> observedEndObjects = new Vector<Observable>();

	protected void enableStartObjectObserving(ShapeGraphicalRepresentation aStartObject) {

		if (aStartObject == null || !aStartObject.isValidated()) {
			return;
		}

		if (enabledStartObjectObserving) {
			disableStartObjectObserving();
		}

		if (aStartObject != null ) {
			aStartObject.addObserver(this);
			observedStartObjects.add((Observable) aStartObject);
			if (!isDeserializing()) {
				for (Object o : aStartObject.getAncestors(true)) {
					if (getGraphicalRepresentation(o) != null) {
						getGraphicalRepresentation(o).addObserver(this);
						observedStartObjects.add((Observable) getGraphicalRepresentation(o));
					}
				}
			}
			enabledStartObjectObserving = true;
		}
	}

	protected void disableStartObjectObserving() {
		if (enabledStartObjectObserving) {
			for (Observable o : observedStartObjects) {
				o.deleteObserver(this);
			}

			enabledStartObjectObserving = false;
		}
	}*/

	/*@Override
	public ShapeGraphicalRepresentation getEndObject() {
		return endObject;
	}

	@Override
	public void setEndObject(ShapeGraphicalRepresentation anEndObject) {
		endObject = anEndObject;
		if (!enabledEndObjectObserving) {
			enableEndObjectObserving(endObject);
		}
	}*/

	/*protected void enableEndObjectObserving(ShapeGraphicalRepresentation anEndObject) {

		if (anEndObject == null || !anEndObject.isValidated()) {
			return;
		}

		if (enabledEndObjectObserving) {
			disableEndObjectObserving();
		}

		if (anEndObject != null) {
			anEndObject.addObserver(this);
			observedEndObjects.add((Observable) anEndObject);
			if (!isDeserializing()) {
				for (Object o : anEndObject.getAncestors(true)) {
					if (getGraphicalRepresentation(o) != null) {
						getGraphicalRepresentation(o).addObserver(this);
						observedEndObjects.add((Observable) getGraphicalRepresentation(o));
					}
				}
			}
			enabledEndObjectObserving = true;
		}
	}

	protected void disableEndObjectObserving() {
		if (enabledEndObjectObserving) {
			for (Observable o : observedEndObjects) {
				o.deleteObserver(this);
			}
			enabledEndObjectObserving = false;
		}
	}

	@Override
	public void observeRelevantObjects() {
		enableStartObjectObserving(getStartObject());
		enableEndObjectObserving(getEndObject());
	}*/

	/*@Override
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
	}*/

	/*@Override
	public Rectangle getViewBounds(double scale) {
		// return getNormalizedBounds(scale);

		Rectangle bounds = getNormalizedBounds(scale);
		Rectangle returned = new Rectangle();
		returned.x = (int) (bounds.x + (minX < 0 ? minX * bounds.width : 0));
		returned.y = (int) (bounds.y + (minY < 0 ? minY * bounds.height : 0));
		returned.width = (int) ((maxX - minX) * bounds.width);
		returned.height = (int) ((maxY - minY) * bounds.height);
		return returned;
	}

	@Override
	public int getExtendedX(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);
		return (int) (bounds.x + (minX < 0 ? minX * bounds.width : 0));
	}

	@Override
	public int getExtendedY(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);
		return (int) (bounds.y + (minY < 0 ? minY * bounds.height : 0));
	}

	@Override
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
	}*/

	/*@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);

		// return AffineTransform.getScaleInstance(bounds.width, bounds.height);

		AffineTransform returned = AffineTransform.getTranslateInstance(minX < 0 ? -minX * bounds.width : 0, minY < 0 ? -minY
				* bounds.height : 0);

		returned.concatenate(AffineTransform.getScaleInstance(bounds.width, bounds.height));

		return returned;

	}*/

	/*@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		Rectangle bounds = getNormalizedBounds(scale);

		// return AffineTransform.getScaleInstance(1.0/bounds.width, 1.0/bounds.height);

		AffineTransform returned = AffineTransform.getTranslateInstance(minX < 0 ? minX * bounds.width : 0, minY < 0 ? minY * bounds.height
				: 0);

		returned.preConcatenate(AffineTransform.getScaleInstance(1.0 / bounds.width, 1.0 / bounds.height));

		return returned;

	}*/

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	/*@Override
	public double distanceToConnector(FGEPoint aPoint, double scale, ConnectorNode<?> connectorNode) {
		return connector.distanceToConnector(aPoint, scale, connectorNode);
	}*/

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	/*@Override
	public void paint(Graphics g, AbstractDianaEditor controller) {
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
	}*/

	/*@Override
	public boolean hasFloatingLabel() {
		return hasText();
	}*/

	@Override
	public void update(Observable observable, Object notification) {
		// System.out.println("ConnectorSpecification received "+notification+" from "+observable);

		super.update(observable, notification);

		if (notification instanceof ConnectorModified) {
			// Reforward notification
			notifyConnectorModified();
		}

		if (notification instanceof ShapeNeedsToBeRedrawn) {
			// Reforward notification
			notifyConnectorNeedsToBeRedrawn();
		}

		if (observable instanceof ForegroundStyle) {
			notifyAttributeChange(FOREGROUND);
		}

		/*if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize) {
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
		}*/
	}

	/*@Override
	public boolean isConnectorConsistent() {
		// if (true) return true;
		return getStartObject() != null && getEndObject() != null && !getStartObject().isDeleted() && !getEndObject().isDeleted()
				&& FGEUtils.areElementsConnectedInGraphicalHierarchy(getStartObject(), getEndObject());
	}

	@Override
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
	}*/

	/*@Override
	public void setRegistered(boolean aFlag) {
		if (aFlag != isRegistered()) {
			super.setRegistered(aFlag);
			if (aFlag && !isDeleted()) {
				refreshConnector();
			}
		}
	}*/

	// Override for a custom view management
	/*@Override
	public JConnectorView makeConnectorView(AbstractDianaEditor controller) {
		return new JConnectorView(this, controller);
	}*/

	@Override
	public EndSymbolType getEndSymbol() {
		return endSymbol;
	}

	@Override
	public void setEndSymbol(EndSymbolType endSymbol) {
		FGENotification notification = requireChange(END_SYMBOL, endSymbol);
		if (notification != null) {
			this.endSymbol = endSymbol;
			hasChanged(notification);
		}
	}

	@Override
	public double getEndSymbolSize() {
		return endSymbolSize;
	}

	@Override
	public void setEndSymbolSize(double endSymbolSize) {
		FGENotification notification = requireChange(END_SYMBOL_SIZE, endSymbolSize);
		if (notification != null) {
			this.endSymbolSize = endSymbolSize;
			hasChanged(notification);
		}
	}

	@Override
	public MiddleSymbolType getMiddleSymbol() {
		return middleSymbol;
	}

	@Override
	public void setMiddleSymbol(MiddleSymbolType middleSymbol) {
		FGENotification notification = requireChange(MIDDLE_SYMBOL, middleSymbol);
		if (notification != null) {
			this.middleSymbol = middleSymbol;
			hasChanged(notification);
		}
	}

	@Override
	public double getMiddleSymbolSize() {
		return middleSymbolSize;
	}

	@Override
	public void setMiddleSymbolSize(double middleSymbolSize) {
		FGENotification notification = requireChange(MIDDLE_SYMBOL_SIZE, middleSymbolSize);
		if (notification != null) {
			this.middleSymbolSize = middleSymbolSize;
			hasChanged(notification);
		}
	}

	@Override
	public StartSymbolType getStartSymbol() {
		return startSymbol;
	}

	@Override
	public void setStartSymbol(StartSymbolType startSymbol) {
		FGENotification notification = requireChange(START_SYMBOL, startSymbol);
		if (notification != null) {
			this.startSymbol = startSymbol;
			hasChanged(notification);
		}
	}

	@Override
	public double getStartSymbolSize() {
		return startSymbolSize;
	}

	@Override
	public void setStartSymbolSize(double startSymbolSize) {
		FGENotification notification = requireChange(START_SYMBOL_SIZE, startSymbolSize);
		if (notification != null) {
			this.startSymbolSize = startSymbolSize;
			hasChanged(notification);
		}
	}

	@Override
	public double getRelativeMiddleSymbolLocation() {
		return relativeMiddleSymbolLocation;
	}

	@Override
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		FGENotification notification = requireChange(RELATIVE_MIDDLE_SYMBOL_LOCATION, relativeMiddleSymbolLocation);
		if (notification != null) {
			this.relativeMiddleSymbolLocation = relativeMiddleSymbolLocation;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getApplyForegroundToSymbols() {
		return applyForegroundToSymbols;
	}

	@Override
	public void setApplyForegroundToSymbols(boolean applyForegroundToSymbols) {
		FGENotification notification = requireChange(APPLY_FOREGROUND_TO_SYMBOLS, applyForegroundToSymbols);
		if (notification != null) {
			this.applyForegroundToSymbols = applyForegroundToSymbols;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getDebugCoveringArea() {
		return debugCoveringArea;
	}

	@Override
	public void setDebugCoveringArea(boolean debugCoveringArea) {
		FGENotification notification = requireChange(DEBUG_COVERING_AREA, debugCoveringArea);
		if (notification != null) {
			this.debugCoveringArea = debugCoveringArea;
			hasChanged(notification);
		}
	}

	// Override when required
	/*@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		// refreshConnector();
	}*/

	/*@Override
	public FGEConnectorGraphicsImpl getGraphics() {
		return graphics;
	}*/

	/*@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return getConnector().getControlAreas();
	}*/

	/**
	 * Override parent's behaviour by enabling start and end object observing
	 */
	/*@Override
	public void setValidated(boolean validated) {
		super.setValidated(validated);
		if (!enabledStartObjectObserving) {
			enableStartObjectObserving(startObject);
		}
		if (!enabledEndObjectObserving) {
			enableEndObjectObserving(endObject);
		}
	}*/

	@Override
	public ConnectorGraphicalRepresentation clone() {
		// logger.info("La GR "+this+" se fait cloner la");
		try {
			return (ConnectorGraphicalRepresentation) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

}
