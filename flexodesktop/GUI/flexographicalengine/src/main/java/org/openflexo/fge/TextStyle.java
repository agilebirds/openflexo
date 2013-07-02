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
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.xmlcode.XMLSerializable;

public class TextStyle extends Observable implements XMLSerializable, Cloneable {
	private static final Logger logger = Logger.getLogger(TextStyle.class.getPackage().getName());

	private Color color;
	private Color backgroundColor = Color.WHITE;
	private Font font;
	private int orientation = 0; // angle in degree
	private boolean backgroundColored = false;

	public static enum Parameters implements GRParameter {
		color, backgroundColor, font, orientation, backgroundColored
	}

	public TextStyle() {
		super();
		color = FGEConstants.DEFAULT_TEXT_COLOR;
		font = FGEConstants.DEFAULT_TEXT_FONT;
	}

	public TextStyle(Color aColor, Font aFont) {
		this();
		color = aColor;
		font = aFont;
	}

	public static TextStyle makeDefault() {
		return makeTextStyle(FGEConstants.DEFAULT_TEXT_COLOR, FGEConstants.DEFAULT_TEXT_FONT);
	}

	public static TextStyle makeTextStyle(Color aColor, Font aFont) {
		return new TextStyle(aColor, aFont);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color aColor) {
		if (requireChange(this.color, aColor)) {
			Color oldColor = color;
			this.color = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.color, oldColor, aColor));
		}
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font aFont) {
		if (requireChange(this.font, aFont)) {
			Font oldFont = this.font;
			this.font = aFont;
			setChanged();
			notifyObservers(new FGENotification(Parameters.font, oldFont, aFont));
		}
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int anOrientation) {
		if (requireChange(this.orientation, anOrientation)) {
			int oldOrientation = this.orientation;
			orientation = anOrientation;
			setChanged();
			notifyObservers(new FGENotification(Parameters.orientation, oldOrientation, anOrientation));
		}
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color aColor) {
		if (requireChange(this.backgroundColor, aColor)) {
			Color oldColor = backgroundColor;
			this.backgroundColor = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.backgroundColor, oldColor, aColor));
		}
	}

	public boolean getIsBackgroundColored() {
		return backgroundColored;
	}

	public void setIsBackgroundColored(boolean aFlag) {
		if (requireChange(this.backgroundColored, aFlag)) {
			boolean oldValue = backgroundColored;
			this.backgroundColored = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.backgroundColored, oldValue, aFlag));
		}
	}

	@Override
	public TextStyle clone() {
		try {
			return (TextStyle) super.clone();
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
