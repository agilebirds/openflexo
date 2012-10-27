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
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GraphicalRepresentationImpl.DependencyLoopException;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLSerializable;

public interface GraphicalRepresentation<O> extends XMLSerializable, Bindable, BindingEvaluationContext, Cloneable, FGEConstants, Observer,
		HasPropertyChangeSupport {

	public static interface LabelMetricsProvider {
		public Dimension getScaledPreferredDimension(double scale);

	}

	public static interface GRParameter {
		public String name();
	}

	public static enum Parameters implements GRParameter {
		identifier,
		layer,
		hasText,
		text,
		isMultilineAllowed,
		lineWrap,
		continuousTextEditing,
		textStyle,
		absoluteTextX,
		absoluteTextY,
		horizontalTextAlignment,
		verticalTextAlignment,
		paragraphAlignment,
		isSelectable,
		isFocusable,
		isSelected,
		isFocused,
		drawControlPointsWhenFocused,
		drawControlPointsWhenSelected,
		isReadOnly,
		isLabelEditable,
		isVisible,
		mouseClickControls,
		mouseDragControls,
		toolTipText,
		variables
	}

	public static enum ParagraphAlignment {
		LEFT, CENTER, RIGHT, JUSTIFY;
	}

	public static enum HorizontalTextAlignment {
		LEFT, CENTER, RIGHT
	}

	public static enum VerticalTextAlignment {
		TOP, MIDDLE, BOTTOM;
	}

	public static class ConstraintDependency {
		GraphicalRepresentation<?> requiringGR;
		GRParameter requiringParameter;
		GraphicalRepresentation<?> requiredGR;
		GRParameter requiredParameter;

		public ConstraintDependency(GraphicalRepresentation<?> requiringGR, GRParameter requiringParameter,
				GraphicalRepresentation<?> requiredGR, GRParameter requiredParameter) {
			super();
			this.requiringGR = requiringGR;
			this.requiringParameter = requiringParameter;
			this.requiredGR = requiredGR;
			this.requiredParameter = requiredParameter;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConstraintDependency) {
				ConstraintDependency opposite = (ConstraintDependency) obj;
				return requiredGR == opposite.requiredGR && requiringGR == opposite.requiringGR
						&& requiringParameter == opposite.requiringParameter && requiredParameter == opposite.requiredParameter;
			}
			return super.equals(obj);
		}
	}

	public static StringEncoder.Converter<FGEPoint> POINT_CONVERTER = new StringEncoder.Converter<FGEPoint>(FGEPoint.class) {
		@Override
		public FGEPoint convertFromString(String value) {
			try {
				FGEPoint returned = new FGEPoint();
				StringTokenizer st = new StringTokenizer(value, ",");
				if (st.hasMoreTokens()) {
					returned.x = Double.parseDouble(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.y = Double.parseDouble(st.nextToken());
				}
				return returned;
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(FGEPoint aPoint) {
			if (aPoint != null) {
				return aPoint.x + "," + aPoint.y;
			} else {
				return null;
			}
		}
	};
	public static StringEncoder.Converter<FGERectPolylin> RECT_POLYLIN_CONVERTER = new StringEncoder.Converter<FGERectPolylin>(
			FGERectPolylin.class) {
		@Override
		public FGERectPolylin convertFromString(String value) {
			try {
				Vector<FGEPoint> points = new Vector<FGEPoint>();
				StringTokenizer st = new StringTokenizer(value, ";");
				while (st.hasMoreTokens()) {
					String nextPoint = st.nextToken();
					points.add(POINT_CONVERTER.convertFromString(nextPoint));
				}
				return new FGERectPolylin(points);
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(FGERectPolylin aPolylin) {
			if (aPolylin != null) {
				StringBuffer sb = new StringBuffer();
				boolean isFirst = true;
				for (FGEPoint pt : aPolylin.getPoints()) {
					if (!isFirst) {
						sb.append(";");
					}
					sb.append(POINT_CONVERTER.convertToString(pt));
					isFirst = false;
				}
				return sb.toString();
			} else {
				return null;
			}
		}
	};

	public abstract void delete();

	public abstract boolean isDeleted();

	public abstract GRParameter parameterWithName(String parameterName);

	public abstract Vector<GRParameter> getAllParameters();

	public abstract void setsWith(GraphicalRepresentation<?> gr);

	public abstract void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters);

	public abstract void initializeDeserialization();

	public abstract void finalizeDeserialization();

	public abstract boolean isDeserializing();

	public abstract void resetToDefaultIdentifier();

	public abstract String getIdentifier();

	public abstract void setIdentifier(String identifier);

	public abstract int getLayer();

	public abstract void setLayer(int layer);

	public abstract O getDrawable();

	public abstract void setDrawable(O aDrawable);

	public abstract Drawing<?> getDrawing();

	public abstract void setDrawing(Drawing<?> drawing);

	public abstract DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation();

	public abstract <O2> GraphicalRepresentation<O2> getGraphicalRepresentation(O2 drawable);

	public abstract List<? extends Object> getContainedObjects(Object drawable);

	public abstract Object getContainer(Object drawable);

	public abstract List<? extends Object> getContainedObjects();

	public abstract List<GraphicalRepresentation<?>> getContainedGraphicalRepresentations();

	public abstract List<GraphicalRepresentation<?>> getOrderedContainedGraphicalRepresentations();

	public abstract void moveToTop(GraphicalRepresentation<?> gr);

	public abstract int getOrder(GraphicalRepresentation<?> child1, GraphicalRepresentation<?> child2);

	public abstract int getLayerOrder();

	public abstract int getIndex();

	public abstract Object getContainer();

	public abstract GraphicalRepresentation<?> getContainerGraphicalRepresentation();

	public abstract GraphicalRepresentation<?> getParentGraphicalRepresentation();

	public abstract boolean contains(GraphicalRepresentation<?> gr);

	public abstract boolean contains(Object drawable);

	public abstract List<Object> getAncestors();

	public abstract List<Object> getAncestors(boolean forceRecompute);

	public abstract boolean isConnectedToDrawing();

	public abstract boolean isAncestorOf(GraphicalRepresentation<?> child);

	public abstract boolean isPointVisible(FGEPoint p);

	public abstract ShapeGraphicalRepresentation<?> shapeHiding(FGEPoint p);

	public abstract TextStyle getTextStyle();

	public abstract void setTextStyle(TextStyle aTextStyle);

	public abstract double getAbsoluteTextX();

	public abstract void setAbsoluteTextX(double absoluteTextX);

	public abstract void setAbsoluteTextXNoNotification(double absoluteTextX);

	public abstract double getAbsoluteTextY();

	public abstract void setAbsoluteTextY(double absoluteTextY);

	public abstract void setAbsoluteTextYNoNotification(double absoluteTextY);

	public abstract boolean getIsFocusable();

	public abstract void setIsFocusable(boolean isFocusable);

	public abstract boolean getDrawControlPointsWhenFocused();

	public abstract void setDrawControlPointsWhenFocused(boolean aFlag);

	public abstract boolean getIsSelectable();

	public abstract void setIsSelectable(boolean isSelectable);

	public abstract boolean getDrawControlPointsWhenSelected();

	public abstract void setDrawControlPointsWhenSelected(boolean aFlag);

	public abstract String getText();

	public abstract void setText(String text);

	public abstract void setTextNoNotification(String text);

	public abstract String getMultilineText();

	public abstract void setMultilineText(String text);

	public abstract boolean getHasText();

	public abstract void setHasText(boolean hasText);

	public abstract boolean getIsMultilineAllowed();

	public abstract void setIsMultilineAllowed(boolean multilineAllowed);

	public abstract boolean getLineWrap();

	public abstract void setLineWrap(boolean lineWrap);

	public abstract boolean getContinuousTextEditing();

	public abstract void setContinuousTextEditing(boolean continuousTextEditing);

	public abstract boolean hasFloatingLabel();

	public abstract boolean getIsFocused();

	public abstract void setIsFocused(boolean aFlag);

	public abstract boolean getIsSelected();

	public abstract void setIsSelected(boolean aFlag);

	public abstract boolean getIsReadOnly();

	public abstract void setIsReadOnly(boolean readOnly);

	public abstract boolean getIsLabelEditable();

	public abstract void setIsLabelEditable(boolean labelEditable);

	public abstract boolean shouldBeDisplayed();

	public abstract boolean getIsVisible();

	public abstract void setIsVisible(boolean isVisible);

	public abstract boolean hasText();

	public abstract int getViewX(double scale);

	public abstract int getViewY(double scale);

	public abstract int getViewWidth(double scale);

	public abstract int getViewHeight(double scale);

	public abstract Rectangle getViewBounds(double scale);

	public abstract FGERectangle getNormalizedBounds();

	public abstract Point getLabelLocation(double scale);

	public abstract Dimension getLabelDimension(double scale);

	public abstract void setLabelLocation(Point point, double scale);

	public abstract Rectangle getLabelBounds(double scale);

	public abstract void paint(Graphics g, DrawingController<?> controller);

	public abstract void notifyChange(GRParameter parameter);

	public abstract void notifyAttributeChange(GRParameter parameter);

	public abstract void notify(FGENotification notification);

	public abstract String getInspectorName();

	public abstract boolean isShape();

	public abstract boolean isConnector();

	public abstract boolean isDrawing();

	public abstract void notifyDrawableAdded(GraphicalRepresentation<?> addedGR);

	public abstract void notifyDrawableRemoved(GraphicalRepresentation<?> removedGR);

	@Override
	public abstract void update(Observable observable, Object notification);

	public abstract Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

	public abstract Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale);

	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	public abstract FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale);

	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	public abstract Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale);

	public abstract FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

	public abstract FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, GraphicalRepresentation<?> source, double scale);

	public abstract FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, GraphicalRepresentation<?> destination,
			double scale);

	public abstract Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, GraphicalRepresentation<?> destination,
			double scale);

	public abstract Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r,
			GraphicalRepresentation<?> destination, double scale);

	public abstract Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, GraphicalRepresentation<?> source, double scale);

	public abstract boolean isRegistered();

	public abstract void setRegistered(boolean aFlag);

	public abstract Vector<MouseClickControl> getMouseClickControls();

	public abstract void setMouseClickControls(Vector<MouseClickControl> mouseClickControls);

	public abstract void addToMouseClickControls(MouseClickControl mouseClickControl);

	public abstract void addToMouseClickControls(MouseClickControl mouseClickControl, boolean isPrioritar);

	public abstract void removeFromMouseClickControls(MouseClickControl mouseClickControl);

	public abstract Vector<MouseDragControl> getMouseDragControls();

	public abstract void setMouseDragControls(Vector<MouseDragControl> mouseDragControls);

	public abstract void addToMouseDragControls(MouseDragControl mouseDragControl);

	public abstract void addToMouseDragControls(MouseDragControl mouseDragControl, boolean isPrioritar);

	public abstract void removeFromMouseDragControls(MouseDragControl mouseDragControl);

	public abstract MouseClickControl createMouseClickControl();

	public abstract void deleteMouseClickControl(MouseClickControl mouseClickControl);

	public abstract boolean isMouseClickControlDeletable(MouseClickControl mouseClickControl);

	public abstract MouseDragControl createMouseDragControl();

	public abstract void deleteMouseDragControl(MouseDragControl mouseDragControl);

	public abstract boolean isMouseDragControlDeletable(MouseDragControl mouseDragControl);

	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	public abstract void notifyLabelWillBeEdited();

	public abstract void notifyLabelHasBeenEdited();

	public abstract void notifyLabelWillMove();

	public abstract void notifyLabelHasMoved();

	// Override when required
	public abstract void notifyObjectHierarchyWillBeUpdated();

	// Override when required
	public abstract void notifyObjectHierarchyHasBeenUpdated();

	public abstract String getToolTipText();

	public abstract void setToolTipText(String tooltipText);

	public abstract HorizontalTextAlignment getHorizontalTextAlignment();

	public abstract void setHorizontalTextAlignment(HorizontalTextAlignment horizontalTextAlignment);

	public abstract VerticalTextAlignment getVerticalTextAlignment();

	public abstract void setVerticalTextAlignment(VerticalTextAlignment verticalTextAlignment);

	public abstract ParagraphAlignment getParagraphAlignment();

	public abstract void setParagraphAlignment(ParagraphAlignment paragraphAlignment);

	public abstract void performRandomLayout(double width, double height);

	public abstract void performAutoLayout(double width, double height);

	public abstract Stroke getSpecificStroke();

	public abstract void setSpecificStroke(Stroke aStroke);

	public abstract boolean isRootGraphicalRepresentation();

	public abstract GraphicalRepresentation<?> getRootGraphicalRepresentation();

	@Override
	public abstract BindingModel getBindingModel();

	@Override
	public abstract BindingFactory getBindingFactory();

	public abstract void updateBindingModel();

	@Override
	public abstract Object getValue(BindingVariable variable);

	public abstract void notifiedBindingModelRecreated();

	public abstract void notifyBindingChanged(DataBinding binding);

	public abstract List<GraphicalRepresentation<?>> retrieveAllContainedGR();

	public abstract Iterator<GraphicalRepresentation<?>> allGRIterator();

	public abstract Iterator<GraphicalRepresentation<?>> allContainedGRIterator();

	public abstract Vector<ConstraintDependency> getDependancies();

	public abstract Vector<ConstraintDependency> getAlterings();

	public abstract void declareDependantOf(GraphicalRepresentation<?> aComponent, GRParameter requiringParameter,
			GRParameter requiredParameter) throws DependencyLoopException;

	public abstract Vector<GRVariable> getVariables();

	public abstract void setVariables(Vector<GRVariable> variables);

	public abstract void addToVariables(GRVariable v);

	public abstract void removeFromVariables(GRVariable v);

	public abstract GRVariable createStringVariable();

	public abstract GRVariable createIntegerVariable();

	public abstract void deleteVariable(GRVariable v);

	@Override
	public abstract PropertyChangeSupport getPropertyChangeSupport();

	@Override
	public abstract String getDeletedProperty();

	/**
	 * Return boolean indicating if this graphical representation is validated. A validated graphical representation is a graphical
	 * representation fully embedded in its graphical representation tree, which means that parent and child are set and correct, and that
	 * start and end shapes are set for connectors
	 * 
	 * 
	 * @return
	 */
	public abstract boolean isValidated();

	public abstract void setValidated(boolean validated);

	public abstract LabelMetricsProvider getLabelMetricsProvider();

	public abstract void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider);

	/**
	 * Returns the number of pixels available for the label considering its positioning. This method is used in case of line wrapping.
	 * 
	 * @param scale
	 * @return
	 */
	public abstract int getAvailableLabelWidth(double scale);

}
