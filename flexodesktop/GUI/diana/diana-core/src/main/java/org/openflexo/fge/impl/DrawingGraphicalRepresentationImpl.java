package org.openflexo.fge.impl;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.control.PredefinedMouseDragControlActionType;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawingDecorationPainter;
import org.openflexo.fge.notifications.DrawingNeedsToBeRedrawn;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillResize;

public abstract class DrawingGraphicalRepresentationImpl extends ContainerGraphicalRepresentationImpl implements
		DrawingGraphicalRepresentation {

	@SuppressWarnings("unused")
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
		// setDrawing(aDrawing);
		// setDrawable(aDrawing != null ? aDrawing.getModel() : null);
	}

	@SuppressWarnings("unused")
	@Deprecated
	private DrawingGraphicalRepresentationImpl(Drawing<?> aDrawing, boolean initBasicControls) {
		this(aDrawing);
		if (initBasicControls) {
			addToMouseClickControls(getFactory().makeMouseClickControl("Drawing selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.SELECTION));
			addToMouseDragControls(getFactory().makeMouseDragControl("Rectangle selection", MouseButton.LEFT,
					PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));
			addToMouseDragControls(getFactory().makeMouseDragControl("Zoom", MouseButton.RIGHT, PredefinedMouseDragControlActionType.ZOOM));
		}
		// width = FGEConstants.DEFAULT_DRAWING_WIDTH;
		// height = FGEConstants.DEFAULT_DRAWING_HEIGHT;
		// bgStyle = getFactory().makeColoredBackground(getBackgroundColor());
	}

	@Override
	public boolean delete() {
		boolean returned = super.delete();
		/*if (graphics != null) {
			graphics.delete();
		}
		graphics = null;
		decorationGraphics = null;*/
		decorationPainter = null;
		return returned;
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

		FGEAttributeNotification notification = requireChange(BACKGROUND_COLOR, backgroundColor);
		if (notification != null) {
			this.backgroundColor = backgroundColor;
			// bgStyle = getFactory().makeColoredBackground(backgroundColor);
			hasChanged(notification);
		}
	}

	@Override
	public Color getFocusColor() {
		return focusColor;
	}

	@Override
	public void setFocusColor(Color focusColor) {
		FGEAttributeNotification notification = requireChange(FOCUS_COLOR, focusColor);
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
		FGEAttributeNotification notification = requireChange(SELECTION_COLOR, selectionColor);
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
		FGEAttributeNotification notification = requireChange(RECTANGLE_SELECTING_SELECTION_COLOR, selectionColor);
		if (notification != null) {
			this.rectangleSelectingSelectionColor = selectionColor;
			hasChanged(notification);
		}
	}

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
	public boolean getIsVisible() {
		return true;
	}

	@Override
	public boolean getDrawWorkingArea() {
		return drawWorkingArea;
	}

	@Override
	public void setDrawWorkingArea(boolean drawWorkingArea) {
		// logger.info("setDrawWorkingArea with "+drawWorkingArea);

		FGEAttributeNotification notification = requireChange(DRAW_WORKING_AREA, drawWorkingArea);
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
		FGEAttributeNotification notification = requireChange(IS_RESIZABLE, isResizable);
		if (notification != null) {
			this.isResizable = isResizable;
			hasChanged(notification);
		}
	}

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

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

}
