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

/**
 * A PatternRole is an element of an EditionPattern, which play a role in this edition pattern
 * 
 * @author sylvain
 *
 */
public abstract class PatternRole extends CalcObject {

	public static enum PatternRoleType
	{
		Shape,
		Connector,
		OntologicObject,
		FlexoModelObject,
		Shema,
		Primitive
	}
	

	private EditionPattern _pattern;
	private String patternRoleName;
	private String description;
		

 	public PatternRole()
	{
	}
	
	public void setEditionPattern(EditionPattern pattern) 
	{
		_pattern = pattern;
	}

	public EditionPattern getEditionPattern() 
	{
		return _pattern;
	}
	
	@Override
	public String getDescription() 
	{
		return description;
	}

	@Override
	public void setDescription(String description) 
	{
		this.description = description;
	}

	@Override
	public OntologyCalc getCalc() 
	{
		if (getEditionPattern() != null) {
			return getEditionPattern().getCalc();
		}
		return null;
	}
	
	@Override
	public String getName()
	{
		return getPatternRoleName();
	}
	
	public String getPatternRoleName() 
	{
		return patternRoleName;
	}

	public void setPatternRoleName(String patternRoleName) 
	{
		this.patternRoleName = patternRoleName;
		/*String oldValue = patternRoleName;
		if (patternRoleName != null && !patternRoleName.equals(oldValue)) {
			this.patternRoleName = patternRoleName;
			setChanged();
			notifyObservers(new NameChanged(oldValue, patternRoleName));
		}*/
	}

	@Override
	public String getInspectorName() 
	{
		if (getType() == PatternRoleType.Shape) {
			return Inspectors.CED.SHAPE_PATTERN_ROLE_INSPECTOR;
		} else if (getType() == PatternRoleType.Connector) {
			return Inspectors.CED.CONNECTOR_PATTERN_ROLE_INSPECTOR;
		} else {
			return Inspectors.CED.PATTERN_ROLE_INSPECTOR;
		}
	}
	
	@Override
	public String toString() 
	{
		return getPatternRoleName();
	}

	public abstract PatternRoleType getType();


	public abstract String getPreciseType();
	

	public void finalizePatternRoleDeserialization()
	{
	}
	


}
