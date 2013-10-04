package org.openflexo.fge.control;

import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.DrawingTreeNode;

public interface MouseDragControl extends MouseControl {

	/**
	 * Handle mouse pressed event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event);

	/**
	 * Handle mouse released event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public void handleMouseReleased(DrawingController<?> controller, MouseEvent event);

	/**
	 * Handle mouse dragged event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public void handleMouseDragged(DrawingController<?> controller, MouseEvent event);

}