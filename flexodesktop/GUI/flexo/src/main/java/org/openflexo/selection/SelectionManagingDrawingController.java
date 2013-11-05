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
package org.openflexo.selection;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseClickControlActionImpl;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.view.JFGEView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.toolbox.ToolBox;

/**
 * Default implementation for a DianaEditor synchronized with a SelectionManager
 * 
 * @author sylvain
 * 
 * @param <D>
 */
public class SelectionManagingDrawingController<M extends FlexoObject> extends JDianaInteractiveEditor<M> implements SelectionListener {

	private static final Logger logger = Logger.getLogger(SelectionManagingDrawingController.class.getPackage().getName());

	private SelectionManager _selectionManager;

	public SelectionManagingDrawingController(Drawing<M> drawing, SelectionManager selectionManager, FGEModelFactory factory,
			SwingToolFactory toolFactory) {
		super(drawing, factory, toolFactory);
		_selectionManager = selectionManager;
		if (_selectionManager != null) {
			selectionManager.addToSelectionListeners(this);
		}
	}

	@Override
	public void delete() {
		super.delete();
		if (_selectionManager != null) {
			_selectionManager.removeFromSelectionListeners(this);
		}
		_selectionManager = null;
	}

	public SelectionManager getSelectionManager() {
		return _selectionManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		if (_selectionManager != null) {
			_selectionManager.removeFromSelectionListeners(this);
		}
		_selectionManager = selectionManager;
		if (_selectionManager != null) {
			selectionManager.addToSelectionListeners(this);
		}
	}

	@Override
	public void setSelectedObjects(List<? extends DrawingTreeNode<?, ?>> someSelectedObjects) {
		if (_selectionManager != null) {
			_selectionManager.resetSelection();
		}
		super.setSelectedObjects(someSelectedObjects);
	}

	@Override
	public void addToSelectedObjects(DrawingTreeNode<?, ?> anObject) {
		// logger.info("_selectionManager="+_selectionManager);
		// logger.info("anObject.getDrawable()="+anObject.getDrawable());
		if (_selectionManager != null) {
			for (FlexoObject o : new ArrayList<FlexoObject>(_selectionManager.getSelection())) {
				if (!mayRepresent(o)) {
					_selectionManager.removeFromSelected(o);
				}
			}
		}
		super.addToSelectedObjects(anObject);
		if (_selectionManager != null) {
			if (anObject != null && anObject.getDrawable() instanceof FlexoObject) {
				// logger.info("Je rajoute "+anObject.getDrawable()+" dans le SM");
				_selectionManager.addToSelected((FlexoObject) anObject.getDrawable());
			}
		}
	}

	@Override
	public void removeFromSelectedObjects(DrawingTreeNode<?, ?> anObject) {
		if (_selectionManager != null) {
			for (FlexoObject o : new ArrayList<FlexoObject>(_selectionManager.getSelection())) {
				if (!mayRepresent(o)) {
					_selectionManager.removeFromSelected(o);
				}
			}
		}
		super.removeFromSelectedObjects(anObject);
		if (_selectionManager != null) {
			if (anObject.getDrawable() instanceof FlexoObject) {
				_selectionManager.removeFromSelected((FlexoObject) anObject.getDrawable());
			}
		}
	}

	/*@Override
	public void clearSelection() {
		super.clearSelection();
		notifyObservers(new EmptySelection());
	}*/

	@Override
	public void selectDrawing() {
		super.selectDrawing();
		if (_selectionManager != null) {
			_selectionManager.setSelectedObject(getDrawing().getModel());
		}
	}

	public static class ShowContextualMenuControl extends MouseClickControlImpl<SelectionManagingDrawingController<?>> {

		public ShowContextualMenuControl(FGEModelFactory factory) {
			this(factory, false);
		}

		public ShowContextualMenuControl(FGEModelFactory factory, boolean controlDown) {
			super("Show contextual menu", MouseButton.RIGHT, 1, new MouseClickControlActionImpl<SelectionManagingDrawingController<?>>() {

				@Override
				public boolean handleClick(DrawingTreeNode<?, ?> node, SelectionManagingDrawingController<?> controller,
						MouseControlContext context) {
					JFGEView<?, ?> view = controller.getDrawingView().viewForNode(node);

					Point newPoint = getPointInView(node, controller, context);
					controller.setLastSelectedNode(node);
					controller.setLastClickedPoint(
							node.convertLocalViewCoordinatesToRemoteNormalizedPoint(newPoint, node, controller.getScale()), node);

					if (!(node.getDrawable() instanceof FlexoObject)) {
						return false;
					}
					if (!(controller instanceof SelectionManagingDrawingController)) {
						logger.warning("Cannot show contextual menu: controller " + controller
								+ " does not implement SelectionManagingDrawingController");
						return false;
					}
					FlexoObject o = (FlexoObject) node.getDrawable();
					SelectionManager selectionManager = ((SelectionManagingDrawingController) controller).getSelectionManager();
					if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
						if (!selectionManager.selectionContains(o)) {
							if (selectionManager.getSelectionSize() < 2) {
								selectionManager.setSelectedObject(o);
							} else {
								selectionManager.addToSelected(o);
							}
						}
					} else {
						boolean isControlDown = context.isControlDown();
						if (isControlDown) {
							if (!selectionManager.selectionContains(o)) {
								selectionManager.addToSelected(o);
							}
						} else {
							if (!selectionManager.selectionContains(o)) {
								selectionManager.setSelectedObject(o);
							}
						}
					}
					selectionManager.getContextualMenuManager().showPopupMenuForObject(o, (Component) view, newPoint);
					return true;
				}
			}, false, controlDown, false, false, factory);
		}

	}

	@Override
	public void fireBeginMultipleSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireEndMultipleSelection() {
		// TODO Auto-generated method stub

	}

	private boolean mayRepresent(FlexoObject o) {
		return getDrawing().getDrawingTreeNode(o) != null;
	}

	@Override
	public void fireObjectDeselected(FlexoObject object) {
		if (mayRepresent(object)) {
			super.removeFromSelectedObjects(getDrawing().getDrawingTreeNode(object));
		}
	}

	@Override
	public void fireObjectSelected(FlexoObject object) {
		if (mayRepresent(object)) {
			super.addToSelectedObjects(getDrawing().getDrawingTreeNode(object));
		}
	}

	@Override
	public void fireResetSelection() {
		super.clearSelection();
	}

	@Override
	public void setLastClickedPoint(FGEPoint lastClickedPoint, DrawingTreeNode<?, ?> node) {
		super.setLastClickedPoint(lastClickedPoint, node);
		if (_selectionManager instanceof MouseSelectionManager) {
			((MouseSelectionManager) _selectionManager).setLastClickedPoint(new Point((int) lastClickedPoint.getX(), (int) lastClickedPoint
					.getY()));
			if (node.getDrawable() instanceof FlexoObject) {
				((MouseSelectionManager) _selectionManager).setLastSelectedObject((FlexoObject) node.getDrawable());
			}
		}
	}

}
