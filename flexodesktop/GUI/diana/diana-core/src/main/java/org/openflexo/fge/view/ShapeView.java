package org.openflexo.fge.view;

import org.openflexo.fge.Drawing.ShapeNode;

public interface ShapeView<O, C> extends FGEContainerView<O, C> {

	public abstract ShapeNode<O> getNode();

}