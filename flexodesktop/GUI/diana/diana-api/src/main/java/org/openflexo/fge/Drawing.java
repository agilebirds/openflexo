/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.ContainerGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.GeometricGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.GeometricGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapeDecorationPainter;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.factory.KeyValueCoding;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This interface is implemented by all objects representing a graphical drawing<br>
 * 
 * The internal structure of a {@link Drawing} rely on a tree whose nodes are {@link DrawingTreeNode}, which refer to a
 * {@link GraphicalRepresentation} and the represented drawable (an arbitrary java {@link Object}).
 * 
 * {@link DrawingImpl} is the default implementation
 * 
 * @see DrawingTreeNode
 * @see DrawingImpl
 * 
 * @author sylvain
 * 
 * @param <M>
 *            Type of object which is handled as root object: this is the model represented by the drawing
 */
public interface Drawing<M> extends HasPropertyChangeSupport {

	/**
	 * Encode the way the internal persistance of values for graphical properties is performed.<br>
	 * <ul>
	 * <li><b>SharedGraphicalRepresentation</b> mode indicates that {@link GraphicalRepresentation} instances are subject to be shared, and
	 * that the framework cannot rely on {@link GraphicalRepresentation} unicity, and thus, cannot assert that GraphicalRepresentation might
	 * carry property values. That means that the persistance encoding should be performed by the framework user using dynamic properties
	 * bound to original model</li>
	 * <li><b>UniqueGraphicalRepresentation</b> mode indicates that the framework user guarantees the providing of a unique
	 * {@link GraphicalRepresentation} instance for each {@link DrawingTreeNode}. Thus, the framework will use those
	 * {@link GraphicalRepresentation} to internally store graphical property values. Those {@link GraphicalRepresentation} may then be used
	 * to provide persistance of graphical properties.</li>
	 * 
	 * @author sylvain
	 * 
	 */
	public enum PersistenceMode {
		SharedGraphicalRepresentations, UniqueGraphicalRepresentations
	}

	/**
	 * This interfaces encodes a node in the drawing tree.<br>
	 * A node essentially references {@link GraphicalRepresentation} and the represented drawable (an arbitrary java {@link Object}).<br>
	 * The {@link GraphicalRepresentation} is observed using {@link PropertyChangeSupport} scheme<br>
	 * The drawable object may be observed both ways:
	 * <ul>
	 * <li>(preferably)using {@link PropertyChangeSupport} scheme, if drawable implements {@link HasPropertyChangeSupport} mechanism</li>
	 * <li>(preferably)using classical {@link Observer}/{@link Observable} scheme, if drawable extends {@link Observable}</li>
	 * </ul>
	 * 
	 * Also referenceq the {@link GRBinding} which is the specification of a node in the drawing tree
	 * 
	 * @author sylvain
	 * 
	 * @param <O>
	 *            Type of (model) object represented by this graphical node
	 * @param <GR>
	 *            Type of GraphicalRepresentation represented by this node
	 */
	public interface DrawingTreeNode<O, GR extends GraphicalRepresentation> extends BindingEvaluationContext, PropertyChangeListener,
			Observer, HasPropertyChangeSupport, KeyValueCoding {

		public static GRParameter<Boolean> IS_FOCUSED = GRParameter.getGRParameter(DrawingTreeNode.class, DrawingTreeNode.IS_FOCUSED_KEY,
				Boolean.class);
		public static GRParameter<Boolean> IS_SELECTED = GRParameter.getGRParameter(DrawingTreeNode.class, DrawingTreeNode.IS_FOCUSED_KEY,
				Boolean.class);

		@PropertyIdentifier(type = Boolean.class)
		public static final String IS_SELECTED_KEY = "isSelected";
		@PropertyIdentifier(type = Boolean.class)
		public static final String IS_FOCUSED_KEY = "isFocused";

		/*public static enum DrawingTreeNodeParameter implements GRParameter {
			isSelected, isFocused;
		}*/

		/**
		 * Return the drawing
		 * 
		 * @return
		 */
		public Drawing<?> getDrawing();

		/**
		 * Return the node specification (the formal binding between a {@link GraphicalRepresentation} and an arbitrary java {@link Object}
		 * of the represented model
		 * 
		 * @return
		 */
		public GRBinding<O, GR> getGRBinding();

		/**
		 * Return the FGEModelFactory (the factory used to build graphical representation objects)
		 * 
		 * @return
		 */
		public FGEModelFactory getFactory();

		/**
		 * Return the represented java {@link Object} (the object of the model represented by this graphical node)
		 * 
		 * @return
		 */
		public O getDrawable();

		/**
		 * Return the graphical representation which represents the drawable (the object of the model represented by this graphical node)
		 * 
		 * @return
		 */
		public GR getGraphicalRepresentation();

		/**
		 * Return parent node (a container node which contains this node)
		 * 
		 * @return
		 */
		public ContainerNode<?, ?> getParentNode();

		/**
		 * Return a list of all ancestors of this node
		 * 
		 * @return
		 */
		public List<DrawingTreeNode<?, ?>> getAncestors();

		/**
		 * Return the index of this node relatively to all children declared in parent node
		 * 
		 * @return
		 */
		public int getIndex();

		/**
		 * Return flag indicating if this node should be displayed, relatively to the value returned by visible feature in
		 * {@link GraphicalRepresentation}, and the structure of the tree (the parent should be visible too)
		 */
		public boolean shouldBeDisplayed();

		/**
		 * Return depth of this node in the whole hierarchy
		 * 
		 * @return
		 */
		public int getDepth();

		/**
		 * Invalidate this graphical node (mark it as to be updated)
		 */
		public void invalidate();

		/**
		 * Validate this graphical node
		 */
		public void validate();

		/**
		 * Return flag indicating if this node has been invalidated
		 * 
		 * @return
		 */
		public boolean isInvalidated();

		/**
		 * Return the list of all control areas declared for this graphical node
		 * 
		 * @return
		 */
		public List<? extends ControlArea<?>> getControlAreas();

		/**
		 * Recursively delete this DrawingTreeNode and all its descendants
		 */
		public boolean delete();

		/**
		 * Return a flag indicating if this node has been deleted
		 * 
		 * @return
		 */
		public boolean isDeleted();

		/**
		 * Returns the property value for supplied parameter<br>
		 * If a dynamic property was set, compute and return this value, according to binding declared as dynamic property value<br>
		 * If many {@link DrawingTreeNode} share same {@link GraphicalRepresentation} (as indicated by {@link Drawing#getPersistenceMode()),
		 * do not store value in GraphicalRepresentation, but store it in the {@link DrawingTreeNode} itself.<br>
		 * This implies that this value is not persistent (not serializable) Otherwise, use the {@link GraphicalRepresentation} as a support
		 * for this value.
		 * 
		 * @param parameter
		 *            parameter which is to be set
		 * @return
		 */
		public <T> T getPropertyValue(GRParameter<T> parameter);

		/**
		 * Sets the property value for supplied parameter<br>
		 * If a dynamic property was set, sets this value according to binding declared as dynamic property value<br>
		 * If many {@link DrawingTreeNode} share same {@link GraphicalRepresentation} (as indicated by {@link Drawing#getPersistenceMode()),
		 * do not store value in GraphicalRepresentation, but store it in the {@link DrawingTreeNode} itself.<br>
		 * This implies that this value is not persistent (not serializable) Otherwise, use the {@link GraphicalRepresentation} as a support
		 * for this value.
		 * 
		 * @param parameter
		 *            parameter which is to be set
		 * @param value
		 *            value to be set
		 * @return
		 */
		public <T> void setPropertyValue(GRParameter<T> parameter, T value);

		public boolean isConnectedToDrawing();

		public boolean isAncestorOf(DrawingTreeNode<?, ?> child);

		public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, DrawingTreeNode<?, ?> source, double scale);

		public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, DrawingTreeNode<?, ?> destination, double scale);

		public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, DrawingTreeNode<?, ?> destination, double scale);

		public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, DrawingTreeNode<?, ?> destination,
				double scale);

		public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, DrawingTreeNode<?, ?> source, double scale);

		public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

		public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale);

		public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

		public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale);

		public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

		public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale);

		public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

		public int getViewX(double scale);

		public int getViewY(double scale);

		public int getViewWidth(double scale);

		public int getViewHeight(double scale);

		public Rectangle getViewBounds(double scale);

		public FGERectangle getNormalizedBounds();

		/**
		 * Return boolean indicating if this graphical representation is validated. A validated graphical representation is a graphical
		 * representation fully embedded in its graphical representation tree, which means that parent and child are set and correct, and
		 * that start and end shapes are set for connectors
		 * 
		 * 
		 * @return
		 */
		public boolean isValidated();

		public void setValidated(boolean validated);

		public LabelMetricsProvider getLabelMetricsProvider();

		public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider);

		/**
		 * Returns the number of pixels available for the label considering its positioning. This method is used in case of line wrapping.
		 * 
		 * @param scale
		 * @return
		 */
		public int getAvailableLabelWidth(double scale);

		public Point getLabelLocation(double scale);

		public Dimension getLabelDimension(double scale);

		public void setLabelLocation(Point point, double scale);

		public Rectangle getLabelBounds(double scale);

		// public FGEGraphics getGraphics();

		// public void paint(Graphics g, DianaEditor<?> controller);

		public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

		public List<ConstraintDependency> getDependancies();

		public List<ConstraintDependency> getAlterings();

		public void declareDependantOf(DrawingTreeNode<?, ?> aNode, GRParameter requiringParameter, GRParameter requiredParameter)
				throws DependencyLoopException;

		public void notifyLabelWillBeEdited();

		public void notifyLabelHasBeenEdited();

		public void notifyLabelWillMove();

		public void notifyLabelHasMoved();

		public void notifyObjectHierarchyWillBeUpdated();

		public void notifyObjectHierarchyHasBeenUpdated();

		public boolean getIsSelected();

		public void setIsSelected(boolean aFlag);

		public boolean getIsFocused();

		public void setIsFocused(boolean aFlag);

		public boolean hasText();

		public String getText();

		public void setText(String text);

		/**
		 * Return flag indicating is this node has a floating label
		 * 
		 * @return
		 */
		public boolean hasFloatingLabel();

		/**
		 * Return flag indicating is this node should display an inside label
		 * 
		 * @return
		 */
		public boolean hasContainedLabel();

		/**
		 * Returned required dimension for label (null when hasContainedLabel() return false)
		 * 
		 * @return
		 */
		public FGEDimension getRequiredLabelSize();

		public boolean isParentLayoutedAsContainer();

		public TextStyle getTextStyle();

		public void setTextStyle(TextStyle style);

	}

	public interface ContainerNode<O, GR extends ContainerGraphicalRepresentation> extends DrawingTreeNode<O, GR> {

		public double getWidth();

		public void setWidth(double aValue);

		public double getHeight();

		public void setHeight(double aValue);

		public FGEDimension getSize();

		public void setSize(FGEDimension newSize);

		public List<? extends DrawingTreeNode<?, ?>> getChildNodes();

		public void addChild(DrawingTreeNode<?, ?> aChildNode);

		public void removeChild(DrawingTreeNode<?, ?> aChildNode);

		public int getOrder(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2);

		public ShapeNode<?> getTopLevelShapeGraphicalRepresentation(FGEPoint p);

		public void notifyNodeAdded(DrawingTreeNode<?, ?> addedNode);

		public void notifyNodeRemoved(DrawingTreeNode<?, ?> removedNode);

		public <O2> boolean hasShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable);

		public <O2> ShapeNode<O2> getShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable);

		public <O2> boolean hasConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable);

		public <O2> ConnectorNode<O2> getConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable);

		/**
		 * Notify that the object just resized
		 */
		public void notifyObjectResized();

		/**
		 * Notify that the object just resized
		 */
		public void notifyObjectResized(FGEDimension oldSize);

		/**
		 * Notify that the object will be resized
		 */
		public void notifyObjectWillResize();

		/**
		 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
		 * resizing: use notifyObjectResize() instead)
		 */
		public void notifyObjectHasResized();

		public boolean isResizing();

		public Dimension getNormalizedLabelSize();

		public Rectangle getNormalizedLabelBounds();

		public FGERectangle getRequiredBoundsForContents();

		public FGERectangle getBounds();

	}

	public interface RootNode<M> extends ContainerNode<M, DrawingGraphicalRepresentation> {

		/**
		 * Paint this {@link RootNode} using supplied FGEDrawingGraphics
		 * 
		 * @param g
		 */
		public void paint(FGEDrawingGraphics g);

	}

	public interface ShapeNode<O> extends ContainerNode<O, ShapeGraphicalRepresentation> {

		public Shape<?> getShape();

		public double getUnscaledViewWidth();

		public double getUnscaledViewHeight();

		/**
		 * Return bounds (including border) relative to parent container
		 * 
		 * @return
		 */
		@Override
		public FGERectangle getBounds();

		/**
		 * Return view bounds (excluding border) relative to parent container
		 * 
		 * @param scale
		 * @return
		 */
		public Rectangle getBounds(double scale);

		/**
		 * Return view bounds (excluding border) relative to given container
		 * 
		 * @param scale
		 * @return
		 */
		public Rectangle getBounds(DrawingTreeNode<?, ?> container, double scale);

		/**
		 * Return logical bounds (including border) relative to given container
		 * 
		 * @param scale
		 * @return
		 */
		public Rectangle getViewBounds(DrawingTreeNode<?, ?> container, double scale);

		public boolean isPointInsideShape(FGEPoint aPoint);

		public FGEShape<?> getFGEShape();

		public FGEShape<?> getFGEShapeOutline();

		public void notifyShapeChanged();

		public void notifyShapeNeedsToBeRedrawn();

		public void notifyObjectMoved();

		public void notifyObjectMoved(FGEPoint oldLocation);

		public void notifyObjectWillMove();

		public void notifyObjectHasMoved();

		public boolean isMoving();

		public void extendParentBoundsToHostThisShape();

		/**
		 * Check and eventually relocate and resize current graphical representation in order to all all contained shape graphical
		 * representations. Contained graphical representations may substantically be relocated.
		 */
		public void extendBoundsToHostContents();

		public double getX();

		public void setX(double aValue);

		public double getY();

		public void setY(double aValue);

		public FGEPoint getLocation();

		public void setLocation(FGEPoint newLocation);

		public FGEPoint getLocationInDrawing();

		public boolean isFullyContainedInContainer();

		public double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation);

		@Override
		public int getAvailableLabelWidth(double scale);

		public ShapeDecorationPainter getDecorationPainter();

		public void setDecorationPainter(ShapeDecorationPainter aPainter);

		public ShapePainter getShapePainter();

		public void setShapePainter(ShapePainter aPainter);

		public void finalizeConstraints();

		/**
		 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to start
		 * @return the area on which the given connector can start
		 */
		public FGEArea getAllowedStartAreaForConnector(ConnectorNode<?> connector);

		/**
		 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to end
		 * @return the area on which the given connector can end
		 */
		public FGEArea getAllowedEndAreaForConnector(ConnectorNode<?> connector);

		/**
		 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to start
		 * 
		 * @return the area on which the given connector can start
		 */
		public FGEArea getAllowedStartAreaForConnectorForDirection(ConnectorNode<?> connector, FGEArea area,
				SimplifiedCardinalDirection direction);

		/**
		 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
		 * 
		 * @param connectorGR
		 *            the connector asking where to end
		 * @return the area on which the given connector can end
		 */
		public FGEArea getAllowedEndAreaForConnectorForDirection(ConnectorNode<?> connector, FGEArea area,
				SimplifiedCardinalDirection direction);

		/**
		 * Paint this {@link ShapeNode} using supplied FGEShapeGraphics
		 * 
		 * @param g
		 */
		public void paint(FGEShapeGraphics g);

		public ForegroundStyle getForegroundStyle();

		public void setForegroundStyle(ForegroundStyle aValue);

		public ShadowStyle getShadowStyle();

		public void setShadowStyle(ShadowStyle style);

		public BackgroundStyle getBackgroundStyle();

		public void setBackgroundStyle(BackgroundStyle style);

		public ShapeSpecification getShapeSpecification();

		public void setShapeSpecification(ShapeSpecification shapeSpecification);
	}

	public interface ConnectorNode<O> extends DrawingTreeNode<O, ConnectorGraphicalRepresentation> {

		public ShapeNode<?> getStartNode();

		public ShapeNode<?> getEndNode();

		public Connector<?> getConnector();

		public void notifyConnectorModified();

		public int getExtendedX(double scale);

		public int getExtendedY(double scale);

		/**
		 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the
		 * two related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
		 */
		public Rectangle getNormalizedBounds(double scale);

		public boolean isConnectorConsistent();

		public void refreshConnector();

		public double distanceToConnector(FGEPoint aPoint, double scale);

		/**
		 * Paint this {@link ConnectorNode} using supplied FGEConnectorGraphics
		 * 
		 * @param g
		 */
		public void paint(FGEConnectorGraphics g);

		public ForegroundStyle getForegroundStyle();

		public void setForegroundStyle(ForegroundStyle aValue);

		public ConnectorSpecification getConnectorSpecification();

		public void setConnectorSpecification(ConnectorSpecification connectorSpecification);
	}

	public interface GeometricNode<O> extends DrawingTreeNode<O, GeometricGraphicalRepresentation> {

		public Rectangle getBounds(double scale);

		public void paintGeometricObject(FGEGeometricGraphics graphics);

		public List<ControlPoint> getControlPoints();

		public List<ControlPoint> rebuildControlPoints();

		public void notifyGeometryChanged();

		/**
		 * Paint this {@link GeometricNode} using supplied FGEGeometricGraphics
		 * 
		 * @param g
		 */
		public void paint(FGEGeometricGraphics g);

		public ForegroundStyle getForegroundStyle();

		public void setForegroundStyle(ForegroundStyle aValue);

	}

	public PersistenceMode getPersistenceMode();

	public boolean isEditable();

	public void setEditable(boolean editable);

	public M getModel();

	public FGEModelFactory getFactory();

	public RootNode<M> getRoot();

	/**
	 * Delete this {@link Drawing} implementation, by deleting all {@link DrawingTreeNode}
	 */
	public void delete();

	/**
	 * Update the whole tree of graphical object hierarchy<br>
	 * Recursively navigate in the tree to find invalidated nodes. Only invalidated nodes are recomputed (and eventually rebuild if the
	 * graphical object hierarchy structure has changed)
	 * 
	 */
	public void updateGraphicalObjectsHierarchy();

	/**
	 * Update of all DrawingTreeNode representing supplied drawable<br>
	 * Recursively navigate in the tree to find invalidated nodes. Only invalidated nodes are recomputed (and eventually rebuild if the
	 * graphical object hierarchy structure has changed)
	 * 
	 * @param drawable
	 */
	public <O> void updateGraphicalObjectsHierarchy(O drawable);

	/**
	 * Invalidate the whole hierarchy.<br>
	 * All nodes of drawing tree are invalidated, which means that a complete recomputing of the whole tree will be performed during next
	 * updateGraphicalHierarchy() call<br>
	 * Existing graphical representation are kept.
	 */
	public void invalidateGraphicalObjectsHierarchy();

	/**
	 * Invalidate the graphical object hierarchy under all nodes where supplied object is represented.<br>
	 * All nodes of drawing tree under supplied node are invalidated, which means that a recomputing of the whole tree under one (or many)
	 * node (the nodes representing supplied drawable) will be performed during next updateGraphicalObjectHierarchy() call.<br>
	 * 
	 */
	public <O> void invalidateGraphicalObjectsHierarchy(O drawable);

	public DrawingGRBinding<M> bindDrawing(Class<M> drawingClass, String name, DrawingGRProvider<M> grProvider);

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ShapeGRProvider<R> grProvider);

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ContainerGRBinding<?, ?> parentBinding,
			ShapeGRProvider<R> grProvider);

	public <R> GeometricGRBinding<R> bindGeometric(Class<R> geometricObjectClass, String name, GeometricGRProvider<R> grProvider);

	public <R> GeometricGRBinding<R> bindGeometric(Class<R> geometricObjectClass, String name, ContainerGRBinding<?, ?> parentBinding,
			GeometricGRProvider<R> grProvider);

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ConnectorGRProvider<R> grProvider);

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding, ConnectorGRProvider<R> grProvider);

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding, ContainerGRBinding<?, ?> parentBinding, ConnectorGRProvider<R> grProvider);

	public <O> ShapeNode<O> createNewShapeNode(ContainerNode<?, ?> parent, ShapeGRBinding<O> binding, O representable);

	public <O> ConnectorNode<O> createNewConnectorNode(ContainerNode<?, ?> parent, ConnectorGRBinding<O> binding, O representable,
			ShapeNode<?> fromNode, ShapeNode<?> toNode);

	public <O> boolean hasPendingConnector(ConnectorGRBinding<O> binding, O drawable, DrawingTreeNodeIdentifier<?> parentNodeIdentifier,
			DrawingTreeNodeIdentifier<?> startNodeIdentifier, DrawingTreeNodeIdentifier<?> endNodeIdentifier);

	public <O> PendingConnector<O> getPendingConnector(ConnectorGRBinding<O> binding, O drawable,
			DrawingTreeNodeIdentifier<?> parentNodeIdentifier, DrawingTreeNodeIdentifier<?> startNodeIdentifier,
			DrawingTreeNodeIdentifier<?> endNodeIdentifier);

	public <O> PendingConnector<O> createPendingConnector(ConnectorGRBinding<O> binding, O drawable,
			DrawingTreeNodeIdentifier<?> parentNodeIdentifier, DrawingTreeNodeIdentifier<?> startNodeIdentifier,
			DrawingTreeNodeIdentifier<?> endNodeIdentifier);

	// public <O> ShapeNode<O> drawShape(ShapeNode<?> parent, ShapeGRBinding<O> binding, O representable);

	/*public DrawingTreeNode<?> getContainer(DrawingTreeNode<?> node);

	public List<DrawingTreeNode<?>> getContainedNodes(DrawingTreeNode<?> parentNode);*/

	/**
	 * Retrieve first drawing tree node matching supplied drawable<br>
	 * Note that GRBinding is not specified here, so if a given drawable is represented through multiple GRBinding, there is no guarantee
	 * that you receive the right object. Use {@link #getDrawingTreeNode(Object, GRBinding)} instead
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O, GR extends GraphicalRepresentation> DrawingTreeNode<O, GR> getDrawingTreeNode(O aDrawable);

	/**
	 * Retrieve drawing tree node matching supplied drawable and grBinding
	 * 
	 * @param aDrawable
	 * @return
	 */
	public <O, GR extends GraphicalRepresentation> DrawingTreeNode<O, GR> getDrawingTreeNode(O aDrawable, GRBinding<O, GR> grBinding);

	/**
	 * Retrieve drawing tree node matching supplied identifier
	 * 
	 * @param identifier
	 * @return
	 */
	public <O> DrawingTreeNode<O, ?> getDrawingTreeNode(DrawingTreeNodeIdentifier<O> identifier);

	/**
	 * Encodes the dependancy between two {@link DrawingTreeNode}
	 * 
	 * @author sylvain
	 * 
	 */
	public static class ConstraintDependency {
		public DrawingTreeNode<?, ?> requiringGR;
		public GRParameter requiringParameter;
		public DrawingTreeNode<?, ?> requiredGR;
		public GRParameter requiredParameter;

		public ConstraintDependency(DrawingTreeNode<?, ?> requiringGR, GRParameter requiringParameter, DrawingTreeNode<?, ?> requiredGR,
				GRParameter requiredParameter) {
			super();
			this.requiringGR = requiringGR;
			this.requiringParameter = requiringParameter;
			this.requiredGR = requiredGR;
			this.requiredParameter = requiredParameter;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConstraintDependency) {
				ConstraintDependency opposite = (ConstraintDependency) obj;
				return requiredGR == opposite.requiredGR && requiringGR == opposite.requiringGR
						&& requiringParameter == opposite.requiringParameter && requiredParameter == opposite.requiredParameter;
			}
			return super.equals(obj);
		}
	}

	/**
	 * Throw when a dependancy raises a loop
	 * 
	 * @author sylvain
	 * 
	 */
	@SuppressWarnings("serial")
	public static class DependencyLoopException extends Exception {
		private List<DrawingTreeNode<?, ?>> dependencies;

		public DependencyLoopException(List<DrawingTreeNode<?, ?>> dependancies) {
			this.dependencies = dependancies;
		}

		@Override
		public String getMessage() {
			return "DependencyLoopException: " + dependencies;
		}
	}

	/**
	 * Encodes an identifier for a DrawingTreeNode<br>
	 * This couple of data always uniquely identifies an occurrence of a given object in a particular context in a Drawing.<br>
	 * Use of this identifier allows to guarantee a conceptual persistence over life cycle of {@link Drawing}.
	 * 
	 * @author sylvain
	 * 
	 * @param <O>
	 */
	public static class DrawingTreeNodeIdentifier<O> {
		private O drawable;
		private GRBinding<O, ?> grBinding;

		public DrawingTreeNodeIdentifier(O drawable, GRBinding<O, ?> grBinding) {
			super();
			this.drawable = drawable;
			this.grBinding = grBinding;
		}

		public O getDrawable() {
			return drawable;
		}

		public GRBinding<O, ?> getGRBinding() {
			return grBinding;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (drawable == null ? 0 : drawable.hashCode());
			result = prime * result + (grBinding == null ? 0 : grBinding.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			DrawingTreeNodeIdentifier other = (DrawingTreeNodeIdentifier) obj;
			if (drawable == null) {
				if (other.drawable != null) {
					return false;
				}
			} else if (!drawable.equals(other.drawable)) {
				return false;
			}
			if (grBinding == null) {
				if (other.grBinding != null) {
					return false;
				}
			} else if (!grBinding.equals(other.grBinding)) {
				return false;
			}
			return true;
		}

	}

	public static interface PendingConnector<O> {
		public ConnectorNode<O> getConnectorNode();

		public DrawingTreeNodeIdentifier<?> getParentNodeIdentifier();

		public DrawingTreeNodeIdentifier<?> getStartNodeIdentifier();

		public DrawingTreeNodeIdentifier<?> getEndNodeIdentifier();

		public boolean tryToResolve(Drawing<?> drawing);

	}

}
