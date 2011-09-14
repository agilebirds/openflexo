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
package org.openflexo.sg.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.ModelReinjectableFile;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.action.MultipleFileGCAction;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sg.generator.ProjectGenerator;


public class AcceptDiskUpdateAndReinjectInModel extends MultipleFileGCAction<AcceptDiskUpdateAndReinjectInModel> {

	private static final Logger logger = Logger.getLogger(AcceptDiskUpdateAndReinjectInModel.class.getPackage().getName());

	public static final MultipleFileGCActionType<AcceptDiskUpdateAndReinjectInModel> actionType = new MultipleFileGCActionType<AcceptDiskUpdateAndReinjectInModel>("accept_and_reinject",
			ROUND_TRIP_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public AcceptDiskUpdateAndReinjectInModel makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new AcceptDiskUpdateAndReinjectInModel(repository, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return (file.getResource() != null && file.getGenerationStatus().isDiskModified() && file instanceof ModelReinjectableFile && ((ModelReinjectableFile) file).needsModelReinjection());
		}

	};

	static {
		FlexoModelObject.addActionForClass(AcceptDiskUpdateAndReinjectInModel.actionType, CGObject.class);
	}

	AcceptDiskUpdateAndReinjectInModel(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Accepting disk update and reinject in model");

		ProjectGenerator pg = (ProjectGenerator) getProjectGenerator();
		pg.setAction(this);

		CGRepository repository = (CGRepository) getRepository();

		if (getSaveBeforeGenerating()) {
			repository.getProject().save();
		}

		makeFlexoProgress(FlexoLocalization.localizedForKey("accepting_and_reinjecting") + " " + getFilesToAccept().size() + " " + FlexoLocalization.localizedForKey("files") + " "
				+ FlexoLocalization.localizedForKey("from") + repository.getDirectory().getAbsolutePath(), getFilesToAccept().size() + 2);

		for (AbstractCGFile file : getFilesToAccept()) {
			setProgress(FlexoLocalization.localizedForKey("accepting_and_reinjecting") + " " + file.getFileName());
			logger.info(FlexoLocalization.localizedForKey("accepting_and_reinjecting") + " " + file.getFileName());
			file.acceptDiskVersion();
		}
		setProgress(FlexoLocalization.localizedForKey("save_rm"));
		repository.getProject().getFlexoRMResource().saveResourceData();

		hideFlexoProgress();

		// Refresh repository
		repository.refresh();

		// And now launch reinjection procedure
		ReinjectInModel reinjectInModelAction = ReinjectInModel.actionType.makeNewEmbeddedAction(getFocusedObject(), getGlobalSelection(), this);
		reinjectInModelAction.setContext(this);
		reinjectInModelAction.setAskReinjectionContext(getAskReinjectionContext());
		reinjectInModelAction.setFilesToReinjectInModel(getFilesToAccept());
		reinjectInModelAction.doAction();
	}

	private Vector<AbstractCGFile> _filesToAccept;

	public Vector<AbstractCGFile> getFilesToAccept() {
		if (_filesToAccept == null) {
			_filesToAccept = getSelectedCGFilesOnWhyCurrentActionShouldApply();
		}
		return _filesToAccept;
	}

	public void setFilesToAccept(Vector<AbstractCGFile> someFiles) {
		_filesToAccept = someFiles;
	}

	private boolean askReinjectionContext = true;

	public boolean getAskReinjectionContext() {
		return askReinjectionContext;
	}

	public void setAskReinjectionContext(boolean askReinjectionContext) {
		this.askReinjectionContext = askReinjectionContext;
	}

	public boolean requiresThreadPool() {
		// TODO Auto-generated method stub
		return false;
	}

}
