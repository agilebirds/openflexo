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

import java.awt.Rectangle;
import java.util.List;

import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.fge.impl.GeometricGraphicalRepresentationImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
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
@ImplementationClass(GeometricGraphicalRepresentationImpl.class)
@XMLElement(xmlTag = "GeometricGraphicalRepresentation")
public interface GeometricGraphicalRepresentation extends GraphicalRepresentation {

	// Property keys

	public static final String FOREGROUND = "foreground";
	public static final String BACKGROUND = "background";
	public static final String GEOMETRIC_OBJECT = "geometricObject";

	public static enum GeometricParameters implements GRParameter {
		foreground, background, geometricObject
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = FOREGROUND)
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = BACKGROUND)
	@XMLElement
	public BackgroundStyle getBackground();

	@Setter(value = BACKGROUND)
	public void setBackground(BackgroundStyle aBackground);

	@Getter(value = GEOMETRIC_OBJECT, isStringConvertable = true)
	@XMLElement
	public FGEArea getGeometricObject();

	@Setter(value = GEOMETRIC_OBJECT)
	public void setGeometricObject(FGEArea geometricObject);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public Rectangle getBounds(double scale);

	public void paintGeometricObject(FGEGeometricGraphics graphics);

	public List<ControlPoint> getControlPoints();

	public List<ControlPoint> rebuildControlPoints();

	public void notifyGeometryChanged();

}
