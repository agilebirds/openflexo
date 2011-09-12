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
package org.openflexo.fps;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.lib.cvsclient.command.PipedFileInformation;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.command.status.StatusInformation;
import org.netbeans.lib.cvsclient.event.BinaryMessageEvent;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.FileAddedEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.FileRemovedEvent;
import org.netbeans.lib.cvsclient.event.FileToRemoveEvent;
import org.netbeans.lib.cvsclient.event.FileUpdatedEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.netbeans.lib.cvsclient.file.FileStatus;

public class CVSListener extends CVSAdapter
{
	private static final Logger logger = Logger.getLogger(CVSListener.class.getPackage().getName());

	private final StringBuffer taggedLine = new StringBuffer();
	private SharedProject _project;

	public CVSListener(SharedProject project) 
	{
		super();
		_project = project;
	}

	   /**
     * Called when the server wants to send a message to be displayed to
     * the user. The message is only for information purposes and clients
     * can choose to ignore these messages if they wish.
     * @param e the event
     */
    @Override
	public void messageSent(MessageEvent e) 
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("messageSent() with "+e);
    }

    /**
     * Called when the server wants to send a binary message to be displayed to
     * the user. The message is only for information purposes and clients
     * can choose to ignore these messages if they wish.
     * @param e the event
     */
    @Override
	public void messageSent(BinaryMessageEvent e) 
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("messageSentBinary() with "+e);
    }

    private boolean fileAddingNotificationEnabled = true;
    
    protected void enableFileAddingNotification()
    {
    	fileAddingNotificationEnabled = true;
    }
    
    protected void disableFileAddingNotification()
    {
    	fileAddingNotificationEnabled = false;
    }
    
    /**
     * Called when a file has been added.
     * @param e the event
     */
    @Override
	public void fileAdded(FileAddedEvent e)
    {
    	if (fileAddingNotificationEnabled) {
    		if (logger.isLoggable(Level.FINE)) logger.fine("fileAdded() with "+e);
    		CVSAbstractFile cvsFile = _project.getCVSAbstractFile(e.getFilePath());
    		String filePath = e.getFilePath();
    		if (cvsFile == null) {
    			cvsFile = _project.createCVSFile(new File(filePath));
    		}
    		if (cvsFile != null) {
    			if (cvsFile instanceof CVSFile) {
    				//logger.info("Before update: "+((CVSFile)cvsFile).getContentOnDisk());
    				((CVSFile)cvsFile).notifyFileUpdated();
    				//logger.info("After update: "+((CVSFile)cvsFile).getContentOnDisk());
    			}
    		}
    		else {
    			logger.info("Cannot find file for path "+filePath);
    		}
    	}
    }

    /**
     * Called when a file is going to be removed.
     * @param e the event
     */
    @Override
	public void fileToRemove(FileToRemoveEvent e)
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("fileToRemove() with "+e);
    }

    /**
     * Called when a file is removed.
     * @param e the event
     */
    @Override
	public void fileRemoved(FileRemovedEvent e) 
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("fileRemoved() with "+e);
  		CVSAbstractFile cvsFile = _project.getCVSAbstractFile(e.getFilePath());
   		if (cvsFile != null) {
   			if (cvsFile instanceof CVSFile) {
   				cvsFile.getContainer().removeFromFiles((CVSFile)cvsFile);
   				((CVSFile)cvsFile).setStatus(CVSStatus.Removed);
   			}
   		}
   		else {
   			logger.info("Cannot find file for path "+e.getFilePath());
   		}
    }

    /**
     * Called when a file has been updated
     * @param e the event
     */
    @Override
	public void fileUpdated(FileUpdatedEvent e) 
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("fileUpdated() with "+e);
   		CVSAbstractFile cvsFile = _project.getCVSAbstractFile(e.getFilePath());
   		if (cvsFile != null) {
   			if (cvsFile instanceof CVSFile) {
   				//logger.info("Before update: "+((CVSFile)cvsFile).getContentOnDisk());
   				((CVSFile)cvsFile).notifyFileUpdated();
   				//logger.info("After update: "+((CVSFile)cvsFile).getContentOnDisk());
  			}
   		}
   		else {
   			logger.info("Cannot find file for path "+e.getFilePath());
   		}
   }

    private boolean _receiveRemoteUpdateRequest = false;
    
    /**
     * Called when file status information has been received
     */
    @Override
	public void fileInfoGenerated(FileInfoEvent e)
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("fileInfoGenerated() with "+e);
    	//logger.info("File "+e.getInfoContainer().getFile()+" "+e.getInfoContainer().getClass().getSimpleName()+" "+e.getInfoContainer());
    	if (e.getInfoContainer() instanceof DefaultFileInfoContainer && _receiveRemoteUpdateRequest) {
    		DefaultFileInfoContainer info = (DefaultFileInfoContainer)e.getInfoContainer();
    		CVSAbstractFile cvsFile = _project.getCVSAbstractFile(info.getFile());
    		boolean fileWasAdded = false;
    		if (cvsFile == null) {
    			cvsFile = _project.createCVSFile(info.getFile());
    			fileWasAdded = true;
    		}
    		if (info.getType().equals("U") && (cvsFile instanceof CVSFile)) {
    			if (fileWasAdded) 
    				((CVSFile)cvsFile).setStatus(CVSStatus.RemotelyAdded);
    			else if (((CVSFile)cvsFile).getStatus() != CVSStatus.LocallyRemoved && ((CVSFile)cvsFile).getStatus() != CVSStatus.LocallyAdded) 
    				((CVSFile)cvsFile).setStatus(CVSStatus.RemotelyModified);
    		}
    		else if (info.getType().equals("C") && (cvsFile instanceof CVSFile)) {
    			((CVSFile)cvsFile).setStatus(CVSStatus.Conflicting);
    		}
    		else if (info.getType().equals("Y") && (cvsFile instanceof CVSFile)) {
    			((CVSFile)cvsFile).setStatus(CVSStatus.RemotelyRemoved);
    		}
    	}
    	else if (e.getInfoContainer() instanceof StatusInformation) {
    		StatusInformation info = (StatusInformation)e.getInfoContainer();
    		CVSAbstractFile cvsAbstractFile = _project.getCVSAbstractFile(info.getFile());
    		if (cvsAbstractFile instanceof CVSFile) {
    			CVSFile cvsFile = (CVSFile)cvsAbstractFile;
    			//logger.info("Received StatusInformation "+info.getStatus()+" for "+info.getFile());
    			if (info.getStatus() == FileStatus.NEEDS_MERGE) {
    				cvsFile.setStatus(CVSStatus.Conflicting);
    			}
    			else if (info.getStatus() == FileStatus.NEEDS_PATCH) {
    				cvsFile.setStatus(CVSStatus.RemotelyModified);
    			}
       			else if (info.getStatus() == FileStatus.MODIFIED) {
    				cvsFile.setStatus(CVSStatus.LocallyModified);
    			}
      			else if (info.getStatus() == FileStatus.UP_TO_DATE) {
    				cvsFile.setStatus(CVSStatus.UpToDate);
    			}
       			else {
       				// Other status will not be interpreted here
       			}
    			cvsFile.setRepositoryFileName(info.getRepositoryFileName());
    			cvsFile.setRepositoryRevision(info.getRepositoryRevision());
    		}
    	}
    	else if (e.getInfoContainer() instanceof PipedFileInformation) {
    		PipedFileInformation info = (PipedFileInformation)e.getInfoContainer();
    		/*logger.info("PipedFileInformation");
    		logger.info("File="+info.getFile());
    		logger.info("getTempFile()="+info.getTempFile());
    		try {
				logger.info("Content="+FileUtils.fileContents(info.getTempFile()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}*/
    		//logger.info("PipedFileInformation received for revision "+info.getRepositoryRevision()+" for File="+info.getFile());
       		CVSAbstractFile cvsAbstractFile = _project.getCVSAbstractFile(info.getFile());
    		if (cvsAbstractFile instanceof CVSFile) {
    			((CVSFile)cvsAbstractFile).receivePipedFileInformation(info);
    		}   		
    	}
       	else if (e.getInfoContainer() instanceof LogInformation) {
       		LogInformation info = (LogInformation)e.getInfoContainer(); 
       		CVSAbstractFile cvsAbstractFile = _project.getCVSAbstractFile(info.getFile());
    		if (cvsAbstractFile instanceof CVSFile) {
    			((CVSFile)cvsAbstractFile).receiveLogInformation(info);
    		}   		
    	}
    }

    /**
     * called when server responses with "ok" or "error", (when the command finishes)
     */
    @Override
	public void commandTerminated(TerminationEvent e)
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("commandTerminated() with "+e);
    }

    /**
     * Fire a module expansion event. This is called when the servers
     * has responded to an expand-modules request.
     */
    @Override
	public void moduleExpanded(ModuleExpansionEvent e)
    {
    	if (logger.isLoggable(Level.FINE)) logger.fine("moduleExpanded() with "+e);
    }

	protected boolean getReceiveRemoteUpdateRequest() {
		return _receiveRemoteUpdateRequest;
	}

	protected void setReceiveRemoteUpdateRequest(boolean receiveRemoteUpdateRequest) {
		_receiveRemoteUpdateRequest = receiveRemoteUpdateRequest;
	}


}

