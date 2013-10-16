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

import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
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
					textfield.setInputVerifier(new InputVerifier() {

						@Override
						public boolean verify(JComponent input) {
							if (input instanceof JTextField) {
								String text = ((JTextField) input).getText();
								return text == null || text.trim().length() == 0 || getValue(text) != null;
							}
							return true;
						}
					});
					if (value != null) {
						textfield.setText(((Number) value).toString());
					} else {
						textfield.setText("");
					}
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
					Object cellEditorValue = super.getCellEditorValue();
					if (cellEditorValue != null && cellEditorValue.toString().trim().length() > 0) {
						return getValue(cellEditorValue.toString().trim());
					}
					return null;
				}

				private Number getValue(String value) {
					try {
						switch (getColumnModel().getNumberType()) {
						case ByteType:
							return Byte.parseByte(value);
						case ShortType:
							return Short.parseShort(value);
						case IntegerType:
							return Integer.parseInt(value);
						case LongType:
							return Long.parseLong(value);
						case FloatType:
							return Float.parseFloat(value);
						case DoubleType:
							return Double.parseDouble(value);
						default:
							return null;
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
		}
		return editor;
	}
}
