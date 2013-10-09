/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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
@XMLElement(xmlTag = "ShapeGraphicalRepresentation")
public interface ShapeGraphicalRepresentation extends ContainerGraphicalRepresentation {

	// Property keys

	@PropertyIdentifier(type = Double.class)
	public static final String X_KEY = "x";
	@PropertyIdentifier(type = Double.class)
	public static final String Y_KEY = "y";
	@PropertyIdentifier(type = LocationConstraints.class)
	public static final String LOCATION_CONSTRAINTS_KEY = "locationConstraints";
	@PropertyIdentifier(type = FGEArea.class)
	public static final String LOCATION_CONSTRAINED_AREA_KEY = "locationConstrainedArea";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String BACKGROUND_KEY = "background";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String SELECTED_FOREGROUND_KEY = "selectedForeground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String SELECTED_BACKGROUND_KEY = "selectedBackground";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOCUSED_FOREGROUND_KEY = "focusedForeground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String FOCUSED_BACKGROUND_KEY = "focusedBackground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_SELECTED_FOREGROUND_KEY = "hasSelectedForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_SELECTED_BACKGROUND_KEY = "hasSelectedBackground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_FOCUSED_FOREGROUND_KEY = "hasFocusedForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_FOCUSED_BACKGROUND_KEY = "hasFocusedBackground";
	@PropertyIdentifier(type = ShapeBorder.class)
	public static final String BORDER_KEY = "border";
	@PropertyIdentifier(type = ShapeType.class)
	public static final String SHAPE_TYPE_KEY = "shapeType";
	@PropertyIdentifier(type = ShapeSpecification.class)
	public static final String SHAPE_SPECIFICATION_KEY = "shapeSpecification";
	@PropertyIdentifier(type = ShadowStyle.class)
	public static final String SHADOW_STYLE_KEY = "shadowStyle";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FLOATING_LABEL_KEY = "isFloatingLabel";
	@PropertyIdentifier(type = Double.class)
	public static final String RELATIVE_TEXT_X_KEY = "relativeTextX";
	@PropertyIdentifier(type = Double.class)
	public static final String RELATIVE_TEXT_Y_KEY = "relativeTextY";
	// @PropertyIdentifier(type = Double.class)
	// public static final String DECORATION_PAINTER = "decorationPainter";
	// public static final String SHAPE_PAINTER = "shapePainter";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ALLOW_TO_LEAVE_BOUNDS_KEY = "allowToLeaveBounds";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADAPT_BOUNDS_TO_CONTENTS_KEY = "adaptBoundsToContents";
	@Deprecated
	@PropertyIdentifier(type = DataBinding.class)
	public static final String X_CONSTRAINTS_KEY = "xConstraints";
	@Deprecated
	@PropertyIdentifier(type = DataBinding.class)
	public static final String Y_CONSTRAINTS_KEY = "yConstraints";
	@Deprecated
	@PropertyIdentifier(type = DataBinding.class)
	public static final String WIDTH_CONSTRAINTS_KEY = "widthConstraints";
	@Deprecated
	@PropertyIdentifier(type = DataBinding.class)
	public static final String HEIGHT_CONSTRAINTS_KEY = "heightConstraints";

	// *******************************************************************************
	// * Inner concepts
	// *******************************************************************************

	/*public static enum ShapeParameters implements GRParameter {
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
	}*/

	public static enum DimensionConstraints {
		FREELY_RESIZABLE, UNRESIZABLE, CONSTRAINED_DIMENSIONS, STEP_CONSTRAINED, WIDTH_FIXED, HEIGHT_FIXED, CONTAINER
	}

	public static enum LocationConstraints {
		/**
		 * ShapeSpecification is freely relocatable in parent global bounds (rectangular bounds, don't care about borders nor shape of
		 * parent)
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

	public static GRParameter<Double> X = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, X_KEY, Double.class);
	public static GRParameter<Double> Y = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, Y_KEY, Double.class);
	public static GRParameter<BackgroundStyle> BACKGROUND = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, BACKGROUND_KEY,
			BackgroundStyle.class);
	public static GRParameter<ForegroundStyle> FOREGROUND = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, FOREGROUND_KEY,
			ForegroundStyle.class);
	public static GRParameter<ForegroundStyle> FOCUSED_FOREGROUND = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			FOCUSED_FOREGROUND_KEY, ForegroundStyle.class);
	public static GRParameter<BackgroundStyle> FOCUSED_BACKGROUND = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			FOCUSED_BACKGROUND_KEY, BackgroundStyle.class);
	public static GRParameter<ForegroundStyle> SELECTED_FOREGROUND = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			SELECTED_FOREGROUND_KEY, ForegroundStyle.class);
	public static GRParameter<BackgroundStyle> SELECTED_BACKGROUND = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			SELECTED_BACKGROUND_KEY, BackgroundStyle.class);
	public static GRParameter<ShadowStyle> SHADOW_STYLE = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, SHADOW_STYLE_KEY,
			ShadowStyle.class);
	public static GRParameter<LocationConstraints> LOCATION_CONSTRAINTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			LOCATION_CONSTRAINTS_KEY, LocationConstraints.class);
	public static GRParameter<FGEArea> LOCATION_CONSTRAINED_AREA = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			LOCATION_CONSTRAINED_AREA_KEY, FGEArea.class);
	public static GRParameter<Boolean> IS_FLOATING_LABEL = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			IS_FLOATING_LABEL_KEY, Boolean.class);
	public static GRParameter<Boolean> ADAPT_BOUNDS_TO_CONTENTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			ADAPT_BOUNDS_TO_CONTENTS_KEY, Boolean.class);
	public static GRParameter<Boolean> ALLOW_TO_LEAVE_BOUNDS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			ALLOW_TO_LEAVE_BOUNDS_KEY, Boolean.class);
	public static GRParameter<Double> RELATIVE_TEXT_X = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, RELATIVE_TEXT_X_KEY,
			Double.class);
	public static GRParameter<Double> RELATIVE_TEXT_Y = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, RELATIVE_TEXT_Y_KEY,
			Double.class);
	public static GRParameter<ShapeBorder> BORDER = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, BORDER_KEY,
			ShapeBorder.class);
	public static GRParameter<ShapeSpecification> SHAPE = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			SHAPE_SPECIFICATION_KEY, ShapeSpecification.class);
	public static GRParameter<ShapeType> SHAPE_TYPE = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, SHAPE_TYPE_KEY,
			ShapeType.class);
	public static GRParameter<DataBinding> X_CONSTRAINTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			X_CONSTRAINTS_KEY, DataBinding.class);
	public static GRParameter<DataBinding> Y_CONSTRAINTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			Y_CONSTRAINTS_KEY, DataBinding.class);
	public static GRParameter<DataBinding> WIDTH_CONSTRAINTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			WIDTH_CONSTRAINTS_KEY, DataBinding.class);
	public static GRParameter<DataBinding> HEIGHT_CONSTRAINTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			HEIGHT_CONSTRAINTS_KEY, DataBinding.class);

	// *******************************************************************************
	// * Model
	// *******************************************************************************

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = X_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getX();

	@Setter(value = X_KEY)
	public void setX(double aValue);

	@Getter(value = Y_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getY();

	@Setter(value = Y_KEY)
	public void setY(double aValue);

	/*@Override
	@Getter(value = WIDTH_KEY, defaultValue = "60.0")
	@XMLAttribute
	public double getWidth();

	@Override
	@Getter(value = HEIGHT_KEY, defaultValue = "20.0")
	@XMLAttribute
	public double getHeight();
	*/
	@Getter(value = LOCATION_CONSTRAINTS_KEY)
	@XMLAttribute
	public LocationConstraints getLocationConstraints();

	@Setter(value = LOCATION_CONSTRAINTS_KEY)
	public void setLocationConstraints(LocationConstraints locationConstraints);

	@Getter(value = LOCATION_CONSTRAINED_AREA_KEY, ignoreType = true)
	public FGEArea getLocationConstrainedArea();

	@Setter(value = LOCATION_CONSTRAINED_AREA_KEY)
	public void setLocationConstrainedArea(FGEArea locationConstrainedArea);

	@Getter(value = FOREGROUND_KEY)
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = SELECTED_FOREGROUND_KEY)
	@XMLElement(context = "Selected")
	public ForegroundStyle getSelectedForeground();

	@Setter(value = SELECTED_FOREGROUND_KEY)
	public void setSelectedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_SELECTED_FOREGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedForeground();

	@Setter(value = HAS_SELECTED_FOREGROUND_KEY)
	public void setHasSelectedForeground(boolean aFlag);

	@Getter(value = FOCUSED_FOREGROUND_KEY)
	@XMLElement(context = "Focused")
	public ForegroundStyle getFocusedForeground();

	@Setter(value = FOCUSED_FOREGROUND_KEY)
	public void setFocusedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_FOCUSED_FOREGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedForeground();

	@Setter(value = HAS_FOCUSED_FOREGROUND_KEY)
	public void setHasFocusedForeground(boolean aFlag);

	@Getter(value = BACKGROUND_KEY)
	@XMLElement
	public BackgroundStyle getBackground();

	@Setter(value = BACKGROUND_KEY)
	public void setBackground(BackgroundStyle aBackground);

	@Getter(value = SELECTED_BACKGROUND_KEY)
	@XMLElement(context = "Selected")
	public BackgroundStyle getSelectedBackground();

	@Setter(value = SELECTED_BACKGROUND_KEY)
	public void setSelectedBackground(BackgroundStyle aBackground);

	@Getter(value = HAS_SELECTED_BACKGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedBackground();

	@Setter(value = HAS_SELECTED_BACKGROUND_KEY)
	public void setHasSelectedBackground(boolean aFlag);

	@Getter(value = FOCUSED_BACKGROUND_KEY)
	@XMLElement(context = "Focused")
	public BackgroundStyle getFocusedBackground();

	@Setter(value = FOCUSED_BACKGROUND_KEY)
	public void setFocusedBackground(BackgroundStyle aBackground);

	@Getter(value = HAS_FOCUSED_BACKGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedBackground();

	@Setter(value = HAS_FOCUSED_BACKGROUND_KEY)
	public void setHasFocusedBackground(boolean aFlag);

	@Getter(value = BORDER_KEY)
	@XMLElement
	public ShapeBorder getBorder();

	@Setter(value = BORDER_KEY)
	public void setBorder(ShapeBorder border);

	@Getter(value = SHAPE_SPECIFICATION_KEY)
	@XMLElement
	public ShapeSpecification getShapeSpecification();

	@Setter(value = SHAPE_SPECIFICATION_KEY)
	public void setShapeSpecification(ShapeSpecification aShape);

	@Getter(value = SHADOW_STYLE_KEY)
	@XMLElement
	public ShadowStyle getShadowStyle();

	@Setter(value = SHADOW_STYLE_KEY)
	public void setShadowStyle(ShadowStyle aShadowStyle);

	@Getter(value = IS_FLOATING_LABEL_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFloatingLabel();

	@Setter(value = IS_FLOATING_LABEL_KEY)
	public void setIsFloatingLabel(boolean isFloatingLabel);

	@Getter(value = RELATIVE_TEXT_X_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getRelativeTextX();

	@Setter(value = RELATIVE_TEXT_X_KEY)
	public void setRelativeTextX(double textX);

	@Getter(value = RELATIVE_TEXT_Y_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getRelativeTextY();

	@Setter(value = RELATIVE_TEXT_Y_KEY)
	public void setRelativeTextY(double textY);

	@Getter(value = ALLOW_TO_LEAVE_BOUNDS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAllowToLeaveBounds();

	@Setter(value = ALLOW_TO_LEAVE_BOUNDS_KEY)
	public void setAllowToLeaveBounds(boolean allowToLeaveBounds);

	@Getter(value = ADAPT_BOUNDS_TO_CONTENTS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAdaptBoundsToContents();

	@Setter(value = ADAPT_BOUNDS_TO_CONTENTS_KEY)
	public void setAdaptBoundsToContents(boolean adaptBoundsToContents);

	@Deprecated
	@Getter(value = X_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getXConstraints();

	@Deprecated
	@Setter(value = X_CONSTRAINTS_KEY)
	public void setXConstraints(DataBinding<Double> xConstraints);

	@Deprecated
	@Getter(value = Y_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getYConstraints();

	@Deprecated
	@Setter(value = Y_CONSTRAINTS_KEY)
	public void setYConstraints(DataBinding<Double> yConstraints);

	@Deprecated
	@Getter(value = WIDTH_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getWidthConstraints();

	@Deprecated
	@Setter(value = WIDTH_CONSTRAINTS_KEY)
	public void setWidthConstraints(DataBinding<Double> widthConstraints);

	@Deprecated
	@Getter(value = HEIGHT_CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public DataBinding<Double> getHeightConstraints();

	@Deprecated
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

	/*public ShapeDecorationPainter getDecorationPainter();

	public void setDecorationPainter(ShapeDecorationPainter aPainter);

	public ShapePainter getShapePainter();

	public void setShapePainter(ShapePainter aPainter);*/

	// Override for a custom view management
	// public ShapeView makeShapeView(DianaEditor controller);

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
	public boolean isAllowedToBeDraggedOutsideParentContainerInsideContainer(ContainerNode<?, ?> container);

	/**
	 * Override this if you want to use this feature Default implementation does nothing return boolean indicating if drag was successfully
	 * performed
	 */
	public boolean dragOutsideParentContainerInsideContainer(ContainerNode<?, ?> container, FGEPoint location);

	// public void performRandomLayout();

	// public void performAutoLayout();

}
