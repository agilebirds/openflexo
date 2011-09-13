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
 * Represents type of a TR CSS (DL_LIST_ROW1,DL_LIST_ROW2)
 * 
 * @author sguerin
 * 
 */
public enum TRCSSType implements StringConvertable
{

	DL_LIST_ROW1("DLListRow1"),
	DL_LIST_ROW2("DLListRow2");
	
	
	private final String name;

	public String getName(){
		return name;
	}
	
	TRCSSType(String _name){
		this.name = _name;
	}
	
	@Override
	public Converter getConverter() {
		return trCSSTypeConverter;
	}
	
	public static final StringEncoder.EnumerationConverter<TRCSSType> trCSSTypeConverter = new StringEncoder.EnumerationConverter<TRCSSType>(TRCSSType.class,"getName");

	/*
	
    private static final Logger logger = Logger.getLogger(TRCSSType.class.getPackage().getName());

    public static final TRCSSType DL_LIST_ROW1 = new DLListRow1Type();

    public static final TRCSSType DL_LIST_ROW2 = new DLListRow2Type();

    public static StringEncoder.Converter trCSSTypeConverter = new Converter(TRCSSType.class) {

        public Object convertFromString(String value)
        {
            return get(value);
        }

        public String convertToString(Object value)
        {
            return ((TRCSSType) value).getName();
        }

    };

    public static class DLListRow1Type extends TRCSSType implements Serializable
    {
        DLListRow1Type()
        {
        }

        public String getName()
        {
            return "DLListRow1";
        }
    }

    public static class DLListRow2Type extends TRCSSType implements Serializable
    {
        DLListRow2Type()
        {
        }

        public String getName()
        {
            return "DLListRow2";
        }
    }

    public abstract String getName();

    public static TRCSSType get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            TRCSSType temp = (TRCSSType) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find ListType named " + typeName);
        return null;
    }

    private Vector<TRCSSType> _availableValues = null;

    public Vector getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<TRCSSType>();
            _availableValues.add(DL_LIST_ROW1);
            _availableValues.add(DL_LIST_ROW2);
        }
        return _availableValues;
    }

    public StringEncoder.Converter getConverter()
    {
        return trCSSTypeConverter;
    }

    public static Vector availableValues()
    {
        return DL_LIST_ROW1.getAvailableValues();
    }
	*/
}
