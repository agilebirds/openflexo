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

import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * This class provide an implementation of a list containing some other list or elements The goal of this implementation is to rely on
 * embedded lists instead of copying data structures into an other one (performance readons): you can manage a List without having to
 * duplicate concatened lists
 * 
 * @author sylvain
 * 
 * @param <E>
 */
public class ConcatenedList<E> extends AbstractList<E> {

	private Vector<Object> embedded;

	public ConcatenedList() {
		super();
		embedded = new Vector<Object>();
	}

	@Override
	public void add(int index, E element) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		int current = 0;
		for (int i = 0; i < embedded.size(); i++) {
			Object o = embedded.get(i);
			if (current == index) {
				embedded.add(i, element);
				return;
			}
			if (o instanceof List) {
				List list = (List) o;
				if (index < current + list.size()) {
					throw new UnsupportedOperationException();
				}
				current += list.size();
			} else {
				if (current == index) {
					embedded.add(i, element);
					return;
				}
				current++;
			}
		}
	}

	public void addElement(E element) {
		embedded.add(element);
	}

	public void addElementList(List<? extends E> elementList) {
		embedded.add(elementList);
	}

	@Override
	public E get(int index) {
		int current = 0;
		for (Object o : embedded) {
			if (o instanceof List) {
				List list = (List) o;
				if (index < current + list.size()) {
					return (E) list.get(index - current);
				}
				current += list.size();
			} else {
				if (index == current) {
					return (E) o;
				}
				current++;
			}
		}
		throw new NoSuchElementException();
	}

	@Override
	public int size() {
		int returned = 0;
		for (Object o : embedded) {
			if (o instanceof List) {
				returned += ((List) o).size();
			} else {
				returned++;
			}
		}
		return returned;
	}

}
