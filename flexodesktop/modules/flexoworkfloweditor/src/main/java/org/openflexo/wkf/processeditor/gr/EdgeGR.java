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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
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
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.inspector.HasIcon;
import org.openflexo.toolbox.ToolBox;
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

		setForeground(ForegroundStyle.makeStyle(Color.DARK_GRAY, 1.6f));

		setMiddleSymbol(MiddleSymbolType.FILLED_ARROW);

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

	protected Connector makeConnector(ConnectorType connectorType) {
		Connector returned = Connector.makeConnector(connectorType, this);

		ensurePolylinConverterIsRegistered();
		ensurePointConverterIsRegistered();
		if (returned instanceof RectPolylinConnector) {
			RectPolylinConnector connector = (RectPolylinConnector) returned;
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

			connector.setStraightLineWhenPossible(true);
			connector.setRectPolylinConstraints(RectPolylinConstraints.NONE);
			connector.setIsRounded(true);
			connector.setIsStartingLocationDraggable(true);
			connector.setIsEndingLocationDraggable(true);
		} else if (returned instanceof CurveConnector) {
			CurveConnector connector = (CurveConnector) returned;
			if (getEdge().hasGraphicalPropertyForKey(getOldStoredCurveCPKey())) {
				getEdge()._setGraphicalPropertyForKey(getEdge()._graphicalPropertyForKey(getOldStoredCurveCPKey()), getStoredCurveCPKey());
				getEdge()._removeGraphicalPropertyWithKey(getOldStoredCurveCPKey());
			}
			// Default value is 0.5 / 0.55: the exact middle of the arc (that will look straight)
			if (!getEdge().hasGraphicalPropertyForKey(getStoredCurveCPKey())) {
				getEdge()._setGraphicalPropertyForKey(new FGEPoint(0.5, 0.55), getStoredCurveCPKey());
			}
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
			dismissGraphicalRepresentation();
		}
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		if (polylinIWillBeAdustedTo != null && getConnector() instanceof RectPolylinConnector && !getEdge().isDeleted()) {
			// logger.info("Post: "+getPostCondition().getName()+" manuallySetPolylin to "+polylinIWillBeAdustedTo);
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
			getEdge()._removeGraphicalPropertyWithKey(getEdge().getLabelXContextForContext(getOldContext()));
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
			getEdge()._removeGraphicalPropertyWithKey(getEdge().getLabelYContextForContext(getOldContext()));
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
			boolean storeContext = true;
			StringBuilder sb = new StringBuilder(BASIC_PROCESS_EDITOR);
			if (startObject instanceof PetriGraphNode) {
				sb.append(((PetriGraphNode) startObject).getDepth());
			} else {
				if (startObject != null) {
					sb.append(startObject.getClass().getSimpleName());
				} else {
					sb.append("???");
					storeContext = false;
				}
			}
			sb.append('_');
			if (endObject instanceof PetriGraphNode) {
				sb.append(((PetriGraphNode) endObject).getDepth());
			} else {
				if (endObject != null) {
					sb.append(endObject.getClass().getSimpleName());
				} else {
					sb.append("???");
					storeContext = false;
				}
			}
			if (storeContext) {
				context = sb.toString();
			} else {
				return sb.toString();
			}
		}
		return context;
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

	private void storeNewLayout() {
		/*
		 * boolean debug = false; if (getDrawable().getName() != null && getDrawable().getName().equals("B")) debug= true;
		 * 
		 * if (debug) { logger.info("storeNewLayout() for "+getDrawable()); logger.info("getStoredPolylinKey() = "+getStoredPolylinKey());
		 * logger.info("isRegistered() = "+isRegistered()); }
		 */

		if (getConnector() instanceof RectPolylinConnector && isRegistered()) {
			RectPolylinConnector connector = (RectPolylinConnector) getConnector();
			if (connector.getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE) {
				ensurePolylinConverterIsRegistered();
				if (connector.getWasManuallyAdjusted() && connector._getPolylin() != null) {
					if (polylinIWillBeAdustedTo == null) { // Store this layout only in no other layout is beeing registering
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Edge " + getPostCondition().getName() + ": store new layout for " + getStoredPolylinKey() + " to "
									+ connector._getPolylin());
						}
						getEdge()._setGraphicalPropertyForKey(connector._getPolylin(), getStoredPolylinKey());
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
		double defaultValue = 1.0;
		/*
		 * if (!getEdge().hasGraphicalPropertyForKey(getRelativeMiddleSymbolLocationKey())) { if (getConnector() instanceof
		 * RectPolylinConnector) { // GPO: does not always work because polylin is modified when pre-condition is relocated. FGERectPolylin
		 * polylin = ((RectPolylinConnector)getConnector()).getCurrentPolylin(); defaultValue =
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
		/*
		 * if (getPostCondition().getEdgeRepresentation() == null) { AbstractNode startingNode = getPostCondition().getStartingNode(); if
		 * (startingNode instanceof FlexoNode && ((FlexoNode)startingNode).isEndNode() && startingNode.getParentPetriGraph().getContainer()
		 * instanceof AbstractNode) startingNode = (AbstractNode) startingNode.getParentPetriGraph().getContainer(); FlexoPetriGraph
		 * parentPG = ((PetriGraphNode)startingNode).getParentPetriGraph(); if (parentPG !=null) { if
		 * (parentPG.getLevel()==FlexoLevel.ACTION) getPostCondition().setEdgeRepresentation(WKFPreferences.getActionConnector()); else if
		 * (parentPG.getLevel()==FlexoLevel.OPERATION) getPostCondition().setEdgeRepresentation(WKFPreferences.getOperationConnector());
		 * else getPostCondition().setEdgeRepresentation(WKFPreferences.getActivityConnector()); } else {
		 * getPostCondition().setEdgeRepresentation(WKFPreferences.getActivityConnector()); } }
		 */
		EdgeRepresentation type = (EdgeRepresentation) getEdge().getWorkflow().getConnectorRepresentation(
				WKFPreferences.getConnectorRepresentation());
		if (isInsideSameActionPetriGraph()) {
			type = EdgeRepresentation.CURVE;
		}
		context = null;
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
		if (getConnector() instanceof RectPolylinConnector) {
			return Math.sin(((RectPolylinConnector) getConnector()).getMiddleSymbolAngle()) * 15;
		}
		return 0;
	}

	// Override to implement default automatic layout
	public double getDefaultLabelY() {
		if (getConnector() instanceof RectPolylinConnector) {
			return Math.cos(((RectPolylinConnector) getConnector()).getMiddleSymbolAngle()) * 15;
		}
		return 0;
	}

}
