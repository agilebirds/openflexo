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

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.openflexo.kvc.KeyValueCoding;

public class Test4Class<T1 extends File & KeyValueCoding, T2 extends Date> extends Hashtable<String, Hashtable<T1, T2>> implements
		Map<String, Hashtable<T1, T2>>, Iterator<Vector<T2>>, Cloneable, Serializable {
	public java.util.Hashtable<java.lang.String, java.util.Vector<java.io.File>> test4 = new java.util.Hashtable<java.lang.String, java.util.Vector<java.io.File>>();

	private String _foo1;
	private int[][] _foo2;
	private Map<File, Hashtable<Integer, String>[]> _foo3;
	private T1 _foo4;
	public T2 foo5;
	public Hashtable<T1, ?>[][] foo6;
	private Hashtable<T1, T2> _foo7;
	private Hashtable<? extends T1, ? super T2> _foo8;
	public java.util.Map<? extends java.io.File, java.util.Hashtable<java.lang.Integer, java.util.Vector<? extends T1>>>[] foo9;

	public Hashtable<T1, T2> method1(String a, Hashtable<T1, T2> b, Map<File, Hashtable<Integer, String>[]> c) {
		return null;
	}

	public Hashtable<T1, T2> method2(Vector<? super T1> t1, Vector<? extends T2> t2) {
		return null;
	}

	public String getFoo1() {
		return _foo1;
	}

	public void setFoo1(String foo1) {
		_foo1 = foo1;
	}

	public int[][] getFoo2() {
		return _foo2;
	}

	public void setFoo2(int[][] foo2) {
		_foo2 = foo2;
	}

	public Map<File, Hashtable<Integer, String>[]> getFoo3() {
		return _foo3;
	}

	public void setFoo3(Map<File, Hashtable<Integer, String>[]> foo3) {
		_foo3 = foo3;
	}

	public T1 getFoo4() {
		return _foo4;
	}

	public void setFoo4(T1 foo4) {
		_foo4 = foo4;
	}

	public Hashtable<T1, T2> getFoo7() {
		return _foo7;
	}

	public void setFoo7(Hashtable<T1, T2> foo7) {
		_foo7 = foo7;
	}

	public Hashtable<? extends T1, ? super T2> getFoo8() {
		return _foo8;
	}

	public void setFoo8(Hashtable<? extends T1, ? super T2> foo8) {
		_foo8 = foo8;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vector<T2> next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}