package org.openflexo.fge.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.impl.ConnectorImpl;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.GeomUtils;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.notifications.ConnectorModified;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;

public class ConnectorNodeImpl<O> extends DrawingTreeNodeImpl<O, ConnectorGraphicalRepresentation> implements ConnectorNode<O> {

	private static final Logger logger = Logger.getLogger(ConnectorNodeImpl.class.getPackage().getName());

	private ShapeNodeImpl<?> startNode;
	private ShapeNodeImpl<?> endNode;

	private Connector<?> connector;

	protected ConnectorNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ConnectorGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		startDrawableObserving();
	}

	@Override
	public ShapeNodeImpl<?> getStartNode() {
		return startNode;
	}

	public void setStartNode(ShapeNodeImpl<?> startNode) {
		if (this.startNode != startNode) {
			disableStartObjectObserving();
			this.startNode = startNode;
			enableStartObjectObserving(startNode);
		}
	}

	@Override
	public ShapeNodeImpl<?> getEndNode() {
		return endNode;
	}

	public void setEndNode(ShapeNodeImpl<?> endNode) {
		if (this.endNode != endNode) {
			disableEndObjectObserving();
			this.endNode = endNode;
			enableEndObjectObserving(endNode);
		}
	}

	@Override
	public Connector<?> getConnector() {
		if (connector == null && getConnectorSpecification() != null) {
			System.out.println("################# MAKE NEW CONNECTOR with " + getConnectorSpecification());
			connector = getConnectorSpecification().makeConnector(this);
			getConnectorSpecification().getPropertyChangeSupport().addPropertyChangeListener(connector);
		}
		return connector;
	}

	@Override
	public void notifyConnectorModified() {
		/*if (!isRegistered()) {
			return;
		}*/
		checkViewBounds();
		setChanged();
		notifyObservers(new ConnectorModified());
	}

	private boolean enabledStartObjectObserving = false;
	private List<DrawingTreeNode<?, ?>> observedStartObjects = new ArrayList<DrawingTreeNode<?, ?>>();

	private boolean enabledEndObjectObserving = false;
	private List<DrawingTreeNode<?, ?>> observedEndObjects = new ArrayList<DrawingTreeNode<?, ?>>();

	protected void enableStartObjectObserving(ShapeNode<?> aStartNode) {

		if (aStartNode == null || !aStartNode.isValidated()) {
			return;
		}

		if (enabledStartObjectObserving) {
			disableStartObjectObserving();
		}

		if (aStartNode != null /*&& !enabledStartObjectObserving*/) {
			aStartNode.getPropertyChangeSupport().addPropertyChangeListener(this);
			observedStartObjects.add(aStartNode);
			System.out.println("Je suis " + this + " et j'observe " + aStartNode);
			// if (!isDeserializing()) {
			for (DrawingTreeNode<?, ?> node : aStartNode.getAncestors()) {
				/*if (getGraphicalRepresentation(o) != null) {
					getGraphicalRepresentation(o).addObserver(this);
					observedStartObjects.add((Observable) getGraphicalRepresentation(o));
				}*/
				if (node != null) {
					node.getPropertyChangeSupport().addPropertyChangeListener(this);
					observedStartObjects.add(node);
				}
			}
			// }
			enabledStartObjectObserving = true;
		}
	}

	protected void disableStartObjectObserving() {
		if (enabledStartObjectObserving) {

			for (DrawingTreeNode<?, ?> node : observedStartObjects) {
				node.getPropertyChangeSupport().removePropertyChangeListener(this);
			}

			enabledStartObjectObserving = false;
		}
	}

	protected void enableEndObjectObserving(ShapeNode<?> aEndNode) {

		if (aEndNode == null || !aEndNode.isValidated()) {
			return;
		}

		if (enabledEndObjectObserving) {
			disableEndObjectObserving();
		}

		if (aEndNode != null /*&& !enabledEndObjectObserving*/) {
			aEndNode.getPropertyChangeSupport().addPropertyChangeListener(this);
			observedEndObjects.add(aEndNode);
			// if (!isDeserializing()) {
			for (DrawingTreeNode<?, ?> node : aEndNode.getAncestors()) {
				/*if (getGraphicalRepresentation(o) != null) {
					getGraphicalRepresentation(o).addObserver(this);
					observedEndObjects.add((Observable) getGraphicalRepresentation(o));
				}*/
				if (node != null) {
					node.getPropertyChangeSupport().addPropertyChangeListener(this);
					observedEndObjects.add(node);
				}
			}
			// }
			enabledEndObjectObserving = true;
		}
	}

	protected void disableEndObjectObserving(/*ShapeGraphicalRepresentation anEndObject*/) {
		if (/*anEndObject != null &&*/enabledEndObjectObserving) {
			/*anEndObject.deleteObserver(this);
			if (!isDeserializing()) {
				for (Object o : anEndObject.getAncestors())
					if (getGraphicalRepresentation(o) != null) getGraphicalRepresentation(o).deleteObserver(this);
			}*/
			for (DrawingTreeNode<?, ?> node : observedEndObjects) {
				node.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			enabledEndObjectObserving = false;
		}
	}

	protected void observeRelevantObjects() {
		enableStartObjectObserving(getStartNode());
		enableEndObjectObserving(getEndNode());
	}

	protected void stopObserveRelevantObjects() {
		if (getStartNode() != null) {
			disableStartObjectObserving();
		}
		if (getEndNode() != null) {
			disableEndObjectObserving();
		}
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
		if (GeomUtils.checkDoubleIsAValue(r.getMinX()) && GeomUtils.checkDoubleIsAValue(r.getMinY())
				&& GeomUtils.checkDoubleIsAValue(r.getMaxX()) && GeomUtils.checkDoubleIsAValue(r.getMaxY())) {
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

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	@Override
	public Rectangle getNormalizedBounds(double scale) {
		if (getStartNode() == null || getStartNode().isDeleted() || getEndNode() == null || getEndNode().isDeleted()) {
			logger.warning("Could not obtain connector bounds: start or end object is null or deleted");
			logger.warning("Object: " + this + " startObject=" + getStartNode() + " endObject=" + getEndNode());
			// Here, we return a (1,1)-size to avoid obtaining Infinity AffinTransform !!!
			return new Rectangle(0, 0, 1, 1);
		}

		if (getParentNode() == null) {
			logger.warning("getNormalizedBounds() called for GR " + this + " with container=null, validated=" + isValidated());
		}

		Rectangle startBounds = getStartNode().getViewBounds(getParentNode(), scale);
		Rectangle endsBounds = getEndNode().getViewBounds(getParentNode(), scale);

		Rectangle bounds = new Rectangle();
		Rectangle2D.union(startBounds, endsBounds, bounds);

		return bounds;
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		FGERectangle drawingViewBounds = new FGERectangle(drawingViewSelection.getX(), drawingViewSelection.getY(),
				drawingViewSelection.getWidth(), drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		for (ControlArea<?> ca : getControlAreas()) {
			if (ca instanceof ControlPoint) {
				ControlPoint cp = (ControlPoint) ca;
				Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getDrawing().getRoot(), scale);
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

	@Override
	public Point getLabelLocation(double scale) {
		Point connectorCenter = convertNormalizedPointToViewCoordinates(getConnector().getMiddleSymbolLocation(), scale);
		return new Point((int) (connectorCenter.x + getGraphicalRepresentation().getAbsoluteTextX() * scale + getViewX(scale)),
				(int) (connectorCenter.y + getGraphicalRepresentation().getAbsoluteTextY() * scale + getViewY(scale)));
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		Point connectorCenter = convertNormalizedPointToViewCoordinates(getConnector().getMiddleSymbolLocation(), scale);
		getGraphicalRepresentation().setAbsoluteTextX(((double) point.x - connectorCenter.x - getViewX(scale)) / scale);
		getGraphicalRepresentation().setAbsoluteTextY(((double) point.y - connectorCenter.y - getViewY(scale)) / scale);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		super.propertyChange(evt);

		if (evt.getPropertyName().equals(ObjectWillMove.EVENT_NAME) || evt.getPropertyName().equals(ObjectWillResize.EVENT_NAME)) {
			((ConnectorImpl<?>) getConnector()).connectorWillBeModified();
			// Propagate notification to views
			forward(evt);
		}
		if (evt.getPropertyName().equals(ObjectHasMoved.EVENT_NAME) || evt.getPropertyName().equals(ObjectHasResized.EVENT_NAME)) {
			((ConnectorImpl<?>) getConnector()).connectorHasBeenModified();
			// Propagate notification to views
			forward(evt);
		}
		if (evt.getPropertyName().equals(ObjectMove.PROPERTY_NAME) || evt.getPropertyName().equals(ObjectResized.PROPERTY_NAME)
				|| evt.getPropertyName().equals(ShapeChanged.EVENT_NAME)) {
			// if (observable == startObject || observable == endObject) {
			// !!! or any of ancestors
			refreshConnector();
			// }
		}

		if (evt.getPropertyName().equals(ConnectorGraphicalRepresentation.CONNECTOR.getName())
				|| evt.getPropertyName().equals(ConnectorGraphicalRepresentation.CONNECTOR_TYPE.getName())) {
			// Connector Specification has changed
			fireConnectorSpecificationChanged();
		}

		/*if (notification instanceof ConnectorModified) {
			updateControlAreas();
		}*/

		if (evt.getSource() instanceof ForegroundStyle) {
			notifyAttributeChanged(ConnectorGraphicalRepresentation.FOREGROUND, null, getGraphicalRepresentation().getForeground());
		}

	}

	private void fireConnectorSpecificationChanged() {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! Changing ConnectorSpecification");
		if (connector != null && getConnectorSpecification() != null) {
			System.out.println("Hop, le connecteur se fait deleter pour le ConnectorNodeImpl " + hashCode());
			getConnectorSpecification().getPropertyChangeSupport().removePropertyChangeListener(connector);
			connector.delete();
			connector = null;
		}
		refreshConnector(true);
		System.out.println("Nouveau connecteur: " + getConnector());
	}

	@Override
	public boolean isConnectorConsistent() {
		// if (true) return true;
		return getStartNode() != null && getEndNode() != null && !getStartNode().isDeleted() && !getEndNode().isDeleted()
				&& FGEUtils.areElementsConnectedInGraphicalHierarchy(getStartNode(), getEndNode());
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
			if (forceRefresh || (getConnector() != null && ((ConnectorImpl<?>) getConnector()).needsRefresh())) {
				try {
					((ConnectorImpl<?>) getConnector()).refreshConnector(forceRefresh);
					System.out.println("Tout se passe bien avec le ConnectorNodeImpl " + hashCode() + " et le connector " + getConnector());
				} catch (Exception e) {
					e.printStackTrace();
					ConnectorSpecification cs = getConnectorSpecification();
					Connector c = getConnector();
					System.out.println("OK, j'ai un pb avec le ConnectorNodeImpl " + hashCode() + " et le connector " + getConnector());
				}
				checkViewBounds();
				notifyConnectorModified();
			}
		} catch (Exception e) {
			logger.warning("Unexpected exception: " + e);
			e.printStackTrace();
		}
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return getConnector().getControlAreas();
	}

	@Override
	public boolean delete() {
		if (!isDeleted()) {
			System.out.println("Hop, le connecteur se fait deleter !!!");
			if (connector != null) {
				connector.delete();
			}
			connector = null;
			super.delete();
			stopDrawableObserving();
			disableStartObjectObserving();
			disableEndObjectObserving();
			return true;
		}
		return false;
	}

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		return connector.distanceToConnector(aPoint, scale);
	}

	@Override
	public String toString() {
		return "ConnectorImpl-" + getIndex() + (getStartNode() != null ? "[Shape-" + getStartNode().getIndex() + "]" : "[???]")
				+ (getEndNode() != null ? "[Shape-" + getEndNode().getIndex() + "]" : "[???]") + ":" + getDrawable();
	}

	@Override
	public boolean hasContainedLabel() {
		return false;
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText();
	}

	@Override
	public FGEDimension getRequiredLabelSize() {
		return null;
	}

	public void paint(FGEConnectorGraphics g) {

		if (getStartNode() == null || getStartNode().isDeleted()) {
			logger.warning("Could not paint connector: start object is null or deleted");
			return;
		}

		if (getEndNode() == null || getEndNode().isDeleted()) {
			logger.warning("Could not paint connector: end object is null or deleted");
			return;
		}

		/*if (FGEConstants.DEBUG) {
			Graphics2D g2 = graphics.getGraphics();
			g2.setColor(Color.PINK);
			g2.drawRect(0, 0, getNode().getViewWidth(controller.getScale()) - 1, getNode().getViewHeight(controller.getScale()) - 1);
		}*/

		if (getConnectorSpecification() != null) {

			if (!isValidated()) {
				logger.warning("paint connector requested for not validated connector graphical representation: " + this);
				return;
			}
			if (getStartNode() == null || getStartNode().isDeleted() || !getStartNode().isValidated()) {
				logger.warning("paint connector requested for invalid start object (either null, deleted or not validated) : " + this
						+ " start=" + getStartNode());
				return;
			}
			if (getEndNode() == null || getEndNode().isDeleted() || !getEndNode().isValidated()) {
				logger.warning("paint connector requested for invalid start object (either null, deleted or not validated) : " + this
						+ " end=" + getEndNode());
				return;
			}

			if (getConnector() == null) {
				logger.warning("null connector for connector specification " + getConnectorSpecification());
			}

			getConnector().paintConnector(g);
		}

	}

	@Override
	public ForegroundStyle getForegroundStyle() {
		return getPropertyValue(ConnectorGraphicalRepresentation.FOREGROUND);
	}

	@Override
	public void setForegroundStyle(ForegroundStyle aValue) {
		setPropertyValue(ConnectorGraphicalRepresentation.FOREGROUND, aValue);
	}

	/**
	 * Convenient method used to retrieve shape specification property value
	 */
	@Override
	public ConnectorSpecification getConnectorSpecification() {
		return getPropertyValue(ConnectorGraphicalRepresentation.CONNECTOR);
	}

	/**
	 * Convenient method used to set shape specification property value
	 */
	@Override
	public void setConnectorSpecification(ConnectorSpecification connectorSpecification) {
		if (connectorSpecification != getConnectorSpecification()) {
			setPropertyValue(ConnectorGraphicalRepresentation.CONNECTOR, connectorSpecification);
			fireConnectorSpecificationChanged();
		}
	}

}
