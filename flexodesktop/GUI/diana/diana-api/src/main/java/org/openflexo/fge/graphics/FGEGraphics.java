package org.openflexo.fge.graphics;

import java.awt.Point;
import java.awt.Rectangle;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;

public interface FGEGraphics extends AbstractFGEGraphics {

	public abstract FGEModelFactory getFactory();

	public abstract DrawingTreeNode<?, ?> getDrawingTreeNode();

	public abstract DrawingTreeNode<?, ?> getNode();

	public abstract GraphicalRepresentation getGraphicalRepresentation();

	public abstract DrawingController<?> getController();

	public abstract double getScale();

	public abstract void delete();

	// public abstract void createGraphics(Graphics2D graphics2D, DrawingController<?> controller);

	// public abstract void releaseGraphics();

	// public abstract Graphics2D cloneGraphics();

	// public abstract void releaseClonedGraphics(Graphics2D oldGraphics);

	// public abstract Graphics2D getGraphics();

	public abstract ForegroundStyle getDefaultForeground();

	public abstract ForegroundStyle getCurrentForeground();

	public abstract void setDefaultForeground(ForegroundStyle aForegound);

	// public abstract void setStroke(Stroke aStroke);

	public abstract void useForegroundStyle(ForegroundStyle aStyle);

	public abstract TextStyle getCurrentTextStyle();

	public abstract BackgroundStyle getDefaultBackground();

	public abstract void setDefaultBackground(BackgroundStyle aBackground);

	public abstract void useBackgroundStyle(BackgroundStyle aStyle);

	public abstract void setDefaultTextStyle(TextStyle aTextStyle);

	public abstract void useTextStyle(TextStyle aStyle);

	public abstract FGERectangle getNormalizedBounds();

	public abstract int getViewWidth();

	public abstract int getViewHeight();

	public abstract int getViewWidth(double scale);

	public abstract int getViewHeight(double scale);

	public abstract Point convertNormalizedPointToViewCoordinates(double x, double y);

	public abstract Point convertNormalizedPointToViewCoordinates(FGEPoint p);

	public abstract Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r);

	public abstract Rectangle convertNormalizedRectangleToViewCoordinates(double x, double y, double width, double height);

	public abstract FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y);

	public abstract FGEPoint convertViewCoordinatesToNormalizedPoint(Point p);

	public abstract FGERectangle convertViewCoordinatesToNormalizedRectangle(Rectangle r);

	public abstract FGERectangle convertViewCoordinatesToNormalizedRectangle(int x, int y, int width, int height);

	public abstract FGERectangle drawString(String text, FGEPoint location, int orientation, HorizontalTextAlignment alignment);

	public abstract FGERectangle drawString(String text, double x, double y, int orientation, HorizontalTextAlignment alignment);

	public abstract FGERectangle drawString(String text, FGEPoint location, HorizontalTextAlignment alignment);

	public abstract FGERectangle drawString(String text, double x, double y, HorizontalTextAlignment alignment);

}