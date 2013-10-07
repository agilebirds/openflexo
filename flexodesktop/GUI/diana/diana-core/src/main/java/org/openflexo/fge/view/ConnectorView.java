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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.tools.DrawingPalette;
import org.openflexo.fge.notifications.ConnectorModified;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.view.listener.FGEViewMouseListener;

/**
 * The ConnectorView is the SWING implementation of a panel showing a {@link ConnectorNode}
 * 
 * @author sylvain
 * 
 * @param <O>
 */
@SuppressWarnings("serial")
public class ConnectorView<O> extends JPanel implements FGEView<O, JPanel> {

	private static final Logger logger = Logger.getLogger(ConnectorView.class.getPackage().getName());

	private ConnectorNode<O> connectorNode;
	private FGEViewMouseListener mouseListener;
	private AbstractDianaEditor<?, SwingFactory, JComponent> controller;

	private LabelView<O> labelView;

	public ConnectorView(ConnectorNode<O> node, AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		super();
		this.controller = controller;
		this.connectorNode = node;
		updateLabelView();
		relocateAndResizeView();
		mouseListener = controller.getDianaFactory().makeViewMouseListener(connectorNode, this, controller);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		connectorNode.addObserver(this);
		setOpaque(false);

		updateVisibility();

		if (getController() instanceof DianaInteractiveEditor) {
			if (((DianaInteractiveEditor<?, ?, JComponent>) controller).getPalettes() != null) {
				for (DrawingPalette p : ((DianaInteractiveEditor<?, ?, JComponent>) controller).getPalettes()) {
					registerPalette(p);
				}
			}
		}

		// setToolTipText(getClass().getSimpleName()+hashCode());

		// logger.info("Build ConnectorView for aGraphicalRepresentation="+aGraphicalRepresentation);
	}

	private boolean isDeleted = false;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public synchronized void delete() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete ConnectorView for " + connectorNode);
		}
		if (getParentView() != null) {
			FGELayeredView<?> parentView = getParentView();
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().invalidate(connectorNode);
				getPaintManager().repaint(parentView);
			}
		}
		if (connectorNode != null) {
			connectorNode.deleteObserver(this);
		}
		setDropTarget(null);
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
		if (labelView != null) {
			labelView.delete();
		}
		labelView = null;
		controller = null;
		mouseListener = null;
		connectorNode = null;
		isDeleted = true;
	}

	@Override
	public O getDrawable() {
		return connectorNode.getDrawable();
	}

	@Override
	public ConnectorNode<O> getNode() {
		return connectorNode;
	}

	@Override
	public DrawingView<?> getDrawingView() {
		return getController().getDrawingView();
	}

	@Override
	public FGELayeredView<?> getParent() {
		return (FGELayeredView<?>) super.getParent();
	}

	public FGELayeredView<?> getParentView() {
		return getParent();
	}

	/*public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return connectorNode.getGraphicalRepresentation();
	}*/

	@Override
	public double getScale() {
		return getController().getScale();
	}

	@Override
	public void rescale() {
		relocateAndResizeView();
	}

	private void relocateAndResizeView() {
		relocateView();
		resizeView();
	}

	private void relocateView() {
		/*logger.info("relocateView to ("
				+getGraphicalRepresentation().getViewX(getScale())+","+
				getGraphicalRepresentation().getViewY(getScale())+")");*/
		if (labelView != null) {
			labelView.updateBounds();
		}
		int newX, newY;
		newX = connectorNode.getViewX(getScale());
		newY = connectorNode.getViewY(getScale());
		if (newX != getX() || newY != getY()) {
			setLocation(newX, newY);
		}
	}

	private void resizeView() {
		/*logger.info("resizeView to ("
				+getGraphicalRepresentation().getViewWidth(getScale())+","+
				getGraphicalRepresentation().getViewHeight(getScale())+")");*/
		if (labelView != null) {
			labelView.updateBounds();
		}
		int newWidth, newHeight;
		newWidth = connectorNode.getViewWidth(getScale());
		newHeight = connectorNode.getViewHeight(getScale());
		if (newWidth != getWidth() || newHeight != getHeight()) {
			setSize(newWidth, newHeight);
			if (getDrawingView().isBuffering()) {
				/* Something very bad happened here:
				 * the view is resizing while drawing view is beeing buffered:
				 * all the things we were buffering may be wrong now, we have to
				 * start buffering again
				 */
				getDrawingView().startBufferingAgain();
			}
		}
	}

	/*private void updateLayer()
	{
		//logger.info("GR: "+getGraphicalRepresentation()+" update layer to "+getLayer());
		if (getParent() instanceof JLayeredPane) {
			if (labelView!=null)
				((JLayeredPane)getParent()).setLayer(labelView, getLayer());
			((JLayeredPane)getParent()).setLayer(this, getLayer());
		}
	}*/

	private void updateLayer() {
		if (getParent() != null) {
			if (labelView != null) {
				getParent().setLayer((Component) labelView, getLayer());
				getParent().setPosition(labelView, connectorNode.getIndex() * 2);
			}
			getParent().setLayer((Component) this, getLayer());
			getParent().setPosition(this, connectorNode.getIndex() * 2 + 1);
		}
	}

	private void updateVisibility() {
		if (labelView != null) {
			labelView.setVisible(connectorNode.shouldBeDisplayed());
		}
		setVisible(connectorNode.shouldBeDisplayed());
	}

	private void updateLabelView() {
		if (!connectorNode.hasText() && labelView != null) {
			labelView.delete();
			labelView = null;
		} else if (connectorNode.hasText() && labelView == null) {
			labelView = new LabelView<O>(getNode(), getController(), this);
			if (getParentView() != null) {
				getParentView().add(getLabelView());
			}
		}
	}

	public Integer getLayer() {
		return FGEConstants.INITIAL_LAYER + connectorNode.getGraphicalRepresentation().getLayer();
	}

	@Override
	public void paint(Graphics g) {
		if (isDeleted()) {
			return;
		}
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (getDrawingView().isBuffering()) {
				// Buffering painting
				if (getPaintManager().isTemporaryObject(connectorNode)) {
					// This object is declared to be a temporary object, to be redrawn
					// continuously, so we need to ignore it: do nothing
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("ConnectorView: buffering paint, ignore: " + connectorNode);
					}
				} else {
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("ConnectorView: buffering paint, draw: " + connectorNode + " clip="
								+ g.getClip());
					}
					connectorNode.paint(g, getController());
					super.paint(g);
				}
			} else {
				if (!getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), connectorNode, getScale())) {
					connectorNode.paint(g, getController());
					super.paint(g);
				}

				/*
				// Use buffer
				Image buffer = getPaintManager().getPaintBuffer();
				Rectangle localViewBounds = g.getClipBounds();
				Rectangle viewBoundsInDrawingView = GraphicalRepresentation.convertRectangle(getGraphicalRepresentation(), localViewBounds, getDrawingGraphicalRepresentation(), getScale());
				Point dp1 = localViewBounds.getLocation();
				Point dp2 = new Point(localViewBounds.x+localViewBounds.width-1,localViewBounds.y+localViewBounds.height-1);
				Point sp1 = viewBoundsInDrawingView.getLocation();
				Point sp2 = new Point(viewBoundsInDrawingView.x+viewBoundsInDrawingView.width-1,viewBoundsInDrawingView.y+viewBoundsInDrawingView.height-1);
				if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
					FGEPaintManager.paintPrimitiveLogger.fine("ConnectorView: use image buffer, copy area from "+sp1+"x"+sp2+" to "+dp1+"x"+dp2);
				g.drawImage(buffer,
						dp1.x,dp1.y,dp2.x,dp2.y,
						sp1.x,sp1.y,sp2.x,sp2.y,
						null);
				 */
			}
		} else {
			// Normal painting
			connectorNode.paint(g, getController());
			super.paint(g);
		}

		// super.paint(g);
		// getGraphicalRepresentation().paint(g,getController());
	}

	@Override
	public AbstractDianaEditor<?, SwingFactory, JComponent> getController() {
		return controller;
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
				if (getNode() != null
						&& ((DianaInteractiveViewer<?, SwingFactory, JComponent>) getController()).getFocusedObjects().contains(getNode())) {
					((DianaInteractiveViewer<?, SwingFactory, JComponent>) getController()).removeFromFocusedObjects(getNode());
				}
				if (getNode() != null
						&& ((DianaInteractiveViewer<?, SwingFactory, JComponent>) getController()).getSelectedObjects().contains(getNode())) {
					((DianaInteractiveViewer<?, SwingFactory, JComponent>) getController()).removeFromSelectedObjects(getNode());
				}
			}
			// Now delete the view
			delete();
		}
	}

	@Override
	public void update(final Observable o, final Object aNotification) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: observable=" + o);
			return;
		}

		// System.out.println("ConnectorView, received: "+aNotification);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(o, aNotification);
				}
			});
		} else {
			if (aNotification instanceof FGENotification) {
				FGENotification notification = (FGENotification) aNotification;
				if (notification instanceof ConnectorModified) {
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
						getPaintManager().invalidate(connectorNode);
					}
					relocateAndResizeView();
					revalidate();
					getPaintManager().repaint(this);
				} else if (notification instanceof NodeDeleted) {
					handleNodeDeleted((NodeDeleted) notification);
				} else if (notification.getParameter() == GraphicalRepresentation.LAYER) {
					updateLayer();
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
						getPaintManager().invalidate(connectorNode);
					}
					getPaintManager().repaint(this);
					/*if (getParentView() != null) {
						getParentView().revalidate();
						getPaintManager().repaint(this);
					}*/
				} else if (notification.getParameter() == DrawingTreeNode.IS_FOCUSED) {
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == DrawingTreeNode.IS_SELECTED) {
					// TODO: ugly hack, please fix this, implement a ForceRepaint in FGEPaintManager
					if (connectorNode.getIsSelected()) {
						requestFocusInWindow();
					}
				} else if (notification.getParameter() == GraphicalRepresentation.TEXT) {
					updateLabelView();
				} else if (notification.getParameter() == GraphicalRepresentation.IS_VISIBLE) {
					updateVisibility();
					if (getPaintManager().isPaintingCacheEnabled()) {
						if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
							getPaintManager().invalidate(connectorNode);
						}
					}
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == ConnectorGraphicalRepresentation.APPLY_FOREGROUND_TO_SYMBOLS) {
					getPaintManager().repaint(this);
				} else if (notification instanceof ObjectWillMove) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().addToTemporaryObjects(connectorNode);
						getPaintManager().invalidate(connectorNode);
					}
				} else if (notification instanceof ObjectMove) {
					relocateView();
					if (getParentView() != null) {
						// getParentView().revalidate();
						getPaintManager().repaint(this);
					}
				} else if (notification instanceof ObjectHasMoved) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().removeFromTemporaryObjects(connectorNode);
						getPaintManager().invalidate(connectorNode);
						getPaintManager().repaint(getParentView());
					}
				} else if (notification instanceof ObjectWillResize) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().addToTemporaryObjects(connectorNode);
						getPaintManager().invalidate(connectorNode);
					}
				} else if (notification instanceof ObjectResized) {
					relocateView();
					if (getParentView() != null) {
						// getParentView().revalidate();
						getPaintManager().repaint(this);
					}
				} else if (notification instanceof ObjectHasResized) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().removeFromTemporaryObjects(connectorNode);
						getPaintManager().invalidate(connectorNode);
						getPaintManager().repaint(getParentView());
					}
				} else {
					// revalidate();
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(connectorNode)) {
						getPaintManager().invalidate(connectorNode);
					}
					getPaintManager().repaint(this);
				}
			} else {
				revalidate();
				getPaintManager().repaint(this);
			}
		}
	}

	@Override
	public LabelView<O> getLabelView() {
		return labelView;
	}

	@Override
	public void registerPalette(DrawingPalette aPalette) {
		// A palette is registered, listen to drag'n'drop events
		setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, aPalette.buildPaletteDropListener(this, controller), true));

	}

	@Override
	public FGEPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, SwingFactory, JComponent>) getController()).getToolTipText();
		}
		return super.getToolTipText(event);
	}

}
