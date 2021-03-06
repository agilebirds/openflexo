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

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.viewpoint.CreationScheme;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ListParameter;

public class CreationSchemeAction extends EditionSchemeAction<CreationSchemeAction> {

	private static final Logger logger = Logger.getLogger(CreationSchemeAction.class.getPackage().getName());

	public static FlexoActionType<CreationSchemeAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<CreationSchemeAction, FlexoModelObject, FlexoModelObject>(
			"create_edition_pattern_instance", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreationSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new CreationSchemeAction(focusedObject, globalSelection, editor);
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
		FlexoModelObject.addActionForClass(actionType, ViewObject.class);
	}

	private View _view;
	private CreationScheme _creationScheme;

	CreationSchemeAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	private EditionPatternInstance editionPatternInstance;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParametersException {
		logger.info("Create EditionPatternInstance using CreationScheme");
		logger.info("getEditionPattern()=" + getEditionPattern());

		retrieveMissingDefaultParameters();
		if (getEditionPattern().getViewPoint().getViewpointOntology() != null) {
			getEditionPattern().getViewPoint().getViewpointOntology().loadWhenUnloaded();
		}

		editionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());

		applyEditionActions();

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

	public View getView() {
		if (_view == null) {
			if (getFocusedObject() instanceof View) {
				_view = (View) getFocusedObject();
			}
		}
		return _view;
	}

	public void setView(View view) {
		_view = view;
	}

	public CreationScheme getCreationScheme() {
		return _creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		_creationScheme = creationScheme;
	}

	@Override
	public EditionScheme getEditionScheme() {
		return getCreationScheme();
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

	@Override
	protected View retrieveOEShema() {
		return getView();
	}

}