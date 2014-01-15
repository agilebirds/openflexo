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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.binding.PatternRoleBindingVariable;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.StringUtils;

/**
 * An EditionScheme represents a behavioural feature attached to an EditionPattern
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(EditionScheme.EditionSchemeImpl.class)
public interface EditionScheme extends EditionSchemeObject, ActionContainer, Function {

	public static final String THIS = "this";
	public static final String VIRTUAL_MODEL_INSTANCE = "virtualModelInstance";

	@PropertyIdentifier(type = EditionPattern.class)
	public static final String EDITION_PATTERN_KEY = "editionPattern";
	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = boolean.class)
	public static final String SKIP_CONFIRMATION_PANEL_KEY = "skipConfirmationPanel";
	@PropertyIdentifier(type = boolean.class)
	public static final String DEFINE_POPUP_DEFAULT_SIZE_KEY = "definePopupDefaultSize";
	@PropertyIdentifier(type = int.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = int.class)
	public static final String HEIGHT_KEY = "height";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = Vector.class)
	public static final String PARAMETERS_KEY = "parameters";

	@Override
	@Getter(value = EDITION_PATTERN_KEY, inverse = EditionPattern.EDITION_SCHEMES_KEY)
	public EditionPattern getEditionPattern();

	@Setter(EDITION_PATTERN_KEY)
	public void setEditionPattern(EditionPattern editionPattern);

	@Override
	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Override
	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Getter(value = SKIP_CONFIRMATION_PANEL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getSkipConfirmationPanel();

	@Setter(SKIP_CONFIRMATION_PANEL_KEY)
	public void setSkipConfirmationPanel(boolean skipConfirmationPanel);

	@Getter(value = DEFINE_POPUP_DEFAULT_SIZE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getDefinePopupDefaultSize();

	@Setter(DEFINE_POPUP_DEFAULT_SIZE_KEY)
	public void setDefinePopupDefaultSize(boolean definePopupDefaultSize);

	@Getter(value = WIDTH_KEY, defaultValue = "0")
	@XMLAttribute
	public int getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(int width);

	@Getter(value = HEIGHT_KEY, defaultValue = "0")
	@XMLAttribute
	public int getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(int height);

	@Override
	@Getter(value = DESCRIPTION_KEY)
	@XMLElement
	public String getDescription();

	@Override
	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	public List<EditionSchemeParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<EditionSchemeParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(EditionSchemeParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(EditionSchemeParameter aParameter);

	@Finder(collection = PARAMETERS_KEY, attribute = EditionSchemeParameter.NAME_KEY)
	public EditionSchemeParameter getParameter(String name);

	@DeserializationFinalizer
	public void finalizeEditionSchemeDeserialization();

	public void updateBindingModels();

	public EditionSchemeType getEditionSchemeType();

	public EditionSchemeActionType getEditionSchemeActionType();

	public EditionSchemeParametersType getEditionSchemeParametersType();

	public EditionSchemeParametersValuesType getEditionSchemeParametersValuesType();

	public EditionSchemeParameters getEditionSchemeParameters();

	public String getSignature();

	@Override
	public List<EditionSchemeParameter> getArguments();

	public static abstract class EditionSchemeImpl extends EditionSchemeObjectImpl implements EditionScheme {

		protected BindingModel _bindingModel;

		//
		protected static final Logger logger = FlexoLogger.getLogger(EditionScheme.class.getPackage().getName());

		private String name;
		private String label;
		private String description;
		// private Vector<EditionAction<?, ?>> actions;
		// private Vector<EditionSchemeParameter> parameters;
		private boolean skipConfirmationPanel = false;

		// private EditionPattern _editionPattern;

		private final EditionSchemeParameters editionSchemeParameters;

		private boolean definePopupDefaultSize = false;
		private int width = 800;
		private int height = 600;

		private final EditionSchemeType editionSchemeType = new EditionSchemeType(this);
		private final EditionSchemeActionType editionSchemeActionType = new EditionSchemeActionType(this);
		private final EditionSchemeParametersType editionSchemeParametersType = new EditionSchemeParametersType(this);
		private final EditionSchemeParametersValuesType editionSchemeParametersValuesType = new EditionSchemeParametersValuesType(this);

		/**
		 * Stores a chained collections of objects which are involved in validation
		 */
		private final ChainedCollection<ViewPointObject> validableObjects = null;

		public EditionSchemeImpl() {
			super();
			// actions = new Vector<EditionAction<?, ?>>();
			// parameters = new Vector<EditionSchemeParameter>();
			editionSchemeParameters = getVirtualModelFactory().newEditionSchemeParameters(this);
		}

		@Override
		public EditionSchemeType getEditionSchemeType() {
			return editionSchemeType;
		}

		@Override
		public EditionSchemeActionType getEditionSchemeActionType() {
			return editionSchemeActionType;
		}

		@Override
		public EditionSchemeParametersType getEditionSchemeParametersType() {
			return editionSchemeParametersType;
		}

		@Override
		public EditionSchemeParametersValuesType getEditionSchemeParametersValuesType() {
			return editionSchemeParametersValuesType;
		}

		@Override
		public EditionSchemeParameters getEditionSchemeParameters() {
			return editionSchemeParameters;
		}

		@Override
		public String getStringRepresentation() {
			return (getEditionPattern() != null ? getEditionPattern().getStringRepresentation() : "null") + "." + getName();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append(getClass().getSimpleName() + " " + getName() + "(" + getParametersFMLRepresentation(context) + ") {", context);
			out.append(StringUtils.LINE_SEPARATOR, context);
			for (EditionAction action : getActions()) {
				out.append(action.getFMLRepresentation(context), context, 1);
				out.append(StringUtils.LINE_SEPARATOR, context);
			}
			out.append("}", context);
			out.append(StringUtils.LINE_SEPARATOR, context);
			return out.toString();
		}

		protected String getParametersFMLRepresentation(FMLRepresentationContext context) {
			if (getParameters().size() > 0) {
				StringBuffer sb = new StringBuffer();
				boolean isFirst = true;
				for (EditionSchemeParameter p : getParameters()) {
					sb.append((isFirst ? "" : ", ") + TypeUtils.simpleRepresentation(p.getType()) + " " + p.getName());
					isFirst = false;
				}
				return sb.toString();
			}
			return "";
		}

		/**
		 * Return the URI of the {@link NamedViewPointObject}<br>
		 * The convention for URI are following: <viewpoint_uri>/<virtual_model_name>#<edition_pattern_name>.<edition_scheme_name> <br>
		 * eg<br>
		 * http://www.mydomain.org/MyViewPoint/MyVirtualModel#MyEditionPattern.MyEditionScheme
		 * 
		 * @return String representing unique URI of this object
		 */
		@Override
		public String getURI() {
			return getEditionPattern().getURI() + "." + getName();
		}

		@Override
		public EditionSchemeImpl getEditionScheme() {
			return this;
		}

		@Override
		public String getLabel() {
			if (label == null || StringUtils.isEmpty(label) || label.equals(name)) {
				return getName();
			}
			return label;
		}

		@Override
		public void setLabel(String label) {
			this.label = label;
		}

		/*@Override
		public EditionPattern getEditionPattern() {
			return _editionPattern;
		}*/

		@Override
		public void setEditionPattern(EditionPattern editionPattern) {
			performSuperSetter(EDITION_PATTERN_KEY, editionPattern);
			// _editionPattern = editionPattern;
			updateBindingModels();
		}

		/*@Override
		public Vector<EditionAction<?, ?>> getActions() {
			return actions;
		}

		@Override
		public void setActions(Vector<EditionAction<?, ?>> actions) {
			this.actions = actions;
			setChanged();
			notifyObservers();
		}

		@Override
		public void addToActions(EditionAction<?, ?> action) {
			// action.setScheme(this);
			action.setActionContainer(this);
			actions.add(action);
			setChanged();
			notifyObservers();
			notifyChange("actions", null, actions);
		}

		@Override
		public void removeFromActions(EditionAction<?, ?> action) {
			// action.setScheme(null);
			action.setActionContainer(null);
			actions.remove(action);
			setChanged();
			notifyObservers();
			notifyChange("actions", null, actions);
		}*/

		/*@Override
		public int getIndex(EditionAction action) {
			return actions.indexOf(action);
		}

		@Override
		public void insertActionAtIndex(EditionAction action, int index) {
			// action.setScheme(this);
			action.setActionContainer(this);
			actions.insertElementAt(action, index);
			setChanged();
			notifyObservers();
			notifyChange("actions", null, actions);
		}

		@Override
		public void actionFirst(EditionAction a) {
			actions.remove(a);
			actions.insertElementAt(a, 0);
			setChanged();
			notifyChange("actions", null, actions);
		}

		@Override
		public void actionUp(EditionAction a) {
			int index = actions.indexOf(a);
			if (index > 0) {
				actions.remove(a);
				actions.insertElementAt(a, index - 1);
				setChanged();
				notifyChange("actions", null, actions);
			}
		}

		@Override
		public void actionDown(EditionAction a) {
			int index = actions.indexOf(a);
			if (index > -1) {
				actions.remove(a);
				actions.insertElementAt(a, index + 1);
				setChanged();
				notifyChange("actions", null, actions);
			}
		}

		@Override
		public void actionLast(EditionAction a) {
			actions.remove(a);
			actions.add(a);
			setChanged();
			notifyChange("actions", null, actions);
		}
		*/

		/*	@Override
			public Vector<EditionSchemeParameter> getParameters() {
				return parameters;
			}*/

		@Override
		public void setParameters(List<EditionSchemeParameter> someParameters) {
			performSuperSetter(PARAMETERS_KEY, someParameters);
			updateBindingModels();
		}

		@Override
		public void addToParameters(EditionSchemeParameter parameter) {
			performSuperAdder(PARAMETERS_KEY, parameter);
			updateBindingModels();
			for (EditionSchemeParameter p : getParameters()) {
				p.notifyBindingModelChanged();
			}
		}

		@Override
		public void removeFromParameters(EditionSchemeParameter parameter) {
			performSuperRemover(PARAMETERS_KEY, parameter);
			updateBindingModels();
		}

		public void parameterFirst(EditionSchemeParameter p) {
			System.out.println("parameterFirst()");
			getParameters().remove(p);
			getParameters().add(0, p);
			setChanged();
			notifyObservers(new DataModification("parameters", null, getParameters()));
		}

		public void parameterUp(EditionSchemeParameter p) {
			System.out.println("parameterUp()");
			int index = getParameters().indexOf(p);
			if (index > 0) {
				getParameters().remove(p);
				getParameters().add(index - 1, p);
				setChanged();
				notifyObservers(new DataModification("parameters", null, getParameters()));
			}
		}

		public void parameterDown(EditionSchemeParameter p) {
			System.out.println("parameterDown()");
			int index = getParameters().indexOf(p);
			if (index > -1) {
				getParameters().remove(p);
				getParameters().add(index + 1, p);
				setChanged();
				notifyObservers(new DataModification("parameters", null, getParameters()));
			}
		}

		public void parameterLast(EditionSchemeParameter p) {
			System.out.println("parameterLast()");
			getParameters().remove(p);
			getParameters().add(p);
			setChanged();
			notifyObservers(new DataModification("parameters", null, getParameters()));
		}

		/*public EditionSchemeParameter getParameter(String name) {
			if (name == null) {
				return null;
			}
			for (EditionSchemeParameter p : parameters) {
				if (name.equals(p.getName())) {
					return p;
				}
			}
			return null;
		}*/

		@Override
		public VirtualModel getVirtualModel() {
			if (getEditionPattern() != null && getEditionPattern().getVirtualModel() != null) {
				return getEditionPattern().getVirtualModel();
			}
			if (getEditionPattern() instanceof VirtualModel) {
				return (VirtualModel) getEditionPattern();
			}
			return null;
		}

		/*
		@Override
		public AddShape createAddShapeAction() {
			AddShape newAction = new AddShape(null);
			if (getEditionPattern().getDefaultShapePatternRole() != null) {
				newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
			}
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddClass createAddClassAction() {
			AddClass newAction = new AddClass(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddIndividual createAddIndividualAction() {
			AddIndividual newAction = new AddIndividual(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddObjectPropertyStatement createAddObjectPropertyStatementAction() {
			AddObjectPropertyStatement newAction = new AddObjectPropertyStatement(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddDataPropertyStatement createAddDataPropertyStatementAction() {
			AddDataPropertyStatement newAction = new AddDataPropertyStatement(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddIsAStatement createAddIsAPropertyAction() {
			AddIsAStatement newAction = new AddIsAStatement(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddRestrictionStatement createAddRestrictionAction() {
			AddRestrictionStatement newAction = new AddRestrictionStatement(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddConnector createAddConnectorAction() {
			AddConnector newAction = new AddConnector(null);
			if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
				newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
			}
			addToActions(newAction);
			return newAction;
		}

		@Override
		public DeclarePatternRole createDeclarePatternRoleAction() {
			DeclarePatternRole newAction = new DeclarePatternRole(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public GraphicalAction createGraphicalAction() {
			GraphicalAction newAction = new GraphicalAction(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public CreateDiagram createAddDiagramAction() {
			CreateDiagram newAction = new CreateDiagram(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public AddEditionPattern createAddEditionPatternAction() {
			AddEditionPattern newAction = new AddEditionPattern(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public ConditionalAction createConditionalAction() {
			ConditionalAction newAction = new ConditionalAction(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public IterationAction createIterationAction() {
			IterationAction newAction = new IterationAction(null);
			addToActions(newAction);
			return newAction;
		}

		public CloneShape createCloneShapeAction() {
			CloneShape newAction = new CloneShape(null);
			if (getEditionPattern().getDefaultShapePatternRole() != null) {
				newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
			}
			addToActions(newAction);
			return newAction;
		}

		public CloneConnector createCloneConnectorAction() {
			CloneConnector newAction = new CloneConnector(null);
			if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
				newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
			}
			addToActions(newAction);
			return newAction;
		}

		public CloneIndividual createCloneIndividualAction() {
			CloneIndividual newAction = new CloneIndividual(null);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public DeleteAction createDeleteAction() {
			DeleteAction newAction = new DeleteAction(null);
			addToActions(newAction);
			return newAction;
		}*/

		/**
		 * Creates a new {@link EditionAction} of supplied class, and add it at the end of action list<br>
		 * Delegates creation to model slot
		 * 
		 * @return newly created {@link EditionAction}
		 */
		@Override
		public <A extends EditionAction<?, ?>> A createAction(Class<A> actionClass, ModelSlot<?> modelSlot) {
			A newAction = modelSlot.createAction(actionClass);
			addToActions(newAction);
			return newAction;
		}

		@Override
		public EditionAction deleteAction(EditionAction anAction) {
			removeFromActions(anAction);
			anAction.delete();
			return anAction;
		}

		public EditionSchemeParameter createURIParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newURIParameter();
			newParameter.setName("uri");
			// newParameter.setLabel("uri");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createTextFieldParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newTextFieldParameter();
			newParameter.setName("textField");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createTextAreaParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newTextAreaParameter();
			newParameter.setName("textArea");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createIntegerParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newIntegerParameter();
			newParameter.setName("integer");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createCheckBoxParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newCheckboxParameter();
			newParameter.setName("checkbox");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createDropDownParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newDropDownParameter();
			newParameter.setName("dropdown");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createIndividualParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newIndividualParameter();
			newParameter.setName("individual");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createClassParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newClassParameter();
			newParameter.setName("class");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createPropertyParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newPropertyParameter();
			newParameter.setName("property");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createObjectPropertyParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newObjectPropertyParameter();
			newParameter.setName("property");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createDataPropertyParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newDataPropertyParameter();
			newParameter.setName("property");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		/*public EditionSchemeImplParameter createFlexoObjectParameter() {
			EditionSchemeParameter newParameter = new FlexoObjectParameter(null);
			newParameter.setName("flexoObject");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}*/

		public EditionSchemeParameter createTechnologyObjectParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newTechnologyObjectParameter();
			newParameter.setName("technologyObject");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createListParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newListParameter();
			newParameter.setName("list");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter createEditionPatternInstanceParameter() {
			EditionSchemeParameter newParameter = getVirtualModelFactory().newEditionPatternInstanceParameter();
			newParameter.setName("editionPatternInstance");
			// newParameter.setLabel("label");
			addToParameters(newParameter);
			return newParameter;
		}

		public EditionSchemeParameter deleteParameter(EditionSchemeParameter aParameter) {
			removeFromParameters(aParameter);
			aParameter.delete();
			return aParameter;
		}

		@Override
		public void finalizeEditionSchemeDeserialization() {
			updateBindingModels();
		}

		@Override
		public boolean getSkipConfirmationPanel() {
			return skipConfirmationPanel;
		}

		@Override
		public void setSkipConfirmationPanel(boolean skipConfirmationPanel) {
			this.skipConfirmationPanel = skipConfirmationPanel;
		}

		@Override
		public BindingModel getBindingModel() {
			if (isRebuildingBindingModel) {
				return _bindingModel;
			}
			if (_bindingModel == null) {
				createBindingModel();
			}
			return _bindingModel;
		}

		@Override
		public BindingModel getInferedBindingModel() {
			return getBindingModel();
		}

		@Override
		public void updateBindingModels() {
			if (isDeserializing()) {
				return;
			}
			/*if (getName().equals("synchronization")) {
				System.out.println("******* updateBindingModels() for " + this + " " + getName() + " and ep=" + getEditionPattern());
			}*/
			logger.fine("updateBindingModels()");
			_bindingModel = null;
			createBindingModel();
			rebuildActionsBindingModel();
			recursivelyUpdateInferedBindingModels(this);
		}

		private void recursivelyUpdateInferedBindingModels(ActionContainer container) {
			/*if (getName().equals("synchronization")) {
				System.out.println("    > recursivelyUpdateInferedBindingModels for " + container + " bindingModel=" + getBindingModel());
			}*/
			for (EditionAction action : container.getActions()) {
				action.rebuildInferedBindingModel();
				if (action instanceof ActionContainer) {
					recursivelyUpdateInferedBindingModels((ActionContainer) action);
				}
			}
		}

		protected void rebuildActionsBindingModel() {
			for (EditionAction action : getActions()) {
				action.rebuildInferedBindingModel();
			}
		}

		private boolean isRebuildingBindingModel = false;

		private final void createBindingModel() {
			_bindingModel = new BindingModel();
			isRebuildingBindingModel = true;
			_bindingModel.addToBindingVariables(new BindingVariable("parameters", getEditionSchemeParametersValuesType()));
			_bindingModel.addToBindingVariables(new BindingVariable("parametersDefinitions", getEditionSchemeParametersType()));
			// _bindingModel.addToBindingVariables(new EditionSchemeParametersBindingVariable(this));
			// _bindingModel.addToBindingVariables(new EditionSchemeParameterListPathElement(this, null));
			appendContextualBindingVariables(_bindingModel);
			if (getEditionPattern() != null) {
				for (final PatternRole role : getEditionPattern().getPatternRoles()) {
					_bindingModel.addToBindingVariables(new PatternRoleBindingVariable(role));
				}
			}
			for (final EditionAction a : getActions()) {
				if (a instanceof AssignableAction && ((AssignableAction) a).getIsVariableDeclaration()) {
					_bindingModel.addToBindingVariables(new BindingVariable(((AssignableAction) a).getVariableName(),
							((AssignableAction) a).getAssignableType(), true) {
						@Override
						public Type getType() {
							return ((AssignableAction) a).getAssignableType();
						}
					});
				}
			}
			notifyBindingModelChanged();
			isRebuildingBindingModel = false;
		}

		protected void appendContextualBindingVariables(BindingModel bindingModel) {
			// Si edition pattern est un diagram spec alors rajouter la varialble diagram
			// AprÃ¨s faudra voir au runtime;
			if (getEditionPattern() != null) {
				bindingModel.addToBindingVariables(new BindingVariable(EditionScheme.THIS, EditionPatternInstanceType
						.getEditionPatternInstanceType(getEditionPattern())));
				/*if (getEditionPattern().getVirtualModel() instanceof DiagramSpecification) {
					bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.DIAGRAM, EditionPatternInstanceType
							.getEditionPatternInstanceType(getEditionPattern().getVirtualModel())));
				} 
				if(getEditionPattern() instanceof DiagramSpecification){
					bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.DIAGRAM, EditionPatternInstanceType
							.getEditionPatternInstanceType(getEditionPattern())));
					bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.TOP_LEVEL, DiagramRootPane.class));
				}
				else {
					bindingModel.addToBindingVariables(new BindingVariable(EditionScheme.VIRTUAL_MODEL_INSTANCE, EditionPatternInstanceType
							.getEditionPatternInstanceType(getEditionPattern().getVirtualModel())));
				}*/
			}
			// if (this instanceof DiagramEditionScheme) {
			/*if (getEditionPattern() != null && getEditionPattern().getVirtualModel() instanceof DiagramSpecification) {
				bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.TOP_LEVEL, DiagramRootPane.class));
			}*/
		}

		@Override
		public void variableAdded(AssignableAction action) {
			updateBindingModels();
		}

		/**
		 * Duplicates this EditionScheme, given a new name<br>
		 * Newly created EditionScheme is added to parent EditionPattern
		 * 
		 * @param newName
		 * @return
		 */
		public EditionScheme duplicate(String newName) {
			EditionScheme newEditionScheme = (EditionScheme) cloneObject();
			newEditionScheme.setName(newName);
			getEditionPattern().addToEditionSchemes(newEditionScheme);
			return newEditionScheme;
		}

		@Override
		public boolean getDefinePopupDefaultSize() {
			return definePopupDefaultSize;
		}

		@Override
		public void setDefinePopupDefaultSize(boolean definePopupDefaultSize) {
			this.definePopupDefaultSize = definePopupDefaultSize;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public void setWidth(int width) {
			this.width = width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public void setHeight(int height) {
			this.height = height;
		}

		@Override
		public Type getReturnType() {
			return Void.TYPE;
		}

		@Override
		public List<EditionSchemeParameter> getArguments() {
			return getParameters();
		}

		private String editionSchemeSignature = null;

		@Override
		public String getSignature() {
			if (editionSchemeSignature == null) {
				StringBuffer signature = new StringBuffer();
				signature.append(getName());
				signature.append("(");
				signature.append(getParameterListAsString(false));
				signature.append(")");
				editionSchemeSignature = signature.toString();
			}
			return editionSchemeSignature;
		}

		private String getParameterListAsString(boolean fullyQualified) {
			StringBuffer returned = new StringBuffer();
			boolean isFirst = true;
			for (EditionSchemeParameter param : getParameters()) {
				returned.append((isFirst ? "" : ",")
						+ (fullyQualified ? TypeUtils.fullQualifiedRepresentation(param.getType()) : TypeUtils.simpleRepresentation(param
								.getType())));
				isFirst = false;
			}
			return returned.toString();
		}

	}
}
