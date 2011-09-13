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
package org.openflexo.foundation;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;

/**
 * Represents type of generation target (proto,hc-wo,engine)
 * 
 * @author sguerin
 * @deprecated
 * 
 */
@Deprecated
public abstract class CodeType extends TargetType implements StringConvertable, ChoiceList, Serializable
{

    public CodeType(FlexoProject project) {
		super(project);
	}

	@Override
    public String getFullyQualifiedName()
    {
        return "CODE_TYPE."+getName();
    }

    @Override
    public FlexoProject getProject()
    {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Don't do that.");
        return null;
    }

    @Override
    public XMLMapping getXMLMapping()
    {
        return null;
    }

    @Override
    public XMLStorageResourceData getXMLResourceData()
    {
        return null;
    }

    private static final Logger logger = Logger.getLogger(CodeType.class.getPackage().getName());

    public static final CodeType PROTOTYPE = new Prototype();

    public static final CodeType HC_WO_APPLICATION = new HCWOApplication();

    public static final CodeType ENGINE_BASED_APPLICATION = new EngineBasedApplication();

    public static final CodeType BPEL = new BPELApplication();

    public static final StringEncoder.Converter<CodeType> codeTypeConverter = new Converter<CodeType>(CodeType.class) {

        @Override
		public CodeType convertFromString(String value)
        {
            return get(value);
        }

        @Override
		public String convertToString(CodeType value)
        {
            return value.getName();
        }

    };

    public static class Prototype extends CodeType
    {
    	
        private Vector<Format> availableFormats;

        protected Prototype()
        {
        	super(null);
            availableFormats = new Vector<Format>();
            availableFormats.add(Format.WEBOBJECTS);
        }

        @Override
		public String getName()
        {
            return "PROTOTYPE";
        }

        /**
         * Overrides getTemplateFolderName
         * 
         * @see org.openflexo.foundation.CodeType#getTemplateFolderName()
         */
        @Override
		public String getTemplateFolderName()
        {
            return "Prototype";
        }
        
        @Override
        public Vector<Format> getAvailableFormats() {
        	return availableFormats;
        }
    }

    public static class HCWOApplication extends CodeType
    {
        private Vector<Format> availableFormats;

        protected HCWOApplication()
        {
        	super(null);
            availableFormats = new Vector<Format>();
            availableFormats.add(Format.WEBOBJECTS);
            availableFormats.add(Format.BPEL);
        }

        @Override
		public String getName()
        {
            return "HC_WO_APPLICATION";
        }

        /**
         * Overrides getTemplateFolderName
         * 
         * @see org.openflexo.foundation.CodeType#getTemplateFolderName()
         */
        @Override
		public String getTemplateFolderName()
        {
            return "HCWOApplication";
        }

        @Override
        public Vector<Format> getAvailableFormats() {
        	return availableFormats;
        }
    }

    public static class EngineBasedApplication extends CodeType
    {
        private Vector<Format> availableFormats;

        protected EngineBasedApplication()
        {
        	super(null);
            availableFormats = new Vector<Format>();
            availableFormats.add(Format.WEBOBJECTS);
        }

        @Override
		public String getName()
        {
            return "ENGINE_BASED_APPLICATION";
        }

        /**
         * Overrides getTemplateFolderName
         * 
         * @see org.openflexo.foundation.CodeType#getTemplateFolderName()
         */
        @Override
		public String getTemplateFolderName()
        {
            return "WOApplication";
        }
        
        @Override
        public Vector<Format> getAvailableFormats() {
        	return availableFormats;
        }
    }

    @Deprecated
    public static class BPELApplication extends CodeType
    {
        private Vector<Format> availableFormats;

        protected BPELApplication()
        {
        	super(null);
            availableFormats = new Vector<Format>();
            availableFormats.add(Format.BPEL);
        }

        @Override
		public String getName()
        {
            return "BPEL";
        }

        /**
         * Overrides getTemplateFolderName
         * 
         * @see org.openflexo.foundation.CodeType#getTemplateFolderName()
         */
        @Override
		public String getTemplateFolderName()
        {
            return "BPEL";
        }
        
        @Override
        public Vector<Format> getAvailableFormats() {
        	return availableFormats;
        }
    }

   public static CodeType get(String typeName)
    {
        for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
            CodeType temp = (CodeType) e.nextElement();
            if (temp.getName().equals(typeName)) {
                return temp;
            }
        }

        if (logger.isLoggable(Level.WARNING))
            logger.warning("Could not find TargetType named " + typeName);
        return null;
    }

    private Vector<CodeType> _availableValues = null;

    @Override
	public Vector<CodeType> getAvailableValues()
    {
        if (_availableValues == null) {
            _availableValues = new Vector<CodeType>();
            _availableValues.add(PROTOTYPE);
            _availableValues.add(HC_WO_APPLICATION);
            _availableValues.add(ENGINE_BASED_APPLICATION);
            _availableValues.add(BPEL);
        }
        return _availableValues;
    }

    @Override
	public StringEncoder.Converter getConverter()
    {
        return codeTypeConverter;
    }

    public static Vector<CodeType> availableValues()
    {
        return PROTOTYPE.getAvailableValues();
    }

    /**
     * @return
     */
    @Override
	public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(getName());
    }

    public boolean isPrototype()
    {
        return this == PROTOTYPE;
    }
    
}
