package org.openflexo.fge.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class RootNodeImpl<M> extends DrawingTreeNodeImpl<M, DrawingGraphicalRepresentation> implements RootNode<M> {

	protected FGEDrawingGraphics graphics;
	private FGEDrawingDecorationGraphics decorationGraphics;
	private BackgroundStyle bgStyle;

	public RootNodeImpl(DrawingImpl<?> drawing, M drawable, GRBinding<M, DrawingGraphicalRepresentation> grBinding,
			DrawingTreeNodeImpl<?, ?> parentNode) {
		super(drawing, drawable, grBinding, parentNode);
		graphics = new FGEDrawingGraphics(this);
		decorationGraphics = new FGEDrawingDecorationGraphics(this);
		bgStyle = drawing.getFactory().makeColoredBackground(getGraphicalRepresentation().getBackgroundColor());
	}

	@Override
	public int getViewX(double scale) {
		return 0;
	}

	@Override
	public int getViewY(double scale) {
		return 0;
	}

	@Override
	public int getViewWidth(double scale) {
		return (int) (getGraphicalRepresentation().getWidth() * scale);
	}

	@Override
	public int getViewHeight(double scale) {
		return (int) (getGraphicalRepresentation().getHeight() * scale);
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0, 0, getGraphicalRepresentation().getWidth(), getGraphicalRepresentation().getHeight(), Filling.FILLED);
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return AffineTransform.getScaleInstance(scale, scale);

		/*AffineTransform returned = AffineTransform.getScaleInstance(getWidth(), getHeight());
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale,scale));
		}
		return returned;*/
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return AffineTransform.getScaleInstance(1 / scale, 1 / scale);

		/*AffineTransform returned = new AffineTransform();
		if (scale != 1) returned = AffineTransform.getScaleInstance(1/scale, 1/scale);
		returned.preConcatenate(AffineTransform.getScaleInstance(1/getWidth(),1/getHeight()));
		return returned;*/
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		return false;
	}

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		Graphics2D g2 = (Graphics2D) g;
		graphics.createGraphics(g2, controller);
		// If there is a decoration painter init its graphics
		if (getGraphicalRepresentation().getDecorationPainter() != null) {
			decorationGraphics.createGraphics(g2, controller);
		}

		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (getGraphicalRepresentation().getDecorationPainter() != null
				&& getGraphicalRepresentation().getDecorationPainter().paintBeforeDrawing()) {
			getGraphicalRepresentation().getDecorationPainter().paintDecoration(decorationGraphics);
		}

		super.paint(g, controller);

		if (!(bgStyle instanceof ColorBackgroundStyle)
				|| !((ColorBackgroundStyle) bgStyle).getColor().equals(getGraphicalRepresentation().getBackgroundColor())) {
			bgStyle = getFactory().makeColoredBackground(getGraphicalRepresentation().getBackgroundColor());
		}

		ForegroundStyle fgStyle = getFactory().makeForegroundStyle(Color.DARK_GRAY);

		graphics.setDefaultForeground(fgStyle);
		graphics.setDefaultBackground(bgStyle);
		if (getGraphicalRepresentation().getDrawWorkingArea()) {
			getGraphicalRepresentation().getWorkingArea().paint(graphics);
		}
		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (getGraphicalRepresentation().getDecorationPainter() != null
				&& !getGraphicalRepresentation().getDecorationPainter().paintBeforeDrawing()) {
			getGraphicalRepresentation().getDecorationPainter().paintDecoration(decorationGraphics);
		}

		graphics.releaseGraphics();

	}

	@Override
	public FGEDrawingGraphics getGraphics() {
		return graphics;
	}

	@Override
	public void delete() {
		super.delete();
		if (graphics != null) {
			graphics.delete();
		}
		graphics = null;
		decorationGraphics = null;
	}

	@Override
	public double getWidth() {
		return getGraphicalRepresentation().getWidth();
	}

	@Override
	public final void setWidth(double aValue) {
		getGraphicalRepresentation().setWidth(aValue);
	}

	@Override
	public double getHeight() {
		return getGraphicalRepresentation().getHeight();
	}

	@Override
	public final void setHeight(double aValue) {
		getGraphicalRepresentation().setHeight(aValue);
	}

}
