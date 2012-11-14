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

import java.awt.Font;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/*
import org.openflexo.foundation.FlexoObject;
import org.openflexo.kvc.ChoiceList;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import java.awt.Font;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
*/
/**
 * Represents type of a text css (FETCH,ACCESSOR)
 * 
 * @author sguerin
 * 
 */

public enum TextCSSClass implements StringConvertable/*extends FlexoObject implements StringConvertable, ChoiceList, Serializable*/
{

	BLOC_BODY_CONTENT("DLBlockBodyContent", new Font("SansSerif", Font.PLAIN, 10)), BLOC_BODY_TITLE("DLBlockBodyTitle", new Font(
			"SansSerif", Font.BOLD, 11)), BLOC_BODY_COMMENT("DLBlockBodyComment", new Font("SansSerif", Font.PLAIN, 9)), BLOC_BODY_EXTRA(
			"DLBlockBodyExtra", new Font("SansSerif", Font.ITALIC, 10));

	private final String name;
	private final Font font;

	public String getName() {
		return name;
	}

	TextCSSClass(String _name, Font _font) {
		this.name = _name;
		this.font = _font;
	}

	public Font font() {
		return font;
	}

	@Override
	public Converter<TextCSSClass> getConverter() {
		return textCSSClassConverter;
	}

	public static final StringEncoder.EnumerationConverter<TextCSSClass> textCSSClassConverter = new StringEncoder.EnumerationConverter<TextCSSClass>(
			TextCSSClass.class, "getName");

	/*
	private static final Logger logger = Logger.getLogger(TextCSSClass.class.getPackage().getName());
	
	public static final TextCSSClass BLOC_BODY_CONTENT = new BlocBodyContent();

	public static final TextCSSClass BLOC_BODY_TITLE = new BlocBodyTitle();

	public static final TextCSSClass BLOC_BODY_COMMENT = new BlocBodyComment();

	public static final TextCSSClass BLOC_BODY_EXTRA = new BlocBodyExtra();

	public static StringEncoder.Converter textCSSClassConverter = new Converter(TextCSSClass.class) {

	    public Object convertFromString(String value)
	    {
	        return get(value);
	    }

	    public String convertToString(Object value)
	    {
	        return ((TextCSSClass) value).getName();
	    }

	};

	public static class BlocBodyContent extends TextCSSClass implements Serializable
	{
	    BlocBodyContent()
	    {
	    }

	    public String getName()
	    {
	        return "DLBlockBodyContent";
	    }
	}

	public static class BlocBodyTitle extends TextCSSClass implements Serializable
	{
	    BlocBodyTitle()
	    {
	    }

	    public String getName()
	    {
	        return "DLBlockBodyTitle";
	    }
	}

	public static class BlocBodyComment extends TextCSSClass implements Serializable
	{
	    BlocBodyComment()
	    {
	    }

	    public String getName()
	    {
	        return "DLBlockBodyComment";
	    }
	}

	public static class BlocBodyExtra extends TextCSSClass implements Serializable
	{
	    BlocBodyExtra()
	    {
	    }

	    public String getName()
	    {
	        return "DLBlockBodyExtra";
	    }
	}

	public abstract String getName();

	public static TextCSSClass get(String typeName)
	{
	    for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
	        TextCSSClass temp = (TextCSSClass) e.nextElement();
	        if (temp.getName().equals(typeName)) {
	            return temp;
	        }
	    }

	    if (logger.isLoggable(Level.WARNING))
	        logger.warning("Could not find TextCSSClass named " + typeName);
	    return null;
	}

	private Vector<TextCSSClass> _availableValues = null;

	public Vector<TextCSSClass> getAvailableValues()
	{
	    if (_availableValues == null) {
	        _availableValues = new Vector<TextCSSClass>();
	        _availableValues.add(BLOC_BODY_CONTENT);
	        _availableValues.add(BLOC_BODY_TITLE);
	        _availableValues.add(BLOC_BODY_COMMENT);
	        _availableValues.add(BLOC_BODY_EXTRA);
	    }
	    return _availableValues;
	}

	public StringEncoder.Converter getConverter()
	{
	    return textCSSClassConverter;
	}

	public static Vector availableValues()
	{
	    return BLOC_BODY_CONTENT.getAvailableValues();
	}

	
	public Font font()
	{
	    return fontForCssClass(this);
	}
	
	protected static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 10);

	public static final Font WOSTRING_FONT = new Font("SansSerif", Font.PLAIN, 10);

	public static final Font COMMENT_FONT = new Font("SansSerif", Font.PLAIN, 9);

	public static final Font EXTRA_FONT = new Font("SansSerif", Font.ITALIC, 10);

	public static final Font LABEL_BOLD_FONT = new Font("SansSerif", Font.BOLD, 11);
	
	private static Font fontForCssClass(TextCSSClass textCSSClass)
	{
	    if (textCSSClass == null)
	        return LABEL_FONT;
	    if (textCSSClass == TextCSSClass.BLOC_BODY_TITLE)
	        return LABEL_FONT;
	    if (textCSSClass == TextCSSClass.BLOC_BODY_CONTENT)
	        return WOSTRING_FONT;
	    if (textCSSClass == TextCSSClass.BLOC_BODY_COMMENT)
	        return COMMENT_FONT;
	    if (textCSSClass == TextCSSClass.BLOC_BODY_EXTRA)
	        return EXTRA_FONT;
	    return LABEL_FONT;
	}
	
	
	*/

}
