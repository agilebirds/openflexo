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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.CustomWidget;
import org.openflexo.inspector.widget.CustomWidget.ApplyCancelListener;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class CustomColumn extends AbstractColumn implements EditableColumn, ApplyCancelListener {

	private static final Logger logger = Logger.getLogger(CustomColumn.class.getPackage().getName());

	PropertyListColumn _propertyListColumn;

	private CustomWidget _viewCustomWidget;

	private CustomWidget _editCustomWidget;

	private AbstractController _controller;

	private boolean useCustomViewForCellRendering;

	public CustomColumn(PropertyListColumn propertyListColumn, String title, int defaultWidth, AbstractController controller) {
		this(propertyListColumn, title, defaultWidth, true, controller);
	}

	public CustomColumn(PropertyListColumn propertyListColumn, String title, int defaultWidth, boolean isResizable,
			AbstractController controller) {
		this(propertyListColumn, title, defaultWidth, isResizable, true, controller);
	}

	public CustomColumn(PropertyListColumn propertyListColumn, String title, int defaultWidth, boolean isResizable, boolean displayTitle,
			AbstractController controller) {
		super(title, defaultWidth, isResizable, displayTitle);
		_propertyListColumn = propertyListColumn;
		_viewCustomWidget = buildCustomWidget();
		_editCustomWidget = buildCustomWidget();
		_editCustomWidget.addApplyCancelListener(this);
		_customCellRenderer = new CustomCellRenderer();
		_customCellEditor = new CustomCellEditor();
		_controller = controller;
		useCustomViewForCellRendering = Boolean.valueOf(propertyListColumn.getValueForParameter("customRendering"));
	}

	private CustomWidget buildCustomWidget() {
		String className = _propertyListColumn.getValueForParameter("className");
		try {
			Class widgetClass = Class.forName(className);
			Class[] constructorClassParams = new Class[2];
			constructorClassParams[0] = PropertyModel.class;
			constructorClassParams[1] = AbstractController.class;
			Constructor c = widgetClass.getConstructor(constructorClassParams);
			Object[] constructorParams = new Object[2];
			constructorParams[0] = _propertyListColumn;
			constructorParams[1] = _controller;
			CustomWidget returned = (CustomWidget) c.newInstance(constructorParams);
			return returned;
		} catch (ClassNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Class not found: " + className + ". See console for details.");
			}
			e.printStackTrace();
		} catch (SecurityException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (InstantiationException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class getValueClass() {
		return _viewCustomWidget.getDefaultType();
	}

	@Override
	public boolean isCellEditableFor(InspectableObject object) {
		return true;
	}

	@Override
	public void setValueFor(InspectableObject object, Object value) {
		setValue(object, value);
		notifyValueChangedFor(object);
	}

	public abstract void setValue(InspectableObject object, Object aValue);

	@Override
	public String toString() {
		return "CustomColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}

	@Override
	public Object getValueFor(InspectableObject object) {
		return getValue(object);
	}

	public abstract Object getValue(InspectableObject object);

	/**
	 * Returns true as cell renderer is required here
	 * 
	 * @return true
	 */
	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	/**
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		return _customCellRenderer;
	}

	private CustomCellRenderer _customCellRenderer;

	protected class CustomCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 * Returns the selector cell renderer.
		 * 
		 * @param table
		 *            the <code>JTable</code>
		 * @param value
		 *            the value to assign to the cell at <code>[row, column]</code>
		 * @param isSelected
		 *            true if cell is selected
		 * @param hasFocus
		 *            true if cell has focus
		 * @param row
		 *            the row of the cell to render
		 * @param column
		 *            the column of the cell to render
		 * @return the default table cell renderer
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c;
			if (isSelected && hasFocus || useCustomViewForCellRendering) {
				c = getViewCustomWidget(elementAt(row)).getDynamicComponent();
				Color fg = null;
				Color bg = null;
				if (isSelected) {
					c.setForeground(fg == null ? table.getSelectionForeground() : fg);
					c.setBackground(bg == null ? table.getSelectionBackground() : bg);
				} else {
					c.setForeground(table.getForeground());
					c.setBackground(table.getBackground());
				}
				setFont(table.getFont());
				if (c instanceof JComponent) {
					if (hasFocus) {
						Border border = null;
						if (isSelected) {
							border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
						}
						if (border == null) {
							border = UIManager.getBorder("Table.focusCellHighlightBorder");
						}
						((JComponent) c).setBorder(border);
					}
					if (hasFocus && !isSelected && table.isCellEditable(row, column)) {
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
				Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (returned instanceof JLabel) {
					((JLabel) returned).setText(_propertyListColumn.getStringRepresentation(value));
					if (ToolBox.isMacOSLaf()) {
						((JLabel) returned).setForeground(getColorFor(value));
					}
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
		return _viewCustomWidget.getColorForObject(value);
	}

	protected CustomWidget getViewCustomWidget(InspectableObject value) {
		_viewCustomWidget.setModel(value);
		_viewCustomWidget.updateWidgetFromModel();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Return _viewCustomWidget for model " + value);
		}
		return _viewCustomWidget;
	}

	protected CustomWidget getEditCustomWidget(InspectableObject value) {
		_editCustomWidget.setModel(value);
		_editCustomWidget.updateWidgetFromModel();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Return _editCustomWidget for model " + value);
		}
		setEditedRowObject(value);
		return _editCustomWidget;
	}

	/**
	 * Returns true as cell editor is required here
	 * 
	 * @return true
	 */
	@Override
	public boolean requireCellEditor() {
		return true;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public TableCellEditor getCellEditor() {
		return _customCellEditor;
	}

	private CustomCellEditor _customCellEditor;

	protected class CustomCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		CustomWidget _customWidget;

		public CustomCellEditor() {
			_customWidget = getEditCustomWidget(null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}

		@Override
		protected void fireEditingCanceled() {
			if (_customWidget != null) {
				_customWidget.fireEditingCanceled();
			}
			super.fireEditingCanceled();
		}

		@Override
		protected void fireEditingStopped() {
			if (_customWidget != null) {
				_customWidget.fireEditingStopped();
			}
			super.fireEditingStopped();
		}

		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		@Override
		public Object getCellEditorValue() {
			return _customWidget.getEditedValue();
		}

		// Implement the one method defined by TableCellEditor.
		@Override
		public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
			// logger.info("elementAt(row)="+elementAt(row));
			if (_customWidget.disableTerminateEditOnFocusLost()) {
				table.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
			}
			_customWidget.setModel(elementAt(row));
			_customWidget.updateWidgetFromModel();
			setEditedRowObject(elementAt(row));
			if (_customWidget.disableTerminateEditOnFocusLost()) {
				_customWidget.addApplyCancelListener(new ApplyCancelListener() {

					@Override
					public void fireApplyPerformed() {
						table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
					}

					@Override
					public void fireCancelPerformed() {
						table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
					}

				});
			}
			return _customWidget.getDynamicComponent();
		}
	}

	protected InspectableObject _editedRowObject;

	protected void setEditedRowObject(InspectableObject anObject) {
		_editedRowObject = anObject;
	}

	@Override
	public void fireApplyPerformed() {
		notifyValueChangedFor(_editedRowObject);
	}

	@Override
	public void fireCancelPerformed() {
	}

}
