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
package org.openflexo.xmlcode.examples.example5;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>PreCondition</code> is intented to represent an oriented edge object in XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class PreCondition implements XMLSerializable {

	public Node attachedNode;
	public Vector incomingEdges;

	public int identifier;

	public PreCondition() {
		super();
		attachedNode = null;
		incomingEdges = new Vector();
	}

	public PreCondition(int anIdentifier, Node node) {
		this();
		attachedNode = node;
		attachedNode.preConditions.add(this);
		identifier = anIdentifier;
	}

	public String toShortString() {
		return "PreCondition:" + identifier;
	}

	@Override
	public String toString() {
		String returned = " [ PreCondition " + identifier + " attached to node id ";
		returned += (attachedNode == null ? -1 : attachedNode.identifier) + " receiving edges : ";
		boolean isFirst = true;
		for (Enumeration en = incomingEdges.elements(); en.hasMoreElements();) {
			Edge edge = (Edge) en.nextElement();
			returned += (isFirst ? "" : ",") + edge.identifier;
			isFirst = false;
		}
		returned += " ]";
		return returned;
	}

}
