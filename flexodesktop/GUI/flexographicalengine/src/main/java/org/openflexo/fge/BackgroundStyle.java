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

import java.awt.Image;
import java.awt.Paint;
import java.io.File;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.inspector.HasIcon;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Represent background properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(BackgroundStyleImpl.class)
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

	/**
	 * Represent an invisible background
	 * 
	 * @author sylvain
	 * 
	 */
	@ModelEntity
	@ImplementationClass(BackgroundStyleImpl.NoneImpl.class)
	@XMLElement(xmlTag = "NoneBackgroundStyle")
	public static interface None extends BackgroundStyle {

	}

	/**
	 * Represents a plain colored background
	 * 
	 * @author sylvain
	 * 
	 */
	@ModelEntity
	@ImplementationClass(BackgroundStyleImpl.ColorImpl.class)
	@XMLElement(xmlTag = "ColorBackgroundStyle")
	public static interface Color extends BackgroundStyle {

		public static final String COLOR = "color";

		@Getter(value = COLOR)
		@XMLAttribute
		public java.awt.Color getColor();

		@Setter(value = COLOR)
		public void setColor(java.awt.Color aColor);

	}

	/**
	 * Represents a background colored with a linear gradient between two colors
	 * 
	 * @author sylvain
	 * 
	 */
	@ModelEntity
	@ImplementationClass(BackgroundStyleImpl.ColorGradientImpl.class)
	@XMLElement(xmlTag = "ColorGradientBackgroundStyle")
	public static interface ColorGradient extends BackgroundStyle {

		public static final String COLOR1 = "color1";
		public static final String COLOR2 = "color2";
		public static final String DIRECTION = "direction";

		public static enum ColorGradientDirection {
			NORTH_SOUTH, WEST_EAST, SOUTH_EAST_NORTH_WEST, SOUTH_WEST_NORTH_EAST
		}

		@Getter(value = COLOR1)
		@XMLAttribute
		public java.awt.Color getColor1();

		@Setter(value = COLOR1)
		public void setColor1(java.awt.Color aColor);

		@Getter(value = COLOR2)
		@XMLAttribute
		public java.awt.Color getColor2();

		@Setter(value = COLOR2)
		public void setColor2(java.awt.Color aColor);

		@Getter(value = DIRECTION)
		@XMLAttribute
		public ColorGradientDirection getDirection();

		@Setter(value = DIRECTION)
		public void setDirection(ColorGradientDirection aDirection);

	}

	/**
	 * Represents a textured background defined with a texture and two colors
	 * 
	 * @author sylvain
	 * 
	 */
	@ModelEntity
	@ImplementationClass(BackgroundStyleImpl.TextureImpl.class)
	@XMLElement(xmlTag = "TexturedBackgroundStyle")
	public static interface Texture extends BackgroundStyle {

		public static final String COLOR1 = "color1";
		public static final String COLOR2 = "color2";
		public static final String TEXTURE_TYPE = "textureType";

		public static enum TextureType implements HasIcon {
			TEXTURE1,
			TEXTURE2,
			TEXTURE3,
			TEXTURE4,
			TEXTURE5,
			TEXTURE6,
			TEXTURE7,
			TEXTURE8,
			TEXTURE9,
			TEXTURE10,
			TEXTURE11,
			TEXTURE12,
			TEXTURE13,
			TEXTURE14,
			TEXTURE15,
			TEXTURE16;

			public ImageIcon getImageIcon() {
				return new ImageIconResource("Motifs/Motif" + (ordinal() + 1) + ".gif");
			}

			@Override
			public ImageIcon getIcon() {
				return getImageIcon();
			}
		}

		@Getter(value = TEXTURE_TYPE)
		@XMLAttribute
		public TextureType getTextureType();

		@Setter(value = TEXTURE_TYPE)
		public void setTextureType(TextureType aTextureType);

		@Getter(value = COLOR1)
		@XMLAttribute
		public java.awt.Color getColor1();

		@Setter(value = COLOR1)
		public void setColor1(java.awt.Color aColor);

		@Getter(value = COLOR2)
		@XMLAttribute
		public java.awt.Color getColor2();

		@Setter(value = COLOR2)
		public void setColor2(java.awt.Color aColor);

	}

	/**
	 * Represents a background filled with an image
	 * 
	 * @author sylvain
	 * 
	 */
	@ModelEntity
	@ImplementationClass(BackgroundStyleImpl.BackgroundImageImpl.class)
	@XMLElement(xmlTag = "ImageBackgroundStyle")
	public static interface BackgroundImage extends BackgroundStyle {

		public static final String IMAGE_FILE = "imageFile";
		public static final String SCALE_X = "scaleX";
		public static final String SCALE_Y = "scaleY";
		public static final String DELTA_X = "deltaX";
		public static final String DELTA_Y = "deltaY";
		public static final String FIT_TO_SHAPE = "fitToShape";
		public static final String IMAGE_BACKGROUND_TYPE = "imageBackgroundType";
		public static final String IMAGE_BACKGROUND_COLOR = "imageBackgroundColor";

		public static enum ImageBackgroundType {
			OPAQUE, TRANSPARENT
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType();

		@Getter(value = IMAGE_FILE)
		@XMLAttribute
		public File getImageFile();

		@Setter(value = IMAGE_FILE)
		public void setImageFile(File anImageFile);

		@Getter(value = IMAGE_BACKGROUND_COLOR)
		@XMLAttribute
		public java.awt.Color getImageBackgroundColor();

		@Setter(value = IMAGE_BACKGROUND_COLOR)
		public void setImageBackgroundColor(java.awt.Color aColor);

		@Getter(value = DELTA_X, defaultValue = "0.0")
		@XMLAttribute
		public double getDeltaX();

		@Setter(value = DELTA_X)
		public void setDeltaX(double aDeltaX);

		@Getter(value = DELTA_Y, defaultValue = "0.0")
		@XMLAttribute
		public double getDeltaY();

		@Setter(value = DELTA_Y)
		public void setDeltaY(double aDeltaY);

		@Getter(value = IMAGE_BACKGROUND_TYPE)
		@XMLAttribute
		public ImageBackgroundType getImageBackgroundType();

		@Setter(value = IMAGE_BACKGROUND_TYPE)
		public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType);

		@Getter(value = SCALE_X, defaultValue = "1.0")
		@XMLAttribute
		public double getScaleX();

		@Setter(value = SCALE_X)
		public void setScaleX(double aScaleX);

		@Getter(value = SCALE_Y, defaultValue = "1.0")
		@XMLAttribute
		public double getScaleY();

		@Setter(value = SCALE_Y)
		public void setScaleY(double aScaleY);

		@Getter(value = FIT_TO_SHAPE, defaultValue = "false")
		@XMLAttribute
		public boolean getFitToShape();

		@Setter(value = FIT_TO_SHAPE)
		public void setFitToShape(boolean aFlag);

		public Image getImage();

		public void setImage(Image image);

		public void setScaleXNoNotification(double aScaleX);

		public void setScaleYNoNotification(double aScaleY);

	}

	public Paint getPaint(GraphicalRepresentation gr, double scale);

	public BackgroundStyleType getBackgroundStyleType();

	@Override
	public String toString();

	public float getTransparencyLevel();

	public void setTransparencyLevel(float aLevel);

	public boolean getUseTransparency();

	public void setUseTransparency(boolean aFlag);

	public BackgroundStyle clone();

}
