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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;


/**
 * @author gpolet
 *
 */
public abstract class FlexoDocFormat extends FlexoObject implements StringConvertable, ChoiceList
{
    public static final StringEncoder.Converter<FlexoDocFormat> flexoDocFormatConverter = new Converter<FlexoDocFormat>(FlexoDocFormat.class) {

        @Override
		public FlexoDocFormat convertFromString(String value)
        {
            return get(value);
        }

        @Override
		public String convertToString(FlexoDocFormat value)
        {
            return value.getName();
        }

    };
    
    public static FlexoDocFormat get(String s) {
        Enumeration<FlexoDocFormat> en = availableValues().elements();
        while (en.hasMoreElements()) {
            FlexoDocFormat df = en.nextElement();
            if (df.getName().equals(s))
                return df;
        }
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find Doc format named '"+s+"'");
        return null;
    }
    
    /**
     * @author gpolet
     *
     */
    public static class LatexFormat extends FlexoDocFormat
    {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.utils.FlexoDocFormat#getName()
         */
        @Override
		public String getName()
        {
            return "LaTeX";
        }

        /**
         * Overrides getConverter
         * @see org.openflexo.xmlcode.StringConvertable#getConverter()
         */
        @Override
		public Converter getConverter()
        {
            return flexoDocFormatConverter;
        }

    }

    /**
     * @author gpolet
     *
     */
    public static class PlainTextFormat extends FlexoDocFormat
    {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.utils.FlexoDocFormat#getName()
         */
        @Override
		public String getName()
        {
            return "Plain text";
        }
        
        /**
         * Overrides getConverter
         * @see org.openflexo.xmlcode.StringConvertable#getConverter()
         */
        @Override
		public Converter getConverter()
        {
            return flexoDocFormatConverter;
        }

    }

    /**
     * @author gpolet
     *
     */
    public static class HTMLFormat extends FlexoDocFormat
    {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.utils.FlexoDocFormat#getName()
         */
        @Override
		public String getName()
        {
            return "HTML";
        }

        /**
         * Overrides getConverter
         * @see org.openflexo.xmlcode.StringConvertable#getConverter()
         */
        @Override
		public Converter getConverter()
        {
            return flexoDocFormatConverter;
        }

    }
    
    @Override
	public Vector<FlexoDocFormat> getAvailableValues() {
        if (availableValues==null) {
            availableValues = new Vector<FlexoDocFormat>();
            availableValues.add(PLAIN_TEXT);
            availableValues.add(LATEX);
            availableValues.add(HTML);
        }
        return availableValues;
    }
    
    public static Vector<FlexoDocFormat> availableValues() {
        return PLAIN_TEXT.getAvailableValues();
    }
    
    public abstract String getName();
    
    private static final Logger logger = Logger.getLogger(FlexoDocFormat.class.getPackage().getName());
    
    private Vector<FlexoDocFormat> availableValues;
    
    public static final FlexoDocFormat PLAIN_TEXT = new PlainTextFormat();
    
    public static final FlexoDocFormat LATEX = new LatexFormat();
    
    public static final FlexoDocFormat HTML = new HTMLFormat();
}
