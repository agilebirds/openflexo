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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundImage.ImageBackgroundType;
import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.TextStyleImpl;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;

public abstract class FGEGraphics {

	private static final int TRANSPARENT_COMPOSITE_RULE = AlphaComposite.SRC_OVER;

	private static final Logger logger = Logger.getLogger(FGEGraphics.class.getPackage().getName());

	private DrawingController<?> _controller;
	private GraphicalRepresentation<?> gr;
	private Graphics2D g2d;

	private ForegroundStyle defaultForeground = ForegroundStyleImpl.makeDefault();
	private BackgroundStyle defaultBackground = BackgroundStyleImpl.makeEmptyBackground();
	private TextStyle defaultTextStyle = TextStyleImpl.makeDefault();

	private ForegroundStyle currentForeground = defaultForeground;
	protected BackgroundStyle currentBackground = defaultBackground;
	private TextStyle currentTextStyle = defaultTextStyle;

	public FGEGraphics(GraphicalRepresentation<?> aGraphicalRepresentation) {
		super();
		gr = aGraphicalRepresentation;
	}

	public GraphicalRepresentation<?> getGraphicalRepresentation() {
		return gr;
	}

	public DrawingController<?> getController() {
		return _controller;
	}

	public double getScale() {
		return getController().getScale();
	}

	public void delete() {
		gr = null;
		_controller = null;
		g2d = null;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	public void createGraphics(Graphics2D graphics2D, DrawingController<?> controller) {
		g2d = (Graphics2D) graphics2D.create();
		_controller = controller;
	}

	public void releaseGraphics() {
		g2d.dispose();
	}

	/**
	 * Creates a new <code>Graphics2D</code> object that is a copy of current <code>Graphics2D</code> object.
	 * 
	 * @return old <code>Graphics2D</code> object
	 */
	public Graphics2D cloneGraphics() {
		Graphics2D returned = g2d;
		g2d = (Graphics2D) g2d.create();
		return returned;
	}

	public void releaseClonedGraphics(Graphics2D oldGraphics) {
		g2d.dispose();
		g2d = oldGraphics;
	}

	public Graphics2D getGraphics() {
		return g2d;
	}

	public ForegroundStyle getDefaultForeground() {
		return defaultForeground;
	}

	public ForegroundStyle getCurrentForeground() {
		return currentForeground;
	}

	public void setDefaultForeground(ForegroundStyle aForegound) {
		defaultForeground = aForegound;
	}

	public void setStroke(Stroke aStroke) {
		g2d.setStroke(aStroke);
	}

	public void useDefaultForegroundStyle() {
		useForegroundStyle(defaultForeground);
	}

	public void useForegroundStyle(ForegroundStyle aStyle) {
		currentForeground = aStyle;
		applyCurrentForegroundStyle();
	}

	private void applyCurrentForegroundStyle() {
		if (g2d == null) {
			return; // Strange...
		}

		// logger.info("Apply "+currentForeground);

		g2d.setColor(currentForeground.getColor());
		g2d.setStroke(currentForeground.getStroke(getScale()));

		if (currentForeground.getUseTransparency()) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentForeground.getTransparencyLevel()));
		} else {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		}

	}

	protected String debugForegroundStyle() {
		BasicStroke stroke = (BasicStroke) g2d.getStroke();
		return "FS-(color=" + g2d.getColor() + ")-(with=" + stroke.getLineWidth() + ")-(transp=" + g2d.getComposite() + ")-(join="
				+ stroke.getLineJoin() + ")-(cap=" + stroke.getEndCap() + ")-(dash=" + stroke.getDashPhase() + ")";
	}

	public BackgroundStyle getDefaultBackground() {
		return defaultBackground;
	}

	public void setDefaultBackground(BackgroundStyle aBackground) {
		defaultBackground = aBackground;
	}

	public void useDefaultBackgroundStyle() {
		useBackgroundStyle(defaultBackground);
	}

	public void useBackgroundStyle(BackgroundStyle aStyle) {
		currentBackground = aStyle;
		applyCurrentBackgroundStyle();
	}

	protected void applyCurrentBackgroundStyle() {
		if (g2d == null) {
			return; // Strange...
		}

		if (currentBackground instanceof BackgroundStyle.None) {
			// Nothing to do
		} else if (currentBackground instanceof BackgroundStyle.Color) {
			g2d.setColor(((BackgroundStyle.Color) currentBackground).getColor());
		} else if (currentBackground instanceof BackgroundStyle.ColorGradient) {
			g2d.setPaint(currentBackground.getPaint(getGraphicalRepresentation(), _controller.getScale()));
		} else if (currentBackground instanceof BackgroundStyle.Texture) {
			g2d.setPaint(currentBackground.getPaint(getGraphicalRepresentation(), _controller.getScale()));
		} else if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			g2d.setPaint(currentBackground.getPaint(getGraphicalRepresentation(), _controller.getScale()));
		}

		if (currentBackground.getUseTransparency()) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentBackground.getTransparencyLevel()));
		} else {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		}

	}

	public void setDefaultTextStyle(TextStyle aTextStyle) {
		defaultTextStyle = aTextStyle;
	}

	public void useDefaultTextStyle() {
		useTextStyle(defaultTextStyle);
	}

	public void useTextStyle(TextStyle aStyle) {
		currentTextStyle = aStyle;
		applyCurrentTextStyle();
	}

	private void applyCurrentTextStyle() {
		g2d.setColor(currentTextStyle.getColor());
		g2d.setFont(currentTextStyle.getFont());
	}

	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0.0, 0.0, 1.0, 1.0);
	}

	public int getViewWidth() {
		return getViewWidth(getScale());
	}

	public int getViewHeight() {
		return getViewHeight(getScale());
	}

	public int getViewWidth(double scale) {
		return getGraphicalRepresentation().getViewWidth(scale);
	}

	public int getViewHeight(double scale) {
		return getGraphicalRepresentation().getViewHeight(scale);
	}

	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(x, y, getScale());
	}

	public final Point convertNormalizedPointToViewCoordinates(FGEPoint p) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y);
	}

	public final Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r) {
		return convertNormalizedRectangleToViewCoordinates(r.x, r.y, r.width, r.height);
	}

	public final Rectangle convertNormalizedRectangleToViewCoordinates(double x, double y, double width, double height) {
		Point p1 = convertNormalizedPointToViewCoordinates(x, y);
		Point p2 = convertNormalizedPointToViewCoordinates(x + width, y + height);
		Dimension d = new Dimension(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
		return new Rectangle(p1, d);
	}

	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(x, y, getScale());
	}

	public final FGEPoint convertViewCoordinatesToNormalizedPoint(Point p) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y);
	}

	public final FGERectangle convertViewCoordinatesToNormalizedRectangle(Rectangle r) {
		return convertViewCoordinatesToNormalizedRectangle(r.x, r.y, r.width, r.height);
	}

	public final FGERectangle convertViewCoordinatesToNormalizedRectangle(int x, int y, int width, int height) {
		FGEPoint p1 = convertViewCoordinatesToNormalizedPoint(x, y);
		FGEPoint p2 = convertViewCoordinatesToNormalizedPoint(x + width, y + height);
		FGEDimension d = new FGEDimension(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
		return new FGERectangle(p1, d, Filling.NOT_FILLED);
	}

	public Rectangle drawControlPoint(double x, double y, int size) {
		if (currentForeground.getNoStroke()) {
			return null;
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		p.x -= size;
		p.y -= size;
		g2d.fillRect(p.x, p.y, size * 2, size * 2);
		return new Rectangle(p.x, p.y, size * 2, size * 2);
	}

	public void drawPoint(FGEPoint p) {
		drawPoint(p.x, p.y);
	}

	public void drawPoint(double x, double y) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(x, y);
		p1.x -= FGEConstants.POINT_SIZE;
		p1.y -= FGEConstants.POINT_SIZE;
		Point p2 = convertNormalizedPointToViewCoordinates(x, y);
		p2.x += FGEConstants.POINT_SIZE;
		p2.y += FGEConstants.POINT_SIZE;
		Point p3 = convertNormalizedPointToViewCoordinates(x, y);
		p3.x -= FGEConstants.POINT_SIZE;
		p3.y += FGEConstants.POINT_SIZE;
		Point p4 = convertNormalizedPointToViewCoordinates(x, y);
		p4.x += FGEConstants.POINT_SIZE;
		p4.y -= FGEConstants.POINT_SIZE;
		g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		g2d.drawLine(p3.x, p3.y, p4.x, p4.y);
	}

	public void drawRoundArroundPoint(FGEPoint p, int size) {
		drawRoundArroundPoint(p.x, p.y, size);
	}

	public void drawRoundArroundPoint(double x, double y, int size) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		p.x -= size;
		p.y -= size;
		g2d.drawOval(p.x, p.y, size * 2, size * 2);
	}

	public Rectangle drawControlPoint(FGEPoint p, int size) {
		return drawControlPoint(p.x, p.y, size);
	}

	public void drawRect(double x, double y, double width, double height) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		// logger.info("drawRect() with "+debugForegroundStyle());
		// SGU: I don't understand why, but is you use non-plain stroke,
		// Rendering using drawRect is not correct !!!
		// g2d.drawRect(r.x,r.y,r.width,r.height);
		if (r.height == 0 || r.width == 0) {
			g2d.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
		} else {
			g2d.drawLine(r.x, r.y, r.x + r.width - 1, r.y);
			g2d.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height - 1);
			g2d.drawLine(r.x + r.width, r.y + r.height, r.x + 1, r.y + r.height);
			g2d.drawLine(r.x, r.y + r.height, r.x, r.y + 1);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void drawRect(FGEPoint p, FGEDimension d) {
		drawRect(p.x, p.y, d.width, d.height);
	}

	public void fillRect(double x, double y, double width, double height) {
		if (currentBackground instanceof BackgroundStyle.None) {
			return;
		}

		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);

		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			fillInShapeWithImage(r);
		} else {
			g2d.fillRect(r.x, r.y, r.width, r.height);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */
	private void fillInShapeWithImage(Shape aShape) {
		Graphics2D oldGraphics = cloneGraphics();
		g2d.clip(aShape);
		// g2d.setClip(aShape);

		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		/*if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			at.concatenate(AffineTransform.getScaleInstance(
					((BackgroundStyle.BackgroundImage)currentBackground).getScaleX(), 
					((BackgroundStyle.BackgroundImage)currentBackground).getScaleY()));
			
		}*/
		if (getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) getGraphicalRepresentation();
			at.concatenate(AffineTransform.getTranslateInstance(gr.getBorder().left, gr.getBorder().top));
		}
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			at.concatenate(AffineTransform.getTranslateInstance(((BackgroundStyle.BackgroundImage) currentBackground).getDeltaX(),
					((BackgroundStyle.BackgroundImage) currentBackground).getDeltaY()));
			if (((BackgroundStyle.BackgroundImage) currentBackground).getImageBackgroundType() == ImageBackgroundType.OPAQUE) {
				g2d.setColor(((BackgroundStyle.BackgroundImage) currentBackground).getImageBackgroundColor());
				g2d.fill(aShape);
			}
		}
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			at.concatenate(AffineTransform.getScaleInstance(((BackgroundStyle.BackgroundImage) currentBackground).getScaleX(),
					((BackgroundStyle.BackgroundImage) currentBackground).getScaleY()));

		}

		if (currentBackground.getUseTransparency()) {
			g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE, currentBackground.getTransparencyLevel()));
		} else {
			g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE));
		}
		g2d.drawImage(((BackgroundStyle.BackgroundImage) currentBackground).getImage(), at, null);

		releaseClonedGraphics(oldGraphics);
	}

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */
	public void drawImage(Image image, FGEPoint p) {
		g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE));
		Point location = convertNormalizedPointToViewCoordinates(p.x, p.y);
		// System.err.println(location);
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		at.preConcatenate(AffineTransform.getTranslateInstance(location.x, location.y));
		g2d.drawImage(image, at, null);
	}

	public void fillRect(FGEPoint p, FGEDimension d) {
		fillRect(p.x, p.y, d.width, d.height);
	}

	public void drawLine(double x1, double y1, double x2, double y2) {
		// logger.info("drawLine("+x1+","+y1+","+x2+","+y2+")"+" with "+debugForegroundStyle());
		if (currentForeground.getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(x1, y1);
		Point p2 = convertNormalizedPointToViewCoordinates(x2, y2);
		// logger.info("drawLine("+p1.x+","+p1.y+","+p2.x+","+p2.y+")");
		g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("drawLine(" + p1.x + "," + p1.y + "," + p2.x + "," + p2.y + ")");
		}
	}

	public void drawLine(FGEPoint p1, FGEPoint p2) {
		drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Rectangle arcRect = convertNormalizedRectangleToViewCoordinates(0, 0, arcwidth, archeight);

		g2d.drawRoundRect(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawRoundRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void drawRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight) {
		drawRoundRect(p.x, p.y, d.width, d.height, arcwidth, archeight);
	}

	public void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		if (currentBackground instanceof BackgroundStyle.None) {
			return;
		}

		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Rectangle arcRect = convertNormalizedRectangleToViewCoordinates(0, 0, arcwidth, archeight);
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			RoundRectangle2D.Double rr = new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);
			fillInShapeWithImage(rr);
		} else {
			g2d.fillRoundRect(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillRoundRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void fillRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight) {
		fillRoundRect(p.x, p.y, d.width, d.height, arcwidth, archeight);
	}

	public void drawPolygon(FGEPolygon polygon) {
		drawPolygon(polygon.getPoints().toArray(new FGEPoint[polygon.getPointsNb()]));
	}

	public void drawPolygon(FGEPoint[] points) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		if (points == null || points.length == 0) {
			return;
		}
		int[] xpoints = new int[points.length];
		int[] ypoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			Point p = convertNormalizedPointToViewCoordinates(points[i]);
			xpoints[i] = p.x;
			ypoints[i] = p.y;
		}
		g2d.drawPolygon(xpoints, ypoints, points.length);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawPolygon(" + points + ")");
		}
	}

	public void fillPolygon(FGEPolygon polygon) {
		fillPolygon(polygon.getPoints().toArray(new FGEPoint[polygon.getPointsNb()]));
	}

	public void fillPolygon(FGEPoint[] points) {
		if (currentBackground instanceof BackgroundStyle.None) {
			return;
		}
		if (points == null || points.length == 0) {
			return;
		}

		int[] xpoints = new int[points.length];
		int[] ypoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			Point p = convertNormalizedPointToViewCoordinates(points[i]);
			xpoints[i] = p.x;
			ypoints[i] = p.y;
		}
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			Polygon p = new Polygon(xpoints, ypoints, points.length);
			fillInShapeWithImage(p);
		} else {
			g2d.fillPolygon(xpoints, ypoints, points.length);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillPolygon(" + points + ")");
		}
	}

	public void drawCircle(double x, double y, double width, double height) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		g2d.drawArc(r.x, r.y, r.width, r.height, 0, 360);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void drawCircle(double x, double y, double width, double height, Stroke stroke) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Stroke back = g2d.getStroke();
		g2d.setStroke(stroke);
		g2d.drawArc(r.x, r.y, r.width, r.height, 0, 360);
		g2d.setStroke(back);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void drawCircle(FGEPoint p, FGEDimension d) {
		drawCircle(p.x, p.y, d.width, d.height);
	}

	public void fillCircle(double x, double y, double width, double height) {
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, 0, 360, Arc2D.CHORD);
			fillInShapeWithImage(a);
		} else {
			g2d.fillArc(r.x, r.y, r.width, r.height, 0, 360);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void fillCircle(FGEPoint p, FGEDimension d) {
		fillCircle(p.x, p.y, d.width, d.height);
	}

	public void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		// System.out.println("drawArc ("+x+","+y+","+width+","+height+")");
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		g2d.drawArc(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle);
		// System.out.println("drawArc("+r.x+","+r.y+","+r.width+","+r.height+")");
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawArc(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void drawArc(FGEPoint p, FGEDimension d, double angleStart, double arcAngle) {
		drawArc(p.x, p.y, d.width, d.height, angleStart, arcAngle);
	}

	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle, Arc2D.PIE);
			fillInShapeWithImage(a);
		} else {
			g2d.fillArc(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillArc(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	public void fillArc(FGEPoint p, FGEDimension d, double angleStart, double arcAngle) {
		fillArc(p.x, p.y, d.width, d.height, angleStart, arcAngle);
	}

	public FGERectangle drawString(String text, FGEPoint location, int orientation, HorizontalTextAlignment alignment) {
		return drawString(text, location.x, location.y, orientation, alignment);
	}

	public void drawCurve(FGEQuadCurve curve) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(curve.getX1(), curve.getY1());
		Point ctrl_p = convertNormalizedPointToViewCoordinates(curve.getCtrlX(), curve.getCtrlY());
		Point p2 = convertNormalizedPointToViewCoordinates(curve.getX2(), curve.getY2());
		QuadCurve2D awtCurve = new QuadCurve2D.Double(p1.x, p1.y, ctrl_p.x, ctrl_p.y, p2.x, p2.y);
		g2d.draw(awtCurve);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("drawCurve(" + p1.x + "," + p1.y + "," + ctrl_p.x + "," + ctrl_p.y + "," + p2.x + "," + p2.y + ")");
		}
	}

	public void drawCurve(FGECubicCurve curve) {
		if (currentForeground.getNoStroke()) {
			return;
		}
		Point p1 = convertNormalizedPointToViewCoordinates(curve.getX1(), curve.getY1());
		Point ctrl_p1 = convertNormalizedPointToViewCoordinates(curve.getCtrlX1(), curve.getCtrlY1());
		Point ctrl_p2 = convertNormalizedPointToViewCoordinates(curve.getCtrlX2(), curve.getCtrlY2());
		Point p2 = convertNormalizedPointToViewCoordinates(curve.getX2(), curve.getY2());
		CubicCurve2D awtCurve = new CubicCurve2D.Double(p1.x, p1.y, ctrl_p1.x, ctrl_p1.y, ctrl_p2.x, ctrl_p2.y, p2.x, p2.y);
		g2d.draw(awtCurve);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("drawCurve(" + p1.x + "," + p1.y + "," + ctrl_p1.x + "," + ctrl_p1.y + "," + p2.x + ctrl_p2.x + "," + ctrl_p2.y
					+ "," + p2.x + "," + p2.y + ")");
		}
	}

	public void drawGeneralShape(FGEGeneralShape shape) {
		if (currentForeground.getNoStroke()) {
			return;
		}

		PathIterator pi = shape.getPathIterator(null);
		FGEPoint current = new FGEPoint();
		FGEPoint first = null;
		while (!pi.isDone()) {
			double[] pts = new double[6];
			FGEPoint p2, cp, cp1, cp2;
			switch (pi.currentSegment(pts)) {
			case PathIterator.SEG_MOVETO:
				current.x = pts[0];
				current.y = pts[1];
				first = current.clone();
				break;
			case PathIterator.SEG_LINETO:
				p2 = new FGEPoint(pts[0], pts[1]);
				drawLine(current, p2);
				current = p2;
				break;
			case PathIterator.SEG_QUADTO:
				cp = new FGEPoint(pts[0], pts[1]);
				p2 = new FGEPoint(pts[2], pts[3]);
				drawCurve(new FGEQuadCurve(current, cp, p2));
				current = p2;
				break;
			case PathIterator.SEG_CUBICTO:
				cp1 = new FGEPoint(pts[0], pts[1]);
				cp2 = new FGEPoint(pts[2], pts[3]);
				p2 = new FGEPoint(pts[4], pts[5]);
				drawCurve(new FGECubicCurve(current, cp1, cp2, p2));
				current = p2;
				break;
			case PathIterator.SEG_CLOSE:
				drawLine(current, first);
				current = first;
				break;
			default:
				break;
			}
			pi.next();
		}

	}

	public void fillGeneralShape(FGEGeneralShape shape) {
		GeneralPath p = shape.getGeneralPath();
		if (currentBackground instanceof BackgroundStyle.BackgroundImage) {
			fillInShapeWithImage(p);
		} else {
			g2d.fill(p);
		}
	}

	public FGERectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment) {
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		Font oldFont = g2d.getFont();
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		if (orientation != 0) {
			at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(orientation)));
		}
		Font font = oldFont.deriveFont(at);
		g2d.setFont(font);
		Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		/*FGERectangle returned = convertViewCoordinatesToNormalizedRectangle(
				(int)(bounds.getX()+p.x-bounds.getWidth()/2),
				(int)(bounds.getY()+p.y+bounds.getHeight()/2),
				(int)bounds.getWidth(),(int)bounds.getHeight());*/
		if (currentTextStyle.getIsBackgroundColored()) {
			g2d.setColor(currentTextStyle.getBackgroundColor());
			g2d.fillRect((int) (bounds.getX() + p.x - bounds.getWidth() / 2), (int) (bounds.getY() + p.y + bounds.getHeight() / 2),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2d.setColor(currentTextStyle.getColor());
		}
		switch (alignment) {
		case CENTER:
			g2d.drawString(text, (int) (p.x - bounds.getWidth() / 2), (int) (p.y + bounds.getHeight() / 2));
			break;
		case LEFT:
			g2d.drawString(text, p.x, (int) (p.y + bounds.getHeight() / 2));
			break;
		case RIGHT:
			g2d.drawString(text, (int) (p.x - bounds.getWidth()), (int) (p.y + bounds.getHeight() / 2));
			break;
		default:
			g2d.drawString(text, (int) (p.x - bounds.getWidth() / 2), (int) (p.y + bounds.getHeight() / 2));
			break;
		}
		g2d.setFont(oldFont);
		return convertViewCoordinatesToNormalizedRectangle((int) (bounds.getX() + p.x - bounds.getWidth() / 2),
				(int) (bounds.getY() + p.y + bounds.getHeight() / 2), (int) bounds.getWidth(), (int) bounds.getHeight());
	}

	public FGERectangle drawString(String text, FGEPoint location, HorizontalTextAlignment alignment) {
		return drawString(text, location.x, location.y, 0, alignment);
	}

	public FGERectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment) {
		return drawString(text, x, y, 0, alignment);
	}

}
