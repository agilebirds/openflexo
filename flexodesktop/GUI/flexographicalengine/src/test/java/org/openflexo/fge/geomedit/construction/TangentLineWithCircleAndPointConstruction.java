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

import java.util.logging.Logger;

import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.logging.FlexoLogger;

public class TangentLineWithCircleAndPointConstruction extends LineConstruction {

	private static final Logger logger = FlexoLogger.getLogger(TangentLineWithCircleAndPointConstruction.class.getPackage().getName());

	public CircleConstruction circleConstruction;
	public PointConstruction pointConstruction;
	public PointConstruction choosingPointConstruction;
	
	public TangentLineWithCircleAndPointConstruction() 
	{
		super();
	}
	
	public TangentLineWithCircleAndPointConstruction(CircleConstruction circleConstruction, PointConstruction pointConstruction,  PointConstruction choosingPointConstruction) 
	{
		this();
		this.circleConstruction = circleConstruction;
		this.pointConstruction = pointConstruction;
		this.choosingPointConstruction = choosingPointConstruction;
	}
	
	@Override
	protected FGELine computeData()
	{
		FGEUnionArea tangentPoints = FGECircle.getTangentsPointsToCircle(circleConstruction.getCircle(),pointConstruction.getPoint());
		if (tangentPoints.isUnionOfPoints()) {
			return new FGELine(tangentPoints.getNearestPoint(choosingPointConstruction.getPoint()),pointConstruction.getPoint());
		}
		logger.warning("Received strange result for FGEEllips.getTangentsPointsToCircle()");
		return null;
	}

	@Override
	public String toString()
	{
		return "TangentLineWithCircleAndPoint[\n"+"> "+circleConstruction.toString()+"\n> "+pointConstruction.toString()+"\n> "+choosingPointConstruction.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { circleConstruction, pointConstruction, choosingPointConstruction };
		return returned;
	}


}
