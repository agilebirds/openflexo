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

import java.awt.Component;
import java.util.Observable;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class IconColumn<D extends Observable> extends AbstractColumn<D,Icon>
{

    public IconColumn(String title, int defaultWidth)
    {
        super(title, defaultWidth, false);
    }

    @Override
	public String getLocalizedTitle()
    {
        return " ";
    }

    @Override
	public Class getValueClass()
    {
        return Icon.class;
    }

    @Override
	public Icon getValueFor(D object)
    {
        return getIcon(object);
    }

    public abstract Icon getIcon(D object);

    @Override
	public String toString()
    {
        return "IconColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
    }

    /**
     * Must be overriden if required
     * 
     * @return
     */
    @Override
	public TableCellRenderer getCellRenderer()
    {
        if (_iconTableCellRenderer == null) {
            _iconTableCellRenderer = new IconCellRenderer();
            _iconTableCellRenderer.setToolTipText(getLocalizedTooltip());
        }
        return _iconTableCellRenderer;
    }

    private TabularViewCellRenderer _iconTableCellRenderer;

    private class IconCellRenderer extends TabularViewCellRenderer
    {
    	IconCellRenderer() {
			super();
		}
        /**
         * 
         * Returns the cell renderer.
         * 
         * @param table
         *            the <code>JTable</code>
         * @param value
         *            the value to assign to the cell at
         *            <code>[row, column]</code>
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
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            ((JLabel) returned).setText(null);
            ((JLabel) returned).setIcon((Icon) value);
            return returned;
        }
    }

}
