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
package org.openflexo.foundation.view.action;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.flexo.model.FlexoModelObject;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.CreationScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ListParameter;
import org.openflexo.foundation.viewpoint.binding.PatternRoleBindingVariable;

public class CreationSchemeAction extends EditionSchemeAction<CreationSchemeAction, CreationScheme, VirtualModelInstance> {

	private static final Logger logger = Logger.getLogger(CreationSchemeAction.class.getPackage().getName());

	public static FlexoActionType<CreationSchemeAction, VirtualModelInstance, VirtualModelInstanceObject> actionType = new FlexoActionType<CreationSchemeAction, VirtualModelInstance, VirtualModelInstanceObject>(
			"create_edition_pattern_instance", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreationSchemeAction makeNewAction(VirtualModelInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
				FlexoEditor editor) {
			return new CreationSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
			return true;
		}

	};

	static {
		// FlexoObject.addActionForClass(actionType, DiagramElement.class);
		FlexoObject.addActionForClass(actionType, VirtualModelInstance.class);
		// FlexoObject.addActionForClass(actionType, View.class);
	}

	private VirtualModelInstance vmInstance;
	private CreationScheme _creationScheme;

	CreationSchemeAction(VirtualModelInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	private EditionPatternInstance editionPatternInstance;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParametersException {
		logger.info("Create EditionPatternInstance using CreationScheme");
		logger.info("getEditionPattern()=" + getEditionPattern());

		retrieveMissingDefaultParameters();

		// getEditionPattern().getViewPoint().getViewpointOntology().loadWhenUnloaded();

		// In case of this action is embedded in a CreateVirtualModelInstance action, the editionPatternInstance (which will be here a
		// VirtualModelInstance) will be already initialized and should subsequently not been recreated)
		if (editionPatternInstance == null) {
			if (getVirtualModelInstance() != null) {
				editionPatternInstance = getVirtualModelInstance().makeNewEditionPatternInstance(getEditionPattern());
			} else {
				logger.warning("Could not create new EditionPatternInstance because container VirtualModelInstance is null");
				throw new InvalidParametersException("VirtualModelInstance");
			}
		}

		applyEditionActions();

	}

	/**
	 * Used when creation of EditionPatternInstance initialization is beeing delegated to an other component.<br>
	 * This happens for example in the case of VirtualModelInstance creation, where the creation of EditionPatternInstance is performed in
	 * the {@link CreateVirtualModelInstance} action
	 * 
	 * @param editionPatternInstance
	 */
	public void initWithEditionPatternInstance(EditionPatternInstance editionPatternInstance) {
		this.editionPatternInstance = editionPatternInstance;
	}

	public boolean retrieveMissingDefaultParameters() {
		boolean returned = true;
		EditionScheme editionScheme = getEditionScheme();
		for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
			if (getParameterValue(parameter) == null) {
				logger.warning("Found not initialized parameter " + parameter);
				Object defaultValue = parameter.getDefaultValue(this);
				if (defaultValue != null) {
					logger.warning("Du coup je lui donne la valeur " + defaultValue);
					parameterValues.put(parameter, defaultValue);
					if (!parameter.isValid(this, defaultValue)) {
						logger.info("Parameter " + parameter + " is not valid for value " + defaultValue);
						returned = false;
					}
				}
			}
			if (parameter instanceof ListParameter) {
				List list = (List) ((ListParameter) parameter).getList(this);
				parameterListValues.put((ListParameter) parameter, list);
			}
		}
		return returned;
	}

	@Override
	public VirtualModelInstance getVirtualModelInstance() {
		if (vmInstance == null) {
			if (getFocusedObject() instanceof VirtualModelInstance) {
				vmInstance = getFocusedObject();
			}
		}
		return vmInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance vmInstance) {
		this.vmInstance = vmInstance;
	}

	public CreationScheme getCreationScheme() {
		return _creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		_creationScheme = creationScheme;
	}

	@Override
	public CreationScheme getEditionScheme() {
		return getCreationScheme();
	}

	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		return getVirtualModelInstance();
	}

	/**
	 * This is the internal code performing execution of a single {@link EditionAction} defined to be part of the execution control graph of
	 * related {@link EditionScheme}<br>
	 */
	@Override
	protected Object performAction(EditionAction action, Hashtable<EditionAction, Object> performedActions) {
		Object assignedObject = super.performAction(action, performedActions);
		if (assignedObject != null && action instanceof AssignableAction) {
			AssignableAction assignableAction = (AssignableAction) action;
			if (assignableAction.getPatternRole() != null && assignedObject instanceof FlexoModelObject) {
				getEditionPatternInstance().setObjectForPatternRole((FlexoModelObject) assignedObject, assignableAction.getPatternRole());
			}
		}

		return assignedObject;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof PatternRoleBindingVariable) {
			return getEditionPatternInstance().getPatternActor(((PatternRoleBindingVariable) variable).getPatternRole());
		} else if (variable.getVariableName().equals(EditionScheme.THIS)) {
			return getEditionPatternInstance();
		}
		return super.getValue(variable);
	}

	@Override
	public void setValue(Object value, BindingVariable variable) {
		if (variable instanceof PatternRoleBindingVariable) {
			getEditionPatternInstance().setPatternActor(value, ((PatternRoleBindingVariable) variable).getPatternRole());
			return;
		}
		super.setValue(value, variable);
	}

}