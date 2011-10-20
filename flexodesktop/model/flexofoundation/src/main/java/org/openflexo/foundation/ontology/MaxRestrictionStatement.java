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

public class MaxRestrictionStatement extends ObjectRestrictionStatement {

	private static final Logger logger = Logger.getLogger(MaxRestrictionStatement.class.getPackage().getName());

	private OntologyObjectProperty property;
	private OntologyClass object;
	private int maxCardinality = 0;
	
	public MaxRestrictionStatement(OntologyObject subject, Statement s, Restriction r)
	{
		super(subject,s,r);
		property = (OntologyObjectProperty)getOntologyLibrary().getProperty(r.getOnProperty().getURI());
		
		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS = ResourceFactory.createProperty( OWL + "onClass" );
		Property MAX_QUALIFIED_CARDINALITY = ResourceFactory.createProperty( OWL + "maxQualifiedCardinality" );

		Statement onClassStmt = r.getProperty(ON_CLASS);
		
		Statement maxCardinalityStmt = r.getProperty(MAX_QUALIFIED_CARDINALITY);
		
		RDFNode onClassStmtValue = onClassStmt.getObject();
		RDFNode maxCardinalityStmtValue = maxCardinalityStmt.getObject();
		
		if (onClassStmtValue.isResource() && onClassStmtValue.canAs(OntClass.class)) {
			object = (OntologyClass)getOntologyLibrary().getOntologyObject(((OntClass)onClassStmtValue.as(OntClass.class)).getURI());
		}
		
		if (maxCardinalityStmtValue.isLiteral() && maxCardinalityStmtValue.canAs(Literal.class)) {
			Literal literal = (Literal)maxCardinalityStmtValue.as(Literal.class);
			maxCardinality = literal.getInt();
		}
		
		
		//object = getOntologyLibrary().getOntologyObject(r.get().getURI());
		//cardinality = r.getCardinality();
	}

	@Override
	public String getClassNameKey()
	{
		return "max_restriction_statement";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "MaxRestrictionStatement: "+getStatement();
	}


	@Override
	public OntologyClass getObject() 
	{
		return object;
	}

	@Override
	public OntologyObjectProperty getProperty() 
	{
		return property;
	}


	@Override
	public String toString() 
	{
		return getSubject().getName()+" "+(property==null?"<NOT FOUND:"+restriction.getOnProperty().getURI()+">":property.getName())+" max "+maxCardinality+" "+(getObject() != null ? getObject().getName() : "<NOT_FOUND:"+getStatement().getObject()+">");
	}

	@Override
	public String getName() {
		return property.getName()+" max "+maxCardinality;
	}

	@Override
	public int getCardinality() {
		return maxCardinality;
	}

	@Override
	public RestrictionType getRestrictionType() {
		return RestrictionType.Max;
	}

}
