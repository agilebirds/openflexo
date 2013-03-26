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
package org.openflexo.components.tabular.model;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Represents a TableModel used by a TabularView
 * 
 * @author sguerin
 * 
 */
public abstract class AbstractModel<M extends FlexoObject, D extends FlexoObject> extends DefaultTableModel implements DataFlexoObserver {

	private static final Logger logger = Logger.getLogger(AbstractModel.class.getPackage().getName());

	private M _model;

	private Vector<AbstractColumn<D, ?>> _columns;

	private int _rowHeight = -1;

	private FlexoProject _project;

	private Vector<D> _observedObjects;

	public AbstractModel(M model, FlexoProject project) {
		super();
		_project = project;
		_model = model;
		_columns = new Vector<AbstractColumn<D, ?>>();
		_observedObjects = new Vector<D>();
	}

	public M getModel() {
		return _model;
	}

	public void setModel(M model) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Set model for " + getClass().getName() + " with " + model);
		}
		M oldModel = _model;
		_model = model;
		fireModelObjectHasChanged(oldModel, model);
		fireTableDataChanged();
	}

	/**
	 * Notifies all listeners that represented model has changed
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	public void fireModelObjectHasChanged(M oldModel, M newModel) {
		fireTableChanged(new ModelObjectHasChanged(this, oldModel, newModel));
	}

	/**
	 * Notifies all listeners that supplied object must be selected
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	public void selectObject(D selectedObject) {
		fireTableChanged(new SelectObjectEvent(this, selectedObject));
	}

	public class TableStructureHasChanged extends TableModelEvent {
		public TableStructureHasChanged(TableModel source) {
			super(source);
		}
	}

	public class ModelObjectHasChanged extends TableModelEvent {
		private M _oldModel;

		private M _newModel;

		public ModelObjectHasChanged(TableModel source, M oldModel, M newModel) {
			super(source);
			_oldModel = oldModel;
			_newModel = newModel;
		}

		public M getNewModel() {
			return _newModel;
		}

		public M getOldModel() {
			return _oldModel;
		}
	}

	public class SelectObjectEvent extends TableModelEvent {
		private D _selectedObject;

		public SelectObjectEvent(TableModel source, D selectedObject) {
			super(source);
			_selectedObject = selectedObject;
		}

		public D getSelectedObject() {
			return _selectedObject;
		}
	}

	public class RowMoveForObjectEvent extends TableModelEvent {
		private D _editedObject;
		private int _newRow;
		private int _column;

		public RowMoveForObjectEvent(TableModel source, D editedObject, int newRow, int column) {
			super(source);
			_editedObject = editedObject;
			_column = column;
			_newRow = newRow;
		}

		public D getEditedObject() {
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

	@Override
	public void fireTableStructureChanged() {
		// logger.info("fireTableStructureChanged()");
		super.fireTableStructureChanged();
		fireTableChanged(new TableStructureHasChanged(this));
	}

	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		// logger.info("fireTableRowsDeleted("+firstRow+","+lastRow+")");
		super.fireTableRowsDeleted(firstRow, lastRow);
	}

	/**
	 * Notifies all listeners that represented model has changed
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	@Override
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fireTableDataChanged() in " + getClass().getName() + " " + hashCode());
		}
		updateObservedObjects();
	}

	private void updateObservedObjects() {
		for (FlexoObject observed : new ArrayList<FlexoObject>(_observedObjects)) {
			observed.deleteObserver(this);
		}
		_observedObjects.clear();
		for (int i = 0; i < getRowCount(); i++) {
			D observed = elementAt(i);
			_observedObjects.add(observed);
			observed.addObserver(this);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable instanceof FlexoObject) {
			int row = indexOf((D) observable);
			fireTableRowsUpdated(row, row);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update row " + row + " for object " + observable);
			}
		}
	}

	@Override
	public abstract int getRowCount();

	public int getRowHeight() {
		return _rowHeight;
	}

	public void setRowHeight(int aRowHeight) {
		_rowHeight = aRowHeight;
	}

	public abstract D elementAt(int row);

	public int indexOf(D object) {
		// logger.info("Search index of="+object+" model="+getModel()+" "+getClass().getName());
		for (int i = 0; i < getRowCount(); i++) {
			D obj = elementAt(i);
			// logger.info("Examine object="+obj);
			if (obj == object) {
				return i;
			}
		}
		return -1;
	}

	public void addToColumns(AbstractColumn<D, ?> aColumn) {
		_columns.add(aColumn);
		aColumn.setModel(this);
	}

	public void insertColumnAtIndex(AbstractColumn<D, ?> aColumn, int index) {
		_columns.insertElementAt(aColumn, index);
		aColumn.setModel(this);
	}

	public void replaceColumnByColumn(AbstractColumn<D, ?> oldColumn, AbstractColumn<D, ?> newColumn) {
		int index = _columns.indexOf(oldColumn);
		if (index < 0) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to replaced a inexisting column. Not going further.");
			}
			return;
		}
		_columns.remove(index);
		_columns.insertElementAt(newColumn, index);
	}

	public void removeFromColumns(AbstractColumn<D, ?> aColumn) {
		_columns.remove(aColumn);
		aColumn.setModel(null);
	}

	public AbstractColumn<D, ?> columnAt(int index) {
		return _columns.elementAt(index);
	}

	public int getTotalPreferredWidth() {
		int returned = 0;
		for (int i = 0; i < getColumnCount(); i++) {
			returned += getDefaultColumnSize(i);
		}
		return returned;
	}

	@Override
	public int getColumnCount() {
		return _columns.size();
	}

	@Override
	public String getColumnName(int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			return column.getLocalizedTitle();
		}
		return "???";
	}

	public int getIndexForColumnWithName(String colName) {
		if (colName == null) {
			return -1;
		}
		for (int i = 0; i < getColumnCount(); i++) {
			if (colName.equals(getColumnName(i))) {
				return i;
			}
		}
		return -1;
	}

	public String getColumnTooltip(int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			return column.getLocalizedTooltip();
		}
		return "-";
	}

	public int getDefaultColumnSize(int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			return column.getDefaultWidth();
		}
		return 75;
	}

	public boolean getColumnResizable(int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			return column.getResizable();
		}
		return true;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			return column.getValueClass();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			D object = elementAt(row);
			return column.isCellEditableFor(object);
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			D object = elementAt(row);
			return column.getValueFor(object);
		}
		return null;

	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null && column instanceof EditableColumn) {
			D object = elementAt(row);
			((EditableColumn) column).setValueFor(object, value);
			fireCellUpdated(object, row, col);
		}
	}

	/**
	 * Notifies all listeners that the value of the cell at <code>[row, column]</code> has been updated.
	 * 
	 * @param editedObject
	 *            object that was updated
	 * @param row
	 *            row of cell which has been updated
	 * @param column
	 *            column of cell which has been updated
	 * @see TableModelEvent
	 * @see EventListenerList
	 */
	public void fireCellUpdated(D editedObject, int row, int column) {
		// fireTableChanged(new TableModelEvent(this, row, row, column));
		int newRow = indexOf(editedObject);
		if (row != newRow) {
			fireTableChanged(new RowMoveForObjectEvent(this, editedObject, newRow, column));
		}
	}

	protected class DMCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			return returned;
		}
	}

	public FlexoProject getProject() {
		return _project;
	}

}
