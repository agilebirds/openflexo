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
package cb.petal;

/**
 * Tuple (name, value), like in ("DataBaseSet" 800)
 * 
 * @version $Id: Tuple.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Tuple extends Literal {
	static final long serialVersionUID = -143079303638884203L;

	private String name;
	private int value;

	public Tuple(String name, int value) {
		super("<tuple>");
		this.name = name;
		this.value = value;
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public void setValue(int v) {
		value = v;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "(\"" + name + "\" " + value + ")";
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public java.lang.Object getLiteralValue() {
		return toString();
	}

	@Override
	public boolean equals(java.lang.Object o) {
		return (o instanceof Tuple) && (((Tuple) o).value == this.value) && (((Tuple) o).name.equals(this.name));
	}
}
