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
package org.openflexo.fib.editor.test;

import java.util.Vector;

public class Prout {
	public String name;
	public String description;
	public Vector<Toto> totoList;

	public Prout(String aName) {
		name = aName;
		totoList = new Vector<Toto>();
		totoList.add(new Toto("youp", "une description pour youp"));
		totoList.add(new Toto("la", "une description pour la"));
		totoList.add(new Toto("boum", "une description pour boum"));
	}

	@Override
	public String toString() {
		return "Prout: name=" + name + " description=" + description;
	}
}