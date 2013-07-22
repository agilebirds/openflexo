package org.openflexo.fge.connectors.impl;

import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;

public class CurvedPolylinConnectorSpecificationImpl extends ConnectorSpecificationImpl implements CurvedPolylinConnectorSpecification {

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public CurvedPolylinConnectorSpecificationImpl() {
		super();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVED_POLYLIN;
	}

	@Override
	public CurvedPolylinConnectorSpecification clone() {
		CurvedPolylinConnectorSpecification returned = new CurvedPolylinConnectorSpecificationImpl();
		return returned;
	}

}
