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
package org.openflexo.foundation.ie.util;

import java.awt.Color;
import java.io.Serializable;

/**
 * Represents a color
 * 
 * @author sguerin
 */
public abstract class FlexoConceptualColor implements Serializable {

	public static final MainColor MAIN_COLOR = new MainColor();

	public static final TextColor TEXT_COLOR = new TextColor();

	public static final OddLineColor ODD_LINE_COLOR = new OddLineColor();

	public static final OtherLineColor OTHER_LINE_COLOR = new OtherLineColor();

	public static class MainColor extends FlexoConceptualColor {
		MainColor() {
		}
	}

	public static class TextColor extends FlexoConceptualColor {
		TextColor() {
		}
	}

	public static class OddLineColor extends FlexoConceptualColor {
		OddLineColor() {
		}
	}

	public static class OtherLineColor extends FlexoConceptualColor {
	}

	public static class CustomColor extends FlexoConceptualColor {

		protected Color _color;

		public CustomColor(Color aColor) {
			super();
			_color = aColor;
		}

		public Color getColor() {
			return _color;
		}
	}

}
