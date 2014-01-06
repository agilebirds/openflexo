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
package org.openflexo.model.factory;

import java.lang.reflect.Type;

/**
 * Classes implementing this interface are intented to implement Key/Value coding.
 * 
 * This scheme allows introspection capabilities on such classes since they are handlable by basic accessors such as objectForKey(String),
 * setObjectForKey (Object,String)<br>
 * Key is a String identifying a key/value pair public methods such as getKey()/setKey()
 * 
 * 
 * @author sguerin
 * 
 */
public interface KeyValueCoding {

	/**
	 * Return boolean indicating if this object responses (has value for) to supplied key
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key);

	/**
	 * Return object matching supplied key, if this object responses to this key
	 * 
	 * @param key
	 * @return
	 */
	public Object objectForKey(String key);

	/**
	 * Sets an object matching supplied key, if this object responses to this key
	 * 
	 * @param key
	 * @return
	 */
	public void setObjectForKey(Object value, String key);

	/**
	 * Return type of key/value pair identified by supplied key identifier
	 * 
	 * @param key
	 * @return
	 */
	public Type getTypeForKey(String key);

}
