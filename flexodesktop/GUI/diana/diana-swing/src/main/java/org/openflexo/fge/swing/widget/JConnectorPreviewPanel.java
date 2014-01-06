package org.openflexo.fge.swing.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.PersistenceMode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.swing.JDianaInteractiveViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.view.widget.ConnectorPreviewPanel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

@SuppressWarnings("serial")
public class JConnectorPreviewPanel extends JPanel implements ConnectorPreviewPanel<JConnectorPreviewPanel> {

	private Drawing<JConnectorPreviewPanel> drawing;
	private DrawingGraphicalRepresentation drawingGR;
	private JDianaInteractiveViewer<JConnectorPreviewPanel> controller;

	private ShapeGraphicalRepresentation startShapeGR;
	private ShapeGraphicalRepresentation endShapeGR;
	private ConnectorGraphicalRepresentation connectorGR;

	private int border = 10;
	private int width = 250;
	private int height = 80;
	private static final float RATIO = 0.6f;

	private FGEModelFactory factory;

	private ForegroundStyle foregroundStyle;

	private ForegroundStyle shapeForegroundStyle;
	private BackgroundStyle shapeBackgroundStyle;
	private ShadowStyle shapeShadowStyle;

	public JConnectorPreviewPanel(ConnectorSpecification aConnectorSpecification) {
		super(new BorderLayout());

		factory = FGECoreUtils.TOOLS_FACTORY;

		// representedDrawing = new RepresentedDrawing();
		// representedShape = new RepresentedShape();
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));

		foregroundStyle = factory.makeForegroundStyle(Color.BLACK);
		shapeForegroundStyle = factory.makeForegroundStyle(Color.GRAY);
		shapeBackgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		shapeShadowStyle = factory.makeNoneShadowStyle();

		// final Vector<RepresentedShape> singleton = new Vector<RepresentedShape>();
		// singleton.add(representedShape);

		drawing = new DrawingImpl<JConnectorPreviewPanel>(this, factory, PersistenceMode.SharedGraphicalRepresentations) {
			@Override
			public void init() {
				final DrawingGRBinding<JConnectorPreviewPanel> previewPanelBinding = bindDrawing(JConnectorPreviewPanel.class,
						"previewPanel", new DrawingGRProvider<JConnectorPreviewPanel>() {
							@Override
							public DrawingGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return drawingGR;
							}
						});
				final ShapeGRBinding<JConnectorPreviewPanel> startShapeBinding = bindShape(JConnectorPreviewPanel.class, "startShape",
						new ShapeGRProvider<JConnectorPreviewPanel>() {
							@Override
							public ShapeGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return startShapeGR;
							}
						});
				final ShapeGRBinding<JConnectorPreviewPanel> endShapeBinding = bindShape(JConnectorPreviewPanel.class, "endShape",
						new ShapeGRProvider<JConnectorPreviewPanel>() {
							@Override
							public ShapeGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return endShapeGR;
							}
						});
				final ConnectorGRBinding<JConnectorPreviewPanel> connectorBinding = bindConnector(JConnectorPreviewPanel.class,
						"connector", new ConnectorGRProvider<JConnectorPreviewPanel>() {
							@Override
							public ConnectorGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return connectorGR;
							}
						});

				previewPanelBinding.addToWalkers(new GRStructureVisitor<JConnectorPreviewPanel>() {

					@Override
					public void visit(JConnectorPreviewPanel previewPanel) {
						drawShape(startShapeBinding, previewPanel, previewPanelBinding, previewPanel);
						drawShape(endShapeBinding, previewPanel, previewPanelBinding, previewPanel);
						drawConnector(connectorBinding, previewPanel, startShapeBinding, previewPanel, endShapeBinding, previewPanel,
								previewPanelBinding, previewPanel);
					}
				});
			}
		};
		drawing.setEditable(true);

		drawingGR = factory.makeDrawingGraphicalRepresentation(false);
		drawingGR.setBackgroundColor(Color.WHITE);
		drawingGR.setWidth(getPanelWidth());
		drawingGR.setHeight(getPanelHeight());
		drawingGR.setDrawWorkingArea(false);

		startShapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		startShapeGR.setX(10);
		startShapeGR.setY(10);
		startShapeGR.setWidth(20);
		startShapeGR.setHeight(20);
		startShapeGR.setForeground(shapeForegroundStyle);
		startShapeGR.setBackground(shapeBackgroundStyle);
		startShapeGR.setShadowStyle(shapeShadowStyle);
		startShapeGR.setShapeSpecification(factory.makeShape(ShapeType.RECTANGLE));
		startShapeGR.setIsSelectable(true);
		startShapeGR.setIsFocusable(true);
		startShapeGR.setIsReadOnly(false);
		startShapeGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

		System.out.println("MDC= " + startShapeGR.getMouseDragControls());

		endShapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		endShapeGR.setX(getPanelWidth() - 30);
		endShapeGR.setY(getPanelHeight() - 30);
		endShapeGR.setWidth(20);
		endShapeGR.setHeight(20);
		endShapeGR.setForeground(shapeForegroundStyle);
		endShapeGR.setBackground(shapeBackgroundStyle);
		endShapeGR.setShadowStyle(shapeShadowStyle);
		endShapeGR.setShapeSpecification(factory.makeShape(ShapeType.RECTANGLE));
		endShapeGR.setIsSelectable(true);
		endShapeGR.setIsFocusable(true);
		endShapeGR.setIsReadOnly(false);
		endShapeGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

		connectorGR = factory.makeConnectorGraphicalRepresentation(ConnectorType.LINE);
		connectorGR.setForeground(foregroundStyle);
		connectorGR.setIsSelectable(true);
		connectorGR.setIsFocusable(true);
		connectorGR.setIsReadOnly(false);

		controller = new JDianaInteractiveViewer<JConnectorPreviewPanel>(drawing, factory, SwingToolFactory.DEFAULT);
		add((JComponent) controller.getDrawingView());
	}

	@Override
	public void delete() {

	}

	public int getPanelWidth() {
		return width;
	}

	public void setPanelWidth(int width) {
		this.width = width;
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		drawingGR.setWidth(getPanelWidth());
	}

	public int getPanelHeight() {
		return height;
	}

	public void setPanelHeight(int height) {
		this.height = height;
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		drawingGR.setHeight(getPanelHeight());
	}

	protected void update() {

		// getShape().updateShape();

		connectorGR.setConnectorSpecification(getConnectorSpecification() != null ? getConnectorSpecification() : factory
				.makeConnector(ConnectorType.LINE));
		connectorGR.notifyConnectorModified();

		controller.getDelegate().repaintAll();

	}

	public ConnectorSpecification getConnectorSpecification() {
		return connectorGR.getConnectorSpecification();
	}

	public void setConnectorSpecification(ConnectorSpecification connectorSpecification) {
		if (connectorSpecification != null
				&& (connectorSpecification != connectorGR.getConnectorSpecification() || !connectorSpecification.equals(connectorGR
						.getConnectorSpecification()))) {
			connectorGR.setConnectorSpecification(/*(ConnectorSpecification)*/connectorSpecification/*.clone()*/);
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
	public JConnectorPreviewPanel getJComponent() {
		return this;
	}

	@Override
	public ConnectorSpecification getEditedObject() {
		return getConnectorSpecification();
	}

	@Override
	public void setEditedObject(ConnectorSpecification object) {
		setConnectorSpecification(object);
	}

	@Override
	public ConnectorSpecification getRevertValue() {
		return null;
	}

	@Override
	public void setRevertValue(ConnectorSpecification object) {
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public Class<ConnectorSpecification> getRepresentedType() {
		return ConnectorSpecification.class;
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
		connectorGR.setForeground(foregroundStyle);
	}

}