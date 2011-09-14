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
package org.openflexo.antar.expr.parser;

import java.util.Date;

public class DateValue extends Value {

    private Date _value;
    
    public static DateValue createDateValue(Date value)
    {
        return new DateValue(value);
    }
 
    public DateValue (Date value)
    {
        super();
        _value = value;
    }
    
    public Date getDateValue() 
    {
        return _value;
    }
    
    @Override
	public String getStringValue() 
    {
        return _value.toString();
    }
    
    @Override
	public String toString()
    {
        return getPrefix()+"Date["+_value+"]";
    }

    public String getSerializationValue()
    {
        return "$\""+_value+"\"";
    }

   @Override
public boolean equals (Object obj)
    {
        if (obj instanceof DateValue) {
            return getDateValue().equals(((DateValue)obj).getDateValue());
        }
        return super.equals(obj);
    }
}
