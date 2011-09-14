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

import org.openflexo.localization.FlexoLocalization;

public enum HyperlinkActionType implements StringConvertable
{
	
	FLEXO_ACTION("FLEXO_ACTION","flexo_action"),
	DISPLAY_ACTION("DISPLAY_ACTION","display_action");	
	
	private final String name;
	private final String localizedKey;

	public String getName(){
		return name;
	}
	public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(localizedKey);
    }
	HyperlinkActionType(String _name, String _locKey){
		this.name = _name;
		this.localizedKey = _locKey;
	}
	
	@Override
	public Converter getConverter() {
		return hyperlinkActionTypeConverter;
	}
	
	public static final StringEncoder.EnumerationConverter<HyperlinkActionType> hyperlinkActionTypeConverter = new StringEncoder.EnumerationConverter<HyperlinkActionType>(HyperlinkActionType.class,"getName");

/*
    private static final Logger logger = FlexoLogger.getLogger(HyperlinkActionType.class.getPackage().getName());
 
    public static final HyperlinkActionType FLEXO_ACTION = new FlexoActionHyperlinkActionType();
    
    public static final HyperlinkActionType DISPLAY_ACTION = new DisplayActionHyperlinkActionType();
    
    public abstract String getName();
    
    public abstract String getLocalizedName();
    
    private final Vector<HyperlinkActionType> _availableValues = new Vector<HyperlinkActionType>();
    
    public static HyperlinkActionType get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            HyperlinkActionType temp = (HyperlinkActionType) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find HyperlinkType named " + typeName);
        return null;
    }

    public static StringEncoder.Converter hyperlinkActionTypeConverter = new Converter(HyperlinkActionType.class) {

        public Object convertFromString(String value)
        {
            return get(value);
        }

        public String convertToString(Object value)
        {
            return ((HyperlinkActionType) value).getName();
        }

    };
    
    public static class FlexoActionHyperlinkActionType extends HyperlinkActionType{

        FlexoActionHyperlinkActionType(){}
        
        public String getName()
        {
            return "FLEXO_ACTION";
        }

        public String getLocalizedName()
        {
            return FlexoLocalization.localizedForKey("flexo_action");
        }
        
    }
    
    public static class DisplayActionHyperlinkActionType extends HyperlinkActionType{

        DisplayActionHyperlinkActionType(){}
        
        public String getName()
        {
            return "DISPLAY_ACTION";
        }

        public String getLocalizedName()
        {
            return FlexoLocalization.localizedForKey("display_action");
        }
        
    }
    
    public static Vector availableValues()
    {
        return FLEXO_ACTION.getAvailableValues();
    }
    
    public Vector getAvailableValues()
    {
        if (_availableValues.size()==0){
            _availableValues.add(FLEXO_ACTION);
            _availableValues.add(DISPLAY_ACTION);
        }
        return _availableValues;
    }
    
    public Converter getConverter()
    {
        return hyperlinkActionTypeConverter;
    }
    */
}
