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
import java.util.Iterator;
import java.util.List;

import cb.petal.Operation;

/**
 * Simple representation of a Java method.
 * 
 * @version $Id: MethodImpl.java,v 1.3 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class MethodImpl extends NodeImpl implements Method {
	private String type;
	private List parameters;
	private List code;
	protected Operation operation;

	public MethodImpl() {
	}

	public void setOperation(Operation o) {
		operation = o;
	}

	public Operation getOperation() {
		return operation;
	}

	@Override
	public void setReturnType(String p) {
		type = p;
	}

	@Override
	public String getReturnType() {
		return type;
	}

	@Override
	public void setParameters(List p) {
		parameters = p;
	}

	@Override
	public List getParameters() {
		return parameters;
	}

	@Override
	public void setCode(List c) {
		code = c;
	}

	@Override
	public List getCode() {
		return code;
	}

	/**
	 * Default implementation prints Java code
	 */
	@Override
	public void dump(PrintWriter stream) {
		printDocumentation(stream, operation);

		stream.print("  ");
		String acc = getAccess();

		if (acc != null)
			stream.print(acc.toLowerCase() + " ");

		stream.print(getReturnType() + " " + getName());
		stream.print("(");

		for (Iterator i = parameters.iterator(); i.hasNext();) {
			Parameter p = (Parameter) i.next();
			p.dump(stream);

			if (i.hasNext())
				stream.print(", ");
		}

		stream.print(")");

		if (is("abstract"))
			stream.println(";");
		else {
			stream.println(" {");

			if (code != null) {
				for (Iterator i = code.iterator(); i.hasNext();)
					stream.println(i.next());
			} else {
				String t = getReturnType().toLowerCase();

				if (!(t.equals("void") || t.equals(""))) {
					stream.println("  return " + cb.util.Constants.getValueForType(t));
				}
			}

			stream.println("  }");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Method) {
			Method m = (Method) o;

			return getParameters().equals(m.getParameters()) && getName().equals(m.getName()) && getReturnType().equals(m.getReturnType());
		} else
			return false;
	}
}
