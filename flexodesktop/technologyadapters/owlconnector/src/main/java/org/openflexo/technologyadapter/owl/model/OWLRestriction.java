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

import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;

public abstract class OWLRestriction extends OWLClass implements IFlexoOntologyFeatureAssociation {

	private static final Logger logger = Logger.getLogger(OWLRestriction.class.getPackage().getName());

	static final String ON_CLASS = "onClass";
	static final String ON_DATA_RANGE = "onDataRange";
	static final String QUALIFIED_CARDINALITY = "qualifiedCardinality";
	static final String MIN_QUALIFIED_CARDINALITY = "minQualifiedCardinality";
	static final String MAX_QUALIFIED_CARDINALITY = "maxQualifiedCardinality";

	public static enum RestrictionType {
		Some, // SomValuesFromRestriction
		Only, // AllValuesFromRestriction
		HasValue, // HasValueRestriction
		Exact, // CardinalityRestriction
		Min, // MinCardinalityRestriction
		Max // MaxCardinalityRestriction
	}

	private final Restriction restriction;
	private OWLProperty property;
	private OWLClass domain;

	protected OWLRestriction(Restriction aRestriction, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aRestriction, ontology, adapter);
		this.restriction = aRestriction;
	}

	@Override
	protected void init() {
		super.init();
		retrieveRestrictionInformations();
	}

	@Override
	protected void update(OntClass anOntClass) {
		super.update(anOntClass);
		retrieveRestrictionInformations();
	}

	private boolean addedToReferencingRestriction = false;

	protected void retrieveRestrictionInformations() {
		property = getOntology().retrieveOntologyProperty(restriction.getOnProperty());
		if (!addedToReferencingRestriction) {
			property.addToReferencingRestriction(this);
		}
	}

	public OWLProperty getProperty() {
		return property;
	}

	@Override
	public boolean delete() {
		return super.delete();
	}

	@Override
	public abstract Restriction getOntResource();

	@Override
	public abstract String getDisplayableDescription();

	@Override
	public String getName() {
		return getDisplayableDescription();
	}

	public abstract OWLConcept<?> getObject();

	public abstract OWLDataType getDataRange();

	@Override
	public boolean isNamedClass() {
		return false;
	}

	@Override
	public OWLClass getDomain() {
		return domain;
	}

	public void setDomain(OWLClass domain) {
		this.domain = domain;
	}

	@Override
	public OWLProperty getFeature() {
		return getProperty();
	}

	@Override
	public IFlexoOntologyObject getRange() {
		if (getFeature() instanceof IFlexoOntologyStructuralProperty) {
			return ((IFlexoOntologyStructuralProperty) getFeature()).getRange();
		}
		return null;
	}

}
