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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author gpolet Created on 28 sept. 2005
 */
public class FlexoProperties extends Properties implements Comparator<Object> {

	private boolean isStoring = false;

	/**
     * 
     */
	public FlexoProperties() {
		super();
	}

	public FlexoProperties(Hashtable<Object, Object> p) {
		super();// Ok, I don't know what Sun has done here but it seems that
				// Properties is the lamest class ever made
				// (not typed although it can only take String as keys and values,
				// and some kind of crappy and totally incoherent way of dealing with some default values).
		putAll(p);// This call is a lot safer than passing the hashtable in the super().
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Properties#store(java.io.OutputStream, java.lang.String)
	 */
	@Override
	public synchronized void store(OutputStream out, String header) throws IOException {
		isStoring = true;
		super.store(out, header);
		isStoring = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Properties#propertyNames()
	 */
	@Override
	public Enumeration<Object> keys() {
		if (isStoring) {
			return sortedKeys();
		} else {
			return super.keys();
		}
	}

	public Enumeration<Object> unsortedKeys() {
		return super.keys();
	}

	public Enumeration<Object> sortedKeys() {
		return new PropsEnum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		return o1.toString().compareTo(o2.toString());
	}

	private class PropsEnum implements Enumeration<Object> {

		private Object[] objects;

		private int index = 0;

		public PropsEnum() {
			index = 0;// Resets the enumeration
			Enumeration en = unsortedKeys();
			objects = new Object[size()];
			int i = 0;
			while (en.hasMoreElements()) {
				objects[i++] = en.nextElement();
			}
			Arrays.sort(objects, FlexoProperties.this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		@Override
		public boolean hasMoreElements() {
			return index < objects.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Enumeration#nextElement()
		 */
		@Override
		public Object nextElement() {
			return objects[index++];
		}

	}

	public boolean isStoring() {
		return isStoring;
	}

	public void setStoring(boolean isStoring) {
		this.isStoring = isStoring;
	}

}
