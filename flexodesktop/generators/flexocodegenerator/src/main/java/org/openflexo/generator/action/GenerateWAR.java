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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.action.GCAction;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;

public class GenerateWAR extends GCAction<GenerateWAR, CGRepository> {

	private static final Logger logger = Logger.getLogger(GenerateWAR.class.getPackage().getName());

	public static FlexoActionType<GenerateWAR, CGRepository, CGObject> actionType = new FlexoActionType<GenerateWAR, CGRepository, CGObject>(
			"generate_war", GENERATE_MENU, WAR_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateWAR makeNewAction(CGRepository repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new GenerateWAR(repository, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGRepository repository, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(CGRepository repository, Vector<CGObject> globalSelection) {
			ProjectGenerator pg = (ProjectGenerator) getProjectGenerator(repository);
			return pg != null && pg.hasBeenInitialized() && repository.getWarDirectory() != null
					&& repository.getWarRepository().isConnected() && repository.getWarRepository().getDirectory().exists();
		}

	};

	static {
		FlexoModelObject.addActionForClass(GenerateWAR.actionType, CGRepository.class);
	}

	GenerateWAR(CGRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private File generatedWar;
	private boolean cleanImmediately = false;

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public CGRepository getRepository() {
		return (CGRepository) super.getRepository();
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException {
		ProjectGenerator pg = getProjectGenerator();
		pg.setAction(this);

		if (getSaveBeforeGenerating()) {
			getRepository().getProject().save();
		}

		logger.info("Generate WAR for " + getFocusedObject());
		if (getFlexoProgress() != null)
			getFlexoProgress().setProgress(
					FlexoLocalization.localizedForKey("generate") + " " + pg.getRepository().getWarName() + " "
							+ FlexoLocalization.localizedForKey("into") + " " + getFocusedObject().getWarDirectory().getAbsolutePath());
		try {
			if (_customOutStream != null) {
				_defaultOutStream = System.out;
				System.setOut(_customOutStream);
			}
			if (_customErrStream != null) {
				_defaultErrStream = System.err;
				System.setErr(_customErrStream);
			}
			generatedWar = pg.generateWar(cleanImmediately);
		} finally {
			if (_defaultOutStream != null) {
				System.setOut(_defaultOutStream);
			}
			if (_defaultErrStream != null) {
				System.setErr(_defaultErrStream);
			}
		}
		pg.getRepository().clearAllJavaParsingData();
		hideFlexoProgress();
	}

	private PrintStream _defaultOutStream;
	private PrintStream _customOutStream;

	public void setCustomOutStream(OutputStream s) {
		_customOutStream = new PrintStream(s);
	}

	private PrintStream _defaultErrStream;
	private PrintStream _customErrStream;

	public void setCustomErrStream(OutputStream s) {
		_customErrStream = new PrintStream(s);
	}

	public File getGeneratedWar() {
		return generatedWar;
	}

	public void setCleanImmediately(boolean cleanImmediately) {
		this.cleanImmediately = cleanImmediately;
	}

}
