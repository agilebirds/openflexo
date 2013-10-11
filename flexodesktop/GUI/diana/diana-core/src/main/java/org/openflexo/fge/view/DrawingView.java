package org.openflexo.fge.view;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.actions.RectangleSelectingAction;

public interface DrawingView<M, C> extends FGEContainerView<M, C> {

	public abstract Drawing<M> getDrawing();

	public abstract M getDrawable();

	public abstract AbstractDianaEditor<M, ?, ? super C> getController();

	public abstract <O> FGEView<?, ?> viewForNode(DrawingTreeNode<?, ?> node);

	public abstract ConnectorView<?, ?> connectorViewForNode(ConnectorNode<?> node);

	public abstract ShapeView<?, ?> shapeViewForNode(ShapeNode<?> node);

	public abstract boolean contains(FGEView<?, ?> view);

	public abstract void delete();

	public void setRectangleSelectingAction(RectangleSelectingAction action);

	public void resetRectangleSelectingAction();

}