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

import java.util.EventObject;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.openflexo.cgmodule.GeneratorPreferences;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.GenerateWAR;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateWARInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	protected class WARBuildListener implements BuildListener {

		private GenerateWAR action;

		private boolean currentTargetIsDist = false;

		public WARBuildListener(GenerateWAR action) {
			this.action = action;
		}

		@Override
		public void buildFinished(BuildEvent arg0) {

		}

		@Override
		public void buildStarted(BuildEvent arg0) {
		}

		@Override
		public void messageLogged(BuildEvent arg0) {
			if (arg0.getPriority() == Project.MSG_INFO) {
				action.getProjectGenerator().log(arg0.getMessage());
			} else if (arg0.getPriority() == Project.MSG_WARN) {
				action.getProjectGenerator().warn(arg0.getMessage());
			} else if (arg0.getPriority() == Project.MSG_ERR) {
				action.getProjectGenerator().err(arg0.getMessage());
			} else if (currentTargetIsDist) {
				ProgressWindow.setSecondaryProgressInstance(arg0.getMessage());
			}
		}

		@Override
		public void targetFinished(BuildEvent arg0) {

		}

		@Override
		public void targetStarted(BuildEvent arg0) {
			if (arg0.getTarget().getName().equals("dist")) {
				currentTargetIsDist = true;
			} else {
				currentTargetIsDist = false;
			}
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("performing_target") + " " + arg0.getTarget().getName());
			if (!currentTargetIsDist) {
				if (arg0.getTarget().getTasks() != null) {
					ProgressWindow.resetSecondaryProgressInstance(arg0.getTarget().getTasks().length);
				}
			} else {
				ProgressWindow.resetSecondaryProgressInstance(22355);
			}

		}

		@Override
		public void taskFinished(BuildEvent arg0) {

		}

		@Override
		public void taskStarted(BuildEvent arg0) {
			if (!currentTargetIsDist) {
				ProgressWindow.setSecondaryProgressInstance(FlexoLocalization.localizedForKey("current_task") + " "
						+ arg0.getTask().getTaskName());
			}
		}

		/**
		 * 
		 */
		public void report() {
		}
	}

	protected WARBuildListener listener;

	GenerateWARInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(GenerateWAR.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateWAR> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateWAR>() {
			@Override
			public boolean run(EventObject e, GenerateWAR action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (action.getFocusedObject().getWarDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (!action.getRepository().getWarRepository().getDirectory().exists()) {
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory") + " "
							+ action.getRepository().getWarRepository().getDirectory().getAbsolutePath() + " "
							+ FlexoLocalization.localizedForKey("does_not_exist") + "\n"
							+ FlexoLocalization.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
						action.getRepository().getWarRepository().getDirectory().mkdirs();
					} else {
						return false;
					}
				}
				/*Class<?> javacClass = null;
				try {
					javacClass = Class.forName("com.sun.tools.javac.Main");
				} catch (ClassNotFoundException e1) {
				}
				if (javacClass == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_jdk_found_impossible_to_compile"));
					return false;
				}*/
				if (action.getRepository().getWarDirectory().exists() && !action.getRepository().getWarDirectory().canWrite()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("permission_denied_for ")
							+ action.getRepository().getWarDirectory().getAbsolutePath());
					return false;
				}
				if (FlexoController.confirm(FlexoLocalization
						.localizedForKey("WAR_generation_is_based_on_code_generated_on_disk._Is_your_code_on_disk_ready_to_be_packaged?"))) {
					action.setSaveBeforeGenerating(GeneratorPreferences.getSaveBeforeGenerating());
					action.getProjectGenerator().startHandleLogs();
					action.getProjectGenerator().addBuildListener(listener = new WARBuildListener(action));
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("build_war"), 50);
					action.setFlexoProgress(ProgressWindow.instance());
					return true;
				}
				return false;
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateWAR> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateWAR>() {
			@Override
			public boolean run(EventObject e, GenerateWAR action) {
				listener.report();
				action.getProjectGenerator().removeBuildListener(listener);
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GenerateWAR> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<GenerateWAR>() {
			@Override
			public boolean handleException(FlexoException exception, GenerateWAR action) {
				action.getProjectGenerator().removeBuildListener(listener);
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();
				if (exception instanceof GenerationException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((GenerationException) exception).getLocalizedMessage());
					return true;
				}
				exception.printStackTrace();
				return false;
			}
		};
	}

}
