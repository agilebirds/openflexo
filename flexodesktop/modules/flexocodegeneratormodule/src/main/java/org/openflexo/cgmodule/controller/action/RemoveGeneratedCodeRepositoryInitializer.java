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
package org.openflexo.cgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.action.RemoveGeneratedCodeRepository;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RemoveGeneratedCodeRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RemoveGeneratedCodeRepositoryInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(RemoveGeneratedCodeRepository.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RemoveGeneratedCodeRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<RemoveGeneratedCodeRepository>() {
			@Override
			public boolean run(ActionEvent e, RemoveGeneratedCodeRepository action) {
				CheckboxParameter deleteFiles = new CheckboxParameter("deleteFiles", "also_delete_files_on_disk", false);
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("would_you_really_like_to_remove_this_generated_code_repository"), deleteFiles);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setDeleteFiles(deleteFiles.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RemoveGeneratedCodeRepository> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RemoveGeneratedCodeRepository>() {
			@Override
			public boolean run(ActionEvent e, RemoveGeneratedCodeRepository action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
