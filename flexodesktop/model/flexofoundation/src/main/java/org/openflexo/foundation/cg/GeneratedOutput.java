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
package org.openflexo.foundation.cg;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.dm.CGRepositoryCreated;
import org.openflexo.foundation.cg.dm.CGRepositoryDeleted;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.rm.FlexoGeneratedOutputResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.XMLMapping;

/**
 * GeneratedCode represents the structure of all the generated code
 * 
 * @author sylvain
 */

public abstract class GeneratedOutput<RD extends GeneratedOutput<RD>> extends CGObject implements XMLStorageResourceData<RD> {

	public static interface GeneratorFactory {

	}

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GeneratedOutput.class.getPackage().getName());

	protected FlexoGeneratedOutputResource _resource;

	private Vector<GenerationRepository> _generatedRepositories;

	private GeneratorFactory factory;

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public GeneratedOutput(FlexoProject project) {
		super(project);
		setGeneratedCode(this);
		_generatedRepositories = new Vector<GenerationRepository>();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public FlexoXMLStorageResource getFlexoResource() {
		return _resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return _resource;
	}

	/**
	 * Overrides getXMLMapping
	 * 
	 * @see org.openflexo.foundation.cg.CGObject#getXMLMapping()
	 */
	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getGeneratedCodeMapping();
	}

	// public abstract GenerationRepository getOrCreateDefaultRepository ();

	public abstract String getDefaultRepositoryName();

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoGeneratedOutputResource) resource;
	}

	/**
	 * Save this object using ResourceManager scheme
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();

	}

	public Vector<GenerationRepository> getGeneratedRepositories() {
		return _generatedRepositories;
	}

	public void setGeneratedRepositories(Vector<GenerationRepository> generatedCodeRepositories) {
		_generatedRepositories = generatedCodeRepositories;
	}

	public void addToGeneratedRepositories(GenerationRepository generatedCodeRepository) {
		_generatedRepositories.add(generatedCodeRepository);
		generatedCodeRepository.setGeneratedCode(this);
		setChanged();
		notifyObservers(new CGRepositoryCreated(generatedCodeRepository));
	}

	public void removeFromGeneratedRepositories(GenerationRepository generatedCodeRepository) {
		_generatedRepositories.remove(generatedCodeRepository);
		generatedCodeRepository.setGeneratedCode(null);
		setChanged();
		notifyObservers(new CGRepositoryDeleted(generatedCodeRepository));
	}

	public GenerationRepository getRepositoryNamed(String name) {
		for (GenerationRepository repository : getGeneratedRepositories()) {
			if (repository.getName().equals(name)) {
				return repository;
			}
		}
		return null;
	}

	/**
	 * @param selectedDMPackage
	 * @return
	 */
	public String getNextGeneratedCodeRepositoryName() {
		String baseName = FlexoLocalization.localizedForKey(getDefaultRepositoryName());
		String testMe = baseName;
		int test = 0;
		while (getRepositoryNamed(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	@Override
	public boolean isContainedIn(CGObject obj) {
		return obj == this;
	}

	@Override
	public boolean hasGenerationErrors() {
		for (GenerationRepository repository : _generatedRepositories) {
			if (repository.hasGenerationErrors()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean needsRegeneration() {
		for (GenerationRepository repository : _generatedRepositories) {
			if (repository.needsRegeneration()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean needsModelReinjection() {
		for (GenerationRepository repository : _generatedRepositories) {
			if (repository.needsModelReinjection()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public GenerationStatus getGenerationStatus() {
		GenerationStatus generationStatus = GenerationStatus.UpToDate;
		for (GenerationRepository repository : _generatedRepositories) {
			if (repository.getGenerationStatus() == GenerationStatus.GenerationModified) {
				return GenerationStatus.GenerationModified;
			}
			if (repository.getGenerationStatus() != GenerationStatus.UpToDate) {
				generationStatus = GenerationStatus.Unknown;
			}
		}
		return generationStatus;
	}

	public abstract CGTemplates getTemplates();

	public GeneratorFactory getFactory() {
		return factory;
	}

	public void setFactory(GeneratorFactory factory) {
		// Do not notify, this is simply a GeneratorFactory set by editors
		this.factory = factory;
	}

}
