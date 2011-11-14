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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.foundation.viewpoint.EditionScheme;

public class ActionSchemeAction extends EditionSchemeAction<ActionSchemeAction> {

	private static final Logger logger = Logger.getLogger(ActionSchemeAction.class.getPackage().getName());

	private ActionSchemeActionType actionType;

	public ActionSchemeAction(ActionSchemeActionType actionType, FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		this.actionType = actionType;
	}

	public ActionScheme getActionScheme() {
		if (actionType != null) {
			return actionType.getActionScheme();
		}
		return null;
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		if (actionType != null) {
			return actionType.getEditionPatternReference().getEditionPatternInstance();
		}
		return null;
	}

	@Override
	public EditionScheme getEditionScheme() {
		return getActionScheme();
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Perform action " + actionType);

		if (getActionScheme() != null && getActionScheme().evaluateCondition(actionType.getEditionPatternReference())) {
			applyEditionActions();
		}
	}

	@Override
	protected View retrieveOEShema() {
		if (getFocusedObject() instanceof ViewObject) {
			return ((ViewObject) getFocusedObject()).getShema();
		}
		return null;
	}

	@Override
	protected Object getOverridenGraphicalRepresentation() {
		return null;
	}
}
