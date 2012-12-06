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

public class RangeStatement extends OWLStatement {

	private static final Logger logger = Logger.getLogger(RangeStatement.class.getPackage().getName());

	private OWLObject<?> range;
	private OWLDataType dataType;

	public OWLDataType getDataType() {
		return dataType;
	}

	public RangeStatement(OWLObject<?> subject, Statement s, OWLTechnologyAdapter adapter) {
		super(subject, s, adapter);
		if (s.getObject() instanceof Resource) {
			range = getOntology().retrieveOntologyObject((Resource) s.getObject());
			if (((Resource) s.getObject()).getURI() != null) {
				dataType = getTechnologyAdapter().getOntologyLibrary().getDataType(((Resource) s.getObject()).getURI());
			}
		} else {
			logger.warning("RangeStatement: object is not a Resource !");
		}
	}

	@Override
	public String getClassNameKey() {
		return "range_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "RangeStatement: " + getStatement();
	}

	public OWLObject<?> getRange() {
		return range;
	}

	@Override
	public String toString() {
		if (getDataType() != null) {
			return getSubject().getName() + " has range " + getDataType().getURI();
		}
		return getSubject().getName() + " has range "
				+ (getRange() != null ? getRange().getName() : "<NOT_FOUND:" + getStatement().getObject() + ">");
	}

	public String getStringRepresentation() {
		if (getDataType() != null) {
			return getDataType().toString();
		}
		if (getRange() != null) {
			return getRange().getName();
		}
		return "";
	}
}
