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
package org.openflexo.technologyadapter.xsd.metamodel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntConcept;
import org.openflexo.technologyadapter.xsd.model.XSOntFeatureAssociation;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSOntologyURIDefinitions;

public abstract class XSOntProperty extends AbstractXSOntConcept implements IFlexoOntologyFeatureAssociation,  IFlexoOntologyStructuralProperty, XSOntologyURIDefinitions, IXMLAttribute {

	protected XSOntClass domain;
	private boolean noDomainFoundYet = true;

	protected XSOntProperty(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);
		domain = ontology.getRootConcept();
	}

	@Override
	public boolean isAnnotationProperty() {
		return false;
	}

	public void newDomainFound(XSOntClass domain) {
		if (noDomainFoundYet) {
			this.domain = domain;
			noDomainFoundYet = false;
		} else {
			this.domain = getOntology().getRootConcept();
		}
	}

	public void resetDomain() {
		this.domain = getOntology().getRootConcept();
		noDomainFoundYet = true;
	}
	

	@Override
	public XSOntClass getDomain() {
		return domain;
	}


	@Override
	public Type getAttributeType() {
		return (Type) this.getRange();
	}

	@Override
	public XSOntology getContainer() {
		return getOntology();
	}

	@Override
	public List<IFlexoOntologyFeatureAssociation> getStructuralFeatureAssociations() {
		return Collections.emptyList();
	}
}
