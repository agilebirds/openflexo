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
package org.openflexo.generator.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.ModelValidationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.PlaySound;

public class SynchronizeRepositoryCodeGeneration extends GCAction<SynchronizeRepositoryCodeGeneration, GenerationRepository> {

	private static final Logger logger = Logger.getLogger(SynchronizeRepositoryCodeGeneration.class.getPackage().getName());

	public static FlexoActionType<SynchronizeRepositoryCodeGeneration, GenerationRepository, CGObject> actionType = new FlexoActionType<SynchronizeRepositoryCodeGeneration, GenerationRepository, CGObject>(
			"synchronize_code_generation", GENERATE_MENU, SYNCHRO_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public SynchronizeRepositoryCodeGeneration makeNewAction(GenerationRepository object, Vector<CGObject> globalSelection,
				FlexoEditor editor) {
			return new SynchronizeRepositoryCodeGeneration(object, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(GenerationRepository focusedObject, Vector<CGObject> globalSelection) {
			Vector<CGObject> topLevelObjects = getSelectedTopLevelObjects(focusedObject, globalSelection);
			for (CGObject obj : topLevelObjects) {
				if (obj instanceof GeneratedOutput) {
					return false;
				}
			}
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(GenerationRepository focusedObject, Vector<CGObject> globalSelection) {
			GenerationRepository repository = getRepository(focusedObject, globalSelection);
			if (repository == null) {
				return false;
			}
			return repository.isEnabled();
		}

	};

	static {
		FlexoModelObject.addActionForClass(SynchronizeRepositoryCodeGeneration.actionType, GenerationRepository.class);
	}

	SynchronizeRepositoryCodeGeneration(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private ValidateProject validateProjectAction;

	private boolean hasFailed = false;

	@Override
	public boolean isLongRunningAction() {
		return true;
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Synchronize repository code generation " + getFocusedObject());
		PlaySound.tryToPlayRandomSound();
		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.setAction(this);

		GenerationRepository repository = getRepository();
		// Save if required
		if (getSaveBeforeGenerating()) {
			getRepository().getProject().save();
		}

		if (repository instanceof CGRepository) {
			// Validate project
			validateProjectAction = ValidateProject.actionType.makeNewEmbeddedAction(getRepository(), null, this);
			validateProjectAction.setContext(this);
			try {
				validateProjectAction.doAction(null);
			} catch (ModelValidationException e) {
				_continueAfterValidation = false;
				hasFailed = true;
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Synchronization has failed because of validation:\n" + e.getDetails());
				}
			} catch (FlexoException e) {
				throw e;
			}

			// Don't continue if validation failed and continue requested anymay
			if (!getContinueAfterValidation()) {
				return;
			}
		}
		// We set ourselved back because ValidateProject has overriden us.
		pg.setAction(this);

		makeFlexoProgress(FlexoLocalization.localizedForKey("synchronize_repository_code_generation_for") + " "
				+ getFocusedObject().getProject().getPrefix() + "Application " + FlexoLocalization.localizedForKey("into") + " "
				+ getRepository().getDirectory().getAbsolutePath(), 15);

		pg.refreshConcernedResources();
		GenerateSourceCode generateSourceCode = GenerateSourceCode.actionType.makeNewEmbeddedAction(getFocusedObject(),
				getGlobalSelection(), this);
		generateSourceCode.doAction();
		hasFailed &= generateSourceCode.didGenerationSucceeded();
		hideFlexoProgress();
	}

	@Override
	public boolean hasActionExecutionSucceeded() {
		return !hasFailed && super.hasActionExecutionSucceeded();
	}

	public String getValidationErrorAsString() {
		if (validateProjectAction != null) {
			return validateProjectAction.readableValidationErrors();
		}
		return null;
	}

	private boolean _continueAfterValidation = true;

	public boolean getContinueAfterValidation() {
		return _continueAfterValidation;
	}

	public void setContinueAfterValidation(boolean continueAfterValidation) {
		_continueAfterValidation = continueAfterValidation;
	}

}
