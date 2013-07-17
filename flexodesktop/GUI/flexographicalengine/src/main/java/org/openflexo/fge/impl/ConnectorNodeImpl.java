package org.openflexo.fge.impl;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.notifications.ConnectorModified;

public class ConnectorNodeImpl<O> extends DrawingTreeNodeImpl<O, ConnectorGraphicalRepresentation> implements ConnectorNode<O> {

	public ConnectorNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ConnectorGraphicalRepresentation> grBinding,
			DrawingTreeNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
	}

	@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return super.getGraphicalRepresentation();
	}

	@Override
	public void notifyConnectorChanged() {
		/*if (!isRegistered()) {
			return;
		}*/
		checkViewBounds();
		setChanged();
		notifyObservers(new ConnectorModified());
	}

}
