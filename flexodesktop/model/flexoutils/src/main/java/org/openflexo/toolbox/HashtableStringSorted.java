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
package org.openflexo.toolbox;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author gpolet
 * 
 */
public class HashtableStringSorted<V> extends Hashtable<String, V> {

	/**
	 * @param titles
	 */
	public HashtableStringSorted(Hashtable<String, V> titles) {
		super(titles);
	}

	/**
     * 
     */
	public HashtableStringSorted() {
		super();
	}

	/**
	 * Overrides keys
	 * 
	 * @see java.util.Hashtable#keys()
	 */
	@Override
	public synchronized Enumeration<String> keys() {
		Enumeration<String> en = super.keys();
		Vector<String> v = new Vector<String>();
		while (en.hasMoreElements()) {
			v.add(en.nextElement());
		}
		Collections.sort(v);
		return v.elements();
	}
}
