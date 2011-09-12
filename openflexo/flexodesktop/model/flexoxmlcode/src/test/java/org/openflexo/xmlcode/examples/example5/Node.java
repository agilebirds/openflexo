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
 * Class <code>Node</code> is intented to represent a graph's node object in
 * XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Node implements XMLSerializable
{

    public Vector preConditions;
    public Vector outgoingEdges;
    public Graph graph;

    public int identifier;

    public Node()
    {
        super();
         outgoingEdges = new Vector();
        preConditions = new Vector();
    }

    public Node(int anIdentifier, Graph aGraph)
    {
        this();
        identifier = anIdentifier;
        graph = aGraph;
        graph.nodes.add(this);
    }

    public String toShortString()
    {
        return "Node:" + identifier;
    }

    @Override
	public String toString()
    {
        String returned = toShortString() + "\n";
        for (Enumeration e = preConditions.elements(); e.hasMoreElements();) {
            returned += e.nextElement().toString()+"\n";
        }
        for (Enumeration e = outgoingEdges.elements(); e.hasMoreElements();) {
            returned += e.nextElement().toString()+"\n";
        }
        return returned;
    }

}
