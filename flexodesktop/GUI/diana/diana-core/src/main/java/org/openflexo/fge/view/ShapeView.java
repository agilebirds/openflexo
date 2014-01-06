package org.openflexo.fge.view;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.graphics.FGEShapeGraphics;

public interface ShapeView<O, C> extends FGEContainerView<O, C> {

	public abstract ShapeNode<O> getNode();

	public FGEShapeGraphics getFGEGraphics();

}