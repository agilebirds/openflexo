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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
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
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.DrawUtils;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.notifications.GraphicalRepresentationAdded;
import org.openflexo.fge.notifications.GraphicalRepresentationDeleted;
import org.openflexo.fge.notifications.GraphicalRepresentationRemoved;
import org.openflexo.fge.notifications.LabelHasEdited;
import org.openflexo.fge.notifications.LabelWillEdit;
import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class GraphicalRepresentation<O> extends DefaultInspectableObject implements XMLSerializable, Bindable,
		BindingEvaluationContext, Cloneable, FGEConstants, Observer {

	private static final Logger logger = Logger.getLogger(GraphicalRepresentation.class.getPackage().getName());
	private Stroke specificStroke = null;

	private static BindingFactory BINDING_FACTORY = new GRBindingFactory();

	private static Vector<Object> EMPTY_VECTOR = new Vector<Object>();
	private static Vector<GraphicalRepresentation<?>> EMPTY_GR_VECTOR = new Vector<GraphicalRepresentation<?>>();

	// *******************************************************************************
	// * Parameters *
	// *******************************************************************************

	public static interface GRParameter {
		public String name();
	}

	public static enum Parameters implements GRParameter {
		identifier,
		layer,
		hasText,
		text,
		isMultilineAllowed,
		continuousTextEditing,
		textStyle,
		relativeTextX,
		relativeTextY,
		absoluteTextX,
		absoluteTextY,
		textAlignment,
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

	protected int layer;

	private TextStyle textStyle = TextStyle.makeDefault();
	private String text;
	private boolean multilineAllowed = false;
	private boolean continuousTextEditing = true;
	private double relativeTextX = 0.5;
	private double relativeTextY = 0.5;
	private double absoluteTextX = 0;
	private double absoluteTextY = 0;
	private TextAlignment textAlignment = TextAlignment.CENTER;

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

	private String toolTipText = null;

	public static enum TextAlignment {
		CENTER, LEFT, RIGHT
	}

	protected static class ConstraintDependency {
		GraphicalRepresentation requiringGR;
		GRParameter requiringParameter;
		GraphicalRepresentation requiredGR;
		GRParameter requiredParameter;

		public ConstraintDependency(GraphicalRepresentation requiringGR, GRParameter requiringParameter,
				GraphicalRepresentation requiredGR, GRParameter requiredParameter) {
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

	private Vector<ConstraintDependency> dependancies;
	private Vector<ConstraintDependency> alterings;

	// *******************************************************************************
	// * Static code *
	// *******************************************************************************

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

	static {
		StringEncoder.getDefaultInstance()._addConverter(POINT_CONVERTER);
		StringEncoder.getDefaultInstance()._addConverter(RECT_POLYLIN_CONVERTER);
	}

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

	protected GraphicalRepresentation(O aDrawable, Drawing<?> aDrawing) {
		super();
		drawable = aDrawable;
		drawing = aDrawing;
		textStyle = TextStyle.makeDefault();
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

	public void delete() {
		isDeleted = true;
		if (textStyle != null) {
			textStyle.deleteObserver(this);
		}
		setChanged();
		notifyObservers(new GraphicalRepresentationDeleted(this));
		deleteObservers();
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

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

	public void setsWith(GraphicalRepresentation<?> gr) {
		if (gr instanceof GraphicalRepresentation) {
			for (Parameters p : Parameters.values()) {
				if (p != Parameters.identifier && p != Parameters.mouseClickControls && p != Parameters.mouseDragControls) {
					_setParameterValueWith(p, gr);
				}
			}
		}
	}

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

	public void initializeDeserialization() {
		isDeserializing = true;
	}

	public void finalizeDeserialization() {
		// logger.info("Hop: finalizeDeserialization for "+this+" root is "+getRootGraphicalRepresentation());

		if (getRootGraphicalRepresentation().getBindingModel() == null) {
			getRootGraphicalRepresentation().createBindingModel();
		}

		isDeserializing = false;
	}

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
				return getParentGraphicalRepresentation().retrieveDefaultIdentifier() + "_" + index;
			}
		}
	}

	public String getIdentifier() {
		if (identifier == null) {
			return retrieveDefaultIdentifier();
		}
		return identifier;
	}

	public void setIdentifier(String identifier) {
		FGENotification notification = requireChange(Parameters.identifier, identifier);
		if (notification != null) {
			this.identifier = identifier;
			hasChanged(notification);
			updateBindingModel();
		}
	}

	public int getLayer() {
		return layer;
	}

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

	public final O getDrawable() {
		return drawable;
	}

	public final void setDrawable(O aDrawable) {
		drawable = aDrawable;
	}

	public Drawing<?> getDrawing() {
		return drawing;
	}

	public void setDrawing(Drawing<?> drawing) {
		this.drawing = drawing;
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return getDrawing().getDrawingGraphicalRepresentation();
	}

	public <O2> GraphicalRepresentation<O2> getGraphicalRepresentation(O2 drawable) {
		return getDrawing().getGraphicalRepresentation(drawable);
	}

	public List<? extends Object> getContainedObjects(Object drawable) {
		return getDrawing().getContainedObjects(drawable);
	}

	public Object getContainer(Object drawable) {
		if (getDrawing() == null) {
			return null;
		}
		return getDrawing().getContainer(drawable);
	}

	public List<? extends Object> getContainedObjects() {
		if (getDrawable() == null) {
			return null;
		}
		if (getDrawing() == null) {
			return null;
		}
		return getDrawing().getContainedObjects(getDrawable());
	}

	public Vector<GraphicalRepresentation<?>> getContainedGraphicalRepresentations() {
		// Indirection added to separate callers that require an ordered list of contained GR and those who do not care. Wa may then later
		// reimplement these methods to optimizer perfs.
		return getOrderedContainedGraphicalRepresentations();
	}

	public Vector<GraphicalRepresentation<?>> getOrderedContainedGraphicalRepresentations() {
		if (!isValidated()) {
			return EMPTY_GR_VECTOR;
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

	private Vector<GraphicalRepresentation<?>> orderedContainedGR = null;

	private Vector<GraphicalRepresentation<?>> getOrderedContainedGR() {
		if (!isValidated()) {
			logger.warning("GR " + this + " is not validated");
			return EMPTY_GR_VECTOR;
		}
		if (orderedContainedGR == null) {
			orderedContainedGR = new Vector<GraphicalRepresentation<?>>();
			for (GraphicalRepresentation<?> c : getOrderedContainedGraphicalRepresentations()) {
				if (!orderedContainedGR.contains(c)) {
					orderedContainedGR.add(c);
				}
			}
		}
		return orderedContainedGR;
	}

	public void moveToTop(GraphicalRepresentation<?> gr) {
		// TODO: something to do here
		logger.info("moveToTop temporarily desactivated");
		/*if (!gr.isValidated()) {
			logger.warning("GR " + gr + " is not validated");
		}
		if (getOrderedContainedGR().contains(gr)) {
			getOrderedContainedGR().remove(gr);
		}
		getOrderedContainedGR().add(gr);*/
	}

	public int getOrder(GraphicalRepresentation child1, GraphicalRepresentation child2) {
		Vector<GraphicalRepresentation<?>> orderedGRList = getOrderedContainedGraphicalRepresentations();

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

	public int getLayerOrder() {
		if (!isValidated()) {
			return -1;
		}
		if (getParentGraphicalRepresentation() == null) {
			return -1;
		}
		Vector<GraphicalRepresentation<?>> orderedGRList = getParentGraphicalRepresentation().getOrderedContainedGraphicalRepresentations();
		/*System.out.println("Index of " + this + " inside parent " + getParentGraphicalRepresentation() + " is "
				+ orderedGRList.indexOf(this));
		for (GraphicalRepresentation gr : orderedGRList) {
			System.out.println("> " + gr + " : is this=" + gr.equals(this));
		}*/
		return orderedGRList.indexOf(this);
	}

	public int getIndex() {
		return getLayerOrder();
	}

	public Object getContainer() {
		if (drawing == null) {
			return null;
		}
		if (drawable == null) {
			return null;
		}
		return drawing.getContainer(drawable);
	}

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

	public GraphicalRepresentation<?> getParentGraphicalRepresentation() {
		return getContainerGraphicalRepresentation();
	}

	public boolean contains(GraphicalRepresentation<?> gr) {
		if (!isValidated()) {
			return false;
		}
		return getContainedGraphicalRepresentations().contains(gr);
	}

	public boolean contains(Object drawable) {
		if (!isValidated()) {
			return false;
		}
		return getContainedGraphicalRepresentations().contains(getGraphicalRepresentation(drawable));
	}

	public Vector<Object> getAncestors() {
		if (!isValidated()) {
			return EMPTY_VECTOR;
		}
		return getAncestors(false);
	}

	public Vector<Object> getAncestors(boolean forceRecompute) {
		if (!isValidated()) {
			return EMPTY_VECTOR;
		}
		if (getDrawing() == null) {
			return EMPTY_VECTOR;
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

	public static GraphicalRepresentation<?> getFirstCommonAncestor(GraphicalRepresentation<?> child1, GraphicalRepresentation<?> child2) {
		if (!child1.isValidated()) {
			return null;
		}
		if (!child2.isValidated()) {
			return null;
		}
		return getFirstCommonAncestor(child1, child2, false);
	}

	public static GraphicalRepresentation<?> getFirstCommonAncestor(GraphicalRepresentation<?> child1, GraphicalRepresentation<?> child2,
			boolean includeCurrent) {
		if (!child1.isValidated()) {
			return null;
		}
		if (!child2.isValidated()) {
			return null;
		}
		Vector<Object> ancestors1 = child1.getAncestors(true);
		if (includeCurrent) {
			ancestors1.insertElementAt(child1, 0);
		}
		Vector<Object> ancestors2 = child2.getAncestors(true);
		if (includeCurrent) {
			ancestors2.insertElementAt(child2, 0);
		}
		for (int i = 0; i < ancestors1.size(); i++) {
			Object o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) {
				return child1.getGraphicalRepresentation(o1);
			}
		}
		return null;
	}

	public static boolean areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation<?> element1, GraphicalRepresentation<?> element2) {
		if (!element1.isValidated()) {
			return false;
		}
		if (!element2.isValidated()) {
			return false;
		}
		if (element1 == null) {
			return false;
		}
		if (element2 == null) {
			return false;
		}
		return getFirstCommonAncestor(element1, element2) != null;
	}

	/*
	 * public boolean isLayerVisibleAccross(GraphicalRepresentation<?> gr) { if (getLayer() > gr.getLayer()) return true;
	 * 
	 * //if (debug) logger.info("this="+this); //if (debug) logger.info("gr="+gr);
	 * 
	 * // But may be i have one parent whose layer is bigger than opposite gr GraphicalRepresentation<?> commonAncestor =
	 * getFirstCommonAncestor(this, gr, true);
	 * 
	 * //if (debug) logger.info("commonAncestor="+commonAncestor+" of "+commonAncestor .getClass().getName());
	 * 
	 * if (commonAncestor == null) return false;
	 * 
	 * GraphicalRepresentation<?> lastAncestor1 = null; GraphicalRepresentation<?> lastAncestor2 = null; if (commonAncestor == this)
	 * lastAncestor1 = this; if (commonAncestor == gr) lastAncestor2 = gr;
	 * 
	 * for (GraphicalRepresentation<?> child : commonAncestor.getContainedGraphicalRepresentations()) { //logger.info("Child:"+child); if
	 * (lastAncestor1 == null && (child == this || child.isAncestorOf(this))) lastAncestor1 = child; if (lastAncestor2 == null && (child ==
	 * gr || child.isAncestorOf(gr))) lastAncestor2 = child; }
	 * 
	 * //if (debug) logger.info("Ancestor1="+lastAncestor1+" layer="+lastAncestor1 .getLayer()); //if (debug)
	 * logger.info("Ancestor2="+lastAncestor2+" layer=" +lastAncestor2.getLayer());
	 * 
	 * if (lastAncestor1 == null) return false; if (lastAncestor2 == null) return false;
	 * 
	 * return lastAncestor1.getLayer() >= lastAncestor2.getLayer(); }
	 */

	/*
	 * public boolean isPointVisible(FGEPoint p) { if (!getIsVisible()) return false;
	 * 
	 * GraphicalRepresentation<?> initialGR = this; GraphicalRepresentation<?> currentGR = this;
	 * 
	 * while (currentGR != null) { GraphicalRepresentation<?> parentGR = currentGR.getContainerGraphicalRepresentation(); if (parentGR ==
	 * null) return true; if (!parentGR.getIsVisible()) return false; for (GraphicalRepresentation<?> child :
	 * parentGR.getContainedGraphicalRepresentations()) { // Only ShapeGR can hide other GR, ignore ConnectorGR here if (child instanceof
	 * ShapeGraphicalRepresentation) { ShapeGraphicalRepresentation<?> shapedChild = (ShapeGraphicalRepresentation<?>)child; if
	 * (shapedChild.getShape().getShape().containsPoint( convertNormalizedPoint(initialGR, p, child))) {
	 * logger.info("GR "+child+" contains point "+p+" on "+initialGR); if(child.getLayer() > currentGR.getLayer()) {
	 * logger.info("GR "+child+" hides point "+p+" on "+initialGR); } } } } currentGR = parentGR; }
	 * 
	 * return true; }
	 */

	public boolean isPointVisible(FGEPoint p) {
		if (!getIsVisible()) {
			return false;
		}

		/*
		 * if (this instanceof ShapeGraphicalRepresentation) { // Be carefull, maybe this point is just on outline // So translate it to the
		 * center to be sure FGEPoint center = ((ShapeGraphicalRepresentation)this).getShape ().getShape().getCenter(); p.x =
		 * p.x+FGEGeometricObject.EPSILON*(center.x-p.x); p.y = p.y+FGEGeometricObject.EPSILON*(center.y-p.y); }
		 * 
		 * DrawingGraphicalRepresentation<?> drawingGR = getDrawingGraphicalRepresentation(); ShapeGraphicalRepresentation<?> topLevelShape
		 * = drawingGR.getTopLevelShapeGraphicalRepresentation( convertNormalizedPoint(this, p, drawingGR));
		 */

		ShapeGraphicalRepresentation<?> topLevelShape = shapeHiding(p);

		return topLevelShape == null;
	}

	public ShapeGraphicalRepresentation<?> shapeHiding(FGEPoint p) {
		if (!getIsVisible()) {
			return null;
		}

		if (this instanceof ShapeGraphicalRepresentation) {
			// Be carefull, maybe this point is just on outline
			// So translate it to the center to be sure
			FGEPoint center = ((ShapeGraphicalRepresentation) this).getShape().getShape().getCenter();
			p.x = p.x + FGEGeometricObject.EPSILON * (center.x - p.x);
			p.y = p.y + FGEGeometricObject.EPSILON * (center.y - p.y);
		}

		DrawingGraphicalRepresentation<?> drawingGR = getDrawingGraphicalRepresentation();
		ShapeGraphicalRepresentation<?> topLevelShape = drawingGR.getTopLevelShapeGraphicalRepresentation(convertNormalizedPoint(this, p,
				drawingGR));

		if (topLevelShape == this || topLevelShape == getParentGraphicalRepresentation()) {
			return null;
		}

		return topLevelShape;
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	public TextStyle getTextStyle() {
		return textStyle;
	}

	public void setTextStyle(TextStyle aTextStyle) {
		FGENotification notification = requireChange(Parameters.textStyle, aTextStyle);
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

	public double getRelativeTextX() {
		return relativeTextX;
	}

	public void setRelativeTextX(double textX) {
		FGENotification notification = requireChange(Parameters.relativeTextX, textX);
		if (notification != null) {
			this.relativeTextX = textX;
			hasChanged(notification);
		}
	}

	public double getRelativeTextY() {
		return relativeTextY;
	}

	public void setRelativeTextY(double textY) {
		FGENotification notification = requireChange(Parameters.relativeTextY, textY);
		if (notification != null) {
			this.relativeTextY = textY;
			hasChanged(notification);
		}
	}

	public double getAbsoluteTextX() {
		return absoluteTextX;
	}

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

	public double getAbsoluteTextY() {
		return absoluteTextY;
	}

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

	public boolean getIsFocusable() {
		return isFocusable;
	}

	public void setIsFocusable(boolean isFocusable) {
		FGENotification notification = requireChange(Parameters.isFocusable, isFocusable);
		if (notification != null) {
			this.isFocusable = isFocusable;
			hasChanged(notification);
		}
	}

	public boolean getDrawControlPointsWhenFocused() {
		return drawControlPointsWhenFocused;
	}

	public void setDrawControlPointsWhenFocused(boolean aFlag) {
		FGENotification notification = requireChange(Parameters.drawControlPointsWhenFocused, aFlag);
		if (notification != null) {
			drawControlPointsWhenFocused = aFlag;
			hasChanged(notification);
		}
	}

	public boolean getIsSelectable() {
		return isSelectable;
	}

	public void setIsSelectable(boolean isSelectable) {
		FGENotification notification = requireChange(Parameters.isSelectable, isSelectable);
		if (notification != null) {
			this.isSelectable = isSelectable;
			hasChanged(notification);
		}
	}

	public boolean getDrawControlPointsWhenSelected() {
		return drawControlPointsWhenSelected;
	}

	public void setDrawControlPointsWhenSelected(boolean aFlag) {
		FGENotification notification = requireChange(Parameters.drawControlPointsWhenSelected, aFlag);
		if (notification != null) {
			drawControlPointsWhenSelected = aFlag;
			hasChanged(notification);
		}
	}

	public String getText() {
		return text;
	}

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

	public String getMultilineText() {
		return getText();
	}

	public void setMultilineText(String text) {
		setText(text);
	}

	public boolean getHasText() {
		return hasText;
	}

	public void setHasText(boolean hasText) {
		FGENotification notification = requireChange(Parameters.hasText, hasText);
		if (notification != null) {
			this.hasText = hasText;
			hasChanged(notification);
		}
	}

	public boolean getIsMultilineAllowed() {
		return multilineAllowed;
	}

	public void setIsMultilineAllowed(boolean multilineAllowed) {
		FGENotification notification = requireChange(Parameters.isMultilineAllowed, multilineAllowed);
		if (notification != null) {
			this.multilineAllowed = multilineAllowed;
			hasChanged(notification);
		}
	}

	public boolean getContinuousTextEditing() {
		return continuousTextEditing;
	}

	public void setContinuousTextEditing(boolean continuousTextEditing) {
		FGENotification notification = requireChange(Parameters.continuousTextEditing, continuousTextEditing);
		if (notification != null) {
			this.continuousTextEditing = continuousTextEditing;
			hasChanged(notification);
		}
	}

	public abstract boolean hasFloatingLabel();

	public boolean getIsFocused() {
		return isFocused;
	}

	public void setIsFocused(boolean aFlag) {
		FGENotification notification = requireChange(Parameters.isFocused, aFlag);
		if (notification != null) {
			isFocused = aFlag;
			hasChanged(notification);
		}
	}

	public boolean getIsSelected() {
		return isSelected;
	}

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

	public boolean getIsReadOnly() {
		return readOnly;
	}

	public void setIsReadOnly(boolean readOnly) {
		FGENotification notification = requireChange(Parameters.isReadOnly, readOnly);
		if (notification != null) {
			this.readOnly = readOnly;
			hasChanged(notification);
		}
	}

	public boolean getIsLabelEditable() {
		return labelEditable;
	}

	public void setIsLabelEditable(boolean labelEditable) {
		FGENotification notification = requireChange(Parameters.isLabelEditable, labelEditable);
		if (notification != null) {
			this.labelEditable = labelEditable;
			hasChanged(notification);
		}
	}

	public boolean shouldBeDisplayed() {
		// logger.info("For "+this+" getIsVisible()="+getIsVisible()+" getContainerGraphicalRepresentation()="+getContainerGraphicalRepresentation());
		return getIsVisible() && getContainerGraphicalRepresentation() != null && getContainerGraphicalRepresentation().shouldBeDisplayed();
	}

	public boolean getIsVisible() {
		if (isDeserializing()) {
			return isVisible;
		}
		return isVisible;
	}

	public void setIsVisible(boolean isVisible) {
		FGENotification notification = requireChange(Parameters.isVisible, isVisible);
		if (notification != null) {
			this.isVisible = isVisible;
			hasChanged(notification);
		}
	}

	public boolean hasText() {
		return getText() != null && !getText().trim().equals("");
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	public abstract int getViewX(double scale);

	public abstract int getViewY(double scale);

	public abstract int getViewWidth(double scale);

	public abstract int getViewHeight(double scale);

	public Rectangle getViewBounds(double scale) {
		Rectangle bounds = new Rectangle();

		bounds.x = getViewX(scale);
		bounds.y = getViewY(scale);
		bounds.width = getViewWidth(scale);
		bounds.height = getViewHeight(scale);

		return bounds;
	}

	public FGERectangle getNormalizedBounds() {
		/*
		 * Rectangle viewBounds = getViewBounds(scale); FGEPoint topLeft = convertViewCoordinatesToNormalizedPoint
		 * (viewBounds.x,viewBounds.y,scale); FGEPoint bottomRight = convertViewCoordinatesToNormalizedPoint
		 * (viewBounds.x+viewBounds.width,viewBounds.y+viewBounds.height,scale); return new
		 * FGERectangle(topLeft.x,topLeft.y,bottomRight.x-topLeft.x,bottomRight .y-topLeft.y);
		 */
		return new FGERectangle(0, 0, 1, 1, Filling.FILLED);
	}

	/**
	 * Return center of label, relative to container view
	 * 
	 * @param scale
	 * @return
	 */
	public abstract Point getLabelViewCenter(double scale);

	/**
	 * Sets center of label, relative to container view
	 * 
	 * @param scale
	 * @return
	 */
	public abstract void setLabelViewCenter(Point aPoint, double scale);

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	public void paint(Graphics g, DrawingController<?> controller) {
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
	}

	// *******************************************************************************
	// * Utils *
	// *******************************************************************************

	protected void notifyChange(GRParameter parameter, Object oldValue, Object newValue) {
		// Never notify unchanged values
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		hasChanged(new FGENotification(parameter, oldValue, newValue));
		/*propagateConstraintsAfterModification(parameter);
		setChanged();
		notifyObservers(new FGENotification(parameter, oldValue, newValue));*/
	}

	public void notifyChange(GRParameter parameter) {
		notifyChange(parameter, null, null);
	}

	public void notifyAttributeChange(GRParameter parameter) {
		notifyChange(parameter);
	}

	protected FGENotification requireChange(GRParameter parameter, Object value) {
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
			if (oldValue.equals(value)) {
				return null; // No change
			} else {
				return new FGENotification(parameter, oldValue, value);
			}
		}
	}

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
	}

	@Override
	public String getInspectorName() {
		return "GraphicalRepresentation.inspector";
	}

	public boolean isShape() {
		return this instanceof ShapeGraphicalRepresentation;
	}

	public boolean isConnector() {
		return this instanceof ConnectorGraphicalRepresentation;
	}

	public boolean isDrawing() {
		return this instanceof DrawingGraphicalRepresentation;
	}

	public void notifyDrawableAdded(GraphicalRepresentation<?> addedGR) {
		addedGR.updateBindingModel();
		setChanged();
		notifyObservers(new GraphicalRepresentationAdded(addedGR));
	}

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

	/**
	 * Convert a point relative to the view representing source drawable, with supplied scale, in a point relative to the view representing
	 * destination drawable
	 * 
	 * @param source
	 *            graphical representation of drawable represented in the source view
	 * @param point
	 *            point to convert
	 * @param destination
	 *            graphical representation of drawable represented in the destination view
	 * @param scale
	 *            the scale to be used to perform this conversion
	 * @return
	 */
	public static Point convertPoint(GraphicalRepresentation<?> source, Point point, GraphicalRepresentation<?> destination, double scale) {
		if (source != destination) {
			AffineTransform at = convertCoordinatesAT(source, destination, scale);
			return (Point) at.transform(point, new Point());
		} else {
			return new Point(point);
		}
	}

	/**
	 * Convert a rectangle coordinates expressed in the view representing source drawable, with supplied scale, in coordinates expressed in
	 * the view representing destination drawable
	 * 
	 * @param source
	 *            graphical representation of drawable represented in the source view
	 * @param aRectangle
	 *            rectangle to convert
	 * @param destination
	 *            graphical representation of drawable represented in the destination view
	 * @param scale
	 *            the scale to be used to perform this conversion
	 * @return
	 */
	public static Rectangle convertRectangle(GraphicalRepresentation<?> source, Rectangle aRectangle,
			GraphicalRepresentation<?> destination, double scale) {
		Point point = new Point(aRectangle.x, aRectangle.y);
		if (source != destination) {
			point = convertPoint(source, point, destination, scale);
		}
		return new Rectangle(point.x, point.y, aRectangle.width, aRectangle.height);
	}

	/**
	 * Build and return a new AffineTransform allowing to perform coordinates conversion from the view representing source drawable, with
	 * supplied scale, to the view representing destination drawable
	 * 
	 * @param source
	 * @param destination
	 * @param scale
	 * @return
	 */
	public static AffineTransform convertCoordinatesAT(GraphicalRepresentation<?> source, GraphicalRepresentation<?> destination,
			double scale) {
		if (source != destination) {
			AffineTransform returned = convertFromDrawableToDrawingAT(source, scale);
			returned.preConcatenate(convertFromDrawingToDrawableAT(destination, scale));
			return returned;
		} else {
			return new AffineTransform();
		}
	}

	/**
	 * Convert a point defined in coordinates system related to "source" graphical representation to related drawing graphical
	 * representation
	 * 
	 * @param destination
	 * @param point
	 * @param scale
	 * @return
	 */
	public static Point convertPointFromDrawableToDrawing(GraphicalRepresentation<?> source, Point point, double scale) {
		AffineTransform at = convertFromDrawableToDrawingAT(source, scale);
		return (Point) at.transform(point, new Point());
	}

	/**
	 * 
	 * Build a new AffineTransform allowing to convert coordinates from coordinate system defined by "source" graphical representation to
	 * related drawing graphical representation
	 * 
	 * @param source
	 * @param scale
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AffineTransform convertFromDrawableToDrawingAT(GraphicalRepresentation<?> source, double scale) {
		double tx = 0;
		double ty = 0;
		if (source == null) {
			logger.warning("Called convertFromDrawableToDrawingAT() for null graphical representation (source)");
			return new AffineTransform();
		}
		Object current = source.getDrawable();
		while (current != source.getDrawing().getModel()) {
			if (source.getDrawing().getGraphicalRepresentation(current) == null) {
				throw new IllegalArgumentException(
						"Drawable "
								+ current
								+ " has no graphical representation.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			if (source.getDrawing().getContainer(current) == null) {
				throw new IllegalArgumentException(
						"Drawable "
								+ current
								+ " has no container.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx += source.getDrawing().getGraphicalRepresentation(current).getViewX(scale);
			ty += source.getDrawing().getGraphicalRepresentation(current).getViewY(scale);
			current = source.getDrawing().getContainer(current);
		}
		return AffineTransform.getTranslateInstance(tx, ty);
	}

	/**
	 * Convert a point defined in related drawing graphical representation coordinates system to the one defined by "destination" graphical
	 * representation
	 * 
	 * @param destination
	 * @param point
	 * @param scale
	 * @return
	 */
	public static Point convertPointFromDrawingToDrawable(GraphicalRepresentation<?> destination, Point point, double scale) {
		AffineTransform at = convertFromDrawingToDrawableAT(destination, scale);
		return (Point) at.transform(point, new Point());
	}

	/**
	 * 
	 * Build a new AffineTransform allowing to convert coordinates from coordinate system defined by related drawing graphical
	 * representation to the one defined by "destination" graphical representation
	 * 
	 * @param destination
	 * @param scale
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AffineTransform convertFromDrawingToDrawableAT(GraphicalRepresentation<?> destination, double scale) {
		double tx = 0;
		double ty = 0;
		if (destination == null) {
			logger.warning("Called convertFromDrawingToDrawableAT() for null graphical representation (destination)");
			return new AffineTransform();
		}
		Object current = destination.getDrawable();
		while (current != destination.getDrawing().getModel()) {
			if (destination.getDrawing().getContainer(current) == null) {
				throw new IllegalArgumentException(
						"Drawable "
								+ current
								+ " has no container.\nDevelopper note: Use GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(GraphicalRepresentation,GraphicalRepresentation) to prevent such cases.");
			}
			tx -= destination.getDrawing().getGraphicalRepresentation(current).getViewX(scale);
			ty -= destination.getDrawing().getGraphicalRepresentation(current).getViewY(scale);
			current = destination.getDrawing().getContainer(current);
		}
		return AffineTransform.getTranslateInstance(tx, ty);
	}

	public final Point convertNormalizedPointToViewCoordinates(double x, double y, double scale) {
		AffineTransform at = convertNormalizedPointToViewCoordinatesAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return new Point((int) returned.x, (int) returned.y);
	}

	public Rectangle convertNormalizedRectangleToViewCoordinates(FGERectangle r, double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertNormalizedPointToViewCoordinates(p1, scale);
		Point pp2 = convertNormalizedPointToViewCoordinates(p2, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	public abstract AffineTransform convertNormalizedPointToViewCoordinatesAT(double scale);

	public final FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y, double scale) {
		AffineTransform at = convertViewCoordinatesToNormalizedPointAT(scale);
		FGEPoint returned = new FGEPoint();
		at.transform(new FGEPoint(x, y), returned);
		return returned;
	}

	public abstract AffineTransform convertViewCoordinatesToNormalizedPointAT(double scale);

	public Point convertNormalizedPointToViewCoordinates(FGEPoint p, double scale) {
		return convertNormalizedPointToViewCoordinates(p.x, p.y, scale);
	}

	public FGEPoint convertViewCoordinatesToNormalizedPoint(Point p, double scale) {
		return convertViewCoordinatesToNormalizedPoint(p.x, p.y, scale);
	}

	/**
	 * Convert a point relative to the normalized coordinates system from source drawable to the normalized coordinates system from
	 * destination drawable
	 * 
	 * @param source
	 * @param point
	 * @param destination
	 * @return
	 */
	public static FGEPoint convertNormalizedPoint(GraphicalRepresentation<?> source, FGEPoint point, GraphicalRepresentation<?> destination) {
		if (point == null) {
			return null;
		}
		AffineTransform at = convertNormalizedCoordinatesAT(source, destination);
		return (FGEPoint) at.transform(point, new FGEPoint());
	}

	/**
	 * Build and return an AffineTransform allowing to convert locations relative to the normalized coordinates system from source drawable
	 * to the normalized coordinates system from destination drawable
	 * 
	 * @param source
	 * @param point
	 * @param destination
	 * @return
	 */
	public static AffineTransform convertNormalizedCoordinatesAT(GraphicalRepresentation<?> source, GraphicalRepresentation<?> destination) {
		if (source == null) {
			logger.warning("null source !");
		}
		AffineTransform returned = source.convertNormalizedPointToViewCoordinatesAT(1.0);
		returned.preConcatenate(convertCoordinatesAT(source, destination, 1.0));
		returned.preConcatenate(destination.convertViewCoordinatesToNormalizedPointAT(1.0));
		return returned;
	}

	public FGEPoint convertRemoteViewCoordinatesToLocalNormalizedPoint(Point p, GraphicalRepresentation<?> source, double scale) {
		if (!isConnectedToDrawing() || !source.isConnectedToDrawing()) {
			return new FGEPoint(p.x / scale, p.y / scale);
		}
		Point pointRelativeToCurrentView = convertPoint(source, p, this, scale);
		return convertViewCoordinatesToNormalizedPoint(pointRelativeToCurrentView, scale);
	}

	public FGEPoint convertLocalViewCoordinatesToRemoteNormalizedPoint(Point p, GraphicalRepresentation<?> destination, double scale) {
		if (!isConnectedToDrawing() || !destination.isConnectedToDrawing()) {
			return new FGEPoint(p.x * scale, p.y * scale);
		}
		Point pointRelativeToRemoteView = convertPoint(this, p, destination, scale);
		return destination.convertViewCoordinatesToNormalizedPoint(pointRelativeToRemoteView, scale);
	}

	public Point convertLocalNormalizedPointToRemoteViewCoordinates(FGEPoint p, GraphicalRepresentation<?> destination, double scale) {
		Point point = convertNormalizedPointToViewCoordinates(p, scale);
		return convertPoint(this, point, destination, scale);
	}

	public Rectangle convertLocalNormalizedRectangleToRemoteViewCoordinates(FGERectangle r, GraphicalRepresentation<?> destination,
			double scale) {
		FGEPoint p1 = new FGEPoint(r.x, r.y);
		FGEPoint p2 = new FGEPoint(r.x + r.width, r.y + r.height);
		Point pp1 = convertLocalNormalizedPointToRemoteViewCoordinates(p1, destination, scale);
		Point pp2 = convertLocalNormalizedPointToRemoteViewCoordinates(p2, destination, scale);
		return new Rectangle(pp1.x, pp1.y, pp2.x - pp1.x, pp2.y - pp1.y);
	}

	public Point convertRemoteNormalizedPointToLocalViewCoordinates(FGEPoint p, GraphicalRepresentation<?> source, double scale) {
		Point point = source.convertNormalizedPointToViewCoordinates(p, scale);
		return convertPoint(source, point, this, scale);
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean aFlag) {
		isRegistered = aFlag;
	}

	public Vector<MouseClickControl> getMouseClickControls() {
		return mouseClickControls;
	}

	public void setMouseClickControls(Vector<MouseClickControl> mouseClickControls) {
		FGENotification notification = requireChange(Parameters.mouseClickControls, mouseClickControls);
		if (notification != null) {
			this.mouseClickControls.addAll(mouseClickControls);
			hasChanged(notification);
		}
	}

	public void addToMouseClickControls(MouseClickControl mouseClickControl) {
		addToMouseClickControls(mouseClickControl, false);
	}

	public void addToMouseClickControls(MouseClickControl mouseClickControl, boolean isPrioritar) {
		if (isPrioritar) {
			mouseClickControls.insertElementAt(mouseClickControl, 0);
		} else {
			mouseClickControls.add(mouseClickControl);
		}
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseClickControls, mouseClickControls, mouseClickControls));
	}

	public void removeFromMouseClickControls(MouseClickControl mouseClickControl) {
		mouseClickControls.remove(mouseClickControl);
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseClickControls, mouseClickControls, mouseClickControls));
	}

	public Vector<MouseDragControl> getMouseDragControls() {
		return mouseDragControls;
	}

	public void setMouseDragControls(Vector<MouseDragControl> mouseDragControls) {
		FGENotification notification = requireChange(Parameters.mouseDragControls, mouseDragControls);
		if (notification != null) {
			this.mouseDragControls.addAll(mouseDragControls);
			hasChanged(notification);
		}
	}

	public void addToMouseDragControls(MouseDragControl mouseDragControl) {
		addToMouseDragControls(mouseDragControl, false);
	}

	public void addToMouseDragControls(MouseDragControl mouseDragControl, boolean isPrioritar) {
		if (isPrioritar) {
			mouseDragControls.insertElementAt(mouseDragControl, 0);
		} else {
			mouseDragControls.add(mouseDragControl);
		}
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseDragControls, mouseDragControls, mouseDragControls));
	}

	public void removeFromMouseDragControls(MouseDragControl mouseDragControl) {
		mouseDragControls.remove(mouseDragControl);
		setChanged();
		notifyObservers(new FGENotification(Parameters.mouseDragControls, mouseDragControls, mouseDragControls));
	}

	public MouseClickControl createMouseClickControl() {
		MouseClickControl returned = MouseClickControl.makeMouseClickControl("Noname", MouseButton.LEFT, 1);
		addToMouseClickControls(returned);
		return returned;
	}

	public void deleteMouseClickControl(MouseClickControl mouseClickControl) {
		removeFromMouseClickControls(mouseClickControl);
	}

	public boolean isMouseClickControlDeletable(MouseClickControl mouseClickControl) {
		return true;
	}

	public MouseDragControl createMouseDragControl() {
		MouseDragControl returned = MouseDragControl.makeMouseDragControl("Noname", MouseButton.LEFT);
		addToMouseDragControls(returned);
		return returned;
	}

	public void deleteMouseDragControl(MouseDragControl mouseDragControl) {
		removeFromMouseDragControls(mouseDragControl);
	}

	public boolean isMouseDragControlDeletable(MouseDragControl mouseDragControl) {
		return true;
	}

	public abstract boolean isContainedInSelection(Rectangle drawingViewSelection, double scale);

	private JLabel _labelUsedToComputedNormalizedLabelBounds;

	public Rectangle getNormalizedLabelBounds() {
		if (_labelUsedToComputedNormalizedLabelBounds == null) {
			_labelUsedToComputedNormalizedLabelBounds = new JLabel();
		}
		return getLabelBounds(_labelUsedToComputedNormalizedLabelBounds, 1);
	}

	public Rectangle getLabelBounds(double scale) {
		if (_labelUsedToComputedNormalizedLabelBounds == null) {
			_labelUsedToComputedNormalizedLabelBounds = new JLabel();
		}
		return getLabelBounds(_labelUsedToComputedNormalizedLabelBounds, scale);
	}

	public Rectangle getLabelBounds(Component component, double scale) {
		Rectangle newBounds;
		// if (hasText()) {
		/*
		 * AffineTransform at = AffineTransform.getScaleInstance(scale,scale); if (getTextStyle().getOrientation() != 0)
		 * at.concatenate(AffineTransform .getRotateInstance(Math.toRadians(getTextStyle().getOrientation()))); Font font =
		 * getTextStyle().getFont().deriveFont(at); FontMetrics fm = component.getFontMetrics(font); int height = 0; int width = 0; if
		 * (getIsMultilineAllowed()) { StringTokenizer st = new StringTokenizer(getText(),LINE_SEPARATOR); while (st.hasMoreTokens()) {
		 * height += fm.getHeight(); width = Math.max(width, fm.stringWidth(st.
		 * nextToken())+4*FGEConstants.CONTROL_POINT_SIZE_FOR_SELECTION); } } else { height = fm.getHeight(); width =
		 * fm.stringWidth(getText())+4*FGEConstants .CONTROL_POINT_SIZE_FOR_SELECTION; } if (getTextStyle().getOrientation() != 0) height =
		 * (int)Math.max(height,height +width*Math.sin(Math.toRadians(getTextStyle().getOrientation())));
		 */
		Dimension labelSize = getLabelSize(component, scale);
		int width = labelSize.width;
		int height = labelSize.height;

		Point center;
		try {
			center = getLabelViewCenter(scale);
			newBounds = getLabelBoundsWithAlignement(center, width, height);

		} catch (IllegalArgumentException e) {
			logger.warning("Unexpected exception: " + e);
			newBounds = new Rectangle(0, 0, 0, 0);
		}
		/*
		 * } else { newBounds = new Rectangle(0,0,0,0); }
		 */

		return newBounds;
	}

	public Dimension getNormalizedLabelSize() {
		if (_labelUsedToComputedNormalizedLabelBounds == null) {
			_labelUsedToComputedNormalizedLabelBounds = new JLabel();
		}
		return getLabelSize(_labelUsedToComputedNormalizedLabelBounds, 1);
	}

	/**
	 * @param labelCenter
	 * @param labelWidth
	 * @param labelHeight
	 * @return
	 */
	public Rectangle getLabelBoundsWithAlignement(Point labelCenter, int labelWidth, int labelHeight) {
		// if (getTextAlignment() == TextAlignment.CENTER) {
		// Bug 1006530 Fix

		// }
		/*
		 * else if (getTextAlignment() == TextAlignment.LEFT) { return new
		 * Rectangle(labelCenter.x,labelCenter.y-labelHeight/2,labelWidth,labelHeight); } else { return new
		 * Rectangle(labelCenter.x-labelWidth,labelCenter.y-labelHeight/2,labelWidth,labelHeight); }
		 */
		// Bug 1007601 Fix
		// When using left/right alignement with multiple lines annotation, position of the text is relative to the center of the shape
		// The objective is to have the center of the label positioned according to relativeX/relativeY relatively to the shape.
		// The text alignement applies for the alignement of the text within its own bounds (therefore a one line label is not modified by
		// its textalignement property.
		return new Rectangle(labelCenter.x - labelWidth / 2, labelCenter.y - labelHeight / 2, labelWidth, labelHeight);
	}

	public Dimension getLabelSize(Component component, double scale) {
		if (hasText()) {
			TextStyle ts = getTextStyle();
			if (ts == null) {
				ts = TextStyle.makeDefault();
			}
			AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
			if (ts.getOrientation() != 0) {
				at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(getTextStyle().getOrientation())));
			}
			Font font = ts.getFont().deriveFont(at);
			FontMetrics fm = component.getFontMetrics(font);
			int height = 2 * fm.getDescent();
			int width = 0;
			int fontHeight = fm.getHeight();
			if (getIsMultilineAllowed()) {
				StringTokenizer st = new StringTokenizer(getText(), StringUtils.LINE_SEPARATOR);
				while (st.hasMoreTokens()) {
					height += fontHeight;
					width = Math.max(width, fm.stringWidth(st.nextToken()) + 4 * FGEConstants.CONTROL_POINT_SIZE);
				}
				if (getText().endsWith(StringUtils.LINE_SEPARATOR)) {
					height += fontHeight;
				}
			} else {
				height = fontHeight;
				width = fm.stringWidth(getText()) + 4 * FGEConstants.CONTROL_POINT_SIZE;
			}
			if (ts.getOrientation() != 0) {
				height = (int) Math.max(height, height + width * Math.sin(Math.toRadians(ts.getOrientation())));
			}

			return new Dimension(width, height);
		} else {
			return new Dimension(0, 0);
		}
	}

	public void notifyLabelWillBeEdited() {
		setChanged();
		notifyObservers(new LabelWillEdit());
	}

	public void notifyLabelHasBeenEdited() {
		setChanged();
		notifyObservers(new LabelHasEdited());
	}

	// Override when required
	public void notifyObjectHierarchyWillBeUpdated() {
		setRegistered(false);
		if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;
	}

	// Override when required
	public void notifyObjectHierarchyHasBeenUpdated() {
		setRegistered(true);
		if (ancestors != null) {
			ancestors.clear();
		}
		ancestors = null;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	public void setToolTipText(String tooltipText) {
		FGENotification notification = requireChange(Parameters.toolTipText, tooltipText);
		if (notification != null) {
			this.toolTipText = tooltipText;
			hasChanged(notification);
		}
	}

	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	public void setTextAlignment(TextAlignment atextAlignment) {
		FGENotification notification = requireChange(Parameters.textAlignment, atextAlignment);
		if (notification != null) {
			this.textAlignment = atextAlignment;
			hasChanged(notification);
		}
	}

	// *******************************************************************************
	// * Layout *
	// *******************************************************************************

	public void performRandomLayout(double width, double height) {
		Random r = new Random();
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				ShapeGraphicalRepresentation<?> child = (ShapeGraphicalRepresentation<?>) gr;
				child.setLocation(new FGEPoint(r.nextDouble() * (width - child.getWidth()), r.nextDouble() * (height - child.getHeight())));
			}
		}
	}

	public void performAutoLayout(double width, double height) {
	}

	public Stroke getSpecificStroke() {
		return specificStroke;
	}

	public void setSpecificStroke(Stroke aStroke) {
		specificStroke = aStroke;
	}

	// *******************************************************************************
	// * Bindings: constraint expressions
	// *******************************************************************************

	public boolean isRootGraphicalRepresentation() {
		return getParentGraphicalRepresentation() == null;
	}

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
		return BINDING_FACTORY;
	}

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
	public Object getValue(BindingVariable variable) {
		BindingVariable bv = getBindingModel().bindingVariableNamed(variable.getVariableName());
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

	public void notifiedBindingModelRecreated() {
		logger.fine("notifiedBindingModelRecreated()");
	}

	public void notifyBindingChanged(DataBinding binding) {
		logger.fine("notifyBindingChanged() for " + binding);
	}

	public Vector<GraphicalRepresentation<?>> retrieveAllContainedGR() {
		if (!isValidated()) {
			return EMPTY_GR_VECTOR;
		}
		Vector<GraphicalRepresentation<?>> returned = new Vector<GraphicalRepresentation<?>>();
		addAllContainedGR(this, returned);
		return returned;
	}

	private void addAllContainedGR(GraphicalRepresentation<?> gr, Vector<GraphicalRepresentation<?>> returned) {
		if (gr.getContainedGraphicalRepresentations() == null) {
			return;
		}
		for (GraphicalRepresentation<?> gr2 : gr.getContainedGraphicalRepresentations()) {
			returned.add(gr2);
			addAllContainedGR(gr2, returned);
		}
	}

	public Iterator<GraphicalRepresentation<?>> allGRIterator() {
		Vector<GraphicalRepresentation<?>> returned = getRootGraphicalRepresentation().retrieveAllContainedGR();
		if (!isValidated()) {
			return returned.iterator();
		}
		returned.insertElementAt(getRootGraphicalRepresentation(), 0);
		return returned.iterator();
	}

	public Iterator<GraphicalRepresentation<?>> allContainedGRIterator() {
		Vector<GraphicalRepresentation<?>> allGR = retrieveAllContainedGR();
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

	public Vector<ConstraintDependency> getDependancies() {
		return dependancies;
	}

	public Vector<ConstraintDependency> getAlterings() {
		return alterings;
	}

	public void declareDependantOf(GraphicalRepresentation aComponent, GRParameter requiringParameter, GRParameter requiredParameter)
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
		if (!aComponent.alterings.contains(newDependancy)) {
			aComponent.alterings.add(newDependancy);
		}
	}

	private void searchLoopInDependenciesWith(GraphicalRepresentation<?> aComponent, Vector<GraphicalRepresentation<?>> actualDependancies)
			throws DependencyLoopException {
		for (ConstraintDependency dependancy : aComponent.dependancies) {
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

	protected static class DependencyLoopException extends Exception {
		private Vector<GraphicalRepresentation<?>> dependencies;

		public DependencyLoopException(Vector<GraphicalRepresentation<?>> dependancies) {
			this.dependencies = dependancies;
		}

		@Override
		public String getMessage() {
			return "DependencyLoopException: " + dependencies;
		}
	}

	protected void propagateConstraintsAfterModification(GRParameter parameter) {
		for (ConstraintDependency dependency : alterings) {
			if (dependency.requiredParameter == parameter) {
				dependency.requiringGR.computeNewConstraint(dependency);
			}
		}
	}

	protected void computeNewConstraint(ConstraintDependency dependency) {
		// None known at this level
	}

	private Vector<GRVariable> variables = new Vector<GRVariable>();

	public Vector<GRVariable> getVariables() {
		return variables;
	}

	public void setVariables(Vector<GRVariable> variables) {
		this.variables = variables;
	}

	public void addToVariables(GRVariable v) {
		variables.add(v);
		setChanged();
		notifyObservers(new FGENotification(Parameters.variables, variables, variables));
	}

	public void removeFromVariables(GRVariable v) {
		variables.remove(v);
		setChanged();
		notifyObservers(new FGENotification(Parameters.variables, variables, variables));
	}

	public GRVariable createStringVariable() {
		GRVariable returned = new GRVariable("variable", GRVariableType.String, "value");
		addToVariables(returned);
		return returned;
	}

	public GRVariable createIntegerVariable() {
		GRVariable returned = new GRVariable("variable", GRVariableType.Integer, "0");
		addToVariables(returned);
		return returned;
	}

	public void deleteVariable(GRVariable v) {
		removeFromVariables(v);
	}

	private boolean validated = false;

	/**
	 * Return boolean indicating if this graphical representation is validated. A validated graphical representation is a graphical
	 * representation fully embedded in its graphical representation tree, which means that parent and child are set and correct, and that
	 * start and end shapes are set for connectors
	 * 
	 * 
	 * @return
	 */
	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

}
