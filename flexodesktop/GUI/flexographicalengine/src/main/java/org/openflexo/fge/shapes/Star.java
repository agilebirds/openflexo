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

import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.shapes.impl.StarImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
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
@ImplementationClass(StarImpl.class)
@XMLElement(xmlTag = "StarShape")
public interface Star extends ShapeSpecification {

	// Property keys

	public static final String N_POINTS = "nPoints";
	public static final String START_ANGLE = "startAngle";
	public static final String RATIO = "ratio";

	public static enum StarParameters implements GRParameter {
		nPoints, startAngle, ratio;
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = N_POINTS, defaultValue = "6")
	@XMLAttribute
	public int getNPoints();

	@Setter(value = N_POINTS)
	public void setNPoints(int pointsNb);

	@Getter(value = START_ANGLE, defaultValue = "90")
	@XMLAttribute
	public int getStartAngle();

	@Setter(value = START_ANGLE)
	public void setStartAngle(int anAngle);

	@Getter(value = RATIO, defaultValue = "0.5")
	@XMLAttribute
	public double getRatio();

	@Setter(value = RATIO)
	public void setRatio(double aRatio);

}
