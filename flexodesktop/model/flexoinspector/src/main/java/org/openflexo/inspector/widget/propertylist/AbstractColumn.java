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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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
public abstract class AbstractColumn {

	private static final Logger logger = Logger.getLogger(AbstractColumn.class.getPackage().getName());

	private String _title;

	private int _defaultWidth;

	private boolean _isResizable;

	private boolean _displayTitle;

	private PropertyListTableModel _model;

	private String tooltipKey;

	private PropertyListCellRenderer _defaultTableCellRenderer;

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

	protected void setModel(PropertyListTableModel model) {
		_model = model;
	}

	protected PropertyListTableModel getModel() {
		return _model;
	}

	public InspectableObject elementAt(int row) {
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

	public int getDefaultWidth() {
		return _defaultWidth;
	}

	public boolean getResizable() {
		return _isResizable;
	}

	public void setDefaultWidth(int width) {
		_defaultWidth = width;
	}

	public abstract Class getValueClass();

	public boolean isCellEditableFor(InspectableObject object) {
		return false;
	}

	public abstract Object getValueFor(InspectableObject object);

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellRenderer() {
		return false;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellRenderer getCellRenderer() {
		return getDefaultTableCellRenderer();
	}

	/**
	 * @return
	 */
	protected TableCellRenderer getDefaultTableCellRenderer() {
		if (_defaultTableCellRenderer == null) {
			_defaultTableCellRenderer = new PropertyListCellRenderer();
		}
		return _defaultTableCellRenderer;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellEditor() {
		return false;
	}

	public String getTooltip(InspectableObject object) {
		if (getModel() != null && tooltipKey != null) {
			return (String) object.objectForKey(tooltipKey);
		}
		return null;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellEditor getCellEditor() {
		return null;
	}

	public PropertyListColumn getPropertyListColumn() {
		return getModel().getPropertyListColumnWithTitle(_title);
	}

	public void notifyValueChangedFor(InspectableObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyValueChangedFor " + object);
		}
		// Following will force the whole row where object was modified to be updated
		// (In case of some computed cells are to be updated according to ths new value)
		getModel().fireTableRowsUpdated(getModel().indexOf(object), getModel().indexOf(object));
	}

	protected class PropertyListCellRenderer extends DefaultTableCellRenderer {

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
			/*if (!isSelected || ToolBox.getPLATFORM()==ToolBox.MACOS)
				setComponentBackground(returned, hasFocus, isSelected, row, column);*/
			if (returned instanceof JComponent) {
				((JComponent) returned).setToolTipText(getTooltip(getModel().elementAt(row)));
			}

			return returned;
		}

		/* protected void setComponentBackground(Component component, boolean hasFocus, boolean isSelected, int row, int column)
		 {
		     if ((hasFocus) && ((getModel() != null) && (getModel().isCellEditable(row, column))) && (isSelected)) {
		         component.setForeground(FlexoCst.SELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR);
		     } else {
		         component.setForeground(FlexoCst.UNSELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR);
		     }
		     if (isSelected) {
		         component.setBackground(FlexoCst.SELECTED_LINES_TABULAR_VIEW_COLOR);
		     } else {
		         if (row % 2 == 0) {
		             component.setBackground(FlexoCst.ODD_LINES_TABULAR_VIEW_COLOR);
		         } else {
		             component.setBackground(FlexoCst.NON_ODD_LINES_TABULAR_VIEW_COLOR);
		         }
		     }
		 }*/
	}

	public void setTooltipKey(String tooltipKey) {
		this.tooltipKey = tooltipKey;
	}

}
