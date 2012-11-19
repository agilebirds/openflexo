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
package org.openflexo.fib.view.widget;

import java.awt.Component;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBListDynamicModel;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBList;

public class FIBListWidget extends FIBMultipleValueWidget<FIBList, JList, Object> implements FIBSelectable {

	static final Logger logger = Logger.getLogger(FIBListWidget.class.getPackage().getName());

	protected JList _list;

	public FIBListWidget(FIBList model, FIBController controller) {
		super(model, controller);

		Object[] listData = { "Item1", "Item2", "Item3" };
		_list = new JList(listData);
		_list.setCellRenderer(getListCellRenderer());
		_list.setSelectionMode(model.getSelectionMode().getMode());
		if (model.getVisibleRowCount() != null) {
			_list.setVisibleRowCount(model.getVisibleRowCount());
		}
		// _list.setPrototypeCellValue("0123456789012345");
		if (model.getRowHeight() != null) {
			_list.setFixedCellHeight(model.getRowHeight());
		}

		_list.setLayoutOrientation(model.getLayoutOrientation().getSwingValue());

		_list.addFocusListener(this);

		_list.setBorder(BorderFactory.createEtchedBorder());

		// _list.setMinimumSize(new Dimension(60,60));
		// _list.setPreferredSize(new Dimension(60,60));
		_list.revalidate();
		_list.repaint();

		updateListModelWhenRequired();

		updateFont();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		updateListModelWhenRequired();
		if (getWidget().getData() != null && notEquals(getValue(), _list.getSelectedValue())) {

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}
			widgetUpdating = true;
			// updateList();
			_list.setSelectedValue(getValue(), true);
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), _list.getSelectedValue())) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + _list.getSelectedValue());
			}
			if (_list.getSelectedValue() != null && !widgetUpdating) {
				setValue(_list.getSelectedValue());
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public FIBListModel getListModel() {
		return (FIBListModel) super.getListModel();
	}

	protected synchronized void updateList() {
		if (listModel == null) {
			updateListModelWhenRequired();
		} else {
			_list.getSelectionModel().removeListSelectionListener((FIBListModel) listModel);
			listModel = new FIBListModel();
			setListModel((FIBListModel) listModel);
		}
	}

	@Override
	protected FIBListModel updateListModelWhenRequired() {
		if (listModel == null) {
			listModel = new FIBListModel();
			setListModel((FIBListModel) listModel);
		} else {
			FIBListModel newListModel = new FIBListModel();
			if (!newListModel.equals(listModel) || didLastKnownValuesChange()) {
				_list.getSelectionModel().removeListSelectionListener((FIBListModel) listModel);
				listModel = newListModel;
				setListModel((FIBListModel) listModel);
			}
		}
		return (FIBListModel) listModel;
	}

	private FIBListModel oldListModel = null;

	private void setListModel(FIBListModel aListModel) {
		// logger.info("************* Updating GUI with " + aListModel);
		widgetUpdating = true;
		if (oldListModel != null) {
			_list.getSelectionModel().removeListSelectionListener(oldListModel);
		}
		oldListModel = aListModel;
		_list.setLayoutOrientation(getWidget().getLayoutOrientation().getSwingValue());
		_list.setSelectionMode(getWidget().getSelectionMode().getMode());
		if (getWidget().getVisibleRowCount() != null) {
			_list.setVisibleRowCount(getWidget().getVisibleRowCount());
		} else {
			_list.setVisibleRowCount(-1);
		}
		if (getWidget().getRowHeight() != null) {
			_list.setFixedCellHeight(getWidget().getRowHeight());
		} else {
			_list.setFixedCellHeight(-1);
		}
		_list.setModel(aListModel);
		_list.revalidate();
		_list.repaint();
		_list.getSelectionModel().addListSelectionListener(aListModel);
		widgetUpdating = false;
		Object objectToSelect = null;
		if (getComponent().getSelected().isValid()) {
			objectToSelect = getComponent().getSelected().getBindingValue(getController());
		}
		if (objectToSelect == null && getWidget().getAutoSelectFirstRow() && _list.getModel().getSize() > 0) {
			objectToSelect = _list.getModel().getElementAt(0);
		}
		if (objectToSelect != null) {
			for (int i = 0; i < _list.getModel().getSize(); i++) {
				if (_list.getModel().getElementAt(i) == objectToSelect) {
					final int index = i;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							_list.setSelectedIndex(index);
						}
					});
				}
			}
		}

		/*if (getWidget().getAutoSelectFirstRow() && _list.getModel().getSize() > 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_list.setSelectedIndex(0);
				}
			});
		}*/
	}

	protected class FIBListModel extends FIBMultipleValueModel implements ListSelectionListener {
		private Object selectedObject;
		private Vector<Object> selection;

		public FIBListModel() {
			super();
			selectedObject = null;
			selection = new Vector<Object>();
		}

		public Object getSelectedObject() {
			return selectedObject;
		}

		public Vector<Object> getSelection() {
			return selection;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// Ignore extra messages.
			if (e.getValueIsAdjusting()) {
				return;
			}

			if (widgetUpdating) {
				return;
			}

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("valueChanged() selected index=" + getListSelectionModel().getMinSelectionIndex());
			}

			updateModelFromWidget();

			int i = getListSelectionModel().getMinSelectionIndex();
			int leadIndex = getListSelectionModel().getLeadSelectionIndex();
			if (!getListSelectionModel().isSelectedIndex(leadIndex)) {
				leadIndex = getListSelectionModel().getAnchorSelectionIndex();
			}
			while (!getListSelectionModel().isSelectedIndex(leadIndex) && i <= getListSelectionModel().getMaxSelectionIndex()) {
				leadIndex = i;
				i++;
			}

			selectedObject = getElementAt(leadIndex);

			Vector<Object> oldSelection = selection;
			selection = new Vector<Object>();
			for (i = getListSelectionModel().getMinSelectionIndex(); i <= getListSelectionModel().getMaxSelectionIndex(); i++) {
				if (getListSelectionModel().isSelectedIndex(i)) {
					selection.add(getElementAt(i));
				}
			}

			getDynamicModel().selected = selectedObject;
			getDynamicModel().selectedIndex = leadIndex;
			getDynamicModel().selection = selection;
			notifyDynamicModelChanged();

			if (getComponent().getSelected().isValid()) {
				logger.fine("Sets SELECTED binding with " + selectedObject);
				getComponent().getSelected().setBindingValue(selectedObject, getController());
			}

			updateFont();

			if (!ignoreNotifications) {
				getController().updateSelection(FIBListWidget.this, oldSelection, selection);
			}

			logger.fine((isFocused() ? "LEADER" : "SECONDARY") + " Selected is " + selectedObject);
			logger.fine((isFocused() ? "LEADER" : "SECONDARY") + " Selection is " + selection);

		}

		private boolean ignoreNotifications = false;

		public void addToSelectionNoNotification(Object o) {
			int index = indexOf(o);
			ignoreNotifications = true;
			getListSelectionModel().addSelectionInterval(index, index);
			ignoreNotifications = false;
		}

		public void removeFromSelectionNoNotification(Object o) {
			int index = indexOf(o);
			ignoreNotifications = true;
			getListSelectionModel().removeSelectionInterval(index, index);
			ignoreNotifications = false;
		}

		public void resetSelectionNoNotification() {
			ignoreNotifications = true;
			getListSelectionModel().clearSelection();
			ignoreNotifications = false;
		}

		public void addToSelection(Object o) {
			int index = indexOf(o);
			getListSelectionModel().addSelectionInterval(index, index);
		}

		public void removeFromSelection(Object o) {
			int index = indexOf(o);
			getListSelectionModel().removeSelectionInterval(index, index);
		}

		public void resetSelection() {
			getListSelectionModel().clearSelection();
		}

	}

	public ListSelectionModel getListSelectionModel() {
		return _list.getSelectionModel();
	}

	@Override
	public JList getJComponent() {
		return _list;
	}

	@Override
	public JList getDynamicJComponent() {
		return _list;
	}

	@Override
	public void updateFont() {
		super.updateFont();
		_list.setFont(getFont());
	}

	@Override
	public FIBListDynamicModel createDynamicModel() {
		return new FIBListDynamicModel(null);
	}

	@Override
	public FIBListDynamicModel getDynamicModel() {
		return (FIBListDynamicModel) super.getDynamicModel();
	}

	@Override
	public boolean mayRepresent(Object o) {
		return getListModel().indexOf(o) > -1;
	}

	@Override
	public void objectAddedToSelection(Object o) {
		getListModel().addToSelectionNoNotification(o);
	}

	@Override
	public void objectRemovedFromSelection(Object o) {
		getListModel().removeFromSelectionNoNotification(o);
	}

	@Override
	public void selectionResetted() {
		getListModel().resetSelectionNoNotification();
	}

	@Override
	public void addToSelection(Object o) {
		getListModel().addToSelection(o);
	}

	@Override
	public void removeFromSelection(Object o) {
		getListModel().removeFromSelection(o);
	}

	@Override
	public void resetSelection() {
		getListModel().resetSelection();
	}

	@Override
	public Object getSelectedObject() {
		return getListModel().getSelectedObject();
	}

	@Override
	public Vector<Object> getSelection() {
		return getListModel().getSelection();
	}

	@Override
	public boolean synchronizedWithSelection() {
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable() {
		return getController().getLastFocusedSelectable() == this;
	}

	private FIBListCellRenderer listCellRenderer;

	@Override
	public FIBListCellRenderer getListCellRenderer() {
		if (listCellRenderer == null) {
			listCellRenderer = new FIBListCellRenderer();
		}
		return listCellRenderer;
	}

	protected class FIBListCellRenderer extends FIBMultipleValueCellRenderer {
		public FIBListCellRenderer() {
			// Dimension s = getJComponent().getSize();
			// setPreferredSize(new Dimension(100,getWidget().getRowHeight()));
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			FIBListCellRenderer label = (FIBListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			// ((JComponent)label).setPreferredSize(new Dimension(label.getWidth(),getWidget().getRowHeight()));

			if (isSelected) {
				if (isLastFocusedSelectable()) {
					if (getWidget().getTextSelectionColor() != null) {
						setForeground(getWidget().getTextSelectionColor());
					}
					if (getWidget().getBackgroundSelectionColor() != null) {
						setBackground(getWidget().getBackgroundSelectionColor());
					}
				} else {
					if (getWidget().getTextNonSelectionColor() != null) {
						setForeground(getWidget().getTextNonSelectionColor());
					}
					if (getWidget().getBackgroundSecondarySelectionColor() != null) {
						setBackground(getWidget().getBackgroundSecondarySelectionColor());
					}
				}
			} else {
				if (getWidget().getTextNonSelectionColor() != null) {
					setForeground(getWidget().getTextNonSelectionColor());
				}
				if (getWidget().getBackgroundNonSelectionColor() != null) {
					setBackground(getWidget().getBackgroundNonSelectionColor());
				}
			}

			return label;
		}
	}

}
