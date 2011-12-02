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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.html.DGJSGenerator;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.ProcessRemoved;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class ProcessJSFileResource extends JSFileResource<DGJSGenerator<FlexoProcess>> implements FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(ProcessJSFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public ProcessJSFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public ProcessJSFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getProcess() == null) {
			return super.getName();
		}
		registerObserverWhenRequired();
		if (super.getName() == null) {
			setName(nameForRepositoryAndProcess(getCGFile().getRepository(), getProcess()));
		}
		return nameForRepositoryAndProcess(getCGFile().getRepository(), getProcess());
	}

	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getProcess() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getProject());
			}
			getProcess().addObserver(this);
		}
	}

	public static String nameForRepositoryAndProcess(GenerationRepository repository, FlexoProcess process) {
		return repository.getName() + ".PROCESS_JS." + process.getName();
	}

	public FlexoProcess getProcess() {
		if (getGenerator() != null) {
			return getGenerator().getObject();
		}
		return null;
	}

	@Override
	protected JSFile createGeneratedResourceData() {
		return new JSFile(getFile(), this);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getProcess() != null) {
			addToDependantResources(getProcess().getFlexoResource());
			addToDependantResources(getProject().getScreenshotResource(getProcess(), true));
			for (AbstractActivityNode activityNode : getProcess().getAllEmbeddedAbstractActivityNodes()) {
				addToDependantResources(getProject().getScreenshotResource(activityNode, true));
			}
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getProcess()) {
			if (dataModification instanceof AttributeDataModification) {
				if (((AttributeDataModification) dataModification).getAttributeName().equals("name")
						&& !getCGFile().getFileName().equals(DGJSGenerator.nameForProcess(getProcess(), getGenerator().getRepository()))) {
					logger.info("Building new resource after process renaming");
					DGJSGenerator<FlexoProcess> generator = getGenerator();
					setGenerator(null);
					getCGFile().setMarkedForDeletion(true);
					generator.refreshConcernedResources();
					generator.getRepository().refresh();
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Resource " + getName() + " is marked as deleted");
					}
					observable.deleteObserver(this);
					isObserverRegistered = false;
				}
			}
			if (dataModification instanceof ProcessRemoved && ((ProcessRemoved) dataModification).getRemovedProcess() == getProcess()) {
				logger.info("Handle process has been deleted");
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				getCGFile().getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
	}

}
