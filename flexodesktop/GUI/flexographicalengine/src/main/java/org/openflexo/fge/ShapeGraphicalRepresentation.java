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
import java.util.Vector;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.kvc.KVCObject;
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
public interface ShapeGraphicalRepresentation<O> extends GraphicalRepresentation<O> {

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

	// *******************************************************************************
	// * Border *
	// *******************************************************************************

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

	public abstract ShapeGraphicalRepresentation<O> clone();

	@Override
	public abstract Vector<GRParameter> getAllParameters();

	@Override
	public abstract void delete();

	@Override
	public abstract void setsWith(GraphicalRepresentation<?> gr);

	@Override
	public abstract void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters);

	// This might be very dangerous to override this (this has been done in the past)
	// SGU: Developer really need to think about what he's doing while overriding this
	@Override
	public abstract void update(Observable observable, Object notification);

	public abstract void extendParentBoundsToHostThisShape();

	public abstract double getX();

	public abstract void setX(double aValue);

	public abstract void setXNoNotification(double aValue);

	public abstract double getY();

	public abstract void setY(double aValue);

	public abstract void setYNoNotification(double aValue);

	public abstract FGEPoint getLocation();

	public abstract void setLocation(FGEPoint newLocation);

	public abstract FGEPoint getLocationInDrawing();

	public abstract Dimension getNormalizedLabelSize();

	public abstract Rectangle getNormalizedLabelBounds();

	public abstract LocationConstraints getLocationConstraints();

	public abstract void setLocationConstraints(LocationConstraints locationConstraints);

	public abstract FGEArea getLocationConstrainedArea();

	public abstract void setLocationConstrainedArea(FGEArea locationConstrainedArea);

	public abstract boolean isFullyContainedInContainer();

	@Override
	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	public abstract boolean isParentLayoutedAsContainer();

	public abstract double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation);

	@Override
	public abstract void setText(String text);

	@Override
	public abstract void setTextStyle(TextStyle textStyle);

	public abstract boolean getAdjustMinimalWidthToLabelWidth();

	public abstract void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth);

	public abstract boolean getAdjustMinimalHeightToLabelHeight();

	public abstract void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight);

	public abstract boolean getAdjustMaximalWidthToLabelWidth();

	public abstract void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth);

	public abstract boolean getAdjustMaximalHeightToLabelHeight();

	public abstract void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight);

	public abstract double getWidth();

	public abstract void setWidth(double aValue);

	public abstract void setWidthNoNotification(double aValue);

	public abstract double getHeight();

	public abstract void setHeight(double aValue);

	public abstract void setHeightNoNotification(double aValue);

	public abstract FGEDimension getSize();

	public abstract void setSize(FGEDimension newSize);

	public abstract double getMinimalWidth();

	public abstract void setMinimalWidth(double minimalWidth);

	public abstract double getMinimalHeight();

	public abstract void setMinimalHeight(double minimalHeight);

	public abstract double getMaximalHeight();

	public abstract void setMaximalHeight(double maximalHeight);

	public abstract double getMaximalWidth();

	public abstract void setMaximalWidth(double maximalWidth);

	public abstract boolean getAllowToLeaveBounds();

	public abstract void setAllowToLeaveBounds(boolean allowToLeaveBounds);

	public abstract DimensionConstraints getDimensionConstraints();

	public abstract void setDimensionConstraints(DimensionConstraints dimensionConstraints);

	public abstract FGESteppedDimensionConstraint getDimensionConstraintStep();

	public abstract void setDimensionConstraintStep(FGESteppedDimensionConstraint dimensionConstraintStep);

	/**
	 * Return required width of shape, giving computed width of current label (useful for auto-layout, when
	 * 
	 * <pre>
	 * adjustMinimalWidthToLabelWidth
	 * </pre>
	 * 
	 * is set to true). Override this method to implement custom layout
	 * 
	 * @param labelWidth
	 * @return
	 */
	public abstract double getRequiredWidth(double labelWidth);

	/**
	 * Return required height of shape, giving computed height of current label (usefull for auto-layout, when
	 * 
	 * <pre>
	 * adjustMinimalHeightToLabelHeight
	 * </pre>
	 * 
	 * is set to true). Override this method to implement custom layout
	 * 
	 * @param labelHeight
	 * @return
	 */
	public abstract double getRequiredHeight(double labelHeight);

	public abstract DataBinding getXConstraints();

	public abstract void setXConstraints(DataBinding xConstraints);

	public abstract DataBinding getYConstraints();

	public abstract void setYConstraints(DataBinding yConstraints);

	public abstract DataBinding getWidthConstraints();

	public abstract void setWidthConstraints(DataBinding widthConstraints);

	public abstract DataBinding getHeightConstraints();

	public abstract void setHeightConstraints(DataBinding heightConstraints);

	public abstract void finalizeConstraints();

	public abstract void updateConstraints();

	public abstract void constraintChanged(DataBinding constraint);

	public abstract ForegroundStyle getForeground();

	public abstract void setForeground(ForegroundStyle aForeground);

	public abstract ForegroundStyle getSelectedForeground();

	public abstract void setSelectedForeground(ForegroundStyle aForeground);

	public abstract boolean getHasSelectedForeground();

	public abstract void setHasSelectedForeground(boolean aFlag);

	public abstract ForegroundStyle getFocusedForeground();

	public abstract void setFocusedForeground(ForegroundStyle aForeground);

	public abstract boolean getHasFocusedForeground();

	public abstract void setHasFocusedForeground(boolean aFlag);

	public abstract boolean getNoStroke();

	public abstract void setNoStroke(boolean noStroke);

	public abstract BackgroundStyle getBackground();

	public abstract void setBackground(BackgroundStyle aBackground);

	public abstract BackgroundStyleType getBackgroundType();

	public abstract void setBackgroundType(BackgroundStyleType backgroundType);

	public abstract BackgroundStyle getSelectedBackground();

	public abstract void setSelectedBackground(BackgroundStyle aBackground);

	public abstract boolean getHasSelectedBackground();

	public abstract void setHasSelectedBackground(boolean aFlag);

	public abstract BackgroundStyle getFocusedBackground();

	public abstract void setFocusedBackground(BackgroundStyle aBackground);

	public abstract boolean getHasFocusedBackground();

	public abstract void setHasFocusedBackground(boolean aFlag);

	public abstract ShapeBorder getBorder();

	public abstract void setBorder(ShapeBorder border);

	public abstract ShadowStyle getShadowStyle();

	public abstract void setShadowStyle(ShadowStyle aShadowStyle);

	public abstract Shape getShape();

	public abstract void setShape(Shape aShape);

	public abstract ShapeType getShapeType();

	public abstract void setShapeType(ShapeType shapeType);

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

	public abstract boolean getIsFloatingLabel();

	public abstract void setIsFloatingLabel(boolean isFloatingLabel);

	@Override
	public abstract boolean hasFloatingLabel();

	public abstract double getRelativeTextX();

	public abstract void setRelativeTextX(double textX);

	public abstract double getRelativeTextY();

	public abstract void setRelativeTextY(double textY);

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
