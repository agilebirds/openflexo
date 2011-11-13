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
package org.openflexo.foundation.rm.cg;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.Merge;
import org.openflexo.diff.merge.MergedDocumentType;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.cg.dm.CGContentRegenerated;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

public abstract class ASCIIFile extends AbstractGeneratedFile {
	static final Logger logger = Logger.getLogger(ASCIIFile.class.getPackage().getName());

	private DiffSource currentDiskContent = null;
	private DiffSource lastGeneratedContent = null;
	private DiffSource lastAcceptedContent = null;
	private Merge _generationMerge;
	private ResultFileMerge _resultFileMerge;

	private boolean hasDiskVersion = false;

	/**
	 * 
	 */
	public ASCIIFile(File f) {
		super(f);
	}

	/**
	 * 
	 */
	public ASCIIFile() {
		super();
	}

	/**
	 * Returns flag indicating if merge for generation is actually raising conflicts (collision in changes)
	 */
	@Override
	public boolean isGenerationConflicting() {
		if (getGenerationMerge() == null) {
			return false;
		}
		return getGenerationMerge().isReallyConflicting();
	}

	/**
	 * @throws SaveGeneratedResourceIOException
	 * @throws SaveGeneratedResourceException
	 * @throws UnresolvedConflictException
	 */
	@Override
	public void writeToFile(File aFile) throws SaveGeneratedResourceIOException, SaveGeneratedResourceException,
			UnresolvedConflictException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("***** writeToFile() called in " + getFlexoResource().getFileName() + " file " + aFile.getAbsolutePath() + " on "
					+ (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(new Date()));
		}

		File path = aFile.getParentFile();

		// Creates directory when non existant
		if (!path.exists()) {
			path.mkdirs();
		}

		// Save content
		try {

			// If no content available, throw exception
			if (getContentToWriteOnDisk() == null) {
				logger.warning("getContentToWriteOnDisk() is null ! for "
						+ (getFlexoResource() != null ? getFlexoResource().getFullyQualifiedName() : getFile()));
				throw new SaveGeneratedResourceException(getFlexoResource(), "Cannot access content to write on disk: "
						+ (getFlexoResource() != null ? getFlexoResource().getFullyQualifiedName() : getFile()));
			}

			// If current generation is conflicting and not marked as merged, don't do it and throw exception
			if (hasDiskVersion && (getFlexoResource().getGenerationStatus() == GenerationStatus.ConflictingUnMerged)
					&& !(isOverrideScheduled())) {
				throw new UnresolvedConflictException(getFlexoResource());
			}

			boolean needsNotifyEndOfSaving = false;
			FileWritingLock lock = null;
			if (!getFlexoResource().isSaving()) {
				logger.warning("writeToFile() called in " + getFlexoResource().getFileName() + " outside of RM-saving scheme");
				lock = getFlexoResource().willWriteOnDisk();
				needsNotifyEndOfSaving = true;
			}

			// Save file in history if edited since last generation
			if (fileOnDiskHasBeenEdited() && manageHistory()) {
				getHistory().storeCurrentFileInHistory(CGVersionIdentifier.VersionType.DiskUpdate);
			}

			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Really writing " + aFile.getAbsolutePath() + " to disk");
			}
			// Save to file the new generation
			FileUtils.saveToFile(aFile, getContentToWriteOnDisk(), getEncoding());

			if (!aFile.exists()) {
				throw new SaveGeneratedResourceIOException(getFlexoResource(), null);
			}

			if (needsNotifyEndOfSaving) {
				getFlexoResource().hasWrittenOnDisk(lock);
			}
			if ((getProject() != null) && getProject().computeDiff) {
				currentDiskContent = new DiffSource(getContentToWriteOnDisk());
				lastAcceptedContent = new DiffSource(getContentToWriteOnDisk());
			}
			// Save file in history
			if (manageHistory()) {
				getHistory().storeCurrentFileInHistory(CGVersionIdentifier.VersionType.GenerationIteration);
			}
			// Now save last generated
			saveLastGeneratedFile();

			// Now save last accepted
			saveLastAcceptedFile();

			// If this was an overriding, discard it
			_overrideIsScheduled = false;
			if ((getProject() != null) && getProject().computeDiff) {
				rebuildMerges();
			}

			hasDiskVersion = true;
		}

		catch (IOException e) {
			throw new SaveGeneratedResourceIOException(getFlexoResource(), e);
		}
	}

	private void saveLastGeneratedFile() throws IOException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to save " + getLastGeneratedFile().getAbsolutePath());
		}

		if (!getLastGeneratedFile().getParentFile().exists()) {
			getLastGeneratedFile().getParentFile().mkdirs();
		}
		if (getCurrentGeneration() != null) {
			try {
				FileUtils.saveToFile(getLastGeneratedFile(), getCurrentGeneration(), getEncoding());
				if ((getProject() != null) && getProject().computeDiff) {
					lastGeneratedContent = new DiffSource(getCurrentGeneration());
				}
			} catch (IOException e) {
				logger.warning("IOException occured when trying to write " + getLastGeneratedFile().getAbsolutePath() + "parent="
						+ getLastGeneratedFile().getParentFile());
				throw e;
			}
		} else {
			lastGeneratedContent = new DiffSource(FlexoLocalization.localizedForKey("unable_to_access_last_generated_file") + "\n"
					+ FlexoLocalization.localizedForKey("file") + " : " + getLastGeneratedFile().getAbsolutePath());
		}

	}

	public void saveAsLastGenerated(String newContent) throws IOException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to save " + getLastGeneratedFile().getAbsolutePath());
		}

		if (!getLastGeneratedFile().getParentFile().exists()) {
			getLastGeneratedFile().getParentFile().mkdirs();
		}
		try {
			FileUtils.saveToFile(getLastGeneratedFile(), newContent, getEncoding());
			lastGeneratedContent = new DiffSource(newContent);
		} catch (IOException e) {
			logger.warning("IOException occured when trying to write " + getLastGeneratedFile().getAbsolutePath() + "parent="
					+ getLastGeneratedFile().getParentFile());
			throw e;
		}
	}

	private void saveLastAcceptedFile() throws IOException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to save " + getLastAcceptedFile().getAbsolutePath());
		}

		if (!getLastAcceptedFile().getParentFile().exists()) {
			getLastAcceptedFile().getParentFile().mkdirs();
		}

		FileUtils.saveToFile(getLastAcceptedFile(), getLastAcceptedContent(), getEncoding());
	}

	protected File getLastAcceptedFile() {
		if (getFlexoResource() == null) {
			return null;
		}
		return getFlexoResource().getLastAcceptedFile();
	}

	protected File getLastGeneratedFile() {
		if (getFlexoResource() == null) {
			return null;
		}
		return getFlexoResource().getLastGeneratedFile();
	}

	@Override
	public void load() throws LoadGeneratedResourceIOException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Loading " + getFlexoResource().getFileName());
		}
		try {
			currentDiskContent = new DiffSource(FileUtils.fileContents(getFile(), getEncoding()));
			if (getLastGeneratedFile() != null) {
				if (!getLastGeneratedFile().exists()) {
					logger.warning("File does not exist: " + getLastGeneratedFile().getAbsolutePath() + ", creating empty one");
					FileUtils.saveToFile(getLastGeneratedFile(), currentDiskContent.getSourceString(), getEncoding());
				}
				lastGeneratedContent = new DiffSource(FileUtils.fileContents(getLastGeneratedFile(), getEncoding()));
			} else {
				logger.warning("Could not find file null");
			}
			if (getLastAcceptedFile() != null) {
				if (!getLastAcceptedFile().exists()) {
					logger.warning("File does not exist: " + getLastAcceptedFile().getAbsolutePath() + ", creating empty one");
					FileUtils.saveToFile(getLastAcceptedFile(), currentDiskContent.getSourceString(), getEncoding());
				}
				lastAcceptedContent = new DiffSource(FileUtils.fileContents(getLastAcceptedFile(), getEncoding()));
			} else {
				if (getLastAcceptedFile() != null) {
					logger.warning("Could not find file " + getLastAcceptedFile().getAbsolutePath());
				} else {
					logger.warning("Could not find file null.");
				}
			}
			hasDiskVersion = true;
		} catch (IOException e) {
			throw new LoadGeneratedResourceIOException(getFlexoResource(), e);
		}
		updateHistory();

	}

	/**
	 * Returns current generation This is "pure" generation (direct output from the generators): does NOT contain merge with changes
	 * registered for last accepted version
	 * 
	 * @return a String representation
	 */
	public abstract String getCurrentGeneration();

	/**
	 * Returns current generated content This content is obtained by merging current generation with changes registered for last accepted
	 * version
	 * 
	 * @return a String representation
	 */
	public String getGeneratedMergedContent() {
		if (!hasDiskVersion) {
			return getCurrentGeneration();
		} else {
			return getGenerationMerge().getMergedSource().getSourceString();
		}
	}

	/**
	 * Returns content to write on disk This content is the merge from the current generated and merge content with content extracted from
	 * the modified file on disk In case of an overriding, this is the overriden version
	 * 
	 * @return
	 */
	public String getContentToWriteOnDisk() {
		if (!hasDiskVersion) {
			return getCurrentGeneration();
		} else {
			if (_overrideIsScheduled) {
				return getContent(_overridenVersion);
			}
			if ((getProject() != null) && getProject().computeDiff) {
				return getResultFileMerge().getMergedSource().getSourceString();
			} else {
				return getCurrentGeneration();
			}
		}
	}

	public String getContent(ContentSource contentSource) {
		if (contentSource.getType() == ContentSourceType.PureGeneration) {
			return getCurrentGeneration();
		} else if (contentSource.getType() == ContentSourceType.GeneratedMerge) {
			return getGeneratedMergedContent();
		} else if (contentSource.getType() == ContentSourceType.ResultFileMerge) {
			return getContentToWriteOnDisk();
		} else if (contentSource.getType() == ContentSourceType.ContentOnDisk) {
			return getCurrentDiskContent();
		} else if (contentSource.getType() == ContentSourceType.LastGenerated) {
			return getLastGeneratedContent();
		} else if (contentSource.getType() == ContentSourceType.LastAccepted) {
			return getLastAcceptedContent();
		} else if (contentSource.getType() == ContentSourceType.HistoryVersion) {
			return getHistoryContent(contentSource.getVersion());
		}
		return null;
	}

	protected String getHistoryContent(CGVersionIdentifier versionId) {
		try {
			if (getHistory().versionWithId(versionId) != null) {
				return getHistory().versionWithId(versionId).getContent();
			} else {
				return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
			}
		} catch (IOFlexoException e) {
			e.printStackTrace();
			return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
		}

	}

	@Override
	public final void generate() throws FlexoException {
		if (!(getFlexoResource() instanceof GenerationAvailableFileResourceInterface)) {
			throw new NotImplementedException("version_without_code_generator");
		}
		// Calling this method assume that this file was never generated.
		// So, merges are not necessary, and file is simply overriden.
		if (getFlexoResource().getGenerator().getGeneratedCode() == null) {
			if (getFlexoResource().getGenerator().getGenerationException() == null) {
				logger.warning("Both GeneratedCode and exception are null: this is should never happen !");
			} else {
				throw (FlexoException) getFlexoResource().getGenerator().getGenerationException();
			}
		}
	}

	@Override
	public final void regenerate() throws FlexoException {
		if (!(getFlexoResource() instanceof GenerationAvailableFileResourceInterface)) {
			throw new NotImplementedException("version_without_code_generator");
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("regenerate() called in " + getClass().getName());
		}

		// OK, generator has performed its job, i have now to handle merges

		if ((lastGeneratedContent == null) && (getProject() != null) && getProject().computeDiff) {
			throw new LoadGeneratedResourceIOException(getFlexoResource(), "Unable to access last generated content");
		}
		if ((lastAcceptedContent == null) && (getProject() != null) && getProject().computeDiff) {
			throw new LoadGeneratedResourceIOException(getFlexoResource(), "Unable to access last accepted content");
		}

		// Normally it's ok.
		// I don't do it now, otherwise, i will erase merging selections
		// rebuildMerges();

	}

	private void rebuildMerges() {
		rebuildGenerationMerge();
		rebuildResultFileMerge();
	}

	private void rebuildGenerationMerge() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("rebuildGenerationMerge() called in " + getFlexoResource().getFileName());
		}
		if (_generationMerge != null) {
			_generationMerge.delete();
			_generationMerge = null;
		}

		if (getCurrentGeneration() != null) {
			_generationMerge = new Merge(_getLastGeneratedContent(), new DiffSource(getCurrentGeneration()), _getLastAcceptedContent(),
					getMergedDocumentType());
			// logger.info("Resource "+getFlexoResource().getFileName()+" build generation merge");
			// logger.info("_getLastGeneratedContent()="+_getLastGeneratedContent().getSourceString());
			// logger.info("getCurrentGeneration()="+getCurrentGeneration());
			// logger.info("_getLastAcceptedContent()="+_getLastAcceptedContent().getSourceString());
		}

	}

	private void rebuildResultFileMerge() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("rebuildResultFileMerge() called in " + getFlexoResource().getFileName());
		}
		if (_resultFileMerge != null) {
			_resultFileMerge.delete();
			_resultFileMerge = null;
		}

		if (getGenerationMerge() != null) {
			_resultFileMerge = new ResultFileMerge(_getLastAcceptedContent(), getGenerationMerge(), getProject().computeDiff
					|| hasCurrentDiskContent() ? currentDiskContent : new DiffSource(""));
		}

	}

	public static class ResultFileMerge extends Merge implements Observer {
		private final Merge _generationMerge;

		public ResultFileMerge(DiffSource lastAcceptedContent, Merge generationMerge, DiffSource currentDiskContent) {
			super(lastAcceptedContent, generationMerge.getMergedSource(), currentDiskContent, generationMerge.getDocumentType());
			_generationMerge = generationMerge;
			_generationMerge.addObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			// Generation merge changed
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("update() in ResultFileMerge, recompute merge");
			}
			recompute();
		}

	}

	public String getCurrentDiskContent() {
		if (currentDiskContent != null) {
			return currentDiskContent.getSourceString();
		} else {
			logger.warning("getCurrentDiskContent() called for null content: " + getFlexoResource().getFileName());
			return "Unable to access current disk content for " + getFlexoResource().getFileName();
		}
	}

	public boolean hasCurrentDiskContent() {
		return currentDiskContent != null;
	}

	public boolean hasLastAcceptedContent() {
		return lastAcceptedContent != null;
	}

	public String getLastAcceptedContent() {
		if (lastAcceptedContent != null) {
			return lastAcceptedContent.getSourceString();
		}
		return FlexoLocalization.localizedForKey("unable_to_access_last_accepted_file")
				+ "\n"
				+ (getFlexoResource().getLastGeneratedFile() != null ? (FlexoLocalization.localizedForKey("file") + " : " + getFlexoResource()
						.getLastGeneratedFile().getAbsolutePath()) : (FlexoLocalization.localizedForKey("file") + " "
						+ FlexoLocalization.localizedForKey("of_resource") + " " + getFlexoResource()));
	}

	protected DiffSource _getLastGeneratedContent() {
		if (lastGeneratedContent == null) {
			if (getCurrentGeneration() != null) {
				lastGeneratedContent = new DiffSource(getCurrentGeneration());
			}
		}
		return lastGeneratedContent;

	}

	protected DiffSource _getLastAcceptedContent() {
		if (lastAcceptedContent == null) {
			if (hasCurrentDiskContent() || getProject().computeDiff) {
				lastAcceptedContent = new DiffSource(getCurrentDiskContent());
			} else {
				lastAcceptedContent = new DiffSource("");
			}
		}
		return lastAcceptedContent;
	}

	public String getLastGeneratedContent() {
		if (lastGeneratedContent != null) {
			return lastGeneratedContent.getSourceString();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Last generated content not found for resource " + getFlexoResource());
		}
		return FlexoLocalization.localizedForKey("unable_to_access_last_generated_file")
				+ "\n"
				+ (getFlexoResource().getLastGeneratedFile() != null ? (FlexoLocalization.localizedForKey("file") + " : " + getFlexoResource()
						.getLastGeneratedFile().getAbsolutePath()) : (FlexoLocalization.localizedForKey("file") + " "
						+ FlexoLocalization.localizedForKey("of_resource") + " " + getFlexoResource()));
	}

	@Override
	public boolean hasVersionOnDisk() {
		return hasDiskVersion;
	}

	public void notifyVersionChangedOnDisk(String newDiskContent) {
		currentDiskContent = new DiffSource(newDiskContent);
		rebuildMerges();
	}

	@Override
	public void notifyVersionChangedOnDisk() {
		try {
			load();
		} catch (LoadGeneratedResourceIOException e) {
			e.printStackTrace();
		}
		rebuildMerges();
	}

	public Merge getGenerationMerge() {
		if (_generationMerge == null) {
			rebuildGenerationMerge();
		}
		return _generationMerge;
	}

	public ResultFileMerge getResultFileMerge() {
		if (_resultFileMerge == null) {
			rebuildResultFileMerge();
		}
		return _resultFileMerge;
	}

	@Override
	public void notifyRegenerated(CGContentRegenerated notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyRegenerated() called in " + getFlexoResource().getFileName());
		}
		if (!getFlexoResource().hasGenerationError()) {
			rebuildMerges();
		}
	}

	@Override
	public void acceptDiskVersion() throws SaveGeneratedResourceIOException {
		// Save file in history
		if (manageHistory()) {
			getHistory().storeCurrentFileInHistory(CGVersionIdentifier.VersionType.DiskUpdate);
		}
		lastAcceptedContent = new DiffSource(getCurrentDiskContent());
		rebuildMerges();
		try {
			lastAcceptedContent = new DiffSource(getCurrentDiskContent());
			saveLastAcceptedFile();
		} catch (IOException e) {
			throw new SaveGeneratedResourceIOException(getFlexoResource(), e);
		}
	}

	private boolean _overrideIsScheduled = false;
	private ContentSource _overridenVersion;

	@Override
	public void overrideWith(ContentSource version) {
		_overrideIsScheduled = true;
		_overridenVersion = version;
	}

	@Override
	public boolean isOverrideScheduled() {
		return _overrideIsScheduled;
	}

	@Override
	public void cancelOverriding() {
		_overrideIsScheduled = false;
		_overridenVersion = null;
	}

	@Override
	public ContentSource getScheduledOverrideVersion() {
		return _overridenVersion;
	}

	@Override
	public boolean doesGenerationKeepFileUnchanged() {
		if (getResultFileMerge() == null) {
			return false;
		}
		// logger.info("doesGenerationKeepFileUnchanged() called for "+getFlexoResource().getFileName()+
		// " changes="+_generationMerge.getChanges().size());
		return (getResultFileMerge().getChanges().size() == 0);
	}

	@Override
	public boolean isTriviallyMergable() {
		return ((getGenerationMerge() != null) && (!getGenerationMerge().isReallyConflicting()) && (getResultFileMerge() != null) && (!getResultFileMerge()
				.isReallyConflicting()));
	}

	@Override
	public boolean areAllConflictsResolved() {
		return ((getGenerationMerge() != null) && (getGenerationMerge().isResolved()) && (getResultFileMerge() != null) && (getResultFileMerge()
				.isResolved()));
	}

	public MergedDocumentType getMergedDocumentType() {
		return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat());
	}

	public String getEncoding() {
		return "UTF-8";
	}
}