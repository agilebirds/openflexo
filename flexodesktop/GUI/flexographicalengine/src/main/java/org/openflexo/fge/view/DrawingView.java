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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.DrawingNeedsToBeRedrawn;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingController.EditorTool;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.RectangleSelectingAction;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.notifications.NodeDeleted;
import org.openflexo.fge.notifications.NodeRemoved;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.view.listener.DrawingViewMouseListener;
import org.openflexo.fge.view.listener.FocusRetriever;
import org.openflexo.swing.MouseResizer;

/**
 * The DrawingView is the SWING implementation of the root pane of a FGE graphical editor<br>
 * The managing of the DrawingView is performed by the {@link DrawingController}.
 * 
 * @author sylvain
 * 
 * @param <M>
 *            the type of represented model
 */
@SuppressWarnings("serial")
public class DrawingView<M> extends FGELayeredView<M> implements Autoscroll {

	private static final Logger logger = Logger.getLogger(DrawingView.class.getPackage().getName());

	private Drawing<M> drawing;
	// private Map<DrawingTreeNode<?, ?>, FGEView<?>> contents;
	private DrawingController<?> _controller;
	private FocusRetriever _focusRetriever;
	private FGEPaintManager _paintManager;

	private MouseResizer resizer;
	private DrawingViewMouseListener<M> mouseListener;

	protected FGEDrawingGraphics graphics;

	private static final FGEModelFactory PAINT_FACTORY = FGEUtils.TOOLS_FACTORY;

	private Rectangle drawnRectangle = new Rectangle();
	private BufferedImage capturedDraggedNodeImage;
	private Point capturedNodeLocation;
	private Point dragOrigin;

	private long cumulatedRepaintTime = 0;

	private boolean isBuffering = false;
	private boolean bufferingHasBeenStartedAgain = false;

	private boolean paintTemporary;

	private RectangleSelectingAction _rectangleSelectingAction;

	private static final int margin = 20;

	private boolean isDeleted = false;

	public DrawingView(DrawingController<M> controller) {
		_controller = controller;
		drawing = controller.getDrawing();
		drawing.getRoot().getGraphicalRepresentation().updateBindingModel();
		// contents = new Hashtable<DrawingTreeNode<?, ?>, FGEView<?>>();
		graphics = new FGEDrawingGraphics(drawing.getRoot());
		_focusRetriever = new FocusRetriever(this);
		if (drawing.getRoot().getGraphicalRepresentation().isResizable()) {
			resizer = new DrawingViewResizer();
		}
		mouseListener = makeDrawingViewMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		installKeyBindings();
		resizeView();
		drawing.getRoot().addObserver(this);

		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof GeometricNode<?>) {
				((GeometricNode<?>) dtn).addObserver(this);
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
		// GPO: no LayoutManager here, so next line is useless?
		revalidate();
	}

	@Override
	public DrawingTreeNode<M, ?> getNode() {
		return drawing.getRoot();
	}

	private void installKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "move_left");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "move_right");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "move_up");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "move_down");
		getActionMap().put("move_left", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getController().leftKeyPressed();
			}
		});
		getActionMap().put("move_right", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getController().rightKeyPressed();
			}
		});
		getActionMap().put("move_up", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getController().upKeyPressed();
			}
		});
		getActionMap().put("move_down", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getController().downKeyPressed();
			}
		});
	}

	public Drawing<M> getDrawing() {
		return drawing;
	}

	@Override
	public M getDrawable() {
		return getModel();
	}

	public M getModel() {
		return drawing.getModel();
	}

	@Override
	public DrawingView<M> getDrawingView() {
		return this;
	}

	@Override
	public LabelView<M> getLabelView() {
		return null;
	}

	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return drawing.getRoot().getGraphicalRepresentation();
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	public Integer getLayer() {
		return JLayeredPane.DEFAULT_LAYER;
	}

	public Dimension getComputedMinimumSize() {
		Rectangle r = getBounds();
		for (int i = 0; i < getComponents().length; i++) {
			Component c = getComponents()[i];
			r = r.union(c.getBounds());
		}
		return r.getSize();
	}

	@Override
	public void rescale() {
		for (FGEView<?> v : _controller.getContents().values()) {
			if (!(v instanceof DrawingView)) {
				v.rescale();
			}
			if (v.getLabelView() != null) {
				v.getLabelView().rescale();
			}
		}
		resizeView();
		// revalidate();
		getPaintManager().invalidate(drawing.getRoot());
		getPaintManager().repaint(this);
	}

	private void resizeView() {
		int offset = getGraphicalRepresentation().isResizable() ? 20 : 0;
		setPreferredSize(new Dimension(drawing.getRoot().getViewWidth(getController().getScale()) + offset, drawing.getRoot()
				.getViewHeight(getController().getScale()) + offset));
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

	protected DrawingViewMouseListener<M> makeDrawingViewMouseListener() {
		return new DrawingViewMouseListener<M>(this);
	}

	@Override
	public DrawingController<?> getController() {
		return _controller;
	}

	@Override
	public void update(final Observable o, final Object notification) {
		if (isDeleted) {
			logger.warning("Received notifications for deleted view: observable=" + o);
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(o, notification);
				}
			});
		} else {
			// logger.info("Received: "+notification);

			if (notification instanceof FGENotification) {
				FGENotification notif = (FGENotification) notification;
				if (notification instanceof NodeAdded) {
					handleNodeAdded((NodeAdded) notification);
				} else if (notification instanceof NodeRemoved) {
					handleNodeRemoved((NodeRemoved) notification);
				} else if (notification instanceof NodeDeleted) {
					handleNodeDeleted((NodeDeleted) notification);
				}
				/*if (notification instanceof NodeAdded) {
					GraphicalRepresentation newGR = ((NodeAdded) notification).getAddedGraphicalRepresentation();
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
				} else if (notification instanceof NodeRemoved) {
					GraphicalRepresentation removedGR = ((NodeRemoved) notification).getRemovedGraphicalRepresentation();
					if (removedGR instanceof ShapeGraphicalRepresentation) {
						ShapeView view = shapeViewForNode((ShapeGraphicalRepresentation) removedGR);
						if (view != null) {
							remove(view);
							revalidate();
							getPaintManager().invalidate(getDrawingGraphicalRepresentation());
							getPaintManager().repaint(this);
						} else {
							// That may happen, remove warning
							// logger.warning("Cannot find view for " + removedGR);
						}
					} else if (removedGR instanceof ConnectorGraphicalRepresentation) {
						ConnectorView view = connectorViewForNode((ConnectorGraphicalRepresentation) removedGR);
						if (view != null) {
							remove(view);
							revalidate();
							getPaintManager().invalidate(getDrawingGraphicalRepresentation());
							getPaintManager().repaint(this);
						} else {
							// That may happen, remove warning
							// logger.warning("Cannot find view for " + removedGR);
						}
					} else if (removedGR instanceof GeometricGraphicalRepresentation) {
						removedGR.deleteObserver(this);
						revalidate();
						getPaintManager().invalidate(getDrawingGraphicalRepresentation());
						getPaintManager().repaint(this);
					}
				}*/else if (notification instanceof ObjectResized) {
					rescale();
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				} else if (notif.getParameter() == DrawingGraphicalRepresentation.BACKGROUND_COLOR) {
					getPaintManager().invalidate(getDrawing().getRoot());
					updateBackground();
					getPaintManager().repaint(this);
				} else if (notif.getParameter() == DrawingGraphicalRepresentation.DRAW_WORKING_AREA) {
					getPaintManager().invalidate(getDrawing().getRoot());
					updateBackground();
					getPaintManager().repaint(this);
				} else if (notif.getParameter() == DrawingGraphicalRepresentation.WIDTH) {
					rescale();
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				} else if (notif.getParameter() == DrawingGraphicalRepresentation.HEIGHT) {
					rescale();
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				} else if (notif.getParameter() == DrawingGraphicalRepresentation.IS_RESIZABLE) {
					if (getDrawing().getRoot().getGraphicalRepresentation().isResizable()) {
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
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				} else if (o instanceof GeometricGraphicalRepresentation) {
					getPaintManager().invalidate(getDrawing().getRoot());
					getPaintManager().repaint(this);
				}
			}
		}
	}

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
		getDrawing().getRoot().paint(g, getController());
	}

	/**
	 * 
	 * @param g
	 *            graphics on which buffering will be performed
	 */
	protected synchronized void prepareForBuffering(Graphics2D g) {
		isBuffering = true;
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

	private void forcePaintTemporaryObjects(ContainerNode<?, ?> parentNode, Graphics g) {
		forcePaintObjects(parentNode, g, true);
	}

	/*private void forcePaintObjects(ContainerNode<?, ?> parentNode, Graphics g) {
		forcePaintObjects(parentNode, g, false);
	}*/

	private void forcePaintObjects(ContainerNode<?, ?> parentNode, final Graphics g, boolean temporaryObjectsOnly) {
		List<? extends DrawingTreeNode<?, ?>> containedGR = parentNode.getChildNodes();
		if (containedGR == null) {
			return;
		}
		paintTemporary = true;
		for (DrawingTreeNode<?, ?> node : new ArrayList<DrawingTreeNode<?, ?>>(containedGR)) {
			if (node.shouldBeDisplayed()
					&& (!temporaryObjectsOnly || getPaintManager().isTemporaryObject(node) || getPaintManager().containsTemporaryObject(
							node))) {
				FGEView<?> view = viewForNode(node);
				if (view == null) {
					continue;
				}
				Component viewAsComponent = (Component) view;
				Graphics childGraphics = g.create(viewAsComponent.getX(), viewAsComponent.getY(), viewAsComponent.getWidth(),
						viewAsComponent.getHeight());
				if (getPaintManager().isTemporaryObject(node) || !temporaryObjectsOnly) {
					if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
						FGEPaintManager.paintPrimitiveLogger.fine("DrawingView: continuous painting, paint " + node
								+ " temporaryObjectsOnly=" + temporaryObjectsOnly);
					}
					node.paint(childGraphics, getController());
					LabelView<?> labelView = view.getLabelView();
					if (labelView != null) {
						Graphics labelGraphics = g.create(labelView.getX(), labelView.getY(), labelView.getWidth(), labelView.getHeight());
						// Tricky area: if label is currently being edited,
						// call to paint is required here
						// to paint text component above buffer image.
						// Otherwise, just call doPaint to force paint label
						labelView.paint(labelGraphics);
						labelGraphics.dispose();
					}
					// do the job for childs
					if (node instanceof ContainerNode) {
						forcePaintObjects((ContainerNode<?, ?>) node, childGraphics, false);
					}
				} else {
					// do the job for childs
					if (node instanceof ContainerNode) {
						forcePaintObjects((ContainerNode<?, ?>) node, childGraphics, true);
					}
				}
			}
		}
		paintTemporary = false;
	}

	public boolean isPaintTemporary() {
		return paintTemporary;
	}

	@Override
	public synchronized void paint(Graphics g) {
		if (isDeleted()) {
			return;
		}
		long startTime = System.currentTimeMillis();
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (isBuffering) {
				// Buffering painting
				if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
					FGEPaintManager.paintPrimitiveLogger.fine("DrawingView: Paint for image buffering area, clip=" + g.getClipBounds());
				}
				super.paint(g);
				if (bufferingHasBeenStartedAgain) {
					g.clearRect(0, 0, drawing.getRoot().getViewWidth(getController().getScale()),
							drawing.getRoot().getViewHeight(getController().getScale()));
					super.paint(g);
					bufferingHasBeenStartedAgain = false;
				}
			} else {
				if (getPaintManager().renderUsingBuffer((Graphics2D) g, g.getClipBounds(), drawing.getRoot(), getScale())) {
					// Now, we still have to paint objects that are declared
					// to be temporary and continuously to be redrawn
					forcePaintTemporaryObjects(drawing.getRoot(), g);
				} else {
					// This failed for some reasons (eg rendering request
					// outside cached image)
					// Skip buffering and perform normal rendering
					super.paint(g);
				}
				paintCapturedNode(g);
			}
		} else {
			// Normal painting
			super.paint(g);
		}

		paintGeometricObjects(g);

		if (!isBuffering) {

			FGEDrawingGraphics graphics = drawing.getRoot().getGraphics();
			Graphics2D g2 = (Graphics2D) g;
			graphics.createGraphics(g2, getController());

			// Don't paint those things in case of buffering
			for (DrawingTreeNode<?, ?> o : new ArrayList<DrawingTreeNode<?, ?>>(getController().getFocusedObjects())) {
				// logger.info("Paint focused " + o);
				paintFocused(o, graphics);
			}

			for (DrawingTreeNode<?, ?> o : new ArrayList<DrawingTreeNode<?, ?>>(getController().getSelectedObjects())) {
				// logger.info("Paint selected " + o + "shouldBeDisplayed=" + o.shouldBeDisplayed());
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

			if (getController().getCurrentTool() == EditorTool.DrawShapeTool) {
				// logger.info("Painting current edited shape");
				paintCurrentEditedShape(graphics);
			}

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

	private void paintGeometricObjects(Graphics g) {
		List<GeometricNode<?>> geomList = new ArrayList<GeometricNode<?>>();
		for (Object n : drawing.getRoot().getChildNodes()) {
			if (n instanceof GeometricNode) {
				geomList.add((GeometricNode<?>) n);
			}
		}
		if (geomList.size() > 0) {
			Collections.sort(geomList, new Comparator<GeometricNode<?>>() {
				@Override
				public int compare(GeometricNode<?> o1, GeometricNode<?> o2) {
					return o1.getGraphicalRepresentation().getLayer() - o2.getGraphicalRepresentation().getLayer();
				}
			});
			for (GeometricNode<?> gn : geomList) {
				gn.paint(g, getController());
			}
		}
	}

	private void paintFocusedFloatingLabel(DrawingTreeNode<?, ?> focusedFloatingLabel, Graphics g) {
		Color color = Color.BLACK;
		if (focusedFloatingLabel.getIsSelected()) {
			color = getGraphicalRepresentation().getSelectionColor();
		} else if (focusedFloatingLabel.getIsFocused()) {
			color = getGraphicalRepresentation().getFocusColor();
		} else {
			return;
		}
		FGEView<?> view = viewForNode(focusedFloatingLabel);
		LabelView<?> labelView = view.getLabelView();
		if (labelView != null) {
			Point p1 = SwingUtilities.convertPoint(labelView, new Point(0, labelView.getHeight() / 2), this);
			Point p2 = SwingUtilities.convertPoint(labelView, new Point(labelView.getWidth(), labelView.getHeight() / 2), this);
			paintControlPoint(p1, color, g);
			paintControlPoint(p2, color, g);
		}
	}

	public void paintControlArea(ControlArea<?> ca, FGEDrawingGraphics graphics) {
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

	private void paintSelected(DrawingTreeNode<?, ?> selected, FGEDrawingGraphics graphics) {

		if (selected.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}

		if (!selected.getGraphicalRepresentation().getDrawControlPointsWhenSelected()) {
			// Don't paint control points in this case
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(PAINT_FACTORY.makeForegroundStyle(getGraphicalRepresentation().getSelectionColor()));

		if (selected instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) selected;
			for (ControlArea<?> ca : shapeNode.getControlAreas()) {
				if (selected.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		}

		else if (selected instanceof ConnectorNode) {

			ConnectorNode<?> connectorNode = (ConnectorNode<?>) selected;
			// g.setColor(getGraphicalRepresentation().getSelectionColor());

			if (connectorNode.getStartNode() == null || connectorNode.getStartNode().isDeleted()) {
				logger.warning("Could not paint connector: start object is null or deleted");
				return;
			}

			if (connectorNode.getEndNode() == null || connectorNode.getEndNode().isDeleted()) {
				logger.warning("Could not paint connector: end object is null or deleted");
				return;
			}

			for (ControlArea<?> ca : connectorNode.getControlAreas()) {
				if (selected.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		}

		if (selected.hasFloatingLabel()) {
			paintFocusedFloatingLabel(selected, graphics.getGraphics());
		}

		graphics.releaseClonedGraphics(oldGraphics);

	}

	private void paintCurrentEditedShape(FGEDrawingGraphics graphics) {

		// logger.info("Painting current edited shape");
		/*GeometricGraphicalRepresentation currentEditedShape = getController().getDrawShapeToolController().getCurrentEditedShapeGR();

		if (currentEditedShape.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}*/

		if (!getController().getDrawShapeToolController().editionHasBeenStarted()) {
			return;
		}

		getController().getDrawShapeToolController().paintCurrentEditedShape(graphics);

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(PAINT_FACTORY.makeForegroundStyle(getGraphicalRepresentation().getFocusColor()));

		for (ControlArea<?> ca : getController().getDrawShapeToolController().getControlAreas()) {
			paintControlArea(ca, graphics);
		}

		graphics.releaseClonedGraphics(oldGraphics);
	}

	private void paintFocused(DrawingTreeNode<?, ?> focused, FGEDrawingGraphics graphics) {
		if (focused.isDeleted()) {
			logger.warning("Cannot paint for a deleted GR");
			return;
		}
		if (!focused.getGraphicalRepresentation().getDrawControlPointsWhenFocused()) {
			// Don't paint control points in this case
			return;
		}

		Graphics2D oldGraphics = graphics.cloneGraphics();
		graphics.setDefaultForeground(PAINT_FACTORY.makeForegroundStyle(getGraphicalRepresentation().getFocusColor()));

		if (focused instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) focused;
			for (ControlArea<?> ca : shapeNode.getControlAreas()) {
				if (focused.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		} else if (focused instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) focused;

			if (connectorNode.getStartNode() == null || connectorNode.getStartNode().isDeleted()) {
				logger.warning("Could not paint connector: start object is null or deleted");
				return;
			}

			if (connectorNode.getEndNode() == null || connectorNode.getEndNode().isDeleted()) {
				logger.warning("Could not paint connector: end object is null or deleted");
				return;
			}

			for (ControlArea<?> ca : connectorNode.getControlAreas()) {
				if (focused.isConnectedToDrawing()) {
					paintControlArea(ca, graphics);
				}
			}
		}
		if (focused.hasFloatingLabel()) {
			paintFocusedFloatingLabel(focused, graphics.getGraphics());
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

	/*public Map<DrawingTreeNode<?, ?>, FGEView<?>> getContents() {
		return contents;
	}*/

	public <O> FGEView<?> viewForNode(DrawingTreeNode<?, ?> node) {
		return _controller.viewForNode(node);
	}

	public ShapeView<?> shapeViewForNode(ShapeNode<?> node) {
		return (ShapeView<?>) viewForNode(node);
	}

	public ConnectorView<?> connectorViewForNode(ConnectorNode<?> node) {
		return (ConnectorView<?>) viewForNode(node);
	}

	public FocusRetriever getFocusRetriever() {
		return _focusRetriever;
	}

	public DrawingViewMouseListener<M> getMouseListener() {
		return mouseListener;
	}

	private DrawingPalette activePalette;

	@Override
	public void registerPalette(DrawingPalette aPalette) {
		// A palette is registered, listen to drag'n'drop events
		logger.fine("Registering drop target");
		setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, aPalette.buildPaletteDropListener(this, _controller), true));
		activePalette = aPalette;
		for (FGEView<?> v : _controller.getContents().values()) {
			if (v != this) {
				v.registerPalette(aPalette);
			}
		}
	}

	@Override
	public FGEPaintManager getPaintManager() {
		return _paintManager;
	}

	public boolean contains(FGEView<?> view) {
		if (view == null) {
			return false;
		}
		if (view == this) {
			return true;
		}
		if (((JComponent) view).getParent() != null && ((JComponent) view).getParent() instanceof FGEView) {
			return contains((FGEView<?>) ((JComponent) view).getParent());
		}
		return false;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		return getController().getToolTipText();
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public void delete() {
		// logger.info("delete() in DrawingView");
		removeMouseListener(mouseListener);
		removeMouseMotionListener(mouseListener);

		List<FGEView<?>> views = new ArrayList<FGEView<?>>(_controller.getContents().values());

		for (FGEView<?> v : views) {
			v.delete();
			// logger.info("Deleted view "+v);
		}
		// contents.clear();
		getGraphicalRepresentation().deleteObserver(this);

		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof GeometricNode<?>) {
				((GeometricNode<?>) dtn).deleteObserver(this);
			}
		}

		isDeleted = true;
	}

	// This call is made on the edition drawing view
	public final void updateCapturedDraggedNodeImagePosition(DropTargetDragEvent e, DrawingView<?> source) {
		capturedNodeLocation = SwingUtilities.convertPoint(((DropTarget) e.getSource()).getComponent(), e.getLocation(), this);
		if (source != this) {
			dragOrigin = activePalette.getPaletteView().dragOrigin; // transfer from
			// the palette
			// to the
			// edition view
		}
		if (dragOrigin == null) {
			return;
		}
		capturedNodeLocation.x -= dragOrigin.x * getScale();
		capturedNodeLocation.y -= dragOrigin.y * getScale();
		capturedDraggedNodeImage = source.capturedDraggedNodeImage;
		if (capturedNodeLocation == null || capturedDraggedNodeImage == null || drawnRectangle != null
				&& capturedNodeLocation.equals(drawnRectangle.getLocation())) {
			return;
		}
		getPaintManager().repaint(this, drawnRectangle.getBounds());
		// System.out.println("Paint: "+drawnRectangle.getBounds()+" isDoubleBuffered="+isDoubleBuffered());
		int scaledWidth = (int) (capturedDraggedNodeImage.getWidth() * getScale());
		int scaledHeight = (int) (capturedDraggedNodeImage.getHeight() * getScale());
		drawnRectangle.setRect((int) capturedNodeLocation.getX(), (int) capturedNodeLocation.getY(), scaledWidth, scaledHeight);
		getPaintManager().repaint(this, drawnRectangle.getBounds());
	}

	private void paintCapturedNode(Graphics g) {
		if (capturedDraggedNodeImage != null && drawnRectangle != null) {
			Graphics2D g2 = (Graphics2D) g;
			// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.drawImage(capturedDraggedNodeImage, (int) drawnRectangle.getX(), (int) drawnRectangle.getY(),
					(int) drawnRectangle.getWidth(), (int) drawnRectangle.getHeight(), this);
		}
	}

	// This call is made on the drawing view of the palette
	public void captureDraggedNode(ShapeView<?> view, MouseEvent e) {
		capturedDraggedNodeImage = view.getScreenshot();
		dragOrigin = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), view);
	}

	public void captureDraggedNode(ShapeView<?> view, DragGestureEvent e) {
		capturedDraggedNodeImage = view.getScreenshot();
		dragOrigin = SwingUtilities.convertPoint(e.getComponent(), e.getDragOrigin(), view);
	}

	public void resetCapturedNode() {
		if (capturedDraggedNodeImage != null) {
			getPaintManager().repaint(this, drawnRectangle.getBounds());
			capturedDraggedNodeImage = null;
		}
	}

	public DrawingPalette getActivePalette() {
		return activePalette;
	}

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

	private final class DrawingViewResizer extends MouseResizer {
		protected DrawingViewResizer() {
			super(DrawingView.this, new MouseResizer.MouseResizerDelegate() {

				@Override
				public void resizeBy(int deltaX, int deltaY) {

				}

				@Override
				public void resizeDirectlyBy(int deltaX, int deltaY) {
					if (deltaX != 0) {
						getDrawing().getRoot().getGraphicalRepresentation()
								.setWidth(getDrawing().getRoot().getGraphicalRepresentation().getWidth() + deltaX / getScale());
					}
					if (deltaY != 0) {
						getDrawing().getRoot().getGraphicalRepresentation()
								.setHeight(getDrawing().getRoot().getGraphicalRepresentation().getHeight() + deltaY / getScale());
					}
				}
			}, ResizeMode.SOUTH, ResizeMode.EAST, ResizeMode.SOUTH_EAST);
		}

		@Override
		protected int getComponentWidth() {
			return getDrawing().getRoot().getViewWidth(getScale());
		}

		@Override
		protected int getComponentHeight() {
			return getDrawing().getRoot().getViewHeight(getScale());
		}
	}

}
