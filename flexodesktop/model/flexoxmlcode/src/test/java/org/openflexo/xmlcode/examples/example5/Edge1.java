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

/**
 * Class <code>Edge</code> is intented to represent an oriented edge object in XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Edge1 extends Edge {

	public Edge1() {
		super();
	}

	public Edge1(int anIdentifier, Node origin, PreCondition destination) {
		super(anIdentifier, origin, destination);
	}

	@Override
	public String shortClassName() {
		return "Edge1";
	}

}
