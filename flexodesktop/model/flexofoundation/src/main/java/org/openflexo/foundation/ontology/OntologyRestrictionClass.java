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

import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;

public abstract class OntologyRestrictionClass extends OntologyClass {

	private static final Logger logger = Logger.getLogger(OntologyRestrictionClass.class.getPackage().getName());

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

	private OntologyProperty property;

	protected OntologyRestrictionClass(Restriction aRestriction, FlexoOntology ontology) {
		super(aRestriction, ontology);
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

	protected void retrieveRestrictionInformations() {
		property = getOntology().retrieveOntologyProperty(restriction.getOnProperty());
	}

	public OntologyProperty getProperty() {
		return property;
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	public abstract String getClassNameKey();

	@Override
	public abstract String getFullyQualifiedName();

	@Override
	public abstract Restriction getOntResource();

	@Override
	public abstract String getDisplayableDescription();

	@Override
	public String getName() {
		return getDisplayableDescription();
	}

	public abstract OntologyClass getObject();

	public abstract OntologicDataType getDataRange();

}
