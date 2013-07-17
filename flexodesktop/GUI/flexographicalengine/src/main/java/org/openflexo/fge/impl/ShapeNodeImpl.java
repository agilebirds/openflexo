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
import org.openflexo.fge.Drawing.ConstraintDependency;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeParameters;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
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

public class ShapeNodeImpl<O> extends DrawingTreeNodeImpl<O, ShapeGraphicalRepresentation> implements ShapeNode<O> {

	private static final Logger logger = Logger.getLogger(ShapeNodeImpl.class.getPackage().getName());

	private double x = 0;
	private double y = 0;
	private double width = 0;
	private double height = 0;

	private boolean isResizing = false;
	private boolean isMoving = false;

	private boolean observeParentGRBecauseMyLocationReferToIt = false;

	private FGEShapeGraphics graphics;
	private FGEShapeDecorationGraphics decorationGraphics;
	private DecorationPainter decorationPainter;
	private ShapePainter shapePainter;

	public ShapeNodeImpl(DrawingImpl<?> drawingImpl, O drawable, GRBinding<O, ShapeGraphicalRepresentation> grBinding,
			DrawingTreeNodeImpl<?, ?> parentNode) {
		super(drawingImpl, drawable, grBinding, parentNode);
		graphics = new FGEShapeGraphics(this);
		width = getGraphicalRepresentation().getMinimalWidth();
		height = getGraphicalRepresentation().getMinimalHeight();
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return super.getGraphicalRepresentation();
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
		return getGraphicalRepresentation().getShape().isPointInsideShape(aPoint);
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
		return (int) (getGraphicalRepresentation().getX() * scale/*-(border!=null?border.left:0)*/);
	}

	@Override
	public int getViewY(double scale) {
		return (int) (getGraphicalRepresentation().getY() * scale/*-(border!=null?border.top:0)*/);
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
	public FGEGraphics getGraphics() {
		return graphics;
	}

	@Override
	public void update(Observable observable, Object notification) {
		// System.out.println("Shape received "+notification+" from "+observable);

		super.update(observable, notification);

		if (notification instanceof FGENotification && observable == getGraphicalRepresentation()) {
			// Those notifications are forwarded by my graphical representation
			FGENotification notif = (FGENotification) notification;

			if (notif.getParameter() == Parameters.text) {
				checkAndUpdateDimensionBoundsIfRequired();
			} else if (notif.getParameter() == Parameters.textStyle) {
				checkAndUpdateDimensionBoundsIfRequired();
			} else if (notif.getParameter() == ShapeParameters.adjustMaximalWidthToLabelWidth
					|| notif.getParameter() == ShapeParameters.adjustMinimalWidthToLabelWidth
					|| notif.getParameter() == ShapeParameters.adjustMaximalHeightToLabelHeight
					|| notif.getParameter() == ShapeParameters.adjustMinimalHeightToLabelHeight) {
				checkAndUpdateDimensionBoundsIfRequired();
			} else if (notif.getParameter() == ShapeParameters.width || notif.getParameter() == ShapeParameters.height
					|| notif.getParameter() == ShapeParameters.minimalWidth || notif.getParameter() == ShapeParameters.minimalHeight
					|| notif.getParameter() == ShapeParameters.maximalWidth || notif.getParameter() == ShapeParameters.maximalHeight) {
				checkAndUpdateDimensionBoundsIfRequired();
			} else if (notif.getParameter() == Parameters.horizontalTextAlignment
					|| notif.getParameter() == Parameters.verticalTextAlignment) {
				checkAndUpdateDimensionBoundsIfRequired();
			} else if (notif.getParameter() == ShapeParameters.locationConstraints
					|| notif.getParameter() == ShapeParameters.locationConstrainedArea
					|| notif.getParameter() == ShapeParameters.dimensionConstraintStep) {
				checkAndUpdateLocationIfRequired();
				getGraphicalRepresentation().getShape().rebuildControlPoints();
			} else if (notif.getParameter() == ShapeParameters.adaptBoundsToContents) {
				extendBoundsToHostContents();
			} else if (notif.getParameter() == ShapeParameters.border) {
				notifyObjectResized();
			} else if (notif.getParameter() == ShapeParameters.shape || notif.getParameter() == ShapeParameters.shapeType) {
				getGraphicalRepresentation().getShape().rebuildControlPoints();
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
			notifyAttributeChanged(ShapeParameters.background, null, getGraphicalRepresentation().getBackground());
		}
		if (observable instanceof ForegroundStyle) {
			notifyAttributeChanged(ShapeParameters.foreground, null, getGraphicalRepresentation().getForeground());
		}
		if (observable instanceof ShadowStyle) {
			notifyAttributeChanged(ShapeParameters.shadowStyle, null, getGraphicalRepresentation().getShadowStyle());
		}
	}

	@Override
	public void extendParentBoundsToHostThisShape() {
		if (getParentNode() instanceof ShapeNode) {
			ShapeNode parent = (ShapeNode) getParentNode();
			parent.extendBoundsToHostContents();
		}
	}

	/*public void extendParentBoundsToHostThisShape2() {
		// System.out.println("parent=" + getParentGraphicalRepresentation());
		if (getParentGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation parent = (ShapeGraphicalRepresentation) getParentGraphicalRepresentation();

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
				for (GraphicalRepresentation child : parent.getContainedGraphicalRepresentations()) {
					if (child instanceof ShapeGraphicalRepresentation && child != this) {
						ShapeGraphicalRepresentation c = (ShapeGraphicalRepresentation) child;
						c.setLocation(new FGEPoint(c.getX() + deltaX, c.getY() + deltaY));
					}
				}
			}

			if (parentNeedsRelocate || parentNeedsResize) {
				FGEPoint oldLocation = new FGEPoint(x, y);
				notifyObjectMoved(oldLocation);
				notifyAttributeChanged(ShapeParameters.x, oldLocation.x, getX());
				notifyAttributeChanged(ShapeParameters.y, oldLocation.y, getY());
			}
		}
	}*/

	/**
	 * Check and eventually relocate and resize current graphical representation in order to all all contained shape graphical
	 * representations. Contained graphical representations may substantically be relocated.
	 */
	@Override
	public void extendBoundsToHostContents() {

		// logger.info("Hop: extendBoundsToHostContents");

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

	@Override
	public double getX() {
		// SGU: in general case, this is NOT forbidden
		if (!getGraphicalRepresentation().getAllowToLeaveBounds()) {
			if (x < 0) {
				return 0;
			}
			double maxX = 0;
			if (getParentNode() instanceof RootNode) {
				maxX = ((RootNode) getParentNode()).getWidth();
			} else if (getParentNode() instanceof ShapeNode) {
				maxX = ((ShapeNode) getParentNode()).getWidth();
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
		if (aValue != x) {
			FGEPoint oldLocation = getLocation();
			setXNoNotification(aValue);
			notifyObjectMoved(oldLocation);
		}
	}

	protected void setXNoNotification(double aValue) {
		x = aValue;
	}

	@Override
	public double getY() {
		// SGU: in general case, this is NOT forbidden
		if (!getGraphicalRepresentation().getAllowToLeaveBounds()) {
			if (y < 0) {
				return 0;
			}
			double maxY = 0;
			if (getParentNode() instanceof RootNode) {
				maxY = ((RootNode) getParentNode()).getHeight();
			} else if (getParentNode() instanceof ShapeNode) {
				maxY = ((ShapeNode) getParentNode()).getHeight();
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
		if (aValue != y) {
			FGEPoint oldLocation = getLocation();
			setYNoNotification(aValue);
			notifyObjectMoved(oldLocation);
		}
	}

	protected void setYNoNotification(double aValue) {
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
			notifyAttributeChanged(ShapeParameters.x, oldX, getX());
			notifyAttributeChanged(ShapeParameters.y, oldY, getY());
			if (!isFullyContainedInContainer()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("setLocation() lead shape going outside it's parent view");
				}
			}
		}
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

	private void updateRequiredBoundsForChildGRLocation(ShapeNode<?> child, FGEPoint newChildLocation) {
		FGERectangle oldBounds = getBounds();
		FGERectangle newBounds = getRequiredBoundsForChildGRLocation(child, newChildLocation);
		// System.out.println("oldBounds= "+oldBounds+" newBounds="+newBounds);
		double deltaX = -newBounds.x + oldBounds.x;
		double deltaY = -newBounds.y + oldBounds.y;
		AffineTransform translation = AffineTransform.getTranslateInstance(deltaX, deltaY);
		for (DrawingTreeNode gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNodeImpl<?> shapeGR = (ShapeNodeImpl<?>) gr;
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

	@Override
	public FGERectangle getRequiredBoundsForContents() {
		FGERectangle requiredBounds = null;
		if (getChildNodes() == null) {
			return new FGERectangle(getGraphicalRepresentation().getMinimalWidth() / 2,
					getGraphicalRepresentation().getMinimalHeight() / 2, getGraphicalRepresentation().getMinimalWidth(),
					getGraphicalRepresentation().getMinimalHeight());
		}
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNodeImpl<?> shapeGR = (ShapeNodeImpl<?>) gr;
				FGERectangle bounds = shapeGR.getBoundsNoBorder();
				if (shapeGR.getGraphicalRepresentation().hasText()) {
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
			requiredBounds = new FGERectangle(getGraphicalRepresentation().getMinimalWidth() / 2, getGraphicalRepresentation()
					.getMinimalHeight() / 2, getGraphicalRepresentation().getMinimalWidth(), getGraphicalRepresentation()
					.getMinimalHeight());
		} else {
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

		requiredBounds.x = requiredBounds.x - getGraphicalRepresentation().getBorder().getLeft();
		requiredBounds.y = requiredBounds.y - getGraphicalRepresentation().getBorder().getTop();

		return requiredBounds;
	}

	private FGERectangle getRequiredBoundsForChildGRLocation(DrawingTreeNode<?, ?> child, FGEPoint newChildLocation) {
		FGERectangle requiredBounds = null;
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNode) {
				ShapeNode shapeGR = (ShapeNode) gr;
				FGERectangle bounds = shapeGR.getBounds();
				if (shapeGR == child) {
					bounds.x = newChildLocation.x;
					bounds.y = newChildLocation.y;
				}
				if (shapeGR.getGraphicalRepresentation().hasText()) {
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

	protected FGEPoint computeConstrainedLocation(FGEPoint newLocation) {
		if (isParentLayoutedAsContainer()) {
			return newLocation;
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.FREELY_MOVABLE) {
			return newLocation.clone();
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.CONTAINED_IN_SHAPE) {
			DrawingTreeNode<?, ?> parent = getParentNode();
			if (parent instanceof ShapeNode) {
				ShapeNode container = (ShapeNode) parent;
				FGEPoint center = new FGEPoint(container.getWidth() / 2, container.getHeight() / 2);
				double authorizedRatio = getMoveAuthorizedRatio(newLocation, center);
				return new FGEPoint(center.x + (newLocation.x - center.x) * authorizedRatio, center.y + (newLocation.y - center.y)
						* authorizedRatio);
			}
		}
		if (getGraphicalRepresentation().getLocationConstraints() == LocationConstraints.AREA_CONSTRAINED) {
			if (getGraphicalRepresentation().getLocationConstrainedArea() == null) {
				// logger.warning("No location constrained are defined");
				return newLocation;
			} else {
				return getGraphicalRepresentation().getLocationConstrainedArea().getNearestPoint(newLocation);
			}
		}
		return newLocation;
	}

	@Override
	public boolean isFullyContainedInContainer() {
		if (getParentNode() == null || getDrawing() == null) {
			return true;
		}
		boolean isFullyContained = true;
		FGERectangle containerViewBounds = new FGERectangle(0, 0, getParentNode().getViewWidth(1), getParentNode().getViewHeight(1),
				Filling.FILLED);
		for (ControlPoint cp : getGraphicalRepresentation().getShape().getControlPoints()) {
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
		for (ControlPoint cp : getGraphicalRepresentation().getShape().getControlPoints()) {
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
	public boolean isParentLayoutedAsContainer() {
		return getParentNode() != null
				&& getParentNode() instanceof ShapeNode
				&& ((ShapeNode<?>) getParentNode()).getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONTAINER;
	}

	@Override
	public double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation) {
		if (isParentLayoutedAsContainer()) {
			// This object is contained in a Shape acting as container: all locations are valid thus,
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
		for (ControlPoint cp : getGraphicalRepresentation().getShape().getControlPoints()) {
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
	public double getWidth() {
		return width;
	}

	@Override
	public final void setWidth(double aValue) {
		if (aValue != width) {
			FGEDimension oldSize = getSize();
			setWidthNoNotification(aValue);
			notifyObjectResized(oldSize);
		}
	}

	protected void setWidthNoNotification(double aValue) {
		width = aValue;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public final void setHeight(double aValue) {
		if (aValue != height) {
			FGEDimension oldSize = getSize();
			setHeightNoNotification(aValue);
			notifyObjectResized(oldSize);
		}
	}

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
			if (getGraphicalRepresentation().hasFloatingLabel()) {
				if (getGraphicalRepresentation().getAbsoluteTextX() >= 0) {
					if (getGraphicalRepresentation().getAbsoluteTextX() < getWidth()) {
						getGraphicalRepresentation().setAbsoluteTextX(
								getGraphicalRepresentation().getAbsoluteTextX() / oldSize.width * getWidth());
					} else {
						getGraphicalRepresentation().setAbsoluteTextX(
								getGraphicalRepresentation().getAbsoluteTextX() + getWidth() - oldSize.width);
					}
				}
				if (getGraphicalRepresentation().getAbsoluteTextY() >= 0) {
					if (getGraphicalRepresentation().getAbsoluteTextY() < getHeight()) {
						getGraphicalRepresentation().setAbsoluteTextY(
								getGraphicalRepresentation().getAbsoluteTextY() / oldSize.height * getHeight());
					} else {
						getGraphicalRepresentation().setAbsoluteTextY(
								getGraphicalRepresentation().getAbsoluteTextY() + getHeight() - oldSize.height);
					}
				}
			}
			checkAndUpdateDimensionBoundsIfRequired();
			if (isParentLayoutedAsContainer()) {
				((ShapeNodeImpl<?>) getParentNode()).checkAndUpdateDimensionIfRequired();
			}
			notifyObjectResized(oldSize);
			notifyAttributeChanged(ShapeParameters.width, oldWidth, getWidth());
			notifyAttributeChanged(ShapeParameters.height, oldHeight, getHeight());
			getGraphicalRepresentation().getShape().notifyObjectResized();
		}
	}

	/**
	 * Calling this method forces FGE to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	protected void checkAndUpdateDimensionIfRequired() {
		if (getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.CONTAINER) {
			List<DrawingTreeNodeImpl<?, ?>> childs = getChildNodes();
			if (childs != null && childs.size() > 0) {
				ShapeNode<?> first = (ShapeNode<?>) childs.get(0);
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
	public double getRequiredHeight(double labelHeight) {
		return labelHeight;
	}

	private boolean isCheckingDimensionConstraints = false;

	private void checkAndUpdateDimensionBoundsIfRequired() {

		if (isCheckingDimensionConstraints || labelMetricsProvider == null) {
			return;
		}

		try {

			// if (getAdaptBoundsToContents()) {
			// extendBoundsToHostContents();
			// }

			isCheckingDimensionConstraints = true;

			// FGERectangle requiredBounds = getRequiredBoundsForContents();

			boolean changed = false;
			FGEDimension newDimension = getSize();
			// double minWidth = (getAdaptBoundsToContents() ? Math.max(getMinimalWidth(), requiredBounds.width) : getMinimalWidth());
			// double minHeight = (getAdaptBoundsToContents() ? Math.max(getMinimalHeight(), requiredBounds.height) : getMinimalHeight());
			double minWidth = getGraphicalRepresentation().getMinimalWidth();
			double minHeight = getGraphicalRepresentation().getMinimalHeight();
			double maxWidth = getGraphicalRepresentation().getMaximalWidth();
			double maxHeight = getGraphicalRepresentation().getMaximalHeight();
			if (getGraphicalRepresentation().hasText() && !getGraphicalRepresentation().getIsFloatingLabel()) {
				Dimension normalizedLabelSize = getNormalizedLabelSize();
				int labelWidth = normalizedLabelSize.width;
				int labelHeight = normalizedLabelSize.height;
				double requiredWidth = getRequiredWidth(labelWidth);
				double requiredHeight = getRequiredHeight(labelHeight);
				double rh = 0, rw = 0;
				FGEPoint rp = new FGEPoint(getGraphicalRepresentation().getRelativeTextX(), getGraphicalRepresentation().getRelativeTextY());
				switch (getGraphicalRepresentation().getVerticalTextAlignment()) {
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

				switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
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

				if (getGraphicalRepresentation().getAdjustMinimalWidthToLabelWidth()) {
					minWidth = Math.max(requiredWidth, minWidth);
					if (getWidth() < minWidth) {
						newDimension.width = minWidth;
						changed = true;
					}
				}

				if (getGraphicalRepresentation().getAdjustMinimalHeightToLabelHeight()) {
					minHeight = Math.max(requiredHeight, minHeight);
					if (getHeight() < minHeight) {
						newDimension.height = minHeight;
						changed = true;
					}
				}

				if (getGraphicalRepresentation().getAdjustMaximalWidthToLabelWidth()) {
					maxWidth = Math.min(requiredWidth, maxWidth);
					if (getWidth() > maxWidth) {
						newDimension.width = maxWidth;
						changed = true;
					}
				}

				if (getGraphicalRepresentation().getAdjustMaximalHeightToLabelHeight()) {
					maxHeight = Math.min(requiredHeight, maxHeight);
					if (getHeight() > maxHeight) {
						newDimension.height = maxHeight;
						changed = true;
					}
				}
			}
			if (getGraphicalRepresentation().getMinimalWidth() > getGraphicalRepresentation().getMaximalWidth()) {
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
			if (getGraphicalRepresentation().getMinimalHeight() > getGraphicalRepresentation().getMaximalHeight()) {
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
			boolean useStepDimensionConstraints = getGraphicalRepresentation().getDimensionConstraints() == DimensionConstraints.STEP_CONSTRAINED
					&& getGraphicalRepresentation().getDimensionConstraintStep() != null;
			if (useStepDimensionConstraints && getGraphicalRepresentation().hasText() && !getGraphicalRepresentation().getIsFloatingLabel()) {
				if (getGraphicalRepresentation().getAdjustMinimalWidthToLabelWidth()
						&& getGraphicalRepresentation().getAdjustMaximalWidthToLabelWidth()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on width! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
				if (getGraphicalRepresentation().getAdjustMinimalHeightToLabelHeight()
						&& getGraphicalRepresentation().getAdjustMaximalHeightToLabelHeight()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Too many constraints on height! Cannot proceed.");
					}
					useStepDimensionConstraints = false;
				}
			}

			if (useStepDimensionConstraints) {
				FGEDimension d = getGraphicalRepresentation().getDimensionConstraintStep().getNearestDimension(newDimension, minWidth,
						maxWidth, minHeight, maxHeight);
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
		if (dependancy.requiringParameter == ShapeParameters.xConstraints && getGraphicalRepresentation().getXConstraints() != null
				&& getGraphicalRepresentation().getXConstraints().isValid()) {
			updateXPosition();
		} else if (dependancy.requiringParameter == ShapeParameters.yConstraints && getGraphicalRepresentation().getYConstraints() != null
				&& getGraphicalRepresentation().getYConstraints().isValid()) {
			updateYPosition();
		} else if (dependancy.requiringParameter == ShapeParameters.widthConstraints
				&& getGraphicalRepresentation().getWidthConstraints() != null
				&& getGraphicalRepresentation().getWidthConstraints().isValid()) {
			updateWidthPosition();
		} else if (dependancy.requiringParameter == ShapeParameters.heightConstraints
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
	public void paint(Graphics g, DrawingController<?> controller) {
		/*if (!getGraphicalRepresentation().isRegistered()) {
			getGraphicalRepresentation().setRegistered(true);
		}*/
		super.paint(g, controller);

		Graphics2D g2 = (Graphics2D) g;
		graphics.createGraphics(g2, controller);

		// If there is a decoration painter init its graphics
		if (decorationPainter != null) {
			decorationGraphics.createGraphics(g2, controller);
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

		if (getGraphicalRepresentation().getShape() != null && getGraphicalRepresentation().getShadowStyle() != null) {
			if (getGraphicalRepresentation().getShadowStyle().getDrawShadow()) {
				getGraphicalRepresentation().getShape().paintShadow(graphics);
			}
			getGraphicalRepresentation().getShape().paintShape(graphics);
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
	public Point getLabelLocation(double scale) {
		Point point;
		if (getGraphicalRepresentation().getIsFloatingLabel()) {
			point = new Point((int) (getGraphicalRepresentation().getAbsoluteTextX() * scale + getViewX(scale)),
					(int) (getGraphicalRepresentation().getAbsoluteTextY() * scale + getViewY(scale)));
		} else {
			FGEPoint relativePosition = new FGEPoint(getGraphicalRepresentation().getRelativeTextX(), getGraphicalRepresentation()
					.getRelativeTextY());
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
			getGraphicalRepresentation().setAbsoluteTextX(p.x);
			getGraphicalRepresentation().setAbsoluteTextY(p.y);
		}
	}

	@Override
	public int getAvailableLabelWidth(double scale) {
		if (getGraphicalRepresentation().getLineWrap()) {
			double rpx = getGraphicalRepresentation().getRelativeTextX();
			switch (getGraphicalRepresentation().getHorizontalTextAlignment()) {
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
	protected void addChild(DrawingTreeNodeImpl<?, ?> aChildNode) {
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
	public void notifyObjectResized() {
		notifyObjectResized(null);
	}

	/**
	 * Notify that the object just resized
	 */
	@Override
	public void notifyObjectResized(FGEDimension oldSize) {
		if (getGraphicalRepresentation().getShape() != null) {
			getGraphicalRepresentation().getShape().updateShape();
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
		for (DrawingTreeNode<?, ?> gr : getChildNodes()) {
			if (gr instanceof ShapeNodeImpl) {
				((ShapeNodeImpl<?>) gr).checkAndUpdateLocationIfRequired();
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
	public void notifyShapeChanged() {
		setChanged();
		notifyObservers(new ShapeChanged());
	}

	@Override
	public void notifyShapeNeedsToBeRedrawn() {
		setChanged();
		notifyObservers(new ShapeNeedsToBeRedrawn());
	}
}
