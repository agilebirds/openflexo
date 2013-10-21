package org.openflexo.fge.view;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public interface ConnectorView<O, C> extends FGEView<O, C> {

	public abstract ConnectorNode<O> getNode();

	public FGEConnectorGraphics getFGEGraphics();

}