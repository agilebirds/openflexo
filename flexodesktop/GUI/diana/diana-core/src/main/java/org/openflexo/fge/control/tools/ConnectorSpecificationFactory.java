package org.openflexo.fge.control.tools;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.impl.CurveConnector;
import org.openflexo.fge.connectors.impl.CurvedPolylinConnector;
import org.openflexo.fge.connectors.impl.LineConnector;
import org.openflexo.fge.connectors.impl.RectPolylinConnector;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;

/**
 * Convenient class used to manipulate ConnectorSpecification instances over ConnectorSpecification class hierarchy
 * 
 * @author sylvain
 * 
 */
public class ConnectorSpecificationFactory implements StyleFactory<ConnectorSpecification, ConnectorType> {

	private static final Logger logger = Logger.getLogger(ConnectorSpecificationFactory.class.getPackage().getName());

	private static final String DELETED = "deleted";

	private ConnectorType connectorType = ConnectorType.LINE;

	private InspectedLineConnectorSpecification lineConnectorSpecification;
	private InspectedCurveConnectorSpecification curveConnectorSpecification;
	private InspectedRectPolylinConnectorSpecification rectPolylinConnectorSpecification;
	private InspectedCurvedPolylinConnectorSpecification curvedPolylinConnectorSpecification;

	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;

	private DianaInteractiveViewer<?, ?, ?> controller;

	public ConnectorSpecificationFactory(DianaInteractiveViewer<?, ?, ?> controller) {
		pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		fgeFactory = controller.getFactory();
		lineConnectorSpecification = new InspectedLineConnectorSpecification(controller, controller.getFactory().makeLineConnector());
		curveConnectorSpecification = new InspectedCurveConnectorSpecification(controller, controller.getFactory().makeCurveConnector());
		rectPolylinConnectorSpecification = new InspectedRectPolylinConnectorSpecification(controller, controller.getFactory()
				.makeRectPolylinConnector());
		curvedPolylinConnectorSpecification = new InspectedCurvedPolylinConnectorSpecification(controller, controller.getFactory()
				.makeCurvedPolylinConnector());

	}

	public FGEModelFactory getFGEFactory() {
		return fgeFactory;
	}

	public void setFGEFactory(FGEModelFactory fgeFactory) {
		this.fgeFactory = fgeFactory;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void delete() {
		getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
		pcSupport = null;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	@Override
	public AbstractInspectedConnectorSpecification<?> getCurrentStyle() {
		return getConnectorSpecification();
	}

	public AbstractInspectedConnectorSpecification<?> getConnectorSpecification() {
		switch (connectorType) {
		case LINE:
			return lineConnectorSpecification;
		case CURVE:
			return curveConnectorSpecification;
		case RECT_POLYLIN:
			return rectPolylinConnectorSpecification;
		case CURVED_POLYLIN:
			return curvedPolylinConnectorSpecification;
		}
		logger.warning("Unexpected " + connectorType);
		return null;
	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	protected boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

	public ConnectorType getStyleType() {
		return connectorType;
	}

	public void setStyleType(ConnectorType connectorType) {

		ConnectorType oldConnectorType = getStyleType();

		if (oldConnectorType == connectorType) {
			return;
		}

		this.connectorType = connectorType;
		pcSupport.firePropertyChange(STYLE_CLASS_CHANGED, oldConnectorType, getStyleType());
		pcSupport.firePropertyChange("styleType", oldConnectorType, getStyleType());
	}

	@Override
	public ConnectorSpecification makeNewStyle(ConnectorSpecification oldConnectorSpecification) {
		ConnectorSpecification returned = null;
		switch (connectorType) {
		case LINE:
			returned = lineConnectorSpecification.cloneStyle();
			break;
		case CURVE:
			returned = curveConnectorSpecification.cloneStyle();
			break;
		case RECT_POLYLIN:
			returned = rectPolylinConnectorSpecification.cloneStyle();
			break;
		case CURVED_POLYLIN:
			returned = curvedPolylinConnectorSpecification.cloneStyle();
			break;
		}
		return returned;
	}

	protected abstract class AbstractInspectedConnectorSpecification<CS extends ConnectorSpecification> extends InspectedStyle<CS>
			implements ConnectorSpecification {

		protected AbstractInspectedConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller, CS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public List<ConnectorNode<?>> getSelection() {
			return getController().getSelectedConnectors();
		}

		@Override
		public StartSymbolType getStartSymbol() {
			return getPropertyValue(ConnectorSpecification.START_SYMBOL);
		}

		@Override
		public void setStartSymbol(StartSymbolType startSymbol) {
			setPropertyValue(ConnectorSpecification.START_SYMBOL, startSymbol);
		}

		@Override
		public EndSymbolType getEndSymbol() {
			return getPropertyValue(ConnectorSpecification.END_SYMBOL);
		}

		@Override
		public void setEndSymbol(EndSymbolType endSymbol) {
			setPropertyValue(ConnectorSpecification.END_SYMBOL, endSymbol);
		}

		@Override
		public MiddleSymbolType getMiddleSymbol() {
			return getPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL);
		}

		@Override
		public void setMiddleSymbol(MiddleSymbolType middleSymbol) {
			setPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL, middleSymbol);
		}

		@Override
		public double getStartSymbolSize() {
			return getPropertyValue(ConnectorSpecification.START_SYMBOL_SIZE);
		}

		@Override
		public void setStartSymbolSize(double startSymbolSize) {
			setPropertyValue(ConnectorSpecification.START_SYMBOL_SIZE, startSymbolSize);
		}

		@Override
		public double getEndSymbolSize() {
			return getPropertyValue(ConnectorSpecification.END_SYMBOL_SIZE);
		}

		@Override
		public void setEndSymbolSize(double endSymbolSize) {
			setPropertyValue(ConnectorSpecification.END_SYMBOL_SIZE, endSymbolSize);
		}

		@Override
		public double getMiddleSymbolSize() {
			return getPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL_SIZE);
		}

		@Override
		public void setMiddleSymbolSize(double middleSymbolSize) {
			setPropertyValue(ConnectorSpecification.MIDDLE_SYMBOL_SIZE, middleSymbolSize);
		}

		@Override
		public double getRelativeMiddleSymbolLocation() {
			return getPropertyValue(ConnectorSpecification.RELATIVE_MIDDLE_SYMBOL_LOCATION);
		}

		@Override
		public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation) {
			setPropertyValue(ConnectorSpecification.RELATIVE_MIDDLE_SYMBOL_LOCATION, relativeMiddleSymbolLocation);
		}

	}

	protected class InspectedLineConnectorSpecification extends AbstractInspectedConnectorSpecification<LineConnectorSpecification>
			implements LineConnectorSpecification {

		protected InspectedLineConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller, LineConnectorSpecification defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public LineConnectorSpecification getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ConnectorNode) {
				if (((ConnectorNode<?>) node).getConnectorSpecification() instanceof LineConnectorSpecification) {
					return (LineConnectorSpecification) ((ConnectorNode<?>) node).getConnectorSpecification();
				}
			}
			return null;
		}

		@Override
		public LineConnector makeConnector(ConnectorNode<?> connectorNode) {
			LineConnector returned = new LineConnector(connectorNode);
			return returned;
		}

		@Override
		public ConnectorType getConnectorType() {
			return ConnectorType.LINE;
		}

		@Override
		public LineConnectorType getLineConnectorType() {
			return getPropertyValue(LineConnectorSpecification.LINE_CONNECTOR_TYPE);
		}

		@Override
		public void setLineConnectorType(LineConnectorType aLineConnectorType) {
			setPropertyValue(LineConnectorSpecification.LINE_CONNECTOR_TYPE, aLineConnectorType);
		}

		@Override
		public FGEPoint getCp1RelativeToStartObject() {
			return getPropertyValue(LineConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT);
		}

		@Override
		public void setCp1RelativeToStartObject(FGEPoint aPoint) {
			setPropertyValue(LineConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT, aPoint);
		}

		@Override
		public FGEPoint getCp2RelativeToEndObject() {
			return getPropertyValue(LineConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT);
		}

		@Override
		public void setCp2RelativeToEndObject(FGEPoint aPoint) {
			setPropertyValue(LineConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT, aPoint);
		}
	}

	protected class InspectedCurveConnectorSpecification extends AbstractInspectedConnectorSpecification<CurveConnectorSpecification>
			implements CurveConnectorSpecification {

		protected InspectedCurveConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller, CurveConnectorSpecification defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public CurveConnectorSpecification getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ConnectorNode) {
				if (((ConnectorNode<?>) node).getConnectorSpecification() instanceof CurveConnectorSpecification) {
					return (CurveConnectorSpecification) ((ConnectorNode<?>) node).getConnectorSpecification();
				}
			}
			return null;
		}

		@Override
		public CurveConnector makeConnector(ConnectorNode<?> connectorNode) {
			return new CurveConnector(connectorNode);
		}

		@Override
		public ConnectorType getConnectorType() {
			return ConnectorType.CURVE;
		}

		@Override
		public FGEPoint getCp1RelativeToStartObject() {
			return getPropertyValue(CurveConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT);
		}

		@Override
		public void setCp1RelativeToStartObject(FGEPoint aPoint) {
			setPropertyValue(CurveConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT, aPoint);
		}

		@Override
		public FGEPoint getCp2RelativeToEndObject() {
			return getPropertyValue(CurveConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT);
		}

		@Override
		public void setCp2RelativeToEndObject(FGEPoint aPoint) {
			setPropertyValue(CurveConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT, aPoint);
		}

		@Override
		public FGEPoint getCpPosition() {
			return getPropertyValue(CurveConnectorSpecification.CP_POSITION);
		}

		@Override
		public void setCpPosition(FGEPoint cpPosition) {
			setPropertyValue(CurveConnectorSpecification.CP_POSITION, cpPosition);
		}

		@Override
		public boolean getAreBoundsAdjustable() {
			return getPropertyValue(CurveConnectorSpecification.ARE_BOUNDS_ADJUSTABLE);
		}

		@Override
		public void setAreBoundsAdjustable(boolean aFlag) {
			setPropertyValue(CurveConnectorSpecification.ARE_BOUNDS_ADJUSTABLE, aFlag);
		}

	}

	protected class InspectedRectPolylinConnectorSpecification extends
			AbstractInspectedConnectorSpecification<RectPolylinConnectorSpecification> implements RectPolylinConnectorSpecification {

		protected InspectedRectPolylinConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller,
				RectPolylinConnectorSpecification defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public RectPolylinConnectorSpecification getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ConnectorNode) {
				if (((ConnectorNode<?>) node).getConnectorSpecification() instanceof RectPolylinConnectorSpecification) {
					return (RectPolylinConnectorSpecification) ((ConnectorNode<?>) node).getConnectorSpecification();
				}
			}
			return null;
		}

		@Override
		public ConnectorType getConnectorType() {
			return ConnectorType.RECT_POLYLIN;
		}

		@Override
		public RectPolylinConnector makeConnector(ConnectorNode<?> connectorNode) {
			return new RectPolylinConnector(connectorNode);
		}

		@Override
		public RectPolylinConstraints getRectPolylinConstraints() {
			return getPropertyValue(RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS);
		}

		@Override
		public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints) {
			setPropertyValue(RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS, aRectPolylinConstraints);
		}

		@Override
		public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints,
				SimplifiedCardinalDirection aStartOrientation, SimplifiedCardinalDirection aEndOrientation) {
			setPropertyValue(RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS, someRectPolylinConstraints);
		}

		@Override
		public boolean getStraightLineWhenPossible() {
			return getPropertyValue(RectPolylinConnectorSpecification.STRAIGHT_LINE_WHEN_POSSIBLE);
		}

		@Override
		public void setStraightLineWhenPossible(boolean aFlag) {
			setPropertyValue(RectPolylinConnectorSpecification.STRAIGHT_LINE_WHEN_POSSIBLE, aFlag);
		}

		@Override
		public RectPolylinAdjustability getAdjustability() {
			return getPropertyValue(RectPolylinConnectorSpecification.ADJUSTABILITY);
		}

		@Override
		public void setAdjustability(RectPolylinAdjustability anAdjustability) {
			setPropertyValue(RectPolylinConnectorSpecification.ADJUSTABILITY, anAdjustability);
		}

		@Override
		public SimplifiedCardinalDirection getEndOrientation() {
			return getPropertyValue(RectPolylinConnectorSpecification.END_ORIENTATION);
		}

		@Override
		public void setEndOrientation(SimplifiedCardinalDirection anOrientation) {
			setPropertyValue(RectPolylinConnectorSpecification.END_ORIENTATION, anOrientation);
		}

		@Override
		public SimplifiedCardinalDirection getStartOrientation() {
			return getPropertyValue(RectPolylinConnectorSpecification.START_ORIENTATION);
		}

		@Override
		public void setStartOrientation(SimplifiedCardinalDirection anOrientation) {
			setPropertyValue(RectPolylinConnectorSpecification.START_ORIENTATION, anOrientation);
		}

		@Override
		public boolean getIsRounded() {
			return getPropertyValue(RectPolylinConnectorSpecification.IS_ROUNDED);
		}

		@Override
		public void setIsRounded(boolean aFlag) {
			setPropertyValue(RectPolylinConnectorSpecification.IS_ROUNDED, aFlag);
		}

		@Override
		public int getArcSize() {
			return getPropertyValue(RectPolylinConnectorSpecification.ARC_SIZE);
		}

		@Override
		public void setArcSize(int anArcSize) {
			setPropertyValue(RectPolylinConnectorSpecification.ARC_SIZE, anArcSize);
		}

		@Override
		public boolean getIsStartingLocationFixed() {
			return getPropertyValue(RectPolylinConnectorSpecification.IS_STARTING_LOCATION_FIXED);
		}

		@Override
		public void setIsStartingLocationFixed(boolean aFlag) {
			setPropertyValue(RectPolylinConnectorSpecification.IS_STARTING_LOCATION_FIXED, aFlag);
		}

		@Override
		public boolean getIsStartingLocationDraggable() {
			return getPropertyValue(RectPolylinConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE);
		}

		@Override
		public void setIsStartingLocationDraggable(boolean aFlag) {
			setPropertyValue(RectPolylinConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE, aFlag);
		}

		@Override
		public boolean getIsEndingLocationFixed() {
			return getPropertyValue(RectPolylinConnectorSpecification.IS_ENDING_LOCATION_FIXED);
		}

		@Override
		public void setIsEndingLocationFixed(boolean aFlag) {
			setPropertyValue(RectPolylinConnectorSpecification.IS_ENDING_LOCATION_FIXED, aFlag);
		}

		@Override
		public boolean getIsEndingLocationDraggable() {
			return getPropertyValue(RectPolylinConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE);
		}

		@Override
		public void setIsEndingLocationDraggable(boolean aFlag) {
			setPropertyValue(RectPolylinConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE, aFlag);
		}

		@Override
		public FGEPoint getCrossedControlPoint() {
			return getPropertyValue(RectPolylinConnectorSpecification.CROSSED_CONTROL_POINT);
		}

		@Override
		public void setCrossedControlPoint(FGEPoint aPoint) {
			setPropertyValue(RectPolylinConnectorSpecification.CROSSED_CONTROL_POINT, aPoint);
		}

		@Override
		public FGEPoint getFixedStartLocation() {
			return getPropertyValue(RectPolylinConnectorSpecification.FIXED_START_LOCATION);
		}

		@Override
		public void setFixedStartLocation(FGEPoint aPoint) {
			setPropertyValue(RectPolylinConnectorSpecification.FIXED_START_LOCATION, aPoint);
		}

		@Override
		public FGEPoint getFixedEndLocation() {
			return getPropertyValue(RectPolylinConnectorSpecification.FIXED_END_LOCATION);
		}

		@Override
		public void setFixedEndLocation(FGEPoint aPoint) {
			setPropertyValue(RectPolylinConnectorSpecification.FIXED_END_LOCATION, aPoint);
		}

		@Override
		public int getPixelOverlap() {
			return getPropertyValue(RectPolylinConnectorSpecification.PIXEL_OVERLAP);
		}

		@Override
		public void setPixelOverlap(int aPixelOverlap) {
			setPropertyValue(RectPolylinConnectorSpecification.PIXEL_OVERLAP, aPixelOverlap);
		}

		@Override
		public FGERectPolylin getPolylin() {
			return getPropertyValue(RectPolylinConnectorSpecification.POLYLIN);
		}

		@Override
		public void setPolylin(FGERectPolylin aPolylin) {
			setPropertyValue(RectPolylinConnectorSpecification.POLYLIN, aPolylin);
		}

		@Override
		public boolean getIsAdjustable() {
			return true;
		}

		@Override
		public void setIsAdjustable(boolean aFlag) {
		}

	}

	protected class InspectedCurvedPolylinConnectorSpecification extends
			AbstractInspectedConnectorSpecification<CurvedPolylinConnectorSpecification> implements CurvedPolylinConnectorSpecification {

		protected InspectedCurvedPolylinConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller,
				CurvedPolylinConnectorSpecification defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public CurvedPolylinConnectorSpecification getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ConnectorNode) {
				if (((ConnectorNode<?>) node).getConnectorSpecification() instanceof CurvedPolylinConnectorSpecification) {
					return (CurvedPolylinConnectorSpecification) ((ConnectorNode<?>) node).getConnectorSpecification();
				}
			}
			return null;
		}

		@Override
		public ConnectorType getConnectorType() {
			return ConnectorType.CURVED_POLYLIN;
		}

		@Override
		public CurvedPolylinConnector makeConnector(ConnectorNode<?> connectorNode) {
			return new CurvedPolylinConnector(connectorNode);
		}
	}
}