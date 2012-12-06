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
package org.openflexo.technologyadapter.xsd.model;

import java.util.logging.Level;

import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectConverter;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.xmlcode.StringConvertable;

public abstract class AbstractXSOntObject extends AbstractOntologyObject implements IFlexoOntologyObject, XSOntologyURIDefinitions,
		InspectableObject, StringConvertable<IFlexoOntologyConcept> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(AbstractXSOntObject.class
			.getPackage().getName());

	private String uri;
	private String name;
	private XSOntology ontology;

	private XSDTechnologyAdapter technologyAdapter;

	protected AbstractXSOntObject(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super();

		technologyAdapter = adapter;
		this.name = name;
		this.uri = uri;
		this.ontology = ontology;
	}

	protected AbstractXSOntObject(XSDTechnologyAdapter adapter) {
		this(null, null, null, adapter);
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("The ontology object name changed, renaming of the object URI not implemented yet");
		}
	}

	public boolean getIsReadOnly() {
		return getFlexoOntology().getIsReadOnly();
	}

	@Override
	public XSOntology getFlexoOntology() {
		return ontology;
	}

	@Override
	public OntologyObjectConverter getConverter() {
		/*if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getOntologyObjectConverter();
		}*/
		return null;
	}

	@Override
	public XSOntology getOntology() {
		return ontology;
	}

	@Override
	public String getFullyQualifiedName() {
		return getURI();
	}

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
	@Deprecated
	public abstract String getClassNameKey();

	@Override
	@Deprecated
	public String getInspectorName() {
		return null;
	}

}
