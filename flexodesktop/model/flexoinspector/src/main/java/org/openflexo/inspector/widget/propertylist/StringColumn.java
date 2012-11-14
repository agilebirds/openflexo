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

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.widget.WidgetFont;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class StringColumn extends AbstractColumn {

	private String font;

	public StringColumn(String title, int defaultWidth) {
		this(title, defaultWidth, true);
	}

	public StringColumn(String title, int defaultWidth, boolean isResizable) {
		this(title, defaultWidth, isResizable, true);
	}

	public StringColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle) {
		super(title, defaultWidth, isResizable, displayTitle);
	}

	public StringColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle, String font) {
		super(title, defaultWidth, isResizable, displayTitle);
		this.font = font;
	}

	@Override
	public Class getValueClass() {
		return String.class;
	}

	@Override
	public Object getValueFor(InspectableObject object) {
		return getValue(object);
	}

	public abstract String getValue(InspectableObject object);

	@Override
	public String toString() {
		return "StringColumn " + "@" + Integer.toHexString(hashCode());
	}

	/**
	 * Returns true as cell renderer is required here
	 * 
	 * @return true
	 */
	@Override
	public boolean requireCellRenderer() {
		return font != null;
	}

	/**
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		if (requireCellRenderer()) {
			if (_customCellRenderer == null) {
				_customCellRenderer = new CustomFontCellRenderer(font);
			}
			return _customCellRenderer;
		}
		return super.getCellRenderer();
	}

	private CustomFontCellRenderer _customCellRenderer;

	protected class CustomFontCellRenderer extends PropertyListCellRenderer {
		private Font customFont;

		CustomFontCellRenderer(String fontAsString) {
			customFont = new WidgetFont(fontAsString).getTheFont();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			((JLabel) returned).setFont(customFont);
			if (returned instanceof JComponent) {
				((JComponent) returned).setToolTipText(getTooltip(getModel().elementAt(row)));
			}
			return returned;
		}
	}

}
