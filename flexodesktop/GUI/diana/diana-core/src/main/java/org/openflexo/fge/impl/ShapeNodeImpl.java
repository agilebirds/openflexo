package org.openflexo.fge.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ConstraintDependency;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.GeomUtils;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphicsImpl;
import org.openflexo.fge.graphics.FGEShapeGraphicsImpl;
import org.openflexo.fge.graphics.ShapeDecorationPainter;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.notifications.BindingChanged;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.ObjectHasMoved;
import org.openflexo.fge.notifications.ObjectHasResized;
import org.openflexo.fge.notifications.ObjectMove;
import org.openflexo.fge.notifications.ObjectResized;
import org.openflexo.fge.notifications.ObjectWillMove;
import org.openflexo.fge.notifications.ObjectWillResize;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.impl.ShapeImpl;

public class ShapeNodeImpl<O> extends ContainerNodeImpl<O, ShapeGraphicalRepresentation> implements ShapeNode<O> {

	private static final Logger logger = Logger.getLogger(ShapeNodeImpl.class.getPackage().getName());

	// private double x = 0;
	// private double y = 0;
	// private double width = 0;
	// private double height = 0;

	private boolean isMoving = false;

	private boolean observeParentGRBecauseMyLocationReferToIt = false;

	private FGEShapeGraphicsImpl graphics;
	private FGEShapeDecorationGraphicsImpl decorationGraphics;
	private ShapeDecorationPainter decorationPainter;
	private ShapePainter shapePainter;

	private ShapeImpl<?> shape;

	protected ShapeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ShapeGraphicalRepresentation> grBinding,
			ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		graphics = new FGEShapeGraphicsImpl(this);
		// width = getGraphicalRepresentation().getMinimalWidth();
		// height = getGraphicalRepresentation().getMinimalHeight();
	}

	@Override
	public ShapeSpecification getShapeSpecification() {
		if (getGraphicalRepresentation() != null) {
			return getGraphicalRepresentation().getShapeSpecification();
		}
		return null;
	}

	@Override
	public ShapeImpl<?> getShape() {
		if (shape == null && getGraphicalRepresentation() != null && getGraphicalRepresentation().getShapeSpecification() != null) {
			shape = (ShapeImpl<?>) getGraphicalRepresentation().getShapeSpecification().makeShape(this);
		}
		return shape;
	}

	@Override
	public FGEShape<?> getFGEShape() {
		if (getShape() != null) {
			return getShape().getShape();
		}
		return null;
	}

	@Override
	public FGEShape<?> getFGEShapeOutline() {
		if (getShape() != null) {
			return getShape().getOutline();
		}
		return null;
	}

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	@Override
	public FGERectangle getBounds() {
		return new FGERectangle(getX(), getY(), getUnscaledViewWidth(), getUnscaledViewHeight());
	}

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	public FGERectangle getBoundsNoBorder() {
		return new FGERectangle(getX(), getY(), getWidth(), getHeight());
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

		bounds.x = (int) ((getX() + (getGraphicalRepresentation().getBorder() != null ? getGraphicalRepresentation().getBorder().getLeft()
				: 0)) * scale);
		bounds.y = (int) ((getY() + (getGraphicalRepresentation().getBorder() != null ? getGraphicalRepresentation().getBorder().getTop()
				: 0)) * scale);
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
	public Rectangle getBounds(DrawingTreeNode<?, ?> aContainer, double scale) {
		Rectangle bounds = getBounds(scale);
		bounds = FGEUtils.convertRectangle(getParentNode(), bounds, aContainer, scale);
		return bounds;
	}

	/**
	 * Return logical bounds (including border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Rectangle getViewBounds(DrawingTreeNode<?, ?> aContainer, double scale) {
		Rectangle bounds = getViewBounds(scale);
		if (getParentNode() == null) {
			logger.warning("Container is null for " + this + " validated=" + isValidated());
		}
		if (aContainer == null) {
			logger.warning("Container is null for " + this + " validated=" + isValidated());
		}
		bounds = FGEUtils.convertRectangle(getParentNode(), bounds, aContainer, scale);
		return bounds;
	}

	@Override
	public boolean isPointInsideShape(FGEPoint aPoint) {
		return getShape().isPointInsideShape(aPoint);
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = AffineTransform.getScaleInstance(getGraphicalRepresentation().getWidth(), getGraphicalRepresentation()
				.getHeight());
		if (getGraphicalRepresentation().getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(getGraphicalRepresentation().getBorder().getLeft(),
					getGraphicalRepresentation().getBorder().getTop()));
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
		if (getGraphicalRepresentation().getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(-getGraphicalRepresentation().getBorder().getLeft(),
					-getGraphicalRepresentation().getBorder().getTop()));
		}
		returned.preConcatenate(AffineTransform.getScaleInstance(1 / getGraphicalRepresentation().getWidth(),
				1 / getGraphicalRepresentation().getHeight()));
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
	public int getViewHeight(double scale) {
		return (int) (getUnscaledViewHeight() * scale) + 1;
	}

	@Override
	public double getUnscaledViewWidth() {
		return getGraphicalRepresentation().getWidth()
				+ (getGraphicalRepresentation().getBorder() != null ? getGraphicalRepresentation().getBorder().getLeft()
						+ getGraphicalRepresentation().getBorder().getRight() : 0);
	}

	@Override
	public double getUnscaledViewHeight() {
		return getGraphicalRepresentation().getHeight()
				+ (getGraphicalRepresentation().getBorder() != null ? getGraphicalRepresentation().getBorder().getTop()
						+ getGraphicalRepresentation().getBorder().getBottom() : 0);
	}

	@Override
	public FGEShapeGraphicsImpl getGraphics() {
		return graphics;
	}

	@Override
	public void update(Observable observable, Object notification) {
		// System.out.println("update() in ShapeNodeImpl, received " + notification + " from " + observable);

		if (temporaryIgnoredObservables.contains(observable)) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		super.update(observable, notification);

		if (notification instanceof FGENotification && observable == getGraphicalRepresentation()) {
			// Those notifications are forwarded by my graphical representation
			FGENotification notif = (FGENotification) notification;

			if (notif.getParameter() == GraphicalRepresentation.TEXT) {
				checkAndUpdateDimensionIfRequired();
			} else if (notif.getParameter() == GraphicalRepresentation.TEXT_STYLE) {
				checkAndUpdateDimensionIfRequired();
			} else if (notif.getParameter() == ShapeGraphicalRepresentation.ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT
					|| notif.getParameter() == ShapeGraphicalRepresentation.ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH
					|| notif.getParameter() == ShapeGraphicalRepresentation.ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT
					|| notif.getParameter() == ShapeGraphicalRepresentation.ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH) {
				checkAndUpdateDimensionIfRequired();
			} else if (notif.getParameter() == ContainerGraphicalRepresentation.WIDTH
					|| notif.getParameter() == ContainerGraphicalRepresentation.HEIGHT
					|| notif.getParameter() == ShapeGraphicalRepresentation.MINIMAL_HEIGHT
					|| notif.getParameter() == ShapeGraphicalRepresentation.MINIMAL_WIDTH
					|| notif.getParameter() == ShapeGraphicalRepresentation.MAXIMAL_HEIGHT
					|| notif.getParameter() == ShapeGraphicalRepresentation.MAXIMAL_WIDTH) {
				checkAndUpdateDimensionIfRequired();
			} else if (notif.getParameter() == GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT
					|| notif.getParameter() == GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT) {
				checkAndUpdateDimensionIfRequired();
			} else if (notif.getParameter() == ShapeGraphicalRepresentation.LOCATION_CONSTRAINTS
					|| notif.getParameter() == ShapeGraphicalRepresentation.LOCATION_CONSTRAINED_AREA
					|| notif.getParameter() == ShapeGraphicalRepresentation.DIMENSION_CONSTRAINT_STEP
					|| notif.getParameter() == ShapeGraphicalRepresentation.DIMENSION_CONSTRAINTS) {
				checkAndUpdateLocationIfRequired();
				getShape().updateControlPoints();
			} else if (notif.getParameter() == ShapeGraphicalRepresentation.ADAPT_BOUNDS_TO_CONTENTS) {
				extendBoundsToHostContents();
			} else if (notif.getParameter() == ShapeGraphicalRepresentation.BORDER) {
				notifyObjectResized();
			} else if (notif.getParameter() == ShapeGraphicalRepresentation.SHAPE
					|| notif.getParameter() == ShapeGraphicalRepresentation.SHAPE_TYPE) {
				getShape().updateShape();
				notifyShapeChanged();
			}

			if (notif instanceof BindingChanged) {
				DataBinding<?> dataBinding = ((BindingChanged) notif).getBinding();
				if (dataBinding == getGraphicalRepresentation().getXConstraints() && dataBinding.isValid()) {
					updateXPosition();
				} else if (dataBinding == getGraphicalRepresentation().getYConstraints() && dataBinding.isValid()) {
					updateYPosition();
				} else if (dataBinding == getGraphicalRepresentation().getWidthConstraints() && dataBinding.isValid()) {
					updateWidthPosition();
				} else if (dataBinding == getGraphicalRepresentation().getHeightConstraints() && dataBinding.isValid()) {
					updateHeightPosition();
				}

			}

		}

		if (observeParentGRBecauseMyLocationReferToIt /*&& observable == getContainerGraphicalRepresentation()*/) {
			if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize
					|| notification instanceof ObjectHasMoved || notification instanceof ObjectHasResized
					|| notification instanceof ObjectMove || notification instanceof ObjectResized || notification instanceof ShapeChanged) {
				checkAndUpdateLocationIfRequired();
			}
		}

		if (observable instanceof BackgroundStyle) {
			notifyAttributeChanged(ShapeGraphicalRepresentation.BACKGROUND, null, getGraphicalRepresentation().getBackground());
		}
		if (observable instanceof ForegroundStyle) {
			notifyAttributeChanged(ShapeGraphicalRepresentation.FOREGROUND, null, getGraphicalRepresentation().getForeground());
		}
		if (observable instanceof ShadowStyle) {
			notifyAttributeChanged(ShapeGraphicalRepresentation.SHADOW_STYLE, null, getGraphicalRepresentation().getShadowStyle());
		}
	}

	@Override
	public void extendParentBoundsToHostThisShape() {
		if (getParentNode() instanceof ShapeNode) {
			ShapeNode parent = (ShapeNode) getParentNode();
			parent.extendBoundsToHostContents();
		}
	}

	/**
	 * Check and eventually relocate and resize current graphical representation in order to all all contained shape graphical
	 * representations. Contained graphical representations may substantically be relocated.
	 */
	@Override
	public void extendBoundsToHostContents() {

		boolean needsResize = false;
		FGEDimension newDimension = new FGEDimension(getWidth(), getHeight());
		boolean needsRelocate = false;
		FGEPoint newPosition = new FGEPoint(getX(), getY());
		double deltaX = 0;
		double deltaY = 0;

		// First compute the delta to be applied (max of all required delta)
		for (DrawingTreeNode<?, ?> child : getChildNodes()) {
			if (child instanceof ShapeNode) {
				ShapeNode gr = (ShapeNode) child;
				if (gr.getX() < -deltaX) {
					deltaX = -gr.getX();
					needsRelocate = true;
				}
				if (gr.getY() < -deltaY) {
					deltaY = -gr.getY();
					needsRelocate = true;
				}
			}
		}

		// Relocate
		if (needsRelocate) {
			System.out.println("Relocate with deltaX=" + deltaX + " deltaY=" + deltaY);
			newPosition.x = newPosition.x - deltaX;
			newPosition.y = newPosition.y - deltaY;
			setLocation(newPosition);
			needsResize = true;
			newDimension = new FGEDimension(getWidth() + deltaX, getHeight() + deltaY);
			for (DrawingTreeNode<?, ?> child : getChildNodes()) {
				if (child instanceof ShapeNode && child != this) {
					ShapeNode c = (ShapeNode) child;
					c.setLocation(new FGEPoint(c.getX() + deltaX, c.getY() + deltaY));
				}
			}
		}

		// First compute the resize to be applied
		for (DrawingTreeNode<?, ?> child : getChildNodes()) {
			if (child instanceof ShapeNode) {
				ShapeNode gr = (ShapeNode) child;
				if (gr.getX() + gr.getWidth() > getWidth()) {
					newDimension.width = gr.getX() + gr.getWidth();
					needsResize = true;
				}
				if (gr.getY() + gr.getHeight() > getHeight()) {
					newDimension.height = gr.getY() + gr.getHeight();
					needsResize = true;
				}
			}
		}

		if (needsResize) {
			System.out.println("Resize to " + newDimension);
			setSize(newDimension);
		}

		/*if (needsRelocate || needsResize) {
			for (GraphicalRepresentation child : getContainedGraphicalRepresentations()) {
				if (child instanceof ShapeGraphicalRepresentation) {
					ShapeGraphicalRepresentation c = (ShapeGraphicalRepresentation) child;
					FGEPoint oldLocation = new FGEPoint(c.getX() - deltaX, c.getY() - deltaY);
					c.notifyObjectMoved(oldLocation);
					c.notifyChange(Parameters.x, oldLocation.x, c.getX());
					c.notifyChange(Parameters.y, oldLocation.y, c.getY());
				}
			}
		}*/

	}

	// ********************************************
	// Location management
	// ********************************************

	@Override
	public double getX() {
		return getPropertyValue(ShapeGraphicalRepresentation.X);
	}

	@Override
	public final void setX(double aValue) {
		if (aValue != getX()) {
			FGEPoint newLocation = new FGEPoint(aValue, getY());
			updateLocation(newLocation);
		}
	}

	protected void setXNoNotification(double aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.X, aValue);
	}

	@Override
	public double getY() {
		return getPropertyValue(ShapeGraphicalRepresentation.Y);
	}

	@Override
	public final void setY(double aValue) {
		if (aValue != getY()) {

			FGEPoint newLocation = new FGEPoint(getX(), aValue);
			updateLocation(newLocation);
		}
	}

	protected void setYNoNotification(double aValue) {
		setPropertyValue(ShapeGraphicalRepresentation.Y, aValue);
	}

	/**
	 * General method called to update location of a ShapeNode
	 * 
	 * @param requestedLocation
	 */
	private void updateLocation(FGEPoint requestedLocation) {

		// If no value supplied, just ignore
		if (requestedLocation == null) {
			return;
		}

		// If value is same, also ignore
		if (requestedLocation.equals(getLocation())) {
			return;
		}

		// Prelude of update, first select new location respecting contextual constraints
		FGEPoint newLocation = getConstrainedLocation(requestedLocation);

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
			notifyAttributeChanged(ShapeGraphicalRepresentation.X, oldX, getX());
			notifyAttributeChanged(ShapeGraphicalRepresentation.Y, oldY, getY());
			if (!isFullyContainedInContainer()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("setLocation() lead shape going outside it's parent view");
				}
			}
		}

	}

	/**
	 * Compute and return a constrained location, according to contextual constraints
	 * 
	 * @param requestedLocation
	 * @return a new location respecting all contextual constraints
	 */
	private FGEPoint getConstrainedLocation(FGEPoint requestedLocation) {

		if (isParentLayoutedAsContainer()) {
			return requestedLocation;
		}

		if (!getGraphicalRepresentation().getAllowToLeaveBounds()) {
			requestedLocation = requestedLocation.clone();
			if (requestedLocation.x < 0) {
				requestedLocation.setX(0);
			}
			double maxX = getParentNode().getWidth();
			if (maxX > 0 && requestedLocation.x > maxX - getWidth()) {
				// logger.info("Relocate x from "+x+" to "+(maxX-getWidth())+" maxX="+maxX+" width="+getWidth());
				requestedLocation.setX(maxX - getWidth());
			}
			if (requestedLocation.y < 0) {
				requestedLocation.setY(0);
			}
			double maxY = getParentNode().getHeight();
			if (maxY > 0 && requestedLocation.y > maxY - getHeight()) {
				// logger.info("Relocate x from "+x+" to "+(maxX-getWidth())+" maxX="+maxX+" width="+getWidth());
				requestedLocation.setY(maxY - getHeight());
			}
		}

		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.FREELY_MOVABLE) {
			return requestedLocation.clone();
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.CONTAINED_IN_SHAPE) {
			DrawingTreeNode<?, ?> parent = getParentNode();
			if (parent instanceof ShapeNode) {
				ShapeNode<?> container = (ShapeNode<?>) parent;
				FGEPoint center = new FGEPoint(container.getWidth() / 2, container.getHeight() / 2);
				double authorizedRatio = getMoveAuthorizedRatio(requestedLocation, center);
				return new FGEPoint(center.x + (requestedLocation.x - center.x) * authorizedRatio, center.y
						+ (requestedLocation.y - center.y) * authorizedRatio);
			}
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.AREA_CONSTRAINED) {
			if (getGraphicalRepresentation().getLocationConstrainedArea() == null) {
				// logger.warning("No location constrained are defined");
				return requestedLocation;
			} else {
				return getGraphicalRepresentation().getLocationConstrainedArea().getNearestPoint(requestedLocation);
			}
		}
		return requestedLocation;
	}

	@Override
	public FGEPoint getLocation() {
		return new FGEPoint(getX(), getY());
	}

	@Override
	public void setLocation(FGEPoint newLocation) {
		updateLocation(newLocation);
	}

	@Override
	public FGEPoint getLocationInDrawing() {
		return FGEUtils.convertNormalizedPoint(this, new FGEPoint(0, 0), getDrawing().getRoot());
	}

	private void setLocationNoCheckNorNotification(FGEPoint newLocation) {
		setXNoNotification(newLocation.x);
		setYNoNotification(newLocation.y);
	}

	private void setLocationForContainerLayout(FGEPoint newLocation) {
		if (getParentNode() instanceof ShapeNodeImpl) {
			ShapeNodeImpl<?> container = (ShapeNodeImpl<?>) getParentNode();
			container.updateRequiredBoundsForChildGRLocation(this, newLocation);
		}
	}

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
			if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.AREA_CONSTRAINED && getParentNode() != null) {
				getParentNode().addObserver(this);
				observeParentGRBecauseMyLocationReferToIt = true;
				// logger.info("Start observe my father");
			}
		}
	}

	@Override
	public boolean isFullyContainedInContainer() {
		if (getParentNode() == null || getDrawing() == null) {
			return true;
		}
		boolean isFullyContained = true;
		FGERectangle containerViewBounds = new FGERectangle(0, 0, getParentNode().getViewWidth(1), getParentNode().getViewHeight(1),
				Filling.FILLED);
		for (ControlPoint cp : getShape().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getParentNode(), 1);
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
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getParentNode(), scale);
			FGEPoint preciseCPInContainerView = new FGEPoint(cpInContainerView.x, cpInContainerView.y);
			if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
				// System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
				isFullyContained = false;
			}
		}
		return isFullyContained;
	}

	@Override
	public double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation) {
		if (isParentLayoutedAsContainer()) {
			// This object is contained in a ShapeSpecification acting as container: all locations are valid thus,
			// container will adapt
			return 1;
		}

		double returnedAuthorizedRatio = 1;
		FGERectangle containerViewArea = new FGERectangle(0, 0, getParentNode().getViewWidth(1), getParentNode().getViewHeight(1),
				Filling.FILLED);
		FGERectangle containerViewBounds = new FGERectangle(0, 0, getParentNode().getViewWidth(1), getParentNode().getViewHeight(1),
				Filling.NOT_FILLED);

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
			Point currentCPInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(cp.getPoint(), getParentNode(), 1);
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

	@Override
	public void finalizeConstraints() {
		if (getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isValid()) {
			getGraphicalRepresentation().getXConstraints().decode();
			try {
				setX((Double) TypeUtils.castTo(getGraphicalRepresentation().getXConstraints().getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
		}
		if (getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isValid()) {
			getGraphicalRepresentation().getYConstraints().decode();
			try {
				setY((Double) TypeUtils.castTo(getGraphicalRepresentation().getYConstraints().getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
		}
		if (getGraphicalRepresentation().getWidthConstraints() != null && getGraphicalRepresentation().getWidthConstraints().isValid()) {
			getGraphicalRepresentation().getWidthConstraints().decode();
			try {
				setWidth((Double) TypeUtils.castTo(getGraphicalRepresentation().getWidthConstraints().getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			getGraphicalRepresentation().setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}
		if (getGraphicalRepresentation().getHeightConstraints() != null && getGraphicalRepresentation().getHeightConstraints().isValid()) {
			getGraphicalRepresentation().getHeightConstraints().decode();
			try {
				setHeight((Double) TypeUtils
						.castTo(getGraphicalRepresentation().getHeightConstraints().getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			getGraphicalRepresentation().setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}
	}

	@Override
	protected void computeNewConstraint(ConstraintDependency dependancy) {
		if (dependancy.requiringParameter == ShapeGraphicalRepresentation.X_CONSTRAINTS
				&& getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isValid()) {
			updateXPosition();
		} else if (dependancy.requiringParameter == ShapeGraphicalRepresentation.Y_CONSTRAINTS
				&& getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isValid()) {
			updateYPosition();
		} else if (dependancy.requiringParameter == ShapeGraphicalRepresentation.WIDTH_CONSTRAINTS
				&& getGraphicalRepresentation().getWidthConstraints() != null
				&& getGraphicalRepresentation().getWidthConstraints().isValid()) {
			updateWidthPosition();
		} else if (dependancy.requiringParameter == ShapeGraphicalRepresentation.HEIGHT_CONSTRAINTS
				&& getGraphicalRepresentation().getHeightConstraints() != null
				&& getGraphicalRepresentation().getHeightConstraints().isValid()) {
			updateHeightPosition();
		}
	}

	public void updateConstraints() {
		// System.out.println("updateConstraints() called, valid=" + xConstraints.isValid() + "," + yConstraints.isValid() + ","
		// + widthConstraints.isValid() + "," + heightConstraints.isValid());
		logger.fine("Called updateConstraints(), drawable=" + getDrawable() + " class=" + getClass());
		if (getGraphicalRepresentation().getXConstraints() != null && getGraphicalRepresentation().getXConstraints().isValid()) {
			// System.out.println("x was " + getX() + " constraint=" + xConstraints);
			updateXPosition();
			// System.out.println("x is now " + getX());
		}
		if (getGraphicalRepresentation().getYConstraints() != null && getGraphicalRepresentation().getYConstraints().isValid()) {
			// System.out.println("y was " + getY() + " constraint=" + yConstraints);
			updateYPosition();
			// System.out.println("y is now " + getY());
		}
		if (getGraphicalRepresentation().getWidthConstraints() != null && getGraphicalRepresentation().getWidthConstraints().isValid()) {
			// System.out.println("width was " + getWidth() + " constraint=" + widthConstraints);
			updateWidthPosition();
			// System.out.println("width is now " + getWidth());
		}
		if (getGraphicalRepresentation().getHeightConstraints() != null && getGraphicalRepresentation().getHeightConstraints().isValid()) {
			// System.out.println("height was " + getHeight() + " constraint=" + heightConstraints);
			updateHeightPosition();
			// System.out.println("height is now " + getHeight());
		}

	}

	private void updateXPosition() {
		try {
			Double n = getGraphicalRepresentation().getXConstraints().getBindingValue(this);
			if (n != null) {
				// System.out.println("New value for x is now: " + newValue);
				setX(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void updateYPosition() {
		try {
			Double n = getGraphicalRepresentation().getYConstraints().getBindingValue(this);
			if (n != null) {
				// System.out.println("New value for y is now: " + newValue);
				setY(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void updateWidthPosition() {
		try {
			Double n = getGraphicalRepresentation().getWidthConstraints().getBindingValue(this);
			if (n != null) {
				// System.out.println("New value for width is now: " + newValue);
				setWidth(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void updateHeightPosition() {
		try {
			Double n = getGraphicalRepresentation().getHeightConstraints().getBindingValue(this);
			if (n != null) {
				// System.out.println("New value for height is now: " + newValue);
				setHeight(n.doubleValue());
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g, DianaEditor<?> controller) {
		/*if (!getGraphicalRepresentation().isRegistered()) {
			getGraphicalRepresentation().setRegistered(true);
		}*/
		super.paint(g, controller);

		Graphics2D g2 = (Graphics2D) g;

		if (controller instanceof AbstractDianaEditor<?, ?, ?>) {
			graphics.createGraphics(g2, (AbstractDianaEditor<?, ?, ?>) controller);
			// If there is a decoration painter init its graphics
			if (decorationPainter != null) {
				decorationGraphics.createGraphics(g2, (AbstractDianaEditor<?, ?, ?>) controller);
			}
		} else {
			logger.warning("Unsupported controller: " + controller);
		}

		// If there is a decoration painter and decoration should be painted BEFORE shape, do it now
		if (decorationPainter != null && decorationPainter.paintBeforeShape()) {
			decorationPainter.paintDecoration(decorationGraphics);
		}

		if (FGEConstants.DEBUG) {
			if (getGraphicalRepresentation().getBorder() != null) {
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
				g2.setColor(Color.BLUE);
				g2.drawRect((int) (getGraphicalRepresentation().getBorder().getLeft() * controller.getScale()),
						(int) (getGraphicalRepresentation().getBorder().getTop() * controller.getScale()),
						(int) (getWidth() * controller.getScale()) - 1, (int) (getHeight() * controller.getScale()) - 1);
			} else {
				g2.setColor(Color.BLUE);
				g2.drawRect(0, 0, getViewWidth(controller.getScale()) - 1, getViewHeight(controller.getScale()) - 1);
			}
		}

		if (getGraphicalRepresentation().getShapeSpecification() != null && getGraphicalRepresentation().getShadowStyle() != null) {
			if (getGraphicalRepresentation().getShadowStyle().getDrawShadow()) {
				getShape().paintShadow(graphics);
			}
			getShape().paintShape(graphics);
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
	public ShapeDecorationPainter getDecorationPainter() {
		return decorationPainter;
	}

	@Override
	public void setDecorationPainter(ShapeDecorationPainter aPainter) {
		decorationGraphics = new FGEShapeDecorationGraphicsImpl(this);
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
	public Point getLabelLocation(double scale) {
		Point point;
		if (getGraphicalRepresentation().getIsFloatingLabel()) {
			point = new Point((int) (getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X) * scale + getViewX(scale)),
					(int) (getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y) * scale + getViewY(scale)));
		} else {
			FGEPoint relativePosition = new FGEPoint(getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_X),
					getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_Y));
			point = convertLocalNormalizedPointToRemoteViewCoordinates(relativePosition, getParentNode(), scale);
		}
		Dimension d = getLabelDimension(scale);
		switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
		case CENTER:
			point.x -= d.width / 2;
			break;
		case LEFT:
			break;
		case RIGHT:
			point.x -= d.width;
			break;

		}
		switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
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
		if (getGraphicalRepresentation().getIsFloatingLabel()) {
			Double oldAbsoluteTextX = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X);
			Double oldAbsoluteTextY = getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y);
			Dimension d = getLabelDimension(scale);
			switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
			case CENTER:
				point.x += d.width / 2;
				break;
			case LEFT:
				break;
			case RIGHT:
				point.x += d.width;
				break;

			}
			switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
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
			setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, p.x);
			setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, p.y);
			notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_X, oldAbsoluteTextX,
					getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X));
			notifyAttributeChanged(GraphicalRepresentation.ABSOLUTE_TEXT_Y, oldAbsoluteTextY,
					getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y));
		}
	}

	@Override
	public int getAvailableLabelWidth(double scale) {
		if (getGraphicalRepresentation().getLineWrap()) {
			double rpx = getGraphicalRepresentation().getRelativeTextX();
			switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
			case RIGHT:
				if (GeomUtils.doubleEquals(rpx, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle RIGHT alignement with relative x position set to 0!");
					}
				} else {
					return (int) (getWidth() * rpx * scale);
				}
			case CENTER:
				if (GeomUtils.doubleEquals(rpx, 0.0)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Impossible to handle CENTER alignement with relative x position set to 0");
					}
				} else if (GeomUtils.doubleEquals(rpx, 1.0)) {
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
				if (GeomUtils.doubleEquals(rpx, 1.0)) {
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
	public void addChild(DrawingTreeNode<?, ?> aChildNode) {
		super.addChild(aChildNode);
		if (getGraphicalRepresentation().getAdaptBoundsToContents()) {
			extendBoundsToHostContents();
		}
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
	public void notifyObjectResized(FGEDimension oldSize) {
		getShape().notifyObjectResized();
		super.notifyObjectResized(oldSize);
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
	public List<? extends ControlArea<?>> getControlAreas() {
		return getShape().getControlAreas();
	}

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * @return the area on which the given connector can start
	 */
	@Override
	public FGEArea getAllowedStartAreaForConnector(ConnectorNode<?> connector) {
		return getShape().getOutline();
	}

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	@Override
	public FGEArea getAllowedEndAreaForConnector(ConnectorNode<?> connector) {
		return getShape().getOutline();
	}

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * 
	 * @return the area on which the given connector can start
	 */
	@Override
	public FGEArea getAllowedStartAreaForConnectorForDirection(ConnectorNode<?> connector, FGEArea area,
			SimplifiedCardinalDirection direction) {
		return area;
	}

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	@Override
	public FGEArea getAllowedEndAreaForConnectorForDirection(ConnectorNode<?> connector, FGEArea area, SimplifiedCardinalDirection direction) {
		return area;
	}

	@Override
	public String toString() {
		return "Shape-" + getIndex() + "[" + getX() + ";" + getY() + "][" + getWidth() + "x" + getHeight() + "][" + getFGEShape() + "]:"
				+ getDrawable();
	}

	@Override
	public boolean hasContainedLabel() {
		return hasText() && !getGraphicalRepresentation().getIsFloatingLabel();
	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText() && getGraphicalRepresentation().getIsFloatingLabel();
	}

	@Override
	public FGEDimension getRequiredLabelSize() {
		Dimension normalizedLabelSize = getNormalizedLabelSize();
		int labelWidth = normalizedLabelSize.width;
		int labelHeight = normalizedLabelSize.height;
		double rh = 0, rw = 0;
		FGEPoint rp = new FGEPoint(getGraphicalRepresentation().getRelativeTextX(), getGraphicalRepresentation().getRelativeTextY());
		switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
		case BOTTOM:
			if (GeomUtils.doubleEquals(rp.y, 0.0)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Impossible to handle BOTTOM alignement with relative y position set to 0!");
				}
			} else {
				rh = labelHeight / rp.y;
			}
			break;
		case MIDDLE:
			if (GeomUtils.doubleEquals(rp.y, 0.0)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Impossible to handle MIDDLE alignement with relative y position set to 0");
				}
			} else if (GeomUtils.doubleEquals(rp.y, 1.0)) {
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
			if (GeomUtils.doubleEquals(rp.x, 1.0)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Impossible to handle TOP alignement with relative y position set to 1!");
				}
			} else {
				rh = labelHeight / (1 - rp.y);
			}
			break;

		}

		switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
		case RIGHT:
			if (GeomUtils.doubleEquals(rp.x, 0.0)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Impossible to handle RIGHT alignement with relative x position set to 0!");
				}
			} else {
				rw = labelWidth / rp.x;
			}
		case CENTER:
			if (GeomUtils.doubleEquals(rp.x, 0.0)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Impossible to handle CENTER alignement with relative x position set to 0");
				}
			} else if (GeomUtils.doubleEquals(rp.x, 1.0)) {
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
			if (GeomUtils.doubleEquals(rp.x, 1.0)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Impossible to handle LEFT alignement with relative x position set to 1!");
				}
			} else {
				rw = labelWidth / (1 - rp.x);
			}
			break;
		}

		return new FGEDimension(rw, rh);
	}

	protected void updateRequiredBoundsForChildGRLocation(ShapeNode<?> child, FGEPoint newChildLocation) {
		FGERectangle oldBounds = getBounds();
		FGERectangle newBounds = getRequiredBoundsForChildGRLocation(child, newChildLocation);
		// System.out.println("oldBounds= "+oldBounds+" newBounds="+newBounds);
		double deltaX = -newBounds.x + oldBounds.x;
		double deltaY = -newBounds.y + oldBounds.y;
		AffineTransform translation = AffineTransform.getTranslateInstance(deltaX, deltaY);
		for (DrawingTreeNode childNode : getChildNodes()) {
			if (childNode instanceof ShapeNode) {
				ShapeNodeImpl<?> shapeNode = (ShapeNodeImpl<?>) childNode;
				if (shapeNode == child) {
					FGEPoint newPoint = newChildLocation.transform(translation);
					shapeNode.setXNoNotification(newPoint.x);
					shapeNode.setYNoNotification(newPoint.y);
				} else {
					FGEPoint newPoint = shapeNode.getLocation().transform(translation);
					shapeNode.setXNoNotification(newPoint.x);
					shapeNode.setYNoNotification(newPoint.y);
				}
				shapeNode.notifyObjectMoved();
			}
		}
		setLocation(new FGEPoint(newBounds.x, newBounds.y));
		setSize(new FGEDimension(newBounds.width, newBounds.height));
	}

	protected FGERectangle getRequiredBoundsForChildGRLocation(DrawingTreeNode<?, ?> child, FGEPoint newChildLocation) {
		FGERectangle requiredBounds = null;
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNode shapeGR = (ShapeNode) gr;
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
			requiredBounds = new FGERectangle(getX(), getY(), getGraphicalRepresentation().getMinimalWidth(), getGraphicalRepresentation()
					.getMinimalHeight());
		} else {
			requiredBounds.x = requiredBounds.x + getX();
			requiredBounds.y = requiredBounds.y + getY();
			if (requiredBounds.width < getGraphicalRepresentation().getMinimalWidth()) {
				requiredBounds.x = requiredBounds.x - (int) ((getGraphicalRepresentation().getMinimalWidth() - requiredBounds.width) / 2.0);
				requiredBounds.width = getGraphicalRepresentation().getMinimalWidth();
			}
			if (requiredBounds.height < getGraphicalRepresentation().getMinimalHeight()) {
				requiredBounds.y = requiredBounds.y
						- (int) ((getGraphicalRepresentation().getMinimalHeight() - requiredBounds.height) / 2.0);
				requiredBounds.height = getGraphicalRepresentation().getMinimalHeight();
			}
		}
		return requiredBounds;
	}

	@Override
	public FGERectangle getRequiredBoundsForContents() {
		FGERectangle requiredBounds = super.getRequiredBoundsForContents();

		requiredBounds.x = requiredBounds.x - getGraphicalRepresentation().getBorder().getLeft();
		requiredBounds.y = requiredBounds.y - getGraphicalRepresentation().getBorder().getTop();

		return requiredBounds;
	}

	/**
	 * Calling this method forces FGE to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	@Override
	protected void checkAndUpdateDimensionIfRequired() {
		if (getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONTAINER) {
			List<DrawingTreeNodeImpl<?, ?>> childs = getChildNodes();
			if (childs != null && childs.size() > 0) {
				ShapeNode<?> first = (ShapeNode<?>) childs.get(0);
				updateRequiredBoundsForChildGRLocation(first, first.getLocation());
			}
		} else {
			setSize(getSize());
		}
	}

}
