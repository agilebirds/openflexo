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

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBNumberColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class NumberColumn extends AbstractColumn<Number> implements EditableColumn<Number> {

	private DefaultCellEditor editor;

	public NumberColumn(FIBNumberColumn columnModel, FIBTableModel tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
	}

	@Override
	public FIBNumberColumn getColumnModel() {
		return (FIBNumberColumn) super.getColumnModel();
	}

	@Override
	public Class<Number> getValueClass() {
		return Number.class;
	}

	@Override
	public String toString() {
		return "IntegerColumn " + "@" + Integer.toHexString(hashCode());
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return getDefaultTableCellRenderer();
	}

	@Override
	public boolean requireCellEditor() {
		return true;
	}

	@Override
	public TableCellEditor getCellEditor() {
		if (editor == null) {
			editor = new DefaultCellEditor(new JTextField()) {
				@Override
				public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
					final JTextField textfield = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
					textfield.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
					textfield.setText(((Number) value).toString());
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							textfield.selectAll();
						}
					});
					return textfield;
				}

				@Override
				public Number getCellEditorValue() {
					switch (getColumnModel().getNumberType()) {
					case ByteType:
						return Byte.parseByte((String) super.getCellEditorValue());
					case ShortType:
						return Short.parseShort((String) super.getCellEditorValue());
					case IntegerType:
						return Integer.parseInt((String) super.getCellEditorValue());
					case LongType:
						return Long.parseLong((String) super.getCellEditorValue());
					case FloatType:
						return Float.parseFloat((String) super.getCellEditorValue());
					case DoubleType:
						return Double.parseDouble((String) super.getCellEditorValue());
					default:
						return null;
					}
				}
			};
		}
		return editor;
	}

}
