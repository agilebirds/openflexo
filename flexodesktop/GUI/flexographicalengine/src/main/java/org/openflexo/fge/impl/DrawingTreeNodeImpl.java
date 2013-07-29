package org.openflexo.fge.impl;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TargetObject;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConstraintDependency;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DependencyLoopException;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.GraphicalRepresentation.LabelMetricsProvider;
import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawUtils;
import org.openflexo.fge.notifications.BindingChanged;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.LabelHasEdited;
import org.openflexo.fge.notifications.LabelHasMoved;
import org.openflexo.fge.notifications.LabelWillEdit;
import org.openflexo.fge.notifications.LabelWillMove;

public abstract class DrawingTreeNodeImpl<O, GR extends GraphicalRepresentation> extends Observable implements DrawingTreeNode<O, GR> {

	private static final Logger logger = Logger.getLogger(DrawingTreeNodeImpl.class.getPackage().getName());

	private final DrawingImpl<?> drawing;
	private O drawable;
	private ContainerNodeImpl<?, ?> parentNode;
	private GR graphicalRepresentation;
	private GRBinding<O, GR> grBinding;

	// private List<ControlArea<?>> controlAreas;

	private List<ConstraintDependency> dependancies;
	private List<ConstraintDependency> alterings;

	private boolean isInvalidated = true;
	private boolean isDeleted = false;

	public DrawingTreeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, GR> grBinding, ContainerNodeImpl<?, ?> parentNode) {
		this.drawing = drawingImpl;
		// logger.info("New DrawingTreeNode for "+aDrawable+" under "+aParentDrawable+" (is "+this+")");
		this.drawable = drawable;
		this.grBinding = grBinding;

		this.parentNode = parentNode;

		Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

		hash.put(drawable, this);

		// parentNode.addChild(this);

		graphicalRepresentation = grBinding.getGRProvider().provideGR(drawable, drawing.getFactory());

		System.out.println("Hop");

		/*if (aParentDrawable == null) { // This is the root node
			graphicalRepresentation = (GraphicalRepresentation) getDrawingGraphicalRepresentation();
		} else {
			graphicalRepresentation = retrieveGraphicalRepresentation(aDrawable);
		}*/

		dependancies = new ArrayList<ConstraintDependency>();
		alterings = new ArrayList<ConstraintDependency>();

		// controlAreas = new ArrayList<ControlArea<?>>();
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
		/*for (DrawingTreeNode<?, ?> dtn : childNodes) {
			dtn.invalidate();
		}*/
	}

	@Override
	public void validate() {
		isInvalidated = false;
	}

	@Override
	public boolean isInvalidated() {
		return isInvalidated;
	}

	@Override
	public ContainerNodeImpl<?, ?> getParentNode() {
		return parentNode;
	}

	protected void setParentNode(ContainerNodeImpl<?, ?> parentNode) {
		this.parentNode = parentNode;
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

	@Override
	public abstract List<? extends ControlArea<?>> getControlAreas();

	/*protected final void updateControlAreas() {
		controlAreas.clear();
		controlAreas.addAll(rebuildControlAreas());
	}*/

	// protected abstract List<? extends ControlArea<?>> rebuildControlAreas();

	/*private void update()
	{
		if (parentNode == null) { // This is the root node
			graphicalRepresentation = (GraphicalRepresentation)getDrawingGraphicalRepresentation();
		}
		else {
			GraphicalRe
			parentNode.notifyDrawableRemoved(removedGR);
			graphicalRepresentation = retrieveGraphicalRepresentation(drawable);
			System.out.println("Tiens maintenant la GR c'est "+graphicalRepresentation);
		}
	}*/

	/**
	 * Recursively delete this DrawingTreeNode and all its descendants
	 */
	@Override
	public void delete() {
		// Normally, it is already done, but check and do it when required...
		if (parentNode instanceof ContainerNode && ((ContainerNode<?, ?>) parentNode).getChildNodes().contains(this)) {
			parentNode.removeChild(this);
		}

		Hashtable<Object, DrawingTreeNode<?, ?>> hash = this.drawing.retrieveHash(grBinding);

		if (drawable != null) {
			hash.remove(drawable);
		}

		drawable = null;
		parentNode = null;
		graphicalRepresentation = null;

		isDeleted = true;
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
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

	protected void updateDependanciesForBinding(DataBinding<?> binding) {
		if (binding == null) {
			return;
		}

		// logger.info("Searching dependancies for "+this);

		DrawingTreeNode<?, ?> node = this;
		// TODO !!!!
		List<TargetObject> targetList = binding.getTargetObjects(this);
		if (targetList != null) {
			for (TargetObject o : targetList) {
				// System.out.println("> "+o.target+" for "+o.propertyName);
				if (o.target instanceof DrawingTreeNode) {
					DrawingTreeNode<?, ?> c = (DrawingTreeNode<?, ?>) o.target;
					GRParameter param = c.getGraphicalRepresentation().parameterWithName(o.propertyName);
					// logger.info("OK, found "+getBindingAttribute()+" of "+getOwner()+" depends of "+param+" , "+c);
					try {
						node.declareDependantOf(c, param, param);
					} catch (DependencyLoopException e) {
						logger.warning("DependancyLoopException raised while declaring dependancy (data lookup)"
								+ "in the context of binding: " + binding.toString() + " node: " + node + " dependancy: " + c
								+ " message: " + e.getMessage());
					}
				}
			}
		}

	}

	@Override
	public List<ConstraintDependency> getDependancies() {
		return dependancies;
	}

	@Override
	public List<ConstraintDependency> getAlterings() {
		return alterings;
	}

	@Override
	public void declareDependantOf(DrawingTreeNode<?, ?> aNode, GRParameter requiringParameter, GRParameter requiredParameter)
			throws DependencyLoopException {
		// logger.info("Component "+this+" depends of "+aComponent);
		if (aNode == this) {
			logger.warning("Forbidden reflexive dependancies");
			return;
		}
		// Look if this dependancy may cause a loop in dependancies
		try {
			List<DrawingTreeNode<?, ?>> actualDependancies = new Vector<DrawingTreeNode<?, ?>>();
			actualDependancies.add(aNode);
			searchLoopInDependenciesWith(aNode, actualDependancies);
		} catch (DependencyLoopException e) {
			logger.warning("Forbidden loop in dependancies: " + e.getMessage());
			throw e;
		}

		ConstraintDependency newDependancy = new ConstraintDependency(this, requiringParameter, aNode, requiredParameter);

		if (!dependancies.contains(newDependancy)) {
			dependancies.add(newDependancy);
			logger.info("Parameter " + requiringParameter + " of GR " + this + " depends of parameter " + requiredParameter + " of GR "
					+ aNode);
		}
		if (!((DrawingTreeNodeImpl<?, ?>) aNode).alterings.contains(newDependancy)) {
			((DrawingTreeNodeImpl<?, ?>) aNode).alterings.add(newDependancy);
		}
	}

	private void searchLoopInDependenciesWith(DrawingTreeNode<?, ?> aNode, List<DrawingTreeNode<?, ?>> actualDependancies)
			throws DependencyLoopException {
		for (ConstraintDependency dependancy : ((DrawingTreeNodeImpl<?, ?>) aNode).dependancies) {
			DrawingTreeNode<?, ?> c = dependancy.requiredGR;
			if (c == this) {
				throw new DependencyLoopException(actualDependancies);
			}
			Vector<DrawingTreeNode<?, ?>> newVector = new Vector<DrawingTreeNode<?, ?>>();
			newVector.addAll(actualDependancies);
			newVector.add(c);
			searchLoopInDependenciesWith(c, newVector);
		}
	}

	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	@Override
	public void update(Observable observable, Object notification) {

		if (notification instanceof FGENotification && observable == getGraphicalRepresentation()) {
			// Those notifications are forwarded by my graphical representation
			FGENotification notif = (FGENotification) notification;

			if (notif instanceof BindingChanged) {
				updateDependanciesForBinding(((BindingChanged) notif).getBinding());
			}

			/*if (notif.getParameter() == Parameters.text) {
				checkAndUpdateDimensionBoundsIfRequired();
			}*/
		}

		if (observable instanceof TextStyle) {
			notifyAttributeChanged(Parameters.textStyle, null, getGraphicalRepresentation().getTextStyle());
		}
	}

	public void notifyAttributeChanged(GRParameter parameter, Object oldValue, Object newValue) {
		propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGENotification(parameter, oldValue, newValue));
	}

	protected void propagateConstraintsAfterModification(GRParameter parameter) {
		for (ConstraintDependency dependency : alterings) {
			if (dependency.requiredParameter == parameter) {
				((DrawingTreeNodeImpl<?, ?>) dependency.requiringGR).computeNewConstraint(dependency);
			}
		}
	}

	protected void computeNewConstraint(ConstraintDependency dependency) {
		// None known at this level
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

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
	}

	/**
	 * Return the index of this drawing tree node relatively to all children declared in parent node
	 * 
	 * @return
	 */
	@Override
	public int getIndex() {
		if (!isValidated()) {
			return -1;
		}
		if (getParentNode() == null) {
			return -1;
		}

		List<DrawingTreeNodeImpl<?, ?>> orderedGRList = getParentNode().getChildNodes();
		return orderedGRList.indexOf(this);
	}

	/**
	 * Return flag indicating if this node should be displayed, relatively to the value returned by visible feature in
	 * {@link GraphicalRepresentation}, and the structure of the tree (the parent should be visible too)
	 */
	@Override
	public boolean shouldBeDisplayed() {
		if (!isValidated()) {
			return false;
		}
		// logger.info("For " + this + " getIsVisible()=" + getGraphicalRepresentation().getIsVisible() + " getParentNode()="
		// + getParentNode());
		return getGraphicalRepresentation().getIsVisible() && getParentNode() != null && getParentNode().shouldBeDisplayed();
	}

	@Override
	public void notifyLabelWillBeEdited() {
		setChanged();
		notifyObservers(new LabelWillEdit());
	}

	@Override
	public void notifyLabelHasBeenEdited() {
		setChanged();
		notifyObservers(new LabelHasEdited());
	}

	@Override
	public void notifyLabelWillMove() {
		setChanged();
		notifyObservers(new LabelWillMove());
	}

	@Override
	public void notifyLabelHasMoved() {
		setChanged();
		notifyObservers(new LabelHasMoved());
	}

	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		// setRegistered(false);
		/*if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;*/
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		// setRegistered(true);
		/*if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;*/
	}

}