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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.DataBinding;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.model.FIBCustomColumn;
import org.openflexo.fib.model.FIBCustomColumn.FIBCustomAssignment;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class CustomColumn<T extends Object> extends AbstractColumn<T> implements
		EditableColumn<T>, ApplyCancelListener {
	private static final Logger logger = Logger.getLogger(CustomColumn.class
			.getPackage().getName());

	private FIBCustomColumn _customColumn;

	private FIBCustomComponent<T, ?> _viewCustomWidget;

	private FIBCustomComponent<T, ?> _editCustomWidget;

	private boolean useCustomViewForCellRendering;
	private boolean disableTerminateEditOnFocusLost;

	public CustomColumn(FIBCustomColumn columnModel, FIBTableModel tableModel,
			FIBController controller) {
		super(columnModel, tableModel, controller);
		_customColumn = columnModel;
		_viewCustomWidget = makeCustomComponent(
				(Class<FIBCustomComponent<T, ?>>) columnModel
						.getComponentClass(),
				(Class<T>) columnModel.getDataClass());
		_editCustomWidget = makeCustomComponent(
				(Class<FIBCustomComponent<T, ?>>) columnModel
						.getComponentClass(),
				(Class<T>) columnModel.getDataClass());
		if (_editCustomWidget != null) {
			_editCustomWidget.addApplyCancelListener(this);
		}
		_customCellRenderer = new CustomCellRenderer();
		_customCellEditor = new CustomCellEditor();
		useCustomViewForCellRendering = columnModel.customRendering;
		disableTerminateEditOnFocusLost = columnModel.disableTerminateEditOnFocusLost;
	}

	@Override
	public FIBCustomColumn getColumnModel() {
		return (FIBCustomColumn) super.getColumnModel();
	}

	private FIBCustomComponent<T, ?> makeCustomComponent(
			Class<FIBCustomComponent<T, ?>> customComponentClass,
			Class<T> dataClass) {
		Class[] types = new Class[1];
		types[0] = dataClass;
		try {
			boolean found = false;
			Constructor<FIBCustomComponent<T, ?>> constructor = null;
			while (!found && types[0] != null && customComponentClass != null) {
				try {
					constructor = customComponentClass.getConstructor(types);
					found = true;
				} catch (NoSuchMethodException e) {
					types[0] = types[0].getSuperclass();
				}
			}
			if (constructor == null) {
				logger.warning("Cound not instanciate class "
						+ customComponentClass
						+ " : no valid constructor found");
				return null;
			}
			Object[] args = new Object[1];
			args[0] = null;
			return constructor.newInstance(args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<T> getValueClass() {
		return TypeUtils.getBaseClass(getColumnModel().getDataClass());
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	@Override
	public String toString() {
		return "CustomColumn " + "[" + getTitle() + "]"
				+ Integer.toHexString(hashCode());
	}

	// Returns true as cell renderer is required here
	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return _customCellRenderer;
	}

	private CustomCellRenderer _customCellRenderer;

	protected class CustomCellRenderer extends FIBTableCellRenderer<T> {

		public CustomCellRenderer() {
			super(CustomColumn.this);
			setFont(CustomColumn.this.getFont());
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c;
			if (((isSelected) && (hasFocus)) || useCustomViewForCellRendering) {
				c = getViewCustomWidget(elementAt(row)).getJComponent();
				Color fg = null;
				Color bg = null;
				if (isSelected) {
					c.setForeground(fg == null ? table.getSelectionForeground()
							: fg);
					c.setBackground(bg == null ? table.getSelectionBackground()
							: bg);
				} else {
					c.setForeground(table.getForeground());
					c.setBackground(table.getBackground());
				}
				setFont(table.getFont());
				if (c instanceof JComponent) {
					if (hasFocus) {
						Border border = null;
						if (isSelected) {
							border = UIManager
									.getBorder("Table.focusSelectedCellHighlightBorder");
						}
						if (border == null) {
							border = UIManager
									.getBorder("Table.focusCellHighlightBorder");
						}
						((JComponent) c).setBorder(border);
					}
					if (hasFocus && !isSelected
							&& table.isCellEditable(row, column)) {
						Color col;
						col = UIManager.getColor("Table.focusCellForeground");
						if (col != null) {
							c.setForeground(col);
						}
						col = UIManager.getColor("Table.focusCellBackground");
						if (col != null) {
							c.setBackground(col);
						}
					}
				}
			} else {
				Component returned = super.getTableCellRendererComponent(table,
						value, isSelected, hasFocus, row, column);
				if (returned instanceof JLabel) {
					((JLabel) returned).setText(renderValue((T) value));
					if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
						((JLabel) returned).setForeground(getColorFor(value));
					}
					((JLabel) returned).setFont(CustomColumn.this.getFont());
				}
				c = returned;
			}
			if (c instanceof JComponent) {
				((JComponent) c).setToolTipText(getTooltip(elementAt(row)));
			}
			return c;
		}

	}

	protected Color getColorFor(Object value) {
		return Color.black;
		// return _viewCustomWidget.getColorForObject(value);
	}

	protected FIBCustomComponent<T, ?> getViewCustomWidget(Object rowObject) {
		if (_viewCustomWidget != null) {
			T value = getValueFor(rowObject);
			_viewCustomWidget.setEditedObject(value);
			_viewCustomWidget.setRevertValue(value);
			logger.fine("Return _viewCustomWidget for model rowObject="
					+ rowObject + " value=" + value);
		}
		return _viewCustomWidget;
	}

	protected FIBCustomComponent<T, ?> getEditCustomWidget(Object rowObject) {
		if (_editCustomWidget != null) {
			T value = getValueFor(rowObject);
			_editCustomWidget.setEditedObject(value);
			_editCustomWidget.setRevertValue(value);
			logger.fine("Return _editCustomWidget for model rowObject="
					+ rowObject + " value=" + value);
			// setEditedRowObject(value);
		}
		return _editCustomWidget;

	}

	// Returns true as cell editor is required here
	@Override
	public boolean requireCellEditor() {
		return true;
	}

	// Must be overriden if required
	@Override
	public TableCellEditor getCellEditor() {
		return _customCellEditor;
	}

	private CustomCellEditor _customCellEditor;

	protected class CustomCellEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {
		FIBCustomComponent<T, ?> _customWidget;

		public CustomCellEditor() {
			_customWidget = getEditCustomWidget(null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}

		@Override
		protected void fireEditingCanceled() {
			// if (_customWidget != null) _customWidget.fireEditingCanceled();
			super.fireEditingCanceled();
		}

		@Override
		protected void fireEditingStopped() {
			// if (_customWidget != null) _customWidget.fireEditingStopped();
			super.fireEditingStopped();
		}

		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		@Override
		public Object getCellEditorValue() {
			if (_customWidget == null) {
				return null;
			}
			return _customWidget.getEditedObject();
		}

		// Implement the one method defined by TableCellEditor.
		@Override
		public Component getTableCellEditorComponent(final JTable table,
				Object value, boolean isSelected, int row, int column) {
			// logger.info("elementAt(row)="+elementAt(row));
			if (disableTerminateEditOnFocusLost) {
				table.putClientProperty("terminateEditOnFocusLost",
						Boolean.FALSE);
			}
			setEditedRowObject(elementAt(row));
			_customWidget.setEditedObject(getValueFor(elementAt(row)));
			if (disableTerminateEditOnFocusLost) {
				_customWidget.addApplyCancelListener(new ApplyCancelListener() {

					@Override
					public void fireApplyPerformed() {
						table.putClientProperty("terminateEditOnFocusLost",
								Boolean.TRUE);
					}

					@Override
					public void fireCancelPerformed() {
						table.putClientProperty("terminateEditOnFocusLost",
								Boolean.TRUE);
					}

				});
			}
			return _customWidget.getJComponent();
		}
	}

	protected Object _editedRowObject;

	protected void setEditedRowObject(Object anObject) {
		logger.info("setEditedRowObject with " + anObject);
		_editedRowObject = anObject;

		for (FIBCustomAssignment assign : getColumnModel().getAssignments()) {
			DataBinding variableDB = assign.getVariable();
			DataBinding valueDB = assign.getValue();

			// logger.info("Assignment");
			// logger.info("variableDB="+variableDB+" valid="+variableDB.getBinding().isBindingValid());
			// logger.info("valueDB="+valueDB+" valid="+valueDB.getBinding().isBindingValid());
			if (valueDB.getBinding().isBindingValid()) {
				Object value = valueDB.getBinding().getBindingValue(this);
				// logger.info("value="+value);
				if (variableDB.getBinding().isBindingValid()) {
					// System.out.println("Assignment "+assign+" set value with "+value);
					variableDB.getBinding().setBindingValue(value, this);
				}
			}
		}

	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(FIBCustom.COMPONENT_NAME)) {
			return _editCustomWidget;
		} else {
			return super.getValue(variable);
		}
	}

	@Override
	public void fireApplyPerformed() {
		logger.fine("fireApplyPerformed() for " + _editedRowObject);
		setValueFor(_editedRowObject, _editCustomWidget.getEditedObject());
		notifyValueChangedFor(_editedRowObject,
				_editCustomWidget.getEditedObject());
	}

	@Override
	public void fireCancelPerformed() {
	}

	protected String renderValue(T value) {
		if (value == null) {
			return "";
		}
		return getStringRepresentation(value);
	}

}
