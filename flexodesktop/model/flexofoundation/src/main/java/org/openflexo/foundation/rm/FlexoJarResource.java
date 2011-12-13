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
package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.JarLoader;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;

/**
 * Represents an EOModel resource
 * 
 * @author sguerin
 * 
 */
public class FlexoJarResource extends FlexoImportedResource<JarLoader> {

	private static final Logger logger = Logger.getLogger(FlexoJarResource.class.getPackage().getName());

	private DMModel _dmModel;
	private ExternalRepository _jarRepository;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoJarResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoJarResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoJarResource(FlexoProject aProject, ExternalRepository jarRepository, FlexoDMResource dmResource, FlexoProjectFile jarFile) {
		this(aProject);
		_jarRepository = jarRepository;
		try {
			setResourceFile(jarFile);
		} catch (InvalidFileNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dmResource != null) {
			dmResource.addToDependentResources(this);
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Build new FlexoJarResource");
		}
	}

	public FlexoJarResource(FlexoProject aProject, ExternalRepository jarRepository, JarLoader aJarLoader, FlexoDMResource dmResource,
			FlexoProjectFile jarFile) {
		this(aProject, jarRepository, dmResource, jarFile);
		_resourceData = aJarLoader;
		aJarLoader.setProject(getProject());
		aJarLoader.setFlexoResource(this);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.JAR;
	}

	@Override
	public String getName() {
		return getFile().getName();
	}

	@Override
	public void setName(String aName) {
		// Not allowed
	}

	public JarLoader getJarLoader() {
		try {
			return getImportedData();
		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "File not found exception.", e);
			}
			e.printStackTrace();
		} catch (ProjectLoadingCancelledException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Project loading cancel exception.", e);
			}
			e.printStackTrace();
		} catch (FlexoException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Flexo exception.", e);
			}
			e.printStackTrace();
		}
		return null;
	}

	public DMModel getDMModel() {
		if (_dmModel == null) {
			_dmModel = project.getDataModel();
		}
		return _dmModel;
	}

	public void setDMModel(DMModel dmModel) {
		_dmModel = dmModel;
	}

	@Override
	protected JarLoader doImport() throws FlexoException {
		logger.info("doImport() " + this + " JarRep=" + getJarRepository());
		JarLoader returned = new JarLoader(getFile(), this, getProject());
		returned.setProject(getProject());
		returned.setFlexoResource(this);
		return returned;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		getProject().getFlexoDMResource().addToDependentResources(this);
	}

	public ExternalRepository getJarRepository() {
		return _jarRepository;
	}

	public void setJarRepository(ExternalRepository jarRepository) {
		_jarRepository = jarRepository;
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	/*public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
	   if (resource instanceof FlexoJarResource) {
		   if (resource.getLastUpdate().before(requestDate)) {
			   logger.info("Finalement, "+this+" ne depend pas de "+resource);
			   logger.info("jarDate["+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(resource.getLastUpdate())+"]"
					   +" < requestDate["+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(requestDate)+"]");
			   return false;
		   }
	   }
	   return super.optimisticallyDependsOf(resource, requestDate);
	}*/

}
