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
package org.openflexo.inspector.widget.propertylist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.WidgetFocusListener;
import org.openflexo.localization.FlexoLocalization;

/**
 * Widget allowing to display/edit a list of values
 * 
 * @author sguerin
 */
public class PropertyListWidget extends DenaliWidget implements TableModelListener, ListSelectionListener {

	private static final Logger logger = Logger.getLogger(PropertyListWidget.class.getPackage().getName());

	private JTable _table;

	private JPanel _dynamicComponent;

	private PropertyListModel _propertyListModel;

	private PropertyListTableModel _tableModel;

	private ListSelectionModel _listSelectionModel;

	private JScrollPane scrollPane;

	public PropertyListWidget(PropertyListModel propertyListModel, AbstractController controller) {
		super(propertyListModel, controller);
		_propertyListModel = propertyListModel;

		getTableModel().addTableModelListener(this);

		_table = new JTable(getTableModel());
		_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_table.addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				super.focusGained(arg0);
			}
		});

		for (int i = 0; i < getTableModel().getColumnCount(); i++) {
			TableColumn col = _table.getColumnModel().getColumn(i);
			FlexoLocalization.localizedForKey(getTableModel().columnAt(i).getTitle(), col);
			col.setWidth(getTableModel().getDefaultColumnSize(i));
			col.setPreferredWidth(getTableModel().getDefaultColumnSize(i));
			if (getTableModel().getColumnResizable(i)) {
				col.setResizable(true);
			} else {
				// L'idee, c'est d'etre vraiment sur ;-) !
				col.setWidth(getTableModel().getDefaultColumnSize(i));
				col.setMinWidth(getTableModel().getDefaultColumnSize(i));
				col.setMaxWidth(getTableModel().getDefaultColumnSize(i));
				col.setResizable(false);
			}
			if (getTableModel().columnAt(i).requireCellRenderer()) {
				col.setCellRenderer(getTableModel().columnAt(i).getCellRenderer());
			}
			if (getTableModel().columnAt(i).requireCellEditor()) {
				col.setCellEditor(getTableModel().columnAt(i).getCellEditor());
			}
		}

		if (propertyListModel.getRowHeight() > 0) {
			_table.setRowHeight(propertyListModel.getRowHeight());
		}

		/*_table.setDefaultRenderer(Language.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JLabel returned = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof Language) returned.setText(((Language)value).getTag());
				return returned;
			}
		});*/

		_table.setShowVerticalLines(true);
		_table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		_table.getTableHeader().setReorderingAllowed(false);
		_listSelectionModel = _table.getSelectionModel();
		_listSelectionModel.addListSelectionListener(this);

		scrollPane = new JScrollPane(_table);
		if (propertyListModel.createNewRowOnClick) {
			scrollPane.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (_table.getCellEditor() != null) {
						_table.getCellEditor().stopCellEditing();
						e.consume();
					}
					if (!e.isConsumed() && e.getClickCount() == 2) {
						Enumeration<PropertyListActionListener> en = getTableModel().getFooter().getAddActionListeners();
						while (en.hasMoreElements()) {
							PropertyListActionListener action = en.nextElement();
							if (action.isAddAction()) {
								action.actionPerformed(null);
								break;
							}
						}
					}
				}
			});
		}
		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);
		_dynamicComponent.add(getTableModel().getFooter(), BorderLayout.SOUTH);
		setVisibleRowCount(propertyListModel.getVisibleRowCount());
		_dynamicComponent.validate();
	}

	public PropertyListTableModel getTableModel() {
		if (_tableModel == null) {
			_tableModel = new PropertyListTableModel(_propertyListModel, this, getController());
		}
		return _tableModel;
	}

	public void setVisibleRowCount(int rows) {
		int height = 0;
		for (int row = 0; row < rows; row++) {
			height += _table.getRowHeight(row);
		}
		height += _table.getTableHeader().getPreferredSize().height;
		int width = 0;
		for (int i = 0; i < getTableModel().getColumnCount(); i++) {
			width += getTableModel().getDefaultColumnSize(i);
		}
		_dynamicComponent.setMinimumSize(new Dimension(width, height));
		_dynamicComponent.setPreferredSize(new Dimension(width, height));
	}

	/*public JLabel getLabel()
	{
	    if (_label == null) {
	        _label = new JLabel(_propertyModel.label + " : ", SwingConstants.CENTER);
	        _label.setText(FlexoLocalization.localizedForKey(_propertyModel.label, _label));
	        _label.setBackground(InspectorCst.BACK_COLOR);
	        _label.setFont(DEFAULT_LABEL_FONT);
	        if (_propertyModel.help != null && !_propertyModel.help.equals(""))
	            _label.setToolTipText(_propertyModel.help);
	    }
	    return _label;
	}*/

	@Override
	public Class getDefaultType() {
		return Vector.class;
	}

	private static final Vector EMPTY_VECTOR = new Vector();

	@Override
	public synchronized void updateWidgetFromModel() {
		if (_table.isEditing()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(getObservedPropertyName() + " - Table is currently editing at col:" + _table.getEditingColumn() + " row:"
						+ _table.getEditingRow());
			}
			_table.getCellEditor().cancelCellEditing();
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(getObservedPropertyName() + " - Table is NOT currently edited ");
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(getObservedPropertyName() + " updateWidgetFromModel() with " + getObjectValue());
		}
		if (getObjectValue() == null) {
			getTableModel().setValues(EMPTY_VECTOR);
		}
		if (getObjectValue() instanceof List) {
			getTableModel().setValues((List) getObjectValue());
		}
		getTableModel().setModel(_propertyListModel.getDerivedModel(getModel()));
	}

	@Override
	public synchronized void updateModelFromWidget() {
	}

	@Override
	public JComponent getDynamicComponent() {
		return _dynamicComponent;
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e instanceof PropertyListTableModel.ModelObjectHasChanged) {
			PropertyListTableModel.ModelObjectHasChanged event = (PropertyListTableModel.ModelObjectHasChanged) e;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Model has changed from " + event.getOldValues() + " to " + event.getNewValues());
			}
		} else if (e instanceof PropertyListTableModel.RowMoveForObjectEvent) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Reselect object, and then the edited cell");
			}
			PropertyListTableModel.RowMoveForObjectEvent event = (PropertyListTableModel.RowMoveForObjectEvent) e;
			_listSelectionModel.removeListSelectionListener(this);
			_listSelectionModel.addSelectionInterval(event.getNewRow(), event.getNewRow());
			_listSelectionModel.addListSelectionListener(this);
			_table.setEditingColumn(event.getColumn());
			_table.setEditingRow(event.getNewRow());
		}
	}

	/**
	 * Overrides
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// Ignore extra messages.
		if (e.getValueIsAdjusting()) {
			return;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("valueChanged() selected index=" + _listSelectionModel.getMinSelectionIndex());
		}

		getTableModel().setSelectedIndex(_listSelectionModel.getMinSelectionIndex());
		Vector<InspectableObject> selectedObjects = new Vector<InspectableObject>();
		for (int i = 0; i < Math.max(_listSelectionModel.getMaxSelectionIndex() + 1, _tableModel.getRowCount()); i++) {
			if (_listSelectionModel.isSelectedIndex(i)) {
				selectedObjects.add(_tableModel.elementAt(i));
			}
		}
		getTableModel().setSelectedObjects(selectedObjects);
		_propertyListModel.setSelectedObject(getTableModel().getSelectedObject());
		notifyInspectedPropertyChanged();
	}

	@Override
	public WidgetLayout getDefaultWidgetLayout() {
		return WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT;
	}

	@Override
	public boolean defaultShouldExpandVertically() {
		return true;
	}

}
