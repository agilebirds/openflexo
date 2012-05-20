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

import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public abstract class ControlStructureAction extends EditionAction implements ActionContainer {

	private static final Logger logger = Logger.getLogger(ControlStructureAction.class.getPackage().getName());

	private Vector<EditionAction> actions;

	public ControlStructureAction() {
		actions = new Vector<EditionAction>();
	}

	/*@Override
	public List<PatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles();
	}*/

	@Override
	protected void rebuildInferedBindingModel() {
		super.rebuildInferedBindingModel();
		for (EditionAction action : getActions()) {
			action.rebuildInferedBindingModel();
		}
	}

	@Override
	public String getInspectorName() {
		return null;
	}

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
		// action.setScheme(getEditionScheme());
		action.setActionContainer(this);
		actions.add(action);
		setChanged();
		notifyObservers();
		notifyChange("actions", null, actions);
	}

	@Override
	public void removeFromActions(EditionAction action) {
		// action.setScheme(null);
		action.setActionContainer(null);
		actions.remove(action);
		setChanged();
		notifyObservers();
	}

	@Override
	public int getIndex(EditionAction action) {
		return actions.indexOf(action);
	}

	@Override
	public void insertActionAtIndex(EditionAction action, int index) {
		// action.setScheme(getEditionScheme());
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

}
