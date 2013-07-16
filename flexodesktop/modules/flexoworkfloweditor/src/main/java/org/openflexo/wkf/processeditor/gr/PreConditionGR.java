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
package org.openflexo.wkf.processeditor.gr;

import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectLocationResetted;
import org.openflexo.foundation.wkf.dm.PreRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class PreConditionGR extends AbstractNodeGR<FlexoPreCondition> implements GraphicalFlexoObserver {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PreConditionGR.class.getPackage().getName());

	public static final int PRECONDITION_SIZE = 10;

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	public PreConditionGR(FlexoPreCondition pre, ProcessRepresentation aDrawing) {
		super(pre, ShapeType.CIRCLE, aDrawing);
		// pre.addObserver(this);
		// setX(getFlexoPreCondition().getPosX());
		// setY(getFlexoPreCondition().getPosY());
		setWidth(PRECONDITION_SIZE);
		setHeight(PRECONDITION_SIZE);
		foreground = ForegroundStyle.makeNone();
		background = BackgroundStyle.makeEmptyBackground();
		background.setTransparencyLevel(0.0f);
		background.setUseTransparency(true);
		setHasText(false);
		setForeground(foreground);
		setBackground(background);
		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(0, 0, 0, 0));
		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		updatePropertiesFromWKFPreferences();
		if (getFlexoPreCondition().getAttachedNode() instanceof AbstractActivityNode) {
			setLayer(ACTIVITY_LAYER + 1);
		} else if (getFlexoPreCondition().getAttachedNode() instanceof OperationNode) {
			setLayer(OPERATION_LAYER + 1);
		} else if (getFlexoPreCondition().getAttachedNode() instanceof ActionNode) {
			setLayer(ACTION_LAYER + 1);
		}
	}

	@Override
	public void delete() {
		super.delete();
		parentGR = null;
		parentOutline = null;
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();
	}

	private GraphicalRepresentation parentGR = null;
	private FGEArea parentOutline = null;

	@Override
	public FGEArea getLocationConstrainedArea() {
		GraphicalRepresentation parent = getContainerGraphicalRepresentation();
		if (parentGR == null || parent != parentGR) {
			if (parent != null && parent instanceof ShapeGraphicalRepresentation) {
				parentOutline = ((ShapeGraphicalRepresentation) parent).getShape().getOutline();
				parentOutline = parentOutline.transform(AffineTransform.getScaleInstance(
						((ShapeGraphicalRepresentation) parent).getWidth(), ((ShapeGraphicalRepresentation) parent).getHeight()));
				ShapeBorder parentBorder = ((ShapeGraphicalRepresentation) parent).getBorder();
				parentOutline = parentOutline.transform(AffineTransform.getTranslateInstance(parentBorder.left - PRECONDITION_SIZE / 2,
						parentBorder.top - PRECONDITION_SIZE / 2));
				// System.out.println("Rebuild outline = "+parentOutline);
				parentGR = parent;
			}
		}
		return parentOutline;
	}

	public FlexoPreCondition getFlexoPreCondition() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getFlexoPreCondition()) {
			if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals("posX")
						|| ((WKFAttributeDataModification) dataModification).getAttributeName().equals("posY")) {
					if (!isUpdatingPosition) {
						// logger.info("----------------- "+dataModification);
						notifyObjectMoved();
					}
				} else {
					notifyShapeNeedsToBeRedrawn();
				}
			} else if (dataModification instanceof NameChanged) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			} else if (dataModification instanceof ObjectLocationChanged) {
				if (!isUpdatingPosition) {
					// logger.info("----------------- "+dataModification);
					notifyObjectMoved();
				}
			} else if (dataModification instanceof ObjectLocationResetted) {
				resetDefaultLocation();
			} else if (dataModification instanceof PreRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

	@Override
	public void update(Observable observable, Object dataModification) {
		if (observable == getContainerGraphicalRepresentation()) {
			if (dataModification instanceof ObjectWillMove || dataModification instanceof ObjectWillResize
					|| dataModification instanceof ObjectHasMoved || dataModification instanceof ObjectHasResized
					|| dataModification instanceof ObjectMove || dataModification instanceof ObjectResized
					|| dataModification instanceof ShapeChanged) {
				// Reinit parent outline that will change
				parentGR = null;
			}
		}
		super.update(observable, dataModification);
	}

	public void resetDefaultLocation() {
		SimplifiedCardinalDirection defaultOrientation = findDefaultOrientation();
		FGEPoint defaultLocation = defaultAnchorPointArrivingFrom(defaultOrientation);
		defaultX = defaultLocation.getX();
		defaultY = defaultLocation.getY();
		if (defaultX > 0) {
			setXNoNotification(defaultX);
		}
		if (defaultY > 0) {
			setYNoNotification(defaultY);
		}
		notifyObjectMoved();
	}

	// Override to implement defaut automatic layout
	@Override
	public double getDefaultX() {
		if (defaultX < 0) {
			resetDefaultLocation();
		}
		return defaultX;
	}

	// Override to implement defaut automatic layout
	@Override
	public double getDefaultY() {
		if (defaultY < 0) {
			resetDefaultLocation();
		}
		return defaultY;
	}

	/**
	 * Explore eventual incoming edge to compute an initial orientation for a default layout
	 * 
	 * @return
	 */
	private SimplifiedCardinalDirection findDefaultOrientation() {
		if (getFlexoPreCondition().getIncomingPostConditions().size() > 0) {
			FlexoPostCondition<?, ?> post = getFlexoPreCondition().getIncomingPostConditions().firstElement();
			if (post.getStartNode() != null && getFlexoPreCondition().getAttachedNode() != null) {

				WKFObject startObject = getDrawing().getFirstVisibleObject(
						EdgeGR.getRepresentedStartObject(post.getStartNode(), getDrawing()));
				// WKFObject endObject =
				// getDrawing().getFirstVisibleObject(EdgeGR.getRepresentedEndObject((WKFObject)post.getEndingObject(),getDrawing()));
				if (startObject != null) {
					ShapeGraphicalRepresentation startGR = (ShapeGraphicalRepresentation) getGraphicalRepresentation(startObject);
					ShapeGraphicalRepresentation endGR = (ShapeGraphicalRepresentation) getGraphicalRepresentation(getFlexoPreCondition()
							.getAttachedNode());
					if (startGR != null && endGR != null && startGR.isRegistered() && endGR.isRegistered()) {
						logger.finer("findDefaultOrientation(): "
								+ FGEPoint.getSimplifiedOrientation(endGR.getLocationInDrawing(), startGR.getLocationInDrawing()));
						return FGEPoint.getSimplifiedOrientation(endGR.getLocationInDrawing(), startGR.getLocationInDrawing());
					}
				}
			}
		}
		return SimplifiedCardinalDirection.WEST;
	}

	private FGEPoint defaultAnchorPointArrivingFrom(SimplifiedCardinalDirection orientation) {
		if (getFlexoPreCondition().getAttachedNode() != null) {
			ShapeGraphicalRepresentation parentGR = (ShapeGraphicalRepresentation) getGraphicalRepresentation(getFlexoPreCondition()
					.getAttachedNode());
			if (parentGR != null) {
				FGEPoint relativePoint;
				if (orientation == SimplifiedCardinalDirection.NORTH) {
					relativePoint = new FGEPoint(0.5, 0);
				} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
					relativePoint = new FGEPoint(0.5, 1);
				} else if (orientation == SimplifiedCardinalDirection.EAST) {
					relativePoint = new FGEPoint(1, 0.5);
				} else {
					/*if (orientation == SimplifiedCardinalDirection.WEST)*/relativePoint = new FGEPoint(0, 0.5);
				}
				FGEPoint returned = new FGEPoint(parentGR.convertNormalizedPointToViewCoordinates(relativePoint, 1.0));
				returned.x = returned.x - PRECONDITION_SIZE / 2;
				returned.y = returned.y - PRECONDITION_SIZE / 2;
				boolean canMove = true;
				boolean increase = true;
				double lastIncrement = 0;
				while (isLocationAlreadyUsed(orientation, returned) && canMove) {
					FGEPoint newPoint = new FGEPoint(returned);
					lastIncrement += PRECONDITION_SIZE * 1.5;
					if (increase) {
						if (orientation == SimplifiedCardinalDirection.NORTH) {
							newPoint.x += lastIncrement;
						} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
							newPoint.x += lastIncrement;
						} else if (orientation == SimplifiedCardinalDirection.EAST) {
							newPoint.y += lastIncrement;
						} else {
							newPoint.y += lastIncrement;
						}
					} else {
						if (orientation == SimplifiedCardinalDirection.NORTH) {
							newPoint.x -= lastIncrement;
						} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
							newPoint.x -= lastIncrement;
						} else if (orientation == SimplifiedCardinalDirection.EAST) {
							newPoint.y -= lastIncrement;
						} else {
							newPoint.y -= lastIncrement;
						}
					}

					// Still within shape?
					if (orientation == SimplifiedCardinalDirection.NORTH) {
						canMove = newPoint.x > 0 && newPoint.x < parentGR.getWidth();
					} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
						canMove = newPoint.x > 0 && newPoint.x < parentGR.getWidth();
					} else if (orientation == SimplifiedCardinalDirection.EAST) {
						canMove = newPoint.y > 0 && newPoint.y < parentGR.getHeight();
					} else {
						canMove = newPoint.y > 0 && newPoint.y < parentGR.getHeight();
					}
					if (canMove) {
						returned = newPoint;
					}
					increase = !increase;
				}
				return returned;
			}
		}
		return new FGEPoint(0, 0);
	}

	/**
	 * @param orientation
	 * @param returned
	 */
	private boolean isLocationAlreadyUsed(SimplifiedCardinalDirection orientation, FGEPoint returned) {
		for (FlexoPreCondition pc : getFlexoPreCondition().getAttachedNode().getPreConditions()) {
			if (pc != getFlexoPreCondition()) {
				GraphicalRepresentation<FlexoPreCondition> gr = getGraphicalRepresentation(pc);
				if (gr instanceof ShapeGraphicalRepresentation
						&& pc.hasLocationForContext(BASIC_PROCESS_EDITOR)
						&& ((ShapeGraphicalRepresentation) gr).getBounds(1.0).intersects(returned.x, returned.y, PRECONDITION_SIZE,
								PRECONDITION_SIZE)) {
					return true;
				}
			}
		}
		return false;
	}

}
