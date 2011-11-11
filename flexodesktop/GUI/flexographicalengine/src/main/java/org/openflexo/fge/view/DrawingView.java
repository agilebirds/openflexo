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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.DrawingNeedsToBeRedrawn;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.RectangleSelectingAction;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.GraphicalRepresentationAdded;
import org.openflexo.fge.notifications.GraphicalRepresentationRemoved;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.view.listener.DrawingViewMouseListener;
import org.openflexo.fge.view.listener.FocusRetriever;
import org.openflexo.swing.MouseResizer;

public class DrawingView<D extends Drawing<?>> extends FGELayeredView<D> implements Autoscroll {

	private final class DrawingViewResizer extends MouseResizer {
		protected DrawingViewResizer() {
			super(DrawingView.this, new MouseResizer.MouseResizerDelegate() {

				@Override
				public void resizeBy(int deltaX, int deltaY) {

				}

				@Override
				public void resizeDirectlyBy(int deltaX, int deltaY) {
					if (deltaX != 0) {
						getDrawing().getDrawingGraphicalRepresentation().setWidth(
								getDrawing().getDrawingGraphicalRepresentation().getWidth() + deltaX / getScale());
					}
					if (deltaY != 0) {
						getDrawing().getDrawingGraphicalRepresentation().setHeight(
								getDrawing().getDrawingGraphicalRepresentation().getHeight() + deltaY / getScale());
					}
				}
			}, ResizeMode.SOUTH, ResizeMode.EAST, ResizeMode.SOUTH_EAST);
		}

		@Override
		protected int getComponentWidth() {
			return getDrawing().getDrawingGraphicalRepresentation().getViewWidth(getScale());
		}

		@Override
		protected int getComponentHeight() {
			return getDrawing().getDrawingGraphicalRepresentation().getViewHeight(getScale());
		}
	}

	private static final Logger logger = Logger.getLogger(DrawingView.class.getPackage().getName());

	private D drawing;
	private Hashtable<GraphicalRepresentation, FGEView> contents;
	private DrawingController<D> _controller;
	private FocusRetriever _focusRetriever;
	private FGEPaintManager _paintManager;

	private MouseResizer resizer;
	private DrawingViewMouseListener mouseListener;

	protected FGEDrawingGraphics graphics;

	public DrawingView(D aDrawing, DrawingController<D> controller) {
		_controller = controller;
		drawing = aDrawing;
		aDrawing.getDrawingGraphicalRepresentation().updateBindingModel();
		contents = new Hashtable<GraphicalRepresentation, FGEView>();
		graphics = new FGEDrawingGraphics(drawing.getDrawingGraphicalRepresentation());
		_focusRetriever = new FocusRetriever(this);
		if (aDrawing.getDrawingGraphicalRepresentation().isResizable()) {
			resizer = new DrawingViewResizer();
		}
		mouseListener = makeDrawingViewMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		resizeView();
		getGraphicalRepresentation().addObserver(this);

		for (Object gr : getGraphicalRepresentation().getContainedGraphicalRepresentations()) {
			if (gr instanceof GeometricGraphicalRepresentation) {
				((GeometricGraphicalRepresentation) gr).addObserver(this);
			}
		}

		if (controller.getPalettes() != null) {
			for (DrawingPalette p : controller.getPalettes()) {
				registerPalette(p);
			}
		}

		updateBackground();
		setOpaque(true);

		_paintManager = new FGEPaintManager(this);

		setToolTipText(getClass().getSimpleName() + hashCode());

		// setDoubleBuffered(true);

		setFocusable(true);

		revalidate();
	}

	public D getDrawing() {
		return drawing;
	}

	@Override
	public DrawingView<D> getDrawingView() {
		return this;
	}

	@Override
	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return drawing.getDrawingGraphicalRepresentation();
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return getGraphicalRepresentation();
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	public Integer getLayer() {
		return JLayeredPane.DEFAULT_LAYER;
	}

	@Override
	public D getModel() {
		return drawing;
	}

	public Dimension getComputedMinimumSize() {
		Rectangle r = getBounds();
		for (int i = 0; i < getComponents().length; i++) {
			Component c = getComponents()[i];
			r = r.union(c.getBounds());
		}
		return r.getSize();
	}

	public void rescale() {
		for (FGEView v : contents.values()) {
			if (v instanceof ShapeView) {
				((ShapeView) v).rescale();
			}
			if (v instanceof ConnectorView) {
				((ConnectorView) v).rescale();
			}
		}
		resizeView();
		// revalidate();
		getPaintManager().invalidate(getGraphicalRepresentation());
		getPaintManager().repaint(this);
	}

	private void resizeView() {
		int offset = getGraphicalRepresentation().isResizable() ? 20 : 0;
		setPreferredSize(new Dimension(getGraphicalRepresentation().getViewWidth(getController().getScale()) + offset,
				getGraphicalRepresentation().getViewHeight(getController().getScale()) + offset));
		if (getParent() != null) {
			getParent().doLayout();
		}
	}

	private void updateBackground() {
		if (getGraphicalRepresentation().getDrawWorkingArea()) {
			setBackground(Color.GRAY);
		} else {
			setBackground(getGraphicalRepresentation().getBackgroundColor());
		}
	}

	protected DrawingViewMouseListener makeDrawingViewMouseListener() {
		return new DrawingViewMouseListener(getGraphicalRepresentation(), this);
	}

	@Override
	public DrawingController<D> getController() {
		return _controller;
	}

	@Override
	public void update(Observable o, Object notification) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: observable=" + o);
			return;
		}

		// logger.info("Received: "+notification);

		if (notification instanceof FGENotification) {
			FGENotification notif = (FGENotification) notification;
			if (notification instanceof GraphicalRepresentationAdded) {
				GraphicalRepresentation newGR = ((GraphicalRepresentationAdded) notification).getAddedGraphicalRepresentation();
				logger.fine("DrawingView: Received ObjectAdded notification, creating view for " + newGR);
				if (newGR instanceof ShapeGraphicalRepresentation) {
					ShapeGraphicalRepresentation shapeGR = (ShapeGraphicalRepresentation) newGR;
					add(shapeGR.makeShapeView(getController()));
					revalidate();
					getPaintManager().invalidate(getDrawingGraphicalRepresentation());
					getPaintManager().repaint(this);
				} else if (newGR instanceof ConnectorGraphicalRepresentation) {
					ConnectorGraphicalRepresentation connectorGR = (ConnectorGraphicalRepresentation) newGR;
					add(connectorGR.makeConnectorView(getController()));
					revalidate();
					getPaintManager().invalidate(getDrawingGraphicalRepresentation());
					getPaintManager().repaint(this);
				} else if (newGR instanceof GeometricGraphicalRepresentation) {
					newGR.addObserver(this);
					revalidate();
					getPaintManager().invalidate(getDrawingGraphicalRepresentation());
					getPaintManager().repaint(this);
				}
			} else if (notification instanceof GraphicalRepresentationRemoved) {
				GraphicalRepresentation<?> removedGR = ((GraphicalRepresentationRemoved) notification).getRemovedGraphicalRepresentation();
				if (removedGR instanceof ShapeGraphicalRepresentation) {
					ShapeView<?> view = shapeViewForObject((ShapeGraphicalRepresentation<?>) removedGR);
					if (view != null) {
						remove(view);
						revalidate();
						getPaintManager().invalidate(getDrawingGraphicalRepresentation());
						getPaintManager().repaint(this);
					} else {
						logger.warning("Cannot find view for " + removedGR);
					}
				} else if (removedGR instanceof ConnectorGraphicalRepresentation) {
					ConnectorView<?> view = connectorViewForObject((ConnectorGraphicalRepresentation<?>) removedGR);
					if (view != null) {
						remove(view);
						revalidate();
						getPaintManager().invalidate(getDrawingGraphicalRepresentation());
						getPaintManager().repaint(this);
					} else {
						logger.warning("Cannot find view for " + removedGR);
					}
				} else if (removedGR instanceof GeometricGraphicalRepresentation) {
					removedGR.deleteObserver(this);
					revalidate();
					getPaintManager().invalidate(getDrawingGraphicalRepresentation());
					getPaintManager().repaint(this);
				}
			} else if (notification instanceof ObjectResized) {
				rescale();
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				getPaintManager().repaint(this);
			} else if (notif.getParameter() == DrawingGraphicalRepresentation.Parameters.backgroundColor) {
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				updateBackground();
				getPaintManager().repaint(this);
			} else if (notif.getParameter() == DrawingGraphicalRepresentation.Parameters.drawWorkingArea) {
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				updateBackground();
				getPaintManager().repaint(this);
			} else if (notif.getParameter() == DrawingGraphicalRepresentation.Parameters.width) {
				rescale();
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				getPaintManager().repaint(this);
			} else if (notif.getParameter() == DrawingGraphicalRepresentation.Parameters.height) {
				rescale();
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				getPaintManager().repaint(this);
			} else if (notif.getParameter() == DrawingGraphicalRepresentation.Parameters.isResizable) {
				if (getDrawingGraphicalRepresentation().isResizable()) {
					removeMouseListener(mouseListener); // We remove the mouse
														// listener, so that the
														// mouse resizer is
														// called before
														// mouseListener
					if (resizer == null) {
						resizer = new DrawingViewResizer();
					} else {
						addMouseListener(resizer);
					}
					addMouseListener(mouseListener);
				} else {
					removeMouseListener(resizer);
				}
			} else if (notif instanceof DrawingNeedsToBeRedrawn) {
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				getPaintManager().repaint(this);
			} else if (o instanceof GeometricGraphicalRepresentation) {
				getPaintManager().invalidate(getDrawingGraphicalRepresentation());
				getPaintManager().repaint(this);
			}
		}
	}

	private RectangleSelectingAction _rectangleSelectingAction;

	public void setRectangleSelectingAction(RectangleSelectingAction action) {
		_rectangleSelectingAction = action;
	}

	public void resetRectangleSelectingAction() {
		_rectangleSelectingAction = null;
		getPaintManager().repaint(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (getGraphicalRepresentation().getDrawWorkingArea()) {
			getGraphicalRepresentation().paint(g, getController());
			// for (Component c : getComponents())
			// System.out.println("Component: "+c);
		}
	}

	private long cumulatedRepaintTime = 0;

	private boolean isBuffering = false;
	private Graphics2D bufferingGraphics;
	private boolean bufferingHasBeenStartedAgain = false;

	/**
	 * 
	 * @param g
	 *            graphics on which buffering will be performed
	 */
	protected synchronized void prepareForBuffering(Graphics2D g) {
		isBuffering = true;
		bufferingGraphics = g;
	}

	public synchronized boolean isBuffering() {
		return isBuffering;
	}

	/**
	 * Perform a new buffering (cancel currently performed buffering)
	 */
	protected void startBufferingAgain() {
		bufferingHasBeenStartedAgain = true;
	}

	private void forcePaintTemporaryObjects(GraphicalRepresentation<?> fatherGraphicalRepresentation, Graphics g) {
		forcePaintObjects(fatherGraphicalRepresentation, g, true);
	}

	private void forcePaintObjects(GraphicalRepresentation<?> fatherGraphicalRepresentation, Graphics g) {
		forcePaintObjects(fatherGraphicalRepresentation, g, false);
	}

	private void forcePaintObjects(GraphicalRepresentation<?> fatherGraphicalRepresentation, Graphics g, boolean temporaryObjectsOnly) {
		List<? extends GraphicalRepresentation<?>> containedGR = fatherGraphicalRepresentation.getContainedGraphicalRepresentations();
		if (containedGR == null) {
			return;
		}
		for (GraphicalRepresentation<?> gr : new ArrayList<GraphicalRepresentation<?>>(containedGR)) {
			if (gr.shouldBeDisplayed()
					&& (!temporaryObjectsOnly || getPaintManager().isTemporaryObject(gr) || getPaintManager().containsTemporaryObject(gr))) {
				FGEView<?> view = viewForObject(gr);
				Component viewAsComponent = (Component) view;
				Graphics childGraphics = g.create(viewAsComponent.getX(), viewAsComponent.getY(), viewAsComponent.getWidth(),
						viewAsComponent.getHeight());
				if (getPaintManager().isTemporaryObject(gr) || !temporaryObjectsOnly) {
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("DrawingView: continuous painting, paint " + gr
								+ " temporaryObjectsOnly=" + temporaryObjectsOnly);
					}
					gr.paint(childGraphics, getController());
					LabelView labelView = null;
					if (view instanceof ShapeView) {
						labelView = ((ShapeView) view).getLabelView();
					} else if (view instanceof ConnectorView) {
						labelView = ((ConnectorView) view).getLabelView();
					}
					if (labelView != null) {
						Graphics labelGraphics = g.create(labelView.getX(), labelView.getY(), labelView.getWidth(), labelView.getHeight());
						// Tricky area: if label is currently beeing edited,
						// call to paint is required here
						// to paint text component above buffer image.
						// Otherwise, just call doPaint to force paint label
						if (labelView.isEditing()) {
							labelView.paint(labelGraphics);
						} else {
							labelView.doPaint(labelGraphics);
						}
						labelGraphics.dispose();
					}
					// do the job for childs
					forcePaintObjects(gr, childGraphics, false);
				} else {
					// do the job for childs
					forcePaintObjects(gr, childGraphics, true);
				}
			}
		}
	}

	@Override
	public synchronized void paint(Graphics g) {
		long startTime = System.currentTimeMillis();
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (isBuffering) {
				// Buffering painting
				if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
					FGEPaintManager.paintPrimitiveLogger.fine("DrawingView: Paint for image buffering area, clip=" + g.getClipBounds());
				}
				super.paint(g);
				if (bufferingHasBeenStartedAgain) {
					g.clearRect(0, 0, getGraphicalRepresentation().getViewWidth(getController().getScale()), getGraphicalRepresentation()
							.getViewHeight(getController().getScale()));
					super.paint(g);
					bufferingHasBeenStartedAgain = false;
				}
			} else {
				if (getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), getDrawingGraphicalRepresentation(), getScale())) {
					// Now, we still have to paint objects that are declared
					// to be temporary and continuously to be redrawn
					forcePaintTemporaryObjects(getDrawingGraphicalRepresentation(), g);
				} else {
					// This failed for some reasons (eg rendering request
					// outside cached image)
					// Skip buffering and perform normal rendering
					super.paint(g);
				}
				// Use buffer
				/*
				 * Image buffer = getPaintManager().getPaintBuffer(); Rectangle
				 * r = g.getClipBounds(); Point p1 = r.getLocation(); Point p2 =
				 * new Point(r.x+r.width,r.y+r.height); if ((p1.x < 0) || (p1.x
				 * > buffer.getWidth(null)) || (p1.y < 0) || (p1.y >
				 * buffer.getHeight(null)) || (p2.x < 0) || (p2.x >
				 * buffer.getWidth(null)) || (p2.y < 0) || (p2.y >
				 * buffer.getHeight(null))) { // We have here a request for
				 * render outside cached image // We cannot do that, so skip
				 * buffer use and do normal painting if
				 * (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
				 * FGEPaintManager.paintPrimitiveLogger.fine(
				 * "DrawingView: request to render outside image buffer, use normal rendering clip="
				 * +r);
				 * getPaintManager().invalidate(getDrawingGraphicalRepresentation
				 * ()); super.paint(g); } else { // OK, we are in our bounds if
				 * (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
				 * FGEPaintManager.paintPrimitiveLogger.fine(
				 * "DrawingView: use image buffer, copy area "+r);
				 * g.drawImage(buffer, p1.x,p1.y,p2.x,p2.y, p1.x,p1.y,p2.x,p2.y,
				 * null); // Now, we still have to paint objects that are
				 * declared // to be temporary and continuously to be redrawn
				 * forcePaintTemporaryObjects
				 * (getDrawingGraphicalRepresentation(),g); }
				 */
			}
		} else {
			// Normal painting
			super.paint(g);
		}

		Vector<GeometricGraphicalRepresentation> geomList = new Vector<GeometricGraphicalRepresentation>();
		for (Object gr : getGraphicalRepresentation().getContainedGraphicalRepresentations()) {
			if (gr instanceof GeometricGraphicalRepresentation) {
				geomList.add((GeometricGraphicalRepresentation) gr);
			}
		}
		if (geomList.size() > 0) {
			Collections.sort(geomList, new Comparator<GeometricGraphicalRepresentation>() {
				@Override
				public int compare(GeometricGraphicalRepresentation o1, GeometricGraphicalRepresentation o2) {
					return o1.getLayer() - o2.getLayer();
				}
			});
			for (GeometricGraphicalRepresentation gr : geomList) {
				gr.paint(g, getController());
			}
		}

		if (!isBuffering) {

			FGEDrawingGraphics graphics = getDrawingGraphicalRepresentation().getGraphics();
			Graphics2D g2 = (Graphics2D) g;
			graphics.createGraphics(g2, getController());

			// Don't paint those things in case of buffering
			for (GraphicalRepresentation o : new Vector<GraphicalRepresentation>(getController().getFocusedObjects())) {
				paintFocused(o, graphics);
			}

			for (GraphicalRepresentation o : new Vector<GraphicalRepresentation>(getController().getSelectedObjects())) {
				if (o.shouldBeDisplayed()) {
					paintSelected(o, graphics);
				}
			}

			/*
			 * if ((getController().getFocusedFloatingLabel() != null &&
			 * getController().getFocusedFloatingLabel().hasFloatingLabel())) {
			 * paintFocusedFloatingLabel
			 * (getController().getFocusedFloatingLabel(), g); }
			 */

			graphics.releaseGraphics();

			if (_rectangleSelectingAction != null) {
				_rectangleSelectingAction.paint(g, getController());
			}
		}

		// Do it once only !!!
		isBuffering = false;

		long endTime = System.currentTimeMillis();
		// System.out.println("END paint() in DrawingView, this took "+(endTime-startTime)+" ms");

		cumulatedRepaintTime += endTime - startTime;

		if (FGEPaintManager.paintStatsLogger.isLoggable(Level.FINE)) {
			FGEPaintManager.paintStatsLogger.fine("PAINT " + getName() + " clip=" + g.getClip() + " time=" + (endTime - startTime)
					+ "ms cumulatedRepaintTime=" + cumulatedRepaintTime + " ms");
		}
	}

	private void paintFocusedFloatingLabel(GraphicalRepresentation<?> focusedFloatingLabel, Graphics g) {
		Color color = Color.BLACK;
		if (focusedFloatingLabel.getIsSelected()) {
			color = getDrawingView().getDrawingGraphicalRepresentation().getSelectionColor();
		} else if (focusedFloatingLabel.getIsFocused()) {
			color = getDrawingView().getDrawingGraphicalRepresentation().getFocusColor();
		} else {
			return;
		}
		LabelView labelView = null;
		FGEView<?> view = viewForObject(focusedFloatingLabel);
		if (view instanceof ShapeView) {
			labelView = ((ShapeView) view).getLabelView();
		} else if (view instanceof ConnectorView) {
			labelView = ((ConnectorView) view).getLabelView();
		}
		if (labelView != null) {
			Point p1 = SwingUtilities.convertPoint(labelView, new Point(0, labelView.getHeight() / 2), this);
			Point p2 = SwingUtilities.convertPoint(labelView, new Point(labelView.getWidth(), labelView.getHeight() / 2), this);
			paintControlPoint(p1, color, g);
			paintControlPoint(p2, color, g);
		}
	}

	protected void paintControlArea(ControlArea<?> ca, FGEDrawingGraphics graphics) {
		Rectangle invalidatedArea = ca.paint(graphics);
		if (invalidatedArea != null) {
			getPaintManager().addTemporaryRepaintArea(invalidatedArea, this);
		}
	}

	private void paintControlPoint(Point location, Color color, Graphics g) {
		int size = FGEConstants.CONTROL_POINT_SIZE;
		g.setColor(color);
		Rectangle r = new Rectangle(location.x - size, location.y - size, size * 2, size * 2);
		g.fillRect(r.x, r.y, r.width, r.height);
		getPaintManager().addTemporaryRepaintArea(r, this);
	}

	private void paintSelected(GraphicalRepresentation selected, FGEDrawingGraphics graphics) {
		if (selected.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}

		if (!selected.getDrawControlPointsWhenSelected()) {
			// Don't paint control points in this case
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();

		if (selected instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation) selected;
			for (ControlArea ca : gr.getControlAreas()) {
				if (selected.isConnectedToDrawing()) {
					graphics.setDefaultForeground(ForegroundStyle.makeStyle(getGraphicalRepresentation().getSelectionColor()));
					paintControlArea(ca, graphics);
				}
			}
		}

		else if (selected instanceof ConnectorGraphicalRepresentation) {
			ConnectorGraphicalRepresentation<?> gr = (ConnectorGraphicalRepresentation) selected;
			// g.setColor(getGraphicalRepresentation().getSelectionColor());

			if (gr.getStartObject() == null || gr.getStartObject().isDeleted()) {
				logger.warning("Could not paint connector: start object is null or deleted");
				return;
			}

			if (gr.getEndObject() == null || gr.getEndObject().isDeleted()) {
				logger.warning("Could not paint connector: end object is null or deleted");
				return;
			}

			for (ControlArea<?> ca : gr.getControlAreas()) {
				if (selected.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		}

		if (selected.hasFloatingLabel()) {
			paintFocusedFloatingLabel(selected, graphics.g2d);
		}

		graphics.releaseClonedGraphics(oldGraphics);

	}

	private void paintFocused(GraphicalRepresentation focused, FGEDrawingGraphics graphics) {
		if (focused.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}
		if (!focused.getDrawControlPointsWhenFocused()) {
			// Don't paint control points in this case
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(ForegroundStyle.makeStyle(getGraphicalRepresentation().getFocusColor()));

		if (focused instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation) focused;
			for (ControlArea ca : gr.getControlAreas()) {
				if (focused.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		} else if (focused instanceof ConnectorGraphicalRepresentation) {
			ConnectorGraphicalRepresentation<?> gr = (ConnectorGraphicalRepresentation) focused;

			if (gr.getStartObject() == null || gr.getStartObject().isDeleted()) {
				logger.warning("Could not paint connector: start object is null or deleted");
				return;
			}

			if (gr.getEndObject() == null || gr.getEndObject().isDeleted()) {
				logger.warning("Could not paint connector: end object is null or deleted");
				return;
			}

			for (ControlArea ca : gr.getControlAreas()) {
				if (focused.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		}
		if (focused.hasFloatingLabel()) {
			paintFocusedFloatingLabel(focused, graphics.g2d);
		}
		/*
		 * else if (focused instanceof GeometricGraphicalRepresentation) {
		 * GeometricGraphicalRepresentation gr =
		 * (GeometricGraphicalRepresentation)focused; Graphics2D g2 =
		 * (Graphics2D) g; graphics.createGraphics(g2,getController());
		 * graphics.
		 * setDefaultForeground(ForegroundStyle.makeStyle(getGraphicalRepresentation
		 * ().getFocusColor()));
		 * graphics.setDefaultBackground(BackgroundStyle.makeColoredBackground
		 * (getGraphicalRepresentation().getFocusColor()));
		 * gr.getGeometricObject().paint(graphics); graphics.releaseGraphics();
		 * }
		 */

		graphics.releaseClonedGraphics(oldGraphics);
	}

	/*
	 * private void paintFocusedFloatingLabel(GraphicalRepresentation focused,
	 * Graphics g) { if (focused instanceof ShapeGraphicalRepresentation) { }
	 * else if (focused instanceof ConnectorGraphicalRepresentation) {
	 * ConnectorGraphicalRepresentation gr =
	 * (ConnectorGraphicalRepresentation)focused;
	 * g.setColor(getGraphicalRepresentation().getFocusColor()); Point p1 =
	 * getGraphicalRepresentation
	 * ().convertRemoteNormalizedPointToLocalViewCoordinates
	 * (gr.getConnector().getLabelCP1().getPoint(), focused, getScale());
	 * g.fillRect(p1.x-FGEConstants.CONTROL_POINT_SIZE,
	 * p1.y-FGEConstants.CONTROL_POINT_SIZE, FGEConstants.CONTROL_POINT_SIZE*2,
	 * FGEConstants.CONTROL_POINT_SIZE*2); Point p2 =
	 * getGraphicalRepresentation(
	 * ).convertRemoteNormalizedPointToLocalViewCoordinates
	 * (gr.getConnector().getLabelCP2().getPoint(), focused, getScale());
	 * g.fillRect(p2.x-FGEConstants.CONTROL_POINT_SIZE,
	 * p2.y-FGEConstants.CONTROL_POINT_SIZE, FGEConstants.CONTROL_POINT_SIZE*2,
	 * FGEConstants.CONTROL_POINT_SIZE*2); } }
	 */

	public Hashtable<GraphicalRepresentation, FGEView> getContents() {
		return contents;
	}

	@SuppressWarnings("unchecked")
	public <O> FGEView<O> viewForObject(GraphicalRepresentation<O> gr) {
		if (gr == getGraphicalRepresentation()) {
			return (FGEView<O>) this;
		}
		return contents.get(gr);
	}

	public <O> ShapeView<O> shapeViewForObject(ShapeGraphicalRepresentation<O> gr) {
		return (ShapeView<O>) viewForObject(gr);
	}

	public <O> ConnectorView<O> connectorViewForObject(ConnectorGraphicalRepresentation<O> gr) {
		return (ConnectorView<O>) viewForObject(gr);
	}

	public FocusRetriever getFocusRetriever() {
		return _focusRetriever;
	}

	public DrawingViewMouseListener getMouseListener() {
		return mouseListener;
	}

	private DrawingPalette activePalette;

	@Override
	public void registerPalette(DrawingPalette aPalette) {
		// A palette is registered, listen to drag'n'drop events
		logger.fine("Registering drop target");
		setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, aPalette.buildPaletteDropListener(this, _controller), true));
		activePalette = aPalette;
		for (FGEView v : contents.values()) {
			v.registerPalette(aPalette);
		}
	}

	@Override
	public FGEPaintManager getPaintManager() {
		return _paintManager;
	}

	public boolean contains(FGEView view) {
		if (view == this) {
			return true;
		}
		if (((JComponent) view).getParent() != null && ((JComponent) view).getParent() instanceof FGEView) {
			return contains(((FGEView) ((JComponent) view).getParent()));
		}
		return false;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		return getController().getToolTipText();
	}

	private boolean isDeleted = false;

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public void delete() {
		// logger.info("delete() in DrawingView");
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);

		Vector<FGEView> views = new Vector<FGEView>(contents.values());

		for (FGEView v : views) {
			v.delete();
			// logger.info("Deleted view "+v);
		}
		contents.clear();
		isDeleted = true;
	}

	private Rectangle drawnRectangle = new Rectangle();
	protected BufferedImage capturedDraggedNodeImage;

	// This call is made on the edition drawing view
	public final void paintDraggedNode(DropTargetDragEvent e, DrawingView source) {
		Point pt = SwingUtilities.convertPoint(((DropTarget) e.getSource()).getComponent(), e.getLocation(), this);
		if (source != this) {
			dragOver = activePalette.getPaletteView().dragOver; // transfer from
																// the palette
																// to the
																// edition view
		}
		if (dragOver == null) {
			return;
		}
		pt.x -= dragOver.x * getScale();
		pt.y -= dragOver.y * getScale();
		BufferedImage img = source.capturedDraggedNodeImage;
		if (pt == null || img == null || drawnRectangle != null && pt.equals(drawnRectangle.getLocation())) {
			return;
		}
		paintImmediately(drawnRectangle.getBounds());
		// System.out.println("Paint: "+drawnRectangle.getBounds()+" isDoubleBuffered="+isDoubleBuffered());
		int scaledWidth = (int) (img.getWidth() * getScale());
		int scaledHeight = (int) (img.getHeight() * getScale());
		drawnRectangle.setRect((int) pt.getX(), (int) pt.getY(), scaledWidth, scaledHeight);
		Graphics2D g = (Graphics2D) this.getGraphics().create();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_RENDER_QUALITY);
		/*
		 * g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
		 * RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		 */
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(img, (int) pt.getX(), (int) pt.getY(), scaledWidth, scaledHeight, this);
		g.dispose();
	}

	private Point dragOver;

	// This call is made on the drawing view of the palette
	public void captureDraggedNode(ShapeView<?> view, DragGestureEvent e) {
		// logger.info("Dragged node has been captured !!!!!!!!!!!!!!!!");
		capturedDraggedNodeImage = view.getScreenshot();
		dragOver = e.getDragOrigin();
	}

	public void resetCapturedNode() {
		if (drawnRectangle != null) {
			paintImmediately(drawnRectangle.getBounds());
			// capturedDraggedNodeImage = null;
		}
	}

	public DrawingPalette getActivePalette() {
		return activePalette;
	}

	private static final int margin = 20;

	@Override
	public void autoscroll(Point p) {
		JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
		if (scroll == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Not inside a scroll pane, cannot scroll!");
			}
			return;
		}
		Rectangle visible = this.getVisibleRect();
		p.x -= visible.x;
		p.y -= visible.y;
		Rectangle inner = this.getParent().getBounds();
		inner.x += margin;
		inner.y += margin;
		inner.height -= 2 * margin;
		inner.width -= 2 * margin;
		if (p.x < inner.x) {// Move Left
			JScrollBar bar = scroll.getHorizontalScrollBar();
			if (bar != null) {
				if (bar.getValue() > bar.getMinimum()) {
					bar.setValue(bar.getValue() - bar.getUnitIncrement(-1));
				}
			}
		} else if (p.x > inner.x + inner.width) { // Move right
			JScrollBar bar = scroll.getHorizontalScrollBar();
			if (bar != null) {
				if (bar.getValue() < bar.getMaximum()) {
					bar.setValue(bar.getValue() + bar.getUnitIncrement(1));
				}
			}
		}
		if (p.y < inner.y) { // Move up
			JScrollBar bar = scroll.getVerticalScrollBar();
			if (bar != null) {
				if (bar.getValue() > bar.getMinimum()) {
					bar.setValue(bar.getValue() - bar.getUnitIncrement(-1));
				}
			}
		} else if (p.y > inner.y + inner.height) { // Move down
			JScrollBar bar = scroll.getVerticalScrollBar();
			if (bar != null) {
				if (bar.getValue() < bar.getMaximum()) {
					bar.setValue(bar.getValue() + bar.getUnitIncrement(1));
				}
			}
		}
	}

	@Override
	public Insets getAutoscrollInsets() {
		Rectangle outer = getBounds();
		Rectangle inner = getParent().getBounds();
		return new Insets(inner.y - outer.y + margin, inner.x - outer.x + margin, outer.height - inner.height - inner.y + outer.y + margin,
				outer.width - inner.width - inner.x + outer.x + margin);
	}
}
