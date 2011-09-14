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
package org.openflexo.inspector.widget;

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
public class WidgetFont extends KVCObject implements StringConvertable
{

    private static final Logger logger = Logger.getLogger(WidgetFont.class.getPackage().getName());

    private Font _theFont;
    public Font getTheFont(){
		return _theFont;
	}
    
    public static final StringEncoder.Converter<WidgetFont> flexoFontConverter = new Converter<WidgetFont>(WidgetFont.class) {
    	
        @Override
		public WidgetFont convertFromString(String value)
        {
            return stringToFont(value);
        }

        @Override
		public String convertToString(WidgetFont value)
        {
            return value.toString();
        }

    };

    public WidgetFont(String s)
    {
        this(nameFromString(s), styleFromString(s), sizeFromString(s));
    }

    public WidgetFont(String name, int style, int size)
    {
        super();
        _theFont = new Font(name,style,size);
    }

    public WidgetFont(Font aFont)
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
    
    public static WidgetFont stringToFont(String s)
    {
        return new WidgetFont(s);
    }

    private static String nameFromString(String s)
    {
        return s.substring(0, s.indexOf(","));
    }

    private static int styleFromString(String s)
    {
    	String parseMe = s.substring(s.indexOf(",") + 1, s.lastIndexOf(","));
    	if (parseMe.equalsIgnoreCase("plain")) return Font.PLAIN;
    	if (parseMe.equalsIgnoreCase("bold")) return Font.BOLD;
    	if (parseMe.equalsIgnoreCase("italic")) return Font.ITALIC;
    	if (parseMe.equalsIgnoreCase("bold+italic")) return Font.BOLD+Font.ITALIC;
    	try {
    		return Integer.parseInt(parseMe);
    	}
    	catch (NumberFormatException e) {
    		// OK, give up	
    	}
    	return Font.PLAIN;
    }

    private static int sizeFromString(String s)
    {
        return Integer.parseInt(s.substring(s.lastIndexOf(",") + 1));
    }

    private static final Hashtable<String,WidgetFont> fontHashtable=new Hashtable<String, WidgetFont>();
    
    public static WidgetFont get(String fontAsString)
    {
        if (fontAsString == null) {
            return null;
        }
        WidgetFont answer = fontHashtable.get(fontAsString);
		if (answer == null) {
			answer = new WidgetFont(fontAsString);
			fontHashtable.put(fontAsString, answer);
		}
		return answer;
	}

	public static void updateGUIBrowser() {
		// TODO Auto-generated method stub
		logger.warning("Immediate update of browser font not implemented yet !");
	}


}
