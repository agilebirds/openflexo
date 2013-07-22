package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.geom.FGEPoint;

public class CurveConnectorSpecificationImpl extends ConnectorSpecificationImpl implements CurveConnectorSpecification {

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
		this.cp1RelativeToStartObject = aPoint;
	}

	@Override
	public FGEPoint getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	@Override
	public void setCp2RelativeToEndObject(FGEPoint aPoint) {
		this.cp2RelativeToEndObject = aPoint;
	}

	@Override
	public FGEPoint getCpPosition() {
		return cpPosition;
	}

	@Override
	public void setCpPosition(FGEPoint cpPosition) {
		this.cpPosition = cpPosition;
	}

	@Override
	public boolean getAreBoundsAdjustable() {
		return areBoundsAdjustable;
	}

	@Override
	public void setAreBoundsAdjustable(boolean aFlag) {
		if (areBoundsAdjustable != aFlag) {
			areBoundsAdjustable = aFlag;
			/*if (getGraphicalRepresentation() != null) {
				updateControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}*/
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

}
