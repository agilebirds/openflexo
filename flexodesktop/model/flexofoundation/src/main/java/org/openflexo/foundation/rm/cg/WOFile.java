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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.MergedDocumentType;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.cg.dm.CGContentRegenerated;
import org.openflexo.foundation.cg.generator.GeneratedComponent;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;

public class WOFile extends AbstractGeneratedFile {
	private static final Logger logger = Logger.getLogger(WOFile.class.getPackage().getName());

	private WOHTMLFile _htmlFile;
	private WODFile _wodFile;
	private WOOFile _wooFile;
	private boolean hasDiskVersion = false;

	/**
	 * 
	 */
	public WOFile(File f) {
		this();
		setFile(f);
	}

	/**
	 * 
	 */
	public WOFile() {
		super();
	}

	@Override
	public void setFile(File file) {
		super.setFile(file);
		String name = getFile().getName().substring(0, getFile().getName().indexOf(".wo"));
		File htmlFile = new File(getFile(), name + ".html");
		File wodFile = new File(getFile(), name + ".wod");
		File wooFile = new File(getFile(), name + ".woo");
		_htmlFile = new WOHTMLFile(htmlFile);
		_wodFile = new WODFile(wodFile);
		_wooFile = new WOOFile(wooFile);
	}

	/**
	 * @throws SaveGeneratedResourceIOException
	 */
	@Override
	public void writeToFile(File aFile) throws SaveGeneratedResourceIOException, SaveGeneratedResourceException,
			UnresolvedConflictException {
		// Save file in history if edited since last generation
		if (fileOnDiskHasBeenEdited() && manageHistory()) {
			getHistory().storeCurrentFileInHistory(CGVersionIdentifier.VersionType.DiskUpdate);
		}

		boolean needsNotifyEndOfSaving = false;
		FileWritingLock lock = null;
		if (!getFlexoResource().isSaving()) {
			logger.warning("writeToFile() called in " + getFlexoResource().getFileName() + " outside of RM-saving scheme");
			lock = getFlexoResource().willWriteOnDisk();
			needsNotifyEndOfSaving = true;
		}

		// Save to file the new generation
		if (!aFile.exists()) {
			aFile.mkdirs();
		}
		_htmlFile.writeToFile(new File(aFile, _htmlFile.getFile().getName()));
		_wodFile.writeToFile(new File(aFile, _wodFile.getFile().getName()));
		_wooFile.writeToFile(new File(aFile, _wooFile.getFile().getName()));

		if (needsNotifyEndOfSaving) {
			getFlexoResource().hasWrittenOnDisk(lock);
		}

		// Save file in history
		if (manageHistory()) {
			getHistory().storeCurrentFileInHistory(CGVersionIdentifier.VersionType.GenerationIteration);
		}

	}

	@Override
	public final void generate() throws FlexoException {
		if (!(getFlexoResource() instanceof GenerationAvailableFileResourceInterface)) {
			throw new NotImplementedException("version_without_code_generator");
		}
		_htmlFile.generate();
		_wodFile.generate();
		_wooFile.generate();
	}

	@Override
	public final void regenerate() throws FlexoException {
		if (!(getFlexoResource() instanceof GenerationAvailableFileResourceInterface)) {
			throw new NotImplementedException("version_without_code_generator");
		}
		_htmlFile.regenerate();
		_wodFile.regenerate();
		_wooFile.regenerate();
	}

	@Override
	public void load() throws LoadGeneratedResourceIOException {
		_htmlFile.load();
		_wodFile.load();
		_wooFile.load();
		updateHistory();
		hasDiskVersion = true;
	}

	@Override
	public void setFlexoResource(CGRepositoryFileResource resource) throws DuplicateResourceException {
		super.setFlexoResource(resource);
		_htmlFile.setFlexoResource(resource);
		_wodFile.setFlexoResource(resource);
		_wooFile.setFlexoResource(resource);
	}

	@Override
	public WOFileResource getFlexoResource() {
		return (WOFileResource) super.getFlexoResource();
	}

	public WOHTMLFile getHTMLFile() {
		return _htmlFile;
	}

	public WODFile getWODFile() {
		return _wodFile;
	}

	public WOOFile getWOOFile() {
		return _wooFile;
	}

	@Override
	public boolean hasVersionOnDisk() {
		return hasDiskVersion;
	}

	public boolean hasLastAcceptedContent() {
		return _htmlFile.hasLastAcceptedContent() && _wodFile.hasLastAcceptedContent() && _wooFile.hasLastAcceptedContent();
	}

	public void notifyVersionChangedOnDisk(String newDiskHTMLContent, String newDiskWODContent, String newDiskWOOContent) {
		_htmlFile.notifyVersionChangedOnDisk(newDiskHTMLContent);
		_wodFile.notifyVersionChangedOnDisk(newDiskWODContent);
		_wooFile.notifyVersionChangedOnDisk(newDiskWOOContent);
	}

	@Override
	public void notifyVersionChangedOnDisk() {
		_htmlFile.notifyVersionChangedOnDisk();
		_wodFile.notifyVersionChangedOnDisk();
		_wooFile.notifyVersionChangedOnDisk();
	}

	@Override
	public void notifyRegenerated(CGContentRegenerated notification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyRegenerated() called in " + getClass().getName());
		}
		_htmlFile.notifyRegenerated(notification);
		_wodFile.notifyRegenerated(notification);
		_wooFile.notifyRegenerated(notification);
	}

	@Override
	public void acceptDiskVersion() throws SaveGeneratedResourceIOException {
		// Save file in history if edited since last generation
		if (manageHistory()) {
			getHistory().storeCurrentFileInHistory(CGVersionIdentifier.VersionType.DiskUpdate);
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("acceptDiskVersion() called in " + getClass().getName());
		}
		_htmlFile.acceptDiskVersion();
		_wodFile.acceptDiskVersion();
		_wooFile.acceptDiskVersion();
	}

	/**
	 * Returns flag indicating if merge for generation is actually raising conflicts (collision in changes)
	 */
	@Override
	public boolean isGenerationConflicting() {
		return (_htmlFile.isGenerationConflicting() || _wodFile.isGenerationConflicting() || _wooFile.isGenerationConflicting());
	}

	@Override
	public boolean doesGenerationKeepFileUnchanged() {
		return (_htmlFile.doesGenerationKeepFileUnchanged() && _wodFile.doesGenerationKeepFileUnchanged() && _wooFile
				.doesGenerationKeepFileUnchanged());
	}

	@Override
	public void overrideWith(ContentSource version) {
		_htmlFile.overrideWith(version);
		_wodFile.overrideWith(version);
		_wooFile.overrideWith(version);
	}

	@Override
	public boolean isOverrideScheduled() {
		return (_htmlFile.isOverrideScheduled() && _wodFile.isOverrideScheduled() && _wooFile.isOverrideScheduled());
	}

	@Override
	public void cancelOverriding() {
		_htmlFile.cancelOverriding();
		_wodFile.cancelOverriding();
		_wooFile.cancelOverriding();
	}

	@Override
	public ContentSource getScheduledOverrideVersion() {
		ContentSource returned = _htmlFile.getScheduledOverrideVersion();
		if (_wodFile.getScheduledOverrideVersion() != returned) {
			logger.warning("Inconsistent data in scheduled version");
		}
		if (_wooFile.getScheduledOverrideVersion() != returned) {
			logger.warning("Inconsistent data in scheduled version");
		}
		return returned;
	}

	@Override
	public boolean isTriviallyMergable() {
		return (_htmlFile.isTriviallyMergable() && _wodFile.isTriviallyMergable() && _wooFile.isTriviallyMergable());
	}

	@Override
	public boolean areAllConflictsResolved() {
		return (_htmlFile.areAllConflictsResolved() && _wodFile.areAllConflictsResolved() && _wooFile.areAllConflictsResolved());
	}

	/*public void generate() throws GenerationException 
	{
		regenerate();
	}

	public void regenerate() throws GenerationException 
	{
		if (getGenerator().getGeneratedCode() == null) {
			if (getGenerator().getGenerationException() == null) {
				if(logger.isLoggable(Level.WARNING))
					logger.warning("Generated code is null and exception is also null: this is strange !("+getFlexoResource().getName()+")");
			}
			else {
				throw (GenerationException)getGenerator().getGenerationException();
			}
		}
		else {
			setHtmlContent(getGenerator().getGeneratedCode().html());
			setWodContent(getGenerator().getGeneratedCode().wod());
			setWooContent(getGenerator().getGeneratedCode().woo());
		}
	}*/

	public class WOHTMLFile extends ASCIIFile {

		public WOHTMLFile(File f) {
			super(f);
		}

		public WOHTMLFile() {
			super();
		}

		@Override
		public String getCurrentGeneration() {
			if ((getFlexoResource() != null) && (getFlexoResource().getGenerator() != null)
					&& (getFlexoResource().getGenerator().getGeneratedCode() instanceof GeneratedComponent)) {
				return ((GeneratedComponent) getFlexoResource().getGenerator().getGeneratedCode()).html();
			}
			return null;
		}

		@Override
		public WOFileResource getFlexoResource() {
			return (WOFileResource) super.getFlexoResource();
		}

		private File _lastAcceptedFile;
		private File _lastGeneratedFile;

		@Override
		protected File getLastAcceptedFile() {
			if (_lastAcceptedFile == null) {
				_lastAcceptedFile = new File(getFlexoResource().getLastAcceptedFile(), WOHTMLFile.this.getFile().getName());
			}
			return _lastAcceptedFile;
		}

		@Override
		protected File getLastGeneratedFile() {
			if (_lastGeneratedFile == null) {
				_lastGeneratedFile = new File(getFlexoResource().getLastGeneratedFile(), WOHTMLFile.this.getFile().getName());
			}
			return _lastGeneratedFile;
		}

		@Override
		public void updateHistory() {
		}

		@Override
		public boolean manageHistory() {
			return false;
		}

		@Override
		protected String getHistoryContent(CGVersionIdentifier versionId) {
			try {
				if (getHistory().versionWithId(versionId) != null) {
					return getHistory().versionWithId(versionId).getHTMLContent();
				} else {
					return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
				}
			} catch (IOFlexoException e) {
				e.printStackTrace();
				return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
			}

		}

		@Override
		public MergedDocumentType getMergedDocumentType() {
			return DefaultMergedDocumentType.HTML;
		}

	}

	public class WODFile extends ASCIIFile {

		public WODFile(File f) {
			super(f);
		}

		public WODFile() {
			super();
		}

		@Override
		public String getCurrentGeneration() {
			if ((getFlexoResource() != null) && (getFlexoResource().getGenerator() != null)
					&& (getFlexoResource().getGenerator().getGeneratedCode() instanceof GeneratedComponent)) {
				return ((GeneratedComponent) getFlexoResource().getGenerator().getGeneratedCode()).wod();
			}
			return null;
		}

		@Override
		public WOFileResource getFlexoResource() {
			return (WOFileResource) super.getFlexoResource();
		}

		private File _lastAcceptedFile;
		private File _lastGeneratedFile;

		@Override
		protected File getLastAcceptedFile() {
			if (_lastAcceptedFile == null) {
				_lastAcceptedFile = new File(getFlexoResource().getLastAcceptedFile(), WODFile.this.getFile().getName());
			}
			return _lastAcceptedFile;
		}

		@Override
		protected File getLastGeneratedFile() {
			if (_lastGeneratedFile == null) {
				_lastGeneratedFile = new File(getFlexoResource().getLastGeneratedFile(), WODFile.this.getFile().getName());
			}
			return _lastGeneratedFile;
		}

		@Override
		public void updateHistory() {
		}

		@Override
		public boolean manageHistory() {
			return false;
		}

		@Override
		protected String getHistoryContent(CGVersionIdentifier versionId) {
			try {
				if (getHistory().versionWithId(versionId) != null) {
					return getHistory().versionWithId(versionId).getWODContent();
				} else {
					return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
				}
			} catch (IOFlexoException e) {
				e.printStackTrace();
				return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
			}

		}

		@Override
		public MergedDocumentType getMergedDocumentType() {
			return DefaultMergedDocumentType.PLIST;
		}

	}

	public class WOOFile extends ASCIIFile {

		public WOOFile(File f) {
			super(f);
		}

		public WOOFile() {
			super();
		}

		@Override
		public String getCurrentGeneration() {
			if ((getFlexoResource() != null) && (getFlexoResource().getGenerator() != null)
					&& (getFlexoResource().getGenerator().getGeneratedCode() instanceof GeneratedComponent)) {
				return ((GeneratedComponent) getFlexoResource().getGenerator().getGeneratedCode()).woo();
			}
			return null;
		}

		@Override
		public WOFileResource getFlexoResource() {
			return (WOFileResource) super.getFlexoResource();
		}

		private File _lastAcceptedFile;
		private File _lastGeneratedFile;

		@Override
		protected File getLastAcceptedFile() {
			if (_lastAcceptedFile == null) {
				_lastAcceptedFile = new File(getFlexoResource().getLastAcceptedFile(), WOOFile.this.getFile().getName());
			}
			return _lastAcceptedFile;
		}

		@Override
		protected File getLastGeneratedFile() {
			if (_lastGeneratedFile == null) {
				_lastGeneratedFile = new File(getFlexoResource().getLastGeneratedFile(), WOOFile.this.getFile().getName());
			}
			return _lastGeneratedFile;
		}

		@Override
		public void updateHistory() {
		}

		@Override
		public boolean manageHistory() {
			return false;
		}

		@Override
		protected String getHistoryContent(CGVersionIdentifier versionId) {
			try {
				if (getHistory().versionWithId(versionId) != null) {
					return getHistory().versionWithId(versionId).getWOOContent();
				} else {
					return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
				}
			} catch (IOFlexoException e) {
				e.printStackTrace();
				return "Unable to access version " + versionId + " for file " + getFlexoResource().getFileName();
			}

		}

		@Override
		public MergedDocumentType getMergedDocumentType() {
			return DefaultMergedDocumentType.PLIST;
		}

	}

}