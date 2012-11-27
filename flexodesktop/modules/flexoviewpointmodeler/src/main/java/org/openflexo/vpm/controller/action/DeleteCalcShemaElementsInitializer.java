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
package org.openflexo.vpm.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.action.DeleteExampleDrawingElements;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DeleteCalcShemaElementsInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteCalcShemaElementsInitializer(VPMControllerActionInitializer actionInitializer) {
		super(DeleteExampleDrawingElements.actionType, actionInitializer);
	}

	@Override
	protected VPMControllerActionInitializer getControllerActionInitializer() {
		return (VPMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteExampleDrawingElements> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteExampleDrawingElements>() {
			@Override
			public boolean run(EventObject e, DeleteExampleDrawingElements action) {
				return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteExampleDrawingElements> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteExampleDrawingElements>() {
			@Override
			public boolean run(EventObject e, DeleteExampleDrawingElements action) {
				if (getControllerActionInitializer().getCEDController().getSelectionManager().getLastSelectedObject() != null
						&& getControllerActionInitializer().getCEDController().getSelectionManager().getLastSelectedObject().isDeleted()) {
					getControllerActionInitializer().getCEDController().getSelectionManager().resetSelection();
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0);
	}

}
