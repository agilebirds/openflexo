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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSAbstractFile;
import org.openflexo.fps.CVSContainer;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.CVSAbstractFile.UpdateListener;
import org.openflexo.fps.CVSAbstractFile.UpdateStatus;
import org.openflexo.localization.FlexoLocalization;

public class MarkAsMergedFiles extends MultipleFileCVSAction<MarkAsMergedFiles> implements UpdateListener
{

    private static final Logger logger = Logger.getLogger(MarkAsMergedFiles.class.getPackage().getName());

    public static MultipleFileCVSActionType<MarkAsMergedFiles> actionType 
    = new MultipleFileCVSActionType<MarkAsMergedFiles> (
    		"mark_as_merged",CVS_OPERATIONS_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
    	@Override
		public MarkAsMergedFiles makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) 
    	{
    		return new MarkAsMergedFiles(focusedObject, globalSelection, editor);
    	}

    	@Override
		protected boolean accept(CVSFile aFile) 
    	{
    		return (aFile.getStatus().isConflicting() && aFile.getMerge().isResolved());
    	}

    };
    
    static {
        FlexoModelObject.addActionForClass (MarkAsMergedFiles.actionType, CVSAbstractFile.class);
    }
    

    MarkAsMergedFiles (FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context)
    {
    	logger.info ("Mark as merged files");
    	makeFlexoProgress(FlexoLocalization.localizedForKey("updating_and_merging_files"), 5);
    	setProgress("preparing_update");
    	
    	Vector<CVSFile> selectedFiles = getSelectedCVSFilesOnWhyCurrentActionShouldApply();
    	
    	Hashtable<CVSContainer,Vector<CVSFile>> filesToMerge 
    	= new Hashtable<CVSContainer,Vector<CVSFile>>();
    	for (CVSFile f : selectedFiles) {
    		CVSContainer dir = f.getContainer();
    		Vector<CVSFile> entriesForDir = filesToMerge.get(dir);
    		if (entriesForDir == null) {
    			entriesForDir = new Vector<CVSFile>();
    			filesToMerge.put(dir, entriesForDir);
    		}
    		entriesForDir.add(f);
    	}   
    	
    	sendUpdateRequests(filesToMerge);
    	
    	waitResponses();
    	
     	setProgress(FlexoLocalization.localizedForKey("saving_files"));
     	
     	//mark as merged is now done in waitResponse
     	
//      	for (CVSFile f : selectedFiles) {
//       		f.markAsMerged();
//       	}
       	    
       	hideFlexoProgress();
    }

    private Vector<CVSAbstractFile.UpdatingThread> updatingThreads;
    private synchronized void sendUpdateRequests(Hashtable<CVSContainer,Vector<CVSFile>> filesToUpdate)
    {
    	updatingThreads = new Vector<CVSAbstractFile.UpdatingThread>();
     	setProgress(FlexoLocalization.localizedForKey("updating_files"));
    	resetSecondaryProgress(filesToUpdate.keySet().size());
    	for (CVSContainer dir : filesToUpdate.keySet()) {
    		setSecondaryProgress(FlexoLocalization.localizedForKey("updating")+" "+((CVSAbstractFile)dir).getFile().getAbsolutePath());
    		CVSFile[] filesToUpdateInThisDir = filesToUpdate.get(dir).toArray(new CVSFile[filesToUpdate.get(dir).size()]);
       		logger.info("Updating in "+dir);
       		CVSAbstractFile.UpdatingThread t = ((CVSAbstractFile)dir).update(this, false, filesToUpdateInThisDir);
       		if(t!=null)updatingThreads.add(t);
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
					file.markAsMerged();
					setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + file.getFile().getName());
				}
			}
		}
    	//we need to kill all pending update threads to avoid corruption of files.
    	if(updatingThreads!=null){
    		for(CVSAbstractFile.UpdatingThread t:updatingThreads){
    			t.abort();
    		}
    	}
    	
    	synchronized (this) {
			while (filesToNotify.size() > 0) {
				CVSFile file = filesToNotify.firstElement();
				filesToNotify.removeElementAt(0);
				file.markAsMerged();
				setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + file.getFile().getName());
			}
		}
    	
    	
    	if (filesToWait > 0) {
    		timeOutReceived = true;
    		logger.warning("Commit finished with time-out expired: still waiting for "+filesToWait+" files");
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
    private static final long TIME_OUT = 20000; // 20 s
    public int filesToWait = 0;
    private Vector<CVSFile> filesToNotify = new Vector<CVSFile>();

    @Override
	public synchronized void notifyUpdateFinished(CVSFile file, UpdateStatus status) 
    {
    	logger.info("Update for "+file.getFile()+" finished with status "+status);
    	filesToWait--;
    	filesToNotify.add(file);
    	lastReception = System.currentTimeMillis();
    }
	
}
