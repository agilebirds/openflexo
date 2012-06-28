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
package org.openflexo.fib.editor.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.notifications.FIBEditorNotification;
import org.openflexo.fib.editor.notifications.FocusedObjectChange;
import org.openflexo.fib.editor.notifications.SelectedObjectChange;
import org.openflexo.fib.model.FIBAddingNotification;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRemovingNotification;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableViewDelegate<M extends FIBComponent, J extends JComponent> implements Observer, MouseListener, FocusListener {

	static final Logger logger = FlexoLogger.getLogger(FIBEditableViewDelegate.class.getPackage().getName());

	private Border focusBorder = BorderFactory.createLineBorder(Color.RED);
	private Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE);

	private boolean isFocused = false;
	private boolean isSelected = false;

	private FIBEditableView<M, J> view;

	public FIBEditableViewDelegate(FIBEditableView<M, J> view) {
		this.view = view;

		view.getJComponent().setFocusable(true);
		view.getJComponent().setRequestFocusEnabled(true);

		recursivelyAddListenersTo(view.getJComponent());

		// view.getJComponent().addMouseListener(this);
		// view.getJComponent().addFocusListener(this);

		view.getEditorController().addObserver(this);

		if (view.getPlaceHolders() != null) {
			for (PlaceHolder ph : view.getPlaceHolders()) {
				// Listen to drag'n'drop events
				new FIBDropTarget(ph);
			}
		}
	}

	public void delete() {
		logger.fine("Delete delegate view=" + view);
		recursivelyDeleteListenersFrom(view.getJComponent());
		view.getEditorController().deleteObserver(this);
		view = null;
	}

	private void recursivelyAddListenersTo(Component c) {
		c.addMouseListener(this);
		c.addFocusListener(this);
		// Listen to drag'n'drop events
		new FIBDropTarget(view);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (!isComponentRootComponentForAnyFIBView(c2)) {
					recursivelyAddListenersTo(c2);
				}
			}
		}
	}

	private void recursivelyDeleteListenersFrom(Component c) {
		c.removeMouseListener(this);
		c.removeFocusListener(this);

		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				if (!isComponentRootComponentForAnyFIBView(c2)) {
					recursivelyDeleteListenersFrom(c2);
				}
			}
		}
	}

	private boolean isComponentRootComponentForAnyFIBView(Component c) {
		Enumeration<FIBView> en = getController().getViews();
		while (en.hasMoreElements()) {
			FIBView v = en.nextElement();
			if (v.getResultingJComponent() == c) {
				return true;
			}
		}
		return false;
	}

	public FIBEditorController getEditorController() {
		return view.getEditorController();
	}

	public FIBController getController() {
		return view.getEditorController().getController();
	}

	public M getFIBComponent() {
		return view.getComponent();
	}

	public void setFocused(boolean aFlag) {
		if (isSelected) {
			return;
		}
		if (aFlag == isFocused) {
			return;
		}
		if (aFlag) {
			// logger.info("Set focused for "+getFIBComponent());
			isFocused = true;
			if (view.getJComponent().getBorder() == null) {
				view.getJComponent().setBorder(focusBorder);
			} else {
				view.getJComponent().setBorder(BorderFactory.createCompoundBorder(focusBorder, view.getJComponent().getBorder()));
			}
		} else {
			isFocused = false;
			if (view.getJComponent().getBorder() == focusBorder || view.getJComponent().getBorder() == selectedBorder) {
				view.getJComponent().setBorder(null);
			} else if (view.getJComponent().getBorder() instanceof CompoundBorder) {
				CompoundBorder b = (CompoundBorder) view.getJComponent().getBorder();
				view.getJComponent().setBorder(b.getInsideBorder());
			}
		}
	}

	public void setSelected(boolean aFlag) {
		if (aFlag == isSelected) {
			return;
		}
		if (aFlag) {
			if (isFocused) {
				setFocused(false);
			}
			isSelected = true;
			if (view.getJComponent().getBorder() == null) {
				view.getJComponent().setBorder(selectedBorder);
			} else {
				view.getJComponent().setBorder(BorderFactory.createCompoundBorder(selectedBorder, view.getJComponent().getBorder()));
			}
		} else {
			isSelected = false;
			if (view.getJComponent().getBorder() == focusBorder || view.getJComponent().getBorder() == selectedBorder) {
				view.getJComponent().setBorder(null);
			} else if (view.getJComponent().getBorder() instanceof CompoundBorder) {
				CompoundBorder b = (CompoundBorder) view.getJComponent().getBorder();
				view.getJComponent().setBorder(b.getInsideBorder());
			}
		}
	}

	public void setPlaceHoldersAreVisible(boolean aFlag) {
		if (view.getPlaceHolders() != null) {
			for (PlaceHolder ph : view.getPlaceHolders()) {
				ph.setVisible(aFlag);
				// if (aFlag) System.out.println("PlaceHolder "+ph+" becomes visible with "+ph.getBounds());
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		getEditorController().setSelectedObject(getFIBComponent());
		view.getJComponent().requestFocus();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
			getEditorController().getContextualMenu().displayPopupMenu(getFIBComponent(), view.getJComponent(), e);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// getController().setSelectedObject(null);
	}

	@Override
	public void focusGained(FocusEvent e) {
		getEditorController().setSelectedObject(getFIBComponent());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		getEditorController().setFocusedObject(getFIBComponent());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		getEditorController().setFocusedObject(null);
	}

	@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof FIBEditorNotification) {
			if (notification instanceof FocusedObjectChange) {
				FocusedObjectChange focusChange = (FocusedObjectChange) notification;
				// System.out.println("Receive "+focusChange);
				if (focusChange.oldValue() == getFIBComponent()) {
					setFocused(false);
				}
				if (focusChange.newValue() == getFIBComponent()) {
					setFocused(true);
				}
			} else if (notification instanceof SelectedObjectChange) {
				SelectedObjectChange selectionChange = (SelectedObjectChange) notification;
				// System.out.println("Receive "+focusChange);
				if (selectionChange.oldValue() == getFIBComponent()) {
					setSelected(false);
				}
				if (selectionChange.newValue() == getFIBComponent()) {
					setSelected(true);
				}
			}
		}
	}

	public void receivedModelNotifications(Observable o, FIBModelNotification dataModification) {
		// System.out.println("receivedModelNotifications o="+o+" dataModification="+dataModification);

		if (o instanceof FIBPanel && view instanceof FIBPanelView) {
			if (dataModification instanceof FIBAddingNotification) {
				if (((FIBAddingNotification) dataModification).getAddedValue() instanceof FIBComponent) {
					// addSubComponent((FIBComponent)((FIBAddingNotification)dataModification).getAddedValue());
					((FIBPanelView) view).updateLayout();
				}
			} else if (dataModification instanceof FIBRemovingNotification) {
				if (((FIBRemovingNotification) dataModification).getRemovedValue() instanceof FIBComponent) {
					// addSubComponent((FIBComponent)((FIBAddingNotification)dataModification).getAddedValue());
					((FIBPanelView) view).updateLayout();
				}
			} else if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBPanel.Parameters.border || n.getAttribute() == FIBPanel.Parameters.borderColor
						|| n.getAttribute() == FIBPanel.Parameters.borderTitle || n.getAttribute() == FIBPanel.Parameters.borderTop
						|| n.getAttribute() == FIBPanel.Parameters.borderLeft || n.getAttribute() == FIBPanel.Parameters.borderRight
						|| n.getAttribute() == FIBPanel.Parameters.borderBottom || n.getAttribute() == FIBPanel.Parameters.titleFont
						|| n.getAttribute() == FIBPanel.Parameters.darkLevel) {
					((FIBPanelView) view).updateBorder();
				} else if (n.getAttribute() == FIBPanel.Parameters.layout || n.getAttribute() == FIBPanel.Parameters.flowAlignment
						|| n.getAttribute() == FIBPanel.Parameters.boxLayoutAxis || n.getAttribute() == FIBPanel.Parameters.vGap
						|| n.getAttribute() == FIBPanel.Parameters.hGap || n.getAttribute() == FIBPanel.Parameters.rows
						|| n.getAttribute() == FIBPanel.Parameters.cols || n.getAttribute() == FIBPanel.Parameters.protectContent) {
					((FIBPanelView) view).updateLayout();
				}
			}
		}

		if (o instanceof FIBWidget) {
			FIBWidgetView widgetView = (FIBWidgetView) view;
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBWidget.Parameters.manageDynamicModel) {
					widgetView.getComponent().updateBindingModel();
				} else if (n.getAttribute() == FIBWidget.Parameters.readOnly) {
					widgetView.updateEnability();
				}
			}
		}

		if (o instanceof FIBMultipleValues) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBMultipleValues.Parameters.staticList || n.getAttribute() == FIBMultipleValues.Parameters.list
						|| n.getAttribute() == FIBMultipleValues.Parameters.array) {
					view.updateDataObject(view.getDataObject());
				}
			}
		}

		if (o instanceof FIBContainer) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBContainer.Parameters.subComponents && view instanceof FIBContainerView) {
					((FIBContainerView) view).updateLayout();
				}
			}
		}

		if (o instanceof FIBComponent) {
			if (dataModification instanceof FIBAttributeNotification) {
				FIBAttributeNotification n = (FIBAttributeNotification) dataModification;
				if (n.getAttribute() == FIBComponent.Parameters.constraints || n.getAttribute() == FIBComponent.Parameters.width
						|| n.getAttribute() == FIBComponent.Parameters.height || n.getAttribute() == FIBComponent.Parameters.minWidth
						|| n.getAttribute() == FIBComponent.Parameters.minHeight || n.getAttribute() == FIBComponent.Parameters.maxWidth
						|| n.getAttribute() == FIBComponent.Parameters.maxHeight
						|| n.getAttribute() == FIBComponent.Parameters.useScrollBar
						|| n.getAttribute() == FIBComponent.Parameters.horizontalScrollbarPolicy
						|| n.getAttribute() == FIBComponent.Parameters.verticalScrollbarPolicy) {
					FIBView parentView = view.getParentView();
					FIBEditorController controller = getEditorController();
					if (parentView instanceof FIBContainerView) {
						((FIBContainerView) parentView).updateLayout();
					}
					controller.notifyFocusedAndSelectedObject();
				} else if (n.getAttribute() == FIBComponent.Parameters.data) {
					view.updateDataObject(view.getDataObject());
				} else if (n.getAttribute() == FIBComponent.Parameters.font) {
					((FIBView) view).updateFont();
				} else if (n.getAttribute() == FIBComponent.Parameters.backgroundColor
						|| n.getAttribute() == FIBComponent.Parameters.foregroundColor
						|| n.getAttribute() == FIBComponent.Parameters.opaque) {
					((FIBView) view).updateGraphicalProperties();
					((FIBView) view).getJComponent().revalidate();
					((FIBView) view).getJComponent().repaint();
				}
			}
		}
	}

	public static class FIBDropTarget extends DropTarget {
		private FIBEditableView editableView;
		private PlaceHolder placeHolder = null;

		public FIBDropTarget(FIBEditableView editableView) {
			super(editableView.getJComponent(), DnDConstants.ACTION_COPY, editableView.getEditorController().getPalette()
					.buildPaletteDropListener(editableView.getJComponent(), editableView.getEditorController()), true);
			this.editableView = editableView;
			logger.fine("Made FIBDropTarget for " + getFIBComponent());
		}

		public FIBDropTarget(PlaceHolder placeHolder) {
			super(placeHolder, DnDConstants.ACTION_COPY, placeHolder.getView().getEditorController().getPalette()
					.buildPaletteDropListener(placeHolder, placeHolder.getView().getEditorController()), true);
			this.placeHolder = placeHolder;
			this.editableView = placeHolder.getView();
			logger.fine("Made FIBDropTarget for " + getFIBComponent());
		}

		public PlaceHolder getPlaceHolder() {
			return placeHolder;
		}

		public boolean isPlaceHolder() {
			return placeHolder != null;
		}

		public FIBComponent getFIBComponent() {
			return editableView.getComponent();
		}

		public FIBEditorController getFIBEditorController() {
			return editableView.getEditorController();
		}

	}
}
