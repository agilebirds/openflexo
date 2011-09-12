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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public abstract class FolderType implements StringConvertable
{
    private static final Logger logger = FlexoLogger.getLogger(FolderType.class.getPackage().getName());
    
    public static final FolderType TAB_FOLDER = new TabFolder();
    
    public static final FolderType POPUP_FOLDER = new PopupFolder();
    
    public static final FolderType PARTIAL_COMPONENT_FOLDER =  new PartialComponentFolder();
    
    public static final FolderType DATA_SCREEN_FOLDER =  new DataScreenFolder();
    
    public static final FolderType MONITORING_SCREEN_FOLDER =  new MonitoringScreenFolder();

    public static final FolderType MONITORING_COMPONENT_FOLDER =  new MonitoringComponentFolder();
    
    public abstract String getName();
    
    private static final Vector<FolderType> _availableValues = new Vector<FolderType>();

    public static StringEncoder.Converter<FolderType> folderTypeConverter = new Converter<FolderType>(FolderType.class) {

        @Override
		public FolderType convertFromString(String value)
        {
            return get(value);
        }

        @Override
		public String convertToString(FolderType value)
        {
            return value.getName();
        }

    };
    
    /**
     * Overrides getConverter
     * @see org.openflexo.xmlcode.StringConvertable#getConverter()
     */
    @Override
	public Converter<FolderType> getConverter()
    {
        return folderTypeConverter;
    }

    public static FolderType get(String s) {
        for (int i = 0; i < availableValues().size(); i++) {
            FolderType f = (FolderType) availableValues().get(i);
            if (f.getName().equals(s))
                return f;
        }
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find corresponding folder type for "+s);
        return null;
    }
    
    public static Vector availableValues()
    {
        return TAB_FOLDER.getAvailableValues();
    }
    
    public Vector getAvailableValues()
    {
        if (_availableValues.size()==0) {
            _availableValues.add(POPUP_FOLDER);
            _availableValues.add(TAB_FOLDER);
            _availableValues.add(DATA_SCREEN_FOLDER);
            _availableValues.add(MONITORING_SCREEN_FOLDER);
            _availableValues.add(PARTIAL_COMPONENT_FOLDER);
            _availableValues.add(MONITORING_COMPONENT_FOLDER);
        }
        return _availableValues;
    }
    
    static class TabFolder extends FolderType {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.ie.util.FolderType#getName()
         */
        @Override
		public String getName()
        {
            return "Tab folder";
        }
        
    }
    
    static class PopupFolder extends FolderType {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.ie.util.FolderType#getName()
         */
        @Override
		public String getName()
        {
            return "Popup folder";
        }
        
    }
    
    static class PartialComponentFolder extends FolderType {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.ie.util.FolderType#getName()
         */
        @Override
		public String getName()
        {
            return "Partial component folder";
        }
        
    }
    
    static class MonitoringScreenFolder extends FolderType {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.ie.util.FolderType#getName()
         */
        @Override
		public String getName()
        {
            return "Monitoring screen folder";
        }
        
    }
    
    static class DataScreenFolder extends FolderType {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.ie.util.FolderType#getName()
         */
        @Override
		public String getName()
        {
            return "Data screen folder";
        }
        
    }
    
    static class MonitoringComponentFolder extends FolderType {

        /**
         * Overrides getName
         * @see org.openflexo.foundation.ie.util.FolderType#getName()
         */
        @Override
		public String getName()
        {
            return "Monitoring component folder";
        }
        
    }
}
