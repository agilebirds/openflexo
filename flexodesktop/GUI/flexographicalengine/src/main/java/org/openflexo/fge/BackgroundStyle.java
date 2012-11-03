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
import org.openflexo.inspector.HasIcon;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Represent background properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(BackgroundStyleImpl.class)
public interface BackgroundStyle extends XMLSerializable, Cloneable, IObservable {

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
	@ImplementationClass(BackgroundStyleImpl.class)
	public static interface None extends BackgroundStyle {

	}

	public static interface Color extends BackgroundStyle {
		public java.awt.Color getColor();

		public void setColor(java.awt.Color aColor);

	}

	public static interface ColorGradient extends BackgroundStyle {

		public static enum ColorGradientDirection {
			NORTH_SOUTH, WEST_EAST, SOUTH_EAST_NORTH_WEST, SOUTH_WEST_NORTH_EAST
		}

		public java.awt.Color getColor1();

		public void setColor1(java.awt.Color aColor);

		public java.awt.Color getColor2();

		public void setColor2(java.awt.Color aColor);

		public ColorGradientDirection getDirection();

		public void setDirection(ColorGradientDirection aDirection);

	}

	public static interface Texture extends BackgroundStyle {

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

		public TextureType getTextureType();

		public void setTextureType(TextureType aTextureType);

		public java.awt.Color getColor1();

		public void setColor1(java.awt.Color aColor);

		public java.awt.Color getColor2();

		public void setColor2(java.awt.Color aColor);
	}

	public static interface BackgroundImage extends BackgroundStyle {
		public static enum ImageBackgroundType {
			OPAQUE, TRANSPARENT
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType();

		public File getImageFile();

		public void setImageFile(File anImageFile);

		public java.awt.Color getImageBackgroundColor();

		public void setImageBackgroundColor(java.awt.Color aColor);

		public double getDeltaX();

		public void setDeltaX(double aDeltaX);

		public double getDeltaY();

		public void setDeltaY(double aDeltaY);

		public ImageBackgroundType getImageBackgroundType();

		public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType);

		public double getScaleX();

		public void setScaleX(double aScaleX);

		public double getScaleY();

		public void setScaleY(double aScaleY);

		public boolean getFitToShape();

		public void setFitToShape(boolean aFlag);

		public Image getImage();

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
