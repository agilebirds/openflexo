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

import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class MaxDataRestrictionStatement extends DataRestrictionStatement {

	private static final Logger logger = Logger.getLogger(MaxDataRestrictionStatement.class.getPackage().getName());

	private OntologyProperty property;
	private int maxCardinality = 0;
	private DataType dataRange = DataType.Unknown;
	
	public MaxDataRestrictionStatement(OntologyObject subject, Statement s, Restriction r)
	{
		super(subject,s,r);
		property = getOntologyLibrary().getProperty(r.getOnProperty().getURI());
		
		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_DATA_RANGE = ResourceFactory.createProperty( OWL + "onDataRange" );
		Property QUALIFIED_CARDINALITY = ResourceFactory.createProperty( OWL + "qualifiedCardinality" );

		Statement onDataRangeStmt = r.getProperty(ON_DATA_RANGE);
		Statement cardinalityStmt = r.getProperty(QUALIFIED_CARDINALITY);
		
		RDFNode onDataRangeStmtValue = onDataRangeStmt.getObject();
		RDFNode maxCardinalityStmtValue = cardinalityStmt.getObject();
		
		if (onDataRangeStmtValue instanceof Resource) {
			dataRange = getDataType(((Resource)onDataRangeStmtValue).getURI());
		}
			
		if (maxCardinalityStmtValue.isLiteral() && maxCardinalityStmtValue.canAs(Literal.class)) {
			Literal literal = (Literal)maxCardinalityStmtValue.as(Literal.class);
			maxCardinality = literal.getInt();
		}
		
		
		//object = getOntologyLibrary().getOntologyObject(r.get().getURI());
		//cardinality = r.getCardinality();
	}

	@Override
	public DataType getDataRange()
	{
		return dataRange;
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
	public OntologyProperty getProperty() 
	{
		return property;
	}


	@Override
	public String toString() 
	{
		return getSubject().getName()+" "+(property==null?"<NOT FOUND:"+restriction.getOnProperty().getURI()+">":property.getName())+" max "+maxCardinality+" "+getDataRange();
	}

	@Override
	public String getName() {
		return property.getName()+" max "+maxCardinality;
	}


}
