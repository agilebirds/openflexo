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
package org.openflexo.fge.graphics;

import java.awt.Paint;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class BackgroundStyle extends Observable implements XMLSerializable, Cloneable {
	static final Logger logger = Logger.getLogger(BackgroundStyle.class.getPackage().getName());

	private transient GraphicalRepresentation graphicalRepresentation;

	private boolean useTransparency = false;
	private float transparencyLevel = 0.5f; // Between 0.0 and 1.0

	public static enum Parameters implements GRParameter {
		color, color1, color2, direction, textureType, imageFile, deltaX, deltaY, imageBackgroundType, scaleX, scaleY, fitToShape, imageBackgroundColor, transparencyLevel, useTransparency;
	}

	public static BackgroundStyle makeEmptyBackground() {
		return new NoneBackgroundStyle();
	}

	public static BackgroundStyle makeColoredBackground(java.awt.Color aColor) {
		return new ColorBackgroundStyle(aColor);
	}

	public static BackgroundStyle makeColorGradientBackground(java.awt.Color color1, java.awt.Color color2,
			ColorGradientBackgroundStyle.ColorGradientDirection direction) {
		return new ColorGradientBackgroundStyle(color1, color2, direction);
	}

	public static BackgroundStyle makeTexturedBackground(TextureBackgroundStyle.TextureType type, java.awt.Color aColor1,
			java.awt.Color aColor2) {
		return new TextureBackgroundStyle(type, aColor1, aColor2);
	}

	public static BackgroundImageBackgroundStyle makeImageBackground(File imageFile) {
		return new BackgroundImageBackgroundStyle(imageFile);
	}

	public static BackgroundImageBackgroundStyle makeImageBackground(ImageIcon image) {
		return new BackgroundImageBackgroundStyle(image);
	}

	public static BackgroundStyle makeBackground(BackgroundStyleType type) {
		if (type == BackgroundStyleType.NONE) {
			return makeEmptyBackground();
		} else if (type == BackgroundStyleType.COLOR) {
			return makeColoredBackground(java.awt.Color.WHITE);
		} else if (type == BackgroundStyleType.COLOR_GRADIENT) {
			return makeColorGradientBackground(java.awt.Color.WHITE, java.awt.Color.BLACK,
					org.openflexo.fge.graphics.ColorGradientBackgroundStyle.ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		} else if (type == BackgroundStyleType.TEXTURE) {
			return makeTexturedBackground(org.openflexo.fge.graphics.TextureBackgroundStyle.TextureType.TEXTURE1, java.awt.Color.RED,
					java.awt.Color.WHITE);
		} else if (type == BackgroundStyleType.IMAGE) {
			return makeImageBackground((File) null);
		}
		return null;
	}

	public abstract Paint getPaint(GraphicalRepresentation gr, double scale);

	public abstract BackgroundStyleType getBackgroundStyleType();

	/*public GraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(
			GraphicalRepresentation graphicalRepresentation)
	{
		this.graphicalRepresentation = graphicalRepresentation;
	}*/

	@Override
	public abstract String toString();

	public static enum BackgroundStyleType {
		NONE, COLOR, COLOR_GRADIENT, TEXTURE, IMAGE
	}

	public float getTransparencyLevel() {
		return transparencyLevel;
	}

	public void setTransparencyLevel(float aLevel) {
		if (requireChange(this.transparencyLevel, aLevel)) {
			float oldValue = transparencyLevel;
			this.transparencyLevel = aLevel;
			setChanged();
			notifyObservers(new FGENotification(Parameters.transparencyLevel, oldValue, aLevel));
		}
	}

	public boolean getUseTransparency() {
		return useTransparency;
	}

	public void setUseTransparency(boolean aFlag) {
		if (requireChange(this.useTransparency, aFlag)) {
			boolean oldValue = useTransparency;
			this.useTransparency = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.useTransparency, oldValue, aFlag));
		}
	}

	@Override
	public BackgroundStyle clone() {
		try {
			BackgroundStyle returned = (BackgroundStyle) super.clone();
			try {
				Field field = Observable.class.getDeclaredField("obs");
				field.setAccessible(true);
				field.set(returned, new Vector());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			returned.graphicalRepresentation = null;
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	private boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

}
