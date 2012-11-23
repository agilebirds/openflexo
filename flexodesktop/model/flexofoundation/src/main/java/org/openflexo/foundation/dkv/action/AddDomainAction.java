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
package org.openflexo.foundation.dkv.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class AddDomainAction extends FlexoUndoableAction<AddDomainAction, DKVObject, DKVObject> {

	protected static final Logger logger = FlexoLogger.getLogger(AddDomainAction.class.getPackage().getName());

	public static FlexoActionType<AddDomainAction, DKVObject, DKVObject> actionType = new FlexoActionType<AddDomainAction, DKVObject, DKVObject>(
			"add_domain", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddDomainAction makeNewAction(DKVObject focusedObject, Vector<DKVObject> globalSelection, FlexoEditor editor) {
			return new AddDomainAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DKVObject object, Vector<DKVObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DKVObject object, Vector<DKVObject> globalSelection) {
			return object instanceof DKVModel.DomainList || object instanceof DKVModel;
		}

		private String[] persistentProperties = { "newDomainName", "newDomainDescription" };

		@Override
		protected String[] getPersistentProperties() {
			return persistentProperties;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DKVModel.DomainList.class);
		FlexoModelObject.addActionForClass(actionType, DKVModel.class);
	}

	private Domain newDomain;
	private DKVModel _dkvModel;
	private String _newDomainName;
	private String _newDomainDescription;

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	public AddDomainAction(DKVObject focusedObject, Vector<DKVObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		newDomain = getDkvModel().addDomainNamed(getNewDomainName());
		newDomain.setDescription(getNewDomainDescription());
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Domain added");
		}
		objectCreated("NEW_DOMAIN", newDomain);
	}

	public Domain getNewDomain() {
		return newDomain;
	}

	public DKVModel getDkvModel() {
		if (_dkvModel == null) {
			return getFocusedObject().getDkvModel();
		}
		return _dkvModel;
	}

	public void setDkvModel(DKVModel dkvModel) {
		_dkvModel = dkvModel;
	}

	public String getNewDomainDescription() {
		return _newDomainDescription;
	}

	public void setNewDomainDescription(String newDomainDescription) {
		_newDomainDescription = newDomainDescription;
	}

	public String getNewDomainName() {
		return _newDomainName;
	}

	public void setNewDomainName(String newDomainName) {
		_newDomainName = newDomainName;
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		doAction(context);
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		getNewDomain().delete();
	}

}
