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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.wkf.action.CreateAssociation;
import org.openflexo.foundation.wkf.action.CreateEdge;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;


public class CreateAssociationInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateAssociationInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(CreateAssociation.actionType,actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer()
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	boolean nameWasEdited = false;


	@Override
	protected FlexoActionInitializer<CreateAssociation> getDefaultInitializer()
	{
		return new FlexoActionInitializer<CreateAssociation>() {
			@Override
			public boolean run(ActionEvent e, CreateAssociation action)
			{
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateAssociation> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<CreateAssociation>() {
			@Override
			public boolean run(ActionEvent e, CreateAssociation action)
			{
				
				getController().getSelectionManager().setSelectedObject(action.getNewAssociation());

				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateEdge> getDefaultExceptionHandler()
	{
		return new FlexoExceptionHandler<CreateEdge>() {
			@Override
			public boolean handleException(FlexoException exception, CreateEdge action) {
				if (exception instanceof CreateAssociation.InvalidEdgeDefinition) {
					FlexoController.notify(exception.getLocalizedMessage());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon()
	{
		return WKFIconLibrary.POSTCONDITION_ICON;
	}

}
