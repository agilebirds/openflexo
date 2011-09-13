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
package org.openflexo.foundation.cg.generator;

import java.util.Hashtable;

/**
 * Encodes the result of a code generation
 * 
 * @author bmangez
 */

public abstract class GeneratedCodeResult extends Hashtable<String, String>
{
	public static String DEFAULT_KEY = "DEFAULT";
	private String _name;
	
    public GeneratedCodeResult(String name)
    {
        super();
        _name = name;
    }

    protected void setDefaultCode(String value)
    {
    	addCode(DEFAULT_KEY, value);
    }
    
    protected void addCode(String key, String value)
    {
        if (value != null) {
            put(key, value);
        } else {
            remove(key);
        }
    }

    public String name()
    {
        return _name;
    }
    
    public String defaultCode()
    {
    	return get(DEFAULT_KEY);
    }
}
