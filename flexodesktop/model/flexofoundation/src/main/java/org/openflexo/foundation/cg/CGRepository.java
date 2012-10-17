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
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.cg.dm.CGRepositoryDisconnected;
import org.openflexo.foundation.rm.ProjectExternalRepository;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * @author gpolet
 * 
 */
public class CGRepository extends GenerationRepository implements ReferenceOwner {

	private static final Logger logger = FlexoLogger.getLogger(CGRepository.class.getPackage().getName());

	private CodeType _targetType;
	private String _superClassesGenerationSubPath;
	private ProjectExternalRepository _warRepository;

	private Format format;

	private String warName;
	private Date lastWarNameUpdate;

	private Date lastLoginPasswordUpdate;

	private String prototypeLogin;
	private String prototypePassword;

	private boolean includeReader = false;
	private FlexoModelObjectReference<DGRepository> readerRepositoryReference;

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public CGRepository(GeneratedCodeBuilder builder) {
		this(builder.generatedCode);
		initializeDeserialization(builder);
	}

	/**
	 * @throws DuplicateCodeRepositoryNameException
	 * 
	 */
	public CGRepository(GeneratedCode generatedCode, String name, File directory) throws DuplicateCodeRepositoryNameException {
		super(generatedCode, name, directory);
	}

	/**
	 * @param generatedCode
	 */
	public CGRepository(GeneratedOutput generatedCode) {
		super(generatedCode);
	}

	@Override
	public void delete(FlexoProgress progress, boolean deleteFiles) {
		if (getReaderRepository() != null) {
			getReaderRepository().removeFromRepositoriedUsingAsReader(this);
		}
		super.delete(progress, deleteFiles);
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddGeneratedCodeRepository.actionType);
		return returned;
	}

	@Override
	public CodeType getTarget() {
		return getTargetType();
	}

	public CodeType getTargetType() {
		if (_targetType == null) {
			_targetType = CodeType.PROTOTYPE;
		}
		return _targetType;
	}

	public void setTargetType(CodeType targetType) {
		CodeType old = this._targetType;
		_targetType = targetType;
		setChanged();
		notifyObservers(new DataModification("targetType", old, targetType));
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && (!includeReader() || getReaderRepository().isEnabled());
	}

	@Override
	public boolean connect() {
		if (getReaderRepository() != null) {
			return getReaderRepository().connect() && super.connect();
		} else {
			return super.connect();
		}
	}

	public ProjectExternalRepository getWarRepository() {
		if (_warRepository == null) {
			_warRepository = getProject().getExternalRepositoryWithKey(getName() + "WAR");
		}
		if (_warRepository == null) {
			try {
				_warRepository = getProject().setDirectoryForRepositoryName(
						getName() + "WAR",
						getDefaultWARDirectory());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _warRepository;
	}

	private File getDefaultWARDirectory() throws IOException {
		return getDirectory() != null ? getDirectory().getParentFile() : FileUtils.createTempDirectory(getProject()
				.getProjectName() + "Application", ".war");
	}

	public File getWarDirectory() {
		if (getWarRepository() != null) {
			return getWarRepository().getDirectory();
		}
		return null;
	}

	public void setWarDirectory(File aDirectory) {
		if (getWarRepository() != null) {
			File oldValue = getWarRepository().getDirectory();
			getWarRepository().setDirectory(aDirectory);
			setChanged();
			notifyObservers(new CGDataModification("warDirectory", oldValue, aDirectory));
		}
	}

	public String getWarName() {
		return warName;
	}

	public void setWarName(String warName) throws DuplicateCodeRepositoryNameException {
		String oldName = this.warName;
		if (oldName == null) {
			if (warName == null) {
				return;
			}
		} else {
			if (oldName.equals(warName)) {
				return;
			}
		}
		this.warName = warName;
		lastWarNameUpdate = new Date();
		setChanged();
		if (warName != null && !warName.matches(ToolBox.WAR_NAME_ACCEPTABLE_CHARS)) {
			this.warName = ToolBox.getWarName(warName);
			notifyObserversAsReentrantModification(new DataModification("warName", oldName, warName));
		} else {
			notifyObservers(new DataModification("warName", oldName, warName));
		}
	}

	public String getSuperClassesGenerationSubPath() {
		if (_superClassesGenerationSubPath == null) {
			_superClassesGenerationSubPath = "/src/main/java/WebObjects/Core";
		}
		return _superClassesGenerationSubPath;
	}

	public void setSuperClassesGenerationSubPath(String subPath) {
		_superClassesGenerationSubPath = subPath;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "generated_code_repository";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.CG.CG_REPOSITORY_INSPECTOR;
	}

	public CGSymbolicDirectory getJavaSymbolicDirectory() {
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.JAVA);
	}

	public CGSymbolicDirectory getResourcesSymbolicDirectory() {
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.RESOURCES);
	}

	public CGSymbolicDirectory getWebResourcesSymbolicDirectory() {
		CGSymbolicDirectory reply = getSymbolicDirectoryNamed(CGSymbolicDirectory.WEBRESOURCES);
		if (reply == null) {
			FlexoProjectFile webSymbDir = new FlexoProjectFile(getProject(), getSourceCodeRepository(), "/src/main/webresources");
			webSymbDir.getFile().mkdirs();
			setSymbolicDirectoryForKey(new CGSymbolicDirectory(this, CGSymbolicDirectory.WEBRESOURCES, webSymbDir),
					CGSymbolicDirectory.WEBRESOURCES);
		}
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.WEBRESOURCES);
	}

	public CGSymbolicDirectory getLibSymbolicDirectory() {
		CGSymbolicDirectory reply = getSymbolicDirectoryNamed(CGSymbolicDirectory.LIB);
		if (reply == null) {
			FlexoProjectFile libSymbDir = new FlexoProjectFile(getProject(), getSourceCodeRepository(), "/lib");
			libSymbDir.getFile().mkdirs();
			setSymbolicDirectoryForKey(new CGSymbolicDirectory(this, CGSymbolicDirectory.LIB, libSymbDir), CGSymbolicDirectory.LIB);
		}
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.LIB);
	}

	public CGSymbolicDirectory getComponentsSymbolicDirectory() {
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.COMPONENTS);
	}

	public CGSymbolicDirectory getProjectSymbolicDirectory() {
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.PROJECT);
	}

	public CGSymbolicDirectory getReaderSymbolicDirectory() {
		return getSymbolicDirectoryNamed(CGSymbolicDirectory.READER);
	}

	@Override
	protected void deleteExternalRepositories() {
		if (getWarRepository() != null) {
			getProject().removeFromExternalRepositories(getWarRepository());
		}
		super.deleteExternalRepositories();
	}

	public void clearAllJavaParsingData() {
		for (CGFile file : getFiles()) {
			file.clearParsingData();
		}
		getProject().getDataModel().getClassLibrary().clearLibrary();
	}

	public String getPrototypeLogin() {
		return prototypeLogin;
	}

	public void setPrototypeLogin(String prototypeLogin) {
		String old = this.prototypeLogin;
		this.prototypeLogin = prototypeLogin;
		lastLoginPasswordUpdate = new Date();
		setChanged();
		notifyObservers(new CGDataModification("prototypeLogin", old, prototypeLogin));
	}

	public String getPrototypePassword() {
		return prototypePassword;
	}

	public void setPrototypePassword(String prototypePassword) {
		String old = this.prototypePassword;
		this.prototypePassword = prototypePassword;
		lastLoginPasswordUpdate = new Date();
		setChanged();
		notifyObservers(new CGDataModification("prototypePassword", old, prototypePassword));
	}

	/**
	 * This date is use to perform fine tuning resource dependancies computing
	 * 
	 * @return
	 */
	public Date getLastWarNameUpdate() {
		if (lastWarNameUpdate == null) {
			lastWarNameUpdate = super.getLastUpdate();
		}
		return lastWarNameUpdate;
	}

	public void setLastWarNameUpdate(Date lastUpdate) {
		lastWarNameUpdate = lastUpdate;
	}

	/**
	 * This date is use to perform fine tuning resource dependancies computing
	 * 
	 * @return
	 */
	public Date getLastLoginPasswordUpdate() {
		if (lastLoginPasswordUpdate == null) {
			lastLoginPasswordUpdate = super.getLastUpdate();
		}
		return lastLoginPasswordUpdate;
	}

	public void setLastLoginPasswordUpdate(Date lastUpdate) {
		lastLoginPasswordUpdate = lastUpdate;
	}

	@Override
	public Format getFormat() {
		if (format == null) {
			return Format.WEBOBJECTS;
		}
		return format;
	}

	@Override
	public void setFormat(Format format) {
		this.format = format;
	}

	public boolean includeReader() {
		if (includeReader && getReaderRepository() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Repository '" + getName() + "' should include reader but has no reader repository!");
			}
		}
		return includeReader && getReaderRepository() != null;
	}

	public void setIncludeReader(boolean includeReader) {
		this.includeReader = includeReader;
	}

	public DGRepository getReaderRepository() {
		if (getReaderRepositoryReference() != null) {
			return getReaderRepositoryReference().getObject(true);
		}
		return null;
	}

	public void setReaderRepository(DGRepository readerRepository) {
		readerRepository.addToRepositoriedUsingAsReader(this);
		readerRepository.addObserver(this);
		if (readerRepositoryReference == null) {
			setReaderRepositoryReference(new FlexoModelObjectReference<DGRepository>(getProject(), readerRepository));
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification obj) {
		if (observable == getReaderRepository() && (obj instanceof CGRepositoryConnected || obj instanceof CGRepositoryDisconnected)) {
			setChanged();
			notifyObservers(obj);
		}
		super.update(observable, obj);
	}

	public FlexoModelObjectReference<DGRepository> getReaderRepositoryReference() {
		return readerRepositoryReference;
	}

	public void setReaderRepositoryReference(FlexoModelObjectReference<DGRepository> readerRepositoryReference) {
		if (this.readerRepositoryReference != null) {
			this.readerRepositoryReference.delete();
		}
		this.readerRepositoryReference = readerRepositoryReference;
		this.readerRepositoryReference.setOwner(this);
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {
		if (reference.getObject() instanceof DGRepository) {
			setReaderRepository((DGRepository) reference.getObject());
		}
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {

	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {

	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference reference) {
		setChanged();
	}
}
