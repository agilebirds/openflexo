package org.openflexo.fge.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

public class ShapePreviewPanel extends JPanel implements FIBCustomComponent<Shape, ShapePreviewPanel> {

	private Drawing<RepresentedDrawing> drawing;
	private DrawingGraphicalRepresentation<RepresentedDrawing> drawingGR;
	private DrawingController<?> controller;
	private RepresentedDrawing representedDrawing;
	private RepresentedShape representedShape;

	private ShapeGraphicalRepresentation<RepresentedShape> shapeGR;

	private int border = 10;
	private int width = 120;
	private int height = 80;
	private static final float RATIO = 0.6f;

	private FGEModelFactory factory;

	private ForegroundStyle foregroundStyle;
	private BackgroundStyle backgroundStyle;
	private ShadowStyle shadowStyle;

	public ShapePreviewPanel(Shape aShape) {
		super(new BorderLayout());

		factory = new FGEModelFactory();

		representedDrawing = new RepresentedDrawing();
		representedShape = new RepresentedShape();
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));

		factory = new FGEModelFactory();
		foregroundStyle = factory.makeForegroundStyle(Color.BLACK);
		backgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		shadowStyle = factory.makeNoneShadowStyle();

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
			public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O aDrawable) {
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
		drawingGR = factory.makeDrawingGraphicalRepresentation(drawing, false);
		drawingGR.setBackgroundColor(Color.WHITE);
		drawingGR.setWidth(getPanelWidth());
		drawingGR.setHeight(getPanelHeight());
		drawingGR.setDrawWorkingArea(false);
		shapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE, representedShape, drawing);
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
		shapeGR.setForeground(getForegroundStyle());
		shapeGR.setBackground(getBackgroundStyle());
		shapeGR.setShadowStyle(getShadowStyle());
		shapeGR.setShape(getShape() != null ? getShape() : factory.makeShape(ShapeType.RECTANGLE, null));
		shapeGR.setIsSelectable(false);
		shapeGR.setIsFocusable(false);
		shapeGR.setIsReadOnly(true);
		shapeGR.setBorder(factory.makeShapeBorder(getBorderSize(), getBorderSize(), getBorderSize(), getBorderSize()));
		shapeGR.setValidated(true);

		controller = new DrawingController<Drawing<?>>(drawing, factory);
		add(controller.getDrawingView());
	}

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
		shapeGR.setBorder(factory.makeShapeBorder(getBorderSize(), getBorderSize(), getBorderSize(), getBorderSize()));
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
		update();
	}

	private boolean sizeConstrainedWithWidth() {
		return (float) (getPanelHeight() - 2 * getBorderSize()) / (float) (getPanelWidth() - 2 * getBorderSize()) > getRatio();
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

		shapeGR.setShape(getShape() != null ? getShape() : factory.makeShape(ShapeType.RECTANGLE, null));
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
		if (shape != null && (shape != shapeGR.getShape() || !shape.equals(shapeGR.getShape()))) {
			shapeGR.setShape(shape.clone());
			/*
			 * if (shape.getShapeType() == ShapeType.CUSTOM_POLYGON) {
			 * System.out.println("Go to edition mode");
			 * controller.setCurrentTool(EditorTool.DrawShapeTool);
			 * controller.getDrawShapeToolController().setShape(
			 * shape.getShape()); }
			 */
			update();
		}
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

	public ForegroundStyle getForegroundStyle() {
		return foregroundStyle;
	}

	public void setForegroundStyle(ForegroundStyle foregroundStyle) {
		this.foregroundStyle = foregroundStyle;
		shapeGR.setForeground(foregroundStyle);
	}

	public BackgroundStyle getBackgroundStyle() {
		return backgroundStyle;
	}

	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		this.backgroundStyle = backgroundStyle;
		shapeGR.setBackground(backgroundStyle);
	}

	public ShadowStyle getShadowStyle() {
		return shadowStyle;
	}

	public void setShadowStyle(ShadowStyle shadowStyle) {
		this.shadowStyle = shadowStyle;
		shapeGR.setShadowStyle(shadowStyle);
	}

}