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
package org.openflexo.foundation.cg;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Format;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.RemoveGeneratedCodeRepository;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.cg.dm.CGFileCreated;
import org.openflexo.foundation.cg.dm.CGFileDeleted;
import org.openflexo.foundation.cg.dm.CGReleaseRegistered;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.cg.dm.CGRepositoryDisconnected;
import org.openflexo.foundation.cg.dm.CGStructureRefreshed;
import org.openflexo.foundation.cg.dm.CustomTemplateRepositoryChanged;
import org.openflexo.foundation.cg.dm.LogAdded;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.cg.version.action.RegisterNewCGRelease;
import org.openflexo.foundation.cg.version.action.RevertRepositoryToVersion;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException;
import org.openflexo.foundation.rm.ProjectExternalRepository;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.localization.FlexoLocalization;

public abstract class GenerationRepository extends CGObject {

	private static final Logger logger = Logger.getLogger(GenerationRepository.class.getPackage().getName());

	private ProjectExternalRepository _sourceCodeRepository;
	private Hashtable<String, CGSymbolicDirectory> _symbolicDirectories;
	private Vector<CGFile> _files;
	private boolean _manageHistory = true;
	private String _displayName;
	private Vector<CGRelease> _releases;

	private Object projectGenerator;

	private CGVersionIdentifier DEFAULT_VERSION_ID = CGVersionIdentifier.DEFAULT_VERSION_ID();

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public GenerationRepository(GeneratedCodeBuilder builder) {
		this(builder.generatedCode);
		initializeDeserialization(builder);
	}

	public GenerationRepository(GeneratedOutput generatedCode) {
		super(generatedCode);
		_symbolicDirectories = new Hashtable<String, CGSymbolicDirectory>();
		_files = new Vector<CGFile>();
		_releases = new Vector<CGRelease>();
	}

	public GenerationRepository(GeneratedOutput generatedCode, String name, File directory) throws DuplicateCodeRepositoryNameException {
		this(generatedCode);
		if (getProject().getExternalRepositoryWithKey(name) != null) {
			throw new DuplicateCodeRepositoryNameException(this, name);
		}
		if (!directory.exists()) {
			directory.mkdirs();
		}
		_sourceCodeRepository = getProject().setDirectoryForRepositoryName(name, directory);
	}

	/**
	 * Overrides finalizeDeserialization
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#finalizeDeserialization(java.lang.Object)
	 */
	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		for (CGFile file : _files) {
			if (file.getResource() == null) {
				file.delete();
			}
		}
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(RemoveGeneratedCodeRepository.actionType);
		returned.add(RegisterNewCGRelease.actionType);
		returned.add(RevertRepositoryToVersion.actionType);
		return returned;
	}

	@Override
	public String getFullyQualifiedName() {
		return (getGeneratedCode() != null ? getGeneratedCode().getFullyQualifiedName() : "null") + "." + getName();
	}

	@Override
	public String getName() {
		return getSourceCodeRepository().getIdentifier();
	}

	@Override
	public void setName(String name) throws DuplicateCodeRepositoryNameException {
		if (_sourceCodeRepository != null) {
			if ((name != null) && !name.equals(getName())) {
				throw new IllegalStateException("External repository identifier cannot change !");
				// if (getProject().getExternalRepositoryWithKey(name) != null) {
				// throw new DuplicateCodeRepositoryNameException(this,name);
				// }
				// _sourceCodeRepository.setIdentifier(name);
			}
		} else {
			_sourceCodeRepository = getProject().getExternalRepositoryWithKey(name);
		}
	}

	public String getDisplayName() {
		if (_displayName == null) {
			_displayName = getName();
		}
		return _displayName;
	}

	public void setDisplayName(String displayName) {
		String oldValue = this._displayName;
		_displayName = displayName;
		setChanged();
		notifyObservers(new CGDataModification("displayName", oldValue, displayName));
	}

	public File getDirectory() {
		return getSourceCodeRepository().getDirectory();
	}

	public void setDirectory(File aDirectory) {
		File oldValue = getSourceCodeRepository().getDirectory();
		getSourceCodeRepository().setDirectory(aDirectory);
		connect();
		setChanged();
		notifyObservers(new CGDataModification("directory", oldValue, aDirectory));
	}

	public void delete(boolean deleteFiles) {
		delete(null, deleteFiles);
	}

	public void delete(FlexoProgress progress, boolean deleteFiles) {
		Vector<CGFile> files = (Vector<CGFile>) getFiles().clone();
		for (CGFile file : files) {
			if (progress != null) {
				progress.setProgress(FlexoLocalization.localizedForKey("deleting") + " " + file.getFileName());
			}
			file.delete(deleteFiles);
		}
		if (deleteFiles) {
			getProject().addToFilesToDelete(getCodeGenerationWorkingDirectory());
		}
		if (deleteFiles) {
			getProject().deleteFilesToBeDeleted();
		}

		deleteExternalRepositories();

		// GPO: the following loop attempts to remove any left-behind resource of this repository.
		// The purpose of doing this is to avoid generated resources from being without a proper CGFile
		// causing them to irremediably be unusable

		Enumeration<FlexoResource> en = ((Hashtable<String, FlexoResource>) getProject().getResources().clone()).elements();
		while (en.hasMoreElements()) {
			FlexoResource element = en.nextElement();
			if (element instanceof CGRepositoryFileResource) {
				CGRepositoryFileResource file = (CGRepositoryFileResource) element;
				if (file.getCGFile() == null) {
					if (file.getName() != null && file.getName().indexOf('.') > -1) {
						if (file.getName().substring(0, file.getName().indexOf('.')).equals(getName())) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Found a resource without CGFile that is supposed to be in this repository: "
										+ file.getFullyQualifiedName() + " I will delete it now");
							}
							file.delete(deleteFiles);
						}
					}
				}
			}
		}

		try {
			getProject().getFlexoRMResource().saveResourceData();
		} catch (SaveXMLResourceException e) {
			e.printStackTrace();
			logger.warning("Unexpected " + e);
		} catch (SaveResourcePermissionDeniedException e) {
			e.printStackTrace();
			logger.warning("Unexpected " + e);
		} catch (SaveResourceException e) {
			e.printStackTrace();
			logger.warning("Unexpected " + e);
		}

		// Too dangerous (if for example, the repository points to '/' or 'c:\' the consequences could be disatrous
		/*if (deleteFiles) // Really delete files on disk
			FileUtils.recursiveDeleteFile(getDirectory());*/

		// rebuildStructure();
		super.delete();
		getGeneratedCode().removeFromGeneratedRepositories(this);
		projectGenerator = null;
	}

	protected void deleteExternalRepositories() {
		if (getSourceCodeRepository() != null) {
			getProject().removeFromExternalRepositories(getSourceCodeRepository());
		}
	}

	public CGSymbolicDirectory getSymbolicDirectory(CGFile file) {
		if (this == file.getRepository()) {
			String filePath = file.getResource().getResourceFile().getRelativePath();
			Enumeration<CGSymbolicDirectory> en = _symbolicDirectories.elements();
			while (en.hasMoreElements()) {
				CGSymbolicDirectory reply = en.nextElement();
				if (filePath.indexOf(reply.getDirectory().getRelativePath()) > -1) {
					return reply;
				}
			}
		}
		return null;
	}

	public abstract TargetType getTarget();

	public abstract Format getFormat();

	public abstract void setFormat(Format format);

	public synchronized Hashtable<String, CGSymbolicDirectory> getSymbolicDirectories() {
		return _symbolicDirectories;
	}

	public void setSymbolicDirectories(Hashtable<String, CGSymbolicDirectory> symbolicDirectories) {
		_symbolicDirectories = symbolicDirectories;
	}

	public void setSymbolicDirectoryForKey(CGSymbolicDirectory dir, String name) {
		dir.setName(name);
		dir.setGeneratedCodeRepository(this);
		_symbolicDirectories.put(name, dir);
		setChanged();
		notifyObservers(new CGDataModification("symbolicDirectories", null, dir));
	}

	public void removeSymbolicDirectoryWithKey(String name) {
		CGSymbolicDirectory old = _symbolicDirectories.get(name);
		if (old != null) {
			old.setGeneratedCodeRepository(null);
		}
		_symbolicDirectories.remove(name);
		setChanged();
		notifyObservers(new CGDataModification("symbolicDirectories", old, null));
	}

	public CGSymbolicDirectory getSymbolicDirectoryNamed(String name) {
		return getSymbolicDirectoryNamed(name, true);
	}

	public CGSymbolicDirectory getSymbolicDirectoryNamed(String name, boolean createIfMissing) {
		CGSymbolicDirectory returned = _symbolicDirectories.get(name);
		if (returned == null && createIfMissing) {
			returned = new CGSymbolicDirectory(this, name, new FlexoProjectFile(getProject(), getSourceCodeRepository(), "/."));
			setSymbolicDirectoryForKey(returned, name);
			returned.getDirectory().getFile().mkdirs();
		}
		return returned;
	}

	public ProjectExternalRepository getSourceCodeRepository() {
		if (_sourceCodeRepository == null) {
			if (_displayName != null) {
				_sourceCodeRepository = getProject().getExternalRepositoryWithKey(_displayName);
			}
			if (_sourceCodeRepository == null) {
				_sourceCodeRepository = getProject().setDirectoryForRepositoryName(_displayName != null ? _displayName : "Default",
						new File(System.getProperty("user.home") + "/" + getProject().getProjectName() + "/" + getTarget().getName()));
			}
		}
		return _sourceCodeRepository;
	}

	@Override
	public boolean isEnabled() {
		return (isConnected && getSourceCodeRepository().isConnected());
	}

	public boolean isConnected() {
		if (!connectionStatusInitialized) {
			connectionStatusInitialized = true;
			isConnected = (getSourceCodeRepository() != null && getSourceCodeRepository().isConnected());
		}
		return isConnected;
	}

	private boolean isConnected = true;
	private boolean connectionStatusInitialized = false;

	public boolean connect() {
		if (getSourceCodeRepository() != null) {
			if (!getSourceCodeRepository().isConnected()) {
				getSourceCodeRepository().setDirectory(getSourceCodeRepository().getDirectory());
			}
			if (getSourceCodeRepository().isConnected()) {
				isConnected = true;
				for (CGFile file : getFiles()) {
					if (file.getResource() != null) {
						file.getResource().activate();
					}
				}
				setChanged();
				notifyObservers(new CGRepositoryConnected(this));
				return true;
			}
		}
		// Cannot connect: repository directory is not valid
		return false;
	}

	public void disconnect() {
		isConnected = false;
		for (CGFile file : getFiles()) {
			if (file.getResource() != null) {
				file.getResource().desactivate();
			}
		}
		setChanged();
		notifyObservers(new CGRepositoryDisconnected(this));
	}

	public Vector<CGFile> getFiles() {
		return _files;
	}

	public void setFiles(Vector<CGFile> files) {
		_files = files;
		structureNeedsToBeRecomputed = true;
		setChanged();
		notifyObservers(new CGDataModification("files", null, files));
	}

	public void addToFiles(CGFile aFile) {
		if (aFile.getResourceName() == null) {
			logger.warning("file: " + aFile + " : cannot add a file with null resource");
		} else {
			aFile.setRepository(this);
			_files.add(aFile);
			structureNeedsToBeRecomputed = true;
			setChanged();
			notifyObservers(new CGFileCreated(aFile));
		}
	}

	public void removeFromFiles(CGFile aFile) {
		aFile.setRepository(null);
		_files.remove(aFile);
		structureNeedsToBeRecomputed = true;
		setChanged();
		notifyObservers(new CGFileDeleted(aFile));
	}

	private boolean structureNeedsToBeRecomputed = true;

	@Override
	public boolean hasGenerationErrors() {
		ensureStructureIsUpToDate();
		return hasGenerationErrors;
	}

	@Override
	public boolean needsRegeneration() {
		ensureStructureIsUpToDate();
		return needsRegeneration;
	}

	@Override
	public boolean needsModelReinjection() {
		ensureStructureIsUpToDate();
		return needsModelReinjection;
	}

	@Override
	public GenerationStatus getGenerationStatus() {
		ensureStructureIsUpToDate();
		return generationStatus;
	}

	public void ensureStructureIsUpToDate() {
		if (structureNeedsToBeRecomputed) {
			rebuildStructure();
		}
	}

	public void refresh() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("refresh()");
		}
		structureNeedsToBeRecomputed = true;
		setChanged(false);
		notifyObservers(new CGStructureRefreshed());
	}

	private int generationModifiedCount;
	private int diskModifiedCount;
	private int conflictsCount;
	private int needsMemoryGenerationCount;
	private int needsModelReinjectionCount;
	private int errorsCount;
	private boolean isRebuildingStructure = false;

	private synchronized void rebuildStructure() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Re-compute structure for repository " + getName());
		}
		if (isRebuildingStructure) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Trying to rebuild structure while already rebuilding. This would result in duplicate elements within the built vectors, returning now! You MUST find the cause of this problem.");
			}
			return;
		}
		isRebuildingStructure = true;
		structureNeedsToBeRecomputed = false;
		hasGenerationErrors = false;
		needsRegeneration = false;
		needsModelReinjection = false;
		generationStatus = GenerationStatus.UpToDate;
		generationModifiedCount = 0;
		diskModifiedCount = 0;
		conflictsCount = 0;
		needsMemoryGenerationCount = 0;
		needsModelReinjectionCount = 0;
		errorsCount = 0;
		try {
			for (CGSymbolicDirectory symbDir : getSymbolicDirectories().values()) {
				symbDir.clearStructure();
			}
			for (CGFile file : _files) {
				if (file.getSymbolicDirectory() != null) {
					file.getSymbolicDirectory().addToStructure(file);
				}
				if (file.hasGenerationErrors()) {
					// logger.info("File "+file+" has generation error");
					CGPathElement current = file;
					while (current.getParent() != null) {
						// logger.info("Set object "+current.getParent()+" to
						// have generation errors");
						((CGObject) current.getParent()).hasGenerationErrors = true;
						current = current.getParent();
					}
					hasGenerationErrors = true;
					errorsCount++;
				}
				if (file.needsRegeneration()) {
					CGPathElement current = file;
					while (current.getParent() != null) {
						((CGObject) current.getParent()).needsRegeneration = true;
						current = current.getParent();
					}
					needsRegeneration = true;
					needsMemoryGenerationCount++;
				}
				if (file instanceof ModelReinjectableFile && ((ModelReinjectableFile) file).needsModelReinjection()) {
					CGPathElement current = file;
					while (current.getParent() != null) {
						((CGObject) current.getParent()).needsModelReinjection = true;
						current = current.getParent();
					}
					needsModelReinjection = true;
					needsModelReinjectionCount++;
				}
				if ((file.getGenerationStatus().isGenerationModified())
						|| (file.getGenerationStatus() == GenerationStatus.ConflictingMarkedAsMerged)) {
					CGPathElement current = file;
					while (current.getParent() != null) {
						if (((CGObject) current.getParent()).generationStatus == GenerationStatus.UpToDate) {
							((CGObject) current.getParent()).generationStatus = GenerationStatus.GenerationModified;
						} else if (((CGObject) current.getParent()).generationStatus == GenerationStatus.DiskModified) {
							((CGObject) current.getParent()).generationStatus = GenerationStatus.ConflictingUnMerged;
						}
						current = current.getParent();
					}
					if (generationStatus == GenerationStatus.UpToDate) {
						generationStatus = GenerationStatus.GenerationModified;
					} else if (generationStatus == GenerationStatus.DiskModified) {
						generationStatus = GenerationStatus.ConflictingUnMerged;
					}
					generationModifiedCount++;
				}
				if ((file.getGenerationStatus().isDiskModified())) {
					CGPathElement current = file;
					while (current.getParent() != null) {
						if (((CGObject) current.getParent()).generationStatus == GenerationStatus.UpToDate) {
							((CGObject) current.getParent()).generationStatus = GenerationStatus.DiskModified;
						} else if (((CGObject) current.getParent()).generationStatus == GenerationStatus.GenerationModified) {
							((CGObject) current.getParent()).generationStatus = GenerationStatus.ConflictingUnMerged;
						}
						current = current.getParent();
					}
					if (generationStatus == GenerationStatus.UpToDate) {
						generationStatus = GenerationStatus.DiskModified;
					} else if (generationStatus == GenerationStatus.GenerationModified) {
						generationStatus = GenerationStatus.ConflictingUnMerged;
					}
					diskModifiedCount++;
				}
				if (file.getGenerationStatus() == GenerationStatus.ConflictingUnMerged) {
					CGPathElement current = file;
					while (current.getParent() != null) {
						((CGObject) current.getParent()).generationStatus = GenerationStatus.ConflictingUnMerged;
						current = current.getParent();
					}
					generationStatus = GenerationStatus.ConflictingUnMerged;
					conflictsCount++;
				}
			}
		} finally {
			isRebuildingStructure = false;
		}
	}

	public void notifyLogAdded() {
		setChanged();
		notifyObservers(new LogAdded());
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	@Override
	public boolean isContainedIn(CGObject obj) {
		if (obj instanceof GeneratedOutput) {
			return (obj == getGeneratedCode());
		} else if (obj instanceof GenerationRepository) {
			return (obj == this);
		}
		return false;
	}

	private CustomCGTemplateRepository _preferredTemplateRepository;
	private String _preferredTemplateRepositoryName;

	public CustomCGTemplateRepository getPreferredTemplateRepository() {
		if (_preferredTemplateRepository == null) {
			if (!isDeserializing() && _preferredTemplateRepositoryName != null) {
				setPreferredTemplateRepositoryName(_preferredTemplateRepositoryName);
			}
		}
		return _preferredTemplateRepository;
	}

	public void setPreferredTemplateRepository(CustomCGTemplateRepository preferredTemplateRepository) {
		CustomCGTemplateRepository old = _preferredTemplateRepository;
		if (old == null || old != preferredTemplateRepository) {
			_preferredTemplateRepository = preferredTemplateRepository;
			_preferredTemplateRepositoryName = (preferredTemplateRepository != null ? preferredTemplateRepository.getName() : null);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new CustomTemplateRepositoryChanged(old, preferredTemplateRepository));
				rebuildStructure();
			}
		}
	}

	public String getPreferredTemplateRepositoryName() {
		if (_preferredTemplateRepository != null) {
			return _preferredTemplateRepository.getName();
		}
		return null;
	}

	public void setPreferredTemplateRepositoryName(String aName) {
		if (!isDeserializing()) {
			CustomCGTemplateRepository newRepository = getGeneratedCode().getTemplates().getCustomCGTemplateRepositoryForName(aName);
			if (newRepository == null) {
				logger.warning("Could not find template repository named " + aName);
			} else {
				setPreferredTemplateRepository(newRepository);
			}
		} else {
			_preferredTemplateRepositoryName = aName;
		}
	}

	public void updatePreferredTemplateRepository() {
		CustomCGTemplateRepository newRepository = getGeneratedCode().getTemplates().getCustomCGTemplateRepositoryForName(
				_preferredTemplateRepositoryName);
		if (newRepository != null) {
			setPreferredTemplateRepository(newRepository);
		}
	}

	private File _codeGenerationWorkingDirectory;

	public File getCodeGenerationWorkingDirectory() {
		if (_codeGenerationWorkingDirectory == null) {
			_codeGenerationWorkingDirectory = new File(ProjectRestructuration.getExpectedGeneratedCodeDirectory(getProject()
					.getProjectDirectory()), getName());
		}
		return _codeGenerationWorkingDirectory;
	}

	public boolean getManageHistory() {
		return _manageHistory;
	}

	public void setManageHistory(boolean manageHistory) {
		_manageHistory = manageHistory;
	}

	private boolean _releasesAreSorted = false;

	public Vector<CGRelease> getReleases() {
		return _releases;
	}

	public void setReleases(Vector<CGRelease> releases) {
		_releases = releases;
		_releasesAreSorted = false;
		setChanged();
	}

	public void addToReleases(CGRelease release) {
		release.setCGRepository(this);
		_releases.add(release);
		_releasesAreSorted = false;
		setChanged();
		notifyObservers(new CGReleaseRegistered(release));
	}

	public void removeFromReleases(CGRelease release) {
		release.setCGRepository(null);
		_releases.remove(release);
		_releasesAreSorted = false;
		setChanged();
	}

	public CGRelease getLastRelease() {
		ensureReleasesAreSorted();
		if (_releases.size() > 0) {
			return _releases.lastElement();
		}
		return null;
	}

	public void ensureReleasesAreSorted() {
		if (!_releasesAreSorted) {
			Collections.sort(_releases, CGRelease.COMPARATOR);
			_releasesAreSorted = true;
		}
	}

	public CGVersionIdentifier getLastReleaseVersionIdentifier() {
		if (getLastRelease() != null) {
			return getLastRelease().getVersionIdentifier();
		}
		return DEFAULT_VERSION_ID;
	}

	public int getConflictsCount() {
		return conflictsCount;
	}

	public int getDiskModifiedCount() {
		return diskModifiedCount;
	}

	public int getErrorsCount() {
		return errorsCount;
	}

	public int getGenerationModifiedCount() {
		return generationModifiedCount;
	}

	public int getNeedsMemoryGenerationCount() {
		return needsMemoryGenerationCount;
	}

	public int getNeedsModelReinjectionCount() {
		return needsModelReinjectionCount;
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public Object getProjectGenerator() {
		return projectGenerator;
	}

	public void setProjectGenerator(Object projectGenerator) {
		this.projectGenerator = projectGenerator;
	}

}
