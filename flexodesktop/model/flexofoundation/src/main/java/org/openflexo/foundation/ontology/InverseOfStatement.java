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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class InverseOfStatement extends OntologyStatement {

	private static final Logger logger = Logger.getLogger(InverseOfStatement.class.getPackage().getName());

	public static final String INVERSE_OF_URI = "http://www.w3.org/2002/07/owl#inverseOf";

	private OntologyObject inverseProperty;

	public InverseOfStatement(OntologyObject subject, Statement s) {
		super(subject, s);
		if (s.getObject() instanceof Resource) {
			inverseProperty = getOntologyLibrary().getOntologyObject(((Resource) s.getObject()).getURI());
		} else {
			logger.warning("SubPropertyStatement: object is not a Resource !");
		}
	}

	@Override
	public String getClassNameKey() {
		return "inverse_property_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "InverseOfStatement: " + getStatement();
	}

	public OntologyObject getInverseProperty() {
		return inverseProperty;
	}

	@Override
	public String toString() {
		return getSubject().getName() + " is inverse property of "
				+ (getInverseProperty() != null ? getInverseProperty().getName() : "<NOT_FOUND:" + getStatement().getObject() + ">");
	}

}
