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
package org.openflexo.fge.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JLayeredPane;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.fge.notifications.NodeRemoved;

@SuppressWarnings("serial")
public abstract class FGELayeredView<O> extends JLayeredPane implements FGEView<O> {

	private static final Logger logger = Logger.getLogger(FGELayeredView.class.getPackage().getName());

	@Override
	public abstract DrawingView<?> getDrawingView();

	/**
	 * Sets the layer attribute on the specified component, making it the bottommost component in that layer. Should be called before adding
	 * to parent.
	 * 
	 * @param c
	 *            the Component to set the layer for
	 * @param layer
	 *            an int specifying the layer to set, where lower numbers are closer to the bottom
	 */
	public void setLayer(FGEView<?> c, int layer) {
		setLayer((Component) c, layer, -1);
	}

	/**
	 * Returns the layer attribute for the specified Component.
	 * 
	 * @param c
	 *            the Component to check
	 * @return an int specifying the component's current layer
	 */
	public int getLayer(FGEView<?> c) {
		return super.getLayer((Component) c);
	}

	/**
	 * Moves the component to the top of the components in its current layer (position 0).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toFront(FGEView<?> c) {
		super.moveToFront((Component) c);
	}

	/**
	 * Moves the component to the bottom of the components in its current layer (position -1).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toBack(FGEView<?> c) {
		super.moveToBack((Component) c);
	}

	public List<FGEView<?>> getViewsInLayer(int layer) {
		List<FGEView<?>> returned = new ArrayList<FGEView<?>>();
		for (Component c : super.getComponentsInLayer(layer)) {
			if (c instanceof FGEView) {
				returned.add((FGEView<?>) c);
			}
		}
		return returned;
	}

	public void add(ShapeView<?> view) {
		logger.info("add view " + view + " under " + this);
		view.setBackground(getBackground());
		if (view.getLabelView() != null) {
			add(view.getLabelView(), view.getLayer(), -1);
		}
		add(view, view.getLayer(), -1);
		/*if (getDrawingView() != null) {
			getDrawingView().getContents().put(view.getNode(), view);
		}*/
	}

	public void remove(ShapeView<?> view) {
		if (view.getLabelView() != null) {
			remove(view.getLabelView());
		}
		remove((Component) view);
		/*if (getDrawingView() != null) {
			getDrawingView().getContents().remove(view.getNode());
		}*/
	}

	public void add(ConnectorView<O> view) {
		view.setBackground(getBackground());
		if (view.getLabelView() != null) {
			add(view.getLabelView(), view.getLayer(), -1);
		}
		add(view, view.getLayer(), -1);
		/*if (getDrawingView() != null) {
			getDrawingView().getContents().put(view.getNode(), view);
		}*/
	}

	public void remove(ConnectorView<O> view) {
		if (view.getLabelView() != null) {
			remove(view.getLabelView());
		}
		remove((Component) view);
		/*if (getDrawingView() != null) {
			getDrawingView().getContents().remove(view.getNode());
		}*/
	}

	protected void handleNodeAdded(NodeAdded notification) {
		DrawingTreeNode<?, ?> newNode = notification.getAddedNode();
		logger.fine("ShapeView: Received NodeAdded notification, creating view for " + newNode);
		if (newNode instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) newNode;
			ShapeView<?> shapeView = getController().makeShapeView(shapeNode);
			add(shapeView);
			revalidate();
			getPaintManager().invalidate(notification.getParent());
			getPaintManager().repaint(this);
			shapeNode.notifyShapeNeedsToBeRedrawn(); // TODO: is this necessary ?
		} else if (newNode instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) newNode;
			ConnectorView<?> connectorView = getController().makeConnectorView(connectorNode);
			add(connectorView);
			revalidate();
			getPaintManager().invalidate(notification.getParent());
			getPaintManager().repaint(this);
		} else if (newNode instanceof GeometricNode) {
			newNode.addObserver(this);
			revalidate();
			getPaintManager().invalidate(notification.getParent());
			getPaintManager().repaint(this);
		}
	}

	protected void handleNodeRemoved(NodeRemoved notification) {
		DrawingTreeNode<?, ?> removedNode = notification.getRemovedNode();
		if (removedNode instanceof ShapeNode) {
			ShapeNode<?> removedShapeNode = (ShapeNode<?>) removedNode;
			ShapeView<?> view = getDrawingView().shapeViewForNode(removedShapeNode);
			if (view != null) {
				remove(view);
				revalidate();
				getPaintManager().invalidate(notification.getParent());
				getPaintManager().invalidate(removedShapeNode);
				getPaintManager().repaint(this);
			} else {
				logger.warning("Cannot find view for " + removedShapeNode);
			}
		} else if (removedNode instanceof ConnectorNode) {
			ConnectorNode<?> removedConnectorNode = (ConnectorNode<?>) removedNode;
			ConnectorView<?> view = getDrawingView().connectorViewForNode(removedConnectorNode);
			if (view != null) {
				remove(view);
				revalidate();
				getPaintManager().invalidate(notification.getParent());
				getPaintManager().invalidate(removedConnectorNode);
				getPaintManager().repaint(this);
			} else {
				logger.warning("Cannot find view for " + removedConnectorNode);
			}
		} else if (removedNode instanceof GeometricNode) {
			removedNode.deleteObserver(this);
			revalidate();
			getPaintManager().repaint(this);
		}
	}

	protected void handleNodeDeleted(NodeDeleted notification) {
		DrawingTreeNode<?, ?> deletedNode = notification.getDeletedNode();
		if (deletedNode == getNode()) {
			// If was not removed, try to do it now
			// TODO: is this necessary ???
			if (deletedNode != null && deletedNode.getParentNode() != null
					&& deletedNode.getParentNode().getChildNodes().contains(deletedNode)) {
				deletedNode.getParentNode().removeChild(deletedNode);
			}
			if (getNode() != null && getController().getFocusedObjects().contains(getNode())) {
				getController().removeFromFocusedObjects(getNode());
			}
			if (getNode() != null && getController().getSelectedObjects().contains(getNode())) {
				getController().removeFromSelectedObjects(getNode());
			}
			// Now delete the view
			delete();
		}
	}

}
