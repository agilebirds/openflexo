package org.openflexo.fge.control;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;

public interface MouseDragControlAction<C extends DrawingController<?>> extends MouseControlAction<C> {

	public static final Logger logger = Logger.getLogger(MouseDragControlAction.class.getPackage().getName());

	public static enum MouseDragControlActionType {
		NONE, MOVE, RECTANGLE_SELECTING, ZOOM, CUSTOM;

		public MouseDragControlAction<?> makeAction(FGEModelFactory factory) {
			return factory.makeMouseDragControlAction(this);
		}
	}

	public abstract MouseDragControlActionType getActionType();

	/**
	 * Handle mouse pressed event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @return
	 */
	public abstract boolean handleMousePressed(DrawingTreeNode<?, ?> node, C controller, MouseEvent event);

	/**
	 * Handle mouse released event, by performing what is required here Return flag indicating if event has been correctely handled and
	 * thus, should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @param isSignificativeDrag
	 *            TODO
	 * @return
	 */
	public abstract boolean handleMouseReleased(DrawingTreeNode<?, ?> node, C controller, MouseEvent event, boolean isSignificativeDrag);

	/**
	 * Handle mouse dragged event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @return
	 */
	public abstract boolean handleMouseDragged(DrawingTreeNode<?, ?> node, C controller, MouseEvent event);

}