package org.openflexo.foundation.view.diagram.viewpoint.action;

import org.openflexo.fge.ConnectorGraphicalRepresentation;

public interface GRConnectorTemplate extends GRTemplate {

	@Override
	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation();

}
