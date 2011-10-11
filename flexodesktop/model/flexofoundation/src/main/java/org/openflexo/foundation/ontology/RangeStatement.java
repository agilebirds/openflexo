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

public class RangeStatement extends OntologyStatement {

	private static final Logger logger = Logger.getLogger(RangeStatement.class.getPackage().getName());

	public static final String RANGE_URI = "http://www.w3.org/2000/01/rdf-schema#range";
	public static final String STRING_URI = "http://www.w3.org/2001/XMLSchema#string";
	
	private OntologyObject range;
	
	private boolean isString = false;
	// TODO handle other datatype
	
	public RangeStatement(OntologyObject subject, Statement s)
	{
		super(subject,s);
		if (s.getObject() instanceof Resource) {
			range = getOntologyLibrary().getOntologyObject(((Resource)s.getObject()).getURI());
			if (((Resource)s.getObject()).getURI() != null)
				isString = ((Resource)s.getObject()).getURI().equals(STRING_URI);
		}
		else {
			logger.warning("RangeStatement: object is not a Resource !");
		}
	}

	@Override
	public String getClassNameKey()
	{
		return "range_statement";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "RangeStatement: "+getStatement();
	}


	public OntologyObject getRange() 
	{
		return range;
	}

	@Override
	public String toString() 
	{
		if (isString()) {
			return getSubject().getName()+" has range string";
		}
		return getSubject().getName()+" has range "+(getRange() != null ? getRange().getName() : "<NOT_FOUND:"+getStatement().getObject()+">");
	}

	public boolean isString() 
	{
		return isString;
	}

	public String getStringRepresentation()
	{
		if (isString()) return "String";
		else if (getRange() != null) return getRange().getName();
		return "";
	}
}
