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
import org.openflexo.foundation.dkv.DKVModel.LanguageList;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class AddLanguageAction extends FlexoUndoableAction<AddLanguageAction, DKVObject, DKVObject> {

	protected static final Logger logger = FlexoLogger.getLogger(AddLanguageAction.class.getPackage().getName());

	public static FlexoActionType<AddLanguageAction, DKVObject, DKVObject> actionType = new FlexoActionType<AddLanguageAction, DKVObject, DKVObject>(
			"add_language", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddLanguageAction makeNewAction(DKVObject focusedObject, Vector<DKVObject> globalSelection, FlexoEditor editor) {
			return new AddLanguageAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DKVObject object, Vector globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(DKVObject object, Vector globalSelection) {
			return object instanceof DKVModel.LanguageList || object instanceof DKVModel;
		}

		private String[] persistentProperties = { "languageName" };

		@Override
		protected String[] getPersistentProperties() {
			return persistentProperties;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DKVModel.class);
		FlexoModelObject.addActionForClass(actionType, LanguageList.class);
	}

	private DKVModel _dkvModel;
	private Language newLanguage;
	private String _languageName;

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected AddLanguageAction(DKVObject focusedObject, Vector<DKVObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		newLanguage = getDkvModel().addLanguageNamed(_languageName);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Language added");
		}
		objectCreated("NEW_LANGUAGE", newLanguage);
	}

	public Language getNewLanguage() {
		return newLanguage;
	}

	public String getLanguageName() {
		return _languageName;
	}

	public void setLanguageName(String languageName) {
		_languageName = languageName;
	}

	public DKVModel getDkvModel() {
		return _dkvModel;
	}

	public void setDkvModel(DKVModel dkvModel) {
		_dkvModel = dkvModel;
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		doAction(context);
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		getNewLanguage().delete();
	}

}
