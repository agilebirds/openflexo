package org.openflexo.fge.impl;

import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class ColorGradientBackgroundStyleImpl extends BackgroundStyleImpl implements ColorGradientBackgroundStyle {

	private java.awt.Color color1;
	private java.awt.Color color2;
	private ColorGradientBackgroundStyle.ColorGradientDirection direction;

	public ColorGradientBackgroundStyleImpl() {
		this(java.awt.Color.WHITE, java.awt.Color.BLACK,
				org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
	}

	public ColorGradientBackgroundStyleImpl(java.awt.Color aColor1, java.awt.Color aColor2,
			ColorGradientBackgroundStyle.ColorGradientDirection aDirection) {
		super();
		this.color1 = aColor1;
		this.color2 = aColor2;
		this.direction = aDirection;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.COLOR_GRADIENT;
	}

	@Override
	public java.awt.Color getColor1() {
		return color1;
	}

	@Override
	public void setColor1(java.awt.Color aColor) {
		if (requireChange(this.color1, aColor)) {
			java.awt.Color oldColor = color1;
			this.color1 = aColor;
			setChanged();
			notifyObservers(new FGEAttributeNotification(COLOR1, oldColor, aColor));
		}
	}

	@Override
	public java.awt.Color getColor2() {
		return color2;
	}

	@Override
	public void setColor2(java.awt.Color aColor) {
		if (requireChange(this.color2, aColor)) {
			java.awt.Color oldColor = color2;
			this.color2 = aColor;
			setChanged();
			notifyObservers(new FGEAttributeNotification(COLOR2, oldColor, aColor));
		}
	}

	@Override
	public ColorGradientBackgroundStyle.ColorGradientDirection getDirection() {
		return direction;
	}

	@Override
	public void setDirection(ColorGradientBackgroundStyle.ColorGradientDirection aDirection) {
		if (requireChange(this.direction, aDirection)) {
			ColorGradientBackgroundStyle.ColorGradientDirection oldTexture = direction;
			this.direction = aDirection;
			setChanged();
			notifyObservers(new FGEAttributeNotification(DIRECTION, oldTexture, aDirection));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.COLOR_GRADIENT(" + getColor1() + "," + getColor2() + "," + getDirection() + ")";
	}*/

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
