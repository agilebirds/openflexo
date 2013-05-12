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
package org.openflexo.wkf.swleditor.gr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERegularPolygon;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.CreateAssociation;
import org.openflexo.foundation.wkf.action.CreateEdge;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.swleditor.AbstractWKFPalette.WKFPaletteElement;
import org.openflexo.wkf.swleditor.SWLEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneView;

public class NodePalette extends ControlArea<FGERoundRectangle> implements SWLEditorConstants, Observer {

	protected static final Color OK = new Color(0, 191, 0);

	private enum Mode {
		NODE, EDGE, EXCLUSIVE_GATEWAY, ANNOTATION;
	}

	private WKFNodeGR<?> nodeGR;
	private FlexoPetriGraph target;

	private FGERoundRectangle nodeRect;
	private FGERectangle edgeRect;
	private FGERectangle exclusiveGatewayRect;
	private FGERectangle annotationRect;

	/** The vertical space between two elements of the palette */
	private static final int SPACING = 3;
	/** The height of an element of the palette */
	private static final int ELEMENTS_HEIGHT = 10;
	/** The width of an element of the palette */
	private static final int ELEMENTS_WIDTH = 16;

	private static final int PALETTE_WIDTH = 16;
	private static final int PALETTE_HEIGHT = 4 * ELEMENTS_HEIGHT + 5 * SPACING;

	private static final ForegroundStyle NONE = ForegroundStyle.makeNone();
	private static final BackgroundStyle DEFAULT = BackgroundStyle.makeColoredBackground(Color.WHITE);
	private static final ForegroundStyle NODE_FOREGROUND = ForegroundStyle.makeStyle(FGEUtils.NICE_DARK_GREEN, 1.0f);
	private static final ForegroundStyle EDGE_FOREGROUND = ForegroundStyle.makeStyle(FGEUtils.NICE_BROWN, 1.0f);
	private static final BackgroundStyle NODE_BACKGROUND = BackgroundStyle.makeColorGradientBackground(FGEUtils.NICE_DARK_GREEN,
			Color.WHITE, ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
	private static final ForegroundStyle OR_FOREGROUND = ForegroundStyle.makeStyle(FGEUtils.NICE_DARK_GREEN, 1.0f);

	static {
		DEFAULT.setUseTransparency(true);
		DEFAULT.setTransparencyLevel(0.3f);
		NODE_BACKGROUND.setUseTransparency(true);
		NODE_BACKGROUND.setTransparencyLevel(0.7f);
	}

	private static FGERoundRectangle makeRoundRect(WKFNodeGR<?> nodeGR) {
		double x = nodeGR.getWidth() + SPACING;
		double y = (nodeGR.getHeight() - PALETTE_HEIGHT) / 2;
		double width = PALETTE_WIDTH;
		double height = PALETTE_HEIGHT;
		return new FGERoundRectangle(x / nodeGR.getWidth(), y / nodeGR.getHeight(), width / nodeGR.getWidth(), height / nodeGR.getHeight(),
				13.0 / nodeGR.getWidth(), 13.0 / nodeGR.getHeight(), Filling.FILLED);
	}

	public NodePalette(WKFNodeGR<?> nodeGR, FlexoPetriGraph target) {
		super(nodeGR, makeRoundRect(nodeGR));
		this.nodeGR = nodeGR;
		this.target = target;
		nodeGR.addObserver(this);
		updateElements();
	}

	@Override
	public boolean isDraggable() {
		return nodeGR.getDrawing().isEditable();
	}

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected boolean isDnd = false;
	protected WKFNodeGR<?> to = null;
	protected GraphicalRepresentation<?> focusedGR;
	private DrawingController<?> controller;
	private FGEPoint normalizedStartPoint;

	private Rectangle previousRectangle;
	private Mode mode;
	private TextStyle annotationTextStyle;

	public void paint(Graphics g, SwimmingLaneEditorController controller) {
		if (drawEdge && currentDraggingLocationInDrawingView != null) {
			FGEShape<?> fgeShape = nodeGR.getShape().getOutline();
			DrawingGraphicalRepresentation<?> drawingGR = controller.getDrawingGraphicalRepresentation();
			double scale = controller.getScale();
			FGEPoint nearestOnOutline = fgeShape.getNearestPoint(drawingGR.convertLocalViewCoordinatesToRemoteNormalizedPoint(
					currentDraggingLocationInDrawingView, nodeGR, scale));
			/*nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint, controller.getDrawingGraphicalRepresentation(), controller.getScale())*/
			Point fromPoint = nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(nearestOnOutline, drawingGR, scale);
			Point toPoint = currentDraggingLocationInDrawingView;

			if (mode == Mode.EDGE) {
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
		if (nodeRect.contains(startPoint)) {
			mode = Mode.NODE;
		} else if (edgeRect.contains(startPoint)) {
			mode = Mode.EDGE;
		} else if (exclusiveGatewayRect.contains(startPoint)) {
			mode = Mode.EXCLUSIVE_GATEWAY;
		} else if (annotationRect.contains(startPoint)) {
			mode = Mode.ANNOTATION;
		}
		if (mode != null) {
			drawEdge = true;
			normalizedStartPoint = startPoint;
			this.controller = controller;
			((SwimmingLaneView) controller.getDrawingView()).setDraggedNodePalette(this);
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
			if (focusedGR instanceof WKFNodeGR && focusedGR != nodeGR) {
				to = (WKFNodeGR<?>) focusedGR;
			} else {
				to = null;
			}

			currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					controller.getDrawingView());
			if (!isDnd) {
				isDnd = nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(normalizedStartPoint,
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
		ShapeView<?> fromView = drawingView.shapeViewForObject(nodeGR);
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
				WKFNode to = null;
				WKFPaletteElement element = null;
				Role role = null;
				GraphicalRepresentation<?> targetGR = null;
				if (target.isRootPetriGraph()) {
					GraphicalRepresentation<?> gr = focusedGR;
					while (gr != null) {
						if (gr.getDrawable() instanceof Role) {
							role = (Role) gr.getDrawable();
							targetGR = gr;
							break;
						}
						gr = gr.getContainerGraphicalRepresentation();
					}
				}
				if (targetGR == null) {
					targetGR = controller.getGraphicalRepresentation(target);
				}
				if (targetGR == null) {
					targetGR = controller.getDrawingGraphicalRepresentation();
				}
				SimplifiedCardinalDirection direction = FGEPoint.getSimplifiedOrientation(
						new FGEPoint(nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint,
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
				switch (mode) {
				case NODE:
					if ((nodeGR.getDrawable() instanceof ActivityNode || nodeGR.getDrawable() instanceof OperationNode || nodeGR
							.getDrawable() instanceof ActionNode) && !((FlexoNode) nodeGR.getDrawable()).isBeginOrEndNode()) {
						DropWKFElement drop = DropWKFElement.actionType.makeNewAction(target, null,
								((SwimmingLaneEditorController) controller).getEditor());
						if (role != null) {
							drop.setRoleToAssociate(role);
						}
						drop.setObject(to = clone((FlexoNode) nodeGR.getDrawable()));
						drop.setResetNodeName(false);
						drop.setLocation(dropLocation.x, dropLocation.y);
						drop.setGraphicalContext(SWIMMING_LANE_EDITOR);
						drop.doAction();
						to = createElement(drop, dropLocation, target, direction);
					} else {
						if (target.getLevel() == FlexoLevel.ACTIVITY) {
							element = ((SwimmingLaneEditorController) controller).getBasicPalette().getNormalActivityElement();
						} else if (target.getLevel() == FlexoLevel.OPERATION) {
							element = ((SwimmingLaneEditorController) controller).getOperationPalette().getNormalOperationElement();
						} else if (target.getLevel() == FlexoLevel.ACTION) {
							element = ((SwimmingLaneEditorController) controller).getActionPalette().getFlexoActionElement();
						} else {
							return;
						}
						DropWKFElement drop = element.createAndExecuteDropElementAction(dropLocation, target, role, false);
						to = createElement(drop, dropLocation, target, direction);
					}
					break;
				case EDGE:
					if (this.to != null) {
						to = this.to.getDrawable();
					}
					break;
				case EXCLUSIVE_GATEWAY:
					if (target.getLevel() == FlexoLevel.ACTIVITY) {
						element = ((SwimmingLaneEditorController) controller).getBasicPalette().getOrOperatorElement();
					} else if (target.getLevel() == FlexoLevel.OPERATION) {
						element = ((SwimmingLaneEditorController) controller).getOperationPalette().getOrOperatorElement();
					} else if (target.getLevel() == FlexoLevel.ACTION) {
						element = ((SwimmingLaneEditorController) controller).getActionPalette().getOrOperatorElement();
					} else {
						return;
					}
					DropWKFElement drop = element.createAndExecuteDropElementAction(dropLocation, target, role, false);
					to = createElement(drop, dropLocation, target, direction);
					break;
				case ANNOTATION:
					/*if (target.getLevel()==FlexoLevel.ACTIVITY)
						element = ((SwimmingLaneEditorController)controller).getActivityPalette().getOrOperatorElement();
					else if (target.getLevel()==FlexoLevel.OPERATION)
						element = ((SwimmingLaneEditorController)controller).getOperationPalette().getOROperatorElement();
					else if (target.getLevel()==FlexoLevel.ACTION)
						element = ((SwimmingLaneEditorController)controller).getActionPalette().getOROperatorElement();
					else
						return ;*/
					drop = ((SwimmingLaneEditorController) controller).getBasicPalette().getAnnotation()
							.createAndExecuteDropElementAction(dropLocation, target, role, false);
					to = createElement(drop, dropLocation, target, direction);
					break;

				default:
					break;
				}
				if (to == null) {
					return;
				}
				if (nodeGR.getDrawable() instanceof AbstractNode && to instanceof AbstractNode) {
					CreateEdge createEdgeAction = CreateEdge.actionType.makeNewAction((AbstractNode) nodeGR.getDrawable(), null,
							((SwimmingLaneEditorController) controller).getEditor());
					createEdgeAction.setStartingNode((AbstractNode) nodeGR.getDrawable());
					createEdgeAction.setEndNode((AbstractNode) to);
					createEdgeAction.doAction();
				} else {
					CreateAssociation createEdgeAction = CreateAssociation.actionType.makeNewAction(nodeGR.getDrawable(), null,
							((SwimmingLaneEditorController) controller).getEditor());
					createEdgeAction.setStartingNode(nodeGR.getDrawable());
					createEdgeAction.setEndNode(to);
					createEdgeAction.doAction();
				}
				switch (mode) {
				case NODE:
				case EXCLUSIVE_GATEWAY:
				case ANNOTATION:
					controller.setSelectedObject(controller.getGraphicalRepresentation(to));
					break;

				default:
					break;
				}
			} finally {
				resetVariables();
				((SwimmingLaneView) controller.getDrawingView()).resetDraggedNodePalette();
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

	private WKFNode clone(FlexoNode flexoNode) {
		return (WKFNode) flexoNode.cloneUsingXMLMapping(true);
	}

	private void resetVariables() {
		drawEdge = false;
		isDnd = false;
		to = null;
		currentDraggingLocationInDrawingView = null;
	}

	private WKFNode createElement(DropWKFElement drop, FGEPoint dropLocation, FlexoPetriGraph container,
			SimplifiedCardinalDirection direction) {
		FGEPoint locationInDrawing = null;
		if (controller.getGraphicalRepresentation(container) != null) {
			locationInDrawing = dropLocation.transform(GraphicalRepresentation.convertCoordinatesAT(
					controller.getGraphicalRepresentation(container), controller.getDrawingGraphicalRepresentation(), 1.0));// gr.getLocationInDrawing();
		}
		if (drop.getObject() != null && drop.hasActionExecutionSucceeded()) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) controller.getGraphicalRepresentation(drop.getObject());
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
		return (WKFNode) drop.getObject();
	}

	@Override
	public Rectangle paint(FGEGraphics drawingGraphics) {
		// System.out.println("Focused:"+nodeGR.getIsFocused());
		if (nodeGR.getIsSelected() && !nodeGR.getIsFocused()) {
			return null;
		}
		if (/*nodeGR.getIsSelected() ||*/nodeGR.isResizing() || nodeGR.isMoving()) {
			return null;
		}
		if (!nodeGR.getDrawing().isEditable()) {
			return null;
		}
		AffineTransform at = GraphicalRepresentation.convertNormalizedCoordinatesAT(nodeGR, drawingGraphics.getGraphicalRepresentation());

		Graphics2D oldGraphics = drawingGraphics.cloneGraphics();

		drawingGraphics.setDefaultForeground(NONE);
		drawingGraphics.setDefaultBackground(DEFAULT);
		FGERoundRectangle paletteRect = (FGERoundRectangle) getArea().transform(at);
		FGERoundRectangle nodeRect = (FGERoundRectangle) this.nodeRect.transform(at);
		FGERectangle edgeRect = (FGERectangle) this.edgeRect.transform(at);
		FGERectangle orRect = (FGERectangle) this.exclusiveGatewayRect.transform(at);
		FGERectangle annotationRect = (FGERectangle) this.annotationRect.transform(at);
		double arrowSize = 4;

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
		FGEPoint eastPt = edgeRect.getEastPt();
		FGEPoint westPt = edgeRect.getWestPt();
		drawingGraphics.drawLine(westPt.x, westPt.y, eastPt.x - arrowSize, eastPt.y);
		drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y, eastPt.x - arrowSize, edgeRect.y + ELEMENTS_HEIGHT);
		drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y, eastPt.x, eastPt.y);
		drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + ELEMENTS_HEIGHT, eastPt.x, eastPt.y);

		// 3. Exclusive gateway
		FGERegularPolygon orPoly = new FGERegularPolygon(orRect.x + (PALETTE_WIDTH - ELEMENTS_HEIGHT) / 2, orRect.y, ELEMENTS_HEIGHT,
				ELEMENTS_HEIGHT, Filling.FILLED, 4, 90);
		FGEPoint northEast = orPoly.getSegments().get(0).getMiddle();
		FGEPoint southEast = orPoly.getSegments().get(1).getMiddle();
		FGEPoint southWest = orPoly.getSegments().get(2).getMiddle();
		FGEPoint northWest = orPoly.getSegments().get(3).getMiddle();

		drawingGraphics.setDefaultForeground(OR_FOREGROUND);
		// drawingGraphics.setDefaultBackground(OR_BACKGROUND);
		drawingGraphics.useDefaultForegroundStyle();
		// drawingGraphics.useDefaultBackgroundStyle();
		drawingGraphics.drawPolygon(orPoly);
		double orOffset = 1/*drawingGraphics.getScale()*/;
		drawingGraphics.drawLine(northWest.x + orOffset, northWest.y + orOffset, southEast.x - orOffset, southEast.y - orOffset);
		drawingGraphics.drawLine(northEast.x - orOffset, northEast.y + orOffset, southWest.x + orOffset, southWest.y - orOffset);

		// 4. Annotation
		drawingGraphics.setDefaultForeground(ForegroundStyle.makeStyle(annotationTextStyle.getColor()));
		drawingGraphics.useDefaultForegroundStyle();
		drawingGraphics.drawLine(annotationRect.getNorthWestPt(), new FGEPoint(annotationRect.x + annotationRect.width / 5,
				annotationRect.y));
		drawingGraphics.drawLine(annotationRect.getNorthWestPt(), annotationRect.getSouthWestPt());
		drawingGraphics.drawLine(new FGEPoint(annotationRect.x + annotationRect.width / 5, annotationRect.y + annotationRect.height),
				annotationRect.getSouthWestPt());
		drawingGraphics.setDefaultTextStyle(annotationTextStyle);
		drawingGraphics.useDefaultTextStyle();
		FGEPoint pt = annotationRect.getNorthWestPt();
		pt.y += annotationTextStyle.getFont().getSize() / 2 - 1;
		pt.x += 1;
		drawingGraphics.drawString("Abc", pt, HorizontalTextAlignment.LEFT);

		drawingGraphics.releaseClonedGraphics(oldGraphics);

		/*drawingGraphics.setDefaultForeground(ForegroundStyle.makeStyle(Color.RED));
		drawingGraphics.drawRect(orRect.x, orRect.y, orRect.width, orRect.height);
		drawingGraphics.drawRect(annotationRect.x, annotationRect.y, annotationRect.width, annotationRect.height);*/

		return drawingGraphics.getGraphicalRepresentation().convertNormalizedRectangleToViewCoordinates(paletteRect.getBoundingBox(),
				drawingGraphics.getScale());

	}

	/*@Override
	public boolean isApplicable(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent e)
	{
		return super.isApplicable(graphicalRepresentation, controller, e)
		&& (RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, upRect)
				|| RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, downRect)
				|| RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, minusRect)
				|| RoleContainerGR.isInsideRectangle(graphicalRepresentation, controller, e, plusRect));
	}*/

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == nodeGR) {
			if (arg instanceof ObjectResized) {
				updateElements();
			}
		}
	}

	private void updateElements() {
		setArea(makeRoundRect(nodeGR));
		AffineTransform at = AffineTransform.getScaleInstance(1 / nodeGR.getWidth(), 1 / nodeGR.getHeight());

		nodeRect = (FGERoundRectangle) new FGERoundRectangle(nodeGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2 + 0.5,
				(nodeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED).transform(at);
		edgeRect = (FGERectangle) new FGERectangle(nodeGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2,
				(nodeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING + (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
				Filling.FILLED).transform(at);
		exclusiveGatewayRect = (FGERectangle) new FGERectangle(nodeGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2,
				(nodeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING + 2 * (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
				Filling.FILLED).transform(at);
		annotationRect = (FGERectangle) new FGERectangle(nodeGR.getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2,
				(nodeGR.getHeight() - PALETTE_HEIGHT) / 2 + SPACING + 3 * (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
				Filling.FILLED).transform(at);
		annotationTextStyle = TextStyle.makeTextStyle(Color.BLACK, new Font("Dialog", Font.PLAIN, 10));
	}

	protected Image getScaledOperatorImage(double scale) {
		return WKFIconLibrary.OR_OPERATOR_ICON.getImage().getScaledInstance((int) (ELEMENTS_WIDTH * scale), (int) (ELEMENTS_WIDTH * scale),
				Image.SCALE_SMOOTH);
	}
}
