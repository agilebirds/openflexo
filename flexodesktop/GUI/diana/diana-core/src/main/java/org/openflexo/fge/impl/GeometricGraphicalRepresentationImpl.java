package org.openflexo.fge.impl;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.toolbox.ToolBox;

public abstract class GeometricGraphicalRepresentationImpl extends GraphicalRepresentationImpl implements GeometricGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GeometricGraphicalRepresentation.class.getPackage().getName());

	// *******************************************************************************
	// * Inner classes *
	// *******************************************************************************

	// *******************************************************************************
	// * Fields *
	// *******************************************************************************

	// private int layer = FGEConstants.DEFAULT_OBJECT_LAYER;
	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private FGEArea geometricObject;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public GeometricGraphicalRepresentationImpl() {
		super();
	}

	@SuppressWarnings("unused")
	@Deprecated
	private GeometricGraphicalRepresentationImpl(FGEArea anObject, Object aDrawable, Drawing<?> aDrawing) {
		this();
		// setDrawable(aDrawable);
		// setDrawing(aDrawing);

		foreground = getFactory().makeForegroundStyle(Color.BLACK);
		// foreground.setGraphicalRepresentation(this);
		foreground.getPropertyChangeSupport().addPropertyChangeListener(this);

		background = getFactory().makeColoredBackground(Color.WHITE);
		// background.setGraphicalRepresentation(this);
		background.getPropertyChangeSupport().addPropertyChangeListener(this);

		setGeometricObject(anObject);

		addToMouseClickControls(getFactory().makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(getFactory().makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			addToMouseClickControls(getFactory().makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	/*@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = super.getAllParameters();
		GeometricParameters[] allParams = GeometricParameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}*/

	// ***************************************************************************
	// * Deletion *
	// ***************************************************************************

	@Override
	public boolean delete() {
		if (background != null) {
			background.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (foreground != null) {
			foreground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		return super.delete();
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public ForegroundStyle getForeground() {
		return foreground;
	}

	@Override
	public void setForeground(ForegroundStyle aForeground) {
		FGEAttributeNotification notification = requireChange(FOREGROUND, aForeground, false);
		if (notification != null) {
			if (foreground != null) {
				foreground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			foreground = aForeground;
			if (aForeground != null) {
				aForeground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getNoStroke() {
		return foreground.getNoStroke();
	}

	@Override
	public void setNoStroke(boolean noStroke) {
		foreground.setNoStroke(noStroke);
	}

	@Override
	public BackgroundStyle getBackground() {
		return background;
	}

	@Override
	public void setBackground(BackgroundStyle aBackground) {
		FGEAttributeNotification notification = requireChange(BACKGROUND, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (background != null) {
				background.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			background = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null) {
				aBackground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public BackgroundStyleType getBackgroundType() {
		return background.getBackgroundStyleType();
	}

	@Override
	public void setBackgroundType(BackgroundStyleType backgroundType) {
		if (backgroundType != getBackgroundType()) {
			setBackground(getFactory().makeBackground(backgroundType));
		}
	}

	/*@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public void setLayer(int layer) {
		FGENotification notification = requireChange(Parameters.layer, layer);
		if (notification != null) {
			this.layer = layer;
			hasChanged(notification);
		}
	}*/

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getSource() instanceof BackgroundStyle) {
			notifyAttributeChange(BACKGROUND);
		}
		if (evt.getSource() instanceof ForegroundStyle) {
			notifyAttributeChange(FOREGROUND);
		}
	}

	@Override
	public FGEArea getGeometricObject() {
		return geometricObject;
	}

	@Override
	public void setGeometricObject(FGEArea geometricObject) {
		FGEAttributeNotification notification = requireChange(GEOMETRIC_OBJECT, geometricObject);
		if (notification != null) {
			this.geometricObject = geometricObject;
			hasChanged(notification);
		}
	}

	/*protected final void notifyCPDragged(String name, FGEPoint newLocation)
	{
		notifyGeometryChanged();
		rebuildControlPoints();
	}*/

}
