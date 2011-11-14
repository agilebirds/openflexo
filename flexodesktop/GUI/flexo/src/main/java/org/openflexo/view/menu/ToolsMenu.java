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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.br.view.BugReportViewerWindow;
import org.openflexo.br.view.NewBugReport;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.validation.ConsistencyCheckDialog;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.validation.ValidationFinishedNotification;
import org.openflexo.foundation.validation.ValidationInitNotification;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationNotification;
import org.openflexo.foundation.validation.ValidationProgressNotification;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationSecondaryInitNotification;
import org.openflexo.foundation.validation.ValidationSecondaryProgressNotification;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.LoggingConfigurationWindow;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.view.controller.FlexoController;

/**
 * Automatic builded 'Tools' menu for modules
 * 
 * @author sguerin
 */
public class ToolsMenu extends FlexoMenu {

	static final Logger logger = FlexoLogger.getLogger(ToolsMenu.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	public JMenuItem loggingItem;

	public JMenuItem localizedEditorItem;

	public JMenuItem rmItem;

	public JMenuItem submitBug;

	public JMenuItem brEditor;

	public JMenuItem repairProject;

	public JMenuItem timeTraveler;

	public LogConfiguratorItem logConfig;

	public SaveDocSubmissionItem saveDocSubmissions;

	public ToolsMenu(FlexoController controller) {
		super("tools", controller);
		addSpecificItems();
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			add(loggingItem = new LoggingItem());
		}
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			add(localizedEditorItem = new LocalizedEditorItem());
		}
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			add(rmItem = new ResourceManagerItem());
		}
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			addSeparator();
		}
		add(submitBug = new SubmitBugItem());
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			add(brEditor = new BREditorItem());
		}
		if ((ModuleLoader.allowsDocSubmission()) && (!ModuleLoader.isAvailable(Module.DRE_MODULE))) {
			addSeparator();
			add(saveDocSubmissions = new SaveDocSubmissionItem());
		}
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			addSeparator();
			add(repairProject = new RepairProjectItem());
		}
		add(timeTraveler = new TimeTraveler());
		addSeparator();
		if (!ModuleLoader.isCustomerRelease() && !ModuleLoader.isAnalystRelease()) {
			add(logConfig = new LogConfiguratorItem());
		}
	}

	public void addSpecificItems() {
		// No specific item here, please override this method when required
	}

	// ==========================================================================
	// ========================== Logging
	// =======================================
	// ==========================================================================

	public class LoggingItem extends FlexoMenuItem {

		public LoggingItem() {
			super(new LoggingAction(), "show_logging", KeyStroke.getKeyStroke(KeyEvent.VK_L, FlexoCst.META_MASK), getController(), true);
		}

	}

	public class LoggingAction extends AbstractAction {
		public LoggingAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FlexoLoggingManager.showLoggingViewer();
		}

	}

	// ==========================================================================
	// ========================== Localized Editor
	// ==============================
	// ==========================================================================

	public class LocalizedEditorItem extends FlexoMenuItem {

		public LocalizedEditorItem() {
			super(new LocalizedEditorAction(), "localized_editor", null, getController(), true);
		}

	}

	public class LocalizedEditorAction extends AbstractAction {
		public LocalizedEditorAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FlexoLocalization.showLocalizedEditor();
		}

	}

	// ===================================================
	// ================== Licence Manager ===============
	// ===================================================

	// ===================================================
	// ================== Log Config =====================
	// ===================================================

	public class LogConfiguratorItem extends FlexoMenuItem {

		public LogConfiguratorItem() {
			super(new LogConfiguratorAction(), "Configure logging...", null, getController(), true);
		}

	}

	public class LogConfiguratorAction extends AbstractAction {
		public LogConfiguratorAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new LoggingConfigurationWindow().askLoggingConfiguration();
		}

	}

	// ===================================================
	// ================== Resource Manager ===============
	// ===================================================

	public class ResourceManagerItem extends FlexoMenuItem {

		public ResourceManagerItem() {
			super(new ResourceManagerAction(), "resource_manager", null, getController(), true);
		}

	}

	public class ResourceManagerAction extends AbstractAction {
		public ResourceManagerAction() {
			super();
			if (getController().getProject() == null) {
				setEnabled(false);
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (getController().getProject() == null) {
				return;
			}
			ModuleLoader.getRMWindow(getController().getProject()).show();
		}

	}

	// ==========================================================================
	// ========================== Submit bug ==============================
	// ==========================================================================

	public class SubmitBugItem extends FlexoMenuItem {

		public SubmitBugItem() {
			super(new SubmitBugAction(), "submit_bug_report", null, getController(), true);
		}

	}

	public class SubmitBugAction extends AbstractAction {
		public SubmitBugAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			NewBugReport.newBugReport(FlexoModule.getActiveModule() != null ? FlexoModule.getActiveModule().getModule() : null);
		}

	}

	// ==========================================================================
	// ========================== BugReport Editor
	// ==============================
	// ==========================================================================

	public class BREditorItem extends FlexoMenuItem {

		public BREditorItem() {
			super(new BREditorAction(), "bug_reports_editor", null, getController(), true);
		}

	}

	public class BREditorAction extends AbstractAction {
		public BREditorAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new BugReportViewerWindow();
		}

	}

	// ==========================================================================
	// ========================== BugReport Editor
	// ==============================
	// ==========================================================================

	public class SaveDocSubmissionItem extends FlexoMenuItem {

		public SaveDocSubmissionItem() {
			super(new SaveDocSubmissionAction(), "save_doc_submission", null, getController(), true);
		}

	}

	public static class SaveDocSubmissionAction extends AbstractAction {
		public SaveDocSubmissionAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle(FlexoLocalization.localizedForKey("please_select_a_file"));
			chooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(".dsr");
				}

				@Override
				public String getDescription() {
					return FlexoLocalization.localizedForKey("doc_submission_report_files");
				}

			});
			if ((chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) && (chooser.getSelectedFile() != null)) {
				try {
					File savedFile;
					if (!chooser.getSelectedFile().getName().endsWith(".dsr")) {
						savedFile = new File(chooser.getSelectedFile().getParentFile(), chooser.getSelectedFile().getName() + ".dsr");
					} else {
						savedFile = chooser.getSelectedFile();
					}
					DocResourceManager drm = DocResourceManager.instance();
					drm.getSessionSubmissions().save(savedFile);
					drm.getSessionSubmissions().clear();
					FlexoController.notify(FlexoLocalization.localizedForKey("doc_submission_report_successfully_saved"));
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
			} else {
				// cancelled, return.
				return;
			}
		}

	}

	public class RepairProjectItem extends FlexoMenuItem {

		public RepairProjectItem() {
			super(new RepairProjectAction(), "repair_project", null, getController(), true);
		}

	}

	public class RepairProjectAction extends AbstractAction implements GraphicalFlexoObserver {
		public RepairProjectAction() {
			super();
			if (getController().getProject() == null) {
				setEnabled(false);
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Validation of project in progress...");
			}

			if (getController().getProject() == null) {
				return;
			}

			ValidationModel validationModel = getController().getProject().getProjectValidationModel();
			validationModel.addObserver(this);
			ValidationReport report = getController().getProject().validate(validationModel);
			validationModel.deleteObserver(this);

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Validation of project terminated");
			}

			getRepairProjectWindow(report).setVisible(true);
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof ValidationNotification) {
				if (dataModification instanceof ValidationInitNotification) {
					ValidationInitNotification initNotification = (ValidationInitNotification) dataModification;
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("validating") + " "
							+ initNotification.getRootObject().getFullyQualifiedName(), initNotification.getNbOfObjectToValidate());
				} else if (dataModification instanceof ValidationProgressNotification) {
					ValidationProgressNotification progressNotification = (ValidationProgressNotification) dataModification;
					ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("validating") + " "
							+ progressNotification.getValidatedObject().getFullyQualifiedName());
				} else if (dataModification instanceof ValidationSecondaryInitNotification) {
					ValidationSecondaryInitNotification initNotification = (ValidationSecondaryInitNotification) dataModification;
					ProgressWindow.resetSecondaryProgressInstance(initNotification.getNbOfRulesToApply());
				} else if (dataModification instanceof ValidationSecondaryProgressNotification) {
					ValidationSecondaryProgressNotification progressNotification = (ValidationSecondaryProgressNotification) dataModification;
					ProgressWindow.setSecondaryProgressInstance(progressNotification.getValidationRule().getLocalizedName());
				} else if (dataModification instanceof ValidationFinishedNotification) {
					ProgressWindow.hideProgressWindow();
				}

			}
		}

	}

	private ConsistencyCheckDialog repairProjectWindow = null;

	protected ConsistencyCheckDialog getRepairProjectWindow(ValidationReport report) {
		if (repairProjectWindow == null || repairProjectWindow.isDisposed()) {
			repairProjectWindow = new ConsistencyCheckDialog(null, report, FlexoLocalization.localizedForKey("project_repairing"));
		} else {
			repairProjectWindow.setValidationReport(report);
		}
		return repairProjectWindow;
	}

	public class TimeTraveler extends FlexoMenuItem {

		public TimeTraveler() {
			super(new TimeTravelAction(), "revert_to_auto_saved_version", null, getController(), true);
		}

	}

	public class TimeTravelAction extends AbstractAction {
		public TimeTravelAction() {
			super();
			if (getController().getProject() == null) {
				setEnabled(false);
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ModuleLoader.isTimeTravelingAvailable()) {
				ModuleLoader.showTimeTravelerDialog();
			} else {
				if (FlexoController.confirm(FlexoLocalization.localizedForKey("time_traveling_is_disabled") + ". "
						+ FlexoLocalization.localizedForKey("would_you_like_to_activate_it_now?"))) {
					GeneralPreferences.setAutoSaveEnabled(true);
					FlexoPreferences.savePreferences(true);
					ModuleLoader.showTimeTravelerDialog();
				}
			}
		}

	}
}
