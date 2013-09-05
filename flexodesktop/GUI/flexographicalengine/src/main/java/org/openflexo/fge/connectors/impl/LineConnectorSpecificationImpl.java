package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGENotification;

public class LineConnectorSpecificationImpl extends ConnectorSpecificationImpl implements LineConnectorSpecification {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LineConnectorSpecification.class.getPackage().getName());

	private FGEPoint cp1RelativeToStartObject;
	private FGEPoint cp2RelativeToEndObject;
	private LineConnectorType lineConnectorType = LineConnectorType.MINIMAL_LENGTH;

	// Used for deserialization
	public LineConnectorSpecificationImpl() {
		super();
	}

	@Override
	public LineConnectorType getLineConnectorType() {
		return lineConnectorType;
	}

	@Override
	public void setLineConnectorType(LineConnectorType aLineConnectorType) {
		FGENotification notification = requireChange(LINE_CONNECTOR_TYPE, aLineConnectorType);
		if (notification != null) {
			lineConnectorType = aLineConnectorType;
			hasChanged(notification);
		}
	}

	@Override
	public FGEPoint getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	@Override
	public void setCp1RelativeToStartObject(FGEPoint aPoint) {
		FGENotification notification = requireChange(CP1_RELATIVE_TO_START_OBJECT, cp1RelativeToStartObject);
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
		FGENotification notification = requireChange(CP2_RELATIVE_TO_END_OBJECT, cp2RelativeToEndObject);
		if (notification != null) {
			this.cp2RelativeToEndObject = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.LINE;
	}

	@Override
	public LineConnectorSpecification clone() {
		LineConnectorSpecification returned = new LineConnectorSpecificationImpl();
		returned.setLineConnectorType(getLineConnectorType());
		returned.setCp1RelativeToStartObject(getCp1RelativeToStartObject());
		returned.setCp2RelativeToEndObject(getCp2RelativeToEndObject());
		return returned;
	}

	@Override
	public LineConnector makeConnector(ConnectorNode<?> connectorNode) {
		LineConnector returned = new LineConnector(connectorNode);
		addObserver(returned);
		return returned;
	}

}
