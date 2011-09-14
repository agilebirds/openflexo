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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.action.ConnectCGRepository;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ConnectCGRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ConnectCGRepositoryInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(ConnectCGRepository.actionType,actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer()
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ConnectCGRepository> getDefaultInitializer()
	{
		return new FlexoActionInitializer<ConnectCGRepository>() {
			@Override
			public boolean run(ActionEvent e, ConnectCGRepository action)
			{
				if (!(action.getFocusedObject() instanceof CGRepository))
					return false;
				CGRepository repository = (CGRepository) action.getFocusedObject();
				if (repository == null)
					return false;
				if (repository.getSourceCodeRepository().getDirectory() == null) {
					repository.getSourceCodeRepository().setDirectory(new File(System.getProperty("user.home")));
				}
				if (repository.getWarRepository().getDirectory() == null) {
					repository.getWarRepository().setDirectory(new File(System.getProperty("user.home")));
				}
				TextFieldParameter paramName = new TextFieldParameter("name", "cg_repository_name", repository.getDisplayName());
				DirectoryParameter paramDir = new DirectoryParameter("directory", "source_directory", repository.getSourceCodeRepository()
						.getDirectory());
				TextFieldParameter paramWarName = new TextFieldParameter("warName", "war_name", repository.getWarName());
				DirectoryParameter paramWarDir = new DirectoryParameter("warDirectory", "war_directory", repository.getWarRepository()
						.getDirectory());
				DirectoryParameter readerDir = null;
				if (repository.includeReader() && repository.getReaderRepository()!=null) {
					readerDir = new DirectoryParameter("readerDirectory", "reader_directory", repository.getReaderRepository().getDirectory());
				}
				ParameterDefinition[] params = new ParameterDefinition[readerDir!=null?5:4];
				params[0] = paramName;
				params[1] = paramDir;
				params[2] = paramWarName;
				params[3] = paramWarDir;
				if (readerDir!=null)
					params[4] = readerDir;
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, FlexoLocalization
								        		.localizedForKey("connect_repository_to_local_file_system"), FlexoLocalization.localizedForKey("enter_parameters_for_connecting_repository_to_the_local_file_system"), params);
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					try {
						repository.setDisplayName(paramName.getValue());
						repository.setDirectory(paramDir.getValue());
						repository.setWarName(paramWarName.getValue());
						repository.setWarDirectory(paramWarDir.getValue());
						if (repository.includeReader() && repository.getReaderRepository()!=null)
							repository.getReaderRepository().setDirectory(readerDir.getValue());
					} catch (DuplicateCodeRepositoryNameException e2) {
						e2.printStackTrace();
						FlexoController.notify(FlexoLocalization.localizedForKey("wrong_name"));
						return false;
					}
					if (!repository.getSourceCodeRepository().isConnected()) {
						FlexoController.notify(FlexoLocalization.localizedForKey("sorry_invalid_directory"));
						return false;
					}
					if (repository.includeReader() && !repository.getReaderRepository().isConnected()) {
						FlexoController.notify(FlexoLocalization.localizedForKey("reader_not_configured"));
						return false;
					}
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ConnectCGRepository> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<ConnectCGRepository>() {
			@Override
			public boolean run(ActionEvent e, ConnectCGRepository action)
			{
				return true;
			}
		};
	}


}
