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
package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.shapes.Arc;

public class ArcImpl extends ShapeImpl implements Arc {

	// private FGEArc arc;

	private int angleStart = 0;
	private int angleExtent = 90;
	private ArcType arcType = ArcType.PIE;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ArcImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.ARC;
	}

	@Override
	protected FGEShape<?> makeShape(ShapeNode<?> node) {
		return new FGEArc(0, 0, 1, 1, angleStart, angleExtent, arcType);
	}

	@Override
	public int getAngleStart() {
		return angleStart;
	}

	@Override
	public void setAngleStart(int anAngle) {
		if (angleStart != anAngle) {
			angleStart = anAngle;
			shapeChanged();
		}
	}

	@Override
	public int getAngleExtent() {
		return angleExtent;
	}

	@Override
	public void setAngleExtent(int anAngle) {
		if (angleExtent != anAngle) {
			angleExtent = anAngle;
			shapeChanged();
		}
	}

	@Override
	public ArcType getArcType() {
		return arcType;
	}

	@Override
	public void setArcType(ArcType anArcType) {
		if (arcType != anArcType) {
			arcType = anArcType;
			shapeChanged();
		}
	}

}
