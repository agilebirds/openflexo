/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.fge.swing;

import static org.openflexo.fge.control.actions.AbstractMouseDragControlActionImpl.logger;

import javax.swing.JComponent;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.CustomMouseClickControl;
import org.openflexo.fge.control.CustomMouseControl.MouseButton;
import org.openflexo.fge.control.CustomMouseDragControl;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.control.MouseDragControlAction.MouseDragControlActionType;
import org.openflexo.fge.control.actions.AbstractMouseClickControlActionImpl;
import org.openflexo.fge.control.actions.AbstractMouseDragControlActionImpl;
import org.openflexo.fge.control.actions.CustomClickControlAction;
import org.openflexo.fge.control.actions.CustomDragControlAction;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.control.actions.MultipleSelectionAction;
import org.openflexo.fge.control.actions.RectangleSelectingAction;
import org.openflexo.fge.control.actions.SelectionAction;
import org.openflexo.fge.control.actions.ZoomAction;
import org.openflexo.fge.swing.view.JConnectorView;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.fge.swing.view.JShapeView;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.listener.FGEViewMouseListener;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingFactory implements DianaViewFactory<SwingFactory, JComponent> {

	public static SwingFactory INSTANCE = new SwingFactory();

	private SwingFactory() {
	}

	/**
	 * Build and return a MouseListener for supplied node and view<br>
	 * Here return null as a simple viewer doesn't allow any editing facility
	 * 
	 * @param node
	 * @param view
	 * @return
	 */
	@Override
	public <O> FGEViewMouseListener makeViewMouseListener(DrawingTreeNode<O, ?> node, FGEView<O, ? extends JComponent> view,
			AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new FGEViewMouseListener(node, view);
	}

	/**
	 * Instantiate a new JDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public <M> JDrawingView<M> makeDrawingView(AbstractDianaEditor<M, SwingFactory, JComponent> controller) {
		return new JDrawingView<M>(controller);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> JShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new JShapeView<O>(shapeNode, controller);
	}

	/**
	 * Instantiate a new JConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> JConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode,
			AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new JConnectorView<O>(connectorNode, controller);
	}

	@Override
	public CustomMouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new CustomMouseClickControlImpl(aName, button, clickCount, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	@Override
	public CustomMouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new CustomMouseClickControlImpl(aName, button, clickCount, actionType, shiftPressed, ctrlPressed, metaPressed, altPressed,
				this);
	}

	@Override
	public CustomMouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<?> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new CustomMouseClickControlImpl(aName, button, clickCount, action, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	@Override
	public CustomMouseDragControl makeMouseDragControl(String aName, MouseButton button, boolean shiftPressed, boolean ctrlPressed,
			boolean metaPressed, boolean altPressed) {
		return new CustomMouseDragControlImpl(aName, button, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	@Override
	public CustomMouseDragControl makeMouseDragControl(String aName, MouseButton button, MouseDragControlActionType actionType,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new CustomMouseDragControlImpl(aName, button, actionType, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	@Override
	public CustomMouseDragControl makeMouseDragControl(String aName, MouseButton button, MouseDragControlAction<?> action,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new CustomMouseDragControlImpl(aName, button, action, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	public MouseDragControlAction<?> makeMouseDragControlAction(MouseDragControlActionType actionType) {
		switch (actionType) {
		case NONE:
			return new AbstractMouseDragControlActionImpl.None();
		case MOVE:
			return new MoveAction();
		case RECTANGLE_SELECTING:
			return new RectangleSelectingAction();
		case ZOOM:
			return new ZoomAction();
		case CUSTOM:
			return new CustomDragControlAction() {

				@Override
				public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller,
						MouseControlInfo controlInfo) {
					logger.info("Perform mouse DRAGGED on undefined CUSTOM AbstractMouseDragControlActionImpl");
					return true;
				}

				@Override
				public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller,
						MouseControlInfo controlInfo) {
					logger.info("Perform mouse PRESSED on undefined CUSTOM AbstractMouseDragControlActionImpl");
					return false;
				}

				@Override
				public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller,
						MouseControlInfo controlInfo, boolean isSignificativeDrag) {
					logger.info("Perform mouse RELEASED on undefined CUSTOM AbstractMouseDragControlActionImpl");
					return false;
				}

			};
		default:
			logger.warning("Unexpected actionType " + actionType);
			return null;
		}
	}

	public MouseClickControlAction<?> makeMouseClickControlAction(MouseClickControlActionType actionType) {
		switch (actionType) {
		case NONE:
			return new AbstractMouseClickControlActionImpl.None();
		case SELECTION:
			return new SelectionAction();
		case MULTIPLE_SELECTION:
			return new MultipleSelectionAction();
		case CUSTOM:
			return new CustomClickControlAction() {
				@Override
				public boolean handleClick(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller,
						MouseControlInfo controlInfo) {
					logger.info("Perform undefined CUSTOM AbstractMouseClickControlActionImpl");
					return true;
				}
			};
		default:
			logger.warning("Unexpected actionType " + actionType);
			return null;
		}
	}

	public CustomMouseClickControl<?> makeMouseClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, false, false, false);
	}

	public CustomMouseClickControl<?> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<?, ?> action) {
		return makeMouseClickControl(aName, button, clickCount, action, false, false, false, false);
	}

	public CustomMouseClickControl<?> makeMouseShiftClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, true, false, false, false);
	}

	public CustomMouseClickControl<?> makeMouseControlClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, true, false, false);
	}

	public CustomMouseClickControl<?> makeMouseMetaClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, false, true, false);
	}

	public CustomMouseClickControl<?> makeMouseAltClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, false, false, true);
	}

	public CustomMouseClickControl<?> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, false, false, false);
	}

	public CustomMouseClickControl<?> makeMouseShiftClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, true, false, false, false);
	}

	public CustomMouseClickControl<?> makeMouseControlClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, true, false, false);
	}

	public CustomMouseClickControl<?> makeMouseMetaClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, false, true, false);
	}

	public CustomMouseClickControl<?> makeMouseAltClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, false, false, true);
	}

	public CustomMouseDragControl<?> makeMouseDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, false, false, false);
	}

	public CustomMouseDragControl<?> makeMouseShiftDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, true, false, false, false);
	}

	public CustomMouseDragControl<?> makeMouseControlDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, true, false, false);
	}

	public CustomMouseDragControl<?> makeMouseMetaDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, false, true, false);
	}

	public CustomMouseDragControl<?> makeMouseAltDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, false, false, true);
	}

	public CustomMouseDragControl<?> makeMouseDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, false, false, false);
	}

	public CustomMouseDragControl<?> makeMouseShiftDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, true, false, false, false);
	}

	public CustomMouseDragControl<?> makeMouseControlDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, true, false, false);
	}

	public CustomMouseDragControl<?> makeMouseMetaDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, false, true, false);
	}

	public CustomMouseDragControl<?> makeMouseAltDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, false, false, true);
	}

}
