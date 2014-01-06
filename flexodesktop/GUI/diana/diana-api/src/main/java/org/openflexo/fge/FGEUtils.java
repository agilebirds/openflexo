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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.converter.PointConverter;
import org.openflexo.fge.converter.RectPolylinConverter;
import org.openflexo.fge.converter.SteppedDimensionConverter;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.toolbox.ColorUtils;

public class FGEUtils {

	static final Logger logger = Logger.getLogger(FGEUtils.class.getPackage().getName());

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

	public static final Color mergeColors(Color color1, Color color2) {
		return new Color((color1.getRed() + color2.getRed()) / 2, (color1.getGreen() + color2.getGreen()) / 2,
				(color1.getBlue() + color2.getBlue()) / 2);
	}

	public static void main(String[] args) {
		Color yellow = new Color(255, 255, 204);
		Color green = new Color(153, 255, 204);
		Color grey = new Color(192, 192, 192);
		System.err.println(emphasizedColor(yellow));
		System.err.println(emphasizedColor(green));
		System.err.println(emphasizedColor(grey));
	}

	public static DrawingTreeNode<?, ?> getFirstCommonAncestor(List<DrawingTreeNode<?, ?>> nodes) {
		if (nodes.size() == 0) {
			return null;
		}
		if (nodes.size() == 1) {
			return nodes.get(0).getParentNode();
		}
		DrawingTreeNode<?, ?> returned = getFirstCommonAncestor(nodes.get(0), nodes.get(1));
		for (int i = 2; i < nodes.size(); i++) {
			returned = getFirstCommonAncestor(returned, nodes.get(i), true);
		}

		return returned;
	}

	public static DrawingTreeNode<?, ?> getFirstCommonAncestor(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2) {
		if (!child1.isValidated()) {
			return null;
		}
		if (!child2.isValidated()) {
			return null;
		}
		return getFirstCommonAncestor(child1, child2, false);
	}

	public static DrawingTreeNode<?, ?> getFirstCommonAncestor(DrawingTreeNode<?, ?> child1, DrawingTreeNode<?, ?> child2,
			boolean includeCurrent) {
		if (!child1.isValidated()) {
			return null;
		}
		if (!child2.isValidated()) {
			return null;
		}
		List<DrawingTreeNode<?, ?>> ancestors1 = child1.getAncestors();
		if (includeCurrent) {
			ancestors1.add(0, child1);
		}
		List<DrawingTreeNode<?, ?>> ancestors2 = child2.getAncestors();
		if (includeCurrent) {
			ancestors2.add(0, child2);
		}
		for (int i = 0; i < ancestors1.size(); i++) {
			DrawingTreeNode<?, ?> o1 = ancestors1.get(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	public static boolean areElementsConnectedInGraphicalHierarchy(DrawingTreeNode<?, ?> element1, DrawingTreeNode<?, ?> element2) {
		if (!element1.isValidated()) {
			return false;
		}
		if (!element2.isValidated()) {
			return false;
		}
		return getFirstCommonAncestor(element1, element2) != null;
	}

	@StringConverter
	public static final Converter<DataBinding<?>> DATA_BINDING_CONVERTER = new DataBindingConverter(DataBinding.class);

	@StringConverter
	public static final Converter<FGEPoint> POINT_CONVERTER = new PointConverter(FGEPoint.class);

	@StringConverter
	public static final Converter<FGERectPolylin> RECT_POLYLIN_CONVERTER = new RectPolylinConverter(FGERectPolylin.class);

	@StringConverter
	public static final Converter<FGESteppedDimensionConstraint> STEPPED_DIMENSION_CONVERTER = new SteppedDimensionConverter(
			FGESteppedDimensionConstraint.class);

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
	public static Point convertPoint(DrawingTreeNode<?, ?> source, Point point, DrawingTreeNode<?, ?> destination, double scale) {
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
	public static Rectangle convertRectangle(DrawingTreeNode<?, ?> source, Rectangle aRectangle, DrawingTreeNode<?, ?> destination,
			double scale) {
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
	public static AffineTransform convertCoordinatesAT(DrawingTreeNode<?, ?> source, DrawingTreeNode<?, ?> destination, double scale) {
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
	public static Point convertPointFromDrawableToDrawing(DrawingTreeNode<?, ?> source, Point point, double scale) {
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
	public static AffineTransform convertFromDrawableToDrawingAT(DrawingTreeNode<?, ?> source, double scale) {
		double tx = 0;
		double ty = 0;
		if (source == null) {
			logger.warning("Called convertFromDrawableToDrawingAT() for null graphical representation (source)");
			return new AffineTransform();
		}
		DrawingTreeNode<?, ?> current = source;
		while (current != source.getDrawing().getRoot()) {
			if (current.getGraphicalRepresentation() == null) {
				throw new IllegalArgumentException(
						"DrawingTreeNode "
								+ current
								+ " has no graphical representation.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			if (current.getParentNode() == null) {
				throw new IllegalArgumentException(
						"DrawingTreeNode "
								+ current
								+ " has no container.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx += current.getViewX(scale);
			ty += current.getViewY(scale);
			current = current.getParentNode();
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
	public static Point convertPointFromDrawingToDrawable(DrawingTreeNode<?, ?> destination, Point point, double scale) {
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
	public static AffineTransform convertFromDrawingToDrawableAT(DrawingTreeNode<?, ?> destination, double scale) {
		double tx = 0;
		double ty = 0;
		if (destination == null) {
			logger.warning("Called convertFromDrawingToDrawableAT() for null graphical representation (destination)");
			return new AffineTransform();
		}
		DrawingTreeNode<?, ?> current = destination;
		while (current != destination.getDrawing().getRoot()) {
			if (current.getParentNode() == null) {
				throw new IllegalArgumentException(
						"DrawingTreeNode "
								+ current
								+ " has no container.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx -= current.getViewX(scale);
			ty -= current.getViewY(scale);
			current = current.getParentNode();
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
	public static FGEPoint convertNormalizedPoint(DrawingTreeNode<?, ?> source, FGEPoint point, DrawingTreeNode<?, ?> destination) {
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
	public static AffineTransform convertNormalizedCoordinatesAT(DrawingTreeNode<?, ?> source, DrawingTreeNode<?, ?> destination) {
		if (source == null) {
			logger.warning("null source !");
		}
		AffineTransform returned = source.convertNormalizedPointToViewCoordinatesAT(1.0);
		returned.preConcatenate(convertCoordinatesAT(source, destination, 1.0));
		returned.preConcatenate(destination.convertViewCoordinatesToNormalizedPointAT(1.0));
		return returned;
	}

	public static interface HasIcon {

		public ImageIcon getIcon();
	}

}
