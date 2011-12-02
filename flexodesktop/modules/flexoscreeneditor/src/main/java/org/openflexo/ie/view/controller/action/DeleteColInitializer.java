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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.action.DeleteCol;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DeleteColInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteColInitializer(IEControllerActionInitializer actionInitializer) {
		super(DeleteCol.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteCol> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteCol>() {
			@Override
			public boolean run(ActionEvent e, DeleteCol action) {
				IEHTMLTableWidget table = null;
				if (action.getFocusedObject() instanceof IETDWidget) {
					table = ((IETDWidget) action.getFocusedObject()).htmlTable();
				} else if (action.getFocusedObject() instanceof IESequenceWidget) {
					table = ((IESequenceWidget) action.getFocusedObject()).htmlTable();
				}
				if (table != null && table.getColCount() == 1) {
					FlexoController.notify(FlexoLocalization.localizedForKey("cannot_delete_last_col"));
				}

				if (getModule().isActive() && table != null && table.getColCount() > 1) {
					return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"));
				} else {
					return false;
				}

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteCol> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteCol>() {
			@Override
			public boolean run(ActionEvent e, DeleteCol action) {
				return true;
			}
		};
	}

}
