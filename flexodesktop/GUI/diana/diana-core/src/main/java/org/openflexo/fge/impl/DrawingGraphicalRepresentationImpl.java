package org.openflexo.fge.impl;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.controller.MouseClickControlActionImpl.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseDragControlActionImpl.MouseDragControlActionType;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawingDecorationPainter;
import org.openflexo.fge.notifications.DrawingNeedsToBeRedrawn;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillResize;

public class DrawingGraphicalRepresentationImpl extends ContainerGraphicalRepresentationImpl implements DrawingGraphicalRepresentation {

	private static final Logger logger = Logger.getLogger(DrawingGraphicalRepresentation.class.getPackage().getName());

	private Color backgroundColor = Color.WHITE;

	private Color rectangleSelectingSelectionColor = Color.BLUE;
	private Color focusColor = Color.RED;
	private Color selectionColor = Color.BLUE;
	private boolean drawWorkingArea = false;
	private boolean isResizable = false;
	protected DrawingDecorationPainter decorationPainter;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public DrawingGraphicalRepresentationImpl() {
		super();
		// graphics = new FGEDrawingGraphicsImpl(this);
	}

	@Deprecated
	private DrawingGraphicalRepresentationImpl(Drawing<?> aDrawing) {
		this();
		setDrawing(aDrawing);
		// setDrawable(aDrawing != null ? aDrawing.getModel() : null);
	}

	@Deprecated
	private DrawingGraphicalRepresentationImpl(Drawing<?> aDrawing, boolean initBasicControls) {
		this(aDrawing);
		if (initBasicControls) {
			addToMouseClickControls(MouseClickControlImpl.makeMouseClickControl("Drawing selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.SELECTION));
			addToMouseDragControls(MouseDragControlImpl.makeMouseDragControl("Rectangle selection", MouseButton.LEFT,
					MouseDragControlActionType.RECTANGLE_SELECTING));
			addToMouseDragControls(MouseDragControlImpl.makeMouseDragControl("Zoom", MouseButton.RIGHT, MouseDragControlActionType.ZOOM));
		}
		// width = FGEConstants.DEFAULT_DRAWING_WIDTH;
		// height = FGEConstants.DEFAULT_DRAWING_HEIGHT;
		// bgStyle = getFactory().makeColoredBackground(getBackgroundColor());
	}

	@Override
	public void delete() {
		super.delete();
		/*if (graphics != null) {
			graphics.delete();
		}
		graphics = null;
		decorationGraphics = null;*/
		decorationPainter = null;
	}

	/*@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = super.getAllParameters();
		Parameters[] allParams = Parameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}*/

	/**
	 * Override parent behaviour by always returning true<br>
	 * IMPORTANT: a drawing graphical representation MUST be always validated
	 */
	/*@Override
	public final boolean isValidated() {
		return true;
	}*/

	// ***************************************************************************
	// * Cloning *
	// ***************************************************************************

	/*@Override
	public final void setsWith(GraphicalRepresentation gr) {
		super.setsWith(gr);
		if (gr instanceof DrawingGraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				_setParameterValueWith(p, gr);
			}
		}
	}*/

	/*@Override
	public final void setsWith(GraphicalRepresentation gr, GRParameter... exceptedParameters) {
		super.setsWith(gr, exceptedParameters);
		if (gr instanceof ConnectorGraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				boolean excepted = false;
				for (GRParameter ep : exceptedParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (!excepted) {
					_setParameterValueWith(p, gr);
				}
			}
		}
	}*/

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public FGERectangle getWorkingArea() {
		return new FGERectangle(0, 0, getWidth(), getHeight(), Filling.FILLED);
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public void setBackgroundColor(Color backgroundColor) {
		// logger.info("For "+this+" Set bg color to "+backgroundColor);

		FGENotification notification = requireChange(BACKGROUND_COLOR, backgroundColor);
		if (notification != null) {
			this.backgroundColor = backgroundColor;
			// bgStyle = getFactory().makeColoredBackground(backgroundColor);
			hasChanged(notification);
		}
	}

	/*	@Override
		public double getHeight() {
			return height;
		}

		@Override
		public void setHeight(double aValue) {
			FGENotification notification = requireChange(DrawingParameters.height, aValue);
			if (notification != null) {
				height = aValue;
				hasChanged(notification);
			}
		}

		@Override
		public double getWidth() {
			return width;
		}

		@Override
		public void setWidth(double aValue) {
			FGENotification notification = requireChange(DrawingParameters.width, aValue);
			if (notification != null) {
				width = aValue;
				hasChanged(notification);
			}
		}*/

	@Override
	public Color getFocusColor() {
		return focusColor;
	}

	@Override
	public void setFocusColor(Color focusColor) {
		FGENotification notification = requireChange(FOCUS_COLOR, focusColor);
		if (notification != null) {
			this.focusColor = focusColor;
			hasChanged(notification);
		}
	}

	@Override
	public Color getSelectionColor() {
		return selectionColor;
	}

	@Override
	public void setSelectionColor(Color selectionColor) {
		FGENotification notification = requireChange(SELECTION_COLOR, selectionColor);
		if (notification != null) {
			this.selectionColor = selectionColor;
			hasChanged(notification);
		}
	}

	@Override
	public Color getRectangleSelectingSelectionColor() {
		return rectangleSelectingSelectionColor;
	}

	@Override
	public void setRectangleSelectingSelectionColor(Color selectionColor) {
		FGENotification notification = requireChange(RECTANGLE_SELECTING_SELECTION_COLOR, selectionColor);
		if (notification != null) {
			this.rectangleSelectingSelectionColor = selectionColor;
			hasChanged(notification);
		}
	}

	/*@Override
	public int getViewX(double scale) {
		return 0;
	}

	@Override
	public int getViewY(double scale) {
		return 0;
	}

	@Override
	public int getViewWidth(double scale) {
		return (int) (getWidth() * scale);
	}

	@Override
	public int getViewHeight(double scale) {
		return (int) (getHeight() * scale);
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0, 0, getWidth(), getHeight(), Filling.FILLED);
	}*/

	/*@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		return false;
	}*/

	/*@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return AffineTransform.getScaleInstance(scale, scale);
	}*/

	/*@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return AffineTransform.getScaleInstance(1 / scale, 1 / scale);

	}*/

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	/*@Override
	public void paint(Graphics g, DrawingControllerImpl controller) {
		Graphics2D g2 = (Graphics2D) g;
		graphics.createGraphics(g2, controller);
		// If there is a decoration painter init its graphics
		if (decorationPainter != null) {
			decorationGraphics.createGraphics(g2, controller);
		}

		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (decorationPainter != null && decorationPainter.paintBeforeDrawing()) {
			decorationPainter.paintDecoration(decorationGraphics);
		}

		super.paint(g, controller);

		if (!(bgStyle instanceof ColorBackgroundStyle) || !((ColorBackgroundStyle) bgStyle).getColor().equals(getBackgroundColor())) {
			bgStyle = getFactory().makeColoredBackground(getBackgroundColor());
		}

		ForegroundStyle fgStyle = getFactory().makeForegroundStyle(Color.DARK_GRAY);

		graphics.setDefaultForeground(fgStyle);
		graphics.setDefaultBackground(bgStyle);
		if (drawWorkingArea) {
			getWorkingArea().paint(graphics);
		}
		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (decorationPainter != null && !decorationPainter.paintBeforeDrawing()) {
			decorationPainter.paintDecoration(decorationGraphics);
		}

		graphics.releaseGraphics();

	}*/

	@Override
	public DrawingDecorationPainter getDecorationPainter() {
		return decorationPainter;
	}

	@Override
	public void setDecorationPainter(DrawingDecorationPainter aPainter) {
		// decorationGraphics = new FGEDrawingDecorationGraphicsImpl(this);
		decorationPainter = aPainter;
	}

	@Override
	public final String getText() {
		return null;
	}

	@Override
	public String getInspectorName() {
		return "DrawingGraphicalRepresentation.inspector";
	}

	/*@Override
	public GraphicalRepresentation getContainerGraphicalRepresentation() {
		return null;
	}

	@Override
	public final boolean shouldBeDisplayed() {
		return true;
	}*/

	@Override
	public boolean getIsVisible() {
		return true;
	}

	/*@Override
	public Vector<GraphicalRepresentation> allGraphicalRepresentations() {
		Vector<GraphicalRepresentation> returned = new Vector<GraphicalRepresentation>();
		_appendGraphicalRepresentations(returned, this);
		return returned;
	}

	private static void _appendGraphicalRepresentations(Vector<GraphicalRepresentation> v, GraphicalRepresentation gr) {
		v.add(gr);
		List<? extends Object> containedObjects = gr.getContainedObjects();
		if (containedObjects == null) {
			return;
		}
		for (Object drawable : containedObjects) {
			GraphicalRepresentation next = gr.getGraphicalRepresentation(drawable);
			_appendGraphicalRepresentations(v, next);
		}
	}*/

	/*@Override
	public void finalizeDeserialization()
	{
		logger.info("ICI ???");
		super.finalizeDeserialization();
		for (GraphicalRepresentation gr : allGraphicalRepresentations()) {
			logger.info("gr="+gr);
			if (gr instanceof ConnectorGraphicalRepresentation) {
				((ConnectorGraphicalRepresentation)gr).observeRelevantObjects();
			}
		}
	}*/

	@Override
	public boolean getDrawWorkingArea() {
		return drawWorkingArea;
	}

	@Override
	public void setDrawWorkingArea(boolean drawWorkingArea) {
		// logger.info("setDrawWorkingArea with "+drawWorkingArea);

		FGENotification notification = requireChange(DRAW_WORKING_AREA, drawWorkingArea);
		if (notification != null) {
			this.drawWorkingArea = drawWorkingArea;
			hasChanged(notification);
		}
	}

	@Override
	public boolean isResizable() {
		return isResizable;
	}

	@Override
	public void setIsResizable(boolean isResizable) {
		FGENotification notification = requireChange(IS_RESIZABLE, isResizable);
		if (notification != null) {
			this.isResizable = isResizable;
			hasChanged(notification);
		}
	}

	/*@Override
	public void startConnectorObserving() {
		for (GraphicalRepresentation gr : allGraphicalRepresentations()) {
			if (gr instanceof ConnectorGraphicalRepresentation) {
				((ConnectorGraphicalRepresentation) gr).observeRelevantObjects();
			}
		}
	}*/

	@Override
	public FGEDimension getSize() {
		return new FGEDimension(getWidth(), getHeight());
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(FGEDimension oldSize) {
		setChanged();
		notifyObservers(new ObjectResized(oldSize, getSize()));
	}

	/**
	 * Notify that the object will be resized
	 */
	@Override
	public void notifyObjectWillResize() {
		setChanged();
		notifyObservers(new ObjectWillResize());
	}

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	@Override
	public void notifyObjectHasResized() {
		setChanged();
		notifyObservers(new ObjectHasResized());
	}

	@Override
	public void notifyDrawingNeedsToBeRedrawn() {
		setChanged();
		notifyObservers(new DrawingNeedsToBeRedrawn());
	}

	/*@Override
	public FGEDrawingGraphicsImpl getGraphics() {
		return graphics;
	}*/

	/*@Override
	public ShapeGraphicalRepresentation getTopLevelShapeGraphicalRepresentation(FGEPoint p) {
		return getTopLevelShapeGraphicalRepresentation(this, p);
	}

	private ShapeGraphicalRepresentation getTopLevelShapeGraphicalRepresentation(GraphicalRepresentation container, FGEPoint p) {

		List<ShapeGraphicalRepresentation> enclosingShapes = new ArrayList<ShapeGraphicalRepresentation>();

		for (GraphicalRepresentation gr : container.getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation child = (ShapeGraphicalRepresentation) gr;
				if (child.getShape().getShape().containsPoint(convertNormalizedPoint(this, p, child))) {
					enclosingShapes.add(child);
				} else {
					// Look if we are not contained in a child shape outside current shape
					GraphicalRepresentation insideFocusedShape = getTopLevelShapeGraphicalRepresentation(child, p);
					if (insideFocusedShape != null && insideFocusedShape instanceof ShapeGraphicalRepresentation) {
						enclosingShapes.add((ShapeGraphicalRepresentation) insideFocusedShape);
					}
				}
			}
		}

		if (enclosingShapes.size() > 0) {

			Collections.sort(enclosingShapes, new Comparator<ShapeGraphicalRepresentation>() {
				@Override
				public int compare(ShapeGraphicalRepresentation o1, ShapeGraphicalRepresentation o2) {
					if (o2.getLayer() == o1.getLayer() && o1.getParentGraphicalRepresentation() != null
							&& o1.getParentGraphicalRepresentation() == o2.getParentGraphicalRepresentation()) {
						return o1.getParentGraphicalRepresentation().getOrder(o1, o2);
					}
					return o2.getLayer() - o1.getLayer();
				}
			});

			ShapeGraphicalRepresentation focusedShape = enclosingShapes.get(0);

			ShapeGraphicalRepresentation insideFocusedShape = getTopLevelShapeGraphicalRepresentation(focusedShape, p);

			if (insideFocusedShape != null) {
				return insideFocusedShape;
			} else {
				return focusedShape;
			}
		}

		return null;

	}*/

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

	/*@Override
	public void performRandomLayout() {
		performRandomLayout(getWidth(), getHeight());
	}

	@Override
	public void performAutoLayout() {
		performAutoLayout(getWidth(), getHeight());
	}*/

}
