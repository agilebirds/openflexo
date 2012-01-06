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
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.ColorCst;
import org.openflexo.components.AskParametersPanel;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorObserver;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * @author gpolet
 * 
 */
public class FlexoAutoSaveThread extends Thread {

	private static final Logger logger = FlexoLogger.getLogger(FlexoAutoSaveThread.class.getPackage().getName());

	private static final String AUTO_SAVE_FILE_NAME_INFO = ".autosave";

	private static final String AUTO_SAVE_FILE_EXTENSION = ".fas"; // fas stands for FlexoAutoSave

	protected static final DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

	/**
	 * The time to sleep between the automatic save operations
	 */
	private long sleepTime = 5 * 60 * 1000; // 5 minutes

	/**
	 * The number of save to perform.
	 */
	private int numberOfIntermediateSave = 12;

	/**
	 * A queue that lists the different files already used. This queue is managed with the FIFO policy.
	 */
	private LinkedList<FlexoAutoSaveFile> projects;

	/**
	 * The project on which this thread works
	 */
	private FlexoProject project;

	/**
	 * The root directory for the specified project containing all the intermediate project save. For example, you will have tempDirectory
	 * that is "C:\Documents and Settings\UserName\Local Settings\Temp\A Flexo Temp Directory" and the <code>projects</code> list will
	 * contain files like:
	 * <ul>
	 * <li>"C:\Documents and Settings\UserName\Local Settings\Temp\A Flexo Temp Directory\A Project.save.1"
	 * <li>"C:\Documents and Settings\UserName\Local Settings\Temp\A Flexo Temp Directory\A Project.save.2"
	 * <li>"C:\Documents and Settings\UserName\Local Settings\Temp\A Flexo Temp Directory\A Project.save.3"
	 * <li>"C:\Documents and Settings\UserName\Local Settings\Temp\A Flexo Temp Directory\A Project.save.4"...
	 * </ul>
	 */
	private File tempDirectory;

	private boolean run = true;

	/**
	 *
	 */
	public FlexoAutoSaveThread(FlexoProject project) {
		super("Auto-save thread for " + project.getName());
		this.project = project;
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
		projects = new LinkedList<FlexoAutoSaveFile>();
		initFromFile();
	}

	private File getNextSaveDirectory() {
		int attempt = 1;
		File nextSaveFile = null;
		while (nextSaveFile == null || nextSaveFile.exists()) {
			nextSaveFile = new File(tempDirectory, project.getName() + AUTO_SAVE_FILE_EXTENSION + "." + attempt);
			attempt++;
		}
		nextSaveFile.mkdirs();
		return nextSaveFile;
	}

	private File getAutoSafeFileInfo() {
		return new File(project.getProjectDirectory(), AUTO_SAVE_FILE_NAME_INFO);
	}

	/**
	 *
	 */
	private void initFromFile() {
		try {
			String content = FileUtils.fileContents(getAutoSafeFileInfo());
			tempDirectory = new File(content.trim());
			if (!tempDirectory.exists() || !content.startsWith(System.getProperty("java.io.tmpdir"))
					&& !content.startsWith(new File(System.getProperty("java.io.tmpdir")).getCanonicalPath())) {
				tempDirectory = getNewTempDirectory();
			}
		} catch (IOException e) {
			if (logger.isLoggable(Level.FINEST)) {
				logger.log(Level.FINEST, "IO exception while opening " + AUTO_SAVE_FILE_NAME_INFO + " file.", e);
			}
			tempDirectory = getNewTempDirectory();
		}
		File files[] = tempDirectory.listFiles(new FilenameFilter() {
			/**
			 * Overrides accept
			 * 
			 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
			 */
			@Override
			public boolean accept(File dir, String name) {
				return name.indexOf(AUTO_SAVE_FILE_EXTENSION) > -1;
			}
		});
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					projects.add(new FlexoAutoSaveFile(file, new Date(file.lastModified())));
				}
			}
		}
		Collections.sort(projects, new Comparator<FlexoAutoSaveFile>() { // This comparator will make oldest files first and newer ones last
					// in the queue
					/**
					 * Overrides compare
					 * 
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					@Override
					public int compare(FlexoAutoSaveFile o1, FlexoAutoSaveFile o2) {
						if (o1.lastModified() < o2.lastModified()) {
							return -1;
						} else if (o1.lastModified() > o2.lastModified()) {
							return 1;
						} else {
							return 0;
						}
					}
				});
	}

	/**
	 * @return
	 */
	private File getNewTempDirectory() {
		tempDirectory = null;
		File tmpdir = new File(System.getProperty("java.io.tmpdir"));
		try {
			tmpdir = tmpdir.getCanonicalFile();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		int attempt = 0;
		while (tempDirectory == null || tempDirectory.exists()) {
			tempDirectory = new File(tmpdir, project.getName() + (attempt > 0 ? attempt : ""));
			attempt++;
		}
		try {
			tempDirectory = tempDirectory.getCanonicalFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		tempDirectory.mkdirs();
		try {
			FileUtils.saveToFile(getAutoSafeFileInfo(), tempDirectory.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return tempDirectory;
	}

	/**
	 * Overrides run
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (run) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "I got interrupted, probably because the user has changed the sleep time", e);
				}
				continue;
			}
			boolean needsSave = false;
			if (projects.size() == 0) {
				needsSave = true;
			} else {
				Date lastAutoSave = projects.getLast().getCreationDate();
				for (FlexoResource<? extends FlexoResourceData> resource : project) {
					if (resource instanceof FlexoStorageResource && resource.getLastUpdate().after(lastAutoSave)) {
						needsSave = true;
						break;
					}
				}
			}
			if (!needsSave) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("project has not changed since last auto-save: " + formatter.format(projects.getLast().getCreationDate()));
				}
				continue;
			}
			try {
				boolean saveActionSuccess = true;
				File nextSaveDirectory = getNextSaveDirectory();
				try {
					project.saveAs(nextSaveDirectory, null, null, false, true);
				} catch (SaveResourceException e) {
					saveActionSuccess = false;
					e.printStackTrace();
				} catch (Exception e) {
					saveActionSuccess = false;
					e.printStackTrace();
				}
				if (saveActionSuccess) {
					projects.add(new FlexoAutoSaveFile(nextSaveDirectory, new Date()));
				} else {
					SwingUtilities.invokeLater(new AutoSaveActionFailed());
				}
				if (projects.size() >= numberOfIntermediateSave && numberOfIntermediateSave > 0) {
					while (projects.size() > numberOfIntermediateSave) {
						FlexoAutoSaveFile toRemove = projects.removeFirst();// First in First out policy
						FileUtils.deleteDir(toRemove.getDirectory());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void restoreAutoSaveProject(FlexoAutoSaveFile autoSaveFile, FlexoProgress progress) throws IOException {
		Module module = FlexoModule.getActiveModule().getModule();
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
		ProjectLoader.instance().closeCurrentProject();
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
			InteractiveFlexoEditor editor = ProjectLoader.instance().loadProject(projectDirectory);
			getModuleLoader().openProjectWithModule(editor, module);
		} catch (ModuleLoadingException e) {
			logger.severe("This shouldn't append since module is already loaded." + e.getMessage());
			e.printStackTrace();
			FlexoController.notify("This shouldn't append since module is already loaded." + e.getMessage());
		} catch (ProjectLoadingCancelledException e) {
			return;
		}
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	public FlexoProject getProject() {
		return project;
	}

	public int getNumberOfIntermediateSave() {
		return numberOfIntermediateSave;
	}

	public void setNumberOfIntermediateSave(int numberOfIntermediateSave) {
		this.numberOfIntermediateSave = numberOfIntermediateSave;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
		if (getState() == State.TIMED_WAITING) {
			this.interrupt();
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public List<File> getSavedFiles() {
		return (List<File>) projects.clone();
	}

	public static class AutoSaveActionFailed implements Runnable {
		/**
		 * Overrides run
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			FlexoController.showError(
					FlexoLocalization.localizedForKey("auto_save_action_failed"),
					FlexoLocalization.localizedForKey("auto_save_action_could_not_be_performed")
							+ "\n"
							+ FlexoLocalization
									.localizedForKey("verify_that_your_disk_is_not_full_and_that_you_can_write_in_the_temp_directory."));
		}
	}

	public static class FlexoAutoSaveFile extends KVCObject implements InspectableObject {

		private File directory;

		private Date creationDate;

		/**
		 *
		 */
		public FlexoAutoSaveFile(File directory, Date creationDate) {
			this.directory = directory;
			this.creationDate = creationDate;
		}

		public String getName() {
			return directory.getName();
		}

		public long lastModified() {
			return creationDate.getTime();
		}

		public File getDirectory() {
			return directory;
		}

		public String getPath() {
			return getDirectory().getAbsolutePath();
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public String getCreationDateAsAString() {
			return formatter.format(creationDate);
		}

		public String getOffset() {
			long current = System.currentTimeMillis();
			long offset = current - creationDate.getTime();
			if (offset < 60 * 60 * 1000) {
				return FlexoLocalization.localizedForKeyWithParams("($minutes) minutes_ago", this);
			} else if (offset < 24 * 60 * 60 * 1000) {
				return FlexoLocalization.localizedForKeyWithParams("($hours) hours_ago_and_($minutesOverHours)_minutes", this);
			} else {
				return formatter.format(creationDate);
			}
		}

		public String minutes() {
			long offset = System.currentTimeMillis() - creationDate.getTime();
			return String.valueOf(Math.round(offset / (60 * 1000)));
		}

		public String minutesOverHours() {
			long offset = System.currentTimeMillis() - creationDate.getTime();
			return String.valueOf(offset / (60 * 1000) % 60);
		}

		public String hours() {
			long offset = System.currentTimeMillis() - creationDate.getTime();
			return String.valueOf(Math.round(offset / (60 * 60 * 1000)));
		}

		/**
		 * Overrides addInspectorObserver
		 * 
		 * @see org.openflexo.inspector.InspectableObject#addInspectorObserver(org.openflexo.inspector.InspectorObserver)
		 */
		@Override
		public void addInspectorObserver(InspectorObserver obs) {

		}

		/**
		 * Overrides deleteInspectorObserver
		 * 
		 * @see org.openflexo.inspector.InspectableObject#deleteInspectorObserver(org.openflexo.inspector.InspectorObserver)
		 */
		@Override
		public void deleteInspectorObserver(InspectorObserver obs) {

		}

		/**
		 * Overrides getInspectorName
		 * 
		 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
		 */
		@Override
		public String getInspectorName() {
			return null;
		}

		/**
		 * Overrides isDeleted
		 * 
		 * @see org.openflexo.inspector.InspectableObject#isDeleted()
		 */
		@Override
		public boolean isDeleted() {
			return false;
		}

		@Override
		public String getInspectorTitle() {
			return null;
		}

		@Override
		public Vector<TabModel> inspectionExtraTabs() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	private AutoSaveService getAutoSaveService() {
		return AutoSaveService.instance();
	}

	public void showTimeTravelerDialog() {
		getAutoSaveService().stopAutoSaveThread();
		final FlexoDialog dialog = new FlexoDialog(FlexoModule.getActiveModule() != null ? FlexoModule.getActiveModule().getFlexoFrame()
				: null, true);
		final ParameterDefinition[] parameters = new ParameterDefinition[2];
		parameters[0] = new ReadOnlyTextFieldParameter("directory", "save_directory", tempDirectory.getAbsolutePath());
		parameters[1] = new PropertyListParameter<FlexoAutoSaveFile>("backUps", FlexoLocalization.localizedForKey("back-ups"), projects,
				20, 12);
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
		south.setBackground(ColorCst.GUI_BACK_COLOR);
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
				getAutoSaveService().startAutoSaveThread();
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
							ProgressWindow.showProgressWindow(null, FlexoLocalization.localizedForKey("project_restoration"), 4);
							if (FlexoModule.getActiveModule() != null) {
								ProgressWindow.instance().centerOnFrame(FlexoModule.getActiveModule().getFlexoFrame());
							}
							restoreAutoSaveProject(autoSaveFile, ProgressWindow.instance());
						} catch (IOException e1) {
							e1.printStackTrace();
							FlexoController.showError(FlexoLocalization
									.localizedForKey("an_error_occured_while_trying_to_restore_your_project")
									+ "\n"
									+ project.getProjectDirectory().getAbsolutePath());
						}
					} else {
						getAutoSaveService().startAutoSaveThread();
					}
				} else {
					getAutoSaveService().startAutoSaveThread();
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
				getAutoSaveService().startAutoSaveThread();
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

	public File getTempDirectory() {
		return tempDirectory;
	}

}
