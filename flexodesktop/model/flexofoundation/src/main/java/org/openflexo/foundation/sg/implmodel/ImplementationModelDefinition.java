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
package org.openflexo.foundation.sg.implmodel;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedSourcesResource;
import org.openflexo.foundation.rm.ImplementationModelResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.implmodel.event.ImplModelNameChanged;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.xmlcode.XMLMapping;

public class ImplementationModelDefinition extends FlexoModelObject {

	protected static final Logger logger = FlexoLogger.getLogger(ImplementationModelDefinition.class.getPackage().getName());

	private GeneratedSources _generatedSources;
	private String _name;

	public ImplementationModelDefinition(GeneratedSourcesBuilder builder) throws DuplicateResourceException {
		this(null, builder.generatedSources, builder.getProject(), false);
		initializeDeserialization(builder);
		_generatedSources = builder.generatedSources;
	}

	public ImplementationModelDefinition(String modelName, GeneratedSources generatedSources, FlexoProject project, boolean checkUnicity)
			throws DuplicateResourceException {
		super(generatedSources.getProject());
		_generatedSources = generatedSources;
		_name = modelName;

		logger.info("Added new ImplementationModelDefinition");
		_generatedSources.addToImplementationModels(this);

		if (checkUnicity) {
			String resourceIdentifier = ImplementationModelResource.resourceIdentifierForName(modelName);
			if (project != null && project.isRegistered(resourceIdentifier)) {
				throw new DuplicateResourceException(resourceIdentifier);
			}
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) throws DuplicateResourceException, InvalidNameException {
		setImplementationModelName(aName);
	}

	private void setImplementationModelName(String name) throws DuplicateResourceException, InvalidNameException {
		if (_name != null && !_name.equals(name) && name != null && !isDeserializing()) {
			if (!name.matches(IERegExp.JAVA_CLASS_NAME_REGEXP)) {
				throw new InvalidNameException();
			}

			if (ReservedKeyword.contains(name)) {
				throw new InvalidNameException();
			}
			if (getProject() != null) {
				ImplementationModelDefinition cd = _generatedSources.getImplementationModelDefinitionNamed(name);
				if (cd != null && cd != this) {
					throw new DuplicateResourceException(getImplementationModelResource(false));
				}
				ImplementationModelResource resource = getImplementationModelResource();
				if (resource != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Renaming implementation model resource !");
					}
					try {
						getProject().renameResource(resource, name);
					} catch (DuplicateResourceException e1) {
						throw e1;
					}
				}
				String oldModelName = _name;
				_name = name;
				setChanged();
				notifyObservers(new ImplModelNameChanged(this, oldModelName, name));
			}
		} else {
			_name = name;
		}
	}

	public ImplementationModelResource getImplementationModelResource() {
		return getImplementationModelResource(true);
	}

	public ImplementationModelResource getImplementationModelResource(boolean createIfNotExists) {
		if (getProject() != null) {
			ImplementationModelResource returned = getProject().getImplementationModelResource(getName());
			if (returned == null && createIfNotExists) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Creating new implementation model resource !");
				}
				GeneratedSourcesResource gsRes = getProject().getGeneratedSourcesResource();
				File implModelFile = new File(ProjectRestructuration.getExpectedImplementationModelDirectory(getProject()
						.getProjectDirectory(), getName()), getName() + ".im");
				FlexoProjectFile resourceImplModelFile = new FlexoProjectFile(implModelFile, getProject());
				ImplementationModelResource implModelRes = null;
				try {
					implModelRes = new ImplementationModelResource(getProject(), getName(), gsRes, resourceImplModelFile);
				} catch (InvalidFileNameException e1) {
					boolean ok = false;
					for (int i = 0; i < 100 && !ok; i++) {
						try {
							implModelFile = new File(ProjectRestructuration.getExpectedImplementationModelDirectory(getProject()
									.getProjectDirectory(), getName()), getName() + i + ".im");
							resourceImplModelFile = new FlexoProjectFile(implModelFile, getProject());
							implModelRes = new ImplementationModelResource(getProject(), getName(), gsRes, resourceImplModelFile);
							ok = true;
						} catch (InvalidFileNameException e) {
						}
					}
					if (!ok) {
						implModelFile = new File(ProjectRestructuration.getExpectedImplementationModelDirectory(getProject()
								.getProjectDirectory(), getName()), FileUtils.getValidFileName(getName()) + getFlexoID() + ".im");
						resourceImplModelFile = new FlexoProjectFile(implModelFile, getProject());
						try {
							implModelRes = new ImplementationModelResource(getProject(), getName(), gsRes, resourceImplModelFile);
						} catch (InvalidFileNameException e) {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("This should really not happen.");
							}
							return null;
						}
					}
				}
				if (implModelRes == null) {
					return null;
				}
				implModelRes.setResourceData(createNewImplementationModel());

				try {
					implModelRes.getResourceData().setFlexoResource(implModelRes);
					getProject().registerResource(implModelRes);
				} catch (DuplicateResourceException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
					return null;
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Registered new implementation model " + getName() + " file: " + implModelFile);
				}
				returned = implModelRes;
			}
			return returned;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access to project !");
			}
		}
		return null;
	}

	public ImplementationModel createNewImplementationModel() {
		return new ImplementationModel(this, getProject());
	}

	public void notifyImplementationModelHasBeenLoaded() {
		/*setChanged(false);
		if (logger.isLoggable(Level.FINE))
		    logger.fine("Notify observers that WO has been loaded");
		notifyObservers(new ShemaLoaded(this));*/
	}

	@Override
	public String getClassNameKey() {
		return "implementation_model_definition";
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + ".SHEMA." + getName();
	}

	public boolean isLoaded() {
		return hasImplementationModelResource() && getImplementationModelResource().isLoaded();
	}

	public boolean hasImplementationModelResource() {

		return getImplementationModelResource(false) != null;
	}

	public ImplementationModel getImplementationModel() {
		return getImplementationModel(null);
	}

	public ImplementationModel getImplementationModel(FlexoProgress progress) {
		return getImplementationModelResource().getResourceData(progress);
	}

	public static class DuplicateImplementationModelNameException extends FlexoException {

		private String name;

		public DuplicateImplementationModelNameException(String newShemaName) {
			this.name = newShemaName;
		}

		public String getName() {
			return name;
		}
	}

	public String getInspectorName() {
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getGeneratedSourcesMapping();
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getGeneratedSources();
	}

	public GeneratedSources getGeneratedSources() {
		return _generatedSources;
	}
}
