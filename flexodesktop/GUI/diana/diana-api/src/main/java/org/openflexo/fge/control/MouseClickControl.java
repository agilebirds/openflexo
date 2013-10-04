package org.openflexo.fge.control;

import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.MouseClickControlAction.MouseClickControlActionType;

public interface MouseClickControl extends MouseControl {

	/**
	 * Handle click event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 */
	public abstract void handleClick(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event);

	public abstract MouseClickControlActionType getActionType();

	public abstract void setActionType(MouseClickControlActionType actionType);

}