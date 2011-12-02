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
package org.openflexo.fib.view.widget.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckBoxColumn;
import org.openflexo.fib.model.FIBCustomColumn;
import org.openflexo.fib.model.FIBDropDownColumn;
import org.openflexo.fib.model.FIBIconColumn;
import org.openflexo.fib.model.FIBLabelColumn;
import org.openflexo.fib.model.FIBNumberColumn;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTableColumn;
import org.openflexo.fib.model.FIBTextFieldColumn;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FIBTableModel extends DefaultTableModel implements ListSelectionListener {

	private static final Logger logger = Logger.getLogger(FIBTableModel.class.getPackage().getName());

	private List _values;
	private Vector<AbstractColumn> _columns;
	private final FIBTableWidgetFooter _footer;
	private FIBTable _fibTable;
	private FIBTableWidget _widget;
	private Object selectedObject;
	private Vector<Object> selection;

	private final Hashtable<Object, RowObjectModificationTracker> _rowObjectModificationTrackers;

	/**
	 * Stores controls: key is the JButton and value the PropertyListActionListener
	 */
	// private Hashtable<JButton,PropertyListActionListener> _controls;

	public FIBTableModel(FIBTable fibTable, FIBTableWidget widget, FIBController controller) {
		super();
		_fibTable = fibTable;
		_widget = widget;
		_values = null;
		_columns = new Vector<AbstractColumn>();
		for (FIBTableColumn column : fibTable.getColumns()) {
			addToColumns(buildTableColumn(column, controller));
		}

		_rowObjectModificationTrackers = new Hashtable<Object, RowObjectModificationTracker>();

		_footer = new FIBTableWidgetFooter(fibTable, this, widget);

	}

	public FIBTable getTable() {
		return _fibTable;
	}

	public void delete() {
		if (_values != null) {
			for (Object o : _values) {
				// logger.info("REMOVE "+o);
				if (o instanceof HasPropertyChangeSupport) {
					PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o).getPropertyChangeSupport();
					// logger.info("Widget "+getWidget()+" remove property change listener: "+o);
					pcSupport.removePropertyChangeListener(getTracker(o));
					deleteTracker(o);
				} else if (o instanceof Observable) {
					// logger.info("Widget "+getWidget()+" remove observable: "+o);
					((Observable) o).deleteObserver(getTracker(o));
					deleteTracker(o);
				}
			}
		}

		for (AbstractColumn c : _columns) {
			c.delete();
		}

		_footer.delete();

		_columns.clear();
		if (_values != null) {
			_values.clear();
		}

		_columns = null;
		_values = null;
		_widget = null;
		_fibTable = null;
	}

	public FIBTableWidget getWidget() {
		return _widget;
	}

	public List getValues() {
		return _values;
	}

	private static final Vector EMPTY_VECTOR = new Vector();

	public void setValues(List values) {
		// logger.info("setValues with "+values);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setValues() with " + values);
		}
		List newValues = values;
		if (values == null) {
			newValues = EMPTY_VECTOR;
		}

		List oldValues = _values;
		_values = new ArrayList();
		ArrayList removedValues = new ArrayList();
		ArrayList addedValues = new ArrayList();
		if (oldValues != null) {
			removedValues.addAll(oldValues);
		}

		for (Object v : newValues) {
			if (oldValues != null && oldValues.contains(v)) {
				removedValues.remove(v);
			} else {
				addedValues.add(v);
			}
			_values.add(v);
		}

		for (Object o : addedValues) {
			logger.fine("ADD " + o);
			if (o instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o).getPropertyChangeSupport();
				logger.fine("Widget " + getWidget() + " remove property change listener: " + o);
				pcSupport.addPropertyChangeListener(getTracker(o));
			} else if (o instanceof Observable) {
				logger.fine("Widget " + getWidget() + " remove observable: " + o);
				((Observable) o).addObserver(getTracker(o));
			}
		}

		for (Object o : removedValues) {
			logger.fine("REMOVE " + o);
			if (o instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o).getPropertyChangeSupport();
				logger.fine("Widget " + getWidget() + " remove property change listener: " + o);
				pcSupport.removePropertyChangeListener(getTracker(o));
				deleteTracker(o);
			} else if (o instanceof Observable) {
				logger.fine("Widget " + getWidget() + " remove observable: " + o);
				((Observable) o).deleteObserver(getTracker(o));
				deleteTracker(o);
			}

		}

		fireModelObjectHasChanged(oldValues, newValues);
		fireTableDataChanged();
	}

	private void deleteTracker(Object o) {
		_rowObjectModificationTrackers.remove(o);
	}

	private RowObjectModificationTracker getTracker(Object o) {
		RowObjectModificationTracker returned = _rowObjectModificationTrackers.get(o);
		if (returned == null) {
			returned = new RowObjectModificationTracker(o);
			_rowObjectModificationTrackers.put(o, returned);
		}
		return returned;
	}

	protected class RowObjectModificationTracker implements Observer, PropertyChangeListener {
		private final Object rowObject;

		public RowObjectModificationTracker(Object rowObject) {
			this.rowObject = rowObject;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// System.out.println("Row object "+evt.getSource()+" : propertyChange "+evt);
			updateRow();
		}

		@Override
		public void update(Observable o, Object arg) {
			updateRow();
		}

		private void updateRow() {
			fireTableRowsUpdated(indexOf(rowObject), indexOf(rowObject));
			getTableWidget().notifyDynamicModelChanged();
		}
	}

	/**
	 * Notifies all listeners that represented model has changed
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	public void fireModelObjectHasChanged(List oldValues, List newValues) {
		fireTableChanged(new ModelObjectHasChanged(this, oldValues, newValues));
	}

	public class ModelObjectHasChanged extends TableModelEvent {
		private final List _oldValues;

		private final List _newValues;

		public ModelObjectHasChanged(TableModel source, List oldValues, List newValues) {
			super(source);
			_oldValues = oldValues;
			_newValues = newValues;
		}

		public List getNewValues() {
			return _newValues;
		}

		public List getOldValues() {
			return _oldValues;
		}
	}

	@Override
	public int getRowCount() {
		if (getValues() != null) {
			return getValues().size();
		}
		return 0;
	}

	public Object elementAt(int row) {
		if ((getValues() != null) && (row >= 0) && (row < getValues().size())) {
			return getValues().get(row);
		}
		return null;
	}

	public int indexOf(Object object) {
		for (int i = 0; i < getRowCount(); i++) {
			if (elementAt(i) == object) {
				return i;
			}
		}
		return -1;
	}

	public void addToColumns(AbstractColumn aColumn) {
		_columns.add(aColumn);
		aColumn.setTableModel(this);
	}

	public void removeFromColumns(AbstractColumn aColumn) {
		_columns.remove(aColumn);
		aColumn.setTableModel(null);
	}

	public AbstractColumn columnAt(int index) {
		AbstractColumn returned = _columns.elementAt(index);
		return returned;
	}

	@Override
	public int getColumnCount() {
		return _columns.size();
	}

	@Override
	public String getColumnName(int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			return column.getLocalizedTitle();
		}
		return "???";
	}

	public int getDefaultColumnSize(int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			return column.getDefaultWidth();
		}
		return 75;
	}

	public boolean getColumnResizable(int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			return column.getResizable();
		}
		return true;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			return column.getValueClass();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			Object object = elementAt(row);
			return column.isCellEditableFor(object);
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			Object object = elementAt(row);
			return column.getValueFor(object);
		}
		return null;

	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		AbstractColumn column = columnAt(col);
		if ((column != null) && (column instanceof EditableColumn)) {
			Object object = elementAt(row);
			((EditableColumn) column).setValueFor(object, value);
			fireCellUpdated(object, row, col);
		}
	}

	public void fireCellUpdated(Object editedObject, int row, int column) {
		// fireTableChanged(new TableModelEvent(this, row, row, column));
		int newRow = indexOf(editedObject);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("editedObject=" + editedObject);
			logger.fine("row was " + row);
			logger.fine("new row is " + newRow);
		}
		if (row != newRow) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("row changed !");
			}
			fireTableChanged(new RowMoveForObjectEvent(this, editedObject, newRow, column));
		}
	}

	public class RowMoveForObjectEvent extends TableModelEvent {
		private final Object _editedObject;
		private final int _newRow;
		private final int _column;

		public RowMoveForObjectEvent(TableModel source, Object editedObject, int newRow, int column) {
			super(source);
			_editedObject = editedObject;
			_column = column;
			_newRow = newRow;
		}

		public Object getEditedObject() {
			return _editedObject;
		}

		@Override
		public int getColumn() {
			return _column;
		}

		public int getNewRow() {
			return _newRow;
		}
	}

	/*   protected class PropertyListCellRenderer extends DefaultTableCellRenderer
	{
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        AbstractColumn col = get
	        if (returned instanceof JComponent)
	        	((JComponent)returned).setToolTipText(getLocalizedTooltip(getModel().elementAt(row)));
	        return returned;
	    }
	}
	 */
	/* protected void addToActions(PropertyListAction plAction)
	{
	    PropertyListActionListener plActionListener = new PropertyListActionListener(plAction, this);
	    JButton newButton = new JButton();
	    newButton.setText(FlexoLocalization.localizedForKey(plAction.name, newButton));
	    if (plAction.help!=null)
	    	newButton.setToolTipText(FlexoLocalization.localizedForKey(plAction.help, newButton));
	    newButton.addActionListener(plActionListener);
	    getControlPanel().add(newButton);
	    _controls.put(newButton, plActionListener);
	    updateControls(null);
	}

	public Enumeration<PropertyListActionListener> getActionListeners() {
		return _controls.elements();
	}*/

	public FIBTableWidgetFooter getFooter() {
		return _footer;

		/*  if (controlPanel == null) {
		    controlPanel = new JPanel() {
		         @Override
		        public void remove(int index)
		        {
		            super.remove(index);
		        }
		    };
		    controlPanel.setLayout(new FlowLayout());
		    controlPanel.setOpaque(false);
		}
		return controlPanel;*/
	}

	public void setModel(Object model) {
		_footer.setModel(model);

		/* for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		    PropertyListActionListener actionListener = (PropertyListActionListener) en.nextElement();
			actionListener.setModel(model);
		}
		updateControls(null);*/
	}

	public FIBTableColumn getPropertyListColumnWithTitle(String title) {
		return _fibTable.getColumnWithTitle(title);
	}

	@Override
	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fireTableRowsUpdated firstRow=" + firstRow + " lastRow=" + lastRow);
		}
		if (firstRow > -1 && lastRow > -1) {
			super.fireTableRowsUpdated(firstRow, lastRow);
		}
	}

	protected FIBTableWidget getTableWidget() {
		return _widget;
	}

	private AbstractColumn buildTableColumn(FIBTableColumn column, FIBController controller) {
		if (column instanceof FIBLabelColumn) {
			return new LabelColumn((FIBLabelColumn) column, this, controller);
		} else if (column instanceof FIBTextFieldColumn) {
			return new TextFieldColumn((FIBTextFieldColumn) column, this, controller);
		} else if (column instanceof FIBCheckBoxColumn) {
			return new CheckBoxColumn((FIBCheckBoxColumn) column, this, controller);
		} else if (column instanceof FIBDropDownColumn) {
			return new DropDownColumn((FIBDropDownColumn) column, this, controller);
		} else if (column instanceof FIBIconColumn) {
			return new IconColumn((FIBIconColumn) column, this, controller);
		} else if (column instanceof FIBNumberColumn) {
			return new NumberColumn((FIBNumberColumn) column, this, controller);
		} else if (column instanceof FIBCustomColumn) {
			return new CustomColumn((FIBCustomColumn) column, this, controller);
		}
		return null;

	}

	public Object getSelectedObject() {
		return selectedObject;
	}

	public Vector<Object> getSelection() {
		return selection;
	}

	public ListSelectionModel getListSelectionModel() {
		return _widget.getListSelectionModel();
	}

	/*public boolean isFocused()
	{
		return _widget.isFocused();
	}*/

	@Override
	public void valueChanged(ListSelectionEvent e) {

		// Ignore extra messages.
		if (e.getValueIsAdjusting()) {
			return;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("valueChanged() selected index=" + getListSelectionModel().getMinSelectionIndex());
		}

		int i = getListSelectionModel().getMinSelectionIndex();
		int leadIndex = getListSelectionModel().getLeadSelectionIndex();
		if (!getListSelectionModel().isSelectedIndex(leadIndex)) {
			leadIndex = getListSelectionModel().getAnchorSelectionIndex();
		}
		while (!getListSelectionModel().isSelectedIndex(leadIndex) && i <= getListSelectionModel().getMaxSelectionIndex()) {
			leadIndex = i;
			i++;
		}

		selectedObject = elementAt(leadIndex);

		Vector<Object> oldSelection = selection;
		selection = new Vector<Object>();
		for (i = getListSelectionModel().getMinSelectionIndex(); i <= getListSelectionModel().getMaxSelectionIndex(); i++) {
			if (getListSelectionModel().isSelectedIndex(i)) {
				selection.add(elementAt(i));
			}
		}

		_widget.getDynamicModel().selected = selectedObject;
		_widget.getDynamicModel().selection = selection;
		_widget.notifyDynamicModelChanged();

		if (_widget.getComponent().getSelected().isValid()) {
			logger.fine("Sets SELECTED binding with " + selectedObject);
			_widget.getComponent().getSelected().setBindingValue(selectedObject, _widget.getController());
		}

		_widget.updateFont();

		if (!ignoreNotifications) {
			_widget.getController().updateSelection(_widget, oldSelection, selection);
		}

		_footer.handleSelectionChanged();

		/*SwingUtilities.invokeLater(new Runnable() {
			
			public void run()
			{
				System.out.println((isFocused() ? "LEADER" : "SECONDARY")+" Le grand vainqueur est "+selectedObject);
				System.out.println((isFocused() ? "LEADER" : "SECONDARY")+" La selection est "+selection);
			}
		});*/

	}

	private boolean ignoreNotifications = false;

	public void addToSelectionNoNotification(Object o) {
		int index = getWidget().getValue().indexOf(o);
		ignoreNotifications = true;
		getListSelectionModel().addSelectionInterval(index, index);
		ignoreNotifications = false;
	}

	public void removeFromSelectionNoNotification(Object o) {
		int index = getWidget().getValue().indexOf(o);
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
		int index = getWidget().getValue().indexOf(o);
		getListSelectionModel().addSelectionInterval(index, index);
	}

	public void removeFromSelection(Object o) {
		int index = getWidget().getValue().indexOf(o);
		getListSelectionModel().removeSelectionInterval(index, index);
	}

	public void resetSelection() {
		getListSelectionModel().clearSelection();
	}

}
