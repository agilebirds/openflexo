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
package org.openflexo.foundation.cg.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GeneratedCode;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.InvalidReaderRepositoryException;
import org.openflexo.foundation.cg.MissingReaderRepositoryException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.utils.FlexoProjectFile;

public class AddGeneratedCodeRepository extends AbstractGCAction<AddGeneratedCodeRepository, CGObject> {

	private static final Logger logger = Logger.getLogger(AddGeneratedCodeRepository.class.getPackage().getName());

	public static FlexoActionType<AddGeneratedCodeRepository, CGObject, CGObject> actionType = new FlexoActionType<AddGeneratedCodeRepository, CGObject, CGObject>(
			"add_repository", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddGeneratedCodeRepository makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new AddGeneratedCodeRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return object != null && object.getGeneratedCode() != null;
		}

	};

	private GenerationRepository _newGeneratedCodeRepository;

	private String _newGeneratedCodeRepositoryName;

	private CodeType _newTargetType;

	private DocType _newDocType;

	private File _newGeneratedCodeRepositoryDirectory;

	private TOCRepository tocRepository;

	private Format format;

	private boolean includeReader = true;

	private DGRepository readerRepository;

	AddGeneratedCodeRepository(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException, MissingReaderRepositoryException,
			InvalidReaderRepositoryException {
		logger.info("Add GeneratedCodeRepository " + getFocusedObject());
		if (getFocusedObject().getGeneratedCode() != null) {
			FlexoProject project = getFocusedObject().getProject();
			GeneratedOutput gc = getFocusedObject().getGeneratedCode();
			if (gc instanceof GeneratedCode) {
				if (includeReader && readerRepository == null) {
					throw new MissingReaderRepositoryException();
				}
				if (includeReader && readerRepository.getFormat() != Format.HTML) {
					throw new InvalidReaderRepositoryException();
				}
				_newGeneratedCodeRepository = new CGRepository((GeneratedCode) gc, getNewGeneratedCodeRepositoryName(),
						getNewGeneratedCodeRepositoryDirectory());
				((CGRepository) _newGeneratedCodeRepository).setTargetType(getNewTargetType());

				if (getNewTargetType() != CodeType.BPEL) {
					((CGRepository) _newGeneratedCodeRepository).setWarName(getNewGeneratedCodeRepositoryName() + "WAR");
				}

				getFocusedObject().getGeneratedCode().addToGeneratedRepositories(_newGeneratedCodeRepository);

				FlexoProjectFile resourcesSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
						"/src/main/resources");
				_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
						CGSymbolicDirectory.RESOURCES, resourcesSymbDir), CGSymbolicDirectory.RESOURCES);
				resourcesSymbDir.getFile().mkdirs();

				if (getNewTargetType() != CodeType.BPEL) {
					FlexoProjectFile javaSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
							"/src/main/java");
					FlexoProjectFile woSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
							"/src/main/components");
					FlexoProjectFile webSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
							"/src/main/webresources");
					FlexoProjectFile libSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
							"/lib");
					FlexoProjectFile projectSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
							"/.");
					_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
							CGSymbolicDirectory.JAVA, javaSymbDir), CGSymbolicDirectory.JAVA);
					_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
							CGSymbolicDirectory.COMPONENTS, woSymbDir), CGSymbolicDirectory.COMPONENTS);
					_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
							CGSymbolicDirectory.PROJECT, projectSymbDir), CGSymbolicDirectory.PROJECT);
					_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
							CGSymbolicDirectory.WEBRESOURCES, webSymbDir), CGSymbolicDirectory.WEBRESOURCES);
					if (includeReader) {
						FlexoProjectFile readerSymbDir = new FlexoProjectFile(project,
								_newGeneratedCodeRepository.getSourceCodeRepository(), "/src/main/resources/reader");
						_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
								CGSymbolicDirectory.READER, readerSymbDir), CGSymbolicDirectory.READER);
					}
					_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
							CGSymbolicDirectory.LIB, libSymbDir), CGSymbolicDirectory.LIB);
					((CGRepository) _newGeneratedCodeRepository).setIncludeReader(includeReader);
					if (includeReader) {
						((CGRepository) _newGeneratedCodeRepository).setReaderRepository(getReaderRepository());
					}
					// NOW we force the creation of all those directories
					// because if one of them is empty : it will never be created
					// and task that build the war may fail if any of those directory doesn't exist.
					projectSymbDir.getFile().mkdirs();
					woSymbDir.getFile().mkdirs();
					webSymbDir.getFile().mkdirs();
					libSymbDir.getFile().mkdirs();
					new File(projectSymbDir.getFile(), "docs").mkdirs();
				}

			} else if (gc instanceof GeneratedDoc) {
				_newGeneratedCodeRepository = new DGRepository((GeneratedDoc) gc, getNewGeneratedCodeRepositoryName(), getNewDocType(),
						getFormat(), getNewGeneratedCodeRepositoryDirectory());
				((DGRepository) _newGeneratedCodeRepository).setPostProductName(getNewGeneratedCodeRepositoryName());
				if (tocRepository != null) {
					((DGRepository) _newGeneratedCodeRepository).setTocRepository(tocRepository);
				}
				getFocusedObject().getGeneratedCode().addToGeneratedRepositories(_newGeneratedCodeRepository);

				String srcSymbDirType = null;
				switch (getFormat()) {
				case HTML:
					srcSymbDirType = CGSymbolicDirectory.HTML;
					createResourcesSymbolicDir(project, srcSymbDirType);
					createJavascriptSymbolicDir(project, srcSymbDirType);
					createFiguresSymbolicDir(project);
					break;
				case LATEX:
					srcSymbDirType = CGSymbolicDirectory.LATEX;
					createFiguresSymbolicDir(project);
					break;
				case DOCX:
					srcSymbDirType = CGSymbolicDirectory.DOCX;
					createFiguresSymbolicDir(project, "/./word/media/figures/");
					createResourcesSymbolicDir(project, srcSymbDirType, "/./word/media");
					break;
				}

				if (srcSymbDirType != null) {
					FlexoProjectFile srcSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(), "/.");
					_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
							srcSymbDirType, srcSymbDir), srcSymbDirType);
				}
			}
		}
	}

	private void createFiguresSymbolicDir(FlexoProject project) {
		createFiguresSymbolicDir(project, null);
	}

	private void createFiguresSymbolicDir(FlexoProject project, String figuresPath) {
		if (figuresPath == null) {
			figuresPath = "/./Figures";
		}

		FlexoProjectFile figSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(), figuresPath);
		_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository,
				CGSymbolicDirectory.FIGURES, figSymbDir), CGSymbolicDirectory.FIGURES);
	}

	/**
	 * @param project
	 * @param srcSymbDirType
	 */
	private void createJavascriptSymbolicDir(FlexoProject project, String srcSymbDirType) {
		FlexoProjectFile jsProcessSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
				"/./processes");
		_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository, srcSymbDirType,
				jsProcessSymbDir), CGSymbolicDirectory.JS_PROCESSES);
	}

	private void createResourcesSymbolicDir(FlexoProject project, String srcSymbDirType) {
		createResourcesSymbolicDir(project, srcSymbDirType, null);
	}

	private void createResourcesSymbolicDir(FlexoProject project, String srcSymbDirType, String resourcesPath) {
		if (resourcesPath == null) {
			resourcesPath = "/./resources";
		}

		FlexoProjectFile resourcesSymbDir = new FlexoProjectFile(project, _newGeneratedCodeRepository.getSourceCodeRepository(),
				resourcesPath);
		_newGeneratedCodeRepository.setSymbolicDirectoryForKey(new CGSymbolicDirectory(_newGeneratedCodeRepository, srcSymbDirType,
				resourcesSymbDir), CGSymbolicDirectory.RESOURCES);
	}

	public String getNewGeneratedCodeRepositoryName() {
		return _newGeneratedCodeRepositoryName;
	}

	public void setNewGeneratedCodeRepositoryName(String newGeneratedCodeRepositoryName) {
		_newGeneratedCodeRepositoryName = newGeneratedCodeRepositoryName;
	}

	public GenerationRepository getNewGeneratedCodeRepository() {
		return _newGeneratedCodeRepository;
	}

	public File getNewGeneratedCodeRepositoryDirectory() {
		return _newGeneratedCodeRepositoryDirectory;
	}

	public void setNewGeneratedCodeRepositoryDirectory(File newGeneratedCodeRepositoryDirectory) {
		_newGeneratedCodeRepositoryDirectory = newGeneratedCodeRepositoryDirectory;
	}

	public CodeType getNewTargetType() {
		if (_newTargetType == null) {
			return CodeType.PROTOTYPE;
		}
		return _newTargetType;
	}

	public void setNewTargetType(CodeType newTargetType) {
		_newTargetType = newTargetType;
	}

	public DocType getNewDocType() {
		if (_newDocType == null && getFocusedObject().getProject().getDocTypes().size() > 0) {
			return getFocusedObject().getProject().getDocTypes().get(0);
		}
		return _newDocType;
	}

	public void setNewDocType(DocType type) {
		_newDocType = type;
	}

	public void setTocRepository(TOCRepository value) {
		this.tocRepository = value;
	}

	public Format getFormat() {
		if (format == null) {
			format = Format.HTML;
		}
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public boolean isIncludeReader() {
		return includeReader;
	}

	public void setIncludeReader(boolean includeReader) {
		this.includeReader = includeReader;
	}

	public DGRepository getReaderRepository() {
		return readerRepository;
	}

	public void setReaderRepository(DGRepository readerRepository) {
		this.readerRepository = readerRepository;
	}

}
