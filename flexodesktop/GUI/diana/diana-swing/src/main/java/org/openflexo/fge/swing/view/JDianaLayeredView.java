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
package org.openflexo.fge.swing.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.fge.notifications.NodeRemoved;
import org.openflexo.fge.swing.paint.FGEPaintManager;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;

@SuppressWarnings("serial")
public abstract class JDianaLayeredView<O> extends JLayeredPane implements FGEContainerView<O, JLayeredPane>, JFGEView<O, JLayeredPane> {

	private static final Logger logger = Logger.getLogger(JDianaLayeredView.class.getPackage().getName());

	private List<FGEView<?, ? extends JComponent>> childViews;

	public JDianaLayeredView() {
		super();
		childViews = new ArrayList<FGEView<?, ? extends JComponent>>();
	}

	@Override
	public abstract JDrawingView<?> getDrawingView();

	/**
	 * Sets the layer attribute on the specified component, making it the bottommost component in that layer. Should be called before adding
	 * to parent.
	 * 
	 * @param c
	 *            the Component to set the layer for
	 * @param layer
	 *            an int specifying the layer to set, where lower numbers are closer to the bottom
	 */
	public void setLayer(FGEView<?, ?> c, int layer) {
		setLayer((Component) c, layer, -1);
	}

	/**
	 * Returns the layer attribute for the specified Component.
	 * 
	 * @param c
	 *            the Component to check
	 * @return an int specifying the component's current layer
	 */
	public int getLayer(FGEView<?, ?> c) {
		return super.getLayer((Component) c);
	}

	/**
	 * Moves the component to the top of the components in its current layer (position 0).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toFront(FGEView<?, ?> c) {
		super.moveToFront((Component) c);
	}

	/**
	 * Moves the component to the bottom of the components in its current layer (position -1).
	 * 
	 * @param c
	 *            the Component to move
	 * @see #setPosition(Component, int)
	 */
	public void toBack(FGEView<?, ?> c) {
		super.moveToBack((Component) c);
	}

	public List<FGEView<?, ?>> getViewsInLayer(int layer) {
		List<FGEView<?, ?>> returned = new ArrayList<FGEView<?, ?>>();
		for (Component c : super.getComponentsInLayer(layer)) {
			if (c instanceof FGEView) {
				returned.add((FGEView<?, ?>) c);
			}
		}
		return returned;
	}

	@Override
	public void addView(FGEView<?, ?> view) {
		logger.info("add view " + view + " under " + this);
		if (view instanceof JShapeView) {
			((JShapeView<?>) view).setBackground(getBackground());
			// logger.info("add the label view " + ((JShapeView<?>) view).getLabelView());
			if (((JShapeView<?>) view).getLabelView() != null) {
				add(((JShapeView<?>) view).getLabelView(), ((JShapeView<?>) view).getLayer(), -1);
			}
			// logger.info("add the view");
			add(((JShapeView<?>) view), ((JShapeView<?>) view).getLayer(), -1);
			childViews.add((JShapeView<?>) view);
		} else if (view instanceof JConnectorView) {
			((JConnectorView<?>) view).setBackground(getBackground());
			if (((JConnectorView<?>) view).getLabelView() != null) {
				add(((JConnectorView<?>) view).getLabelView(), ((JConnectorView<?>) view).getLayer(), -1);
			}
			add(((JConnectorView<?>) view), ((JConnectorView<?>) view).getLayer(), -1);
			childViews.add((JConnectorView<?>) view);
		}
	}

	public void removeView(FGEView<?, ?> view) {
		logger.info("remove view " + view + " from " + this);
		if (view instanceof JShapeView) {
			if (((JShapeView<?>) view).getLabelView() != null) {
				remove(((JShapeView<?>) view).getLabelView());
			}
			remove(((JShapeView<?>) view));
			childViews.remove((JShapeView<?>) view);
		} else if (view instanceof JConnectorView) {
			if (((JConnectorView<?>) view).getLabelView() != null) {
				remove(((JConnectorView<?>) view).getLabelView());
			}
			remove(((JConnectorView<?>) view));
			childViews.remove((JConnectorView<?>) view);
		}
	}

	protected void handleNodeAdded(NodeAdded notification) {
		DrawingTreeNode<?, ?> newNode = notification.getAddedNode();
		logger.fine("JShapeView: Received NodeAdded notification, creating view for " + newNode);
		if (newNode instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) newNode;
			JShapeView<?> shapeView = (JShapeView<?>) getController().makeShapeView(shapeNode);
			addView(shapeView);
			revalidate();
			getPaintManager().invalidate(notification.getParent());
			getPaintManager().repaint(this);
			shapeNode.notifyShapeNeedsToBeRedrawn(); // TODO: is this necessary ?
		} else if (newNode instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) newNode;
			JConnectorView<?> connectorView = (JConnectorView<?>) getController().makeConnectorView(connectorNode);
			addView(connectorView);
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
			JShapeView<?> view = (JShapeView<?>) getDrawingView().shapeViewForNode(removedShapeNode);
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
			JConnectorView<?> view = getDrawingView().connectorViewForNode(removedConnectorNode);
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
			if (getController() instanceof DianaInteractiveViewer) {
				if (getNode() != null && ((DianaInteractiveViewer<?, ?, ?>) getController()).getFocusedObjects().contains(getNode())) {
					((DianaInteractiveViewer<?, ?, ?>) getController()).removeFromFocusedObjects(getNode());
				}
				if (getNode() != null && ((DianaInteractiveViewer<?, ?, ?>) getController()).getSelectedObjects().contains(getNode())) {
					((DianaInteractiveViewer<?, ?, ?>) getController()).removeFromSelectedObjects(getNode());
				}
			}
			// Now delete the view
			delete();
		}
	}

	@Override
	public AbstractDianaEditor<?, ?, ? super JLayeredPane> getController() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FGEView<?, ? extends JComponent>> getChildViews() {
		return childViews;
	}

	public FGEPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

}
