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
import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents an Arc, as defined by an arc type, an angle start, and an angle extent
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "ArcShape")
public interface Arc extends ShapeSpecification {

	// Property keys

	@PropertyIdentifier(type = Integer.class)
	public static final String ANGLE_EXTENT_KEY = "angleExtent";
	@PropertyIdentifier(type = Integer.class)
	public static final String ANGLE_START_KEY = "angleStart";
	@PropertyIdentifier(type = ArcType.class)
	public static final String ARC_TYPE_KEY = "arcType";

	public static GRParameter<Integer> ANGLE_EXTENT = GRParameter.getGRParameter(Arc.class, ANGLE_EXTENT_KEY, Integer.class);
	public static GRParameter<Integer> ANGLE_START = GRParameter.getGRParameter(Arc.class, ANGLE_START_KEY, Integer.class);
	public static GRParameter<ArcType> ARC_TYPE = GRParameter.getGRParameter(Arc.class, ARC_TYPE_KEY, ArcType.class);

	/*public static enum ArcParameters implements GRParameter {
		angleExtent, angleStart, arcType;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = ANGLE_START_KEY, defaultValue = "0")
	@XMLAttribute
	public int getAngleStart();

	@Setter(value = ANGLE_START_KEY)
	public void setAngleStart(int anAngle);

	@Getter(value = ANGLE_EXTENT_KEY, defaultValue = "90")
	@XMLAttribute
	public int getAngleExtent();

	@Setter(value = ANGLE_EXTENT_KEY)
	public void setAngleExtent(int anAngle);

	@Getter(value = ARC_TYPE_KEY)
	@XMLAttribute
	public ArcType getArcType();

	@Setter(value = ARC_TYPE_KEY)
	public void setArcType(ArcType anArcType);

}
