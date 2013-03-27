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

import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParametersBindingVariable;
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
public abstract class EditionScheme extends EditionSchemeObject implements ActionContainer {

	protected BindingModel _bindingModel;

	//
	protected static final Logger logger = FlexoLogger.getLogger(EditionScheme.class.getPackage().getName());

	public static final String THIS = "this";
	public static final String VIRTUAL_MODEL_INSTANCE = "virtualModelInstance";

	private String name;
	private String label;
	private String description;
	private Vector<EditionAction<?, ?, ?>> actions;
	private Vector<EditionSchemeParameter> parameters;
	private boolean skipConfirmationPanel = false;

	private EditionPattern _editionPattern;

	private EditionSchemeParameters editionSchemeParameters;

	private boolean definePopupDefaultSize = false;
	private int width = 800;
	private int height = 600;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	public EditionScheme(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		actions = new Vector<EditionAction<?, ?, ?>>();
		parameters = new Vector<EditionSchemeParameter>();
		editionSchemeParameters = new EditionSchemeParameters(this);
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
	public Vector<EditionAction<?, ?, ?>> getActions() {
		return actions;
	}

	@Override
	public void setActions(Vector<EditionAction<?, ?, ?>> actions) {
		this.actions = actions;
		setChanged();
		notifyObservers();
	}

	@Override
	public void addToActions(EditionAction<?, ?, ?> action) {
		// action.setScheme(this);
		action.setActionContainer(this);
		actions.add(action);
		setChanged();
		notifyObservers();
		notifyChange("actions", null, actions);
	}

	@Override
	public void removeFromActions(EditionAction<?, ?, ?> action) {
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
		notifyObservers();
	}

	@Override
	public void actionUp(EditionAction a) {
		int index = actions.indexOf(a);
		if (index > 0) {
			actions.remove(a);
			actions.insertElementAt(a, index - 1);
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void actionDown(EditionAction a) {
		int index = actions.indexOf(a);
		if (index > -1) {
			actions.remove(a);
			actions.insertElementAt(a, index + 1);
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void actionLast(EditionAction a) {
		actions.remove(a);
		actions.add(a);
		setChanged();
		notifyObservers();
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
		parameters.remove(p);
		parameters.insertElementAt(p, 0);
		setChanged();
		notifyObservers();
	}

	public void parameterUp(EditionSchemeParameter p) {
		int index = parameters.indexOf(p);
		if (index > 0) {
			parameters.remove(p);
			parameters.insertElementAt(p, index - 1);
			setChanged();
			notifyObservers();
		}
	}

	public void parameterDown(EditionSchemeParameter p) {
		int index = parameters.indexOf(p);
		if (index > -1) {
			parameters.remove(p);
			parameters.insertElementAt(p, index + 1);
			setChanged();
			notifyObservers();
		}
	}

	public void parameterLast(EditionSchemeParameter p) {
		parameters.remove(p);
		parameters.add(p);
		setChanged();
		notifyObservers();
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
	public <A extends EditionAction<M, MM, ?>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> A createAction(
			Class<A> actionClass, ModelSlot<M, MM> modelSlot) {
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

	public EditionSchemeParameter createListParameter() {
		EditionSchemeParameter newParameter = new ListParameter(null);
		newParameter.setName("list");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createEditionPatternParameter() {
		EditionSchemeParameter newParameter = new EditionPatternParameter(null);
		newParameter.setName("editionPattern");
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
		logger.fine("updateBindingModels()");
		_bindingModel = null;
		createBindingModel();
		rebuildActionsBindingModel();
	}

	protected void rebuildActionsBindingModel() {
		for (EditionAction action : getActions()) {
			action.rebuildInferedBindingModel();
		}
	}

	private final void createBindingModel() {
		_bindingModel = new BindingModel();
		_bindingModel.addToBindingVariables(new EditionSchemeParametersBindingVariable(this));
		// _bindingModel.addToBindingVariables(new EditionSchemeParameterListPathElement(this, null));
		appendContextualBindingVariables(_bindingModel);
		if (getEditionPattern() != null) {
			for (final PatternRole role : getEditionPattern().getPatternRoles()) {
				_bindingModel.addToBindingVariables(new PatternRoleBindingVariable(role));
			}
		}
		notifyBindingModelChanged();
	}

	protected void appendContextualBindingVariables(BindingModel bindingModel) {
		bindingModel.addToBindingVariables(new BindingVariable(EditionScheme.THIS, getEditionPattern()));
		if (getEditionPattern() != null) {
			bindingModel.addToBindingVariables(new BindingVariable(EditionScheme.VIRTUAL_MODEL_INSTANCE, getEditionPattern()
					.getVirtualModel()));
		}
		if (this instanceof DiagramEditionScheme) {
			bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.TOP_LEVEL, DiagramRootPane.class));
		}
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

}
