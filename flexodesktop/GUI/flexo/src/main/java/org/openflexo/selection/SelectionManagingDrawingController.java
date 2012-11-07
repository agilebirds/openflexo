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

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.FGEView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.toolbox.ToolBox;

/**
 * Default implementation for a DrawingController synchronized with a SelectionManager
 * 
 * @author sylvain
 * 
 * @param <D>
 */
public class SelectionManagingDrawingController<D extends Drawing<? extends FlexoModelObject>> extends DrawingController<D> implements
		SelectionListener {

	private static final Logger logger = Logger.getLogger(SelectionManagingDrawingController.class.getPackage().getName());

	private SelectionManager _selectionManager;

	public SelectionManagingDrawingController(D drawing, SelectionManager selectionManager, FGEModelFactory factory) {
		super(drawing, factory);
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
			selectionManager.removeFromSelectionListeners(this);
		}
		_selectionManager = selectionManager;
		if (_selectionManager != null) {
			selectionManager.addToSelectionListeners(this);
		}
	}

	@Override
	public void setSelectedObjects(List<? extends GraphicalRepresentation<?>> someSelectedObjects) {
		if (_selectionManager != null) {
			_selectionManager.resetSelection();
		}
		super.setSelectedObjects(someSelectedObjects);
	}

	@Override
	public void addToSelectedObjects(GraphicalRepresentation<?> anObject) {
		// logger.info("_selectionManager="+_selectionManager);
		// logger.info("anObject.getDrawable()="+anObject.getDrawable());
		if (_selectionManager != null) {
			for (FlexoModelObject o : new ArrayList<FlexoModelObject>(_selectionManager.getSelection())) {
				if (!mayRepresent(o)) {
					_selectionManager.removeFromSelected(o);
				}
			}
		}
		super.addToSelectedObjects(anObject);
		if (_selectionManager != null) {
			if (anObject.getDrawable() instanceof FlexoModelObject) {
				// logger.info("Je rajoute "+anObject.getDrawable()+" dans le SM");
				_selectionManager.addToSelected((FlexoModelObject) anObject.getDrawable());
			}
		}
	}

	@Override
	public void removeFromSelectedObjects(GraphicalRepresentation<?> anObject) {
		if (_selectionManager != null) {
			for (FlexoModelObject o : new ArrayList<FlexoModelObject>(_selectionManager.getSelection())) {
				if (!mayRepresent(o)) {
					_selectionManager.removeFromSelected(o);
				}
			}
		}
		super.removeFromSelectedObjects(anObject);
		if (_selectionManager != null) {
			if (anObject.getDrawable() instanceof FlexoModelObject) {
				_selectionManager.removeFromSelected((FlexoModelObject) anObject.getDrawable());
			}
		}
	}

	@Override
	public void clearSelection() {
		super.clearSelection();
		notifyObservers(new EmptySelection());
	}

	@Override
	public void selectDrawing() {
		super.selectDrawing();
		if (_selectionManager != null) {
			_selectionManager.setSelectedObject(getDrawing().getModel());
		}
	}

	public static class ShowContextualMenuControl extends MouseClickControl {

		public ShowContextualMenuControl() {
			this(false);
		}

		public ShowContextualMenuControl(boolean controlDown) {
			super("Show contextual menu", MouseButton.RIGHT, 1, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						java.awt.event.MouseEvent event) {
					FGEView<?> view = controller.getDrawingView().viewForObject(graphicalRepresentation);
					Point newPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
					controller.setLastClickedPoint(new FGEPoint(newPoint.x / controller.getScale(), newPoint.y / controller.getScale()));
					controller.setLastSelectedGR(graphicalRepresentation);
					if (!(graphicalRepresentation.getDrawable() instanceof FlexoModelObject)) {
						return false;
					}
					if (!(controller instanceof SelectionManagingDrawingController)) {
						logger.warning("Cannot show contextual menu: controller " + controller
								+ " does not implement SelectionManagingDrawingController");
						return false;
					}
					FlexoModelObject o = (FlexoModelObject) graphicalRepresentation.getDrawable();
					SelectionManager selectionManager = ((SelectionManagingDrawingController<?>) controller).getSelectionManager();
					if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
						if (!selectionManager.selectionContains(o)) {
							if (selectionManager.getSelectionSize() < 2) {
								selectionManager.setSelectedObject(o);
							} else {
								selectionManager.addToSelected(o);
							}
						}
					} else {
						boolean isControlDown = event.isControlDown();
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
			}, false, controlDown, false, false);
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

	private boolean mayRepresent(FlexoModelObject o) {
		return getDrawing().getGraphicalRepresentation(o) != null;
	}

	@Override
	public void fireObjectDeselected(FlexoModelObject object) {
		if (mayRepresent(object)) {
			super.removeFromSelectedObjects(getDrawing().getGraphicalRepresentation(object));
		}
	}

	@Override
	public void fireObjectSelected(FlexoModelObject object) {
		if (mayRepresent(object)) {
			super.addToSelectedObjects(getDrawing().getGraphicalRepresentation(object));
		}
	}

	@Override
	public void fireResetSelection() {
		super.clearSelection();
	}

	@Override
	public void setLastClickedPoint(FGEPoint lastClickedPoint) {
		super.setLastClickedPoint(lastClickedPoint);
		if (_selectionManager instanceof MouseSelectionManager) {
			((MouseSelectionManager) _selectionManager).setLastClickedPoint(new Point((int) lastClickedPoint.getX(), (int) lastClickedPoint
					.getY()));
		}
	}

	@Override
	public void setLastSelectedGR(GraphicalRepresentation<?> lastSelectedGR) {
		super.setLastSelectedGR(lastSelectedGR);
		if (_selectionManager instanceof MouseSelectionManager) {
			if (lastSelectedGR.getDrawable() instanceof FlexoModelObject) {
				((MouseSelectionManager) _selectionManager).setLastSelectedObject((FlexoModelObject) lastSelectedGR.getDrawable());
			}
		}
	}
}
