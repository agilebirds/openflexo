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

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class ObjectPropertyStatement extends PropertyStatement {

	private static final Logger logger = Logger.getLogger(ObjectPropertyStatement.class.getPackage().getName());

	private OWLObjectProperty property;
	private OWLObject<?> statementObject;
	private Literal literal;

	public ObjectPropertyStatement(OWLObject<?> subject, Statement s) {
		super(subject, s);
		property = getOntology().getObjectProperty(s.getPredicate().getURI());
		if (s.getObject() instanceof Resource) {
			statementObject = getOntology().retrieveOntologyObject((Resource) s.getObject());
			/*if (statementObject instanceof FlexoOntology) {
				System.out.println("OK, i have my ontology with resource "+s.getObject().getClass().getName());
				System.out.println("resource: "+s.getObject());
				System.out.println("uri: "+((Resource)s.getObject()).getURI());
			}*/
			if (statementObject == null) {
				logger.warning("Ontology: " + getOntology() + " cannot retrieve " + s.getObject() + " for statement " + s);
			}
		} else if (s.getObject() instanceof Literal) {
			literal = (Literal) s.getObject();
		} else {
			logger.warning("ObjectPropertyStatement: object is not a Resource nor a Litteral !");
		}
	}

	@Override
	public OWLObjectProperty getPredicate() {
		return (OWLObjectProperty) super.getPredicate();
	}

	@Override
	public Literal getLiteral() {
		return literal;
	}

	@Override
	public String getClassNameKey() {
		return "object_property_statement";
	}

	@Override
	public String getFullyQualifiedName() {
		return "ObjectPropertyStatement: " + getStatement();
	}

	@Override
	public OWLObjectProperty getProperty() {
		return property;
	}

	public OWLObject<?> getStatementObject() {
		return statementObject;
	}

	/**
	 * Creates a new Statement equals to this one with a new Object value
	 * 
	 * @param anObject
	 */
	public final void setStatementObject(OWLObject<?> anObject) {
		// Take care to this point: this object will disappear and be replaced by a new one
		// during updateOntologyStatements() !!!!!

		// logger.info("Change object from="+getStatementObject().getURI()+" to="+anObject.getURI());
		getSubject().removePropertyStatement(this);
		getSubject().getOntResource().addProperty(getProperty().getOntProperty(), anObject.getResource());
		getSubject().updateOntologyStatements();
		statementObject = anObject;
	}

	@Override
	public String toString() {
		if (hasLitteralValue()) {
			return (isAnnotationProperty() ? "(A) " : "") + getSubject().getName() + " " + getProperty().getName() + "=\"" + getLiteral()
					+ "\" language=" + getLanguage();
		} else {
			return (isAnnotationProperty() ? "(A) " : "") + getSubject().getName() + " " + getProperty().getName() + " "
					+ (getStatementObject() != null ? getStatementObject().getName() : getStatementObject());
		}
	}

	@Override
	public boolean isAnnotationProperty() {
		return getProperty().isAnnotationProperty();
	}

}
