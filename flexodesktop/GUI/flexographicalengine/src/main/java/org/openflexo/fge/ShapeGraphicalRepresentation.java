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

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl.ShapeBorderImpl;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a shape in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ShapeGraphicalRepresentationImpl.class)
@XMLElement(xmlTag = "ShapeGraphicalRepresentation")
public interface ShapeGraphicalRepresentation extends ContainerGraphicalRepresentation {

	// Property keys

	public static final String X = "x";
	public static final String Y = "y";
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
	public static final String ADAPT_BOUNDS_TO_CONTENTS = "adaptBoundsToContents";
	public static final String X_CONSTRAINTS_KEY = "xConstraints";
	public static final String Y_CONSTRAINTS_KEY = "yConstraints";
	public static final String WIDTH_CONSTRAINTS_KEY = "widthConstraints";
	public static final String HEIGHT_CONSTRAINTS_KEY = "heightConstraints";

	// *******************************************************************************
	// * Inner concepts
	// *******************************************************************************

	public static enum ShapeParameters implements GRParameter {
		x, y,
		// width,
		// height,
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
		adaptBoundsToContents,
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
		 * ShapeSpecification is freely relocatable in parent global bounds (rectangular bounds, don't care about borders nor shape of parent)
		 */
		FREELY_MOVABLE,
		/**
		 * ShapeSpecification is freely relocatable in parent exact bounds (shape of this GR must be fully located INSIDE parent GR outline)
		 */
		CONTAINED_IN_SHAPE,
		/**
		 * ShapeSpecification is not movable
		 */
		UNMOVABLE, RELATIVE_TO_PARENT, X_FIXED, Y_FIXED, AREA_CONSTRAINED;
	}

	@ModelEntity
	@ImplementationClass(ShapeBorderImpl.class)
	@XMLElement(xmlTag = "ShapeBorder")
	public static interface ShapeBorder extends FGEObject {

		public static final String TOP = "top";
		public static final String BOTTOM = "bottom";
		public static final String LEFT = "left";
		public static final String RIGHT = "right";

		@Getter(value = TOP, defaultValue = "20")
		@XMLAttribute
		public int getTop();

		@Setter(value = TOP)
		public void setTop(int top);

		@Getter(value = BOTTOM, defaultValue = "20")
		@XMLAttribute
		public int getBottom();

		@Setter(value = BOTTOM)
		public void setBottom(int bottom);

		@Getter(value = LEFT, defaultValue = "20")
		@XMLAttribute
		public int getLeft();

		@Setter(value = LEFT)
		public void setLeft(int left);

		@Getter(value = RIGHT, defaultValue = "20")
		@XMLAttribute
		public int getRight();

		@Setter(value = RIGHT)
		public void setRight(int right);

		public ShapeBorder clone();

	}

	// *******************************************************************************
	// * Model
	// *******************************************************************************

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = X, defaultValue = "0.0")
	@XMLAttribute
	public double getX();

	@Setter(value = X)
	public void setX(double aValue);

	@Getter(value = Y, defaultValue = "0.0")
	@XMLAttribute
	public double getY();

	@Setter(value = Y)
	public void setY(double aValue);

	@Override
	@Getter(value = WIDTH, defaultValue = "60.0")
	@XMLAttribute
	public double getWidth();

	@Override
	@Getter(value = HEIGHT, defaultValue = "20.0")
	@XMLAttribute
	public double getHeight();

	@Getter(value = MINIMAL_WIDTH, defaultValue = "0.0")
	@XMLAttribute
	public double getMinimalWidth();

	@Setter(value = MINIMAL_WIDTH)
	public void setMinimalWidth(double minimalWidth);

	@Getter(value = MINIMAL_HEIGHT, defaultValue = "0.0")
	@XMLAttribute
	public double getMinimalHeight();

	@Setter(value = MINIMAL_HEIGHT)
	public void setMinimalHeight(double minimalHeight);

	@Getter(value = MAXIMAL_HEIGHT, defaultValue = "POSITIVE_INFINITY")
	@XMLAttribute
	public double getMaximalHeight();

	@Setter(value = MAXIMAL_HEIGHT)
	public void setMaximalHeight(double maximalHeight);

	@Getter(value = MAXIMAL_WIDTH, defaultValue = "POSITIVE_INFINITY")
	@XMLAttribute
	public double getMaximalWidth();

	@Setter(value = MAXIMAL_WIDTH)
	public void setMaximalWidth(double maximalWidth);

	@Getter(value = DIMENSION_CONSTRAINT_STEP, isStringConvertable = true)
	@XMLAttribute
	public FGESteppedDimensionConstraint getDimensionConstraintStep();

	@Setter(value = DIMENSION_CONSTRAINT_STEP)
	public void setDimensionConstraintStep(FGESteppedDimensionConstraint dimensionConstraintStep);

	@Getter(value = LOCATION_CONSTRAINTS)
	@XMLAttribute
	public LocationConstraints getLocationConstraints();

	@Setter(value = LOCATION_CONSTRAINTS)
	public void setLocationConstraints(LocationConstraints locationConstraints);

	@Getter(value = LOCATION_CONSTRAINED_AREA, ignoreType = true)
	public FGEArea getLocationConstrainedArea();

	@Setter(value = LOCATION_CONSTRAINED_AREA)
	public void setLocationConstrainedArea(FGEArea locationConstrainedArea);

	@Getter(value = DIMENSION_CONSTRAINTS)
	@XMLAttribute
	public DimensionConstraints getDimensionConstraints();

	@Setter(value = DIMENSION_CONSTRAINTS)
	public void setDimensionConstraints(DimensionConstraints dimensionConstraints);

	@Getter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH, defaultValue = "true")
	@XMLAttribute
	public boolean getAdjustMinimalWidthToLabelWidth();

	@Setter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH)
	public void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth);

	@Getter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT, defaultValue = "true")
	@XMLAttribute
	public boolean getAdjustMinimalHeightToLabelHeight();

	@Setter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT)
	public void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight);

	@Getter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH, defaultValue = "false")
	@XMLAttribute
	public boolean getAdjustMaximalWidthToLabelWidth();

	@Setter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH)
	public void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth);

	@Getter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT, defaultValue = "false")
	@XMLAttribute
	public boolean getAdjustMaximalHeightToLabelHeight();

	@Setter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT)
	public void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight);

	@Getter(value = FOREGROUND)
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = SELECTED_FOREGROUND)
	@XMLElement(context = "Selected")
	public ForegroundStyle getSelectedForeground();

	@Setter(value = SELECTED_FOREGROUND)
	public void setSelectedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_SELECTED_FOREGROUND, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedForeground();

	@Setter(value = HAS_SELECTED_FOREGROUND)
	public void setHasSelectedForeground(boolean aFlag);

	@Getter(value = FOCUSED_FOREGROUND)
	@XMLElement(context = "Focused")
	public ForegroundStyle getFocusedForeground();

	@Setter(value = FOCUSED_FOREGROUND)
	public void setFocusedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_FOCUSED_FOREGROUND, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedForeground();

	@Setter(value = HAS_FOCUSED_FOREGROUND)
	public void setHasFocusedForeground(boolean aFlag);

	@Getter(value = BACKGROUND)
	@XMLElement
	public BackgroundStyle getBackground();

	@Setter(value = BACKGROUND)
	public void setBackground(BackgroundStyle aBackground);

	@Getter(value = SELECTED_BACKGROUND)
	@XMLElement(context = "Selected")
	public BackgroundStyle getSelectedBackground();

	@Setter(value = SELECTED_BACKGROUND)
	public void setSelectedBackground(BackgroundStyle aBackground);

	@Getter(value = HAS_SELECTED_BACKGROUND, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedBackground();

	@Setter(value = HAS_SELECTED_BACKGROUND)
	public void setHasSelectedBackground(boolean aFlag);

	@Getter(value = FOCUSED_BACKGROUND)
	@XMLElement(context = "Focused")
	public BackgroundStyle getFocusedBackground();

	@Setter(value = FOCUSED_BACKGROUND)
	public void setFocusedBackground(BackgroundStyle aBackground);

	@Getter(value = HAS_FOCUSED_BACKGROUND, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedBackground();

	@Setter(value = HAS_FOCUSED_BACKGROUND)
	public void setHasFocusedBackground(boolean aFlag);

	@Getter(value = BORDER)
	@XMLElement
	public ShapeBorder getBorder();

	@Setter(value = BORDER)
	public void setBorder(ShapeBorder border);

	@Getter(value = SHAPE)
	@XMLElement
	public ShapeSpecification getShape();

	@Setter(value = SHAPE)
	public void setShape(ShapeSpecification aShape);

	@Getter(value = SHADOW_STYLE)
	@XMLElement
	public ShadowStyle getShadowStyle();

	@Setter(value = SHADOW_STYLE)
	public void setShadowStyle(ShadowStyle aShadowStyle);

	@Getter(value = IS_FLOATING_LABEL, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFloatingLabel();

	@Setter(value = IS_FLOATING_LABEL)
	public void setIsFloatingLabel(boolean isFloatingLabel);

	@Getter(value = RELATIVE_TEXT_X, defaultValue = "0.0")
	@XMLAttribute
	public double getRelativeTextX();

	@Setter(value = RELATIVE_TEXT_X)
	public void setRelativeTextX(double textX);

	@Getter(value = RELATIVE_TEXT_Y, defaultValue = "0.0")
	@XMLAttribute
	public double getRelativeTextY();

	@Setter(value = RELATIVE_TEXT_Y)
	public void setRelativeTextY(double textY);

	@Getter(value = ALLOW_TO_LEAVE_BOUNDS, defaultValue = "true")
	@XMLAttribute
	public boolean getAllowToLeaveBounds();

	@Setter(value = ALLOW_TO_LEAVE_BOUNDS)
	public void setAllowToLeaveBounds(boolean allowToLeaveBounds);

	@Getter(value = ADAPT_BOUNDS_TO_CONTENTS, defaultValue = "true")
	@XMLAttribute
	public boolean getAdaptBoundsToContents();

	@Setter(value = ADAPT_BOUNDS_TO_CONTENTS)
	public void setAdaptBoundsToContents(boolean adaptBoundsToContents);

	@Getter(value = X_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getXConstraints();

	@Setter(value = X_CONSTRAINTS_KEY)
	public void setXConstraints(DataBinding<Double> xConstraints);

	@Getter(value = Y_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getYConstraints();

	@Setter(value = Y_CONSTRAINTS_KEY)
	public void setYConstraints(DataBinding<Double> yConstraints);

	@Getter(value = WIDTH_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getWidthConstraints();

	@Setter(value = WIDTH_CONSTRAINTS_KEY)
	public void setWidthConstraints(DataBinding<Double> widthConstraints);

	@Getter(value = HEIGHT_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getHeightConstraints();

	@Setter(value = HEIGHT_CONSTRAINTS_KEY)
	public void setHeightConstraints(DataBinding<Double> heightConstraints);

	// *******************************************************************************
	// * Position management
	// *******************************************************************************

	public FGEPoint getLocation();

	// public void setLocation(FGEPoint newLocation);

	// public FGEPoint getLocationInDrawing();

	// *******************************************************************************
	// * Size management
	// *******************************************************************************

	// public Dimension getNormalizedLabelSize();

	// public Rectangle getNormalizedLabelBounds();

	// public FGERectangle getRequiredBoundsForContents();

	// public boolean isFullyContainedInContainer();

	// public boolean isParentLayoutedAsContainer();

	// public double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation);

	@Override
	public FGEDimension getSize();

	// public void setSize(FGEDimension newSize);

	// *******************************************************************************
	// * Properties management
	// *******************************************************************************

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public ShapeType getShapeType();

	public void setShapeType(ShapeType shapeType);

	public ShapeGraphicalRepresentation clone();

	// public void extendParentBoundsToHostThisShape();

	// public void extendBoundsToHostContents();

	// public void finalizeConstraints();

	// public void updateConstraints();

	// See notifiedBindingChanged(DataBinding<?>)
	// public void constraintChanged(DataBinding<?> constraint);

	public boolean getNoStroke();

	public void setNoStroke(boolean noStroke);

	public BackgroundStyleType getBackgroundType();

	public void setBackgroundType(BackgroundStyleType backgroundType);

	public void notifyShapeChanged();

	public void notifyShapeNeedsToBeRedrawn();

	/*public void notifyObjectMoved();

	public void notifyObjectMoved(FGEPoint oldLocation);

	public void notifyObjectWillMove();

	public void notifyObjectHasMoved();

	public boolean isMoving();

	public void notifyObjectResized();

	public void notifyObjectResized(FGEDimension oldSize);

	public void notifyObjectWillResize();

	public void notifyObjectHasResized();

	public boolean isResizing();*/

	// public double getUnscaledViewWidth();

	// public double getUnscaledViewHeight();

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	// public FGERectangle getBounds();

	/**
	 * Return view bounds (excluding border) relative to parent container
	 * 
	 * @param scale
	 * @return
	 */
	// public Rectangle getBounds(double scale);

	/**
	 * Return view bounds (excluding border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	// public Rectangle getBounds(GraphicalRepresentation container, double scale);

	/**
	 * Return logical bounds (including border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	// public Rectangle getViewBounds(GraphicalRepresentation container, double scale);

	// public boolean isPointInsideShape(FGEPoint aPoint);

	/*public DecorationPainter getDecorationPainter();

	public void setDecorationPainter(DecorationPainter aPainter);

	public ShapePainter getShapePainter();

	public void setShapePainter(ShapePainter aPainter);*/

	// Override for a custom view management
	// public ShapeView makeShapeView(DrawingController controller);

	@Override
	public String toString();

	// public List<? extends ControlArea<?>> getControlAreas();

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * @return the area on which the given connector can start
	 */
	// public FGEArea getAllowedStartAreaForConnector(ConnectorGraphicalRepresentation connectorGR);

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	// public FGEArea getAllowedEndAreaForConnector(ConnectorGraphicalRepresentation connectorGR);

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * 
	 * @return the area on which the given connector can start
	 */
	// public FGEArea getAllowedStartAreaForConnectorForDirection(ConnectorGraphicalRepresentation connectorGR, FGEArea area,
	// SimplifiedCardinalDirection direction);

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	// public FGEArea getAllowedEndAreaForConnectorForDirection(ConnectorGraphicalRepresentation connectorGR, FGEArea area,
	// SimplifiedCardinalDirection direction);

	// public FGEShapeGraphics getGraphics();

	/**
	 * Override this if you want to use such a feature
	 * 
	 * @return
	 */
	public boolean isAllowedToBeDraggedOutsideParentContainer();

	/**
	 * Override this if you want to use this feature Default implementation return always false
	 * 
	 * @return
	 */
	public boolean isAllowedToBeDraggedOutsideParentContainerInsideContainer(GraphicalRepresentation container);

	/**
	 * Override this if you want to use this feature Default implementation does nothing return boolean indicating if drag was successfully
	 * performed
	 */
	public boolean dragOutsideParentContainerInsideContainer(GraphicalRepresentation container, FGEPoint location);

	// public void performRandomLayout();

	// public void performAutoLayout();

}
