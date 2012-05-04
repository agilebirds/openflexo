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
package org.openflexo.ant;

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoActionUndoInitializer;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.UserType;
import org.openflexo.toolbox.ResourceLocator;

public class GenerateHelpSetTask extends Task {
	private static final Logger logger = FlexoLogger.getLogger(GenerateHelpSetTask.class.getPackage().getName());
	private String baseName;
	private Vector<HelpLanguage> languages;

	public void setResourceDir(File resourceDir) {
		if (resourceDir != null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Setting resource path to " + resourceDir.getAbsolutePath());
			}
			ResourceLocator.resetFlexoResourceLocation(resourceDir);
		}
	}

	public GenerateHelpSetTask() {
		super();
	}

	public void setBaseName(String base) {
		this.baseName = base;
	}

	// The method executing the task
	@Override
	public void execute() throws BuildException {
		GenerateHelpSet action = GenerateHelpSet.actionType.makeNewAction(null, null, new FlexoEditor() {

			@Override
			public boolean performResourceScanning() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void notifyObjectDeleted(FlexoModelObject object) {
				// TODO Auto-generated method stub

			}

			@Override
			public void notifyObjectCreated(FlexoModelObject object) {
				// TODO Auto-generated method stub

			}

			@Override
			public void notifyObjectChanged(FlexoModelObject object) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionVisible(
					FlexoActionType<A, T1, T2> actionType) {
				return true;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
					FlexoActionType<A, T1, T2> actionType) {
				return true;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionVisibleCondition<? super A, T1, T2> getVisibleConditionFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionUndoInitializer<? super A> getUndoInitializerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionUndoFinalizer<? super A> getUndoFinalizerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionRedoInitializer<? super A> getRedoInitializerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionRedoFinalizer<? super A> getRedoFinalizerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public FlexoProject getProject() {
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionInitializer<? super A> getInitializerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public FlexoProgressFactory getFlexoProgressFactory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionFinalizer<? super A> getFinalizerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoExceptionHandler<? super A> getExceptionHandlerFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getEnabledIconFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionEnableCondition<? super A, T1, T2> getEnableConditionFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getDisabledIconFor(
					FlexoActionType<A, T1, T2> actionType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void focusOn(FlexoModelObject object) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeUndone(
					FlexoAction<A, T1, T2> action) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeRedone(
					FlexoAction<A, T1, T2> action) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBePerformed(
					FlexoAction<A, T1, T2> action) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenUndone(
					FlexoAction<A, T1, T2> action, boolean success) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenRedone(
					FlexoAction<A, T1, T2> action, boolean success) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenPerformed(
					FlexoAction<A, T1, T2> action, boolean success) {
				// TODO Auto-generated method stub

			}

			@Override
			public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void executeAction(A action)
					throws FlexoException {
				action.execute();
			}
		});
		action.setNote("none");
		action.setBaseName(baseName);
		for (HelpLanguage language : languages) {
			for (UserType userType : language.getDistributions()) {
				Language lg = DocResourceManager.instance().getDocResourceCenter().getLanguageNamed(language.getIsoCode());
				String title = language.getTitle();
				action.addToGeneration(title, lg, userType.getIdentifier(), userType.getDocumentationFolders());
			}
		}
		action.doAction();
	}

	public void addConfiguredLanguage(HelpLanguage lg) {
		if (languages == null) {
			languages = new Vector<HelpLanguage>();
		}
		languages.add(lg);
	}

}