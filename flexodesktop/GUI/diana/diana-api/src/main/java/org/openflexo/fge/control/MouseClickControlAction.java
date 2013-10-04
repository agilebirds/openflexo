package org.openflexo.fge.control;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;

public interface MouseClickControlAction extends MouseControlAction {

	public static final Logger logger = Logger.getLogger(MouseClickControlAction.class.getPackage().getName());

	public static enum MouseClickControlActionType {
		NONE, SELECTION, MULTIPLE_SELECTION, CONTINUOUS_SELECTION, CUSTOM;

		protected MouseClickControlAction makeAction() {
			/*if (this == NONE) {
				return new None();
			} else if (this == SELECTION) {
				return new SelectionAction();
			} else if (this == MULTIPLE_SELECTION) {
				return new MultipleSelectionAction();
			} else if (this == CONTINUOUS_SELECTION) {
				return new ContinuousSelectionAction();
			} else if (this == CUSTOM) {
				return new CustomClickControlAction() {
					@Override
					public boolean handleClick(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
						logger.info("Perform undefined CUSTOM MouseClickControlActionImpl");
						return true;
					}
				};
			}*/
			logger.warning("Not implemented !!!");
			return null;
		}
	}

	public abstract MouseClickControlActionType getActionType();

	/**
	 * Handle click event, by performing what is required here Return flag indicating if event has been correctely handled and thus, should
	 * be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @return
	 */
	public abstract boolean handleClick(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event);

}