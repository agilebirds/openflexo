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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.controller.MouseDragControlAction.MouseDragControlActionType;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.toolbox.ToolBox;

public class ShapeGraphicalRepresentationImpl<O> extends GraphicalRepresentationImpl<O> implements ShapeGraphicalRepresentation<O> {

	private static final Logger logger = Logger.getLogger(ShapeGraphicalRepresentationImpl.class.getPackage().getName());

	// *******************************************************************************
	// * ShapeParameters *
	// *******************************************************************************

	private double x = 0;
	private double y = 0;
	private double width = 1;
	private double height = 1;
	private double minimalWidth = 0;
	private double minimalHeight = 0;
	private double maximalWidth = Double.POSITIVE_INFINITY;
	private double maximalHeight = Double.POSITIVE_INFINITY;

	private DimensionConstraints dimensionConstraints = DimensionConstraints.FREELY_RESIZABLE;
	private FGESteppedDimensionConstraint dimensionConstraintStep = null;
	private boolean adjustMinimalWidthToLabelWidth = true;
	private boolean adjustMinimalHeightToLabelHeight = true;
	private boolean adjustMaximalWidthToLabelWidth = false;
	private boolean adjustMaximalHeightToLabelHeight = false;

	private LocationConstraints locationConstraints = LocationConstraints.FREELY_MOVABLE;

	private FGEArea locationConstrainedArea = null;

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private ForegroundStyle selectedForeground = null;
	private BackgroundStyle selectedBackground = null;
	private ForegroundStyle focusedForeground = null;
	private BackgroundStyle focusedBackground = null;

	private boolean hasSelectedForeground = false;
	private boolean hasSelectedBackground = false;
	private boolean hasFocusedForeground = false;
	private boolean hasFocusedBackground = false;

	private ShapeBorder border = new ShapeBorder();

	private Shape shape = null;

	private ShadowStyle shadowStyle = ShadowStyle.makeDefault();

	private boolean allowToLeaveBounds = true;

	/*private boolean drawShadow = true;
	private int shadowDarkness = FGEConstants.DEFAULT_SHADOW_DARKNESS;
	private int shadowDeep = FGEConstants.DEFAULT_SHADOW_DEEP;
	private int shadowBlur = FGEConstants.DEFAULT_SHADOW_BLUR;*/

	private boolean isFloatingLabel = true;
	private double relativeTextX = 0.5;
	private double relativeTextY = 0.5;

	private boolean isResizing = false;
	private boolean isMoving = false;

	// *******************************************************************************
	// * Geometry constraints *
	// *******************************************************************************

	protected FGEShapeGraphics graphics;
	protected FGEShapeDecorationGraphics decorationGraphics;
	protected DecorationPainter decorationPainter;
	protected ShapePainter shapePainter;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Never use this constructor (used during deserialization only)
	public ShapeGraphicalRepresentationImpl() {
		this(null, null);
	}

	private ShapeGraphicalRepresentationImpl(O aDrawable, Drawing<?> aDrawing) {
		super(aDrawable, aDrawing);
	}

	public ShapeGraphicalRepresentationImpl(ShapeType shapeType, O aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);
		setShapeType(shapeType);
		layer = FGEConstants.DEFAULT_SHAPE_LAYER;
		foreground = ForegroundStyle.makeDefault();
		// foreground.setGraphicalRepresentation(this);
		foreground.addObserver(this);
		background = BackgroundStyle.makeColoredBackground(Color.WHITE);
		// background.setGraphicalRepresentation(this);
		background.addObserver(this);

		graphics = new FGEShapeGraphics(this);

		addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}
		addToMouseDragControls(MouseDragControl.makeMouseDragControl("Move", MouseButton.LEFT, MouseDragControlActionType.MOVE));
		addToMouseDragControls(MouseDragControl.makeMouseDragControl("Zoom", MouseButton.RIGHT, MouseDragControlActionType.ZOOM));
		addToMouseDragControls(MouseDragControl.makeMouseShiftDragControl("Rectangle selection", MouseButton.LEFT,
				MouseDragControlActionType.RECTANGLE_SELECTING));
	}

	public ShapeGraphicalRepresentationImpl(ShapeGraphicalRepresentationImpl<?> aGR, O aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);

		setsWith(aGR);
		// setShape(aGR.getShape().clone());
		// getShape().setGraphicalRepresentation(this);
		// getShape().rebuildControlPoints();

		graphics = new FGEShapeGraphics(this);

		addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}
		addToMouseDragControls(MouseDragControl.makeMouseDragControl("Move", MouseButton.LEFT, MouseDragControlActionType.MOVE));
		addToMouseDragControls(MouseDragControl.makeMouseDragControl("Zoom", MouseButton.RIGHT, MouseDragControlActionType.ZOOM));
		addToMouseDragControls(MouseDragControl.makeMouseShiftDragControl("Rectangle selection", MouseButton.LEFT,
				MouseDragControlActionType.RECTANGLE_SELECTING));

	}

	@Override
	public ShapeGraphicalRepresentationImpl<O> clone() {
		// logger.info("La GR "+this+" se fait cloner la");
		try {
			return (ShapeGraphicalRepresentationImpl<O>) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = super.getAllParameters();
		ShapeParameters[] allParams = ShapeParameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}

	// ***************************************************************************
	// * Deletion *
	// ***************************************************************************

	@Override
	public void delete() {
		if (background != null) {
			background.deleteObserver(this);
		}
		if (foreground != null) {
			foreground.deleteObserver(this);
		}
		if (selectedBackground != null) {
			selectedBackground.deleteObserver(this);
		}
		if (selectedForeground != null) {
			selectedForeground.deleteObserver(this);
		}
		if (focusedBackground != null) {
			focusedBackground.deleteObserver(this);
		}
		if (focusedForeground != null) {
			focusedForeground.deleteObserver(this);
		}
		if (shadowStyle != null) {
			shadowStyle.deleteObserver(this);
		}
		super.delete();
	}

	// ***************************************************************************
	// * Cloning *
	// ***************************************************************************

	@Override
	public final void setsWith(GraphicalRepresentation<?> gr) {
		super.setsWith(gr);
		if (gr instanceof ShapeGraphicalRepresentationImpl) {
			for (ShapeParameters p : ShapeParameters.values()) {
				if (p != ShapeParameters.shape && p != ShapeParameters.shapeType) {
					_setParameterValueWith(p, gr);
				}
			}
			Shape shapeToCopy = ((ShapeGraphicalRepresentationImpl<?>) gr).getShape();
			Shape clone = shapeToCopy.clone();
			setShape(clone);
		}
	}

	@Override
	public final void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedShapeParameters) {
		super.setsWith(gr, exceptedShapeParameters);
		if (gr instanceof ShapeGraphicalRepresentationImpl) {
			for (ShapeParameters p : ShapeParameters.values()) {
				boolean excepted = false;
				for (GRParameter ep : exceptedShapeParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (p != ShapeParameters.shape && p != ShapeParameters.shapeType && !excepted) {
					_setParameterValueWith(p, gr);
				}
			}
			Shape shapeToCopy = ((ShapeGraphicalRepresentationImpl<?>) gr).getShape();
			Shape clone = shapeToCopy.clone();
			setShape(clone);
		}
	}

	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	// This might be very dangerous to override this (this has been done in the past)
	// SGU: Developer really need to think about what he's doing while overriding this
	@Override
	public void update(Observable observable, Object notification) {
		// System.out.println("Shape received "+notification+" from "+observable);

		super.update(observable, notification);

		if (observeParentGRBecauseMyLocationReferToIt && observable == getContainerGraphicalRepresentation()) {
			if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize
					|| notification instanceof ObjectHasMoved || notification instanceof ObjectHasResized
					|| notification instanceof ObjectMove || notification instanceof ObjectResized || notification instanceof ShapeChanged) {
				checkAndUpdateLocationIfRequired();
			}
		}

		if (observable instanceof BackgroundStyle) {
			notifyAttributeChange(ShapeParameters.background);
		}
		if (observable instanceof ForegroundStyle) {
			notifyAttributeChange(ShapeParameters.foreground);
		}
		if (observable instanceof ShadowStyle) {
			notifyAttributeChange(ShapeParameters.shadowStyle);
		}
	}

	// *******************************************************************************
	// * Location management *
	// *******************************************************************************

	@Override
	public void extendParentBoundsToHostThisShape() {
		// System.out.println("parent=" + getParentGraphicalRepresentation());
		if (getParentGraphicalRepresentation() instanceof ShapeGraphicalRepresentationImpl) {
			ShapeGraphicalRepresentationImpl<?> parent = (ShapeGraphicalRepresentationImpl) getParentGraphicalRepresentation();

			// parent.updateRequiredBoundsForChildGRLocation(this, new FGEPoint(x, y));

			boolean parentNeedsResize = false;
			FGEDimension newDimension = new FGEDimension(parent.getWidth(), parent.getHeight());
			boolean parentNeedsRelocate = false;
			FGEPoint newPosition = new FGEPoint(parent.getX(), parent.getY());
			double deltaX = 0;
			double deltaY = 0;
			if (x < 0) {
				newPosition.x = newPosition.x + x;
				parentNeedsRelocate = true;
				deltaX = -x;
			}
			if (y < 0) {
				newPosition.y = newPosition.y + y;
				parentNeedsRelocate = true;
				deltaY = -y;
			}
			if (parentNeedsRelocate) {
				parent.setLocation(newPosition);
				setLocation(new FGEPoint(getX() - deltaX, getY() - deltaY));
				parentNeedsResize = true;
				newDimension = new FGEDimension(parent.getWidth() + deltaX, parent.getHeight() + deltaY);
			}

			if (x + getWidth() > parent.getWidth()) {
				newDimension.width = x + getWidth();
				parentNeedsResize = true;
			}
			if (y + getHeight() > parent.getHeight()) {
				newDimension.height = y + getHeight();
				parentNeedsResize = true;
			}
			if (parentNeedsResize) {
				parent.setSize(newDimension);
			}

			if (parentNeedsRelocate) {
				for (GraphicalRepresentation<?> child : parent.getContainedGraphicalRepresentations()) {
					if (child instanceof ShapeGraphicalRepresentationImpl && child != this) {
						ShapeGraphicalRepresentationImpl<?> c = (ShapeGraphicalRepresentationImpl<?>) child;
						c.setLocation(new FGEPoint(c.getX() + deltaX, c.getY() + deltaY));
					}
				}
			}

			if (parentNeedsRelocate || parentNeedsResize) {
				FGEPoint oldLocation = new FGEPoint(x, y);
				notifyObjectMoved(oldLocation);
				notifyChange(ShapeParameters.x, oldLocation.x, getX());
				notifyChange(ShapeParameters.y, oldLocation.y, getY());
			}
		}
	}

	@Override
	public double getX() {
		// SGU: in general case, this is NOT forbidden
		if (!getAllowToLeaveBounds()) {
			if (x < 0) {
				return 0;
			}
			double maxX = 0;
			if (getContainerGraphicalRepresentation() instanceof DrawingGraphicalRepresentation) {
				maxX = ((DrawingGraphicalRepresentation<?>) getContainerGraphicalRepresentation()).getWidth();
			} else if (getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentationImpl) {
				maxX = ((ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation()).getWidth();
			}
			if (maxX > 0 && x > maxX - getWidth()) {
				// logger.info("Relocate x from "+x+" to "+(maxX-getWidth())+" maxX="+maxX+" width="+getWidth());
				return maxX - getWidth();
			}
		}
		return x;
	}

	@Override
	public final void setX(double aValue) {
		FGENotification notification = requireChange(ShapeParameters.x, aValue);
		if (notification != null) {
			FGEPoint oldLocation = getLocation();
			setXNoNotification(aValue);
			hasChanged(notification);
			notifyObjectMoved(oldLocation);
		}
	}

	@Override
	public void setXNoNotification(double aValue) {
		x = aValue;
	}

	@Override
	public double getY() {
		// SGU: in general case, this is NOT forbidden
		if (!getAllowToLeaveBounds()) {
			if (y < 0) {
				return 0;
			}
			double maxY = 0;
			if (getContainerGraphicalRepresentation() instanceof DrawingGraphicalRepresentation) {
				maxY = ((DrawingGraphicalRepresentation<?>) getContainerGraphicalRepresentation()).getHeight();
			} else if (getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentationImpl) {
				maxY = ((ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation()).getHeight();
			}
			if (maxY > 0 && y > maxY - getHeight()) {
				// logger.info("Relocate y from " + y + " to " + (maxY - getHeight()) + " maxY=" + maxY + " height=" + getHeight());
				return maxY - getHeight();
			}
		}
		return y;
	}

	@Override
	public final void setY(double aValue) {
		FGENotification notification = requireChange(ShapeParameters.y, aValue);
		if (notification != null) {
			FGEPoint oldLocation = getLocation();
			setYNoNotification(aValue);
			hasChanged(notification);
			notifyObjectMoved(oldLocation);
		}
	}

	@Override
	public void setYNoNotification(double aValue) {
		y = aValue;
	}

	@Override
	public FGEPoint getLocation() {
		return new FGEPoint(getX(), getY());
	}

	@Override
	public void setLocation(FGEPoint newLocation) {
		if (newLocation == null) {
			return;
		}
		newLocation = computeConstrainedLocation(newLocation);
		FGEPoint oldLocation = getLocation();
		if (!newLocation.equals(oldLocation)) {
			double oldX = getX();
			double oldY = getY();
			if (isParentLayoutedAsContainer()) {
				setLocationForContainerLayout(newLocation);
			} else {
				setXNoNotification(newLocation.x);
				setYNoNotification(newLocation.y);
			}
			notifyObjectMoved(oldLocation);
			notifyChange(ShapeParameters.x, oldX, getX());
			notifyChange(ShapeParameters.y, oldY, getY());
			if (!isFullyContainedInContainer()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("setLocation() lead shape going outside it's parent view");
				}
			}
		}
	}

	@Override
	public FGEPoint getLocationInDrawing() {
		return GraphicalRepresentation.convertNormalizedPoint(this, new FGEPoint(0, 0), getDrawingGraphicalRepresentation());
	}

	/*@Override
	public void setAbsoluteTextX(double absoluteTextX)
	{
		super.setAbsoluteTextX(absoluteTextX);
		if (isParentLayoutedAsContainer()) {
			ShapeGraphicalRepresentation<?> container = (ShapeGraphicalRepresentation<?>)getContainerGraphicalRepresentation();
			FGEPoint oldLocation = container.getLocation();
			FGERectangle oldBounds = container.getBounds();
			FGERectangle newBounds = container.getRequiredBoundsForChildGRLocation(this,getLocation());
			System.out.println("oldBounds= "+oldBounds+" newBounds="+newBounds);
			container.updateRequiredBoundsForChildGRLocation(this,getLocation());
			if (container.getLocation().x != oldLocation.x) {
				System.out.println("Trying "+absoluteTextX+" use finally "+(absoluteTextX-container.getLocation().x+oldLocation.x)+" but container moved from "+oldLocation.x+" to "+container.getLocation().x);
				super.setAbsoluteTextX(absoluteTextX-container.getLocation().x+oldLocation.x);
			}
		}
	}*/

	private void setLocationNoCheckNorNotification(FGEPoint newLocation) {
		setXNoNotification(newLocation.x);
		setYNoNotification(newLocation.y);
	}

	private void setLocationForContainerLayout(FGEPoint newLocation) {
		ShapeGraphicalRepresentationImpl<?> container = (ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation();
		container.updateRequiredBoundsForChildGRLocation(this, newLocation);
	}

	private void updateRequiredBoundsForChildGRLocation(ShapeGraphicalRepresentationImpl<?> child, FGEPoint newChildLocation) {
		FGERectangle oldBounds = getBounds();
		FGERectangle newBounds = getRequiredBoundsForChildGRLocation(child, newChildLocation);
		// System.out.println("oldBounds= "+oldBounds+" newBounds="+newBounds);
		double deltaX = -newBounds.x + oldBounds.x;
		double deltaY = -newBounds.y + oldBounds.y;
		AffineTransform translation = AffineTransform.getTranslateInstance(deltaX, deltaY);
		for (GraphicalRepresentation<?> gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentationImpl) {
				ShapeGraphicalRepresentationImpl<?> shapeGR = (ShapeGraphicalRepresentationImpl<?>) gr;
				if (shapeGR == child) {
					shapeGR.setLocationNoCheckNorNotification(newChildLocation.transform(translation));
				} else {
					// FGEPoint oldLocationInParent = new FGEPoint(oldBounds.x+shapeGR.getLocation().x,oldBounds.y+shapeGR.getLocation().y);
					// FGEPoint newLocationInParent = new
					// FGEPoint(newBounds.x+shapeGR.getLocation().transform(translation).x,newBounds.y+shapeGR.getLocation().transform(translation).y);
					// logger.info("Opposite was: "+shapeGR.getLocation()+" now "+shapeGR.getLocation().transform(translation)+" parent was: "+oldLocationInParent+" now "+newLocationInParent);
					shapeGR.setLocationNoCheckNorNotification(shapeGR.getLocation().transform(translation));
				}
				shapeGR.notifyObjectMoved();
			}
		}
		setLocation(new FGEPoint(newBounds.x, newBounds.y));
		setSize(new FGEDimension(newBounds.width, newBounds.height));
	}

	@Override
	public Dimension getNormalizedLabelSize() {
		if (labelMetricsProvider != null) {
			return labelMetricsProvider.getScaledPreferredDimension(1.0);
		} else {
			return new Dimension(0, 0);
		}
	}

	@Override
	public Rectangle getNormalizedLabelBounds() {
		Dimension normalizedLabelSize = getNormalizedLabelSize();
		Rectangle r = new Rectangle(getLabelLocation(1.0), normalizedLabelSize);
		return r;
	}

	private FGERectangle getRequiredBoundsForChildGRLocation(GraphicalRepresentation<?> child, FGEPoint newChildLocation) {
		FGERectangle requiredBounds = null;
		for (GraphicalRepresentation<?> gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentationImpl) {
				ShapeGraphicalRepresentationImpl<?> shapeGR = (ShapeGraphicalRepresentationImpl<?>) gr;
				FGERectangle bounds = shapeGR.getBounds();
				if (shapeGR == child) {
					bounds.x = newChildLocation.x;
					bounds.y = newChildLocation.y;
				}
				if (shapeGR.hasText()) {
					Rectangle labelBounds = shapeGR.getNormalizedLabelBounds(); // getLabelBounds((new JLabel()), 1.0);
					FGERectangle labelBounds2 = new FGERectangle(labelBounds.x, labelBounds.y, labelBounds.width, labelBounds.height);
					bounds = bounds.rectangleUnion(labelBounds2);
				}

				if (requiredBounds == null) {
					requiredBounds = bounds;
				} else {
					requiredBounds = requiredBounds.rectangleUnion(bounds);
				}
			}
		}
		if (requiredBounds == null) {
			requiredBounds = new FGERectangle(getX(), getY(), getMinimalWidth(), getMinimalHeight());
		} else {
			requiredBounds.x = requiredBounds.x + getX();
			requiredBounds.y = requiredBounds.y + getY();
			if (requiredBounds.width < getMinimalWidth()) {
				requiredBounds.x = requiredBounds.x - (int) ((getMinimalWidth() - requiredBounds.width) / 2.0);
				requiredBounds.width = getMinimalWidth();
			}
			if (requiredBounds.height < getMinimalHeight()) {
				requiredBounds.y = requiredBounds.y - (int) ((getMinimalHeight() - requiredBounds.height) / 2.0);
				requiredBounds.height = getMinimalHeight();
			}
		}
		return requiredBounds;
	}

	@Override
	public LocationConstraints getLocationConstraints() {
		return locationConstraints;
	}

	@Override
	public void setLocationConstraints(LocationConstraints locationConstraints) {
		FGENotification notification = requireChange(ShapeParameters.locationConstraints, locationConstraints);
		if (notification != null && getShape() != null) {
			this.locationConstraints = locationConstraints;
			if (isRegistered()) {
				if (APPLY_CONSTRAINTS_IMMEDIATELY) {
					checkAndUpdateLocationIfRequired();
				}
				getShape().rebuildControlPoints();
				hasChanged(notification);
			}
		}
	}

	@Override
	public FGEArea getLocationConstrainedArea() {
		return locationConstrainedArea;
	}

	@Override
	public void setLocationConstrainedArea(FGEArea locationConstrainedArea) {
		FGENotification notification = requireChange(ShapeParameters.locationConstrainedArea, locationConstrainedArea);
		if (notification != null) {
			this.locationConstrainedArea = locationConstrainedArea;
			if (isRegistered()) {
				if (APPLY_CONSTRAINTS_IMMEDIATELY) {
					checkAndUpdateLocationIfRequired();
				}
				getShape().rebuildControlPoints();
				hasChanged(notification);
			}
		}
	}

	private boolean observeParentGRBecauseMyLocationReferToIt = false;

	/**
	 * Calling this method forces FGE to check (and eventually update) location of current graphical representation according defined
	 * location constraints
	 */
	protected void checkAndUpdateLocationIfRequired() {
		try {
			setLocation(getLocation());
		} catch (IllegalArgumentException e) {
			// May happen if object hierarchy inconsistent (or not consistent yet)
			logger.fine("Ignore IllegalArgumentException: " + e.getMessage());
		}
		if (!observeParentGRBecauseMyLocationReferToIt) {
			if (locationConstraints == LocationConstraints.AREA_CONSTRAINED && getContainerGraphicalRepresentation() != null) {
				getContainerGraphicalRepresentation().addObserver(this);
				observeParentGRBecauseMyLocationReferToIt = true;
				// logger.info("Start observe my father");
			}
		}
	}

	protected FGEPoint computeConstrainedLocation(FGEPoint newLocation) {
		if (isParentLayoutedAsContainer()) {
			return newLocation;
		}
		if (getLocationConstraints() == LocationConstraints.FREELY_MOVABLE) {
			return newLocation.clone();
		}
		if (getLocationConstraints() == LocationConstraints.CONTAINED_IN_SHAPE) {
			GraphicalRepresentation<?> parent = getContainerGraphicalRepresentation();
			if (parent instanceof ShapeGraphicalRepresentationImpl) {
				ShapeGraphicalRepresentationImpl<?> container = (ShapeGraphicalRepresentationImpl<?>) parent;
				FGEPoint center = new FGEPoint(container.getWidth() / 2, container.getHeight() / 2);
				double authorizedRatio = getMoveAuthorizedRatio(newLocation, center);
				return new FGEPoint(center.x + (newLocation.x - center.x) * authorizedRatio, center.y + (newLocation.y - center.y)
						* authorizedRatio);
			}
		}
		if (getLocationConstraints() == LocationConstraints.AREA_CONSTRAINED) {
			if (getLocationConstrainedArea() == null) {
				// logger.warning("No location constrained are defined");
				return newLocation;
			} else {
				return getLocationConstrainedArea().getNearestPoint(newLocation);
			}
		}
		return newLocation;
	}

	@Override
	public boolean isFullyContainedInContainer() {
		if (getContainerGraphicalRepresentation() == null || getDrawing() == null) {
			return true;
		}
		boolean isFullyContained = true;
		FGERectangle containerViewBounds = new FGERectangle(0, 0, getContainerGraphicalRepresentation().getViewWidth(1),
				getContainerGraphicalRepresentation().getViewHeight(1), Filling.FILLED);
		for (ControlPoint cp : getShape().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(),
					getContainerGraphicalRepresentation(), 1);
			FGEPoint preciseCPInContainerView = new FGEPoint(cpInContainerView.x, cpInContainerView.y);
			if (!containerViewBounds.containsPoint(preciseCPInContainerView)) {
				// System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
				isFullyContained = false;
			}
		}
		return isFullyContained;
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		FGERectangle drawingViewBounds = new FGERectangle(drawingViewSelection.getX(), drawingViewSelection.getY(),
				drawingViewSelection.getWidth(), drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		for (ControlPoint cp : getShape().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(),
					getDrawingGraphicalRepresentation(), scale);
			FGEPoint preciseCPInContainerView = new FGEPoint(cpInContainerView.x, cpInContainerView.y);
			if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
				// System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
				isFullyContained = false;
			}
		}
		return isFullyContained;
	}

	@Override
	public boolean isParentLayoutedAsContainer() {
		return getContainerGraphicalRepresentation() != null
				&& getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentationImpl
				&& ((ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation()).getDimensionConstraints() == DimensionConstraints.CONTAINER;
	}

	@Override
	public double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation) {
		if (isParentLayoutedAsContainer()) {
			// This object is contained in a Shape acting as container: all locations are valid thus,
			// container will adapt
			return 1;
		}

		double returnedAuthorizedRatio = 1;
		FGERectangle containerViewArea = new FGERectangle(0, 0, getContainerGraphicalRepresentation().getViewWidth(1),
				getContainerGraphicalRepresentation().getViewHeight(1), Filling.FILLED);
		FGERectangle containerViewBounds = new FGERectangle(0, 0, getContainerGraphicalRepresentation().getViewWidth(1),
				getContainerGraphicalRepresentation().getViewHeight(1), Filling.NOT_FILLED);

		/*boolean wasInside = true;
		FGERectangle containerViewBounds = new FGERectangle(0,0,
				getContainerGraphicalRepresentation().getViewWidth(1),
				getContainerGraphicalRepresentation().getViewHeight(1));
		for (ControlPoint cp : getShape().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(
					cp.getPoint(),
					getContainerGraphicalRepresentation(),
					1);
			if (!containerViewBounds.contains(cpInContainerView)) wasInside = false;
		}
		if (!wasInside) {
			logger.warning("getMoveAuthorizedRatio() called for a shape whose initial location wasn't in container shape");
			return 1;
		}*/
		for (ControlPoint cp : getShape().getControlPoints()) {
			Point currentCPInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(),
					getContainerGraphicalRepresentation(), 1);
			FGEPoint initialCPInContainerView = new FGEPoint((int) (currentCPInContainerView.x + initialLocation.x - getX()),
					(int) (currentCPInContainerView.y + initialLocation.y - getY()));
			FGEPoint desiredCPInContainerView = new FGEPoint((int) (currentCPInContainerView.x + desiredLocation.x - getX()),
					(int) (currentCPInContainerView.y + desiredLocation.y - getY()));
			if (!containerViewArea.containsPoint(initialCPInContainerView)) {
				logger.warning("getMoveAuthorizedRatio() called for a shape whose initial location wasn't in container shape");
				return 1;
			}
			if (!containerViewArea.containsPoint(desiredCPInContainerView)) {
				// We are now sure that desired move will make the shape
				// go outside parent bounds
				FGESegment segment = new FGESegment(initialCPInContainerView, desiredCPInContainerView);
				FGEArea intersection = FGEIntersectionArea.makeIntersection(segment, containerViewBounds);
				if (intersection instanceof FGEPoint) {
					// Intersection is normally a point
					FGEPoint intersect = (FGEPoint) intersection;
					double currentRatio = 1;
					if (Math.abs(desiredCPInContainerView.x - initialCPInContainerView.x) > FGEGeometricObject.EPSILON) {
						currentRatio = (intersect.x - initialCPInContainerView.x)
								/ (desiredCPInContainerView.x - initialCPInContainerView.x) - FGEGeometricObject.EPSILON;
					} else if (Math.abs(desiredCPInContainerView.y - initialCPInContainerView.y) > FGEGeometricObject.EPSILON) {
						currentRatio = (intersect.y - initialCPInContainerView.y)
								/ (desiredCPInContainerView.y - initialCPInContainerView.y) - FGEGeometricObject.EPSILON;
					} else {
						logger.warning("Unexpected unsignifiant move from " + initialCPInContainerView + " to " + desiredCPInContainerView);
					}
					if (currentRatio < returnedAuthorizedRatio) {
						returnedAuthorizedRatio = currentRatio;
					}
				} else {
					logger.warning("Unexpected intersection: " + intersection);
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getMoveAuthorizedRatio() initial=" + initialLocation + " desired=" + desiredLocation + " return "
					+ returnedAuthorizedRatio);
		}
		if (returnedAuthorizedRatio < 0) {
			returnedAuthorizedRatio = 0;
		}
		return returnedAuthorizedRatio;
	}

	// *******************************************************************************
	// * Size management *
	// *******************************************************************************

	@Override
	public final void setText(String text) {
		super.setText(text);
		checkAndUpdateDimensionBoundsIfRequired();
	}

	@Override
	public void setTextStyle(TextStyle textStyle) {
		super.setTextStyle(textStyle);
		checkAndUpdateDimensionBoundsIfRequired();
	}

	@Override
	public boolean getAdjustMinimalWidthToLabelWidth() {
		return adjustMinimalWidthToLabelWidth;
	}

	@Override
	public void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth) {
		FGENotification notification = requireChange(ShapeParameters.adjustMinimalWidthToLabelWidth, adjustMinimalWidthToLabelWidth);
		if (notification != null) {
			this.adjustMinimalWidthToLabelWidth = adjustMinimalWidthToLabelWidth;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdjustMinimalHeightToLabelHeight() {
		return adjustMinimalHeightToLabelHeight;
	}

	@Override
	public void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight) {
		FGENotification notification = requireChange(ShapeParameters.adjustMinimalHeightToLabelHeight, adjustMinimalHeightToLabelHeight);
		if (notification != null) {
			this.adjustMinimalHeightToLabelHeight = adjustMinimalHeightToLabelHeight;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdjustMaximalWidthToLabelWidth() {
		return adjustMaximalWidthToLabelWidth;
	}

	@Override
	public void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth) {
		FGENotification notification = requireChange(ShapeParameters.adjustMaximalWidthToLabelWidth, adjustMaximalWidthToLabelWidth);
		if (notification != null) {
			this.adjustMaximalWidthToLabelWidth = adjustMaximalWidthToLabelWidth;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdjustMaximalHeightToLabelHeight() {
		return adjustMaximalHeightToLabelHeight;
	}

	@Override
	public void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight) {
		FGENotification notification = requireChange(ShapeParameters.adjustMaximalHeightToLabelHeight, adjustMaximalHeightToLabelHeight);
		if (notification != null) {
			this.adjustMaximalHeightToLabelHeight = adjustMaximalHeightToLabelHeight;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public final void setWidth(double aValue) {
		FGENotification notification = requireChange(ShapeParameters.width, aValue);
		if (notification != null) {
			FGEDimension oldSize = getSize();
			setWidthNoNotification(aValue);
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			notifyObjectResized(oldSize);
		}
	}

	@Override
	public void setWidthNoNotification(double aValue) {
		width = aValue;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public final void setHeight(double aValue) {
		FGENotification notification = requireChange(ShapeParameters.height, aValue);
		if (notification != null) {
			FGEDimension oldSize = getSize();
			setHeightNoNotification(aValue);
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			notifyObjectResized(oldSize);
		}
	}

	@Override
	public void setHeightNoNotification(double aValue) {
		height = aValue;
	}

	@Override
	public FGEDimension getSize() {
		return new FGEDimension(getWidth(), getHeight());
	}

	@Override
	public void setSize(FGEDimension newSize) {
		if (newSize == null) {
			return;
		}
		// Preventing size from being negative or equals to 0
		if (newSize.width <= 0) {
			newSize.width = FGEGeometricObject.EPSILON;
		}
		if (newSize.height <= 0) {
			newSize.height = FGEGeometricObject.EPSILON;
		}
		FGEDimension oldSize = getSize();
		if (!newSize.equals(oldSize)) {
			double oldWidth = getWidth();
			double oldHeight = getHeight();
			setWidthNoNotification(newSize.width);
			setHeightNoNotification(newSize.height);
			if (hasFloatingLabel()) {
				if (getAbsoluteTextX() >= 0) {
					if (getAbsoluteTextX() < getWidth()) {
						setAbsoluteTextX(getAbsoluteTextX() / oldSize.width * getWidth());
					} else {
						setAbsoluteTextX(getAbsoluteTextX() + getWidth() - oldSize.width);
					}
				}
				if (getAbsoluteTextY() >= 0) {
					if (getAbsoluteTextY() < getHeight()) {
						setAbsoluteTextY(getAbsoluteTextY() / oldSize.height * getHeight());
					} else {
						setAbsoluteTextY(getAbsoluteTextY() + getHeight() - oldSize.height);
					}
				}
			}
			checkAndUpdateDimensionBoundsIfRequired();
			if (isParentLayoutedAsContainer()) {
				((ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation()).checkAndUpdateDimensionIfRequired();
			}
			notifyObjectResized(oldSize);
			notifyChange(ShapeParameters.width, oldWidth, getWidth());
			notifyChange(ShapeParameters.height, oldHeight, getHeight());
			getShape().notifyObjectResized();
		}
	}

	@Override
	public double getMinimalWidth() {
		return minimalWidth;
	}

	@Override
	public final void setMinimalWidth(double minimalWidth) {
		FGENotification notification = requireChange(ShapeParameters.minimalWidth, minimalWidth);
		if (notification != null) {
			this.minimalWidth = minimalWidth;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getMinimalHeight() {
		return minimalHeight;
	}

	@Override
	public final void setMinimalHeight(double minimalHeight) {
		FGENotification notification = requireChange(ShapeParameters.minimalHeight, minimalHeight);
		if (notification != null) {
			this.minimalHeight = minimalHeight;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getMaximalHeight() {
		return maximalHeight;
	}

	@Override
	public final void setMaximalHeight(double maximalHeight) {
		FGENotification notification = requireChange(ShapeParameters.maximalHeight, maximalHeight);
		if (notification != null) {
			this.maximalHeight = maximalHeight;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getMaximalWidth() {
		return maximalWidth;
	}

	@Override
	public final void setMaximalWidth(double maximalWidth) {
		FGENotification notification = requireChange(ShapeParameters.maximalWidth, maximalWidth);
		if (notification != null) {
			this.maximalWidth = maximalWidth;
			checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public final boolean getAllowToLeaveBounds() {
		return allowToLeaveBounds;
	}

	@Override
	public void setAllowToLeaveBounds(boolean allowToLeaveBounds) {
		FGENotification notification = requireChange(ShapeParameters.allowToLeaveBounds, allowToLeaveBounds);
		if (notification != null) {
			this.allowToLeaveBounds = allowToLeaveBounds;
			hasChanged(notification);
		}
	}

	@Override
	public DimensionConstraints getDimensionConstraints() {
		// Shape dimension constraints may override defaults

		if (shape != null && shape.areDimensionConstrained()) {
			if (dimensionConstraints == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				return DimensionConstraints.CONSTRAINED_DIMENSIONS;
			}
			if (dimensionConstraints == DimensionConstraints.FREELY_RESIZABLE) {
				return DimensionConstraints.CONSTRAINED_DIMENSIONS;
			}
			return DimensionConstraints.UNRESIZABLE;
		}
		return dimensionConstraints;
	}

	@Override
	public void setDimensionConstraints(DimensionConstraints dimensionConstraints) {
		FGENotification notification = requireChange(ShapeParameters.dimensionConstraints, dimensionConstraints);
		if (notification != null && getShape() != null) {
			this.dimensionConstraints = dimensionConstraints;
			getShape().rebuildControlPoints();
			hasChanged(notification);
		}
	}

	@Override
	public FGESteppedDimensionConstraint getDimensionConstraintStep() {
		return dimensionConstraintStep;
	}

	@Override
	public void setDimensionConstraintStep(FGESteppedDimensionConstraint dimensionConstraintStep) {
		FGENotification notification = requireChange(ShapeParameters.dimensionConstraintStep, dimensionConstraintStep);
		if (notification != null) {
			this.dimensionConstraintStep = dimensionConstraintStep;
			if (isRegistered()) {
				if (APPLY_CONSTRAINTS_IMMEDIATELY) {
					checkAndUpdateDimensionIfRequired();
				}
				getShape().rebuildControlPoints();
				hasChanged(notification);
			}
		}
	}

	/**
	 * Calling this method forces FGE to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	protected void checkAndUpdateDimensionIfRequired() {
		if (getDimensionConstraints() == DimensionConstraints.CONTAINER) {
			List<? extends GraphicalRepresentation<?>> childs = getContainedGraphicalRepresentations();
			if (childs != null && childs.size() > 0) {
				ShapeGraphicalRepresentationImpl<?> first = (ShapeGraphicalRepresentationImpl<?>) childs.get(0);
				updateRequiredBoundsForChildGRLocation(first, first.getLocation());
			}
		} else {
			checkAndUpdateDimensionBoundsIfRequired();
		}
	}

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
	@Override
	public double getRequiredWidth(double labelWidth) {
		return labelWidth;
	}

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
	@Override
	public double getRequiredHeight(double labelHeight) {
		return labelHeight;
	}

	private boolean isCheckingDimensionConstraints = false;

	private void checkAndUpdateDimensionBoundsIfRequired() {

		if (isCheckingDimensionConstraints || labelMetricsProvider == null) {
			return;
		}

		try {

			isCheckingDimensionConstraints = true;

			boolean changed = false;
			FGEDimension newDimension = getSize();
			double minWidth = getMinimalWidth();
			double minHeight = getMinimalHeight();
			double maxWidth = getMaximalWidth();
			double maxHeight = getMaximalHeight();
			if (hasText() && !getIsFloatingLabel()) {
				Dimension normalizedLabelSize = getNormalizedLabelSize();
				int labelWidth = normalizedLabelSize.width;
				int labelHeight = normalizedLabelSize.height;
				double requiredWidth = getRequiredWidth(labelWidth);
				double requiredHeight = getRequiredHeight(labelHeight);
				double rh = 0, rw = 0;
				FGEPoint rp = new FGEPoint(getRelativeTextX(), getRelativeTextY());
				switch (getVerticalTextAlignment()) {
				case BOTTOM:
					if (FGEUtils.doubleEquals(rp.y, 0.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle BOTTOM alignement with relative y position set to 0!");
						}
					} else {
						rh = labelHeight / rp.y;
					}
					break;
				case MIDDLE:
					if (FGEUtils.doubleEquals(rp.y, 0.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle MIDDLE alignement with relative y position set to 0");
						}
					} else if (FGEUtils.doubleEquals(rp.y, 1.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle MIDDLE alignement with relative y position set to 1");
						}
					} else {
						if (rp.y > 0.5) {
							rh = labelHeight / (2 * (1 - rp.y));
						} else {
							rh = labelHeight / (2 * rp.y);
						}
					}
					break;
				case TOP:
					if (FGEUtils.doubleEquals(rp.x, 1.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle TOP alignement with relative y position set to 1!");
						}
					} else {
						rh = labelHeight / (1 - rp.y);
					}
					break;

				}

				switch (getHorizontalTextAlignment()) {
				case RIGHT:
					if (FGEUtils.doubleEquals(rp.x, 0.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle RIGHT alignement with relative x position set to 0!");
						}
					} else {
						rw = labelWidth / rp.x;
					}
				case CENTER:
					if (FGEUtils.doubleEquals(rp.x, 0.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle CENTER alignement with relative x position set to 0");
						}
					} else if (FGEUtils.doubleEquals(rp.x, 1.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle CENTER alignement with relative x position set to 1");
						}
					} else {
						if (rp.x > 0.5) {
							rw = labelWidth / (2 * (1 - rp.x));
						} else {
							rw = labelWidth / (2 * rp.x);
						}
					}
					break;
				case LEFT:
					if (FGEUtils.doubleEquals(rp.x, 1.0)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Impossible to handle LEFT alignement with relative x position set to 1!");
						}
					} else {
						rw = labelWidth / (1 - rp.x);
					}
					break;
				}

				requiredWidth = Math.max(rw, requiredWidth);
				requiredHeight = Math.max(rh, requiredHeight);

				if (getAdjustMinimalWidthToLabelWidth()) {
					minWidth = Math.max(requiredWidth, minWidth);
					if (getWidth() < minWidth) {
						newDimension.width = minWidth;
						changed = true;
					}
				}

				if (getAdjustMinimalHeightToLabelHeight()) {
					minHeight = Math.max(requiredHeight, minHeight);
					if (getHeight() < minHeight) {
						newDimension.height = minHeight;
						changed = true;
					}
				}

				if (getAdjustMaximalWidthToLabelWidth()) {
					maxWidth = Math.min(requiredWidth, maxWidth);
					if (getWidth() > maxWidth) {
						newDimension.width = maxWidth;
						changed = true;
					}
				}

				if (getAdjustMaximalHeightToLabelHeight()) {
					maxHeight = Math.min(requiredHeight, maxHeight);
					if (getHeight() > maxHeight) {
						newDimension.height = maxHeight;
						changed = true;
					}
				}
			}
			if (getMinimalWidth() > getMaximalWidth()) {
				logger.warning("Minimal width > maximal width, cannot proceed");
			} else {
				if (getWidth() < minWidth) {
					newDimension.width = minWidth;
					changed = true;
				}
				if (getWidth() > maxWidth) {
					newDimension.width = maxWidth;
					changed = true;
				}
			}
			if (getMinimalHeight() > getMaximalHeight()) {
				logger.warning("Minimal height > maximal height, cannot proceed");
			} else {
				if (getHeight() < minHeight) {
					newDimension.height = minHeight;
					changed = true;
				}
				if (getHeight() > maxHeight) {
					newDimension.height = maxHeight;
					changed = true;
				}
			}
			boolean useStepDimensionConstraints = getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED
					&& getDimensionConstraintStep() != null;
			if (useStepDimensionConstraints && hasText() && !isFloatingLabel) {
				if (getAdjustMinimalWidthToLabelWidth() && getAdjustMaximalWidthToLabelWidth()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on width! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
				if (getAdjustMinimalHeightToLabelHeight() && getAdjustMaximalHeightToLabelHeight()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on height! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
			}

			if (useStepDimensionConstraints) {
				FGEDimension d = getDimensionConstraintStep().getNearestDimension(newDimension, minWidth, maxWidth, minHeight, maxHeight);
				if (!d.equals(newDimension)) {
					newDimension = d;
					changed = true;
				}
			}
			if (changed) {
				setSize(newDimension);
				checkAndUpdateLocationIfRequired();
			}
		} finally {
			isCheckingDimensionConstraints = false;
		}
	}

	// *******************************************************************************
	// * Geometry constraints *
	// *******************************************************************************

	public static BindingDefinition X_CONSTRAINTS = new BindingDefinition("xConstraints", Double.class, BindingDefinitionType.GET, false);
	public static BindingDefinition Y_CONSTRAINTS = new BindingDefinition("yConstraints", Double.class, BindingDefinitionType.GET, false);
	public static BindingDefinition WIDTH_CONSTRAINTS = new BindingDefinition("widthConstraints", Double.class, BindingDefinitionType.GET,
			false);
	public static BindingDefinition HEIGHT_CONSTRAINTS = new BindingDefinition("heightConstraints", Double.class,
			BindingDefinitionType.GET, false);

	private DataBinding xConstraints;
	private DataBinding yConstraints;
	private DataBinding widthConstraints;
	private DataBinding heightConstraints;

	@Override
	public DataBinding getXConstraints() {
		if (xConstraints == null) {
			xConstraints = new DataBinding(this, ShapeParameters.xConstraints, X_CONSTRAINTS);
		}
		return xConstraints;
	}

	@Override
	public void setXConstraints(DataBinding xConstraints) {
		FGENotification notification = requireChange(ShapeParameters.xConstraints, xConstraints);
		if (notification != null) {
			xConstraints.setOwner(this);
			xConstraints.setBindingAttribute(ShapeParameters.xConstraints);
			xConstraints.setBindingDefinition(X_CONSTRAINTS);
			this.xConstraints = xConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DataBinding getYConstraints() {
		if (yConstraints == null) {
			yConstraints = new DataBinding(this, ShapeParameters.yConstraints, Y_CONSTRAINTS);
		}
		return yConstraints;
	}

	@Override
	public void setYConstraints(DataBinding yConstraints) {
		FGENotification notification = requireChange(ShapeParameters.yConstraints, yConstraints);
		if (notification != null) {
			yConstraints.setOwner(this);
			yConstraints.setBindingAttribute(ShapeParameters.yConstraints);
			yConstraints.setBindingDefinition(Y_CONSTRAINTS);
			this.yConstraints = yConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DataBinding getWidthConstraints() {
		if (widthConstraints == null) {
			widthConstraints = new DataBinding(this, ShapeParameters.widthConstraints, WIDTH_CONSTRAINTS);
		}
		return widthConstraints;
	}

	@Override
	public void setWidthConstraints(DataBinding widthConstraints) {
		FGENotification notification = requireChange(ShapeParameters.widthConstraints, widthConstraints);
		if (notification != null) {
			widthConstraints.setOwner(this);
			widthConstraints.setBindingAttribute(ShapeParameters.widthConstraints);
			widthConstraints.setBindingDefinition(WIDTH_CONSTRAINTS);
			this.widthConstraints = widthConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DataBinding getHeightConstraints() {
		if (heightConstraints == null) {
			heightConstraints = new DataBinding(this, ShapeParameters.heightConstraints, HEIGHT_CONSTRAINTS);
		}
		return heightConstraints;
	}

	@Override
	public void setHeightConstraints(DataBinding heightConstraints) {
		FGENotification notification = requireChange(ShapeParameters.heightConstraints, heightConstraints);
		if (notification != null) {
			heightConstraints.setOwner(this);
			heightConstraints.setBindingAttribute(ShapeParameters.heightConstraints);
			heightConstraints.setBindingDefinition(HEIGHT_CONSTRAINTS);
			this.heightConstraints = heightConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public void finalizeConstraints() {
		if (xConstraints != null && xConstraints.isValid()) {
			xConstraints.finalizeDeserialization();
			setX((Double) TypeUtils.castTo(xConstraints.getBindingValue(this), Double.class));
			setLocationConstraints(LocationConstraints.UNMOVABLE);
		}
		if (yConstraints != null && yConstraints.isValid()) {
			yConstraints.finalizeDeserialization();
			setY((Double) TypeUtils.castTo(yConstraints.getBindingValue(this), Double.class));
			setLocationConstraints(LocationConstraints.UNMOVABLE);
		}
		if (widthConstraints != null && widthConstraints.isValid()) {
			widthConstraints.finalizeDeserialization();
			setWidth((Double) TypeUtils.castTo(widthConstraints.getBindingValue(this), Double.class));
			setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}
		if (heightConstraints != null && heightConstraints.isValid()) {
			heightConstraints.finalizeDeserialization();
			setHeight((Double) TypeUtils.castTo(heightConstraints.getBindingValue(this), Double.class));
			setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}
	}

	@Override
	protected void computeNewConstraint(ConstraintDependency dependancy) {
		if (dependancy.requiringParameter == ShapeParameters.xConstraints && xConstraints != null && xConstraints.isValid()) {
			updateXPosition();
		} else if (dependancy.requiringParameter == ShapeParameters.yConstraints && yConstraints != null && yConstraints.isValid()) {
			updateYPosition();
		} else if (dependancy.requiringParameter == ShapeParameters.widthConstraints && widthConstraints != null
				&& widthConstraints.isValid()) {
			updateWidthPosition();
		} else if (dependancy.requiringParameter == ShapeParameters.heightConstraints && heightConstraints != null
				&& heightConstraints.isValid()) {
			updateHeightPosition();
		}
	}

	@Override
	public void updateConstraints() {
		// System.out.println("updateConstraints() called, valid=" + xConstraints.isValid() + "," + yConstraints.isValid() + ","
		// + widthConstraints.isValid() + "," + heightConstraints.isValid());
		logger.info("Called updateConstraints()");
		if (xConstraints != null && xConstraints.isValid()) {
			// System.out.println("x was " + getX() + " constraint=" + xConstraints);
			updateXPosition();
			// System.out.println("x is now " + getX());
		}
		if (yConstraints != null && yConstraints.isValid()) {
			// System.out.println("y was " + getY() + " constraint=" + yConstraints);
			updateYPosition();
			// System.out.println("y is now " + getY());
		}
		if (widthConstraints != null && widthConstraints.isValid()) {
			// System.out.println("width was " + getWidth() + " constraint=" + widthConstraints);
			updateWidthPosition();
			// System.out.println("width is now " + getWidth());
		}
		if (heightConstraints != null && heightConstraints.isValid()) {
			// System.out.println("height was " + getHeight() + " constraint=" + heightConstraints);
			updateHeightPosition();
			// System.out.println("height is now " + getHeight());
		}
	}

	private void updateXPosition() {
		Number n = (Number) xConstraints.getBindingValue(this);
		if (n != null) {
			// System.out.println("New value for x is now: " + newValue);
			setX(n.doubleValue());
		}
	}

	private void updateYPosition() {
		Number n = (Number) yConstraints.getBindingValue(this);
		if (n != null) {
			// System.out.println("New value for y is now: " + newValue);
			setY(n.doubleValue());
		}
	}

	private void updateWidthPosition() {
		Number n = (Number) widthConstraints.getBindingValue(this);
		if (n != null) {
			// System.out.println("New value for width is now: " + newValue);
			setWidth(n.doubleValue());
		}
	}

	private void updateHeightPosition() {
		Number n = (Number) heightConstraints.getBindingValue(this);
		if (n != null) {
			// System.out.println("New value for height is now: " + newValue);
			setHeight(n.doubleValue());
		}
	}

	@Override
	public void constraintChanged(DataBinding constraint) {
		// logger.info(">>>>>>> OK, constraint "+constraint+" changed for "+constraint.getBindingAttribute());
		constraint.updateDependancies();
		if (constraint.getBindingAttribute() == ShapeParameters.xConstraints && xConstraints != null && xConstraints.isValid()) {
			updateXPosition();
		} else if (constraint.getBindingAttribute() == ShapeParameters.yConstraints && yConstraints != null && yConstraints.isValid()) {
			updateYPosition();
		} else if (constraint.getBindingAttribute() == ShapeParameters.widthConstraints && widthConstraints != null
				&& widthConstraints.isValid()) {
			updateWidthPosition();
		} else if (constraint.getBindingAttribute() == ShapeParameters.heightConstraints && heightConstraints != null
				&& heightConstraints.isValid()) {
			updateHeightPosition();
		}
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
		FGENotification notification = requireChange(ShapeParameters.foreground, aForeground, false);
		if (notification != null) {
			if (foreground != null) {
				foreground.deleteObserver(this);
			}
			foreground = aForeground;
			if (aForeground != null) {
				aForeground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public ForegroundStyle getSelectedForeground() {
		if (selectedForeground == null) {
			selectedForeground = foreground.clone();
		}
		return selectedForeground;
	}

	@Override
	public void setSelectedForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(ShapeParameters.selectedForeground, aForeground, false);
		if (notification != null) {
			if (selectedForeground != null) {
				selectedForeground.deleteObserver(this);
			}
			selectedForeground = aForeground;
			if (aForeground != null) {
				aForeground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasSelectedForeground() {
		return hasSelectedForeground;
	}

	@Override
	public void setHasSelectedForeground(boolean aFlag) {
		hasSelectedForeground = aFlag;
	}

	@Override
	public ForegroundStyle getFocusedForeground() {
		if (focusedForeground == null) {
			focusedForeground = foreground.clone();
		}
		return focusedForeground;
	}

	@Override
	public void setFocusedForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(ShapeParameters.focusedForeground, aForeground, false);
		if (notification != null) {
			if (focusedForeground != null) {
				focusedForeground.deleteObserver(this);
			}
			focusedForeground = aForeground;
			if (aForeground != null) {
				aForeground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasFocusedForeground() {
		return hasFocusedForeground;
	}

	@Override
	public void setHasFocusedForeground(boolean aFlag) {
		hasFocusedForeground = aFlag;
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
		FGENotification notification = requireChange(ShapeParameters.background, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (background != null) {
				background.deleteObserver(this);
			}
			background = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null) {
				aBackground.addObserver(this);
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
			setBackground(BackgroundStyle.makeBackground(backgroundType));
		}
	}

	@Override
	public BackgroundStyle getSelectedBackground() {
		if (selectedBackground == null) {
			selectedBackground = background.clone();
		}
		return selectedBackground;
	}

	@Override
	public void setSelectedBackground(BackgroundStyle aBackground) {
		FGENotification notification = requireChange(ShapeParameters.selectedBackground, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (selectedBackground != null) {
				selectedBackground.deleteObserver(this);
			}
			selectedBackground = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null) {
				aBackground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasSelectedBackground() {
		return hasSelectedBackground;
	}

	@Override
	public void setHasSelectedBackground(boolean aFlag) {
		hasSelectedBackground = aFlag;
	}

	@Override
	public BackgroundStyle getFocusedBackground() {
		if (focusedBackground == null) {
			focusedBackground = background.clone();
		}
		return focusedBackground;
	}

	@Override
	public void setFocusedBackground(BackgroundStyle aBackground) {
		FGENotification notification = requireChange(ShapeParameters.focusedBackground, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (focusedBackground != null) {
				focusedBackground.deleteObserver(this);
			}
			focusedBackground = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null) {
				aBackground.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasFocusedBackground() {
		return hasFocusedBackground;
	}

	@Override
	public void setHasFocusedBackground(boolean aFlag) {
		hasFocusedBackground = aFlag;
	}

	@Override
	public ShapeBorder getBorder() {
		return border;
	}

	@Override
	public void setBorder(ShapeBorder border) {
		FGENotification notification = requireChange(ShapeParameters.border, border);
		if (notification != null) {
			this.border = border;
			hasChanged(notification);
			notifyObjectResized();
		}
	}

	@Override
	public ShadowStyle getShadowStyle() {
		return shadowStyle;
	}

	@Override
	public void setShadowStyle(ShadowStyle aShadowStyle) {
		FGENotification notification = requireChange(ShapeParameters.shadowStyle, aShadowStyle);
		if (notification != null) {
			if (shadowStyle != null) {
				shadowStyle.deleteObserver(this);
			}
			this.shadowStyle = aShadowStyle;
			if (aShadowStyle != null) {
				aShadowStyle.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	/*public boolean getDrawShadow()
	{
		return drawShadow;
	}

	public void setDrawShadow(boolean drawShadow)
	{
		FGENotification notification = requireChange(ShapeParameters.drawShadow,
				drawShadow);
		if (notification != null) {
			this.drawShadow = drawShadow;
			hasChanged(notification);
		}
	}*/

	/*public int getShadowDarkness()
	{
		return shadowDarkness;
	}

	public void setShadowDarkness(int shadowDarkness)
	{
		FGENotification notification = requireChange(
				ShapeParameters.shadowDarkness, shadowDarkness);
		if (notification != null) {
			this.shadowDarkness = shadowDarkness;
			hasChanged(notification);
		}
	}


	public int getShadowBlur()
	{
		return shadowBlur;
	}

	public void setShadowBlur(int shadowBlur)
	{
		FGENotification notification = requireChange(ShapeParameters.shadowBlur,
				shadowBlur);
		if (notification != null) {
			this.shadowBlur = shadowBlur;
			hasChanged(notification);
		}
	}

	public int getShadowDeep()
	{
		return shadowDeep;
	}

	public void setShadowDeep(int shadowDeep)
	{
		FGENotification notification = requireChange(ShapeParameters.shadowDeep,
				shadowDeep);
		if (notification != null) {
			this.shadowDeep = shadowDeep;
			hasChanged(notification);
		}
	}*/

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public void setShape(Shape aShape) {
		aShape.setGraphicalRepresentation(this);
		FGENotification notification = requireChange(ShapeParameters.shape, aShape);
		if (notification != null) {
			ShapeType oldType = aShape != null ? aShape.getShapeType() : null;
			this.shape = aShape;
			shape.rebuildControlPoints();
			hasChanged(notification);
			setChanged();
			notifyObservers(new FGENotification(ShapeParameters.shapeType, oldType, aShape.getShapeType()));
			notifyShapeChanged();
		}
	}

	@Override
	public ShapeType getShapeType() {
		if (shape != null) {
			return shape.getShapeType();
		}
		return null;
	}

	@Override
	public void setShapeType(ShapeType shapeType) {
		if (getShapeType() != shapeType) {
			setShape(Shape.makeShape(shapeType, this));
			if (getShape().areDimensionConstrained()) {
				double newSize = Math.max(getWidth(), getHeight());
				setWidth(newSize);
				setHeight(newSize);
			}
		}
	}

	@Override
	public void notifyShapeChanged() {
		setChanged();
		notifyObservers(new ShapeChanged());
	}

	@Override
	public void notifyShapeNeedsToBeRedrawn() {
		setChanged();
		notifyObservers(new ShapeNeedsToBeRedrawn());
	}

	@Override
	public void notifyObjectMoved() {
		boolean mustNotify = !isMoving();
		if (mustNotify) {
			notifyObjectWillMove();
		}
		notifyObjectMoved(null);
		if (mustNotify) {
			notifyObjectHasMoved();
		}
	}

	@Override
	public void notifyObjectMoved(FGEPoint oldLocation) {
		setChanged();
		notifyObservers(new ObjectMove(oldLocation, getLocation()));
	}

	@Override
	public void notifyObjectWillMove() {
		isMoving = true;
		setChanged();
		notifyObservers(new ObjectWillMove());
	}

	@Override
	public void notifyObjectHasMoved() {
		isMoving = false;
		setChanged();
		notifyObservers(new ObjectHasMoved());
	}

	@Override
	public boolean isMoving() {
		return isMoving;
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized() {
		notifyObjectResized(null);
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(FGEDimension oldSize) {
		if (getShape() != null) {
			getShape().updateShape();
		}
		setChanged();
		notifyObservers(new ObjectResized(oldSize, getSize()));
	}

	/**
	 * Notify that the object will be resized
	 */
	@Override
	public void notifyObjectWillResize() {
		isResizing = true;
		setChanged();
		notifyObservers(new ObjectWillResize());
	}

	/**
	 * Notify that the object resizing has finished (take care that this just notify END of resize, this should NOT be used to notify a
	 * resizing: use notifyObjectResize() instead)
	 */
	@Override
	public void notifyObjectHasResized() {
		isResizing = false;
		for (GraphicalRepresentation<?> gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentationImpl) {
				((ShapeGraphicalRepresentationImpl<?>) gr).checkAndUpdateLocationIfRequired();
			}
		}
		setChanged();
		notifyObservers(new ObjectHasResized());
	}

	@Override
	public boolean isResizing() {
		return isResizing;
	}

	@Override
	public final boolean shouldBeDisplayed() {
		return super.shouldBeDisplayed();
	}

	@Override
	public boolean getIsFloatingLabel() {
		return isFloatingLabel;
	}

	@Override
	public void setIsFloatingLabel(boolean isFloatingLabel) {
		FGENotification notification = requireChange(ShapeParameters.isFloatingLabel, isFloatingLabel);
		if (notification != null) {
			this.isFloatingLabel = isFloatingLabel;
			hasChanged(notification);
		}
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText() && getIsFloatingLabel();
	}

	@Override
	public double getRelativeTextX() {
		return relativeTextX;
	}

	@Override
	public void setRelativeTextX(double textX) {
		FGENotification notification = requireChange(ShapeParameters.relativeTextX, textX);
		if (notification != null) {
			this.relativeTextX = textX;
			hasChanged(notification);
		}
	}

	@Override
	public double getRelativeTextY() {
		return relativeTextY;
	}

	@Override
	public void setRelativeTextY(double textY) {
		FGENotification notification = requireChange(ShapeParameters.relativeTextY, textY);
		if (notification != null) {
			this.relativeTextY = textY;
			hasChanged(notification);
		}
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public int getViewX(double scale) {
		return (int) (getX() * scale/*-(border!=null?border.left:0)*/);
	}

	@Override
	public int getViewY(double scale) {
		return (int) (getY() * scale/*-(border!=null?border.top:0)*/);
	}

	@Override
	public int getViewWidth(double scale) {
		return (int) (getUnscaledViewWidth() * scale) + 1;
	}

	@Override
	public double getUnscaledViewWidth() {
		return getWidth() + (getBorder() != null ? getBorder().left + getBorder().right : 0);
	}

	@Override
	public int getViewHeight(double scale) {
		return (int) (getUnscaledViewHeight() * scale) + 1;
	}

	@Override
	public double getUnscaledViewHeight() {
		return getHeight() + (getBorder() != null ? getBorder().top + getBorder().bottom : 0);
	}

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	@Override
	public FGERectangle getBounds() {
		return new FGERectangle(getX(), getY(), getWidth() + (getBorder() != null ? getBorder().left + getBorder().right : 0),
				getUnscaledViewHeight());
	}

	/**
	 * Return view bounds (excluding border) relative to parent container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = (int) ((getX() + (getBorder() != null ? getBorder().left : 0)) * scale);
		bounds.y = (int) ((getY() + (getBorder() != null ? getBorder().top : 0)) * scale);
		bounds.width = (int) (getWidth() * scale);
		bounds.height = (int) (getHeight() * scale);

		return bounds;
	}

	/**
	 * Return view bounds (excluding border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getBounds(GraphicalRepresentation<?> container, double scale) {
		Rectangle bounds = getBounds(scale);
		bounds = convertRectangle(getContainerGraphicalRepresentation(), bounds, container, scale);
		return bounds;
	}

	/**
	 * Return logical bounds (including border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getViewBounds(GraphicalRepresentation<?> container, double scale) {
		Rectangle bounds = getViewBounds(scale);
		if (getContainerGraphicalRepresentation() == null) {
			logger.warning("Container is null for " + this + " validated=" + isValidated());
		}
		if (container == null) {
			logger.warning("Container is null for " + this + " validated=" + isValidated());
		}
		bounds = convertRectangle(getContainerGraphicalRepresentation(), bounds, container, scale);
		return bounds;
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = AffineTransform.getScaleInstance(getWidth(), getHeight());
		if (getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(getBorder().left, getBorder().top));
		}
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
		}
		return returned;
		/*
		double x2 = x*getWidth();
		double y2 = y*getHeight();
		if (getBorder() != null) {
			x2 += getBorder().left;
			y2 += getBorder().top;
		}
		if (scale != 1) {
			x2 = x2*scale;
			y2 = y2*scale;
		}
		return new Point((int)x2,(int)y2);*/
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		AffineTransform returned = new AffineTransform();
		if (scale != 1) {
			returned = AffineTransform.getScaleInstance(1 / scale, 1 / scale);
		}
		if (getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(-getBorder().left, -getBorder().top));
		}
		returned.preConcatenate(AffineTransform.getScaleInstance(1 / getWidth(), 1 / getHeight()));
		return returned;
		/*
		double x2= (double)x;
		double y2= (double)y;

		if (scale != 1) {
			x2 = x2/scale;
			y2 = y2/scale;
		}

		if (getBorder() != null) {
			x2 -= getBorder().left;
			y2 -= getBorder().top;
		}

		x2 = x2/getWidth();
		y2 = y2/getHeight();

		return new FGEPoint(x2,y2);*/
	}

	@Override
	public boolean isPointInsideShape(FGEPoint aPoint) {
		return shape.isPointInsideShape(aPoint);
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public DecorationPainter getDecorationPainter() {
		return decorationPainter;
	}

	@Override
	public void setDecorationPainter(DecorationPainter aPainter) {
		decorationGraphics = new FGEShapeDecorationGraphics(this);
		decorationPainter = aPainter;
	}

	@Override
	public ShapePainter getShapePainter() {
		return shapePainter;
	}

	@Override
	public void setShapePainter(ShapePainter aPainter) {
		shapePainter = aPainter;
	}

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		if (!isRegistered()) {
			setRegistered(true);
		}
		super.paint(g, controller);

		Graphics2D g2 = (Graphics2D) g;
		graphics.createGraphics(g2, controller);

		// If there is a decoration painter init its graphics
		if (decorationPainter != null) {
			decorationGraphics.createGraphics(g2, controller);
		}

		// If there is a decoration painter and decoration should be painted BEFORE shape, fo it now
		if (decorationPainter != null && decorationPainter.paintBeforeShape()) {
			decorationPainter.paintDecoration(decorationGraphics);
		}

		if (FGEConstants.DEBUG) {
			if (getBorder() != null) {
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
				g2.setColor(Color.BLUE);
				g2.drawRect((int) (getBorder().left * controller.getScale()), (int) (getBorder().top * controller.getScale()),
						(int) (getWidth() * controller.getScale()) - 1, (int) (getHeight() * controller.getScale()) - 1);
			} else {
				g2.setColor(Color.BLUE);
				g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
			}
		}

		if (shape != null && getShadowStyle() != null) {
			if (getShadowStyle().getDrawShadow()) {
				shape.paintShadow(graphics);
			}
			shape.paintShape(graphics);
		}

		if (shapePainter != null) {
			shapePainter.paintShape(graphics);
		}

		// If there is a decoration painter and decoration should be painted AFTER shape, do it now
		if (decorationPainter != null && !decorationPainter.paintBeforeShape()) {
			decorationPainter.paintDecoration(decorationGraphics);
		}

		graphics.releaseGraphics();

		if (decorationPainter != null) {
			decorationGraphics.releaseGraphics();
		}
	}

	@Override
	public Point getLabelLocation(double scale) {
		Point point;
		if (getIsFloatingLabel()) {
			point = new Point((int) (getAbsoluteTextX() * scale + getViewX(scale)), (int) (getAbsoluteTextY() * scale + getViewY(scale)));
		} else {
			FGEPoint relativePosition = new FGEPoint(getRelativeTextX(), getRelativeTextY());
			point = convertLocalNormalizedPointToRemoteViewCoordinates(relativePosition, getContainerGraphicalRepresentation(), scale);
		}
		Dimension d = getLabelDimension(scale);
		switch (getHorizontalTextAlignment()) {
		case CENTER:
			point.x -= d.width / 2;
			break;
		case LEFT:
			break;
		case RIGHT:
			point.x -= d.width;
			break;

		}
		switch (getVerticalTextAlignment()) {
		case BOTTOM:
			point.y -= d.height;
			break;
		case MIDDLE:
			point.y -= d.height / 2;
			break;
		case TOP:
			break;

		}
		return point;
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		if (getIsFloatingLabel()) {
			Dimension d = getLabelDimension(scale);
			switch (getHorizontalTextAlignment()) {
			case CENTER:
				point.x += d.width / 2;
				break;
			case LEFT:
				break;
			case RIGHT:
				point.x += d.width;
				break;

			}
			switch (getVerticalTextAlignment()) {
			case BOTTOM:
				point.y += d.height;
				break;
			case MIDDLE:
				point.y += d.height / 2;
				break;
			case TOP:
				break;
			}
			FGEPoint p = new FGEPoint((point.x - getViewX(scale)) / scale, (point.y - getViewY(scale)) / scale);
			setAbsoluteTextX(p.x);
			setAbsoluteTextY(p.y);
		}
	}

	@Override
	public int getAvailableLabelWidth(double scale) {
		if (getLineWrap()) {
			double rpx = getRelativeTextX();
			switch (getHorizontalTextAlignment()) {
			case RIGHT:
				if (FGEUtils.doubleEquals(rpx, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle RIGHT alignement with relative x position set to 0!");
					}
				} else {
					return (int) (getWidth() * rpx * scale);
				}
			case CENTER:
				if (FGEUtils.doubleEquals(rpx, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle CENTER alignement with relative x position set to 0");
					}
				} else if (FGEUtils.doubleEquals(rpx, 1.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle CENTER alignement with relative x position set to 1");
					}
				} else {
					if (rpx > 0.5) {
						return (int) (getWidth() * 2 * (1 - rpx) * scale);
					} else {
						return (int) (getWidth() * 2 * rpx * scale);
					}
				}
				break;
			case LEFT:
				if (FGEUtils.doubleEquals(rpx, 1.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle LEFT alignement with relative x position set to 1");
					}
				} else {
					return (int) (getWidth() * (1 - rpx) * scale);
				}
				break;
			}
		}
		return super.getAvailableLabelWidth(scale);
	}

	@Override
	public void setHorizontalTextAlignment(org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment horizontalTextAlignment) {
		super.setHorizontalTextAlignment(horizontalTextAlignment);
		checkAndUpdateDimensionBoundsIfRequired();
	}

	@Override
	public void setVerticalTextAlignment(org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment verticalTextAlignment) {
		super.setVerticalTextAlignment(verticalTextAlignment);
		checkAndUpdateDimensionBoundsIfRequired();
	}

	@Override
	public String getInspectorName() {
		return "ShapeGraphicalRepresentation.inspector";
	}

	// Override for a custom view management
	@Override
	public ShapeView<O> makeShapeView(DrawingController<?> controller) {
		return new ShapeView<O>(this, controller);
	}

	@Override
	public String toString() {
		if (isRegistered()) {
			return getClass().getSimpleName() + "[" + getShapeType() + "," + getBounds(1) + "," + getText() + "]";
		} else {
			return getClass().getSimpleName() + "[" + getShapeType() + "," + getText() + "]";
		}
	}

	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		// The two calls below seem to be very important to ensure proper layout of
		// containers with constraints CONTAINER!
		checkAndUpdateLocationIfRequired();
		checkAndUpdateDimensionIfRequired();
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return getShape().getControlPoints();
	}

	@Override
	public FGEShapeGraphics getGraphics() {
		return graphics;
	}

	/**
	 * Override this if you want to use such a feature
	 * 
	 * @return
	 */
	@Override
	public boolean isAllowedToBeDraggedOutsideParentContainer() {
		return false;
	}

	/**
	 * Override this if you want to use this feature Default implementation return always false
	 * 
	 * @return
	 */
	@Override
	public boolean isAllowedToBeDraggedOutsideParentContainerInsideContainer(GraphicalRepresentation<?> container) {
		return false;
	}

	/**
	 * Override this if you want to use this feature Default implementation does nothing return boolean indicating if drag was successfully
	 * performed
	 */
	@Override
	public boolean dragOutsideParentContainerInsideContainer(GraphicalRepresentation<?> container, FGEPoint location) {
		return false;
	}

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

	@Override
	public void performRandomLayout() {
		performRandomLayout(getWidth(), getHeight());
	}

	@Override
	public void performAutoLayout() {
		performAutoLayout(getWidth(), getHeight());
	}

}