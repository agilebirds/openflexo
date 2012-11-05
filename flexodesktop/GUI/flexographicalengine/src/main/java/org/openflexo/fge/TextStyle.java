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
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represent text properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(TextStyleImpl.class)
public interface TextStyle extends FGEStyle {

	public static enum Parameters implements GRParameter {
		color, backgroundColor, font, orientation, backgroundColored
	}

	public Color getColor();

	public void setColor(Color aColor);

	public Font getFont();

	public void setFont(Font aFont);

	public int getOrientation();

	public void setOrientation(int anOrientation);

	public Color getBackgroundColor();

	public void setBackgroundColor(Color aColor);

	public boolean getIsBackgroundColored();

	public void setIsBackgroundColored(boolean aFlag);

	public TextStyle clone();

}
