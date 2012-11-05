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
import java.awt.Stroke;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.inspector.HasIcon;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represent foreground properties (line properties) which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ForegroundStyleImpl.class)
public interface ForegroundStyle extends FGEStyle {

	public static enum Parameters implements GRParameter {
		color, lineWidth, capStyle, joinStyle, dashStyle, noStroke, useTransparency, transparencyLevel
	}

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

	public abstract CapStyle getCapStyle();

	public abstract void setCapStyle(CapStyle aCapStyle);

	public abstract Color getColor();

	public abstract void setColor(Color aColor);

	public abstract void setColorNoNotification(Color aColor);

	public abstract DashStyle getDashStyle();

	public abstract void setDashStyle(DashStyle aDashStyle);

	public abstract JoinStyle getJoinStyle();

	public abstract void setJoinStyle(JoinStyle aJoinStyle);

	public abstract double getLineWidth();

	public abstract void setLineWidth(double aLineWidth);

	public abstract boolean getNoStroke();

	public abstract void setNoStroke(boolean aFlag);

	public abstract Stroke getStroke(double scale);

	public abstract float getTransparencyLevel();

	public abstract void setTransparencyLevel(float aLevel);

	public abstract boolean getUseTransparency();

	public abstract void setUseTransparency(boolean aFlag);

	public abstract ForegroundStyle clone();

	public abstract String toNiceString();

}
