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
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterPathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPathElement;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.logging.FlexoLogger;

public abstract class EditionScheme extends ViewPointObject {

	//
	protected static final Logger logger = FlexoLogger.getLogger(EditionScheme.class.getPackage().getName());

	public static final String TOP_LEVEL = "topLevel";
	public static final String TARGET = "target";
	public static final String FROM_TARGET = "fromTarget";
	public static final String TO_TARGET = "toTarget";

	public static enum EditionSchemeType {
		DropScheme, LinkScheme, ActionScheme
	}

	private String name;
	private String label;
	private String description;
	private Vector<EditionAction> actions;
	private Vector<EditionSchemeParameter> parameters;
	private boolean skipConfirmationPanel = false;

	private EditionPattern _editionPattern;

	public EditionScheme() {
		actions = new Vector<EditionAction>();
		parameters = new Vector<EditionSchemeParameter>();
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
		if (label == null)
			return getName();
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

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

	public EditionAction getAction(PatternRole role) {
		for (EditionAction a : getActions()) {
			if (a.getPatternRole() == role) {
				return a;
			}
		}
		return null;
	}

	public Vector<EditionAction> getActions() {
		return actions;
	}

	public void setActions(Vector<EditionAction> actions) {
		this.actions = actions;
	}

	public void addToActions(EditionAction action) {
		action.setScheme(this);
		actions.add(action);
	}

	public void removeFromActions(EditionAction action) {
		action.setScheme(null);
		actions.remove(action);
	}

	public void actionFirst(EditionAction a) {
		actions.remove(a);
		actions.insertElementAt(a, 0);
		setChanged();
		notifyObservers();
	}

	public void actionUp(EditionAction a) {
		int index = actions.indexOf(a);
		actions.remove(a);
		actions.insertElementAt(a, index - 1);
		setChanged();
		notifyObservers();
	}

	public void actionDown(EditionAction a) {
		int index = actions.indexOf(a);
		actions.remove(a);
		actions.insertElementAt(a, index + 1);
		setChanged();
		notifyObservers();
	}

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
	public ViewPoint getCalc() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getCalc();
		}
		return null;
	}

	public AddShape createAddShapeAction() {
		AddShape newAction = new AddShape();
		newAction.setPatternRole(getEditionPattern().getDefaultShapePatternRole());
		addToActions(newAction);
		return newAction;
	}

	public AddClass createAddClassAction() {
		AddClass newAction = new AddClass();
		addToActions(newAction);
		return newAction;
	}

	public AddIndividual createAddIndividualAction() {
		AddIndividual newAction = new AddIndividual();
		addToActions(newAction);
		return newAction;
	}

	public AddObjectPropertyStatement createAddObjectPropertyStatementAction() {
		AddObjectPropertyStatement newAction = new AddObjectPropertyStatement();
		addToActions(newAction);
		return newAction;
	}

	public AddDataPropertyStatement createAddDataPropertyStatementAction() {
		AddDataPropertyStatement newAction = new AddDataPropertyStatement();
		addToActions(newAction);
		return newAction;
	}

	public AddIsAStatement createAddIsAPropertyAction() {
		AddIsAStatement newAction = new AddIsAStatement();
		addToActions(newAction);
		return newAction;
	}

	public AddRestrictionStatement createAddRestrictionAction() {
		AddRestrictionStatement newAction = new AddRestrictionStatement();
		addToActions(newAction);
		return newAction;
	}

	public AddConnector createAddConnectorAction() {
		AddConnector newAction = new AddConnector();
		newAction.setPatternRole(getEditionPattern().getDefaultConnectorPatternRole());
		addToActions(newAction);
		return newAction;
	}

	public DeclarePatternRole createDeclarePatternRoleAction() {
		DeclarePatternRole newAction = new DeclarePatternRole();
		addToActions(newAction);
		return newAction;
	}

	public AddShema createAddShemaAction() {
		AddShema newAction = new AddShema();
		addToActions(newAction);
		return newAction;
	}

	public GoToAction createGoToAction() {
		GoToAction newAction = new GoToAction();
		addToActions(newAction);
		return newAction;
	}

	public EditionAction deleteAction(EditionAction anAction) {
		removeFromActions(anAction);
		anAction.delete();
		return anAction;
	}

	public EditionSchemeParameter createURIParameter() {
		EditionSchemeParameter newParameter = new URIParameter();
		newParameter.setName("uri");
		newParameter.setLabel("uri");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTextFieldParameter() {
		EditionSchemeParameter newParameter = new TextFieldParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createTextAreaParameter() {
		EditionSchemeParameter newParameter = new TextAreaParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createIntegerParameter() {
		EditionSchemeParameter newParameter = new IntegerParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createCheckBoxParameter() {
		EditionSchemeParameter newParameter = new CheckboxParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createDropDownParameter() {
		EditionSchemeParameter newParameter = new DropDownParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createIndividualParameter() {
		EditionSchemeParameter newParameter = new IndividualParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createClassParameter() {
		EditionSchemeParameter newParameter = new ClassParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter createFlexoObjectParameter() {
		EditionSchemeParameter newParameter = new FlexoObjectParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionSchemeParameter deleteParameter(EditionSchemeParameter aParameter) {
		removeFromParameters(aParameter);
		aParameter.delete();
		return aParameter;
	}

	public void finalizeEditionSchemeDeserialization() {
		for (EditionAction a : getActions()) {
			if (a.getPatternRole() != null) {
				a.updatePatternRoleType();
			}
		}
		updateBindingModels();
	}

	public boolean getSkipConfirmationPanel() {
		return skipConfirmationPanel;
	}

	public void setSkipConfirmationPanel(boolean skipConfirmationPanel) {
		this.skipConfirmationPanel = skipConfirmationPanel;
	}

	private BindingModel _bindingModel;
	private BindingModel _parametersBindingModel;

	@Override
	public BindingModel getBindingModel() {
		if (_bindingModel == null) {
			createBindingModel();
		}
		return _bindingModel;
	}

	public BindingModel getParametersBindingModel() {
		if (_parametersBindingModel == null) {
			createParametersBindingModel();
		}
		return _parametersBindingModel;
	}

	public void updateBindingModels() {
		logger.fine("updateBindingModels()");
		_bindingModel = null;
		_parametersBindingModel = null;
		createBindingModel();
	}

	private void createBindingModel() {
		_bindingModel = new BindingModel();
		_bindingModel.addToBindingVariables(new EditionSchemeParameterListPathElement(this, null));
		appendContextualBindingVariables(_bindingModel);
		_bindingModel.addToBindingVariables(new GraphicalElementPathElement.ViewPathElement(TOP_LEVEL, null));
		if (getEditionPattern() != null) {
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				PatternRolePathElement newPathElement = PatternRolePathElement.makePatternRolePathElement(pr, this);
				_bindingModel.addToBindingVariables(newPathElement);
			}
		}
		notifyBindingModelChanged();
	}

	protected abstract void appendContextualBindingVariables(BindingModel bindingModel);

	private void createParametersBindingModel() {
		_parametersBindingModel = new BindingModel();
		for (EditionSchemeParameter p : parameters) {
			_parametersBindingModel.addToBindingVariables(new EditionSchemeParameterPathElement<EditionScheme>(null, p));
		}

		/*for (PatternRole role : getPatternRoles()) {
			_bindingModel.addToBindingVariables(PatternRolePathElement.makePatternRolePathElement(role,this));
		}	*/
	}

}
