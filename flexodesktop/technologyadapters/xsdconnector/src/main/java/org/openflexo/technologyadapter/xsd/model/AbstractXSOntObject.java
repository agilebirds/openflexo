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

import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import java.util.UUID;

public abstract class AbstractXSOntObject extends FlexoOntologyObjectImpl implements IFlexoOntologyObject, XSOntologyURIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(AbstractXSOntObject.class
			.getPackage().getName());

	protected String uri;
	private String name;
	private XSOntology ontology;

	private XSDTechnologyAdapter technologyAdapter;

	protected AbstractXSOntObject(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super();

		technologyAdapter = adapter;
		this.name = name;
		if (uri == null ){
			// defaults to a UUID in case no URI provided, as for individuals e.g.
			// TODO: fix this when refactoring XSDConnector that should not inherit from FlexoOntology!
			this.uri = UUID.randomUUID().toString();
			}
		else {
			this.uri = uri;
		}
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
	public XSOntology getOntology() {
		return ontology;
	}

	@Override
	public String getFullyQualifiedName() {
		return getURI();
	}

}
