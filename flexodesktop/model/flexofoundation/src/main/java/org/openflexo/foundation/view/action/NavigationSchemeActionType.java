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
import org.openflexo.foundation.viewpoint.NavigationScheme;
import org.openflexo.localization.LocalizedDelegate;

public class NavigationSchemeActionType extends FlexoActionType<NavigationSchemeAction, FlexoModelObject, FlexoModelObject> {

	private NavigationScheme navigationScheme;
	private EditionPatternReference editionPatternReference;

	public NavigationSchemeActionType(NavigationScheme navigationScheme, EditionPatternReference editionPatternReference) {
		super(navigationScheme.getName(), FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE);
		this.navigationScheme = navigationScheme;
		this.editionPatternReference = editionPatternReference;
	}

	@Override
	public LocalizedDelegate getLocalizer() {
		return navigationScheme.getLocalizedDictionary();
	}

	@Override
	public boolean isEnabled(FlexoModelObject object, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		return isEnabledForSelection(object, globalSelection);
	}

	@Override
	protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
		return navigationScheme.evaluateCondition(editionPatternReference);
	}

	@Override
	protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
		return true;
	}

	@Override
	public NavigationSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		return new NavigationSchemeAction(this, focusedObject, globalSelection, editor);
	}

	public NavigationScheme getNavigationScheme() {
		return navigationScheme;
	}

	public EditionPatternReference getEditionPatternReference() {
		return editionPatternReference;
	}

}
