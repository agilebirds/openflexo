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


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.DuplicateDocTypeException;
import org.openflexo.foundation.cg.action.AddDocType;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddDocTypeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddDocTypeInitializer(DEControllerActionInitializer actionInitializer)
	{
		super(AddDocType.actionType,actionInitializer);
	}

	@Override
	protected DEControllerActionInitializer getControllerActionInitializer() 
	{
		return (DEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddDocType> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddDocType>() {
			@Override
			public boolean run(ActionEvent e, AddDocType action)
			{
				ParameterDefinition pd[] = new ParameterDefinition[1];
				pd[0] = new TextFieldParameter("name","name","");
				AskParametersDialog d = AskParametersDialog.createAskParametersDialog(null,null,FlexoLocalization.localizedForKey("add_doc_type"), FlexoLocalization.localizedForKey("enter_name_and_folder"), pd);
				if (d.getStatus()==AskParametersDialog.VALIDATE) {
					if (pd[0].getValue()!=null && ((String)pd[0].getValue()).length()>0) {
						action.setNewName((String) pd[0].getValue());
						return true;
					} 
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddDocType> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddDocType>() {
			@Override
			public boolean run(ActionEvent e, AddDocType action)
			{
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddDocType> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<AddDocType>() {
			@Override
			public boolean handleException(FlexoException exception, AddDocType action) {
				if (exception instanceof DuplicateDocTypeException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("a_doc_type_with_a_similar_name_"+((DuplicateDocTypeException)exception).getDocType().getName()+FlexoLocalization.localizedForKey("_already_exists")));
					return true;
				}
				return false;
			}
		};
	}



}
