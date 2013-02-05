package org.openflexo.builders;

import java.io.File;
import java.io.IOException;

import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.utils.FlexoBuilderEditor;
import org.openflexo.builders.utils.FlexoBuilderListener;
import org.openflexo.builders.utils.FlexoBuilderProjectReferenceLoader;
import org.openflexo.builders.utils.FlexoBuilderResourceCenterService;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.xml.XMLSerializationService;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public abstract class FlexoExternalMainWithProject extends FlexoExternalMain {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(FlexoExternalMainWithProject.class
			.getPackage().getName());

	private static final int PROJECT_LOADING_FAILURE = -8;
	private static final int PROJECT_CANCELED_FAILURE = -18;

	public static final String SERVER_URL = "serverURL=";
	public static final String SERVER_LOGIN = "serverLogin=";
	public static final String SERVER_PASSWORD = "serverPassword=";

	protected java.io.File projectDirectory;

	protected FlexoBuilderEditor editor;

	protected FlexoProject project;
	private ApplicationContext applicationContext;

	protected String serverURL;
	protected String serverLogin;
	protected String serverPassword;

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
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(SERVER_URL)) {
					serverURL = args[i].substring(SERVER_URL.length());
				} else if (args[i].startsWith(SERVER_LOGIN)) {
					serverLogin = args[i].substring(SERVER_LOGIN.length());
				} else if (args[i].startsWith(SERVER_PASSWORD)) {
					serverPassword = args[i].substring(SERVER_PASSWORD.length());
				} else if (projectDirectory == null) {

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
					}
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
			protected XMLSerializationService createXMLSerializationService() {
				return XMLSerializationService.createInstance();
			}

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
				return FlexoBuilderResourceCenterService.getNewInstance(getWorkingDir());
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
				return new FlexoBuilderProjectReferenceLoader(FlexoExternalMainWithProject.this, getProjectLoader(), serverURL,
						serverLogin, serverPassword);
			}

			@Override
			protected TechnologyAdapterService createTechnologyAdapterService(FlexoResourceCenterService resourceCenterService) {
				TechnologyAdapterService returned = DefaultTechnologyAdapterService.getNewInstance(resourceCenterService);
				return returned;
			}

			@Override
			protected TechnologyAdapterControllerService createTechnologyAdapterControllerService() {
				return null;
			}

			@Override
			protected ViewPointLibrary createViewPointLibraryService() {
				return new ViewPointLibrary();
			}

			@Override
			protected InformationSpace createInformationSpace() {
				return new InformationSpace();
			}

		};

		try {
			editor = (FlexoBuilderEditor) applicationContext.getProjectLoader().loadProject(projectDirectory);
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
	public void handleActionFailed(FlexoAction<?, ? extends FlexoObject, ? extends FlexoObject> action) {
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

}
