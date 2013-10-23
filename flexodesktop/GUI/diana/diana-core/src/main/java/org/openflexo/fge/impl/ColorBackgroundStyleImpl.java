package org.openflexo.fge.impl;

import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class ColorBackgroundStyleImpl extends BackgroundStyleImpl implements ColorBackgroundStyle {

	private java.awt.Color color;

	public ColorBackgroundStyleImpl() {
		color = java.awt.Color.WHITE;
	}

	public ColorBackgroundStyleImpl(java.awt.Color aColor) {
		color = aColor;
	}

	@Override
	public java.awt.Color getColor() {
		return color;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.COLOR;
	}

	@Override
	public void setColor(java.awt.Color aColor) {
		if (requireChange(this.color, aColor)) {
			java.awt.Color oldColor = color;
			this.color = aColor;
			setChanged();
			notifyObservers(new FGEAttributeNotification(COLOR, oldColor, aColor));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.COLOR(" + getColor() + ")";
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
