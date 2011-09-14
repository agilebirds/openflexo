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

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>Edge</code> is intented to represent an oriented edge object in
 * XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Edge implements XMLSerializable
{

    public Node originNode;

    public Node destinationNode;

    public Edge(GraphBuilder gb)
    {
        super();
        gb.edges.add(this);
    }

    public Edge(GraphBuilder gb, Node origin, Node destination)
    {
        super();
        originNode = origin;
        destinationNode = destination;
        origin.edges.add(this);
        gb.edges.add(this);
    }

    @Override
	public String toString()
    {
        String returned = " [ Edge: ";
        returned += (originNode == null ? null : originNode.toShortString()) + "-";
        returned += (destinationNode == null ? null : destinationNode.toShortString()) + " ] ";
        return returned;
    }

    public void finalizeEdgeSerialization()
    {
        System.out.println("finalizeEdgeSerialization");
    }
}
