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
package org.openflexo.wkf.roleeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
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
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.action.AddRole;
import org.openflexo.foundation.wkf.action.AddRoleSpecialization;

public class FloatingPalette extends ControlArea<FGERoundRectangle> implements Observer {

	private static final Logger logger = Logger.getLogger(FloatingPalette.class.getPackage().getName());

	protected static final Color OK = new Color(0, 191, 0);

	private enum Mode {
		ROLE, SPECIALIZATION;
	}

	private RoleGR roleGR;
	private RoleList target;

	private FGERoundRectangle roleRect;
	private FGERectangle edgeRect;

	/** The vertical space between two elements of the palette */
	private static final int SPACING = 5;
	/** The height of an element of the palette */
	private static final int ELEMENTS_HEIGHT = 8;
	/** The width of an element of the palette */
	private static final int ELEMENTS_WIDTH = 12;

	private static final ForegroundStyle NONE = ForegroundStyle.makeNone();
	private static final BackgroundStyle DEFAULT = BackgroundStyle.makeColoredBackground(Color.WHITE);
	private static final ForegroundStyle NODE_FOREGROUND = ForegroundStyle.makeStyle(Color.RED, 1.0f);
	private static final ForegroundStyle EDGE_FOREGROUND = ForegroundStyle.makeStyle(FGEUtils.NICE_BROWN, 1.0f);
	private static final BackgroundStyle NODE_BACKGROUND = BackgroundStyle.makeColorGradientBackground(Color.ORANGE, Color.WHITE,
			ColorGradientDirection.SOUTH_EAST_NORTH_WEST);

	static {
		DEFAULT.setUseTransparency(true);
		DEFAULT.setTransparencyLevel(0.3f);
		NODE_BACKGROUND.setUseTransparency(true);
		NODE_BACKGROUND.setTransparencyLevel(0.7f);
	}

	private SimplifiedCardinalDirection orientation;

	public FloatingPalette(RoleGR roleGR, RoleList target, SimplifiedCardinalDirection orientation) {
		super(roleGR, makeRoundRect(roleGR, orientation));
		this.roleGR = roleGR;
		this.target = target;
		this.orientation = orientation;
		roleGR.addObserver(this);
		updateElements(orientation);
	}

	@Override
	public boolean isDraggable() {
		return roleGR.getDrawing().isEditable();
	}

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected boolean isDnd = false;
	protected RoleGR to = null;
	protected GraphicalRepresentation<?> focusedGR;
	private RoleEditorController controller;
	private FGEPoint normalizedStartPoint;

	private Rectangle previousRectangle;
	private Mode mode;

	public void paint(Graphics g, RoleEditorController controller) {
		if (drawEdge && currentDraggingLocationInDrawingView != null) {
			FGEShape<?> fgeShape = roleGR.getShape().getOutline();
			DrawingGraphicalRepresentation<?> drawingGR = controller.getDrawingGraphicalRepresentation();
			double scale = controller.getScale();
			FGEPoint nearestOnOutline = fgeShape.getNearestPoint(drawingGR.convertLocalViewCoordinatesToRemoteNormalizedPoint(
					currentDraggingLocationInDrawingView, roleGR, scale));
			/*nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint, controller.getDrawingGraphicalRepresentation(), controller.getScale())*/
			Point fromPoint = roleGR.convertLocalNormalizedPointToRemoteViewCoordinates(nearestOnOutline, drawingGR, scale);
			Point toPoint = currentDraggingLocationInDrawingView;

			if (mode == Mode.SPECIALIZATION) {
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
			mode = Mode.ROLE;
		} else if (edgeRect.contains(startPoint)) {
			mode = Mode.SPECIALIZATION;
		}
		if (mode != null) {
			drawEdge = true;
			normalizedStartPoint = startPoint;
			this.controller = (RoleEditorController) controller;
			((RoleEditorView) controller.getDrawingView()).setFloatingPalette(this);
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
			if (focusedGR instanceof RoleGR && focusedGR != roleGR) {
				to = (RoleGR) focusedGR;
			} else {
				to = null;
			}

			currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					controller.getDrawingView());
			if (!isDnd) {
				isDnd = roleGR.convertLocalNormalizedPointToRemoteViewCoordinates(normalizedStartPoint,
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
		ShapeView<?> fromView = drawingView.shapeViewForObject(roleGR);
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
	public void stopDragging(DrawingController<?> controller, GraphicalRepresentation<?> focusedGR) {
		if (drawEdge && currentDraggingLocationInDrawingView != null && isDnd) {
			try {
				GraphicalRepresentation<?> targetGR = controller.getGraphicalRepresentation(target);
				if (targetGR == null) {
					targetGR = controller.getDrawingGraphicalRepresentation();
				}
				SimplifiedCardinalDirection direction = FGEPoint.getSimplifiedOrientation(
						new FGEPoint(roleGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint,
								controller.getDrawingGraphicalRepresentation(), controller.getScale())), new FGEPoint(
								currentDraggingLocationInDrawingView));
				Point dropPoint = currentDraggingLocationInDrawingView;
				if (dropPoint.x < 0) {
					dropPoint.x = 0;
				}
				if (dropPoint.y < 0) {
					dropPoint.y = 0;
				}
				Point p = GraphicalRepresentation.convertPoint(controller.getDrawingGraphicalRepresentation(), dropPoint, targetGR,
						controller.getScale());
				FGEPoint dropLocation = new FGEPoint(p.x / controller.getScale(), p.y / controller.getScale());
				Role from = roleGR.getDrawable();
				Role to = null;
				switch (mode) {
				case ROLE:
					to = createRole(dropLocation, target, from, direction);
					break;
				case SPECIALIZATION:
					if (this.to != null) {
						to = this.to.getDrawable();
					}
					break;
				default:
					logger.warning("Not implemented !!!");
					break;
				}
				if (to == null) {
					return;
				}

				AddRoleSpecialization addRoleSpecializationAction = AddRoleSpecialization.actionType.makeNewAction(from, null,
						((RoleEditorController) controller).getEditor());
				addRoleSpecializationAction.setNewParentRole(to);
				addRoleSpecializationAction.setRoleSpecializationAutomaticallyCreated(true);
				addRoleSpecializationAction.doAction();

				if (mode == Mode.ROLE) {
					controller.setSelectedObject(controller.getGraphicalRepresentation(to));
				}

			} finally {
				resetVariables();
				((RoleEditorView) controller.getDrawingView()).resetFloatingPalette();
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

	private void resetVariables() {
		drawEdge = false;
		isDnd = false;
		to = null;
		currentDraggingLocationInDrawingView = null;
	}

	private Role createRole(FGEPoint dropLocation, RoleList container, Role from, SimplifiedCardinalDirection direction) {
		FGEPoint locationInDrawing = null;
		if (controller.getGraphicalRepresentation(container) != null) {
			locationInDrawing = dropLocation.transform(GraphicalRepresentation.convertCoordinatesAT(
					controller.getGraphicalRepresentation(container), controller.getDrawingGraphicalRepresentation(), 1.0));// gr.getLocationInDrawing();
		}

		FlexoWorkflow workflow = container.getWorkflow();
		AddRole addRole = AddRole.actionType.makeNewAction(workflow, null, controller.getWKFController().getEditor());
		addRole.setLocation(dropLocation.x, dropLocation.y);
		addRole.setNewRoleName(workflow.getRoleList().getNextNewUserRoleName());
		addRole.setRoleToClone(from);
		addRole.setRoleAutomaticallyCreated(true);
		addRole.doAction();

		if (addRole.getNewRole() != null) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) controller.getGraphicalRepresentation(addRole
					.getNewRole());
			if (locationInDrawing == null) {
				locationInDrawing = gr.getLocationInDrawing();
			}
			double xOffset = 0;
			double yOffset = 0;
			if (gr != null) {
				if (gr.getBorder() != null) {
					xOffset -= gr.getBorder().left;
					yOffset -= gr.getBorder().top;
				}
				/*switch (direction) {
				case NORTH:
					xOffset-=gr.getWidth()/2;
					yOffset-=gr.getHeight();
					break;
				case SOUTH:
					xOffset-=gr.getWidth()/2;
					break;
				case WEST:
					xOffset-=gr.getWidth();
					yOffset-=gr.getHeight()/2;
					break;
				case EAST:
					yOffset-=gr.getHeight()/2;
					break;
				default:
					break;
				}*/
				xOffset -= gr.getWidth() / 2;
				yOffset -= gr.getHeight() / 2;
				if (xOffset < 0 && -xOffset > locationInDrawing.x) {
					xOffset = -locationInDrawing.x;
				}
				if (yOffset < 0 && -yOffset > locationInDrawing.y) {
					yOffset = -locationInDrawing.y;
				}
				gr.setX(gr.getX() + xOffset);
				gr.setY(gr.getY() + yOffset);
			}
		}
		return addRole.getNewRole();
	}

	@Override
	public Rectangle paint(FGEGraphics drawingGraphics) {
		// System.out.println("Focused:"+nodeGR.getIsFocused());
		if (roleGR.getIsSelected() && !roleGR.getIsFocused()) {
			return null;
		}
		if (/*nodeGR.getIsSelected() ||*/roleGR.isResizing() || roleGR.isMoving()) {
			return null;
		}
		if (!roleGR.getDrawing().isEditable()) {
			return null;
		}
		AffineTransform at = GraphicalRepresentation.convertNormalizedCoordinatesAT(roleGR, drawingGraphics.getGraphicalRepresentation());

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
		if (o == roleGR) {
			if (arg instanceof ObjectResized) {
				updateElements(orientation);
			}
		}
	}

	private int PALETTE_WIDTH = 16;
	private int PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;

	private void updateElements(SimplifiedCardinalDirection orientation) {
		setArea(makeRoundRect(roleGR, orientation));
		AffineTransform at = AffineTransform.getScaleInstance(1 / roleGR.getWidth(), 1 / roleGR.getHeight());

		if (orientation == SimplifiedCardinalDirection.EAST || orientation == SimplifiedCardinalDirection.WEST) {
			PALETTE_WIDTH = ELEMENTS_WIDTH + 4;
			PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;
		} else if (orientation == SimplifiedCardinalDirection.NORTH || orientation == SimplifiedCardinalDirection.SOUTH) {
			PALETTE_WIDTH = 2 * ELEMENTS_WIDTH + 3 * SPACING;
			PALETTE_HEIGHT = ELEMENTS_HEIGHT + 4;
		}

		switch (orientation) {
		case EAST:
			roleRect = (FGERoundRectangle) new FGERoundRectangle(roleGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2 + 0.5,
					(roleGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED)
					.transform(at);
			edgeRect = (FGERectangle) new FGERectangle(roleGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2,
					(roleGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING + (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
					Filling.FILLED).transform(at);
			break;
		case WEST:
			roleRect = (FGERoundRectangle) new FGERoundRectangle(-SPACING - ELEMENTS_WIDTH, (roleGR.getHeight() - PALETTE_HEIGHT) / 2
					+ SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED).transform(at);
			edgeRect = (FGERectangle) new FGERectangle(-SPACING - ELEMENTS_WIDTH, (roleGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING
					+ (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
			break;
		case NORTH:
			roleRect = (FGERoundRectangle) new FGERoundRectangle((roleGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING, -SPACING
					- ELEMENTS_HEIGHT, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED).transform(at);
			edgeRect = (FGERectangle) new FGERectangle((roleGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING + (SPACING + ELEMENTS_WIDTH),
					-SPACING - ELEMENTS_HEIGHT, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
			break;
		case SOUTH:
			roleRect = (FGERoundRectangle) new FGERoundRectangle((roleGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING, roleGR.getHeight()
					+ SPACING + (PALETTE_HEIGHT - ELEMENTS_HEIGHT) / 2 + 0.5, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED)
					.transform(at);
			edgeRect = (FGERectangle) new FGERectangle((roleGR.getWidth() - PALETTE_WIDTH) / 2 + SPACING + (SPACING + ELEMENTS_WIDTH),
					roleGR.getHeight() + SPACING + (PALETTE_HEIGHT - ELEMENTS_HEIGHT) / 2 + 0.5, ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
					Filling.FILLED).transform(at);
			break;

		default:
			break;
		}

	}

	private static FGERoundRectangle makeRoundRect(RoleGR roleGR, SimplifiedCardinalDirection orientation) {
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
			x = roleGR.getWidth() + SPACING;
			y = (roleGR.getHeight() - PALETTE_HEIGHT) / 2;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / roleGR.getWidth(), y / roleGR.getHeight(), width / roleGR.getWidth(), height
					/ roleGR.getHeight(), 13.0 / roleGR.getWidth(), 13.0 / roleGR.getHeight(), Filling.FILLED);
		case WEST:
			x = -SPACING - ELEMENTS_WIDTH;
			y = (roleGR.getHeight() - PALETTE_HEIGHT) / 2;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / roleGR.getWidth(), y / roleGR.getHeight(), width / roleGR.getWidth(), height
					/ roleGR.getHeight(), 13.0 / roleGR.getWidth(), 13.0 / roleGR.getHeight(), Filling.FILLED);
		case NORTH:
			x = (roleGR.getWidth() - PALETTE_WIDTH) / 2;
			y = -SPACING - ELEMENTS_HEIGHT;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / roleGR.getWidth(), y / roleGR.getHeight(), width / roleGR.getWidth(), height
					/ roleGR.getHeight(), 13.0 / roleGR.getWidth(), 13.0 / roleGR.getHeight(), Filling.FILLED);
		case SOUTH:
			x = (roleGR.getWidth() - PALETTE_WIDTH) / 2;
			y = roleGR.getHeight() + SPACING;
			width = PALETTE_WIDTH;
			height = PALETTE_HEIGHT;
			return new FGERoundRectangle(x / roleGR.getWidth(), y / roleGR.getHeight(), width / roleGR.getWidth(), height
					/ roleGR.getHeight(), 13.0 / roleGR.getWidth(), 13.0 / roleGR.getHeight(), Filling.FILLED);
		default:
			return null;
		}
	}

}
