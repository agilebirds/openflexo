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
package org.openflexo.wkf.swleditor.gr;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;


public interface SWLContainerGR 
{

	public int getSwimmingLaneNb();

	public void setSwimmingLaneNb(int swlNb);

	public int getSwimmingLaneHeight();

	public void setSwimmingLaneHeight(int height);

	public double getWidth();
	
	public double getHeight();
	
	public SwimmingLaneRepresentation getDrawing();
	
	public FGEArea getLocationConstrainedAreaForChild(AbstractNodeGR node);
}
