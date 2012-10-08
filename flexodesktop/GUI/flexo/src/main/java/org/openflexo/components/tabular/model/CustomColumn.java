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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.swing.CustomPopup;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class CustomColumn<D extends FlexoModelObject, T> extends AbstractColumn<D, T> implements EditableColumn<D, T> {

	protected FlexoProject _project;

	public CustomColumn(String title, int defaultWidth, FlexoProject project) {
		super(title, defaultWidth, true);
		_project = project;
		_selectorCellRenderer = new SelectorCellRenderer();
		_selectorCellEditor = new SelectorCellEditor();
	}

	@Override
	public boolean isCellEditableFor(D object) {
		return true;
	}

	@Override
	public void setValueFor(D object, T value) {
		setValue(object, value);
	}

	public abstract void setValue(D object, T aValue);

	@Override
	public String toString() {
		return "SelectorColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}

	@Override
	public T getValueFor(D object) {
		return getValue(object);
	}

	public abstract T getValue(D object);

	/**
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		return _selectorCellRenderer;
	}

	private SelectorCellRenderer _selectorCellRenderer;

	protected class SelectorCellRenderer extends TabularViewCellRenderer {
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
			D rowObject = elementAt(row);
			if (isSelected && hasFocus) {
				CustomPopup<T> returned = getViewSelector(rowObject, (T) value);
				if (ToolBox.isMacOSLaf()) {
					setComponentBackground(returned, hasFocus, isSelected, row, column);
				}
				return returned;
			} else {
				Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (returned instanceof JLabel) {
					((JLabel) returned).setText(getViewSelector(rowObject, (T) value).renderedString((T) value));
				}
				return returned;
			}
		}
	}

	protected abstract TextFieldCustomPopup<T> getViewSelector(D rowObject, T value);

	protected abstract TextFieldCustomPopup<T> getEditSelector(D rowObject, T value);

	/**
	 * Must be overriden if required
	 * 
	 * @return
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
		return _selectorCellEditor;
	}

	protected SelectorCellEditor _selectorCellEditor;

	protected class SelectorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, ApplyCancelListener {
		TextFieldCustomPopup<T> _selector;

		public SelectorCellEditor() {
			_selector = getEditSelector(null, null);
			_selector.setBorder(null);
			_selector.getTextField().setBorder(null);
			_selector.addApplyCancelListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}

		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		@Override
		public Object getCellEditorValue() {
			return _selector.getEditedObject();
		}

		// Implement the one method defined by TableCellEditor.
		@Override
		public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
			table.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
			addCellEditorListener(new CellEditorListener() {
				@Override
				public void editingCanceled(ChangeEvent e) {
					table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
				}

				@Override
				public void editingStopped(ChangeEvent e) {
					table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
				}
			});
			setEditedRowObject(elementAt(row));
			_selector = getEditSelector(elementAt(row), (T) value);
			_selector.getTextField().setBorder(null);
			_selector.setBorder(null);
			/*_selector.setEditedObject((T)value);
			 _selector.setRevertValue((T) value);*/
			return _selector;
		}

		@Override
		public void fireApplyPerformed() {
			actionPerformed(null);
		}

		@Override
		public void fireCancelPerformed() {
			actionPerformed(null);
		}

		@Override
		protected void fireEditingCanceled() {
			if (_selector != null) {
				_selector.closePopup();
			}
			super.fireEditingCanceled();
		}

		@Override
		protected void fireEditingStopped() {
			if (_selector != null) {
				_selector.closePopup();
			}
			super.fireEditingStopped();

		}

	}

	protected D _editedRowObject;

	protected void setEditedRowObject(D anObject) {
		_editedRowObject = anObject;
	}

}
