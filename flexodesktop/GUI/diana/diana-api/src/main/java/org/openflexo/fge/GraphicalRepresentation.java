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
import java.awt.Stroke;
import java.beans.PropertyChangeSupport;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.control.CustomMouseClickControl;
import org.openflexo.fge.control.CustomMouseDragControl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

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
public interface GraphicalRepresentation extends FGEObject, Bindable, Observer {

	// Property keys

	public static final String DRAWABLE_KEY = "drawable";
	public static final String DRAWING_KEY = "drawing";

	@PropertyIdentifier(type = String.class)
	public static final String IDENTIFIER_KEY = "identifier";
	@PropertyIdentifier(type = Integer.class)
	public static final String LAYER_KEY = "layer";
	// public static final String HAS_TEXT = "hasText";
	@PropertyIdentifier(type = Double.class)
	public static final String TRANSPARENCY_KEY = "transparency";
	@PropertyIdentifier(type = String.class)
	public static final String TEXT_KEY = "text";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_MULTILINE_ALLOWED_KEY = "isMultilineAllowed";
	@PropertyIdentifier(type = Boolean.class)
	public static final String LINE_WRAP_KEY = "lineWrap";
	@PropertyIdentifier(type = Boolean.class)
	public static final String CONTINUOUS_TEXT_EDITING_KEY = "continuousTextEditing";
	@PropertyIdentifier(type = TextStyle.class)
	public static final String TEXT_STYLE_KEY = "textStyle";
	@PropertyIdentifier(type = Double.class)
	public static final String ABSOLUTE_TEXT_X_KEY = "absoluteTextX";
	@PropertyIdentifier(type = Double.class)
	public static final String ABSOLUTE_TEXT_Y_KEY = "absoluteTextY";
	@PropertyIdentifier(type = HorizontalTextAlignment.class)
	public static final String HORIZONTAL_TEXT_ALIGNEMENT_KEY = "horizontalTextAlignment";
	@PropertyIdentifier(type = VerticalTextAlignment.class)
	public static final String VERTICAL_TEXT_ALIGNEMENT_KEY = "verticalTextAlignment";
	@PropertyIdentifier(type = ParagraphAlignment.class)
	public static final String PARAGRAPH_ALIGNMENT_KEY = "paragraphAlignment";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_SELECTABLE_KEY = "isSelectable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_FOCUSABLE_KEY = "isFocusable";
	// public static final String IS_SELECTED = "isSelected";
	// public static final String IS_FOCUSED = "isFocused";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY = "drawControlPointsWhenFocused";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY = "drawControlPointsWhenSelected";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_READ_ONLY_KEY = "isReadOnly";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_LABEL_EDITABLE_KEY = "isLabelEditable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_VISIBLE_KEY = "isVisible";
	public static final String MOUSE_CLICK_CONTROLS_KEY = "mouseClickControls";
	public static final String MOUSE_DRAG_CONTROLS_KEY = "mouseDragControls";
	@PropertyIdentifier(type = String.class)
	public static final String TOOLTIP_TEXT_KEY = "toolTipText";

	// public static final String VARIABLES = "variables";

	// *******************************************************************************
	// * Inner concepts
	// *******************************************************************************

	public static interface LabelMetricsProvider {
		public Dimension getScaledPreferredDimension(double scale);

	}

	/*public static interface GRParameter {
		public String name();
	}

	public static enum Parameters implements GRParameter {
		identifier, layer, hasText, text, isMultilineAllowed, lineWrap, continuousTextEditing, textStyle, absoluteTextX, // TODO: remove ?
		absoluteTextY, // TODO: remove ?
		horizontalTextAlignment,
		verticalTextAlignment,
		paragraphAlignment,
		isSelectable,
		isFocusable,
		// isSelected,
		// isFocused,
		drawControlPointsWhenFocused,
		drawControlPointsWhenSelected,
		isReadOnly,
		isLabelEditable,
		isVisible, // TODO: remove ?
		mouseClickControls,
		mouseDragControls,
		toolTipText,
		variables;

	}*/

	public static enum ParagraphAlignment {
		LEFT, CENTER, RIGHT, JUSTIFY;
	}

	public static enum HorizontalTextAlignment {
		LEFT, CENTER, RIGHT
	}

	public static enum VerticalTextAlignment {
		TOP, MIDDLE, BOTTOM;
	}

	public static GRParameter<String> IDENTIFIER = GRParameter.getGRParameter(GraphicalRepresentation.class, IDENTIFIER_KEY, String.class);
	public static GRParameter<Integer> LAYER = GRParameter.getGRParameter(GraphicalRepresentation.class, LAYER_KEY, Integer.class);
	public static GRParameter<Double> TRANSPARENCY = GRParameter.getGRParameter(GraphicalRepresentation.class, TRANSPARENCY_KEY,
			Double.class);
	public static GRParameter<String> TEXT = GRParameter.getGRParameter(GraphicalRepresentation.class, TEXT_KEY, String.class);
	public static GRParameter<TextStyle> TEXT_STYLE = GRParameter.getGRParameter(GraphicalRepresentation.class, TEXT_STYLE_KEY,
			TextStyle.class);
	public static GRParameter<HorizontalTextAlignment> HORIZONTAL_TEXT_ALIGNEMENT = GRParameter.getGRParameter(
			GraphicalRepresentation.class, HORIZONTAL_TEXT_ALIGNEMENT_KEY, HorizontalTextAlignment.class);
	public static GRParameter<VerticalTextAlignment> VERTICAL_TEXT_ALIGNEMENT = GRParameter.getGRParameter(GraphicalRepresentation.class,
			VERTICAL_TEXT_ALIGNEMENT_KEY, VerticalTextAlignment.class);
	public static GRParameter<Double> ABSOLUTE_TEXT_X = GRParameter.getGRParameter(GraphicalRepresentation.class, ABSOLUTE_TEXT_X_KEY,
			Double.class);
	public static GRParameter<Double> ABSOLUTE_TEXT_Y = GRParameter.getGRParameter(GraphicalRepresentation.class, ABSOLUTE_TEXT_Y_KEY,
			Double.class);
	public static GRParameter<Boolean> IS_MULTILINE_ALLOWED = GRParameter.getGRParameter(GraphicalRepresentation.class,
			IS_MULTILINE_ALLOWED_KEY, Boolean.class);
	public static GRParameter<Boolean> LINE_WRAP = GRParameter.getGRParameter(GraphicalRepresentation.class, LINE_WRAP_KEY, Boolean.class);
	public static GRParameter<Boolean> CONTINUOUS_TEXT_EDITING = GRParameter.getGRParameter(GraphicalRepresentation.class,
			CONTINUOUS_TEXT_EDITING_KEY, Boolean.class);

	public static GRParameter<ParagraphAlignment> PARAGRAPH_ALIGNEMENT = GRParameter.getGRParameter(GraphicalRepresentation.class,
			PARAGRAPH_ALIGNMENT_KEY, ParagraphAlignment.class);
	public static GRParameter<Boolean> IS_SELECTABLE = GRParameter.getGRParameter(GraphicalRepresentation.class, IS_SELECTABLE_KEY,
			Boolean.class);
	public static GRParameter<Boolean> IS_FOCUSABLE = GRParameter.getGRParameter(GraphicalRepresentation.class, IS_FOCUSABLE_KEY,
			Boolean.class);
	public static GRParameter<Boolean> DRAW_CONTROL_POINTS_WHEN_FOCUSED = GRParameter.getGRParameter(GraphicalRepresentation.class,
			DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY, Boolean.class);
	public static GRParameter<Boolean> DRAW_CONTROL_POINTS_WHEN_SELECTED = GRParameter.getGRParameter(GraphicalRepresentation.class,
			DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY, Boolean.class);
	public static GRParameter<Boolean> IS_READ_ONLY = GRParameter.getGRParameter(GraphicalRepresentation.class, IS_READ_ONLY_KEY,
			Boolean.class);
	public static GRParameter<Boolean> IS_LABEL_EDITABLE = GRParameter.getGRParameter(GraphicalRepresentation.class, IS_LABEL_EDITABLE_KEY,
			Boolean.class);
	public static GRParameter<Boolean> IS_VISIBLE = GRParameter
			.getGRParameter(GraphicalRepresentation.class, IS_VISIBLE_KEY, Boolean.class);
	public static GRParameter<Vector> MOUSE_CLICK_CONTROLS = GRParameter.getGRParameter(GraphicalRepresentation.class,
			MOUSE_CLICK_CONTROLS_KEY, Vector.class);
	public static GRParameter<Vector> MOUSE_DRAG_CONTROLS = GRParameter.getGRParameter(GraphicalRepresentation.class,
			MOUSE_DRAG_CONTROLS_KEY, Vector.class);
	public static GRParameter<String> TOOLTIP_TEXT = GRParameter.getGRParameter(GraphicalRepresentation.class, TOOLTIP_TEXT_KEY,
			String.class);

	// *******************************************************************************
	// * Model
	// *******************************************************************************

	@Getter(value = DRAWING_KEY, ignoreType = true)
	@CloningStrategy(CloningStrategy.StrategyType.REFERENCE)
	public Drawing<?> getDrawing();

	@Setter(value = DRAWING_KEY)
	public void setDrawing(Drawing<?> drawing);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = IDENTIFIER_KEY)
	@XMLAttribute
	public String getIdentifier();

	@Setter(value = IDENTIFIER_KEY)
	public void setIdentifier(String identifier);

	@Getter(value = LAYER_KEY, defaultValue = "0")
	@XMLAttribute
	public int getLayer();

	@Setter(value = LAYER_KEY)
	public void setLayer(int layer);

	/*@Getter(value = HAS_TEXT, defaultValue = "true")
	@XMLAttribute
	public boolean getHasText();

	@Setter(value = HAS_TEXT)
	public void setHasText(boolean hasText);*/

	@Getter(value = TEXT_KEY)
	@XMLAttribute
	public String getText();

	@Setter(value = TEXT_KEY)
	public void setText(String text);

	public String getMultilineText();

	public void setMultilineText(String text);

	@Getter(value = IS_MULTILINE_ALLOWED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsMultilineAllowed();

	@Setter(value = IS_MULTILINE_ALLOWED_KEY)
	public void setIsMultilineAllowed(boolean multilineAllowed);

	@Getter(value = LINE_WRAP_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getLineWrap();

	@Setter(value = LINE_WRAP_KEY)
	public void setLineWrap(boolean lineWrap);

	@Getter(value = CONTINUOUS_TEXT_EDITING_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getContinuousTextEditing();

	@Setter(value = CONTINUOUS_TEXT_EDITING_KEY)
	public void setContinuousTextEditing(boolean continuousTextEditing);

	@Getter(value = TEXT_STYLE_KEY)
	@XMLElement
	public TextStyle getTextStyle();

	@Setter(value = TEXT_STYLE_KEY)
	public void setTextStyle(TextStyle aTextStyle);

	@Getter(value = ABSOLUTE_TEXT_X_KEY, defaultValue = "0")
	@XMLAttribute
	public double getAbsoluteTextX();

	@Setter(value = ABSOLUTE_TEXT_X_KEY)
	public void setAbsoluteTextX(double absoluteTextX);

	@Getter(value = ABSOLUTE_TEXT_Y_KEY, defaultValue = "0")
	@XMLAttribute
	public double getAbsoluteTextY();

	@Setter(value = ABSOLUTE_TEXT_Y_KEY)
	public void setAbsoluteTextY(double absoluteTextY);

	@Getter(value = HORIZONTAL_TEXT_ALIGNEMENT_KEY, defaultValue = "CENTER")
	@XMLAttribute
	public HorizontalTextAlignment getHorizontalTextAlignment();

	@Setter(value = HORIZONTAL_TEXT_ALIGNEMENT_KEY)
	public void setHorizontalTextAlignment(HorizontalTextAlignment horizontalTextAlignment);

	@Getter(value = VERTICAL_TEXT_ALIGNEMENT_KEY, defaultValue = "MIDDLE")
	@XMLAttribute
	public VerticalTextAlignment getVerticalTextAlignment();

	@Setter(value = VERTICAL_TEXT_ALIGNEMENT_KEY)
	public void setVerticalTextAlignment(VerticalTextAlignment verticalTextAlignment);

	@Getter(value = PARAGRAPH_ALIGNMENT_KEY, defaultValue = "CENTER")
	@XMLAttribute
	public ParagraphAlignment getParagraphAlignment();

	@Setter(value = PARAGRAPH_ALIGNMENT_KEY)
	public void setParagraphAlignment(ParagraphAlignment paragraphAlignment);

	@Getter(value = IS_SELECTABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsSelectable();

	@Setter(value = IS_SELECTABLE_KEY)
	public void setIsSelectable(boolean isSelectable);

	@Getter(value = IS_FOCUSABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsFocusable();

	@Setter(value = IS_FOCUSABLE_KEY)
	public void setIsFocusable(boolean isFocusable);

	/*@Getter(value = IS_SELECTED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsSelected();

	@Setter(value = IS_SELECTED)
	public void setIsSelected(boolean aFlag);

	@Getter(value = IS_FOCUSED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsFocused();

	@Setter(value = IS_FOCUSED)
	public void setIsFocused(boolean aFlag);*/

	@Getter(value = DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawControlPointsWhenFocused();

	@Setter(value = DRAW_CONTROL_POINTS_WHEN_FOCUSED_KEY)
	public void setDrawControlPointsWhenFocused(boolean aFlag);

	@Getter(value = DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawControlPointsWhenSelected();

	@Setter(value = DRAW_CONTROL_POINTS_WHEN_SELECTED_KEY)
	public void setDrawControlPointsWhenSelected(boolean aFlag);

	@Getter(value = IS_READ_ONLY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsReadOnly();

	@Setter(value = IS_READ_ONLY_KEY)
	public void setIsReadOnly(boolean readOnly);

	@Getter(value = IS_LABEL_EDITABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsLabelEditable();

	@Setter(value = IS_LABEL_EDITABLE_KEY)
	public void setIsLabelEditable(boolean labelEditable);

	@Getter(value = IS_VISIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsVisible();

	@Setter(value = IS_VISIBLE_KEY)
	public void setIsVisible(boolean isVisible);

	@Getter(value = TRANSPARENCY_KEY, defaultValue = "1.0")
	@XMLAttribute
	public Double getTransparency();

	@Setter(value = TRANSPARENCY_KEY)
	public void setTransparency(Double transparency);

	@Getter(value = MOUSE_CLICK_CONTROLS_KEY, cardinality = Cardinality.LIST, ignoreType = true)
	public Vector<CustomMouseClickControl> getMouseClickControls();

	@Setter(value = MOUSE_CLICK_CONTROLS_KEY)
	public void setMouseClickControls(Vector<CustomMouseClickControl> mouseClickControls);

	@Adder(value = MOUSE_CLICK_CONTROLS_KEY)
	public void addToMouseClickControls(CustomMouseClickControl mouseClickControl);

	public void addToMouseClickControls(CustomMouseClickControl mouseClickControl, boolean isPrioritar);

	@Remover(value = MOUSE_CLICK_CONTROLS_KEY)
	public void removeFromMouseClickControls(CustomMouseClickControl mouseClickControl);

	@Getter(value = MOUSE_DRAG_CONTROLS_KEY, cardinality = Cardinality.LIST, ignoreType = true)
	public Vector<CustomMouseDragControl> getMouseDragControls();

	@Setter(value = MOUSE_DRAG_CONTROLS_KEY)
	public void setMouseDragControls(Vector<CustomMouseDragControl> mouseDragControls);

	@Adder(value = MOUSE_DRAG_CONTROLS_KEY)
	public void addToMouseDragControls(CustomMouseDragControl mouseDragControl);

	public void addToMouseDragControls(CustomMouseDragControl mouseDragControl, boolean isPrioritar);

	@Remover(value = MOUSE_DRAG_CONTROLS_KEY)
	public void removeFromMouseDragControls(CustomMouseDragControl mouseDragControl);

	@Getter(value = TOOLTIP_TEXT_KEY)
	@XMLAttribute
	public String getToolTipText();

	@Setter(value = TOOLTIP_TEXT_KEY)
	public void setToolTipText(String tooltipText);

	// @Getter(value = VARIABLES, cardinality = Cardinality.LIST)
	// @XMLElement
	// public Vector<GRVariable> getVariables();

	// @Setter(value = VARIABLES)
	// public void setVariables(Vector<GRVariable> variables);

	// @Adder(value = VARIABLES)
	// public void addToVariables(GRVariable v);

	// @Remover(value = VARIABLES)
	// public void removeFromVariables(GRVariable v);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	// public GRParameter<?> parameterWithName(String parameterName);

	// public Collection<GRParameter<?>> getAllParameters();

	public void setsWith(GraphicalRepresentation gr);

	public void setsWith(GraphicalRepresentation gr, GRParameter<?>... exceptedParameters);

	public void initializeDeserialization();

	public void finalizeDeserialization();

	public boolean isDeserializing();

	// *******************************************************************************
	// * Graphical Utils
	// *******************************************************************************

	// public boolean hasFloatingLabel();

	// public boolean shouldBeDisplayed();

	/*public DrawingGraphicalRepresentation getDrawingGraphicalRepresentation();

	public GraphicalRepresentation getGraphicalRepresentation(Object drawable);

	public List<? extends Object> getContainedObjects(Object drawable);

	public Object getContainer(Object drawable);

	public List<? extends Object> getContainedObjects();

	public List<GraphicalRepresentation> getContainedGraphicalRepresentations();

	public List<GraphicalRepresentation> getOrderedContainedGraphicalRepresentations();*/

	public void moveToTop(GraphicalRepresentation gr);

	// public int getOrder(GraphicalRepresentation child1, GraphicalRepresentation child2);

	// public int getLayerOrder();

	// public int getIndex();

	/*public Object getContainer();

	public GraphicalRepresentation getContainerGraphicalRepresentation();

	public GraphicalRepresentation getParentGraphicalRepresentation();

	public boolean contains(GraphicalRepresentation gr);

	public boolean contains(Object drawable);

	public List<Object> getAncestors();

	public List<Object> getAncestors(boolean forceRecompute);

	public boolean isConnectedToDrawing();

	public boolean isAncestorOf(GraphicalRepresentation child);*/

	// public boolean isPointVisible(FGEPoint p);

	// public ShapeGraphicalRepresentation shapeHiding(FGEPoint p);

	// public boolean hasText();

	/*public int getViewX(double scale);

	public int getViewY(double scale);

	public int getViewWidth(double scale);

	public int getViewHeight(double scale);

	public Rectangle getViewBounds(double scale);

	public FGERectangle getNormalizedBounds();*/

	/*public Point getLabelLocation(double scale);

	public Dimension getLabelDimension(double scale);

	public void setLabelLocation(Point point, double scale);

	public Rectangle getLabelBounds(double scale);

	public void paint(Graphics g, DianaEditor controller);*/

	/*public void notifyChange(GRParameter parameter, Object oldValue, Object newValue);

	public void notifyChange(GRParameter parameter);

	public void notifyAttributeChange(GRParameter parameter);

	public void notify(FGENotification notification);*/

	// @Override
	// public String getInspectorName();

	public boolean isShape();

	public boolean isConnector();

	public boolean isDrawing();

	// public void notifyDrawableAdded(GraphicalRepresentation addedGR);

	// public void notifyDrawableRemoved(GraphicalRepresentation removedGR);

	@Override
	public void update(Observable observable, Object notification);

	/*public Point convertNormalizedPointToViewCoordinates(double x, double y, double scale);

	public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale);

	public AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale);

	public AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale);

	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale);

	public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, GraphicalRepresentation source, double scale);

	public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, GraphicalRepresentation destination, double scale);

	public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, GraphicalRepresentation destination, double scale);

	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, GraphicalRepresentation destination,
			double scale);

	public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, GraphicalRepresentation source, double scale);
	*/

	// public boolean isRegistered();

	// public void setRegistered(boolean aFlag);

	public CustomMouseClickControl createMouseClickControl();

	public void deleteMouseClickControl(CustomMouseClickControl mouseClickControl);

	public boolean isMouseClickControlDeletable(CustomMouseClickControl mouseClickControl);

	public CustomMouseDragControl createMouseDragControl();

	public void deleteMouseDragControl(CustomMouseDragControl mouseDragControl);

	public boolean isMouseDragControlDeletable(CustomMouseDragControl mouseDragControl);

	// public boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	/*public void notifyLabelWillBeEdited();

	public void notifyLabelHasBeenEdited();

	public void notifyLabelWillMove();

	public void notifyLabelHasMoved();*/

	// Override when required
	// public void notifyObjectHierarchyWillBeUpdated();

	// Override when required
	// public void notifyObjectHierarchyHasBeenUpdated();

	// public void performRandomLayout(double width, double height);

	// public void performAutoLayout(double width, double height);

	public Stroke getSpecificStroke();

	public void setSpecificStroke(Stroke aStroke);

	/*public boolean isRootGraphicalRepresentation();

	public GraphicalRepresentation getRootGraphicalRepresentation();*/

	public void createBindingModel();

	@Override
	public BindingModel getBindingModel();

	@Override
	public BindingFactory getBindingFactory();

	public void updateBindingModel();

	/*@Override
	public Object getValue(BindingVariable variable);*/

	public void notifiedBindingModelRecreated();

	public void notifyBindingChanged(DataBinding<?> binding);

	// public List<GraphicalRepresentation> retrieveAllContainedGR();

	// public Iterator<GraphicalRepresentation> allGRIterator();

	// public Iterator<GraphicalRepresentation> allContainedGRIterator();

	/*public Vector<ConstraintDependency> getDependancies();

	public Vector<ConstraintDependency> getAlterings();

	public void declareDependantOf(GraphicalRepresentation aComponent, GRParameter requiringParameter, GRParameter requiredParameter)
			throws DependencyLoopException;
	*/
	// public GRVariable createStringVariable();

	// public GRVariable createIntegerVariable();

	// public void deleteVariable(GRVariable v);

	@Override
	public PropertyChangeSupport getPropertyChangeSupport();

	@Override
	public String getDeletedProperty();

	/*public boolean isValidated();

	public void setValidated(boolean validated);

	public LabelMetricsProvider getLabelMetricsProvider();

	public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider);

	public int getAvailableLabelWidth(double scale);*/

}
