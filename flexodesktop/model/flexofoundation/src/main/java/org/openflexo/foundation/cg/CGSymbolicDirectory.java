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

import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;

public class CGSymbolicDirectory extends CGObject implements CGPathElement {

	private static final Logger logger = Logger.getLogger(CGSymbolicDirectory.class.getPackage().getName());

	private GenerationRepository _codeRepository;
	private String _name;
	private FlexoProjectFile _directory;

	public static final String JAVA = "Sources";
	public static final String RESOURCES = "Resources";
	public static final String WEBRESOURCES = "WebResources";
	public static final String LIB = "Libraries";
	public static final String COMPONENTS = "Components";
	public static final String PROJECT = "Project";
	public static final String LATEX = "Latex";
	public static final String HTML = "HTML";
	public static final String DOCX = "Docx";
	public static final String JS_PROCESSES = "Processes";
	public static final String CSS_RESOURCES = "Css";
	public static final String IMG_RESOURCES = "Img";
	public static final String JS_RESOURCES = "Scripts";
	public static final String FIGURES = "Figures";
	public static final String READER = "Reader";

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public CGSymbolicDirectory(GeneratedCodeBuilder builder) {
		this(builder.generatedCode);
		initializeDeserialization(builder);
	}

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public CGSymbolicDirectory(GeneratedSourcesBuilder builder) {
		this(builder.generatedSources);
		initializeDeserialization(builder);
	}

	public CGSymbolicDirectory(GeneratedOutput generatedCode) {
		super(generatedCode);
		_subFolders = new Vector<CGFolder>();
		_files = new Vector<CGFile>() {
			/**
			 * Overrides add
			 * 
			 * @see java.util.Vector#add(java.lang.Object)
			 */
			@Override
			public synchronized boolean add(CGFile o) {
				if (indexOf(o) > -1) {
					logger.severe("ERROR: Adding twice " + o);
				}
				return super.add(o);
			}
		};
	}

	public CGSymbolicDirectory(GenerationRepository repository, String name, FlexoProjectFile directory) {
		this(repository.getGeneratedCode());
		_codeRepository = repository;
		setName(name);
		setDirectory(directory);
	}

	@Override
	public String getFullyQualifiedName() {
		return getGeneratedCodeRepository().getFullyQualifiedName() + "." + getName();
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

	public GenerationRepository getGeneratedCodeRepository() {
		return _codeRepository;
	}

	public void setGeneratedCodeRepository(GenerationRepository codeRepository) {
		_codeRepository = codeRepository;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) {
		_name = aName;
		setChanged();
	}

	public FlexoProjectFile getDirectory() {
		return _directory;
	}

	public void setDirectory(FlexoProjectFile directory) {
		// logger.info("Sets directory to be now "+directory);
		directory.setProject(getProject());
		if (isDeserializing() || directory.getExternalRepository() == getGeneratedCodeRepository().getSourceCodeRepository()) {
			_directory = directory;
			setChanged();
		}
	}

	@Override
	public boolean hasGenerationErrors() {
		return hasGenerationErrors;
	}

	@Override
	public boolean needsRegeneration() {
		return needsRegeneration;
	}

	@Override
	public boolean needsModelReinjection() {
		return needsModelReinjection;
	}

	@Override
	public GenerationStatus getGenerationStatus() {
		return generationStatus;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.CG_SYMB_DIR_INSPECTOR;
	}

	private Vector<CGFolder> _subFolders;
	private Vector<CGFile> _files;

	@Override
	public CGPathElement getParent() {
		return null;
	}

	@Override
	public CGFolder getDirectoryNamed(String aName) {
		for (CGFolder dir : getSubFolders()) {
			if (dir.getName().equals(aName)) {
				return dir;
			}
		}
		return null;
	}

	private String getRelativePathFrom(FlexoProjectFile aFile) {
		if (aFile.getExternalRepository() == _directory.getExternalRepository() && aFile.getExternalRepository() != null) {
			String symbDirPath = _directory.getRelativePath();
			String searchedPath = aFile.getRelativePath();
			if (searchedPath.indexOf(symbDirPath) > -1) {
				return searchedPath.substring(searchedPath.indexOf(symbDirPath) + symbDirPath.length());
			}
		}
		logger.info("aFile.getExternalRepository()=" + aFile.getExternalRepository());
		logger.info("_directory.getExternalRepository()=" + _directory.getExternalRepository());
		logger.info("symbDirPath=" + _directory.getRelativePath());
		logger.info("searchedPath=" + aFile.getRelativePath());
		return null;
	}

	private boolean isEnabled = false;

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	protected void clearStructure() {
		hasGenerationErrors = false;
		needsRegeneration = false;
		needsModelReinjection = false;
		generationStatus = GenerationStatus.UpToDate;
		for (CGFolder folder : _subFolders) {
			folder.clearFiles();
		}
		_files.clear();
		isEnabled = false;
	}

	protected void addToStructure(CGFile file) {
		if (file.getResource() == null && !file.isDeleted()) {
			logger.warning("file: " + file + " : resource is null for a non-deleted object");
			return;
		}
		FlexoProjectFile projectFile = file.getResource().getResourceFile();
		if (projectFile == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning(" null project file for " + file + " resource: " + file.getResource());
			}
			return;
		}
		if (projectFile.getExternalRepository() == null
				|| projectFile.getExternalRepository() != getGeneratedCodeRepository().getSourceCodeRepository()) {
			logger.warning("File: " + file + " : found invalid external repository. Repair it.");
			projectFile.setExternalRepository(getGeneratedCodeRepository().getSourceCodeRepository());
		}
		// logger.info("file.getResource()="+file.getResource());
		// logger.info("projectFile="+projectFile);
		String relativePath = getRelativePathFrom(projectFile);
		if (relativePath != null) {

			StringTokenizer st = new StringTokenizer(relativePath, "/");
			CGPathElement parent = this;
			String dirName;
			while (st.hasMoreTokens() && (dirName = st.nextToken()) != null && st.hasMoreTokens()) {
				if (parent.getDirectoryNamed(dirName) == null) {
					parent.getSubFolders().add(new CGFolder(getGeneratedCodeRepository(), dirName, parent));
				}
				if (file.isEnabled()) {
					parent.getDirectoryNamed(dirName).isEnabled = true;
				}
				parent = parent.getDirectoryNamed(dirName);
			}
			if (file.isEnabled()) {
				isEnabled = true;
			}
			file.setParent(parent);
			parent.getFiles().add(file);
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("relative path: " + relativePath + " for file : " + projectFile.getStringRepresentation());
			}
		}
	}

	@Override
	public synchronized Vector<CGFile> getFiles() {
		return _files;
	}

	public CGFile[] getSortedFiles() {
		CGFile[] files = getFiles().toArray(new CGFile[getFiles().size()]);
		Arrays.sort(files, new Comparator<CGFile>() {

			@Override
			public int compare(CGFile o1, CGFile o2) {
				return o1.getFileName().toLowerCase().compareTo(o2.getFileName().toLowerCase());
			}

		});
		return files;
	}

	@Override
	public Vector<CGFolder> getSubFolders() {
		return _subFolders;
	}

	public CGFolder[] getSortedSubFolders() {
		CGFolder[] folders = getSubFolders().toArray(new CGFolder[getSubFolders().size()]);
		Arrays.sort(folders, new Comparator<CGFolder>() {

			@Override
			public int compare(CGFolder o1, CGFolder o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}

		});
		return folders;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	@Override
	public boolean isContainedIn(CGObject obj) {
		if (obj instanceof GeneratedOutput) {
			return obj == getGeneratedCode();
		} else if (obj instanceof GenerationRepository) {
			return obj == getGeneratedCodeRepository();
		} else if (obj instanceof CGSymbolicDirectory) {
			return obj == this;
		}
		return false;
	}

}
