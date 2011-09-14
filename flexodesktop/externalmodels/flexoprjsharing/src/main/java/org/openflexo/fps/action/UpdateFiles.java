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
package org.openflexo.fps.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSAbstractFile;
import org.openflexo.fps.CVSConstants;
import org.openflexo.fps.CVSContainer;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.CVSAbstractFile.UpdateListener;
import org.openflexo.fps.CVSAbstractFile.UpdateStatus;
import org.openflexo.localization.FlexoLocalization;

public class UpdateFiles extends MultipleFileCVSAction<UpdateFiles> implements UpdateListener
{

    private static final Logger logger = Logger.getLogger(UpdateFiles.class.getPackage().getName());

    public static MultipleFileCVSActionType<UpdateFiles> actionType 
    = new MultipleFileCVSActionType<UpdateFiles> (
    		"cvs_update",CVS_OPERATIONS_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
    	@Override
		public UpdateFiles makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) 
    	{
    		return new UpdateFiles(focusedObject, globalSelection, editor);
    	}

    	@Override
		protected boolean accept(CVSFile aFile) 
    	{
    		return (aFile.getStatus().isRemotelyModified() 
    				&& !aFile.getStatus().isConflicting());
    	}

    };
    
    static {
        FlexoModelObject.addActionForClass (UpdateFiles.actionType, CVSAbstractFile.class);
    }
    

    UpdateFiles (FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context)
    {
    	logger.info ("Update files");
    	makeFlexoProgress(FlexoLocalization.localizedForKey("updating_files"), 4);
    	setProgress("preparing_update");
    	Hashtable<CVSContainer,Vector<CVSFile>> filesToUpdate 
    	= new Hashtable<CVSContainer,Vector<CVSFile>>();
    	for (CVSFile f : getSelectedCVSFilesOnWhyCurrentActionShouldApply()) {
    		CVSContainer dir = f.getContainer();
    		Vector<CVSFile> entriesForDir = filesToUpdate.get(dir);
    		if (entriesForDir == null) {
    			entriesForDir = new Vector<CVSFile>();
    			filesToUpdate.put(dir, entriesForDir);
    		}
    		entriesForDir.add(f);
    	}   
    	
    	sendUpdateRequests(filesToUpdate);
    	
    	waitResponses();
    	
    	hideFlexoProgress();
    }

    private synchronized void sendUpdateRequests(Hashtable<CVSContainer,Vector<CVSFile>> filesToUpdate)
    {
     	setProgress(FlexoLocalization.localizedForKey("updating_files"));
    	resetSecondaryProgress(filesToUpdate.keySet().size());
    	for (CVSContainer dir : filesToUpdate.keySet()) {
    		setSecondaryProgress(FlexoLocalization.localizedForKey("updating")+" "+((CVSAbstractFile)dir).getFile().getAbsolutePath());
    		CVSFile[] filesToUpdateInThisDir = filesToUpdate.get(dir).toArray(new CVSFile[filesToUpdate.get(dir).size()]);
       		logger.info("Updating in "+dir);
       		((CVSAbstractFile)dir).update(this, false, filesToUpdateInThisDir);
       		filesToWait += filesToUpdateInThisDir.length;
    	}      	
     }

    private void waitResponses()
    {
    	setProgress(FlexoLocalization.localizedForKey("waiting_for_responses"));
    	resetSecondaryProgress(filesToWait);
    	
    	lastReception = System.currentTimeMillis();
    	
    	while (filesToWait > 0 && System.currentTimeMillis() - lastReception < TIME_OUT) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (this) {
				while (filesToNotify.size() > 0) {
					CVSFile file = filesToNotify.firstElement();
					filesToNotify.removeElementAt(0);
					setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + file.getFile().getName());
				}
			}
		}
    	
    	if (filesToWait > 0) {
    		timeOutReceived = true;
    		logger.warning("Update finished with time-out expired: still waiting for "+filesToWait+" files");
    	}
    	
     }

    @Override
	public boolean hasActionExecutionSucceeded ()
    {
    	if (timeOutReceived) return false;
    	else return super.hasActionExecutionSucceeded();
    }
    
    private boolean timeOutReceived;
    private	long lastReception;
    private static final long TIME_OUT = CVSConstants.TIME_OUT; // 60 s
    private int filesToWait = 0;
    private Vector<CVSFile> filesToNotify = new Vector<CVSFile>();
    
    @Override
	public synchronized void notifyUpdateFinished(CVSFile file, UpdateStatus status) 
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Update for "+file.getFile()+" finished with status "+status);
    	filesToWait--;
    	filesToNotify.add(file);
    	lastReception = System.currentTimeMillis();
    }
	
}
