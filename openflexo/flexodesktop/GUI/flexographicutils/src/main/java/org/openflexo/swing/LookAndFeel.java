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
package org.openflexo.swing;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;


/**
 * @author gpolet
 *
 */
public class LookAndFeel extends KVCObject implements StringConvertable, ChoiceList
{

    private static final Vector<LookAndFeel> availableValues = new Vector<LookAndFeel>();

    public static final StringEncoder.Converter<LookAndFeel> lookAndFeelConverter = new Converter<LookAndFeel>(LookAndFeel.class) {

        @Override
		public LookAndFeel convertFromString(String value)
        {
            Enumeration<LookAndFeel> en = availableValues().elements();
            while (en.hasMoreElements()) {
                LookAndFeel laf = en.nextElement();
                if (laf.getClassName().equals(value))
                    return laf;
            }
            return getDefaultLookAndFeel();
        }

        @Override
		public String convertToString(LookAndFeel value)
        {
            return value.getClassName();
        }

    };

    public static LookAndFeel getDefaultLookAndFeel() {
    	for(LookAndFeel feel:availableValues()) {
    		if (feel.getClassName().equals(UIManager.getSystemLookAndFeelClassName()))
    			return feel;
    	}
    	return availableValues().firstElement();
    }

    public static Vector<LookAndFeel> availableValues()
    {
        if (availableValues.size()==0) {
            LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < lafs.length; i++) {
                LookAndFeelInfo feel = lafs[i];
                availableValues.add(new LookAndFeel(feel));
            }
        }
        return availableValues;
    }

    public static LookAndFeel get(String s)
    {
        return lookAndFeelConverter.convertFromString(s);
    }

    private LookAndFeelInfo info;

    /**
     *
     */
    public LookAndFeel(LookAndFeelInfo info)
    {
        this.info = info;
    }

    public String getClassName()
    {
        return info.getClassName();
    }

    public String getName()
    {
        return info.getName();
    }

    /**
     * Overrides getConverter
     * @see org.openflexo.xmlcode.StringConvertable#getConverter()
     */
    @Override
	public Converter getConverter()
    {
        return lookAndFeelConverter;
    }

    /**
     * Overrides getAvailableValues
     * @see org.openflexo.kvc.ChoiceList#getAvailableValues()
     */
    @Override
	public Vector getAvailableValues()
    {
        return availableValues;
    }

}
