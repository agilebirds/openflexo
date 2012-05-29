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

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

/**
 * Abstract class representing a primitive to be executed as an atomic action of an EditionScheme
 * 
 * @author sylvain
 * 
 */
public abstract class EditionAction extends EditionSchemeObject {

	private static final Logger logger = Logger.getLogger(EditionAction.class.getPackage().getName());

	public static enum EditionActionType {
		AddClass,
		AddIndividual,
		AddObjectPropertyStatement,
		AddDataPropertyStatement,
		AddIsAStatement,
		AddRestrictionStatement,
		AddConnector,
		AddShape,
		AddDiagram,
		AddEditionPattern,
		DeclarePatternRole,
		DeleteAction,
		GraphicalAction,
		GoToObject,
		Iteration,
		Conditional
	}

	public static enum EditionActionBindingAttribute implements InspectorBindingAttribute {
		conditional,
		assignation,
		individualName,
		className,
		container,
		fromShape,
		toShape,
		object,
		subject,
		father,
		value,
		restrictionType,
		cardinality,
		target,
		diagramName,
		view,
		condition,
		iteration
	}

	// private EditionScheme _scheme;
	private String description;
	// private String patternRole;

	private ViewPointDataBinding conditional;

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);

	private ActionContainer actionContainer;

	private BindingModel inferedBindingModel = null;

	public EditionAction() {
	}

	public abstract EditionActionType getEditionActionType();

	@Override
	public EditionScheme getEditionScheme() {
		return getScheme();
	}

	public EditionScheme getScheme() {
		if (actionContainer instanceof EditionScheme) {
			return (EditionScheme) actionContainer;
		} else if (actionContainer != null) {
			return actionContainer.getEditionScheme();
		}
		return null;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getScheme() != null) {
			return getScheme().getViewPoint();
		}
		return null;
	}

	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(action);
		}
		return true;
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (getScheme() == null) {
			return null;
		}
		return getScheme().getEditionPattern();
	}

	public int getIndex() {
		if (getScheme() != null && getScheme().getActions() != null) {
			return getScheme().getActions().indexOf(this);
		}
		return -1;
	}

	@Override
	public final BindingModel getBindingModel() {
		if (getActionContainer() != null) {
			return getActionContainer().getInferedBindingModel();
		}
		return null;
	}

	public final BindingModel getInferedBindingModel() {
		if (inferedBindingModel == null) {
			rebuildInferedBindingModel();
		}
		return inferedBindingModel;
	}

	protected void rebuildInferedBindingModel() {
		inferedBindingModel = buildInferedBindingModel();
	}

	protected BindingModel buildInferedBindingModel() {
		BindingModel returned;
		if (getActionContainer() == null) {
			returned = new BindingModel();
		} else {
			returned = new BindingModel(getActionContainer().getInferedBindingModel());
		}
		return returned;
	}

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, EditionActionBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		if (conditional != null) {
			conditional.setOwner(this);
			conditional.setBindingAttribute(EditionActionBindingAttribute.conditional);
			conditional.setBindingDefinition(getConditionalBindingDefinition());
		}
		this.conditional = conditional;
		notifyBindingChanged(this.conditional);
	}

	public String getStringRepresentation() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public ActionContainer getActionContainer() {
		return actionContainer;
	}

	public void setActionContainer(ActionContainer actionContainer) {
		this.actionContainer = actionContainer;
		rebuildInferedBindingModel();
	}

	private void insertActionAtCurrentIndex(EditionAction editionAction) {
		getActionContainer().insertActionAtIndex(editionAction, getActionContainer().getIndex(this) + 1);
	}

	public AddShape createAddShapeAction() {
		AddShape newAction = new AddShape();
		if (getEditionPattern().getDefaultShapePatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
		}
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddClass createAddClassAction() {
		AddClass newAction = new AddClass();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddIndividual createAddIndividualAction() {
		AddIndividual newAction = new AddIndividual();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddObjectPropertyStatement createAddObjectPropertyStatementAction() {
		AddObjectPropertyStatement newAction = new AddObjectPropertyStatement();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddDataPropertyStatement createAddDataPropertyStatementAction() {
		AddDataPropertyStatement newAction = new AddDataPropertyStatement();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddIsAStatement createAddIsAPropertyAction() {
		AddIsAStatement newAction = new AddIsAStatement();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddRestrictionStatement createAddRestrictionAction() {
		AddRestrictionStatement newAction = new AddRestrictionStatement();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddConnector createAddConnectorAction() {
		AddConnector newAction = new AddConnector();
		if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
		}
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public DeclarePatternRole createDeclarePatternRoleAction() {
		DeclarePatternRole newAction = new DeclarePatternRole();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public GraphicalAction createGraphicalAction() {
		GraphicalAction newAction = new GraphicalAction();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddDiagram createAddDiagramAction() {
		AddDiagram newAction = new AddDiagram();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddEditionPattern createAddEditionPatternAction() {
		AddEditionPattern newAction = new AddEditionPattern();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public ConditionalAction createConditionalAction() {
		ConditionalAction newAction = new ConditionalAction();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public IterationAction createIterationAction() {
		IterationAction newAction = new IterationAction();
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public static class ConditionalBindingMustBeValid extends BindingMustBeValid<EditionAction> {
		public ConditionalBindingMustBeValid() {
			super("'conditional'_binding_is_not_valid", EditionAction.class);
		}

		@Override
		public ViewPointDataBinding getBinding(EditionAction object) {
			return object.getConditional();
		}

		@Override
		public BindingDefinition getBindingDefinition(EditionAction object) {
			return object.getConditionalBindingDefinition();
		}

	}

}
