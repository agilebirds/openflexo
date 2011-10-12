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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.view.action.EditionSchemeAction;



public abstract class AddProperty<R extends OntologicObjectPatternRole> extends EditionAction<R> {

	private static final Logger logger = Logger.getLogger(AddProperty.class.getPackage().getName());

	private String subject;
	
	public AddProperty() {
	}
	
	public String _getSubject()
	{
		return subject;
	}
	
	public void _setSubject(String aSubject)
	{
		subject = aSubject;
	}
	
	private Vector<String> availableSubjectValues = null;
	
	public Vector<String> getAvailableSubjectValues()
	{
		if (availableSubjectValues == null) {
			availableSubjectValues = new Vector<String>();
			switch (getScheme().getEditionSchemeType()) {
			case DropScheme:
				availableSubjectValues.add(EditionAction.CONTAINER);
				availableSubjectValues.add(EditionAction.CONTAINER_OF_CONTAINER);
				break;
			case LinkScheme:
				availableSubjectValues.add(EditionAction.FROM_TARGET);
				availableSubjectValues.add(EditionAction.TO_TARGET);
				break;
			default:
				break;
			}
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableSubjectValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableSubjectValues.add(p.getName());
			}
		}
		return availableSubjectValues;
	}

	public OntologyObject getPropertySubject(EditionSchemeAction action)
	{
		return retrieveOntologyObject(_getSubject(), action);
	}

	@Override
	public R getPatternRole() {
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
	public void setPatternRole(R patternRole) 
	{
		super.setPatternRole(patternRole);
	}
	

}
