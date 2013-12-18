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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.binding.PatternRoleBindingVariable;

public class DeletionSchemeAction extends EditionSchemeAction<DeletionSchemeAction, DeletionScheme, EditionPatternInstance> {

	private static final Logger logger = Logger.getLogger(DeletionSchemeAction.class.getPackage().getName());

	public static FlexoActionType<DeletionSchemeAction, EditionPatternInstance, VirtualModelInstanceObject> actionType = new FlexoActionType<DeletionSchemeAction, EditionPatternInstance, VirtualModelInstanceObject>(
			"delete_edition_pattern_instance", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeletionSchemeAction makeNewAction(EditionPatternInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
				FlexoEditor editor) {
			return new DeletionSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(EditionPatternInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(EditionPatternInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoObject.addActionForClass(actionType, EditionPatternInstance.class);
	}

	private VirtualModelInstance vmInstance;
	private DeletionScheme deletionScheme;

	DeletionSchemeAction(EditionPatternInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private EditionPatternInstance editionPatternInstanceToDelete;

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParametersException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete EditionPatternInstance using DeletionScheme");
			logger.fine("getDeletionScheme()=" + getDeletionScheme());
			logger.fine("getEditionPatternInstance()=" + getEditionPatternInstance());
		}
		applyEditionActions();

	}

	public final EditionPatternInstance getEditionPatternInstance() {
		return getFocusedObject();
	}

	@Override
	public VirtualModelInstance getVirtualModelInstance() {
		if (vmInstance == null) {
			if (getFocusedObject() instanceof VirtualModelInstance) {
				vmInstance = (VirtualModelInstance) getFocusedObject();
			}
		}
		return vmInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance vmInstance) {
		this.vmInstance = vmInstance;
	}

	public DeletionScheme getDeletionScheme() {
		return deletionScheme;
	}

	public void setDeletionScheme(DeletionScheme deletionScheme) {
		this.deletionScheme = deletionScheme;
	}

	@Override
	public DeletionScheme getEditionScheme() {
		return getDeletionScheme();
	}

	/*@Override
	public EditionPatternInstance getEditionPatternInstance() {
		return getEditionPatternInstanceToDelete();
	}

	public EditionPatternInstance getEditionPatternInstanceToDelete() {
		if (editionPatternInstanceToDelete == null && getFocusedObject() instanceof DiagramElement) {
			editionPatternInstanceToDelete = ((DiagramElement) getFocusedObject()).getEditionPatternInstance();
		}
		return editionPatternInstanceToDelete;
	}

	public void setEditionPatternInstanceToDelete(EditionPatternInstance editionPatternInstanceToDelete) {
		this.editionPatternInstanceToDelete = editionPatternInstanceToDelete;
	}*/

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		return getVirtualModelInstance();
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