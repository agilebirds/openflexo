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
import org.openflexo.fps.CVSAbstractFile.CommitListener;
import org.openflexo.fps.CVSAbstractFile.CommitStatus;
import org.openflexo.fps.CVSAbstractFile.UpdateListener;
import org.openflexo.fps.CVSAbstractFile.UpdateStatus;
import org.openflexo.localization.FlexoLocalization;

public class OverrideAndCommitFiles extends MultipleFileCVSAction<OverrideAndCommitFiles> implements UpdateListener,CommitListener
{

    private static final Logger logger = Logger.getLogger(OverrideAndCommitFiles.class.getPackage().getName());

    public static MultipleFileCVSActionType<OverrideAndCommitFiles> actionType 
    = new MultipleFileCVSActionType<OverrideAndCommitFiles> (
    		"cvs_override_and_commit",CVS_OVERRIDE_OPERATIONS_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
    	@Override
		public OverrideAndCommitFiles makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) 
    	{
    		return new OverrideAndCommitFiles(focusedObject, globalSelection, editor);
    	}

    	@Override
		protected boolean accept(CVSFile aFile) 
    	{
       		return (aFile.getStatus().isConflicting() && aFile.getContentOnDisk() != null);
    	}

    };
    
    static {
        FlexoModelObject.addActionForClass (OverrideAndCommitFiles.actionType, CVSAbstractFile.class);
    }
    

    OverrideAndCommitFiles (FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context)
    {
    	logger.info ("Override and commit files");
    	makeFlexoProgress(FlexoLocalization.localizedForKey("override_and_commit_files"), 7);
    	
    	setProgress("preparing_overriding");
    	
    	Vector<CVSFile> selectedFiles = getSelectedCVSFilesOnWhyCurrentActionShouldApply();
    	
     	Hashtable<CVSFile,String> _localVersions = new Hashtable<CVSFile,String>();
    	
     	// Store local versions on disk
    	for (CVSFile f : selectedFiles) {
     		_localVersions.put(f, f.getContentOnDisk());
    	}   
    	
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
    	
    	waitUpdateResponses();
    	
     	setProgress(FlexoLocalization.localizedForKey("saving_files"));
     	
      	for (CVSFile f : selectedFiles) {
       		f._overrideWithVersion(_localVersions.get(f));
       	}
       	    
       	setProgress("preparing_commit");
    	Hashtable<CVSContainer,Vector<CVSFile>> filesToCommit 
    	= new Hashtable<CVSContainer,Vector<CVSFile>>();
    	for (CVSFile f : selectedFiles) {
    		CVSContainer dir = f.getContainer();
    		Vector<CVSFile> entriesForDir = filesToCommit.get(dir);
    		if (entriesForDir == null) {
    			entriesForDir = new Vector<CVSFile>();
    			filesToCommit.put(dir, entriesForDir);
    		}
    		entriesForDir.add(f);
    	}   
    	
    	sendCommitRequests(filesToCommit);
    	
    	waitCommitResponses();
    	
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
       		updatingFilesToWait += filesToUpdateInThisDir.length;
    	}      	
     }

    private void waitUpdateResponses()
    {
    	setProgress(FlexoLocalization.localizedForKey("waiting_for_responses"));
    	resetSecondaryProgress(updatingFilesToWait);
    	
    	lastUpdateReception = System.currentTimeMillis();
    	
    	while (updatingFilesToWait > 0 && System.currentTimeMillis() - lastUpdateReception < TIME_OUT) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized (this) {
			while (updateFilesToNotify.size() > 0) {
				CVSFile file = updateFilesToNotify.firstElement();
				updateFilesToNotify.removeElementAt(0);
				setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + file.getFile().getName());
			}
		}
    	
    	if (updatingFilesToWait > 0) {
    		updateTimeOutReceived = true;
    		logger.warning("Commit finished with time-out expired: still waiting for "+updatingFilesToWait+" files");
    	}
    	
     }

    @Override
	public boolean hasActionExecutionSucceeded ()
    {
    	if (updateTimeOutReceived) return false;
       	if (commitTimeOutReceived) return false;
            	else return super.hasActionExecutionSucceeded();
    }
    
    private static final long TIME_OUT = 10000; // 10 s

    private boolean updateTimeOutReceived;
    private	long lastUpdateReception;
    private int updatingFilesToWait = 0;
    private Vector<CVSFile> updateFilesToNotify = new Vector<CVSFile>();
    private Vector<CVSFile> commitFilesToNotify = new Vector<CVSFile>();

    @Override
	public synchronized void notifyUpdateFinished(CVSFile file, UpdateStatus status) 
    {
    	logger.info("Update for "+file.getFile()+" finished with status "+status);
    	updatingFilesToWait--;
    	updateFilesToNotify.add(file);
    	lastUpdateReception = System.currentTimeMillis();
    }
	
    private synchronized void sendCommitRequests(Hashtable<CVSContainer,Vector<CVSFile>> filesToCommit)
    {
     	setProgress(FlexoLocalization.localizedForKey("committing_files"));
    	resetSecondaryProgress(filesToCommit.keySet().size());
    	for (CVSContainer dir : filesToCommit.keySet()) {
    		setSecondaryProgress(FlexoLocalization.localizedForKey("committing")+" "+((CVSAbstractFile)dir).getFile().getAbsolutePath());
    		CVSFile[] filesToCommitInThisDir = filesToCommit.get(dir).toArray(new CVSFile[filesToCommit.get(dir).size()]);
       		logger.info("Committing in "+dir);
       		((CVSAbstractFile)dir).commit(getCommitMessage(), this, filesToCommitInThisDir);
       		committingFilesToWait += filesToCommitInThisDir.length;
    	}      	
     }

    private void waitCommitResponses()
    {
    	setProgress(FlexoLocalization.localizedForKey("waiting_for_responses"));
    	resetSecondaryProgress(committingFilesToWait);
    	
    	lastCommitReception = System.currentTimeMillis();
    	
    	while (committingFilesToWait > 0 
    			&& System.currentTimeMillis()-lastCommitReception < TIME_OUT) {
    			try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		synchronized(this) {
    		while (commitFilesToNotify.size()>0) {
    			CVSFile file = commitFilesToNotify.firstElement();
    			commitFilesToNotify.removeElementAt(0);
				setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for")+" "+file.getFile().getName());
			}}
    	}
    	
    	if (committingFilesToWait > 0) {
    		commitTimeOutReceived = true;
    		logger.warning("Commit finished with time-out expired: still waiting for "+committingFilesToWait+" files");
    	}
    	
     }

    private boolean commitTimeOutReceived;
    private	long lastCommitReception;
    private int committingFilesToWait = 0;

    @Override
	public synchronized void notifyCommitFinished(CVSFile file, CommitStatus status) 
    {
    	logger.info("Commit for "+file.getFile()+" finished with status "+status);
    	committingFilesToWait--;
    	commitFilesToNotify.add(file);
    	lastCommitReception = System.currentTimeMillis();
    }

	private String commitMessage = null;

	public String getCommitMessage() 
	{
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) 
	{
		this.commitMessage = commitMessage;
	}

}
