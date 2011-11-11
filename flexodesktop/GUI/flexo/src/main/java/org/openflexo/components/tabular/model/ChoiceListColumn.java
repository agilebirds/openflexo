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
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.kvc.ChoiceList;

/**
 * Represents a column where values can be viewed and edited as ChoiceList instances
 * 
 * @author sguerin
 * 
 */
public abstract class ChoiceListColumn<D extends FlexoModelObject> extends AbstractColumn<D, ChoiceList> implements
		EditableColumn<D, ChoiceList> {

	private ChoiceListCellRenderer _cellRenderer;

	private ChoiceListCellEditor _cellEditor;

	public ChoiceListColumn(String title, int defaultWidth) {
		super(title, defaultWidth, true);
		_cellRenderer = new ChoiceListCellRenderer();
		_cellEditor = new ChoiceListCellEditor(new JComboBox());
	}

	@Override
	public Class getValueClass() {
		return ChoiceList.class;
	}

	@Override
	public ChoiceList getValueFor(D object) {
		return getValue(object);
	}

	public abstract ChoiceList getValue(D object);

	@Override
	public boolean isCellEditableFor(D object) {
		return true;
	}

	@Override
	public void setValueFor(D object, ChoiceList value) {
		setValue(object, value);
	}

	public abstract void setValue(D object, ChoiceList aValue);

	/**
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		return _cellRenderer;
	}

	protected class ChoiceListCellRenderer extends TabularViewCellRenderer {
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
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (returned instanceof JLabel) {
				((JLabel) returned).setText(renderChoiceListValue((ChoiceList) value));
			}
			return returned;
		}
	}

	protected abstract String renderChoiceListValue(ChoiceList value);

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
		return _cellEditor;
	}

	protected class ChoiceListCellEditor extends DefaultCellEditor {
		private ChoiceListComboBoxModel _comboBoxModel;

		private JComboBox comboBox;

		public ChoiceListCellEditor(JComboBox aComboBox) {
			super(aComboBox);
			comboBox = aComboBox;
			comboBox.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (returned instanceof JLabel) {
						((JLabel) returned).setText(renderChoiceListValue((ChoiceList) value));
					}
					return returned;
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component returned = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			comboBox.setModel(getComboBoxModel((ChoiceList) value));
			return returned;
		}

		private ChoiceListComboBoxModel getComboBoxModel(ChoiceList choiceList) {
			if (_comboBoxModel == null) {
				_comboBoxModel = new ChoiceListComboBoxModel(choiceList);
			}
			_comboBoxModel.setChoiceList(choiceList);
			return _comboBoxModel;
		}

		protected class ChoiceListComboBoxModel extends DefaultComboBoxModel {
			protected ChoiceListComboBoxModel(ChoiceList choiceList) {
				super();
				setChoiceList(choiceList);
			}

			protected void setChoiceList(ChoiceList aChoiceList) {
				if (aChoiceList != null) {
					removeAllElements();
					for (Enumeration en = aChoiceList.getAvailableValues().elements(); en.hasMoreElements();) {
						addElement(en.nextElement());
					}
					setSelectedItem(aChoiceList);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "ChoiceListColumn " + "@" + Integer.toHexString(hashCode());
	}
}
