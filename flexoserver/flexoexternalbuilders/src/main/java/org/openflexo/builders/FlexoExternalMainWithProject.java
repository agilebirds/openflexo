package org.openflexo.builders;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.utils.FlexoBuilderEditor;
import org.openflexo.builders.utils.FlexoBuilderListener;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.toolbox.FileUtils;

public abstract class FlexoExternalMainWithProject extends FlexoExternalMain {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(FlexoExternalMainWithProject.class
			.getPackage().getName());

	private static final int PROJECT_LOADING_FAILURE = -8;
	private static final int PROJECT_CANCELED_FAILURE = -18;

	protected java.io.File projectDirectory;

	protected FlexoBuilderEditor editor;

	protected FlexoProject project;
	private ApplicationContext applicationContext;

	public FlexoExternalMainWithProject() {

	}

	/**
	 * This is only code by test classes to dereference project from everywhere.
	 */
	public void close() {
		applicationContext.getProjectLoader().closeProject(project);
		applicationContext.getModuleLoader().closeAllModulesWithoutConfirmation();
		editor = null;
		project = null;
	}

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		GeneralPreferences.setAutoSaveEnabled(false);
		GeneralPreferences.save();
		if (args.length > 0) {
			for (int i = 0; i < args.length && projectDirectory == null; i++) {
				if (args[i].startsWith("\"")) {
					args[i] = args[i].substring(1);
				}
				if (args[i].endsWith("\"")) {
					args[i] = args[i].substring(0, args[i].length() - 1);
				}
				projectDirectory = new File(args[i]);
				if (!projectDirectory.exists()) {
					projectDirectory = null;
				} else if (!projectDirectory.getName().toLowerCase().endsWith(".prj")) {
					projectDirectory = searchProjectDirectory(projectDirectory);
				} else {
					break;// Project found!
				}
			}
		}
		if (projectDirectory == null) {
			throw new MissingArgumentException("Project directory");
		}
		if (getWorkingDir() == null || !getWorkingDir().exists() || !getWorkingDir().canWrite()) {
			try {
				workingDir = FileUtils.createTempDirectory(getClass().getSimpleName(), "");
			} catch (IOException e) {
				e.printStackTrace();
				setExitCodeCleanUpAndExit(LOCAL_IO_EXCEPTION);
				return;
			}
		}
		applicationContext = new ApplicationContext() {

			@Override
			public FlexoEditor makeFlexoEditor(FlexoProject project) {
				FlexoBuilderEditor builderEditor = new FlexoBuilderEditor(FlexoExternalMainWithProject.this, project);
				builderEditor.setFactory(new FlexoProgressFactory() {
					@Override
					public FlexoProgress makeFlexoProgress(String title, int steps) {
						return new FlexoBuilderProgress(title, steps);
					}
				});
				return builderEditor;
			}

			@Override
			protected FlexoResourceCenterService createResourceCenterService() {
				return DefaultResourceCenterService.getNewInstance(getWorkingDir());
			}

			@Override
			protected FlexoEditor createApplicationEditor() {
				return new DefaultFlexoEditor(null);
			}

			@Override
			public ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory) {
				return null;
			}

			@Override
			protected FlexoProjectReferenceLoader createProjectReferenceLoader() {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Project reference loader is not implemented for external builders");
				}
				return null;
			}
		};
		try {
			editor = loadProject(projectDirectory);
		} catch (ProjectLoadingCancelledException e) {
			// Should not happen in external builder
			e.printStackTrace();
			setExitCodeCleanUpAndExit(PROJECT_CANCELED_FAILURE);
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			System.exit(PROJECT_LOADING_FAILURE);
		}
		project = editor.getProject();
		project.getGeneratedCode().setFactory(editor);
		project.getGeneratedDoc().setFactory(editor);

	}

	@Override
	public void handleActionFailed(FlexoAction<?, ? extends FlexoModelObject, ? extends FlexoModelObject> action) {
		handleActionFailed(action, projectDirectory);
	}

	protected class FlexoBuilderProgress implements FlexoProgress {
		private static final boolean REPORT = false;

		public void reportMainStepMessage(String message) {
			if (!REPORT) {
				return;
			}
			writeToConsole(FlexoBuilderListener.MAIN_STEP_START_TAG);
			writeToConsole(message);
			writeToConsole(FlexoBuilderListener.MAIN_STEP_END_TAG);
		}

		public void reportMainStepCount(int count) {
			if (!REPORT) {
				return;
			}
			writeToConsole(FlexoBuilderListener.MAIN_STEP_COUNT_START_TAG);
			writeToConsole(String.valueOf(count));
			writeToConsole(FlexoBuilderListener.MAIN_STEP_COUNT_END_TAG);
		}

		public void reportSubStepMessage(String message) {
			if (!REPORT) {
				return;
			}
			writeToConsole(FlexoBuilderListener.SUB_STEP_START_TAG);
			writeToConsole(message);
			writeToConsole(FlexoBuilderListener.SUB_STEP_END_TAG);
		}

		public void reportSubStepCount(int count) {
			if (!REPORT) {
				return;
			}
			writeToConsole(FlexoBuilderListener.SUB_STEP_COUNT_START_TAG);
			writeToConsole(String.valueOf(count));
			writeToConsole(FlexoBuilderListener.SUB_STEP_COUNT_END_TAG);
		}

		public FlexoBuilderProgress(String title, int steps) {
			reportMainStepMessage(title);
			reportMainStepCount(steps);
		}

		@Override
		public void hideWindow() {

		}

		@Override
		public void resetSecondaryProgress(int steps) {
			reportSubStepCount(steps);
		}

		@Override
		public void setProgress(String stepName) {
			reportMainStepMessage(stepName);
		}

		@Override
		public void setSecondaryProgress(String stepName) {
			reportSubStepMessage(stepName);
		}

	}

	public FlexoBuilderEditor loadProject(File projectDirectory) throws ProjectLoadingCancelledException, ProjectInitializerException {
		return (FlexoBuilderEditor) FlexoResourceManager.initializeExistingProject(projectDirectory, applicationContext,
				getResourceCenterService());
	}

}
