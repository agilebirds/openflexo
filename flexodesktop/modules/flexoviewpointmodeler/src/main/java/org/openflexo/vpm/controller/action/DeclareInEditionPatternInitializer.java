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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.action.DeclareInEditionPattern;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.controller.CEDController;


public class DeclareInEditionPatternInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeclareInEditionPatternInitializer(CEDControllerActionInitializer actionInitializer)
	{
		super(DeclareInEditionPattern.actionType,actionInitializer);
	}

	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() 
	{
		return (CEDControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	public CEDController getController()
	{
		return (CEDController)super.getController();
	}
	
	@Override
	protected FlexoActionInitializer<DeclareInEditionPattern> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DeclareInEditionPattern>() {
			@Override
			public boolean run(ActionEvent e, DeclareInEditionPattern action)
			{
				
				FIBDialog dialog = FIBDialog.instanciateComponent(
						CEDCst.DECLARE_IN_EDITION_PATTERN_DIALOG_FIB,
						action, null, true);
				return (dialog.getStatus() == Status.VALIDATED);
			}
		};
	}
	

	@Override
	protected FlexoActionFinalizer<DeclareInEditionPattern> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DeclareInEditionPattern>() {
			@Override
			public boolean run(ActionEvent e, DeclareInEditionPattern action)
			{
				getController().setCurrentEditedObjectAsModuleView(action.getEditionPattern(),getController().VIEW_POINT_PERSPECTIVE);
				getController().getSelectionManager().setSelectedObject(action.getPatternRole());
				return true;
			}
		};
	}


}
