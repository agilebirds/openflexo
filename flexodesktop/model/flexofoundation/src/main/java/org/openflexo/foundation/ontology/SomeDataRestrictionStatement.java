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

import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.rdf.model.Statement;

public class SomeDataRestrictionStatement extends DataRestrictionStatement {

	private static final Logger logger = Logger.getLogger(SomeDataRestrictionStatement.class.getPackage().getName());

	private OntologyDataProperty property;
	private OntologicDataType dataRange;

	public SomeDataRestrictionStatement(OntologyObject subject, Statement s, SomeValuesFromRestriction r) {
		super(subject, s, r);
		property = (OntologyDataProperty) getOntologyLibrary().getProperty(r.getOnProperty().getURI());
		dataRange = OntologicDataType.fromURI(r.getSomeValuesFrom().getURI());
	}

	@Override
	public OntologicDataType getDataRange() {
		return dataRange;
	}

	@Override
	public String getClassNameKey() {
		return "some_restriction_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "SomeRestrictionStatement: " + getStatement();
	}

	@Override
	public OntologyDataProperty getProperty() {
		return property;
	}

	@Override
	public String toString() {
		return getSubject().getName() + " "
				+ (property == null ? "<NOT FOUND:" + restriction.getOnProperty().getURI() + ">" : property.getName()) + " some "
				+ getDataRange();
	}

	@Override
	public String getName() {
		return property.getName() + " some";
	}

	@Override
	public int getCardinality() {
		return -1;
	}

	@Override
	public RestrictionType getRestrictionType() {
		return RestrictionType.Some;
	}

}
