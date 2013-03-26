package org.openflexo.fge.graphics;

import java.awt.GradientPaint;
import java.awt.Paint;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;

public class ColorGradientBackgroundStyle extends BackgroundStyle {
	private java.awt.Color color1;
	private java.awt.Color color2;
	private ColorGradientBackgroundStyle.ColorGradientDirection direction;

	public ColorGradientBackgroundStyle() {
		this(java.awt.Color.WHITE, java.awt.Color.BLACK,
				org.openflexo.fge.graphics.ColorGradientBackgroundStyle.ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
	}

	public ColorGradientBackgroundStyle(java.awt.Color aColor1, java.awt.Color aColor2,
			ColorGradientBackgroundStyle.ColorGradientDirection aDirection) {
		super();
		this.color1 = aColor1;
		this.color2 = aColor2;
		this.direction = aDirection;
	}

	public static enum ColorGradientDirection {
		NORTH_SOUTH, WEST_EAST, SOUTH_EAST_NORTH_WEST, SOUTH_WEST_NORTH_EAST
	}

	@Override
	public Paint getPaint(GraphicalRepresentation gr, double scale) {
		switch (direction) {
		case SOUTH_EAST_NORTH_WEST:
			return new GradientPaint(0, 0, color1, gr.getViewWidth(scale), gr.getViewHeight(scale), color2);
		case SOUTH_WEST_NORTH_EAST:
			return new GradientPaint(0, gr.getViewHeight(scale), color1, gr.getViewWidth(scale), 0, color2);
		case WEST_EAST:
			return new GradientPaint(0, 0.5f * gr.getViewHeight(scale), color1, gr.getViewWidth(scale), 0.5f * gr.getViewHeight(scale),
					color2);
		case NORTH_SOUTH:
			return new GradientPaint(0.5f * gr.getViewWidth(scale), 0, color1, 0.5f * gr.getViewWidth(scale), gr.getViewHeight(scale),
					color2);
		default:
			return new GradientPaint(0, 0, color1, gr.getViewWidth(scale), gr.getViewHeight(scale), color2);
		}
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.COLOR_GRADIENT;
	}

	public java.awt.Color getColor1() {
		return color1;
	}

	public void setColor1(java.awt.Color aColor) {
		if (requireChange(this.color1, aColor)) {
			java.awt.Color oldColor = color1;
			this.color1 = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.color1, oldColor, aColor));
		}
	}

	public java.awt.Color getColor2() {
		return color2;
	}

	public void setColor2(java.awt.Color aColor) {
		if (requireChange(this.color2, aColor)) {
			java.awt.Color oldColor = color2;
			this.color2 = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.color2, oldColor, aColor));
		}
	}

	public ColorGradientBackgroundStyle.ColorGradientDirection getDirection() {
		return direction;
	}

	public void setDirection(ColorGradientBackgroundStyle.ColorGradientDirection aDirection) {
		if (requireChange(this.direction, aDirection)) {
			ColorGradientBackgroundStyle.ColorGradientDirection oldTexture = direction;
			this.direction = aDirection;
			setChanged();
			notifyObservers(new FGENotification(Parameters.direction, oldTexture, aDirection));
		}
	}

	@Override
	public String toString() {
		return "BackgroundStyle.COLOR_GRADIENT(" + getColor1() + "," + getColor2() + "," + getDirection() + ")";
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