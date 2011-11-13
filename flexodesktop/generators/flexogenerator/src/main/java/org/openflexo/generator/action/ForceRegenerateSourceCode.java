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

import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.localization.FlexoLocalization;

public class ForceRegenerateSourceCode extends MultipleFileGCAction<ForceRegenerateSourceCode> {

	private static final Logger logger = Logger.getLogger(ForceRegenerateSourceCode.class.getPackage().getName());

	public static final MultipleFileGCActionType<ForceRegenerateSourceCode> actionType = new MultipleFileGCActionType<ForceRegenerateSourceCode>(
			"force_regenerate_code", GENERATE_MENU, GENERATION_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public ForceRegenerateSourceCode makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ForceRegenerateSourceCode(repository, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return (file.getResource() != null);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ForceRegenerateSourceCode.actionType, CGObject.class);
	}

	/*    private class ForceRegenerateSourceCodeForFile extends GenerateSourceCode.CGFileRunnable {

			public ForceRegenerateSourceCodeForFile(AbstractCGFile file) {
				super(file);
			}

			public void run() {
				logger.info(FlexoLocalization.localizedForKey("force_regenerate") +  " " + file.getFileName());
	    		try {
					file.getGenerator().generate(true);
				} catch (GenerationException e) {
					notifyActionFailed(e, e.getMessage());
				}
			}

			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("force_regenerate")+" "+file.getFileName();
			}
	    	
	    }
	*/
	ForceRegenerateSourceCode(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException {
		logger.info("Force regenerate source code for " + getFocusedObject());

		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.getProject().getResourceManagerInstance().stopResourcePeriodicChecking();
		try {
			pg.setAction(this);
			GenerationRepository repository = getRepository();
			if (getSaveBeforeGenerating()) {
				repository.getProject().save();
			}
			// Rebuild and refresh resources, performed here to get also newly created resource on
			// getSelectedCGFilesOnWhyCurrentActionShouldApply
			pg.refreshConcernedResources(null);

			Vector<AbstractCGFile> selectedFiles = getSelectedCGFilesOnWhyCurrentActionShouldApply();

			makeFlexoProgress(
					FlexoLocalization.localizedForKey("force_regenerate") + " " + selectedFiles.size() + " "
							+ FlexoLocalization.localizedForKey("files") + " " + FlexoLocalization.localizedForKey("into") + " "
							+ getRepository().getDirectory().getAbsolutePath(), selectedFiles.size() + 1);
			Vector<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> resources = new Vector<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>();
			for (AbstractCGFile file : selectedFiles) {
				file.setForceRegenerate(true);
				resources.add(file.getResource());
			}
			getProjectGenerator().sortResourcesForGeneration(resources);
			HashSet<IFlexoResourceGenerator> generators = new HashSet<IFlexoResourceGenerator>();
			for (CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r : resources) {
				AbstractCGFile file = (AbstractCGFile) r.getCGFile();
				if (file == null) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Found a file referencing a resource but the resource does not know the file!: " + r);
					continue;
				}
				setProgress(FlexoLocalization.localizedForKey("force_regenerate") + " " + file.getFileName());
				if (file.getGenerator() != null && !generators.contains(file.getGenerator())) {
					generators.add(file.getGenerator());
					// addJob(new ForceRegenerateSourceCodeForFile(file));
					file.getGenerator().generate(true);
				}
			}
			// waitForAllJobsToComplete();
			/*if (logger.isLoggable(Level.INFO))
				logger.info("All jobs are finished");*/
			if (repository instanceof CGRepository)
				((CGRepository) repository).clearAllJavaParsingData();
			hideFlexoProgress();
		} finally {
			pg.getProject().getResourceManagerInstance().startResourcePeriodicChecking();
		}
	}

	public boolean requiresThreadPool() {
		return true;
	}

}
