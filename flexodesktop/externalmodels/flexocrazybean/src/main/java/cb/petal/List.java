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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Lists (list foo ...) containing other petal nodes.
 * 
 * @version $Id: List.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class List implements PetalNode {
	static final long serialVersionUID = -9142706599368764080L;

	private ArrayList list = new ArrayList();
	private String name;

	public List(String name) {
		setName(name);
	}

	@Override
	public java.lang.Object clone() {
		List list = new List(name);

		list.list = (ArrayList) this.list.clone();
		return list;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		return o instanceof List && ((List) o).name.equals(this.name) && ((List) o).list.equals(this.list);
	}

	public void setName(String n) {
		if (n != null) {
			n = n.intern();
		}

		name = n;
	}

	public final String getName() {
		return name;
	}

	@Override
	public final String getKind() {
		return "list";
	}

	@Override
	public final int getChildCount() {
		return list.size();
	}

	public final PetalNode get(int i) {
		return (PetalNode) list.get(i);
	}

	public final void set(int i, PetalNode node) {
		list.set(i, node);
	}

	public final void add(PetalNode value) {
		list.add(value);
	}

	public final void remove(PetalNode value) {
		list.remove(value);
	}

	public final int size() {
		return list.size();
	}

	public final java.util.List getElements() {
		return (ArrayList) list.clone();
	}

	@Override
	public final String toString() {
		StringBuffer buf = new StringBuffer("(list " + name + "\n");

		for (Iterator i = list.iterator(); i.hasNext();) {
			buf.append(i.next());

			if (i.hasNext()) {
				buf.append("\n");
			}
		}

		buf.append(")\n");

		return buf.toString();
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
