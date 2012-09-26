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
package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.toolbox.FileUtils;

/**
 * This class represents a File Flexo resource. A File FlexoResource represent an object handled by Flexo Application Suite (all concerned
 * modules), which could be stored in a File, generally located in related {@link FlexoProject} project directory.
 * 
 * @author sguerin
 */
public abstract class FlexoFileResource<RD extends FlexoResourceData> extends FlexoResource<RD> {

	/**
	 * This constant traduces the delay accepted for the File System to effectively write a file on disk after the date it was requested. If
	 * file is written after this delay, the ResourceManager will interprete it as a concurrent file modification requiring to be handled
	 * properly. In fact, this is not a big problem but resource management may be affected.
	 */
	public static final long ACCEPTABLE_FS_DELAY = 4000;

	private static final Logger logger = Logger.getLogger(FlexoFileResource.class.getPackage().getName());

	protected FlexoProjectFile resourceFile;

	// This variable is only used to be reset when we have written on disk.
	private Date _diskLastModifiedDate;

	private boolean _isSaving = false;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoFileResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoFileResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * Override this
	 */
	@Override
	public boolean isToBeSerialized() {
		return true;
	}

	public FlexoProjectFile getResourceFile() {
		if (resourceFile != null) {
			resourceFile.setProject(project);
		}
		return resourceFile;
	}

	public void setResourceFile(FlexoProjectFile aFile) throws InvalidFileNameException {
		if (aFile == null) {
			throw new InvalidFileNameException("null file");
		}
		if (!aFile.nameIsValid()) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("File name: " + aFile.getRelativePath() + " is not valid for object of class " + getClass().getName());
			}
			throw new InvalidFileNameException(aFile.getRelativePath());
		}
		if (getProject().getFlexoRMResource() != null && !getProject().getFlexoRMResource().getIsLoading()
				&& !(this instanceof FlexoRMResource)) {
			FlexoResource res = getProject().resourceForFileName(aFile);
			if (res != null && res != this) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("The name " + ((FlexoFileResource) res).getResourceFile().getStringRepresentation()
							+ " is equivalent to " + aFile.getStringRepresentation() + " and can therefore not be used twice!!");
					logger.severe("The existing resource is " + ((FlexoFileResource) res).getName()
							+ "\n The resource we try to associate to the same file is " + getName());
				}
				throw new InvalidFileNameException(aFile.getRelativePath());
			}
		}
		resourceFile = aFile;
		if (getProject() != null && resourceFile != null) {
			resourceFile.setProject(getProject());
		}
		if (resourceFile != null && getProject() != null && resourceFile.getFile() != null) {
			File f = resourceFile.getFile();
			for (File file : new ArrayList<File>(getProject().getFilesToDelete())) {
				if (file.getAbsolutePath().toLowerCase().equals(f.getAbsolutePath().toLowerCase())) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Petit filou");
					}
					getProject().removeFromFilesToDelete(file);
					if (!file.getAbsolutePath().equals(f.getAbsolutePath())) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Super filou!");
						}
						resourceFile.setFile(file);
					}
					break;
				}
			}
		}
	}

	public void recoverFile() {
		if (getFile() == null) {
			return;
		}
		if (getFile().exists()) {
			return;
		}
		if (getFile().getParentFile().exists()) {
			File[] files = getFile().getParentFile().listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.getName().equalsIgnoreCase(getFile().getName())) {
					try {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Found file " + file.getAbsolutePath() + ". Using it and repairing project as well!");
						}
						setResourceFile(new FlexoProjectFile(file, getProject()));
						break;
					} catch (InvalidFileNameException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public File getFile() {
		if (resourceFile != null) {
			resourceFile.setProject(project);
			return resourceFile.getFile();
		} else {
			return null;
		}
	}

	public boolean isConnected() {
		return getFile() != null && (resourceFile.getExternalRepository() == null || resourceFile.getExternalRepository().isConnected());
	}

	public String getFileName() {
		return getFile() != null ? getFile().getName() : null;
	}

	public boolean renameFileTo(String name) throws InvalidFileNameException, IOException {
		File newFile = new File(getFile().getParentFile(), name);
		if (getFile().exists()) {
			FileUtils.rename(getFile(), newFile);
			if (getFile().exists()) {
				getFile().delete();
			}
			resetDiskLastModifiedDate();
		}
		if (getResourceFile().getExternalRepository() != null) {
			String relPath = getResourceFile().getRelativePath();
			relPath = relPath.replace('\\', '/');
			if (relPath.indexOf('/') > -1) {
				relPath = relPath.substring(0, relPath.lastIndexOf('/') + 1) + name;
			} else {
				relPath = name;
			}
			setResourceFile(new FlexoProjectFile(getProject(), getResourceFile().getExternalRepository(), relPath));
		} else {
			setResourceFile(new FlexoProjectFile(newFile, getProject()));
		}
		return true;
	}

	/**
	 * Returns the last modified date of the underlying file that Flexo has computed (or remembered) so that we get milliseconds precision
	 * 
	 * @return the last modified date known by Flexo with milliseconds precision.
	 */
	public final synchronized Date getDiskLastModifiedDate() {
		if ((_diskLastModifiedDate == null || _diskLastModifiedDate.getTime() == 0 || isConnected() && !getFile().exists()) && !_isSaving) {
			if (getFile() != null && getFile().exists()) {
				_diskLastModifiedDate = FileUtils.getDiskLastModifiedDate(getFile());
			} else {
				// logger.warning("File "+getFile().getAbsolutePath()+" doesn't exist");
				_diskLastModifiedDate = new Date(0); // means never
				_lastWrittenOnDisk = new Date(0);
			}
			if (_lastWrittenOnDisk == null) {
				_lastWrittenOnDisk = _diskLastModifiedDate;
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm:ss SSS");
			if (_diskLastModifiedDate.getTime() > _lastWrittenOnDisk.getTime() + ACCEPTABLE_FS_DELAY) {
				if (_lastWrittenOnDisk.getTime() != 0) {
					// Here we have written on disk, and somehow the disk last modified date is still bigger than the acceptable delay
					// This can happen sometimes if it takes too long to write on disk
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Resource "
								+ this
								+ " : declared lastWrittenOnDisk date is anterior to current effective last modified date: which means that file on disk in newer than expected"
								+ "_diskLastModifiedDate[" + simpleDateFormat.format(_diskLastModifiedDate) + "]" + " > lastWrittenOnDisk["
								+ simpleDateFormat.format(new Date(_lastWrittenOnDisk.getTime() + ACCEPTABLE_FS_DELAY)) + "]");
					}
				}
				// Since we are in this block (diskLastModified was null see the top 'if'), we consider that it is some kind of bug in the
				// FS
				// and we update accordingly so that the resource checking thread won't think that the resource was updated by another
				// application
				_lastWrittenOnDisk = _diskLastModifiedDate;
			} else if (_lastWrittenOnDisk.getTime() - _diskLastModifiedDate.getTime() > ACCEPTABLE_FS_DELAY) {
				if (getFile().exists()) { // Warn it only if file exists:
					// otherwise it's normal
					logger.warning("Resource "
							+ this
							+ " : declared lastWrittenOnDisk date is posterior to current effective last modified date (with a delay, due to FS date implementation): which means that something strange happened"
							+ "_diskLastModifiedDate[" + simpleDateFormat.format(_diskLastModifiedDate) + "]" + " < lastWrittenOnDisk["
							+ simpleDateFormat.format(_lastWrittenOnDisk) + "]");
					// We should rather go back in time and consider that the information we stored is no longer correct.
					_lastWrittenOnDisk = _diskLastModifiedDate;
				}
			}
		}
		return _lastWrittenOnDisk;
	}

	// This is the date known by Flexo (with milliseconds precision) at which we have written on the disk.
	private Date _lastWrittenOnDisk;

	public final synchronized void _setLastWrittenOnDisk(Date aDate) {
		if (logger.isLoggable(Level.FINE) && aDate != null) {
			logger.fine("Resource " + this + "/" + hashCode() + " declared to be saved on disk on "
					+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(aDate));
		}
		_diskLastModifiedDate = null;
		_lastWrittenOnDisk = aDate;
	}

	public final synchronized Date _getLastWrittenOnDisk() {
		return getDiskLastModifiedDate();
	}

	public synchronized boolean hasMoreRecentThanExpectedDiskUpdate() {
		// if (logger.isLoggable(Level.FINEST))
		// logger.finest("Resource " + this + " has more recent than expected disk update ?");
		File f = getFile();
		if (f == null) {
			if (resourceFile == null) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Found resource " + this + " without file)");
				}
			}
			return false;
		} else if (!f.exists()) {
			return false;
		}
		if (!_isSaving) {
			Date fsDate = FileUtils.getDiskLastModifiedDate(f);
			Date knownDate = getDiskLastModifiedDate();
			if (fsDate.getTime() > knownDate.getTime() + ACCEPTABLE_FS_DELAY) {
				// _setLastWrittenOnDisk(FileUtils.getDiskLastModifiedDate(getFile()));
				_setLastWrittenOnDisk(fsDate);
				// if (logger.isLoggable(Level.FINE))
				// logger.fine("ID=" + getProject().getID() + " Resource " + this + "/" + hashCode()
				// + " has more recent than expected disk update "
				// + (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(knownDate) + " < "
				// + (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(fsDate));
				return true;
			}
		}
		return false;
	}

	/**
	 * This method should be used parsimoniously since RM will not detect disk updates. It should only be used in few cases, eg when
	 * converting resources so that RM don't complain about updates in files, or when managing disk update accepting
	 * 
	 */
	protected synchronized void resetDiskLastModifiedDate() {
		if (getFile() == null || !getFile().exists()) {
			if (getFile() != null) {
				logger.warning("resetDiskLastModifiedDate() called for non existant file: " + getFile().getAbsolutePath());
			} else {
				logger.warning("resetDiskLastModifiedDate() called for null file on resource " + this);
			}
			_setLastWrittenOnDisk(null);
		} else {
			_setLastWrittenOnDisk(new Date());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("ID=" + getProject().getID() + " Resource " + this + " resetDiskLastModifiedDate() "
						+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(_lastWrittenOnDisk));
			}
		}
	}

	protected FileWritingLock willWriteOnDisk() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("willWriteOnDisk()");
		}
		_isSaving = true;
		// This locking scheme was an attempt which seems to be unnecessary
		// Disactivated it. But kept for future needing if required
		// return new FileWritingLock();
		return null;
	}

	protected void hasWrittenOnDisk(FileWritingLock lock) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("hasWrittenOnDisk()");
		}
		if (lock != null) {
			lock.start();
		} else {
			notifyHasBeenWrittenOnDisk();
		}
	}

	protected void notifyHasBeenWrittenOnDisk() {
		resetDiskLastModifiedDate();
		_isSaving = false;
	}

	public class FileWritingLock extends Thread {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoFileResource.FileWritingLock.class.getPackage().getName());

		private Date _previousLastModified;

		private FileWritingLock() {
			super("FileWritingLock:" + getFile().getAbsolutePath());
			if (getFile().exists()) {
				_previousLastModified = FileUtils.getDiskLastModifiedDate(getFile());
				if (new Date().getTime() - _previousLastModified.getTime() < 1000) {
					// Last modified is this second: no way to know that file has been written,
					// Sets to null
					_previousLastModified = null;
				}
			} else {
				_previousLastModified = null;
			}
		}

		@Override
		public void run() {
			Date startChecking = new Date();
			logger.info("Checking that file " + getFile().getAbsolutePath() + " has been successfully written");

			boolean fileHasBeenWritten = false;

			while (new Date().getTime() <= startChecking.getTime() + ACCEPTABLE_FS_DELAY && !fileHasBeenWritten) {
				if (_previousLastModified == null) {
					fileHasBeenWritten = getFile().exists();
				} else {
					Date currentLastModifiedDate = FileUtils.getDiskLastModifiedDate(getFile());
					fileHasBeenWritten = currentLastModifiedDate.after(_previousLastModified);
				}
				if (!fileHasBeenWritten) {
					logger.info("Waiting file " + getFile().getAbsolutePath() + " to be written, thread " + this);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (!fileHasBeenWritten) {
				logger.warning("TIME-OUT occured while waiting file " + getFile().getAbsolutePath() + " to be written, thread " + this);
			} else {
				logger.info("File " + getFile().getAbsolutePath() + " has been written, thread " + this);
			}

			notifyHasBeenWrittenOnDisk();
		}

	}

	protected boolean isSaving() {
		return _isSaving;
	}

	/**
	 * Delete this resource by deleting the file
	 */
	@Override
	public synchronized void delete() {
		delete(true);
	}

	/**
	 * Delete this resource. Delete file is flag deleteFile is true.
	 */
	public synchronized void delete(boolean deleteFile) {
		super.delete();
		if (getFile() != null && getFile().exists() && deleteFile) {
			getProject().addToFilesToDelete(getFile());
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Will delete file " + getFile().getAbsolutePath() + " upon next save of RM");
			}
		}
	}

	public synchronized boolean hasWritePermission() {
		return getFile() == null || (!getFile().exists() || getFile().canWrite()) && getFile().getParentFile() != null
				&& (!getFile().getParentFile().exists() || getFile().getParentFile().canWrite());
	}

	/**
	 * Returns boolean indicating if merge scheme is implemented for this kind of resource If set to true, method #performMerge() must be
	 * overriden in related class
	 * 
	 * @return
	 */
	public boolean implementsResourceMerge() {
		return false;
	}

	/**
	 * Perform merge between data read from a updated resource on disk and data stored in memory NOTE: must be overriden in subclasses if
	 * relevant
	 */
	public synchronized void performMerge() throws FlexoException {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Merge NOT implemented for resource type " + getResourceType().getName());
		}
	}

	/**
	 * Perform update of specified resource data from data read from a updated resource on disk
	 */
	public synchronized void performDiskUpdate() throws FlexoException {
		forwardPropagateUpdate();
	}

	protected synchronized void forwardPropagateUpdate() throws FlexoException {
		Vector resourcesThatAreConcerned = getResourceUpdateNotificationTargets();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Concerned resources: " + resourcesThatAreConcerned);
		}
		for (Enumeration en = resourcesThatAreConcerned.elements(); en.hasMoreElements();) {
			FlexoResource next = (FlexoResource) en.nextElement();
			try {
				next.update();
			} catch (ResourceDependencyLoopException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Loop in dependant resources of " + this + "!", e);
				}
			} catch (FileNotFoundException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "File not found for " + next + "!", e);
				}
			}
		}
	}

	private Vector<FlexoResource> getResourceUpdateNotificationTargets() {
		Vector<FlexoResource> returned = new Vector<FlexoResource>();
		Vector<FlexoResource> requesters = new Vector<FlexoResource>();
		addResourceUpdateNotificationTargets(returned, requesters);
		return returned;
	}

	private void addResourceUpdateNotificationTargets(Vector<FlexoResource> targetList, Vector<FlexoResource> requesters) {
		if (requesters.contains(this)) {
			return;
		}
		requesters.add(this);
		Enumeration en = getSynchronizedResources().elements();
		while (en.hasMoreElements()) {
			FlexoFileResource res = (FlexoFileResource) en.nextElement();
			if (!targetList.contains(res) && propagateToSynchronizedResourceForResourceUpdateNotification(res)) {
				targetList.add(res);
			}
			// No recursion with synchronized resources
			// res.addResourceUpdateNotificationTargets(targetList,requesters);
		}
		en = getAlteredResources().elements();
		while (en.hasMoreElements()) {
			FlexoFileResource res = (FlexoFileResource) en.nextElement();
			if (!targetList.contains(res) && propagateToAlteredResourceForResourceUpdateNotification(res)) {
				targetList.add(res);
			}
			res.addResourceUpdateNotificationTargets(targetList, requesters);
		}
	}

	/**
	 * Override this if required. Default behaviour is to return false, and thus, never propagate a ResourceUpdateNotification through
	 * synchronized resources
	 * 
	 * @param originResource
	 * @param targetResource
	 * @return
	 */
	protected boolean propagateToSynchronizedResourceForResourceUpdateNotification(FlexoResource targetResource) {
		return false;
	}

	/**
	 * Override this if required. Default behaviour is to propagate ResourceUpdateNotification to all loaded storage resources
	 * 
	 * @param originResource
	 * @param targetResource
	 * @return
	 */
	protected boolean propagateToAlteredResourceForResourceUpdateNotification(FlexoResource targetResource) {
		return targetResource instanceof FlexoStorageResource && ((FlexoStorageResource) targetResource).isLoaded();
	}

}
