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
package org.openflexo.foundation.cg.version.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;


public class CleanIntermediateFiles extends AbstractGCAction<CleanIntermediateFiles,CGObject>
{

    private static final Logger logger = Logger.getLogger(CleanIntermediateFiles.class.getPackage().getName());

    public static FlexoActionType<CleanIntermediateFiles,CGObject,CGObject> actionType
    = new FlexoActionType<CleanIntermediateFiles,CGObject,CGObject> (
    		"clean_intermediate_files",
    		versionningMenu,
    		versionningCleanGroup,
    		FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public CleanIntermediateFiles makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) 
        {
            return new CleanIntermediateFiles(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) 
        {
            Vector<CGObject> topLevelObjects = getSelectedTopLevelObjects(focusedObject, globalSelection);
            for (CGObject obj : topLevelObjects) {
            	if (obj instanceof GeneratedOutput) return false;
             }
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) 
        {
         	GenerationRepository repository = getRepository(focusedObject, globalSelection);
        	if (repository == null) return false;
            return repository.getManageHistory() ;
        }
        
     };
    
    static {
        FlexoModelObject.addActionForClass (CleanIntermediateFiles.actionType, CGObject.class);
     }
    

    CleanIntermediateFiles (CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

	@Override
	protected void doAction(Object context) throws SaveResourceException, FlexoException 
	{
		logger.info ("Clean intermediate versions");
		for (CGFile file : getSelectedFiles()) {
			logger.info ("Clean for file "+file.getFileName());
            if (file.getGeneratedResourceData() instanceof AbstractGeneratedFile)
                ((AbstractGeneratedFile)file.getGeneratedResourceData()).getHistory().clean(cleanBeforeFirstRelease,releasesToClean);
		}
	}
	
	private boolean cleanBeforeFirstRelease;
	private Vector<CGRelease> releasesToClean;
	
	public boolean getCleanBeforeFirstRelease() 
	{
		return cleanBeforeFirstRelease;
	}

	public void setCleanBeforeFirstRelease(boolean cleanBeforeFirstRelease) 
	{
		this.cleanBeforeFirstRelease = cleanBeforeFirstRelease;
	}

	public Vector<CGRelease> getReleasesToClean() 
	{
		return releasesToClean;
	}

	public void setReleasesToClean(Vector<CGRelease> releasesToClean) 
	{
		this.releasesToClean = releasesToClean;
	}
	

}
