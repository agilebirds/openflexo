package org.openflexo.foundation.resource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.toolbox.FileUtils;

/**
 * Default implementation for {@link FlexoFileResource}
 * 
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Sylvain
 * 
 */
public abstract class FlexoFileResourceImpl<RD extends ResourceData<RD>> extends FlexoResourceImpl<RD> implements FlexoFileResource<RD> {

	/**
	 * This constant traduces the delay accepted for the File System to effectively write a file on disk after the date it was requested. If
	 * file is written after this delay, the ResourceManager will interprete it as a concurrent file modification requiring to be handled
	 * properly. In fact, this is not a big problem but resource management may be affected.
	 */
	public static final long ACCEPTABLE_FS_DELAY = 4000;

	/**
	 * This variable is only used to be reset when we have written on disk.
	 */
	private Date _diskLastModifiedDate;

	/**
	 * Flag indicating if resource is currently saving
	 */
	private boolean _isSaving = false;

	/**
	 * This is the date known by Flexo (with milliseconds precision) at which we have written on the disk.
	 */
	private Date _lastWrittenOnDisk;

	public synchronized boolean hasWritePermission() {
		return getFile() == null || (!getFile().exists() || getFile().canWrite()) && getFile().getParentFile() != null
				&& (!getFile().getParentFile().exists() || getFile().getParentFile().canWrite());
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
		}
	}

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

	@Override
	public boolean renameFileTo(String name) throws InvalidFileNameException, IOException {
		File newFile = new File(getFile().getParentFile(), name);
		if (getFile().exists()) {
			FileUtils.rename(getFile(), newFile);
			if (getFile().exists()) {
				getFile().delete();
			}
			resetDiskLastModifiedDate();
		}
		/*if (getResourceFile().getExternalRepository() != null) {
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
		}*/
		return true;
	}

	/**
	 * Returns the last modified date of the underlying file that Flexo has computed (or remembered) so that we get milliseconds precision
	 * 
	 * @return the last modified date known by Flexo with milliseconds precision.
	 */
	public final synchronized Date getDiskLastModifiedDate() {
		if ((_diskLastModifiedDate == null || _diskLastModifiedDate.getTime() == 0 || !getFile().exists()) && !_isSaving) {
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

	/**
	 * Delete this resource by deleting the file
	 */
	@Override
	public boolean delete() {
		if (hasWritePermission()) {
			return delete(true);
		} else {
			logger.warning("Delete requested for READ-ONLY file resource " + this);
			return false;
		}
	}

	/**
	 * Delete this resource. Delete file is flag deleteFile is true.
	 */
	@Override
	public boolean delete(boolean deleteFile) {
		if (hasWritePermission()) {
			if (super.delete()) {
				if (getFile() != null && getFile().exists() && deleteFile) {
					getServiceManager().getResourceManager().addToFilesToDelete(getFile());
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Will delete file " + getFile().getAbsolutePath() + " upon next save of RM");
					}
				}
				return true;
			}
			return false;
		} else {
			logger.warning("Delete requested for READ-ONLY file resource " + this);
			return false;
		}
	}

}
