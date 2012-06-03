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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.notifications.ConnectorModified;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.GraphicalRepresentationDeleted;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.view.listener.ConnectorViewMouseListener;

public class ConnectorView<O> extends JComponent implements FGEView<O> {

	private static final Logger logger = Logger.getLogger(ConnectorView.class.getPackage().getName());

	private ConnectorGraphicalRepresentation<O> graphicalRepresentation;
	private ConnectorViewMouseListener mouseListener;
	private DrawingController<?> _controller;

	private LabelView<O> _labelView;

	private BufferedViewHelper paintDelegate;

	public ConnectorView(ConnectorGraphicalRepresentation<O> aGraphicalRepresentation, DrawingController<?> controller) {
		super();
		this.paintDelegate = new BufferedViewHelper(this);
		_controller = controller;
		graphicalRepresentation = aGraphicalRepresentation;
		updateLabelView();
		relocateAndResizeView();
		mouseListener = makeConnectorViewMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		getGraphicalRepresentation().addObserver(this);
		setOpaque(false);

		updateVisibility();

		if (controller.getPalettes() != null) {
			for (DrawingPalette p : controller.getPalettes()) {
				registerPalette(p);
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
			logger.fine("Delete ConnectorView for " + getGraphicalRepresentation());
		}
		if (getParentView() != null) {
			FGELayeredView<?> parentView = getParentView();
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().repaint(parentView);
			}
		}
		if (getGraphicalRepresentation() != null) {
			getGraphicalRepresentation().deleteObserver(this);
		}
		setDropTarget(null);
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
		if (_labelView != null) {
			_labelView.delete();
		}
		_labelView = null;
		_controller = null;
		mouseListener = null;
		graphicalRepresentation = null;
		isDeleted = true;
	}

	@Override
	public O getModel() {
		return getDrawable();
	}

	public O getDrawable() {
		return getGraphicalRepresentation().getDrawable();
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

	@Override
	public ConnectorGraphicalRepresentation<O> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return graphicalRepresentation.getDrawingGraphicalRepresentation();
	}

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
		if (_labelView != null) {
			_labelView.updateBounds();
		}
		int newX, newY;
		newX = getGraphicalRepresentation().getViewX(getScale());
		newY = getGraphicalRepresentation().getViewY(getScale());
		if (newX != getX() || newY != getY()) {
			setLocation(newX, newY);
		}
	}

	private void resizeView() {
		/*logger.info("resizeView to ("
				+getGraphicalRepresentation().getViewWidth(getScale())+","+
				getGraphicalRepresentation().getViewHeight(getScale())+")");*/
		if (_labelView != null) {
			_labelView.updateBounds();
		}
		int newWidth, newHeight;
		newWidth = getGraphicalRepresentation().getViewWidth(getScale());
		newHeight = getGraphicalRepresentation().getViewHeight(getScale());
		if (newWidth != getWidth() || newHeight != getHeight()) {
			setSize(newWidth, newHeight);
		}
	}

	/*private void updateLayer()
	{
		//logger.info("GR: "+getGraphicalRepresentation()+" update layer to "+getLayer());
		if (getParent() instanceof JLayeredPane) {
			if (_labelView!=null)
				((JLayeredPane)getParent()).setLayer(_labelView, getLayer());
			((JLayeredPane)getParent()).setLayer(this, getLayer());
		}
	}*/

	private void updateLayer() {
		if (getParent() != null) {
			if (_labelView != null) {
				getParent().setLayer((Component) _labelView, getLayer());
				getParent().setPosition(_labelView, getGraphicalRepresentation().getLayerOrder() * 2);
			}
			getParent().setLayer((Component) this, getLayer());
			getParent().setPosition(this, getGraphicalRepresentation().getLayerOrder() * 2 + 1);
		}
	}

	private void updateVisibility() {
		if (_labelView != null) {
			_labelView.setVisible(getGraphicalRepresentation().shouldBeDisplayed());
		}
		setVisible(getGraphicalRepresentation().shouldBeDisplayed());
	}

	private void updateLabelView() {
		if (!getGraphicalRepresentation().getHasText() && _labelView != null) {
			_labelView.delete();
			_labelView = null;
		} else if (getGraphicalRepresentation().getHasText() && _labelView == null) {
			_labelView = new LabelView<O>(getGraphicalRepresentation(), getController(), this);
			if (getParentView() != null) {
				getParentView().add(getLabelView());
			}
		}
	}

	public Integer getLayer() {
		return FGEConstants.INITIAL_LAYER + getGraphicalRepresentation().getLayer();
	}

	@Override
	public void paint(Graphics g) {
		paintDelegate.paint(g);
	}

	@Override
	public boolean useBuffer() {
		ShapeGraphicalRepresentation<?> startGR = getGraphicalRepresentation().getStartObject();
		if (startGR.isResizing()) {
			return false;
		}
		ShapeGraphicalRepresentation<?> endGR = getGraphicalRepresentation().getEndObject();
		if (endGR.isResizing()) {
			return false;
		}
		return endGR.isMoving() == startGR.isMoving();
	}

	@Override
	public void doUnbufferedPaint(Graphics g) {
		// Nothing to do here
	}

	@Override
	public void doPaint(Graphics g) {
		getGraphicalRepresentation().paint(g, getController());
	}

	@Override
	public void superPaint(Graphics g) {

	}

	@Override
	public BufferedViewHelper getPaintDelegate() {
		return paintDelegate;
	}

	protected ConnectorViewMouseListener makeConnectorViewMouseListener() {
		return new ConnectorViewMouseListener(graphicalRepresentation, this);
	}

	@Override
	public DrawingController<?> getController() {
		return _controller;
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
					getPaintDelegate().invalidateBuffer();
					relocateAndResizeView();
					getPaintManager().repaint(this);
				} else if (notification instanceof GraphicalRepresentationDeleted) {
					GraphicalRepresentation<?> deletedGR = ((GraphicalRepresentationDeleted) notification)
							.getDeletedGraphicalRepresentation();
					// If was not removed, try to do it now
					if (getGraphicalRepresentation() != null && getGraphicalRepresentation().getContainerGraphicalRepresentation() != null
							&& getGraphicalRepresentation().getContainerGraphicalRepresentation().contains(getGraphicalRepresentation())) {
						getGraphicalRepresentation().getContainerGraphicalRepresentation().notifyDrawableRemoved(deletedGR);
					}
					if (getGraphicalRepresentation() != null && getController().getFocusedObjects().contains(getGraphicalRepresentation())) {
						getController().removeFromFocusedObjects(getGraphicalRepresentation());
					}
					if (getGraphicalRepresentation() != null && getController().getSelectedObjects().contains(getGraphicalRepresentation())) {
						getController().removeFromSelectedObjects(getGraphicalRepresentation());
					}
					delete();
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.layer) {
					updateLayer();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.isFocused) {
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.isSelected) {
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.hasText) {
					updateLabelView();
				} else if (notification.getParameter() == GraphicalRepresentation.Parameters.isVisible) {
					updateVisibility();
					getPaintDelegate().invalidateBuffer();
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == ConnectorGraphicalRepresentation.Parameters.applyForegroundToSymbols) {
					getPaintManager().repaint(this);
				} else if (notification instanceof ObjectWillMove) {
					getPaintDelegate().invalidateBuffer();
				} else if (notification instanceof ObjectMove) {
					relocateView();
					if (getParentView() != null) {
						// getParentView().revalidate();
						getPaintManager().repaint(this);
					}
				} else if (notification instanceof ObjectHasMoved) {
					getPaintDelegate().invalidateBuffer();
					getPaintManager().repaint(getParentView());
				} else if (notification instanceof ObjectWillResize) {
					getPaintDelegate().invalidateBuffer();
				} else if (notification instanceof ObjectResized) {
					relocateView();
					if (getParentView() != null) {
						getPaintManager().repaint(this);
					}
				} else if (notification instanceof ObjectHasResized) {
					getPaintDelegate().invalidateBuffer();
					getPaintManager().repaint(getParentView());
				}
			}
		}
	}

	@Override
	public LabelView<O> getLabelView() {
		return _labelView;
	}

	@Override
	public void registerPalette(DrawingPalette aPalette) {
		// A palette is registered, listen to drag'n'drop events
		setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, aPalette.buildPaletteDropListener(this, _controller), true));

	}

	@Override
	public FGEPaintManager getPaintManager() {
		return getDrawingView().getPaintManager();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		return getController().getToolTipText();
	}

}
