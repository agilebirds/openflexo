/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
 * Represents a regular star, as defined by a number of points, a start angle, and a ratio between interior and exterior edges
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "StarShape")
public interface Star extends ShapeSpecification {

	// Property keys

	@PropertyIdentifier(type = Integer.class)
	public static final String N_POINTS_KEY = "nPoints";
	@PropertyIdentifier(type = Integer.class)
	public static final String START_ANGLE_KEY = "startAngle";
	@PropertyIdentifier(type = Double.class)
	public static final String RATIO_KEY = "ratio";

	public static GRParameter<Integer> N_POINTS = GRParameter.getGRParameter(Star.class, N_POINTS_KEY, Integer.class);
	public static GRParameter<Integer> START_ANGLE = GRParameter.getGRParameter(Star.class, START_ANGLE_KEY, Integer.class);
	public static GRParameter<Double> RATIO = GRParameter.getGRParameter(Star.class, RATIO_KEY, Double.class);

	/*public static enum StarParameters implements GRParameter {
		nPoints, startAngle, ratio;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = N_POINTS_KEY, defaultValue = "6")
	@XMLAttribute
	public int getNPoints();

	@Setter(value = N_POINTS_KEY)
	public void setNPoints(int pointsNb);

	@Getter(value = START_ANGLE_KEY, defaultValue = "90")
	@XMLAttribute
	public int getStartAngle();

	@Setter(value = START_ANGLE_KEY)
	public void setStartAngle(int anAngle);

	@Getter(value = RATIO_KEY, defaultValue = "0.5")
	@XMLAttribute
	public double getRatio();

	@Setter(value = RATIO_KEY)
	public void setRatio(double aRatio);

}
