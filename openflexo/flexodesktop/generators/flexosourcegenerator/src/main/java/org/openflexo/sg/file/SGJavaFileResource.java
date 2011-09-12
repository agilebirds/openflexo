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
package org.openflexo.sg.file;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sg.generator.SGGenerator;
import org.openflexo.sg.generator.SGJavaClassGenerator;


/**
 * @author sylvain
 * 
 */
public class SGJavaFileResource extends JavaFileResource<SGJavaClassGenerator, SGJavaFile> implements GenerationAvailableFileResource, FlexoObserver {
	static final Logger logger = FlexoLogger.getLogger(SGJavaFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public SGJavaFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public SGJavaFileResource(FlexoProject aProject) {
		super(aProject);
	}

	public DMEntity getEntity() {
		if (getGenerator() != null)
			return getGenerator().getEntity();
		return null;
	}

	private boolean isObserverRegistered = false;

	@Override
	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getEntity() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE))
				logger.fine("*** addObserver " + getFileName() + " for " + getEntity());
			getEntity().addObserver(this);
		}
	}

	@Override
	protected GeneratedJavaFile createGeneratedResourceData() {
		return new GeneratedJavaFile(getFile(), this);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();

		if (getEntity() != null) {
			addToDependantResources(getProject().getFlexoDMResource());
		}
		if (getGenerator() != null)
			getGenerator().rebuildDependanciesForResource(this);
	}

	@Override
	public GeneratedJavaFile getGeneratedResourceData() {
		return (GeneratedJavaFile) super.getGeneratedResourceData();
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof TemplateLocator) {
			return ((TemplateLocator) resource).needsUpdateForResource(this);
		} else if (resource instanceof FlexoDMResource) {
			FlexoDMResource dmRes = (FlexoDMResource) resource;
			if (dmRes.isLoaded() && getEntity() != null) {
				if (!requestDate.before(getEntity().getLastUpdate())) {
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for UTIL JAVA FILE " + getEntity().getName());
					return false;
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getEntity()) {
			if (dataModification instanceof DMEntityClassNameChanged) {
				logger.info("Building new resource after entity renaming");
				SGGenerator generator = getGenerator();
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				generator.refreshConcernedResources();
				generator.getRepository().refresh();
			} else if (dataModification instanceof EntityDeleted) {
				logger.info("Handle entity has been deleted");
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				getCGFile().getRepository().refresh();
			}
		}

	}

	@Override
	public SourceRepository getRepository() {
		return getGenerator().getProjectGenerator().getRepository();
	}

}
