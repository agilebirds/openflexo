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

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.IOExceptionOccuredException;
import org.openflexo.generator.exception.MultipleGenerationException;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.localization.FlexoLocalization;

public class WriteModifiedGeneratedFiles extends MultipleFileGCAction<WriteModifiedGeneratedFiles>
{

	private static final Logger logger = Logger.getLogger(WriteModifiedGeneratedFiles.class.getPackage().getName());

	public static final MultipleFileGCActionType<WriteModifiedGeneratedFiles> actionType 
	= new MultipleFileGCActionType<WriteModifiedGeneratedFiles> ("write_to_disk",
			GENERATE_MENU, WRITE_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) 
	{
		/**
         * Factory method
         */
        @Override
		public WriteModifiedGeneratedFiles makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) 
        {
            return new WriteModifiedGeneratedFiles(repository, globalSelection, editor);
        }
		
        @Override
		protected boolean accept (AbstractCGFile file)
        {
        	return  (file.isCodeGenerationAvailable()
    				&& !file.hasGenerationErrors()
    				&& file.getResource() != null
    				&& file.getResource().needsGeneration()
    				&& file.getGenerationStatus() != GenerationStatus.ConflictingUnMerged)
        			|| (file.isMarkedForDeletion());
        }

	};
	
    static {
        FlexoModelObject.addActionForClass (WriteModifiedGeneratedFiles.actionType, CGObject.class);
    }
    
    WriteModifiedGeneratedFiles (CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }
    
    private MultipleGenerationException exception;

    @Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException
    {
    	logger.info ("Write modified files");
       	
    	AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
    	pg.setAction(this);
 
    	GenerationRepository repository = getRepository();
    	
    	if (getSaveBeforeGenerating()) {
    		repository.getProject().save();
    	}
    	
      	makeFlexoProgress(FlexoLocalization.localizedForKey("write") +  " "
       			+ getFilesToWrite().size() + " "
       			+ FlexoLocalization.localizedForKey("files") +" "
      			+ FlexoLocalization.localizedForKey("into")  +" "
      			+ repository.getDirectory().getAbsolutePath(), getFilesToWrite().size()+2);
     	Vector<AbstractCGFile> filesMarkedForDeletion = new Vector<AbstractCGFile>();
     	Vector<AbstractCGFile> filesToWrite = new Vector<AbstractCGFile>();
     	for (AbstractCGFile file : getFilesToWrite()) {
     		/*if (file.needsMemoryGeneration()) {
     			if(file.getGenerator()!=null){
     	    		file.getGenerator().refreshConcernedResources();
     	    		file.getGenerator().generate(true);
     			}else{
     				logger.warning("No generator for file : "+file);
     			}
     		}*/
     	    // GPO: The above code has been commented. Unless we find a very good reason for calling the code above, we should not do this. Why?
    		// Well because! No, because when we call generate(false), we may trigger the generator to run again. So far it ain't too bad,
    		// except that when the generator is done, it sends a notification CGContentRegenerated and it causes the flag "mark as merged" to
    		// go back to false (making it impossible to write it down!). In conclusion, if you decide to uncomment the block above, then you
    		// need to do something about the org.openflexo.foundation.cg.CGFile.update(FlexoObservable, DataModification) method that sets
    		// the flag markAsMerged back to false
     		if (file.isMarkedForDeletion()) {
     			filesMarkedForDeletion.add(file);
     		} else {
     			filesToWrite.add(file);
     		}
     	}
     	// 1. We delete the files
     	for (AbstractCGFile file : filesMarkedForDeletion) {
 			setProgress(FlexoLocalization.localizedForKey("delete") +  " " + file.getFileName());
 			logger.info (FlexoLocalization.localizedForKey("delete") +  " " + file.getFileName());
     		try {
				file.writeModifiedFile();
			} catch (FlexoException e) {
				if (exception==null)
					exception = new MultipleGenerationException();
				exception.addToExceptions(e);
			}

     	}
     	
     	// 2. We write the new ones.
     	for (AbstractCGFile file : filesToWrite) {
 			setProgress(FlexoLocalization.localizedForKey("write") +  " " + file.getFileName());
            logger.info (FlexoLocalization.localizedForKey("write") +  " " + file.getFileName());
     		try {
     			file.writeModifiedFile();
     		} catch (FlexoException e) {
     			if (exception==null)
     				exception = new MultipleGenerationException();
     			exception.addToExceptions(e);
     		}
     	}
     	try {
			pg.copyAdditionalFiles();
		} catch (IOException e) {
			if (exception==null)
 				exception = new MultipleGenerationException();
 			exception.addToExceptions(new IOExceptionOccuredException(e,pg));
		}
		setProgress(FlexoLocalization.localizedForKey("save_rm"));
		repository.getProject().getFlexoRMResource().saveResourceData();
		if (repository instanceof CGRepository)
     		((CGRepository)repository).clearAllJavaParsingData();		     	 
     	hideFlexoProgress();
     	if (exception!=null)
     		throw exception;
    }

    private Vector<AbstractCGFile> _filesToWrite;

    public Vector<AbstractCGFile> getFilesToWrite()
    {
    	if (_filesToWrite == null) {
    		_filesToWrite = getSelectedCGFilesOnWhyCurrentActionShouldApply();
    	}
    	return _filesToWrite;
    }

	public void setFilesToWrite(Vector<AbstractCGFile> someFiles)
	{
		_filesToWrite = someFiles;
	}

	public boolean requiresThreadPool() {
		// TODO Auto-generated method stub
		return false;
	}
    

}
