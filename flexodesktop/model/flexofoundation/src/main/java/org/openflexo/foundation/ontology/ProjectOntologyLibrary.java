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
package org.openflexo.foundation.ontology;

import java.io.File;

import org.openflexo.foundation.ontology.dm.OntologyImported;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.rm.FlexoProject;

public class ProjectOntologyLibrary extends OntologyLibrary {

	private FlexoProject project;

	public ProjectOntologyLibrary(FlexoResourceCenter resourceCenter, FlexoProject project) {
		super(resourceCenter, resourceCenter.retrieveBaseOntologyLibrary());
		this.project = project;
	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	public ProjectOWLOntology _loadProjectOntology(String ontologyUri, File projectOntologyFile) {
		ProjectOWLOntology projectOntology = new ProjectOWLOntology(ontologyUri, projectOntologyFile, this);
		ontologies.put(ontologyUri, projectOntology);
		setChanged();
		notifyObservers(new OntologyImported(projectOntology));
		return projectOntology;
	}

	/**
	 * Return true if URI is valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	@Override
	public boolean testValidURI(String ontologyURI, String conceptURI) {
		getProject().getProjectOntology().loadWhenUnloaded();
		return super.testValidURI(ontologyURI, conceptURI);
	}

	/**
	 * Return true if URI is valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	@Override
	public boolean isDuplicatedURI(String ontologyURI, String conceptURI) {
		getProject().getProjectOntology().loadWhenUnloaded();
		return super.isDuplicatedURI(ontologyURI, conceptURI);
	}

}
