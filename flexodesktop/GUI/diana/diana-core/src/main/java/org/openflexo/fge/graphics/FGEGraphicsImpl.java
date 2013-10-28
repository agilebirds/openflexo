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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.view.FGEView;

/**
 * This is the generic base implementation of a {@link FGEGraphics}
 * 
 * @author sylvain
 * 
 */
public abstract class FGEGraphicsImpl implements FGEGraphics {

	private static final Logger logger = Logger.getLogger(FGEGraphicsImpl.class.getPackage().getName());

	// TODO: do we need this ?
	protected AbstractDianaEditor<?, ?, ?> _controller;
	private DrawingTreeNode<?, ?> dtn;
	private FGEView<?, ?> view;

	private static final FGEModelFactory GRAPHICS_FACTORY = FGECoreUtils.TOOLS_FACTORY;
	private static final ForegroundStyle DEFAULT_FG = GRAPHICS_FACTORY.makeDefaultForegroundStyle();
	private static final BackgroundStyle DEFAULT_BG = GRAPHICS_FACTORY.makeEmptyBackground();
	private static final TextStyle DEFAULT_TEXT = GRAPHICS_FACTORY.makeDefaultTextStyle();

	private ForegroundStyle defaultForeground = DEFAULT_FG;
	private BackgroundStyle defaultBackground = DEFAULT_BG;
	private TextStyle defaultTextStyle = DEFAULT_TEXT;

	private ForegroundStyle currentForeground = defaultForeground;
	private BackgroundStyle currentBackground = defaultBackground;
	private TextStyle currentTextStyle = defaultTextStyle;

	public FGEGraphicsImpl(DrawingTreeNode<?, ?> dtn, FGEView<?, ?> view) {
		super();
		this.dtn = dtn;
		this.view = view;
	}

	public FGEModelFactory getFactory() {
		return GRAPHICS_FACTORY;
	}

	public DrawingTreeNode<?, ?> getDrawingTreeNode() {
		return dtn;
	}

	public DrawingTreeNode<?, ?> getNode() {
		return getDrawingTreeNode();
	}

	public FGEView<?, ?> getView() {
		return view;
	}

	public GraphicalRepresentation getGraphicalRepresentation() {
		return dtn.getGraphicalRepresentation();
	}

	public AbstractDianaEditor<?, ?, ?> getController() {
		return _controller;
	}

	public double getScale() {
		return getController().getScale();
	}

	public void delete() {
		dtn = null;
		_controller = null;
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

	@Override
	public void useDefaultForegroundStyle() {
		useForegroundStyle(defaultForeground);
	}

	public void useForegroundStyle(ForegroundStyle aStyle) {
		currentForeground = aStyle;
		applyCurrentForegroundStyle();
	}

	protected abstract void applyCurrentForegroundStyle();

	public TextStyle getCurrentTextStyle() {
		return currentTextStyle;
	}

	public BackgroundStyle getDefaultBackground() {
		return defaultBackground;
	}

	public void setDefaultBackground(BackgroundStyle aBackground) {
		defaultBackground = aBackground;
	}

	public BackgroundStyle getCurrentBackground() {
		return currentBackground;
	}

	@Override
	public void useDefaultBackgroundStyle() {
		useBackgroundStyle(defaultBackground);
	}

	public void useBackgroundStyle(BackgroundStyle aStyle) {
		currentBackground = aStyle;
		applyCurrentBackgroundStyle();
	}

	protected abstract void applyCurrentBackgroundStyle();

	public void setDefaultTextStyle(TextStyle aTextStyle) {
		defaultTextStyle = aTextStyle;
	}

	@Override
	public void useDefaultTextStyle() {
		useTextStyle(defaultTextStyle);
	}

	public void useTextStyle(TextStyle aStyle) {
		currentTextStyle = aStyle;
		applyCurrentTextStyle();
	}

	protected abstract void applyCurrentTextStyle();

	@Override
	public FGERectangle getNodeNormalizedBounds() {
		return getNode().getNormalizedBounds();
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
		return dtn.getViewWidth(scale);
	}

	public int getViewHeight(double scale) {
		return dtn.getViewHeight(scale);
	}

	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return dtn.convertNormalizedPointToViewCoordinates(x, y, getScale());
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
		return dtn.convertViewCoordinatesToNormalizedPoint(x, y, getScale());
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

	@Override
	public void drawPoint(FGEPoint p) {
		drawPoint(p.x, p.y);
	}

	@Override
	public void drawRoundArroundPoint(FGEPoint p, int size) {
		drawRoundArroundPoint(p.x, p.y, size);
	}

	@Override
	public Rectangle drawControlPoint(FGEPoint p, int size) {
		return drawControlPoint(p.x, p.y, size);
	}

	@Override
	public void drawRect(FGEPoint p, FGEDimension d) {
		drawRect(p.x, p.y, d.width, d.height);
	}

	@Override
	public void fillRect(FGEPoint p, FGEDimension d) {
		fillRect(p.x, p.y, d.width, d.height);
	}

	@Override
	public void drawLine(FGEPoint p1, FGEPoint p2) {
		drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	@Override
	public void drawRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight) {
		drawRoundRect(p.x, p.y, d.width, d.height, arcwidth, archeight);
	}

	@Override
	public void fillRoundRect(FGEPoint p, FGEDimension d, double arcwidth, double archeight) {
		fillRoundRect(p.x, p.y, d.width, d.height, arcwidth, archeight);
	}

	@Override
	public void drawPolygon(FGEPolygon polygon) {
		drawPolygon(polygon.getPoints().toArray(new FGEPoint[polygon.getPointsNb()]));
	}

	@Override
	public void fillPolygon(FGEPolygon polygon) {
		fillPolygon(polygon.getPoints().toArray(new FGEPoint[polygon.getPointsNb()]));
	}

	@Override
	public void drawCircle(FGEPoint p, FGEDimension d) {
		drawCircle(p.x, p.y, d.width, d.height);
	}

	@Override
	public void fillCircle(FGEPoint p, FGEDimension d) {
		fillCircle(p.x, p.y, d.width, d.height);
	}

	@Override
	public void drawArc(FGEPoint p, FGEDimension d, double angleStart, double arcAngle) {
		drawArc(p.x, p.y, d.width, d.height, angleStart, arcAngle);
	}

	@Override
	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		fillArc(x, y, width, height, angleStart, arcAngle, false);
	}

	@Override
	public void fillArc(FGEPoint p, FGEDimension d, double angleStart, double arcAngle) {
		fillArc(p.x, p.y, d.width, d.height, angleStart, arcAngle);
	}

	public FGERectangle drawString(String text, FGEPoint location, int orientation, HorizontalTextAlignment alignment) {
		return drawString(text, location.x, location.y, orientation, alignment);
	}

	@Override
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

	public FGERectangle drawString(String text, FGEPoint location, HorizontalTextAlignment alignment) {
		return drawString(text, location.x, location.y, 0, alignment);
	}

	public FGERectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment) {
		return drawString(text, x, y, 0, alignment);
	}

	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		return getNode().convertNormalizedPointToViewCoordinates(x, y, scale);
	}

	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return getNode().convertViewCoordinatesToNormalizedPoint(p, scale);
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return getNode().convertNormalizedPointToViewCoordinatesAT(scale);
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return getNode().convertViewCoordinatesToNormalizedPointAT(scale);
	}

}
