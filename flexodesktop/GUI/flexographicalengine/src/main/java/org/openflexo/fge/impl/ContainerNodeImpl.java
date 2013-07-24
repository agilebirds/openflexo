package org.openflexo.fge.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeRemoved;

public abstract class ContainerNodeImpl<O, GR extends ContainerGraphicalRepresentation> extends DrawingTreeNodeImpl<O, GR> implements
		ContainerNode<O, GR> {

	private static final Logger logger = Logger.getLogger(ContainerNodeImpl.class.getPackage().getName());

	private List<DrawingTreeNodeImpl<?, ?>> childNodes;

	public ContainerNodeImpl(DrawingImpl<?> drawing, O drawable, GRBinding<O, GR> grBinding, ContainerNodeImpl<?, ?> parentNode) {
		super(drawing, drawable, grBinding, parentNode);
		childNodes = new ArrayList<DrawingTreeNodeImpl<?, ?>>();
	}

	@Override
	public double getWidth() {
		return getGraphicalRepresentation().getWidth();
	}

	@Override
	public void setWidth(double aValue) {
		getGraphicalRepresentation().setWidth(aValue);
	}

	@Override
	public double getHeight() {
		return getGraphicalRepresentation().getHeight();
	}

	@Override
	public void setHeight(double aValue) {
		getGraphicalRepresentation().setHeight(aValue);
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
	public void addChild(DrawingTreeNodeImpl<?, ?> aChildNode) {
		if (aChildNode == null) {
			logger.warning("Cannot add null node");
			return;
		}
		if (childNodes.contains(aChildNode)) {
			logger.warning("Node already present");
		} else {
			aChildNode.setParentNode(this);
			childNodes.add(aChildNode);
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
	public void delete() {
		if (childNodes != null) {
			for (DrawingTreeNode<?, ?> n : new ArrayList<DrawingTreeNode<?, ?>>(childNodes)) {
				removeChild(n);
			}
			childNodes.clear();
		}

		childNodes = null;

		super.delete();
	}

	@Override
	public void notifyNodeAdded(DrawingTreeNode<?, ?> addedNode) {
		addedNode.getGraphicalRepresentation().updateBindingModel();
		setChanged();
		notifyObservers(new NodeAdded(addedNode, this));
	}

	@Override
	public void notifyNodeRemoved(DrawingTreeNode<?, ?> removedNode) {
		removedNode.getGraphicalRepresentation().updateBindingModel();
		setChanged();
		notifyObservers(new NodeRemoved(removedNode, this));
	}

}
