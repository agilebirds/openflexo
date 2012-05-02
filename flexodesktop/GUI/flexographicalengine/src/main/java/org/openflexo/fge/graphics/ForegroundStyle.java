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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.inspector.HasIcon;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.XMLSerializable;

public class ForegroundStyle extends Observable implements XMLSerializable, Cloneable {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ForegroundStyle.class.getPackage().getName());

	public static enum Parameters implements GRParameter {
		color, lineWidth, capStyle, joinStyle, dashStyle, noStroke, useTransparency, transparencyLevel
	}

	// TODO: remove this, debug only
	// public ShapeGraphicalRepresentation owner;

	private boolean noStroke = false;

	private Color color;
	private double lineWidth;
	private JoinStyle joinStyle;
	private CapStyle capStyle;
	private DashStyle dashStyle;

	private boolean useTransparency = false;
	private float transparencyLevel = 0.5f; // Between 0.0 and 1.0

	private Stroke stroke;
	private double strokeScale;

	public static enum JoinStyle implements HasIcon {
		/**
		 * Joins path segments by extending their outside edges until they meet.
		 */
		JOIN_MITER,

		/**
		 * Joins path segments by rounding off the corner at a radius of half the line width.
		 */
		JOIN_ROUND,

		/**
		 * Joins path segments by connecting the outer corners of their wide outlines with a straight segment.
		 */
		JOIN_BEVEL;

		@Override
		public ImageIcon getIcon() {
			if (this == JOIN_MITER) {
				return FGEIconLibrary.JOIN_MITER_ICON;
			} else if (this == JOIN_ROUND) {
				return FGEIconLibrary.JOIN_ROUND_ICON;
			} else if (this == JOIN_BEVEL) {
				return FGEIconLibrary.JOIN_BEVEL_ICON;
			}
			return null;
		}
	}

	public static enum CapStyle implements HasIcon {
		/**
		 * Ends unclosed subpaths and dash segments with no added decoration.
		 */
		CAP_BUTT,

		/**
		 * Ends unclosed subpaths and dash segments with a round decoration that has a radius equal to half of the width of the pen.
		 */
		CAP_ROUND,

		/**
		 * Ends unclosed subpaths and dash segments with a square projection that extends beyond the end of the segment to a distance equal
		 * to half of the line width.
		 */
		CAP_SQUARE;

		@Override
		public ImageIcon getIcon() {
			if (this == CAP_BUTT) {
				return FGEIconLibrary.CAP_BUTT_ICON;
			} else if (this == CAP_ROUND) {
				return FGEIconLibrary.CAP_ROUND_ICON;
			} else if (this == CAP_SQUARE) {
				return FGEIconLibrary.CAP_SQUARE_ICON;
			}
			return null;
		}

	}

	public static enum DashStyle implements HasIcon {
		PLAIN_STROKE, SMALL_DASHES, MEDIUM_DASHES, MEDIUM_SPACED_DASHES, BIG_DASHES, DOTS_DASHES, DOT_LINES_DASHES;

		@Override
		public ImageIcon getIcon() {
			if (this == PLAIN_STROKE) {
				return FGEIconLibrary.PLAIN_STROKE_ICON;
			} else if (this == SMALL_DASHES) {
				return FGEIconLibrary.SMALL_DASHES_ICON;
			} else if (this == MEDIUM_DASHES) {
				return FGEIconLibrary.MEDIUM_DASHES_ICON;
			} else if (this == MEDIUM_SPACED_DASHES) {
				return FGEIconLibrary.MEDIUM_SPACED_DASHES_ICON;
			} else if (this == BIG_DASHES) {
				return FGEIconLibrary.BIG_DASHES_ICON;
			} else if (this == DOTS_DASHES) {
				return FGEIconLibrary.DOTS_DASHES_ICON;
			} else if (this == DOT_LINES_DASHES) {
				return FGEIconLibrary.DOTS_LINES_DASHES_ICON;
			}
			return null;
		}

		/**
		 * Returns the array representing the lengths of the dash segments. Alternate entries in the array represent the user space lengths
		 * of the opaque and transparent segments of the dashes. As the pen moves along the outline of the <code>Shape</code> to be stroked,
		 * the user space distance that the pen travels is accumulated. The distance value is used to index into the dash array. The pen is
		 * opaque when its current cumulative distance maps to an even element of the dash array and transparent otherwise.
		 * 
		 * @return the dash array.
		 */
		public float[] getDashArray() {
			if (this == PLAIN_STROKE) {
				return null;
			} else if (this == SMALL_DASHES) {
				float[] da = { 3, 2 };
				return da;
			} else if (this == MEDIUM_DASHES) {
				float[] da = { 5, 3 };
				return da;
			} else if (this == MEDIUM_SPACED_DASHES) {
				float[] da = { 5, 5 };
				return da;
			} else if (this == BIG_DASHES) {
				float[] da = { 10, 5 };
				return da;
			} else if (this == DOTS_DASHES) {
				float[] da = { 1, 4 };
				return da;
			} else if (this == DOT_LINES_DASHES) {
				float[] da = { 15, 3, 3, 3 };
				return da;
			}
			return null;
		}

		/**
		 * Returns the current dash phase. The dash phase is a distance specified in user coordinates that represents an offset into the
		 * dashing pattern. In other words, the dash phase defines the point in the dashing pattern that will correspond to the beginning of
		 * the stroke.
		 * 
		 * @return the dash phase as a <code>float</code> value.
		 */
		public float getDashPhase() {
			if (this == PLAIN_STROKE) {
				return 0;
			} else if (this == SMALL_DASHES) {
				return 0;
			} else if (this == MEDIUM_DASHES) {
				return 0;
			} else if (this == MEDIUM_SPACED_DASHES) {
				return 0;
			} else if (this == BIG_DASHES) {
				return 0;
			} else if (this == DOTS_DASHES) {
				return 0;
			} else if (this == DOT_LINES_DASHES) {
				return 0;
			}
			return 0;
		}

	}

	public ForegroundStyle() {
		super();
		noStroke = false;
		color = Color.BLACK;
		lineWidth = 1.0;
		joinStyle = JoinStyle.JOIN_MITER;
		capStyle = CapStyle.CAP_SQUARE;
		dashStyle = DashStyle.PLAIN_STROKE;
	}

	public ForegroundStyle(Color aColor) {
		this();
		color = aColor;
	}

	public static ForegroundStyle makeDefault() {
		return new ForegroundStyle();
	}

	public static ForegroundStyle makeNone() {
		ForegroundStyle returned = new ForegroundStyle();
		returned.setNoStroke(true);
		return returned;
	}

	public static ForegroundStyle makeStyle(Color aColor) {
		return new ForegroundStyle(aColor);
	}

	public static ForegroundStyle makeStyle(Color aColor, float aLineWidth) {
		ForegroundStyle returned = new ForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		return returned;
	}

	public static ForegroundStyle makeStyle(Color aColor, float aLineWidth, JoinStyle joinStyle, CapStyle capStyle, DashStyle dashStyle) {
		ForegroundStyle returned = new ForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setJoinStyle(joinStyle);
		returned.setCapStyle(capStyle);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	public static ForegroundStyle makeStyle(Color aColor, float aLineWidth, DashStyle dashStyle) {
		ForegroundStyle returned = new ForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	public CapStyle getCapStyle() {
		return capStyle;
	}

	public void setCapStyle(CapStyle aCapStyle) {
		if (requireChange(this.color, aCapStyle)) {
			CapStyle oldCapStyle = capStyle;
			this.capStyle = aCapStyle;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(Parameters.capStyle, oldCapStyle, aCapStyle));
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color aColor) {
		if (requireChange(this.color, aColor)) {
			java.awt.Color oldColor = color;
			this.color = aColor;
			setChanged();
			notifyObservers(new FGENotification(Parameters.color, oldColor, aColor));
		}
	}

	public void setColorNoNotification(Color aColor) {
		this.color = aColor;
	}

	public DashStyle getDashStyle() {
		return dashStyle;
	}

	public void setDashStyle(DashStyle aDashStyle) {
		if (requireChange(this.color, aDashStyle)) {
			DashStyle oldDashStyle = dashStyle;
			this.dashStyle = aDashStyle;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(Parameters.dashStyle, oldDashStyle, dashStyle));
		}
	}

	public JoinStyle getJoinStyle() {
		return joinStyle;
	}

	public void setJoinStyle(JoinStyle aJoinStyle) {
		if (requireChange(this.joinStyle, aJoinStyle)) {
			JoinStyle oldJoinStyle = joinStyle;
			this.joinStyle = aJoinStyle;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(Parameters.joinStyle, oldJoinStyle, aJoinStyle));
		}
	}

	public double getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(double aLineWidth) {
		if (requireChange(this.lineWidth, aLineWidth)) {
			double oldLineWidth = lineWidth;
			lineWidth = aLineWidth;
			stroke = null;
			setChanged();
			notifyObservers(new FGENotification(Parameters.lineWidth, oldLineWidth, aLineWidth));
		}
	}

	public boolean getNoStroke() {
		return noStroke;
	}

	public void setNoStroke(boolean aFlag) {
		if (requireChange(this.noStroke, aFlag)) {
			boolean oldValue = noStroke;
			this.noStroke = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.noStroke, oldValue, aFlag));
		}
	}

	public Stroke getStroke(double scale) {
		if (stroke == null || scale != strokeScale) {
			if (dashStyle == DashStyle.PLAIN_STROKE) {
				stroke = new BasicStroke((float) (lineWidth * scale), capStyle.ordinal(), joinStyle.ordinal());
			} else {
				float[] scaledDashArray = new float[dashStyle.getDashArray().length];
				for (int i = 0; i < dashStyle.getDashArray().length; i++) {
					scaledDashArray[i] = (float) (dashStyle.getDashArray()[i] * scale * lineWidth);
				}
				float scaledDashedPhase = (float) (dashStyle.getDashPhase() * scale * lineWidth);
				stroke = new BasicStroke((float) (lineWidth * scale), capStyle.ordinal(), joinStyle.ordinal(), 10, scaledDashArray,
						scaledDashedPhase);
			}
			strokeScale = scale;
		}
		return stroke;
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
	public ForegroundStyle clone() {
		try {
			ForegroundStyle returned = (ForegroundStyle) super.clone();
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "ForegroundStyle " + Integer.toHexString(hashCode()) + " [noStroke=" + noStroke + ",lineWidth=" + lineWidth + ",color="
				+ color + ",joinStyle=" + joinStyle + ",capStyle=" + capStyle + ",dashStyle=" + dashStyle + ",useTransparency="
				+ useTransparency + ",transparencyLevel=" + transparencyLevel + "]";
	}

	public String toNiceString() {
		if (getNoStroke()) {
			return FlexoLocalization.localizedForKey(GraphicalRepresentation.LOCALIZATION, "no_stroke");
		} else {
			return lineWidth + "pt, " + color;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ForegroundStyle) {
			logger.info("Equals called for ForegroundStyle !!!!!!!!!");
			ForegroundStyle fs = (ForegroundStyle) obj;
			return (getNoStroke() == fs.getNoStroke() && getLineWidth() == fs.getLineWidth() && getColor() == fs.getColor()
					&& getJoinStyle() == fs.getJoinStyle() && getCapStyle() == fs.getCapStyle() && getDashStyle() == fs.getDashStyle()
					&& getUseTransparency() == fs.getUseTransparency() && getTransparencyLevel() == fs.getTransparencyLevel());
		}
		return super.equals(obj);
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
