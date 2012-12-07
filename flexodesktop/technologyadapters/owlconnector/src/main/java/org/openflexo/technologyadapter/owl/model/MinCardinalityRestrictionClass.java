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

import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class MinCardinalityRestrictionClass extends OWLRestriction {

	private static final Logger logger = Logger.getLogger(MinCardinalityRestrictionClass.class.getPackage().getName());

	private final Restriction restriction;
	private OWLClass object;
	private int minCardinality = -1;
	private OWLDataType dataRange;

	@Deprecated
	protected MinCardinalityRestrictionClass(Restriction aRestriction, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aRestriction, ontology, adapter);
		this.restriction = aRestriction;
		retrieveRestrictionInformations();
	}

	protected MinCardinalityRestrictionClass(MinCardinalityRestriction aRestriction, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aRestriction, ontology, adapter);
		this.restriction = aRestriction;
		retrieveRestrictionInformations();
	}

	@Override
	protected void retrieveRestrictionInformations() {
		super.retrieveRestrictionInformations();

		if (restriction instanceof MinCardinalityRestriction) {
			minCardinality = ((MinCardinalityRestriction) restriction).getMinCardinality();
		}

		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS_PROPERTY = ResourceFactory.createProperty(OWL + ON_CLASS);
		Property ON_DATA_RANGE_PROPERTY = ResourceFactory.createProperty(OWL + ON_DATA_RANGE);
		Property MIN_QUALIFIED_CARDINALITY_PROPERTY = ResourceFactory.createProperty(OWL + MIN_QUALIFIED_CARDINALITY);

		Statement onClassStmt = restriction.getProperty(ON_CLASS_PROPERTY);
		Statement onDataRangeStmt = restriction.getProperty(ON_DATA_RANGE_PROPERTY);
		Statement cardinalityStmt = restriction.getProperty(MIN_QUALIFIED_CARDINALITY_PROPERTY);

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
				minCardinality = literal.getInt();
			}
		}

	}

	@Override
	public String getClassNameKey() {
		return "min_cardinality_restriction";
	}

	@Override
	public String getFullyQualifiedName() {
		return "MinCardinalityRestrictionClass:" + getDisplayableDescription();
	}

	@Override
	public Restriction getOntResource() {
		return restriction;
	}

	@Override
	public String getDisplayableDescription() {
		return (getProperty() != null ? getProperty().getName() : "null") + " min " + minCardinality + " "
				+ (getObject() != null ? getObject().getName() : getDataRange());
	}

	@Override
	public OWLClass getObject() {
		return object;
	}

	public int getMinCardinality() {
		return minCardinality;
	}

	@Override
	public OWLDataType getDataRange() {
		return dataRange;
	}

	@Override
	public Integer getLowerBound() {
		return getMinCardinality();
	}

	@Override
	public Integer getUpperBound() {
		return null;
	}

}
