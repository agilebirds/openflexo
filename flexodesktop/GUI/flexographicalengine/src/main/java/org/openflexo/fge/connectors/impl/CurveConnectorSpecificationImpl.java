package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGENotification;

public class CurveConnectorSpecificationImpl extends ConnectorSpecificationImpl implements CurveConnectorSpecification {

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
		FGENotification notification = requireChange(CurveConnectorParameters.cp1RelativeToStartObject, cp1RelativeToStartObject);
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
		FGENotification notification = requireChange(CurveConnectorParameters.cp2RelativeToEndObject, cp2RelativeToEndObject);
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
		FGENotification notification = requireChange(CurveConnectorParameters.cpPosition, cpPosition);
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
		FGENotification notification = requireChange(CurveConnectorParameters.areBoundsAdjustable, aFlag);
		if (notification != null) {
			areBoundsAdjustable = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVE;
	}

	@Override
	public CurveConnectorSpecification clone() {
		CurveConnectorSpecification returned = new CurveConnectorSpecificationImpl();
		returned.setCpPosition(getCpPosition());
		returned.setCp1RelativeToStartObject(getCp1RelativeToStartObject());
		returned.setCp2RelativeToEndObject(getCp2RelativeToEndObject());
		returned.setAreBoundsAdjustable(getAreBoundsAdjustable());
		return returned;
	}

	@Override
	public CurveConnector makeConnector(ConnectorNode<?> connectorNode) {
		CurveConnector returned = new CurveConnector(connectorNode);
		addObserver(returned);
		return returned;
	}

}
