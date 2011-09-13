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
package org.openflexo.fge.geomedit;

import java.util.Vector;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.IntersectionConstruction;
import org.openflexo.fge.geomedit.construction.ObjectReference;
import org.openflexo.fge.geomedit.gr.ComputedAreaGraphicalRepresentation;


public class ObjectIntersection extends GeometricObject<FGEArea> {

	private ComputedAreaGraphicalRepresentation<ObjectIntersection> graphicalRepresentation;
	
	// Called for LOAD
	public ObjectIntersection(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public ObjectIntersection(GeometricSet set, IntersectionConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new ComputedAreaGraphicalRepresentation<ObjectIntersection>(this,set.getEditedDrawing());
	}

	@Override
	public ComputedAreaGraphicalRepresentation<ObjectIntersection> getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ComputedAreaGraphicalRepresentation<ObjectIntersection> aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public IntersectionConstruction getConstruction()
	{
		return (IntersectionConstruction)super.getConstruction();
	}

	public void setConstruction(IntersectionConstruction intersectionConstruction)
	{
		_setConstruction(intersectionConstruction);
	}


	@Override
	public String getInspectorName()
	{
		return "Intersection.inspector";
	}

	public Vector<ObjectReference<? extends FGEArea>> getObjects()
	{
		return getConstruction().objectConstructions;
	}
	
}
