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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterListPathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPathElement;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public abstract class EditionScheme extends EditionPatternObject implements ActionContainer {

	protected BindingModel _bindingModel;

	//
	protected static final Logger logger = FlexoLogger.getLogger(EditionScheme.class.getPackage().getName());

	public static final String TOP_LEVEL = "topLevel";
	public static final String TARGET = "target";
	public static final String FROM_TARGET = "fromTarget";
	public static final String TO_TARGET = "toTarget";

	public static enum EditionSchemeType {
		CreationScheme, DropScheme, LinkScheme, ActionScheme, NavigationScheme, DeletionScheme
	}

	private String name;
	private String label;
	private String description;
	private Vector<EditionAction> actions;
	private Vector<EditionSchemeParameter> parameters;
	private boolean skipConfirmationPanel = false;

	private EditionPattern _editionPattern;

	private boolean definePopupDefaultSize = false;
	private int width = 800;
	private int height = 600;

	public EditionScheme() {
		actions = new Vector<EditionAction>();
		parameters = new Vector<EditionSchemeParameter>();
	}

	@Override
	public String getURI() {
		return getEditionPattern().getURI() + "#" + getName();
	}

	public abstract EditionSchemeType getEditionSchemeType();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
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
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/*public EditionAction getAction(PatternRole role) {
		for (EditionAction a : getActions()) {
			if (a.getPatternRole() == role) {
				return a;
			}
		}
		return null;
	}*/

	@Override
	public Vector<EditionAction> getActions() {
		return actions;
	}

	@Override
	public void setActions(Vector<EditionAction> actions) {
		this.actions = actions;
		setChanged();
		notifyObservers();
	}

	@Override
	public void addToActions(EditionAction action) {
		action.setScheme(this);
		actions.add(action);
		setChanged();
		notifyObservers();
		notifyChange("actions", null, actions);
	}

	@Override
	public void removeFromActions(EditionAction action) {
		action.setScheme(null);
		actions.remove(action);
		setChanged();
		notifyObservers();
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
		if (index > 0) {
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
		parameters.remove(p);
		parameters.insertElementAt(p, index - 1);
		setChanged();
		notifyObservers();
	}

	public void parameterDown(EditionSchemeParameter p) {
		int index = parameters.indexOf(p);
		parameters.remove(p);
		parameters.insertElementAt(p, index + 1);
		setChanged();
		notifyObservers();
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
	public ViewPoint getViewPoint() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getViewPoint();
		}
		return null;
	}

	@Override
	public AddShape createAddShapeAction() {
		AddShape newAction = new AddShape();
		if (getEditionPattern().getDefaultShapePatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
		}
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddClass createAddClassAction() {
		AddClass newAction = new AddClass();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddIndividual createAddIndividualAction() {
		AddIndividual newAction = new AddIndividual();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddObjectPropertyStatement createAddObjectPropertyStatementAction() {
		AddObjectPropertyStatement newAction = new AddObjectPropertyStatement();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddDataPropertyStatement createAddDataPropertyStatementAction() {
		AddDataPropertyStatement newAction = new AddDataPropertyStatement();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddIsAStatement createAddIsAPropertyAction() {
		AddIsAStatement newAction = new AddIsAStatement();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddRestrictionStatement createAddRestrictionAction() {
		AddRestrictionStatement newAction = new AddRestrictionStatement();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddConnector createAddConnectorAction() {
		AddConnector newAction = new AddConnector();
		if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
		}
		addToActions(newAction);
		return newAction;
	}

	@Override
	public DeclarePatternRole createDeclarePatternRoleAction() {
		DeclarePatternRole newAction = new DeclarePatternRole();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public GraphicalAction createGraphicalAction() {
		GraphicalAction newAction = new GraphicalAction();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddDiagram createAddDiagramAction() {
		AddDiagram newAction = new AddDiagram();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public AddEditionPattern createAddEditionPatternAction() {
		AddEditionPattern newAction = new AddEditionPattern();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public ConditionalAction createConditionalAction() {
		ConditionalAction newAction = new ConditionalAction();
		addToActions(newAction);
		return newAction;
	}

	@Override
	public IterationAction createIterationAction() {
		IterationAction newAction = new IterationAction();
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
		EditionSchemeParameter newParameter = new URIParameter();
		newParameter.setName("uri");
		// newParameter.setLabel("uri");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTextFieldParameter() {
		EditionSchemeParameter newParameter = new TextFieldParameter();
		newParameter.setName("textField");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTextAreaParameter() {
		EditionSchemeParameter newParameter = new TextAreaParameter();
		newParameter.setName("textArea");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createIntegerParameter() {
		EditionSchemeParameter newParameter = new IntegerParameter();
		newParameter.setName("integer");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createCheckBoxParameter() {
		EditionSchemeParameter newParameter = new CheckboxParameter();
		newParameter.setName("checkbox");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createDropDownParameter() {
		EditionSchemeParameter newParameter = new DropDownParameter();
		newParameter.setName("dropdown");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createIndividualParameter() {
		EditionSchemeParameter newParameter = new IndividualParameter();
		newParameter.setName("individual");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createClassParameter() {
		EditionSchemeParameter newParameter = new ClassParameter();
		newParameter.setName("class");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createObjectPropertyParameter() {
		EditionSchemeParameter newParameter = new ObjectPropertyParameter();
		newParameter.setName("property");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createDataPropertyParameter() {
		EditionSchemeParameter newParameter = new DataPropertyParameter();
		newParameter.setName("property");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createFlexoObjectParameter() {
		EditionSchemeParameter newParameter = new FlexoObjectParameter();
		newParameter.setName("flexoObject");
		// newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createListParameter() {
		EditionSchemeParameter newParameter = new ListParameter();
		newParameter.setName("list");
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
		/*for (EditionAction a : getActions()) {
			if (a.getPatternRole() != null) {
				a.updatePatternRoleType();
			}
		}*/
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

	public void updateBindingModels() {
		logger.fine("updateBindingModels()");
		_bindingModel = null;
		createBindingModel();
	}

	private final void createBindingModel() {
		_bindingModel = new BindingModel();
		_bindingModel.addToBindingVariables(new EditionSchemeParameterListPathElement(this, null));
		appendContextualBindingVariables(_bindingModel);
		if (getEditionPattern() != null) {
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				PatternRolePathElement newPathElement = PatternRolePathElement.makePatternRolePathElement(pr, this);
				_bindingModel.addToBindingVariables(newPathElement);
			}
		}
		notifyBindingModelChanged();
	}

	protected void appendContextualBindingVariables(BindingModel bindingModel) {
		bindingModel.addToBindingVariables(new GraphicalElementPathElement.ViewPathElement(TOP_LEVEL, null));
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
