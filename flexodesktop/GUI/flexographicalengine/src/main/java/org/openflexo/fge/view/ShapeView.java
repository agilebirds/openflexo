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
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.GraphicalRepresentationAdded;
import org.openflexo.fge.notifications.GraphicalRepresentationDeleted;
import org.openflexo.fge.notifications.GraphicalRepresentationRemoved;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.view.listener.ShapeViewMouseListener;

public class ShapeView<O> extends FGELayeredView<O> {

	private static final Logger logger = Logger.getLogger(ShapeView.class.getPackage().getName());

	private ShapeGraphicalRepresentation<O> graphicalRepresentation;
	private ShapeViewMouseListener mouseListener;
	private DrawingController _controller;

	private LabelView<O> _labelView;

	public ShapeView(ShapeGraphicalRepresentation<O> aGraphicalRepresentation, DrawingController<?> controller) {
		super();
		logger.fine("Create ShapeView " + Integer.toHexString(hashCode()) + " for " + aGraphicalRepresentation);
		_controller = controller;
		graphicalRepresentation = aGraphicalRepresentation;
		graphicalRepresentation.finalizeConstraints();
		updateLabelView();
		relocateAndResizeView();
		mouseListener = makeShapeViewMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		getGraphicalRepresentation().addObserver(this);
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

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_UP) {
					getController().upKeyPressed();
					event.consume();
					return;
				} else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
					getController().downKeyPressed();
					event.consume();
					return;
				} else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
					getController().rightKeyPressed();
					event.consume();
					return;
				} else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
					getController().leftKeyPressed();
					event.consume();
					return;
				}
			}
		});

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
	public void delete() {
		logger.fine("Delete ShapeView " + Integer.toHexString(hashCode()) + " for " + getGraphicalRepresentation());
		if (getParentView() != null) {
			FGELayeredView parentView = getParentView();
			// logger.warning("Unexpected not null parent, proceeding anyway");
			parentView.remove(this);
			parentView.revalidate();
			if (getPaintManager() != null) {
				getPaintManager().invalidate(getGraphicalRepresentation());
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
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	@Override
	public FGELayeredView getParent() {
		return (FGELayeredView) super.getParent();
	}

	public FGELayeredView getParentView() {
		return getParent();
	}

	@Override
	public ShapeGraphicalRepresentation<O> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return graphicalRepresentation.getDrawingGraphicalRepresentation();
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	public void rescale() {
		relocateAndResizeView();
	}

	private void relocateAndResizeView() {
		relocateView();
		resizeView();
		// System.out.println("relocateAndResizeView() for "+drawable+" bounds="+getBounds());
	}

	private void relocateView() {
		if (getX() != getGraphicalRepresentation().getViewX(getScale()) || getY() != getGraphicalRepresentation().getViewY(getScale())) {
			if (_labelView != null) {
				_labelView.updateBounds();
			}
			setLocation(getGraphicalRepresentation().getViewX(getScale()), getGraphicalRepresentation().getViewY(getScale()));
		} else {
			// logger.info("Ignore relocateView() because unchanged");
		}
	}

	private void resizeView() {
		if (getWidth() != getGraphicalRepresentation().getViewWidth(getScale())
				|| getHeight() != getGraphicalRepresentation().getViewHeight(getScale())) {
			if (_labelView != null) {
				_labelView.updateBounds();
			}
			setSize(getGraphicalRepresentation().getViewWidth(getScale()), getGraphicalRepresentation().getViewHeight(getScale()));
		} else {
			// logger.info("Ignore resizeView() because unchanged");
		}
	}

	private void updateLayer() {
		if (getParent() instanceof JLayeredPane) {
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
				getParentView().add(getLabelView(), getLayer(), -1);
			}
		}
	}

	public Integer getLayer() {
		return FGEConstants.INITIAL_LAYER + getGraphicalRepresentation().getLayer();
	}

	@Override
	public void paint(Graphics g) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (getDrawingView().isBuffering()) {
				// Buffering painting
				if (getPaintManager().isTemporaryObject(getGraphicalRepresentation())) {
					// This object is declared to be a temporary object, to be redrawn
					// continuously, so we need to ignore it: do nothing
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("ShapeView: buffering paint, ignore: " + getGraphicalRepresentation());
					}
				} else {
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("ShapeView: buffering paint, draw: " + getGraphicalRepresentation()
								+ " clip=" + g.getClip());
					}
					doPaint(g);
				}
			} else {
				if (!getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), getGraphicalRepresentation(), getScale())) {
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
		getGraphicalRepresentation().paint(g, getController());
		super.paint(g);
	}

	protected ShapeViewMouseListener makeShapeViewMouseListener() {
		return new ShapeViewMouseListener(graphicalRepresentation, this);
	}

	@Override
	public DrawingController<?> getController() {
		return _controller;
	}

	@Override
	public void update(Observable o, Object aNotification) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: observable=" + o);
			return;
		}

		// logger.info("For "+getGraphicalRepresentation().getClass().getSimpleName()+" received: "+aNotification);

		if (aNotification instanceof FGENotification) {
			FGENotification notification = (FGENotification) aNotification;
			if (notification instanceof GraphicalRepresentationAdded) {
				GraphicalRepresentation<?> newGR = ((GraphicalRepresentationAdded) notification).getAddedGraphicalRepresentation();
				logger.fine("ShapeView: Received ObjectAdded notification, creating view for " + newGR);
				if (newGR instanceof ShapeGraphicalRepresentation) {
					ShapeGraphicalRepresentation<?> shapeGR = (ShapeGraphicalRepresentation<?>) newGR;
					add(shapeGR.makeShapeView(getController()));
					revalidate();
					getPaintManager().repaint(this);
					shapeGR.notifyShapeNeedsToBeRedrawn();
				} else if (newGR instanceof ConnectorGraphicalRepresentation) {
					ConnectorGraphicalRepresentation<?> connectorGR = (ConnectorGraphicalRepresentation<?>) newGR;
					add(connectorGR.makeConnectorView(getController()));
					revalidate();
					getPaintManager().repaint(this);
				} else if (newGR instanceof GeometricGraphicalRepresentation) {
					newGR.addObserver(this);
					revalidate();
					getPaintManager().repaint(this);
				}
			} else if (notification instanceof GraphicalRepresentationRemoved) {
				GraphicalRepresentation<?> removedGR = ((GraphicalRepresentationRemoved) notification).getRemovedGraphicalRepresentation();
				if (removedGR instanceof ShapeGraphicalRepresentation) {
					ShapeView<?> view = getDrawingView().shapeViewForObject((ShapeGraphicalRepresentation<?>) removedGR);
					if (view != null) {
						remove(view);
						revalidate();
						getPaintManager().invalidate(getGraphicalRepresentation());
						getPaintManager().repaint(this);
					} else {
						logger.warning("Cannot find view for " + removedGR);
					}
				} else if (removedGR instanceof ConnectorGraphicalRepresentation) {
					ConnectorView<?> view = getDrawingView().connectorViewForObject((ConnectorGraphicalRepresentation<?>) removedGR);
					if (view != null) {
						remove(view);
						revalidate();
						getPaintManager().invalidate(getGraphicalRepresentation());
						getPaintManager().repaint(this);
					} else {
						logger.warning("Cannot find view for " + removedGR);
					}
				} else if (removedGR instanceof GeometricGraphicalRepresentation) {
					removedGR.deleteObserver(this);
					revalidate();
					getPaintManager().repaint(this);
				}
			} else if (notification instanceof GraphicalRepresentationDeleted) {
				GraphicalRepresentation<?> deletedGR = ((GraphicalRepresentationDeleted) notification).getDeletedGraphicalRepresentation();
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
			} else if (notification instanceof ObjectWillMove) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
					getPaintManager().invalidate(getGraphicalRepresentation());
				}
			} else if (notification instanceof ObjectMove) {
				relocateView();
				if (getParentView() != null) {
					getPaintManager().repaint(this);
				}
			} else if (notification instanceof ObjectHasMoved) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().removeFromTemporaryObjects(getGraphicalRepresentation());
					getPaintManager().invalidate(getGraphicalRepresentation());
					getPaintManager().repaint(getParentView());
				}
			} else if (notification instanceof ObjectWillResize) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
					getPaintManager().invalidate(getGraphicalRepresentation());
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
					getPaintManager().removeFromTemporaryObjects(getGraphicalRepresentation());
					getPaintManager().invalidate(getGraphicalRepresentation());
					getPaintManager().repaint(getParentView());
				}
			} else if (notification instanceof ShapeNeedsToBeRedrawn) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					/*getPaintManager().resetTemporaryObjects();
					getPaintManager().invalidate(getGraphicalRepresentation());
					getPaintManager().repaint(getParentView());*/
					getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
					getPaintManager().repaint(this);
				}
			} else if (notification.getParameter() == GraphicalRepresentation.Parameters.layer) {
				updateLayer();
				if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(getGraphicalRepresentation())) {
					getPaintManager().invalidate(getGraphicalRepresentation());
				}
				getPaintManager().repaint(this);
				/*if (getParentView() != null) {
					getParentView().revalidate();
					getPaintManager().repaint(this);
				}*/
			} else if (notification.getParameter() == GraphicalRepresentation.Parameters.isFocused) {
				getPaintManager().repaint(this);
			} else if (notification.getParameter() == GraphicalRepresentation.Parameters.hasText) {
				updateLabelView();
			} else if (notification.getParameter() == GraphicalRepresentation.Parameters.isSelected) {
				if (getParent() != null) {
					getParent().moveToFront(this);
				}
				if (getParent() != null && getLabelView() != null) {
					getParent().moveToFront(getLabelView());
				}
				getPaintManager().repaint(this);

				requestFocusInWindow();
				// requestFocus();
			} else if (notification.getParameter() == GraphicalRepresentation.Parameters.isVisible) {
				updateVisibility();
				if (getParentView() != null) {
					getParentView().revalidate();
					getPaintManager().repaint(getParentView());
				}
			} else {
				// revalidate();
				if (getPaintManager().isPaintingCacheEnabled()) {
					if (!getPaintManager().isTemporaryObjectOrParentIsTemporaryObject(getGraphicalRepresentation())) {
						getPaintManager().invalidate(getGraphicalRepresentation());
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
			screenshot = new BufferedImage(bounds.width, bounds.height, java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);// buffered image
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
