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

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;

public abstract class XSOntProperty extends AbstractXSOntConcept implements IFlexoOntologyStructuralProperty, XSOntologyURIDefinitions {

	private XSOntClass domain;
	private boolean noDomainFoundYet = true;
	private List<XSOntRestriction> referencingRestrictions;

	protected XSOntProperty(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);
		domain = ontology.getRootConcept();
		referencingRestrictions = new ArrayList<XSOntRestriction>();
	}

	@Override
	public boolean isAnnotationProperty() {
		return false;
	}

	@Override
	public XSOntClass getDomain() {
		return domain;
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
	public XSOntology getContainer() {
		return getOntology();
	}

	@Override
	public List<IFlexoOntologyFeatureAssociation> getFeatureAssociations() {
		return null;
	}

	public List<XSOntRestriction> getReferencingRestrictions() {
		return referencingRestrictions;
	}

	public void addToReferencingRestriction(XSOntRestriction aRestriction) {
		referencingRestrictions.add(aRestriction);
	}

	public void removeFromReferencingRestriction(XSOntRestriction aRestriction) {
		referencingRestrictions.remove(aRestriction);
	}

	@Override
	public List<XSOntRestriction> getReferencingFeatureAssociations() {
		return getReferencingRestrictions();
	}
}
