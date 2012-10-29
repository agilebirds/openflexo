/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Vector;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawingDecorationPainter;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents the diagram as the top-level graphical representation <br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity
@ImplementationClass(DrawingGraphicalRepresentationImpl.class)
public interface DrawingGraphicalRepresentation<M> extends GraphicalRepresentation<M> {

	public static enum DrawingParameters implements GRParameter {
		backgroundColor, width, height, rectangleSelectingSelectionColor, focusColor, selectionColor, drawWorkingArea, isResizable;
	}

	@Override
	public abstract void delete();

	@Override
	public abstract Vector<GRParameter> getAllParameters();

	/**
	 * Override parent behaviour by always returning true<br>
	 * IMPORTANT: a drawing graphical representation MUST be always validated
	 */
	@Override
	public abstract boolean isValidated();

	@Override
	public abstract void setsWith(GraphicalRepresentation<?> gr);

	@Override
	public abstract void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters);

	public abstract FGERectangle getWorkingArea();

	@Override
	public abstract int getLayer();

	public abstract Color getBackgroundColor();

	public abstract void setBackgroundColor(Color backgroundColor);

	public abstract double getHeight();

	public abstract void setHeight(double aValue);

	public abstract double getWidth();

	public abstract void setWidth(double aValue);

	public abstract Color getFocusColor();

	public abstract void setFocusColor(Color focusColor);

	public abstract Color getSelectionColor();

	public abstract void setSelectionColor(Color selectionColor);

	public abstract Color getRectangleSelectingSelectionColor();

	public abstract void setRectangleSelectingSelectionColor(Color selectionColor);

	@Override
	public abstract int getViewX(double scale);

	@Override
	public abstract int getViewY(double scale);

	@Override
	public abstract int getViewWidth(double scale);

	@Override
	public abstract int getViewHeight(double scale);

	@Override
	public abstract FGERectangle getNormalizedBounds();

	@Override
	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	@Override
	public abstract void paint(Graphics g, DrawingController controller);

	public abstract DrawingDecorationPainter getDecorationPainter();

	public abstract void setDecorationPainter(DrawingDecorationPainter aPainter);

	@Override
	public abstract boolean hasText();

	@Override
	public abstract boolean hasFloatingLabel();

	@Override
	public abstract String getInspectorName();

	@Override
	public abstract GraphicalRepresentation<?> getContainerGraphicalRepresentation();

	@Override
	public abstract boolean shouldBeDisplayed();

	@Override
	public abstract boolean getIsVisible();

	public abstract Vector<GraphicalRepresentation> allGraphicalRepresentations();

	public abstract boolean getDrawWorkingArea();

	public abstract void setDrawWorkingArea(boolean drawWorkingArea);

	public abstract boolean isResizable();

	public abstract void setIsResizable(boolean isResizable);

	public abstract void startConnectorObserving();

	public abstract FGEDimension getSize();

	/**
	 * Notify that the object just resized
	 */
	public abstract void notifyObjectResized(FGEDimension oldSize);

	/**
	 * Notify that the object will be resized
	 */
	public abstract void notifyObjectWillResize();

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	public abstract void notifyObjectHasResized();

	public abstract void notifyDrawingNeedsToBeRedrawn();

	public abstract FGEDrawingGraphics getGraphics();

	public abstract ShapeGraphicalRepresentation<?> getTopLevelShapeGraphicalRepresentation(FGEPoint p);

	public abstract void performRandomLayout();

	public abstract void performAutoLayout();

}
