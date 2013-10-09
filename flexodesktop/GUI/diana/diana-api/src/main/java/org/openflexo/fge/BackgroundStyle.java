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

import java.awt.Paint;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * Represent background properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(NoneBackgroundStyle.class), @Import(ColorBackgroundStyle.class), @Import(ColorGradientBackgroundStyle.class),
		@Import(TextureBackgroundStyle.class), @Import(BackgroundImageBackgroundStyle.class) })
public interface BackgroundStyle extends FGEStyle {

	@PropertyIdentifier(type = Float.class)
	public static final String TRANSPARENCY_LEVEL_KEY = "transparencyLevel";
	@PropertyIdentifier(type = Boolean.class)
	public static final String USE_TRANSPARENCY_KEY = "useTransparency";

	public static GRParameter<Float> TRANSPARENCY_LEVEL = GRParameter.getGRParameter(BackgroundStyle.class, TRANSPARENCY_LEVEL_KEY,
			Float.class);
	public static GRParameter<Boolean> USE_TRANSPARENCY = GRParameter.getGRParameter(BackgroundStyle.class, USE_TRANSPARENCY_KEY,
			Boolean.class);

	/*public static enum Parameters implements GRParameter {
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
	}*/

	public static enum BackgroundStyleType {
		NONE, COLOR, COLOR_GRADIENT, TEXTURE, IMAGE
	}

	public Paint getPaint(DrawingTreeNode<?, ?> dtn, double scale);

	public BackgroundStyleType getBackgroundStyleType();

	@Override
	public String toString();

	@Getter(value = TRANSPARENCY_LEVEL_KEY, defaultValue = "1.0")
	@XMLAttribute
	public float getTransparencyLevel();

	@Setter(value = TRANSPARENCY_LEVEL_KEY)
	public void setTransparencyLevel(float aLevel);

	@Getter(value = USE_TRANSPARENCY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getUseTransparency();

	@Setter(value = USE_TRANSPARENCY_KEY)
	public void setUseTransparency(boolean aFlag);

	public BackgroundStyle clone();

}
