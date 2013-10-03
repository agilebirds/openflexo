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

import com.hp.hpl.jena.rdf.model.Statement;

public abstract class OWLStatement extends OWLObject {

	private static final Logger logger = Logger.getLogger(OWLStatement.class.getPackage().getName());

	private final OWLConcept<?> _subject;

	private final Statement _statement;

	public OWLStatement(OWLConcept<?> subject, Statement s, OWLTechnologyAdapter adapter) {
		super(adapter);
		_subject = subject;
		_statement = s;

		if (!s.getSubject().equals(_subject.getOntResource())) {
			logger.warning("Inconsistant data: subject is not " + this);
		}
	}

	/**
	 * Not relevant since an OWL Statement has no name, return {@link #getDisplayableDescription()}
	 */
	@Override
	public String getName() {
		return getDisplayableDescription();
	}

	/**
	 * Not relevant since an OWL Statement has no name
	 */
	@Override
	public void setName(String name) throws Exception {
	}

	/**
	 * Return null as OWL statement has no URI
	 */
	@Override
	public String getURI() {
		// a OWL statement has no URI
		return null;
	}

	@Override
	public boolean delete() {
		if (getSubject() != null && getStatement() != null) {
			getSubject().getFlexoOntology().getOntModel().remove(getStatement());
			getSubject().update();
		}
		super.delete();
		return true;
	}

	@Override
	public OWLOntology getFlexoOntology() {
		if (_subject != null) {
			return _subject.getFlexoOntology();
		}
		return null;
	}

	@Override
	public OWLOntology getOntology() {
		return getFlexoOntology();
	}

	public OWLConcept<?> getSubject() {
		return _subject;
	}

	public Statement getStatement() {
		return _statement;
	}

	@Override
	public String getDisplayableDescription() {
		return toString();
	}

	public OWLProperty getPredicate() {
		return getOntology().getProperty(_statement.getPredicate().getURI());
	}
}
