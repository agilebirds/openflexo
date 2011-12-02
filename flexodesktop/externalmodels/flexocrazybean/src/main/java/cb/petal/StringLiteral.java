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
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * There are two kinds of string encodings in Rose: Either the usual "foo bar", or a multi line string where each line starts with a |.
 * 
 * @version $Id: StringLiteral.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class StringLiteral extends Literal {
	static final long serialVersionUID = -4568943900411619930L;

	private ArrayList values = new ArrayList();
	private boolean multi = false;

	public StringLiteral(String v) {
		super("<String>");

		if (v.equals("")) {
			addLine("");
		} else {
			StringTokenizer tok = new StringTokenizer(v, "\r\n");

			multi = tok.countTokens() > 1;
			while (tok.hasMoreTokens()) {
				addLine(tok.nextToken());
			}
		}
	}

	public StringLiteral(Collection c) {
		super("<String>");
		values = new ArrayList(c);
		multi = true; // Initialized as multi line string
	}

	public void addLine(String v) {
		values.add(v);
	}

	public void removeLine(String v) {
		values.remove(v);
	}

	public Collection getLines() {
		return values;
	}

	/**
	 * @return first line, if available
	 */
	public String getValue() {
		if (values.size() > 0) {
			return (String) values.get(0);
		} else {
			return null;
		}
	}

	@Override
	public java.lang.Object getLiteralValue() {
		StringBuffer buf = new StringBuffer();

		for (Iterator i = values.iterator(); i.hasNext();) {
			buf.append(i.next());
			if (i.hasNext()) {
				buf.append("\n");
			}
		}

		return buf.toString();
	}

	public void setMulti(boolean m) {
		multi = m;
	}

	public boolean getMulti() {
		return multi;
	}

	public boolean isMultiLine() {
		return (values.size() > 1) || multi;
	}

	@Override
	public String toString() {
		if (isMultiLine()) {
			StringBuffer buf = new StringBuffer(cb.util.Constants.getNewLine());

			for (Iterator i = values.iterator(); i.hasNext();) {
				buf.append("|" + i.next() + cb.util.Constants.getNewLine());
			}

			return buf.toString();
		} else {
			return '"' + getValue() + '"';
		}
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public boolean equals(java.lang.Object o) {
		return (o instanceof StringLiteral) && (((StringLiteral) o).values.equals(this.values));
	}
}
