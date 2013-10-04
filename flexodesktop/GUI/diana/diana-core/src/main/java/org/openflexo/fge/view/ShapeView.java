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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingControllerImpl;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.fge.notifications.NodeRemoved;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.view.listener.ShapeViewMouseListener;

/**
 * The {@link ShapeView} is the SWING implementation of a panel showing a {@link ShapeNode}
 * 
 * @author sylvain
 * 
 * @param <O>
 */
@SuppressWarnings("serial")
public class ShapeView<O> extends FGELayeredView<O> {

	private static final Logger logger = Logger.getLogger(ShapeView.class.getPackage().getName());

	private ShapeNode<O> shapeNode;
	private ShapeViewMouseListener mouseListener;
	private DrawingControllerImpl<?> _controller;

	private LabelView<O> labelView;

	public ShapeView(ShapeNode<O> node, DrawingControllerImpl<?> controller) {
		super();
		logger.fine("Create ShapeView " + Integer.toHexString(hashCode()) + " for " + node);
		_controller = controller;
		this.shapeNode = node;
		node.finalizeConstraints();
		updateLabelView();
		if (getController() != null) {
			relocateAndResizeView();
		}
		mouseListener = makeShapeViewMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		shapeNode.addObserver(this);
		setOpaque(false);
		updateVisibility();
		setFocusable(true);

		if (controller.getPalettes() != null) {
			for (DrawingPalette p : controller.getPalettes()) {
				registerPalette(p);
			}
		}
		// logger.info("make ShapeView with "+aGraphicalRepresentation+" bounds="+getBounds());

		// setToolTipText(getClass().getSimpleName()+hashCode());

		// System.out.println("isDoubleBuffered()="+isDoubleBuffered());

		// revalidate();
	}

	public void disableFGEViewMouseListener() {
		System.out.println("Disable FGEViewMouseListener ");
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
	}

	public void enableFGEViewMouseListener() {
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}

	private boolean isDeleted = false;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public synchronized void delete() {
		logger.fine("Delete ShapeView " + Integer.toHexString(hashCode()) + " for " + shapeNode);
		if (getParentView() != null) {
			FGELayeredView<?> parentView = getParentView();
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().invalidate(shapeNode);
				getPaintManager().repaint(parentView);
			}
		}
		if (shapeNode != null) {
			shapeNode.deleteObserver(this);
		}
		setDropTarget(null);
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);
		if (labelView != null) {
			labelView.delete();
		}
		labelView = null;
		_controller = null;
		mouseListener = null;
		shapeNode = null;
		isDeleted = true;
	}

	@Override
	public O getDrawable() {
		return shapeNode.getDrawable();
	}

	@Override
	public ShapeNode<O> getNode() {
		return shapeNode;
	}

	@Override
	public DrawingView<?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	@Override
	public FGELayeredView<?> getParent() {
		return (FGELayeredView<?>) super.getParent();
	}

	public FGELayeredView<?> getParentView() {
		return getParent();
	}

	/*public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return shapeNode.getGraphicalRepresentation();
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
		// System.out.println("relocateAndResizeView() for "+drawable+" bounds="+getBounds());
	}

	private void relocateView() {
		if (getX() != shapeNode.getViewX(getScale()) || getY() != shapeNode.getViewY(getScale())) {
			if (labelView != null) {
				labelView.updateBounds();
			}
			setLocation(shapeNode.getViewX(getScale()), shapeNode.getViewY(getScale()));
		} else {
			// logger.info("Ignore relocateView() because unchanged");
		}
	}

	private void resizeView() {
		if (getWidth() != shapeNode.getViewWidth(getScale()) || getHeight() != shapeNode.getViewHeight(getScale())) {
			if (labelView != null) {
				labelView.updateBounds();
			}
			setSize(shapeNode.getViewWidth(getScale()), shapeNode.getViewHeight(getScale()));
		} else {
			// logger.info("Ignore resizeView() because unchanged");
		}
	}

	private void updateLayer() {
		if (getParent() instanceof JLayeredPane) {
			if (labelView != null) {
				getParent().setLayer((Component) labelView, getLayer());
				getParent().setPosition(labelView, shapeNode.getIndex() * 2);
			}
			getParent().setLayer((Component) this, getLayer());
			getParent().setPosition(this, shapeNode.getIndex() * 2 + 1);
		}
	}

	private void updateVisibility() {
		if (labelView != null) {
			labelView.setVisible(shapeNode.shouldBeDisplayed());
		}

		setVisible(shapeNode.shouldBeDisplayed());
	}

	private void updateLabelView() {
		if (!shapeNode.hasText() && labelView != null) {
			labelView.delete();
			labelView = null;
		} else if (shapeNode.hasText() && labelView == null) {
			labelView = new LabelView<O>(shapeNode, getController(), this);
			if (getParentView() != null) {
				getParentView().add(getLabelView(), getLayer(), -1);
			}
		}
	}

	public Integer getLayer() {
		return FGEConstants.INITIAL_LAYER + shapeNode.getGraphicalRepresentation().getLayer();
	}

	@Override
	public void paint(Graphics g) {
		if (isDeleted()) {
			return;
		}
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (getDrawingView().isBuffering()) {
				// Buffering painting
				if (getPaintManager().isTemporaryObject(shapeNode)) {
					// This object is declared to be a temporary object, to be redrawn
					// continuously, so we need to ignore it: do nothing
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("ShapeView: buffering paint, ignore: " + shapeNode);
					}
				} else {
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger
								.fine("ShapeView: buffering paint, draw: " + shapeNode + " clip=" + g.getClip());
					}
					doPaint(g);
				}
			} else {
				if (!getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), shapeNode, getScale())) {
					doPaint(g);
				}

				/*
				// Use buffer
				Image buffer = getPaintManager().getPaintBuffer();
				Rectangle localViewBounds = g.getClipBounds();
				Rectangle viewBoundsInDrawingView = GraphicalRepresentation.convertRectangle(getGraphicalRepresentation(), localViewBounds, getDrawingGraphicalRepresentation(), getScale());
				//System.out.println("SHAPEVIEW  Paint buffer "+g.getClipBounds());
				Point dp1 = localViewBounds.getLocation();
				Point dp2 = new Point(localViewBounds.x+localViewBounds.width,localViewBounds.y+localViewBounds.height);
				Point sp1 = viewBoundsInDrawingView.getLocation();
				Point sp2 = new Point(viewBoundsInDrawingView.x+viewBoundsInDrawingView.width,viewBoundsInDrawingView.y+viewBoundsInDrawingView.height);
				if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
					FGEPaintManager.paintPrimitiveLogger.fine("ShapeView: use image buffer, copy area from "+sp1+"x"+sp2+" to "+dp1+"x"+dp2);
				g.drawImage(buffer,
						dp1.x,dp1.y,dp2.x,dp2.y,
						sp1.x,sp1.y,sp2.x,sp2.y,
						null);
				 */
			}
		} else {
			// Normal painting
			doPaint(g);
		}

		// getGraphicalRepresentation().paint(g,getController());
		// super.paint(g);
	}

	private void doPaint(Graphics g) {
		shapeNode.paint(g, getController());
		super.paint(g);
	}

	protected ShapeViewMouseListener makeShapeViewMouseListener() {
		return new ShapeViewMouseListener(shapeNode, this);
	}

	@Override
	public DrawingControllerImpl<?> getController() {
		return _controller;
	}

	@Override
	public void update(final Observable o, final Object aNotification) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: observable=" + o);
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(o, aNotification);
				}
			});
		} else {
			// logger.info("update() in ShapeView for " + getNode() + " received: " + aNotification);

			if (aNotification instanceof FGENotification) {
				FGENotification notification = (FGENotification) aNotification;
				if (notification instanceof NodeAdded) {
					handleNodeAdded((NodeAdded) notification);
				} else if (notification instanceof NodeRemoved) {
					handleNodeRemoved((NodeRemoved) notification);
				} else if (notification instanceof NodeDeleted) {
					handleNodeDeleted((NodeDeleted) notification);
				} else if (notification instanceof ObjectWillMove) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().addToTemporaryObjects(shapeNode);
						getPaintManager().invalidate(shapeNode);
					}
				} else if (notification instanceof ObjectMove) {
					relocateView();
					if (getParentView() != null) {
						getPaintManager().repaint(this);
					}
				} else if (notification instanceof ObjectHasMoved) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().removeFromTemporaryObjects(shapeNode);
						getPaintManager().invalidate(shapeNode);
						getPaintManager().repaint(getParentView());
					}
				} else if (notification instanceof ObjectWillResize) {
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().addToTemporaryObjects(shapeNode);
						getPaintManager().invalidate(shapeNode);
					}
				} else if (notification instanceof ObjectResized) {
					resizeView();
					if (getParentView() != null) {
						getParentView().revalidate();
						getPaintManager().repaint(this);
					}
				} else if (notification instanceof ObjectHasResized) {
					resizeView();
					if (getPaintManager().isPaintingCacheEnabled()) {
						getPaintManager().removeFromTemporaryObjects(shapeNode);
						getPaintManager().invalidate(shapeNode);
						getPaintManager().repaint(getParentView());
					}
				} else if (notification instanceof ShapeNeedsToBeRedrawn) {
					getPaintManager().invalidate(shapeNode);
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.LAYER) {
					updateLayer();
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
						getPaintManager().invalidate(shapeNode);
					}
					getPaintManager().repaint(this);

				} else if (notification.getParameter() == DrawingTreeNode.IS_FOCUSED) {
					getPaintManager().repaint(this);
				} else if (notification.getParameter() == GraphicalRepresentation.TEXT) {
					updateLabelView();
				} else if (notification.getParameter() == DrawingTreeNode.IS_SELECTED) {
					if (getParent() != null) {
						getParent().moveToFront(this);
					}
					if (getParent() != null && getLabelView() != null) {
						getParent().moveToFront(getLabelView());
					}
					getPaintManager().repaint(this);
					if (shapeNode.getIsSelected()) {
						requestFocusInWindow();
						// requestFocus();
					}
				} else if (notification.getParameter() == GraphicalRepresentation.IS_VISIBLE) {
					updateVisibility();
					if (getPaintManager().isPaintingCacheEnabled()) {
						if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
							getPaintManager().invalidate(shapeNode);
						}
					}
					getPaintManager().repaint(this);

				} else {
					// revalidate();
					if (getPaintManager().isPaintingCacheEnabled()) {
						if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(shapeNode)) {
							getPaintManager().invalidate(shapeNode);
						}
					}
					getPaintManager().repaint(this);
					// revalidate();
					// getPaintManager().repaint(this);
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

	private BufferedImage screenshot;

	public BufferedImage getScreenshot() {
		if (screenshot == null) {
			captureScreenshot();
		}
		return screenshot;
	}

	private void captureScreenshot() {
		JComponent lbl = this;
		getController().disablePaintingCache();
		try {
			Rectangle bounds = new Rectangle(getBounds());
			if (getLabelView() != null) {
				bounds = bounds.union(getLabelView().getBounds());
			}
			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc == null) {
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			}
			screenshot = gc.createCompatibleImage(bounds.width, bounds.height, Transparency.TRANSLUCENT);// buffered image
			// reference passing
			// the label's ht
			// and width
			Graphics2D graphics = screenshot.createGraphics();// creating the graphics for buffered image
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Sets the Composite for the Graphics2D
																								// context
			lbl.print(graphics); // painting the graphics to label
			/*if (this.getGraphicalRepresentation().getBackground() instanceof BackgroundImage) {
				graphics.drawImage(((BackgroundImage)this.getGraphicalRepresentation().getBackground()).getImage(),0,0,null);
			}*/
			if (getLabelView() != null) {
				Rectangle r = getLabelView().getBounds();
				getLabelView().print(graphics.create(r.x - bounds.x, r.y - bounds.y, r.width, r.height));
			}
			graphics.dispose();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Captured image on " + this);
			}
		} finally {
			getController().enablePaintingCache();
		}
	}

}
