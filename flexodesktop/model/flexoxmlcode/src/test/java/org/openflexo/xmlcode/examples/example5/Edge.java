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

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>Edge</code> is intented to represent an oriented edge object in
 * XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public abstract class Edge implements XMLSerializable
{

    public Node originNode;
    public PreCondition destinationPreCondition;

    public int identifier;

    public Edge()
    {
        super();
        originNode = null;
        destinationPreCondition = null;
    }

    public Edge(int anIdentifier, Node origin, PreCondition destination)
    {
        this();
        originNode = origin;
        destinationPreCondition = destination;
        origin.outgoingEdges.add(this);
        destination.incomingEdges.add(this);
        identifier = anIdentifier;
    }

    @Override
	public String toString()
    {
        String returned = " [ "+shortClassName()+": "+identifier+" from ";
        returned += (originNode == null ? null : originNode.toShortString()) + " to ";
        returned += (destinationPreCondition == null ? null : destinationPreCondition.toShortString()) + " ] ";
        return returned;
    }

    public abstract String shortClassName();
}
