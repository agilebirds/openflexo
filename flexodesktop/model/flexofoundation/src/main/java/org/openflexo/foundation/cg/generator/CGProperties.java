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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties used in the context of Code Generator
 * 
 * @author bmangez
 */
public class CGProperties implements Map {

	public CGProperties() {
		super();
		_properties = new Properties();
	}

	public CGProperties(CGProperties prop) {
		super();
		_properties = new Properties(prop._properties);
	}

	@Override
	public Object clone() {
		CGProperties answer = new CGProperties();
		answer._properties = (Properties) _properties.clone();
		return answer;
	}

	public void put(String key, String value) {
		if (value == null)
			_properties.remove(key);
		else
			_properties.put(key, value);
	}

	public Object get(String key) {
		return _properties.get(key);
	}

	public Enumeration keys() {
		return _properties.keys();
	}

	public Enumeration elements() {
		return _properties.elements();
	}

	@Override
	public void clear() {
		_properties.clear();

	}

	@Override
	public boolean containsKey(Object arg0) {
		return _properties.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return _properties.containsValue(arg0);
	}

	@Override
	public Set entrySet() {
		return _properties.entrySet();
	}

	@Override
	public Object get(Object arg0) {
		return _properties.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return _properties.isEmpty();
	}

	@Override
	public Set keySet() {
		return _properties.keySet();
	}

	@Override
	public Object put(Object arg0, Object arg1) {
		return _properties.put(arg0, arg1);
	}

	@Override
	public void putAll(Map arg0) {
		_properties.putAll(arg0);
	}

	@Override
	public Object remove(Object arg0) {
		return _properties.remove(arg0);
	}

	@Override
	public int size() {
		return _properties.size();
	}

	@Override
	public Collection values() {
		return _properties.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		Enumeration keys = keys();
		while (keys.hasMoreElements()) {
			Object o = keys.nextElement();
			sb.append(o + "=" + get(o));
			if (keys.hasMoreElements())
				sb.append(" , ");
		}
		sb.append("}");
		return sb.toString();
	}

	private Properties _properties;
}
