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
package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.logging.FlexoLogger;


public abstract class AddConcept extends EditionAction<OntologicObjectPatternRole> {

	protected static final Logger logger = FlexoLogger.getLogger(AddConcept.class.getPackage().getName());

	public AddConcept() {
	}
	
	private String conceptURI;

	public String _getConceptURI()
	{
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) 
	{
		this.conceptURI = conceptURI;
	}
	
	public OntologyClass getOntologyClass()
	{
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getClass(_getConceptURI());
	}
	
	public void setOntologyClass(OntologyClass ontologyClass)
	{
		conceptURI = (ontologyClass != null ? ontologyClass.getURI() : null);
	}

	/*public OntologyObject getOntologyObject(FlexoProject project)
	{
		getCalc().loadWhenUnloaded();
		if (StringUtils.isEmpty(getConceptURI())) return null;
		return project.getOntologyLibrary().getOntologyObject(getConceptURI());
	}*/
	
	@Override
	public OntologicObjectPatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}
	
	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
	// Need to be fixed in KeyValueProperty.java
	@Override
	public void setPatternRole(OntologicObjectPatternRole patternRole) 
	{
		super.setPatternRole(patternRole);
	}
	

	
}
