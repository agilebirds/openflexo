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

import java.awt.Color;
import java.awt.Font;

import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.impl.TextStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent text properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(TextStyleImpl.class)
@XMLElement(xmlTag = "TextStyle")
public interface TextStyle extends FGEStyle {

	// Property keys

	public static final String COLOR = "color";
	public static final String BACKGROUND_COLOR = "backgroundColor";
	public static final String FONT = "font";
	public static final String ORIENTATION = "orientation";
	public static final String IS_BACKGROUND_COLORED = "isBackgroundColored";

	public static enum Parameters implements GRParameter {
		color, backgroundColor, font, orientation, backgroundColored
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = COLOR)
	@XMLAttribute
	public Color getColor();

	@Setter(value = COLOR)
	public void setColor(Color aColor);

	@Getter(value = BACKGROUND_COLOR)
	@XMLAttribute
	public Color getBackgroundColor();

	@Setter(value = BACKGROUND_COLOR)
	public void setBackgroundColor(Color aColor);

	@Getter(value = FONT)
	@XMLAttribute
	public Font getFont();

	@Setter(value = FONT)
	public void setFont(Font aFont);

	@Getter(value = ORIENTATION, defaultValue = "0")
	@XMLAttribute
	public int getOrientation();

	@Setter(value = ORIENTATION)
	public void setOrientation(int anOrientation);

	@Getter(value = IS_BACKGROUND_COLORED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsBackgroundColored();

	@Setter(value = IS_BACKGROUND_COLORED)
	public void setIsBackgroundColored(boolean aFlag);

	public TextStyle clone();

}
