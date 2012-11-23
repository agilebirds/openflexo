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
package org.openflexo.foundation.dm.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMSet;
import org.openflexo.foundation.rm.FlexoProject;

public class CreateDMRepository<A extends CreateDMRepository<A>> extends FlexoAction<A, DMObject, DMObject> {

	private static final Logger logger = Logger.getLogger(CreateDMRepository.class.getPackage().getName());

	public static final String PROJECT_REPOSITORY = "PROJECT_REPOSITORY";
	public static final String PROJECT_DATABASE_REPOSITORY = "PROJECT_DATABASE_REPOSITORY";
	public static final String EXTERNAL_REPOSITORY = "EXTERNAL_REPOSITORY";
	public static final String DENALI_FOUNDATION_REPOSITORY = "DENALI_FOUNDATION_REPOSITORY";
	public static final String EXTERNAL_DATABASE_REPOSITORY = "EXTERNAL_DATABASE_REPOSITORY";
	public static final String RATIONAL_ROSE_REPOSITORY = "RATIONAL_ROSE_REPOSITORY";
	public static final String THESAURUS_REPOSITORY = "THESAURUS_REPOSITORY";
	public static final String THESAURUS_DATABASE_REPOSITORY = "THESAURUS_DATABASE_REPOSITORY";

	CreateDMRepository(FlexoActionType<A, DMObject, DMObject> actionType, DMObject focusedObject, Vector<DMObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	protected DMRepository _newRepository = null;
	private String _repositoryType;
	private String _newRepositoryName;
	private File _jarFile;
	private File _denaliFoundationRepositoryFile;
	private File _rationalRoseFile;
	private String _rationalRosePackageName;
	private FlexoProject _project;

	public File getDenaliFoundationRepositoryFile() {
		return _denaliFoundationRepositoryFile;
	}

	public void setDenaliFoundationRepositoryFile(File denaliFoundationRepositoryFile) {
		_denaliFoundationRepositoryFile = denaliFoundationRepositoryFile;
	}

	public File getJarFile() {
		return _jarFile;
	}

	public void setJarFile(File jarFile) {
		_jarFile = jarFile;
	}

	public File getRationalRoseFile() {
		return _rationalRoseFile;
	}

	public void setRationalRoseFile(File rationalRoseFile) {
		_rationalRoseFile = rationalRoseFile;
	}

	public String getRationalRosePackageName() {
		if (_rationalRosePackageName != null) {
			return _rationalRosePackageName;
		}
		return "rational.rose";
	}

	public void setRationalRosePackageName(String rationalRosePackageName) {
		_rationalRosePackageName = rationalRosePackageName;
	}

	public DMRepository getNewRepository() {
		return _newRepository;
	}

	public void setNewRepository(DMRepository newRepository) {
		_newRepository = newRepository;
	}

	public String getNewRepositoryName() {
		return _newRepositoryName;
	}

	public void setNewRepositoryName(String newRepositoryName) {
		_newRepositoryName = newRepositoryName;
	}

	public String getRepositoryType() {
		return _repositoryType;
	}

	public void setRepositoryType(String repositoryType) {
		_repositoryType = repositoryType;
	}

	public FlexoProject getProject() {
		if (_project == null && getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return _project;
	}

	public void setProject(FlexoProject project) {
		_project = project;
	}

	private DMSet importedClassSet = null;

	public DMSet getImportedClassSet() {
		return importedClassSet;
	}

	public void setImportedClassSet(DMSet importedClassSet) {
		this.importedClassSet = importedClassSet;
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		logger.info("CREATE DMRepository");

		// TODO: fixed an NPE that i could not reproduce
		// (initializer was not called, and therefore repository type was null)
		if (getRepositoryType() == null) {
			return;
		}

		if (getRepositoryType().equals(CreateDMRepository.PROJECT_REPOSITORY)) {

		} else if (getRepositoryType().equals(CreateDMRepository.PROJECT_DATABASE_REPOSITORY)) {

		} else if (getRepositoryType().equals(CreateDMRepository.EXTERNAL_REPOSITORY)) {

		} else if (getRepositoryType().equals(CreateDMRepository.RATIONAL_ROSE_REPOSITORY)) {

		} else if (getRepositoryType().equals(CreateDMRepository.EXTERNAL_DATABASE_REPOSITORY)) {

		} else if (getRepositoryType().equals(CreateDMRepository.THESAURUS_REPOSITORY)) {

		} else if (getRepositoryType().equals(CreateDMRepository.THESAURUS_DATABASE_REPOSITORY)) {

		}
	}
}
