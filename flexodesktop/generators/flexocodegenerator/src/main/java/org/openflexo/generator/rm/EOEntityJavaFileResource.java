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
package org.openflexo.generator.rm;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.dm.GenericRecordGenerator;
import org.openflexo.logging.FlexoLogger;

/**
 * @author sylvain
 * 
 */
public class EOEntityJavaFileResource extends JavaFileResource<GenericRecordGenerator, CGJavaFile> implements
		GenerationAvailableFileResource, FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(EOEntityJavaFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public EOEntityJavaFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public EOEntityJavaFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getEntity() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getEntity());
			}
			getEntity().addObserver(this);
		}
	}

	public DMEOEntity getEntity() {
		if (getGenerator() != null) {
			return getGenerator().getEOEntity();
		}
		return null;
	}

	@Override
	protected EOEntityJavaFile createGeneratedResourceData() {
		return new EOEntityJavaFile(getFile(), this);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToDependentResources(getProject().getFlexoDMResource());
	}

	@Override
	public EOEntityJavaFile getGeneratedResourceData() {
		return (EOEntityJavaFile) super.getGeneratedResourceData();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getEntity()) {
			if (dataModification instanceof DMEntityClassNameChanged) {
				logger.info("Building new resource after entity renaming");
				FlexoResourceGenerator generator = getGenerator();
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				generator.refreshConcernedResources();
				generator.getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			} else if (dataModification instanceof DMObjectDeleted) {
				logger.info("Handle entity has been deleted");
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				getCGFile().getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
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
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("OPTIMIST DEPENDANCY CHECKING for ENTITY " + getEntity().getName());
					}
					return false;
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

}
