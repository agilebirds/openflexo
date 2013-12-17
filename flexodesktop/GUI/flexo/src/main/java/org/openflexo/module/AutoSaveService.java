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
package org.openflexo.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.GeneralPreferences;
import org.openflexo.components.AskParametersPanel;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoAutoSaveThread.FlexoAutoSaveFile;
import org.openflexo.prefs.PreferencesHaveChanged;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

public class AutoSaveService implements FlexoObserver {

	private static final Logger logger = Logger.getLogger(AutoSaveService.class.getPackage().getName());

	private FlexoAutoSaveThread autoSaveThread = null;

	private final FlexoProject project;

	private final ProjectLoader projectLoader;

	public AutoSaveService(ProjectLoader projectLoader, FlexoProject project) {
		super();
		this.projectLoader = projectLoader;
		this.project = project;
		GeneralPreferences.getPreferences().addObserver(this);
	}

	public void close() {
		GeneralPreferences.getPreferences().deleteObserver(this);
		stop();
	}

	public FlexoProject getProject() {
		return project;
	}

	public boolean isRunning() {
		return autoSaveThread != null;
	}

	public void start() {
		if (autoSaveThread != null && autoSaveThread.isAlive()) {
			return;
		}
		if (GeneralPreferences.getAutoSaveEnabled() && autoSaveThread == null) {
			autoSaveThread = new FlexoAutoSaveThread(project);
			setAutoSaveLimit();
			setAutoSaveInterval();
			autoSaveThread.start();
		}
	}

	public void stop() {
		if (autoSaveThread != null) {
			autoSaveThread.setRun(false);
			if (autoSaveThread.getState() == Thread.State.TIMED_WAITING) {
				autoSaveThread.interrupt();
			}
			autoSaveThread = null;
		}
	}

	private void pause() {
		if (autoSaveThread != null) {
			autoSaveThread.pause();
		}
	}

	private void resume() {
		if (autoSaveThread != null) {
			autoSaveThread.unpause();
		}
	}

	private void setAutoSaveLimit() {
		if (autoSaveThread != null) {
			autoSaveThread.setNumberOfIntermediateSave(GeneralPreferences.getAutoSaveLimit());
		}
	}

	private void setAutoSaveInterval() {
		if (autoSaveThread != null) {
			autoSaveThread.setSleepTime(GeneralPreferences.getAutoSaveInterval() * 60 * 1000);
		}
	}

	public File getAutoSaveDirectory() {
		if (autoSaveThread != null) {
			return autoSaveThread.getTempDirectory();
		} else {
			return null;
		}
	}

	public void showTimeTravelerDialog() {
		pause();
		final FlexoDialog dialog = new FlexoDialog(FlexoFrame.getActiveFrame(), true);
		final ParameterDefinition[] parameters = new ParameterDefinition[2];
		parameters[0] = new ReadOnlyTextFieldParameter("directory", "save_directory", autoSaveThread.getTempDirectory().getAbsolutePath());
		parameters[1] = new PropertyListParameter<FlexoAutoSaveFile>("backUps", FlexoLocalization.localizedForKey("back-ups"),
				autoSaveThread.getProjects(), 20, 12);
		((PropertyListParameter) parameters[1]).addReadOnlyTextFieldColumn("creationDateAsAString", "creation_date", 100, true);
		((PropertyListParameter) parameters[1]).addReadOnlyTextFieldColumn("offset", "offset", 100, true);
		((PropertyListParameter) parameters[1]).addReadOnlyTextFieldColumn("path", "path", 450, true);
		/*parameters[1] = new CheckboxParameter("autoSaveEnabled", FlexoLocalization.localizedForKey("enable_auto_save"),GeneralPreferences.getAutoSaveEnabled());
		parameters[2] = new IntegerParameter("autoSaveInterval", FlexoLocalization.localizedForKey("auto_save_interval (minutes)"),GeneralPreferences.getAutoSaveInterval());
		parameters[2].setDepends("autoSaveEnabled");
		parameters[2].setConditional("autoSaveEnabled=true");
		parameters[3] = new IntegerParameter("autoSaveLimit", FlexoLocalization.localizedForKey("limit (0=no_limit)"),GeneralPreferences.getAutoSaveLimit());
		parameters[3].setDepends("autoSaveEnabled");
		parameters[3].setConditional("autoSaveEnabled=true");*/
		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
		north.setBackground(Color.WHITE);
		JLabel label = new JLabel("<html>" + FlexoLocalization.localizedForKey("time_travel_info") + "</html>",
				IconLibrary.TIME_TRAVEL_ICON, SwingConstants.LEFT);
		north.add(label);
		AskParametersPanel panel = new AskParametersPanel(project, parameters);
		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton cancel = new JButton(FlexoLocalization.localizedForKey("cancel"));
		cancel.addActionListener(new ActionListener() {
			/**
			 * Overrides actionPerformed
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				resume();
			}
		});
		JButton ok = new JButton(FlexoLocalization.localizedForKey("restore"));
		ok.addActionListener(new ActionListener() {
			/**
			 * Overrides actionPerformed
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				FlexoAutoSaveFile autoSaveFile = (FlexoAutoSaveFile) ((PropertyListParameter) parameters[1]).getSelectedObject();
				if (autoSaveFile != null) {
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("are_you_sure_that_you_want_to_revert_to_that_version?"))) {
						try {
							ProgressWindow.showProgressWindow(FlexoFrame.getActiveFrame(),
									FlexoLocalization.localizedForKey("project_restoration"), 4);
							restoreAutoSaveProject(autoSaveFile, ProgressWindow.instance());
						} catch (IOException e1) {
							e1.printStackTrace();
							FlexoController.showError(FlexoLocalization
									.localizedForKey("an_error_occured_while_trying_to_restore_your_project")
									+ "\n"
									+ project.getProjectDirectory().getAbsolutePath());
						}
					} else {
						resume();
					}
				} else {
					resume();
				}

			}
		});
		dialog.addWindowListener(new WindowAdapter() {
			/**
			 * Overrides windowClosing
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				resume();
				super.windowClosing(e);
			}
		});
		south.add(ok);
		south.add(cancel);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(panel);
		dialog.getContentPane().add(north, BorderLayout.NORTH);
		dialog.getContentPane().add(south, BorderLayout.SOUTH);
		dialog.setTitle(FlexoLocalization.localizedForKey("time_traveler"));
		dialog.validate();
		dialog.pack();
		dialog.show();
	}

	public void restoreAutoSaveProject(FlexoAutoSaveFile autoSaveFile, FlexoProgress progress) throws IOException {
		File projectDirectory = project.getProjectDirectory();
		File dest = null;
		int attempt = 0;
		while (dest == null || dest.exists()) {
			dest = new File(projectDirectory.getParentFile(), projectDirectory.getName() + ".restore" + (attempt == 0 ? "" : "." + attempt));
			attempt++;
		}
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("creating_restore_project_at") + " " + dest.getAbsolutePath());
		}
		FileUtils.copyContentDirToDir(projectDirectory, dest);
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("closing_project"));
		}
		projectLoader.closeProject(project);
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("deleting_project"));
		}
		FileUtils.deleteDir(projectDirectory);
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("restoring_project"));
		}
		FileUtils.copyContentDirToDir(autoSaveFile.getDirectory(), projectDirectory);
		if (progress != null) {
			progress.hideWindow();
		}
		try {
			projectLoader.loadProject(projectDirectory);
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == GeneralPreferences.getPreferences()) {
			if (dataModification instanceof PreferencesHaveChanged) {
				String key = ((PreferencesHaveChanged) dataModification).propertyName();
				if (GeneralPreferences.AUTO_SAVE_ENABLED.equals(key)) {
					if (GeneralPreferences.isAutoSavedEnabled()) {
						start();
					} else {
						stop();
					}
				} else if (GeneralPreferences.AUTO_SAVE_INTERVAL.equals(key)) {
					setAutoSaveInterval();
				} else if (GeneralPreferences.AUTO_SAVE_LIMIT.equals(key)) {
					setAutoSaveLimit();
				}
			}
		}
	}
}
