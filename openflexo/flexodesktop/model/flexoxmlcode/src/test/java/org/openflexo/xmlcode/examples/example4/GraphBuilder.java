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
package org.openflexo.xmlcode.examples.example4;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class GraphBuilder
{

    public Vector nodes = new Vector();

    public Vector edges = new Vector();

    @Override
	public String toString()
    {
        String returned = "";
        for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
            returned += ((Node) e.nextElement()).toString() + "\n";
        }
        for (Enumeration e = edges.elements(); e.hasMoreElements();) {
            returned += ((Edge) e.nextElement()).toString() + "\n";
        }
        return returned;
    }
}
