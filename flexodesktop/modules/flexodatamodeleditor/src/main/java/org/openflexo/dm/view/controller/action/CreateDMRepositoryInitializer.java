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
package org.openflexo.dm.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.dm.view.DMModelView;
import org.openflexo.dm.view.popups.AskNewRepositoryDialog;
import org.openflexo.dm.view.popups.ImportRationalRoseRepositoryDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.foundation.dm.action.CreateProjectDatabaseRepository;
import org.openflexo.foundation.dm.action.CreateProjectRepository;
import org.openflexo.foundation.dm.action.ImportExternalDatabaseRepository;
import org.openflexo.foundation.dm.action.ImportJARFileRepository;
import org.openflexo.foundation.dm.action.ImportRationalRoseRepository;
import org.openflexo.foundation.dm.action.ImportThesaurusDatabaseRepository;
import org.openflexo.foundation.dm.action.ImportThesaurusRepository;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.module.ModuleLoader;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateDMRepositoryInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMRepositoryInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMRepository.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionFinalizer<CreateDMRepository> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				if (action.getNewRepository() != null) {
					logger.info("Finalizer for CreateDMRepository in DMModelView with " + action.getNewRepository());
					if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getNewRepository()
							.getDMModel()) {
						DMModelView dmModelView = (DMModelView) getControllerActionInitializer().getDMController()
								.getCurrentEditedObjectView();
						dmModelView.getRepositoryFolderTable().selectObject(action.getNewRepository().getRepositoryFolder());
						dmModelView.getRepositoriesTable().selectObject(action.getNewRepository());
					}
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_REPOSITORY_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return null;
	}

	@Override
	public void init() {
		initActionType(CreateDMRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				return (AskNewRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController().getProject(),
						getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE);
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_REPOSITORY_ICON, null);

		initActionType(CreateProjectRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				return (AskNewRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController().getProject(),
						getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE);
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_REPOSITORY_ICON, null);

		initActionType(CreateProjectDatabaseRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				return (AskNewRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController().getProject(),
						getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE);
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_EOREPOSITORY_ICON, null);

		initActionType(ImportExternalDatabaseRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				return (AskNewRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController().getProject(),
						getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE);
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_EOREPOSITORY_ICON, null);

		if (ModuleLoader.isDevelopperRelease() || ModuleLoader.isMaintainerRelease()) {
			initActionType(ImportJARFileRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
				@Override
				public boolean run(ActionEvent e, CreateDMRepository action) {
					return (AskNewRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController().getProject(),
							getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE);
				}
			}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_JAR_REPOSITORY_ICON, null);
		}

		initActionType(ImportRationalRoseRepository.actionType, new FlexoActionInitializer<ImportRationalRoseRepository>() {
			@Override
			public boolean run(ActionEvent e, ImportRationalRoseRepository action) {
				return (ImportRationalRoseRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController()
						.getProject(), getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE);
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_REPOSITORY_ICON, null);

		initActionType(ImportThesaurusRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				// Not implemented yet
				return false;
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_REPOSITORY_ICON, null);

		initActionType(ImportThesaurusDatabaseRepository.actionType, new FlexoActionInitializer<CreateDMRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateDMRepository action) {
				// Not implemented yet
				return false;
			}
		}, getDefaultFinalizer(), getDefaultExceptionHandler(), null, null, null, DMEIconLibrary.DM_EOREPOSITORY_ICON, null);
	}

}
