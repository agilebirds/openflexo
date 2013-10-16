package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.impl.FGEObjectImpl;

public abstract class ConnectorSpecificationImpl extends FGEObjectImpl implements ConnectorSpecification {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ConnectorSpecification.class.getPackage().getName());

	public ConnectorSpecificationImpl() {
		super();
	}

	@Override
	public abstract ConnectorType getConnectorType();

	// @Override
	// public abstract ConnectorSpecification clone();

}
