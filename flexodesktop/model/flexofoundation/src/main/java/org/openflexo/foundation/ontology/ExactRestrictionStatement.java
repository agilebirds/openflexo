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

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class ExactRestrictionStatement extends ObjectRestrictionStatement {

	private static final Logger logger = Logger.getLogger(ExactRestrictionStatement.class.getPackage().getName());

	private OntologyProperty property;
	private OntologyObject object;
	private int cardinality = 0;
	
	public ExactRestrictionStatement(OntologyObject subject, Statement s, Restriction r)
	{
		super(subject,s,r);
		property = getOntologyLibrary().getProperty(r.getOnProperty().getURI());
		
		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS = ResourceFactory.createProperty( OWL + "onClass" );
		Property QUALIFIED_CARDINALITY = ResourceFactory.createProperty( OWL + "qualifiedCardinality" );

		Statement onClassStmt = r.getProperty(ON_CLASS);
		Statement cardinalityStmt = r.getProperty(QUALIFIED_CARDINALITY);
		
		RDFNode onClassStmtValue = onClassStmt.getObject();
		RDFNode cardinalityStmtValue = cardinalityStmt.getObject();
		
		//System.out.println("onClassStmtValue="+onClassStmtValue);
		//System.out.println("cardinalityStmtValue="+cardinalityStmtValue);

		if (onClassStmtValue.isResource() && onClassStmtValue.canAs(OntClass.class)) {
			object = getOntologyLibrary().getOntologyObject(((OntClass)onClassStmtValue.as(OntClass.class)).getURI());
		}
		
		if (cardinalityStmtValue.isLiteral() && cardinalityStmtValue.canAs(Literal.class)) {
			Literal literal = (Literal)cardinalityStmtValue.as(Literal.class);
			cardinality = literal.getInt();
		}
		
		
		//object = getOntologyLibrary().getOntologyObject(r.get().getURI());
		//cardinality = r.getCardinality();
	}

	@Override
	public String getClassNameKey()
	{
		return "exact_restriction_statement";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "ExactRestrictionStatement: "+getStatement();
	}


	@Override
	public OntologyObject getObject() 
	{
		return object;
	}

	@Override
	public OntologyProperty getProperty() 
	{
		return property;
	}

	@Override
	public String toString() 
	{
		return getSubject().getName()+" "+(property==null?"<NOT FOUND:"+restriction.getOnProperty().getURI()+">":property.getName())+" exact "+cardinality+" "+(getObject() != null ? getObject().getName() : "<NOT_FOUND:"+getStatement().getObject()+">");
	}

	@Override
	public String getName() {
		return property.getName()+" exact "+cardinality;
	}


}
