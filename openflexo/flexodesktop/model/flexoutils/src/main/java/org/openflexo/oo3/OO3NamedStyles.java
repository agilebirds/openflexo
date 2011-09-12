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

import java.util.Hashtable;

import org.openflexo.xmlcode.XMLSerializable;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3NamedStyles implements XMLSerializable
{

    public static final String HIGHLIGHT = "Highlight";

    public static final String CITATION = "Citation";

    public static final String EMPHASIS = "Emphasis";

    public Hashtable<String,OO3NamedStyle> namedStyles;

    public OO3NamedStyles()
    {
        super();
        namedStyles = new Hashtable<String,OO3NamedStyle>();
        add(HIGHLIGHT, OO3Style.getHighLightStyle());
        add(CITATION, OO3Style.getCitationStyle());
        add(EMPHASIS, OO3Style.getEmphasisStyle());
    }

    public void add(String name, OO3Style style)
    {
        namedStyles.put(name, new OO3NamedStyle(name, style));
    }

    public static class OO3NamedStyle implements XMLSerializable
    {
        public String name;

        public OO3Style style;

        public OO3NamedStyle()
        {
            super();
        }

        public OO3NamedStyle(String n, OO3Style st)
        {
            this();
            this.style = st;
            this.name = n;
        }

    }

    public OO3NamedStyle styleWithName(String styleName)
    {
        return namedStyles.get(styleName);
    }

}
