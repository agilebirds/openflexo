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
import org.openflexo.javaparser.FJPDMSet;
import org.openflexo.javaparser.FJPTypeResolver;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sg.file.SGJavaFile;
import org.openflexo.sg.generator.ProjectGenerator;

public class ReinjectInModel extends MultipleFileGCAction<ReinjectInModel> {

	private static final Logger logger = Logger.getLogger(ReinjectInModel.class.getPackage().getName());

	public static final MultipleFileGCActionType<ReinjectInModel> actionType = new MultipleFileGCActionType<ReinjectInModel>(
			"reinject_in_model", MODEL_MENU, MODEL_GROUP1, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public ReinjectInModel makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ReinjectInModel(repository, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return (file.getResource() != null && file instanceof ModelReinjectableFile && ((ModelReinjectableFile) file)
					.needsModelReinjection());
		}

	};

	static {
		FlexoModelObject.addActionForClass(ReinjectInModel.actionType, CGObject.class);
	}

	ReinjectInModel(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Reinject in model");

		ProjectGenerator pg = (ProjectGenerator) getProjectGenerator();
		pg.setAction(this);

		CGRepository repository = (CGRepository) getRepository();

		if (getSaveBeforeGenerating()) {
			repository.getProject().save();
		}

		makeFlexoProgress(FlexoLocalization.localizedForKey("reinjecting_in_model") + " " + getFilesToReinjectInModel().size() + " "
				+ FlexoLocalization.localizedForKey("files") + " " + FlexoLocalization.localizedForKey("from")
				+ repository.getDirectory().getAbsolutePath(), getFilesToReinjectInModel().size() + 2);

		for (AbstractCGFile file : getFilesToReinjectInModel()) {
			setProgress(FlexoLocalization.localizedForKey("reinjecting_in_model") + " " + file.getFileName());
			logger.info(FlexoLocalization.localizedForKey("reinjecting_in_model") + " " + file.getFileName());
			if (file instanceof SGJavaFile) {
				try {
					((SGJavaFile) file).updateModel(_updatedSet);
				} catch (FJPTypeResolver.CrossReferencedEntitiesException e) {
					// TODO fix this
					e.printStackTrace();
				}
			}
		}
		setProgress(FlexoLocalization.localizedForKey("save_rm"));
		repository.getProject().getFlexoRMResource().saveResourceData();

		hideFlexoProgress();

		// Refresh repository
		repository.refresh();
		repository.clearAllJavaParsingData();

	}

	private Vector<AbstractCGFile> _filesToReinjectInModel;

	public Vector<AbstractCGFile> getFilesToReinjectInModel() {
		if (_filesToReinjectInModel == null) {
			_filesToReinjectInModel = getSelectedCGFilesOnWhyCurrentActionShouldApply();
		}
		return _filesToReinjectInModel;
	}

	public void setFilesToReinjectInModel(Vector<AbstractCGFile> someFiles) {
		_filesToReinjectInModel = someFiles;
	}

	private FJPDMSet _updatedSet;

	public FJPDMSet getUpdatedSet() {
		return _updatedSet;
	}

	public void setUpdatedSet(FJPDMSet updatedSet) {
		_updatedSet = updatedSet;
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
