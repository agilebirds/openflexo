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
package org.openflexo.foundation.view.diagram.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.action.NavigationSchemeActionType;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.model.ViewObject;
import org.openflexo.foundation.view.diagram.viewpoint.NavigationScheme;
import org.openflexo.foundation.viewpoint.EditionScheme;

public class NavigationSchemeAction extends EditionSchemeAction<NavigationSchemeAction> {

	private static final Logger logger = Logger.getLogger(NavigationSchemeAction.class.getPackage().getName());

	private NavigationSchemeActionType actionType;

	public NavigationSchemeAction(NavigationSchemeActionType actionType, FlexoModelObject focusedObject,
			Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		this.actionType = actionType;
	}

	public NavigationScheme getNavigationScheme() {
		if (actionType != null) {
			return actionType.getNavigationScheme();
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
		return getNavigationScheme();
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Perform navigation " + actionType);

		if (evaluateCondition()) {
			// If target diagram is not existant, we must create it
			if (getTargetObject() == null) {
				applyEditionActions();
			}
		}
	}

	public boolean evaluateCondition() {
		if (getNavigationScheme() == null) {
			logger.warning("No navigation scheme. Please investigate !");
			return false;
		}
		return getNavigationScheme().evaluateCondition(actionType.getEditionPatternReference());
	}

	public FlexoModelObject getTargetObject() {
		if (getNavigationScheme() == null) {
			logger.warning("No navigation scheme. Please investigate !");
			return null;
		}
		return getNavigationScheme().evaluateTargetObject(actionType.getEditionPatternReference());
	}

	@Override
	public View retrieveOEShema() {
		if (getFocusedObject() instanceof ViewObject) {
			return ((ViewObject) getFocusedObject()).getView();
		}
		return null;
	}

}
