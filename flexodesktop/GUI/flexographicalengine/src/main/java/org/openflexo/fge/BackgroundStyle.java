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

import java.awt.Paint;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represent background properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(BackgroundStyleImpl.class)
@Imports({ @Import(NoneBackgroundStyle.class), @Import(ColorBackgroundStyle.class), @Import(ColorGradientBackgroundStyle.class),
		@Import(TextureBackgroundStyle.class), @Import(BackgroundImageBackgroundStyle.class) })
public interface BackgroundStyle extends FGEStyle {

	public static enum Parameters implements GRParameter {
		color,
		color1,
		color2,
		direction,
		textureType,
		imageFile,
		deltaX,
		deltaY,
		imageBackgroundType,
		scaleX,
		scaleY,
		fitToShape,
		imageBackgroundColor,
		transparencyLevel,
		useTransparency;
	}

	public static enum BackgroundStyleType {
		NONE, COLOR, COLOR_GRADIENT, TEXTURE, IMAGE
	}

	public Paint getPaint(DrawingTreeNode<?, ?> dtn, double scale);

	public BackgroundStyleType getBackgroundStyleType();

	@Override
	public String toString();

	public float getTransparencyLevel();

	public void setTransparencyLevel(float aLevel);

	public boolean getUseTransparency();

	public void setUseTransparency(boolean aFlag);

	public BackgroundStyle clone();

}
