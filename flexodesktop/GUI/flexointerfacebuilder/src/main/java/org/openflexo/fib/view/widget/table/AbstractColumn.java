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

import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBTableColumn;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;

public abstract class AbstractColumn<T> implements BindingEvaluationContext, Observer {

	private static final Logger logger = Logger.getLogger(AbstractColumn.class.getPackage().getName());

	private String title;
	private int defaultWidth;
	private boolean isResizable;
	private boolean displayTitle;

	// private FIBTableModel fibTableModel;
	private FIBTableColumn columnModel;

	private String tooltipKey;

	private FIBTableCellRenderer<T> _defaultTableCellRenderer;

	private FIBController controller;

	private FIBTableModel tableModel;

	private DynamicFormatter formatter;

	public AbstractColumn(FIBTableColumn columnModel, FIBTableModel tableModel, FIBController controller) {
		super();
		this.controller = controller;
		this.tableModel = tableModel;
		this.columnModel = columnModel;
		formatter = new DynamicFormatter();
		title = columnModel.getTitle();
		defaultWidth = columnModel.getColumnWidth();
		isResizable = columnModel.getResizable();
		displayTitle = columnModel.getDisplayTitle();

		columnModel.addObserver(this);
	}

	public void delete() {
		columnModel.deleteObserver(this);

		this.controller = null;
		this.columnModel = null;
		title = null;
	}

	public FIBTableWidget getTableWidget() {
		return getTableModel().getTableWidget();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FIBAttributeNotification && o == columnModel) {
			FIBAttributeNotification dataModification = (FIBAttributeNotification) arg;
			if (dataModification.getAttribute() == FIBTableColumn.Parameters.columnWidth
					|| dataModification.getAttribute() == FIBTableColumn.Parameters.data
					|| dataModification.getAttribute() == FIBTableColumn.Parameters.displayTitle
					|| dataModification.getAttribute() == FIBTableColumn.Parameters.font
					|| dataModification.getAttribute() == FIBTableColumn.Parameters.resizable
					|| dataModification.getAttribute() == FIBTableColumn.Parameters.title) {
				((FIBTableWidget) controller.viewForComponent(columnModel.getTable())).updateTable();
			}
		}
	}

	public FIBController getController() {
		return controller;
	}

	public String getLocalized(String key) {
		if (getController() != null) {
			return FlexoLocalization.localizedForKey(getController().getLocalizer(), key);
		} else {
			logger.warning("Controller not defined");
			return key;
		}
	}

	public FIBTableModel getTableModel() {
		return tableModel;
	}

	protected void setTableModel(FIBTableModel model) {
		tableModel = model;
	}

	public Object elementAt(int row) {
		return tableModel.elementAt(row);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocalizedTitle() {
		if ((title == null) || (!displayTitle)) {
			return " ";
		}
		return getLocalized(getTitle());
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public boolean getResizable() {
		return isResizable;
	}

	public void setDefaultWidth(int width) {
		defaultWidth = width;
	}

	public boolean isCellEditableFor(Object object) {
		return false;
	}

	public abstract Class<T> getValueClass();

	public synchronized T getValueFor(final Object object) {
		iteratorObject = object;
		/*
		 * System.out.println("column: "+columnModel);
		 * System.out.println("binding: "
		 * +columnModel.getData()+" valid: "+columnModel.getData().isValid());
		 * System.out.println("iterator: "+iteratorObject);
		 * System.out.println("return: "
		 * +columnModel.getData().getBindingValue(this));
		 */
		return (T) columnModel.getData().getBindingValue(this);
		// return (T)FIBKeyValueCoder.getObjectValue(object, keyValuePath);
	}

	public synchronized void setValueFor(final Object object, T value) {
		iteratorObject = object;
		columnModel.getData().setBindingValue(value, this);
		// FIBKeyValueCoder.setObjectValue(object, value, keyValuePath);
		notifyValueChangedFor(object, value);
	}

	protected Object iteratorObject;

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("iterator")) {
			return iteratorObject;
		} else {
			return getController().getValue(variable);
		}
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellRenderer() {
		return false;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellRenderer getCellRenderer() {
		return getDefaultTableCellRenderer();
	}

	/**
	 * @return
	 */
	protected TableCellRenderer getDefaultTableCellRenderer() {
		if (_defaultTableCellRenderer == null) {
			_defaultTableCellRenderer = new FIBTableCellRenderer<T>(this);
		}
		return _defaultTableCellRenderer;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellEditor() {
		return false;
	}

	public String getTooltip(Object object) {
		if (columnModel.getTooltip().isSet() && columnModel.getTooltip().isValid()) {
			iteratorObject = object;
			return (String) columnModel.getTooltip().getBindingValue(this);
		}
		return null;
	}

	public Color getSpecificColor(Object object) {
		if (columnModel.getColor().isSet() && columnModel.getColor().isValid()) {
			iteratorObject = object;
			return (Color) columnModel.getColor().getBindingValue(this);
		}
		return null;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellEditor getCellEditor() {
		return null;
	}

	public FIBTableColumn getPropertyListColumn() {
		return getTableModel().getPropertyListColumnWithTitle(title);
	}

	public void notifyValueChangedFor(Object object, T value) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyValueChangedFor " + object);
		}
		// Following will force the whole row where object was modified to be
		// updated
		// (In case of some computed cells are to be updated according to ths
		// new value)
		getTableModel().fireTableRowsUpdated(getTableModel().indexOf(object), getTableModel().indexOf(object));
		getTableModel().getTableWidget().notifyDynamicModelChanged();

		if (getColumnModel().getValueChangedAction().isValid()) {
			getColumnModel().getValueChangedAction().execute(getController());
		}

	}

	public void setTooltipKey(String tooltipKey) {
		this.tooltipKey = tooltipKey;
	}

	public FIBTableColumn getColumnModel() {
		return columnModel;
	}

	public Font getFont() {
		return getColumnModel().retrieveValidFont();
	}

	public String getStringRepresentation(final Object value) {
		if (value == null) {
			return "";
		}
		if (getColumnModel().getFormat().isValid()) {
			formatter.setValue(value);
			return (String) getColumnModel().getFormat().getBindingValue(formatter);
		}
		return value.toString();
	}

	protected class DynamicFormatter implements BindingEvaluationContext {
		private Object value;

		private void setValue(Object aValue) {
			value = aValue;
		}

		@Override
		public Object getValue(BindingVariable variable) {
			return value;
		}
	}

}
