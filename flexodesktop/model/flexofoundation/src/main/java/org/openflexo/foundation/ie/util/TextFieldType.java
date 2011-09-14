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
 * Represents type of a TextField (TEXT,DATE,INTEGER,FLOAT,KEYVALUE)
 * 
 * @author sguerin
 * 
 */
public enum TextFieldType implements StringConvertable
{

	TEXT("Text"),
	DATE("Date"),
	INTEGER("Integer"),
	FLOAT("Float"),
	DOUBLE("Double"),
	KEYVALUE("KeyValue"),
	STATUS_LIST("StatusList");
	
	
	private final String name;

	public String getName(){
		return name;
	}
	
	TextFieldType(String _name){
		this.name = _name;
	}
	
	@Override
	public Converter getConverter() {
		return textFieldTypeConverter;
	}
	
	public static final StringEncoder.EnumerationConverter<TextFieldType> textFieldTypeConverter = new StringEncoder.EnumerationConverter<TextFieldType>(TextFieldType.class,"getName");

	
	/*
	
    private static final Logger logger = Logger.getLogger(TextFieldType.class.getPackage().getName());

    public static final TextFieldType TEXT = new TextType();

    public static final TextFieldType DATE = new DateType();

    public static final TextFieldType INTEGER = new IntegerType();

    public static final TextFieldType FLOAT = new FloatType();
    
    public static final TextFieldType DOUBLE = new DoubleType();

    public static final TextFieldType KEYVALUE = new KeyValueType();
    
    public static final TextFieldType STATUS_LIST = new StatusListType();

    public static final StringEncoder.Converter textFieldTypeConverter = new Converter(TextFieldType.class) {

        public Object convertFromString(String value)
        {
            return get(value);
        }

        public String convertToString(Object value)
        {
            return ((TextFieldType) value).getName();
        }

    };

    public static class TextType extends TextFieldType implements Serializable
    {
        protected TextType()
        {
        }

        public String getName()
        {
            return "Text";
        }
    }

    public static class DateType extends TextFieldType implements Serializable
    {
        protected DateType()
        {
        }

        public String getName()
        {
            return "Date";
        }
    }

    public static class IntegerType extends TextFieldType implements Serializable
    {
        protected IntegerType()
        {
        }

        public String getName()
        {
            return "Integer";
        }
    }

    public static class FloatType extends TextFieldType implements Serializable
    {
        protected FloatType()
        {
        }

        public String getName()
        {
            return "Float";
        }
    }

    public static class DoubleType extends TextFieldType implements Serializable
    {
        protected DoubleType()
        {
        }

        public String getName()
        {
            return "Double";
        }
    }

    public static class KeyValueType extends TextFieldType implements Serializable
    {
        protected KeyValueType()
        {
        }

        public String getName()
        {
            return "KeyValue";
        }
    }
    
    public static class StatusListType extends TextFieldType implements Serializable
    {
        protected StatusListType()
        {
        }

        public String getName()
        {
            return "StatusList";
        }
    }

    public abstract String getName();

    public static TextFieldType get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            TextFieldType temp = (TextFieldType) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find ListType named " + typeName);
        return null;
    }

    private static final Vector<TextFieldType> _availableValues = new Vector<TextFieldType>();
    private static final Vector<TextFieldType> _availableValuesForTextField = new Vector<TextFieldType>();

    public Vector<TextFieldType> getAvailableValues()
    {
        if (_availableValues.size()==0) {
            _availableValues.add(TEXT);
            _availableValues.add(DATE);
            _availableValues.add(INTEGER);
            _availableValues.add(FLOAT);
            _availableValues.add(DOUBLE);
            _availableValues.add(KEYVALUE);
            _availableValues.add(STATUS_LIST);
        }
        return _availableValues;
    }

    public StringEncoder.Converter getConverter()
    {
        return textFieldTypeConverter;
    }

    public static Vector availableValues()
    {
        return TEXT.getAvailableValues();
    }

	public static Vector availableFieldTypeForTextField() {
		if (_availableValuesForTextField.size()==0) {
			_availableValuesForTextField.add(TEXT);
			_availableValuesForTextField.add(DATE);
			_availableValuesForTextField.add(INTEGER);
			_availableValuesForTextField.add(FLOAT);
            _availableValuesForTextField.add(DOUBLE);
        }
        return _availableValuesForTextField;
	}
	*/
}
