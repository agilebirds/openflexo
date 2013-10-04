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
package org.openflexo.fge;

import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a geometric object in a diagram<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "GeometricGraphicalRepresentation")
public interface GeometricGraphicalRepresentation extends GraphicalRepresentation {

	// Property keys

	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String BACKGROUND_KEY = "background";
	@PropertyIdentifier(type = FGEArea.class)
	public static final String GEOMETRIC_OBJECT_KEY = "geometricObject";

	public static GRParameter<BackgroundStyle> BACKGROUND = GRParameter.getGRParameter(GeometricGraphicalRepresentation.class,
			GeometricGraphicalRepresentation.BACKGROUND_KEY, BackgroundStyle.class);
	public static GRParameter<ForegroundStyle> FOREGROUND = GRParameter.getGRParameter(GeometricGraphicalRepresentation.class,
			GeometricGraphicalRepresentation.FOREGROUND_KEY, ForegroundStyle.class);
	public static GRParameter<FGEArea> GEOMETRIC_OBJECT = GRParameter.getGRParameter(GeometricGraphicalRepresentation.class,
			GeometricGraphicalRepresentation.GEOMETRIC_OBJECT_KEY, FGEArea.class);

	/*public static enum GeometricParameters implements GRParameter {
		foreground, background, geometricObject
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = FOREGROUND_KEY)
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = BACKGROUND_KEY)
	@XMLElement
	public BackgroundStyle getBackground();

	@Setter(value = BACKGROUND_KEY)
	public void setBackground(BackgroundStyle aBackground);

	@Getter(value = GEOMETRIC_OBJECT_KEY, isStringConvertable = true)
	@XMLElement
	public FGEArea getGeometricObject();

	@Setter(value = GEOMETRIC_OBJECT_KEY)
	public void setGeometricObject(FGEArea geometricObject);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public boolean getNoStroke();

	public void setNoStroke(boolean noStroke);

	public BackgroundStyleType getBackgroundType();

	public void setBackgroundType(BackgroundStyleType backgroundType);

	/*public Rectangle getBounds(double scale);

	public void paintGeometricObject(FGEGeometricGraphics graphics);

	public List<ControlPoint> getControlPoints();

	public List<ControlPoint> rebuildControlPoints();

	public void notifyGeometryChanged();*/

}
