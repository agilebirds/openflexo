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
import org.openflexo.foundation.ptoc.action.RemovePTOCRepository;
import org.openflexo.foundation.toc.action.RemoveTOCRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class RemovePTOCRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RemovePTOCRepositoryInitializer(DEControllerActionInitializer actionInitializer)
	{
		super(RemovePTOCRepository.actionType,actionInitializer);
	}

	@Override
	protected DEControllerActionInitializer getControllerActionInitializer() 
	{
		return (DEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RemovePTOCRepository> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<RemovePTOCRepository>() {
			@Override
			public boolean run(ActionEvent e, RemovePTOCRepository action)
			{
				return FlexoController.confirm(FlexoLocalization.localizedForKey("are_you_sure_you_want_to_remove_this_table_of_content")+" "+action.getFocusedObject().getTitle());
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RemovePTOCRepository> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<RemovePTOCRepository>() {
			@Override
			public boolean run(ActionEvent e, RemovePTOCRepository action)
			{
				return true;
			}
		};
	}

}
