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

public class SubClassStatement extends OntologyStatement {

	private static final Logger logger = Logger.getLogger(SubClassStatement.class.getPackage().getName());

	private OntologyObject<?> parent;

	public SubClassStatement(OntologyObject<?> subject, Statement s) {
		super(subject, s);
		// System.out.println("s.getObject() is a " + s.getObject().getClass().getName() + " : " + s.getObject());
		if (s.getObject() instanceof Resource) {
			parent = getOntology().retrieveOntologyObject((Resource) s.getObject());
		} else {
			logger.warning("SubClassStatement: object is not a Resource !");
		}
	}

	@Override
	public String getClassNameKey() {
		return "sub_class_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "SubClassStatement: " + getStatement();
	}

	public OntologyObject<?> getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return getSubject().getName() + " is a subclass of "
				+ (getParent() != null ? getParent().getName() : "<NOT_FOUND:" + getStatement().getObject() + ">");
	}

}
