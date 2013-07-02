package org.openflexo.fge.impl;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.notifications.FGENotification;

public class TextStyleImpl extends FGEStyleImpl implements TextStyle {

	private static final Logger logger = Logger.getLogger(TextStyle.class.getPackage().getName());

	private Color color;
	private Color backgroundColor = Color.WHITE;
	private Font font;
	private int orientation = 0; // angle in degree
	private boolean backgroundColored = false;

	public TextStyleImpl() {
		super();
		color = FGEConstants.DEFAULT_TEXT_COLOR;
		font = FGEConstants.DEFAULT_TEXT_FONT;
	}

	@Deprecated
	private TextStyleImpl(Color aColor, Font aFont) {
		this();
		color = aColor;
		font = aFont;
	}

	@Deprecated
	private static TextStyleImpl makeDefault() {
		return makeTextStyle(FGEConstants.DEFAULT_TEXT_COLOR, FGEConstants.DEFAULT_TEXT_FONT);
	}

	@Deprecated
	private static TextStyleImpl makeTextStyle(Color aColor, Font aFont) {
		return new TextStyleImpl(aColor, aFont);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color aColor) {
		if (requireChange(this.color, aColor)) {
			Color oldColor = color;
			this.color = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.color, oldColor, aColor));
		}
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font aFont) {
		if (requireChange(this.font, aFont)) {
			Font oldFont = this.font;
			this.font = aFont;
			setChanged();
			notifyObservers(new FGENotification(Parameters.font, oldFont, aFont));
		}
	}

	@Override
	public int getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(int anOrientation) {
		if (requireChange(this.orientation, anOrientation)) {
			int oldOrientation = this.orientation;
			orientation = anOrientation;
			setChanged();
			notifyObservers(new FGENotification(Parameters.orientation, oldOrientation, anOrientation));
		}
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public void setBackgroundColor(Color aColor) {
		if (requireChange(this.backgroundColor, aColor)) {
			Color oldColor = backgroundColor;
			this.backgroundColor = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.backgroundColor, oldColor, aColor));
		}
	}

	@Override
	public boolean getIsBackgroundColored() {
		return backgroundColored;
	}

	@Override
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
