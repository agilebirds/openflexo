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

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openflexo.inspector.InspectableObject;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class IconColumn extends AbstractColumn implements EditableColumn
{

    public IconColumn(String title, int defaultWidth)
    {
        this(title, defaultWidth, true);
    }

    public IconColumn(String title, int defaultWidth, boolean isResizable)
    {
        this(title, defaultWidth, isResizable, true);
    }

    public IconColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle)
    {
        super(title, defaultWidth, isResizable, displayTitle);
    }

    @Override
	public Class getValueClass()
    {
        return Boolean.class;
    }

    @Override
	public Object getValueFor(InspectableObject object)
    {
        return getValue(object);
    }

    public abstract Icon getValue(InspectableObject object);

    @Override
	public boolean isCellEditableFor(InspectableObject object)
    {
        return false;
    }

    @Override
	public void setValueFor(InspectableObject object, Object value)
    {
        setValue(object, (Icon) value);
        notifyValueChangedFor(object);
   }

    public abstract void setValue(InspectableObject object, Icon aValue);

    @Override
	public String toString()
    {
        return "IconColumn " + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Returns true as cell renderer is required here
     * 
     * @return true
     */
    @Override
	public boolean requireCellRenderer()
    {
        return true;
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
        }
        return _iconTableCellRenderer;
    }

    private IconCellRenderer _iconTableCellRenderer;

    class IconCellRenderer extends PropertyListCellRenderer
    {

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
