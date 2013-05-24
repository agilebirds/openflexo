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

public class Coucou {
	private String name;
	public String description;
	private Vector<Prout> prouts;
	public boolean showProuts = false;

	public byte testByte = (byte) 127;
	public short testShort = 10;
	public int testInteger = 1089;
	public long testLong = 1099886657;
	public float testFloat = 98873.7812873f;
	public double testDouble = 1.9823983276276;

	public Coucou() {
		name = "coucou";
		description = "une description qui decrit ce que c'est";
		prouts = new Vector<Prout>();
		createNewProut();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		System.out.println("Name: " + name);
		this.name = name;
	}

	public Vector<Prout> getProuts() {
		return prouts;
	}

	public void setProuts(Vector<Prout> prouts) {
		this.prouts = prouts;
	}

	public void addToProuts(Prout aProut) {
		prouts.add(aProut);
	}

	public void removeFromProuts(Prout aProut) {
		prouts.remove(aProut);
	}

	public Prout createNewProut() {
		Prout returned = new Prout("hop");
		addToProuts(returned);
		return returned;
	}

	public void deleteProut(Prout prout) {
		removeFromProuts(prout);
	}

	public boolean isProutAddable() {
		return true;
	}

	public boolean isProutDeletable(Prout aProut) {
		return true;
	}

}