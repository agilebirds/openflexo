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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.cg.dm.CGContentRegenerated;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.cg.dm.CGFileChangedOnDisk;
import org.openflexo.foundation.cg.dm.CGFileDeleted;
import org.openflexo.foundation.cg.dm.CGFileDiskVersionAccepted;
import org.openflexo.foundation.cg.dm.CGFileEdited;
import org.openflexo.foundation.cg.dm.CGFileGenerationUnchanged;
import org.openflexo.foundation.cg.dm.CGFileHistoryRefreshed;
import org.openflexo.foundation.cg.dm.CGFileMarkedAsMerged;
import org.openflexo.foundation.cg.dm.CGFileReleased;
import org.openflexo.foundation.cg.dm.CGFileRevertToSaved;
import org.openflexo.foundation.cg.dm.CGFileSavedAfterEdition;
import org.openflexo.foundation.cg.dm.CGFileWillRegenerate;
import org.openflexo.foundation.cg.dm.CGFileWritenOnDisk;
import org.openflexo.foundation.cg.dm.MarkAsDontGenerateDataModification;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.TemplateFileNotification;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.OpenTemplateFileInNewWindow;
import org.openflexo.foundation.cg.templates.action.RedefineCustomTemplateFile;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.rm.cg.SaveGeneratedResourceIOException;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileFormat;

public class CGFile extends CGObject implements CGPathElement {

	private static final Logger logger = Logger.getLogger(CGFile.class.getPackage().getName());

	private GenerationRepository _repository;
	private CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> _resource;
	private CGSymbolicDirectory _symbolicDirectory;
	private String _symbolicDirectoryName;
	private boolean _markedForDeletion = false;
	private boolean _markedAsMerged = false;
	private boolean _markedAsDoNotGenerate = false;

	private Vector<String> usedTemplates;

	/**
	 * Create a new CGFile.
	 */
	public CGFile(GeneratedCodeBuilder builder) {
		this(builder.generatedCode);
		initializeDeserialization(builder);
	}

	/**
	 * Create a new CGFile.
	 */
	public CGFile(GeneratedSourcesBuilder builder) {
		this(builder.generatedSources);
		initializeDeserialization(builder);
	}

	public CGFile(GeneratedOutput generatedCode) {
		super(generatedCode);
		usedTemplates = new Vector<String>();
	}

	public CGFile(GenerationRepository repository, CGRepositoryFileResource resource) {
		this(repository.getGeneratedCode());
		setRepository(repository);
		_resource = resource;
	}

	@Override
	public String getFullyQualifiedName() {
		return getRepository().getFullyQualifiedName() + "." + _resource.getResourceIdentifier();
	}

	public FileFormat getFileFormat() {
		return getResource().getResourceFormat();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "generated_code_file";
	}

	public CGRepositoryFileResource _getResource() {
		return _resource;
	}

	public CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> getResource() {
		return _resource;
	}

	public void setResource(CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> resource) {
		_resource = resource;
	}

	public String getResourceIdentifier() {
		return _resource.getResourceIdentifier();
	}

	public void setResourceIdentifier(String resourceIdentifier) {
		_resource = (CGRepositoryFileResource) getProject().resourceForKey(resourceIdentifier);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.CG_FILE_INSPECTOR;
	}

	public GenerationRepository getRepository() {
		return _repository;
	}

	public void setRepository(GenerationRepository repository) {
		_repository = repository;
	}

	public CGSymbolicDirectory getSymbolicDirectory() {
		if (_symbolicDirectory == null && _symbolicDirectoryName != null && getRepository() != null) {
			setSymbolicDirectory(getRepository().getSymbolicDirectoryNamed(_symbolicDirectoryName));
		}
		return _symbolicDirectory;
	}

	public void setSymbolicDirectory(CGSymbolicDirectory symbolicDirectory) {
		_symbolicDirectory = symbolicDirectory;
	}

	public String getSymbolicDirectoryName() {
		if (_symbolicDirectory != null) {
			return _symbolicDirectory.getName();
		}
		return _symbolicDirectoryName;
	}

	public void setSymbolicDirectoryName(String aSymbolicDirectoryName) {
		if (getRepository() != null) {
			setSymbolicDirectory(getRepository().getSymbolicDirectoryNamed(aSymbolicDirectoryName));
		} else {
			_symbolicDirectoryName = aSymbolicDirectoryName;
		}
	}

	public String getFileName() {
		if (getResource() != null && getResource().getResourceFile() != null && getResource().getResourceFile().getFile() != null) {
			return getResource().getResourceFile().getFile().getName();
		}
		return "???";
	}

	public String getPathName() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getResourceFile().getStringRepresentation();
		}
		return "???";
	}

	public Date getLastGenerationDate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getLastGenerationDate();
		}
		return null;
	}

	public Date getDiskLastModifiedDate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getDiskLastModifiedDate();
		}
		return null;
	}

	public Date getLastAcceptingDate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getLastAcceptingDate();
		}
		return null;
	}

	public Date getLastGenerationCheckedDate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getLastGenerationCheckedDate();
		}
		return null;
	}

	public Date getMemoryLastGenerationDate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getMemoryLastGenerationDate();
		}
		return null;
	}

	@Override
	public Date getLastUpdate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getLastUpdate();
		}
		return null;
	}

	public String getResourceName() {
		if (_resource != null) {
			return _resource.getResourceIdentifier();
		}
		return null;
	}

	public void setResourceName(String aResourceName) {
		if (getProject() != null) {
			_resource = (CGRepositoryFileResource) getProject().resourceForKey(aResourceName);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found resource " + _resource);
			}
			if (_resource != null) {
				_resource.setCGFile(this);
			} else {
				logger.warning("Cannot find resource " + aResourceName);
			}
		}
	}

	public GeneratedResourceData getGeneratedResourceData() {
		if (getResource() != null) {
			return getResource().getGeneratedResourceData();
		}
		return null;
	}

	public boolean isCodeGenerationAvailable() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return (!isMarkedForDeletion() && isCodeGenerationAvailable() || isMarkedForDeletion()) && !getMarkedAsDoNotGenerate();
	}

	@Override
	public boolean hasGenerationErrors() {
		if (getMarkedAsDoNotGenerate()) {
			return false;
		}
		/*if (getRepository() != null)
			getRepository().ensureStructureIsUpToDate();*/
		return hasGenerationErrors;
	}

	@Override
	public boolean needsRegeneration() {
		if (getMarkedAsDoNotGenerate()) {
			return false;
		}
		return needsMemoryGeneration();
	}

	@Override
	public GenerationStatus getGenerationStatus() {
		if (getMarkedAsDoNotGenerate()) {
			return GenerationStatus.UpToDate;
		}
		if (getResource() != null) {
			return getResource().getGenerationStatus();
		}
		return GenerationStatus.Unknown;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	public boolean isContainedInFolder(CGFolder folder) {
		CGPathElement current = this;
		while (current != folder && current.getParent() != null) {
			current = current.getParent();
		}
		return current == folder;
	}

	@Override
	public boolean isContainedIn(CGObject obj) {
		if (obj instanceof GeneratedOutput) {
			return obj == getGeneratedCode();
		} else if (obj instanceof GenerationRepository) {
			return obj == _repository;
		} else if (obj instanceof CGSymbolicDirectory) {
			return obj == getSymbolicDirectory();
		} else if (obj instanceof CGFolder) {
			return isContainedInFolder((CGFolder) obj);
		} else if (obj instanceof CGFile) {
			return obj == this;
		}
		return false;
	}

	private CGPathElement _parentFolder;

	@Override
	public CGPathElement getParent() {
		return _parentFolder;
	}

	public void setParent(CGPathElement parentFolder) {
		_parentFolder = parentFolder;
	}

	private static final Vector<CGFolder> NO_SUB_FOLDERS = new Vector<CGFolder>();
	private static final Vector<CGFile> NO_FILES = new Vector<CGFile>();

	@Override
	public Vector<CGFolder> getSubFolders() {
		return NO_SUB_FOLDERS;
	}

	@Override
	public CGFolder getDirectoryNamed(String aName) {
		return null;
	}

	@Override
	public synchronized Vector<CGFile> getFiles() {
		return NO_FILES;
	}

	public Vector<String> getUsedTemplates() {
		if (getResource() != null && getResource().getGenerator() != null && getResource().getGenerator().getUsedTemplates().size() > 0) {
			usedTemplates.clear();
			for (CGTemplate file : getResource().getGenerator().getUsedTemplates()) {
				usedTemplates.add(file.getRelativePath());
			}
		}
		return usedTemplates;
	}

	public void setUsedTemplates(Vector<String> usedTemplates) {
		this.usedTemplates = usedTemplates;
	}

	public void addToUsedTemplates(String template) {
		this.usedTemplates.add(template);
	}

	public void removeFromUsedTemplates(String template) {
		this.usedTemplates.remove(template);
	}

	@Override
	public String toString() {
		return getClass().getName() + "/" + getResourceName();
	}

	// ==========================================================================
	// ================================= Deletion ==============================
	// ==========================================================================

	public boolean isMarkedForDeletion() {
		return _markedForDeletion;
	}

	public void setMarkedForDeletion(boolean markedForDeletion) {
		logger.info("-----------> Hop, je supprime " + this);
		_markedForDeletion = markedForDeletion;
	}

	@Override
	public final boolean delete() {
		return delete(false);
	}

	public final boolean delete(boolean deleteFiles) {
		if (getRepository() != null) {
			getRepository().removeFromFiles(this);
		}
		if (getResource() != null) {
			if (getResource().getGenerator() != null) {
				getResource().getGenerator().deleteObserver(this);
			}
			if (!getResource().isDeleted()) {
				getResource().delete(deleteFiles);
			}
		}
		super.delete();
		setChanged();
		notifyObservers(new CGFileDeleted(this));
		deleteObservers();
		return true;
	}

	public void writeModifiedFile() throws SaveResourceException, FlexoException {
		if (getMarkedAsDoNotGenerate()) {
			return;
		}
		if (isMarkedForDeletion()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Delete modified file: " + getFileName());
			}
			getResource().delete(true);
			delete();
		} else {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Write modified file: "
						+ (getResource() != null ? getResource().getFile() != null ? getResource().getFile().getAbsolutePath()
								: "file is null" : "resource is null"));
			}
			boolean updated = getResource().ensureGenerationIsUpToDate();
			if (getResource().isForceRegenerate()
					|| !getResource().getDependentResources().elements(false, getProject().getDependancyScheme()).hasMoreElements()
					&& !updated || !getResource().doesGenerationKeepFileUnchanged() || !getResource().getFile().exists()) {
				getResource().generate();
			} else {
				getResource().setLastGenerationDate(new Date());
			}
			getResource().notifyResourceHasBeenWritten();
			setChanged();
			notifyObservers(new CGFileWritenOnDisk(this));
			getRepository().refresh();
		}
		// Date date2 = new Date();
		// logger.info("Time for writeModifiedFile() "+getFileName()+": "+(date2.getTime()-date1.getTime())+" ms");
	}

	public void acceptDiskVersion() throws SaveGeneratedResourceIOException {
		getResource().acceptDiskVersion();
		setChanged();
		notifyObservers(new CGFileDiskVersionAccepted(this));
	}

	public void dismissWhenUnchanged() {
		if (getResource().doesGenerationKeepFileUnchanged()) {
			logger.info("File " + getFileName() + " seems to be unchanged, dismiss this file");
			getResource().setLastGenerationCheckedDate(new Date());
			if (getResource().isForceRegenerate()) {
				getResource().setForceRegenerate(false);
			}
			getResource().notifyResourceDismissal();
			setChanged();
			notifyObservers(new CGFileGenerationUnchanged(this));
		} else {
			logger.info("File " + getFileName() + " has really changed, does nothing");
		}
	}

	public void overrideWith(ContentSource contentSource, boolean doItNow) throws SaveResourceException, FlexoException {
		if (getGeneratedResourceData() != null && getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			((AbstractGeneratedFile) getGeneratedResourceData()).overrideWith(contentSource);
		}
		setForceRegenerate(true);
		if (doItNow) {
			writeModifiedFile();
		}
	}

	public void cancelOverriding() {
		if (isOverrideScheduled()) {
			if (getGeneratedResourceData() != null && getGeneratedResourceData() instanceof AbstractGeneratedFile) {
				((AbstractGeneratedFile) getGeneratedResourceData()).cancelOverriding();
			}
			setForceRegenerate(false);
		}
	}

	// ==========================================================================
	// ============================= Observing stuff ===========================
	// ==========================================================================

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (getMarkedAsDoNotGenerate()) {
			return;
			// if (logger.isLoggable(Level.INFO)) logger.info ("CGFile : RECEIVED "+dataModification+" for "+observable);
		}

		// First notify this generation for generated resource data
		// to be aware of that modif (and update their merge objects)
		if (dataModification instanceof CGContentRegenerated) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("CGContentRegenerated received in CGFile");
			}

			_markedAsMerged = false;
			getResource().notifyRegenerated((CGContentRegenerated) dataModification);
		}

		// Forward notifications to module view and other observers
		setChanged();
		notifyObservers(dataModification);

		if (dataModification instanceof TemplateFileNotification) {
			if (getRepository() != null) {
				// It looks like something else makes the refresh. eventually we'll remove this commented code
				// getRepository().invalidateStructure();
				// _markedAsMerged = false;
				// logger.info("Il semblerait que le fichier "+getFileName()+" ait ete affecte par la modification sur le template "+dataModification.newValue());
			}
		}

	}

	// ==========================================================================
	// ================= Templates management (from inspector) =================
	// ==========================================================================

	public void redefineTemplate(CGTemplateFile template) {
		if (redefineTemplateActionizer != null) {
			redefineTemplateActionizer.run(template, null);
		}
		/*logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
		// TODO: Please implement this better later
		// Used editor will be null
		RedefineCustomTemplateFile action = RedefineCustomTemplateFile.actionType.makeNewAction(template, null);
		action.setContext(this);
		action.doAction();*/
	}

	public boolean isApplicationTemplate(CGTemplate template) {
		return template.isApplicationTemplate();
	}

	public void editTemplate(CGTemplateFile template) {
		if (editCustomTemplateActionizer != null) {
			editCustomTemplateActionizer.run(template, null);
		}
		/*logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
		// TODO: Please implement this better later
		// Used editor will be null
		EditCustomTemplateFile action = EditCustomTemplateFile.actionType.makeNewAction(template, null);
		action.doAction();*/
	}

	public boolean isCustomTemplate(CGTemplate template) {
		return template.isCustomTemplate();
	}

	public void showTemplate(CGTemplate template) {
		if (showTemplateActionizer != null) {
			showTemplateActionizer.run(template, null);
		}
		/*logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
		// TODO: Please implement this better later
		// Used editor will be null
		OpenTemplateFileInNewWindow action = OpenTemplateFileInNewWindow.actionType.makeNewAction(template, null);
		action.doAction();*/
	}

	public static FlexoActionizer<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject> showTemplateActionizer;
	public static FlexoActionizer<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject> editCustomTemplateActionizer;
	public static FlexoActionizer<RedefineCustomTemplateFile, CGTemplate, CGTemplate> redefineTemplateActionizer;

	public boolean isShowable(CGTemplate template) {
		return true;
	}

	public boolean hasVersionOnDisk() {
		if (getResource() != null && getResource().getGeneratedResourceData() != null
				&& getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			return ((AbstractGeneratedFile) getResource().getGeneratedResourceData()).hasVersionOnDisk();
		}
		return false;
	}

	public interface FileContentEditor {
		public String getEditedContentForKey(String contentKey);

		public void setEditedContent(CGFile file);
	}

	private FileContentEditor _editor;

	public boolean isEdited() {
		return _editor != null;
	}

	public void edit(FileContentEditor editor) {
		_editor = editor;
		editor.setEditedContent(this);
		setChanged();
		notifyObservers(new CGFileEdited(this));
	}

	public void save() throws SaveResourceException {
		_markedAsMerged = false;
		getResource().saveEditedVersion(_editor);
		_editor = null;
		setChanged();
		notifyObservers(new CGFileSavedAfterEdition(this));
	}

	public void revertToSaved() {
		logger.info("Not implemented yet");
		_editor = null;
		setChanged();
		notifyObservers(new CGFileRevertToSaved(this));
	}

	public void notifyResourceChangedOnDisk() {
		_markedAsMerged = false;
		setChanged();
		notifyObservers(new CGFileChangedOnDisk(this));
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

	/**
	 * Return flag indicating if this file has been explicitely marked as merged (in case of this file has merges). This flag is volontary
	 * non-persistent.
	 * 
	 * @return a boolean value
	 */
	public boolean isMarkedAsMerged() {
		return _markedAsMerged;
	}

	/**
	 * Sets flag indicating if this file has been explicitely marked as merged (in case of this file has merges). This flag is volontary
	 * non-persistent.
	 */
	public void setMarkedAsMerged(boolean markedAsMerged) {
		_markedAsMerged = markedAsMerged;
		setChanged();
		notifyObservers(new CGFileMarkedAsMerged(this));
	}

	public void setForceRegenerate(boolean forceRegenerate) {
		getResource().setForceRegenerate(forceRegenerate);
		setChanged();
		notifyObservers(new CGFileWillRegenerate(this));
	}

	public boolean isForceRegenerate() {
		if (getResource() == null) {
			return false;
		}
		return getResource().isForceRegenerate();
	}

	public boolean isOverrideScheduled() {
		return isForceRegenerate() && getGeneratedResourceData() != null && getGeneratedResourceData() instanceof AbstractGeneratedFile
				&& ((AbstractGeneratedFile) getGeneratedResourceData()).isOverrideScheduled();
	}

	public ContentSource getScheduledOverrideVersion() {
		if (isOverrideScheduled() && getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			return ((AbstractGeneratedFile) getGeneratedResourceData()).getScheduledOverrideVersion();
		}
		return null;
	}

	/**
	 * Returns flag indicating if merge for generation is actually raising conflicts (collision in changes)
	 */
	public boolean isGenerationConflicting() {
		if (getResource() != null && getResource().getGeneratedResourceData() != null
				&& getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			return ((AbstractGeneratedFile) getResource().getGeneratedResourceData()).isGenerationConflicting();
		}
		return false;
	}

	// Override it in sub-classes
	public boolean needsMemoryGeneration() {
		return false;
	}

	public boolean isTriviallyMergable() {
		return getGenerationStatus() == GenerationStatus.ConflictingUnMerged && getResource() != null
				&& getResource().getGeneratedResourceData() != null
				&& getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile
				&& ((AbstractGeneratedFile) getResource().getGeneratedResourceData()).isTriviallyMergable();
	}

	public void releaseAs(CGRelease newCGRelease) {
		if (getResource() != null && getResource().getGeneratedResourceData() != null
				&& getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			((AbstractGeneratedFile) getGeneratedResourceData()).getHistory().storeCurrentFileInHistoryAs(
					newCGRelease.getVersionIdentifier());
			setChanged();
			notifyObservers(new CGFileReleased(this));
		}

	}

	public void notifyHistoryRefreshed() {
		setChanged();
		notifyObservers(new CGFileHistoryRefreshed(this));
	}

	// Override it in sub-classes
	@Override
	public boolean needsModelReinjection() {
		return false;
	}

	public boolean supportModelReinjection() {
		return false;
	}

	// ==================================================================
	// ======================== Debug management ========================
	// ==================================================================

	public String getLastGenerationDateAsString() {
		if (getLastGenerationDate() != null) {
			if (getLastGenerationDate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastGenerationDate());
		}
		return "???";
	}

	public String getDiskLastModifiedDateAsString() {
		if (getDiskLastModifiedDate() != null) {
			if (getDiskLastModifiedDate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getDiskLastModifiedDate());
		}
		return "???";
	}

	public String getLastAcceptingDateAsString() {
		if (getLastAcceptingDate() != null) {
			if (getLastAcceptingDate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastAcceptingDate());
		}
		return "???";
	}

	public String getLastGenerationCheckedDateAsString() {
		if (getLastGenerationCheckedDate() != null) {
			if (getLastGenerationCheckedDate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastGenerationCheckedDate());
		}
		return "???";
	}

	public String getMemoryLastGenerationDateAsString() {
		if (getMemoryLastGenerationDate() != null) {
			if (getMemoryLastGenerationDate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getMemoryLastGenerationDate());
		}
		return "???";
	}

	public String getNeedsUpdateReason() {
		if (getMarkedAsDoNotGenerate()) {
			return null;
		}
		if (getResource() != null) {
			return getResource().getNeedsUpdateReason();
		}
		return null;
	}

	public void notifyGenerationStatusChange(GenerationStatus oldStatus, GenerationStatus newStatus) {
		setChanged(false);
		notifyObservers(new GenerationStatusModification(oldStatus, newStatus));
	}

	public void notifyFileNameChanged(String old, String newName) {
		setChanged();
		notifyObservers(new CGDataModification("fileName", old, newName));
	}

	public boolean getMarkedAsDoNotGenerate() {
		return _markedAsDoNotGenerate;
	}

	public void setMarkedAsDoNotGenerate(boolean asDoNotGenerate) {
		if (asDoNotGenerate == _markedAsDoNotGenerate) {
			return;
		}
		_markedAsDoNotGenerate = asDoNotGenerate;
		setChanged();
		notifyObservers(new MarkAsDontGenerateDataModification(asDoNotGenerate));
	}

	public void clearParsingData() {
		// override in subclasses
	}

}
