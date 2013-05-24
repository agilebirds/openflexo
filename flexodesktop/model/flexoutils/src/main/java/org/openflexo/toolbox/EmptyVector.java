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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

;

/**
 * The purpose of this class is to have an empty vector an re-use wherever we only want to have an empty vector that does not change over
 * time and avoid taking memory. If a programmer calls add by mistake on this class, at least it will have no impact on the rest of the
 * application.
 * 
 * @author gpolet
 * 
 */
public class EmptyVector<T> extends Vector<T> {

	private static final Hashtable<Class, EmptyVector> values = new Hashtable<Class, EmptyVector>();

	public static final Vector EMPTY_VECTOR = new EmptyVector();

	@SuppressWarnings("unchecked")
	public static <T> Vector<T> EMPTY_VECTOR() {
		return EMPTY_VECTOR;
	}

	public static <T> Vector<T> EMPTY_VECTOR(Class<T> c) {
		if (values.get(c) == null) {
			values.put(c, new EmptyVector<T>());
		}
		return values.get(c);
	}

	/**
	 * Overrides add
	 * 
	 * @see java.util.Vector#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, Object element) {
		System.err.println("Called add(int index, Object element) on EmptyVector. This is probably a mistake.");
	}

	/**
	 * Overrides add
	 * 
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	@Override
	public synchronized boolean add(Object o) {
		System.err.println("Called add(Object o) on EmptyVector. This is probably a mistake.");
		return false;
	}

	/**
	 * Overrides addAll
	 * 
	 * @see java.util.Vector#addAll(java.util.Collection)
	 */
	@Override
	public synchronized boolean addAll(Collection c) {
		System.err.println("Called addAll(Collection c) on EmptyVector. This is probably a mistake.");
		return false;
	}

	/**
	 * Overrides addAll
	 * 
	 * @see java.util.Vector#addAll(int, java.util.Collection)
	 */
	@Override
	public synchronized boolean addAll(int index, Collection c) {
		System.err.println("Called addAll(int index, Collection c) on EmptyVector. This is probably a mistake.");
		return false;
	}

	/**
	 * Overrides addElement
	 * 
	 * @see java.util.Vector#addElement(java.lang.Object)
	 */
	@Override
	public synchronized void addElement(Object obj) {
		System.err.println("Called addElement(Object obj) on EmptyVector. This is probably a mistake.");
	}

	public EmptyVector() {
		super(0);
	}
}
