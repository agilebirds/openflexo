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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeNode;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.fib.model.validation.ValidationWarning;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.DeserializationInitializer;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.toolbox.StringUtils;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBComponent.FIBComponentImpl.class)
@Imports({ @Import(FIBPanel.class), @Import(FIBTab.class), @Import(FIBSplitPanel.class), @Import(FIBTabPanel.class),
		@Import(FIBBrowser.class), @Import(FIBButton.class), @Import(FIBCheckBox.class), @Import(FIBColor.class), @Import(FIBCustom.class),
		@Import(FIBFile.class), @Import(FIBFont.class), @Import(FIBHtmlEditor.class), @Import(FIBImage.class), @Import(FIBLabel.class),
		@Import(FIBCheckboxList.class), @Import(FIBDropDown.class), @Import(FIBList.class), @Import(FIBRadioButtonList.class),
		@Import(FIBNumber.class), @Import(FIBReferencedComponent.class), @Import(FIBTable.class), @Import(FIBEditor.class),
		@Import(FIBEditorPane.class), @Import(FIBTextArea.class), @Import(FIBTextField.class) })
public abstract interface FIBComponent extends FIBModelObject, TreeNode {

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

	@PropertyIdentifier(type = FIBContainer.class)
	public static final String PARENT_KEY = "parent";
	@PropertyIdentifier(type = Integer.class)
	public static final String INDEX_KEY = "index";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = Class.class)
	public static final String DATA_CLASS_KEY = "dataClass";
	@PropertyIdentifier(type = Class.class)
	public static final String CONTROLLER_CLASS_KEY = "controllerClass";
	@PropertyIdentifier(type = ComponentConstraints.class)
	public static final String CONSTRAINTS_KEY = "constraints";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VISIBLE_KEY = "visible";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = Boolean.class)
	public static final String OPAQUE_KEY = "opaque";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_KEY = "backgroundColor";
	@PropertyIdentifier(type = Color.class)
	public static final String FOREGROUND_COLOR_KEY = "foregroundColor";
	@PropertyIdentifier(type = Integer.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Integer.class)
	public static final String HEIGHT_KEY = "height";
	@PropertyIdentifier(type = Integer.class)
	public static final String MIN_WIDTH_KEY = "minWidth";
	@PropertyIdentifier(type = Integer.class)
	public static final String MIN_HEIGHT_KEY = "minHeight";
	@PropertyIdentifier(type = Integer.class)
	public static final String MAX_WIDTH_KEY = "maxWidth";
	@PropertyIdentifier(type = Integer.class)
	public static final String MAX_HEIGHT_KEY = "maxHeight";
	@PropertyIdentifier(type = boolean.class)
	public static final String USE_SCROLL_BAR_KEY = "useScrollBar";
	@PropertyIdentifier(type = HorizontalScrollBarPolicy.class)
	public static final String HORIZONTAL_SCROLLBAR_POLICY_KEY = "horizontalScrollbarPolicy";
	@PropertyIdentifier(type = VerticalScrollBarPolicy.class)
	public static final String VERTICAL_SCROLLBAR_POLICY_KEY = "verticalScrollbarPolicy";
	@PropertyIdentifier(type = Vector.class)
	public static final String EXPLICIT_DEPENDANCIES_KEY = "explicitDependancies";
	@PropertyIdentifier(type = FIBLocalizedDictionary.class)
	public static final String LOCALIZED_DICTIONARY_KEY = "localizedDictionary";

	@Override
	@Getter(value = PARENT_KEY, inverse = FIBContainer.SUB_COMPONENTS_KEY)
	public FIBContainer getParent();

	@Setter(PARENT_KEY)
	public void setParent(FIBContainer parent);

	@Getter(value = INDEX_KEY)
	@XMLAttribute
	public Integer getIndex();

	@Setter(INDEX_KEY)
	public void setIndex(Integer index);

	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding<?> getData();

	@Setter(DATA_KEY)
	public void setData(DataBinding<?> data);

	@Getter(value = DATA_CLASS_KEY)
	@XMLAttribute(xmlTag = "dataClassName")
	public Class<?> getDataClass();

	@Setter(DATA_CLASS_KEY)
	public void setDataClass(Class<?> dataClass);

	@Getter(value = CONTROLLER_CLASS_KEY)
	@XMLAttribute(xmlTag = "controllerClassName")
	public Class<? extends FIBController> getControllerClass();

	@Setter(CONTROLLER_CLASS_KEY)
	public void setControllerClass(Class<? extends FIBController> controllerClass);

	@Getter(value = CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public ComponentConstraints getConstraints();

	@Setter(CONSTRAINTS_KEY)
	public void setConstraints(ComponentConstraints constraints);

	@Getter(value = VISIBLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getVisible();

	@Setter(VISIBLE_KEY)
	public void setVisible(DataBinding<Boolean> visible);

	@Getter(value = FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(FONT_KEY)
	public void setFont(Font font);

	@Getter(value = OPAQUE_KEY)
	@XMLAttribute
	public Boolean getOpaque();

	@Setter(OPAQUE_KEY)
	public void setOpaque(Boolean opaque);

	@Getter(value = BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundColor();

	@Setter(BACKGROUND_COLOR_KEY)
	public void setBackgroundColor(Color backgroundColor);

	@Getter(value = FOREGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getForegroundColor();

	@Setter(FOREGROUND_COLOR_KEY)
	public void setForegroundColor(Color foregroundColor);

	@Getter(value = WIDTH_KEY)
	@XMLAttribute
	public Integer getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(Integer width);

	@Getter(value = HEIGHT_KEY)
	@XMLAttribute
	public Integer getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(Integer height);

	@Getter(value = MIN_WIDTH_KEY)
	@XMLAttribute
	public Integer getMinWidth();

	@Setter(MIN_WIDTH_KEY)
	public void setMinWidth(Integer minWidth);

	@Getter(value = MIN_HEIGHT_KEY)
	@XMLAttribute
	public Integer getMinHeight();

	@Setter(MIN_HEIGHT_KEY)
	public void setMinHeight(Integer minHeight);

	@Getter(value = MAX_WIDTH_KEY)
	@XMLAttribute
	public Integer getMaxWidth();

	@Setter(MAX_WIDTH_KEY)
	public void setMaxWidth(Integer maxWidth);

	@Getter(value = MAX_HEIGHT_KEY)
	@XMLAttribute
	public Integer getMaxHeight();

	@Setter(MAX_HEIGHT_KEY)
	public void setMaxHeight(Integer maxHeight);

	@Getter(value = USE_SCROLL_BAR_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getUseScrollBar();

	@Setter(USE_SCROLL_BAR_KEY)
	public void setUseScrollBar(boolean useScrollBar);

	@Getter(value = HORIZONTAL_SCROLLBAR_POLICY_KEY)
	@XMLAttribute
	public HorizontalScrollBarPolicy getHorizontalScrollbarPolicy();

	@Setter(HORIZONTAL_SCROLLBAR_POLICY_KEY)
	public void setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy horizontalScrollbarPolicy);

	@Getter(value = VERTICAL_SCROLLBAR_POLICY_KEY)
	@XMLAttribute
	public VerticalScrollBarPolicy getVerticalScrollbarPolicy();

	@Setter(VERTICAL_SCROLLBAR_POLICY_KEY)
	public void setVerticalScrollbarPolicy(VerticalScrollBarPolicy verticalScrollbarPolicy);

	@Getter(value = EXPLICIT_DEPENDANCIES_KEY, cardinality = Cardinality.LIST, inverse = FIBDependancy.OWNER_KEY)
	public List<FIBDependancy> getExplicitDependancies();

	@Setter(EXPLICIT_DEPENDANCIES_KEY)
	public void setExplicitDependancies(List<FIBDependancy> explicitDependancies);

	@Adder(EXPLICIT_DEPENDANCIES_KEY)
	public void addToExplicitDependancies(FIBDependancy aExplicitDependancie);

	@Remover(EXPLICIT_DEPENDANCIES_KEY)
	public void removeFromExplicitDependancies(FIBDependancy aExplicitDependancie);

	@Getter(value = LOCALIZED_DICTIONARY_KEY, inverse = FIBLocalizedDictionary.OWNER_KEY)
	public FIBLocalizedDictionary getLocalizedDictionary();

	@Setter(LOCALIZED_DICTIONARY_KEY)
	public void setLocalizedDictionary(FIBLocalizedDictionary localizedDictionary);

	public boolean isRootComponent();

	public FIBComponent getRootComponent();

	@Deprecated
	public void notifiedBindingModelRecreated();

	public Type getDynamicAccessType();

	/**
	 * Recursive lookup method for contained FIBComponent
	 * 
	 * @param name
	 * @return
	 */
	public FIBComponent getComponentNamed(String name);

	public Font retrieveValidFont();

	public Color retrieveValidForegroundColor();

	public Color retrieveValidBackgroundColor();

	public void updateBindingModel();

	public void declareDependantOf(FIBComponent aComponent);

	public List<FIBButton> getDefaultButtons();

	public Date getLastModified();

	public void setLastModified(Date lastModified);

	public String getDefinitionFile();

	public void setDefinitionFile(String definitionFile);

	public FIBLocalizedDictionary retrieveFIBLocalizedDictionary();

	public Type getDataType();

	public boolean definePreferredDimensions();

	public boolean defineMaxDimensions();

	public boolean defineMinDimensions();

	public Iterator<FIBComponent> getMayDependsIterator();

	public Iterator<FIBComponent> getMayAltersIterator();

	public abstract String getIdentifier();

	public List<DataBinding<?>> getDeclaredBindings();

	@DeserializationInitializer
	public void initializeDeserialization();

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public static abstract class FIBComponentImpl extends FIBModelObjectImpl implements FIBComponent {

		private static final Logger logger = Logger.getLogger(FIBComponent.class.getPackage().getName());

		private BindingFactory bindingFactory;
		public static Color DISABLED_COLOR = Color.GRAY;

		@Deprecated
		public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
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

		public FIBComponentImpl() {
			super();
			explicitDependancies = new Vector<FIBDependancy>();
			mayDepends = new Vector<FIBComponent>();
			mayAlters = new Vector<FIBComponent>();
		}

		@Override
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

		@Override
		public ComponentConstraints getConstraints() {
			constraints = _normalizeConstraintsWhenRequired(constraints);
			return constraints;
		}

		@Override
		public void setConstraints(ComponentConstraints someConstraints) {
			// ComponentConstraints normalizedConstraints = constraints;
			ComponentConstraints normalizedConstraints = _normalizeConstraintsWhenRequired(someConstraints);
			FIBPropertyNotification<ComponentConstraints> notification = requireChange(CONSTRAINTS_KEY, normalizedConstraints);
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

		@Override
		public boolean isRootComponent() {
			return getParent() == null;
		}

		/**
		 * Return the root component for this component. Iterate over the top of the component hierarchy.
		 * 
		 * @return
		 */
		@Override
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

		@Override
		public void updateBindingModel() {
			if (deserializationPerformed) {
				logger.fine("updateBindingModel()");
				FIBComponentImpl root = (FIBComponentImpl) getRootComponent();
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
						_bindingModel
								.addToBindingVariables(new BindingVariable(subComponent.getName(), subComponent.getDynamicAccessType()));
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

		@Override
		@Deprecated
		public void notifiedBindingModelRecreated() {
		}

		protected boolean deserializationPerformed = true;

		@Override
		public void initializeDeserialization() {
			deserializationPerformed = false;
		}

		@Override
		public void finalizeDeserialization() {

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

		@Override
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
					public FIBComponentImpl next() {
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

		@Override
		public Iterator<FIBComponent> getMayDependsIterator() {
			return new ArrayList<FIBComponent>(mayDepends).iterator();
		}

		@Override
		public Iterator<FIBComponent> getMayAltersIterator() {
			return new ArrayList<FIBComponent>(mayAlters).iterator();
		}

		@Override
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
			if (!((FIBComponentImpl) aComponent).mayAlters.contains(this)) {
				((FIBComponentImpl) aComponent).mayAlters.add(this);
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

		@Override
		public Integer getIndex() {
			if (index == null) {
				if (getConstraints() != null && getConstraints().hasIndex()) {
					return getConstraints().getIndex();
				}
			}
			return index;
		}

		@Override
		public void setIndex(Integer index) {
			FIBPropertyNotification<Integer> notification = requireChange(INDEX_KEY, index);
			if (notification != null) {
				this.index = index;
				hasChanged(notification);
				if (getParent() != null) {
					getParent().reorderComponents();
				}
			}
		}

		@Override
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

		@Override
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

		@Override
		public DataBinding<Boolean> getVisible() {
			if (visible == null) {
				visible = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return visible;
		}

		@Override
		public void setVisible(DataBinding<Boolean> visible) {
			if (visible != null) {
				visible = new DataBinding<Boolean>(visible.toString(), this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			this.visible = visible;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " ("
					+ (getName() != null ? getName() : getIdentifier() != null ? getIdentifier() : "unnamed") + ")";
		}

		/**
		 * Return the FIBComponent this component refer to
		 * 
		 * @return
		 */
		@Override
		public FIBComponentImpl getComponent() {
			return this;
		}

		@Override
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

		@Override
		public Class<?> getDataClass() {
			return dataClass;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public void setDataClass(Class<?> dataClass) {
			FIBPropertyNotification<Class> notification = requireChange(DATA_CLASS_KEY, (Class) dataClass);
			if (notification != null) {
				this.dataClass = dataClass;
				updateBindingModel();
				hasChanged(notification);
			}
		}

		@Override
		public Class<? extends FIBController> getControllerClass() {
			return controllerClass;
		}

		@Override
		public void setControllerClass(Class<? extends FIBController> controllerClass) {

			FIBPropertyNotification<Class> notification = requireChange(CONTROLLER_CLASS_KEY, (Class) controllerClass);
			if (notification != null) {
				this.controllerClass = controllerClass;
				updateBindingModel();
				hasChanged(notification);
			}
		}

		public Type getDefaultDataClass() {
			return Object.class;

		}

		@Override
		public Type getDynamicAccessType() {
			if (data != null) {
				Type[] args = new Type[3];
				args[0] = new WilcardTypeImpl(FIBComponent.class);
				args[1] = new WilcardTypeImpl(JComponent.class);
				args[2] = getDataType();
				return new ParameterizedTypeImpl(FIBView.class, args);
			}
			return null;
		}

		@Override
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

		@Override
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

		@Override
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

		@Override
		public Font getFont() {
			return font;
		}

		@Override
		public void setFont(Font font) {
			FIBPropertyNotification<Font> notification = requireChange(FONT_KEY, font);
			if (notification != null) {
				this.font = font;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getOpaque() {
			return opaque;
		}

		@Override
		public void setOpaque(Boolean opaque) {
			FIBPropertyNotification<Boolean> notification = requireChange(OPAQUE_KEY, opaque);
			if (notification != null) {
				this.opaque = opaque;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundColor() {
			return backgroundColor;
		}

		@Override
		public void setBackgroundColor(Color backgroundColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_COLOR_KEY, backgroundColor);
			if (notification != null) {
				this.backgroundColor = backgroundColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}

		@Override
		public void setForegroundColor(Color foregroundColor) {
			FIBPropertyNotification<Color> notification = requireChange(FOREGROUND_COLOR_KEY, foregroundColor);
			if (notification != null) {
				this.foregroundColor = foregroundColor;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getUseScrollBar() {
			return useScrollBar;
		}

		@Override
		public void setUseScrollBar(boolean useScrollBar) {
			FIBPropertyNotification<Boolean> notification = requireChange(USE_SCROLL_BAR_KEY, useScrollBar);
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

		@Override
		public HorizontalScrollBarPolicy getHorizontalScrollbarPolicy() {
			return horizontalScrollbarPolicy;
		}

		@Override
		public void setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy horizontalScrollbarPolicy) {
			FIBPropertyNotification<HorizontalScrollBarPolicy> notification = requireChange(HORIZONTAL_SCROLLBAR_POLICY_KEY,
					horizontalScrollbarPolicy);
			if (notification != null) {
				this.horizontalScrollbarPolicy = horizontalScrollbarPolicy;
				hasChanged(notification);
			}
		}

		@Override
		public VerticalScrollBarPolicy getVerticalScrollbarPolicy() {
			return verticalScrollbarPolicy;
		}

		@Override
		public void setVerticalScrollbarPolicy(VerticalScrollBarPolicy verticalScrollbarPolicy) {
			FIBPropertyNotification<VerticalScrollBarPolicy> notification = requireChange(VERTICAL_SCROLLBAR_POLICY_KEY,
					verticalScrollbarPolicy);
			if (notification != null) {
				this.verticalScrollbarPolicy = verticalScrollbarPolicy;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getWidth() {
			return width;
		}

		@Override
		public void setWidth(Integer width) {
			FIBPropertyNotification<Integer> notification = requireChange(WIDTH_KEY, width);
			if (notification != null) {
				this.width = width;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getHeight() {
			return height;
		}

		@Override
		public void setHeight(Integer height) {
			FIBPropertyNotification<Integer> notification = requireChange(HEIGHT_KEY, height);
			if (notification != null) {
				this.height = height;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMinWidth() {
			return minWidth;
		}

		@Override
		public void setMinWidth(Integer minWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(MIN_HEIGHT_KEY, minWidth);
			if (notification != null) {
				this.minWidth = minWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMinHeight() {
			return minHeight;
		}

		@Override
		public void setMinHeight(Integer minHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(MIN_HEIGHT_KEY, minHeight);
			if (notification != null) {
				this.minHeight = minHeight;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMaxWidth() {
			return maxWidth;
		}

		@Override
		public void setMaxWidth(Integer maxWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(MAX_WIDTH_KEY, maxWidth);
			if (notification != null) {
				this.maxWidth = maxWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMaxHeight() {
			return maxHeight;
		}

		@Override
		public void setMaxHeight(Integer maxHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(MAX_HEIGHT_KEY, maxHeight);
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
			performSuperSetter(NAME_KEY, name);
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
				performSuperAdder(PARAMETERS_KEY, p);
				updateBindingModel();
			}
		}

		@Override
		public boolean definePreferredDimensions() {
			return width != null && height != null;
		}

		public void setDefinePreferredDimensions(boolean definePreferredDimensions) {
			if (definePreferredDimensions() != definePreferredDimensions) {
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
				notifyChange("definePreferredDimensions", !definePreferredDimensions(), definePreferredDimensions());
			}
		}

		@Override
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

		@Override
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

		@Override
		public Vector<FIBDependancy> getExplicitDependancies() {
			return explicitDependancies;
		}

		public void setExplicitDependancies(Vector<FIBDependancy> explicitDependancies) {
			FIBPropertyNotification<Vector<FIBDependancy>> notification = requireChange(EXPLICIT_DEPENDANCIES_KEY, explicitDependancies);
			explicitDependancies = null;
			if (notification != null) {
				this.explicitDependancies = explicitDependancies;
				hasChanged(notification);
			}
		}

		@Override
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
			getPropertyChangeSupport().firePropertyChange(EXPLICIT_DEPENDANCIES_KEY, null, explicitDependancies);
		}

		@Override
		public void removeFromExplicitDependancies(FIBDependancy p) {
			p.setOwner(null);
			explicitDependancies.remove(p);
			getPropertyChangeSupport().firePropertyChange(EXPLICIT_DEPENDANCIES_KEY, null, explicitDependancies);
		}

		public FIBDependancy createNewExplicitDependancy() {
			FIBDependancy returned = getFactory().newInstance(FIBDependancy.class);
			addToExplicitDependancies(returned);
			return returned;
		}

		public void deleteExplicitDependancy(FIBDependancy p) {
			removeFromExplicitDependancies(p);
		}

		@Override
		public FIBLocalizedDictionary retrieveFIBLocalizedDictionary() {
			if (getLocalizedDictionary() == null) {
				FIBLocalizedDictionary newFIBLocalizedDictionary = getFactory().newInstance(FIBLocalizedDictionary.class);
				setLocalizedDictionary(newFIBLocalizedDictionary);
			}
			return getLocalizedDictionary();
		}

		@Override
		public String getDefinitionFile() {
			return definitionFile;
		}

		@Override
		public void setDefinitionFile(String definitionFile) {
			this.definitionFile = definitionFile;
		}

		@Override
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

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = new ArrayList<DataBinding<?>>();
			returned.add(getData());
			returned.add(getVisible());
			return returned;
		}

		/*@Override
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

		}*/

		@Override
		public Date getLastModified() {
			return lastModified;
		}

		@Override
		public void setLastModified(Date lastModified) {
			this.lastModified = lastModified;
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

	public static class RemoveExtraLocalizedDictionary extends FixProposal<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> {

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
				List<FIBButton> defaultButtons = ((FIBContainer) object).getDefaultButtons();
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

}
