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

import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class CardinalityRestrictionClass extends OWLRestriction {

	private static final Logger logger = Logger.getLogger(CardinalityRestrictionClass.class.getPackage().getName());

	private final Restriction restriction;
	private OWLClass object;
	private int cardinality = -1;
	private OWLDataType dataRange;

	@Deprecated
	protected CardinalityRestrictionClass(Restriction aRestriction, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aRestriction, ontology, adapter);
		this.restriction = aRestriction;
		retrieveRestrictionInformations();
	}

	protected CardinalityRestrictionClass(CardinalityRestriction aRestriction, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aRestriction, ontology, adapter);
		this.restriction = aRestriction;
		retrieveRestrictionInformations();
	}

	@Override
	protected void retrieveRestrictionInformations() {
		super.retrieveRestrictionInformations();

		if (restriction instanceof CardinalityRestriction) {
			cardinality = ((CardinalityRestriction) restriction).getCardinality();
		}

		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS_PROPERTY = ResourceFactory.createProperty(OWL + ON_CLASS);
		Property ON_DATA_RANGE_PROPERTY = ResourceFactory.createProperty(OWL + ON_DATA_RANGE);
		Property QUALIFIED_CARDINALITY_PROPERTY = ResourceFactory.createProperty(OWL + QUALIFIED_CARDINALITY);

		Statement onClassStmt = restriction.getProperty(ON_CLASS_PROPERTY);
		Statement onDataRangeStmt = restriction.getProperty(ON_DATA_RANGE_PROPERTY);
		Statement cardinalityStmt = restriction.getProperty(QUALIFIED_CARDINALITY_PROPERTY);

		if (onClassStmt != null) {
			RDFNode onClassStmtValue = onClassStmt.getObject();
			if (onClassStmtValue != null && onClassStmtValue.canAs(OntClass.class)) {
				object = getOntology().retrieveOntologyClass(onClassStmtValue.as(OntClass.class));
			}
		}

		if (onDataRangeStmt != null) {
			RDFNode onDataRangeStmtValue = onDataRangeStmt.getObject();
			dataRange = getTechnologyAdapter().getOntologyLibrary().getDataType(((Resource) onDataRangeStmtValue).getURI());
		}

		if (cardinalityStmt != null) {
			RDFNode cardinalityStmtValue = cardinalityStmt.getObject();
			if (cardinalityStmtValue.isLiteral() && cardinalityStmtValue.canAs(Literal.class)) {
				Literal literal = cardinalityStmtValue.as(Literal.class);
				cardinality = literal.getInt();
			}
		}

	}

	@Override
	public String getClassNameKey() {
		return "cardinality_restriction";
	}

	@Override
	public String getFullyQualifiedName() {
		return "CardinalityRestrictionClass:" + getDisplayableDescription();
	}

	@Override
	public Restriction getOntResource() {
		return restriction;
	}

	@Override
	public String getDisplayableDescription() {
		return (getProperty() != null ? getProperty().getName() : "null") + " exact " + cardinality + " "
				+ (getObject() != null ? getObject().getName() : getDataRange());
	}

	@Override
	public OWLClass getObject() {
		return object;
	}

	public int getCardinality() {
		return cardinality;
	}

	@Override
	public OWLDataType getDataRange() {
		return dataRange;
	}

	@Override
	public Integer getLowerBound() {
		return getCardinality();
	}

	@Override
	public Integer getUpperBound() {
		return getCardinality();
	}

}
