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
package org.openflexo.doceditor.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.toc.action.RemoveTOCEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class RemoveTOCEntryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RemoveTOCEntryInitializer(DEControllerActionInitializer actionInitializer) {
		super(RemoveTOCEntry.actionType, actionInitializer);
	}

	@Override
	protected DEControllerActionInitializer getControllerActionInitializer() {
		return (DEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RemoveTOCEntry> getDefaultInitializer() {
		return new FlexoActionInitializer<RemoveTOCEntry>() {
			@Override
			public boolean run(ActionEvent e, RemoveTOCEntry action) {
				return FlexoController.confirm(FlexoLocalization.localizedForKey("are_you_sure_you_want_to_remove_this_toc_entry") + " "
						+ action.getFocusedObject().getTitle());
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RemoveTOCEntry> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RemoveTOCEntry>() {
			@Override
			public boolean run(ActionEvent e, RemoveTOCEntry action) {
				return true;
			}
		};
	}

}
