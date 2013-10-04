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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.logging.FlexoLogger;

public class IntersectionConstruction extends GeometricConstruction<FGEArea> {

	private static final Logger logger = FlexoLogger.getLogger(IntersectionConstruction.class.getPackage().getName());

	public Vector<ObjectReference<? extends FGEArea>> objectConstructions;

	public IntersectionConstruction() {
		super();
		objectConstructions = new Vector<ObjectReference<? extends FGEArea>>();
	}

	public IntersectionConstruction(Vector<ObjectReference<? extends FGEArea>> someGeometricConstructions) {
		this();
		this.objectConstructions.addAll(someGeometricConstructions);
	}

	@Override
	protected FGEArea computeData() {
		FGEArea o1 = objectConstructions.get(0).getData();
		FGEArea o2 = objectConstructions.get(1).getData();
		FGEArea returned = o1.intersect(o2);
		logger.info("Intersection between " + o1 + " and " + o2 + " gives " + returned);
		if (returned == null) {
			new Exception("Unexpected intersection").printStackTrace();
		}
		return returned;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("IntersectionConstruction[\n");
		for (GeometricConstruction c : objectConstructions) {
			sb.append("> " + c.toString() + "\n");
		}
		sb.append("-> " + getData() + "\n");
		sb.append("]");
		return sb.toString();
	}

	@Override
	public GeometricConstruction[] getDepends() {
		return objectConstructions.toArray(new GeometricConstruction[objectConstructions.size()]);
	}

}
