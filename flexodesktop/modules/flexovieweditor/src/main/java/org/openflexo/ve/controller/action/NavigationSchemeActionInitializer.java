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
package org.openflexo.ve.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.diagram.action.NavigationSchemeAction;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.widget.ParametersRetriever;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class NavigationSchemeActionInitializer extends ActionInitializer<NavigationSchemeAction, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	NavigationSchemeActionInitializer(VEControllerActionInitializer actionInitializer) {
		super(null, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<NavigationSchemeAction> getDefaultInitializer() {
		return new FlexoActionInitializer<NavigationSchemeAction>() {
			@Override
			public boolean run(EventObject e, NavigationSchemeAction action) {
				if (!action.evaluateCondition()) {
					return false;
				}

				if (action.getTargetObject() == null) {
					// If target diagram is not existant, we must create it
					// First retrieve parameters
					return ParametersRetriever.retrieveParameters(action, action.escapeParameterRetrievingWhenValid);
				} else {
					// Target diagram is already existing, finalizer will show it
					// First retrieve parameters
					return true;
				}

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<NavigationSchemeAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<NavigationSchemeAction>() {
			@Override
			public boolean run(EventObject e, NavigationSchemeAction action) {
				if (action.getTargetObject() != null) {
					// Editor will handle switch to right module and perspective, and select target object
					getEditor().focusOn(action.getTargetObject());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<NavigationSchemeAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<NavigationSchemeAction>() {
			@Override
			public boolean handleException(FlexoException exception, NavigationSchemeAction action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.NAVIGATION_SCHEME_ICON;
	}

}
