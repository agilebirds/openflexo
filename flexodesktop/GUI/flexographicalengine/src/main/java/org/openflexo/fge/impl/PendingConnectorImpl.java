package org.openflexo.fge.impl;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNodeIdentifier;
import org.openflexo.fge.Drawing.PendingConnector;

public class PendingConnectorImpl<O> implements PendingConnector<O> {
	private ConnectorNodeImpl<O> connectorNode;
	private DrawingTreeNodeIdentifier<?> parentNodeIdentifier;
	private DrawingTreeNodeIdentifier<?> startNodeIdentifier;
	private DrawingTreeNodeIdentifier<?> endNodeIdentifier;

	public PendingConnectorImpl(ConnectorNode<O> connectorNode, DrawingTreeNodeIdentifier<?> parentNodeIdentifier,
			DrawingTreeNodeIdentifier<?> startNodeIdentifier, DrawingTreeNodeIdentifier<?> endNodeIdentifier) {
		super();
		this.connectorNode = (ConnectorNodeImpl<O>) connectorNode;
		this.parentNodeIdentifier = parentNodeIdentifier;
		this.startNodeIdentifier = startNodeIdentifier;
		this.endNodeIdentifier = endNodeIdentifier;
	}

	@Override
	public ConnectorNode<O> getConnectorNode() {
		return connectorNode;
	}

	@Override
	public DrawingTreeNodeIdentifier<?> getParentNodeIdentifier() {
		return parentNodeIdentifier;
	}

	@Override
	public DrawingTreeNodeIdentifier<?> getStartNodeIdentifier() {
		return startNodeIdentifier;
	}

	@Override
	public DrawingTreeNodeIdentifier<?> getEndNodeIdentifier() {
		return endNodeIdentifier;
	}

	@Override
	public boolean tryToResolve(Drawing<?> drawing) {
		ContainerNodeImpl<?, ?> parentNode = (ContainerNodeImpl<?, ?>) drawing.getDrawingTreeNode(parentNodeIdentifier);
		ShapeNodeImpl<?> startNode = (ShapeNodeImpl<?>) drawing.getDrawingTreeNode(startNodeIdentifier);
		ShapeNodeImpl<?> endNode = (ShapeNodeImpl<?>) drawing.getDrawingTreeNode(endNodeIdentifier);
		if (parentNode != null && startNode != null && endNode != null) {
			parentNode.addChild(connectorNode);
			connectorNode.setStartNode(startNode);
			connectorNode.setEndNode(endNode);
			return true;
		}
		return false;
	}

}