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
package org.openflexo.fge;

import java.awt.Color;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a plain colored background
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ColorBackgroundStyle")
public interface ColorBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR_KEY = "color";

	public static GRParameter<Color> COLOR = GRParameter.getGRParameter(ColorBackgroundStyle.class, COLOR_KEY, Color.class);

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public java.awt.Color getColor();

	@Setter(value = COLOR_KEY)
	public void setColor(java.awt.Color aColor);

}
