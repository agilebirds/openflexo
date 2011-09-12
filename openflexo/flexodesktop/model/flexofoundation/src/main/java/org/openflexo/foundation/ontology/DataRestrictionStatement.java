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
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class DataRestrictionStatement extends RestrictionStatement {

	private static final Logger logger = Logger.getLogger(DataRestrictionStatement.class.getPackage().getName());

	public DataRestrictionStatement(OntologyObject subject, Statement s, Restriction r)
	{
		super(subject,s,r);
	}
	
	public abstract DataType getDataRange();
	
	public static enum DataType 
	{
		String,
		Unknown
	}
	
	public static DataType getDataType(String dataTypeAsString) 
	{
		if (dataTypeAsString.equals("http://www.w3.org/2001/XMLSchema#string")) return DataType.String;
		return DataType.Unknown;
	}
}
