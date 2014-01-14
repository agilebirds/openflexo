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
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(ControlStructureAction.ControlStructureActionImpl.class)
public abstract interface ControlStructureAction extends EditionAction<ModelSlot<?>, Object>, ActionContainer {

	public static abstract class ControlStructureActionImpl extends EditionActionImpl<ModelSlot<?>, Object> implements
			ControlStructureAction {

		private static final Logger logger = Logger.getLogger(ControlStructureAction.class.getPackage().getName());

		// private Vector<EditionAction<?, ?>> actions;

		public ControlStructureActionImpl() {
			super();
			// actions = new Vector<EditionAction<?, ?>>();
		}

		/*@Override
		public List<PatternRole> getAvailablePatternRoles() {
			return getEditionPattern().getPatternRoles();
		}*/

		@Override
		protected void rebuildInferedBindingModel() {
			super.rebuildInferedBindingModel();
			for (EditionAction<?, ?> action : getActions()) {
				action.rebuildInferedBindingModel();
			}

		}

		@Override
		protected BindingModel buildInferedBindingModel() {
			BindingModel returned = super.buildInferedBindingModel();
			for (final EditionAction<?, ?> a : getActions()) {
				if (a instanceof AssignableAction && ((AssignableAction) a).getIsVariableDeclaration()) {
					returned.addToBindingVariables(new BindingVariable(((AssignableAction) a).getVariableName(), ((AssignableAction) a)
							.getAssignableType(), true) {
						@Override
						public Type getType() {
							return ((AssignableAction) a).getAssignableType();
						}
					});
				}
			}
			return returned;
		}

		@Override
		public void variableAdded(AssignableAction action) {
			rebuildInferedBindingModel();
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
			// action.setScheme(getEditionScheme());
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

		@Override
		public int getIndex(EditionAction<?, ?> action) {
			return getActions().indexOf(action);
		}

		@Override
		public void insertActionAtIndex(EditionAction<?, ?> action, int index) {
			// action.setScheme(getEditionScheme());
			action.setActionContainer(this);
			getActions().add(index, action);
			setChanged();
			notifyObservers();
			notifyChange("actions", null, getActions());
		}

		@Override
		public void actionFirst(EditionAction<?, ?> a) {
			getActions().remove(a);
			getActions().add(0, a);
			setChanged();
			notifyChange("actions", null, getActions());
		}

		@Override
		public void actionUp(EditionAction<?, ?> a) {
			int index = getActions().indexOf(a);
			if (index > 0) {
				getActions().remove(a);
				getActions().add(index - 1, a);
				setChanged();
				notifyChange("actions", null, getActions());
			}
		}

		@Override
		public void actionDown(EditionAction<?, ?> a) {
			int index = getActions().indexOf(a);
			if (index > 0) {
				getActions().remove(a);
				getActions().add(index + 1, a);
				setChanged();
				notifyChange("actions", null, getActions());
			}
		}

		@Override
		public void actionLast(EditionAction<?, ?> a) {
			getActions().remove(a);
			getActions().add(a);
			setChanged();
			notifyChange("actions", null, getActions());
		}

		/*	@Override
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

			@Override
			public CloneShape createCloneShapeAction() {
				CloneShape newAction = new CloneShape(null);
				if (getEditionPattern().getDefaultShapePatternRole() != null) {
					newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
				}
				addToActions(newAction);
				return newAction;
			}

			@Override
			public CloneConnector createCloneConnectorAction() {
				CloneConnector newAction = new CloneConnector(null);
				if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
					newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
				}
				addToActions(newAction);
				return newAction;
			}

			@Override
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
		 * Creates a new {@link EditionAction} of supplied class, and add it to the list of contained action managed by this control
		 * structure action<br>
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
		public EditionAction<?, ?> deleteAction(EditionAction<?, ?> anAction) {
			removeFromActions(anAction);
			anAction.delete();
			return anAction;
		}

		@Override
		public final void finalizePerformAction(EditionSchemeAction action, Object initialContext) {
			// Not applicable for ControlStructureAction
		};

		@Override
		public Collection<EditionAction<?, ?>> getEmbeddedValidableObjects() {
			return actions;
		}

	}
}
