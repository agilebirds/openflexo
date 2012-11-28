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

import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;

public abstract class XSOntProperty extends AbstractXSOntObject implements OntologyProperty, XSOntologyURIDefinitions {

	private AbstractXSOntObject domain;
	private boolean noDomainFoundYet = true;

	protected XSOntProperty(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
		domain = ontology.getThingConcept();
	}

	@Override
	public boolean isAnnotationProperty() {
		return false;
	}

	@Override
	public OntologyObject getDomain() {
		return domain;
	}

	public void newDomainFound(AbstractXSOntObject domain) {
		if (noDomainFoundYet) {
			this.domain = domain;
			noDomainFoundYet = false;
		} else {
			this.domain = getOntology().getThingConcept();
		}
	}

	public void resetDomain() {
		this.domain = getOntology().getThingConcept();
		noDomainFoundYet = true;
	}

}
