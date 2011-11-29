package org.openflexo.fge.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

public class ShapePreviewPanel extends JPanel implements
		FIBCustomComponent<Shape, ShapePreviewPanel> {

	private Drawing<RepresentedDrawing> drawing;
	private DrawingGraphicalRepresentation<RepresentedDrawing> drawingGR;
	private DrawingController<?> controller;
	private RepresentedDrawing representedDrawing;
	private RepresentedShape representedShape;

	private ShapeGraphicalRepresentation<RepresentedShape> shapeGR;

	public ShapePreviewPanel(Shape aShape) {
		super(new BorderLayout());
		representedDrawing = new RepresentedDrawing();
		representedShape = new RepresentedShape();
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));

		final Vector<RepresentedShape> singleton = new Vector<RepresentedShape>();
		singleton.add(representedShape);

		drawing = new Drawing<RepresentedDrawing>() {
			@Override
			public List<?> getContainedObjects(Object aDrawable) {
				// System.out.println("getContainedObjects() for " + aDrawable);
				if (aDrawable == representedDrawing) {
					return singleton;
				}
				return null;
			}

			@Override
			public Object getContainer(Object aDrawable) {
				// System.out.println("getContainer() for " + aDrawable);
				if (aDrawable == representedShape) {
					return representedDrawing;
				}
				return null;
			}

			@Override
			public DrawingGraphicalRepresentation<RepresentedDrawing> getDrawingGraphicalRepresentation() {
				// System.out.println("getDrawingGraphicalRepresentation() is "
				// + drawingGR);
				return drawingGR;
			}

			@Override
			public <O> GraphicalRepresentation<O> getGraphicalRepresentation(
					O aDrawable) {
				// System.out.println("getContainer() for " + aDrawable);
				if (aDrawable == representedDrawing) {
					return (GraphicalRepresentation<O>) drawingGR;
				} else if (aDrawable == representedShape) {
					return (GraphicalRepresentation<O>) shapeGR;
				}
				return null;
			}

			@Override
			public RepresentedDrawing getModel() {
				return representedDrawing;
			}

		};
		drawingGR = new DrawingGraphicalRepresentation<RepresentedDrawing>(
				drawing, false);
		drawingGR.setBackgroundColor(Color.WHITE);
		drawingGR.setWidth(getPanelWidth());
		drawingGR.setHeight(getPanelHeight());
		drawingGR.setDrawWorkingArea(false);
		shapeGR = new ShapeGraphicalRepresentation<RepresentedShape>(
				ShapeType.RECTANGLE, representedShape, drawing);
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
		shapeGR.setForeground(ForegroundStyle.makeStyle(Color.BLACK));
		shapeGR.setBackground(BackgroundStyle
				.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR));
		shapeGR.setShadowStyle(ShadowStyle.makeDefault());
		shapeGR.setShape(getShape() != null ? getShape() : Shape.makeShape(
				ShapeType.RECTANGLE, null));
		shapeGR.setIsSelectable(false);
		shapeGR.setIsFocusable(false);
		shapeGR.setIsReadOnly(true);
		shapeGR.setBorder(new ShapeBorder(getBorderSize(), getBorderSize(),
				getBorderSize(), getBorderSize()));

		controller = new DrawingController<Drawing<?>>(drawing);
		add(controller.getDrawingView());
	}

	private int border = 10;
	private int width = 120;
	private int height = 80;
	private static final float RATIO = 0.6f;

	public float getRatio() {
		if (getShape().areDimensionConstrained()) {
			return 1.0f;
		} else {
			return RATIO;
		}
	}

	public int getBorderSize() {
		return border;
	}

	public void setBorderSize(int border) {
		this.border = border;
		shapeGR.setBorder(new ShapeBorder(getBorderSize(), getBorderSize(),
				getBorderSize(), getBorderSize()));
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
		update();
	}

	private boolean sizeConstrainedWithWidth() {
		return (float) (getPanelHeight() - 2 * getBorderSize())
				/ (float) (getPanelWidth() - 2 * getBorderSize()) > getRatio();
	}

	private int getShapeX() {
		if (sizeConstrainedWithWidth()) {
			return 0;
		} else {
			return (getPanelWidth() - getShapeWidth()) / 2 - getBorderSize();
		}
	}

	private int getShapeY() {
		if (sizeConstrainedWithWidth()) {
			return (getPanelHeight() - getShapeHeight()) / 2 - getBorderSize();
		} else {
			return 0;
		}
	}

	private int getShapeWidth() {
		if (sizeConstrainedWithWidth()) {
			return getPanelWidth() - 2 * getBorderSize();
		} else {
			return (int) (getShapeHeight() / getRatio());
		}
	}

	private int getShapeHeight() {
		if (sizeConstrainedWithWidth()) {
			return (int) (getShapeWidth() * getRatio());
		} else {
			return getPanelHeight() - 2 * getBorderSize();
		}
	}

	public int getPanelWidth() {
		return width;
	}

	public void setPanelWidth(int width) {
		this.width = width;
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		drawingGR.setWidth(getPanelWidth());
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
	}

	public int getPanelHeight() {
		return height;
	}

	public void setPanelHeight(int height) {
		this.height = height;
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		drawingGR.setHeight(getPanelHeight());
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
	}

	protected void update() {

		getShape().updateShape();

		shapeGR.setShape(getShape() != null ? getShape() : Shape.makeShape(
				ShapeType.RECTANGLE, null));
		shapeGR.notifyShapeChanged();

		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());

		if (controller.getPaintManager() != null) {
			controller.getPaintManager().repaint(controller.getDrawingView());
		}

	}

	public Shape getShape() {
		return shapeGR.getShape();
	}

	public void setShape(Shape shape) {
		shapeGR.setShape(shape);
		update();
	}

	@Override
	public ShapePreviewPanel getJComponent() {
		return this;
	}

	@Override
	public Shape getEditedObject() {
		return getShape();
	}

	@Override
	public void setEditedObject(Shape object) {
		setShape(object);
	}

	@Override
	public Shape getRevertValue() {
		return null;
	}

	@Override
	public void setRevertValue(Shape object) {
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public Class<Shape> getRepresentedType() {
		return Shape.class;
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	public class RepresentedDrawing {
	}

	public class RepresentedShape {
		public Shape getRepresentedShape() {
			return getShape();
		}
	}

}