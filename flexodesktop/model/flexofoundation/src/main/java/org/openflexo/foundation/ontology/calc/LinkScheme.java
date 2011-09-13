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
package org.openflexo.foundation.ontology.calc;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.toolbox.StringUtils;


public class LinkScheme extends EditionScheme {

	private String fromTarget;
	private String toTarget;

	public LinkScheme() 
	{
		super();
	}

	@Override
	public EditionSchemeType getEditionSchemeType()
	{
		return EditionSchemeType.LinkScheme;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.LINK_SCHEME_INSPECTOR;
	}

	public String _getFromTarget() {
		return fromTarget;
	}

	public void _setFromTarget(String fromTarget) {
		this.fromTarget = fromTarget;
	}

	public String _getToTarget() {
		return toTarget;
	}

	public void _setToTarget(String toTarget) {
		this.toTarget = toTarget;
	}

	public OntologyClass getFromTargetClass()
	{
		if (StringUtils.isEmpty(_getFromTarget())) {
			return null;
		}
		return getOntologyLibrary().getClass(_getFromTarget());
	}
	
	public void setFromTargetClass(OntologyClass targetClass)
	{
		_setFromTarget(targetClass != null ? targetClass.getURI() : null);
	}

	public OntologyClass getToTargetClass()
	{
		if (StringUtils.isEmpty(_getToTarget())) {
			return null;
		}
		return getOntologyLibrary().getClass(_getToTarget());
	}
	
	public void setToTargetClass(OntologyClass targetClass)
	{
		_setToTarget(targetClass != null ? targetClass.getURI() : null);
	}


	
	public boolean isValidTarget(OntologyObject actualFromTarget, OntologyObject actualToTarget)
	{
		/*System.out.println("actualFromTarget: "+actualFromTarget);
		System.out.println("actualToTarget: "+actualToTarget);
		System.out.println("getFromTarget(): "+getFromTarget());
		System.out.println("getToTarget(): "+getToTarget());
		System.out.println("desiredFrom: "+getDesiredFromTargetObject(actualFromTarget.getProject()));
		System.out.println("desiredTo: "+getDesiredToTargetObject(actualToTarget.getProject()));*/
		return getFromTargetClass().isSuperConceptOf(actualFromTarget)
		&& getToTargetClass().isSuperConceptOf(actualToTarget);
	}
	

}
