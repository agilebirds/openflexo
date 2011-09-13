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
package org.openflexo.foundation.utils;

import java.awt.Font;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class FlexoFont extends KVCObject implements StringConvertable
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoFont.class.getPackage().getName());

    private Font _theFont;
    
     public static final StringEncoder.Converter<FlexoFont> flexoFontConverter = new Converter<FlexoFont>(FlexoFont.class) {

        @Override
		public FlexoFont convertFromString(String value)
        {
            return stringToFont(value);
        }

        @Override
		public String convertToString(FlexoFont value)
        {
            return value.toString();
        }

    };

    public static final FlexoFont SANS_SERIF = new FlexoFont("Lucida Sans",Font.PLAIN, 10);

    public FlexoFont(String s)
    {
        this(nameFromString(s), styleFromString(s), sizeFromString(s));
    }

    public FlexoFont(String name, int style, int size)
    {
        super();
        _theFont = new Font(name,style,size);
    }

    public FlexoFont(Font aFont)
    {
        this(aFont.getName(), aFont.getStyle(), aFont.getSize());
    }

    @Override
	public String toString()
    {
        return _theFont.getName() + "," + _theFont.getStyle() + "," + _theFont.getSize();
    }

    @Override
	public StringEncoder.Converter getConverter()
    {
        return flexoFontConverter;
    }

    public static StringEncoder.Converter getConverterStatic()
    {
        return flexoFontConverter;
    }

    public static FlexoFont stringToFont(String s)
    {
        return new FlexoFont(s);
    }

    private static String nameFromString(String s)
    {
        return s.substring(0, s.indexOf(","));
    }

    private static int styleFromString(String s)
    {
        return Integer.parseInt(s.substring(s.indexOf(",") + 1, s.lastIndexOf(",")));
    }

    private static int sizeFromString(String s)
    {
        return Integer.parseInt(s.substring(s.lastIndexOf(",") + 1));
    }

    private static final Hashtable<String, FlexoFont> fontHashtable = new Hashtable<String, FlexoFont>();

    public static FlexoFont get(String fontAsString)
    {
        if (fontAsString == null) {
            return null;
        }
        FlexoFont answer = fontHashtable.get(fontAsString);
        if(answer==null){
        		answer = new FlexoFont(fontAsString);
        		fontHashtable.put(fontAsString,answer);
        }
        return answer;
    }

	public int getSize()
	{
		return getFont().getSize();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == this) return true;
		if (obj instanceof FlexoFont) {
			FlexoFont font = (FlexoFont)obj;
			return getFont().equals(font.getFont());
		}
		return super.equals(obj);
	}

	public Font getFont()
	{
		return _theFont;
	}

	public void setFont(Font aFont)
	{
		_theFont = aFont;
	}
	    

}
