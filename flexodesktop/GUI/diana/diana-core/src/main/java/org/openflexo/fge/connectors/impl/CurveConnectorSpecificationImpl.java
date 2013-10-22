package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class CurveConnectorSpecificationImpl extends ConnectorSpecificationImpl implements CurveConnectorSpecification {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CurveConnectorSpecification.class.getPackage().getName());

	private FGEPoint cp1RelativeToStartObject;
	private FGEPoint cp2RelativeToEndObject;
	private FGEPoint cpPosition;

	private boolean areBoundsAdjustable;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public CurveConnectorSpecificationImpl() {
		super();
	}

	@Override
	public FGEPoint getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	@Override
	public void setCp1RelativeToStartObject(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(CP1_RELATIVE_TO_START_OBJECT, cp1RelativeToStartObject);
		if (notification != null) {
			this.cp1RelativeToStartObject = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public FGEPoint getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	@Override
	public void setCp2RelativeToEndObject(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(CP2_RELATIVE_TO_END_OBJECT, cp2RelativeToEndObject);
		if (notification != null) {
			this.cp2RelativeToEndObject = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public FGEPoint getCpPosition() {
		return cpPosition;
	}

	@Override
	public void setCpPosition(FGEPoint cpPosition) {
		FGEAttributeNotification notification = requireChange(CP_POSITION, cpPosition);
		if (notification != null) {
			this.cpPosition = cpPosition;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAreBoundsAdjustable() {
		return areBoundsAdjustable;
	}

	@Override
	public void setAreBoundsAdjustable(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(ARE_BOUNDS_ADJUSTABLE, aFlag);
		if (notification != null) {
			areBoundsAdjustable = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVE;
	}

	/*@Override
	public CurveConnectorSpecification clone() {
		CurveConnectorSpecification returned = (CurveConnectorSpecification) cloneObject();
		returned.setCpPosition(getCpPosition());
		returned.setCp1RelativeToStartObject(getCp1RelativeToStartObject());
		returned.setCp2RelativeToEndObject(getCp2RelativeToEndObject());
		returned.setAreBoundsAdjustable(getAreBoundsAdjustable());
		return returned;
	}*/

	@Override
	public CurveConnector makeConnector(ConnectorNode<?> connectorNode) {
		CurveConnector returned = new CurveConnector(connectorNode);
		getPropertyChangeSupport().addPropertyChangeListener(returned);
		return returned;
	}

}
