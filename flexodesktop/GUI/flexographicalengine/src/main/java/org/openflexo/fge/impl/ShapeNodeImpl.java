package org.openflexo.fge.impl;

import java.awt.geom.AffineTransform;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;

public class ShapeNodeImpl<O> extends DrawingTreeNodeImpl<O, ShapeGraphicalRepresentation> implements ShapeNode<O> {

	private FGEShapeGraphics graphics;

	public ShapeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ShapeGraphicalRepresentation> grBinding,
			DrawingTreeNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		graphics = new FGEShapeGraphics(this);
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return super.getGraphicalRepresentation();
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = AffineTransform.getScaleInstance(getGraphicalRepresentation().getWidth(), getGraphicalRepresentation()
				.getHeight());
		if (getGraphicalRepresentation().getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(getGraphicalRepresentation().getBorder().getLeft(),
					getGraphicalRepresentation().getBorder().getTop()));
		}
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
		}
		return returned;
		/*
		double x2 = x*getWidth();
		double y2 = y*getHeight();
		if (getBorder() != null) {
			x2 += getBorder().left;
			y2 += getBorder().top;
		}
		if (scale != 1) {
			x2 = x2*scale;
			y2 = y2*scale;
		}
		return new Point((int)x2,(int)y2);*/
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		AffineTransform returned = new AffineTransform();
		if (scale != 1) {
			returned = AffineTransform.getScaleInstance(1 / scale, 1 / scale);
		}
		if (getGraphicalRepresentation().getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(-getGraphicalRepresentation().getBorder().getLeft(),
					-getGraphicalRepresentation().getBorder().getTop()));
		}
		returned.preConcatenate(AffineTransform.getScaleInstance(1 / getGraphicalRepresentation().getWidth(),
				1 / getGraphicalRepresentation().getHeight()));
		return returned;
		/*
		double x2= (double)x;
		double y2= (double)y;

		if (scale != 1) {
			x2 = x2/scale;
			y2 = y2/scale;
		}

		if (getBorder() != null) {
			x2 -= getBorder().left;
			y2 -= getBorder().top;
		}

		x2 = x2/getWidth();
		y2 = y2/getHeight();

		return new FGEPoint(x2,y2);*/
	}

	@Override
	public int getViewX(double scale) {
		return (int) (getGraphicalRepresentation().getX() * scale/*-(border!=null?border.left:0)*/);
	}

	@Override
	public int getViewY(double scale) {
		return (int) (getGraphicalRepresentation().getY() * scale/*-(border!=null?border.top:0)*/);
	}

	@Override
	public int getViewWidth(double scale) {
		return (int) (getUnscaledViewWidth() * scale) + 1;
	}

	@Override
	public int getViewHeight(double scale) {
		return (int) (getUnscaledViewHeight() * scale) + 1;
	}

	@Override
	public double getUnscaledViewWidth() {
		return getGraphicalRepresentation().getWidth()
				+ (getGraphicalRepresentation().getBorder() != null ? getGraphicalRepresentation().getBorder().getLeft()
						+ getGraphicalRepresentation().getBorder().getRight() : 0);
	}

	@Override
	public double getUnscaledViewHeight() {
		return getGraphicalRepresentation().getHeight()
				+ (getGraphicalRepresentation().getBorder() != null ? getGraphicalRepresentation().getBorder().getTop()
						+ getGraphicalRepresentation().getBorder().getBottom() : 0);
	}

	@Override
	public FGEGraphics getGraphics() {
		return graphics;
	}
}
