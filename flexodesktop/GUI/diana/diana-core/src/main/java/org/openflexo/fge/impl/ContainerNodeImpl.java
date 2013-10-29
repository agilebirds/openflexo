package org.openflexo.fge.impl;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeRemoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillResize;

public abstract class ContainerNodeImpl<O, GR extends ContainerGraphicalRepresentation> extends DrawingTreeNodeImpl<O, GR> implements
		ContainerNode<O, GR> {

	private static final Logger logger = Logger.getLogger(ContainerNodeImpl.class.getPackage().getName());

	private List<DrawingTreeNodeImpl<?, ?>> childNodes;

	private boolean isResizing = false;
	private boolean isCheckingDimensionConstraints = false;

	protected ContainerNodeImpl(DrawingImpl<?> drawing, O drawable, GRBinding<O, GR> grBinding, ContainerNodeImpl<?, ?> parentNode) {
		super(drawing, drawable, grBinding, parentNode);
		childNodes = new ArrayList<DrawingTreeNodeImpl<?, ?>>();
	}

	@Override
	public ShapeNode<?> getTopLevelShapeGraphicalRepresentation(FGEPoint p) {
		return getTopLevelShapeGraphicalRepresentation(this, p);
	}

	private ShapeNode<?> getTopLevelShapeGraphicalRepresentation(ContainerNode<?, ?> container, FGEPoint p) {

		List<ShapeNode<?>> enclosingShapes = new ArrayList<ShapeNode<?>>();

		for (DrawingTreeNode<?, ?> dtn : container.getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeNode<?> child = (ShapeNode<?>) dtn;
				if (child.getShape().getShape().containsPoint(FGEUtils.convertNormalizedPoint(this, p, child))) {
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

	@Override
	public List<DrawingTreeNodeImpl<?, ?>> getChildNodes() {
		return childNodes;
	}

	@Override
	public void addChild(DrawingTreeNode<?, ?> aChildNode) {
		if (aChildNode == null) {
			logger.warning("Cannot add null node");
			return;
		}
		if (childNodes.contains(aChildNode)) {
			logger.warning("Node already present");
		} else {
			System.out.println("Add child " + aChildNode + " as child as " + this);
			((DrawingTreeNodeImpl<?, ?>) aChildNode).setParentNode(this);
			childNodes.add((DrawingTreeNodeImpl<?, ?>) aChildNode);
		}
	}

	@Override
	public void removeChild(DrawingTreeNode<?, ?> aChildNode) {
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
	public void invalidate() {
		super.invalidate();
		for (DrawingTreeNode<?, ?> dtn : childNodes) {
			dtn.invalidate();
		}
	}

	/**
	 * Recursively delete this DrawingTreeNode and all its descendants
	 */
	@Override
	public boolean delete() {
		if (!isDeleted()) {
			if (childNodes != null) {
				for (DrawingTreeNode<?, ?> n : new ArrayList<DrawingTreeNode<?, ?>>(childNodes)) {
					removeChild(n);
				}
				childNodes.clear();
			}

			childNodes = null;

			return super.delete();
		}
		return false;
	}

	@Override
	public void notifyNodeAdded(DrawingTreeNode<?, ?> addedNode) {
		if (addedNode.getGraphicalRepresentation() != null) {
			addedNode.getGraphicalRepresentation().updateBindingModel();
		}
		setChanged();
		notifyObservers(new NodeAdded(addedNode, this));
	}

	@Override
	public void notifyNodeRemoved(DrawingTreeNode<?, ?> removedNode) {
		if (removedNode.getGraphicalRepresentation() != null) {
			removedNode.getGraphicalRepresentation().updateBindingModel();
		}
		setChanged();
		notifyObservers(new NodeRemoved(removedNode, this));
	}

	@Override
	public <O2> boolean hasShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable) {
		return getShapeFor(binding, aDrawable) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O2> ShapeNode<O2> getShapeFor(ShapeGRBinding<O2> binding, O2 aDrawable) {
		for (DrawingTreeNode<?, ?> child : childNodes) {
			if (child instanceof ShapeNode && child.getGRBinding() == binding && child.getDrawable() == aDrawable) {
				return (ShapeNode<O2>) child;
			}
		}
		return null;
	}

	@Override
	public <O2> boolean hasConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable) {
		return getConnectorFor(binding, aDrawable) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O2> ConnectorNode<O2> getConnectorFor(ConnectorGRBinding<O2> binding, O2 aDrawable) {
		for (DrawingTreeNode<?, ?> child : childNodes) {
			if (child instanceof ConnectorNode && child.getGRBinding() == binding && child.getDrawable() == aDrawable) {
				return (ConnectorNode<O2>) child;
			}
		}
		return null;
	}

	// ********************************************
	// Size management
	// ********************************************

	@Override
	public double getWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.WIDTH);
	}

	@Override
	public final void setWidth(double aValue) {
		if (aValue != getWidth()) {
			FGEDimension newDimension = new FGEDimension(aValue, getHeight());
			updateSize(newDimension);
		}
	}

	protected void setWidthNoNotification(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.WIDTH, aValue);
	}

	@Override
	public double getHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.HEIGHT);
	}

	@Override
	public final void setHeight(double aValue) {
		if (aValue != getHeight()) {
			FGEDimension newDimension = new FGEDimension(getWidth(), aValue);
			updateSize(newDimension);
		}
	}

	public void setHeightNoNotification(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.HEIGHT, aValue);
	}

	@Override
	public FGEDimension getSize() {
		return new FGEDimension(getWidth(), getHeight());
	}

	@Override
	public void setSize(FGEDimension newSize) {
		updateSize(newSize);
	}

	public double getMinimalWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.MINIMAL_WIDTH);
	}

	protected void setMinimalWidth(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MINIMAL_WIDTH, aValue);
	}

	public double getMaximalWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_WIDTH);
	}

	protected void setMaximalWidth(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_WIDTH, aValue);
	}

	public double getMinimalHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.MINIMAL_HEIGHT);
	}

	protected void setMinimalHeight(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MINIMAL_HEIGHT, aValue);
	}

	public double getMaximalHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_HEIGHT);
	}

	protected void setMaximalHeight(double aValue) {
		setPropertyValue(ContainerGraphicalRepresentation.MAXIMAL_HEIGHT, aValue);
	}

	protected boolean getAdjustMinimalWidthToLabelWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH);
	}

	protected void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH, adjustMinimalWidthToLabelWidth);
	}

	protected boolean getAdjustMinimalHeightToLabelHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT);
	}

	protected void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT, adjustMinimalHeightToLabelHeight);
	}

	protected boolean getAdjustMaximalWidthToLabelWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH);
	}

	protected void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH, adjustMaximalWidthToLabelWidth);
	}

	protected boolean getAdjustMaximalHeightToLabelHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT);
	}

	protected void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight) {
		setPropertyValue(ContainerGraphicalRepresentation.ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT, adjustMaximalHeightToLabelHeight);
	}

	protected FGESteppedDimensionConstraint getDimensionConstraintStep() {
		return getPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINT_STEP);
	}

	protected void setDimensionConstraintStep(FGESteppedDimensionConstraint dimensionConstraintStep) {
		setPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINT_STEP, dimensionConstraintStep);
	}

	protected DimensionConstraints getDimensionConstraints() {
		return getPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINTS);
	}

	protected void setDimensionConstraints(DimensionConstraints dimensionConstraints) {
		setPropertyValue(ContainerGraphicalRepresentation.DIMENSION_CONSTRAINTS, dimensionConstraints);
	}

	/**
	 * General method called to update size of a ContainerNode
	 * 
	 * @param requestedSize
	 */
	private void updateSize(FGEDimension requestedSize) {

		// If no value supplied, just ignore
		if (requestedSize == null) {
			return;
		}

		// If value is same, also ignore
		if (requestedSize.equals(getSize())) {
			return;
		}

		// Prelude of update, first select new size respecting contextual constraints
		FGEDimension newSize = getConstrainedSize(requestedSize);
		if (!newSize.equals(requestedSize)) {
			logger.info("Dimension constraints force " + requestedSize + " to be " + newSize);
		}

		FGEDimension oldSize = getSize();
		if (!newSize.equals(oldSize)) {
			double oldWidth = getWidth();
			double oldHeight = getHeight();
			/*if (isParentLayoutedAsContainer()) {
				setLocationForContainerLayout(newLocation);
			} else {*/
			setWidthNoNotification(newSize.width);
			setHeightNoNotification(newSize.height);
			// }
			notifyObjectResized(oldSize);
			notifyAttributeChanged(ContainerGraphicalRepresentation.WIDTH, oldWidth, getWidth());
			notifyAttributeChanged(ContainerGraphicalRepresentation.HEIGHT, oldHeight, getHeight());
			/*if (!isFullyContainedInContainer()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("setLocation() lead shape going outside it's parent view");
				}
			}*/
		}

		/*if (newSize == null) {
			return;
		}
		// Preventing size from being negative or equals to 0
		if (newSize.width <= 0) {
			newSize.width = FGEGeometricObject.EPSILON;
		}
		if (newSize.height <= 0) {
			newSize.height = FGEGeometricObject.EPSILON;
		}
		FGEDimension oldSize = getSize();
		if (!newSize.equals(oldSize)) {
			double oldWidth = getWidth();
			double oldHeight = getHeight();
			setWidthNoNotification(newSize.width);
			setHeightNoNotification(newSize.height);
			if (hasFloatingLabel()) {
				if (getGraphicalRepresentation().getAbsoluteTextX() >= 0) {
					if (getGraphicalRepresentation().getAbsoluteTextX() < getWidth()) {
						getGraphicalRepresentation().setAbsoluteTextX(
								getGraphicalRepresentation().getAbsoluteTextX() / oldSize.width * getWidth());
					} else {
						getGraphicalRepresentation().setAbsoluteTextX(
								getGraphicalRepresentation().getAbsoluteTextX() + getWidth() - oldSize.width);
					}
				}
				if (getGraphicalRepresentation().getAbsoluteTextY() >= 0) {
					if (getGraphicalRepresentation().getAbsoluteTextY() < getHeight()) {
						getGraphicalRepresentation().setAbsoluteTextY(
								getGraphicalRepresentation().getAbsoluteTextY() / oldSize.height * getHeight());
					} else {
						getGraphicalRepresentation().setAbsoluteTextY(
								getGraphicalRepresentation().getAbsoluteTextY() + getHeight() - oldSize.height);
					}
				}
			}
			checkAndUpdateDimensionBoundsIfRequired();
			if (isParentLayoutedAsContainer()) {
				((ShapeNodeImpl<?>) getParentNode()).checkAndUpdateDimensionIfRequired();
			}
			notifyObjectResized(oldSize);
			notifyAttributeChanged(ContainerGraphicalRepresentation.WIDTH, oldWidth, getWidth());
			notifyAttributeChanged(ContainerGraphicalRepresentation.HEIGHT, oldHeight, getHeight());
			// getGraphicalRepresentation().getShape().notifyObjectResized();
		}*/
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized() {
		notifyObjectResized(null);
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(FGEDimension oldSize) {
		setChanged();
		notifyObservers(new ObjectResized(oldSize, getSize()));
	}

	/**
	 * Notify that the object will be resized
	 */
	@Override
	public void notifyObjectWillResize() {
		isResizing = true;
		setChanged();
		notifyObservers(new ObjectWillResize());
	}

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	@Override
	public void notifyObjectHasResized() {
		isResizing = false;
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNodeImpl) {
				((ShapeNodeImpl<?>) gr).checkAndUpdateLocationIfRequired();
			}
		}
		setChanged();
		notifyObservers(new ObjectHasResized());
	}

	@Override
	public boolean isResizing() {
		return isResizing;
	}

	/**
	 * Calling this method forces FGE to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	protected void checkAndUpdateDimensionIfRequired() {
		setSize(getSize());
	}

	private FGEDimension getConstrainedSize(FGEDimension requestedSize) {

		if (isCheckingDimensionConstraints || labelMetricsProvider == null) {
			return requestedSize;
		}

		try {

			// if (getAdaptBoundsToContents()) {
			// extendBoundsToHostContents();
			// }

			isCheckingDimensionConstraints = true;

			// FGERectangle requiredBounds = getRequiredBoundsForContents();

			FGEDimension newDimension = new FGEDimension(requestedSize.getWidth(), requestedSize.getHeight());

			// Preventing size from being negative or equals to 0
			if (newDimension.width <= 0) {
				newDimension.width = FGEGeometricObject.EPSILON;
			}
			if (newDimension.height <= 0) {
				newDimension.height = FGEGeometricObject.EPSILON;
			}

			// double minWidth = (getAdaptBoundsToContents() ? Math.max(getMinimalWidth(), requiredBounds.width) : getMinimalWidth());
			// double minHeight = (getAdaptBoundsToContents() ? Math.max(getMinimalHeight(), requiredBounds.height) : getMinimalHeight());
			double minWidth = getMinimalWidth();
			double minHeight = getMinimalHeight();
			double maxWidth = getMaximalWidth();
			double maxHeight = getMaximalHeight();

			if (hasContainedLabel()) {
				Dimension normalizedLabelSize = getNormalizedLabelSize();
				int labelWidth = normalizedLabelSize.width;
				int labelHeight = normalizedLabelSize.height;
				FGEDimension requiredLabelDimension = getRequiredLabelSize();
				double rh = requiredLabelDimension.getHeight();
				double rw = requiredLabelDimension.getWidth();
				double requiredWidth = Math.max(rw, labelWidth);
				double requiredHeight = Math.max(rh, labelHeight);

				if (getAdjustMinimalWidthToLabelWidth()) {
					minWidth = Math.max(requiredWidth, minWidth);
				}

				if (getGraphicalRepresentation().getAdjustMinimalHeightToLabelHeight()) {
					minHeight = Math.max(requiredHeight, minHeight);
				}

				if (getGraphicalRepresentation().getAdjustMaximalWidthToLabelWidth()) {
					maxWidth = Math.min(requiredWidth, maxWidth);
				}

				if (getGraphicalRepresentation().getAdjustMaximalHeightToLabelHeight()) {
					maxHeight = Math.min(requiredHeight, maxHeight);
				}
			}

			if (minWidth > maxWidth) {
				logger.warning("Minimal width > maximal width, cannot proceed");
			} else {
				if (newDimension.width < minWidth) {
					newDimension.width = minWidth;
				}
				if (newDimension.width > maxWidth) {
					newDimension.width = maxWidth;
				}
			}
			if (minHeight > maxHeight) {
				logger.warning("Minimal height > maximal height, cannot proceed");
			} else {
				if (newDimension.height < minHeight) {
					newDimension.height = minHeight;
				}
				if (newDimension.height > maxHeight) {
					newDimension.height = maxHeight;
				}
			}

			boolean useStepDimensionConstraints = getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED
					&& getDimensionConstraintStep() != null;

			if (useStepDimensionConstraints && hasContainedLabel()) {
				if (getAdjustMinimalWidthToLabelWidth() && getAdjustMaximalWidthToLabelWidth()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on width! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
				if (getAdjustMinimalHeightToLabelHeight() && getAdjustMaximalHeightToLabelHeight()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on height! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
			}

			if (useStepDimensionConstraints) {
				FGEDimension d = getDimensionConstraintStep().getNearestDimension(newDimension, minWidth, maxWidth, minHeight, maxHeight);
				newDimension.width = d.width;
				newDimension.height = d.height;
			}

			return newDimension;

		} finally {
			isCheckingDimensionConstraints = false;
		}
	}

	@Override
	public Dimension getNormalizedLabelSize() {
		if (labelMetricsProvider != null) {
			return labelMetricsProvider.getScaledPreferredDimension(1.0);
		} else {
			return new Dimension(0, 0);
		}
	}

	@Override
	public Rectangle getNormalizedLabelBounds() {
		Dimension normalizedLabelSize = getNormalizedLabelSize();
		Rectangle r = new Rectangle(getLabelLocation(1.0), normalizedLabelSize);
		return r;
	}

	@Override
	public FGERectangle getRequiredBoundsForContents() {
		FGERectangle requiredBounds = null;
		if (getChildNodes() == null) {
			return new FGERectangle(getGraphicalRepresentation().getMinimalWidth() / 2,
					getGraphicalRepresentation().getMinimalHeight() / 2, getGraphicalRepresentation().getMinimalWidth(),
					getGraphicalRepresentation().getMinimalHeight());
		}
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNodeImpl<?> shapeGR = (ShapeNodeImpl<?>) gr;
				FGERectangle bounds = shapeGR.getBoundsNoBorder();
				if (shapeGR.hasText()) {
					Rectangle labelBounds = shapeGR.getNormalizedLabelBounds(); // getLabelBounds((new JLabel()), 1.0);
					FGERectangle labelBounds2 = new FGERectangle(labelBounds.x, labelBounds.y, labelBounds.width, labelBounds.height);
					bounds = bounds.rectangleUnion(labelBounds2);
				}

				if (requiredBounds == null) {
					requiredBounds = bounds;
				} else {
					requiredBounds = requiredBounds.rectangleUnion(bounds);
				}
			}
		}
		if (requiredBounds == null) {
			requiredBounds = new FGERectangle(getGraphicalRepresentation().getMinimalWidth() / 2, getGraphicalRepresentation()
					.getMinimalHeight() / 2, getGraphicalRepresentation().getMinimalWidth(), getGraphicalRepresentation()
					.getMinimalHeight());
		} else {
			if (requiredBounds.width < getGraphicalRepresentation().getMinimalWidth()) {
				requiredBounds.x = requiredBounds.x - (int) ((getGraphicalRepresentation().getMinimalWidth() - requiredBounds.width) / 2.0);
				requiredBounds.width = getGraphicalRepresentation().getMinimalWidth();
			}
			if (requiredBounds.height < getGraphicalRepresentation().getMinimalHeight()) {
				requiredBounds.y = requiredBounds.y
						- (int) ((getGraphicalRepresentation().getMinimalHeight() - requiredBounds.height) / 2.0);
				requiredBounds.height = getGraphicalRepresentation().getMinimalHeight();
			}
		}

		return requiredBounds;
	}

}
