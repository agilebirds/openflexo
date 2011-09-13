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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.KeywordSubstitutionOptions;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.remove.RemoveCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.BinaryMessageEvent;
import org.netbeans.lib.cvsclient.event.CVSListener;
import org.netbeans.lib.cvsclient.event.FileAddedEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.FileRemovedEvent;
import org.netbeans.lib.cvsclient.event.FileToRemoveEvent;
import org.netbeans.lib.cvsclient.event.FileUpdatedEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.event.TerminationEvent;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoRunnable;


public abstract class CVSAbstractFile extends CVSObject {

	protected static final Logger logger = Logger.getLogger(SharedProject.class.getPackage().getName());

	private File _localFile;
	private CVSContainer _container;
	protected SharedProject _sharedProject;
	
	public CVSAbstractFile (File localFile, SharedProject sharedProject)
	{
		super();
		_sharedProject = sharedProject;
		try {
			_localFile = localFile.getCanonicalFile();
		}
		catch (IOException e) {
			logger.warning("Could not retrieve cannonical file for "+localFile);
			e.printStackTrace();
			_localFile = localFile;
		}
	}
	
	public File getFile() 
	{
		return _localFile;
	}

	public String getFileName() 
	{
		if (_localFile != null) return _localFile.getName();
		return "???";
	}

	public CVSAbstractFile getCVSAbstractFile(File aFile)
	{
		if (aFile.equals(getFile())) return this;
		if (!FileUtils.isFileContainedIn(aFile, getFile())) return null;
		if (this instanceof CVSContainer) {
		for (CVSFile f : ((CVSContainer)this).getFiles()) {
			if (f.getFile().equals(aFile)) {
				//logger.info("Return "+f);
				return f;
			}
		}
		for (CVSDirectory d : ((CVSContainer)this).getDirectories()) {
			CVSAbstractFile returned = d.getCVSAbstractFile(aFile);
			if (returned != null) return returned;
		}
		}
		//logger.info("Return null");
		return null;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName()+":"+getFile().getAbsolutePath();
	}
	
	private CVSStatus _derivedStatus = null;
	
	@Override
	public CVSStatus getDerivedStatus()
	{
		if (_derivedStatus == null) {
			_derivedStatus = computeDerivedStatus();
		}
		return _derivedStatus;
	}

	private CVSStatus computeDerivedStatus()
	{
		CVSStatus returned = CVSStatus.UpToDate;
		if (this instanceof CVSContainer) {
			CVSContainer container = (CVSContainer)this;
			Vector<CVSAbstractFile> files = new Vector<CVSAbstractFile>();
			files.addAll(container.getFiles());
			files.addAll(container.getDirectories());
			for (CVSAbstractFile f : files) {
				CVSStatus status = f.getDerivedStatus();
				if (status.isConflicting()) {
					returned = CVSStatus.Conflicting;
				}
				else if (status.isLocallyModified()) {
					if (returned.isConflicting()) {
						// OK, nothing to do
					}
					else if (returned.isRemotelyModified()) {
						returned = CVSStatus.Conflicting;
					}
					else if (returned.isLocallyModified()) {
						if (returned != status) {
							returned = CVSStatus.LocallyModified;
						}
					}
					else if (returned.isUpToDate()) {
						returned = status;
					}
				}
				else if (status.isRemotelyModified()) {
					if (returned.isConflicting()) {
						// OK, nothing to do
					}
					else if (returned.isRemotelyModified()) {
						if (returned != status) {
							returned = CVSStatus.RemotelyModified;
						}
					}
					else if (returned.isLocallyModified()) {
						returned = CVSStatus.Conflicting;
					}
					else if (returned.isUpToDate()) {
						returned = status;
					}
				}
			}
		}
		else if (this instanceof CVSFile) {
			returned = ((CVSFile)this).getStatus();
		}

		return returned;
	}
	
	public void notifyStatusChanged(CVSAbstractFile file)
	{
		CVSStatus newDerivedStatus = computeDerivedStatus();
		if (newDerivedStatus != _derivedStatus) {
			_derivedStatus = newDerivedStatus;
			setChanged();
			if (getContainer() != null) getContainer().notifyStatusChanged(this);
		}
	}

	public CVSContainer getContainer()
	{
		return _container;
	}

	public void setContainer(CVSContainer container)
	{
		_container = container;
	}

	public SharedProject getSharedProject() {
		return _sharedProject;
	}
	
	@Override
	public boolean isContainedIn(FPSObject obj) 
	{
		if (obj instanceof CVSRepositoryList) {
			return getSharedProject().getCVSRepository().isContainedIn(obj);
		}
		else if (obj instanceof CVSRepository) {
			return getSharedProject().getCVSRepository() == obj;
		}
		else if (obj instanceof CVSModule) {
			return getSharedProject().getCVSModule() == obj;
		}
		else if (obj instanceof SharedProject) {
			return getSharedProject() == obj;
		}
		else if (obj instanceof CVSAbstractFile) {
			CVSAbstractFile current = this;
			while (current != null) {
				if (current == obj) return true;
				if (current.getContainer() instanceof CVSAbstractFile) {
					current = (CVSAbstractFile)current.getContainer();
				}
				else current = null;
			}
		}
		return false;
	}

	// ***************************************************************************
	// **************************** Committing stuff *****************************
	// ***************************************************************************

	public interface CommitListener
	{
		public void notifyCommitFinished (CVSFile file, CommitStatus status);
	}
	
	private CommitListener _commitListener;
	
	private CommittingThread _committingThread;
	
	public synchronized void commit(String commitMessage, CommitListener commitListener, CVSFile... files)
	{
		if (_committingThread == null) {
			Vector<CVSFile> filesToCommit = new Vector<CVSFile>();
			if (this instanceof CVSContainer) {
				for (CVSFile f : files) {
					if (((CVSContainer)this).getFiles().contains(f)) filesToCommit.add(f);
					else logger.warning("Exclude "+f+" from files to commit since not belonging to current directory");
				}
			}
			else if (this instanceof CVSFile
					&& files.length == 1
					&& files[0] == this) {
				filesToCommit.add(files[0]);
			}
			logger.info("Committing "+filesToCommit+" in directory "+getFile());		
			_committingThread = new CommittingThread(commitMessage,filesToCommit);
            getSharedProject().addToThreadPool(_committingThread);
			_commitListener = commitListener;
		}
	}
	
	private synchronized void notifyCommitFinished(CVSFile file, Entry entry)
	{
		file.setEntry(entry);
		if (this instanceof CVSContainer 
				&& file.getStatus() == CVSStatus.LocallyRemoved) {
			((CVSContainer)this).removeFromFiles(file);
		}
		file.setStatus(CVSStatus.UpToDate);
		if (_commitListener != null) _commitListener.notifyCommitFinished(file, CommitStatus.OK);
	}
	
	private synchronized void notifyCommitFailed(CVSFile file)
	{
		if (_commitListener != null) _commitListener.notifyCommitFinished(file, CommitStatus.Error);
	}
	
	public synchronized boolean isCommitting()
	{
		return (_committingThread != null);
	}
	
	public static enum CommitStatus
	{
		NotFinished{
		    /**
		     * Overrides toString
		     * @see java.lang.Enum#toString()
		     */
		    @Override
		    public String toString()
		    {
		        return "NotFinished";
		    }
        },
		OK{
            /**
             * Overrides toString
             * @see java.lang.Enum#toString()
             */
            @Override
            public String toString()
            {
                return "OK";
            }
        },
		Error{
            /**
             * Overrides toString
             * @see java.lang.Enum#toString()
             */
            @Override
            public String toString()
            {
                return "Error";
            }
        }
	}
	
	private class CommittingThread implements FlexoRunnable, CVSListener
	{
		private CommitStatus status;
		private Vector<CVSFile> _filesToCommit;
		private String _commitMessage;
		
		protected CommittingThread(String commitMessage, Vector<CVSFile> filesToCommit)
		{
			_filesToCommit = filesToCommit;
			_commitMessage = commitMessage;
		}
		
		@Override
		public void run() 
		{
            if (logger.isLoggable(Level.INFO))
                logger.info("Starting to commit "+_filesToCommit);
			status = CommitStatus.OK;

			// Handle cases where the directory itself doesn't exist
			if (CVSAbstractFile.this instanceof CVSDirectory) {
				logger.fine("Committing in directory "+CVSAbstractFile.this.getFileName());
				if (!((CVSDirectory)CVSAbstractFile.this).existsOnCVSRepository()) {
					logger.fine("Add non-existing directory "+CVSAbstractFile.this.getFileName());
					cvsAddDirectory();
				}
			}
			
			// Add required files if any
			try {
				cvsAddTextFiles();
				cvsAddBinaryFiles();
			} catch(Exception e) {
				e.printStackTrace();
			}

			// Remove required files if any
			cvsRemove();

			CVSConnection connection = null;

			if (status == CommitStatus.OK) {
				status = CommitStatus.NotFinished;
				CommitCommand commitCommand = new CommitCommand();
				File[] files = new File[_filesToCommit.size()];
				for (int i=0; i<_filesToCommit.size(); i++) files[i] = _filesToCommit.get(i).getFile();
				commitCommand.setFiles(files);
				commitCommand.setMessage(_commitMessage);
				
				try {
					connection = getSharedProject().openConnection();
					connection.getClient().getEventManager().addCVSListener(this);
					connection.getClient().setLocalPath(getFile().getCanonicalPath());
					connection.executeCommand(commitCommand);
				} catch (CommandAbortedException e) {
					e.printStackTrace();
					status = CommitStatus.Error;
				} catch (CommandException e) {
					e.printStackTrace();
					status = CommitStatus.Error;
				} catch (AuthenticationException e) {
					e.printStackTrace();
					status = CommitStatus.Error;
				} catch (IOException e) {
					e.printStackTrace();
					status = CommitStatus.Error;
				}
			}

			if (status == CommitStatus.OK) {
				for (CVSFile f : _filesToCommit) {
					try {
						notifyCommitFinished(f,connection.getClient().getEntry(f.getFile()));
					} catch (IOException e) {
						logger.warning("Unexpected exception "+e.getMessage());
						e.printStackTrace();
						notifyCommitFailed(f);
					}
				}
			}
			else if (status == CommitStatus.Error) {
				for (CVSFile f : _filesToCommit) {
					notifyCommitFailed(f);
				}
			}
            if (logger.isLoggable(Level.INFO))
                logger.info("End of commit "+_filesToCommit+": "+status);
			_committingThread = null;
		}

		private void cvsAddTextFiles() 
		{
			Vector<CVSFile> filesToAdd = new Vector<CVSFile>();
			//logger.info("Add text files: ");
			for (CVSFile f : _filesToCommit) {
				if (f.getStatus() == CVSStatus.LocallyAdded && !f.isBinary()) {
					filesToAdd.add(f);
					//logger.info("TEXT file: "+f.getFileName());
				}
			}
			if (filesToAdd.size() == 0) {
				status = CommitStatus.OK;
				return;
			}			
			status = CommitStatus.NotFinished;
			AddCommand addCommand = new AddCommand();
			File[] files = new File[filesToAdd.size()];
			for (int i=0; i<filesToAdd.size(); i++) files[i] = filesToAdd.get(i).getFile();
			addCommand.setFiles(files);
			
			try {
				CVSConnection connection = getSharedProject().openConnection();
				connection.getClient().getEventManager().addCVSListener(this);
				connection.getClient().setLocalPath(getFile().getCanonicalPath());
				connection.executeCommand(addCommand);
			} catch (CommandAbortedException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (CommandException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (IOException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			}
		}

		private void cvsAddBinaryFiles() 
		{
			Vector<CVSFile> filesToAdd = new Vector<CVSFile>();
			//logger.info("Add binary files: ");
			for (CVSFile f : _filesToCommit) {
				if (f.getStatus() == CVSStatus.LocallyAdded && f.isBinary()) {
					filesToAdd.add(f);
					//logger.info("BINARY file: "+f.getFileName());
				}
			}
			if (filesToAdd.size() == 0) {
				status = CommitStatus.OK;
				return;
			}			
			status = CommitStatus.NotFinished;
			AddCommand addCommand = new AddCommand();
			File[] files = new File[filesToAdd.size()];
			for (int i=0; i<filesToAdd.size(); i++) files[i] = filesToAdd.get(i).getFile();
			addCommand.setFiles(files);
			addCommand.setKeywordSubst(KeywordSubstitutionOptions.BINARY);
			
			try {
				CVSConnection connection = getSharedProject().openConnection();
				connection.getClient().getEventManager().addCVSListener(this);
				connection.getClient().setLocalPath(getFile().getCanonicalPath());
				connection.executeCommand(addCommand);
			} catch (CommandAbortedException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (CommandException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (IOException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			}
		}
		
		private Vector<File> appendFiles(File dir,Vector<File> reply){
			File[] files = dir.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			});
			for(int i=0;i<files.length;i++){
				reply.add(files[i]);
			}
			return reply;
		}
		
		private Vector<File> appendInnerDirectories(File dir,Vector<File> reply){
			File[] dirs = dir.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory() && !pathname.getName().equals("CVS");
				}
			});
			for(int i=0;i<dirs.length;i++){
				reply.add(dirs[i]);
				appendInnerDirectories(dirs[i], reply);
				appendFiles(dirs[i], reply);
			}
			return reply;
		}
		private void cvsAddDirectory() 
		{			
			status = CommitStatus.NotFinished;
			AddCommand addCommand = new AddCommand();
			
			Vector<File> dirsToAdd = new Vector<File>();
			dirsToAdd.add(getFile());
			appendInnerDirectories(getFile(), dirsToAdd);
			File[] files = new File[dirsToAdd.size()];
			int i=0;
			Enumeration<File> en = dirsToAdd.elements();
			while(en.hasMoreElements()){
				files[i] = en.nextElement();
				i++;
			}
			
			
			//File[] files = new File[1];
			//files[0] = getFile();
			addCommand.setFiles(files);
			
			try {
				CVSConnection connection = getSharedProject().openConnection();
				connection.getClient().getEventManager().addCVSListener(this);
				connection.getClient().setLocalPath(getFile().getParentFile().getCanonicalPath());
				connection.executeCommand(addCommand);
			} catch (CommandAbortedException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (CommandException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (IOException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			}
		}

		private void cvsRemove() 
		{
			Vector<CVSFile> filesToRemove = new Vector<CVSFile>();
			for (CVSFile f : _filesToCommit) {
				if (f.getStatus() == CVSStatus.LocallyRemoved) {
					filesToRemove.add(f);
				}
			}
			if (filesToRemove.size() == 0) {
				status = CommitStatus.OK;
				return;
			}			
			status = CommitStatus.NotFinished;
			RemoveCommand removeCommand = new RemoveCommand();
			File[] files = new File[filesToRemove.size()];
			for (int i=0; i<filesToRemove.size(); i++) files[i] = filesToRemove.get(i).getFile();
			removeCommand.setFiles(files);
			
			try {
				CVSConnection connection = getSharedProject().openConnection();
				connection.getClient().getEventManager().addCVSListener(this);
				connection.getClient().setLocalPath(getFile().getCanonicalPath());
				connection.executeCommand(removeCommand);
			} catch (CommandAbortedException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (CommandException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			} catch (IOException e) {
				e.printStackTrace();
				status = CommitStatus.Error;
			}
		}


		@Override
		public void commandTerminated(TerminationEvent e) 
		{
			status = (e.isError()?CommitStatus.Error:CommitStatus.OK);
		}

		@Override
		public void fileAdded(FileAddedEvent e) {
		}

		@Override
		public void fileInfoGenerated(FileInfoEvent e) {
		}

		@Override
		public void fileRemoved(FileRemovedEvent e) {
		}

		@Override
		public void fileToRemove(FileToRemoveEvent e) {
		}

		@Override
		public void fileUpdated(FileUpdatedEvent e) {
		}

		@Override
		public void messageSent(MessageEvent e) {
		}

		@Override
		public void messageSent(BinaryMessageEvent e) {
		}

		@Override
		public void moduleExpanded(ModuleExpansionEvent e) {
		}

        /**
         * Overrides getName
         * @see org.openflexo.toolbox.FlexoRunnable#getName()
         */
        @Override
		public String getName()
        {
            return "Committing in "+getFile().getName();
        }

	}

	// ***************************************************************************
	// **************************** Updating stuff *****************************
	// ***************************************************************************

	public interface UpdateListener
	{
		public void notifyUpdateFinished (CVSFile file, UpdateStatus status);
	}
	
	private UpdateListener _updateListener;
	
	private UpdatingThread _updatingThread;
	
	// WARNING: override option (update -C) not implemented on CVS on MacOSX1.4 !!! :-(
	public synchronized UpdatingThread update(UpdateListener updateListener, boolean override, CVSFile... files)
	{
		if (_updatingThread == null) {
			Vector<CVSFile> filesToUpdate = new Vector<CVSFile>();
			if (this instanceof CVSContainer) {
				for (CVSFile f : files) {
					if (((CVSContainer)this).getFiles().contains(f)) filesToUpdate.add(f);
					else logger.warning("Exclude "+f+" from files to update since not belonging to current directory");
				}
			}
			else if (this instanceof CVSFile
					&& files.length == 1
					&& files[0] == this) {
				filesToUpdate.add(files[0]);
			}
			logger.info("Updating "+filesToUpdate+" in directory "+getFile());		
			_updatingThread = new UpdatingThread(filesToUpdate,override);
            getSharedProject().addToThreadPool(_updatingThread);
			_updateListener = updateListener;
			return _updatingThread;
		}
		return null;
	}
	
	private synchronized void notifyUpdateFinished(CVSFile file, Entry entry)
	{
		file.setEntry(entry);
		/*if (this instanceof CVSContainer 
				&& file.getStatus() == CVSStatus.RemotelyRemoved) {
			((CVSContainer)this).removeFromFiles(file);
		}
		file.setStatus(CVSStatus.UpToDate);*/
		if (_updateListener != null) _updateListener.notifyUpdateFinished(file, UpdateStatus.OK);
	}
	
	private synchronized void notifyUpdateFailed(CVSFile file)
	{
		if (_updateListener != null) _updateListener.notifyUpdateFinished(file, UpdateStatus.Error);
	}
	
	public synchronized boolean isUpdating()
	{
		return (_updatingThread != null);
	}
	
	public static enum UpdateStatus
	{
		NotFinished,
		OK,
		Error
	}
	
	public class UpdatingThread implements CVSListener,FlexoRunnable
	{
		private UpdateStatus status;
		private Vector<CVSFile> _filesToUpdate;
		private boolean _override;
		private CVSConnection connection = null;
		
		protected UpdatingThread(Vector<CVSFile> filesToUpdate, boolean override)
		{
			_filesToUpdate = filesToUpdate;
			_override = override;
		}
		
		public void abort(){
			if(connection!=null && connection.getClient()!=null)
				connection.getClient().abort();
		}
		@Override
		public void run() 
		{
            if (logger.isLoggable(Level.INFO))
                logger.info("Starting to update "+_filesToUpdate);
			connection = null;

			status = UpdateStatus.NotFinished;
			UpdateCommand updateCommand = new UpdateCommand();
			File[] files = new File[_filesToUpdate.size()];
			for (int i=0; i<_filesToUpdate.size(); i++) files[i] = _filesToUpdate.get(i).getFile();
			updateCommand.setFiles(files);
			updateCommand.setCleanCopy(_override);

			try {
				connection = getSharedProject().openConnection();
				connection.getClient().getEventManager().addCVSListener(this);
				connection.getClient().setLocalPath(getFile().getCanonicalPath());
				connection.executeCommand(updateCommand);
			} catch (CommandAbortedException e) {
				e.printStackTrace();
				status = UpdateStatus.Error;
			} catch (CommandException e) {
				e.printStackTrace();
				status = UpdateStatus.Error;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				status = UpdateStatus.Error;
			} catch (IOException e) {
				e.printStackTrace();
				status = UpdateStatus.Error;
			}

			if (status == UpdateStatus.OK) {
				for (CVSFile f : _filesToUpdate) {
					try {
						notifyUpdateFinished(f,connection.getClient().getEntry(f.getFile()));
					} catch (IOException e) {
						logger.warning("Unexpected exception "+e.getMessage());
						e.printStackTrace();
						notifyUpdateFailed(f);
					}
				}
			}
			else if (status == UpdateStatus.Error) {
				for (CVSFile f : _filesToUpdate) {
					notifyUpdateFailed(f);
				}
			}
            if (logger.isLoggable(Level.INFO))
                logger.info("End of commit "+_filesToUpdate+": "+status);
			_updatingThread = null;

		}

		@Override
		public void commandTerminated(TerminationEvent e) 
		{
			status = (e.isError()?UpdateStatus.Error:UpdateStatus.OK);
		}

		@Override
		public void fileAdded(FileAddedEvent e) {
			//logger.info("fileAdded() "+e);
		}

		@Override
		public void fileInfoGenerated(FileInfoEvent e) {
			//logger.info("fileInfoGenerated() "+e);
		}

		@Override
		public void fileRemoved(FileRemovedEvent e) {
			//logger.info("fileRemoved() "+e);
		}

		@Override
		public void fileToRemove(FileToRemoveEvent e) {
			//logger.info("fileToRemove() "+e);
		}

		@Override
		public void fileUpdated(FileUpdatedEvent e) {
			//logger.info("fileUpdated() "+e);
		}

		@Override
		public void messageSent(MessageEvent e) {
			//logger.info("messageSent() "+e);
		}

		@Override
		public void messageSent(BinaryMessageEvent e) {
			//logger.info("messageSent() "+e);
		}

		@Override
		public void moduleExpanded(ModuleExpansionEvent e) {
			//logger.info("moduleExpanded() "+e);
		}

        /**
         * Overrides getName
         * @see org.openflexo.toolbox.FlexoRunnable#getName()
         */
        @Override
		public String getName()
        {
            return "Updating in "+getFile().getName();
        }

	}


}
