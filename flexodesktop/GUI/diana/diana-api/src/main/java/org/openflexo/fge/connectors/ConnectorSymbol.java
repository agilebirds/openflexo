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
package org.openflexo.fge.connectors;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils.HasIcon;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;

public interface ConnectorSymbol {

	public static enum StartSymbolType implements ConnectorSymbol, HasIcon {
		NONE,
		ARROW,
		PLAIN_ARROW,
		FILLED_ARROW,
		PLAIN_DOUBLE_ARROW,
		FILLED_DOUBLE_ARROW,
		PLAIN_CIRCLE,
		FILLED_CIRCLE,
		PLAIN_SQUARE,
		FILLED_SQUARE,
		PLAIN_DIAMOND,
		PLAIN_LONG_DIAMOND,
		FILLED_DIAMOND,
		DEFAULT_FLOW;

		@Override
		public ImageIcon getIcon() {
			if (this == NONE) {
				return FGEIconLibrary.START_NONE_ICON;
			} else if (this == ARROW) {
				return FGEIconLibrary.START_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return FGEIconLibrary.START_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return FGEIconLibrary.START_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return FGEIconLibrary.START_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return FGEIconLibrary.START_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return FGEIconLibrary.START_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return FGEIconLibrary.START_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return FGEIconLibrary.START_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return FGEIconLibrary.START_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND || this == PLAIN_LONG_DIAMOND) {
				return FGEIconLibrary.START_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return FGEIconLibrary.START_FILLED_DIAMOND_ICON;
			} else if (this == DEFAULT_FLOW) {
				return FGEIconLibrary.DEFAULT_FLOW_ICON;
			}
			return null;
		}

		@Override
		public FGEArea getSymbol() {
			if (this == NONE) {
				return SymbolShapes.EMPTY_AREA;
			} else if (this == ARROW) {
				return SymbolShapes.BASIC_ARROW;
			} else if (this == PLAIN_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == FILLED_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == PLAIN_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == FILLED_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == PLAIN_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == FILLED_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == PLAIN_DIAMOND) {
				return SymbolShapes.DIAMOND;
			} else if (this == PLAIN_LONG_DIAMOND) {
				return SymbolShapes.LONG_DIAMOND;
			} else if (this == FILLED_DIAMOND) {
				return SymbolShapes.DIAMOND;
			} else if (this == DEFAULT_FLOW) {
				return SymbolShapes.SLASH;
			}
			return null;
		}

		@Override
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeEmptyBackground();
			} else if (this == ARROW) {
				return factory.makeEmptyBackground();
			} else if (this == PLAIN_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_CIRCLE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_CIRCLE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_SQUARE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_SQUARE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == PLAIN_LONG_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DIAMOND) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == DEFAULT_FLOW) {
				return factory.makeColoredBackground(fgColor);
			}
			return null;

		}

		@Override
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeNoneForegroundStyle();
			} else if (this == ARROW) {
				return fgStyle; // Use connector fg style
			} else {
				return factory.makeForegroundStyle(fgStyle.getColor());
			}
		}

	}

	public static enum EndSymbolType implements ConnectorSymbol, HasIcon {
		NONE,
		ARROW,
		PLAIN_ARROW,
		FILLED_ARROW,
		PLAIN_DOUBLE_ARROW,
		FILLED_DOUBLE_ARROW,
		PLAIN_CIRCLE,
		FILLED_CIRCLE,
		PLAIN_SQUARE,
		FILLED_SQUARE,
		PLAIN_DIAMOND,
		FILLED_DIAMOND;

		@Override
		public ImageIcon getIcon() {
			if (this == NONE) {
				return FGEIconLibrary.END_NONE_ICON;
			} else if (this == ARROW) {
				return FGEIconLibrary.END_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return FGEIconLibrary.END_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return FGEIconLibrary.END_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return FGEIconLibrary.END_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return FGEIconLibrary.END_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return FGEIconLibrary.END_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return FGEIconLibrary.END_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return FGEIconLibrary.END_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return FGEIconLibrary.END_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND) {
				return FGEIconLibrary.END_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return FGEIconLibrary.END_FILLED_DIAMOND_ICON;
			}
			return null;
		}

		@Override
		public FGEArea getSymbol() {
			if (this == NONE) {
				return SymbolShapes.EMPTY_AREA;
			} else if (this == ARROW) {
				return SymbolShapes.BASIC_ARROW;
			} else if (this == PLAIN_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == FILLED_ARROW) {
				return SymbolShapes.ARROW;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW;
			} else if (this == PLAIN_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == FILLED_CIRCLE) {
				return SymbolShapes.CIRCLE;
			} else if (this == PLAIN_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == FILLED_SQUARE) {
				return SymbolShapes.SQUARE;
			} else if (this == PLAIN_DIAMOND) {
				return SymbolShapes.DIAMOND;
			} else if (this == FILLED_DIAMOND) {
				return SymbolShapes.DIAMOND;
			}
			return null;
		}

		@Override
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeEmptyBackground();
			} else if (this == ARROW) {
				return factory.makeEmptyBackground();
			} else if (this == PLAIN_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_CIRCLE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_CIRCLE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_SQUARE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_SQUARE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DIAMOND) {
				return factory.makeColoredBackground(fgColor);
			}
			return null;

		}

		@Override
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeNoneForegroundStyle();
			} else if (this == ARROW) {
				return fgStyle; // Use connector fg style
			} else {
				return factory.makeForegroundStyle(fgStyle.getColor());
			}
		}

	}

	public static enum MiddleSymbolType implements ConnectorSymbol, HasIcon {
		NONE,
		ARROW,
		PLAIN_ARROW,
		FILLED_ARROW,
		PLAIN_DOUBLE_ARROW,
		FILLED_DOUBLE_ARROW,
		PLAIN_CIRCLE,
		FILLED_CIRCLE,
		PLAIN_SQUARE,
		FILLED_SQUARE,
		PLAIN_DIAMOND,
		FILLED_DIAMOND;

		@Override
		public ImageIcon getIcon() {
			if (this == NONE) {
				return FGEIconLibrary.MIDDLE_NONE_ICON;
			} else if (this == ARROW) {
				return FGEIconLibrary.MIDDLE_ARROW_ICON;
			} else if (this == PLAIN_ARROW) {
				return FGEIconLibrary.MIDDLE_PLAIN_ARROW_ICON;
			} else if (this == FILLED_ARROW) {
				return FGEIconLibrary.MIDDLE_FILLED_ARROW_ICON;
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return FGEIconLibrary.MIDDLE_PLAIN_DOUBLE_ARROW_ICON;
			} else if (this == FILLED_DOUBLE_ARROW) {
				return FGEIconLibrary.MIDDLE_FILLED_DOUBLE_ARROW_ICON;
			} else if (this == PLAIN_CIRCLE) {
				return FGEIconLibrary.MIDDLE_PLAIN_CIRCLE_ICON;
			} else if (this == FILLED_CIRCLE) {
				return FGEIconLibrary.MIDDLE_FILLED_CIRCLE_ICON;
			} else if (this == PLAIN_SQUARE) {
				return FGEIconLibrary.MIDDLE_PLAIN_SQUARE_ICON;
			} else if (this == FILLED_SQUARE) {
				return FGEIconLibrary.MIDDLE_FILLED_SQUARE_ICON;
			} else if (this == PLAIN_DIAMOND) {
				return FGEIconLibrary.MIDDLE_PLAIN_DIAMOND_ICON;
			} else if (this == FILLED_DIAMOND) {
				return FGEIconLibrary.MIDDLE_FILLED_DIAMOND_ICON;
			}
			return null;
		}

		@Override
		public FGEArea getSymbol() {
			// Translate to put the middle of the symbol at required location
			AffineTransform translator = AffineTransform.getTranslateInstance(0.5, 0);
			if (this == NONE) {
				return SymbolShapes.EMPTY_AREA.transform(translator);
			} else if (this == ARROW) {
				return SymbolShapes.BASIC_ARROW.transform(translator);
			} else if (this == PLAIN_ARROW) {
				return SymbolShapes.ARROW.transform(translator);
			} else if (this == FILLED_ARROW) {
				return SymbolShapes.ARROW.transform(translator);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW.transform(translator);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return SymbolShapes.DOUBLE_ARROW.transform(translator);
			} else if (this == PLAIN_CIRCLE) {
				return SymbolShapes.CIRCLE.transform(translator);
			} else if (this == FILLED_CIRCLE) {
				return SymbolShapes.CIRCLE.transform(translator);
			} else if (this == PLAIN_SQUARE) {
				return SymbolShapes.SQUARE.transform(translator);
			} else if (this == FILLED_SQUARE) {
				return SymbolShapes.SQUARE.transform(translator);
			} else if (this == PLAIN_DIAMOND) {
				return SymbolShapes.DIAMOND.transform(translator);
			} else if (this == FILLED_DIAMOND) {
				return SymbolShapes.DIAMOND.transform(translator);
			}
			return null;
		}

		@Override
		public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeEmptyBackground();
			} else if (this == ARROW) {
				return factory.makeEmptyBackground();
			} else if (this == PLAIN_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DOUBLE_ARROW) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DOUBLE_ARROW) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_CIRCLE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_CIRCLE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_SQUARE) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_SQUARE) {
				return factory.makeColoredBackground(fgColor);
			} else if (this == PLAIN_DIAMOND) {
				return factory.makeColoredBackground(bgColor);
			} else if (this == FILLED_DIAMOND) {
				return factory.makeColoredBackground(fgColor);
			}
			return null;

		}

		@Override
		public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory) {
			if (this == NONE) {
				return factory.makeNoneForegroundStyle();
			} else if (this == ARROW) {
				return fgStyle; // Use connector fg style
			} else {
				return factory.makeForegroundStyle(fgStyle.getColor());
			}
		}

	}

	public static class SymbolShapes {
		private static FGEArea EMPTY_AREA = new FGEEmptyArea();
		static FGEArea BASIC_ARROW = new FGEUnionArea(new FGESegment(new FGEPoint(0, 0), new FGEPoint(1, 0.5)), new FGESegment(
				new FGEPoint(1, 0.5), new FGEPoint(0, 1)));
		static FGEArea ARROW = new FGEPolygon(Filling.FILLED, new FGEPoint(0, 0.1), new FGEPoint(1, 0.5), new FGEPoint(0, 0.9));
		/*private static FGEArea CENTERED_ARROW = new FGEPolygon(
				Filling.FILLED,
				new FGEPoint(0.5,0.1),
				new FGEPoint(1.5,0.5),
				new FGEPoint(0.5,0.9));*/
		static FGEArea DOUBLE_ARROW = new FGEUnionArea(new FGEPolygon(Filling.FILLED, new FGEPoint(0, 0.2), new FGEPoint(0.5, 0.5),
				new FGEPoint(0, 0.8)), new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0.2), new FGEPoint(1.0, 0.5),
				new FGEPoint(0.5, 0.8)));
		static FGEArea CIRCLE = new FGEEllips(0, 0, 1, 1, Filling.FILLED);
		static FGEArea SQUARE = new FGERectangle(0, 0, 1, 1, Filling.FILLED);
		static FGEArea DIAMOND = new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0), new FGEPoint(1, 0.5), new FGEPoint(0.5, 1),
				new FGEPoint(0, 0.5));
		static FGEArea LONG_DIAMOND = new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0.2), new FGEPoint(1, 0.5), new FGEPoint(0.5, 0.8),
				new FGEPoint(0, 0.5));
		static FGEArea SLASH = new FGESegment(new FGEPoint(0.0, 0), new FGEPoint(0.2, 1));
	}

	public FGEArea getSymbol();

	public BackgroundStyle getBackgroundStyle(Color fgColor, Color bgColor, FGEModelFactory factory);

	public ForegroundStyle getForegroundStyle(ForegroundStyle fgStyle, FGEModelFactory factory);

}
