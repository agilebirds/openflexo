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
package org.openflexo.ve.shema;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.ViewShape.DropAndLinkScheme;
import org.openflexo.foundation.view.action.AddConnector;
import org.openflexo.foundation.view.action.DropSchemeAction;
import org.openflexo.foundation.view.action.LinkSchemeAction;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.LinkScheme;
import org.openflexo.localization.FlexoLocalization;

public class FloatingPalette extends ControlArea<FGERoundRectangle> implements Observer {

	private static final Logger logger = Logger.getLogger(FloatingPalette.class.getPackage().getName());

	protected static final Color OK = new Color(0, 191, 0);

	private enum Mode {
		CREATE_SHAPE_AND_LINK, LINK_ONLY;
	}

	private VEShapeGR shapeGR;
	private ViewObject target;

	private FGERoundRectangle roleRect;
	private FGERectangle edgeRect;

	/** The vertical space between two elements of the palette */
	private static final int SPACING = 5;
	/** The height of an element of the palette */
	private static final int ELEMENTS_HEIGHT = 8;
	/** The width of an element of the palette */
	private static final int ELEMENTS_WIDTH = 12;

	private static final ForegroundStyle NONE = ForegroundStyleImpl.makeNone();
	private static final BackgroundStyle DEFAULT = BackgroundStyleImpl.makeColoredBackground(Color.WHITE);
	private static final ForegroundStyle NODE_FOREGROUND = ForegroundStyleImpl.makeStyle(Color.RED, 1.0f);
	private static final ForegroundStyle EDGE_FOREGROUND = ForegroundStyleImpl.makeStyle(FGEUtils.NICE_BROWN, 1.0f);
	private static final BackgroundStyle NODE_BACKGROUND = BackgroundStyleImpl.makeColorGradientBackground(Color.ORANGE, Color.WHITE,
			ColorGradientDirection.SOUTH_EAST_NORTH_WEST);

	static {
		DEFAULT.setUseTransparency(true);
		DEFAULT.setTransparencyLevel(0.3f);
		NODE_BACKGROUND.setUseTransparency(true);
		NODE_BACKGROUND.setTransparencyLevel(0.7f);
	}

	private SimplifiedCardinalDirection orientation;

	public FloatingPalette(VEShapeGR shapeGR, ViewObject target, SimplifiedCardinalDirection orientation) {
		super(shapeGR, makeRoundRect(shapeGR, orientation));
		this.shapeGR = shapeGR;
		this.target = target;
		this.orientation = orientation;
		shapeGR.addObserver(this);
		updateElements(orientation);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected boolean isDnd = false;
	protected VEShapeGR to = null;
	protected GraphicalRepresentation<?> focusedGR;
	private VEShemaController controller;
	private FGEPoint normalizedStartPoint;

	private Rectangle previousRectangle;
	private Mode mode;

	public void paint(Graphics g, VEShemaController controller) {
		if (drawEdge && currentDraggingLocationInDrawingView != null) {
			FGEShape<?> fgeShape = shapeGR.getShape().getOutline();
			DrawingGraphicalRepresentation<?> drawingGR = controller.getDrawingGraphicalRepresentation();
			double scale = controller.getScale();
			FGEPoint nearestOnOutline = fgeShape.getNearestPoint(drawingGR.convertLocalViewCoordinatesToRemoteNormalizedPoint(
					currentDraggingLocationInDrawingView, shapeGR, scale));
			/*nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint, controller.getDrawingGraphicalRepresentation(), controller.getScale())*/
			Point fromPoint = shapeGR.convertLocalNormalizedPointToRemoteViewCoordinates(nearestOnOutline, drawingGR, scale);
			Point toPoint = currentDraggingLocationInDrawingView;

			if (mode == Mode.LINK_ONLY) {
				if (to != null && isDnd) {
					// toPoint = drawingGR.convertRemoteNormalizedPointToLocalViewCoordinates(to.getShape().getShape().getCenter(), to,
					// scale);
					g.setColor(OK);
				} else {
					g.setColor(Color.RED);
				}

			} else {
				if (isDnd) {
					g.setColor(OK);
				} else {
					g.setColor(Color.RED);
				}
			}
			g.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
			int x, y, w, h;
			if (fromPoint.x >= toPoint.x) {
				x = toPoint.x;
				w = fromPoint.x - toPoint.x;
			} else {
				x = fromPoint.x;
				w = toPoint.x - fromPoint.x;
			}
			if (fromPoint.y >= toPoint.y) {
				y = toPoint.y;
				h = fromPoint.y - toPoint.y;
			} else {
				y = fromPoint.y;
				h = toPoint.y - fromPoint.y;
			}
			previousRectangle = new Rectangle(x, y, w, h);
		}
	}

	@Override
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
		mode = null;
		if (roleRect.contains(startPoint)) {
			mode = Mode.CREATE_SHAPE_AND_LINK;
		} else if (edgeRect.contains(startPoint)) {
			mode = Mode.LINK_ONLY;
		}
		if (mode != null) {
			drawEdge = true;
			normalizedStartPoint = startPoint;
			this.controller = (VEShemaController) controller;
			((VEShemaView) controller.getDrawingView()).setFloatingPalette(this);
		} else {
			drawEdge = false;
		}
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		if (drawEdge) {
			DrawingView<?> drawingView = controller.getDrawingView();
			FGEPaintManager paintManager = drawingView.getPaintManager();
			// Attempt to repaint relevant zone only
			Rectangle oldBounds = previousRectangle;
			if (oldBounds != null) {
				oldBounds.x -= 1;
				oldBounds.y -= 1;
				oldBounds.width += 2;
				oldBounds.height += 2;
			}
			focusedGR = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
			if (focusedGR instanceof VEShapeGR && focusedGR != shapeGR) {
				to = (VEShapeGR) focusedGR;
			} else {
				to = null;
			}

			currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					controller.getDrawingView());
			if (!isDnd) {
				isDnd = shapeGR.convertLocalNormalizedPointToRemoteViewCoordinates(normalizedStartPoint,
						controller.getDrawingGraphicalRepresentation(), controller.getScale()).distance(
						currentDraggingLocationInDrawingView) > 5;
			}

			// Attempt to repaint relevant zone only
			Rectangle newBounds = getBoundsToRepaint(drawingView);
			Rectangle boundsToRepaint;
			if (oldBounds != null) {
				boundsToRepaint = oldBounds.union(newBounds);
			} else {
				boundsToRepaint = newBounds;
			}
			paintManager.repaint(drawingView, boundsToRepaint);

			// Alternative @brutal zone
			// paintManager.repaint(drawingView);

			return true;
		}
		return false;
	}

	// Attempt to repaint relevant zone only
	private Rectangle getBoundsToRepaint(DrawingView<?> drawingView) {
		ShapeView<?> fromView = drawingView.shapeViewForObject(shapeGR);
		Rectangle fromViewBounds = SwingUtilities.convertRectangle(fromView, fromView.getBounds(), drawingView);
		Rectangle boundsToRepaint = fromViewBounds;

		if (to != null) {
			ShapeView<?> toView = drawingView.shapeViewForObject(to);
			Rectangle toViewBounds = SwingUtilities.convertRectangle(toView, toView.getBounds(), drawingView);
			boundsToRepaint = fromViewBounds.union(toViewBounds);
		}

		if (currentDraggingLocationInDrawingView != null) {
			Rectangle lastLocationBounds = new Rectangle(currentDraggingLocationInDrawingView);
			boundsToRepaint = fromViewBounds.union(lastLocationBounds);
		}

		// logger.fine("boundsToRepaint="+boundsToRepaint);

		return boundsToRepaint;
	}

	@Override
	public void stopDragging(DrawingController<?> controller, GraphicalRepresentation focusedGR) {
		if (drawEdge && currentDraggingLocationInDrawingView != null && isDnd) {
			try {
				GraphicalRepresentation<?> targetGR = controller.getGraphicalRepresentation(target);
				if (targetGR == null) {
					targetGR = controller.getDrawingGraphicalRepresentation();
				}
				if (focusedGR == null) {
					focusedGR = controller.getDrawingGraphicalRepresentation();
				}
				SimplifiedCardinalDirection direction = FGEPoint.getSimplifiedOrientation(
						new FGEPoint(shapeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint,
								controller.getDrawingGraphicalRepresentation(), controller.getScale())), new FGEPoint(
								currentDraggingLocationInDrawingView));
				Point dropPoint = currentDraggingLocationInDrawingView;
				if (dropPoint.x < 0) {
					dropPoint.x = 0;
				}
				if (dropPoint.y < 0) {
					dropPoint.y = 0;
				}

				Point p = FGEUtils.convertPoint(controller.getDrawingGraphicalRepresentation(), dropPoint, focusedGR,
						controller.getScale());
				FGEPoint dropLocation = new FGEPoint(p.x / controller.getScale(), p.y / controller.getScale());
				ViewShape to = null;

				switch (mode) {
				case CREATE_SHAPE_AND_LINK:
					askAndApplyDropAndLinkScheme(dropLocation, focusedGR);
					break;
				case LINK_ONLY:
					if (this.to != null) {
						to = this.to.getDrawable();
						askAndApplyLinkScheme(dropLocation, to);
					}
					break;
				default:
					logger.warning("Not implemented !!!");
					break;
				}
				if (to == null) {
					return;
				}

			} finally {
				resetVariables();
				((VEShemaView) controller.getDrawingView()).resetFloatingPalette();
				DrawingView<?> drawingView = controller.getDrawingView();
				FGEPaintManager paintManager = drawingView.getPaintManager();
				paintManager.invalidate(drawingView.getDrawingGraphicalRepresentation());
				paintManager.repaint(drawingView.getDrawingGraphicalRepresentation());
			}
		} else {
			resetVariables();
		}
		super.stopDragging(controller, focusedGR);
	}

	private void askAndApplyDropAndLinkScheme(final FGEPoint dropLocation, GraphicalRepresentation focusedGR) {
		ViewObject container = null;
		EditionPattern targetEP = null;
		if (focusedGR == null || focusedGR instanceof VEShemaGR) {
			container = target.getShema();
			targetEP = null;
		} else if (focusedGR.getDrawable() instanceof ViewElement) {
			container = (ViewElement) focusedGR.getDrawable();
			targetEP = ((ViewElement) container).getEditionPatternReference().getEditionPattern();
		}
		if (container == null) {
			return;
		}
		if (shapeGR.getOEShape().getAvailableDropAndLinkSchemeFromThisShape(targetEP) == null
				|| shapeGR.getOEShape().getAvailableDropAndLinkSchemeFromThisShape(targetEP).size() == 0) {
			return;
		}

		if (shapeGR.getOEShape().getAvailableDropAndLinkSchemeFromThisShape(targetEP).size() == 1) {
			applyDropAndLinkScheme(shapeGR.getOEShape().getAvailableDropAndLinkSchemeFromThisShape(targetEP).firstElement(), dropLocation,
					container);
			return;
		}

		JPopupMenu popup = new JPopupMenu();
		for (final DropAndLinkScheme dropAndLinkScheme : shapeGR.getOEShape().getAvailableDropAndLinkSchemeFromThisShape(targetEP)) {
			JMenuItem menuItem = new JMenuItem(
					FlexoLocalization.localizedForKey(dropAndLinkScheme.linkScheme.getLabel() != null ? dropAndLinkScheme.linkScheme
							.getLabel() : dropAndLinkScheme.linkScheme.getName()));
			final ViewObject finalContainer = container;
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					applyDropAndLinkScheme(dropAndLinkScheme, dropLocation, finalContainer);
				}
			});
			menuItem.setToolTipText(dropAndLinkScheme.dropScheme.getDescription());
			popup.add(menuItem);
		}
		popup.show((Component) controller.getDrawingView().viewForObject(controller.getGraphicalRepresentation(target)),
				(int) dropLocation.x, (int) dropLocation.y);

	}

	private void askAndApplyLinkScheme(final FGEPoint dropLocation, final ViewShape to) {
		Vector<LinkScheme> availableConnectors = new Vector<LinkScheme>();
		// Lets look if we match a CalcPaletteConnector
		final ViewShape from = shapeGR.getDrawable();
		if (from.getShema().getCalc() != null && from.getEditionPattern() != null && to.getEditionPattern() != null) {
			availableConnectors = from.getShema().getCalc().getConnectorsMatching(from.getEditionPattern(), to.getEditionPattern());

		}

		if (availableConnectors.size() == 1) {
			LinkSchemeAction action = LinkSchemeAction.actionType.makeNewAction(from.getShema(), null, controller.getVEController()
					.getEditor());
			action.setLinkScheme(availableConnectors.firstElement());
			action.setFromShape(from);
			action.setToShape(to);
			action.escapeParameterRetrievingWhenValid = true;
			action.doAction();
		} else if (availableConnectors.size() > 1) {
			JPopupMenu popup = new JPopupMenu();
			for (final LinkScheme linkScheme : availableConnectors) {
				// final CalcPaletteConnector connector = availableConnectors.get(linkScheme);
				// System.out.println("Available: "+paletteConnector.getEditionPattern().getName());
				JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(linkScheme.getLabel() != null ? linkScheme.getLabel()
						: linkScheme.getName()));
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// System.out.println("Action "+paletteConnector.getEditionPattern().getName());
						LinkSchemeAction action = LinkSchemeAction.actionType.makeNewAction(from.getShema(), null, controller
								.getVEController().getEditor());
						action.setLinkScheme(linkScheme);
						action.setFromShape(from);
						action.setToShape(to);
						action.escapeParameterRetrievingWhenValid = true;
						action.doAction();
					}
				});
				menuItem.setToolTipText(linkScheme.getDescription());
				popup.add(menuItem);
			}
			/*JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey("graphical_connector_only"));
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AddConnector action = AddConnector.actionType.makeNewAction(shapeGR.getDrawable(), null, (controller).getOEController()
							.getEditor());
					action.setToShape(to);
					action.setAutomaticallyCreateConnector(true);
					action.doAction();
				}
			});
			menuItem.setToolTipText(FlexoLocalization.localizedForKey("draw_basic_graphical_connector_without_ontologic_semantic"));
			popup.add(menuItem);*/
			popup.show((Component) controller.getDrawingView().viewForObject(controller.getGraphicalRepresentation(target)),
					(int) dropLocation.x, (int) dropLocation.y);
		} else {
			AddConnector action = AddConnector.actionType.makeNewAction(shapeGR.getDrawable(), null, controller.getVEController()
					.getEditor());
			action.setToShape(to);
			action.setAutomaticallyCreateConnector(true);
			action.doAction();
		}

	}

	protected void applyDropAndLinkScheme(final DropAndLinkScheme dropAndLinkScheme, final FGEPoint dropLocation, ViewObject container) {
		applyDropAndLinkScheme(dropAndLinkScheme.dropScheme, dropAndLinkScheme.linkScheme, dropLocation, container);
	}

	/*protected void applyDropAndLinkScheme(final DropAndLinkScheme dropAndLinkScheme, final FGEPoint dropLocation) {
		Vector<DropScheme> allDS = findCompatibleDropSchemes(linkScheme);
		if (allDS.size() == 0) {
			return;
		} else if (allDS.size() == 1) {
			applyDropAndLinkScheme(allDS.firstElement(), linkScheme, dropLocation);
			return;
		} else {
			JPopupMenu popup = new JPopupMenu();
			for (final DropScheme dropScheme : allDS) {
				JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(dropScheme.getLabel() != null ? dropScheme.getLabel()
						: dropScheme.getName()));
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applyDropAndLinkScheme(dropScheme, linkScheme, dropLocation);
					}
				});
				menuItem.setToolTipText(linkScheme.getDescription());
				popup.add(menuItem);
			}
			popup.show((Component) controller.getDrawingView().viewForObject(controller.getGraphicalRepresentation(target)),
					(int) dropLocation.x, (int) dropLocation.y);
		}
	}*/

	protected void applyDropAndLinkScheme(DropScheme dropScheme, LinkScheme linkScheme, FGEPoint dropLocation, ViewObject container) {

		logger.info("applyDropAndLinkScheme container=" + container);

		ViewShape newShape = createNewShape(dropLocation, container, dropScheme);

		if (newShape != null) {
			createNewConnector(shapeGR.getDrawable(), newShape, linkScheme);
			controller.setSelectedObject(controller.getGraphicalRepresentation(newShape));
		}
	}

	/*private Vector<DropScheme> findCompatibleDropSchemes(LinkScheme linkScheme) {
		Vector<DropScheme> dropSchemes = new Vector<DropScheme>();
		EditionPattern targetEditionPattern = linkScheme.getToTargetEditionPattern();
		return targetEditionPattern.getDropSchemes();
	}*/

	private void resetVariables() {
		drawEdge = false;
		isDnd = false;
		to = null;
		currentDraggingLocationInDrawingView = null;
	}

	private ViewShape createNewShape(FGEPoint dropLocation, ViewObject container, DropScheme dropScheme) {

		DropSchemeAction dropSchemeAction = DropSchemeAction.actionType.makeNewAction(container, null, controller.getVEController()
				.getEditor());
		dropSchemeAction.setDropScheme(dropScheme);
		dropSchemeAction.escapeParameterRetrievingWhenValid = true;
		dropSchemeAction.doAction();

		if (dropSchemeAction.getPrimaryShape() != null) {

			GraphicalRepresentation<?> targetGR = controller.getDrawing().getGraphicalRepresentation(target);

			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) controller.getGraphicalRepresentation(dropSchemeAction
					.getPrimaryShape());

			double xOffset = 0;
			double yOffset = 0;
			if (gr != null) {
				if (gr.getBorder() != null) {
					xOffset -= gr.getBorder().getLeft();
					yOffset -= gr.getBorder().getTop();
				}
				xOffset -= gr.getWidth() / 2;
				yOffset -= gr.getHeight() / 2;
				gr.setX(dropLocation.x + xOffset);
				gr.setY(dropLocation.y + yOffset);
			}

		}
		return dropSchemeAction.getPrimaryShape();
	}

	private ViewConnector createNewConnector(ViewShape from, ViewShape to, LinkScheme linkScheme) {

		LinkSchemeAction linkSchemeAction = LinkSchemeAction.actionType.makeNewAction(from.getShema(), null, controller.getVEController()
				.getEditor());
		linkSchemeAction.setLinkScheme(linkScheme);
		linkSchemeAction.setFromShape(from);
		linkSchemeAction.setToShape(to);
		linkSchemeAction.escapeParameterRetrievingWhenValid = true;
		linkSchemeAction.doAction();

		return linkSchemeAction.getNewConnector();

	}

	@Override
	public Rectangle paint(FGEGraphics drawingGraphics) {
		// System.out.println("Focused:"+nodeGR.getIsFocused());
		if (shapeGR.getIsSelected() && !shapeGR.getIsFocused()) {
			return null;
		}
		if (/*nodeGR.getIsSelected() ||*/shapeGR.isResizing() || shapeGR.isMoving()) {
			return null;
		}
		AffineTransform at = FGEUtils.convertNormalizedCoordinatesAT(shapeGR,
				drawingGraphics.getGraphicalRepresentation());

		Graphics2D oldGraphics = drawingGraphics.cloneGraphics();

		drawingGraphics.setDefaultForeground(NONE);
		drawingGraphics.setDefaultBackground(DEFAULT);
		FGERoundRectangle paletteRect = (FGERoundRectangle) getArea().transform(at);
		FGERoundRectangle nodeRect = (FGERoundRectangle) this.roleRect.transform(at);
		FGERectangle edgeRect = (FGERectangle) this.edgeRect.transform(at);
		double arrowSize = 4/** drawingGraphics.getScale() */
		;

		paletteRect.paint(drawingGraphics);

		// 1. Node
		drawingGraphics.setDefaultForeground(NODE_FOREGROUND);
		drawingGraphics.setDefaultBackground(NODE_BACKGROUND);
		nodeRect.paint(drawingGraphics);

		// 2. Edge
		drawingGraphics.setDefaultForeground(EDGE_FOREGROUND);
		// drawingGraphics.setDefaultBackground(EDGE_BACKGROUND);
		drawingGraphics.useDefaultForegroundStyle();
		// drawingGraphics.useDefaultBackgroundStyle();
		FGEPoint eastPt, westPt, northPt, southPt;
		switch (orientation) {
		case EAST:
			eastPt = edgeRect.getEastPt();
			westPt = edgeRect.getWestPt();
			drawingGraphics.drawLine(westPt.x, westPt.y, eastPt.x - arrowSize, eastPt.y);
			drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + 1, eastPt.x - arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1);
			drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + 1, eastPt.x, eastPt.y);
			drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1, eastPt.x, eastPt.y);
			break;
		case WEST:
			eastPt = edgeRect.getEastPt();
			westPt = edgeRect.getWestPt();
			drawingGraphics.drawLine(eastPt.x, eastPt.y, edgeRect.x + arrowSize, eastPt.y);
			drawingGraphics.drawLine(edgeRect.x + arrowSize, edgeRect.y + 1, edgeRect.x + arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1);
			drawingGraphics.drawLine(edgeRect.x + arrowSize, edgeRect.y + 1, westPt.x, westPt.y);
			drawingGraphics.drawLine(edgeRect.x + arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1, westPt.x, westPt.y);
			break;
		case NORTH:
			northPt = edgeRect.getNorthPt();
			southPt = edgeRect.getSouthPt();
			drawingGraphics.drawLine(southPt.x, southPt.y, southPt.x, edgeRect.y + arrowSize);
			drawingGraphics.drawLine(edgeRect.x + 2, edgeRect.y + arrowSize, edgeRect.x + ELEMENTS_WIDTH - 2, edgeRect.y + arrowSize);
			drawingGraphics.drawLine(edgeRect.x + 2, edgeRect.y + arrowSize, northPt.x, northPt.y);
			drawingGraphics.drawLine(edgeRect.x + ELEMENTS_WIDTH - 2, edgeRect.y + arrowSize, northPt.x, northPt.y);
			break;
		case SOUTH:
			northPt = edgeRect.getNorthPt();
			southPt = edgeRect.getSouthPt();
			drawingGraphics.drawLine(northPt.x, northPt.y, northPt.x, southPt.y - arrowSize);
			drawingGraphics.drawLine(edgeRect.x + 2, southPt.y - arrowSize, edgeRect.x + ELEMENTS_WIDTH - 2, southPt.y - arrowSize);
			drawingGraphics.drawLine(edgeRect.x + 2, southPt.y - arrowSize, southPt.x, southPt.y);
			drawingGraphics.drawLine(edgeRect.x + ELEMENTS_WIDTH - 2, southPt.y - arrowSize, southPt.x, southPt.y);
			break;

		default:
			break;
		}

		drawingGraphics.releaseClonedGraphics(oldGraphics);
		return drawingGraphics.getGraphicalRepresentation().convertNormalizedRectangleToViewCoordinates(paletteRect.getBoundingBox(),
				drawingGraphics.getScale());

	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == shapeGR) {
			if (arg instanceof ObjectResized) {
				updateElements(orientation);
			}
		}
	}

	private int PALETTE_WIDTH = 16;
	private int PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;

	private void updateElements(SimplifiedCardinalDirection orientation) {
		setArea(makeRoundRect(shapeGR, orientation));
		AffineTransform at = AffineTransform.getScaleInstance(1 / shapeGR.getWidth(), 1 / shapeGR.getHeight());

		if (orientation == SimplifiedCardinalDirection.EAST || orientation == SimplifiedCardinalDirection.WEST) {
			PALETTE_WIDTH = ELEMENTS_WIDTH + 4;
			PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;
		} else if (orientation == SimplifiedCardinalDirection.NORTH || orientation == SimplifiedCardinalDirection.SOUTH) {
			PALETTE_WIDTH = 2 * ELEMENTS_WIDTH + 3 * SPACING;
			PALETTE_HEIGHT = ELEMENTS_HEIGHT + 4;
		}

		switch (orientation) {
		case EAST:
			roleRect = (FGERoundRectangle) new FGERoundRectangle(shapeGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2 + 0.5,
					(shapeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED)
					.transform(at);
			edgeRect = (FGERectangle) new FGERectangle(shapeGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2,
					(shapeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING + (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
					Filling.FILLED).transform(at);
			break;
		case WEST:
			roleRect = (FGERoundRectangle) new FGERoundRectangle(-SPACING - ELEMENTS_WIDTH, (shapeGR.getHeight() - PALETTE_HEIGHT) / 2
					+ SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED).transform(at);
			edgeRect = (FGERectangle) new FGERectangle(-SPACING - ELEMENTS_WIDTH, (shapeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING
					+ (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
			break;
		case NORTH:
			roleRect = (FGERoundRectangle) new FGERoundRectangle((shapeGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING, -SPACING
					- ELEMENTS_HEIGHT, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED).transform(at);
			edgeRect = (FGERectangle) new FGERectangle((shapeGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING + (SPACING + ELEMENTS_WIDTH),
					-SPACING - ELEMENTS_HEIGHT, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
			break;
		case SOUTH:
			roleRect = (FGERoundRectangle) new FGERoundRectangle((shapeGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING, shapeGR.getHeight()
					+ SPACING + (PALETTE_HEIGHT - ELEMENTS_HEIGHT) / 2 + 0.5, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED)
					.transform(at);
			edgeRect = (FGERectangle) new FGERectangle((shapeGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING + (SPACING + ELEMENTS_WIDTH),
					shapeGR.getHeight() + SPACING + (PALETTE_HEIGHT - ELEMENTS_HEIGHT) / 2 + 0.5, ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
					Filling.FILLED).transform(at);
			break;

		default:
			break;
		}

	}

	private static FGERoundRectangle makeRoundRect(VEShapeGR shapeGR, SimplifiedCardinalDirection orientation) {
		double x, y, width, height;
		int PALETTE_WIDTH = 0, PALETTE_HEIGHT = 0;

		if (orientation == SimplifiedCardinalDirection.EAST || orientation == SimplifiedCardinalDirection.WEST) {
			PALETTE_WIDTH = ELEMENTS_WIDTH + 4;
			PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;
		} else if (orientation == SimplifiedCardinalDirection.NORTH || orientation == SimplifiedCardinalDirection.SOUTH) {
			PALETTE_WIDTH = 2 * ELEMENTS_WIDTH + 3 * SPACING;
			PALETTE_HEIGHT = ELEMENTS_HEIGHT + 4;
		}

		switch (orientation) {
		case EAST:
			x = shapeGR.getWidth() + SPACING;
			y = (shapeGR.getHeight() - PALETTE_HEIGHT) / 2;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(), height
					/ shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
		case WEST:
			x = -SPACING - ELEMENTS_WIDTH;
			y = (shapeGR.getHeight() - PALETTE_HEIGHT) / 2;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(), height
					/ shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
		case NORTH:
			x = (shapeGR.getWidth() - PALETTE_WIDTH) / 2;
			y = -SPACING - ELEMENTS_HEIGHT;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(), height
					/ shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
		case SOUTH:
			x = (shapeGR.getWidth() - PALETTE_WIDTH) / 2;
			y = shapeGR.getHeight() + SPACING;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(), height
					/ shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
		default:
			return null;
		}
	}

}
