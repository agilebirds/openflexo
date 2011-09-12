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
 * Represents type of format for a TextField (NONE,URL,EMAIL,PHONE,BANK_ACCOUNT)
 * 
 * @author sguerin
 * 
 */
public enum TextFieldFormatType implements StringConvertable
{

	NONE("None"),
	URL("Url"),
	EMAIL("Email"),
	PHONE("Phone"),
	BANK_ACCOUNT("BankAccount")/*,
	AUTOCOMPLETE("AutoComplete")*/;
	
	
	private final String name;

	public String getName(){
		return name;
	}
	
	TextFieldFormatType(String _name){
		this.name = _name;
	}
	
	@Override
	public Converter getConverter() {
		return textFieldFormatTypeConverter;
	}
	
	public static final StringEncoder.EnumerationConverter<TextFieldFormatType> textFieldFormatTypeConverter = new StringEncoder.EnumerationConverter<TextFieldFormatType>(TextFieldFormatType.class,"getName");

	
	/*
	
	
    private static final Logger logger = Logger.getLogger(TextFieldFormatType.class.getPackage().getName());

    public static final TextFieldFormatType NONE = new NoneType();

    public static final TextFieldFormatType URL = new UrlType();

    public static final TextFieldFormatType EMAIL = new EmailType();

    public static final TextFieldFormatType PHONE = new PhoneType();

    public static final TextFieldFormatType BANK_ACCOUNT = new BankAccountType();

    public static final TextFieldFormatType AUTOCOMPLETE = new AutoCompleteType();

    public static StringEncoder.Converter textFieldFormatTypeConverter = new Converter(TextFieldFormatType.class) {

        public Object convertFromString(String value)
        {
            return get(value);
        }

        public String convertToString(Object value)
        {
            return ((TextFieldFormatType) value).getName();
        }

    };

    public static class NoneType extends TextFieldFormatType implements Serializable
    {
        protected NoneType()
        {
        }

        public String getName()
        {
            return "None";
        }
    }

    public static class UrlType extends TextFieldFormatType implements Serializable
    {
        protected UrlType()
        {
        }

        public String getName()
        {
            return "Url";
        }
    }

    public static class EmailType extends TextFieldFormatType implements Serializable
    {
        protected EmailType()
        {
        }

        public String getName()
        {
            return "Email";
        }
    }

    public static class PhoneType extends TextFieldFormatType implements Serializable
    {
        protected PhoneType()
        {
        }

        public String getName()
        {
            return "Phone";
        }
    }

    public static class BankAccountType extends TextFieldFormatType implements Serializable
    {
        
        protected BankAccountType()
        {
        }

        public String getName()
        {
            return "BankAccount";
        }
    }

    public static class AutoCompleteType extends TextFieldFormatType implements Serializable
    {
        protected AutoCompleteType()
        {
        }

        public String getName()
        {
            return "AutoComplete";
        }
    }

    public abstract String getName();

    public static TextFieldFormatType get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            TextFieldFormatType temp = (TextFieldFormatType) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find ListType named " + typeName);
        return null;
    }

    private Vector<TextFieldFormatType> _availableValues = null;

    public Vector getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<TextFieldFormatType>();
            _availableValues.add(NONE);
            _availableValues.add(URL);
            _availableValues.add(EMAIL);
            _availableValues.add(PHONE);
            _availableValues.add(BANK_ACCOUNT);
            _availableValues.add(AUTOCOMPLETE);
        }
        return _availableValues;
    }

    public StringEncoder.Converter getConverter()
    {
        return textFieldFormatTypeConverter;
    }

    public static Vector availableValues()
    {
        return NONE.getAvailableValues();
    }
*/
}
