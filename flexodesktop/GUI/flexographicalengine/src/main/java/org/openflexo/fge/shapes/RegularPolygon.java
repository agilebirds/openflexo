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
package org.openflexo.fge.shapes;

import org.openflexo.fge.shapes.impl.RegularPolygonImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a regular polygon, with more than 3 points
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(RegularPolygonImpl.class)
@XMLElement(xmlTag = "RegularPolygonShape")
public interface RegularPolygon extends Polygon {

	// Property keys

	public static final String N_POINTS = "nPoints";
	public static final String START_ANGLE = "startAngle";

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = N_POINTS, defaultValue = "5")
	@XMLAttribute
	public int getNPoints();

	@Setter(value = N_POINTS)
	public void setNPoints(int pointsNb);

	@Getter(value = START_ANGLE, defaultValue = "90")
	@XMLAttribute
	public int getStartAngle();

	@Setter(value = START_ANGLE)
	public void setStartAngle(int anAngle);

}
