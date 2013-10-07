package org.openflexo.fge.view;

import org.openflexo.fge.Drawing.ConnectorNode;

public interface ConnectorView<O, C> extends FGEView<O, C> {

	public abstract ConnectorNode<O> getNode();

}