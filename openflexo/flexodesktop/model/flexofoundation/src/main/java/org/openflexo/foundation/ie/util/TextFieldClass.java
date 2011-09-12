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
package org.openflexo.foundation.ie.util;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of a text css (FETCH,ACCESSOR)
 * 
 * @author gpolet
 * 
 */

public enum TextFieldClass implements StringConvertable
{

	DLNORMAL("DLNormal"),
	DLEXTENSIBLE("DLExtensible"),
	DLMEDIUM("DLMedium"),
	DLSHORT("DLShort"),
	DLTINY("DLTiny");
	
	
	private final String name;

	public String getName(){
		return name;
	}
	
	TextFieldClass(String _name){
		this.name = _name;
	}
	
	@Override
	public Converter getConverter() {
		return textFieldClassConverter;
	}
	
	public static final StringEncoder.EnumerationConverter<TextFieldClass> textFieldClassConverter = new StringEncoder.EnumerationConverter<TextFieldClass>(TextFieldClass.class,"getName");
	
	/*
    private static final Logger logger = Logger.getLogger(TextCSSClass.class.getPackage().getName());

    public static final TextFieldClass DLNORMAL = new DLNormal();

    public static final TextFieldClass DLEXTENSIBLE = new DLExtensible();

    public static final TextFieldClass DLMEDIUM = new DLMedium();

    public static final TextFieldClass DLSHORT = new DLShort();
    
    public static final TextFieldClass DLTINY = new DLTiny();

    public static StringEncoder.Converter textFieldClassConverter = new Converter(TextFieldClass.class) {

        public Object convertFromString(String value)
        {
            return get(value);
        }

        public String convertToString(Object value)
        {
            return ((TextFieldClass) value).getName();
        }

    };

    public static class DLNormal extends TextFieldClass implements Serializable
    {
        DLNormal()
        {
        }

        public String getName()
        {
            return "DLNormal";
        }
    }

    public static class DLExtensible extends TextFieldClass implements Serializable
    {
        DLExtensible()
        {
        }

        public String getName()
        {
            return "DLExtensible";
        }
    }

    public static class DLMedium extends TextFieldClass implements Serializable
    {
        DLMedium()
        {
        }

        public String getName()
        {
            return "DLMedium";
        }
    }

    public static class DLShort extends TextFieldClass implements Serializable
    {
        DLShort()
        {
        }

        public String getName()
        {
            return "DLShort";
        }
    }

    public static class DLTiny extends TextFieldClass implements Serializable
    {
        DLTiny()
        {
        }

        public String getName()
        {
            return "DLTiny";
        }
    }
    
	public Converter getConverter() {
		return textCSSClassConverter;
	}
	
	public static StringEncoder.EnumerationConverter textCSSClassConverter = new StringEncoder.EnumerationConverter(TextCSSClass.class,"getName");

	
    public abstract String getName();

    public static TextFieldClass get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            TextFieldClass temp = (TextFieldClass) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find TextFieldClass named " + typeName);
        return null;
    }

    private Vector<TextFieldClass> _availableValues = null;

    public Vector getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<TextFieldClass>();
            _availableValues.add(DLNORMAL);
            _availableValues.add(DLEXTENSIBLE);
            _availableValues.add(DLMEDIUM);
            _availableValues.add(DLSHORT);
            _availableValues.add(DLTINY);
        }
        return _availableValues;
    }

    public StringEncoder.Converter getConverter()
    {
        return textFieldClassConverter;
    }

    public static Vector availableValues()
    {
        return DLNORMAL.getAvailableValues();
    }
	*/
}
