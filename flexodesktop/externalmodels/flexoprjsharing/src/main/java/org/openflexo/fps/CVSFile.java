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
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.PipedFileInformation;
import org.netbeans.lib.cvsclient.command.log.LogCommand;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.command.status.StatusCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.IMerge;
import org.openflexo.diff.merge.Merge;
import org.openflexo.diff.merge.MergedDocumentType;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.fps.CVSRevisionIdentifier.InvalidVersionFormatException;
import org.openflexo.fps.dm.CVSFileEdited;
import org.openflexo.fps.dm.CVSFileMarkedAsMerged;
import org.openflexo.fps.dm.CVSFileRevertToSaved;
import org.openflexo.fps.dm.CVSFileRevisionLoaded;
import org.openflexo.fps.dm.CVSFileSavedAfterEdition;
import org.openflexo.fps.dm.CVSStatusChanged;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoRunnable;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xml.diff3.XMLDiff3;

public class CVSFile extends CVSAbstractFile {

	public static boolean xmlDiff3MergeEnabled = false;

	protected static final Logger logger = Logger.getLogger(CVSFile.class.getPackage().getName());

	private Entry _entry;

	private CVSStatus _status = CVSStatus.UpToDate;

	private CVSRevisionIdentifier _repositoryRevision;

	private String _repositoryFileName;

	public CVSFile(File localFile, Entry entry, SharedProject sharedProject) {
		this(localFile, sharedProject);
		_entry = entry;
	}

	public CVSFile(File localFile, SharedProject sharedProject) {
		super(localFile, sharedProject);
	}

	protected void checkLocallyModified() {
		if (getLastModified() == null) {
			if (getEntry() != null && getEntry().getConflict() != null && getEntry().getConflict().equals(Entry.MERGE_TIMESTAMP)) {
				setStatus(CVSStatus.MarkedAsMerged);
			}
		} else if (getLocalFileLastModifiedTruncated().after(getLastModified())) {
			setStatus(CVSStatus.LocallyModified);
		}
		if (getEntry() != null && getEntry().hadConflicts()) {
			setStatus(CVSStatus.MarkedAsMerged);
		}

	}

	protected Entry getEntry() {
		return _entry;
	}

	protected void setEntry(Entry entry) {
		_entry = entry;
	}

	@Override
	public CVSStatus getDerivedStatus() {
		return getStatus();
	}

	public CVSStatus getStatus() {
		return _status;
	}

	public void setStatus(CVSStatus status) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("File " + getFileName() + " received new status " + status);
		}
		CVSStatus oldStatus = _status;
		if (oldStatus != status) {
			_status = status;
			if (getContainer() != null) {
				getContainer().notifyStatusChanged(this);
			}
			if (oldStatus.isConflicting()) {
				getSharedProject().decConflictsCount(this);
			} else if (oldStatus.isLocallyModified()) {
				getSharedProject().decLocallyModifiedCount();
			} else if (oldStatus.isRemotelyModified()) {
				getSharedProject().decRemotelyModifiedCount();
			}
			if (status.isConflicting()) {
				getSharedProject().incConflictsCount(this);
			} else if (status.isLocallyModified()) {
				getSharedProject().incLocallyModifiedCount();
			} else if (status.isRemotelyModified()) {
				getSharedProject().incRemotelyModifiedCount();
			}
			setChanged();
			notifyObservers(new CVSStatusChanged(this, oldStatus, status));
		}
	}

	@Override
	public String getInspectorName() {
		return Inspectors.FPS.CVS_FILE_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		return "cvs_file";
	}

	public Date getStickyDate() {
		if (_entry != null) {
			return _entry.getDate();
		}
		return new Date(0);
	}

	public Date getLastModified() {
		if (_entry != null) {
			return _entry.getLastModified();
		}
		return new Date(0);
	}

	public Date getLocalFileLastModified() {
		/*
		 * Under Windows, this will return milliseconds while it does not under
		 * MacOS X It is therefore recommanded to use
		 * getLocalFileLastModifiedTruncated
		 */
		if (getFile().exists()) {
			return new Date(getFile().lastModified());
		}
		return new Date(0);
	}

	/**
	 * This method returns the last modified date for this local file but milliseconds are truncated from that time.
	 * 
	 * @return the last modified date for this local file but milliseconds are truncated from that time
	 */
	public Date getLocalFileLastModifiedTruncated() {
		Date date = getLocalFileLastModified();
		long ms = date.getTime();
		date.setTime(ms - ms % 1000);
		return date;
	}

	public String getOptions() {
		if (_entry != null) {
			return _entry.getOptions();
		}
		return null;
	}

	public CVSRevisionIdentifier getRevision() {
		if (_entry != null) {
			try {
				return new CVSRevisionIdentifier(_entry.getRevision());
			} catch (InvalidVersionFormatException e) {
				logger.warning("Invalid revision " + _entry.getRevision());
			}
		}
		return null;
	}

	public String getStickyInformation() {
		if (_entry != null) {
			return _entry.getStickyInformation();
		}
		return null;
	}

	public String getTag() {
		if (_entry != null) {
			return _entry.getTag();
		}
		return null;
	}

	public boolean isBinary() {
		if (_entry != null) {
			return _entry.isBinary();
		}
		return CVSConstants.isBinaryFile(getFile());
	}

	public String getLastModifiedAsString() {
		if (getLastModified() == null || getLastModified().equals(new Date(0))) {
			return FlexoLocalization.localizedForKey("never");
		}
		return new SimpleDateFormat("dd/MM HH:mm:ss").format(getLastModified());
	}

	public String getStickyDateAsString() {
		if (getStickyDate() == null || getStickyDate().equals(new Date(0))) {
			return FlexoLocalization.localizedForKey("never");
		}
		return new SimpleDateFormat("dd/MM HH:mm:ss").format(getStickyDate());
	}

	public String getStatusAsString() {
		return FlexoLocalization.localizedForKey(getStatus().toString());
	}

	@Override
	public boolean isEnabled() {
		return !getStatus().isIgnored();
	}

	private ResourceType _resourceType;

	private boolean _triedToResolveResourceType = false;

	public ResourceType getResourceType() {
		if (!_triedToResolveResourceType) {
			_triedToResolveResourceType = true;
			if (getFileName().endsWith(".rmxml")) {
				_resourceType = ResourceType.RM;
			}
			if (getFileName().endsWith(".rmxml.ts")) {
				_resourceType = ResourceType.RM;
			}
			if (getFileName().endsWith(".wkf")) {
				_resourceType = ResourceType.WORKFLOW;
			}
			if (getFileName().endsWith(".wolib")) {
				_resourceType = ResourceType.COMPONENT_LIBRARY;
			}
			if (getFileName().endsWith(".menu")) {
				_resourceType = ResourceType.NAVIGATION_MENU;
			}
			if (getFileName().endsWith(".xml") && getFile().getParentFile().getName().equals("Workflow")) {
				_resourceType = ResourceType.PROCESS;
			}
			if (getFileName().endsWith(".woxml")) {
				_resourceType = ResourceType.OPERATION_COMPONENT;
			}
			if (getFileName().endsWith(".cg")) {
				_resourceType = ResourceType.GENERATED_CODE;
			}
			if (getFileName().endsWith(".dm")) {
				_resourceType = ResourceType.DATA_MODEL;
			}
			if (getFileName().endsWith(".jar")) {
				_resourceType = ResourceType.JAR;
			}
			if (getFileName().endsWith(".dkv")) {
				_resourceType = ResourceType.DKV_MODEL;
			}
			if (getFileName().endsWith(".jpg") || getFileName().endsWith(".png")) {
				_resourceType = ResourceType.SCREENSHOT;
			}
			if (getFileName().endsWith(".ws")) {
				_resourceType = ResourceType.WS_LIBRARY;
			}
			if (getFileName().endsWith(".xml") && getFile().getParentFile().getName().equals("WebService")) {
				_resourceType = ResourceType.WSDL;
			}
			if (getFileName().endsWith(".api.LAST_ACCEPTED")) {
				_resourceType = ResourceType.API_FILE;
			}
			if (getFileName().endsWith(".api.LAST_GENERATED")) {
				_resourceType = ResourceType.API_FILE;
			}
			if (getFileName().endsWith(".java.LAST_ACCEPTED")) {
				_resourceType = ResourceType.JAVA_FILE;
			}
			if (getFileName().endsWith(".java.LAST_GENERATED")) {
				_resourceType = ResourceType.JAVA_FILE;
			}
		}
		return _resourceType;
	}

	public String getRepositoryFileName() {
		return _repositoryFileName;
	}

	public void setRepositoryFileName(String repositoryFileName) {
		_repositoryFileName = repositoryFileName;
	}

	public CVSRevisionIdentifier getRepositoryRevision() {
		return _repositoryRevision;
	}

	public void setRepositoryRevision(String repositoryRevision) {
		try {
			_repositoryRevision = new CVSRevisionIdentifier(repositoryRevision);
			unlockRevisionRetrievers(_repositoryRevision);
		} catch (InvalidVersionFormatException e) {
			logger.warning("Invalid revision " + repositoryRevision);
		}
	}

	public void markAsMerged() {
		try {
			FileUtils.saveToFile(getFile(), getMerge().getMergedText());
		} catch (IOException e) {
			e.printStackTrace();
		}
		_contentOnDisk = getMerge().getMergedText();
		_contentOnDiskBeforeMerge = null;
		setStatus(CVSStatus.MarkedAsMerged);
		rebuildMerge();
		setChanged();
		notifyObservers(new CVSFileMarkedAsMerged(this));
	}

	public void _overrideWithVersion(String versionToCommit) {
		try {
			FileUtils.saveToFile(getFile(), versionToCommit);
		} catch (IOException e) {
			e.printStackTrace();
		}
		_setContentOnDisk(versionToCommit);
		setChanged();
		notifyObservers(new CVSFileSavedAfterEdition(this));
	}

	public interface FileContentEditor {
		public String getEditedContent();

		public void setEditedContent(CVSFile file);
	}

	private FileContentEditor _editor;

	public boolean isEdited() {
		return _editor != null;
	}

	public void edit(FileContentEditor editor) {
		_editor = editor;
		editor.setEditedContent(this);
		setChanged();
		notifyObservers(new CVSFileEdited(this));
	}

	public void save() throws SaveResourceException {
		String newContentOnDisk = _editor.getEditedContent();
		_setContentOnDisk(newContentOnDisk);
		setChanged();
		notifyObservers(new CVSFileSavedAfterEdition(this));
	}

	public void _setContentOnDisk(String newContentOnDisk) {
		try {
			FileUtils.saveToFile(getFile(), newContentOnDisk);
		} catch (IOException e) {
			e.printStackTrace();
		}
		_contentOnDisk = newContentOnDisk;
		_editor = null;
		if (_merge != null) {
			rebuildMerge();
		}
		if (getStatus().isUpToDate()) {
			setStatus(CVSStatus.LocallyModified);
		} else if (getStatus().isLocallyModified()) {
			// Already done
		} else if (getStatus().isRemotelyModified()) {
			setStatus(CVSStatus.Conflicting);
		}
	}

	public void revertToSaved() {
		_editor = null;
		setChanged();
		notifyObservers(new CVSFileRevertToSaved(this));
	}

	public void notifyFileUpdated() {
		_contentOnDisk = null;
		setStatus(CVSStatus.UpToDate);
	}

	private String _contentOnDisk;

	public String getContentOnDisk() {
		if (_contentOnDisk == null && getFile().exists()) {
			try {
				_contentOnDisk = FileUtils.fileContents(getFile());
			} catch (IOException e) {
				logger.warning("Could not read " + getFile() + ": " + e.getClass().getSimpleName() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		return _contentOnDisk;
	}

	public boolean hasVersionOnDisk() {
		return getContentOnDisk() != null;
	}

	private String _contentOnDiskBeforeMerge;

	private CVSRevisionIdentifier _revisionOnWhichContentOnDiskBeforeMergeWasBasedOn;

	public String getContentOnDiskBeforeMerge() {
		if (_contentOnDiskBeforeMerge == null) {
			File[] allPossibleFiles = getFile().getParentFile().listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().indexOf(".#" + getFile().getName()) == 0;
				}
			});
			File choosenFile = null;
			_revisionOnWhichContentOnDiskBeforeMergeWasBasedOn = null;
			for (File f : allPossibleFiles) {
				try {
					CVSRevisionIdentifier id = new CVSRevisionIdentifier(f.getName().substring((".#" + getFile().getName()).length()));
					if (_revisionOnWhichContentOnDiskBeforeMergeWasBasedOn == null
							|| id.isGreaterThan(_revisionOnWhichContentOnDiskBeforeMergeWasBasedOn)) {
						_revisionOnWhichContentOnDiskBeforeMergeWasBasedOn = id;
						choosenFile = f;
					}
				} catch (InvalidVersionFormatException e) {
					logger.warning("Strange revision " + f.getName().substring((".#" + getFile().getName()).length()));
				}
			}
			if (choosenFile != null) {
				try {
					_contentOnDiskBeforeMerge = FileUtils.fileContents(choosenFile);
				} catch (IOException e) {
					logger.warning("Could not read " + choosenFile + ": " + e.getClass().getSimpleName() + " : " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return _contentOnDiskBeforeMerge;
	}

	public CVSRevisionIdentifier getRevisionOnWhichContentOnDiskBeforeMergeWasBasedOn() {
		getContentOnDiskBeforeMerge();
		return _revisionOnWhichContentOnDiskBeforeMergeWasBasedOn;
	}

	private String _contentOnRepository = null;

	private RevisionRetriever _contentOnRepositoryRevisionRetriever;

	public synchronized String getContentOnRepository() {
		return getContentOnRepository(null);
	}

	public synchronized String getContentOnRepository(RevisionRetrieverListener listener) {
		if (_contentOnRepository == null && _contentOnRepositoryRevisionRetriever == null && !getSharedProject().isSynchronizing()) {
			_contentOnRepository = retrieveContentOnRepository(listener);
		}
		return _contentOnRepository;
	}

	public synchronized boolean isReceivingContentOnRepository() {
		return _contentOnRepositoryRevisionRetriever != null;
	}

	private synchronized String retrieveContentOnRepository(RevisionRetrieverListener listener) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to retrieve revision " + getRepositoryRevision() + " for file " + getFile());
		}

		_contentOnRepositoryRevisionRetriever = new RevisionRetriever(getRepositoryRevision(), listener);
		getSharedProject().addToThreadPool(_contentOnRepositoryRevisionRetriever);
		return "Loading revision " + getRepositoryRevision() + " for file " + getFile();
	}

	private static final String LAST = "last";

	private class RevisionRetriever implements FlexoRunnable {
		private CVSRevisionIdentifier revisionToRetrieve;
		private RevisionRetrieverListener _listener;

		protected RevisionRetriever(CVSRevisionIdentifier aRevisionToRetrieve, RevisionRetrieverListener listener) {
			revisionToRetrieve = aRevisionToRetrieve;
			_listener = listener;
		}

		@Override
		public void run() {
			// Perform a update -p -r revision
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("[" + Thread.currentThread().getName() + "] Starting " + getName());
			}
			UpdateCommand updateCommand = new UpdateCommand();
			updateCommand.setRecursive(false);
			File[] files = { getFile() };
			updateCommand.setFiles(files);
			updateCommand.setUpdateByRevision(revisionToRetrieve != null ? revisionToRetrieve.toString() : null);
			updateCommand.setPipeToOutput(true);
			try {
				CVSConnection connection = getSharedProject().openConnection();
				connection.getClient().getEventManager().addCVSListener(new CVSAdapter() {
					@Override
					public void fileInfoGenerated(FileInfoEvent e) {
						if (revisionToRetrieve == null) { // Means "get the
															// last one"
							if (e.getInfoContainer() instanceof PipedFileInformation) {
								PipedFileInformation info = (PipedFileInformation) e.getInfoContainer();
								setRepositoryRevision(info.getRepositoryRevision());
								if (logger.isLoggable(Level.FINE)) {
									logger.fine("Sets repository revision for " + getFileName() + " to be " + info.getRepositoryRevision());
								}
							}
						}
					}
				});
				connection.executeCommand(updateCommand);
			} catch (CommandAbortedException e) {
				e.printStackTrace();
			} catch (CommandException e) {
				e.printStackTrace();
			} catch (AuthenticationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("[" + Thread.currentThread().getName() + "] Ending " + getName());
			}

			if (_listener != null) {
				_listener.notifyRevisionRetrieved(CVSFile.this, revisionToRetrieve);
			}
		}

		public synchronized CVSRevisionIdentifier getRevisionToRetrieve() {
			if (revisionToRetrieve == null && getRepositoryRevision() != null) {
				return getRepositoryRevision();
			}
			return revisionToRetrieve;
		}

		/**
		 * Overrides getName
		 * 
		 * @see org.openflexo.toolbox.FlexoRunnable#getName()
		 */
		@Override
		public String getName() {
			return "cvs-retrieve " + getFile().getName() + "-" + revisionToRetrieve;
		}

	}

	private Hashtable<CVSRevisionIdentifier, CVSRevision> _revisions = new Hashtable<CVSRevisionIdentifier, CVSRevision>();

	protected synchronized void receivePipedFileInformation(PipedFileInformation info) {
		String receivedRepositoryRevision = info.getRepositoryRevision();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("receivePipedFileInformation() for file=" + getFile() + " " + receivedRepositoryRevision + " on file "
					+ info.getTempFile());
		}

		CVSRevisionIdentifier revisionId = null;
		try {
			revisionId = new CVSRevisionIdentifier(receivedRepositoryRevision);
		} catch (InvalidVersionFormatException e1) {
			logger.warning("Invalid revision " + receivedRepositoryRevision);
			return;
		}

		try {
			CVSRevision revision = new CVSRevision(revisionId, this);
			revision.setContents(FileUtils.fileContents(info.getTempFile()));
			_revisions.put(revisionId, revision);
			_revisionsNeedReordering = true;
			info.getTempFile().delete();
		} catch (IOException e) {
			logger.warning("Could not retrieve data from file " + info.getTempFile());
			e.printStackTrace();
		}

		unlockRevisionRetrievers(revisionId);
	}

	private synchronized void unlockRevisionRetrievers(CVSRevisionIdentifier revisionId) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("unlockRevisionRetrievers() called with " + revisionId);
		}
		if (_originalContentRevisionRetriever != null) {
			if (_originalContentRevisionRetriever.getRevisionToRetrieve().equals(revisionId) && _revisions.get(revisionId) != null) {
				setOriginalContent(_revisions.get(revisionId).getContents());
				_originalContentRevisionRetriever = null;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Unlocking OriginalContentRevisionRetriever");
				}
				return;
			}
		}
		if (_contentOnRepositoryRevisionRetriever != null) {
			if (_contentOnRepositoryRevisionRetriever.getRevisionToRetrieve() != null
					&& _contentOnRepositoryRevisionRetriever.getRevisionToRetrieve().equals(revisionId)
					&& _revisions.get(revisionId) != null) {
				setContentOnRepository(_revisions.get(revisionId).getContents());
				_contentOnRepositoryRevisionRetriever = null;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Unlocking ContentOnRepositoryRevisionRetriever");
				}
				return;
			}
		}
	}

	/*
	 * protected synchronized void receivePipedFileInformation
	 * (PipedFileInformation info) { String receivedRepositoryRevision =
	 * info.getRepositoryRevision(); if (receivedRepositoryRevision == null) {
	 * receivedRepositoryRevision =
	 * (getRepositoryRevision()!=null?getRepositoryRevision():LAST); }
	 * logger.info("receivePipedFileInformation() for file="+getFile()+"
	 * "+receivedRepositoryRevision+" on file "+info.getTempFile()); if
	 * (_contentOnRepositoryRevisionRetriever != null) { logger.info("1
	 * _contentOnRepositoryRevisionRetriever.getRevisionToRetrieve()="+_contentOnRepositoryRevisionRetriever.getRevisionToRetrieve());
	 * if (getStatus() == CVSStatus.RemotelyAdded ||
	 * _contentOnRepositoryRevisionRetriever.getRevisionToRetrieve().equals(receivedRepositoryRevision)) {
	 * try { logger.info("2");
	 * setContentOnRepository(FileUtils.fileContents(info.getTempFile())); }
	 * catch (IOException e) { logger.warning("Could not retrieve data from file
	 * "+info.getTempFile()); e.printStackTrace(); } logger.info("3");
	 * _contentOnRepositoryRevisionRetriever = null;
	 * info.getTempFile().delete(); } } if (_originalContentRevisionRetriever !=
	 * null) { logger.info("4
	 * _originalContentRevisionRetriever.getRevisionToRetrieve()="+_originalContentRevisionRetriever.getRevisionToRetrieve());
	 * if
	 * (_originalContentRevisionRetriever.getRevisionToRetrieve().equals(receivedRepositoryRevision)) {
	 * logger.info("5"); try { logger.info("6");
	 * setOriginalContent(FileUtils.fileContents(info.getTempFile())); } catch
	 * (IOException e) { logger.warning("Could not retrieve data from file
	 * "+info.getTempFile()); e.printStackTrace(); } logger.info("7");
	 * _originalContentRevisionRetriever = null; info.getTempFile().delete(); } } }
	 */

	private synchronized void setContentOnRepository(String newContentOnRepository) {
		_contentOnRepository = newContentOnRepository;
		if (_merge != null) {
			rebuildMerge();
		}
		// logger.info("Content on repository is now: "+_contentOnRepository);
		setChanged();
		notifyObservers(new CVSFileRevisionLoaded(this));
	}

	public interface RevisionRetrieverListener {
		public void notifyRevisionRetrieved(CVSFile file, CVSRevisionIdentifier revision);
	}

	private String _originalContent = null;

	private RevisionRetriever _originalContentRevisionRetriever;

	public synchronized String getOriginalContent() {
		return getOriginalContent(null);
	}

	public synchronized String getOriginalContent(RevisionRetrieverListener listener) {
		if (_originalContent == null && _originalContentRevisionRetriever == null && !getSharedProject().isSynchronizing()) {
			_originalContent = retrieveOriginalContent(listener);
		}
		return _originalContent;
	}

	public synchronized boolean isReceivingOriginalContent() {
		return _originalContentRevisionRetriever != null;
	}

	private synchronized String retrieveOriginalContent(RevisionRetrieverListener listener) {
		logger.info("Try to retrieve revision " + getRevision() + " for file " + getFile());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to retrieve revision " + getRevision() + " for file " + getFile());
		}

		_originalContentRevisionRetriever = new RevisionRetriever(getRevision(), listener);
		getSharedProject().addToThreadPool(_originalContentRevisionRetriever);
		return "Loading revision " + getRevision() + " for file " + getFile();
	}

	private synchronized void setOriginalContent(String originalContent) {
		_originalContent = originalContent;
		// logger.info("Original content is now: "+_originalContent);
		if (_merge != null) {
			rebuildMerge();
		}
		setChanged();
		notifyObservers(new CVSFileRevisionLoaded(this));
	}

	private IMerge _merge;

	public synchronized IMerge getMerge() {
		if (CVSFile.xmlDiff3MergeEnabled && getResourceType() != null && getResourceType().isFlexoXMLStorageResource()) {
			if (_xmlDiff3 == null) {
				rebuildXMLDiff3();
			}
			return _xmlDiff3;
		} else {
			if (_merge == null) {
				rebuildMerge();
			}
			return _merge;
		}
	}

	private synchronized void rebuildMerge() {
		if (getContentOnDisk() == null) {
			return;
		}
		if (getOriginalContent() == null) {
			return;
		}
		if (getContentOnRepository() == null) {
			return;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("BEGIN Thread " + Thread.currentThread() + ": rebuildMerge() for " + getFileName());
		}
		MergedDocumentType docType = DefaultMergedDocumentType.LINES;
		if (getResourceType() != null) {
			docType = getResourceType().getMergedDocumentType();
		}
		_merge = new Merge(new DiffSource(getOriginalContent()), new DiffSource(getContentOnDisk()), new DiffSource(
				getContentOnRepository()), docType);
		/*
		 * logger.info("Merge for "+getFileName()+"
		 * @"+Integer.toHexString(_merge.hashCode()) +"
		 * original="+_merge.getOriginalSource().tokensCount() +"
		 * left="+_merge.getLeftSource().tokensCount() +"
		 * right="+_merge.getRightSource().tokensCount());
		 */
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("END Thread " + Thread.currentThread() + ": rebuildMerge() for " + getFileName() + " merge="
					+ Integer.toHexString(_merge.hashCode()));
		}
	}

	private XMLDiff3 _xmlDiff3;

	// public synchronized XMLDiff3 getXMLDiff3()
	// {
	// if (_xmlDiff3 == null) {
	// rebuildXMLDiff3();
	// }
	// return _xmlDiff3;
	// }

	private synchronized void rebuildXMLDiff3() {
		if (getContentOnDisk() == null) {
			return;
		}
		if (getOriginalContent() == null) {
			return;
		}
		if (getContentOnRepository() == null) {
			return;
		}

		Document docSource = null;
		Document v1 = null;
		Document v2 = null;
		try {
			docSource = ToolBox.parseXMLData(new StringReader(getOriginalContent()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot build XMLDiff because cannot read OriginalContent", e);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot build XMLDiff because cannot parse OriginalContent :\n" + getOriginalContent(), e);
		}
		try {
			v1 = ToolBox.parseXMLData(new StringReader(getContentOnRepository()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot build XMLDiff because cannot read Content on repository", e);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot build XMLDiff because cannot parse Content on repository :\n" + getContentOnRepository(), e);
		}
		try {
			v2 = ToolBox.parseXMLData(new StringReader(getContentOnDisk()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot build XMLDiff because cannot read Content on disk", e);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot build XMLDiff because cannot parse Content on disk :\n" + getContentOnDisk(), e);
		}
		_xmlDiff3 = new XMLDiff3(docSource, v1, v2, getResourceType().getMapping(getSharedProject().getFlexoXMLMappings()),
				getResourceType().getMergedDocumentType());
	}

	public synchronized void retrieveStatus() {
		StatusCommand statusCommand = new StatusCommand();
		File[] files = { getFile() };
		statusCommand.setFiles(files);

		try {
			CVSConnection connection = getSharedProject().openConnection();
			connection.executeCommand(statusCommand);
		} catch (CommandAbortedException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean isCVSHistoryRetrievable() {
		// logger.info("isCVSHistoryRetrievable() for "+getFileName()+" :
		// "+getStatus().isUnderCVS());
		return getStatus().isUnderCVS();
	}

	public synchronized void retrieveCVSHistory() {
		LogCommand logCommand = new LogCommand();
		File[] files = { getFile() };
		logCommand.setFiles(files);

		try {
			CVSConnection connection = getSharedProject().openConnection();
			connection.executeCommand(logCommand);
		} catch (CommandAbortedException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected synchronized void receiveLogInformation(LogInformation info) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("receiveLogInformation() for file=" + getFile());
		}
		headRevision = info.getHeadRevision();
		branch = info.getBranch();
		accessList = info.getAccessList();
		keywordSubstitution = info.getKeywordSubstitution();
		description = info.getDescription();
		locks = info.getLocks();
		for (Iterator it = info.getRevisionList().iterator(); it.hasNext();) {
			LogInformation.Revision revisionInfo = (LogInformation.Revision) it.next();
			try {
				CVSRevisionIdentifier id = new CVSRevisionIdentifier(revisionInfo.getNumber());
				CVSRevision cvsRevision = _revisions.get(id);
				if (cvsRevision == null) {
					cvsRevision = new CVSRevision(id, this);
					_revisions.put(id, cvsRevision);
					_revisionsNeedReordering = true;
				}
				cvsRevision.setRevisionInfo(revisionInfo);
			} catch (InvalidVersionFormatException e) {
				logger.warning("Invalid revision : " + revisionInfo.getNumber());
			}
		}
		setChanged();
		notifyObservers(new AttributeDataModification("orderedRevisions", null, null));
	}

	private boolean _revisionsNeedReordering = true;

	private Vector<CVSRevision> _orderedRevisions = new Vector<CVSRevision>();

	public Vector<CVSRevision> getOrderedRevisions() {
		if (_revisionsNeedReordering) {
			_orderedRevisions.clear();
			_orderedRevisions.addAll(_revisions.values());
			Collections.sort(_orderedRevisions, CVSRevision.COMPARATOR);
		}
		return _orderedRevisions;
	}

	private String headRevision;

	private String branch;

	private String accessList;

	private String keywordSubstitution;

	private String description;

	private String locks;

	public String getAccessList() {
		return accessList;
	}

	public String getBranch() {
		return branch;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getHeadRevision() {
		return headRevision;
	}

	public String getKeywordSubstitution() {
		return keywordSubstitution;
	}

	public String getLocks() {
		return locks;
	}

}
