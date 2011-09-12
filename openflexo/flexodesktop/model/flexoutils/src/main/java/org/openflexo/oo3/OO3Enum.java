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
package org.openflexo.oo3;

import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Enum implements XMLSerializable
{
    public Vector<OO3EnumElement> enumElements;

    public String defaultValue;

    public OO3Enum()
    {
        super();
        enumElements = new Vector<OO3EnumElement>();
        defaultValue = null;
    }

    public void addElement(String value, String name)
    {
        OO3EnumElement newOO3EnumElement = new OO3EnumElement(value, name);
        enumElements.add(newOO3EnumElement);
        if (defaultValue == null) {
            defaultValue = value;
        }
    }

    public static class OO3EnumElement implements XMLSerializable
    {
        public String value;

        public String name;

        public OO3EnumElement()
        {
            super();
        }

        public OO3EnumElement(String v, String n)
        {
            this();
            this.value = v;
            this.name = n;
        }
    }

    public static OO3Enum getUnderlineEnum()
    {
        OO3Enum returned = new OO3Enum();
        returned.addElement("0", "none");
        returned.addElement("1", "single");
        returned.addElement("2", "thick");
        returned.addElement("3", "double");
        return returned;
    }

}
