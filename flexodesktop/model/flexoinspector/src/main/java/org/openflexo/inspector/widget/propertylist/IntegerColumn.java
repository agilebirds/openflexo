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

import org.openflexo.inspector.InspectableObject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class IntegerColumn extends AbstractColumn implements EditableColumn
{

    public IntegerColumn(String title, int defaultWidth)
    {
        this(title, defaultWidth, true);
    }

    public IntegerColumn(String title, int defaultWidth, boolean isResizable)
    {
        this(title, defaultWidth, isResizable, true);
    }

    public IntegerColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle)
    {
        super(title, defaultWidth, isResizable, displayTitle);
    }

    @Override
	public Class getValueClass()
    {
        return Integer.class;
    }

    @Override
	public Object getValueFor(InspectableObject object)
    {
        return getValue(object);
    }

    public abstract Integer getValue(InspectableObject object);

    @Override
	public boolean isCellEditableFor(InspectableObject object)
    {
        return true;
    }

    @Override
	public void setValueFor(InspectableObject object, Object value)
    {
        setValue(object, (Integer) value);
        notifyValueChangedFor(object);
    }

    public abstract void setValue(InspectableObject object, Integer aValue);

    @Override
	public String toString()
    {
        return "IntegerColumn " + "@" + Integer.toHexString(hashCode());
    }
}
