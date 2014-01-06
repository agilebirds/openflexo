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

import org.openflexo.dg.html.DGJSGenerator;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.wkf.ProcessFolder;

/**
 * @author gpolet
 * 
 */
public class ProcessFolderJSFileResource extends JSFileResource<DGJSGenerator<ProcessFolder>> implements FlexoObserver {
	/**
	 * @param builder
	 */
	public ProcessFolderJSFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public ProcessFolderJSFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getProcessFolder() == null) {
			return super.getName();
		}
		registerObserverWhenRequired();
		if (super.getName() == null) {
			setName(nameForRepositoryAndProcessFolder(getCGFile().getRepository(), getProcessFolder()));
		}
		return nameForRepositoryAndProcessFolder(getCGFile().getRepository(), getProcessFolder());
	}

	public void registerObserverWhenRequired() {
		if (!isObserverRegistered && getProcessFolder() != null) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getProject());
			}
			getProcessFolder().addObserver(this);
		}
	}

	public static String nameForRepositoryAndProcessFolder(GenerationRepository repository, ProcessFolder processFolder) {
		return repository.getName() + ".PROCESSFOLDER_JS." + processFolder.getName() + "-" + processFolder.getFlexoID()
				+ (repository.getProject() == processFolder.getProject() ? "" : " " + processFolder.getProject().getProjectURI());
	}

	public ProcessFolder getProcessFolder() {
		if (getGenerator() != null) {
			return getGenerator().getObject();
		}
		return null;
	}

	@Override
	protected JSFile createGeneratedResourceData() {
		return new JSFile(getFile(), this);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getProcessFolder()) {
			if (dataModification instanceof AttributeDataModification) {
				if (((AttributeDataModification) dataModification).getAttributeName().equals("name")
						&& !getCGFile().getFileName().equals(
								DGJSGenerator.nameForProcessFolder(getProcessFolder(), getGenerator().getRepository()))) {
					logger.info("Building new resource after process folder renaming");
					DGJSGenerator<ProcessFolder> generator = getGenerator();
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
			if (dataModification instanceof ObjectDeleted && ((ObjectDeleted) dataModification).getDeletedObject() == getProcessFolder()) {
				logger.info("Handle process folder has been deleted");
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				getCGFile().getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
	}

}
