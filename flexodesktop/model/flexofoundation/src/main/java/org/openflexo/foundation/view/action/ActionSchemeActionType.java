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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.localization.LocalizedDelegate;

public class ActionSchemeActionType extends FlexoActionType<ActionSchemeAction, FlexoModelObject, FlexoModelObject> {

	private ActionScheme actionScheme;
	private EditionPatternReference editionPatternReference;

	public ActionSchemeActionType(ActionScheme actionScheme, EditionPatternReference editionPatternReference) {
		super(actionScheme.getLabel(), FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE);
		this.actionScheme = actionScheme;
		this.editionPatternReference = editionPatternReference;
	}

	@Override
	public LocalizedDelegate getLocalizer() {
		return actionScheme.getLocalizedDictionary();
	}

	@Override
	public boolean isEnabled(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
		return isEnabledForSelection(object, globalSelection);
	}

	@Override
	public boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
		return actionScheme.evaluateCondition(editionPatternReference);
	}

	@Override
	public boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
		return true;
	}

	@Override
	public ActionSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		return new ActionSchemeAction(this, focusedObject, globalSelection, editor);
	}

	public ActionScheme getActionScheme() {
		return actionScheme;
	}

	public EditionPatternReference getEditionPatternReference() {
		return editionPatternReference;
	}

}
