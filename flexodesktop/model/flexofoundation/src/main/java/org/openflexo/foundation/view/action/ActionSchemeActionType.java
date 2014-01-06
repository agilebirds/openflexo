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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.localization.LocalizedDelegate;

public class ActionSchemeActionType extends FlexoActionType<ActionSchemeAction, EditionPatternInstance, VirtualModelInstanceObject> {

	private final ActionScheme actionScheme;
	private final EditionPatternInstance editionPatternInstance;

	public ActionSchemeActionType(ActionScheme actionScheme, EditionPatternInstance editionPatternInstance) {
		super(actionScheme.getLabel(), FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE);
		this.actionScheme = actionScheme;
		this.editionPatternInstance = editionPatternInstance;
	}

	@Override
	public LocalizedDelegate getLocalizer() {
		return actionScheme.getLocalizedDictionary();
	}

	@Override
	public boolean isEnabled(EditionPatternInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
		return isEnabledForSelection(object, globalSelection);
	}

	@Override
	public boolean isEnabledForSelection(EditionPatternInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
		return actionScheme.evaluateCondition(editionPatternInstance);
	}

	@Override
	public boolean isVisibleForSelection(EditionPatternInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
		return true;
	}

	@Override
	public ActionSchemeAction makeNewAction(EditionPatternInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
			FlexoEditor editor) {
		return new ActionSchemeAction(this, focusedObject, globalSelection, editor);
	}

	public ActionScheme getActionScheme() {
		return actionScheme;
	}

	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

}
