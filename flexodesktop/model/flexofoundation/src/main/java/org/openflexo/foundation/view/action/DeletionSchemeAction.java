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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.viewpoint.DeletionScheme;

public class DeletionSchemeAction extends EditionSchemeAction<DeletionSchemeAction, DeletionScheme> {

	private static final Logger logger = Logger.getLogger(DeletionSchemeAction.class.getPackage().getName());

	public static FlexoActionType<DeletionSchemeAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<DeletionSchemeAction, FlexoModelObject, FlexoModelObject>(
			"delete_edition_pattern_instance", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeletionSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new DeletionSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DiagramElement.class);
	}

	private VirtualModelInstance vmInstance;
	private DeletionScheme deletionScheme;

	DeletionSchemeAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private EditionPatternInstance editionPatternInstanceToDelete;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParametersException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete EditionPatternInstance using DeletionScheme");
			logger.fine("getDeletionScheme()=" + getDeletionScheme());
			logger.fine("getEditionPatternInstance()=" + getEditionPatternInstance());
		}
		applyEditionActions();

	}

	@Override
	public VirtualModelInstance getVirtualModelInstance() {
		if (vmInstance == null) {
			FlexoModelObject vObject = getFocusedObject();
			if (vObject instanceof VirtualModelInstance) {
				vmInstance = (VirtualModelInstance) getFocusedObject();
			}
			else if (vObject instanceof EditionPatternInstance ) {
				vmInstance = ((EditionPatternInstance) vObject).getVirtualModelInstance();
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

	@Override
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
	}

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		return getVirtualModelInstance();
	}

}