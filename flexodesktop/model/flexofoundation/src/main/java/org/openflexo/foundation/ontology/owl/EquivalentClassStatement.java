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
package org.openflexo.foundation.ontology.owl;

import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class EquivalentClassStatement extends OWLStatement {

	private static final Logger logger = Logger.getLogger(EquivalentClassStatement.class.getPackage().getName());

	private OWLObject<?> equivalentObject;

	public EquivalentClassStatement(OWLObject<?> subject, Statement s) {
		super(subject, s);
		if (s.getObject() instanceof Resource) {
			equivalentObject = getOntology().retrieveOntologyObject((Resource) s.getObject());
		} else {
			logger.warning("EquivalentClassStatement: object is not a Resource !");
		}
	}

	@Override
	public String getClassNameKey() {
		return "equivalent_class_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "EquivalentClassStatement: " + getStatement();
	}

	public OWLObject<?> getEquivalentObject() {
		return equivalentObject;
	}

	@Override
	public String toString() {
		return getSubject().getName()
				+ " is equivalent to "
				+ (getEquivalentObject() != null ? getEquivalentObject().getDisplayableDescription() : "<NOT_FOUND:"
						+ getStatement().getObject() + ">");
	}

}
