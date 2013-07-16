package org.openflexo.fge.impl;

import java.awt.geom.AffineTransform;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.ShapeGraphicalRepresentation;

public class ShapeNodeImpl<O> extends DrawingTreeNodeImpl<O, ShapeGraphicalRepresentation> implements ShapeNode<O> {

	public ShapeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ShapeGraphicalRepresentation> grBinding,
			DrawingTreeNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
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

}
