package org.openflexo.fge.control;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;

public interface MouseDragControlAction {

	public static final Logger logger = Logger.getLogger(MouseDragControlAction.class.getPackage().getName());

	public static enum MouseDragControlActionType {
		NONE, MOVE, RECTANGLE_SELECTING, ZOOM, CUSTOM;

		public MouseDragControlAction makeAction() {
			/*if (this == NONE) {
				return new None();
			} else if (this == MOVE) {
				return new MoveAction();
			} else if (this == RECTANGLE_SELECTING) {
				return new RectangleSelectingAction();
			} else if (this == ZOOM) {
				return new ZoomAction();
			} else if (this == CUSTOM) {
				return new CustomDragControlAction() {

					@Override
					public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
						logger.info("Perform mouse DRAGGED on undefined CUSTOM MouseDragControlActionImpl");
						return true;
					}

					@Override
					public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
						logger.info("Perform mouse PRESSED on undefined CUSTOM MouseDragControlActionImpl");
						return false;
					}

					@Override
					public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event,
							boolean isSignificativeDrag) {
						logger.info("Perform mouse RELEASED on undefined CUSTOM MouseDragControlActionImpl");
						return false;
					}

				};
			}*/
			logger.warning("Not implemented !!!");
			return null;
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
	public abstract boolean handleMousePressed(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event);

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
	public abstract boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event,
			boolean isSignificativeDrag);

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
	public abstract boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event);

}