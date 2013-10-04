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

import org.openflexo.fge.GRParameter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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
@XMLElement(xmlTag = "PolygonShape")
public interface RegularPolygon extends Polygon {

	// Property keys

	@PropertyIdentifier(type = Integer.class)
	public static final String N_POINTS_KEY = "nPoints";
	@PropertyIdentifier(type = Integer.class)
	public static final String START_ANGLE_KEY = "startAngle";

	public static GRParameter<Integer> N_POINTS = GRParameter.getGRParameter(RegularPolygon.class, N_POINTS_KEY, Integer.class);
	public static GRParameter<Integer> START_ANGLE = GRParameter.getGRParameter(RegularPolygon.class, START_ANGLE_KEY, Integer.class);

	/*public static enum RegularPolygonParameters implements GRParameter {
		nPoints, startAngle;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = N_POINTS_KEY, defaultValue = "5")
	@XMLAttribute
	public int getNPoints();

	@Setter(value = N_POINTS_KEY)
	public void setNPoints(int pointsNb);

	@Getter(value = START_ANGLE_KEY, defaultValue = "90")
	@XMLAttribute
	public int getStartAngle();

	@Setter(value = START_ANGLE_KEY)
	public void setStartAngle(int anAngle);

}
