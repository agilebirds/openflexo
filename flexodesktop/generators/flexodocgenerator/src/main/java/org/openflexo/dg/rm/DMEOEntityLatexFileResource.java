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
package org.openflexo.dg.rm;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class DMEOEntityLatexFileResource extends LatexFileResource<DGLatexGenerator<DMEOEntity>> implements FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(DMEOEntityLatexFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public DMEOEntityLatexFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public DMEOEntityLatexFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getEntity() == null) {
			return super.getName();
		}
		registerObserverWhenRequired();
		if (super.getName() == null) {
			setName(nameForRepositoryAndEntity(getCGFile().getRepository(), getEntity()));
		}
		return nameForRepositoryAndEntity(getCGFile().getRepository(), getEntity());
	}

	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getEntity() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getEntity());
			}
			getEntity().addObserver(this);
		}
	}

	public static String nameForRepositoryAndEntity(GenerationRepository repository, DMEOEntity entity) {
		return repository.getName() + ".DMEOENTITY_LATEX." + entity.getFullyQualifiedName();
	}

	public DMEOEntity getEntity() {
		if (getGenerator() != null) {
			return getGenerator().getObject();
		}
		return null;
	}

	@Override
	protected LatexFile createGeneratedResourceData() {
		return new LatexFile(getFile(), this);
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
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getEntity()) {
			if (dataModification instanceof DMEntityClassNameChanged) {
				logger.info("Building new resource after entity renaming");
				DGLatexGenerator<DMEOEntity> generator = getGenerator();
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				generator.refreshConcernedResources();
				generator.getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			} else if (dataModification instanceof DMObjectDeleted && dataModification.oldValue() == getEntity()) {
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
		if (resource instanceof FlexoDMResource) {
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
