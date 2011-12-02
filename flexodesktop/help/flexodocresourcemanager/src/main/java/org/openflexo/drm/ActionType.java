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
package org.openflexo.drm;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;
import org.openflexo.xmlcode.XMLSerializable;

public enum ActionType implements XMLSerializable, StringConvertable {

	SUBMITTED("SUBMITTED"), APPROVED("APPROVED"), REFUSED("REFUSED"), REVIEWED("REVIEWED");

	private final String name;

	public String getName() {
		return name;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	ActionType(String _name) {
		this.name = _name;
	}

	@Override
	public Converter getConverter() {
		return actionTypeConverter;
	}

	public static StringEncoder.EnumerationConverter actionTypeConverter = new StringEncoder.EnumerationConverter(ActionType.class,
			"getName");

	/*
	
	private static final Logger logger = Logger.getLogger(ActionType.class.getPackage().getName());

	public static final ActionType SUBMITTED = new SubmittedActionType();

	public static final ActionType APPROVED = new ApprovedActionType();

	public static final ActionType REFUSED = new RefusedActionType();

	public static final ActionType REVIEWED = new ReviewedActionType();

	public static StringEncoder.Converter actionTypeConverter = new Converter(ActionType.class) {

	    public Object convertFromString(String value)
	    {
	        return get(value);
	    }

	    public String convertToString(Object value)
	    {
	        return ((ActionType) value).getName();
	    }

	};

	public static class SubmittedActionType extends ActionType
	{
	    SubmittedActionType(){}
	    
	    public String getName()
	    {
	        return "SUBMITTED";
	    }

	}

	public static class ApprovedActionType extends ActionType
	{
	    ApprovedActionType(){}
	    
	    public String getName()
	    {
	        return "APPROVED";
	    }

	}

	public static class RefusedActionType extends ActionType
	{
	    RefusedActionType(){}
	    
	    public String getName()
	    {
	        return "REFUSED";
	    }

	}

	public static class ReviewedActionType extends ActionType
	{
	    ReviewedActionType(){}
	    
	    public String getName()
	    {
	        return "REVIEWED";
	    }

	}

	public abstract String getName();

	public static ActionType get(String typeName)
	{
	    for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
	        ActionType temp = (ActionType) e.nextElement();
	        if (temp.getName().equals(typeName)) {
	            return temp;
	        }
	    }

	    if (logger.isLoggable(Level.WARNING))
	        logger.warning("Could not find ActionType named " + typeName);
	    return null;
	}

	private Vector _availableValues = null;

	public Vector getAvailableValues()
	{
	    if (_availableValues == null) {
	        _availableValues = new Vector();
	        _availableValues.add(SUBMITTED);
	        _availableValues.add(APPROVED);
	        _availableValues.add(REFUSED);
	        _availableValues.add(REVIEWED);
	               }
	    return _availableValues;
	}

	public StringEncoder.Converter getConverter()
	{
	    return actionTypeConverter;
	}

	public static Vector availableValues()
	{
	    return SUBMITTED.getAvailableValues();
	}

	/**
	 * @return
	 */
	/*
	public String getLocalizedName()
	{
	    return FlexoLocalization.localizedForKey(getName());
	}
	*/
}
