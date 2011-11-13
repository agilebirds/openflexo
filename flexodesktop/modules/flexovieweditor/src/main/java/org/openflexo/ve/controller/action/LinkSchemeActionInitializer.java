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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.action.LinkSchemeAction;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.controller.OEController;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class LinkSchemeActionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	LinkSchemeActionInitializer(OEControllerActionInitializer actionInitializer) {
		super(LinkSchemeAction.actionType, actionInitializer);
	}

	@Override
	protected OEControllerActionInitializer getControllerActionInitializer() {
		return (OEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<LinkSchemeAction> getDefaultInitializer() {
		return new FlexoActionInitializer<LinkSchemeAction>() {
			@Override
			public boolean run(ActionEvent e, LinkSchemeAction action) {
				return ParametersRetriever.retrieveParameters(action, action.escapeParameterRetrievingWhenValid);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<LinkSchemeAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<LinkSchemeAction>() {
			@Override
			public boolean run(ActionEvent e, LinkSchemeAction action) {
				((OEController) getController()).getSelectionManager().setSelectedObject(action.getNewConnector());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<LinkSchemeAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<LinkSchemeAction>() {
			@Override
			public boolean handleException(FlexoException exception, LinkSchemeAction action) {
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
		return VEIconLibrary.SHAPE_ICON;
	}

}
