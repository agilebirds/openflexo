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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBIconColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class IconColumn extends AbstractColumn<Icon> implements EditableColumn<Icon> {

	public IconColumn(FIBIconColumn columnModel, FIBTableModel tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
	}

	@Override
	public FIBIconColumn getColumnModel() {
		return (FIBIconColumn) super.getColumnModel();
	}

	@Override
	public Class<Icon> getValueClass() {
		return Icon.class;
	}

	@Override
	public String toString() {
		return "IconColumn " + "@" + Integer.toHexString(hashCode());
	}

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
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		if (_iconTableCellRenderer == null) {
			_iconTableCellRenderer = new IconCellRenderer();
		}
		return _iconTableCellRenderer;
	}

	private IconCellRenderer _iconTableCellRenderer;

	class IconCellRenderer extends FIBTableCellRenderer {

		public IconCellRenderer() {
			super(IconColumn.this);
		}

		/**
		 * 
		 * Returns the cell renderer.
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
			((JLabel) returned).setText(null);
			if (value instanceof Icon) {
				((JLabel) returned).setIcon((Icon) value);
			} else {
				((JLabel) returned).setIcon(null);
			}
			return returned;
		}
	}

}
