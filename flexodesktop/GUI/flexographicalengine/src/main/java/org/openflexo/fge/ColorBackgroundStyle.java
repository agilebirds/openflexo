package org.openflexo.fge;

import java.awt.Paint;

import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.BackgroundStyle.Parameters;
import org.openflexo.fge.notifications.FGENotification;

public class ColorBackgroundStyle extends BackgroundStyle {
	private java.awt.Color color;

	public ColorBackgroundStyle() {
		color = java.awt.Color.WHITE;
	}

	public ColorBackgroundStyle(java.awt.Color aColor) {
		color = aColor;
	}

	@Override
	public Paint getPaint(GraphicalRepresentation gr, double scale) {
		return color;
	}

	public java.awt.Color getColor() {
		return color;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.COLOR;
	}

	public void setColor(java.awt.Color aColor) {
		if (requireChange(this.color, aColor)) {
			java.awt.Color oldColor = color;
			this.color = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.color, oldColor, aColor));
		}
	}

	@Override
	public String toString() {
		return "BackgroundStyle.COLOR(" + getColor() + ")";
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