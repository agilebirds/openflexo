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
package org.openflexo.fge.geomedit.construction;

import org.openflexo.fge.geom.area.FGEArea;

public class ObjectReference<O extends FGEArea> extends GeometricConstruction<O> {

	public GeometricConstruction<O> reference;
	
	public ObjectReference() 
	{
		super();
	}

	public ObjectReference(GeometricConstruction<O> aReference) 
	{
		this();
		this.reference = aReference;
	}

	
	@Override
	protected O computeData()
	{
		return reference.getData();
	}
	
	@Override
	public String toString()
	{
		return "ObjectReference["+reference.toString()+"]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { reference };
		return returned;
	}


}
