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

/**
 * Node that can be dumped to a file.
 * 
 * @version $Id: Node.java,v 1.2 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public interface Node {
	public void setName(String n);

	public String getName();

	public void setAccess(String a);

	public String getAccess();

	/**
	 * E.g., if(is("public")) ...
	 */
	public boolean is(String s);

	public void dump(PrintWriter stream);
}
