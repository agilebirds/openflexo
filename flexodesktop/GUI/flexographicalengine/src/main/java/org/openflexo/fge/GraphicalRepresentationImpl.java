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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GRBindingFactory.ComponentPathElement;
import org.openflexo.fge.GRBindingFactory.ComponentsBindingVariable;
import org.openflexo.fge.GRVariable.GRVariableType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawUtils;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.GraphicalRepresentationAdded;
import org.openflexo.fge.notifications.GraphicalRepresentationDeleted;
import org.openflexo.fge.notifications.GraphicalRepresentationRemoved;
import org.openflexo.fge.notifications.LabelHasEdited;
import org.openflexo.fge.notifications.LabelHasMoved;
import org.openflexo.fge.notifications.LabelWillEdit;
import org.openflexo.fge.notifications.LabelWillMove;
import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.xmlcode.StringEncoder;

public abstract class GraphicalRepresentationImpl<O> extends DefaultInspectableObject implements GraphicalRepresentation<O> {

	static final Logger logger = Logger.getLogger(GraphicalRepresentationUtils.class.getPackage().getName());

	static {
		StringEncoder.getDefaultInstance()._addConverter(GraphicalRepresentationUtils.POINT_CONVERTER);
		StringEncoder.getDefaultInstance()._addConverter(GraphicalRepresentationUtils.RECT_POLYLIN_CONVERTER);
	}

	private Stroke specificStroke = null;

	// *******************************************************************************
	// * Parameters *
	// *******************************************************************************

	protected int layer;

	private TextStyle textStyle = TextStyleImpl.makeDefault();
	private String text;
	private boolean multilineAllowed = false;
	private boolean lineWrap = false;
	private boolean continuousTextEditing = true;
	private double absoluteTextX = 0;
	private double absoluteTextY = 0;
	private HorizontalTextAlignment horizontalTextAlignment = HorizontalTextAlignment.CENTER;
	private VerticalTextAlignment verticalTextAlignment = VerticalTextAlignment.MIDDLE;
	private ParagraphAlignment paragraphAlignment = ParagraphAlignment.CENTER;

	private boolean isSelectable = true;
	private boolean isFocusable = true;
	private boolean isSelected = false;
	private boolean isFocused = false;
	private boolean drawControlPointsWhenFocused = true;
	private boolean drawControlPointsWhenSelected = true;

	protected boolean isVisible = true;
	private boolean readOnly = false;
	private boolean labelEditable = true;

	private Vector<MouseClickControl> mouseClickControls;
	private Vector<MouseDragControl> mouseDragControls;

	private PropertyChangeSupport pcSupport;

	private String toolTipText = null;

	private Vector<ConstraintDependency> dependancies;
	private Vector<ConstraintDependency> alterings;

	// *******************************************************************************
	// * Inner classes *
	// *******************************************************************************

	// *******************************************************************************
	// * Fields *
	// *******************************************************************************

	private O drawable;
	private Drawing<?> drawing;

	private Vector<Object> ancestors;

	private boolean isRegistered = false;
	private boolean isDeleted = false;
	private boolean hasText = true;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	protected GraphicalRepresentationImpl(O aDrawable, Drawing<?> aDrawing) {
		super();
		drawable = aDrawable;
		drawing = aDrawing;
		textStyle = TextStyleImpl.makeDefault();
		// textStyle.setGraphicalRepresentation(this);
		if (textStyle != null) {
			textStyle.addObserver(this);
		}

		mouseClickControls = new Vector<MouseClickControl>();
		mouseDragControls = new Vector<MouseDragControl>();

		dependancies = new Vector<ConstraintDependency>();
		alterings = new Vector<ConstraintDependency>();

	}

	// ***************************************************************************
	// * Deletion *
	// ***************************************************************************

	@Override
	public void delete() {
		isDeleted = true;
		if (textStyle != null) {
			textStyle.deleteObserver(this);
		}
		_bindingModel = null;
		setChanged();
		notifyObservers(new GraphicalRepresentationDeleted(this));
		deleteObservers();
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public GRParameter parameterWithName(String parameterName) {
		if (parameterName == null) {
			return null;
		}
		for (GRParameter param : getAllParameters()) {
			if (param.name().equals(parameterName)) {
				return param;
			}
		}
		return null;
	}

	@Override
	public Vector<GRParameter> getAllParameters() {
		Vector<GRParameter> returned = new Vector<GRParameter>();
		Parameters[] allParams = Parameters.values();
		for (int i = 0; i < allParams.length; i++) {
			returned.add(allParams[i]);
		}
		return returned;
	}

	// ***************************************************************************
	// * Cloning *
	// ***************************************************************************

	@Override
	public void setsWith(GraphicalRepresentation<?> gr) {
		if (gr instanceof GraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				if (p != Parameters.identifier && p != Parameters.mouseClickControls && p != Parameters.mouseDragControls) {
					_setParameterValueWith(p, gr);
				}
			}
		}
	}

	@Override
	public void setsWith(GraphicalRepresentation<?> gr, GRParameter... exceptedParameters) {
		if (gr instanceof GraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				boolean excepted = false;
				for (GRParameter ep : exceptedParameters) {
					if (p == ep) {
						excepted = true;
					}
				}
				if (p != Parameters.mouseClickControls && p != Parameters.mouseDragControls && p != Parameters.identifier && !excepted) {
					_setParameterValueWith(p, gr);
				}
			}
		}
	}

	protected void _setParameterValueWith(Enum<?> parameterKey, GraphicalRepresentation<?> gr) {
		Class<?> type = getTypeForKey(parameterKey.name());
		if (type.isPrimitive()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Primitive: " + parameterKey.name() + " of " + type + " values " + gr.valueForKey(parameterKey.name()));
			}
			if (type == Boolean.TYPE) {
				setBooleanValueForKey(gr.booleanValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Integer.TYPE) {
				setIntegerValueForKey(gr.integerValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Short.TYPE) {
				setShortValueForKey(gr.shortValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Long.TYPE) {
				setLongValueForKey(gr.longValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Float.TYPE) {
				setFloatValueForKey(gr.floatValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Double.TYPE) {
				setDoubleValueForKey(gr.doubleValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Byte.TYPE) {
				setByteValueForKey(gr.byteValueForKey(parameterKey.name()), parameterKey.name());
			}
			if (type == Character.TYPE) {
				setCharacterForKey(gr.characterForKey(parameterKey.name()), parameterKey.name());
			}
		} else {
			Object value = gr.objectForKey(parameterKey.name());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Object: " + parameterKey.name() + " of " + type);
			}
			if (value instanceof Cloneable) { // Try to clone...
				try {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Cloning object: " + parameterKey.name() + " of " + type);
					}
					Method cloneMethod = value.getClass().getMethod("clone", (Class[]) null);
					value = cloneMethod.invoke(value, (Object[]) null);
				} catch (Exception e) {
					logger.warning("Cannot clone parameter: " + value);
					e.printStackTrace();
				}
			}
			Object currentValue = objectForKey(parameterKey.name());
			if (value != currentValue) {
				setObjectForKey(value, parameterKey.name());
			}
		}
	}

	// *************************************************************************
	// * Serialization *
	// *************************************************************************

	private boolean isDeserializing = false;

	@Override
	public void initializeDeserialization() {
		isDeserializing = true;
	}

	@Override
	public void finalizeDeserialization() {
		// logger.info("Hop: finalizeDeserialization for "+this+" root is "+getRootGraphicalRepresentation());

		if (getRootGraphicalRepresentation().getBindingModel() == null) {
			((GraphicalRepresentationImpl<?>) getRootGraphicalRepresentation()).createBindingModel();
		}

		isDeserializing = false;
	}

	@Override
	public boolean isDeserializing() {
		return isDeserializing;
	}

	// *******************************************************************************
	// * Instance methods *
	// *******************************************************************************

	private String identifier = null;

	public void resetToDefaultIdentifier() {
		identifier = retrieveDefaultIdentifier();
	}

	private String retrieveDefaultIdentifier() {
		if (getParentGraphicalRepresentation() == null) {
			return "root";
		} else {
			// int index = getParentGraphicalRepresentation().getContainedGraphicalRepresentations().indexOf(this);
			int index = getIndex();
			if (getParentGraphicalRepresentation().getParentGraphicalRepresentation() == null) {
				// logger.info("retrieveDefaultIdentifier return "+index);
				return "object_" + index;
			} else {
				return ((GraphicalRepresentationImpl<?>) getParentGraphicalRepresentation()).retrieveDefaultIdentifier() + "_" + index;
			}
		}
	}

	@Override
	public String getIdentifier() {
		if (identifier == null) {
			return retrieveDefaultIdentifier();
		}
		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		FGENotification notification = requireChange(Parameters.identifier, identifier);
		if (notification != null) {
			this.identifier = identifier;
			hasChanged(notification);
			updateBindingModel();
		}
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public void setLayer(int layer) {
		/*
		 * Vector<GraphicalRepresentation> allGRInSameLayer = null; GraphicalRepresentation<?> parent = getParentGraphicalRepresentation();
		 * if (parent != null) { for (GraphicalRepresentation<?> child : parent.getContainedGraphicalRepresentations()) { if
		 * (child.getLayer() == layer) { System.out.println("Il faudrait changer la layer de "+child +" en meme temps"); if
		 * (allGRInSameLayer == null) allGRInSameLayer = new Vector<GraphicalRepresentation>(); allGRInSameLayer.add(child); } } }
		 * 
		 * if (allGRInSameLayer == null || allGRInSameLayer.size() == 1) { FGENotification notification = requireChange(Parameters.layer,
		 * layer); if (notification != null) { this.layer = layer; hasChanged(notification); } } else { for (GraphicalRepresentation<?>
		 * child : allGRInSameLayer) { child.proceedSetLayer(layer); } }
		 */
		FGENotification notification = requireChange(Parameters.layer, layer);
		if (notification != null) {
			this.layer = layer;
			hasChanged(notification);
		}
	}

	/*
	 * private void proceedSetLayer(int layer) { int oldLayer = layer; this.layer = layer; hasChanged(new
	 * FGENotification("layer",oldLayer,layer)); }
	 */

	@Override
	public final O getDrawable() {
		return drawable;
	}

	@Override
	public final void setDrawable(O aDrawable) {
		drawable = aDrawable;
	}

	@Override
	public Drawing<?> getDrawing() {
		return drawing;
	}

	@Override
	public void setDrawing(Drawing<?> drawing) {
		this.drawing = drawing;
	}

	@Override
	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return getDrawing().getDrawingGraphicalRepresentation();
	}

	@Override
	public <O2> GraphicalRepresentation<O2> getGraphicalRepresentation(O2 drawable) {
		return getDrawing().getGraphicalRepresentation(drawable);
	}

	@Override
	public List<? extends Object> getContainedObjects(Object drawable) {
		return getDrawing().getContainedObjects(drawable);
	}

	@Override
	public Object getContainer(Object drawable) {
		if (getDrawing() == null) {
			return null;
		}
		return getDrawing().getContainer(drawable);
	}

	@Override
	public List<? extends Object> getContainedObjects() {
		if (getDrawable() == null) {
			return null;
		}
		if (getDrawing() == null) {
			return null;
		}
		return getDrawing().getContainedObjects(getDrawable());
	}

	@Override
	public List<GraphicalRepresentation<?>> getContainedGraphicalRepresentations() {
		// Indirection added to separate callers that require an ordered list of contained GR and those who do not care. Wa may then later
		// reimplement these methods to optimizer perfs.
		return getOrderedContainedGraphicalRepresentations();
	}

	@Override
	public List<GraphicalRepresentation<?>> getOrderedContainedGraphicalRepresentations() {
		if (!isValidated()) {
			return GraphicalRepresentationUtils.EMPTY_GR_VECTOR;
		}

		if (getContainedObjects() == null) {
			return null;
		}

		List<GraphicalRepresentation<?>> toRemove = new ArrayList<GraphicalRepresentation<?>>(getOrderedContainedGR());

		for (Object o : getContainedObjects()) {
			GraphicalRepresentation<Object> gr = getDrawing().getGraphicalRepresentation(o);
			if (gr != null) {
				if (orderedContainedGR.contains(gr)) {
					// OK, fine
					toRemove.remove(gr);
				} else {
					orderedContainedGR.add(gr);
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find any gr for " + o);
				}
			}
		}

		for (GraphicalRepresentation<?> c : toRemove) {
			orderedContainedGR.remove(c);
		}

		return orderedContainedGR;
	}

	private List<GraphicalRepresentation<?>> orderedContainedGR = null;

	private List<GraphicalRepresentation<?>> getOrderedContainedGR() {
		if (!isValidated()) {
			logger.warning("GR " + this + " is not validated");
			return GraphicalRepresentationUtils.EMPTY_GR_VECTOR;
		}
		if (orderedContainedGR == null) {
			orderedContainedGR = new ArrayList<GraphicalRepresentation<?>>();
			for (GraphicalRepresentation<?> c : getOrderedContainedGraphicalRepresentations()) {
				if (!orderedContainedGR.contains(c)) {
					orderedContainedGR.add(c);
				}
			}
		}
		return orderedContainedGR;
	}

	@Override
	public void moveToTop(GraphicalRepresentation<?> gr) {
		// TODO: something to do here
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("moveToTop temporarily desactivated");
		}
		/*if (!gr.isValidated()) {
			logger.warning("GR " + gr + " is not validated");
		}
		if (getOrderedContainedGR().contains(gr)) {
			getOrderedContainedGR().remove(gr);
		}
		getOrderedContainedGR().add(gr);*/
	}

	@Override
	public int getOrder(GraphicalRepresentation<?> child1, GraphicalRepresentation<?> child2) {
		List<GraphicalRepresentation<?>> orderedGRList = getOrderedContainedGraphicalRepresentations();

		// logger.info("getOrder: "+orderedGRList);
		if (!orderedGRList.contains(child1)) {
			return 0;
		}
		if (!orderedGRList.contains(child2)) {
			return 0;
		}
		/*
		 * if (orderedGRList.indexOf(child1)<orderedGRList.indexOf(child2)) logger
		 * .info("Ordering: "+child1+" on top of "+child2+" child1:"+orderedGRList
		 * .indexOf(child1)+" child2:"+orderedGRList.indexOf(child2)); else logger
		 * .info("Ordering: "+child2+" on top of "+child1+" child1:"+orderedGRList
		 * .indexOf(child1)+" child2:"+orderedGRList.indexOf(child2));
		 */
		return orderedGRList.indexOf(child1) - orderedGRList.indexOf(child2);
	}

	@Override
	public int getLayerOrder() {
		if (!isValidated()) {
			return -1;
		}
		if (getParentGraphicalRepresentation() == null) {
			return -1;
		}
		List<GraphicalRepresentation<?>> orderedGRList = getParentGraphicalRepresentation().getOrderedContainedGraphicalRepresentations();
		/*System.out.println("Index of " + this + " inside parent " + getParentGraphicalRepresentation() + " is "
				+ orderedGRList.indexOf(this));
		for (GraphicalRepresentation gr : orderedGRList) {
			System.out.println("> " + gr + " : is this=" + gr.equals(this));
		}*/
		return orderedGRList.indexOf(this);
	}

	@Override
	public int getIndex() {
		return getLayerOrder();
	}

	@Override
	public Object getContainer() {
		if (drawing == null) {
			return null;
		}
		if (drawable == null) {
			return null;
		}
		return drawing.getContainer(drawable);
	}

	@Override
	public GraphicalRepresentation<?> getContainerGraphicalRepresentation() {
		if (!isValidated()) {
			return null;
		}
		Object container = getContainer();
		if (container == null) {
			// logger.warning("No container for "+this);
			return null;
		}
		return getDrawing().getGraphicalRepresentation(getContainer());
	}

	@Override
	public GraphicalRepresentation<?> getParentGraphicalRepresentation() {
		return getContainerGraphicalRepresentation();
	}

	@Override
	public boolean contains(GraphicalRepresentation<?> gr) {
		if (!isValidated()) {
			return false;
		}
		return getContainedGraphicalRepresentations().contains(gr);
	}

	@Override
	public boolean contains(Object drawable) {
		if (!isValidated()) {
			return false;
		}
		return getContainedGraphicalRepresentations().contains(getGraphicalRepresentation(drawable));
	}

	@Override
	public List<Object> getAncestors() {
		if (!isValidated()) {
			return GraphicalRepresentationUtils.EMPTY_VECTOR;
		}
		return getAncestors(false);
	}

	@Override
	public List<Object> getAncestors(boolean forceRecompute) {
		if (!isValidated()) {
			return GraphicalRepresentationUtils.EMPTY_VECTOR;
		}
		if (getDrawing() == null) {
			return GraphicalRepresentationUtils.EMPTY_VECTOR;
		}
		if (ancestors == null || forceRecompute) {
			ancestors = new Vector<Object>();
			Object current = getDrawable();
			while (current != getDrawing().getModel()) {
				Object container = drawing.getContainer(current);
				if (container == null) {
					// throw new
					// IllegalArgumentException("Drawable "+current+" has no container");
					return ancestors;
				}
				ancestors.add(container);
				current = container;
			}
		}
		return ancestors;
	}

	@Override
	public boolean isConnectedToDrawing() {
		if (!isValidated()) {
			return false;
		}
		Object current = getDrawable();
		while (current != getDrawing().getModel()) {
			Object container = drawing.getContainer(current);
			if (container == null) {
				return false;
			}
			current = container;
		}
		return true;
	}

	@Override
	public boolean isAncestorOf(GraphicalRepresentation<?> child) {
		if (!isValidated()) {
			return false;
		}
		GraphicalRepresentation<?> father = child.getContainerGraphicalRepresentation();
		while (father != null) {
			if (father == this) {
				return true;
			}
			father = father.getContainerGraphicalRepresentation();
		}
		return false;
	}

	@Override
	public boolean isPointVisible(FGEPoint p) {
		if (!getIsVisible()) {
			return false;
		}

		/*
		 * if (this instanceof ShapeGraphicalRepresentation) { // Be careful, maybe this point is just on outline // So translate it to the
		 * center to be sure FGEPoint center = ((ShapeGraphicalRepresentation)this).getShape ().getShape().getCenter(); p.x =
		 * p.x+FGEGeometricObject.EPSILON*(center.x-p.x); p.y = p.y+FGEGeometricObject.EPSILON*(center.y-p.y); }
		 * 
		 * DrawingGraphicalRepresentation<?> drawingGR = getDrawingGraphicalRepresentation(); ShapeGraphicalRepresentation<?> topLevelShape
		 * = drawingGR.getTopLevelShapeGraphicalRepresentation( convertNormalizedPoint(this, p, drawingGR));
		 */

		GraphicalRepresentation<?> topLevelShape = shapeHiding(p);

		return topLevelShape == null;
	}

	@Override
	public ShapeGraphicalRepresentation<?> shapeHiding(FGEPoint p) {
		if (!getIsVisible()) {
			return null;
		}

		if (this instanceof ShapeGraphicalRepresentation) {
			// Be careful, maybe this point is just on outline
			// So translate it to the center to be sure
			FGEPoint center = ((ShapeGraphicalRepresentation<?>) this).getShape().getShape().getCenter();
			p.x = p.x + FGEGeometricObject.EPSILON * (center.x - p.x);
			p.y = p.y + FGEGeometricObject.EPSILON * (center.y - p.y);
		}

		DrawingGraphicalRepresentation<?> drawingGR = getDrawingGraphicalRepresentation();
		ShapeGraphicalRepresentation<?> topLevelShape = drawingGR.getTopLevelShapeGraphicalRepresentation(GraphicalRepresentationUtils
				.convertNormalizedPoint(this, p, drawingGR));

		if (topLevelShape == this || topLevelShape == getParentGraphicalRepresentation()) {
			return null;
		}

		return topLevelShape;
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public TextStyle getTextStyle() {
		return textStyle;
	}

	@Override
	public void setTextStyle(TextStyle aTextStyle) {
		FGENotification notification = requireChange(Parameters.textStyle, aTextStyle, false);
		if (notification != null) {
			if (textStyle != null) {
				textStyle.deleteObserver(this);
			}
			this.textStyle = aTextStyle;
			if (aTextStyle != null) {
				aTextStyle.addObserver(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public double getAbsoluteTextX() {
		return absoluteTextX;
	}

	@Override
	public final void setAbsoluteTextX(double absoluteTextX) {
		/*
		 * if (absoluteTextX < 0) absoluteTextX = 0; if (getContainerGraphicalRepresentation() != null &&
		 * getContainerGraphicalRepresentation() instanceof ShapeGraphicalRepresentation && absoluteTextX > ((ShapeGraphicalRepresentation
		 * <?>)getContainerGraphicalRepresentation()).getWidth()) { absoluteTextX =
		 * ((ShapeGraphicalRepresentation<?>)getContainerGraphicalRepresentation ()).getWidth(); }
		 */
		FGENotification notification = requireChange(Parameters.absoluteTextX, absoluteTextX);
		if (notification != null) {
			setAbsoluteTextXNoNotification(absoluteTextX);
			hasChanged(notification);
		}
	}

	public void setAbsoluteTextXNoNotification(double absoluteTextX) {
		this.absoluteTextX = absoluteTextX;
	}

	@Override
	public double getAbsoluteTextY() {
		return absoluteTextY;
	}

	@Override
	public final void setAbsoluteTextY(double absoluteTextY) {
		FGENotification notification = requireChange(Parameters.absoluteTextY, absoluteTextY);
		if (notification != null) {
			setAbsoluteTextYNoNotification(absoluteTextY);
			hasChanged(notification);
		}
	}

	public void setAbsoluteTextYNoNotification(double absoluteTextY) {
		this.absoluteTextY = absoluteTextY;
	}

	@Override
	public boolean getIsFocusable() {
		return isFocusable;
	}

	@Override
	public void setIsFocusable(boolean isFocusable) {
		FGENotification notification = requireChange(Parameters.isFocusable, isFocusable);
		if (notification != null) {
			this.isFocusable = isFocusable;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getDrawControlPointsWhenFocused() {
		return drawControlPointsWhenFocused;
	}

	@Override
	public void setDrawControlPointsWhenFocused(boolean aFlag) {
		FGENotification notification = requireChange(Parameters.drawControlPointsWhenFocused, aFlag);
		if (notification != null) {
			drawControlPointsWhenFocused = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsSelectable() {
		return isSelectable;
	}

	@Override
	public void setIsSelectable(boolean isSelectable) {
		FGENotification notification = requireChange(Parameters.isSelectable, isSelectable);
		if (notification != null) {
			this.isSelectable = isSelectable;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getDrawControlPointsWhenSelected() {
		return drawControlPointsWhenSelected;
	}

	@Override
	public void setDrawControlPointsWhenSelected(boolean aFlag) {
		FGENotification notification = requireChange(Parameters.drawControlPointsWhenSelected, aFlag);
		if (notification != null) {
			drawControlPointsWhenSelected = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		FGENotification notification = requireChange(Parameters.text, text);
		if (notification != null) {
			setTextNoNotification(text);
			hasChanged(notification);
		}
	}

	public void setTextNoNotification(String text) {
		this.text = text;
	}

	@Override
	public String getMultilineText() {
		return getText();
	}

	@Override
	public void setMultilineText(String text) {
		setText(text);
	}

	@Override
	public boolean getHasText() {
		return hasText;
	}

	@Override
	public void setHasText(boolean hasText) {
		FGENotification notification = requireChange(Parameters.hasText, hasText);
		if (notification != null) {
			this.hasText = hasText;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsMultilineAllowed() {
		return multilineAllowed;
	}

	@Override
	public void setIsMultilineAllowed(boolean multilineAllowed) {
		FGENotification notification = requireChange(Parameters.isMultilineAllowed, multilineAllowed);
		if (notification != null) {
			this.multilineAllowed = multilineAllowed;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getLineWrap() {
		return lineWrap;
	}

	@Override
	public void setLineWrap(boolean lineWrap) {
		FGENotification notification = requireChange(Parameters.lineWrap, lineWrap);
		if (notification != null) {
			this.lineWrap = lineWrap;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getContinuousTextEditing() {
		return continuousTextEditing;
	}

	@Override
	public void setContinuousTextEditing(boolean continuousTextEditing) {
		FGENotification notification = requireChange(Parameters.continuousTextEditing, continuousTextEditing);
		if (notification != null) {
			this.continuousTextEditing = continuousTextEditing;
			hasChanged(notification);
		}
	}

	@Override
	public abstract boolean hasFloatingLabel();

	@Override
	public boolean getIsFocused() {
		return isFocused;
	}

	@Override
	public void setIsFocused(boolean aFlag) {
		FGENotification notification = requireChange(Parameters.isFocused, aFlag);
		if (notification != null) {
			isFocused = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsSelected() {
		return isSelected;
	}

	@Override
	public void setIsSelected(boolean aFlag) {
		if (getParentGraphicalRepresentation() != null && aFlag) {
			getParentGraphicalRepresentation().moveToTop(this);
		}
		FGENotification notification = requireChange(Parameters.isSelected, aFlag);
		if (notification != null) {
			isSelected = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsReadOnly() {
		return readOnly;
	}

	@Override
	public void setIsReadOnly(boolean readOnly) {
		FGENotification notification = requireChange(Parameters.isReadOnly, readOnly);
		if (notification != null) {
			this.readOnly = readOnly;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsLabelEditable() {
		return labelEditable;
	}

	@Override
	public void setIsLabelEditable(boolean labelEditable) {
		FGENotification notification = requireChange(Parameters.isLabelEditable, labelEditable);
		if (notification != null) {
			this.labelEditable = labelEditable;
			hasChanged(notification);
		}
	}

	@Override
	public boolean shouldBeDisplayed() {
		// logger.info("For "+this+" getIsVisible()="+getIsVisible()+" getContainerGraphicalRepresentation()="+getContainerGraphicalRepresentation());
		return getIsVisible() && getContainerGraphicalRepresentation() != null && getContainerGraphicalRepresentation().shouldBeDisplayed();
	}

	@Override
	public boolean getIsVisible() {
		if (isDeserializing()) {
			return isVisible;
		}
		return isVisible;
	}

	@Override
	public void setIsVisible(boolean isVisible) {
		FGENotification notification = requireChange(Parameters.isVisible, isVisible);
		if (notification != null) {
			this.isVisible = isVisible;
			hasChanged(notification);
		}
	}

	@Override
	public boolean hasText() {
		return getText() != null && !getText().trim().equals("");
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public abstract int getViewX(double scale);

	@Override
	public abstract int getViewY(double scale);

	@Override
	public abstract int getViewWidth(double scale);

	@Override
	public abstract int getViewHeight(double scale);

	@Override
	public Rectangle getViewBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = getViewX(scale);
		bounds.y = getViewY(scale);
		bounds.width = getViewWidth(scale);
		bounds.height = getViewHeight(scale);

		return bounds;
	}

	@Override
	public FGERectangle getNormalizedBounds() {
		return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
	}

	@Override
	public Point getLabelLocation(double scale) {
		return new Point((int) (getAbsoluteTextX() * scale + getViewX(scale)), (int) (getAbsoluteTextY() * scale + getViewY(scale)));
	}

	@Override
	public Dimension getLabelDimension(double scale) {
		Dimension d;
		if (labelMetricsProvider != null) {
			d = labelMetricsProvider.getScaledPreferredDimension(scale);
		} else {
			d = new Dimension(0, 0);
		}
		return d;
	}

	@Override
	public void setLabelLocation(Point point, double scale) {
		setAbsoluteTextX((point.x - getViewX(scale)) / scale);
		setAbsoluteTextY((point.y - getViewY(scale)) / scale);
	}

	@Override
	public Rectangle getLabelBounds(double scale) {
		return new Rectangle(getLabelLocation(scale), getLabelDimension(scale));
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public void paint(Graphics g, DrawingController<?> controller) {
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
	}

	// *******************************************************************************
	// * Utils *
	// *******************************************************************************

	@Override
	public void notifyChange(GRParameter parameter, Object oldValue, Object newValue) {
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		hasChanged(new FGENotification(parameter, oldValue, newValue));
		/*propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGENotification(parameter, oldValue, newValue));*/
	}

	@Override
	public void notifyChange(GRParameter parameter) {
		notifyChange(parameter, null, null);
	}

	@Override
	public void notifyAttributeChange(GRParameter parameter) {
		notifyChange(parameter);
	}

	/**
	 * Build and return a new notification for a potential parameter change, given a new value. Change is required if values are different
	 * considering equals() method
	 * 
	 * @param parameter
	 * @param value
	 * @param useEquals
	 * @return
	 */
	protected FGENotification requireChange(GRParameter parameter, Object value) {
		return requireChange(parameter, value, true);
	}

	/**
	 * Build and return a new notification for a potential parameter change, given a new value. Change is required if values are different
	 * considering:
	 * <ul>
	 * <li>If useEquals flag set to true, equals() method
	 * <li>
	 * <li>If useEquals flag set to false, same reference for objects, same value for primitives</li>
	 * </ul>
	 * 
	 * @param parameter
	 * @param value
	 * @param useEquals
	 * @return
	 */
	protected FGENotification requireChange(GRParameter parameter, Object value, boolean useEquals) {
		Class<?> type = getTypeForKey(parameter.name());
		Object oldValue = null;
		if (type.isPrimitive()) {
			if (type == Boolean.TYPE) {
				oldValue = booleanValueForKey(parameter.name());
			}
			if (type == Integer.TYPE) {
				oldValue = integerValueForKey(parameter.name());
			}
			if (type == Short.TYPE) {
				oldValue = shortValueForKey(parameter.name());
			}
			if (type == Long.TYPE) {
				oldValue = longValueForKey(parameter.name());
			}
			if (type == Float.TYPE) {
				oldValue = floatValueForKey(parameter.name());
			}
			if (type == Double.TYPE) {
				oldValue = doubleValueForKey(parameter.name());
			}
			if (type == Byte.TYPE) {
				oldValue = byteValueForKey(parameter.name());
			}
			if (type == Character.TYPE) {
				oldValue = characterForKey(parameter.name());
			}
		} else {
			oldValue = objectForKey(parameter.name());
			if (value == oldValue && value != null && !value.getClass().isEnum()) {
				// logger.warning(parameter.name() + ": require change called for same object: aren't you wrong ???");
			}
		}
		// System.out.println("param: "+parameterKey.name()+" value="+oldValue+" value="+value);
		if (oldValue == null) {
			if (value == null) {
				return null; // No change
			} else {
				return new FGENotification(parameter, oldValue, value);
			}
		} else {
			if (useEquals) {
				if (oldValue.equals(value)) {
					return null; // No change
				} else {
					return new FGENotification(parameter, oldValue, value);
				}
			} else {
				if (oldValue == value) {
					return null; // No change
				} else {
					return new FGENotification(parameter, oldValue, value);
				}
			}
		}
	}

	@Override
	public void notify(FGENotification notification) {
		hasChanged(notification);
	}

	/**
	 * This method is called whenever a notification is triggered from GR model
	 * 
	 * @param notification
	 */
	protected void hasChanged(FGENotification notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Change attribute " + notification.parameter + " for object " + this + " was: " + notification.oldValue
					+ " is now: " + notification.newValue);
		}
		propagateConstraintsAfterModification(notification.parameter);
		setChanged();
		notifyObservers(notification);
		getPropertyChangeSupport().firePropertyChange(notification.propertyName(), notification.oldValue, notification.newValue);
	}

	@Override
	public String getInspectorName() {
		return "GraphicalRepresentationUtils.inspector";
	}

	@Override
	public boolean isShape() {
		return this instanceof ShapeGraphicalRepresentation;
	}

	@Override
	public boolean isConnector() {
		return this instanceof ConnectorGraphicalRepresentation;
	}

	@Override
	public boolean isDrawing() {
		return this instanceof DrawingGraphicalRepresentation;
	}

	@Override
	public void notifyDrawableAdded(GraphicalRepresentation<?> addedGR) {
		addedGR.updateBindingModel();
		setChanged();
		notifyObservers(new GraphicalRepresentationAdded(addedGR));
	}

	@Override
	public void notifyDrawableRemoved(GraphicalRepresentation<?> removedGR) {
		removedGR.updateBindingModel();
		setChanged();
		notifyObservers(new GraphicalRepresentationRemoved(removedGR));
	}

	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	@Override
	public void update(Observable observable, Object notification) {
		if (observable instanceof TextStyle) {
			notifyAttributeChange(Parameters.textStyle);
		}
	}

	// *******************************************************************************
	// * Coordinates manipulation *
	// *******************************************************************************

	@Override
	public final Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		AffineTransform at = convertNormalizedPointToViewCoordinatesAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return new Point((int) returned.x, (int) returned.y);
	}

	@Override
	public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertNormalizedPointToViewCoordinates(p1, scale);
		Point pp2 = convertNormalizedPointToViewCoordinates(p2, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	@Override
	public final FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale) {
		AffineTransform at = convertViewCoordinatesToNormalizedPointAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return returned;
	}

	@Override
	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	@Override
	public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y, scale);
	}

	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y, scale);
	}

	@Override
	public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, GraphicalRepresentation<?> source, double scale) {
		if (!isConnectedToDrawing() || !source.isConnectedToDrawing()) {
			return new FGEPoint(p.x / scale, p.y / scale);
		}
		Point pointRelativeToCurrentView = GraphicalRepresentationUtils.convertPoint(source, p, this, scale);
		return convertViewCoordinatesToNormalizedPoint(pointRelativeToCurrentView, scale);
	}

	@Override
	public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, GraphicalRepresentation<?> destination, double scale) {
		if (!isConnectedToDrawing() || !destination.isConnectedToDrawing()) {
			return new FGEPoint(p.x * scale, p.y * scale);
		}
		Point pointRelativeToRemoteView = GraphicalRepresentationUtils.convertPoint(this, p, destination, scale);
		return destination.convertViewCoordinatesToNormalizedPoint(pointRelativeToRemoteView, scale);
	}

	@Override
	public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, GraphicalRepresentation<?> destination, double scale) {
		Point point = convertNormalizedPointToViewCoordinates(p, scale);
		return GraphicalRepresentationUtils.convertPoint(this, point, destination, scale);
	}

	@Override
	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, GraphicalRepresentation<?> destination,
			double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertLocalNormalizedPointToRemoteViewCoordinates(p1, destination, scale);
		Point pp2 = convertLocalNormalizedPointToRemoteViewCoordinates(p2, destination, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	@Override
	public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, GraphicalRepresentation<?> source, double scale) {
		Point point = source.convertNormalizedPointToViewCoordinates(p, scale);
		return GraphicalRepresentationUtils.convertPoint(source, point, this, scale);
	}

	@Override
	public boolean isRegistered() {
		return isRegistered;
	}

	@Override
	public void setRegistered(boolean aFlag) {
		isRegistered = aFlag;
	}

	@Override
	public Vector<MouseClickControl> getMouseClickControls() {
		return mouseClickControls;
	}

	@Override
	public void setMouseClickControls(Vector<MouseClickControl> mouseClickControls) {
		FGENotification notification = requireChange(Parameters.mouseClickControls, mouseClickControls);
		if (notification != null) {
			this.mouseClickControls.addAll(mouseClickControls);
			hasChanged(notification);
		}
	}

	@Override
	public void addToMouseClickControls(MouseClickControl mouseClickControl) {
		addToMouseClickControls(mouseClickControl, false);
	}

	@Override
	public void addToMouseClickControls(MouseClickControl mouseClickControl, boolean isPrioritar) {
		if (isPrioritar) {
			mouseClickControls.insertElementAt(mouseClickControl, 0);
		} else {
			mouseClickControls.add(mouseClickControl);
		}
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseClickControls, mouseClickControls, mouseClickControls));
	}

	@Override
	public void removeFromMouseClickControls(MouseClickControl mouseClickControl) {
		mouseClickControls.remove(mouseClickControl);
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseClickControls, mouseClickControls, mouseClickControls));
	}

	@Override
	public Vector<MouseDragControl> getMouseDragControls() {
		return mouseDragControls;
	}

	@Override
	public void setMouseDragControls(Vector<MouseDragControl> mouseDragControls) {
		FGENotification notification = requireChange(Parameters.mouseDragControls, mouseDragControls);
		if (notification != null) {
			this.mouseDragControls.addAll(mouseDragControls);
			hasChanged(notification);
		}
	}

	@Override
	public void addToMouseDragControls(MouseDragControl mouseDragControl) {
		addToMouseDragControls(mouseDragControl, false);
	}

	@Override
	public void addToMouseDragControls(MouseDragControl mouseDragControl, boolean isPrioritar) {
		if (isPrioritar) {
			mouseDragControls.insertElementAt(mouseDragControl, 0);
		} else {
			mouseDragControls.add(mouseDragControl);
		}
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseDragControls, mouseDragControls, mouseDragControls));
	}

	@Override
	public void removeFromMouseDragControls(MouseDragControl mouseDragControl) {
		mouseDragControls.remove(mouseDragControl);
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseDragControls, mouseDragControls, mouseDragControls));
	}

	@Override
	public MouseClickControl createMouseClickControl() {
		MouseClickControl returned = MouseClickControl.makeMouseClickControl("Noname", MouseButton.LEFT, 1);
		addToMouseClickControls(returned);
		return returned;
	}

	@Override
	public void deleteMouseClickControl(MouseClickControl mouseClickControl) {
		removeFromMouseClickControls(mouseClickControl);
	}

	@Override
	public boolean isMouseClickControlDeletable(MouseClickControl mouseClickControl) {
		return true;
	}

	@Override
	public MouseDragControl createMouseDragControl() {
		MouseDragControl returned = MouseDragControl.makeMouseDragControl("Noname", MouseButton.LEFT);
		addToMouseDragControls(returned);
		return returned;
	}

	@Override
	public void deleteMouseDragControl(MouseDragControl mouseDragControl) {
		removeFromMouseDragControls(mouseDragControl);
	}

	@Override
	public boolean isMouseDragControlDeletable(MouseDragControl mouseDragControl) {
		return true;
	}

	@Override
	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	@Override
	public void notifyLabelWillBeEdited() {
		setChanged();
		notifyObservers(new LabelWillEdit());
	}

	@Override
	public void notifyLabelHasBeenEdited() {
		setChanged();
		notifyObservers(new LabelHasEdited());
	}

	@Override
	public void notifyLabelWillMove() {
		setChanged();
		notifyObservers(new LabelWillMove());
	}

	@Override
	public void notifyLabelHasMoved() {
		setChanged();
		notifyObservers(new LabelHasMoved());
	}

	// Override when required
	@Override
	public void notifyObjectHierarchyWillBeUpdated() {
		setRegistered(false);
		if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;
	}

	// Override when required
	@Override
	public void notifyObjectHierarchyHasBeenUpdated() {
		setRegistered(true);
		if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;
	}

	@Override
	public String getToolTipText() {
		return toolTipText;
	}

	@Override
	public void setToolTipText(String tooltipText) {
		FGENotification notification = requireChange(Parameters.toolTipText, tooltipText);
		if (notification != null) {
			this.toolTipText = tooltipText;
			hasChanged(notification);
		}
	}

	@Override
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return horizontalTextAlignment;
	}

	@Override
	public void setHorizontalTextAlignment(HorizontalTextAlignment horizontalTextAlignment) {
		FGENotification notification = requireChange(Parameters.horizontalTextAlignment, horizontalTextAlignment);
		if (notification != null) {
			this.horizontalTextAlignment = horizontalTextAlignment;
			hasChanged(notification);
		}
	}

	@Override
	public VerticalTextAlignment getVerticalTextAlignment() {
		return verticalTextAlignment;
	}

	@Override
	public void setVerticalTextAlignment(VerticalTextAlignment verticalTextAlignment) {
		FGENotification notification = requireChange(Parameters.verticalTextAlignment, verticalTextAlignment);
		if (notification != null) {
			this.verticalTextAlignment = verticalTextAlignment;
			hasChanged(notification);
		}
	}

	@Override
	public ParagraphAlignment getParagraphAlignment() {
		return paragraphAlignment;
	}

	@Override
	public void setParagraphAlignment(ParagraphAlignment paragraphAlignment) {
		FGENotification notification = requireChange(Parameters.paragraphAlignment, paragraphAlignment);
		if (notification != null) {
			this.paragraphAlignment = paragraphAlignment;
			hasChanged(notification);
		}
	}

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

	@Override
	public void performRandomLayout(double width, double height) {
		Random r = new Random();
		for (GraphicalRepresentation<?> gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation<?> child = (ShapeGraphicalRepresentation<?>) gr;
				child.setLocation(new FGEPoint(r.nextDouble() * (width - child.getWidth()), r.nextDouble() * (height - child.getHeight())));
			}
		}
	}

	@Override
	public void performAutoLayout(double width, double height) {
	}

	@Override
	public Stroke getSpecificStroke() {
		return specificStroke;
	}

	@Override
	public void setSpecificStroke(Stroke aStroke) {
		specificStroke = aStroke;
	}

	// *******************************************************************************
	// * Bindings: constraint expressions
	// *******************************************************************************

	@Override
	public boolean isRootGraphicalRepresentation() {
		return getParentGraphicalRepresentation() == null;
	}

	@Override
	public GraphicalRepresentation<?> getRootGraphicalRepresentation() {
		GraphicalRepresentation<?> current = this;
		while (current != null && !current.isRootGraphicalRepresentation()) {
			current = current.getParentGraphicalRepresentation();
		}
		return current;
	}

	private BindingModel _bindingModel = null;

	@Override
	public final BindingModel getBindingModel() {
		if (_bindingModel == null) {
			createBindingModel();
		}
		return _bindingModel;
		/*if (isRootGraphicalRepresentation()) {
			return _bindingModel;
		}
		return getRootGraphicalRepresentation().getBindingModel();*/
	}

	@Override
	public BindingFactory getBindingFactory() {
		return GraphicalRepresentationUtils.BINDING_FACTORY;
	}

	@Override
	public void updateBindingModel() {
		logger.fine("updateBindingModel()");
		/*if (getRootGraphicalRepresentation() != null) {
			getRootGraphicalRepresentation()._bindingModel = null;
			getRootGraphicalRepresentation().createBindingModel();
		}*/
		createBindingModel();
	}

	private void createBindingModel() {
		_bindingModel = new BindingModel();

		_bindingModel.addToBindingVariables(new GRBindingFactory.ComponentPathElement("this", this, this));
		if (getParentGraphicalRepresentation() != null) {
			_bindingModel.addToBindingVariables(new GRBindingFactory.ComponentPathElement("parent", getParentGraphicalRepresentation(),
					this));
		}
		_bindingModel.addToBindingVariables(new ComponentsBindingVariable(this));

		Iterator<GraphicalRepresentation<?>> it = allContainedGRIterator();
		while (it.hasNext()) {
			GraphicalRepresentation<?> subComponent = it.next();
			// _bindingModel.addToBindingVariables(new ComponentBindingVariable(subComponent));
			subComponent.notifiedBindingModelRecreated();
		}

		logger.fine("Created binding model at root component level:\n" + _bindingModel);
	}

	@Override
	public Object getValue(BindingVariable<?> variable) {
		BindingVariable<?> bv = getBindingModel().bindingVariableNamed(variable.getVariableName());
		/*if (bv instanceof ComponentBindingVariable) {
			return ((ComponentBindingVariable) bv).getReference();
		}
		else*/if (bv instanceof ComponentPathElement) {
			return ((ComponentPathElement) bv).getComponent();
		} else if (bv instanceof ComponentsBindingVariable) {
			return getRootGraphicalRepresentation();
		} else {
			logger.warning("Could not find variable named " + variable);
			return null;
		}
	}

	@Override
	public void notifiedBindingModelRecreated() {
		logger.fine("notifiedBindingModelRecreated()");
	}

	@Override
	public void notifyBindingChanged(DataBinding binding) {
		logger.fine("notifyBindingChanged() for " + binding);
	}

	@Override
	public List<GraphicalRepresentation<?>> retrieveAllContainedGR() {
		if (!isValidated()) {
			return GraphicalRepresentationUtils.EMPTY_GR_VECTOR;
		}
		List<GraphicalRepresentation<?>> returned = new ArrayList<GraphicalRepresentation<?>>();
		addAllContainedGR(this, returned);
		return returned;
	}

	private void addAllContainedGR(GraphicalRepresentation<?> gr, List<GraphicalRepresentation<?>> returned) {
		if (gr.getContainedGraphicalRepresentations() == null) {
			return;
		}
		for (GraphicalRepresentation<?> gr2 : gr.getContainedGraphicalRepresentations()) {
			returned.add(gr2);
			addAllContainedGR(gr2, returned);
		}
	}

	@Override
	public Iterator<GraphicalRepresentation<?>> allGRIterator() {
		List<GraphicalRepresentation<?>> returned = getRootGraphicalRepresentation().retrieveAllContainedGR();
		if (!isValidated()) {
			return returned.iterator();
		}
		returned.add(0, getRootGraphicalRepresentation());
		return returned.iterator();
	}

	@Override
	public Iterator<GraphicalRepresentation<?>> allContainedGRIterator() {
		List<GraphicalRepresentation<?>> allGR = retrieveAllContainedGR();
		if (!isValidated()) {
			return allGR.iterator();
		}
		if (allGR == null) {
			return new Iterator<GraphicalRepresentation<?>>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public GraphicalRepresentation<?> next() {
					return null;
				}

				@Override
				public void remove() {
				}
			};
		} else {
			return allGR.iterator();
		}
	}

	@Override
	public Vector<ConstraintDependency> getDependancies() {
		return dependancies;
	}

	@Override
	public Vector<ConstraintDependency> getAlterings() {
		return alterings;
	}

	@Override
	public void declareDependantOf(GraphicalRepresentation<?> aComponent, GRParameter requiringParameter, GRParameter requiredParameter)
			throws DependencyLoopException {
		// logger.info("Component "+this+" depends of "+aComponent);
		if (aComponent == this) {
			logger.warning("Forbidden reflexive dependancies");
			return;
		}
		// Look if this dependancy may cause a loop in dependancies
		try {
			Vector<GraphicalRepresentation<?>> actualDependancies = new Vector<GraphicalRepresentation<?>>();
			actualDependancies.add(aComponent);
			searchLoopInDependenciesWith(aComponent, actualDependancies);
		} catch (DependencyLoopException e) {
			logger.warning("Forbidden loop in dependancies: " + e.getMessage());
			throw e;
		}

		ConstraintDependency newDependancy = new ConstraintDependency(this, requiringParameter, aComponent, requiredParameter);

		if (!dependancies.contains(newDependancy)) {
			dependancies.add(newDependancy);
			logger.info("Parameter " + requiringParameter + " of GR " + this + " depends of parameter " + requiredParameter + " of GR "
					+ aComponent);
		}
		if (!((GraphicalRepresentationImpl<?>) aComponent).alterings.contains(newDependancy)) {
			((GraphicalRepresentationImpl<?>) aComponent).alterings.add(newDependancy);
		}
	}

	private void searchLoopInDependenciesWith(GraphicalRepresentation<?> aComponent, Vector<GraphicalRepresentation<?>> actualDependancies)
			throws DependencyLoopException {
		for (ConstraintDependency dependancy : ((GraphicalRepresentationImpl<?>) aComponent).dependancies) {
			GraphicalRepresentation<?> c = dependancy.requiredGR;
			if (c == this) {
				throw new DependencyLoopException(actualDependancies);
			}
			Vector<GraphicalRepresentation<?>> newVector = new Vector<GraphicalRepresentation<?>>();
			newVector.addAll(actualDependancies);
			newVector.add(c);
			searchLoopInDependenciesWith(c, newVector);
		}
	}

	protected void propagateConstraintsAfterModification(GRParameter parameter) {
		for (ConstraintDependency dependency : alterings) {
			if (dependency.requiredParameter == parameter) {
				((GraphicalRepresentationImpl<?>) dependency.requiringGR).computeNewConstraint(dependency);
			}
		}
	}

	protected void computeNewConstraint(ConstraintDependency dependency) {
		// None known at this level
	}

	private Vector<GRVariable> variables = new Vector<GRVariable>();

	@Override
	public Vector<GRVariable> getVariables() {
		return variables;
	}

	@Override
	public void setVariables(Vector<GRVariable> variables) {
		this.variables = variables;
	}

	@Override
	public void addToVariables(GRVariable v) {
		variables.add(v);
		setChanged();
		notifyObservers(new FGENotification(Parameters.variables, variables, variables));
	}

	@Override
	public void removeFromVariables(GRVariable v) {
		variables.remove(v);
		setChanged();
		notifyObservers(new FGENotification(Parameters.variables, variables, variables));
	}

	@Override
	public GRVariable createStringVariable() {
		GRVariable returned = new GRVariable("variable", GRVariableType.String, "value");
		addToVariables(returned);
		return returned;
	}

	@Override
	public GRVariable createIntegerVariable() {
		GRVariable returned = new GRVariable("variable", GRVariableType.Integer, "0");
		addToVariables(returned);
		return returned;
	}

	@Override
	public void deleteVariable(GRVariable v) {
		removeFromVariables(v);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		if (pcSupport == null) {
			pcSupport = new PropertyChangeSupport(this);
		}
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return "delete";
	}

	private boolean validated = false;
	protected LabelMetricsProvider labelMetricsProvider;

	/**
	 * Return boolean indicating if this graphical representation is validated. A validated graphical representation is a graphical
	 * representation fully embedded in its graphical representation tree, which means that parent and child are set and correct, and that
	 * start and end shapes are set for connectors
	 * 
	 * 
	 * @return
	 */
	@Override
	public boolean isValidated() {
		return validated;
	}

	@Override
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	@Override
	public LabelMetricsProvider getLabelMetricsProvider() {
		return labelMetricsProvider;
	}

	@Override
	public void setLabelMetricsProvider(LabelMetricsProvider labelMetricsProvider) {
		this.labelMetricsProvider = labelMetricsProvider;
	}

	/**
	 * Returns the number of pixels available for the label considering its positioning. This method is used in case of line wrapping.
	 * 
	 * @param scale
	 * @return
	 */
	@Override
	public int getAvailableLabelWidth(double scale) {
		return Integer.MAX_VALUE;
	}

}
