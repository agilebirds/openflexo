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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.localization.FlexoLocalization;

public class GenerateSourceCode extends MultipleFileGCAction<GenerateSourceCode>
{

	private static final Logger logger = Logger.getLogger(GenerateSourceCode.class.getPackage().getName());

	public static final MultipleFileGCActionType<GenerateSourceCode> actionType 
	= new MultipleFileGCActionType<GenerateSourceCode> ("generate_code_if_required",
			GENERATE_MENU, GENERATION_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) 
	{
		/**
         * Factory method
         */
        @Override
		public GenerateSourceCode makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) 
        {
            return new GenerateSourceCode(focusedObject, globalSelection, editor);
        }
		
        @Override
		protected boolean accept (AbstractCGFile file)
        {
      		return (file.getResource() != null
    				&& file.needsMemoryGeneration());
        }

	};
	


    static {
        FlexoModelObject.addActionForClass (GenerateSourceCode.actionType, CGObject.class);
    }
    
    GenerateSourceCode (CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

/*    private class GenerateSourceCodeForFile extends GenerateSourceCode.CGFileRunnable {

		public GenerateSourceCodeForFile(AbstractCGFile file) {
			super(file);
		}

		public void run() {
			logger.info(FlexoLocalization.localizedForKey("generate") +  " " + file.getFileName());
    		file.getGenerator().refreshConcernedResources(getRepository());
    		try {
				file.getGenerator().generate(false);
			} catch (GenerationException e) {
				notifyActionFailed(e, e.getMessage());
			}
		}

		@Override
		public String getLocalizedName() {
			return FlexoLocalization.localizedForKey("generating")+" "+file.getFileName();
		}
    	
    }
*/    
    @Override
	protected void doAction(Object context) throws FlexoException {
		logger.info("Generate source code for " + getFocusedObject());

       	AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
    	pg.setAction(this);
 
    	GenerationRepository repository = getRepository();
		if (getSaveBeforeGenerating()) {
			repository.getProject().save();
		}

		// Rebuild and refresh resources, performed here to get also newly created resource on getSelectedCGFilesOnWhyCurrentActionShouldApply
		pg.refreshConcernedResources(null);

		Vector<AbstractCGFile> selectedFiles = getSelectedCGFilesOnWhyCurrentActionShouldApply();
		long start, end;
		boolean hasProgress = getFlexoProgress() != null;
		makeFlexoProgress(FlexoLocalization.localizedForKey("generate") + " " + selectedFiles.size() + " "
				+ FlexoLocalization.localizedForKey("files") + " " + FlexoLocalization.localizedForKey("into") + " "
				+ getRepository().getDirectory().getAbsolutePath(), selectedFiles.size() + 1);
		start = System.currentTimeMillis();
		Vector<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> resources = new Vector<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>();
		for (AbstractCGFile file : selectedFiles) {
			resources.add(file.getResource());
		}

		getProjectGenerator().sortResourcesForGeneration(resources);
		HashSet<IFlexoResourceGenerator> generators = new HashSet<IFlexoResourceGenerator>();
		for (CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r : resources) {
			AbstractCGFile file = (AbstractCGFile) r.getCGFile();
			if (file==null) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Found a file referencing a resource but the resource does not know the file!: "+r);
				continue;
			}
    		setProgress(FlexoLocalization.localizedForKey("generate") +  " " + file.getFileName());
			if (file.needsMemoryGeneration()) {
				if (file.getGenerator() != null && !generators.contains(file.getGenerator())) {
					generators.add(file.getGenerator());
					//addJob(new GenerateSourceCodeForFile(file));
					file.getGenerator().generate(false);
				}
			}
		}
		//waitForAllJobsToComplete();
		end = System.currentTimeMillis();
		if (repository instanceof CGRepository)
     		((CGRepository)repository).clearAllJavaParsingData();
		if (!hasProgress)
			hideFlexoProgress();
		if (logger.isLoggable(Level.INFO))
			logger.info("Generation took: " + (end - start) + " ms");
	}

	public boolean requiresThreadPool() {
		return true;
	}

 }
