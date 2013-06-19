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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;

/**
 * Abstract class representing a primitive to be executed as an atomic action of an EditionScheme
 * 
 * An edition action adresses a {@link ModelSlot}
 * 
 * @author sylvain
 * 
 */
public abstract class EditionAction<MS extends ModelSlot<?>, T> extends EditionSchemeObject {

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
		AddEditionPatternInstance,
		MatchEditionPatternInstance,
		CloneShape,
		CloneConnector,
		CloneIndividual,
		DeclarePatternRole,
		Assignation,
		Execution,
		DeleteAction,
		GraphicalAction,
		GoToObject,
		Iteration,
		FetchRequestIteration,
		Conditional,
		FetchRequest,
		SelectIndividual,
		SelectEditionPatternInstance
	}

	private MS modelSlot;

	private DataBinding<Boolean> conditional;

	private ActionContainer actionContainer;

	private BindingModel inferedBindingModel = null;

	public EditionAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
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
	public VirtualModel<?> getVirtualModel() {
		if (getScheme() != null) {
			return getScheme().getVirtualModel();
		}
		return null;
	}

	public MS getModelSlot() {
		return modelSlot;
	}

	public void setModelSlot(MS modelSlot) {
		this.modelSlot = modelSlot;
	}

	public <MS2 extends ModelSlot<?>> List<MS2> getAvailableModelSlots(Class<MS2> msType) {
		if (getEditionPattern() != null && getEditionPattern().getVirtualModel() != null) {
			return getEditionPattern().getVirtualModel().getModelSlots(msType);
		} else if (getEditionPattern() instanceof VirtualModel) {
			return ((VirtualModel) getEditionPattern()).getModelSlots(msType);
		}
		return null;
	}

	public List<VirtualModelModelSlot> getAvailableVirtualModelModelSlots() {
		return getAvailableModelSlots(VirtualModelModelSlot.class);
	}

	public ModelSlotInstance<MS, ?> getModelSlotInstance(EditionSchemeAction action) {
		if (action.getVirtualModelInstance() != null) {
			return action.getVirtualModelInstance().getModelSlotInstance(getModelSlot());
		} else {
			logger.severe("Could not access virtual model instance for action " + action);
			return null;
		}
	}

	public boolean evaluateCondition(EditionSchemeAction action) {
		if (getConditional().isValid()) {
			try {
				return getConditional().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Perform batch of actions, in the context provided by supplied {@link EditionSchemeAction}<br>
	 * An action is performed if and only if the condition evaluation returns true. All finalizers of actions are invoked in a second step
	 * when all actions are performed.
	 * 
	 * @param action
	 * @return
	 */
	public static void performBatchOfActions(Collection<EditionAction<?, ?>> actions, EditionSchemeAction contextAction) {

		Hashtable<EditionAction, Object> performedActions = new Hashtable<EditionAction, Object>();

		for (EditionAction editionAction : actions) {
			if (editionAction.evaluateCondition(contextAction)) {
				Object assignedObject = editionAction.performAction(contextAction);
				if (assignedObject != null) {
					performedActions.put(editionAction, assignedObject);
				}

				if (assignedObject != null && editionAction instanceof AssignableAction) {
					AssignableAction assignableAction = (AssignableAction) editionAction;
					if (assignableAction.getIsVariableDeclaration()) {
						System.out.println("Setting variable " + assignableAction.getVariableName() + " with value " + assignedObject
								+ " of " + (assignedObject != null ? assignedObject.getClass() : "null"));
						contextAction.declareVariable(assignableAction.getVariableName(), assignedObject);
					}
					if (assignableAction.getAssignation().isSet() && assignableAction.getAssignation().isValid()) {
						try {
							assignableAction.getAssignation().setBindingValue(assignedObject, contextAction);
						} catch (Exception e) {
							logger.warning("Unexpected assignation issue, " + assignableAction.getAssignation() + " object="
									+ assignedObject + " exception: " + e);
							e.printStackTrace();
						}
					}
					if (assignableAction.getPatternRole() != null && assignedObject instanceof FlexoModelObject) {
						contextAction.getEditionPatternInstance().setObjectForPatternRole((FlexoModelObject) assignedObject,
								assignableAction.getPatternRole());
					}
				}
			}
		}

		for (EditionAction editionAction : performedActions.keySet()) {
			editionAction.finalizePerformAction(contextAction, performedActions.get(editionAction));
		}
	}

	/**
	 * Execute edition action in the context provided by supplied {@link EditionSchemeAction}<br>
	 * Note than returned object will be used to be further reinjected in finalizer
	 * 
	 * @param action
	 * @return
	 */
	public abstract T performAction(EditionSchemeAction action);

	/**
	 * Provides hooks after executing edition action in the context provided by supplied {@link EditionSchemeAction}
	 * 
	 * @param action
	 * @param initialContext
	 *            the object that was returned during {@link #performAction(EditionSchemeAction)} call
	 * @return
	 */
	public abstract void finalizePerformAction(EditionSchemeAction action, T initialContext);

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
		if (getActionContainer() == null || isDeserializing() /* Prevent StackOverflow !!! */) {
			returned = new BindingModel();
		} else {
			returned = new BindingModel(getActionContainer().getInferedBindingModel());
		}
		return returned;
	}

	public DataBinding<Boolean> getConditional() {
		if (conditional == null) {
			conditional = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		return conditional;
	}

	public void setConditional(DataBinding<Boolean> conditional) {
		if (conditional != null) {
			conditional.setOwner(this);
			conditional.setDeclaredType(Boolean.class);
			conditional.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		this.conditional = conditional;
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

	private void insertActionAtCurrentIndex(EditionAction<?, ?> editionAction) {
		if (getActionContainer() != null) {
			getActionContainer().insertActionAtIndex(editionAction, getActionContainer().getIndex(this) + 1);
		}
	}

	/*public AddShape createAddShapeAction() {
		AddShape newAction = new AddShape(null);
		if (getEditionPattern().getDefaultShapePatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
		}
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddClass createAddClassAction() {
		AddClass newAction = new AddClass(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddIndividual createAddIndividualAction() {
		AddIndividual newAction = new AddIndividual(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddObjectPropertyStatement createAddObjectPropertyStatementAction() {
		AddObjectPropertyStatement newAction = new AddObjectPropertyStatement(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddDataPropertyStatement createAddDataPropertyStatementAction() {
		AddDataPropertyStatement newAction = new AddDataPropertyStatement(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddIsAStatement createAddIsAPropertyAction() {
		AddIsAStatement newAction = new AddIsAStatement(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddRestrictionStatement createAddRestrictionAction() {
		AddRestrictionStatement newAction = new AddRestrictionStatement(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddConnector createAddConnectorAction() {
		AddConnector newAction = new AddConnector(null);
		if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
		}
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public DeclarePatternRole createDeclarePatternRoleAction() {
		DeclarePatternRole newAction = new DeclarePatternRole(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public GraphicalAction createGraphicalAction() {
		GraphicalAction newAction = new GraphicalAction(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public CreateDiagram createAddDiagramAction() {
		CreateDiagram newAction = new CreateDiagram(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public AddEditionPattern createAddEditionPatternAction() {
		AddEditionPattern newAction = new AddEditionPattern(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public ConditionalAction createConditionalAction() {
		ConditionalAction newAction = new ConditionalAction(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public IterationAction createIterationAction() {
		IterationAction newAction = new IterationAction(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public CloneShape createCloneShapeAction() {
		CloneShape newAction = new CloneShape(null);
		if (getEditionPattern().getDefaultShapePatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultShapePatternRole().getPatternRoleName()));
		}
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public CloneConnector createCloneConnectorAction() {
		CloneConnector newAction = new CloneConnector(null);
		if (getEditionPattern().getDefaultConnectorPatternRole() != null) {
			newAction.setAssignation(new ViewPointDataBinding(getEditionPattern().getDefaultConnectorPatternRole().getPatternRoleName()));
		}
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}

	public CloneIndividual createCloneIndividualAction() {
		CloneIndividual newAction = new CloneIndividual(null);
		insertActionAtCurrentIndex(newAction);
		return newAction;
	}*/

	/**
	 * Creates a new {@link EditionAction} of supplied class, and add it to parent container at the index where this action is itself
	 * registered<br>
	 * Delegates creation to model slot
	 * 
	 * @return newly created {@link EditionAction}
	 */
	public <A extends EditionAction<?, ?>> A createActionAtCurrentIndex(Class<A> actionClass, ModelSlot<?> modelSlot) {
		A newAction = modelSlot.createAction(actionClass);
		insertActionAtCurrentIndex(newAction);
		return null;
	}

	public static class ConditionalBindingMustBeValid extends BindingMustBeValid<EditionAction> {
		public ConditionalBindingMustBeValid() {
			super("'conditional'_binding_is_not_valid", EditionAction.class);
		}

		@Override
		public DataBinding<Boolean> getBinding(EditionAction object) {
			return object.getConditional();
		}

	}

}
