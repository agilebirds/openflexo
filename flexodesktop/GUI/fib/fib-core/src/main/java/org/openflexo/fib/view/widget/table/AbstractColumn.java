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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBTableColumn;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a column in a table
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of row object beeing handled by this column
 * @param <V>
 *            type of value beeing managed by column's cells
 */
public abstract class AbstractColumn<T, V> implements BindingEvaluationContext, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(AbstractColumn.class.getPackage().getName());

	private String title;
	private int defaultWidth;
	private boolean isResizable;
	private boolean displayTitle;

	// private FIBTableModel fibTableModel;
	private FIBTableColumn columnModel;

	private String tooltipKey;

	private FIBTableCellRenderer<T, V> _defaultTableCellRenderer;

	private FIBController controller;

	private FIBTableModel<T> tableModel;

	private DynamicFormatter formatter;

	public AbstractColumn(FIBTableColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super();
		this.controller = controller;
		this.tableModel = tableModel;
		this.columnModel = columnModel;
		formatter = new DynamicFormatter();
		title = columnModel.getTitle();
		defaultWidth = columnModel.getColumnWidth();
		isResizable = columnModel.getResizable();
		displayTitle = columnModel.getDisplayTitle();

		columnModel.getPropertyChangeSupport().addPropertyChangeListener(this);

	}

	public void delete() {
		columnModel.getPropertyChangeSupport().removePropertyChangeListener(this);

		this.controller = null;
		this.columnModel = null;
		title = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == columnModel) {
			if ((evt.getPropertyName().equals(FIBTableColumn.Parameters.columnWidth.name()))
					|| (evt.getPropertyName().equals(FIBTableColumn.Parameters.data.name()))
					|| (evt.getPropertyName().equals(FIBTableColumn.Parameters.displayTitle.name()))
					|| (evt.getPropertyName().equals(FIBTableColumn.Parameters.font.name()))
					|| (evt.getPropertyName().equals(FIBTableColumn.Parameters.resizable.name()))
					|| (evt.getPropertyName().equals(FIBTableColumn.Parameters.title.name()))) {
				if (controller.viewForComponent((FIBComponent) columnModel.getTable()) != null) {
					((FIBTableWidget) controller.viewForComponent((FIBComponent) columnModel.getTable())).updateTable();
				}
			}
		}

	}

	public FIBController getController() {
		return controller;
	}

	public String getLocalized(String key) {
		if (getController() != null) {
			return FlexoLocalization.localizedForKey(getController().getLocalizerForComponent((FIBComponent) getColumnModel().getTable()),
					key);
		} else {
			logger.warning("Controller not defined");
			return key;
		}
	}

	public FIBTableModel<T> getTableModel() {
		return tableModel;
	}

	protected void setTableModel(FIBTableModel<T> model) {
		tableModel = model;
	}

	public T elementAt(int row) {
		return tableModel.elementAt(row);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocalizedTitle() {
		if (title == null || !displayTitle) {
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

	public boolean isCellEditableFor(T object) {
		return false;
	}

	public abstract Class<V> getValueClass();

	@SuppressWarnings("unchecked")
	public synchronized V getValueFor(final T object, BindingEvaluationContext evaluationContext) {
		iteratorObject = object;
		bindingEvaluationContext = evaluationContext;
		/*
		 * System.out.println("column: "+columnModel);
		 * System.out.println("binding: "
		 * +columnModel.getData()+" valid: "+columnModel.getData().isValid());
		 * System.out.println("iterator: "+iteratorObject);
		 * System.out.println("return: "
		 * +columnModel.getData().getBindingValue(this));
		 */
		try {
			return (V) columnModel.getData().getBindingValue(this);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			return null;
		} catch (NullReferenceException e) {
			// logger.warning("Unexpected " + e.getMessage());
			// e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized void setValueFor(final T object, V value, BindingEvaluationContext evaluationContext) {
		iteratorObject = object;
		bindingEvaluationContext = evaluationContext;
		try {
			columnModel.getData().setBindingValue(value, this);
			notifyValueChangedFor(object, value, bindingEvaluationContext);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NotSettableContextException e) {
			e.printStackTrace();
		}
	}

	protected T iteratorObject;

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("iterator")) {
			return iteratorObject;
		} else {
			return getBindingEvaluationContext().getValue(variable);
		}
	}

	private BindingEvaluationContext bindingEvaluationContext;

	public BindingEvaluationContext getBindingEvaluationContext() {
		if (bindingEvaluationContext != null) {
			return bindingEvaluationContext;
		}
		return getController();
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
			_defaultTableCellRenderer = new FIBTableCellRenderer<T, V>(this);
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

	public String getTooltip(T object) {
		if (columnModel.getTooltip().isSet() && columnModel.getTooltip().isValid()) {
			iteratorObject = object;
			try {
				return columnModel.getTooltip().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Color getSpecificColor(T object) {
		if (columnModel.getColor().isSet() && columnModel.getColor().isValid()) {
			iteratorObject = object;
			try {
				return columnModel.getColor().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Color getSpecificBgColor(T object) {
		if (columnModel.getBgColor().isSet() && columnModel.getBgColor().isValid()) {
			iteratorObject = object;
			try {
				return columnModel.getBgColor().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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

	public void notifyValueChangedFor(T object, V value, BindingEvaluationContext evaluationContext) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyValueChangedFor " + object);
		}
		bindingEvaluationContext = evaluationContext;
		// Following will force the whole row where object was modified to be
		// updated
		// (In case of some computed cells are to be updated according to ths
		// new value)
		getTableModel().fireTableRowsUpdated(getTableModel().indexOf(object), getTableModel().indexOf(object));
		// getTableModel().getTableWidget().notifyDynamicModelChanged();

		if (getColumnModel().getValueChangedAction().isValid()) {
			try {
				getColumnModel().getValueChangedAction().execute(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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
			try {
				return getColumnModel().getFormat().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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
			if (variable.getVariableName().equals("object")) {
				return value;
			} else {
				return controller.getValue(variable);
			}
		}

	}

}
