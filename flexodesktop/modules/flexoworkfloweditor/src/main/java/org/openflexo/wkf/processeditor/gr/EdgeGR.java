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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinConstraints;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PostInserted;
import org.openflexo.foundation.wkf.dm.PostRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.inspector.HasIcon;
import org.openflexo.swing.FlexoFont;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public abstract class EdgeGR<O extends WKFEdge<?, ?>> extends WKFConnectorGR<O> implements ProcessEditorConstants {

	protected static final Logger logger = Logger.getLogger(EdgeGR.class.getPackage().getName());
	private final boolean isInduced;

	public EdgeGR(O edge, WKFObject startObject, WKFObject endObject, ProcessRepresentation aDrawing) {
		super(ConnectorType.RECT_POLYLIN, aDrawing.getFirstVisibleObject(getRepresentedStartObject(edge.getStartNode(), aDrawing)),
				aDrawing.getFirstVisibleObject(getRepresentedEndObject(edge.getEndNode(), aDrawing)), edge, aDrawing);
		if (startObject == null) {
			logger.warning("Edge: " + edge + " first visible object for " + edge.getStartNode() + " is null !");
		}
		if (endObject == null) {
			logger.warning("Edge: " + edge + " first visible object for " + edge.getEndNode() + " is null !");
		}
		edge.addObserver(this);
		isInduced = aDrawing.getFirstVisibleObject(edge.getStartNode()) != edge.getStartNode()
				|| aDrawing.getFirstVisibleObject(edge.getEndNode()) != edge.getEndNode();
		setForeground(ForegroundStyle.makeStyle(WKFCst.EDGE_COLOR, 1.0f));

		setMiddleSymbol(MiddleSymbolType.NONE);
		setEndSymbol(EndSymbolType.FILLED_ARROW);

		addToMouseClickControls(new ResetLayout(), true);

		addToMouseClickControls(new ProcessEditorController.ShowContextualMenuControl(false));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new ProcessEditorController.ShowContextualMenuControl(true));
		}

		setIsMultilineAllowed(true);
		updatePropertiesFromWKFPreferences();

	}

	@Override
	public final void delete() {
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	protected static WKFObject getRepresentedStartObject(WKFObject declaredStartObject, ProcessRepresentation aDrawing) {
		return declaredStartObject;
	}

	protected static WKFObject getRepresentedEndObject(WKFObject declaredEndObject, ProcessRepresentation aDrawing) {
		if (declaredEndObject instanceof FlexoPreCondition && ((FlexoPreCondition) declaredEndObject).getAttachedBeginNode() != null
				&& aDrawing.isVisible(((FlexoPreCondition) declaredEndObject).getAttachedBeginNode())) {
			return ((FlexoPreCondition) declaredEndObject).getAttachedBeginNode();
		}
		return declaredEndObject;
	}

	protected ConnectorSpecification makeConnector(ConnectorType connectorType) {
		ConnectorSpecification returned = ConnectorSpecification.makeConnector(connectorType, this);

		ensurePolylinConverterIsRegistered();
		ensurePointConverterIsRegistered();
		if (returned instanceof RectPolylinConnectorSpecification) {
			RectPolylinConnectorSpecification connector = (RectPolylinConnectorSpecification) returned;
			// connector.setAdjustability(RectPolylinAdjustability.FULLY_ADJUSTABLE);
			connector.setAdjustability(WKFPreferences.getConnectorAdjustability());
			// logger.info("Post: "+getPostCondition().getName()+" hasGraphicalPropertyForKey("+getStoredPolylinKey()+"): "+getPostCondition().hasGraphicalPropertyForKey(getStoredPolylinKey()));
			if (getEdge().hasGraphicalPropertyForKey(getOldStoredPolylinKey())) {
				getEdge()._setGraphicalPropertyForKey(getEdge()._graphicalPropertyForKey(getOldStoredPolylinKey()), getStoredPolylinKey());
				getEdge()._removeGraphicalPropertyWithKey(getOldStoredPolylinKey());
			}
			if (getEdge().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
				polylinIWillBeAdustedTo = (FGERectPolylin) getEdge()._graphicalPropertyForKey(getStoredPolylinKey());
				connector.setWasManuallyAdjusted(true);
			}
			if (getEdge().getEndNode() instanceof FlexoPreCondition
					&& !getEdge().hasGraphicalPropertyForKey(getPreConditionLayoutTransformFlagKey())
					&& getEdge().hasGraphicalPropertyForKey(
							getRelativeMiddleSymbolLocationKey(getContext(startObject, getEdge().getEndNode(), false)))) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						convertOldLayout((RectPolylinConnectorSpecification) getConnectorSpecification());
					}
				});
			} else {
				if (getEdge().hasGraphicalPropertyForKey(getOldCrossedCPKey())) {
					getEdge()._setGraphicalPropertyForKey(getEdge()._graphicalPropertyForKey(getOldCrossedCPKey()), getCrossedCPKey());
					getEdge()._removeGraphicalPropertyWithKey(getOldCrossedCPKey());
				}

				if (getEdge().hasGraphicalPropertyForKey(getCrossedCPKey())) {
					connector.setCrossedControlPoint((FGEPoint) getEdge()._graphicalPropertyForKey(getCrossedCPKey()));
				}

				if (getEdge().hasGraphicalPropertyForKey(getOldFixedStartLocationKey())) {
					getEdge()._setGraphicalPropertyForKey(getEdge()._graphicalPropertyForKey(getOldFixedStartLocationKey()),
							getFixedStartLocationKey());
					getEdge()._removeGraphicalPropertyWithKey(getOldFixedStartLocationKey());
				}
				if (getEdge().hasGraphicalPropertyForKey(getFixedStartLocationKey())) {
					connector.setFixedStartLocation((FGEPoint) getEdge()._graphicalPropertyForKey(getFixedStartLocationKey()));
				}

				if (getEdge().hasGraphicalPropertyForKey(getOldFixedEndLocationKey())) {
					getEdge()._setGraphicalPropertyForKey(getEdge()._graphicalPropertyForKey(getOldFixedEndLocationKey()),
							getFixedEndLocationKey());
					getEdge()._removeGraphicalPropertyWithKey(getOldFixedEndLocationKey());
				}
				if (getEdge().hasGraphicalPropertyForKey(getFixedEndLocationKey())) {
					connector.setFixedEndLocation((FGEPoint) getEdge()._graphicalPropertyForKey(getFixedEndLocationKey()));
				}
			}

			connector.setStraightLineWhenPossible(true);
			connector.setRectPolylinConstraints(RectPolylinConstraints.NONE);
			connector.setIsRounded(true);
			connector.setIsStartingLocationDraggable(true);
			connector.setIsEndingLocationDraggable(true);
		} else if (returned instanceof CurveConnectorSpecification) {
			CurveConnectorSpecification connector = (CurveConnectorSpecification) returned;
			if (getEdge().hasGraphicalPropertyForKey(getOldStoredCurveCPKey())) {
				getEdge()._setGraphicalPropertyForKey(getEdge()._graphicalPropertyForKey(getOldStoredCurveCPKey()), getStoredCurveCPKey());
				getEdge()._removeGraphicalPropertyWithKey(getOldStoredCurveCPKey());
			}
			// Default value is 0.5 / 0.55: the exact middle of the arc (that will look straight)
			if (!getEdge().hasGraphicalPropertyForKey(getStoredCurveCPKey())) {
				getEdge()._setGraphicalPropertyForKey(new FGEPoint(0.5, 0.55), getStoredCurveCPKey());
			}
			connector.setCpPosition((FGEPoint) getEdge()._graphicalPropertyForKey(getStoredCurveCPKey()));
		}

		return returned;
	}

	protected void convertOldLayout(RectPolylinConnectorSpecification connector) {
		if (getEdge().hasGraphicalPropertyForKey(getPreConditionLayoutTransformFlagKey()) || isDeleted()) {
			return;
		}
		double relativeLocation = (Double) getEdge()._graphicalPropertyForKey(
				getRelativeMiddleSymbolLocationKey(getContext(startObject, getEdge().getEndNode(), false)));
		setRelativeMiddleSymbolLocation(relativeLocation);
		Rectangle normalizedBounds = getNormalizedBounds(1.0);
		String oldContext = getContext(startObject, getEdge().getEndNode(), false);
		AffineTransform startToDrawingAT = convertFromDrawableToDrawingAT(getStartObject(), 1.0);
		AffineTransform endToDrawingAT = convertFromDrawableToDrawingAT(getEndObject(), 1.0);
		double posx = ((FlexoPreCondition) getEdge().getEndNode()).getX(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		double posy = ((FlexoPreCondition) getEdge().getEndNode()).getY(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		FGEPoint startLocationInDrawing = new FGEPoint();
		FGEPoint endLocationInDrawing = new FGEPoint();
		startToDrawingAT.transform(startLocationInDrawing, startLocationInDrawing);
		ShapeBorder startBorder = getStartObject().getBorder();
		startLocationInDrawing.x -= startBorder.left;
		startLocationInDrawing.y -= startBorder.top;
		// Start object bounds in drawing coordinates
		FGEDimension startSize = getStartObject().getSize();
		startSize.width += startBorder.right + startBorder.left;
		startSize.height += startBorder.bottom + startBorder.top;
		FGERectangle startObjectRect = new FGERectangle(startLocationInDrawing, startSize, Filling.FILLED);
		// Compute the precondition location into the drawing coordinates
		endToDrawingAT.transform(new FGEPoint(posx, posy), endLocationInDrawing);
		/*endLocationInDrawing.x += getEndObject().getBorder().left;
		endLocationInDrawing.y += getEndObject().getBorder().top;*/
		// Pre condition bounds in drawing coordinates
		FGERectangle preConditionRect = new FGERectangle(endLocationInDrawing, new FGEDimension(PreConditionGR.PRECONDITION_SIZE,
				PreConditionGR.PRECONDITION_SIZE), Filling.FILLED);
		// Compute previous connector bounds (since before, end object was the precondition)
		FGERectangle connectorRectWithPreConditionInDrawing = preConditionRect.rectangleUnion(startObjectRect);
		FGEPoint oldCrossedPoint = (FGEPoint) getEdge()._graphicalPropertyForKey(getCrossedCPKey(oldContext));
		if (oldCrossedPoint != null) {
			getEdge()._removeGraphicalPropertyWithKey(getCrossedCPKey(oldContext));
			FGEPoint crossedPointInDrawing = new FGEPoint(connectorRectWithPreConditionInDrawing.x
					+ connectorRectWithPreConditionInDrawing.width * oldCrossedPoint.x, connectorRectWithPreConditionInDrawing.y
					+ connectorRectWithPreConditionInDrawing.height * oldCrossedPoint.y);
			FGEPoint crossedPointInConnector = new FGEPoint();
			convertFromDrawingToDrawableAT(this, 1.0).transform(crossedPointInDrawing, crossedPointInConnector);
			crossedPointInConnector.x /= normalizedBounds.width;
			crossedPointInConnector.y /= normalizedBounds.height;
			connector.setCrossedControlPoint(crossedPointInConnector);
		}
		Point2D labelLocation = getEdge().getLabelLocation(oldContext);
		if (labelLocation != null) {
			setAbsoluteTextX(labelLocation.getX());
			setAbsoluteTextY(labelLocation.getY());
		}
		if (getEdge().hasGraphicalPropertyForKey(getFixedStartLocationKey(oldContext))) {
			connector.setFixedStartLocation((FGEPoint) getEdge()._graphicalPropertyForKey(getFixedStartLocationKey(oldContext)));
			getEdge()._removeGraphicalPropertyWithKey(getFixedStartLocationKey(oldContext));
		}
		ShapeBorder border = getEndObject().getBorder();
		FGEPoint endLocation = new FGEPoint(posx / (getEndObject().getWidth() + border.left), posy
				/ (getEndObject().getHeight() + border.top));
		connector.setFixedEndLocation(endLocation);
		getEdge()._removeGraphicalPropertyWithKey(getFixedEndLocationKey(oldContext));
		getEdge()._removeGraphicalPropertyWithKey(
				getRelativeMiddleSymbolLocationKey(getContext(startObject, getEdge().getEndNode(), false)));
		getEdge()._setGraphicalPropertyForKey(Boolean.TRUE, getPreConditionLayoutTransformFlagKey());
	}

	private FGERectPolylin polylinIWillBeAdustedTo;

	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		super.notifyObjectHierarchyWillBeUpdated();
		if (getDrawing().getFirstVisibleObject(getRepresentedStartObject(getEdge().getStartNode(), getDrawing())) != startObject
				|| getDrawing().getFirstVisibleObject(getRepresentedEndObject(getEdge().getEndNode(), getDrawing())) != endObject) {
			dismissGraphicalRepresentation();
		}
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		if (polylinIWillBeAdustedTo != null && getConnectorSpecification() instanceof RectPolylinConnectorSpecification && !getEdge().isDeleted()) {
			// logger.info("Post: "+getPostCondition().getName()+" manuallySetPolylin to "+polylinIWillBeAdustedTo);
			((RectPolylinConnectorSpecification) getConnectorSpecification()).manuallySetPolylin(polylinIWillBeAdustedTo);
			polylinIWillBeAdustedTo = null;
			refreshConnector();
		}
	}

	@Override
	public boolean getIsVisible() {
		return true;
	}

	@Override
	public String getText() {
		return getEdge().getName();
	}

	@Override
	public void setTextNoNotification(String aName) {
		getEdge().setName(aName);
	}

	public WKFEdge<?, ?> getEdge() {
		return getDrawable();
	}

	public FlexoPostCondition<?, ?> getPostCondition() {
		if (getEdge() instanceof FlexoPostCondition) {
			return (FlexoPostCondition<?, ?>) getEdge();
		}
		return null;
	}

	@Override
	public String toString() {
		return "EdgeGR of " + getDrawable();
	}

	@Override
	public StartSymbolType getStartSymbol() {
		if (getPostCondition() != null) {
			if (getPostCondition().startOperatorIsIfNode()) {
				IFOperator ifOperator = (IFOperator) getPostCondition().getStartNode();
				if (ifOperator.isNegativeEvaluationPostcondition(getPostCondition())) {
					return StartSymbolType.PLAIN_CIRCLE;
				}
			}
			if (getPostCondition().getIsDefaultFlow()) {
				return StartSymbolType.DEFAULT_FLOW;
			} else if (getPostCondition().getIsConditional()) {
				if (!getPostCondition().getStartNode().isOperatorNode()) {
					return StartSymbolType.PLAIN_LONG_DIAMOND;
				}
			} else {
				if (isInduced()) {
					return StartSymbolType.NONE;
				} else {
					return StartSymbolType.FILLED_CIRCLE;
				}
			}
		}
		return StartSymbolType.NONE;
	}

	@Override
	public double getStartSymbolSize() {
		if (getPostCondition() != null) {
			if (getPostCondition().startOperatorIsIfNode()) {
				IFOperator ifOperator = (IFOperator) getEdge().getStartNode();
				if (ifOperator.isNegativeEvaluationPostcondition(getPostCondition())) {
					return 5;
				}
			}
			if (getPostCondition().getIsDefaultFlow()) {
				return 15;
			} else if (getPostCondition().getIsConditional()) {
				return 10;
			} else {
				return 5;
			}
		}
		return 0;
	}

	@Override
	public double getAbsoluteTextX() {
		if (getEdge().hasLabelLocationForContext(getOldContext())) {
			getEdge().setLabelX(getEdge().getLabelX(getOldContext(), getDefaultLabelX()), getContext());
			getEdge()._removeGraphicalPropertyWithKey(getEdge().getLabelXKeyForContext(getOldContext()));
		}
		if (!getEdge().hasLabelLocationForContext(getContext())) {
			getEdge().getLabelX(getContext(), getDefaultLabelX());
		}
		return getEdge().getLabelX(getContext());
	}

	@Override
	public void setAbsoluteTextXNoNotification(double posX) {
		getEdge().setLabelX(posX, getContext());
	}

	@Override
	public double getAbsoluteTextY() {
		if (getEdge().hasLabelLocationForContext(getOldContext())) {
			getEdge().setLabelY(getEdge().getLabelY(getOldContext(), getDefaultLabelY()), getContext());
			getEdge()._removeGraphicalPropertyWithKey(getEdge().getLabelYKeyForContext(getOldContext()));
		}
		if (!getEdge().hasLabelLocationForContext(getContext())) {
			getEdge().getLabelY(getContext(), getDefaultLabelY());
		}
		return getEdge().getLabelY(getContext());
	}

	@Override
	public void setAbsoluteTextYNoNotification(double posY) {
		getEdge().setLabelY(posY, getContext());
	}

	public void resetLayout() {
		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
			getEdge().setLocationConstraintFlag(true);
			((RectPolylinConnectorSpecification) getConnectorSpecification()).setIsStartingLocationFixed(false);
			((RectPolylinConnectorSpecification) getConnectorSpecification()).setIsEndingLocationFixed(false);
			if (WKFPreferences.getConnectorAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE) {
				((RectPolylinConnectorSpecification) getConnectorSpecification()).setWasManuallyAdjusted(false);
			} else if (WKFPreferences.getConnectorAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
				((RectPolylinConnectorSpecification) getConnectorSpecification()).setCrossedControlPoint(null);
				refreshConnector();
			}
		}
	}

	@Override
	public void refreshConnector() {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification && getEndObject() instanceof PreConditionGR) {
			PreConditionGR preGR = (PreConditionGR) getEndObject();
			AbstractNodeGR<?> nodeGR = (AbstractNodeGR<?>) preGR.getContainerGraphicalRepresentation();
			FGEPoint preLocationInNode = GraphicalRepresentation.convertNormalizedPoint(preGR, new FGEPoint(0.5, 0.5), nodeGR);
			SimplifiedCardinalDirection orientation = FGEPoint.getSimplifiedOrientation(new FGEPoint(0.5, 0.5), preLocationInNode);
			endOrientationFixed = true;
			newEndOrientation = orientation;

			/*
			 * if (connector.getRectPolylinConstraints() == RectPolylinConstraints.START_ORIENTATION_FIXED) {
			 * connector.setRectPolylinConstraints(RectPolylinConstraints.ORIENTATIONS_FIXED,connector.getStartOrientation(),orientation); }
			 * else { connector.setRectPolylinConstraints(RectPolylinConstraints.END_ORIENTATION_FIXED,null,orientation); }
			 * connector.setIsEndingLocationFixed(true); connector.setIsEndingLocationDraggable(false); connector.setFixedEndLocation(new
			 * FGEPoint(0.5+0.5*Math.cos(orientation.getRadians()),0.5-0.5*Math.sin(orientation.getRadians())));
			 */
		}
		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
			if (getStartObject() instanceof AnnotationGR) {
				startOrientationFixed = ((AnnotationGR) getStartObject()).getModel().isAnnotation();
				double d = getStartObject().getLocationInDrawing().x + getStartObject().getWidth() / 2;
				double e = getEndObject().getLocationInDrawing().x + getEndObject().getWidth() / 2;
				if (d > e) {
					newStartOrientation = SimplifiedCardinalDirection.WEST;
				} else {
					newStartOrientation = SimplifiedCardinalDirection.EAST;
				}
			}
			if (getEndObject() instanceof AnnotationGR) {
				endOrientationFixed = ((AnnotationGR) getEndObject()).getModel().isAnnotation();
				double d = getStartObject().getLocationInDrawing().x + getStartObject().getWidth() / 2;
				double e = getEndObject().getLocationInDrawing().x + getEndObject().getWidth() / 2;
				if (d < e) {
					newEndOrientation = SimplifiedCardinalDirection.WEST;
				} else {
					newEndOrientation = SimplifiedCardinalDirection.EAST;
				}
			}
		}

		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
			RectPolylinConnectorSpecification connector = (RectPolylinConnectorSpecification) getConnectorSpecification();
			if (endOrientationFixed) {
				if (startOrientationFixed) {
					connector.setRectPolylinConstraints(RectPolylinConstraints.ORIENTATIONS_FIXED, newStartOrientation, newEndOrientation);
				} else {
					connector.setRectPolylinConstraints(RectPolylinConstraints.END_ORIENTATION_FIXED, null, newEndOrientation);
				}
			} else {
				if (startOrientationFixed) {
					connector.setRectPolylinConstraints(RectPolylinConstraints.START_ORIENTATION_FIXED, newStartOrientation, null);
				} else {
					connector.setRectPolylinConstraints(RectPolylinConstraints.NONE);
				}
			}
			if (startOrientationFixed) {
				connector.setIsStartingLocationFixed(true);
				connector.setIsStartingLocationDraggable(false);
				if (newStartOrientation != null) {
					connector.setFixedStartLocation(new FGEPoint(0.5 + 0.5 * Math.cos(newStartOrientation.getRadians()), 0.5 - 0.5 * Math
							.sin(newStartOrientation.getRadians())));
				}
			}
			if (endOrientationFixed) {
				connector.setIsEndingLocationFixed(true);
				connector.setIsEndingLocationDraggable(false);
				if (newEndOrientation != null) {
					connector.setFixedEndLocation(new FGEPoint(0.5 + 0.5 * Math.cos(newEndOrientation.getRadians()), 0.5 - 0.5 * Math
							.sin(newEndOrientation.getRadians())));
				}
			}
			/*
			 * if (newOrientationConstraints == RectPolylinConstraints.START_ORIENTATION_FIXED) { newOrientationConstraints =
			 * RectPolylinConstraints.ORIENTATIONS_FIXED; } else { newOrientationConstraints = RectPolylinConstraints.END_ORIENTATION_FIXED;
			 * }
			 * 
			 * 
			 * switch (newOrientationConstraints) { case ORIENTATIONS_FIXED:
			 * connector.setRectPolylinConstraints(RectPolylinConstraints.ORIENTATIONS_FIXED,newStartOrientation,newEndOrientation); break;
			 * case START_ORIENTATION_FIXED:
			 * connector.setRectPolylinConstraints(RectPolylinConstraints.START_ORIENTATION_FIXED,newStartOrientation,null); break; case
			 * END_ORIENTATION_FIXED:
			 * connector.setRectPolylinConstraints(RectPolylinConstraints.END_ORIENTATION_FIXED,null,newEndOrientation); break; default:
			 * break; }
			 * 
			 * connector.setRectPolylinConstraints(newOrientationConstraints,newStartOrientation,newEndOrientation);
			 */
		}
		super.refreshConnector();
		storeNewLayout();
	}

	protected boolean startOrientationFixed;
	protected boolean endOrientationFixed;
	protected SimplifiedCardinalDirection newStartOrientation;
	protected SimplifiedCardinalDirection newEndOrientation;

	private String context;

	@Override
	public void notifyConnectorChanged() {
		super.notifyConnectorChanged();
		storeNewLayout();
	}

	private String getContext() {
		if (context == null) {
			return getContext(startObject, endObject, true);
		}
		return context;
	}

	private String getContext(WKFObject start, WKFObject end, boolean storeContext) {
		StringBuilder sb = new StringBuilder(BASIC_PROCESS_EDITOR);
		if (start instanceof PetriGraphNode) {
			sb.append(((PetriGraphNode) start).getDepth());
		} else {
			if (start != null) {
				sb.append(start.getClass().getSimpleName());
			} else {
				sb.append("???");
				storeContext = false;
			}
		}
		sb.append('_');
		if (end instanceof PetriGraphNode) {
			sb.append(((PetriGraphNode) end).getDepth());
		} else {
			if (end != null) {
				sb.append(end.getClass().getSimpleName());
			} else {
				sb.append("???");
				storeContext = false;
			}
		}
		if (storeContext) {
			return context = sb.toString();
		} else {
			return sb.toString();
		}
	}

	private String getPreConditionLayoutTransformFlagKey() {
		return "pcl_transform_" + getContext();
	}

	private String getStoredPolylinKey() {
		return getStoredPolylinKey(getContext());
	}

	private String getStoredCurveCPKey() {
		return getStoredCurveCPKey(getContext());
	}

	private String getCrossedCPKey() {
		return getCrossedCPKey(getContext());
	}

	private String getFixedStartLocationKey() {
		return getFixedStartLocationKey(getContext());
	}

	private String getFixedEndLocationKey() {
		return getFixedEndLocationKey(getContext());
	}

	private String getRelativeMiddleSymbolLocationKey() {
		return getRelativeMiddleSymbolLocationKey(getContext());
	}

	private String getStoredPolylinKey(String context) {
		return "polylin_" + context;
	}

	private String getStoredCurveCPKey(String context) {
		return "curve_cp_" + context;
	}

	private String getCrossedCPKey(String context) {
		return "crossed_cp_" + context;
	}

	private String getFixedStartLocationKey(String context) {
		return "fixed_start_location_" + context;
	}

	private String getFixedEndLocationKey(String context) {
		return "fixed_end_location_" + context;
	}

	private String getRelativeMiddleSymbolLocationKey(String context) {
		return "middle_symbol_location_" + context;
	}

	private String getOldContext() {
		return BASIC_PROCESS_EDITOR + "_" + (startObject != null ? startObject.getFlexoID() : "???") + "_"
				+ (endObject != null ? endObject.getFlexoID() : "???");
	}

	private String getOldStoredPolylinKey() {
		return "polylin_" + getOldContext();
	}

	private String getOldStoredCurveCPKey() {
		return "curve_cp_" + getOldContext();
	}

	private String getOldCrossedCPKey() {
		return "crossed_cp_" + getOldContext();
	}

	private String getOldFixedStartLocationKey() {
		return "fixed_start_location_" + getOldContext();
	}

	private String getOldFixedEndLocationKey() {
		return "fixed_end_location_" + getOldContext();
	}

	private String getOldRelativeMiddleSymbolLocationKey() {
		return "middle_symbol_location_" + getOldContext();
	}

	public boolean startLocationManuallyAdjusted() {
		return getConnectorSpecification() instanceof RectPolylinConnectorSpecification && ((RectPolylinConnectorSpecification) getConnectorSpecification()).getIsStartingLocationFixed();
	}

	public boolean endLocationManuallyAdjusted() {
		return getConnectorSpecification() instanceof RectPolylinConnectorSpecification && ((RectPolylinConnectorSpecification) getConnectorSpecification()).getIsEndingLocationFixed();
	}

	private void storeNewLayout() {
		/*
		 * boolean debug = false; if (getDrawable().getName() != null && getDrawable().getName().equals("B")) debug= true;
		 * 
		 * if (debug) { logger.info("storeNewLayout() for "+getDrawable()); logger.info("getStoredPolylinKey() = "+getStoredPolylinKey());
		 * logger.info("isRegistered() = "+isRegistered()); }
		 */

		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification && isRegistered()) {
			RectPolylinConnectorSpecification connector = (RectPolylinConnectorSpecification) getConnectorSpecification();
			if (connector.getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE) {
				ensurePolylinConverterIsRegistered();
				if (connector.getWasManuallyAdjusted() && connector.getPolylin() != null) {
					if (polylinIWillBeAdustedTo == null) { // Store this layout only in no other layout is beeing registering
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Edge " + getPostCondition().getName() + ": store new layout for " + getStoredPolylinKey() + " to "
									+ connector.getPolylin());
						}
						getEdge()._setGraphicalPropertyForKey(connector.getPolylin(), getStoredPolylinKey());
					}
				} else {
					if (getEdge().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Edge " + getPostCondition().getName() + ": remove layout for " + getStoredPolylinKey());
						}
						getEdge()._removeGraphicalPropertyWithKey(getStoredPolylinKey());
					}
				}
			} else if (connector.getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
				ensurePointConverterIsRegistered();
				if (connector.getCrossedControlPoint() != null) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Edge " + getPostCondition().getName() + ": store new ccp for " + getCrossedCPKey() + " to "
								+ connector.getCrossedControlPoint());
					}
					getEdge()._setGraphicalPropertyForKey(connector.getCrossedControlPoint(), getCrossedCPKey());
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Edge " + getPostCondition().getName() + ": remove layout for " + getCrossedCPKey());
					}
					getEdge()._removeGraphicalPropertyWithKey(getCrossedCPKey());
				}
			}
			if (connector.getIsStartingLocationFixed()) {
				ensurePointConverterIsRegistered();
				getEdge()._setGraphicalPropertyForKey(connector.getFixedStartLocation(), getFixedStartLocationKey());
			} else {
				if (getEdge().hasGraphicalPropertyForKey(getFixedStartLocationKey())) {
					getEdge()._removeGraphicalPropertyWithKey(getFixedStartLocationKey());
				}
			}
			if (connector.getIsEndingLocationFixed()) {
				ensurePointConverterIsRegistered();
				getEdge()._setGraphicalPropertyForKey(connector.getFixedEndLocation(), getFixedEndLocationKey());
			} else {
				if (getEdge().hasGraphicalPropertyForKey(getFixedEndLocationKey())) {
					getEdge()._removeGraphicalPropertyWithKey(getFixedEndLocationKey());
				}
			}
		} else if (getConnectorSpecification() instanceof CurveConnectorSpecification && isRegistered()) {
			CurveConnectorSpecification connector = (CurveConnectorSpecification) getConnectorSpecification();
			ensurePointConverterIsRegistered();
			getEdge()._setGraphicalPropertyForKey(connector.getCpPosition(), getStoredCurveCPKey());
			if (connector.getAreBoundsAdjustable()) {
				ensurePointConverterIsRegistered();
				getEdge()._setGraphicalPropertyForKey(connector.getCp1RelativeToStartObject(), getFixedStartLocationKey());
				getEdge()._setGraphicalPropertyForKey(connector.getCp2RelativeToEndObject(), getFixedEndLocationKey());
			} else {
				if (getEdge().hasGraphicalPropertyForKey(getFixedStartLocationKey())) {
					getEdge()._removeGraphicalPropertyWithKey(getFixedStartLocationKey());
				}
				if (getEdge().hasGraphicalPropertyForKey(getFixedEndLocationKey())) {
					getEdge()._removeGraphicalPropertyWithKey(getFixedEndLocationKey());
				}
			}
		}

	}

	@Override
	public double getRelativeMiddleSymbolLocation() {
		double defaultValue = 1.0;
		/*
		 * if (!getEdge().hasGraphicalPropertyForKey(getRelativeMiddleSymbolLocationKey())) { if (getConnector() instanceof
		 * RectPolylinConnectorSpecification) { // GPO: does not always work because polylin is modified when pre-condition is relocated. FGERectPolylin
		 * polylin = ((RectPolylinConnectorSpecification)getConnector()).getCurrentPolylin(); defaultValue =
		 * polylin.getRelativeLocation(polylin.getBiggestSegment().getMiddle());
		 * System.err.println(defaultValue+" "+polylin.getSegmentNb()); } }
		 */
		if (getEdge().hasGraphicalPropertyForKey(getOldRelativeMiddleSymbolLocationKey())) {
			defaultValue = getEdge()._doubleGraphicalPropertyForKey(getOldRelativeMiddleSymbolLocationKey(), defaultValue);
			getEdge()._removeGraphicalPropertyWithKey(getOldRelativeMiddleSymbolLocationKey());
		}
		return getEdge()._doubleGraphicalPropertyForKey(getRelativeMiddleSymbolLocationKey(), defaultValue);
	}

	@Override
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		getEdge()._setGraphicalPropertyForKey(relativeMiddleSymbolLocation, getRelativeMiddleSymbolLocationKey());
	}

	private boolean isPolylinConverterRegistered = false;

	private void ensurePolylinConverterIsRegistered() {
		if (!isPolylinConverterRegistered) {
			if (getEdge().getProject().getStringEncoder()._converterForClass(FGERectPolylin.class) == null) {
				getEdge().getProject().getStringEncoder()._addConverter(RECT_POLYLIN_CONVERTER);
			}
			isPolylinConverterRegistered = true;
		}

	}

	private boolean isPointConverterRegistered = false;

	private void ensurePointConverterIsRegistered() {
		if (!isPointConverterRegistered) {
			if (getEdge().getProject().getStringEncoder()._converterForClass(FGEPoint.class) == null) {
				getEdge().getProject().getStringEncoder()._addConverter(POINT_CONVERTER);
			}
			isPointConverterRegistered = true;
		}

	}

	public class ResetLayout extends MouseClickControl {

		public ResetLayout() {
			super("ResetLayout", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
						java.awt.event.MouseEvent event) {
					// logger.info("Reset layout for edge");
					resetLayout();
					return true;
				}
			}, false, false, false, false);
		}

	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getModel()) {
			if (dataModification instanceof PostInserted || dataModification instanceof PostRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof WKFAttributeDataModification) {
				String propertyName = ((WKFAttributeDataModification) dataModification).propertyName();
				if (FlexoPostCondition.EDGE_REPRESENTATION.equals(propertyName)) {
					updatePropertiesFromWKFPreferences();
					refreshConnector();
				} else if ("isConditional".equals(propertyName)) {
					refreshConnector(true);
				} else if ("isDefaultFlow".equals(((WKFAttributeDataModification) dataModification).propertyName())) {
					refreshConnector(true);
				} else if (WKFEdge.LOCATION_CONSTRAINT_FLAG.equals(propertyName) && (Boolean) dataModification.newValue()) {
					refreshConnector(true);
				}
			} else if (dataModification instanceof NameChanged) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			} else if ("hideWhenInduced".equals(dataModification.propertyName())) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (RepresentableFlexoModelObject.getBgColorKeyForContext(BASIC_PROCESS_EDITOR).equals(dataModification.propertyName())
					|| RepresentableFlexoModelObject.getTextColorKeyForContext(BASIC_PROCESS_EDITOR)
							.equals(dataModification.propertyName())) {
				updatePropertiesFromWKFPreferences();
			}
		}
	}

	public boolean isInduced() {
		return isInduced;
	}

	public boolean isInsideSameActionPetriGraph() {
		return getPostCondition() != null
				&& getPostCondition().getStartNode() instanceof ActionNode
				&& getPostCondition().getNextNode() instanceof ActionNode
				&& ((ActionNode) getPostCondition().getStartNode()).getParentPetriGraph() == ((ActionNode) getPostCondition().getNextNode())
						.getParentPetriGraph();
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		EdgeRepresentation type = (EdgeRepresentation) getEdge().getWorkflow().getConnectorRepresentation(
				WKFPreferences.getConnectorRepresentation());
		if (isInsideSameActionPetriGraph()) {
			type = EdgeRepresentation.CURVE;
		}
		context = null;
		TextStyle ts = TextStyle.makeTextStyle(getDrawable().getTextColor(BASIC_PROCESS_EDITOR, Color.BLACK), getEdgeFont().getFont());
		ts.setBackgroundColor(getDrawable().getBgColor(BASIC_PROCESS_EDITOR, Color.WHITE));
		ts.setIsBackgroundColored(true);
		setTextStyle(ts);
		setConnectorSpecification(makeConnector(type.getConnectorType()));
	}

	private FlexoFont getEdgeFont() {
		if (getDrawable().getWorkflow() != null) {
			return getDrawable().getWorkflow().getEdgeFont(WKFPreferences.getEdgeFont());
		} else {
			return WKFPreferences.getEdgeFont();
		}
	}

	public static enum EdgeRepresentation implements HasIcon {
		RECT_POLYLIN, CURVE, LINE;

		@Override
		public ImageIcon getIcon() {
			if (this == RECT_POLYLIN) {
				return org.openflexo.fge.FGEIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			} else if (this == CURVE) {
				return org.openflexo.fge.FGEIconLibrary.CURVE_CONNECTOR_ICON;
			} else if (this == LINE) {
				return org.openflexo.fge.FGEIconLibrary.LINE_CONNECTOR_ICON;
			}
			return null;
		}

		public ConnectorType getConnectorType() {
			if (this == RECT_POLYLIN) {
				return ConnectorType.RECT_POLYLIN;
			} else if (this == CURVE) {
				return ConnectorType.CURVE;
			} else if (this == LINE) {
				return ConnectorType.LINE;
			}
			return null;
		}
	}

	// Override to implement default automatic layout
	public double getDefaultLabelX() {
		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
			return Math.sin(((RectPolylinConnectorSpecification) getConnectorSpecification()).getMiddleSymbolAngle()) * 15;
		}
		return 0;
	}

	// Override to implement default automatic layout
	public double getDefaultLabelY() {
		if (getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
			return Math.cos(((RectPolylinConnectorSpecification) getConnectorSpecification()).getMiddleSymbolAngle()) * 15;
		}
		return 0;
	}

}
