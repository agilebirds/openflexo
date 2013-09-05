package org.openflexo.fge.impl;

import java.util.logging.Logger;

import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;

public abstract class ContainerGraphicalRepresentationImpl extends GraphicalRepresentationImpl implements ContainerGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DrawingGraphicalRepresentation.class.getPackage().getName());

	private double width;
	private double height;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ContainerGraphicalRepresentationImpl() {
		super();
		// graphics = new FGEDrawingGraphics(this);
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setWidth(double aValue) {
		FGENotification notification = requireChange(WIDTH, aValue);
		if (notification != null) {
			// FGEDimension oldSize = getSize();
			width = aValue;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			// notifyObjectResized(oldSize);
		}
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setHeight(double aValue) {
		FGENotification notification = requireChange(HEIGHT, aValue);
		if (notification != null) {
			// FGEDimension oldSize = getSize();
			height = aValue;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			// notifyObjectResized(oldSize);
		}
	}
}
