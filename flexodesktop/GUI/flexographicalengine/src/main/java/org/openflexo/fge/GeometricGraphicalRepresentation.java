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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.cp.GeometryAdjustingControlPoint;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEGeneralShape.GeneralShapePathElement;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.GeometryModified;
import org.openflexo.toolbox.ToolBox;

public class GeometricGraphicalRepresentation<O> extends GraphicalRepresentation<O> implements Observer {

	private static final Logger logger = Logger.getLogger(GeometricGraphicalRepresentation.class.getPackage().getName());

	// *******************************************************************************
	// * Parameters *
	// *******************************************************************************

	public static enum Parameters implements GRParameter {
		foreground, background, layer, geometricObject
	}

	// *******************************************************************************
	// * Inner classes *
	// *******************************************************************************

	// *******************************************************************************
	// * Fields *
	// *******************************************************************************

	private int layer = FGEConstants.DEFAULT_OBJECT_LAYER;
	private ForegroundStyle foreground;
	private BackgroundStyle background;

	protected FGEGeometricGraphics graphics;

	private FGEArea geometricObject;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public GeometricGraphicalRepresentation(FGEArea anObject, O aDrawable, Drawing<?> aDrawing) {
		super(aDrawable, aDrawing);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		// foreground.setGraphicalRepresentation(this);
		foreground.addObserver(this);

		background = BackgroundStyle.makeColoredBackground(Color.WHITE);
		// background.setGraphicalRepresentation(this);
		background.addObserver(this);

		geometricObject = anObject;
		rebuildControlPoints();

		graphics = new FGEGeometricGraphics(this);

		addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = super.getAllParameters();
		Parameters[] allParams = Parameters.values();
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
		super.delete();
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	public ForegroundStyle getForeground() {
		return foreground;
	}

	public void setForeground(ForegroundStyle aForeground) {
		FGENotification notification = requireChange(Parameters.foreground, aForeground);
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

	public boolean getNoStroke() {
		return foreground.getNoStroke();
	}

	public void setNoStroke(boolean noStroke) {
		foreground.setNoStroke(noStroke);
	}

	public BackgroundStyle getBackground() {
		return background;
	}

	public void setBackground(BackgroundStyle aBackground) {
		FGENotification notification = requireChange(Parameters.background, aBackground);
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

	public BackgroundStyleType getBackgroundType() {
		return background.getBackgroundStyleType();
	}

	public void setBackgroundType(BackgroundStyleType backgroundType) {
		if (backgroundType != getBackgroundType()) {
			setBackground(BackgroundStyle.makeBackground(backgroundType));
		}
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public void setLayer(int layer) {
		FGENotification notification = requireChange(Parameters.layer, layer);
		if (notification != null) {
			this.layer = layer;
			hasChanged(notification);
		}
	}

	@Override
	public int getViewX(double scale) {
		return getViewBounds(scale).x;
	}

	@Override
	public int getViewY(double scale) {
		return getViewBounds(scale).y;
	}

	@Override
	public int getViewWidth(double scale) {
		return getViewBounds(scale).width;
	}

	@Override
	public int getViewHeight(double scale) {
		return getViewBounds(scale).height;
	}

	@Override
	public Rectangle getViewBounds(double scale) {
		return getBounds(scale);
	}

	public Rectangle getBounds(double scale) {
		return getContainerGraphicalRepresentation().getViewBounds(scale);
		// return new Rectangle(0,0,1,1);
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		Rectangle bounds = getBounds(1);
		return new FGERectangle(0, 0, bounds.width, bounds.height, Filling.FILLED);
	}

	@Override
	public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale) {
		FGERectangle drawingViewBounds = new FGERectangle(drawingViewSelection.getX(), drawingViewSelection.getY(),
				drawingViewSelection.getWidth(), drawingViewSelection.getHeight(), Filling.FILLED);
		boolean isFullyContained = true;
		/*	for (ControlPoint cp : getConnector().getControlPoints()) {
			Point cpInContainerView = convertLocalNormalizedPointToRemoteViewCoordinates(
					cp.getPoint(), 
					getDrawingGraphicalRepresentation(), 
					scale);
			FGEPoint preciseCPInContainerView = new FGEPoint(cpInContainerView.x,cpInContainerView.y);
			if (!drawingViewBounds.containsPoint(preciseCPInContainerView)) {
				//System.out.println("Going outside: point="+preciseCPInContainerView+" bounds="+containerViewBounds);
				isFullyContained = false;
			}
		}*/
		return isFullyContained;
	}

	@Override
	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale) {
		return AffineTransform.getScaleInstance(scale, scale);

		/*double width = getContainerGraphicalRepresentation().getViewWidth(scale);
		double height = getContainerGraphicalRepresentation().getViewHeight(scale);
		AffineTransform returned = AffineTransform.getScaleInstance(width,height);
		if (scale != 1) {
			returned.preConcatenate(AffineTransform.getScaleInstance(scale,scale));
		}
		return returned;*/
	}

	@Override
	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale) {
		return AffineTransform.getScaleInstance(1 / scale, 1 / scale);

		/*double width = getContainerGraphicalRepresentation().getViewWidth(scale);
		double height = getContainerGraphicalRepresentation().getViewHeight(scale);
		AffineTransform returned = new AffineTransform();
		if (scale != 1) returned = AffineTransform.getScaleInstance(1/scale, 1/scale);
		returned.preConcatenate(AffineTransform.getScaleInstance(1/width,1/height));
		return returned;*/
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	public void paintGeometricObject(FGEGeometricGraphics graphics) {
		getGeometricObject().paint(graphics);
	}

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		if (!isRegistered()) {
			setRegistered(true);
		}

		super.paint(g, controller);

		Graphics2D g2 = (Graphics2D) g;
		graphics.createGraphics(g2, controller);

		graphics.setDefaultBackground(getBackground());
		graphics.setDefaultForeground(getForeground());
		graphics.setDefaultTextStyle(getTextStyle());

		if (getIsSelected() || getIsFocused()) {
			ForegroundStyle style = getForeground().clone();
			if (getIsSelected()) {
				style.setColorNoNotification(getDrawingGraphicalRepresentation().getSelectionColor());
			} else if (getIsFocused()) {
				style.setColorNoNotification(getDrawingGraphicalRepresentation().getFocusColor());
			}
			graphics.setDefaultForeground(style);
		}

		paintGeometricObject(graphics);

		if (getIsSelected() || getIsFocused()) {
			Color color = null;
			if (getIsSelected()) {
				color = getDrawingGraphicalRepresentation().getSelectionColor();
			} else if (getIsFocused()) {
				color = getDrawingGraphicalRepresentation().getFocusColor();
			}
			for (ControlPoint cp : getControlPoints()) {
				cp.paint(graphics);
			}
		}

		if (hasFloatingLabel()) {
			graphics.useTextStyle(getTextStyle());
			graphics.drawString(getText(), new FGEPoint(getLabelRelativePosition().x + getAbsoluteTextX(), getLabelRelativePosition().y
					+ getAbsoluteTextY()), getTextAlignment());
		}

		graphics.releaseGraphics();
	}

	protected FGEPoint getLabelRelativePosition() {
		if (getGeometricObject() instanceof FGEPoint) {
			return (FGEPoint) getGeometricObject();
		} else if (getGeometricObject() instanceof FGEAbstractLine) {
			FGEAbstractLine line = (FGEAbstractLine) getGeometricObject();
			return (new FGESegment(line.getP1(), line.getP2())).getMiddle();
		} else if (getGeometricObject() instanceof FGEShape) {
			return ((FGEShape) getGeometricObject()).getCenter();
		}
		return new FGEPoint(0, 0);
	}

	/**
	 * Return center of label, relative to container view
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public Point getLabelViewCenter(double scale) {
		return new Point((int) (getAbsoluteTextX() * scale + getViewX(scale)), (int) (getAbsoluteTextY() * scale + getViewY(scale)));
	}

	/**
	 * Sets center of label, relative to container view
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public void setLabelViewCenter(Point aPoint, double scale) {
		setAbsoluteTextX(((double) aPoint.x - (double) getViewX(scale)) / scale);
		setAbsoluteTextY(((double) aPoint.y - (double) getViewY(scale)) / scale);

	}

	@Override
	public boolean hasFloatingLabel() {
		return hasText();
	}

	@Override
	public String getInspectorName() {
		return "GeometricGraphicalRepresentation.inspector";
	}

	@Override
	public void update(Observable observable, Object notification) {
		super.update(observable, notification);

		if (observable instanceof BackgroundStyle) {
			notifyAttributeChange(Parameters.background);
		}
		if (observable instanceof ForegroundStyle) {
			notifyAttributeChange(Parameters.foreground);
		}
	}

	public FGEArea getGeometricObject() {
		return geometricObject;
	}

	public void setGeometricObject(FGEArea geometricObject) {
		FGENotification notification = requireChange(Parameters.geometricObject, geometricObject);
		if (notification != null) {
			this.geometricObject = geometricObject;
			rebuildControlPoints();
			hasChanged(notification);
		}
	}

	protected Vector<ControlPoint> _controlPoints;

	public List<ControlPoint> getControlPoints() {
		if (_controlPoints == null) {
			rebuildControlPoints();
		}
		return _controlPoints;
	}

	/*protected final void notifyCPDragged(String name, FGEPoint newLocation)
	{
		notifyGeometryChanged();
		rebuildControlPoints();
	}*/

	protected List<ControlPoint> buildControlPointsForPoint(FGEPoint point) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		returned.add(new GeometryAdjustingControlPoint<FGEPoint>(this, "point", point.clone()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEPoint) getGeometricObject()).x = newAbsolutePoint.x;
				((FGEPoint) getGeometricObject()).y = newAbsolutePoint.y;
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEPoint geometricObject) {
				setPoint(geometricObject);
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForLine(FGEAbstractLine line) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		returned.add(new GeometryAdjustingControlPoint<FGELine>(this, "p1", line.getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGELine) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGELine geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGELine>(this, "p2", line.getP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGELine) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGELine geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForCurve(FGEQuadCurve curve) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "p1", curve.getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "cp", curve.getCtrlPoint()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setCtrlPoint(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getCtrlPoint());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "p3", curve.getP3()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setP3(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getP3());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGEQuadCurve>(this, "p2", curve.getP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGEQuadCurve) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEQuadCurve geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForCurve(FGECubicCurve curve) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "p1", curve.getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "cp1", curve.getCtrlP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setCtrlP1(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getCtrlP1());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "cp2", curve.getCtrlP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setCtrlP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getCtrlP2());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGECubicCurve>(this, "p2", curve.getP2()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				((FGECubicCurve) getGeometricObject()).setP2(newAbsolutePoint);
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGECubicCurve geometricObject) {
				setPoint(geometricObject.getP2());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForPolygon(FGEPolygon polygon) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		for (int i = 0; i < polygon.getPointsNb(); i++) {
			final int index = i;
			returned.add(new GeometryAdjustingControlPoint<FGEPolygon>(this, "p" + i, polygon.getPointAt(i)) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return new FGEPlane();
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint p = ((FGEPolygon) getGeometricObject()).getPointAt(index);
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEPolygon geometricObject) {
					setPoint(geometricObject.getPointAt(index));
				}
			});
		}
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForPolylin(FGEPolylin polylin) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		for (int i = 0; i < polylin.getPointsNb(); i++) {
			final int index = i;
			returned.add(new GeometryAdjustingControlPoint<FGEPolylin>(this, "p" + i, polylin.getPointAt(i)) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return new FGEPlane();
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint p = ((FGEPolylin) getGeometricObject()).getPointAt(index);
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEPolylin geometricObject) {
					setPoint(geometricObject.getPointAt(index));
				}
			});
		}
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForEllips(FGEEllips ellips) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		// TODO
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForRectangle(FGERectangle rectangle) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "northWest", rectangle.getNorthWestPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getSouthEastPt(),
						CardinalQuadrant.NORTH_WEST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).x = pt.x;
				((FGERectangle) getGeometricObject()).y = pt.y;
				((FGERectangle) getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getNorthWestPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "northEast", rectangle.getNorthEastPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getSouthWestPt(),
						CardinalQuadrant.NORTH_EAST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).y = pt.y;
				((FGERectangle) getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getNorthEastPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "southWest", rectangle.getSouthWestPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getNorthEastPt(),
						CardinalQuadrant.SOUTH_WEST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).x = pt.x;
				((FGERectangle) getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getSouthWestPt());
			}
		});
		returned.add(new GeometryAdjustingControlPoint<FGERectangle>(this, "southEast", rectangle.getSouthEastPt()) {
			private double initialWidth;
			private double initialHeight;

			@Override
			public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
				initialWidth = ((FGERectangle) getGeometricObject()).width;
				initialHeight = ((FGERectangle) getGeometricObject()).height;
				setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane(((FGERectangle) getGeometricObject()).getNorthWestPt(),
						CardinalQuadrant.SOUTH_EAST));
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
				setPoint(pt);

				((FGERectangle) getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
				((FGERectangle) getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGERectangle geometricObject) {
				setPoint(geometricObject.getSouthEastPt());
			}
		});
		return returned;
	}

	protected List<ControlPoint> buildControlPointsForGeneralShape(FGEGeneralShape shape) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		returned.add(new GeometryAdjustingControlPoint<FGEGeneralShape>(this, "p0", shape.getPathElements().firstElement().getP1()) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				FGEPoint p = ((FGEGeneralShape) getGeometricObject()).getPathElements().firstElement().getP1();
				p.x = newAbsolutePoint.x;
				p.y = newAbsolutePoint.y;
				setPoint(newAbsolutePoint);
				notifyGeometryChanged();
				return true;
			}

			@Override
			public void update(FGEGeneralShape geometricObject) {
				setPoint(geometricObject.getPathElements().firstElement().getP1());
			}
		});
		for (int i = 0; i < shape.getPathElements().size(); i++) {
			final int index = i;
			GeneralShapePathElement<?> element = shape.getPathElements().get(i);
			returned.add(new GeometryAdjustingControlPoint<FGEGeneralShape>(this, "p" + i, element.getP2()) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return new FGEPlane();
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint p = ((FGEGeneralShape) getGeometricObject()).getPathElements().get(index).getP2();
					p.x = newAbsolutePoint.x;
					p.y = newAbsolutePoint.y;
					((FGEGeneralShape) getGeometricObject()).refresh();
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEGeneralShape geometricObject) {
					setPoint(geometricObject.getPathElements().get(index).getP2());
				}
			});

		}
		return returned;
	}

	private void updateControlPoints() {
		boolean cpNeedsToBeRebuilt = false;
		for (ControlPoint cp : _controlPoints) {
			if (cp instanceof GeometryAdjustingControlPoint) {
				((GeometryAdjustingControlPoint) cp).update(getGeometricObject());
			} else {
				cpNeedsToBeRebuilt = true;
			}
		}
		if (cpNeedsToBeRebuilt) {
			rebuildControlPoints();
		}
	}

	public List<ControlPoint> rebuildControlPoints() {
		if (_controlPoints == null) {
			_controlPoints = new Vector<ControlPoint>();
		}
		_controlPoints.clear();

		if (getGeometricObject() == null) {
			return _controlPoints;
		}

		if (getGeometricObject() instanceof FGEPoint) {
			_controlPoints.addAll(buildControlPointsForPoint((FGEPoint) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGEAbstractLine) {
			_controlPoints.addAll(buildControlPointsForLine((FGEAbstractLine) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGERectangle) {
			_controlPoints.addAll(buildControlPointsForRectangle((FGERectangle) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGERoundRectangle) {
			_controlPoints.addAll(buildControlPointsForRectangle(((FGERoundRectangle) getGeometricObject()).getBoundingBox()));
		} else if (getGeometricObject() instanceof FGEEllips) {
			_controlPoints.addAll(buildControlPointsForEllips((FGEEllips) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGEPolygon) {
			_controlPoints.addAll(buildControlPointsForPolygon((FGEPolygon) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGEPolylin) {
			_controlPoints.addAll(buildControlPointsForPolylin((FGEPolylin) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGEQuadCurve) {
			_controlPoints.addAll(buildControlPointsForCurve((FGEQuadCurve) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGECubicCurve) {
			_controlPoints.addAll(buildControlPointsForCurve((FGECubicCurve) getGeometricObject()));
		} else if (getGeometricObject() instanceof FGEGeneralShape) {
			_controlPoints.addAll(buildControlPointsForGeneralShape((FGEGeneralShape) getGeometricObject()));
		}
		return _controlPoints;
	}

	public void notifyGeometryChanged() {
		updateControlPoints();
		setChanged();
		notifyObservers(new GeometryModified());
		if (getGeometricObject() instanceof FGEPoint) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
		} else if (getGeometricObject() instanceof FGELine) {
			notifyChange("drawable.x1");
			notifyChange("drawable.y1");
			notifyChange("drawable.x2");
			notifyChange("drawable.y2");
		} else if (getGeometricObject() instanceof FGERectangle) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
			notifyChange("drawable.width");
			notifyChange("drawable.height");
		} else if (getGeometricObject() instanceof FGECircle) {
			notifyChange("drawable.centerX");
			notifyChange("drawable.centerY");
			notifyChange("drawable.radius");
		} else if (getGeometricObject() instanceof FGEEllips) {
			notifyChange("drawable.x");
			notifyChange("drawable.y");
			notifyChange("drawable.width");
			notifyChange("drawable.height");
		}
	}

	// Hack: for the inspector !!!
	private void notifyChange(final String string) {
		notify(new FGENotification(string, null, null));
	}

}
