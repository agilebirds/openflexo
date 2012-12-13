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

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Statement;

public class DataPropertyStatement extends PropertyStatement {

	private static final Logger logger = Logger.getLogger(DataPropertyStatement.class.getPackage().getName());

	private OWLDataProperty property;
	private Literal literal;

	public DataPropertyStatement(OWLConcept<?> subject, Statement s, OWLTechnologyAdapter adapter) {
		super(subject, s, adapter);
		property = getOntology().getDataProperty(s.getPredicate().getURI());
		if (s.getObject() instanceof Literal) {
			literal = (Literal) s.getObject();
		} else {
			logger.warning("DataPropertyStatement: object is not a Literal !");
		}
	}

	@Override
	public OWLDataProperty getProperty() {
		return property;
	}

	public OWLDataType getDataType() {
		if (getProperty() != null) {
			return property.getDataType();
		}
		return null;
	}

	@Override
	public Literal getLiteral() {
		return literal;
	}

	@Override
	public String toString() {
		return (isAnnotationProperty() ? "(A) " : "") + getSubject().getName() + " " + getProperty().getName() + "=\"" + getLiteral()
				+ "\" language=" + getLanguage();
	}

	@Override
	public boolean isAnnotationProperty() {
		return getProperty().isAnnotationProperty();
	}

	@Override
	public OWLDataProperty getPredicate() {
		return (OWLDataProperty) super.getPredicate();
	}

	public Object getValue() {
		if (getDataType() != null && getLiteral() != null) {
			return getDataType().valueFromLiteral(getLiteral());
		}
		return null;
	}

	/**
	 * Creates a new Statement equals to this one with a new value
	 * 
	 * @param anObject
	 */
	public final void setValue(Object aValue) {
		// Take care to this point: this object will disappear and be replaced by a new one
		// during updateOntologyStatements() !!!!!

		// logger.info("Change object from="+getStatementObject().getURI()+" to="+anObject.getURI());
		getSubject().removePropertyStatement(this);
		getSubject().getOntResource().addLiteral(getProperty().getOntProperty(), aValue);
		getSubject().updateOntologyStatements();
	}

}
