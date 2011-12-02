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

import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class DMModelLatexFileResource extends LatexFileResource<DGLatexGenerator<DMModel>> implements FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(DMModelLatexFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public DMModelLatexFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public DMModelLatexFileResource(FlexoProject aProject) {
		super(aProject);
	}

	private boolean isObserverRegistered = false;

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getModel() == null) {
			return super.getName();
		}
		registerObserverWhenRequired();
		if (super.getName() == null) {
			setName(nameForRepositoryAndModel(getCGFile().getRepository(), getModel()));
		}
		return nameForRepositoryAndModel(getCGFile().getRepository(), getModel());
	}

	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getModel() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getModel());
			}
			getModel().addObserver(this);
		}
	}

	public static String nameForRepositoryAndModel(GenerationRepository repository, DMModel model) {
		return repository.getName() + ".DMMODEL_LATEX." + model.getName();
	}

	public DMModel getModel() {
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
		addToDependantResources(getProject().getTOCResource());
		addToDependantResources(getProject().getFlexoDMResource());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getModel()) {
			if (dataModification instanceof DMEntityClassNameChanged) {
				logger.info("Building new resource after entity renaming");
				DGLatexGenerator<DMModel> generator = getGenerator();
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				generator.refreshConcernedResources();
				generator.getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
	}

}
