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
package org.openflexo.fib.utils.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Observable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class AbstractColumn<D extends Observable, T> {
	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font MEDIUM_FONT = new Font("SansSerif", Font.PLAIN, 10);
	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 9);

	public static final Color SELECTED_LINES_TABULAR_VIEW_COLOR = new Color(181, 213, 255);
	public static final Color ODD_LINES_TABULAR_VIEW_COLOR = new Color(237, 243, 254);
	public static final Color NON_ODD_LINES_TABULAR_VIEW_COLOR = Color.WHITE;
	public static final Color SELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR = Color.WHITE;
	public static final Color UNSELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR = Color.BLACK;
	public static final Color UNSELECTED_DISABLED_CELL_TABULAR_VIEW_FOREGROUND_COLOR = Color.LIGHT_GRAY;

	private String _title;

	private int _defaultWidth;

	private boolean _isResizable;

	private boolean _displayTitle;

	private AbstractModel<?, D> _model;

	public AbstractColumn(String unlocalizedTitle, int defaultWidth, boolean isResizable) {
		this(unlocalizedTitle, defaultWidth, isResizable, true);
	}

	public AbstractColumn(String unlocalizedTitle, int defaultWidth, boolean isResizable, boolean displayTitle) {
		super();
		_title = unlocalizedTitle;
		_defaultWidth = defaultWidth;
		_isResizable = isResizable;
		_displayTitle = displayTitle;
	}

	public void setModel(AbstractModel<?, D> model) {
		_model = model;
	}

	public AbstractModel<?, D> getModel() {
		return _model;
	}

	public D elementAt(int row) {
		return _model.elementAt(row);
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getLocalizedTitle() {
		if ((_title == null) || (!_displayTitle)) {
			return " ";
		}
		return FlexoLocalization.localizedForKey(getTitle());
	}

	public String getLocalizedTooltip() {
		if (_title == null) {
			return " ";
		}
		return FlexoLocalization.localizedForKey(_title);
	}

	public int getDefaultWidth() {
		return _defaultWidth;
	}

	public boolean getResizable() {
		return _isResizable;
	}

	public void setDefaultWidth(int width) {
		_defaultWidth = width;
	}

	public abstract Class<T> getValueClass();

	public boolean isCellEditableFor(D object) {
		return false;
	}

	public abstract T getValueFor(D object);

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellRenderer getCellRenderer() {
		return getDefaultCellRenderer();
	}

	private TabularViewCellRenderer _defaultTableCellRenderer;

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellRenderer getDefaultCellRenderer() {
		if (_defaultTableCellRenderer == null) {
			_defaultTableCellRenderer = new TabularViewCellRenderer();
		}
		return _defaultTableCellRenderer;
	}

	protected class TabularViewCellRenderer extends DefaultTableCellRenderer {

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
			if (!isSelected || ToolBox.getPLATFORM() == ToolBox.MACOS) {
				setComponentBackground(returned, hasFocus, isSelected, row, column);
			}
			// returned.setFont(AdvancedPrefs.getBrowserFont().getTheFont());
			if (returned instanceof JComponent) {
				((JComponent) returned).setToolTipText(getLocalizedTooltip(getModel().elementAt(row)));
			}

			return returned;
		}

		protected void setComponentBackground(Component component, boolean hasFocus, boolean isSelected, int row, int column) {
			if ((hasFocus) && ((getModel() != null) && (getModel().isCellEditable(row, column))) && (isSelected)) {
				component.setForeground(SELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR);
			} else {
				component.setForeground(UNSELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR);
			}
			if (isSelected) {
				component.setBackground(SELECTED_LINES_TABULAR_VIEW_COLOR);
			} else {
				if (row % 2 == 0) {
					component.setBackground(ODD_LINES_TABULAR_VIEW_COLOR);
				} else {
					component.setBackground(NON_ODD_LINES_TABULAR_VIEW_COLOR);
				}
			}
		}
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellRenderer() {
		return true;
	}

	public String getLocalizedTooltip(D object) {
		return getLocalizedTooltip();
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellEditor() {
		return false;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellEditor getCellEditor() {
		return null;
	}

	public boolean getDisplayTitle() {
		return _displayTitle;
	}

}
