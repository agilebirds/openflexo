/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
import java.awt.Stroke;

import javax.swing.ImageIcon;

import org.openflexo.fge.FGEUtils.HasIcon;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent foreground properties (line properties) which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ForegroundStyle")
public interface ForegroundStyle extends FGEStyle {

	// Property keys

	@PropertyIdentifier(type = Color.class)
	public static final String COLOR_KEY = "color";
	@PropertyIdentifier(type = Double.class)
	public static final String LINE_WIDTH_KEY = "lineWidth";
	@PropertyIdentifier(type = CapStyle.class)
	public static final String CAP_STYLE_KEY = "capStyle";
	@PropertyIdentifier(type = JoinStyle.class)
	public static final String JOIN_STYLE_KEY = "joinStyle";
	@PropertyIdentifier(type = DashStyle.class)
	public static final String DASH_STYLE_KEY = "dashStyle";
	@PropertyIdentifier(type = Boolean.class)
	public static final String NO_STROKE_KEY = "noStroke";
	@PropertyIdentifier(type = Boolean.class)
	public static final String USE_TRANSPARENCY_KEY = "useTransparency";
	@PropertyIdentifier(type = Float.class)
	public static final String TRANSPARENCY_LEVEL_KEY = "transparencyLevel";

	public static GRParameter<Color> COLOR = GRParameter.getGRParameter(ForegroundStyle.class, COLOR_KEY, Color.class);
	public static GRParameter<Double> LINE_WIDTH = GRParameter.getGRParameter(ForegroundStyle.class, LINE_WIDTH_KEY, Double.class);
	public static GRParameter<CapStyle> CAP_STYLE = GRParameter.getGRParameter(ForegroundStyle.class, CAP_STYLE_KEY, CapStyle.class);
	public static GRParameter<JoinStyle> JOIN_STYLE = GRParameter.getGRParameter(ForegroundStyle.class, JOIN_STYLE_KEY, JoinStyle.class);
	public static GRParameter<DashStyle> DASH_STYLE = GRParameter.getGRParameter(ForegroundStyle.class, DASH_STYLE_KEY, DashStyle.class);
	public static GRParameter<Boolean> NO_STROKE = GRParameter.getGRParameter(ForegroundStyle.class, NO_STROKE_KEY, Boolean.class);
	public static GRParameter<Boolean> USE_TRANSPARENCY = GRParameter.getGRParameter(ForegroundStyle.class, USE_TRANSPARENCY_KEY,
			Boolean.class);
	public static GRParameter<Float> TRANSPARENCY_LEVEL = GRParameter.getGRParameter(ForegroundStyle.class, TRANSPARENCY_LEVEL_KEY,
			Float.class);

	/*public static enum Parameters implements GRParameter {
		color, lineWidth, capStyle, joinStyle, dashStyle, noStroke, useTransparency, transparencyLevel
	}*/

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
		 * of the opaque and transparent segments of the dashes. As the pen moves along the outline of the <code>ShapeSpecification</code>
		 * to be stroked, the user space distance that the pen travels is accumulated. The distance value is used to index into the dash
		 * array. The pen is opaque when its current cumulative distance maps to an even element of the dash array and transparent
		 * otherwise.
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

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public Color getColor();

	@Setter(value = COLOR_KEY)
	public void setColor(Color aColor);

	@Getter(value = LINE_WIDTH_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getLineWidth();

	@Setter(value = LINE_WIDTH_KEY)
	public void setLineWidth(double aLineWidth);

	@Getter(value = CAP_STYLE_KEY)
	@XMLAttribute
	public CapStyle getCapStyle();

	@Setter(value = CAP_STYLE_KEY)
	public void setCapStyle(CapStyle aCapStyle);

	@Getter(value = JOIN_STYLE_KEY)
	@XMLAttribute
	public JoinStyle getJoinStyle();

	@Setter(value = JOIN_STYLE_KEY)
	public void setJoinStyle(JoinStyle aJoinStyle);

	@Getter(value = DASH_STYLE_KEY)
	@XMLAttribute
	public DashStyle getDashStyle();

	@Setter(value = DASH_STYLE_KEY)
	public void setDashStyle(DashStyle aDashStyle);

	@Getter(value = NO_STROKE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getNoStroke();

	@Setter(value = NO_STROKE_KEY)
	public void setNoStroke(boolean aFlag);

	@Getter(value = TRANSPARENCY_LEVEL_KEY, defaultValue = "0.5")
	@XMLAttribute
	public float getTransparencyLevel();

	@Setter(value = TRANSPARENCY_LEVEL_KEY)
	public void setTransparencyLevel(float aLevel);

	@Getter(value = USE_TRANSPARENCY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getUseTransparency();

	@Setter(value = USE_TRANSPARENCY_KEY)
	public void setUseTransparency(boolean aFlag);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public void setColorNoNotification(Color aColor);

	public Stroke getStroke(double scale);

	// public ForegroundStyle clone();

	public String toNiceString();

}
