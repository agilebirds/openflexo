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

import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentNameChanged2;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;

/**
 * @author gpolet
 * 
 */
public class ComponentLatexFileResource<T extends ComponentDefinition> extends LatexFileResource<DGLatexGenerator<T>> {

	private boolean isObserverRegistered;

	/**
     *
     */
	public ComponentLatexFileResource(FlexoProject project) {
		super(project);
	}

	/**
     *
     */
	public ComponentLatexFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getComponentDefinition() != null) {
			addToDependentResources(getComponentDefinition().getComponentResource());
		}
	}

	public ComponentDefinition getComponentDefinition() {
		if (getGenerator() != null) {
			return getGenerator().getObject();
		}
		return null;
	}

	public void resetGenerator() {
		setGenerator(null);
	}

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getComponentDefinition() == null) {
			return super.getName();
		}
		registerObserverWhenRequired();
		if (super.getName() == null /*||  !super.getName().equals(nameForRepositoryAndComponent(getCGFile().getRepository(), getComponentDefinition()))*/) {
			setName(nameForRepositoryAndComponent(getCGFile().getRepository(), getComponentDefinition()));
		}
		return nameForRepositoryAndComponent(getCGFile().getRepository(), getComponentDefinition());
	}

	public void registerObserverWhenRequired() {
		if (!isObserverRegistered && getComponentDefinition() != null) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getProject());
			}
			getComponentDefinition().addObserver(this);
			getComponentDefinition().getWOComponent().addObserver(this);
		}
	}

	public static String nameForRepositoryAndComponent(GenerationRepository repository, ComponentDefinition cd) {
		if (cd instanceof OperationComponentDefinition) {
			return repository.getName() + ".PAGE_LATEX." + cd.getName();
		} else if (cd instanceof PopupComponentDefinition) {
			return repository.getName() + ".POPUP_LATEX." + cd.getName();
		} else if (cd instanceof PopupComponentDefinition) {
			return repository.getName() + ".TAB_LATEX." + cd.getName();
		} else {
			return repository.getName() + ".COMPONENT_LATEX." + cd.getName();
		}
	}

	/**
	 * Overrides optimisticallyDependsOf
	 * 
	 * @see org.openflexo.dg.rm.LatexFileResource#optimisticallyDependsOf(org.openflexo.foundation.rm.FlexoResource, java.util.Date)
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof FlexoDMResource) {
			FlexoDMResource dmRes = (FlexoDMResource) resource;
			if (dmRes.isLoaded() && getComponentDefinition() != null && getComponentDefinition().getComponentDMEntity() != null) {
				if (!requestDate.before(getComponentDefinition().getComponentDMEntity().getLastUpdate())) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("OPTIMIST DEPENDANCY CHECKING for JAVA COMPONENT " + getComponentDefinition().getName());
					}
					return false;
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getComponentDefinition()) {
			if (dataModification instanceof ComponentNameChanged2) {
				logger.info("Building new resource after component renaming");
				DGLatexGenerator<T> generator = getGenerator();
				resetGenerator();
				getCGFile().setMarkedForDeletion(true);
				generator.refreshConcernedResources();
				generator.getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
			if (dataModification instanceof ComponentDeleted) {
				logger.info("Handle component has been deleted");
				resetGenerator();
				getCGFile().setMarkedForDeletion(true);
				getCGFile().getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
	}
}
