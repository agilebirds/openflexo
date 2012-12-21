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
package org.openflexo.technologyadapter.owl.model;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

public abstract class OWLObject extends FlexoOntologyObjectImpl implements IFlexoOntologyObject, OWL2URIDefinitions, RDFURIDefinitions,
		RDFSURIDefinitions {

	private static final Logger logger = Logger.getLogger(OWLObject.class.getPackage().getName());

	private OWLTechnologyAdapter technologyAdapter;

	public OWLObject(OWLTechnologyAdapter adapter) {
		super();
		technologyAdapter = adapter;
	}

	@Override
	public OWLOntology getOntology() {
		return getFlexoOntology();
	}

	@Override
	public abstract OWLOntology getFlexoOntology();

	@Override
	public boolean isOntology() {
		return false;
	}

	@Override
	public boolean isOntologyClass() {
		return false;
	}

	@Override
	public boolean isOntologyIndividual() {
		return false;
	}

	@Override
	public boolean isOntologyObjectProperty() {
		return false;
	}

	@Override
	public boolean isOntologyDataProperty() {
		return false;
	}

	@Override
	public OWLTechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	public OWLOntologyLibrary getOntologyLibrary() {
		return getTechnologyAdapter().getOntologyLibrary();
	}

	@Override
	public final String getFullyQualifiedName() {
		return getClass().getSimpleName() + "." + getURI();
	}
}
