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
package org.openflexo.tm.hibernate.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.sgmodule.controller.action.SGControllerActionInitializer;
import org.openflexo.tm.hibernate.impl.HibernateImplementation;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateHibernateModelActionInitializer extends
		ActionInitializer<CreateHibernateModelAction, HibernateImplementation, HibernateImplementation> {

	public static String HIBERNATE_CREATEMODEL_DIALOG_FIB_RESOURCE_PATH = "/Hibernate/Fib/Dialog/CreateModelDialog.fib";

	public CreateHibernateModelActionInitializer(SGControllerActionInitializer actionInitializer) {
		super(CreateHibernateModelAction.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateHibernateModelAction> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateHibernateModelAction>() {
			@Override
			public boolean run(ActionEvent e, CreateHibernateModelAction action) {
				FIBDialog dialog = FIBDialog.instanciateComponent(HIBERNATE_CREATEMODEL_DIALOG_FIB_RESOURCE_PATH, action, null, true);
				return (dialog.getStatus() == Status.VALIDATED);
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateHibernateModelAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateHibernateModelAction>() {
			@Override
			public boolean handleException(FlexoException exception, CreateHibernateModelAction action) {
				if (exception instanceof InvalidParametersException) {
					FlexoController.notify(exception.getLocalizedMessage());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SGIconLibrary.GENERATED_CODE_ICON;
	}

}
