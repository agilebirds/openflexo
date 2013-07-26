package org.openflexo.fge.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

@SuppressWarnings("serial")
public class ShapePreviewPanel extends JPanel implements FIBCustomComponent<ShapeSpecification, ShapePreviewPanel> {

	private Drawing<ShapePreviewPanel> drawing;
	private DrawingGraphicalRepresentation drawingGR;
	private DrawingController<ShapePreviewPanel> controller;
	// private RepresentedDrawing representedDrawing;
	// private RepresentedShape representedShape;

	private ShapeGraphicalRepresentation shapeGR;

	private int border = 10;
	private int width = 120;
	private int height = 80;
	private static final float RATIO = 0.6f;

	private FGEModelFactory factory;

	private ForegroundStyle foregroundStyle;
	private BackgroundStyle backgroundStyle;
	private ShadowStyle shadowStyle;

	public ShapePreviewPanel(ShapeSpecification aShape) {
		super(new BorderLayout());

		factory = FGEUtils.TOOLS_FACTORY;

		// representedDrawing = new RepresentedDrawing();
		// representedShape = new RepresentedShape();
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));

		foregroundStyle = factory.makeForegroundStyle(Color.BLACK);
		backgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		shadowStyle = factory.makeNoneShadowStyle();

		// final Vector<RepresentedShape> singleton = new Vector<RepresentedShape>();
		// singleton.add(representedShape);

		drawing = new DrawingImpl<ShapePreviewPanel>(this, factory) {
			@Override
			public void init() {
				final DrawingGRBinding<ShapePreviewPanel> previewPanelBinding = bindDrawing(ShapePreviewPanel.class, "previewPanel",
						new DrawingGRProvider<ShapePreviewPanel>() {
							@Override
							public DrawingGraphicalRepresentation provideGR(ShapePreviewPanel drawable, FGEModelFactory factory) {
								return drawingGR;
							}
						});
				final ShapeGRBinding<ShapePreviewPanel> shapeBinding = bindShape(ShapePreviewPanel.class, "line",
						new ShapeGRProvider<ShapePreviewPanel>() {
							@Override
							public ShapeGraphicalRepresentation provideGR(ShapePreviewPanel drawable, FGEModelFactory factory) {
								return shapeGR;
							}
						});

				previewPanelBinding.addToWalkers(new GRStructureWalker<ShapePreviewPanel>() {

					@Override
					public void walk(ShapePreviewPanel previewPanel) {
						drawShape(shapeBinding, previewPanel, previewPanel);
					}
				});
			}
		};
		drawing.setEditable(false);

		drawingGR = factory.makeDrawingGraphicalRepresentation(drawing, false);
		drawingGR.setBackgroundColor(Color.WHITE);
		drawingGR.setWidth(getPanelWidth());
		drawingGR.setHeight(getPanelHeight());
		drawingGR.setDrawWorkingArea(false);
		shapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE, drawing);
		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());
		shapeGR.setForeground(getForegroundStyle());
		shapeGR.setBackground(getBackgroundStyle());
		shapeGR.setShadowStyle(getShadowStyle());
		shapeGR.setShape(getShape() != null ? getShape() : factory.makeShape(ShapeType.RECTANGLE));
		shapeGR.setIsSelectable(false);
		shapeGR.setIsFocusable(false);
		shapeGR.setIsReadOnly(true);
		shapeGR.setBorder(factory.makeShapeBorder(getBorderSize(), getBorderSize(), getBorderSize(), getBorderSize()));

		controller = new DrawingController<ShapePreviewPanel>(drawing, factory);
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

		// getShape().updateShape();

		shapeGR.setShape(getShape() != null ? getShape() : factory.makeShape(ShapeType.RECTANGLE));
		shapeGR.notifyShapeChanged();

		shapeGR.setX(getShapeX());
		shapeGR.setY(getShapeY());
		shapeGR.setWidth(getShapeWidth());
		shapeGR.setHeight(getShapeHeight());

		if (controller.getPaintManager() != null) {
			controller.getPaintManager().repaint(controller.getDrawingView());
		}

	}

	public ShapeSpecification getShape() {
		return shapeGR.getShape();
	}

	public void setShape(ShapeSpecification shape) {
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
	public ShapeSpecification getEditedObject() {
		return getShape();
	}

	@Override
	public void setEditedObject(ShapeSpecification object) {
		setShape(object);
	}

	@Override
	public ShapeSpecification getRevertValue() {
		return null;
	}

	@Override
	public void setRevertValue(ShapeSpecification object) {
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public Class<ShapeSpecification> getRepresentedType() {
		return ShapeSpecification.class;
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	/*public class RepresentedDrawing {
	}

	public class RepresentedShape {
		public ShapeSpecification getRepresentedShape() {
			return getShape();
		}
	}*/

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