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

import java.util.Collection;
import java.util.Iterator;

import cb.petal.Documented;
import cb.petal.PetalObject;
import cb.petal.StringLiteral;

/**
 * Simple representation of a node.
 * 
 * @version $Id: NodeImpl.java,v 1.3 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class NodeImpl implements Node {
	protected String name;
	protected String access;

	@Override
	public void setName(String n) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setAccess(String a) {
		access = a;
	}

	@Override
	public String getAccess() {
		return access;
	}

	@Override
	public boolean is(String s) {
		return access.toLowerCase().indexOf(s.toLowerCase()) >= 0;
	}

	protected static void print(java.io.PrintWriter stream, String pre, String o, String post) {
		if ((o != null) && !"".equals(o)) {
			stream.print(pre + o + post);
		}
	}

	protected static void printDocumentation(java.io.PrintWriter stream, Documented d) {
		if (d != null) {
			StringLiteral str = (StringLiteral) ((PetalObject) d).getProperty("documentation");

			if (str != null) {
				stream.print("  /**");
				Collection lines = str.getLines();

				for (Iterator i = lines.iterator(); i.hasNext();) {
					stream.println(" * " + i.next());
				}

				stream.println("  */");
			}
		}
	}
}
