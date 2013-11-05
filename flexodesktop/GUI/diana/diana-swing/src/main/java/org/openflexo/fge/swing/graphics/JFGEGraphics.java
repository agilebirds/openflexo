package org.openflexo.fge.swing.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.NoneBackgroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.FGEGraphicsImpl;
import org.openflexo.fge.swing.view.JFGEView;

import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

/**
 * This is the SWING base implementation of a {@link FGEGraphics}.<br>
 * We mainly wrap a {@link Graphics2D} object and use those paint primitive
 * 
 * @author sylvain
 * 
 */
public abstract class JFGEGraphics extends FGEGraphicsImpl {

	private static final Logger logger = Logger.getLogger(JFGEGraphics.class.getPackage().getName());

	protected Graphics2D g2d;

	private static final int TRANSPARENT_COMPOSITE_RULE = AlphaComposite.SRC_OVER;

	protected JFGEGraphics(DrawingTreeNode<?, ?> dtn, JFGEView<?, ?> view) {
		super(dtn, view);
	}

	@Override
	public JFGEView<?, ?> getView() {
		return (JFGEView<?, ?>) super.getView();
	}

	@Override
	public void delete() {
		super.delete();
		g2d = null;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */

	public void createGraphics(Graphics2D graphics2D, AbstractDianaEditor<?, ?, ?> controller) {
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

	public void setStroke(Stroke aStroke) {
		g2d.setStroke(aStroke);
	}

	protected void applyCurrentForegroundStyle() {
		if (g2d == null) {
			return; // Strange...
		}

		// logger.info("Apply "+currentForeground);

		if (getCurrentForeground() != null) {
			g2d.setColor(getCurrentForeground().getColor());
			Stroke stroke = getStroke(getCurrentForeground(), getScale());
			if (stroke != null) {
				g2d.setStroke(stroke);
			}

			if (getCurrentForeground().getUseTransparency()) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getCurrentForeground().getTransparencyLevel()));
			} else {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
			}
		}

	}

	protected String debugForegroundStyle() {
		BasicStroke stroke = (BasicStroke) g2d.getStroke();
		return "FS-(color=" + g2d.getColor() + ")-(with=" + stroke.getLineWidth() + ")-(transp=" + g2d.getComposite() + ")-(join="
				+ stroke.getLineJoin() + ")-(cap=" + stroke.getEndCap() + ")-(dash=" + stroke.getDashPhase() + ")";
	}

	protected void applyCurrentBackgroundStyle() {
		if (g2d == null) {
			return; // Strange...
		}

		if (getCurrentBackground() != null) {
			Paint paint = getPaint(getCurrentBackground(), getScale());
			if (paint != null) {
				g2d.setPaint(paint);
				if (getCurrentBackground().getUseTransparency()) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getCurrentBackground().getTransparencyLevel()));
				} else {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
				}
			} else {
				// paint was null, meaning that Paint could not been obtained yet (texture not ready yet)
				// the best is to paint it totally transparent
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));
			}
		}
	}

	protected void applyCurrentTextStyle() {
		g2d.setColor(getCurrentTextStyle().getColor());
		g2d.setFont(getCurrentTextStyle().getFont());
	}

	@Override
	public Rectangle drawControlPoint(double x, double y, int size) {
		if (getCurrentForeground().getNoStroke()) {
			return null;
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		p.x -= size;
		p.y -= size;
		g2d.fillRect(p.x, p.y, size * 2, size * 2);
		return new Rectangle(p.x, p.y, size * 2, size * 2);
	}

	@Override
	public void drawPoint(double x, double y) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void drawRoundArroundPoint(double x, double y, int size) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		p.x -= size;
		p.y -= size;
		g2d.drawOval(p.x, p.y, size * 2, size * 2);
	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void fillRect(double x, double y, double width, double height) {
		if (getCurrentBackground() instanceof NoneBackgroundStyle) {
			return;
		}

		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);

		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
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
			ShapeGraphicalRepresentation gr = (ShapeGraphicalRepresentation) getGraphicalRepresentation();
			at.concatenate(AffineTransform.getTranslateInstance(gr.getBorder().getLeft(), gr.getBorder().getTop()));
		}
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			at.concatenate(AffineTransform.getTranslateInstance(((BackgroundImageBackgroundStyle) getCurrentBackground()).getDeltaX(),
					((BackgroundImageBackgroundStyle) getCurrentBackground()).getDeltaY()));
			if (((BackgroundImageBackgroundStyle) getCurrentBackground()).getImageBackgroundType() == BackgroundImageBackgroundStyle.ImageBackgroundType.OPAQUE) {
				g2d.setColor(((BackgroundImageBackgroundStyle) getCurrentBackground()).getImageBackgroundColor());
				g2d.fill(aShape);
			}
		}
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			at.concatenate(AffineTransform.getScaleInstance(((BackgroundImageBackgroundStyle) getCurrentBackground()).getScaleX(),
					((BackgroundImageBackgroundStyle) getCurrentBackground()).getScaleY()));

		}

		if (getCurrentBackground().getUseTransparency()) {
			g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE, getCurrentBackground().getTransparencyLevel()));
		} else {
			g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE));
		}
		g2d.drawImage(((BackgroundImageBackgroundStyle) getCurrentBackground()).getImage(), at, null);

		releaseClonedGraphics(oldGraphics);
	}

	/**
	 * This method is used to paint an image or a portion of an image into a supplied shape. Background properties are used, and
	 * transparency managed here.
	 * 
	 * @param aShape
	 */

	@Override
	public void drawImage(Image image, FGEPoint p) {
		g2d.setComposite(AlphaComposite.getInstance(TRANSPARENT_COMPOSITE_RULE));
		Point location = convertNormalizedPointToViewCoordinates(p.x, p.y);
		// System.err.println(location);
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		at.preConcatenate(AffineTransform.getTranslateInstance(location.x, location.y));
		g2d.drawImage(image, at, null);
	}

	@Override
	public void fillRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		if (getCurrentBackground() instanceof NoneBackgroundStyle) {
			return;
		}

		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Rectangle arcRect = convertNormalizedRectangleToViewCoordinates(0, 0, arcwidth, archeight);
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			RoundRectangle2D.Double rr = new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);
			fillInShapeWithImage(rr);
		} else {
			g2d.fillRoundRect(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillRoundRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawRoundRect(double x, double y, double width, double height, double arcwidth, double archeight) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		Rectangle arcRect = convertNormalizedRectangleToViewCoordinates(0, 0, arcwidth, archeight);

		g2d.drawRoundRect(r.x, r.y, r.width, r.height, arcRect.width, arcRect.height);

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawRoundRect(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		// logger.info("drawLine("+x1+","+y1+","+x2+","+y2+")"+" with "+debugForegroundStyle());
		if (getCurrentForeground() == null || getCurrentForeground().getNoStroke()) {
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

	@Override
	public void drawPolygon(FGEPoint[] points) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void fillPolygon(FGEPoint[] points) {
		if (getCurrentBackground() instanceof NoneBackgroundStyle) {
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
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			Polygon p = new Polygon(xpoints, ypoints, points.length);
			fillInShapeWithImage(p);
		} else {
			g2d.fillPolygon(xpoints, ypoints, points.length);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillPolygon(" + points + ")");
		}
	}

	@Override
	public void drawCircle(double x, double y, double width, double height) {
		if (getCurrentForeground().getNoStroke()) {
			return;
		}
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		g2d.drawArc(r.x, r.y, r.width, r.height, 0, 360);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("drawCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawCircle(double x, double y, double width, double height, Stroke stroke) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void fillCircle(double x, double y, double width, double height) {
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, 0, 360, Arc2D.CHORD);
			fillInShapeWithImage(a);
		} else {
			g2d.fillArc(r.x, r.y, r.width, r.height, 0, 360);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillCircle(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawArc(double x, double y, double width, double height, double angleStart, double arcAngle) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void fillArc(double x, double y, double width, double height, double angleStart, double arcAngle, boolean chord) {
		Rectangle r = convertNormalizedRectangleToViewCoordinates(x, y, width, height);
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle, chord ? Arc2D.CHORD
					: Arc2D.PIE);
			fillInShapeWithImage(a);
		} else {
			if (chord) {
				Arc2D.Double a = new Arc2D.Double(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle, Arc2D.CHORD);
				g2d.setClip(a);
			}
			g2d.fillArc(r.x, r.y, r.width, r.height, (int) angleStart, (int) arcAngle);
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("fillArc(" + r.x + "," + r.y + "," + r.width + "," + r.height + ")");
		}
	}

	@Override
	public void drawCurve(FGEQuadCurve curve) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void drawCurve(FGECubicCurve curve) {
		if (getCurrentForeground().getNoStroke()) {
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

	@Override
	public void fillGeneralShape(FGEGeneralShape shape) {
		AffineTransform at = getNode().convertNormalizedPointToViewCoordinatesAT(getScale());
		FGEGeneralShape transformedShape = shape.transform(at);
		GeneralPath p = transformedShape.getGeneralPath();
		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			fillInShapeWithImage(p);
		} else {
			g2d.fill(p);
		}
	}

	public FGERectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment) {
		if (text == null || text.length() == 0) {
			return new FGERectangle();
		}
		Point p = convertNormalizedPointToViewCoordinates(x, y);
		Font oldFont = g2d.getFont();
		AffineTransform at = AffineTransform.getScaleInstance(getScale(), getScale());
		if (orientation != 0) {
			at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(orientation)));
		}
		Font font = oldFont.deriveFont(at);
		Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
		g2d.setFont(font);
		/*FGERectangle returned = convertViewCoordinatesToNormalizedRectangle(
				(int)(bounds.getX()+p.x-bounds.getWidth()/2),
				(int)(bounds.getY()+p.y+bounds.getHeight()/2),
				(int)bounds.getWidth(),(int)bounds.getHeight());*/
		FGERectangle bounds = new FGERectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
		if (orientation != 0) {
			bounds = bounds.transform(AffineTransform.getRotateInstance(Math.toRadians(orientation))).getEmbeddingBounds();
		}
		int x2 = (int) (p.x - bounds.getWidth() / 2);
		int y2 = (int) (p.y + bounds.getHeight() / 2);
		switch (alignment) {
		case LEFT:
			x2 = p.x;
			break;
		case RIGHT:
			x2 = (int) (p.x - bounds.getWidth());
			break;
		case CENTER:
		default:
			break;
		}
		// GPO: Je crois que c'est complètement foireux si le background est "colored"
		if (getCurrentTextStyle().getIsBackgroundColored()) {
			g2d.setColor(getCurrentTextStyle().getBackgroundColor());
			g2d.fillRect((int) (bounds.getX() + p.x - bounds.getWidth() / 2), (int) (bounds.getY() + p.y + bounds.getHeight() / 2),
					(int) bounds.getWidth(), (int) bounds.getHeight());
			g2d.setColor(getCurrentTextStyle().getColor());
		}
		g2d.drawString(text, x2, y2);
		g2d.setFont(oldFont);
		return convertViewCoordinatesToNormalizedRectangle((int) (bounds.getX() + p.x - bounds.getWidth() / 2),
				(int) (bounds.getY() + p.y + bounds.getHeight() / 2), (int) bounds.getWidth(), (int) bounds.getHeight());
	}

	// TODO: implements cache for stroke
	private Stroke cachedStroke = null;

	/**
	 * Computes and return stroke for supplied ForegroundStyle and scale<br>
	 * Stores a cached value when possible
	 * 
	 * @param foregroundStyle
	 * @param scale
	 * @return
	 */
	public Stroke getStroke(ForegroundStyle foregroundStyle, double scale) {
		// if (cachedStroke == null || cachedStrokeFS == null || !cachedStrokeFS.equalsObject(foregroundStyle) || scale != cachedStokeScale)
		// {
		if (foregroundStyle.getDashStyle() == null) {
			return null;
		}
		if (foregroundStyle.getDashStyle() == DashStyle.PLAIN_STROKE) {
			cachedStroke = new BasicStroke((float) (foregroundStyle.getLineWidth() * scale), foregroundStyle.getCapStyle().ordinal(),
					foregroundStyle.getJoinStyle().ordinal());
		} else {
			float[] scaledDashArray = new float[foregroundStyle.getDashStyle().getDashArray().length];
			for (int i = 0; i < foregroundStyle.getDashStyle().getDashArray().length; i++) {
				scaledDashArray[i] = (float) (foregroundStyle.getDashStyle().getDashArray()[i] * scale * foregroundStyle.getLineWidth());
			}
			float scaledDashedPhase = (float) (foregroundStyle.getDashStyle().getDashPhase() * scale * foregroundStyle.getLineWidth());
			cachedStroke = new BasicStroke((float) (foregroundStyle.getLineWidth() * scale), foregroundStyle.getCapStyle().ordinal(),
					foregroundStyle.getJoinStyle().ordinal(), 10, scaledDashArray, scaledDashedPhase);
		}
		// cachedStokeScale = scale;
		// }
		return cachedStroke;
	}

	/**
	 * Computes and return stroke for supplied ForegroundStyle and scale<br>
	 * Stores a cached value when possible
	 * 
	 * @param foregroundStyle
	 * @param scale
	 * @return
	 */
	public Paint getPaint(BackgroundStyle backgroundStyle, double scale) {
		if (backgroundStyle instanceof NoneBackgroundStyle) {
			return null;
		} else if (backgroundStyle instanceof ColorBackgroundStyle) {
			return ((ColorBackgroundStyle) backgroundStyle).getColor();
		} else if (getCurrentBackground() instanceof ColorGradientBackgroundStyle) {
			return getGradientPaint((ColorGradientBackgroundStyle) backgroundStyle, scale);
		} else if (getCurrentBackground() instanceof TextureBackgroundStyle) {
			return getTexturePaint((TextureBackgroundStyle) backgroundStyle, scale);
		} else if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle) {
			return Color.WHITE;
		} else {
			return null;
		}
	}

	private GradientPaint getGradientPaint(ColorGradientBackgroundStyle bs, double scale) {
		switch (bs.getDirection()) {
		case SOUTH_EAST_NORTH_WEST:
			return new GradientPaint(0, 0, bs.getColor1(), getNode().getViewWidth(scale), getNode().getViewHeight(scale), bs.getColor2());
		case SOUTH_WEST_NORTH_EAST:
			return new GradientPaint(0, getNode().getViewHeight(scale), bs.getColor1(), getNode().getViewWidth(scale), 0, bs.getColor2());
		case WEST_EAST:
			return new GradientPaint(0, 0.5f * getNode().getViewHeight(scale), bs.getColor1(), getNode().getViewWidth(scale),
					0.5f * getNode().getViewHeight(scale), bs.getColor2());
		case NORTH_SOUTH:
			return new GradientPaint(0.5f * getNode().getViewWidth(scale), 0, bs.getColor1(), 0.5f * getNode().getViewWidth(scale),
					getNode().getViewHeight(scale), bs.getColor2());
		default:
			return new GradientPaint(0, 0, bs.getColor1(), getNode().getViewWidth(scale), getNode().getViewHeight(scale), bs.getColor2());
		}
	}

	private synchronized TexturePaint getTexturePaint(TextureBackgroundStyle bs, double scale) {
		final BufferedImage coloredTexture = getColoredTexture(bs);
		if (coloredTexture != null) {
			return new TexturePaint(coloredTexture, new Rectangle(0, 0, coloredTexture.getWidth(), coloredTexture.getHeight()));
		}
		// Since image building take some time, colored texture might not be ready yet
		// In this case, invoke repaint later
		logger.warning("ColoredTexture not ready");
		repaintWhenColoredTextureHasBeenComputed();
		return null;
	}

	private synchronized void repaintWhenColoredTextureHasBeenComputed() {
		if (coloredTexture != null) {
			// Now it's ok, proceed repaint
			getView().getPaintManager().invalidate(getNode());
			getView().getPaintManager().repaint(getView());
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaintWhenColoredTextureHasBeenComputed();
				}
			});
		}
	}

	private BufferedImage coloredTexture;
	private ToolkitImage coloredImage;
	private ToolkitImage requestedColoredImage;
	private TextureType coloredTextureMadeForThisTextureType = null;
	private Color coloredTextureMadeForThisColor1 = null;
	private Color coloredTextureMadeForThisColor2 = null;

	/**
	 * Internally called to rebuild colored texture if cached value is not up-to-date (parameters have changed)
	 * 
	 * @param bs
	 */
	private synchronized void rebuildColoredTextureWhenRequired(TextureBackgroundStyle bs) {
		if (coloredTexture == null || coloredTextureMadeForThisTextureType == null || coloredTextureMadeForThisColor1 == null
				|| coloredTextureMadeForThisColor2 == null || coloredTextureMadeForThisTextureType != bs.getTextureType()
				|| coloredTextureMadeForThisColor1 != bs.getColor1() || coloredTextureMadeForThisColor2 != bs.getColor2()) {
			// Texture needs to be rebuilt
			rebuildColoredTexture(bs);
		} else {
			logger.fine("Texture is still valid");
		}
	}

	/**
	 * Internnly called to rebuild colored texture
	 * 
	 * @param bs
	 */
	private synchronized void rebuildColoredTexture(final TextureBackgroundStyle bs) {
		coloredTexture = null;
		coloredImage = null;
		if (bs.getTextureType() == null) {
			return;
		}
		final Image initialImage = bs.getTextureType().getImageIcon().getImage();
		ColorSwapFilter imgfilter = new ColorSwapFilter(java.awt.Color.BLACK, bs.getColor1(), java.awt.Color.WHITE, bs.getColor2()) {
			@Override
			public void imageComplete(int status) {
				super.imageComplete(status);
				synchronized (JFGEGraphics.this) {
					coloredTexture = new BufferedImage(coloredImage.getWidth(null), coloredImage.getHeight(null),
							BufferedImage.TYPE_INT_ARGB);
					Graphics gi = coloredTexture.getGraphics();
					if (coloredImage != null) {
						gi.drawImage(coloredImage, 0, 0, null);
					}
					coloredTextureMadeForThisTextureType = bs.getTextureType();
					coloredTextureMadeForThisColor1 = bs.getColor1();
					coloredTextureMadeForThisColor2 = bs.getColor2();
					logger.fine("Image has been computed, status=" + status);
				}
			}
		};

		// Launch a background job building a new image with specified two colors
		ImageProducer producer = new FilteredImageSource(initialImage.getSource(), imgfilter);
		coloredImage = (ToolkitImage) Toolkit.getDefaultToolkit().createImage(producer);
		ImageRepresentation consumer = new ImageRepresentation(coloredImage, null, true);
		producer.addConsumer(consumer);
		try {
			producer.startProduction(consumer);
		} catch (RuntimeException e) {
			logger.warning("Unexpected exception: " + e);
		}

	}

	private BufferedImage getColoredTexture(TextureBackgroundStyle bs) {
		rebuildColoredTextureWhenRequired(bs);
		return coloredTexture;

		/*if (coloredTexture == null) {
			rebuildColoredTexture(bs);
		} 
		return coloredTexture;*/

	}

	static class ColorSwapFilter extends RGBImageFilter {
		private int target1;
		private int replacement1;
		private int target2;
		private int replacement2;

		public ColorSwapFilter(java.awt.Color target1, java.awt.Color replacement1, java.awt.Color target2, java.awt.Color replacement2) {
			this.target1 = target1.getRGB();
			this.replacement1 = replacement1.getRGB();
			this.target2 = target2.getRGB();
			this.replacement2 = replacement2.getRGB();
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// if (x==0 && y==0) logger.info("Starting convert image");
			// if (x==15 && y==15) logger.info("Finished convert image");
			if (rgb == target1) {
				return replacement1;
			} else if (rgb == target2) {
				return replacement2;
			}
			return rgb;
		}

		@Override
		public void imageComplete(int status) {
			super.imageComplete(status);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("imageComplete status=" + status);
			}
		}

	}

}
