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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.xml.DefaultStringEncoder;
import org.openflexo.toolbox.ColorUtils;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.StringEncoder;

public class FGEUtils {

	private static final Logger logger = Logger.getLogger(FGEUtils.class.getPackage().getName());

	public static final double PHI = (Math.sqrt(5) + 1) / 2;

	public static final Color NICE_RED = new Color(255, 153, 153);
	public static final Color NICE_BLUE = new Color(153, 153, 255);
	public static final Color NICE_YELLOW = new Color(255, 255, 153);
	public static final Color NICE_PINK = new Color(255, 204, 255);
	public static final Color NICE_GREEN = new Color(153, 255, 153);
	public static final Color NICE_TURQUOISE = new Color(153, 255, 255);
	public static final Color NICE_ORANGE = new Color(255, 204, 102);

	public static final Color NICE_DARK_GREEN = new Color(0, 153, 51);
	public static final Color NICE_BROWN = new Color(186, 112, 0);
	public static final Color NICE_BORDEAU = new Color(153, 0, 51);

	/**
	 * Returns the color that has the best contrast ratio with the oppositeColor. The algorthm used is the one considered by the W3C for
	 * WCAG 2.0.
	 * 
	 * @param oppositeColor
	 *            - the opposite color to consider
	 * @param colors
	 *            - all colors amongst which to choose.
	 * @return the color the best contrast ratio compared to opposite color. See
	 *         http://www.w3.org/TR/2007/WD-WCAG20-TECHS-20070517/Overview.html#G18
	 */
	public static Color chooseBestColor(Color oppositeColor, Color... colors) {
		// int bestContrast = Integer.MIN_VALUE;
		double contrastRatio = 0;
		Color returned = null;
		for (Color c : colors) {
			/*
			 * int colorConstrastDiff = getColorConstrastDiff(oppositeColor,c); int colorBrightnessDiff =
			 * getColorBrightnessDiff(oppositeColor,c); int colorContrast = colorConstrastDiff+colorBrightnessDiff; if (colorContrast >
			 * bestContrast) { bestContrast = colorContrast; returned = c; }
			 */
			double d = ColorUtils.getContrastRatio(oppositeColor, c);
			if (d > contrastRatio) {
				contrastRatio = d;
				returned = c;
			}
		}
		return returned;
	}

	public static Color emphasizedColor(Color c) {
		if (c == null) {
			return c;
		}
		double l = ColorUtils.getRelativeLuminance(c);
		Color test = c, best = c;
		double ratio = 0, bestRatio = -1;
		int count = -1;
		if (l > 0.5) {
			do {
				test = test.darker();
				ratio = ColorUtils.getContrastRatio(test, c);
				if (ratio > bestRatio) {
					bestRatio = ratio;
					best = test;
				}
				count++;
			} while (ratio < 5 && count < 10);
		} else {
			test = new Color(test.getRed() == 0 ? 5 : test.getRed(), test.getGreen() == 0 ? 5 : test.getGreen(), test.getBlue() == 0 ? 5
					: test.getBlue());
			do {
				test = test.brighter();
				ratio = ColorUtils.getContrastRatio(test, c);
				if (ratio > bestRatio) {
					bestRatio = ratio;
					best = test;
				}
				count++;
			} while (ratio < 5 && count < 10);
		}
		return best;
	}

	/**
	 * Return flag indicating if supplied AffineTransform is valid (does not contain Infinity of NaN values)
	 * 
	 * @param at
	 * @return
	 */
	public static boolean checkAffineTransform(AffineTransform at) {
		if (!checkDoubleIsAValue(at.getScaleX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getScaleY())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getTranslateX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getTranslateY())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getShearX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getShearY())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getScaleX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getScaleX())) {
			return false;
		}
		return true;
	}

	public static boolean checkDoubleIsAValue(Double v) {
		return !(v.isInfinite() || v.isNaN());
	}

	public static final boolean doubleEquals(double v1, double v2) {
		return Math.abs(v1 - v2) < FGEGeometricObject.EPSILON;
	}

	public static final Color mergeColors(Color color1, Color color2) {
		return new Color((color1.getRed() + color2.getRed()) / 2, (color1.getGreen() + color2.getGreen()) / 2,
				(color1.getBlue() + color2.getBlue()) / 2);
	}

	public static double getSlope(FGEPoint p1, FGEPoint p2) {
		double x = p2.x - p1.x;
		double y = p2.y - p1.y;
		// GPO: In some rare cases, it seems that this returns -PI/2 instead of 3PI/2
		// Example: new FGESegment(0.05369127516778524,0.06451612903225806,0.053691275167785234,-0.5806451612903225)
		// FGESegment.getAngle() has been fixed since but if it re-appears, maybe we should uncomment the code below
		// Mainly, calling methods should take into account that this method can return a value -PI/2 or 3PI/2
		return Math.atan2(/* Math.abs(x)<EPSILON?0:x, Math.abs(y)<EPSILON?0:y */x, y) + Math.PI / 2;
	}

	public static void main(String[] args) {
		Color yellow = new Color(255, 255, 204);
		Color green = new Color(153, 255, 204);
		Color grey = new Color(192, 192, 192);
		System.err.println(emphasizedColor(yellow));
		System.err.println(emphasizedColor(green));
		System.err.println(emphasizedColor(grey));
	}

	// Instantiate a new localizer in directory src/dev/resources/FGELocalized
	// Little hack to be removed: linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FGELocalized"),
			new LocalizedDelegateGUIImpl(new FileResource("Localized"), null, false), true);

	public static BindingFactory BINDING_FACTORY = new GRBindingFactory();

	public static final List<Object> EMPTY_VECTOR = Collections.emptyList();

	public static final List<GraphicalRepresentation<?>> EMPTY_GR_VECTOR = Collections.emptyList();

	public static GraphicalRepresentation<?> getFirstCommonAncestor(GraphicalRepresentation<?> child1, GraphicalRepresentation<?> child2) {
		if (!child1.isValidated()) {
			return null;
		}
		if (!child2.isValidated()) {
			return null;
		}
		return getFirstCommonAncestor(child1, child2, false);
	}

	public static GraphicalRepresentation<?> getFirstCommonAncestor(GraphicalRepresentation<?> child1, GraphicalRepresentation<?> child2,
			boolean includeCurrent) {
		if (!child1.isValidated()) {
			return null;
		}
		if (!child2.isValidated()) {
			return null;
		}
		List<Object> ancestors1 = child1.getAncestors(true);
		if (includeCurrent) {
			ancestors1.add(0, child1);
		}
		List<Object> ancestors2 = child2.getAncestors(true);
		if (includeCurrent) {
			ancestors2.add(0, child2);
		}
		for (int i = 0; i < ancestors1.size(); i++) {
			Object o1 = ancestors1.get(i);
			if (ancestors2.contains(o1)) {
				return child1.getGraphicalRepresentation(o1);
			}
		}
		return null;
	}

	public static boolean areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation<?> element1, GraphicalRepresentation<?> element2) {
		if (!element1.isValidated()) {
			return false;
		}
		if (!element2.isValidated()) {
			return false;
		}
		return getFirstCommonAncestor(element1, element2) != null;
	}

	/**
	 * Convert a point relative to the view representing source drawable, with supplied scale, in a point relative to the view representing
	 * destination drawable
	 * 
	 * @param source
	 *            graphical representation of drawable represented in the source view
	 * @param point
	 *            point to convert
	 * @param destination
	 *            graphical representation of drawable represented in the destination view
	 * @param scale
	 *            the scale to be used to perform this conversion
	 * @return
	 */
	public static Point convertPoint(GraphicalRepresentation<?> source, Point point, GraphicalRepresentation<?> destination, double scale) {
		if (source != destination) {
			AffineTransform at = convertCoordinatesAT(source, destination, scale);
			return (Point) at.transform(point, new Point());
		} else {
			return new Point(point);
		}
	}

	/**
	 * Convert a rectangle coordinates expressed in the view representing source drawable, with supplied scale, in coordinates expressed in
	 * the view representing destination drawable
	 * 
	 * @param source
	 *            graphical representation of drawable represented in the source view
	 * @param aRectangle
	 *            rectangle to convert
	 * @param destination
	 *            graphical representation of drawable represented in the destination view
	 * @param scale
	 *            the scale to be used to perform this conversion
	 * @return
	 */
	public static Rectangle convertRectangle(GraphicalRepresentation<?> source, Rectangle aRectangle,
			GraphicalRepresentation<?> destination, double scale) {
		Point point = new Point(aRectangle.x, aRectangle.y);
		if (source != destination) {
			point = convertPoint(source, point, destination, scale);
		}
		return new Rectangle(point.x, point.y, aRectangle.width, aRectangle.height);
	}

	/**
	 * Build and return a new AffineTransform allowing to perform coordinates conversion from the view representing source drawable, with
	 * supplied scale, to the view representing destination drawable
	 * 
	 * @param source
	 * @param destination
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertCoordinatesAT(GraphicalRepresentation<?> source, GraphicalRepresentation<?> destination,
			double scale) {
		if (source != destination) {
			AffineTransform returned = convertFromDrawableToDrawingAT(source, scale);
			returned.preConcatenate(convertFromDrawingToDrawableAT(destination, scale));
			return returned;
		} else {
			return new AffineTransform();
		}
	}

	/**
	 * Convert a point defined in coordinates system related to "source" graphical representation to related drawing graphical
	 * representation
	 * 
	 * @param destination
	 * @param point
	 * @param scale
	 * @return
	 */
	public static Point convertPointFromDrawableToDrawing(GraphicalRepresentation<?> source, Point point, double scale) {
		AffineTransform at = convertFromDrawableToDrawingAT(source, scale);
		return (Point) at.transform(point, new Point());
	}

	/**
	 * 
	 * Build a new AffineTransform allowing to convert coordinates from coordinate system defined by "source" graphical representation to
	 * related drawing graphical representation
	 * 
	 * @param source
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertFromDrawableToDrawingAT(GraphicalRepresentation<?> source, double scale) {
		double tx = 0;
		double ty = 0;
		if (source == null) {
			logger.warning("Called convertFromDrawableToDrawingAT() for null graphical representation (source)");
			return new AffineTransform();
		}
		Object current = source.getDrawable();
		while (current != source.getDrawing().getModel()) {
			if (source.getDrawing().getGraphicalRepresentation(current) == null) {
				throw new IllegalArgumentException(
						"Drawable "
								+ current
								+ " has no graphical representation.\nDevelopper note: Use GraphicalRepresentationUtils.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			if (source.getDrawing().getContainer(current) == null) {
				throw new IllegalArgumentException(
						"Drawable "
								+ current
								+ " has no container.\nDevelopper note: Use GraphicalRepresentationUtils.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx += source.getDrawing().getGraphicalRepresentation(current).getViewX(scale);
			ty += source.getDrawing().getGraphicalRepresentation(current).getViewY(scale);
			current = source.getDrawing().getContainer(current);
		}
		return AffineTransform.getTranslateInstance(tx, ty);
	}

	/**
	 * Convert a point defined in related drawing graphical representation coordinates system to the one defined by "destination" graphical
	 * representation
	 * 
	 * @param destination
	 * @param point
	 * @param scale
	 * @return
	 */
	public static Point convertPointFromDrawingToDrawable(GraphicalRepresentation<?> destination, Point point, double scale) {
		AffineTransform at = convertFromDrawingToDrawableAT(destination, scale);
		return (Point) at.transform(point, new Point());
	}

	/**
	 * 
	 * Build a new AffineTransform allowing to convert coordinates from coordinate system defined by related drawing graphical
	 * representation to the one defined by "destination" graphical representation
	 * 
	 * @param destination
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertFromDrawingToDrawableAT(GraphicalRepresentation<?> destination, double scale) {
		double tx = 0;
		double ty = 0;
		if (destination == null) {
			logger.warning("Called convertFromDrawingToDrawableAT() for null graphical representation (destination)");
			return new AffineTransform();
		}
		Object current = destination.getDrawable();
		while (current != destination.getDrawing().getModel()) {
			if (destination.getDrawing().getContainer(current) == null) {
				throw new IllegalArgumentException(
						"Drawable "
								+ current
								+ " has no container.\nDevelopper note: Use GraphicalRepresentationUtils.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx -= destination.getDrawing().getGraphicalRepresentation(current).getViewX(scale);
			ty -= destination.getDrawing().getGraphicalRepresentation(current).getViewY(scale);
			current = destination.getDrawing().getContainer(current);
		}
		return AffineTransform.getTranslateInstance(tx, ty);
	}

	/**
	 * Convert a point relative to the normalized coordinates system from source drawable to the normalized coordinates system from
	 * destination drawable
	 * 
	 * @param source
	 * @param point
	 * @param destination
	 * @return
	 */
	public static FGEPoint convertNormalizedPoint(GraphicalRepresentation<?> source, FGEPoint point, GraphicalRepresentation<?> destination) {
		if (point == null) {
			return null;
		}
		AffineTransform at = convertNormalizedCoordinatesAT(source, destination);
		return (FGEPoint) at.transform(point, new FGEPoint());
	}

	/**
	 * Build and return an AffineTransform allowing to convert locations relative to the normalized coordinates system from source drawable
	 * to the normalized coordinates system from destination drawable
	 * 
	 * @param source
	 * @param point
	 * @param destination
	 * @return
	 */
	public static AffineTransform convertNormalizedCoordinatesAT(GraphicalRepresentation<?> source, GraphicalRepresentation<?> destination) {
		if (source == null) {
			logger.warning("null source !");
		}
		AffineTransform returned = source.convertNormalizedPointToViewCoordinatesAT(1.0);
		returned.preConcatenate(convertCoordinatesAT(source, destination, 1.0));
		returned.preConcatenate(destination.convertViewCoordinatesToNormalizedPointAT(1.0));
		return returned;
	}

	public static final StringEncoder.Converter<FGEPoint> POINT_CONVERTER = new StringEncoder.Converter<FGEPoint>(FGEPoint.class) {
		@Override
		public FGEPoint convertFromString(String value) {
			try {
				FGEPoint returned = new FGEPoint();
				StringTokenizer st = new StringTokenizer(value, ",");
				if (st.hasMoreTokens()) {
					returned.x = Double.parseDouble(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.y = Double.parseDouble(st.nextToken());
				}
				return returned;
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(FGEPoint aPoint) {
			if (aPoint != null) {
				return aPoint.x + "," + aPoint.y;
			} else {
				return null;
			}
		}
	};

	public static final DefaultStringEncoder.Converter<FGEPoint> POINT_CONVERTER_2 = new DefaultStringEncoder.Converter<FGEPoint>(
			FGEPoint.class) {
		@Override
		public FGEPoint convertFromString(String value, ModelFactory factory) {
			try {
				FGEPoint returned = new FGEPoint();
				StringTokenizer st = new StringTokenizer(value, ",");
				if (st.hasMoreTokens()) {
					returned.x = Double.parseDouble(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.y = Double.parseDouble(st.nextToken());
				}
				return returned;
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(FGEPoint aPoint) {
			if (aPoint != null) {
				return aPoint.x + "," + aPoint.y;
			} else {
				return null;
			}
		}
	};

	public static final StringEncoder.Converter<FGERectPolylin> RECT_POLYLIN_CONVERTER = new StringEncoder.Converter<FGERectPolylin>(
			FGERectPolylin.class) {
		@Override
		public FGERectPolylin convertFromString(String value) {
			try {
				Vector<FGEPoint> points = new Vector<FGEPoint>();
				StringTokenizer st = new StringTokenizer(value, ";");
				while (st.hasMoreTokens()) {
					String nextPoint = st.nextToken();
					points.add(POINT_CONVERTER.convertFromString(nextPoint));
				}
				return new FGERectPolylin(points);
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(FGERectPolylin aPolylin) {
			if (aPolylin != null) {
				StringBuffer sb = new StringBuffer();
				boolean isFirst = true;
				for (FGEPoint pt : aPolylin.getPoints()) {
					if (!isFirst) {
						sb.append(";");
					}
					sb.append(POINT_CONVERTER.convertToString(pt));
					isFirst = false;
				}
				return sb.toString();
			} else {
				return null;
			}
		}
	};

	public static final DefaultStringEncoder.Converter<FGESteppedDimensionConstraint> STEPPED_DIMENSION_CONVERTER = new DefaultStringEncoder.Converter<FGESteppedDimensionConstraint>(
			FGESteppedDimensionConstraint.class) {
		@Override
		public FGESteppedDimensionConstraint convertFromString(String value, ModelFactory factory) {
			try {
				Double hStep = null;
				Double vStep = null;
				StringTokenizer st = new StringTokenizer(value, ",");
				if (st.hasMoreTokens()) {
					hStep = Double.parseDouble(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					vStep = Double.parseDouble(st.nextToken());
				}
				return new FGESteppedDimensionConstraint(hStep, vStep);
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a FGESteppedDimensionConstraint:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(FGESteppedDimensionConstraint aDim) {
			if (aDim != null) {
				return aDim.getHorizontalStep() + "," + aDim.getVerticalStep();
			} else {
				return null;
			}
		}
	};

	/**
	 * This is the FGE model factory shared by all FGE tools
	 */
	public static final FGEModelFactory TOOLS_FACTORY = new FGEModelFactory();

}
