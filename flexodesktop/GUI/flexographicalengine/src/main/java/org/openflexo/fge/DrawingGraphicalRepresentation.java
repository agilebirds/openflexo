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

import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawingDecorationPainter;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the diagram as the top-level graphical representation <br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DrawingGraphicalRepresentationImpl.class)
@XMLElement(xmlTag = "DrawingGraphicalRepresentation")
public interface DrawingGraphicalRepresentation extends ContainerGraphicalRepresentation {

	// Property keys

	public static final String BACKGROUND_COLOR = "backgroundColor";
	public static final String RECTANGLE_SELECTING_SELECTION_COLOR = "rectangleSelectingSelectionColor";
	public static final String FOCUS_COLOR = "focusColor";
	public static final String SELECTION_COLOR = "selectionColor";
	public static final String DRAW_WORKING_AREA = "drawWorkingArea";
	public static final String IS_RESIZABLE = "isResizable";

	public static enum DrawingParameters implements GRParameter {
		backgroundColor, width, height, rectangleSelectingSelectionColor, focusColor, selectionColor, drawWorkingArea, isResizable;
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = BACKGROUND_COLOR)
	@XMLAttribute
	public abstract Color getBackgroundColor();

	@Setter(value = BACKGROUND_COLOR)
	public abstract void setBackgroundColor(Color backgroundColor);

	@Override
	@Getter(value = WIDTH, defaultValue = "" + FGEConstants.DEFAULT_DRAWING_WIDTH)
	@XMLAttribute
	public abstract double getWidth();

	@Override
	@Getter(value = HEIGHT, defaultValue = "" + FGEConstants.DEFAULT_DRAWING_HEIGHT)
	@XMLAttribute
	public abstract double getHeight();

	@Getter(value = FOCUS_COLOR)
	@XMLAttribute
	public abstract Color getFocusColor();

	@Setter(value = FOCUS_COLOR)
	public abstract void setFocusColor(Color focusColor);

	@Getter(value = SELECTION_COLOR)
	@XMLAttribute
	public abstract Color getSelectionColor();

	@Setter(value = SELECTION_COLOR)
	public abstract void setSelectionColor(Color selectionColor);

	@Getter(value = RECTANGLE_SELECTING_SELECTION_COLOR)
	@XMLAttribute
	public abstract Color getRectangleSelectingSelectionColor();

	@Setter(value = RECTANGLE_SELECTING_SELECTION_COLOR)
	public abstract void setRectangleSelectingSelectionColor(Color selectionColor);

	@Getter(value = DRAW_WORKING_AREA, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getDrawWorkingArea();

	@Setter(DRAW_WORKING_AREA)
	public abstract void setDrawWorkingArea(boolean drawWorkingArea);

	@Getter(value = IS_RESIZABLE, defaultValue = "false")
	@XMLAttribute
	public abstract boolean isResizable();

	@Setter(IS_RESIZABLE)
	public abstract void setIsResizable(boolean isResizable);

	public abstract FGERectangle getWorkingArea();

	// @Override
	// public abstract void paint(Graphics g, DrawingController<?> controller);

	public abstract DrawingDecorationPainter getDecorationPainter();

	public abstract void setDecorationPainter(DrawingDecorationPainter aPainter);

	// public abstract Vector<GraphicalRepresentation> allGraphicalRepresentations();

	// public abstract void startConnectorObserving();

	@Override
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

	// public abstract FGEDrawingGraphics getGraphics();

	// public abstract ShapeGraphicalRepresentation getTopLevelShapeGraphicalRepresentation(FGEPoint p);

	// public abstract void performRandomLayout();

	// public abstract void performAutoLayout();

}
