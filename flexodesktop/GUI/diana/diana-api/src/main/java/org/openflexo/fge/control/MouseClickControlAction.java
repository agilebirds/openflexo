package org.openflexo.fge.control;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;

public interface MouseClickControlAction<C extends DrawingController<?>> extends MouseControlAction<C> {

	public static final Logger logger = Logger.getLogger(MouseClickControlAction.class.getPackage().getName());

	public static enum MouseClickControlActionType {
		NONE, SELECTION, MULTIPLE_SELECTION, CONTINUOUS_SELECTION, CUSTOM;

		public MouseClickControlAction<?> makeAction(FGEModelFactory factory) {
			return factory.makeMouseClickControlAction(this);
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
	public abstract boolean handleClick(DrawingTreeNode<?, ?> node, C controller, MouseEvent event);

}