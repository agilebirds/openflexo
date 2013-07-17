package org.openflexo.fge.impl;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawUtils;

public abstract class DrawingTreeNodeImpl<O, GR extends GraphicalRepresentation> implements DrawingTreeNode<O, GR> {

	private final DrawingImpl<?> drawing;
	private O drawable;
	private DrawingTreeNodeImpl<?, ?> parentNode;
	private List<DrawingTreeNodeImpl<?, ?>> childNodes;
	private GR graphicalRepresentation;
	private GRBinding<O, GR> grBinding;

	boolean isInvalidated = true;

	public DrawingTreeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, GR> grBinding, DrawingTreeNodeImpl<?, ?> parentNode) {
		this.drawing = drawingImpl;
		// logger.info("New DrawingTreeNode for "+aDrawable+" under "+aParentDrawable+" (is "+this+")");
		this.drawable = drawable;
		this.grBinding = grBinding;
		this.parentNode = parentNode;

		if (parentNode != null) {
			parentNode.childNodes.add(this);
		}
		childNodes = new ArrayList<DrawingTreeNodeImpl<?, ?>>();
		Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

		hash.put(drawable, this);
		graphicalRepresentation = grBinding.getGRProvider().provideGR(drawable, drawing.getFactory());

		/*if (aParentDrawable == null) { // This is the root node
			graphicalRepresentation = (GraphicalRepresentation) getDrawingGraphicalRepresentation();
		} else {
			graphicalRepresentation = retrieveGraphicalRepresentation(aDrawable);
		}*/
	}

	@Override
	public Drawing<?> getDrawing() {
		return this.drawing;
	}

	@Override
	public FGEModelFactory getFactory() {
		return getDrawing().getFactory();
	}

	@Override
	public GRBinding<O, GR> getGRBinding() {
		return grBinding;
	}

	@Override
	public void invalidate() {
		// System.out.println("* Invalidate " + drawable.getClass().getSimpleName() + " : " + drawable);
		isInvalidated = true;
		for (DrawingTreeNode<?, ?> dtn : childNodes) {
			dtn.invalidate();
		}
	}

	@Override
	public boolean isInvalidated() {
		return isInvalidated;
	}

	@Override
	public DrawingTreeNodeImpl<?, ?> getParentNode() {
		return parentNode;
	}

	@Override
	public List<DrawingTreeNodeImpl<?, ?>> getChildNodes() {
		return childNodes;
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getAncestors() {
		List<DrawingTreeNode<?, ?>> ancestors = new ArrayList<DrawingTreeNode<?, ?>>();
		ancestors.add(this);
		if (parentNode != null) {
			ancestors.addAll(parentNode.getAncestors());
		}
		return ancestors;
	}

	public DrawingTreeNode<?, ?> getCommonAncestor(DrawingTreeNode<?, ?> o) {
		List<DrawingTreeNode<?, ?>> ancestors = o.getAncestors();
		for (DrawingTreeNode<?, ?> ancestor : getAncestors()) {
			if (ancestors.contains(ancestor)) {
				return ancestor;
			}
		}
		return null;
	}

	@Override
	public int getDepth() {
		int returned = 0;
		DrawingTreeNode<?, ?> current = this;
		while (current.getParentNode() != null) {
			returned++;
			current = current.getParentNode();
		}
		return returned;
	}

	@Override
	public GR getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	/*private void update()
	{
		if (parentNode == null) { // This is the root node
			graphicalRepresentation = (GraphicalRepresentation)getDrawingGraphicalRepresentation();
		}
		else {
			GraphicalRe
			parentGR.notifyDrawableRemoved(removedGR);
			graphicalRepresentation = retrieveGraphicalRepresentation(drawable);
			System.out.println("Tiens maintenant la GR c'est "+graphicalRepresentation);
		}
	}*/

	private void removeChild(DrawingTreeNode<?, ?> aChildNode) {
		if (aChildNode == null) {
			DrawingImpl.logger.warning("Cannot remove null node");
			return;
		}
		if (childNodes.contains(aChildNode)) {
			childNodes.remove(aChildNode);
		} else {
			DrawingImpl.logger.warning("Cannot remove node: not present");
		}
		aChildNode.delete();
	}

	/**
	 * Recursively delete this DrawingTreeNode and all its descendants
	 */
	@Override
	public void delete() {
		// Normally, it is already done, but check and do it when required...
		if (parentNode != null && parentNode.childNodes.contains(this)) {
			parentNode.removeChild(this);
		}

		if (childNodes != null) {
			for (DrawingTreeNode<?, ?> n : new ArrayList<DrawingTreeNode<?, ?>>(childNodes)) {
				removeChild(n);
			}
			childNodes.clear();
		}

		childNodes = null;

		Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

		if (drawable != null) {
			hash.remove(drawable);
		}

		drawable = null;
		parentNode = null;
		graphicalRepresentation = null;

	}

	/*Vector<DrawingTreeNode<?>> nodesToRemove = new Vector<DrawingTreeNode<?>>();

	protected void beginUpdateObjectHierarchy() {

		// Invalidated nodes are to be removed rigth now
		// (we are sure that we don't want to keep it)
		if (childNodes != null) {
			for (DrawingTreeNode<?> n : new ArrayList<DrawingTreeNode<?>>(childNodes)) {
				if (n.isInvalidated) {
					removeDrawable(n.drawable, drawable);
				}
			}
		}

		// Remaining nodes are marked to potential deletion
		if (childNodes != null) {
			for (DrawingTreeNode<?> n : childNodes) {
				nodesToRemove.add(n);
			}
		}
	}

	protected void endUpdateObjectHierarchy() {
		// Nodes that keep marked for deletion are deleted now
		for (DrawingTreeNode<?> n : nodesToRemove) {
			removeDrawable(n.drawable, drawable);
		}
		nodesToRemove.clear();
	}*/

	@Override
	public O getDrawable() {
		return drawable;
	}

	@Override
	public <O2, P> boolean hasShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <O2, P> Drawing.ShapeNode<O2> getShapeFor(GRBinding.ShapeGRBinding<O2> binding, O2 aDrawable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O2, F, T> boolean hasConnectorFor(GRBinding.ConnectorGRBinding<O2> binding, O2 aDrawable, Drawing.ShapeNode<?> from,
			Drawing.ShapeNode<?> to) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <O2, F, T> Drawing.DrawingTreeNode<O2, ?> getConnectorFor(GRBinding.ConnectorGRBinding<O2> binding, O2 aDrawable,
			Drawing.ShapeNode<?> from, Drawing.ShapeNode<?> to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("this")) {
			return getGraphicalRepresentation();
		} else if (variable.getVariableName().equals("parent")) {
			return getParentNode().getGraphicalRepresentation();
		} else {
			DrawingImpl.logger.warning("Could not find variable named " + variable);
			return null;
		}
	}

	@Override
	public int getOrder(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2) {
		if (!getChildNodes().contains(child1)) {
			return 0;
		}
		if (!getChildNodes().contains(child2)) {
			return 0;
		}
		return getChildNodes().indexOf(child1) - getChildNodes().indexOf(child2);
	}

	@Override
	public final Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		AffineTransform at = convertNormalizedPointToViewCoordinatesAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return new Point((int) returned.x, (int) returned.y);
	}

	@Override
	public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertNormalizedPointToViewCoordinates(p1, scale);
		Point pp2 = convertNormalizedPointToViewCoordinates(p2, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public final FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale) {
		AffineTransform at = convertViewCoordinatesToNormalizedPointAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return returned;
	}

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	@Override
	public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y, scale);
	}

	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y, scale);
	}

	@Override
	public boolean isConnectedToDrawing() {
		if (!isValidated()) {
			return false;
		}
		DrawingTreeNode<?, ?> current = this;
		while (current != getDrawing().getRoot()) {
			DrawingTreeNode<?, ?> container = current.getParentNode();
			if (container == null) {
				return false;
			}
			current = container;
		}
		return true;
	}

	@Override
	public boolean isAncestorOf(DrawingTreeNode<?, ?> child) {
		if (!isValidated()) {
			return false;
		}
		DrawingTreeNode<?, ?> father = child.getParentNode();
		while (father != null) {
			if (father == this) {
				return true;
			}
			father = father.getParentNode();
		}
		return false;
	}

	@Override
	public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, DrawingTreeNode<?, ?> source, double scale) {
		if (!isConnectedToDrawing() || !source.isConnectedToDrawing()) {
			return new FGEPoint(p.x / scale, p.y / scale);
		}
		Point pointRelativeToCurrentView = FGEUtils.convertPoint(source, p, this, scale);
		return convertViewCoordinatesToNormalizedPoint(pointRelativeToCurrentView, scale);
	}

	@Override
	public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, DrawingTreeNode<?, ?> destination, double scale) {
		if (!isConnectedToDrawing() || !destination.isConnectedToDrawing()) {
			return new FGEPoint(p.x * scale, p.y * scale);
		}
		Point pointRelativeToRemoteView = FGEUtils.convertPoint(this, p, destination, scale);
		return destination.convertViewCoordinatesToNormalizedPoint(pointRelativeToRemoteView, scale);
	}

	@Override
	public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, DrawingTreeNode<?, ?> destination, double scale) {
		Point point = convertNormalizedPointToViewCoordinates(p, scale);
		return FGEUtils.convertPoint(this, point, destination, scale);
	}

	@Override
	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, DrawingTreeNode<?, ?> destination, double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertLocalNormalizedPointToRemoteViewCoordinates(p1, destination, scale);
		Point pp2 = convertLocalNormalizedPointToRemoteViewCoordinates(p2, destination, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, DrawingTreeNode<?, ?> source, double scale) {
		Point point = source.convertNormalizedPointToViewCoordinates(p, scale);
		return FGEUtils.convertPoint(source, point, this, scale);
	}

	@Override
	public abstract int getViewX(double scale);

	@Override
	public abstract int getViewY(double scale);

	@Override
	public abstract int getViewWidth(double scale);

	@Override
	public abstract int getViewHeight(double scale);

	@Override
	public Rectangle getViewBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = getViewX(scale);
		bounds.y = getViewY(scale);
		bounds.width = getViewWidth(scale);
		bounds.height = getViewHeight(scale);

		return bounds;
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
	}

	private boolean validated = false;
	protected LabelMetricsProvider labelMetricsProvider;

	/**
	 * Return boolean indicating if this graphical representation is validated. A validated graphical representation is a graphical
	 * representation fully embedded in its graphical representation tree, which means that parent and child are set and correct, and that
	 * start and end shapes are set for connectors
	 * 
	 * 
	 * @return
	 */
	@Override
	public boolean isValidated() {
		return validated;
	}

	@Override
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	@Override
	public LabelMetricsProvider getLabelMetricsProvider() {
		return labelMetricsProvider;
	}

	@Override
	public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider) {
		this.labelMetricsProvider = labelMetricsProvider;
	}

	/**
	 * Returns the number of pixels available for the label considering its positioning. This method is used in case of line wrapping.
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public int getAvailableLabelWidth(double scale) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Point getLabelLocation(double scale) {
		return new Point((int) (getGraphicalRepresentation().getAbsoluteTextX() * scale + getViewX(scale)),
				(int) (getGraphicalRepresentation().getAbsoluteTextY() * scale + getViewY(scale)));
	}

	@Override
	public Dimension getLabelDimension(double scale) {
		Dimension d;
		if (labelMetricsProvider != null) {
			d = labelMetricsProvider.getScaledPreferredDimension(scale);
		} else {
			d = new Dimension(0, 0);
		}
		return d;
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		getGraphicalRepresentation().setAbsoluteTextX((point.x - getViewX(scale)) / scale);
		getGraphicalRepresentation().setAbsoluteTextY((point.y - getViewY(scale)) / scale);
	}

	@Override
	public Rectangle getLabelBounds(double scale) {
		return new Rectangle(getLabelLocation(scale), getLabelDimension(scale));
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
	}

	@Override
	public ShapeNode<?> getTopLevelShapeGraphicalRepresentation(FGEPoint p) {
		return getTopLevelShapeGraphicalRepresentation(this, p);
	}

	private ShapeNode<?> getTopLevelShapeGraphicalRepresentation(DrawingTreeNode<?, ?> container, FGEPoint p) {

		List<ShapeNode<?>> enclosingShapes = new ArrayList<ShapeNode<?>>();

		for (DrawingTreeNode<?, ?> dtn : container.getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeNode<?> child = (ShapeNode<?>) dtn;
				if (child.getGraphicalRepresentation().getShape().getShape().containsPoint(FGEUtils.convertNormalizedPoint(this, p, child))) {
					enclosingShapes.add(child);
				} else {
					// Look if we are not contained in a child shape outside current shape
					ShapeNode<?> insideFocusedShape = getTopLevelShapeGraphicalRepresentation(child, p);
					if (insideFocusedShape != null && insideFocusedShape instanceof ShapeGraphicalRepresentation) {
						enclosingShapes.add(insideFocusedShape);
					}
				}
			}
		}

		if (enclosingShapes.size() > 0) {

			Collections.sort(enclosingShapes, new Comparator<ShapeNode<?>>() {
				@Override
				public int compare(ShapeNode<?> o1, ShapeNode<?> o2) {
					if (o2.getGraphicalRepresentation().getLayer() == o1.getGraphicalRepresentation().getLayer()
							&& o1.getParentNode() != null && o1.getParentNode() == o2.getParentNode()) {
						return o1.getParentNode().getOrder(o1, o2);
					}
					return o2.getGraphicalRepresentation().getLayer() - o1.getGraphicalRepresentation().getLayer();
				}
			});

			ShapeNode<?> focusedShape = enclosingShapes.get(0);

			ShapeNode<?> insideFocusedShape = getTopLevelShapeGraphicalRepresentation(focusedShape, p);

			if (insideFocusedShape != null) {
				return insideFocusedShape;
			} else {
				return focusedShape;
			}
		}

		return null;

	}

}