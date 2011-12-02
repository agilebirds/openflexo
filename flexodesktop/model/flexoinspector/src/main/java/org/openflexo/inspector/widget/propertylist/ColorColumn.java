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

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class ColorColumn extends AbstractColumn {

	public ColorColumn(PropertyListColumn col, String title, int defaultWidth) {
		this(col, title, defaultWidth, true);
	}

	public ColorColumn(PropertyListColumn col, String title, int defaultWidth, boolean isResizable) {
		this(title, defaultWidth, isResizable, true);
	}

	public ColorColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle) {
		super(title, defaultWidth, isResizable, displayTitle);
	}

	@Override
	public Class getValueClass() {
		return Color.class;
	}

	@Override
	public Object getValueFor(InspectableObject object) {
		return getValue(object);
	}

	@Override
	public boolean isCellEditableFor(InspectableObject object) {
		return true;
	}

	public void setValueFor(InspectableObject object, Object value) {
		setValue(object, value);
		notifyValueChangedFor(object);
	}

	public abstract void setValue(InspectableObject object, Object aValue);

	public abstract Object getValue(InspectableObject object);

	@Override
	public String toString() {
		return "ColorColumn " + "@" + Integer.toHexString(hashCode());
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
		if (_colorCellEditor == null) {
			_colorCellEditor = new ColorCellEditor();
		}
		return _colorCellEditor;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		if (_colorCellEditor == null) {
			_colorCellEditor = new ColorCellEditor();
		}
		return _colorCellEditor;
	}

	protected ColorCellEditor _colorCellEditor;

	protected InspectableObject currentlyInspectedRow;

	protected class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer, ActionListener {
		JButton _customWidget;

		public void setColor(Color c) {
			_customWidget.setBackground(c);
		}

		public ColorCellEditor() {
			this(null, null);
		}

		public ColorCellEditor(Color initialColor, InspectableObject inspectable) {
			super();
			_customWidget = new ColorButton(initialColor, inspectable);
		}

		private class ColorButton extends JButton {
			public InspectableObject inspectableObject;

			public ColorButton() {
				this(null, null);
			}

			public ColorButton(Color initialColor, InspectableObject inspectable) {
				super();
				inspectableObject = inspectable;
				setBackground(initialColor);
				setBorder(BorderFactory.createEtchedBorder());
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (inspectableObject != currentlyInspectedRow) {
							return;
						}
						// get the new color
						Color selectedColor = JColorChooser.showDialog(ColorButton.this,
								FlexoLocalization.localizedForKey("select_a_color"), getBackground());
						if (selectedColor != null) {
							setBackground(selectedColor);

							// String colorAsString = selectedColor.getRed() +
							// "," + selectedColor.getGreen() + "," +
							// selectedColor.getBlue();

							// System.out.println("Sets color with "+selectedColor);
							setValue(inspectableObject, selectedColor);
						}
					}
				});
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}

		protected JButton getEditCustomWidget(Color value, int row) {
			// _customWidget.setBackground(value);

			_customWidget = new ColorButton(value, elementAt(row));
			return _customWidget;
		}

		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		@Override
		public Object getCellEditorValue() {
			return _customWidget.getBackground();
		}

		// Implement the one method defined by TableCellEditor.
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			currentlyInspectedRow = elementAt(row);
			return getEditCustomWidget((Color) value, row);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			currentlyInspectedRow = elementAt(row);
			JButton editCustomWidget = getEditCustomWidget((Color) value, row);
			editCustomWidget.setToolTipText(getTooltip(currentlyInspectedRow));
			return editCustomWidget;

		}
	}

}
