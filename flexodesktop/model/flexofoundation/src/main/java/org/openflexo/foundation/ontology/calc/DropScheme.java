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


public class DropScheme extends EditionScheme {

	private String target;

	public DropScheme() 
	{
		super();
	}

	@Override
	public EditionSchemeType getEditionSchemeType()
	{
		return EditionSchemeType.DropScheme;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.DROP_SCHEME_INSPECTOR;
	}

	public String _getTarget() 
	{
		return target;
	}

	public void _setTarget(String target) 
	{
		this.target = target;
	}

	public OntologyClass getTargetClass()
	{
		if (StringUtils.isEmpty(_getTarget())) {
			return null;
		}
		return getOntologyLibrary().getClass(_getTarget());
	}
	
	public void setTargetClass(OntologyClass targetClass)
	{
		_setTarget(targetClass != null ? targetClass.getURI() : null);
	}

	public boolean isTopTarget()
	{
		return getTopTarget();
	}

	public boolean getTopTarget()
	{
		if (StringUtils.isEmpty(_getTarget())) {
			return false;
		}
		return _getTarget().equalsIgnoreCase("top");
	}
	
	public void setTopTarget(boolean flag)
	{
		if (flag) {
			_setTarget("top");
		}
		else {
			_setTarget("");
		}
	}
	
	public boolean isValidTarget(OntologyObject aTarget)
	{
		if (getTargetClass() == null) {
			return false;
		}
		return getTargetClass().isSuperConceptOf(aTarget);
	}
	

}
