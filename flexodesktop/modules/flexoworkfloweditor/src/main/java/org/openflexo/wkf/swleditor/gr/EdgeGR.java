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
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.CurveConnector;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector.RectPolylinAdjustability;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector.RectPolylinConstraints;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PostInserted;
import org.openflexo.foundation.wkf.dm.PostRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.gr.EdgeGR.EdgeRepresentation;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class EdgeGR<O extends WKFEdge<?, ?>> extends WKFConnectorGR<O> {

	private static final Logger logger = Logger.getLogger(EdgeGR.class.getPackage().getName());

	private boolean isInduced;

	public EdgeGR(O edge, WKFObject startObject, WKFObject endObject, SwimmingLaneRepresentation aDrawing) {
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

		setForeground(ForegroundStyle.makeStyle(Color.DARK_GRAY, 1.6f));

		setMiddleSymbol(MiddleSymbolType.NONE);
		setEndSymbol(EndSymbolType.FILLED_ARROW);
		addToMouseClickControls(new ResetLayout(), true);

		addToMouseClickControls(new SwimmingLaneEditorController.ShowContextualMenuControl(false));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new SwimmingLaneEditorController.ShowContextualMenuControl(true));
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

	protected static WKFObject getRepresentedStartObject(WKFObject declaredStartObject, SwimmingLaneRepresentation aDrawing) {
		return declaredStartObject;
	}

	private static WKFObject getRepresentedEndObject(WKFObject declaredEndObject, SwimmingLaneRepresentation aDrawing) {
		if (declaredEndObject instanceof FlexoPreCondition && ((FlexoPreCondition) declaredEndObject).getAttachedBeginNode() != null
				&& aDrawing.isVisible(((FlexoPreCondition) declaredEndObject).getAttachedBeginNode())) {
			return ((FlexoPreCondition) declaredEndObject).getAttachedBeginNode();
		}
		return declaredEndObject;
	}

	private Connector makeConnector(ConnectorType connectorType) {
		Connector returned = Connector.makeConnector(connectorType, this);

		if (returned instanceof RectPolylinConnector) {
			RectPolylinConnector connector = (RectPolylinConnector) returned;
			// connector.setAdjustability(RectPolylinAdjustability.FULLY_ADJUSTABLE);
			connector.setAdjustability(WKFPreferences.getConnectorAdjustability());
			// logger.info("Post: "+getPostCondition().getName()+" hasGraphicalPropertyForKey("+getStoredPolylinKey()+"): "+getPostCondition().hasGraphicalPropertyForKey(getStoredPolylinKey()));
			if (getEdge().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
				ensurePolylinConverterIsRegistered();
				polylinIWillBeAdustedTo = (FGERectPolylin) getEdge()._graphicalPropertyForKey(getStoredPolylinKey());
				connector.setWasManuallyAdjusted(true);
			}
			if (getEdge().hasGraphicalPropertyForKey(getCrossedCPKey())) {
				ensurePointConverterIsRegistered();
				connector.setCrossedControlPoint((FGEPoint) getEdge()._graphicalPropertyForKey(getCrossedCPKey()));
			}
			if (getEdge().hasGraphicalPropertyForKey(getFixedStartLocationKey())) {
				ensurePointConverterIsRegistered();
				connector.setFixedStartLocation((FGEPoint) getEdge()._graphicalPropertyForKey(getFixedStartLocationKey()));
			}
			if (getEdge().hasGraphicalPropertyForKey(getFixedEndLocationKey())) {
				ensurePointConverterIsRegistered();
				connector.setFixedEndLocation((FGEPoint) getEdge()._graphicalPropertyForKey(getFixedEndLocationKey()));
			}

			connector.setStraightLineWhenPossible(true);
			connector.setRectPolylinConstraints(RectPolylinConstraints.NONE);
			connector.setIsRounded(true);
			connector.setIsStartingLocationDraggable(true);
			connector.setIsEndingLocationDraggable(true);
		} else if (returned instanceof CurveConnector) {
			ensurePointConverterIsRegistered();
			CurveConnector connector = (CurveConnector) returned;
			connector._setCpPosition((FGEPoint) getEdge()._graphicalPropertyForKey(getStoredCurveCPKey()));
		}

		return returned;
	}

	private FGERectPolylin polylinIWillBeAdustedTo;

	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		super.notifyObjectHierarchyWillBeUpdated();
		if (getDrawing().getFirstVisibleObject(getRepresentedStartObject(getEdge().getStartNode(), getDrawing())) != startObject
				|| getDrawing().getFirstVisibleObject(getRepresentedEndObject(getEdge().getEndNode(), getDrawing())) != endObject) {
			// logger.info("Start or end object changed for "+getPostCondition());
			dismissGraphicalRepresentation();
		}
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		if (polylinIWillBeAdustedTo != null && getConnector() instanceof RectPolylinConnector && !getEdge().isDeleted()) {
			((RectPolylinConnector) getConnector()).manuallySetPolylin(polylinIWillBeAdustedTo);
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
			return (FlexoPostCondition<?, ?>) getDrawable();
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
				return StartSymbolType.NONE;
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
		if (getConnector() instanceof RectPolylinConnector) {
			((RectPolylinConnector) getConnector()).setIsStartingLocationFixed(false);
			((RectPolylinConnector) getConnector()).setIsEndingLocationFixed(false);
			if (WKFPreferences.getConnectorAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE) {
				((RectPolylinConnector) getConnector()).setWasManuallyAdjusted(false);
			} else if (WKFPreferences.getConnectorAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
				((RectPolylinConnector) getConnector()).setCrossedControlPoint(null);
				refreshConnector();
			}
		}
	}

	protected boolean startOrientationFixed;
	protected boolean endOrientationFixed;
	protected SimplifiedCardinalDirection newStartOrientation;
	protected SimplifiedCardinalDirection newEndOrientation;

	@Override
	public void refreshConnector() {
		if (!isConnectorConsistent()) {
			// Dont' go further for connector that are inconsistent (this may happen
			// during big model restructurations (for example during a multiple delete)
			return;
		}
		if (getConnector() instanceof RectPolylinConnector && getEndObject() instanceof PreConditionGR) {
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
		if (getConnector() instanceof RectPolylinConnector) {
			if (getStartObject() instanceof AnnotationGR) {
				startOrientationFixed = true;
				double d = getStartObject().getLocationInDrawing().x + getStartObject().getWidth() / 2;
				double e = getEndObject().getLocationInDrawing().x + getEndObject().getWidth() / 2;
				if (d > e) {
					newStartOrientation = SimplifiedCardinalDirection.WEST;
				} else {
					newStartOrientation = SimplifiedCardinalDirection.EAST;
				}
			}
			if (getEndObject() instanceof AnnotationGR) {
				endOrientationFixed = true;
				double d = getStartObject().getLocationInDrawing().x + getStartObject().getWidth() / 2;
				double e = getEndObject().getLocationInDrawing().x + getEndObject().getWidth() / 2;
				if (d < e) {
					newEndOrientation = SimplifiedCardinalDirection.WEST;
				} else {
					newEndOrientation = SimplifiedCardinalDirection.EAST;
				}
			}
		}

		if (getConnector() instanceof RectPolylinConnector) {
			RectPolylinConnector connector = (RectPolylinConnector) getConnector();
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

	@Override
	public void notifyConnectorChanged() {
		super.notifyConnectorChanged();
		storeNewLayout();
	}

	private String getContext() {
		return SWIMMING_LANE_EDITOR + "_" + (startObject != null ? startObject.getFlexoID() : "???") + "_"
				+ (endObject != null ? endObject.getFlexoID() : "???");
	}

	private String getStoredPolylinKey() {
		return "polylin_" + getContext();
	}

	private String getStoredCurveCPKey() {
		return "curve_cp_" + getContext();
	}

	private String getCrossedCPKey() {
		return "crossed_cp_" + getContext();
	}

	private String getFixedStartLocationKey() {
		return "fixed_start_location_" + getContext();
	}

	private String getFixedEndLocationKey() {
		return "fixed_end_location_" + getContext();
	}

	private String getRelativeMiddleSymbolLocationKey() {
		return "middle_symbol_location_" + getContext();
	}

	private void storeNewLayout() {
		if (getConnector() instanceof RectPolylinConnector && isRegistered()) {
			RectPolylinConnector connector = (RectPolylinConnector) getConnector();
			if (connector.getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE) {
				ensurePolylinConverterIsRegistered();
				if (connector.getWasManuallyAdjusted() && connector._getPolylin() != null) {
					if (polylinIWillBeAdustedTo == null) { // Store this layout only in no other layout is beeing registering
						// logger.info("Post "+getPostCondition().getName()+": store new layout to "+connector._getPolylin());
						getEdge()._setGraphicalPropertyForKey(connector._getPolylin(), getStoredPolylinKey());
					}
				} else {
					if (getEdge().hasGraphicalPropertyForKey(getStoredPolylinKey())) {
						getEdge()._removeGraphicalPropertyWithKey(getStoredPolylinKey());
					}
				}
			} else if (connector.getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
				ensurePointConverterIsRegistered();
				if (connector.getCrossedControlPoint() != null) {
					getEdge()._setGraphicalPropertyForKey(connector.getCrossedControlPoint(), getCrossedCPKey());
				} else {
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
		} else if (getConnector() instanceof CurveConnector && isRegistered()) {
			CurveConnector connector = (CurveConnector) getConnector();
			ensurePointConverterIsRegistered();
			getEdge()._setGraphicalPropertyForKey(connector._getCpPosition(), getStoredCurveCPKey());
			if (connector.getAreBoundsAdjustable()) {
				ensurePointConverterIsRegistered();
				getEdge()._setGraphicalPropertyForKey(connector._getCp1RelativeToStartObject(), getFixedStartLocationKey());
				getEdge()._setGraphicalPropertyForKey(connector._getCp2RelativeToEndObject(), getFixedEndLocationKey());
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
		return getEdge()._doubleGraphicalPropertyForKey(getRelativeMiddleSymbolLocationKey(), 1.0);
	}

	@Override
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
		getEdge()._setGraphicalPropertyForKey(relativeMiddleSymbolLocation, getRelativeMiddleSymbolLocationKey());
	}

	public boolean startLocationManuallyAdjusted() {
		return getConnector() instanceof RectPolylinConnector && ((RectPolylinConnector) getConnector()).getIsStartingLocationFixed();
	}

	public boolean endLocationManuallyAdjusted() {
		return getConnector() instanceof RectPolylinConnector && ((RectPolylinConnector) getConnector()).getIsEndingLocationFixed();
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
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
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
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals(FlexoPostCondition.EDGE_REPRESENTATION)) {
					updatePropertiesFromWKFPreferences();
					refreshConnector();
				} else if ("isConditional".equals(((WKFAttributeDataModification) dataModification).propertyName())) {
					refreshConnector(true);
				} else if ("isDefaultFlow".equals(((WKFAttributeDataModification) dataModification).propertyName())) {
					refreshConnector(true);
				}
			} else if (dataModification instanceof NameChanged) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			} else if ("hideWhenInduced".equals(dataModification.propertyName())) {
				getDrawing().updateGraphicalObjectsHierarchy();
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
		TextStyle ts = TextStyle.makeTextStyle(Color.BLACK, getEdgeFont().getFont());
		ts.setBackgroundColor(Color.WHITE);
		ts.setIsBackgroundColored(true);
		setTextStyle(ts);
		setConnector(makeConnector(type.getConnectorType()));
	}

	private FlexoFont getEdgeFont() {
		if (getDrawable().getWorkflow() != null) {
			return getDrawable().getWorkflow().getEdgeFont(WKFPreferences.getEdgeFont());
		} else {
			return WKFPreferences.getEdgeFont();
		}
	}

	// Override to implement defaut automatic layout
	public double getDefaultLabelX() {
		if (getConnector() instanceof RectPolylinConnector) {
			return Math.sin(((RectPolylinConnector) getConnector()).getMiddleSymbolAngle()) * 15;
		}
		return 0;
	}

	// Override to implement defaut automatic layout
	public double getDefaultLabelY() {
		if (getConnector() instanceof RectPolylinConnector) {
			return Math.cos(((RectPolylinConnector) getConnector()).getMiddleSymbolAngle()) * 15;
		}
		return 0;
	}

}
