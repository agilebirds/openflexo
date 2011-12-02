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
import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>Node</code> is intented to represent a graph's node object in XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Node implements XMLSerializable {

	public Vector edges;

	public Hashtable nodeProperties;

	private int id;

	private static int nodeId = 1;

	public Node(GraphBuilder gb) {
		super();
		id = nodeId++;
		edges = new Vector();
		gb.nodes.add(this);
		initProperties();
	}

	private void initProperties() {
		nodeProperties = new Hashtable();
		/*
		 * if (id > 1 && id <= 3) { properties.put ("test1", "value1");
		 * properties.put ("test2", new File("truc/essai")); } if (id >= 3) {
		 * properties.put ("test3", "value3"); try { properties.put ("test4",
		 * new URL("http://picolibre.enst-bretagne.fr")); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } properties.put
		 * ("test5", new Date()); }
		 */
	}

	public String toShortString() {
		return "Node: " + id;
	}

	@Override
	public String toString() {
		String returned = toShortString() + " with edges ";
		for (Enumeration e = edges.elements(); e.hasMoreElements();) {
			returned += e.nextElement().toString();
		}
		return returned;
	}

	public void finalizeNodeSerialization(GraphBuilder gb) {
		System.out.println("finalizeNodeSerialization");
	}

}
