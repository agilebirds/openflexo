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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.dm.view.popups.AskNewRepositoryDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateAnyDMRepository;
import org.openflexo.foundation.dm.action.CreateProjectDatabaseRepository;
import org.openflexo.foundation.dm.action.CreateProjectRepository;
import org.openflexo.foundation.dm.action.ImportExternalDatabaseRepository;
import org.openflexo.foundation.dm.action.ImportJARFileRepository;
import org.openflexo.foundation.dm.action.ImportRationalRoseRepository;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class DMControllerActionInitializer extends ControllerActionInitializer {

	protected static final Logger logger = Logger.getLogger(DMControllerActionInitializer.class.getPackage().getName());

	DMController _dmController;

	public DMControllerActionInitializer(InteractiveFlexoEditor editor, DMController controller) {
		super(editor, controller);
		_dmController = controller;
	}

	protected DMSelectionManager getDMSelectionManager() {
		return _dmController.getDMSelectionManager();
	}

	protected DMController getDMController() {
		return _dmController;
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new DMSetPropertyInitializer(this);

		new DMCopyInitializer(this);
		new DMCutInitializer(this);
		new DMPasteInitializer(this);
		new DMDeleteInitializer(this);
		new DMSelectAllInitializer(this);
		new CreateDMRepositoryInitializer<CreateAnyDMRepository>(CreateAnyDMRepository.actionType, this) {

			@Override
			protected Icon getEnabledIcon() {
				return DMEIconLibrary.DM_REPOSITORY_ICON;
			}

			@Override
			protected FlexoActionInitializer<CreateAnyDMRepository> getDefaultInitializer() {
				return new FlexoActionInitializer<CreateAnyDMRepository>() {
					@Override
					public boolean run(EventObject e, CreateAnyDMRepository action) {
						return AskNewRepositoryDialog.displayDialog(action,
								getControllerActionInitializer().getDMController().getProject(), getControllerActionInitializer()
										.getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE;
					}
				};
			}
		};
		new CreateDMRepositoryInitializer<CreateProjectDatabaseRepository>(CreateProjectDatabaseRepository.actionType, this) {

			@Override
			protected Icon getEnabledIcon() {
				return DMEIconLibrary.DM_EOREPOSITORY_ICON;
			}

			@Override
			protected FlexoActionInitializer<CreateProjectDatabaseRepository> getDefaultInitializer() {
				return new FlexoActionInitializer<CreateProjectDatabaseRepository>() {
					@Override
					public boolean run(EventObject e, CreateProjectDatabaseRepository action) {
						return AskNewRepositoryDialog.displayDialog(action,
								getControllerActionInitializer().getDMController().getProject(), getControllerActionInitializer()
										.getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE;
					}
				};
			}
		};
		new CreateDMRepositoryInitializer<CreateProjectRepository>(CreateProjectRepository.actionType, this) {

			@Override
			protected Icon getEnabledIcon() {
				return DMEIconLibrary.DM_REPOSITORY_ICON;
			}

			@Override
			protected FlexoActionInitializer<CreateProjectRepository> getDefaultInitializer() {
				return new FlexoActionInitializer<CreateProjectRepository>() {
					@Override
					public boolean run(EventObject e, CreateProjectRepository action) {
						return AskNewRepositoryDialog.displayDialog(action,
								getControllerActionInitializer().getDMController().getProject(), getControllerActionInitializer()
										.getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE;
					}
				};
			}
		};
		new CreateDMRepositoryInitializer<ImportExternalDatabaseRepository>(ImportExternalDatabaseRepository.actionType, this) {

			@Override
			protected Icon getEnabledIcon() {
				return DMEIconLibrary.DM_EOREPOSITORY_ICON;
			}

			@Override
			protected FlexoActionInitializer<ImportExternalDatabaseRepository> getDefaultInitializer() {
				return new FlexoActionInitializer<ImportExternalDatabaseRepository>() {
					@Override
					public boolean run(EventObject e, ImportExternalDatabaseRepository action) {
						return AskNewRepositoryDialog.displayDialog(action,
								getControllerActionInitializer().getDMController().getProject(), getControllerActionInitializer()
										.getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE;
					}
				};
			}
		};
		new CreateDMRepositoryInitializer<ImportRationalRoseRepository>(ImportRationalRoseRepository.actionType, this) {

			@Override
			protected Icon getEnabledIcon() {
				return DMEIconLibrary.DM_REPOSITORY_ICON;
			}

			@Override
			protected FlexoActionInitializer<ImportRationalRoseRepository> getDefaultInitializer() {
				return new FlexoActionInitializer<ImportRationalRoseRepository>() {
					@Override
					public boolean run(EventObject e, ImportRationalRoseRepository action) {
						return AskNewRepositoryDialog.displayDialog(action,
								getControllerActionInitializer().getDMController().getProject(), getControllerActionInitializer()
										.getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE;
					}
				};
			}
		};
		if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
			new CreateDMRepositoryInitializer<ImportJARFileRepository>(ImportJARFileRepository.actionType, this) {

				@Override
				protected Icon getEnabledIcon() {
					return DMEIconLibrary.DM_JAR_REPOSITORY_ICON;
				}

				@Override
				protected FlexoActionInitializer<ImportJARFileRepository> getDefaultInitializer() {
					return new FlexoActionInitializer<ImportJARFileRepository>() {
						@Override
						public boolean run(EventObject e, ImportJARFileRepository action) {
							return AskNewRepositoryDialog.displayDialog(action, getControllerActionInitializer().getDMController()
									.getProject(), getControllerActionInitializer().getDMController().getFlexoFrame()) == AskNewRepositoryDialog.VALIDATE;
						}
					};
				}
			};
		}
		new CreateDMPackageInitializer(this);
		new CreateDMEntityInitializer(this);
		new CreateDMPropertyInitializer(this);
		new CreateDMMethodInitializer(this);
		new CreateDMTranstyperInitializer(this);
		new CreateDMEOModelInitializer(this);
		new ImportDMEOModelInitializer(this);
		new CreateDMEOEntityInitializer(this);
		new CreateDMEOAttributeInitializer(this);
		new CreateDMEORelationshipInitializer(this);
		new UpdateDMRepositoryInitializer(this);
		new ShowTypeHierarchyInitializer(this);
		new ImportJDKEntityInitializer(this);
		new UpdateLoadableDMEntityInitializer(this);
		new DuplicateDMMethodInitializer(this);
		new CreateComponentFromEntityInitializer(this);
		new ResetSourceCodeInitializer(this);
		new CreateERDiagramInitializer(this);
		new GenerateProcessesBusinessDataDMEntityInitializer(this);
		new UpdateAutoGeneratedBusinessDataDMEntityInitializer(this);
	}
}
