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
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.binding.PatternRoleBindingVariable;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.StringUtils;

/**
 * An EditionScheme represents a behavioural feature attached to an EditionPattern
 * 
 * @author sylvain
 * 
 */
public abstract class EditionScheme extends EditionSchemeObject implements ActionContainer, Function {

	protected BindingModel _bindingModel;

	//
	protected static final Logger logger = FlexoLogger.getLogger(EditionScheme.class.getPackage().getName());

	public static final String THIS = "this";
	public static final String VIRTUAL_MODEL_INSTANCE = "virtualModelInstance";

	private String name;
	private String label;
	private String description;
	private Vector<EditionAction<?, ?>> actions;
	private Vector<EditionSchemeParameter> parameters;
	private boolean skipConfirmationPanel = false;

	private EditionPattern _editionPattern;

	private EditionSchemeParameters editionSchemeParameters;

	private boolean definePopupDefaultSize = false;
	private int width = 800;
	private int height = 600;

	private EditionSchemeType editionSchemeType = new EditionSchemeType(this);
	private EditionSchemeActionType editionSchemeActionType = new EditionSchemeActionType(this);
	private EditionSchemeParametersType editionSchemeParametersType = new EditionSchemeParametersType(this);
	private EditionSchemeParametersValuesType editionSchemeParametersValuesType = new EditionSchemeParametersValuesType(this);

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	public EditionScheme(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		actions = new Vector<EditionAction<?, ?>>();
		parameters = new Vector<EditionSchemeParameter>();
		editionSchemeParameters = new EditionSchemeParameters(this);
	}

	public EditionSchemeType getEditionSchemeType() {
		return editionSchemeType;
	}

	public EditionSchemeActionType getEditionSchemeActionType() {
		return editionSchemeActionType;
	}

	public EditionSchemeParametersType getEditionSchemeParametersType() {
		return editionSchemeParametersType;
	}

	public EditionSchemeParametersValuesType getEditionSchemeParametersValuesType() {
		return editionSchemeParametersValuesType;
	}

	public EditionSchemeParameters getEditionSchemeParameters() {
		return editionSchemeParameters;
	}

	@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getActions(), getParameters());
		}
		return validableObjects;
	}

	@Override
	public String getFullyQualifiedName() {
		return (getEditionPattern() != null ? getEditionPattern().getFullyQualifiedName() : "null") + "." + getName();
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
	public EditionScheme getEditionScheme() {
		return this;
	}

	public String getLabel() {
		if (label == null || StringUtils.isEmpty(label) || label.equals(name)) {
			return getName();
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return _editionPattern;
	}

	public void setEditionPattern(EditionPattern editionPattern) {
		_editionPattern = editionPattern;
		updateBindingModels();
	}

	@Override
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
	}

	@Override
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

	public Vector<EditionSchemeParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<EditionSchemeParameter> someParameters) {
		parameters = someParameters;
		updateBindingModels();
	}

	public void addToParameters(EditionSchemeParameter parameter) {
		parameter.setScheme(this);
		parameters.add(parameter);
		updateBindingModels();
		for (EditionSchemeParameter p : parameters) {
			p.notifyBindingModelChanged();
		}
	}

	public void removeFromParameters(EditionSchemeParameter parameter) {
		parameter.setScheme(null);
		parameters.remove(parameter);
		updateBindingModels();
	}

	public void parameterFirst(EditionSchemeParameter p) {
		System.out.println("parameterFirst()");
		parameters.remove(p);
		parameters.insertElementAt(p, 0);
		setChanged();
		notifyObservers(new DataModification("parameters", null, parameters));
	}

	public void parameterUp(EditionSchemeParameter p) {
		System.out.println("parameterUp()");
		int index = parameters.indexOf(p);
		if (index > 0) {
			parameters.remove(p);
			parameters.insertElementAt(p, index - 1);
			setChanged();
			notifyObservers(new DataModification("parameters", null, parameters));
		}
	}

	public void parameterDown(EditionSchemeParameter p) {
		System.out.println("parameterDown()");
		int index = parameters.indexOf(p);
		if (index > -1) {
			parameters.remove(p);
			parameters.insertElementAt(p, index + 1);
			setChanged();
			notifyObservers(new DataModification("parameters", null, parameters));
		}
	}

	public void parameterLast(EditionSchemeParameter p) {
		System.out.println("parameterLast()");
		parameters.remove(p);
		parameters.add(p);
		setChanged();
		notifyObservers(new DataModification("parameters", null, parameters));
	}

	public EditionSchemeParameter getParameter(String name) {
		if (name == null) {
			return null;
		}
		for (EditionSchemeParameter p : parameters) {
			if (name.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	@Override
	public VirtualModel<?> getVirtualModel() {
		if (getEditionPattern() != null && getEditionPattern().getVirtualModel() != null) {
			return getEditionPattern().getVirtualModel();
		}
		if (getEditionPattern() instanceof VirtualModel) {
			return (VirtualModel<?>) getEditionPattern();
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
		EditionSchemeParameter newParameter = new URIParameter(null);
		newParameter.setName("uri");
		// newParameter.setLabel("uri");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTextFieldParameter() {
		EditionSchemeParameter newParameter = new TextFieldParameter(null);
		newParameter.setName("textField");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTextAreaParameter() {
		EditionSchemeParameter newParameter = new TextAreaParameter(null);
		newParameter.setName("textArea");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createIntegerParameter() {
		EditionSchemeParameter newParameter = new IntegerParameter(null);
		newParameter.setName("integer");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createCheckBoxParameter() {
		EditionSchemeParameter newParameter = new CheckboxParameter(null);
		newParameter.setName("checkbox");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createDropDownParameter() {
		EditionSchemeParameter newParameter = new DropDownParameter(null);
		newParameter.setName("dropdown");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createIndividualParameter() {
		EditionSchemeParameter newParameter = new IndividualParameter(null);
		newParameter.setName("individual");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createClassParameter() {
		EditionSchemeParameter newParameter = new ClassParameter(null);
		newParameter.setName("class");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createPropertyParameter() {
		EditionSchemeParameter newParameter = new PropertyParameter(null);
		newParameter.setName("property");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createObjectPropertyParameter() {
		EditionSchemeParameter newParameter = new ObjectPropertyParameter(null);
		newParameter.setName("property");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createDataPropertyParameter() {
		EditionSchemeParameter newParameter = new DataPropertyParameter(null);
		newParameter.setName("property");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createFlexoObjectParameter() {
		EditionSchemeParameter newParameter = new FlexoObjectParameter(null);
		newParameter.setName("flexoObject");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTechnologyObjectParameter() {
		EditionSchemeParameter newParameter = new TechnologyObjectParameter(null);
		newParameter.setName("technologyObject");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createListParameter() {
		EditionSchemeParameter newParameter = new ListParameter(null);
		newParameter.setName("list");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createEditionPatternInstanceParameter() {
		EditionSchemeParameter newParameter = new EditionPatternInstanceParameter(null);
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

	public void finalizeEditionSchemeDeserialization() {
		updateBindingModels();
	}

	public boolean getSkipConfirmationPanel() {
		return skipConfirmationPanel;
	}

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
				_bindingModel.addToBindingVariables(new BindingVariable(((AssignableAction) a).getVariableName(), ((AssignableAction) a)
						.getAssignableType(), true) {
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
		// Après faudra voir au runtime;
		if (getEditionPattern() != null) {
			bindingModel.addToBindingVariables(new BindingVariable(EditionScheme.THIS, EditionPatternInstanceType
					.getEditionPatternInstanceType(getEditionPattern())));
			if (getEditionPattern().getVirtualModel() instanceof DiagramSpecification) {
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
			}
		}
		// if (this instanceof DiagramEditionScheme) {
		if (getEditionPattern() != null && getEditionPattern().getVirtualModel() instanceof DiagramSpecification) {
			bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.TOP_LEVEL, DiagramRootPane.class));
		}
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
		EditionScheme newEditionScheme = (EditionScheme) cloneUsingXMLMapping();
		newEditionScheme.setName(newName);
		getEditionPattern().addToEditionSchemes(newEditionScheme);
		return newEditionScheme;
	}

	public boolean getDefinePopupDefaultSize() {
		return definePopupDefaultSize;
	}

	public void setDefinePopupDefaultSize(boolean definePopupDefaultSize) {
		this.definePopupDefaultSize = definePopupDefaultSize;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

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
