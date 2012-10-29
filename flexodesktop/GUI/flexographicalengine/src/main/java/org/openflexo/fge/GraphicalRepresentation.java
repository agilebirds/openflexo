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
import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * This is the common super interfaces for all graphical representation object encoded in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(GraphicalRepresentationImpl.class)
public interface GraphicalRepresentation<O> extends XMLSerializable, Bindable, BindingEvaluationContext, Cloneable, FGEConstants, Observer,
		HasPropertyChangeSupport, KeyValueCoding {

	public static final String IDENTIFIER = "identifier";
	public static final String LAYER = "layer";
	public static final String HAS_TEXT = "hasText";
	public static final String TEXT = "text";
	public static final String IS_MULTILINE_ALLOWED = "isMultilineAllowed";
	public static final String LINE_WRAP = "lineWrap";
	public static final String CONTINUOUS_TEXT_EDITING = "continuousTextEditing";
	public static final String TEXT_STYLE = "textStyle";
	public static final String ABSOLUTE_TEXT_X = "absoluteTextX";
	public static final String ABSOLUTE_TEXT_Y = "absoluteTextY";
	public static final String HORIZONTAL_TEXT_ALIGNEMENT = "horizontalTextAlignment";
	public static final String VERTICAL_TEXT_ALIGNEMENT = "verticalTextAlignment";
	public static final String PARAGRAPH_ALIGNMENT = "paragraphAlignment";
	public static final String IS_SELECTABLE = "isSelectable";
	public static final String IS_FOCUSABLE = "isFocusable";
	public static final String IS_SELECTED = "isSelected";
	public static final String IS_FOCUSED = "isFocused";
	public static final String DRAW_CONTROL_POINTS_WHEN_FOCUSED = "drawControlPointsWhenFocused";
	public static final String DRAW_CONTROL_POINTS_WHEN_SELECTED = "drawControlPointsWhenSelected";
	public static final String IS_READ_ONLY = "isReadOnly";
	public static final String IS_LABEL_EDITABLE = "isLabelEditable";
	public static final String IS_VISIBLE = "isVisible";
	public static final String MOUSE_CLICK_CONTROLS = "mouseClickControls";
	public static final String MOUSE_DRAG_CONTROLS = "mouseDragControls";
	public static final String TOOLTIP_TEXT = "toolTipText";
	public static final String VARIABLES = "variables";

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

	@SuppressWarnings("serial")
	public static class DependencyLoopException extends Exception {
		private Vector<GraphicalRepresentation<?>> dependencies;

		public DependencyLoopException(Vector<GraphicalRepresentation<?>> dependancies) {
			this.dependencies = dependancies;
		}

		@Override
		public String getMessage() {
			return "DependencyLoopException: " + dependencies;
		}
	}

	// *******************************************************************************
	// * Model
	// *******************************************************************************

	public abstract O getDrawable();

	public abstract void setDrawable(O aDrawable);

	public abstract Drawing<?> getDrawing();

	public abstract void setDrawing(Drawing<?> drawing);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = IDENTIFIER)
	@XMLAttribute
	public abstract String getIdentifier();

	@Setter(value = IDENTIFIER)
	public abstract void setIdentifier(String identifier);

	@Getter(value = LAYER, defaultValue = "0")
	@XMLAttribute
	public abstract int getLayer();

	@Setter(value = LAYER)
	public abstract void setLayer(int layer);

	@Getter(value = HAS_TEXT, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getHasText();

	@Setter(value = HAS_TEXT)
	public abstract void setHasText(boolean hasText);

	@Getter(value = TEXT)
	@XMLAttribute
	public abstract String getText();

	@Setter(value = TEXT)
	public abstract void setText(String text);

	public abstract String getMultilineText();

	public abstract void setMultilineText(String text);

	@Getter(value = IS_MULTILINE_ALLOWED, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getIsMultilineAllowed();

	@Setter(value = IS_MULTILINE_ALLOWED)
	public abstract void setIsMultilineAllowed(boolean multilineAllowed);

	@Getter(value = LINE_WRAP, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getLineWrap();

	@Setter(value = LINE_WRAP)
	public abstract void setLineWrap(boolean lineWrap);

	@Getter(value = CONTINUOUS_TEXT_EDITING, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getContinuousTextEditing();

	@Setter(value = CONTINUOUS_TEXT_EDITING)
	public abstract void setContinuousTextEditing(boolean continuousTextEditing);

	@Getter(value = TEXT_STYLE)
	@XMLElement
	public abstract TextStyle getTextStyle();

	@Setter(value = TEXT_STYLE)
	public abstract void setTextStyle(TextStyle aTextStyle);

	@Getter(value = ABSOLUTE_TEXT_X, defaultValue = "0")
	@XMLAttribute
	public abstract double getAbsoluteTextX();

	@Setter(value = ABSOLUTE_TEXT_X)
	public abstract void setAbsoluteTextX(double absoluteTextX);

	@Getter(value = ABSOLUTE_TEXT_Y, defaultValue = "0")
	@XMLAttribute
	public abstract double getAbsoluteTextY();

	@Setter(value = ABSOLUTE_TEXT_Y)
	public abstract void setAbsoluteTextY(double absoluteTextY);

	@Getter(value = HORIZONTAL_TEXT_ALIGNEMENT, defaultValue = "CENTER")
	@XMLAttribute
	public abstract HorizontalTextAlignment getHorizontalTextAlignment();

	@Setter(value = HORIZONTAL_TEXT_ALIGNEMENT)
	public abstract void setHorizontalTextAlignment(HorizontalTextAlignment horizontalTextAlignment);

	@Getter(value = VERTICAL_TEXT_ALIGNEMENT, defaultValue = "MIDDLE")
	@XMLAttribute
	public abstract VerticalTextAlignment getVerticalTextAlignment();

	@Setter(value = VERTICAL_TEXT_ALIGNEMENT)
	public abstract void setVerticalTextAlignment(VerticalTextAlignment verticalTextAlignment);

	@Getter(value = PARAGRAPH_ALIGNMENT, defaultValue = "CENTER")
	@XMLAttribute
	public abstract ParagraphAlignment getParagraphAlignment();

	@Setter(value = PARAGRAPH_ALIGNMENT)
	public abstract void setParagraphAlignment(ParagraphAlignment paragraphAlignment);

	@Getter(value = IS_SELECTABLE, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getIsSelectable();

	@Setter(value = IS_SELECTABLE)
	public abstract void setIsSelectable(boolean isSelectable);

	@Getter(value = IS_FOCUSABLE, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getIsFocusable();

	@Setter(value = IS_FOCUSABLE)
	public abstract void setIsFocusable(boolean isFocusable);

	@Getter(value = IS_SELECTED, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getIsSelected();

	@Setter(value = IS_SELECTED)
	public abstract void setIsSelected(boolean aFlag);

	@Getter(value = IS_FOCUSED, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getIsFocused();

	@Setter(value = IS_FOCUSED)
	public abstract void setIsFocused(boolean aFlag);

	@Getter(value = DRAW_CONTROL_POINTS_WHEN_FOCUSED, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getDrawControlPointsWhenFocused();

	@Setter(value = DRAW_CONTROL_POINTS_WHEN_FOCUSED)
	public abstract void setDrawControlPointsWhenFocused(boolean aFlag);

	@Getter(value = DRAW_CONTROL_POINTS_WHEN_SELECTED, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getDrawControlPointsWhenSelected();

	@Setter(value = DRAW_CONTROL_POINTS_WHEN_SELECTED)
	public abstract void setDrawControlPointsWhenSelected(boolean aFlag);

	@Getter(value = IS_READ_ONLY, defaultValue = "false")
	@XMLAttribute
	public abstract boolean getIsReadOnly();

	@Setter(value = IS_READ_ONLY)
	public abstract void setIsReadOnly(boolean readOnly);

	@Getter(value = IS_LABEL_EDITABLE, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getIsLabelEditable();

	@Setter(value = IS_LABEL_EDITABLE)
	public abstract void setIsLabelEditable(boolean labelEditable);

	@Getter(value = IS_VISIBLE, defaultValue = "true")
	@XMLAttribute
	public abstract boolean getIsVisible();

	@Setter(value = IS_VISIBLE)
	public abstract void setIsVisible(boolean isVisible);

	@Getter(value = MOUSE_CLICK_CONTROLS, cardinality = Cardinality.LIST)
	@XMLElement
	public abstract Vector<MouseClickControl> getMouseClickControls();

	@Setter(value = MOUSE_CLICK_CONTROLS)
	public abstract void setMouseClickControls(Vector<MouseClickControl> mouseClickControls);

	@Adder(id = MOUSE_CLICK_CONTROLS)
	public abstract void addToMouseClickControls(MouseClickControl mouseClickControl);

	public abstract void addToMouseClickControls(MouseClickControl mouseClickControl, boolean isPrioritar);

	@Remover(id = MOUSE_CLICK_CONTROLS)
	public abstract void removeFromMouseClickControls(MouseClickControl mouseClickControl);

	@Getter(value = MOUSE_DRAG_CONTROLS, cardinality = Cardinality.LIST)
	@XMLElement
	public abstract Vector<MouseDragControl> getMouseDragControls();

	@Setter(value = MOUSE_DRAG_CONTROLS)
	public abstract void setMouseDragControls(Vector<MouseDragControl> mouseDragControls);

	@Adder(id = MOUSE_DRAG_CONTROLS)
	public abstract void addToMouseDragControls(MouseDragControl mouseDragControl);

	public abstract void addToMouseDragControls(MouseDragControl mouseDragControl, boolean isPrioritar);

	@Remover(id = MOUSE_DRAG_CONTROLS)
	public abstract void removeFromMouseDragControls(MouseDragControl mouseDragControl);

	@Getter(value = TOOLTIP_TEXT)
	@XMLAttribute
	public abstract String getToolTipText();

	@Setter(value = TOOLTIP_TEXT)
	public abstract void setToolTipText(String tooltipText);

	@Getter(value = VARIABLES, cardinality = Cardinality.LIST)
	@XMLElement
	public abstract Vector<GRVariable> getVariables();

	@Setter(value = VARIABLES)
	public abstract void setVariables(Vector<GRVariable> variables);

	@Adder(id = VARIABLES)
	public abstract void addToVariables(GRVariable v);

	@Remover(id = VARIABLES)
	public abstract void removeFromVariables(GRVariable v);

	// *******************************************************************************
	// * Deletion management
	// *******************************************************************************

	/**
	 * Delete this graphical representation
	 */
	public abstract void delete();

	/**
	 * Return a flag indicating if this graphical representation has been deleted
	 * 
	 * @return
	 */
	public abstract boolean isDeleted();

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public abstract GRParameter parameterWithName(String parameterName);

	public abstract Vector<GRParameter> getAllParameters();

	public abstract void setsWith(GraphicalRepresentation<?> gr);

	public abstract void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters);

	public abstract void initializeDeserialization();

	public abstract void finalizeDeserialization();

	public abstract boolean isDeserializing();

	// *******************************************************************************
	// * Graphical Utils
	// *******************************************************************************

	public abstract boolean hasFloatingLabel();

	public abstract boolean shouldBeDisplayed();

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

	public abstract void notifyChange(GRParameter parameter, Object oldValue, Object newValue);

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
	public abstract Object getValue(BindingVariable<?> variable);

	public abstract void notifiedBindingModelRecreated();

	public abstract void notifyBindingChanged(DataBinding binding);

	public abstract List<GraphicalRepresentation<?>> retrieveAllContainedGR();

	public abstract Iterator<GraphicalRepresentation<?>> allGRIterator();

	public abstract Iterator<GraphicalRepresentation<?>> allContainedGRIterator();

	public abstract Vector<ConstraintDependency> getDependancies();

	public abstract Vector<ConstraintDependency> getAlterings();

	public abstract void declareDependantOf(GraphicalRepresentation<?> aComponent, GRParameter requiringParameter,
			GRParameter requiredParameter) throws DependencyLoopException;

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

	/**
	 * Adds an observer to the set of observers for this object, provided that it is not the same as some observer already in the set. The
	 * order in which notifications will be delivered to multiple observers is not specified. See the class comment.
	 * 
	 * @param o
	 *            an observer to be added.
	 * @throws NullPointerException
	 *             if the parameter o is null.
	 */
	public void addObserver(Observer o);

	/**
	 * Deletes an observer from the set of observers of this object. Passing <CODE>null</CODE> to this method will have no effect.
	 * 
	 * @param o
	 *            the observer to be deleted.
	 */
	public void deleteObserver(Observer o);

	/**
	 * If this object has changed, as indicated by the <code>hasChanged</code> method, then notify all of its observers and then call the
	 * <code>clearChanged</code> method to indicate that this object has no longer changed.
	 * <p>
	 * Each observer has its <code>update</code> method called with two arguments: this observable object and <code>null</code>. In other
	 * words, this method is equivalent to: <blockquote><tt>
	 * notifyObservers(null)</tt></blockquote>
	 * 
	 * @see java.util.Observable#clearChanged()
	 * @see java.util.Observable#hasChanged()
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void notifyObservers();

	/**
	 * If this object has changed, as indicated by the <code>hasChanged</code> method, then notify all of its observers and then call the
	 * <code>clearChanged</code> method to indicate that this object has no longer changed.
	 * <p>
	 * Each observer has its <code>update</code> method called with two arguments: this observable object and the <code>arg</code> argument.
	 * 
	 * @param arg
	 *            any object.
	 * @see java.util.Observable#clearChanged()
	 * @see java.util.Observable#hasChanged()
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void notifyObservers(Object arg);

	/**
	 * Clears the observer list so that this object no longer has any observers.
	 */
	public void deleteObservers();

	/**
	 * Tests if this object has changed.
	 * 
	 * @return <code>true</code> if and only if the <code>setChanged</code> method has been called more recently than the
	 *         <code>clearChanged</code> method on this object; <code>false</code> otherwise.
	 * @see java.util.Observable#clearChanged()
	 * @see java.util.Observable#setChanged()
	 */
	public boolean hasChanged();

	/**
	 * Returns the number of observers of this <tt>Observable</tt> object.
	 * 
	 * @return the number of observers of this object.
	 */
	public int countObservers();

}
