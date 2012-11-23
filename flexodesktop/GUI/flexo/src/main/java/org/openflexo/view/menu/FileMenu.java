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
package org.openflexo.view.menu;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.NewProjectComponent;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.ImportProject;
import org.openflexo.foundation.action.ValidateProject;
import org.openflexo.foundation.imported.action.RefreshImportedProcessAction;
import org.openflexo.foundation.imported.action.RefreshImportedRoleAction;
import org.openflexo.foundation.imported.action.UploadPrjAction;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceExceptionList;
import org.openflexo.foundation.utils.OperationCancelledException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.ProjectLoader;
import org.openflexo.print.PrintManagingController;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;

/**
 * 'File' menu
 * 
 * @author sguerin
 */
public class FileMenu extends FlexoMenu {

	static final Logger logger = Logger.getLogger(FileMenu.class.getPackage().getName());

	public NewProjectItem newProjectItem;

	public OpenProjectItem openProjectItem;

	public JMenu recentProjectMenu;

	public JMenu exportMenu;

	public JMenu importMenu;

	public SaveProjectItem saveProjectItem;

	public SaveAsProjectItem saveAsProjectItem;

	public SaveProjectForServerItem saveProjectForServerItem;

	public SendProjectToFlexoServerItem sendProjectToFlexoServerItem;

	public ReloadProjectItem reloadProjectItem;

	public InspectProjectItem inspectProjectItem;

	public PageSetUpItem pageSetupItem;

	public QuitItem quitItem;

	protected FlexoController _controller;

	private ImportProjectMenuItem importProject;

	protected FileMenu(FlexoController controller) {
		this(controller, true);
	}

	protected FileMenu(FlexoController controller, boolean insertCommonItems) {
		super("file", controller);
		_controller = controller;
		if (insertCommonItems) {
			add(newProjectItem = new NewProjectItem());
			add(openProjectItem = new OpenProjectItem());
			add(recentProjectMenu = new JMenu());
			recentProjectMenu.setText(FlexoLocalization.localizedForKey("recent_projects", recentProjectMenu));
			add(importProject = new ImportProjectMenuItem());
			add(saveProjectItem = new SaveProjectItem());
			add(saveAsProjectItem = new SaveAsProjectItem());
			add(saveProjectForServerItem = new SaveProjectForServerItem());
			add(sendProjectToFlexoServerItem = new SendProjectToFlexoServerItem());
			add(reloadProjectItem = new ReloadProjectItem());
			addSeparator();
			if (addImportItems()) {
				add(importMenu);
			}
			if (addExportItems()) {
				add(exportMenu);
			}
			if (importMenu != null || exportMenu != null) {
				addSeparator();
			}
		}
		addSpecificItems();
		if (insertCommonItems) {
			add(inspectProjectItem = new InspectProjectItem());
			if (controller instanceof PrintManagingController) {
				addSeparator();
				addPrintItems();
				add(pageSetupItem = new PageSetUpItem());
			}
			addSeparator();
		}
		add(quitItem = new QuitItem());

		updateRecentProjectMenu();
	}

	public void updateRecentProjectMenu() {
		if (recentProjectMenu != null) {
			recentProjectMenu.removeAll();
			Enumeration<File> en = GeneralPreferences.getLastOpenedProjects().elements();
			while (en.hasMoreElements()) {
				File f = en.nextElement();
				recentProjectMenu.add(new ProjectItem(f));
			}
		}
	}

	public void addToExportItems(FlexoMenuItem exportItem) {
		if (exportMenu == null) {
			exportMenu = new JMenu();
			exportMenu.setText(FlexoLocalization.localizedForKey("export", exportMenu));
		}
		exportMenu.add(exportItem);
	}

	public void addToImportItems(FlexoMenuItem importItem) {
		if (importMenu == null) {
			importMenu = new JMenu();
			importMenu.setText(FlexoLocalization.localizedForKey("import", importMenu));
		}
		importMenu.add(importItem);
	}

	protected boolean addExportItems() {
		return false;
	}

	protected boolean addImportItems() {
		return false;
	}

	public void addSpecificItems() {
		// No specific item here, please override this method when required
	}

	public void addPrintItems() {
		// No specific item here, please override this method when required
	}

	public void quit() {
		try {
			getModuleLoader().quit(true);
		} catch (OperationCancelledException e) {
			// User pressed cancel.
			if (logger.isLoggable(Level.FINEST)) {
				logger.log(Level.FINEST, "Cancelled saving", e);
			}
		}
	}

	// ==========================================================================
	// ============================= NewProject ================================
	// ==========================================================================

	public class NewProjectItem extends FlexoMenuItem {

		public NewProjectItem() {
			super(new NewProjectAction(), "new_project", KeyStroke.getKeyStroke(KeyEvent.VK_N, FlexoCst.META_MASK), getController(), true);
			setText(FlexoLocalization.localizedForKey("new_project", this));
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, FlexoCst.META_MASK));
		}

	}

	public class NewProjectAction extends AbstractAction {
		public NewProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			File projectDirectory = NewProjectComponent.getProjectDirectory();
			if (projectDirectory != null) {
				getController().getProjectLoader().newProject(projectDirectory);

			}
		}
	}

	// ==========================================================================
	// ============================= OpenProject ===============================
	// ==========================================================================

	public class OpenProjectItem extends FlexoMenuItem {

		public OpenProjectItem() {
			super(new OpenProjectAction(), "open_project", KeyStroke.getKeyStroke(KeyEvent.VK_O, FlexoCst.META_MASK), getController(), true);
		}

	}

	public class OpenProjectAction extends AbstractAction {
		public OpenProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			File projectDirectory = OpenProjectComponent.getProjectDirectory();
			if (projectDirectory != null) {
				try {
					getProjectLoader().loadProject(projectDirectory);
				} catch (ProjectLoadingCancelledException e) {
				} catch (ProjectInitializerException e) {
					e.printStackTrace();
					FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
							+ projectDirectory.getAbsolutePath());
				}
			}
		}
	}

	public class ProjectItem extends FlexoMenuItem {

		/**
		 *
		 */
		public ProjectItem(File project) {
			super(new RecentProjectAction(project), project.getName(), null, getController(), false);
		}

	}

	public class RecentProjectAction extends AbstractAction {
		private File projectDirectory;

		public RecentProjectAction(File projectDirectory) {
			super();
			this.projectDirectory = projectDirectory;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				getProjectLoader().loadProject(projectDirectory);
			} catch (ProjectLoadingCancelledException e) {
			} catch (ProjectInitializerException e) {
				e.printStackTrace();
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
						+ projectDirectory.getAbsolutePath());
			}
		}
	}

	public class ImportProjectMenuItem extends FlexoMenuItem {

		public ImportProjectMenuItem() {
			super(new ImportProjectAction(), "import_project", null, getController(), true);
			setIcon(IconLibrary.IMPORT_ICON);
		}

	}

	public class ImportProjectAction extends AbstractAction {
		public ImportProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FlexoEditor editor = getController().getEditor();
			if (editor != null) {
				editor.performActionType(ImportProject.actionType, editor.getProject(), null, e);
			}
		}

	}

	public class SaveProjectItem extends FlexoMenuItem {

		public SaveProjectItem() {
			super(new SaveProjectAction(), "save_project", KeyStroke.getKeyStroke(KeyEvent.VK_S, FlexoCst.META_MASK), getController(), true);
			setIcon(IconLibrary.SAVE_ICON);
		}

	}

	public class SaveProjectAction extends AbstractAction {
		public SaveProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Cursor c = FileMenu.this._controller.getFlexoFrame().getCursor();
			FileMenu.this._controller.getFlexoFrame().setCursor(Cursor.WAIT_CURSOR);
			try {
				getModuleLoader().saveModifiedProjects();
			} catch (SaveResourceExceptionList e) {
				e.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("errors_during_saving"),
						FlexoLocalization.localizedForKey("errors_during_saving"));
			} catch (OperationCancelledException e) {
				// User pressed cancel.
				if (logger.isLoggable(Level.FINEST)) {
					logger.log(Level.FINEST, "Cancelled saving", e);
				}
			}
			FileMenu.this._controller.getFlexoFrame().setCursor(c);
		}

	}

	public class SaveAsProjectItem extends FlexoMenuItem {

		public SaveAsProjectItem() {
			super(new SaveAsProjectAction(), "save_project_as", null, getController(), true);
			setIcon(IconLibrary.SAVE_ICON);
		}

	}

	public class SaveAsProjectAction extends AbstractAction {
		public SaveAsProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getProjectLoader().saveAsProject(getController().getProject());
		}

	}

	public class SendProjectToFlexoServerItem extends FlexoMenuItem {

		public SendProjectToFlexoServerItem() {
			super(new SendProjectToFlexoServerAction(), "send_project_to_flexo_server", null, getController(), true);
			setIcon(IconLibrary.NETWORK_ICON);
		}

	}

	public class SendProjectToFlexoServerAction extends AbstractAction {
		public SendProjectToFlexoServerAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean isOperationConfirmed = saveForServerPreprocessing();
			if (!isOperationConfirmed) {
				return;
			}

			UploadPrjAction refresh = UploadPrjAction.actionType.makeNewAction(getController().getProject(), null, getController()
					.getEditor());
			refresh.doAction();
		}

	}

	// ==========================================================================
	// ============================= SaveProjectForServer =============================
	// ==========================================================================

	public class SaveProjectForServerItem extends FlexoMenuItem {

		public SaveProjectForServerItem() {
			super(new SaveProjectForServerAction(), "save_project_for_server", null, getController(), true);
			setIcon(IconLibrary.SAVE_ICON);
		}

	}

	private boolean saveForServerPreprocessing() {
		FlexoProject project = _controller.getProject();
		if (project.getImportedProcessLibrary() != null && project.getImportedProcessLibrary().size() > 0
				|| project.getImportedRoleList() != null && project.getImportedRoleList().size() > 0) {
			int i = FlexoController.confirmYesNoCancel(FlexoLocalization
					.localizedForKey("would_you_like_to_refresh_imported_objects_first") + "?");
			switch (i) {
			case JOptionPane.YES_OPTION:
				PPMWebServiceClient client = getController().getWSClient();
				if (client == null) {
					break;
				}
				boolean goOn = true;
				if (goOn && project.getImportedProcessLibrary() != null && project.getImportedProcessLibrary().size() > 0) {
					RefreshImportedProcessAction refresh = RefreshImportedProcessAction.actionType.makeNewAction(
							project.getImportedProcessLibrary(), null, getController().getEditor());
					refresh.setWebService(client.getWebService_PortType());
					refresh.setLogin(client.getLogin());
					refresh.setMd5Password(client.getEncriptedPWD());
					refresh.setAutomaticAction(true);
					refresh.doAction();
					goOn = refresh.hasActionExecutionSucceeded();
				}
				if (goOn && project.getImportedRoleList() != null && project.getImportedRoleList().size() > 0) {
					RefreshImportedRoleAction refresh = RefreshImportedRoleAction.actionType.makeNewAction(project.getImportedRoleList(),
							null, getController().getEditor());
					refresh.setWebService(client.getWebService_PortType());
					refresh.setLogin(client.getLogin());
					refresh.setMd5Password(client.getEncriptedPWD());
					refresh.setAutomaticAction(true);
					refresh.doAction();
					goOn = refresh.hasActionExecutionSucceeded();
				}
				if (goOn) {
					ValidationReport report = project.getImportedObjectValidationModel().validate(project.getWorkflow());
					if (report.getErrorNb() != 0 || report.getWarningNb() != 0) {
						getController().getConsistencyCheckWindow(true).setValidationReport(report);
						getController().getConsistencyCheckWindow(true).setModal(true);
						getController().getConsistencyCheckWindow(true).setVisible(true);
					}
				}
			case JOptionPane.NO_OPTION:
				break;
			default:
				return false;
			}
		}

		int i = FlexoController.confirmYesNoCancel(FlexoLocalization
				.localizedForKey("would_you_like_to_check_your_project_consistency_first") + "?");
		switch (i) {
		case JOptionPane.YES_OPTION:
			ValidateProject validate = ValidateProject.actionType.makeNewAction(project, null, getController().getEditor());
			validate.doAction();
			if (validate.getErrorsNb() > 0) {
				StringBuilder sb = new StringBuilder();
				if (validate.getIeValidationReport().getErrorNb() > 0) {
					sb.append(FlexoLocalization.localizedForKey("there_are_errors_in_your_components") + "\n");
				}
				if (validate.getWkfValidationReport().getErrorNb() > 0) {
					sb.append(FlexoLocalization.localizedForKey("there_are_errors_in_your_processes") + "\n");
				}
				if (validate.getDmValidationReport().getErrorNb() > 0) {
					sb.append(FlexoLocalization.localizedForKey("there_are_errors_in_your_data_model") + "\n");
				}
				if (validate.getDkvValidationReport().getErrorNb() > 0) {
					sb.append(FlexoLocalization.localizedForKey("there_are_errors_in_the_dkv") + "\n");
				}
				sb.append(FlexoLocalization.localizedForKey("would_you_like_to_continue_anyway") + "?");
				if (!FlexoController.confirmWithWarning(sb.toString())) {
					return false;
				}
			} else {
				if (GeneralPreferences.getNotifyValidProject()) {
					ParameterDefinition[] params = new ParameterDefinition[1];
					CheckboxParameter dontNotify = new CheckboxParameter("dont_notify", "dont_notify_if_project_is_valid", false);
					params[0] = dontNotify;
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(project,
							FlexoLocalization.localizedForKey("project_is_valid"), FlexoLocalization.localizedForKey("project_is_valid"),
							params);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						if (dontNotify.getValue()) {
							GeneralPreferences.setNotifyValidProject(false);
							GeneralPreferences.save();
						}
					} else {
						return false;
					}
				}
			}
			break;
		case JOptionPane.NO_OPTION:
			break;
		default:
			return false;
		}
		return true;
	}

	protected ProjectLoader getProjectLoader() {
		return getController().getProjectLoader();
	}

	public class SaveProjectForServerAction extends AbstractAction {
		public SaveProjectForServerAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean isOperationConfirmed = saveForServerPreprocessing();
			if (isOperationConfirmed) {
				getProjectLoader().saveProjectForServer(getController().getProject());
			}
		}

	}

	// ==========================================================================
	// ============================= ReloadProject =============================
	// ==========================================================================

	public class ReloadProjectItem extends FlexoMenuItem {

		public ReloadProjectItem() {
			super(new ReloadProjectAction(), "reload_project", null, getController(), true);
		}

	}

	public class ReloadProjectAction extends AbstractAction {
		public ReloadProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				getProjectLoader().reloadProject(getController().getProject());
			} catch (ProjectLoadingCancelledException e) {

			} catch (ProjectInitializerException e) {
				e.printStackTrace();
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
						+ e.getProjectDirectory().getAbsolutePath());
			}
		}

	}

	// ==========================================================================
	// ============================= InspectProject
	// =============================
	// ==========================================================================

	public class InspectProjectItem extends FlexoMenuItem {

		public InspectProjectItem() {
			super(new InspectProjectAction(), "inspect_project", null, getController(), true);
			setIcon(IconLibrary.INSPECT_ICON);
		}

	}

	public class InspectProjectAction extends AbstractAction {
		public InspectProjectAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// FlexoModule.getActiveModule().getFlexoController().showInspector();
			// FlexoModule.getActiveModule().getFlexoController().setCurrentInspectedObject(FlexoModule.getActiveModule().getFlexoController().getProject());
			FlexoController controller = getController();
			controller.getSelectionManager().setSelectedObject(controller.getProject());
			controller.showInspector();
			/*
			 * int state = controller.getInspectorWindow().getExtendedState(); state &= ~Frame.ICONIFIED;
			 * controller.getInspectorWindow().setExtendedState(state);
			 */
		}
	}

	// ==========================================================================
	// ============================= Quit Flexo
	// =================================
	// ==========================================================================

	public class QuitItem extends FlexoMenuItem {

		public QuitItem() {
			super(new QuitAction(), "quit", ToolBox.getPLATFORM() == ToolBox.WINDOWS ? KeyStroke.getKeyStroke(KeyEvent.VK_F4,
					InputEvent.ALT_MASK) : KeyStroke.getKeyStroke(KeyEvent.VK_Q, FlexoCst.META_MASK), getController(), true);
		}

	}

	public class QuitAction extends AbstractAction {
		public QuitAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			quit();
		}

	}

	// ==========================================================================
	// ============================= PageSetUP ================================
	// ==========================================================================

	public class PageSetUpItem extends FlexoMenuItem {

		public PageSetUpItem() {
			super(new PageSetUpAction(), "page_setup", null, getController(), true);
		}

	}

	public class PageSetUpAction extends AbstractAction {
		public PageSetUpAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			((PrintManagingController) _controller).getPrintManager().pageSetup();
		}
	}

}
