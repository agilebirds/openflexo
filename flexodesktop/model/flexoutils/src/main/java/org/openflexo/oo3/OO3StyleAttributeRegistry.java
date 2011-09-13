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
public class OO3StyleAttributeRegistry implements XMLSerializable
{
    public Vector<StyleAttribute> styleAttributes;

    public OO3StyleAttributeRegistry()
    {
        super();
        styleAttributes = new Vector<StyleAttribute>();
        styleAttributes.add(new StyleAttribute("0", "font-fill", "font", "fill-color", "color", OO3Color.textColor()));
        styleAttributes.add(new StyleAttribute("0", "font-italic", "font", "italic", "bool", "no"));
        styleAttributes.add(new StyleAttribute("0", "font-size", "font", "size", "number", "12"));
        styleAttributes.add(new StyleAttribute("0", "font-weight", "font", "weight", "number", "5"));
        styleAttributes.add(new StyleAttribute("0", "text-background-color", "text", "background color", "color", OO3Color.bgColor()));
        styleAttributes.add(new StyleAttribute("0", "underline-color", "underline", "color", "color", OO3Color.textColor()));
        styleAttributes.add(new StyleAttribute("1", "underline-style", "underline", "style", "enum", OO3Enum.getUnderlineEnum()));
    }

    public static class StyleAttribute implements XMLSerializable
    {
        public String version;

        public String key;

        public String group;

        public String name;

        public String className;

        public OO3Color color;

        public String value;

        public OO3Enum en;

        public StyleAttribute()
        {
            super();
        }

        public StyleAttribute(String v, String k, String gr, String n, String cn)
        {
            this();
            this.version = v;
            this.key = k;
            this.group = gr;
            this.name = n;
            this.className = cn;
        }

        public StyleAttribute(String v, String k, String gr, String n, String cn, OO3Color col)
        {
            this(v, k, gr, n, cn);
            this.color = col;
        }

        public StyleAttribute(String v, String k, String gr, String n, String cn, String val)
        {
            this(v, k, gr, n, cn);
            this.value = val;
        }

        public StyleAttribute(String v, String k, String gr, String n, String cn, OO3Enum enumeration)
        {
            this(v, k, gr, n, cn);
            this.en = enumeration;
        }

    }

}
