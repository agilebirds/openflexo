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
package org.openflexo.foundation.ie.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.wizards.EntityFromWidgets;
import org.openflexo.foundation.ie.wizards.PropertyProposal;

public class GenerateEntityFromSelection extends FlexoAction<GenerateEntityFromSelection, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(GenerateComponentScreenshot.class.getPackage().getName());
	private boolean _useDMEOEntity;

	public static FlexoActionType<GenerateEntityFromSelection, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<GenerateEntityFromSelection, FlexoModelObject, FlexoModelObject>(
			"generate_entity", FlexoActionType.editGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateEntityFromSelection makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new GenerateEntityFromSelection(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

	};

	private boolean _hasBeenRegenerated;
	private String _eoModelName;
	private List<PropertyProposal> _selectedProposal;
	private String _eoEntityName;
	private String _projectDateBaseRepositoryName;

	GenerateEntityFromSelection(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public List<FlexoModelObject> getSelection() {
		ArrayList<FlexoModelObject> reply = new ArrayList<FlexoModelObject>();
		if (getFocusedObject() != null)
			reply.add(getFocusedObject());
		if (getGlobalSelection() != null && getGlobalSelection().size() > 0) {
			reply.addAll(getGlobalSelection());
		}
		return reply;
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getSelection().size() > 0) {
			try {
				generator.justDoIt(getProjectDatabaseRepositoryName(), getEOModelName(), getEOEntityName(), getSelectedProposals(),
						getEditor());
			} catch (InvalidNameException e) {
				e.printStackTrace();
				throw new FlexoException(e.getMessage());
			}
		} else {
			logger.warning("Selection is empty !");
		}
	}

	public String getEOModelName() {
		return _eoModelName;
	}

	public List<PropertyProposal> getSelectedProposals() {
		return _selectedProposal;
	}

	public String getEOEntityName() {
		return _eoEntityName;
	}

	public String getProjectDatabaseRepositoryName() {
		return _projectDateBaseRepositoryName;
	}

	public void setEOModelName(String value) {
		_eoModelName = value;
	}

	public void setSelectedProposals(List<PropertyProposal> value) {
		_selectedProposal = value;
	}

	public void setEOEntityName(String value) {
		_eoEntityName = value;
	}

	public void setProjectDatabaseRepositoryName(String value) {
		_projectDateBaseRepositoryName = value;
	}

	public boolean getUseDMEOEntity() {
		return _useDMEOEntity;
	}

	public void setUseDMEOEntity(boolean value) {
		_useDMEOEntity = value;
	}

	private EntityFromWidgets generator;

	public EntityFromWidgets getEntityFromWidgets() {
		return generator;
	}

	public void setEntityFromWidgets(EntityFromWidgets entityFromWidgets) {
		generator = entityFromWidgets;
	}

	public boolean hasBeenRegenerated() {
		return _hasBeenRegenerated;
	}

}
