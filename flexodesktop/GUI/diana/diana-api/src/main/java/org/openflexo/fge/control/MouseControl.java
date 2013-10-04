package org.openflexo.fge.control;

import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.DrawingTreeNode;

public interface MouseControl {

	public static enum MouseButton {
		LEFT, RIGHT, CENTER
	}

	public abstract boolean isApplicable(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent e);

	public abstract boolean isModelEditionAction();

}