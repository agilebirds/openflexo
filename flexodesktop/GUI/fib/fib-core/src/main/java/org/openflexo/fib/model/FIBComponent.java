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
package org.openflexo.fib.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeNode;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DependingObjects.HasDependencyBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.TargetObject;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.fib.model.validation.ValidationWarning;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.toolbox.StringUtils;

public abstract class FIBComponent extends FIBModelObject implements TreeNode, HasDependencyBinding {

	private static final Logger logger = Logger.getLogger(FIBComponent.class.getPackage().getName());

	private BindingFactory bindingFactory;
	public static Color DISABLED_COLOR = Color.GRAY;

	@Deprecated
	public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	private BindingDefinition DATA;

	private String definitionFile;

	private Date lastModified;

	@Deprecated
	public BindingDefinition getDataBindingDefinition() {
		if (DATA == null) {
			DATA = new BindingDefinition("data", getDefaultDataClass(), DataBinding.BindingDefinitionType.GET, false);
		}
		return DATA;
	}

	public static enum Parameters implements FIBModelAttribute {
		index,
		data,
		visible,
		dataClass,
		controllerClass,
		font,
		opaque,
		backgroundColor,
		foregroundColor,
		width,
		height,
		minWidth,
		minHeight,
		maxWidth,
		maxHeight,
		useScrollBar,
		horizontalScrollbarPolicy,
		verticalScrollbarPolicy,
		constraints,
		explicitDependancies
	}

	public static enum VerticalScrollBarPolicy {
		VERTICAL_SCROLLBAR_AS_NEEDED {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
			}
		},
		VERTICAL_SCROLLBAR_NEVER {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
			}
		},
		VERTICAL_SCROLLBAR_ALWAYS {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
			}
		};
		public abstract int getPolicy();
	}

	public static enum HorizontalScrollBarPolicy {
		HORIZONTAL_SCROLLBAR_AS_NEEDED {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
			}
		},
		HORIZONTAL_SCROLLBAR_NEVER {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
			}
		},
		HORIZONTAL_SCROLLBAR_ALWAYS {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
			}
		};
		public abstract int getPolicy();
	}

	private Integer index;
	private DataBinding<?> data;
	private DataBinding<Boolean> visible;

	private Font font;
	private Boolean opaque;
	private Color backgroundColor;
	private Color foregroundColor;

	private Integer width;
	private Integer height;

	private Integer minWidth;
	private Integer minHeight;

	private Integer maxWidth;
	private Integer maxHeight;

	private boolean useScrollBar = false;
	private HorizontalScrollBarPolicy horizontalScrollbarPolicy = null;
	private VerticalScrollBarPolicy verticalScrollbarPolicy = null;
	// private String dataClassName;

	private final Vector<FIBComponent> mayDepends;
	private final Vector<FIBComponent> mayAlters;

	private Class dataClass;
	private Class<? extends FIBController> controllerClass;

	private FIBContainer parent;

	public FIBComponent() {
		super();
		explicitDependancies = new Vector<FIBDependancy>();
		mayDepends = new Vector<FIBComponent>();
		mayAlters = new Vector<FIBComponent>();
	}

	@Override
	public void delete() {

		if (getParent() != null) {
			getParent().removeFromSubComponents(this);
		}
		super.delete();
	}

	public void setParent(FIBContainer parent) {
		this.parent = parent;
	}

	@Override
	public FIBContainer getParent() {
		return parent;
	}

	/**
	 * Return a boolean indicating if hierarchy is valid (no cycle was detected in hierarchy)
	 * 
	 * @return
	 */
	private boolean hasValidHierarchy() {
		List<FIBComponent> ancestors = new Vector<FIBComponent>();
		FIBComponent c = this;
		while (c != null) {
			if (ancestors.contains(c)) {
				return false;
			}
			ancestors.add(c);
			c = c.getParent();
		}
		return true;
	}

	/*public Hashtable<String,String> getLayoutConstraints() 
	{
		return layoutConstraints;
	}

	public String getConstraint(String constraint)
	{
		return layoutConstraints.get(constraint);
	}

	public boolean getBooleanConstraint(String constraint)
	{
		return layoutConstraints.get(constraint) != null && layoutConstraints.get(constraint).equalsIgnoreCase("true");
	}


	public String _getConstraints() 
	{
		if (layoutConstraints.size() == 0) return null;
		StringBuffer returned = new StringBuffer();
		boolean isFirst = true;
		for (String key : layoutConstraints.keySet()) {
			String value = layoutConstraints.get(key);
			returned.append((isFirst?"":";")+key+"="+value);
			isFirst = false;
		}
		return returned.toString();
	}

	public void _setConstraints(String someConstraints) 
	{
		StringTokenizer st = new StringTokenizer(someConstraints,";");
		while (st.hasMoreTokens()) {
			String next = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(next,"=");
			String key = null;
			String value = null;
			if (st2.hasMoreTokens()) key = st2.nextToken();
			if (st2.hasMoreTokens()) value = st2.nextToken();
			if (key != null && value != null) {
				layoutConstraints.put(key,value);
			}
		}
	}
	 */

	private ComponentConstraints constraints;

	public ComponentConstraints getConstraints() {
		constraints = _normalizeConstraintsWhenRequired(constraints);
		return constraints;
	}

	public void setConstraints(ComponentConstraints someConstraints) {
		// ComponentConstraints normalizedConstraints = constraints;
		ComponentConstraints normalizedConstraints = _normalizeConstraintsWhenRequired(someConstraints);
		FIBAttributeNotification<ComponentConstraints> notification = requireChange(Parameters.constraints, normalizedConstraints);
		if (notification != null) {
			normalizedConstraints.setComponent(this);
			this.constraints = normalizedConstraints;
			hasChanged(notification);
		}
	}

	private ComponentConstraints _normalizeConstraintsWhenRequired(ComponentConstraints someConstraints) {
		if (getParent() instanceof FIBSplitPanel) {
			if (someConstraints == null) {
				SplitLayoutConstraints returned = new SplitLayoutConstraints(((FIBSplitPanel) getParent()).getFirstEmptyPlaceHolder());
				returned.setComponent(this);
				return returned;
			}
			if (!(someConstraints instanceof SplitLayoutConstraints)) {
				return new SplitLayoutConstraints(someConstraints);
			}
			someConstraints.setComponent(this);
			return someConstraints;
		} else if (getParent() instanceof FIBPanel) {
			// Init to default value when relevant but null
			if (someConstraints == null) {
				ComponentConstraints returned;
				switch (((FIBPanel) getParent()).getLayout()) {
				case none:
					returned = new NoneLayoutConstraints();
					break;
				case flow:
					returned = new FlowLayoutConstraints();
					break;
				case grid:
					returned = new GridLayoutConstraints();
					break;
				case box:
					returned = new BoxLayoutConstraints();
					break;
				case border:
					returned = new BorderLayoutConstraints();
					break;
				case twocols:
					returned = new TwoColsLayoutConstraints();
					break;
				case gridbag:
					returned = new GridBagLayoutConstraints();
					break;
				case buttons:
					returned = new ButtonLayoutConstraints();
					break;
				default:
					returned = new NoneLayoutConstraints();
					break;
				}
				returned.setComponent(this);
				return returned;
			}
			// Mutate to right type when necessary
			switch (((FIBPanel) getParent()).getLayout()) {
			case none:
				if (!(someConstraints instanceof NoneLayoutConstraints)) {
					return new NoneLayoutConstraints(someConstraints);
				}
				break;
			case flow:
				if (!(someConstraints instanceof FlowLayoutConstraints)) {
					return new FlowLayoutConstraints(someConstraints);
				}
				break;
			case grid:
				if (!(someConstraints instanceof GridLayoutConstraints)) {
					return new GridLayoutConstraints(someConstraints);
				}
				break;
			case box:
				if (!(someConstraints instanceof BoxLayoutConstraints)) {
					return new BoxLayoutConstraints(someConstraints);
				}
				break;
			case border:
				if (!(someConstraints instanceof BorderLayoutConstraints)) {
					return new BorderLayoutConstraints(someConstraints);
				}
				break;
			case twocols:
				if (!(someConstraints instanceof TwoColsLayoutConstraints)) {
					return new TwoColsLayoutConstraints(someConstraints);
				}
				break;
			case gridbag:
				if (!(someConstraints instanceof GridBagLayoutConstraints)) {
					return new GridBagLayoutConstraints(someConstraints);
				}
				break;
			default:
			}
			someConstraints.setComponent(this);
			return someConstraints;
		} else {
			// No constraints for a component which container is not custom layouted
			return someConstraints;
		}
	}

	/*public String _getConditional() 
	{
		return conditional;
	}

	public void _setConditional(String conditional) 
	{
		this.conditional = conditional;
		DefaultExpressionParser parser = new DefaultExpressionParser();
		Vector<Variable> variables;
		try {
			conditionalExpression = parser.parse(conditional);
			variables = Expression.extractVariables(conditional);
			System.out.println("Variables for "+conditional+"\n"+variables);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		}	
	}

	public boolean isConditional()
	{
		return conditional != null;
	}

	public boolean evaluateCondition(final Object dataObject)
	{
		if (dataObject == null) return false;

		try {
			Expression returned =  conditionalExpression.evaluate(
					new EvaluationContext(
							new ExpressionParser.DefaultConstantFactory(),
							new VariableFactory() {
								public Expression makeVariable(Word value) {
									Object valueObject = FIBKeyValueCoder.getObjectValue(dataObject,value.getValue());
									if (valueObject instanceof String) {
										return new Constant.StringConstant((String)valueObject);
									}
									else if (valueObject instanceof Enum) {
										return new Constant.EnumConstant(((Enum)valueObject).name());
									}
									else if (valueObject instanceof Integer) {
										return new Constant.IntegerConstant((Integer)valueObject);
									}
									else if (valueObject instanceof Long) {
										return new Constant.IntegerConstant((Long)valueObject);
									}
									else if (valueObject instanceof Short) {
										return new Constant.IntegerConstant((Short)valueObject);
									}
									else if (valueObject instanceof Float) {
										return new Constant.FloatConstant((Float)valueObject);
									}
									else if (valueObject instanceof Double) {
										return new Constant.FloatConstant((Double)valueObject);
									}
									else if (valueObject instanceof Boolean) {
										return ((Boolean)valueObject ? Constant.BooleanConstant.TRUE : Constant.BooleanConstant.FALSE);
									}
									// TODO Handle others
									return new Variable(value.getValue());
								}
							},
							new ExpressionParser.DefaultFunctionFactory()));
			System.out.println("After evaluation: "+returned+" of "+returned.getClass().getName());
			if (returned instanceof BooleanConstant) return ((BooleanConstant)returned).getValue();
			logger.warning("Could not evaluate: "+conditional+" found: "+returned);
			return true;
		}
		catch (TypeMismatchException e) {
			e.printStackTrace();
			logger.warning("TypeMismatch: "+e.getMessage());
		}


		return true;
	}*/

	public boolean isRootComponent() {
		return getParent() == null;
	}

	/**
	 * Return the root component for this component. Iterate over the top of the component hierarchy.
	 * 
	 * @return
	 */
	public FIBComponent getRootComponent() {
		FIBComponent current = this;
		while (current != null && !current.isRootComponent()) {
			current = current.getParent();
		}
		return current;
	}

	protected BindingModel _bindingModel = null;

	@Override
	public BindingModel getBindingModel() {
		if (isRootComponent()) {
			if (_bindingModel == null) {
				createBindingModel();
			}
			return _bindingModel;
		} else {
			if (getRootComponent() != null && getRootComponent() != this) {
				return getRootComponent().getBindingModel();
			}
			return null;
		}
	}

	public void updateBindingModel() {
		if (deserializationPerformed) {
			logger.fine("updateBindingModel()");
			FIBComponent root = getRootComponent();
			if (root != null) {
				root._bindingModel = null;
				root.createBindingModel();
			}
		}
	}

	/**
	 * Creates binding variable identified by "data"<br>
	 * Default behavior is to generate a binding variable with the java type identified by data class
	 */
	protected void createDataBindingVariable() {
		_bindingModel.addToBindingVariables(new BindingVariable("data", dataClass != null ? dataClass : Object.class));
	}

	protected void createBindingModel() {
		createBindingModel(null);
	}

	protected void createBindingModel(BindingModel baseBindingModel) {
		if (_bindingModel == null) {
			if (baseBindingModel == null) {
				_bindingModel = new BindingModel();
			} else {
				_bindingModel = new BindingModel(baseBindingModel);
			}

			/*Class dataClass = null;
			try {
			if (dataClassName != null) {
				dataClass = Class.forName(dataClassName);
				logger.fine("Found: "+dataClassName);
			}
			} catch (ClassNotFoundException e) {
			logger.warning("Not found: "+dataClassName);
			}*/
			// if (dataClass == null) dataClass = Object.class;

			createDataBindingVariable();

			if (StringUtils.isNotEmpty(getName()) && getDynamicAccessType() != null) {
				_bindingModel.addToBindingVariables(new BindingVariable(getName(), getDynamicAccessType()));
			}

			Iterator<FIBComponent> it = subComponentIterator();
			while (it.hasNext()) {
				FIBComponent subComponent = it.next();
				if (StringUtils.isNotEmpty(subComponent.getName()) && subComponent.getDynamicAccessType() != null) {
					_bindingModel.addToBindingVariables(new BindingVariable(subComponent.getName(), subComponent.getDynamicAccessType()));
				}
			}

			Class myControllerClass = getControllerClass();
			if (myControllerClass == null) {
				myControllerClass = FIBController.class;
			}

			_bindingModel.addToBindingVariables(new BindingVariable("controller", myControllerClass));

			it = subComponentIterator();
			while (it.hasNext()) {
				FIBComponent subComponent = it.next();
				subComponent.notifiedBindingModelRecreated();
			}

		}
		// logger.info("Created binding model at root component level:\n"+_bindingModel);
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> binding) {
		super.notifiedBindingChanged(binding);
		if (binding == getData()) {
			logger.info("notified data changed");
		}
	}

	public void notifiedBindingModelRecreated() {
	}

	protected boolean deserializationPerformed = true;

	@Override
	public void initializeDeserialization() {
		super.initializeDeserialization();
		deserializationPerformed = false;
	}

	@Override
	public void finalizeDeserialization() {
		// System.out.println("finalizeDeserialization for "+this+" isRoot="+isRootComponent());

		super.finalizeDeserialization();
		deserializationPerformed = true;

		if (isRootComponent()) {
			updateBindingModel();
		}

		if (data != null) {
			data.decode();
		}

		if (visible != null) {
			visible.decode();
		}

	}

	public Vector<FIBComponent> getNamedComponents() {
		Vector<FIBComponent> returned = new Vector<FIBComponent>();
		for (FIBComponent c : retrieveAllSubComponents()) {
			if (StringUtils.isNotEmpty(c.getName())) {
				returned.add(c);
			}
		}
		return returned;
	}

	public FIBComponent getComponentNamed(String name) {
		if (StringUtils.isNotEmpty(this.getName()) && this.getName().equals(name)) {
			return this;
		}
		for (FIBComponent c : retrieveAllSubComponents()) {
			if (StringUtils.isNotEmpty(c.getName()) && c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public Vector<FIBComponent> retrieveAllSubComponents() {
		if (this instanceof FIBContainer) {
			Vector<FIBComponent> returned = new Vector<FIBComponent>();
			addAllSubComponents((FIBContainer) this, returned);
			return returned;
		}
		return null;
	}

	private void addAllSubComponents(FIBContainer c, Vector<FIBComponent> returned) {
		for (FIBComponent c2 : c.getSubComponents()) {
			returned.add(c2);
			if (c2 instanceof FIBContainer) {
				addAllSubComponents((FIBContainer) c2, returned);
			}
			/*else if (c2 instanceof FIBReferencedComponent){
				FIBComponent referenced =((FIBReferencedComponent) c2).getComponent();
				
				// TEST
				FIBReferencedComponent ref = ((FIBReferencedComponent) c2);
				
				
				if (referenced instanceof FIBContainer){
					returned.add((FIBContainer) referenced);
					addAllSubComponents((FIBContainer) referenced, returned);
				}
			}*/
		}
	}

	public Iterator<FIBComponent> subComponentIterator() {
		Vector<FIBComponent> allSubComponents = retrieveAllSubComponents();
		if (allSubComponents == null) {
			return new Iterator<FIBComponent>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public FIBComponent next() {
					return null;
				}

				@Override
				public void remove() {
				}
			};
		} else {
			return allSubComponents.iterator();
		}
	}

	public Vector<FIBComponent> getMayDepends() {
		return mayDepends;
	}

	public Iterator<FIBComponent> getMayDependsIterator() {
		return new ArrayList<FIBComponent>(mayDepends).iterator();
	}

	public Iterator<FIBComponent> getMayAltersIterator() {
		return new ArrayList<FIBComponent>(mayAlters).iterator();
	}

	public void declareDependantOf(FIBComponent aComponent) /*throws DependancyLoopException*/{
		// logger.info("Component "+this+" depends of "+aComponent);
		if (aComponent == this) {
			logger.warning("Forbidden reflexive dependencies");
			return;
		}
		// Look if this dependancy may cause a loop in dependancies
		/*try {
			Vector<FIBComponent> dependancies = new Vector<FIBComponent>();
			dependancies.add(aComponent);
			searchLoopInDependenciesWith(aComponent, dependancies);
		} catch (DependencyLoopException e) {
			logger.warning("Forbidden loop in dependencies: " + e.getMessage());
			throw e;
		}*/

		if (!mayDepends.contains(aComponent)) {
			mayDepends.add(aComponent);
			logger.fine("Component " + this + " depends of " + aComponent);
		}
		if (!aComponent.mayAlters.contains(this)) {
			aComponent.mayAlters.add(this);
		}
	}

	/*private void searchLoopInDependanciesWith(FIBComponent aComponent, Vector<FIBComponent> dependancies) throws DependancyLoopException {
		for (FIBComponent c : aComponent.mayDepends) {
			if (c == this) {
				throw new DependencyLoopException(dependencies);
			}
			Vector<FIBComponent> newVector = new Vector<FIBComponent>();
			newVector.addAll(dependencies);
			newVector.add(c);
			searchLoopInDependenciesWith(c, newVector);
		}
	}*/

	/*protected static class DependancyLoopException extends Exception {
		private final Vector<FIBComponent> dependancies;

		public DependencyLoopException(Vector<FIBComponent> dependancies) {
			this.dependencies = dependancies;
		}

		@Override
		public String getMessage() {
			return "DependencyLoopException: " + dependencies;
		}
	}*/

	public Integer getIndex() {
		if (index == null) {
			if (getConstraints() != null && getConstraints().hasIndex()) {
				return getConstraints().getIndex();
			}
		}
		return index;
	}

	public void setIndex(Integer index) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.index, index);
		if (notification != null) {
			this.index = index;
			hasChanged(notification);
			if (getParent() != null) {
				getParent().reorderComponents();
			}
		}
	}

	public DataBinding<?> getData() {
		if (data == null) {
			data = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET) {
				@Override
				public Type getDeclaredType() {
					return getDataType();
				}
			};
			data.setBindingName("data");
		}
		return data;
	}

	public void setData(DataBinding<?> data) {
		if (data != null) {
			this.data = new DataBinding<Object>(data.toString(), this, data.getDeclaredType(), data.getBindingDefinitionType()) {
				@Override
				public Type getDeclaredType() {
					return getDataType();
				}
			};
			data.setBindingName("data");
		} else {
			this.data = null;
		}
	}

	public DataBinding<Boolean> getVisible() {
		if (visible == null) {
			visible = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return visible;
	}

	public void setVisible(DataBinding<Boolean> visible) {
		if (visible != null) {
			visible = new DataBinding<Boolean>(visible.toString(), this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		this.visible = visible;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" + (getName() != null ? getName() : getIdentifier() != null ? getIdentifier() : "unnamed")
				+ ")";
	}

	public abstract String getIdentifier();

	/**
	 * Return the FIBComponent this component refer to
	 * 
	 * @return
	 */
	@Override
	public FIBComponent getComponent() {
		return this;
	}

	public Type getDataType() {
		if (dataClass == null) {
			return Object.class;
		}
		return dataClass;

		/*if (dataClassName == null) return null;
		if (dataClass == null) {
			try {
				dataClass = Class.forName(dataClassName);
			} catch (ClassNotFoundException e) {
				logger.warning("Not found: "+dataClassName);
				dataClass = Object.class;
			}
		}
		return dataClass;*/

	}

	public Class<?> getDataClass() {
		return dataClass;
	}

	@SuppressWarnings("rawtypes")
	public void setDataClass(Class<?> dataClass) {
		FIBAttributeNotification<Class> notification = requireChange(Parameters.dataClass, (Class) dataClass);
		if (notification != null) {
			this.dataClass = dataClass;
			updateBindingModel();
			hasChanged(notification);
		}
	}

	public Class<? extends FIBController> getControllerClass() {
		return controllerClass;
	}

	public void setControllerClass(Class<? extends FIBController> controllerClass) {

		FIBAttributeNotification<Class> notification = requireChange(Parameters.controllerClass, (Class) controllerClass);
		if (notification != null) {
			this.controllerClass = controllerClass;
			updateBindingModel();
			hasChanged(notification);
		}
	}

	public Type getDefaultDataClass() {
		return Object.class;

	}

	public Type getDynamicAccessType() {
		if (data != null) {
			Type[] args = new Type[1];
			args[0] = getDataType();
			return new ParameterizedTypeImpl(FIBView.class, args);
		}
		return null;
	}

	public void clearParameters() {
		getParameters().clear();
	}

	public final Font retrieveValidFont() {
		if (font == null) {
			if (!isRootComponent() && hasValidHierarchy()) {
				return getParent().retrieveValidFont();
			} else {
				return null; // Use system default
			}
		}

		return getFont();
	}

	public final Color retrieveValidForegroundColor() {
		if (foregroundColor == null) {
			if (!isRootComponent() && hasValidHierarchy()) {
				return getParent().retrieveValidForegroundColor();
			} else {
				return null; // Use default
			}
		}

		return getForegroundColor();
	}

	public final Color retrieveValidBackgroundColor() {
		if (backgroundColor == null) {
			if (!isRootComponent() && hasValidHierarchy()) {
				return getParent().retrieveValidBackgroundColor();
			} else {
				return null; // Use system default
			}
		}

		return getBackgroundColor();
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		FIBAttributeNotification<Font> notification = requireChange(Parameters.font, font);
		if (notification != null) {
			this.font = font;
			hasChanged(notification);
		}
	}

	public Boolean getOpaque() {
		return opaque;
	}

	public void setOpaque(Boolean opaque) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.opaque, opaque);
		if (notification != null) {
			this.opaque = opaque;
			hasChanged(notification);
		}
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundColor, backgroundColor);
		if (notification != null) {
			this.backgroundColor = backgroundColor;
			hasChanged(notification);
		}
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.foregroundColor, foregroundColor);
		if (notification != null) {
			this.foregroundColor = foregroundColor;
			hasChanged(notification);
		}
	}

	public boolean getUseScrollBar() {
		return useScrollBar;
	}

	public void setUseScrollBar(boolean useScrollBar) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.useScrollBar, useScrollBar);
		if (notification != null) {
			this.useScrollBar = useScrollBar;
			if (useScrollBar) {
				horizontalScrollbarPolicy = HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED;
				verticalScrollbarPolicy = VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED;
			} else {
				horizontalScrollbarPolicy = null;
				verticalScrollbarPolicy = null;
			}
			hasChanged(notification);
		}
	}

	public HorizontalScrollBarPolicy getHorizontalScrollbarPolicy() {
		return horizontalScrollbarPolicy;
	}

	public void setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy horizontalScrollbarPolicy) {
		FIBAttributeNotification<HorizontalScrollBarPolicy> notification = requireChange(Parameters.horizontalScrollbarPolicy,
				horizontalScrollbarPolicy);
		if (notification != null) {
			this.horizontalScrollbarPolicy = horizontalScrollbarPolicy;
			hasChanged(notification);
		}
	}

	public VerticalScrollBarPolicy getVerticalScrollbarPolicy() {
		return verticalScrollbarPolicy;
	}

	public void setVerticalScrollbarPolicy(VerticalScrollBarPolicy verticalScrollbarPolicy) {
		FIBAttributeNotification<VerticalScrollBarPolicy> notification = requireChange(Parameters.verticalScrollbarPolicy,
				verticalScrollbarPolicy);
		if (notification != null) {
			this.verticalScrollbarPolicy = verticalScrollbarPolicy;
			hasChanged(notification);
		}
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.width, width);
		if (notification != null) {
			this.width = width;
			hasChanged(notification);
		}
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.height, height);
		if (notification != null) {
			this.height = height;
			hasChanged(notification);
		}
	}

	public Integer getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(Integer minWidth) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.minWidth, minWidth);
		if (notification != null) {
			this.minWidth = minWidth;
			hasChanged(notification);
		}
	}

	public Integer getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(Integer minHeight) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.minHeight, minHeight);
		if (notification != null) {
			this.minHeight = minHeight;
			hasChanged(notification);
		}
	}

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.maxWidth, maxWidth);
		if (notification != null) {
			this.maxWidth = maxWidth;
			hasChanged(notification);
		}
	}

	public Integer getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(Integer maxHeight) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.maxHeight, maxHeight);
		if (notification != null) {
			this.maxHeight = maxHeight;
			hasChanged(notification);
		}
	}

	@Override
	public void setName(String name) {
		if (StringUtils.isEmpty(name)) {
			name = null;
		}
		super.setName(name);
		updateBindingModel();
	}

	@Override
	public void addToParameters(FIBParameter p) {
		// Little hask to recover previously created fib
		if (p.getName().equals("controllerClassName")) {
			try {
				Class<?> myControllerClass = Class.forName(p.getValue());
				if (FIBController.class.isAssignableFrom(myControllerClass)) {
					setControllerClass((Class<? extends FIBController>) myControllerClass);
				}
			} catch (ClassNotFoundException e) {
				logger.warning("Could not find class " + p.getValue());
			}

		} else {
			super.addToParameters(p);
			updateBindingModel();
		}
	}

	public boolean definePreferredDimensions() {
		return width != null && height != null;
	}

	public void setDefinePreferredDimensions(boolean definePreferredDimensions) {
		if (definePreferredDimensions) {
			FIBView<?, ?, ?> v = FIBController.makeView(this, (LocalizedDelegate) null);
			Dimension p = v.getJComponent().getPreferredSize();
			setWidth(p.width);
			setHeight(p.height);
			v.delete();
		} else {
			setWidth(null);
			setHeight(null);
		}
	}

	public boolean defineMaxDimensions() {
		return maxWidth != null && maxHeight != null;
	}

	public void setDefineMaxDimensions(boolean defineMaxDimensions) {
		if (defineMaxDimensions) {
			FIBView<?, ?, ?> v = FIBController.makeView(this, (LocalizedDelegate) null);
			setMaxWidth(1024);
			setMaxHeight(1024);
			v.delete();
		} else {
			setMaxWidth(null);
			setMaxHeight(null);
		}
	}

	public boolean defineMinDimensions() {
		return minWidth != null && minHeight != null;
	}

	public void setDefineMinDimensions(boolean defineMinDimensions) {
		if (defineMinDimensions) {
			FIBView<?, ?, ?> v = FIBController.makeView(this, (LocalizedDelegate) null);
			Dimension p = v.getJComponent().getMinimumSize();
			setMinWidth(p.width);
			setMinHeight(p.height);
			v.delete();
		} else {
			setMinWidth(null);
			setMinHeight(null);
		}
	}

	private Vector<FIBDependancy> explicitDependancies;

	// private Vector<FIBComponentDependancy> componentDependancies;

	/*public Vector<FIBComponentDependancy> getComponentDependancies()
	{
		if (componentDependancies == null) {
			componentDependancies = new Vector<FIBComponentDependancy>();
			for (Iterator<FIBComponent> it=getMayDependsIterator(); it.hasNext();) {
				componentDependancies.add(new DynamicFIBDependancy(this,it.next()));
			}
			componentDependancies.addAll(explicitDependancies);
		}
		return componentDependancies;
	}*/

	public Vector<FIBDependancy> getExplicitDependancies() {
		return explicitDependancies;
	}

	public void setExplicitDependancies(Vector<FIBDependancy> explicitDependancies) {
		FIBAttributeNotification<Vector<FIBDependancy>> notification = requireChange(Parameters.explicitDependancies, explicitDependancies);
		explicitDependancies = null;
		if (notification != null) {
			this.explicitDependancies = explicitDependancies;
			hasChanged(notification);
		}
	}

	public void addToExplicitDependancies(FIBDependancy p) {
		p.setOwner(this);
		explicitDependancies.add(p);
		if (p.getMasterComponent() != null) {
			// try {
			p.getOwner().declareDependantOf(p.getMasterComponent());
			/*} catch (DependancyLoopException e) {
				logger.warning("DependancyLoopException raised while applying explicit dependancy for " + p.getOwner() + " and "
						+ p.getMasterComponent() + " message: " + e.getMessage());
			}*/
		}
		// componentDependancies = null;
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBDependancy>(Parameters.explicitDependancies, p));
	}

	public void removeFromExplicitDependancies(FIBDependancy p) {
		p.setOwner(null);
		explicitDependancies.remove(p);
		// componentDependancies = null;
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBDependancy>(Parameters.explicitDependancies, p));
	}

	public FIBDependancy createNewExplicitDependancy() {
		FIBDependancy returned = new FIBDependancy();
		addToExplicitDependancies(returned);
		return returned;
	}

	public void deleteExplicitDependancy(FIBDependancy p) {
		removeFromExplicitDependancies(p);
	}

	private FIBLocalizedDictionary localizedDictionary;

	public FIBLocalizedDictionary retrieveFIBLocalizedDictionary() {
		if (getLocalizedDictionary() == null) {
			setLocalizedDictionary(new FIBLocalizedDictionary());
		}
		return getLocalizedDictionary();
	}

	public void setLocalizedDictionary(FIBLocalizedDictionary localizedDictionary) {
		if (localizedDictionary != null) {
			localizedDictionary.setComponent(this);
		}
		this.localizedDictionary = localizedDictionary;
	}

	public FIBLocalizedDictionary getLocalizedDictionary() {
		return localizedDictionary;
	}

	public String getDefinitionFile() {
		return definitionFile;
	}

	public void setDefinitionFile(String definitionFile) {
		this.definitionFile = definitionFile;
	}

	public List<FIBButton> getDefaultButtons() {
		List<FIBButton> defaultButtons = new ArrayList<FIBButton>();
		if (this instanceof FIBContainer) {
			List<FIBButton> buttons = getFIBButtons(((FIBContainer) this).getSubComponents());
			if (buttons.size() > 0) {
				for (FIBButton b : buttons) {
					if (b.isDefault() != null && b.isDefault()) {
						defaultButtons.add(b);
					}
				}
			}
		}
		return defaultButtons;
	}

	private List<FIBButton> getFIBButtons(List<FIBComponent> subComponents) {
		List<FIBButton> buttons = new ArrayList<FIBButton>();
		for (FIBComponent c : subComponents) {
			if (c instanceof FIBButton) {
				buttons.add((FIBButton) c);
			} else if (c instanceof FIBContainer) {
				buttons.addAll(getFIBButtons(((FIBContainer) c).getSubComponents()));
			}
		}
		return buttons;
	}

	@Override
	protected void applyValidation(ValidationReport report) {
		super.applyValidation(report);
		performValidation(RootComponentShouldHaveDataClass.class, report);
		performValidation(DataBindingMustBeValid.class, report);
		performValidation(VisibleBindingMustBeValid.class, report);
		performValidation(NonRootComponentShouldNotHaveLocalizedDictionary.class, report);
	}

	@Override
	public List<DataBinding<?>> getDependencyBindings() {
		List<DataBinding<?>> returned = new ArrayList<DataBinding<?>>();
		returned.add(getData());
		returned.add(getVisible());
		return returned;
	}

	@Override
	public List<TargetObject> getChainedBindings(DataBinding<?> binding, TargetObject object) {
		return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public static class RootComponentShouldHaveDataClass extends ValidationRule<RootComponentShouldHaveDataClass, FIBComponent> {
		public RootComponentShouldHaveDataClass() {
			super(FIBModelObject.class, "root_component_should_have_data_class");
		}

		@Override
		public ValidationIssue<RootComponentShouldHaveDataClass, FIBComponent> applyValidation(FIBComponent object) {
			if (object.isRootComponent() && object.getDataClass() == null) {
				return new ValidationWarning<RootComponentShouldHaveDataClass, FIBComponent>(this, object,
						"component_($object.toString)_is_declared_as_root_but_does_not_have_any_data_class");
			}
			return null;
		}

	}

	public static class NonRootComponentShouldNotHaveLocalizedDictionary extends
			ValidationRule<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> {
		public NonRootComponentShouldNotHaveLocalizedDictionary() {
			super(FIBModelObject.class, "non_root_component_should_not_have_localized_dictionary");
		}

		@Override
		public ValidationIssue<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> applyValidation(FIBComponent object) {
			if (!object.isRootComponent() && object.getLocalizedDictionary() != null) {
				RemoveExtraLocalizedDictionary fixProposal = new RemoveExtraLocalizedDictionary();
				return new ValidationWarning<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent>(this, object,
						"component_($object.toString)_has_a_localized_dictionary_but_is_not_root_component", fixProposal);
			}
			return null;
		}

	}

	protected static class RemoveExtraLocalizedDictionary extends
			FixProposal<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> {

		public RemoveExtraLocalizedDictionary() {
			super("remove_extra_dictionary");
		}

		@Override
		protected void fixAction() {
			getObject().setLocalizedDictionary(null);
		}

	}

	public static class RootComponentShouldHaveMaximumOneDefaultButton extends
			ValidationRule<RootComponentShouldHaveMaximumOneDefaultButton, FIBComponent> {
		public RootComponentShouldHaveMaximumOneDefaultButton() {
			super(FIBModelObject.class, "root_component_should_have_maximum_one_default_button");
		}

		@Override
		public ValidationIssue<RootComponentShouldHaveMaximumOneDefaultButton, FIBComponent> applyValidation(FIBComponent object) {
			if (object.isRootComponent() && object instanceof FIBContainer) {
				List<FIBButton> defaultButtons = object.getDefaultButtons();
				if (defaultButtons.size() > 1) {
					return new ValidationWarning<RootComponentShouldHaveMaximumOneDefaultButton, FIBComponent>(this, object,
							"component_($object.toString)_has_more_than_one_default_button");
				}
			}
			return null;
		}

	}

	public static class DataBindingMustBeValid extends BindingMustBeValid<FIBComponent> {
		public DataBindingMustBeValid() {
			super("'data'_binding_is_not_valid", FIBComponent.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBComponent object) {
			return object.getData();
		}

	}

	public static class VisibleBindingMustBeValid extends BindingMustBeValid<FIBComponent> {
		public VisibleBindingMustBeValid() {
			super("'visible'_binding_is_not_valid", FIBComponent.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBComponent object) {
			return object.getVisible();
		}

	}

	public void notifiedBindingDecoded(DataBinding<?> binding) {
		logger.warning("A-t-on encore besoin de ca ?");
	}

	/*@Override
	public void notifiedBindingDecoded(DataBinding<?> binding) {
		if (binding == null) {
			return;
		}

		if (binding.isValid() && binding.getExpression() != null) {
			// System.out.println("For binding " + binding);
			for (BindingValue e : binding.getExpression().getAllBindingValues()) {
				// System.out.println(" > binding variable " + e.getBindingVariable() + " of " + e.getBindingVariable().getType());
				if (TypeUtils.isTypeAssignableFrom(FIBComponentDynamicModel.class, e.getBindingVariable().getType())) {
					FIBComponent c = getRootComponent().getComponentNamed(e.getBindingVariable().getVariableName());
					if (c != null) {
						// System.out.println("Component " + toString() + " depends of " + c.toString());
						declareDependantOf(c);
					} else {
						logger.warning("Cannot find component named " + e.getBindingVariable().getVariableName() + " in " + this.getName());
					}
				}
			}
		}

	}*/

	@Override
	public BindingFactory getBindingFactory() {
		if (bindingFactory != null) {
			return bindingFactory;
		}
		if (getParent() != null) {
			return getParent().getBindingFactory();
		}
		return FIBLibrary.instance().getBindingFactory();
	}

	public void setBindingFactory(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
	}
}
