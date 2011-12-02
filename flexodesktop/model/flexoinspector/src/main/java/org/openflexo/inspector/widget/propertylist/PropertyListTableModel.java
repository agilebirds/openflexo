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

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.model.PropertyListModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class PropertyListTableModel extends DefaultTableModel {

	private static final Logger logger = Logger.getLogger(PropertyListTableModel.class.getPackage().getName());

	private List _values;

	private Vector _columns;

	// private JPanel controlPanel;

	private PropertyListWidgetFooter _footer;

	private PropertyListModel _propertyListModel;

	private PropertyListWidget _widget;

	/**
	 * Stores controls: key is the JButton and value the PropertyListActionListener
	 */
	// private Hashtable<JButton,PropertyListActionListener> _controls;

	public PropertyListTableModel(PropertyListModel propertyListModel, PropertyListWidget widget, AbstractController controller) {
		super();
		_propertyListModel = propertyListModel;
		_widget = widget;
		_values = null;
		_columns = new Vector();
		for (Enumeration en = propertyListModel.getColumns().elements(); en.hasMoreElements();) {
			PropertyListColumn plColumn = (PropertyListColumn) en.nextElement();
			addToColumns(plColumn.getTableColumn(controller));
		}
		_footer = new PropertyListWidgetFooter(propertyListModel, this, widget);
		/*
		controlPanel = null;
		_controls = new Hashtable<JButton, PropertyListActionListener>();
		for (Enumeration en = propertyListModel.getActions().elements(); en.hasMoreElements();) {
		    PropertyListAction plAction = (PropertyListAction) en.nextElement();
		    addToActions(plAction);
		}*/
	}

	public List getValues() {
		return _values;
	}

	private static final Vector EMPTY_VECTOR = new Vector();

	public void setValues(List values) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setValues() with " + values);
		}
		List newValues = values;
		if (values == null) {
			newValues = EMPTY_VECTOR;
		}

		List oldValues = _values;
		_values = newValues;
		fireModelObjectHasChanged(oldValues, newValues);
		fireTableDataChanged();
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
		private List _oldValues;

		private List _newValues;

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

	public InspectableObject elementAt(int row) {
		if ((getValues() != null) && (row >= 0) && (row < getValues().size())) {
			Object returned = getValues().get(row);
			if (returned instanceof InspectableObject) {
				// logger.info("element at "+row+" is "+returned);
				return (InspectableObject) returned;
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Found a non-inspectable object in a inspectable property-list");
				}
			}
		}
		return null;
	}

	public int indexOf(InspectableObject object) {
		for (int i = 0; i < getRowCount(); i++) {
			InspectableObject obj = elementAt(i);
			if (obj == object) {
				return i;
			}
		}
		return -1;
	}

	public void addToColumns(AbstractColumn aColumn) {
		_columns.add(aColumn);
		aColumn.setModel(this);
	}

	public void removeFromColumns(AbstractColumn aColumn) {
		_columns.remove(aColumn);
		aColumn.setModel(null);
	}

	public AbstractColumn columnAt(int index) {
		AbstractColumn returned = (AbstractColumn) _columns.elementAt(index);
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
	public Class getColumnClass(int col) {
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
			InspectableObject object = elementAt(row);
			return column.isCellEditableFor(object);
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AbstractColumn column = columnAt(col);
		if (column != null) {
			InspectableObject object = elementAt(row);
			return column.getValueFor(object);
		}
		return null;

	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		AbstractColumn column = columnAt(col);
		if ((column != null) && (column instanceof EditableColumn)) {
			InspectableObject object = elementAt(row);
			((EditableColumn) column).setValueFor(object, value);
			fireCellUpdated(object, row, col);
		}
	}

	public void fireCellUpdated(InspectableObject editedObject, int row, int column) {
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
		private InspectableObject _editedObject;
		private int _newRow;
		private int _column;

		public RowMoveForObjectEvent(TableModel source, InspectableObject editedObject, int newRow, int column) {
			super(source);
			_editedObject = editedObject;
			_column = column;
			_newRow = newRow;
		}

		public InspectableObject getEditedObject() {
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

	protected PropertyListWidgetFooter getFooter() {
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

	private InspectableObject _selectedObject;

	private Vector<InspectableObject> selectedObjects;

	protected InspectableObject getSelectedObject() {
		return _selectedObject;
	}

	protected void setSelectedObject(InspectableObject selectedObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setSelectedObject with " + selectedObject);
		}
		_selectedObject = selectedObject;
		_footer.handleSelectionChanged();

		/*for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		    PropertyListActionListener actionListener = (PropertyListActionListener) en.nextElement();
		    actionListener.setSelectedObject(selectedObject);
		}
		updateControls(selectedObject);*/
	}

	protected Vector<InspectableObject> getSelectedObjects() {
		return selectedObjects;
	}

	protected void setSelectedObjects(Vector<InspectableObject> selectedObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setSelectedObjects with " + selectedObject);
		}
		this.selectedObjects = selectedObject;
		_footer.handleSelectionChanged();

		/*for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		    PropertyListActionListener actionListener = (PropertyListActionListener) en.nextElement();
		    actionListener.setSelectedObjects(selectedObjects);
		}*/
	}

	protected void setSelectedIndex(int selectedIndex) {
		InspectableObject selectedObject = elementAt(selectedIndex);
		setSelectedObject(selectedObject);
	}

	protected void setModel(InspectableObject model) {
		_footer.setModel(model);

		/* for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		     PropertyListActionListener actionListener = (PropertyListActionListener) en.nextElement();
		 	actionListener.setModel(model);
		 }
		 updateControls(null);*/
	}

	public PropertyListColumn getPropertyListColumnWithTitle(String title) {
		return _propertyListModel.getPropertyListColumnWithTitle(title);
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

	protected PropertyListWidget getPropertyListWidget() {
		return _widget;
	}

}
