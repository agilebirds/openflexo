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

import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.rdf.model.Statement;

public class OnlyRestrictionStatement extends ObjectRestrictionStatement {

	private static final Logger logger = Logger.getLogger(OnlyRestrictionStatement.class.getPackage().getName());

	private OntologyObjectProperty property;
	private OntologyClass object;

	public OnlyRestrictionStatement(OntologyObject subject, Statement s, AllValuesFromRestriction r) {
		super(subject, s, r);
		if (r.getOnProperty() != null) {
			property = (OntologyObjectProperty) getOntologyLibrary().getProperty(r.getOnProperty().getURI());
		}
		if (r.getAllValuesFrom() != null) {
			object = (OntologyClass) getOntologyLibrary().getOntologyObject(r.getAllValuesFrom().getURI());
		}
	}

	@Override
	public String getClassNameKey() {
		return "only_restriction_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "OnlyRestrictionStatement: " + getStatement();
	}

	@Override
	public OntologyClass getObject() {
		return object;
	}

	@Override
	public OntologyObjectProperty getProperty() {
		return property;
	}

	@Override
	public String toString() {
		return getSubject().getName() + " "
				+ (property == null ? "<NOT FOUND:" + restriction.getOnProperty().getURI() + ">" : property.getName()) + " only "
				+ (getObject() != null ? getObject().getName() : "<NOT_FOUND:" + getStatement().getObject() + ">");
	}

	@Override
	public String getName() {
		return property.getName() + " only";
	}

	@Override
	public int getCardinality() {
		return -1;
	}

	@Override
	public RestrictionType getRestrictionType() {
		return RestrictionType.Only;
	}

}
