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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sg.action.RemoveImplementationModel;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class RemoveImplementionModelInitializer extends
		ActionInitializer<RemoveImplementationModel, ImplementationModel, ImplementationModel> {

	RemoveImplementionModelInitializer(SGControllerActionInitializer actionInitializer) {
		super(RemoveImplementationModel.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RemoveImplementationModel> getDefaultInitializer() {
		return new FlexoActionInitializer<RemoveImplementationModel>() {
			@Override
			public boolean run(ActionEvent e, RemoveImplementationModel action) {
				return FlexoController.confirmWithWarning(FlexoLocalization
						.localizedForKey("are_you_sure_you_want_to_delete_implementation_model"));
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
