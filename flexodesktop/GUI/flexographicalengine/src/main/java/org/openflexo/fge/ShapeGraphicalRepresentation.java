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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Observable;

import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.kvc.KVCObject;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Represents a shape in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity
@ImplementationClass(ShapeGraphicalRepresentationImpl.class)
public interface ShapeGraphicalRepresentation<O> extends GraphicalRepresentation<O> {

	// Property keys

	public static final String X = "x";
	public static final String Y = "y";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String MINIMAL_WIDTH = "minimalWidth";
	public static final String MINIMAL_HEIGHT = "minimalHeight";
	public static final String MAXIMAL_WIDTH = "maximalWidth";
	public static final String MAXIMAL_HEIGHT = "maximalHeight";
	public static final String DIMENSION_CONSTRAINT_STEP = "dimensionConstraintStep";
	public static final String LOCATION_CONSTRAINTS = "locationConstraints";
	public static final String LOCATION_CONSTRAINED_AREA = "locationConstrainedArea";
	public static final String DIMENSION_CONSTRAINTS = "dimensionConstraints";
	public static final String ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH = "adjustMinimalWidthToLabelWidth";
	public static final String ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT = "adjustMinimalHeightToLabelHeight";
	public static final String ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH = "adjustMaximalWidthToLabelWidth";
	public static final String ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT = "adjustMaximalHeightToLabelHeight";
	public static final String FOREGROUND = "foreground";
	public static final String BACKGROUND = "background";
	public static final String SELECTED_FOREGROUND = "selectedForeground";
	public static final String SELECTED_BACKGROUND = "selectedBackground";
	public static final String FOCUSED_FOREGROUND = "focusedForeground";
	public static final String FOCUSED_BACKGROUND = "focusedBackground";
	public static final String HAS_SELECTED_FOREGROUND = "hasSelectedForeground";
	public static final String HAS_SELECTED_BACKGROUND = "hasSelectedBackground";
	public static final String HAS_FOCUSED_FOREGROUND = "hasFocusedForeground";
	public static final String HAS_FOCUSED_BACKGROUND = "hasFocusedBackground";
	public static final String BORDER = "border";
	public static final String SHAPE_TYPE = "shapeType";
	public static final String SHAPE = "shape";
	public static final String SHADOW_STYLE = "shadowStyle";
	public static final String IS_FLOATING_LABEL = "isFloatingLabel";
	public static final String RELATIVE_TEXT_X = "relativeTextX";
	public static final String RELATIVE_TEXT_Y = "relativeTextY";
	public static final String DECORATION_PAINTER = "decorationPainter";
	public static final String SHAPE_PAINTER = "shapePainter";
	public static final String ALLOW_TO_LEAVE_BOUNDS = "allowToLeaveBounds";
	public static final String X_CONSTRAINTS = "xConstraints";
	public static final String Y_CONSTRAINTS = "yConstraints";
	public static final String WIDTH_CONSTRAINTS = "widthConstraints";
	public static final String HEIGHT_CONSTRAINTS = "heightConstraints";

	// *******************************************************************************
	// * Inner concepts
	// *******************************************************************************

	public static enum ShapeParameters implements GRParameter {
		x,
		y,
		width,
		height,
		minimalWidth,
		minimalHeight,
		maximalWidth,
		maximalHeight,
		dimensionConstraintStep,
		locationConstraints,
		locationConstrainedArea,
		dimensionConstraints,
		adjustMinimalWidthToLabelWidth,
		adjustMinimalHeightToLabelHeight,
		adjustMaximalWidthToLabelWidth,
		adjustMaximalHeightToLabelHeight,
		foreground,
		background,
		selectedForeground,
		selectedBackground,
		focusedForeground,
		focusedBackground,
		hasSelectedForeground,
		hasSelectedBackground,
		hasFocusedForeground,
		hasFocusedBackground,
		border,
		shapeType,
		shape,
		shadowStyle,
		isFloatingLabel,
		relativeTextX,
		relativeTextY,
		decorationPainter,
		shapePainter,
		specificStroke,
		allowToLeaveBounds,
		xConstraints,
		yConstraints,
		widthConstraints,
		heightConstraints;
	}

	public static enum DimensionConstraints {
		FREELY_RESIZABLE, UNRESIZABLE, CONSTRAINED_DIMENSIONS, STEP_CONSTRAINED, WIDTH_FIXED, HEIGHT_FIXED, CONTAINER
	}

	public static enum LocationConstraints {
		/**
		 * Shape is freely relocatable in parent global bounds (rectangular bounds, don't care about borders nor shape of parent)
		 */
		FREELY_MOVABLE,
		/**
		 * Shape is freely relocatable in parent exact bounds (shape of this GR must be fully located INSIDE parent GR outline)
		 */
		CONTAINED_IN_SHAPE,
		/**
		 * Shape is not movable
		 */
		UNMOVABLE, RELATIVE_TO_PARENT, X_FIXED, Y_FIXED, AREA_CONSTRAINED;
	}

	public static class ShapeBorder extends KVCObject implements XMLSerializable, Cloneable {
		public int top;
		public int bottom;
		public int left;
		public int right;

		public ShapeBorder() {
			super();
			this.top = FGEConstants.DEFAULT_BORDER_SIZE;
			this.bottom = FGEConstants.DEFAULT_BORDER_SIZE;
			this.left = FGEConstants.DEFAULT_BORDER_SIZE;
			this.right = FGEConstants.DEFAULT_BORDER_SIZE;
		}

		public ShapeBorder(int top, int bottom, int left, int right) {
			super();
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
		}

		public ShapeBorder(ShapeBorder border) {
			super();
			this.top = border.top;
			this.bottom = border.bottom;
			this.left = border.left;
			this.right = border.right;
		}

		@Override
		public ShapeBorder clone() {
			try {
				return (ShapeBorder) super.clone();
			} catch (CloneNotSupportedException e) {
				// cannot happen, we are clonable
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public String toString() {
			return "ShapeBorder [" + left + "," + top + "," + right + "," + bottom + "]";
		}

	}

	// *******************************************************************************
	// * Model
	// *******************************************************************************

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = X)
	@XMLAttribute
	public abstract double getX();

	@Setter(value = X)
	public abstract void setX(double aValue);

	@Getter(value = Y)
	@XMLAttribute
	public abstract double getY();

	@Setter(value = Y)
	public abstract void setY(double aValue);

	@Getter(value = WIDTH)
	@XMLAttribute
	public abstract double getWidth();

	@Setter(value = WIDTH)
	public abstract void setWidth(double aValue);

	@Getter(value = HEIGHT)
	@XMLAttribute
	public abstract double getHeight();

	@Setter(value = HEIGHT)
	public abstract void setHeight(double aValue);

	@Getter(value = MINIMAL_WIDTH)
	@XMLAttribute
	public abstract double getMinimalWidth();

	@Setter(value = MINIMAL_WIDTH)
	public abstract void setMinimalWidth(double minimalWidth);

	@Getter(value = MINIMAL_HEIGHT)
	@XMLAttribute
	public abstract double getMinimalHeight();

	@Setter(value = MINIMAL_HEIGHT)
	public abstract void setMinimalHeight(double minimalHeight);

	@Getter(value = MAXIMAL_HEIGHT)
	@XMLAttribute
	public abstract double getMaximalHeight();

	@Setter(value = MAXIMAL_HEIGHT)
	public abstract void setMaximalHeight(double maximalHeight);

	@Getter(value = MAXIMAL_WIDTH)
	@XMLAttribute
	public abstract double getMaximalWidth();

	@Setter(value = MAXIMAL_WIDTH)
	public abstract void setMaximalWidth(double maximalWidth);

	@Getter(value = DIMENSION_CONSTRAINT_STEP)
	@XMLAttribute
	public abstract FGESteppedDimensionConstraint getDimensionConstraintStep();

	@Setter(value = DIMENSION_CONSTRAINT_STEP)
	public abstract void setDimensionConstraintStep(FGESteppedDimensionConstraint dimensionConstraintStep);

	@Getter(value = LOCATION_CONSTRAINTS)
	@XMLAttribute
	public abstract LocationConstraints getLocationConstraints();

	@Setter(value = LOCATION_CONSTRAINTS)
	public abstract void setLocationConstraints(LocationConstraints locationConstraints);

	@Getter(value = LOCATION_CONSTRAINED_AREA)
	@XMLAttribute
	public abstract FGEArea getLocationConstrainedArea();

	@Setter(value = LOCATION_CONSTRAINED_AREA)
	public abstract void setLocationConstrainedArea(FGEArea locationConstrainedArea);

	@Getter(value = DIMENSION_CONSTRAINTS)
	@XMLAttribute
	public abstract DimensionConstraints getDimensionConstraints();

	@Setter(value = DIMENSION_CONSTRAINTS)
	public abstract void setDimensionConstraints(DimensionConstraints dimensionConstraints);

	@Getter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getAdjustMinimalWidthToLabelWidth();

	@Setter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH)
	public abstract void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth);

	@Getter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getAdjustMinimalHeightToLabelHeight();

	@Setter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT)
	public abstract void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight);

	@Getter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getAdjustMaximalWidthToLabelWidth();

	@Setter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH)
	public abstract void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth);

	@Getter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getAdjustMaximalHeightToLabelHeight();

	@Setter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT)
	public abstract void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight);

	@Getter(value = FOREGROUND)
	@XMLElement
	public abstract ForegroundStyle getForeground();

	@Setter(value = FOREGROUND)
	public abstract void setForeground(ForegroundStyle aForeground);

	@Getter(value = SELECTED_FOREGROUND)
	@XMLElement(context = "Selected")
	public abstract ForegroundStyle getSelectedForeground();

	@Setter(value = SELECTED_FOREGROUND)
	public abstract void setSelectedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_SELECTED_FOREGROUND)
	@XMLAttribute
	public abstract boolean getHasSelectedForeground();

	@Setter(value = HAS_SELECTED_FOREGROUND)
	public abstract void setHasSelectedForeground(boolean aFlag);

	@Getter(value = FOCUSED_FOREGROUND)
	@XMLElement(context = "Focused")
	public abstract ForegroundStyle getFocusedForeground();

	@Setter(value = FOCUSED_FOREGROUND)
	public abstract void setFocusedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_FOCUSED_FOREGROUND)
	@XMLAttribute
	public abstract boolean getHasFocusedForeground();

	@Setter(value = HAS_FOCUSED_FOREGROUND)
	public abstract void setHasFocusedForeground(boolean aFlag);

	@Getter(value = BACKGROUND)
	@XMLElement
	public abstract BackgroundStyle getBackground();

	@Setter(value = BACKGROUND)
	public abstract void setBackground(BackgroundStyle aBackground);

	@Getter(value = SELECTED_BACKGROUND)
	@XMLElement(context = "Selected")
	public abstract BackgroundStyle getSelectedBackground();

	@Setter(value = SELECTED_BACKGROUND)
	public abstract void setSelectedBackground(BackgroundStyle aBackground);

	@Getter(value = HAS_SELECTED_BACKGROUND)
	@XMLAttribute
	public abstract boolean getHasSelectedBackground();

	@Setter(value = HAS_SELECTED_BACKGROUND)
	public abstract void setHasSelectedBackground(boolean aFlag);

	@Getter(value = FOCUSED_BACKGROUND)
	@XMLElement(context = "Focused")
	public abstract BackgroundStyle getFocusedBackground();

	@Setter(value = FOCUSED_BACKGROUND)
	public abstract void setFocusedBackground(BackgroundStyle aBackground);

	@Getter(value = HAS_FOCUSED_BACKGROUND)
	@XMLAttribute
	public abstract boolean getHasFocusedBackground();

	@Setter(value = HAS_FOCUSED_BACKGROUND)
	public abstract void setHasFocusedBackground(boolean aFlag);

	@Getter(value = BORDER)
	@XMLAttribute
	public abstract ShapeBorder getBorder();

	@Setter(value = BORDER)
	public abstract void setBorder(ShapeBorder border);

	@Getter(value = SHAPE_TYPE)
	@XMLAttribute
	public abstract ShapeType getShapeType();

	@Setter(value = SHAPE_TYPE)
	public abstract void setShapeType(ShapeType shapeType);

	@Getter(value = SHAPE)
	@XMLAttribute
	public abstract Shape getShape();

	@Setter(value = SHAPE)
	public abstract void setShape(Shape aShape);

	@Getter(value = SHADOW_STYLE)
	@XMLElement
	public abstract ShadowStyle getShadowStyle();

	@Setter(value = SHADOW_STYLE)
	public abstract void setShadowStyle(ShadowStyle aShadowStyle);

	@Getter(value = IS_FLOATING_LABEL)
	@XMLAttribute
	public abstract boolean getIsFloatingLabel();

	@Setter(value = IS_FLOATING_LABEL)
	public abstract void setIsFloatingLabel(boolean isFloatingLabel);

	@Getter(value = RELATIVE_TEXT_X)
	@XMLAttribute
	public abstract double getRelativeTextX();

	@Setter(value = RELATIVE_TEXT_X)
	public abstract void setRelativeTextX(double textX);

	@Getter(value = RELATIVE_TEXT_Y)
	@XMLAttribute
	public abstract double getRelativeTextY();

	@Setter(value = RELATIVE_TEXT_Y)
	public abstract void setRelativeTextY(double textY);

	@Getter(value = ALLOW_TO_LEAVE_BOUNDS)
	@XMLAttribute
	public abstract boolean getAllowToLeaveBounds();

	@Setter(value = ALLOW_TO_LEAVE_BOUNDS)
	public abstract void setAllowToLeaveBounds(boolean allowToLeaveBounds);

	@Getter(value = X_CONSTRAINTS)
	@XMLAttribute
	public abstract DataBinding getXConstraints();

	@Setter(value = X_CONSTRAINTS)
	public abstract void setXConstraints(DataBinding xConstraints);

	@Getter(value = Y_CONSTRAINTS)
	@XMLAttribute
	public abstract DataBinding getYConstraints();

	@Setter(value = Y_CONSTRAINTS)
	public abstract void setYConstraints(DataBinding yConstraints);

	@Getter(value = WIDTH_CONSTRAINTS)
	@XMLAttribute
	public abstract DataBinding getWidthConstraints();

	@Setter(value = WIDTH_CONSTRAINTS)
	public abstract void setWidthConstraints(DataBinding widthConstraints);

	@Getter(value = HEIGHT_CONSTRAINTS)
	@XMLAttribute
	public abstract DataBinding getHeightConstraints();

	@Setter(value = HEIGHT_CONSTRAINTS)
	public abstract void setHeightConstraints(DataBinding heightConstraints);

	// *******************************************************************************
	// * Position management
	// *******************************************************************************

	public abstract FGEPoint getLocation();

	public abstract void setLocation(FGEPoint newLocation);

	public abstract FGEPoint getLocationInDrawing();

	// *******************************************************************************
	// * Size management
	// *******************************************************************************

	public abstract Dimension getNormalizedLabelSize();

	public abstract Rectangle getNormalizedLabelBounds();

	public abstract boolean isFullyContainedInContainer();

	public abstract boolean isParentLayoutedAsContainer();

	public abstract double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation);

	public abstract FGEDimension getSize();

	public abstract void setSize(FGEDimension newSize);

	// *******************************************************************************
	// * Properties management
	// *******************************************************************************

	@Override
	public abstract boolean hasFloatingLabel();

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public abstract ShapeGraphicalRepresentation<O> clone();

	// This might be very dangerous to override this (this has been done in the past)
	// SGU: Developer really need to think about what he's doing while overriding this
	@Override
	public abstract void update(Observable observable, Object notification);

	public abstract void extendParentBoundsToHostThisShape();

	public abstract void finalizeConstraints();

	public abstract void updateConstraints();

	public abstract void constraintChanged(DataBinding constraint);

	public abstract boolean getNoStroke();

	public abstract void setNoStroke(boolean noStroke);

	public abstract BackgroundStyleType getBackgroundType();

	public abstract void setBackgroundType(BackgroundStyleType backgroundType);

	public abstract void notifyShapeChanged();

	public abstract void notifyShapeNeedsToBeRedrawn();

	public abstract void notifyObjectMoved();

	public abstract void notifyObjectMoved(FGEPoint oldLocation);

	public abstract void notifyObjectWillMove();

	public abstract void notifyObjectHasMoved();

	public abstract boolean isMoving();

	/**
	 * Notify that the object just resized
	 */
	public abstract void notifyObjectResized();

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

	public abstract boolean isResizing();

	@Override
	public abstract boolean shouldBeDisplayed();

	@Override
	public abstract int getViewX(double scale);

	@Override
	public abstract int getViewY(double scale);

	@Override
	public abstract int getViewWidth(double scale);

	public abstract double getUnscaledViewWidth();

	@Override
	public abstract int getViewHeight(double scale);

	public abstract double getUnscaledViewHeight();

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	public abstract FGERectangle getBounds();

	/**
	 * Return view bounds (excluding border) relative to parent container
	 * 
	 * @param scale
	 * @return
	 */
	public abstract Rectangle getBounds(double scale);

	/**
	 * Return view bounds (excluding border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	public abstract Rectangle getBounds(GraphicalRepresentation<?> container, double scale);

	/**
	 * Return logical bounds (including border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	public abstract Rectangle getViewBounds(GraphicalRepresentation<?> container, double scale);

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	public abstract boolean isPointInsideShape(FGEPoint aPoint);

	public abstract DecorationPainter getDecorationPainter();

	public abstract void setDecorationPainter(DecorationPainter aPainter);

	public abstract ShapePainter getShapePainter();

	public abstract void setShapePainter(ShapePainter aPainter);

	@Override
	public abstract void paint(Graphics g, DrawingController<?> controller);

	@Override
	public abstract Point getLabelLocation(double scale);

	@Override
	public abstract void setLabelLocation(Point point, double scale);

	@Override
	public abstract int getAvailableLabelWidth(double scale);

	@Override
	public abstract void setHorizontalTextAlignment(
			org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment horizontalTextAlignment);

	@Override
	public abstract void setVerticalTextAlignment(org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment verticalTextAlignment);

	@Override
	public abstract String getInspectorName();

	// Override for a custom view management
	public abstract ShapeView<O> makeShapeView(DrawingController<?> controller);

	@Override
	public abstract String toString();

	@Override
	public abstract void notifyObjectHierarchyHasBeenUpdated();

	public abstract List<? extends ControlArea<?>> getControlAreas();

	public abstract FGEShapeGraphics getGraphics();

	/**
	 * Override this if you want to use such a feature
	 * 
	 * @return
	 */
	public abstract boolean isAllowedToBeDraggedOutsideParentContainer();

	/**
	 * Override this if you want to use this feature Default implementation return always false
	 * 
	 * @return
	 */
	public abstract boolean isAllowedToBeDraggedOutsideParentContainerInsideContainer(GraphicalRepresentation<?> container);

	/**
	 * Override this if you want to use this feature Default implementation does nothing return boolean indicating if drag was successfully
	 * performed
	 */
	public abstract boolean dragOutsideParentContainerInsideContainer(GraphicalRepresentation<?> container, FGEPoint location);

	public abstract void performRandomLayout();

	public abstract void performAutoLayout();

}
