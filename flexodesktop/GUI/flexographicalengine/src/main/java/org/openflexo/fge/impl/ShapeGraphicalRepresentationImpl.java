package org.openflexo.fge.impl;

import java.awt.Color;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.controller.MouseDragControlAction.MouseDragControlActionType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.toolbox.ToolBox;

public class ShapeGraphicalRepresentationImpl extends ContainerGraphicalRepresentationImpl implements ShapeGraphicalRepresentation {

	private static final Logger logger = Logger.getLogger(ShapeGraphicalRepresentation.class.getPackage().getName());

	private double x = 0;
	private double y = 0;

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

	private ShapeBorder border = new ShapeBorderImpl();

	private ShapeSpecification shape = null;

	private ShadowStyle shadowStyle;

	private boolean allowToLeaveBounds = true;
	private boolean adaptBoundsToContents = false;

	/*private boolean drawShadow = true;
	private int shadowDarkness = FGEConstants.DEFAULT_SHADOW_DARKNESS;
	private int shadowDeep = FGEConstants.DEFAULT_SHADOW_DEEP;
	private int shadowBlur = FGEConstants.DEFAULT_SHADOW_BLUR;*/

	private boolean isFloatingLabel = true;
	private double relativeTextX = 0.5;
	private double relativeTextY = 0.5;

	// private boolean isResizing = false;
	// private boolean isMoving = false;

	// private FGEShapeGraphics graphics;
	// private FGEShapeDecorationGraphics decorationGraphics;
	// private DecorationPainter decorationPainter;
	// private ShapePainter shapePainter;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeGraphicalRepresentationImpl() {
		super();
		// graphics = new FGEShapeGraphics(this);
	}

	@Deprecated
	private ShapeGraphicalRepresentationImpl(Object aDrawable, Drawing<?> aDrawing) {
		this();
		// setDrawable(aDrawable);
		setDrawing(aDrawing);
	}

	@Deprecated
	public ShapeGraphicalRepresentationImpl(ShapeType shapeType, Object aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);
		setShapeType(shapeType);
		layer = FGEConstants.DEFAULT_SHAPE_LAYER;
		foreground = getFactory().makeDefaultForegroundStyle();
		// foreground.setGraphicalRepresentation(this);
		foreground.addObserver(this);
		background = getFactory().makeColoredBackground(Color.WHITE);
		// background.setGraphicalRepresentation(this);
		background.addObserver(this);
		shadowStyle = getFactory().makeDefaultShadowStyle();
		// background.setGraphicalRepresentation(this);
		shadowStyle.addObserver(this);

		// graphics = new FGEShapeGraphics(this);

		init();
	}

	@Deprecated
	private ShapeGraphicalRepresentationImpl(ShapeGraphicalRepresentation aGR, Object aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);

		setsWith(aGR);
		init();
	}

	@Deprecated
	private void init() {

		// graphics = new FGEShapeGraphics(this);

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
	public ShapeGraphicalRepresentationImpl clone() {
		// logger.info("La GR "+this+" se fait cloner la");
		try {
			return (ShapeGraphicalRepresentationImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	/*@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = super.getAllParameters();
		Parameters[] allParams = Parameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}*/

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

	/*@Override
	public final void setsWith(GraphicalRepresentation gr) {
		super.setsWith(gr);
		if (gr instanceof ShapeGraphicalRepresentation) {
			for (ShapeParameters p : ShapeParameters.values()) {
				if (p != ShapeParameters.shape && p != ShapeParameters.shapeType) {
					_setParameterValueWith(p, gr);
				}
			}
			ShapeSpecification shapeToCopy = ((ShapeGraphicalRepresentation) gr).getShape();
			ShapeSpecification clone = shapeToCopy.clone();
			setShape(clone);
		}
	}

	@Override
	public final void setsWith(GraphicalRepresentation gr, GRParameter... exceptedParameters) {
		super.setsWith(gr, exceptedParameters);
		if (gr instanceof ShapeGraphicalRepresentation) {
			for (ShapeParameters p : ShapeParameters.values()) {
				boolean excepted = false;
				for (GRParameter ep : exceptedParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (p != ShapeParameters.shape && p != ShapeParameters.shapeType && !excepted) {
					_setParameterValueWith(p, gr);
				}
			}
			ShapeSpecification shapeToCopy = ((ShapeGraphicalRepresentation) gr).getShape();
			ShapeSpecification clone = shapeToCopy.clone();
			setShape(clone);
		}
	}*/

	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	// This might be very dangerous to override this (this has been done in the past)
	// SGU: Developer really need to think about what he's doing while overriding this
	@Override
	public void update(Observable observable, Object notification) {
		// System.out.println("ShapeSpecification received "+notification+" from "+observable);

		super.update(observable, notification);

		/*if (observeParentGRBecauseMyLocationReferToIt) {
			if (notification instanceof ObjectWillMove || notification instanceof ObjectWillResize
					|| notification instanceof ObjectHasMoved || notification instanceof ObjectHasResized
					|| notification instanceof ObjectMove || notification instanceof ObjectResized || notification instanceof ShapeChanged) {
				checkAndUpdateLocationIfRequired();
			}
		}*/

		if (notification instanceof ShapeChanged) {
			// Reforward notification
			notifyShapeChanged();
		}

		if (notification instanceof ShapeNeedsToBeRedrawn) {
			// Reforward notification
			notifyShapeNeedsToBeRedrawn();
		}

		if (observable instanceof BackgroundStyle) {
			notifyAttributeChange(BACKGROUND);
		}
		if (observable instanceof ForegroundStyle) {
			notifyAttributeChange(FOREGROUND);
		}
		if (observable instanceof ShadowStyle) {
			notifyAttributeChange(SHADOW_STYLE);
		}
	}

	// *******************************************************************************
	// * Location management *
	// *******************************************************************************

	/*@Override
	public void extendParentBoundsToHostThisShape() {
		if (getParentGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation parent = (ShapeGraphicalRepresentation) getParentGraphicalRepresentation();
			parent.extendBoundsToHostContents();
		}
	}*/

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
				notifyChange(ShapeParameters.x, oldLocation.x, getX());
				notifyChange(ShapeParameters.y, oldLocation.y, getY());
			}
		}
	}*/

	/**
	 * Check and eventually relocate and resize current graphical representation in order to all all contained shape graphical
	 * representations. Contained graphical representations may substantically be relocated.
	 */
	/*@Override
	public void extendBoundsToHostContents() {

		// logger.info("Hop: extendBoundsToHostContents");

		boolean needsResize = false;
		FGEDimension newDimension = new FGEDimension(getWidth(), getHeight());
		boolean needsRelocate = false;
		FGEPoint newPosition = new FGEPoint(getX(), getY());
		double deltaX = 0;
		double deltaY = 0;

		// First compute the delta to be applied (max of all required delta)
		for (GraphicalRepresentation child : getContainedGraphicalRepresentations()) {
			if (child instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation gr = (ShapeGraphicalRepresentation) child;
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
			for (GraphicalRepresentation child : getContainedGraphicalRepresentations()) {
				if (child instanceof ShapeGraphicalRepresentation && child != this) {
					ShapeGraphicalRepresentation c = (ShapeGraphicalRepresentation) child;
					c.setLocation(new FGEPoint(c.getX() + deltaX, c.getY() + deltaY));
				}
			}
		}

		// First compute the resize to be applied
		for (GraphicalRepresentation child : getContainedGraphicalRepresentations()) {
			if (child instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation gr = (ShapeGraphicalRepresentation) child;
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


	}*/

	@Override
	public double getX() {
		// SGU: in general case, this is NOT forbidden
		/*if (!getAllowToLeaveBounds()) {
			if (x < 0) {
				return 0;
			}
			double maxX = 0;
			if (getContainerGraphicalRepresentation() instanceof DrawingGraphicalRepresentation) {
				maxX = ((DrawingGraphicalRepresentation) getContainerGraphicalRepresentation()).getWidth();
			} else if (getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				maxX = ((ShapeGraphicalRepresentation) getContainerGraphicalRepresentation()).getWidth();
			}
			if (maxX > 0 && x > maxX - getWidth()) {
				// logger.info("Relocate x from "+x+" to "+(maxX-getWidth())+" maxX="+maxX+" width="+getWidth());
				return maxX - getWidth();
			}
		}*/
		return x;
	}

	@Override
	public final void setX(double aValue) {
		FGENotification notification = requireChange(X, aValue);
		if (notification != null) {
			FGEPoint oldLocation = getLocation();
			x = aValue;
			hasChanged(notification);
			// notifyObjectMoved(oldLocation);
		}
	}

	/*public void setXNoNotification(double aValue) {
		x = aValue;
	}*/

	@Override
	public double getY() {
		// SGU: in general case, this is NOT forbidden
		/*if (!getAllowToLeaveBounds()) {
			if (y < 0) {
				return 0;
			}
			double maxY = 0;
			if (getContainerGraphicalRepresentation() instanceof DrawingGraphicalRepresentation) {
				maxY = ((DrawingGraphicalRepresentation) getContainerGraphicalRepresentation()).getHeight();
			} else if (getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				maxY = ((ShapeGraphicalRepresentation) getContainerGraphicalRepresentation()).getHeight();
			}
			if (maxY > 0 && y > maxY - getHeight()) {
				// logger.info("Relocate y from " + y + " to " + (maxY - getHeight()) + " maxY=" + maxY + " height=" + getHeight());
				return maxY - getHeight();
			}
		}*/
		return y;
	}

	@Override
	public final void setY(double aValue) {
		FGENotification notification = requireChange(Y, aValue);
		if (notification != null) {
			FGEPoint oldLocation = getLocation();
			y = aValue;
			hasChanged(notification);
			// notifyObjectMoved(oldLocation);
		}
	}

	/*public void setYNoNotification(double aValue) {
		y = aValue;
	}*/

	@Override
	public FGEPoint getLocation() {
		return new FGEPoint(getX(), getY());
	}

	/*@Override
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
	}*/

	/*@Override
	public FGEPoint getLocationInDrawing() {
		return FGEUtils.convertNormalizedPoint(this, new FGEPoint(0, 0), getDrawingGraphicalRepresentation());
	}*/

	/*@Override
	public void setAbsoluteTextX(double absoluteTextX)
	{
		super.setAbsoluteTextX(absoluteTextX);
		if (isParentLayoutedAsContainer()) {
			ShapeGraphicalRepresentation container = (ShapeGraphicalRepresentation)getContainerGraphicalRepresentation();
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

	/*private void setLocationNoCheckNorNotification(FGEPoint newLocation) {
		setXNoNotification(newLocation.x);
		setYNoNotification(newLocation.y);
	}*/

	/*private void setLocationForContainerLayout(FGEPoint newLocation) {
		ShapeGraphicalRepresentationImpl<?> container = (ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation();
		container.updateRequiredBoundsForChildGRLocation(this, newLocation);
	}*/

	/*private void updateRequiredBoundsForChildGRLocation(ShapeGraphicalRepresentation child, FGEPoint newChildLocation) {
		FGERectangle oldBounds = getBounds();
		FGERectangle newBounds = getRequiredBoundsForChildGRLocation(child, newChildLocation);
		// System.out.println("oldBounds= "+oldBounds+" newBounds="+newBounds);
		double deltaX = -newBounds.x + oldBounds.x;
		double deltaY = -newBounds.y + oldBounds.y;
		AffineTransform translation = AffineTransform.getTranslateInstance(deltaX, deltaY);
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
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
	}*/

	/*@Override
	public Dimension getNormalizedLabelSize() {
		if (labelMetricsProvider != null) {
			return labelMetricsProvider.getScaledPreferredDimension(1.0);
		} else {
			return new Dimension(0, 0);
		}
	}*/

	/*@Override
	public Rectangle getNormalizedLabelBounds() {
		Dimension normalizedLabelSize = getNormalizedLabelSize();
		Rectangle r = new Rectangle(getLabelLocation(1.0), normalizedLabelSize);
		return r;
	}*/

	/*@Override
	public FGERectangle getRequiredBoundsForContents() {
		FGERectangle requiredBounds = null;
		if (getContainedGraphicalRepresentations() == null) {
			return new FGERectangle(getMinimalWidth() / 2, getMinimalHeight() / 2, getMinimalWidth(), getMinimalHeight());
		}
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentationImpl<?> shapeGR = (ShapeGraphicalRepresentationImpl<?>) gr;
				FGERectangle bounds = shapeGR.getBoundsNoBorder();
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
			requiredBounds = new FGERectangle(getMinimalWidth() / 2, getMinimalHeight() / 2, getMinimalWidth(), getMinimalHeight());
		} else {
			if (requiredBounds.width < getMinimalWidth()) {
				requiredBounds.x = requiredBounds.x - (int) ((getMinimalWidth() - requiredBounds.width) / 2.0);
				requiredBounds.width = getMinimalWidth();
			}
			if (requiredBounds.height < getMinimalHeight()) {
				requiredBounds.y = requiredBounds.y - (int) ((getMinimalHeight() - requiredBounds.height) / 2.0);
				requiredBounds.height = getMinimalHeight();
			}
		}

		requiredBounds.x = requiredBounds.x - getBorder().getLeft();
		requiredBounds.y = requiredBounds.y - getBorder().getTop();

		return requiredBounds;
	}*/

	/*private FGERectangle getRequiredBoundsForChildGRLocation(GraphicalRepresentation child, FGEPoint newChildLocation) {
		FGERectangle requiredBounds = null;
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation shapeGR = (ShapeGraphicalRepresentation) gr;
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
	}*/

	@Override
	public LocationConstraints getLocationConstraints() {
		return locationConstraints;
	}

	@Override
	public void setLocationConstraints(LocationConstraints locationConstraints) {
		FGENotification notification = requireChange(LOCATION_CONSTRAINTS, locationConstraints);
		if (notification != null) {
			this.locationConstraints = locationConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public FGEArea getLocationConstrainedArea() {
		return locationConstrainedArea;
	}

	@Override
	public void setLocationConstrainedArea(FGEArea locationConstrainedArea) {
		FGENotification notification = requireChange(LOCATION_CONSTRAINED_AREA, locationConstrainedArea);
		if (notification != null) {
			this.locationConstrainedArea = locationConstrainedArea;
			hasChanged(notification);
		}
	}

	// private boolean observeParentGRBecauseMyLocationReferToIt = false;

	/**
	 * Calling this method forces FGE to check (and eventually update) location of current graphical representation according defined
	 * location constraints
	 */
	/*protected void checkAndUpdateLocationIfRequired() {
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
	}*/

	/*protected FGEPoint computeConstrainedLocation(FGEPoint newLocation) {
		if (isParentLayoutedAsContainer()) {
			return newLocation;
		}
		if (getLocationConstraints() == LocationConstraints.FREELY_MOVABLE) {
			return newLocation.clone();
		}
		if (getLocationConstraints() == LocationConstraints.CONTAINED_IN_SHAPE) {
			GraphicalRepresentation parent = getContainerGraphicalRepresentation();
			if (parent instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation container = (ShapeGraphicalRepresentation) parent;
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
	}*/

	/*@Override
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
	}*/

	/*@Override
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
	}*/

	/*@Override
	public boolean isParentLayoutedAsContainer() {
		return getContainerGraphicalRepresentation() != null
				&& getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentation
				&& ((ShapeGraphicalRepresentation) getContainerGraphicalRepresentation()).getDimensionConstraints() == DimensionConstraints.CONTAINER;
	}*/

	/*@Override
	public double getMoveAuthorizedRatio(FGEPoint desiredLocation, FGEPoint initialLocation) {
		if (isParentLayoutedAsContainer()) {
			// This object is contained in a ShapeSpecification acting as container: all locations are valid thus,
			// container will adapt
			return 1;
		}

		double returnedAuthorizedRatio = 1;
		FGERectangle containerViewArea = new FGERectangle(0, 0, getContainerGraphicalRepresentation().getViewWidth(1),
				getContainerGraphicalRepresentation().getViewHeight(1), Filling.FILLED);
		FGERectangle containerViewBounds = new FGERectangle(0, 0, getContainerGraphicalRepresentation().getViewWidth(1),
				getContainerGraphicalRepresentation().getViewHeight(1), Filling.NOT_FILLED);

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
	}*/

	@Override
	public final boolean getAllowToLeaveBounds() {
		return allowToLeaveBounds;
	}

	@Override
	public void setAllowToLeaveBounds(boolean allowToLeaveBounds) {
		FGENotification notification = requireChange(ALLOW_TO_LEAVE_BOUNDS, allowToLeaveBounds);
		if (notification != null) {
			this.allowToLeaveBounds = allowToLeaveBounds;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdaptBoundsToContents() {
		return adaptBoundsToContents;
	}

	@Override
	public void setAdaptBoundsToContents(boolean adaptBoundsToContents) {
		FGENotification notification = requireChange(ADAPT_BOUNDS_TO_CONTENTS, adaptBoundsToContents);
		if (notification != null) {
			this.adaptBoundsToContents = adaptBoundsToContents;
			hasChanged(notification);
			/*if (adaptBoundsToContents) {
				extendBoundsToHostContents();
			}*/
		}
	}

	/**
	 * Calling this method forces FGE to check (and eventually update) dimension of current graphical representation according defined
	 * dimension constraints
	 */
	/*protected void checkAndUpdateDimensionIfRequired() {
		if (getDimensionConstraints() == DimensionConstraints.CONTAINER) {
			List<? extends GraphicalRepresentation> childs = getContainedGraphicalRepresentations();
			if (childs != null && childs.size() > 0) {
				ShapeGraphicalRepresentation first = (ShapeGraphicalRepresentation) childs.get(0);
				updateRequiredBoundsForChildGRLocation(first, first.getLocation());
			}
		} else {
			checkAndUpdateDimensionBoundsIfRequired();
		}
	}*/

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
	/*public double getRequiredWidth(double labelWidth) {
		return labelWidth;
	}*/

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
	/*public double getRequiredHeight(double labelHeight) {
		return labelHeight;
	}*/

	// private boolean isCheckingDimensionConstraints = false;

	/*private void checkAndUpdateDimensionBoundsIfRequired() {

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
	}*/

	// *******************************************************************************
	// * Geometry constraints *
	// *******************************************************************************

	/*public static BindingDefinition X_CONSTRAINTS = new BindingDefinition("xConstraints", Double.class, BindingDefinitionType.GET, false);
	public static BindingDefinition Y_CONSTRAINTS = new BindingDefinition("yConstraints", Double.class, BindingDefinitionType.GET, false);
	public static BindingDefinition WIDTH_CONSTRAINTS = new BindingDefinition("widthConstraints", Double.class, BindingDefinitionType.GET,
			false);
	public static BindingDefinition HEIGHT_CONSTRAINTS = new BindingDefinition("heightConstraints", Double.class,
			BindingDefinitionType.GET, false);*/

	private DataBinding<Double> xConstraints;
	private DataBinding<Double> yConstraints;
	private DataBinding<Double> widthConstraints;
	private DataBinding<Double> heightConstraints;

	@Deprecated
	@Override
	public DataBinding<Double> getXConstraints() {
		if (xConstraints == null) {
			xConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
		}
		return xConstraints;
	}

	@Deprecated
	@Override
	public void setXConstraints(DataBinding<Double> xConstraints) {
		FGENotification notification = requireChange(X_CONSTRAINTS, xConstraints);
		if (notification != null) {
			if (xConstraints != null) {
				xConstraints.setOwner(this);
				xConstraints.setDeclaredType(Double.class);
				xConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.xConstraints = xConstraints;
			hasChanged(notification);
		}
	}

	@Deprecated
	@Override
	public DataBinding<Double> getYConstraints() {
		if (yConstraints == null) {
			yConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
		}
		return yConstraints;
	}

	@Deprecated
	@Override
	public void setYConstraints(DataBinding<Double> yConstraints) {
		FGENotification notification = requireChange(Y_CONSTRAINTS, yConstraints);
		if (notification != null) {
			if (yConstraints != null) {
				yConstraints.setOwner(this);
				yConstraints.setDeclaredType(Double.class);
				yConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.yConstraints = yConstraints;
			hasChanged(notification);
		}
	}

	@Deprecated
	@Override
	public DataBinding<Double> getWidthConstraints() {
		if (widthConstraints == null) {
			widthConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
		}
		return widthConstraints;
	}

	@Deprecated
	@Override
	public void setWidthConstraints(DataBinding<Double> widthConstraints) {
		FGENotification notification = requireChange(WIDTH_CONSTRAINTS, widthConstraints);
		if (notification != null) {
			if (widthConstraints != null) {
				widthConstraints.setOwner(this);
				widthConstraints.setDeclaredType(Double.class);
				widthConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.widthConstraints = widthConstraints;
			hasChanged(notification);
		}
	}

	@Deprecated
	@Override
	public DataBinding<Double> getHeightConstraints() {
		if (heightConstraints == null) {
			heightConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
		}
		return heightConstraints;
	}

	@Deprecated
	@Override
	public void setHeightConstraints(DataBinding<Double> heightConstraints) {
		FGENotification notification = requireChange(HEIGHT_CONSTRAINTS, heightConstraints);
		if (notification != null) {
			if (heightConstraints != null) {
				heightConstraints.setOwner(this);
				heightConstraints.setDeclaredType(Double.class);
				heightConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.heightConstraints = heightConstraints;
			hasChanged(notification);
		}
	}

	/*@Override
	public void finalizeConstraints() {
		if (xConstraints != null && xConstraints.isValid()) {
			xConstraints.decode();
			try {
				setX((Double) TypeUtils.castTo(xConstraints.getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			setLocationConstraints(LocationConstraints.UNMOVABLE);
		}
		if (yConstraints != null && yConstraints.isValid()) {
			yConstraints.decode();
			try {
				setY((Double) TypeUtils.castTo(yConstraints.getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			setLocationConstraints(LocationConstraints.UNMOVABLE);
		}
		if (widthConstraints != null && widthConstraints.isValid()) {
			widthConstraints.decode();
			try {
				setWidth((Double) TypeUtils.castTo(widthConstraints.getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}
		if (heightConstraints != null && heightConstraints.isValid()) {
			heightConstraints.decode();
			try {
				setHeight((Double) TypeUtils.castTo(heightConstraints.getBindingValue(this), Double.class));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
		}
	}*/

	/*@Override
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
	}*/

	/*@Override
	public void updateConstraints() {
		// System.out.println("updateConstraints() called, valid=" + xConstraints.isValid() + "," + yConstraints.isValid() + ","
		// + widthConstraints.isValid() + "," + heightConstraints.isValid());
		logger.fine("Called updateConstraints(), drawable=" + getDrawable() + " index=" + getIndex() + " class=" + getClass());
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

	}*/

	/*private void updateXPosition() {
		try {
			Double n = xConstraints.getBindingValue(this);
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
			Double n = yConstraints.getBindingValue(this);
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
			Double n = widthConstraints.getBindingValue(this);
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
			Double n = heightConstraints.getBindingValue(this);
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
	}*/

	/*@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		// logger.info("Binding changed to " + dataBinding);

		super.notifiedBindingChanged(dataBinding);
		if (dataBinding == getXConstraints() && dataBinding.isValid()) {
			updateXPosition();
		} else if (dataBinding == getYConstraints() && dataBinding.isValid()) {
			updateYPosition();
		} else if (dataBinding == getWidthConstraints() && dataBinding.isValid()) {
			updateWidthPosition();
		} else if (dataBinding == getHeightConstraints() && dataBinding.isValid()) {
			updateHeightPosition();
		}
	}*/

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public ForegroundStyle getForeground() {
		return foreground;
	}

	@Override
	public void setForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(FOREGROUND, aForeground, false);
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
		FGENotification notification = requireChange(SELECTED_FOREGROUND, aForeground, false);
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
		FGENotification notification = requireChange(FOCUSED_FOREGROUND, aForeground, false);
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
		FGENotification notification = requireChange(BACKGROUND, aBackground, false);
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
			setBackground(getFactory().makeBackground(backgroundType));
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
		FGENotification notification = requireChange(SELECTED_BACKGROUND, aBackground, false);
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
		FGENotification notification = requireChange(FOCUSED_BACKGROUND, aBackground, false);
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
		FGENotification notification = requireChange(BORDER, border);
		if (notification != null) {
			this.border = border;
			hasChanged(notification);
			// notifyObjectResized();
		}
	}

	@Override
	public ShadowStyle getShadowStyle() {
		return shadowStyle;
	}

	@Override
	public void setShadowStyle(ShadowStyle aShadowStyle) {
		FGENotification notification = requireChange(SHADOW_STYLE, aShadowStyle);
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
		FGENotification notification = requireChange(Parameters.drawShadow,
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
				Parameters.shadowDarkness, shadowDarkness);
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
		FGENotification notification = requireChange(Parameters.shadowBlur,
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
		FGENotification notification = requireChange(Parameters.shadowDeep,
				shadowDeep);
		if (notification != null) {
			this.shadowDeep = shadowDeep;
			hasChanged(notification);
		}
	}*/

	@Override
	public ShapeSpecification getShape() {
		return shape;
	}

	@Override
	public void setShape(ShapeSpecification aShape) {
		if (shape != aShape) {
			if (shape != null) {
				shape.deleteObserver(this);
			}
			aShape.addObserver(this);
			FGENotification notification = requireChange(SHAPE, aShape);
			if (notification != null) {
				ShapeType oldType = aShape != null ? aShape.getShapeType() : null;
				this.shape = aShape;
				// shape.rebuildControlPoints();
				hasChanged(notification);
				setChanged();
				notifyObservers(new FGENotification(SHAPE_TYPE, oldType, aShape.getShapeType()));
				// notifyShapeChanged();
			}
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
			setShape(getFactory().makeShape(shapeType/*, this*/));
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

	/*@Override
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

	@Override
	public void notifyObjectResized() {
		notifyObjectResized(null);
	}

	@Override
	public void notifyObjectResized(FGEDimension oldSize) {
		if (getShape() != null) {
			getShape().updateShape();
		}
		setChanged();
		notifyObservers(new ObjectResized(oldSize, getSize()));
	}

	@Override
	public void notifyObjectWillResize() {
		isResizing = true;
		setChanged();
		notifyObservers(new ObjectWillResize());
	}

	@Override
	public void notifyObjectHasResized() {
		isResizing = false;
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
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
	}*/

	@Override
	public boolean getIsFloatingLabel() {
		return isFloatingLabel;
	}

	@Override
	public void setIsFloatingLabel(boolean isFloatingLabel) {
		FGENotification notification = requireChange(IS_FLOATING_LABEL, isFloatingLabel);
		if (notification != null) {
			this.isFloatingLabel = isFloatingLabel;
			hasChanged(notification);
		}
	}

	@Override
	public double getRelativeTextX() {
		return relativeTextX;
	}

	@Override
	public void setRelativeTextX(double textX) {
		FGENotification notification = requireChange(RELATIVE_TEXT_X, textX);
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
		FGENotification notification = requireChange(RELATIVE_TEXT_Y, textY);
		if (notification != null) {
			this.relativeTextY = textY;
			hasChanged(notification);
		}
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	/*@Override
	public int getViewX(double scale) {
		return (int) (getX() * scale);
	}

	@Override
	public int getViewY(double scale) {
		return (int) (getY() * scale);
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
		return getWidth() + (getBorder() != null ? getBorder().getLeft() + getBorder().getRight() : 0);
	}

	@Override
	public double getUnscaledViewHeight() {
		return getHeight() + (getBorder() != null ? getBorder().getTop() + getBorder().getBottom() : 0);
	}*/

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	/*@Override
	public FGERectangle getBounds() {
		return new FGERectangle(getX(), getY(), getUnscaledViewWidth(), getUnscaledViewHeight());
	}*/

	/**
	 * Return bounds (including border) relative to parent container
	 * 
	 * @return
	 */
	/*public FGERectangle getBoundsNoBorder() {
		return new FGERectangle(getX(), getY(), getWidth(), getHeight());
	}*/

	/**
	 * Return view bounds (excluding border) relative to parent container
	 * 
	 * @param scale
	 * @return
	 */
	/*@Override
	public Rectangle getBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = (int) ((getX() + (getBorder() != null ? getBorder().getLeft() : 0)) * scale);
		bounds.y = (int) ((getY() + (getBorder() != null ? getBorder().getTop() : 0)) * scale);
		bounds.width = (int) (getWidth() * scale);
		bounds.height = (int) (getHeight() * scale);

		return bounds;
	}*/

	/**
	 * Return view bounds (excluding border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	/*@Override
	public Rectangle getBounds(GraphicalRepresentation container, double scale) {
		Rectangle bounds = getBounds(scale);
		bounds = convertRectangle(getContainerGraphicalRepresentation(), bounds, container, scale);
		return bounds;
	}*/

	/**
	 * Return logical bounds (including border) relative to given container
	 * 
	 * @param scale
	 * @return
	 */
	/*@Override
	public Rectangle getViewBounds(GraphicalRepresentation container, double scale) {
		Rectangle bounds = getViewBounds(scale);
		if (getContainerGraphicalRepresentation() == null) {
			logger.warning("Container is null for " + this + " validated=" + isValidated());
		}
		if (container == null) {
			logger.warning("Container is null for " + this + " validated=" + isValidated());
		}
		bounds = convertRectangle(getContainerGraphicalRepresentation(), bounds, container, scale);
		return bounds;
	}*/

	/*@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		AffineTransform returned = AffineTransform.getScaleInstance(getWidth(), getHeight());
		if (getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(getBorder().getLeft(), getBorder().getTop()));
		}
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
		}
		return returned;
	}*/

	/*@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		AffineTransform returned = new AffineTransform();
		if (scale != 1) {
			returned = AffineTransform.getScaleInstance(1 / scale, 1 / scale);
		}
		if (getBorder() != null) {
			returned.preConcatenate(AffineTransform.getTranslateInstance(-getBorder().getLeft(), -getBorder().getTop()));
		}
		returned.preConcatenate(AffineTransform.getScaleInstance(1 / getWidth(), 1 / getHeight()));
		return returned;
	}*/

	/*@Override
	public boolean isPointInsideShape(FGEPoint aPoint) {
		return shape.isPointInsideShape(aPoint);
	}*/

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	/*@Override
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
	}*/

	/*@Override
	public void paint(Graphics g, DrawingController controller) {
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
				g2.drawRect((int) (getBorder().getLeft() * controller.getScale()), (int) (getBorder().getTop() * controller.getScale()),
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
	}*/

	/*@Override
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
	}*/

	/*@Override
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
	}*/

	/*@Override
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
	}*/

	@Override
	public void setHorizontalTextAlignment(org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment horizontalTextAlignment) {
		super.setHorizontalTextAlignment(horizontalTextAlignment);
		// checkAndUpdateDimensionBoundsIfRequired();
	}

	@Override
	public void setVerticalTextAlignment(org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment verticalTextAlignment) {
		super.setVerticalTextAlignment(verticalTextAlignment);
		// checkAndUpdateDimensionBoundsIfRequired();
	}

	/*@Override
	public String getInspectorName() {
		return "ShapeGraphicalRepresentation.inspector";
	}*/

	// Override for a custom view management
	/*@Override
	public ShapeView<Object> makeShapeView(DrawingController controller) {
		return new ShapeView<Object>(this, controller);
	}*/

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getShapeType() + "," + getText() + "]";
	}

	/*@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		super.notifyObjectHierarchyHasBeenUpdated();
		// The two calls below seem to be very important to ensure proper layout of
		// containers with constraints CONTAINER!
		checkAndUpdateLocationIfRequired();
		checkAndUpdateDimensionIfRequired();
	}*/

	/*@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return getShape().getControlAreas();
	}*/

	/*@Override
	public FGEShapeGraphics getGraphics() {
		return graphics;
	}*/

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
	public boolean isAllowedToBeDraggedOutsideParentContainerInsideContainer(ContainerNode<?, ?> container) {
		return false;
	}

	/**
	 * Override this if you want to use this feature Default implementation does nothing return boolean indicating if drag was successfully
	 * performed
	 */
	@Override
	public boolean dragOutsideParentContainerInsideContainer(ContainerNode<?, ?> container, FGEPoint location) {
		return false;
	}

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

	/*@Override
	public void performRandomLayout() {
		performRandomLayout(getWidth(), getHeight());
	}

	@Override
	public void performAutoLayout() {
		performAutoLayout(getWidth(), getHeight());
	}*/

	/*@Override
	public void notifyDrawableAdded(GraphicalRepresentation addedGR) {
		super.notifyDrawableAdded(addedGR);
		if (getAdaptBoundsToContents()) {
			extendBoundsToHostContents();
		}
	}*/

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * @return the area on which the given connector can start
	 */
	/*@Override
	public FGEArea getAllowedStartAreaForConnector(ConnectorGraphicalRepresentation connectorGR) {
		return getShape().getOutline();
	}*/

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	/*@Override
	public FGEArea getAllowedEndAreaForConnector(ConnectorGraphicalRepresentation connectorGR) {
		return getShape().getOutline();
	}*/

	/**
	 * Returns the area on which the given connector can start. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to start
	 * 
	 * @return the area on which the given connector can start
	 */
	/*@Override
	public FGEArea getAllowedStartAreaForConnectorForDirection(ConnectorGraphicalRepresentation connectorGR, FGEArea area,
			SimplifiedCardinalDirection direction) {
		return area;
	}*/

	/**
	 * Returns the area on which the given connector can end. The area is expressed in this normalized coordinates
	 * 
	 * @param connectorGR
	 *            the connector asking where to end
	 * @return the area on which the given connector can end
	 */
	/*@Override
	public FGEArea getAllowedEndAreaForConnectorForDirection(ConnectorGraphicalRepresentation connectorGR, FGEArea area,
			SimplifiedCardinalDirection direction) {
		return area;
	}*/

	public static class ShapeBorderImpl extends FGEObjectImpl implements ShapeBorder {
		private int top = FGEConstants.DEFAULT_BORDER_SIZE;
		private int bottom = FGEConstants.DEFAULT_BORDER_SIZE;
		private int left = FGEConstants.DEFAULT_BORDER_SIZE;
		private int right = FGEConstants.DEFAULT_BORDER_SIZE;

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

		@Override
		public int getTop() {
			return top;
		}

		@Override
		public void setTop(int top) {
			this.top = top;
		}

		@Override
		public int getBottom() {
			return bottom;
		}

		@Override
		public void setBottom(int bottom) {
			this.bottom = bottom;
		}

		@Override
		public int getLeft() {
			return left;
		}

		@Override
		public void setLeft(int left) {
			this.left = left;
		}

		@Override
		public int getRight() {
			return right;
		}

		@Override
		public void setRight(int right) {
			this.right = right;
		}

	}

}
