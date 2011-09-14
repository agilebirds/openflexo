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
import java.util.logging.Logger;


import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.ModelReinjectableFile;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.action.MultipleFileGCAction;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.javaparser.FJPDMSet;
import org.openflexo.javaparser.FJPTypeResolver;
import org.openflexo.localization.FlexoLocalization;


public class UpdateModel extends MultipleFileGCAction<UpdateModel>
{

	private static final Logger logger = Logger.getLogger(UpdateModel.class.getPackage().getName());

	public static final MultipleFileGCActionType<UpdateModel> actionType 
	= new MultipleFileGCActionType<UpdateModel> ("update_model",
			MODEL_MENU,MODEL_GROUP3,FlexoActionType.NORMAL_ACTION_TYPE) 
	{
		/**
         * Factory method
         */
        @Override
		public UpdateModel makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) 
        {
            return new UpdateModel(repository, globalSelection, editor);
        }
		
        @Override
		protected boolean accept (AbstractCGFile file)
        {
         	return (file.getResource() != null
    				&& file instanceof ModelReinjectableFile
    				&& file.supportModelReinjection());
        }

	};
	
    static {
        FlexoModelObject.addActionForClass (UpdateModel.actionType, CGObject.class);
    }
    
    UpdateModel (CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException
    {
    	logger.info ("Update model");
       	
       	AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
    	pg.setAction(this);
 
    	CGRepository repository = (CGRepository) getRepository();
    	
    	if (getSaveBeforeGenerating()) {
    		repository.getProject().save();
    	}
    	
    	makeFlexoProgress(FlexoLocalization.localizedForKey("updating_model_for") +  " "
    			+ getFilesToUpdate().size() + " "
    			+ FlexoLocalization.localizedForKey("files") +" "
    			+ FlexoLocalization.localizedForKey("from") 
    			+ repository.getDirectory().getAbsolutePath(), getFilesToUpdate().size()+2);

    	for (ModelReinjectableFile file : getFilesToUpdate()) {
    		setProgress(FlexoLocalization.localizedForKey("updating_model_for") +  " " + ((AbstractCGFile)file).getFileName());
          	logger.info(FlexoLocalization.localizedForKey("updating_model_for") +  " " + ((AbstractCGFile)file).getFileName());
          	if (file instanceof CGJavaFile) {
          		try {
					((CGJavaFile)file).updateModel(_updatedSet);
				} catch (FJPTypeResolver.CrossReferencedEntitiesException e) {
					// TODO fix this
					e.printStackTrace();
				}
           	}
     	}
    	setProgress(FlexoLocalization.localizedForKey("save_rm"));
    	repository.getProject().getFlexoRMResource().saveResourceData();

     	hideFlexoProgress();
     	
     	repository.clearAllJavaParsingData();

    }

    private Vector<ModelReinjectableFile> _filesToUpdate;

    public Vector<ModelReinjectableFile> getFilesToUpdate()
    {
    	if (_filesToUpdate == null) {
    		_filesToUpdate = new Vector<ModelReinjectableFile>();
    		for (AbstractCGFile file : getSelectedCGFilesOnWhyCurrentActionShouldApply()) {
    			_filesToUpdate.add((ModelReinjectableFile)file);
    		}
    	}
    	return _filesToUpdate;
    }

    private FJPDMSet _updatedSet;
    
    public FJPDMSet getUpdatedSet()
    {
        return _updatedSet;
    }

    public void setUpdatedSet(FJPDMSet updatedSet)
    {
        _updatedSet = updatedSet;
    }

	public boolean requiresThreadPool() {
		// TODO Auto-generated method stub
		return false;
	}


}
