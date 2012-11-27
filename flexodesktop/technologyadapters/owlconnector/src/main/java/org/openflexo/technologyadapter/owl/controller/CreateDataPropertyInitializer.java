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
package org.openflexo.technologyadapter.owl.controller;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.icon.OntologyIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.action.CreateDataProperty;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateDataPropertyInitializer extends ActionInitializer<CreateDataProperty, OWLObject, OWLObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDataPropertyInitializer(ControllerActionInitializer actionInitializer) {
		super(CreateDataProperty.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<CreateDataProperty> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDataProperty>() {
			@Override
			public boolean run(EventObject e, CreateDataProperty action) {
				FIBDialog dialog = FIBDialog.instanciateAndShowDialog(OWLFIBLibrary.CREATE_DATA_PROPERTY_DIALOG_FIB, action,
						FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
				return dialog.getStatus() == Status.VALIDATED;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDataProperty> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDataProperty>() {
			@Override
			public boolean run(EventObject e, CreateDataProperty action) {
				getController().getSelectionManager().setSelectedObject((FlexoModelObject) action.getNewProperty());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
	}

}
