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

import javax.swing.JComponent;
import javax.swing.table.TableCellRenderer;

import org.openflexo.foundation.FlexoModelObject;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class StringColumn<D extends FlexoModelObject> extends AbstractColumn<D,String>
{

    public StringColumn(String title, int defaultWidth)
    {
        this(title, defaultWidth, true);
    }

    public StringColumn(String title, int defaultWidth, boolean isResizable)
    {
        this(title, defaultWidth, isResizable, true);
    }

    public StringColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle)
    {
        super(title, defaultWidth, isResizable, displayTitle);
    }

    @Override
	public Class getValueClass()
    {
        return String.class;
    }

    @Override
	public String getValueFor(D object)
    {
        return getValue(object);
    }

    /**
     * Overrides getCellRenderer
     * @see org.openflexo.components.tabular.model.AbstractColumn#getCellRenderer()
     */
    @Override
	public TableCellRenderer getCellRenderer()
    {
        TableCellRenderer r= super.getCellRenderer();
        if (r instanceof JComponent)
            ((JComponent)r).setToolTipText(getLocalizedTooltip());
        return r;
    }
    
    public abstract String getValue(D object);

    @Override
	public String toString()
    {
        return "StringColumn " + "@" + Integer.toHexString(hashCode());
    }
}
