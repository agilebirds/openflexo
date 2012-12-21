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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class TypeStatement extends OWLStatement {

	private static final Logger logger = Logger.getLogger(TypeStatement.class.getPackage().getName());

	private OWLConcept<?> type;

	public TypeStatement(OWLConcept<?> subject, Statement s, OWLTechnologyAdapter adapter) {
		super(subject, s, adapter);
		if (s.getObject() instanceof Resource) {
			type = getOntology().retrieveOntologyObject((Resource) s.getObject());
		} else {
			logger.warning("TypeStatement: object is not a Resource !");
		}
	}

	public OWLConcept<?> getType() {
		return type;
	}

	@Override
	public String toString() {
		return getSubject().getName() + " is a "
				+ (getType() != null ? getType().getName() : "<NOT_FOUND:" + getStatement().getObject() + ">");
	}

}
