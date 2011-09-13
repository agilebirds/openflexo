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
package org.openflexo.foundation.dm;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.FlexoObject;

/**
 * Represents the implementation type of a property
 * 
 * @author sguerin
 * 
 */
public abstract class DMPropertyImplementationType extends FlexoObject implements StringConvertable, ChoiceList, Serializable
{

    private static final Logger logger = Logger.getLogger(DMPropertyImplementationType.class.getPackage().getName());

    public static final DMPropertyImplementationType PUBLIC_ACCESSORS_PRIVATE_FIELD = new PublicAccessorsPrivateField();
    public static final DMPropertyImplementationType PUBLIC_ACCESSORS_PROTECTED_FIELD = new PublicAccessorsProtectedField();
    public static final DMPropertyImplementationType PUBLIC_ACCESSORS_ONLY = new PublicAccessorsOnly();
    public static final DMPropertyImplementationType PUBLIC_FIELD = new PublicField();
    public static final DMPropertyImplementationType PROTECTED_FIELD = new ProtectedField();
    public static final DMPropertyImplementationType PUBLIC_STATIC_FINAL_FIELD = new PublicStaticFinalField();

    public static final StringEncoder.Converter<DMPropertyImplementationType> propertyImplementationTypeConverter = new Converter<DMPropertyImplementationType>(DMPropertyImplementationType.class) {

        @Override
		public DMPropertyImplementationType convertFromString(String value)
        {
            return get(value);
        }

        @Override
		public String convertToString(DMPropertyImplementationType value)
        {
            return value.getName();
        }

    };

    static class PublicAccessorsPrivateField extends DMPropertyImplementationType
    {
        @Override
		public String getName()
        {
            return "PUBLIC_ACCESSORS_PRIVATE_FIELD";
        }

		@Override
		public boolean requiresField() 
		{
			return true;
		}
		
		@Override
		public boolean requiresAccessors() 
		{
			return true;
		}

    }

    static class PublicAccessorsProtectedField extends DMPropertyImplementationType
    {
        @Override
		public String getName()
        {
            return "PUBLIC_ACCESSORS_PROTECTED_FIELD";
        }

		@Override
		public boolean requiresField() 
		{
			return true;
		}
		
		@Override
		public boolean requiresAccessors() 
		{
			return true;
		}

    }

    static class PublicAccessorsOnly extends DMPropertyImplementationType
    {
        @Override
		public String getName()
        {
            return "PUBLIC_ACCESSORS_ONLY";
        }

		@Override
		public boolean requiresField() 
		{
			return false;
		}
		
		@Override
		public boolean requiresAccessors() 
		{
			return true;
		}

    }

    static class PublicField extends DMPropertyImplementationType
    {
        @Override
		public String getName()
        {
            return "PUBLIC_FIELD";
        }

		@Override
		public boolean requiresField() 
		{
			return true;
		}
		
		@Override
		public boolean requiresAccessors() 
		{
			return false;
		}

    }

    static class ProtectedField extends DMPropertyImplementationType
    {
        @Override
		public String getName()
        {
            return "PROTECTED_FIELD";
        }

		@Override
		public boolean requiresField() 
		{
			return true;
		}

		@Override
		public boolean requiresAccessors() 
		{
			return false;
		}
    }
    
    static class PublicStaticFinalField extends DMPropertyImplementationType
    {
        @Override
		public String getName()
        {
            return "PUBLIC_STATIC_FINAL_FIELD";
        }

		@Override
		public boolean requiresField() 
		{
			return true;
		}

		@Override
		public boolean requiresAccessors() 
		{
			return false;
		}
    }
    
    public abstract boolean requiresField();
    
    public abstract boolean requiresAccessors();
    
     public abstract String getName();

    public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(getName());
    }

    public static DMPropertyImplementationType get(String cardName)
    {
        for (Enumeration e = PUBLIC_ACCESSORS_PRIVATE_FIELD.getAvailableValues().elements(); e.hasMoreElements();) {
            DMPropertyImplementationType temp = (DMPropertyImplementationType) e.nextElement();
            if (temp.getName().equals(cardName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find ImplementationType named " + cardName);
        return null;
    }

    private Vector<DMPropertyImplementationType> _availableValues = null;

    @Override
	public Vector getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<DMPropertyImplementationType>();
            _availableValues.add(PUBLIC_ACCESSORS_PRIVATE_FIELD);
            _availableValues.add(PUBLIC_ACCESSORS_PROTECTED_FIELD);
            _availableValues.add(PUBLIC_ACCESSORS_ONLY);
            _availableValues.add(PUBLIC_FIELD);
            _availableValues.add(PROTECTED_FIELD);
            _availableValues.add(PUBLIC_STATIC_FINAL_FIELD);
        }
        return _availableValues;
    }

    @Override
	public StringEncoder.Converter getConverter()
    {
        return propertyImplementationTypeConverter;
    }

    public static Vector availableValues()
    {
        return PUBLIC_ACCESSORS_PRIVATE_FIELD.getAvailableValues();
    }

}
