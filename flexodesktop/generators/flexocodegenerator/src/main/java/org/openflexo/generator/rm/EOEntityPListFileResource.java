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

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.PListFileResource;
import org.openflexo.generator.cg.CGPListFile;
import org.openflexo.generator.dm.EOEntityPListGenerator;
import org.openflexo.generator.rm.GenerationAvailableFileResource;

public class EOEntityPListFileResource extends PListFileResource<EOEntityPListGenerator, CGPListFile> implements
		GenerationAvailableFileResource, FlexoObserver {

	private boolean isObserverRegistered = false;

	public EOEntityPListFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	public EOEntityPListFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	protected EOEntityPListFile createGeneratedResourceData() {
		return new EOEntityPListFile(getFile(), this);
	}

	public DMEOEntity getEntity() {
		if (getGenerator() != null)
			return getGenerator().getEntity();
		return null;
	}

	public void registerObserverWhenRequired() {
		if (!isObserverRegistered && getEntity() != null) {
			isObserverRegistered = true;
			getEntity().addObserver(this);
		}
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getEntity()) {
			if (dataModification instanceof DMAttributeDataModification) {
				if (((DMAttributeDataModification) dataModification).getAttributeName().equals(DMEOEntity.ENTITY_CLASS_NAME_KEY)) {
					logger.info("Building new resource after entity renaming");
					EOEntityPListGenerator generator = getGenerator();
					setGenerator(null);
					getCGFile().setMarkedForDeletion(true);
					generator.refreshConcernedResources();
					generator.getRepository().refresh();
					observable.deleteObserver(this);
					isObserverRegistered = false;
				}
			} else if (dataModification instanceof EntityDeleted) {
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
	 * @param repository
	 * @param entity
	 * @return
	 */
	public static String nameForRepositoryAndEntity(CGRepository repository, DMEOEntity entity) {
		return repository.getName() + ".EOENTITY_PLIST" + entity.getFullyQualifiedName();
	}

	@Override
	public EOEntityPListFile getGeneratedResourceData() {
		return (EOEntityPListFile) super.getGeneratedResourceData();
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
		if (resource instanceof FlexoDMResource) {
			FlexoDMResource dmRes = (FlexoDMResource) resource;
			if (dmRes.isLoaded() && getEntity() != null) {
				if (!requestDate.before(getEntity().getLastUpdate())) {
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for PLIST EOENTITY " + getEntity().getName());
					return false;
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
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
	}

	static String getDefaultFileName(DMEOEntity entity) {
		String fullQualifiedName = entity.getFullyQualifiedName();
		String basicName = fullQualifiedName;
		if (fullQualifiedName.lastIndexOf(".") > -1)
			basicName = fullQualifiedName.substring(fullQualifiedName.lastIndexOf(".") + 1);
		return basicName + ".plist";
	}

}
