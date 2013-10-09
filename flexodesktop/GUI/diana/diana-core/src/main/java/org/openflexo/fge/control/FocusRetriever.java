package org.openflexo.fge.control;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.cp.ControlArea;

public interface FocusRetriever<ME> {

	public abstract DianaInteractiveViewer<?, ?, ?> getController();

	public abstract void handleMouseMove(ME event);

	public abstract boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, ME event);

	public abstract ControlArea<?> getFocusedControlAreaForDrawable(DrawingTreeNode<?, ?> node, ME event);

	public abstract ControlArea<?> getFocusedControlAreaForDrawable(DrawingTreeNode<?, ?> node, ContainerNode<?, ?> container, ME event);

	public abstract DrawingTreeNode<?, ?> getFocusedObject(ME event);

	public abstract DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, ME event);

	public abstract double getScale();

}