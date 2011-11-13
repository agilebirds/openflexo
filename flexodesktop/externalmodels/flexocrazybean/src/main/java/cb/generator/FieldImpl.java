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
package cb.generator;

import java.io.PrintWriter;

import cb.petal.ClassAttribute;

/**
 * Simple representation of a Java field.
 * 
 * @version $Id: FieldImpl.java,v 1.3 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class FieldImpl extends NodeImpl implements Field {
	private String init, type;
	protected ClassAttribute attribute;

	public FieldImpl() {
	}

	public void setAttribute(ClassAttribute a) {
		attribute = a;
	}

	public ClassAttribute getAttribute() {
		return attribute;
	}

	@Override
	public void setInitialValue(String s) {
		init = s;
	}

	@Override
	public String getInitialValue() {
		return init;
	}

	@Override
	public void setType(String p) {
		type = p;
	}

	@Override
	public String getType() {
		return type;
	}

	/**
	 * Default implementation prints Java code
	 */
	@Override
	public void dump(PrintWriter stream) {
		printDocumentation(stream, attribute);

		stream.print("  ");
		print(stream, "", getAccess(), " ");
		stream.print(getType() + " " + getName());
		print(stream, " = ", getInitialValue(), "");
		stream.println(";");
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Field) {
			Field f = (Field) o;

			return getType().equals(f.getType()) && getName().equals(f.getName());
		} else
			return false;
	}
}
