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

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Color implements XMLSerializable
{

    public String r;

    public String g;

    public String b;

    public String catalog;

    public String name;

    public String w;

    public String a;

    public static OO3Color textColor()
    {
        OO3Color returned = new OO3Color();
        returned.catalog = "System";
        returned.name = "textColor";
        return returned;
    }

    public static OO3Color bgColor()
    {
        OO3Color returned = new OO3Color();
        returned.w = "0";
        returned.a = "0";
        return returned;
    }

    public static OO3Color rgbColor(float r, float g, float b)
    {
        OO3Color returned = new OO3Color();
        returned.r = "" + r;
        returned.g = "" + g;
        returned.b = "" + b;
        return returned;
    }
}
