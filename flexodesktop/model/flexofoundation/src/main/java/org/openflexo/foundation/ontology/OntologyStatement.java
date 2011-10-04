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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.InspectableObject;

import com.hp.hpl.jena.rdf.model.Statement;

public abstract class OntologyStatement extends AbstractOntologyObject implements InspectableObject {

	private static final Logger logger = Logger.getLogger(OntologyStatement.class.getPackage().getName());
	
	private final OntologyObject _subject;
	
	private final Statement _statement;
	
	public OntologyStatement(OntologyObject subject, Statement s)
	{
		super();
		_subject = subject;
		_statement = s;
		
		 if (!s.getSubject().equals(_subject.getOntResource())) {
			 logger.warning("Inconsistant data: subject is not "+this);
		 }
	}
	
	@Override
	public void delete()
	{
		if ((getSubject() != null) && (getStatement() != null)) {
			getSubject().getFlexoOntology().getOntModel().remove(getStatement());
			getSubject().update();
		}
		super.delete();
	}
	
	@Override
	public FlexoOntology getFlexoOntology() 
	{
		if (_subject != null) {
			return _subject.getFlexoOntology();
		}
		return null;
	}
	
	@Override
	public FlexoProject getProject()
	{
		if (_subject != null) {
			return _subject.getProject();
		}
		return null;
	}

	public OntologyObject getSubject() 
	{
		return _subject;
	}

	public Statement getStatement() 
	{
		return _statement;
	}
	
	/*public OntologyLibrary getOntologyLibrary()
	{
		return getProject().getOntologyLibrary();
	}*/

	@Override
	public final String getInspectorName()
	{
		return Inspectors.VE.ONTOLOGY_STATEMENT_INSPECTOR;
	}

	@Override
	public String getDisplayableDescription()
	{
		return toString();
	}

}
