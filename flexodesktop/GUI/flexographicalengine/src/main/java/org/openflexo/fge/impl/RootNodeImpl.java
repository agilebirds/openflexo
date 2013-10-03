package org.openflexo.fge.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.List;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.fge.graphics.FGEDrawingGraphics;

public class RootNodeImpl<M> extends ContainerNodeImpl<M, DrawingGraphicalRepresentation> implements RootNode<M> {

	protected FGEDrawingGraphics graphics;
	private FGEDrawingDecorationGraphics decorationGraphics;
	private BackgroundStyle bgStyle;

	protected RootNodeImpl(DrawingImpl<M> drawing, M drawable, GRBinding<M, DrawingGraphicalRepresentation> grBinding) {
		super(drawing, drawable, grBinding, null);
		graphics = new FGEDrawingGraphics(this);
		decorationGraphics = new FGEDrawingDecorationGraphics(this);
		startDrawableObserving();
	}

	private BackgroundStyle getBGStyle() {
		if (bgStyle == null && getGraphicalRepresentation() != null) {
			bgStyle = getFactory().makeColoredBackground(getGraphicalRepresentation().getBackgroundColor());
		}
		return bgStyle;
	}

	/**
	 * Return bounds relative to parent container
	 * 
	 * @return
	 */
	@Override
	public FGERectangle getBounds() {
		return new FGERectangle(0, 0, getWidth(), getHeight());
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

		if (!(getBGStyle() instanceof ColorBackgroundStyle)
				|| !((ColorBackgroundStyle) getBGStyle()).getColor().equals(getGraphicalRepresentation().getBackgroundColor())) {
			bgStyle = getFactory().makeColoredBackground(getGraphicalRepresentation().getBackgroundColor());
		}

		ForegroundStyle fgStyle = getFactory().makeForegroundStyle(Color.DARK_GRAY);

		graphics.setDefaultForeground(fgStyle);
		graphics.setDefaultBackground(getBGStyle());
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
	public List<ControlArea<?>> getControlAreas() {
		// No control areas are declared for the root node
		return Collections.emptyList();
	}

	/**
	 * Return flag indicating if this node should be displayed, relatively to the value returned by visible feature in
	 * {@link GraphicalRepresentation}, and the structure of the tree (the parent should be visible too)<br>
	 * Here, as a root node, no parent node is expected to be set and visible
	 */
	@Override
	public boolean shouldBeDisplayed() {
		if (!isValidated()) {
			return false;
		}
		return getGraphicalRepresentation().getIsVisible();
	}

	@Override
	public String toString() {
		return "Root[" + getWidth() + "x" + getHeight() + "]:" + getDrawable();
	}

	@Override
	public final boolean hasText() {
		return false;
	}

	@Override
	public boolean hasContainedLabel() {
		return false;
	}

	@Override
	public boolean hasFloatingLabel() {
		return false;
	}

	@Override
	public FGEDimension getRequiredLabelSize() {
		return null;
	}
}
